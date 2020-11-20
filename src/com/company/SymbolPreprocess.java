package com.company;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolPreprocess {
//    private static final String ANDROID_JAR_PATH = "/var/shared/android_jars/";
    private static final String ANDROID_JAR_PATH = "../android_jars/";
    private static CombinedTypeSolver typeSolver;


    public static String oldApiNameModified;
    public static String newApiNameModified;
    private static String functionName;
    private static JavaSymbolSolver symbolSolver;

    public static List<String> listFilesForFolder(final File folder) {
        List<String> retval = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {

            if (fileEntry.isDirectory()) {
                retval.addAll(listFilesForFolder(fileEntry));
            } else {
                if (fileEntry.getName().contains(".jar")) {
                    retval.add(fileEntry.toString());
                }
            }
        }
        return retval;
    }

    public static void initTypeSolver() {
        typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        final File folder = new File(ANDROID_JAR_PATH);
        List<String> jarFiles = listFilesForFolder(folder);
        jarFiles.forEach(jarPath -> {
            try {
                JarTypeSolver jarSolver = new JarTypeSolver(jarPath);
                typeSolver.add(jarSolver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

    }

    private static class ApiNameRevert extends ModifierVisitor<List<String>> {
        private static String originalName;
        public ApiNameRevert(String function) {
            this.originalName = function;
        }

        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
            super.visit(methodCall, collector);
            if (methodCall.getName().toString().contains(originalName)) {
                methodCall.setName(originalName);
            }
            return methodCall;
        }
    }

    private static class ApiNameModifier extends ModifierVisitor<List<String>> {

        private static String oldApiName;
        private static String newApiName;
        private static ArrayList<String> oldApiParam;
        private static ArrayList<String> newApiParam;

        //        private static List<Integer> lineNumberList;
        private static int modifier = 0;
        private static int linePosition = 0;

        ApiNameModifier(String oldApiName, String newApiName) {
            this.oldApiName = oldApiName;
            this.newApiName = newApiName;

            functionName = Util.getFunctionName(oldApiName);
            oldApiNameModified = functionName + "OLD";
            newApiNameModified = functionName + "NEW";


            int numParam = Util.getNumParameter(oldApiName);
            oldApiParam = new ArrayList<>();
            newApiParam = new ArrayList<>();
            for (int i = 0; i < numParam; i++) {
                oldApiParam.add(Util.getParameterType(oldApiName,i));
                newApiParam.add(Util.getParameterType(newApiName,i));
            }
//            this.lineNumberList = new ArrayList<>();
        }

        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
            super.visit(methodCall, collector);
            // API Name Check
            if (methodCall.getName().toString().equals(functionName)) {
                NodeList<Expression> arguments = methodCall.getArguments();
                // Flag to decide whether it is an old api call or new api call:
                // 0 = not decided yet
                // 1 = new api
                // -1 = old api
                int flag = 0;

                // Synonym for number type
                ArrayList<String> numberSynonym = new ArrayList<>();
                numberSynonym.add("long");
                numberSynonym.add("int");
                numberSynonym.add("float");
                numberSynonym.add("short");
                numberSynonym.add("double");

                if (arguments.size() == oldApiParam.size()) {
                    for (int i = 0; i < arguments.size(); i++) {
                        String actualArgument = arguments.get(i).calculateResolvedType().describe();
                        // NEED SPECIAL PROCESSING IF INT or INTEGER or LONG
                        if (numberSynonym.contains(oldApiParam.get(i))) {
                            oldApiParam.set(i, "num");
                        }
                        if (numberSynonym.contains(newApiParam.get(i))) {
                            oldApiParam.set(i, "num");
                        }
                        if (numberSynonym.contains(actualArgument)) {
                            actualArgument = "num";
                        }
                        if (actualArgument.contains(oldApiParam.get(i)) || oldApiParam.get(i).contains(actualArgument)) {
                            flag -= 1;
                        }
                        if (actualArgument.contains(newApiParam.get(i)) || newApiParam.get(i).contains(actualArgument)) {
                            flag += 1;
                        }
                        if (flag != 0) {
                            break;
                        }
                    }
                }

                // Modify the API name here
                if (flag > 0) {
                    methodCall.setName(newApiNameModified);
                } else if (flag < 0) {
                    methodCall.setName(oldApiNameModified);
                }

            }
            return methodCall;
        }
    }

    public static void modifyFunctionName(String filePath, String oldQualifiedName, String newQualifiedName) {
        File file = new File(filePath);
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> assignmentBlock = new ArrayList<>();
        ModifierVisitor<List<String>> apiNameModifier = new ApiNameModifier(oldQualifiedName, newQualifiedName);
        apiNameModifier.visit(cu, assignmentBlock);
        file.delete();
        // Rewrite the file
        try {
            Files.write(new File(filePath).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void revertOriginalName(String filePath, String functionName) {
        File file = new File(filePath);
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> assignmentBlock = new ArrayList<>();
        ModifierVisitor<List<String>> apiNameReverter = new ApiNameRevert(functionName);
        apiNameReverter.visit(cu, assignmentBlock);
        file.delete();
        // Rewrite the file
        try {
            Files.write(new File(filePath).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
