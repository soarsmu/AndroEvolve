package com.company;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeparateDeclaration {
    private static class ObjectModifier extends ModifierVisitor<List<String>> {
        private static String apiName;

        ObjectModifier(String apiName) {
            this.apiName = apiName;
//            this.lineNumberList = new ArrayList<>();
        }

        public VariableDeclarationExpr visit (VariableDeclarationExpr varDecExpr, List<String> collector) {
            super.visit(varDecExpr, collector);
            boolean related = false;
            for (int i = 0; i < AssignmentModifier.listRelated.size(); i++) {
                if (varDecExpr.toString().contains(AssignmentModifier.listRelated.get(i))) {
                    related = true;
                    break;
                }
            }
            if (varDecExpr.toString().contains("=")) {
                if (related) {
                    varDecExpr.findAncestor(BlockStmt.class).ifPresent(blockStmt -> {
                        NodeList<Statement> stmtList = blockStmt.getStatements();
                        for (int i = 0; i < stmtList.size(); i++) {
                            if (stmtList.get(i).toString().contains(varDecExpr.toString())) {
                                String stringStmt = varDecExpr.toString();

                                String varType = stringStmt.substring(0, stringStmt.indexOf(" "));
                                String assignment = stringStmt.substring(stringStmt.indexOf(" ") + 1) + ";";
                                String varName = assignment.substring(0, assignment.indexOf("=")).trim();
                                Statement declareStmt = StaticJavaParser.parseStatement(varType + " " + varName + ";");
                                Statement assignStmt = StaticJavaParser.parseStatement(assignment);
                                blockStmt.setStatement(i, assignStmt);
                                blockStmt.addStatement(i, declareStmt);
                            }
                        }
                    });
                }
            }
            return varDecExpr;
        }
    }


    private static class AssignmentModifier extends ModifierVisitor<List<String>> {

        private static String apiName;
        //        private static List<Integer> lineNumberList;
        private static int modifier = 0;
        private static int linePosition = 0;
        private static List<String> listRelated;

        AssignmentModifier(String apiName) {
            this.apiName = apiName;
            listRelated = new ArrayList<>();
//            this.lineNumberList = new ArrayList<>();
        }

        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
            super.visit(methodCall, collector);
            // API Name Check
            if (methodCall.getName().toString().equals(apiName)) {
                String methodCallName = methodCall.toString().substring(0, methodCall.toString().indexOf("("));
                // Check if have className
                if (methodCallName.contains(".")) {
                    listRelated.add(methodCallName.substring(0, methodCallName.indexOf(".")));
                }
                // Add all the arguments also
                NodeList<Expression> listArgs = methodCall.getArguments();
                for (int i = 0; i < listArgs.size(); i++) {
                    listRelated.add(listArgs.get(i).toString());
                }

                // VariableDeclarationExpr Check
                methodCall.findAncestor(VariableDeclarationExpr.class).ifPresent(expr -> {
                    String varDeclare = expr.toString();

                    expr.findAncestor(BlockStmt.class).ifPresent(blockStmt -> {
                        List<Statement> blockList = blockStmt.getStatements();
                        List<Integer> declareFound = new ArrayList<>();
                        // Get the line number which has the same declaration and assignment

                        for (int i = 0; i < blockList.size(); i++) {

                            if (blockList.get(i).toString().contains(varDeclare)) {
                                declareFound.add(i);
                            }
                        }
                        // Modify the file
                        for (int i = declareFound.size() - 1; i >= 0; i--) {

                            int currentLine = declareFound.get(i);
                            String stringStmt = varDeclare;
                            String varType = stringStmt.substring(0, stringStmt.indexOf(" "));
                            String assignment = stringStmt.substring(stringStmt.indexOf(" ") + 1) + ";";
                            String varName = assignment.substring(0, assignment.indexOf("=")).trim();
                            Statement declareStmt = StaticJavaParser.parseStatement(varType + " " + varName + ";");
                            Statement assignStmt = StaticJavaParser.parseStatement(assignment);
                            blockStmt.setStatement(currentLine, assignStmt);
                            blockStmt.addStatement(currentLine, declareStmt);
                        }
                    });
                });

            }
            return methodCall;
        }
    }

    public static void separateDeclaration(String FILEPATH, String apiName, boolean createUpdate) {
        // write your code here
        File file = new File(FILEPATH);
//        File file = new File(FILEPATH);
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> assignmentBlock = new ArrayList<>();
        ModifierVisitor<List<String>> assignmentVisitor = new AssignmentModifier(apiName);
        assignmentVisitor.visit(cu, assignmentBlock);
        if (!createUpdate) {
            ObjectModifier objectVisitor = new ObjectModifier(apiName);
            objectVisitor.visit(cu, assignmentBlock);
        }

        file.delete();
        try {
            Files.write(new File(FILEPATH).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
