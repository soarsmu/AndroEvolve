package com.company;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserSymbolDeclaration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.company.Util.*;



public class Pipeline {

    private static class NormalizeFileVisitor extends ModifierVisitor<List<String>> {
        private static String apiName;
        private static String fullyQualifiedName;
        private static String statementCocci;
        private static String variableCocci;
        private static BlockStmt pastBlock;

        public NormalizeFileVisitor(String apiName, String fullyQualifiedName) {
            this.apiName = apiName;
            this.fullyQualifiedName = fullyQualifiedName;
            if (getReturnValue(fullyQualifiedName).equals("void")) {
                statementCocci = "";
            } else {
                statementCocci = createStatementPullCocci(fullyQualifiedName);
            }

            variableCocci = createVariablePullCocci(fullyQualifiedName);
        }

        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
            super.visit(methodCall, collector);
            NodeList<Expression> arg = methodCall.getArguments();
            int numArg = getNumParameter(fullyQualifiedName);

//            System.out.println("This is method call: " + methodCall.getName().toString());
//            System.out.println("This is numarg: " + numArg);
//            System.out.println("This is what we lookin for: " + apiName);
//            System.out.println("Outside the if: " + methodCall.toString());
            if ((methodCall.getName().toString().contains(apiName)) && (numArg == arg.size()))  {

                boolean processAsStatement = true;
                try {
                    methodCall.findAncestor(Statement.class).get();
                } catch (Exception E) {
                    processAsStatement = false;
                }
                if (processAsStatement) {
                    methodCall.findAncestor(Statement.class).ifPresent(statement -> {
                        try {
                            Pattern p2 = Pattern.compile(apiName);
                            String toNormalize = statement.toString();
                            Matcher m2 = p2.matcher(toNormalize);
                            int occur = 0;
                            while (m2.find()) {
                                occur++;
                            }
                            if (occur > 1) {
                                // For case that a statement contains multiple same API Call
                                // Need separate processing due to multiple error
                                // Need to change all getCurrentHour instance into tempFunctionReturnValue
                                List<MethodCallExpr> listMethod = statement.findAll(MethodCallExpr.class);
                                boolean flagChange = false;
                                for (int i = 0; i < listMethod.size(); i++) {
                                    if ((listMethod.get(i).toString().contains(apiName)) && (listMethod.get(i).getArguments().size() == numArg)) {
                                        if (flagChange) {
                                            toNormalize = toNormalize.replace(listMethod.get(i).toString(), "tempFunctionReturnValue");
                                        } else {
                                            flagChange = true;
                                        }
                                    }
                                }
                            }

                            FileWriter fw = new FileWriter("tempEdit.txt");
                            PrintWriter pw = new PrintWriter(fw);
                            FileWriter fw1 = new FileWriter("tempEdit1.txt");
                            PrintWriter pw1 = new PrintWriter(fw1);
                            pw.println("class something {");
                            pw.println("public void somefunc() {");
                            pw.println(toNormalize);
                            pw.println("}");
                            pw.println("}");
                            pw.close();

                            pw1.println("class something {");
                            pw1.println("public void somefunc() {");
                            pw1.println(toNormalize);
                            pw1.println("}");
                            pw1.println("}");
                            pw1.close();
                            // PUT THE NORMALIZATION HERE


                            String returnValue = getReturnValue(fullyQualifiedName);
                            int numParam = getNumParameter(fullyQualifiedName);

                            ArrayList<String> allCommand = new ArrayList<>();
                            Runtime runtime = Runtime.getRuntime();
                            if (!returnValue.equals("void")) {
                                // In case return value is not void, pull out the statement
                                StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + statementCocci + " " + "tempEdit.txt" + " --in-place");
                                try {
                                    System.out.println(spatchCommand);
                                    Thread.sleep(200);
                                    Runtime.getRuntime().exec(spatchCommand.toString());
                                    Thread.sleep(200);
                                    File temp = new File(statementCocci);
//                                    temp.delete();
                                } catch (InterruptedException | IOException e) {
                                    // e.printStackTrace();
                                }
                            }
                            StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + variableCocci + " " + "tempEdit.txt" + " --in-place");
                            allCommand.add(spatchCommand.toString());
                            try {
                                System.out.println(spatchCommand);
                                Runtime.getRuntime().exec(spatchCommand.toString());
                                Thread.sleep(200);
                                File temp = new File(variableCocci);
//                                temp.delete();
                            } catch (IOException | InterruptedException e) {
                                // e.printStackTrace();
                            }

                            // END OF NORMALIZATION


                            Scanner s = new Scanner(new File("tempEdit.txt"));
                            ArrayList<String> listNewStmt = new ArrayList<>();
                            while (s.hasNextLine()) {
                                listNewStmt.add(s.nextLine());
                            }
                            s.close();

                            try {
                                methodCall.findAncestor(BlockStmt.class).get();
                                methodCall.findAncestor(BlockStmt.class).ifPresent(block -> {
                                    NodeList<Statement> listOriginalStmt = block.getStatements();
                                    for (int i = 0; i < listOriginalStmt.size(); i++) {
                                        Statement currStmt = listOriginalStmt.get(i);
                                        currStmt = (Statement) currStmt.setParentNode(block);
                                        if (currStmt.toString().contains(statement.toString())) {
                                            String modifiedStmt = currStmt.toString();
                                            String lastLine = listNewStmt.get(listNewStmt.size() - 3).trim();
                                            int diff = 3;
                                            Statement temp;
                                            while (true) {
                                                try {
                                                    temp = StaticJavaParser.parseStatement(lastLine);
                                                    if (true) {
                                                        break;
                                                    }
                                                } catch (Exception E2) {
                                                    diff += 1;
                                                    lastLine = listNewStmt.get(listNewStmt.size() - diff).trim() + lastLine;
                                                }
                                            }
                                            modifiedStmt = modifiedStmt.replace(statement.toString(), lastLine);
                                            Statement nodeModifiedStmt = StaticJavaParser.parseStatement(modifiedStmt);
                                            nodeModifiedStmt = (Statement) nodeModifiedStmt.setParentNode(block);
                                            // Count the occurence of the API Call
                                            int count = 0;
                                            Pattern p = Pattern.compile(apiName);
                                            Matcher m = p.matcher(modifiedStmt);
                                            while (m.find()) {
                                                count++;
                                            }
                                            if (count == 1) {
                                                block.setStatement(i, nodeModifiedStmt);
                                            } else {
                                                // For case that a statement contains multiple same API Call
                                                // Need separate processing due to multiple error
                                                // Need to change all getCurrentHour instance into tempFunctionReturnValue
                                                String toChange = methodCall.toString();
                                                modifiedStmt = modifiedStmt.replace(toChange, "tempFunctionReturnValue");
                                                block.setStatement(i, StaticJavaParser.parseStatement(modifiedStmt));
                                            }

                                            for (int j = listNewStmt.size() - 1 - diff; j > 1; j--) {
                                                String toParse = listNewStmt.get(j).trim();
                                                while (true) {
                                                    try {
                                                        StaticJavaParser.parseStatement(toParse);
                                                        if (true) {
                                                            break;
                                                        }
                                                    } catch (Exception E2) {
                                                        j = j - 1;
                                                        toParse = listNewStmt.get(j).trim() + toParse.trim();
                                                    }
                                                }

                                                Statement newStmt = null;
                                                try {
                                                    newStmt = StaticJavaParser.parseStatement(toParse);
                                                } catch (Exception E2) {
//                                                    System.out.println("Exception during parseStatement");
                                                }
                                                newStmt = (Statement) newStmt.setParentNode(block);
                                                block.addStatement(i, newStmt);
                                            }
                                            break;
                                        }
                                    }
                                    pastBlock = block;
                                });
                            } catch (Exception E2) {
//                                System.out.println(E2);
                            }
                        } catch (IOException e) {
                            // e.printStackTrace();
                        }
                    });
                } else {
                    methodCall.findAncestor(Expression.class).ifPresent(statement -> {
                        try {
                            Pattern p2 = Pattern.compile(apiName);
                            String toNormalize = statement.toString();
                            Matcher m2 = p2.matcher(toNormalize);
                            int occur = 0;
                            while (m2.find()) {
                                occur++;
                            }
                            if (occur > 1) {
                                // For case that a statement contains multiple same API Call
                                // Need separate processing due to multiple error
                                // Need to change all getCurrentHour instance into tempFunctionReturnValue
                                List<MethodCallExpr> listMethod = statement.findAll(MethodCallExpr.class);
                                boolean flagChange = false;
                                for (int i = 0; i < listMethod.size(); i++) {
                                    if ((listMethod.get(i).toString().contains(apiName)) && (listMethod.get(i).getArguments().size() == numArg)) {
                                        if (flagChange) {
                                            toNormalize = toNormalize.replace(listMethod.get(i).toString(), "tempFunctionReturnValue");
                                        } else {
                                            flagChange = true;
                                        }
                                    }
                                }
                            }

                            FileWriter fw = new FileWriter("tempEdit.txt");
                            PrintWriter pw = new PrintWriter(fw);
                            FileWriter fw1 = new FileWriter("tempEdit1.txt");
                            PrintWriter pw1 = new PrintWriter(fw1);
                            pw.println("class something {");
                            pw.println("public void somefunc() {");
                            pw.println(toNormalize.toString() + ";");
                            pw.println("}");
                            pw.println("}");
                            pw.close();

                            pw1.println("class something {");
                            pw1.println("public void somefunc() {");
                            pw1.println(toNormalize.toString() + ";");
                            pw1.println("}");
                            pw1.println("}");
                            pw1.close();
                            // PUT THE NORMALIZATION HERE


                            String returnValue = getReturnValue(fullyQualifiedName);
                            int numParam = getNumParameter(fullyQualifiedName);

                            ArrayList<String> allCommand = new ArrayList<>();
                            Runtime runtime = Runtime.getRuntime();
                            if (!returnValue.equals("void")) {
                                // In case return value is not void, pull out the statement
                                StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + statementCocci + " " + "tempEdit.txt" + " --in-place");
                                try {
                                    Thread.sleep(200);
                                    Runtime.getRuntime().exec(spatchCommand.toString());
                                    Thread.sleep(200);
                                    File temp = new File(statementCocci);
//                                    temp.delete();
                                } catch (InterruptedException | IOException e) {
                                    // e.printStackTrace();
                                }
                            }
                            StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + variableCocci + " " + "tempEdit.txt" + " --in-place");
                            allCommand.add(spatchCommand.toString());
                            try {
                                Runtime.getRuntime().exec(spatchCommand.toString());
                                Thread.sleep(200);
                                File temp = new File(variableCocci);
//                                temp.delete();
                            } catch (IOException | InterruptedException e) {
                                // e.printStackTrace();
                            }

                            // END OF NORMALIZATION


                            Scanner s = new Scanner(new File("tempEdit.txt"));
                            ArrayList<String> listNewStmt = new ArrayList<>();
                            while (s.hasNextLine()) {
                                listNewStmt.add(s.nextLine());
                            }
                            s.close();

                            try {
                                methodCall.findAncestor(BlockStmt.class).get();
                                methodCall.findAncestor(BlockStmt.class).ifPresent(block -> {
                                    NodeList<Statement> listOriginalStmt = block.getStatements();
                                    for (int i = 0; i < listOriginalStmt.size(); i++) {
                                        Statement currStmt = listOriginalStmt.get(i);
                                        currStmt = (Statement) currStmt.setParentNode(block);
                                        if (currStmt.toString().contains(statement.toString())) {

                                            String modifiedStmt = currStmt.toString();
                                            String lastLine = listNewStmt.get(listNewStmt.size() - 3).trim();
                                            lastLine = lastLine.substring(0, lastLine.length() - 1);
                                            int diff = 3;
                                            Expression temp;
                                            while (true) {
                                                try {
                                                    temp = StaticJavaParser.parseExpression(lastLine);
                                                    if (true) {
                                                        break;
                                                    }
                                                } catch (Exception E) {
                                                    diff += 1;
                                                    lastLine = listNewStmt.get(listNewStmt.size() - diff).trim() + lastLine;
                                                }
                                            }
                                            modifiedStmt = modifiedStmt.replace(statement.toString(), lastLine);
                                            Statement nodeModifiedStmt = StaticJavaParser.parseStatement(modifiedStmt);
                                            nodeModifiedStmt = (Statement) nodeModifiedStmt.setParentNode(block);
                                            // Count the occurence of the API Call
                                            int count = 0;
                                            Pattern p = Pattern.compile(apiName);
                                            Matcher m = p.matcher(modifiedStmt);
                                            while (m.find()) {
                                                count++;
                                            }
                                            if (count == 1) {
                                                block.setStatement(i, nodeModifiedStmt);
                                            } else {
                                                // For case that a statement contains multiple same API Call
                                                // Need separate processing due to multiple error
                                                // Need to change all getCurrentHour instance into tempFunctionReturnValue
                                                String toChange = methodCall.toString();
                                                modifiedStmt = modifiedStmt.replace(toChange, "tempFunctionReturnValue");
                                                block.setStatement(i, StaticJavaParser.parseStatement(modifiedStmt));
                                            }

                                            for (int j = listNewStmt.size() - 1 - diff; j > 1; j--) {
                                                String toParse = listNewStmt.get(j).trim();
                                                while (true) {
                                                    try {
                                                        StaticJavaParser.parseStatement(toParse);
                                                        if (true) {
                                                            break;
                                                        }
                                                    } catch (Exception E) {
                                                        j = j - 1;
                                                        toParse = listNewStmt.get(j).trim() + toParse.trim();
                                                    }
                                                }

                                                Statement newStmt = null;
                                                try {
                                                    newStmt = StaticJavaParser.parseStatement(toParse);
                                                } catch (Exception E) {
//                                                    System.out.println("Exception during parseStatement");
                                                }
                                                newStmt = (Statement) newStmt.setParentNode(block);
                                                block.addStatement(i, newStmt);
                                            }
                                            break;
                                        }
                                    }
                                    pastBlock = block;
                                });
                            } catch (Exception E) {
//
                            }
                        } catch (IOException e) {
                            // e.printStackTrace();
                        }
                    });
                }


            }
            File toDelete = new File(statementCocci);
//            toDelete.deleteOnExit();
            toDelete = new File(variableCocci);
//            toDelete.deleteOnExit();
            return methodCall;
        }
    }


    public static class ParseResult {
        public String ifCondition;
        public String lowerApiBlock;
        public String upperApiBlock;
        public List<MethodDeclaration> lowerMethodList;
        public List<ClassOrInterfaceDeclaration> lowerClassList;
        public List<String> lowerNameList;
        public List<MethodDeclaration> upperMethodList;
        public List<ClassOrInterfaceDeclaration> upperClassList;
        public List<String> upperNameList;
        public List<String> upperMethodName;
        public List<String> lowerMethodName;


        public ParseResult() {
            this.ifCondition = "";
            lowerApiBlock = "";
            lowerMethodList = new ArrayList<>();
            lowerNameList = new ArrayList<>();
            upperApiBlock = "";
            upperMethodList = new ArrayList<>();
            upperNameList = new ArrayList<>();
            upperMethodName = new ArrayList<>();
            lowerMethodName = new ArrayList<>();
        }

        @Override
        public String toString() {
            StringBuilder returnValue = new StringBuilder("");
            returnValue.append("If condition: " + this.ifCondition + "\n");
            returnValue.append("oldBlock: \n" + lowerApiBlock + "\n");
            returnValue.append("newBlock: \n" + upperApiBlock + "\n");
            return returnValue.toString();
        }
    }

    public static String modifyFile(String filepath) {
        List<String> content = null;
        try {
            content = Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // e.printStackTrace();
        }
        int sizeContent = content.size();
        int i = 0;
        while (i < sizeContent){
            if (Pattern.matches(" *int[ ] *.*= *Build.VERSION.*;.*", content.get(i))){
                int indexInt = content.get(i).indexOf("int ");
                int indexOfEqual = content.get(i).indexOf("=");
                int indexOfSemicolon = content.get(i).indexOf(";");
                String variableName = content.get(i).substring(indexInt+4,indexOfEqual).trim();
                String changeName = content.get(i).substring(indexOfEqual+1,indexOfSemicolon).trim();
                int start = i + 1;
                Pattern patternFirst = Pattern.compile("[(] *"+variableName+"[<>= ]");
                Pattern patternLast = Pattern.compile("[<>= ]"+variableName+" *[)]");
                int startIndex = -1;
                int endIndex = -1;
                Matcher matcher;
                while (start < sizeContent){
                    if (Pattern.matches(" *if *[(] *"+variableName+"[<>= ].*[)].*", content.get(start)) || Pattern.matches(" *if *[(].*[>=< ]"+variableName+" *[)].*", content.get(start)) ){
                        matcher = patternFirst.matcher(content.get(start));
                        if (matcher.find()){
                            startIndex = matcher.start();
                            endIndex = matcher.end();
                        } else {
                            matcher = patternLast.matcher(content.get(start));
                            if (matcher.find()){
                                startIndex = matcher.start();
                                endIndex = matcher.end();
                            }
                        }
                        if (startIndex != -1 && endIndex != -1){
                            content.set(start, content.get(start).substring(0,startIndex+1)+changeName+content.get(start).substring(endIndex-1));
                        }
//                        content.set(start, content.get(start).replace(variableName, changeName));
                    }
                    start++;
                }
            }
            i++;
        }
        String filename = "temporaryOutput.java";
        try {
            PrintWriter writer = new PrintWriter(filename);
            for (i = 0; i < content.size(); i++) {
                writer.println(content.get(i));
            }
            writer.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }

        return filename;
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        public static List<ParseResult> parsingResult;
        private static String apiNameOld;
        private static String apiNameNew;
        private static List<String> listMethodName;
        private static List<MethodDeclaration> listMethodDeclaration;
        private static List<ClassOrInterfaceDeclaration> listAddedClass;
        private static List<String> listAddedClassName;
        private static List<String> listAddedName;
        private static List<String> listAddedMethodName;
        private static boolean flagArgument;

        private static List<List<String>> variableName;


        MethodNameCollector(String apiNameOld, String apiNameNew) {
            this.apiNameNew = apiNameNew;
            this.apiNameOld = apiNameOld;
            this.parsingResult = new ArrayList<>();
            this.listMethodName = new ArrayList<>();
            this.listMethodDeclaration = new ArrayList<>();
            this.listAddedClass = new ArrayList<>();
            this.listAddedClassName = new ArrayList<>();
            this.listAddedName = new ArrayList<>();
            this.variableName = new ArrayList<>();
            this.listAddedMethodName = new ArrayList<>();
            this.flagArgument = false;
        }

        private boolean isMethodExist(MethodDeclaration method) {
            String name = method.getNameAsString() + method.getParameters().toString();

            if (listMethodName.contains(name)) {
                return true;
            } else {
                return false;
            }
        }

        private boolean processStatement(Statement currStmt) {
            final boolean[] returnValue = new boolean[1];
            returnValue[0] = true;
            if (currStmt.isReturnStmt()) {
                try {
                    currStmt.asReturnStmt().setExpression(recursiveNode(currStmt.asReturnStmt().getExpression().get()));
                } catch (Exception E4) {
                }
            } else if (currStmt.isIfStmt()) {
                currStmt.asIfStmt().setCondition(recursiveNode(currStmt.asIfStmt().getCondition()));
                processStatement(currStmt.asIfStmt().getThenStmt());
                try {
                    processStatement(currStmt.asIfStmt().getElseStmt().get());
                } catch (Exception E5) {
                }
            } else if (currStmt.isWhileStmt()) {
                currStmt.asWhileStmt().setCondition(recursiveNode(currStmt.asWhileStmt().getCondition()));
                processStatement(currStmt.asWhileStmt().getBody());
            } else if (currStmt.isAssertStmt()) {
                currStmt.asAssertStmt().setCheck(recursiveNode(currStmt.asAssertStmt().getCheck()));
            } else if (currStmt.isForStmt()) {
                processStatement(currStmt.asForStmt().getBody());
            } else if (currStmt.isTryStmt()) {
                processStatement(currStmt.asTryStmt().getTryBlock());
            } else if (currStmt.isBlockStmt()) {
                NodeList<Statement> listStmt = currStmt.asBlockStmt().getStatements();
                for (int i = 0; i < listStmt.size(); i++) {
                    boolean result = processStatement(listStmt.get(i));
                    if (!result) {
                        i--;
                    }
                }
            } else {
                currStmt.findFirst(VariableDeclarator.class).ifPresent(variableDeclarator -> {
                    // Process every assignment here
                    variableName.get(variableName.size() - 1).add(variableDeclarator.getNameAsString());
                    // Add the variable
                    try {
                        Expression newInitializer = recursiveNode(variableDeclarator.getInitializer().get());
                        variableDeclarator.setInitializer(newInitializer);
                    } catch (Exception E2) {
//                        System.out.println("Variable not initialized");
                    }

                });
                currStmt.findFirst(AssignExpr.class).ifPresent(assignExpr -> {
                    // Process every assignment here
                    if (variableName.get(variableName.size() - 1).contains(assignExpr.getTarget().toString())) {
                        Expression newInitializer = recursiveNode(assignExpr.getValue());
                        assignExpr.setValue(newInitializer);
                    } else {
                        currStmt.remove();
                        returnValue[0] = false;
//                        listStatement.remove(currStmt);
//                        i.getAndDecrement();
                    }
                });
                currStmt.findFirst(MethodCallExpr.class).ifPresent(methodCallExpr -> {
                    // Process every method call expr
                    Expression newExpression = recursiveNode(methodCallExpr);
                    methodCallExpr.replace(newExpression);
                });
            }
            return returnValue[0];
        }

        private Expression recursiveNode(Expression toRecurse) {
            if (toRecurse.isThisExpr()) {
                // Case of this
                // Need to check whether in the target this also correct
                return toRecurse;
            } else if (toRecurse.isFieldAccessExpr()) {
                // Case for accessing object or class field
                final Expression[] returnValue = {null};
                // This is vital to check whether the scope is inside the file or outside the file
                // SHOULD CHECK WHETHER ITS A THIS FIELD ACCESS FIRST
                if (toRecurse.asFieldAccessExpr().getScope().isThisExpr()) {
                    SimpleName varName = toRecurse.asFieldAccessExpr().getName();
                    toRecurse.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(classDef -> {
                        // Also get all the field declarations
                        List<FieldDeclaration> listField = classDef.getFields();
                        // Get all the constructor
                        List<ConstructorDeclaration> listConstructor = classDef.getConstructors();
//                        List<ConstructorDeclaration> listConstructor = classDef.findAll(ConstructorDeclaration.class,
//                                constructorDeclaration -> {
//                                    return constructorDeclaration.findAncestor(ClassOrInterfaceDeclaration.class).get().getName() ==
//                                            classDef.getName();
//                                });
                        // Loop through constructor to find applicable switch

                        Expression initializer = null;
                        for (int j = 0; j < listField.size(); j++) {
                            // Check if contain assignment
                            if (listField.get(j).toString().contains("=")) {
                                VariableDeclarator temp = listField.get(j).findFirst(VariableDeclarator.class).get();
                                if (temp.getName().toString().contains(varName.toString())) {
                                    Expression value = temp.getInitializer().get();
                                    initializer = recursiveNode(value);
                                    break;
                                }
                            }
                        }
                        // Default constructor case
                        if (initializer == null) {
                            try {
                                ConstructorDeclaration defConstructor = classDef.getDefaultConstructor().get();
                                List<AssignExpr> varDeclareList = defConstructor.findAll(AssignExpr.class);
                                for (AssignExpr varDeclare : varDeclareList) {
                                    // if contain the variable name
                                    if (varDeclare.getTarget().toString().contains(varName.toString())) {
                                        Expression value = varDeclare.getValue();
                                        initializer = recursiveNode(value);
                                    }
                                }
                            } catch (Exception E) {
//                                System.out.println("NO Default Constructor");
                            }

                        }
                        // Check whether initializer is still null after check in field
                        if (initializer == null) {
                            for (int j = 0; j < listConstructor.size(); j++) {
                                ConstructorDeclaration currConstructor = listConstructor.get(j);
                                BlockStmt block = currConstructor.getBody();
                                List<AssignExpr> varDeclareList = currConstructor.findAll(AssignExpr.class);

                                for (AssignExpr varDeclare : varDeclareList) {
                                    // if contain the variable name
                                    if (varDeclare.getTarget().toString().contains(varName.toString())) {
                                        Expression value = varDeclare.getValue();
                                        initializer = recursiveNode(value);
                                    }
                                }
                                if (initializer != null) {
                                    break;
                                }
                            }
                        }
                        if (initializer != null) {
                            returnValue[0] = initializer;
                        } else {
                            returnValue[0] = null;
                        }
                    });
                    return returnValue[0];
                }

                try {
                    // if no exception, meaning the object is local
                    // Current convention:
                    // if object is local, just assign using the default value from the
                    // most basic constructor or the first retrieved constructor
                    toRecurse.asFieldAccessExpr().resolve();
                    String varName = toRecurse.asFieldAccessExpr().getName().toString();
                    Expression scope = toRecurse.asFieldAccessExpr().getScope();
                    Node node = ((JavaParserSymbolDeclaration) scope.asNameExpr().resolve()).getWrappedNode();
                    node.findFirst(MethodCallExpr.class).ifPresent(methodCallExpr -> {
                        String classname = methodCallExpr.getName().toString();
                        methodCallExpr.findAncestor(CompilationUnit.class).ifPresent(cu -> {
                            List<ClassOrInterfaceDeclaration> listClass = cu.findAll(ClassOrInterfaceDeclaration.class);
                            ClassOrInterfaceDeclaration processedClass = null;
                            for (int j = 0; j < listClass.size(); j++) {
                                ClassOrInterfaceDeclaration currentClass = listClass.get(j);
                                if (currentClass.getName().toString().contains(classname)) {
                                    processedClass = currentClass;
                                    break;
                                }
                            }
                            Expression initializer = null;
                            // If the relevant class if found, lets get the constructor
                            if (processedClass != null) {
                                // Loop through all available constructor to find the first
                                // applicable initializer
                                List<FieldDeclaration> listField = processedClass.getFields();
                                for (int j = 0; j < listField.size(); j++) {
                                    // Check if contain assignment
                                    if (listField.get(j).toString().contains("=")) {
                                        VariableDeclarator temp = listField.get(j).findFirst(VariableDeclarator.class).get();
                                        if (temp.getName().toString().contains(varName.toString())) {
                                            Expression value = temp.getInitializer().get();
                                            initializer = recursiveNode(value);
                                            break;
                                        }
                                    }
                                }

                                // Default constructor case
                                if (initializer == null) {
                                    try {
                                        ConstructorDeclaration defConstructor = processedClass.getDefaultConstructor().get();
                                        List<AssignExpr> varDeclareList = defConstructor.findAll(AssignExpr.class);
                                        for (AssignExpr varDeclare : varDeclareList) {
                                            // if contain the variable name
                                            if (varDeclare.getTarget().toString().contains(varName.toString())) {
                                                Expression value = varDeclare.getValue();
                                                initializer = recursiveNode(value);
                                            }
                                        }
                                    } catch (Exception E) {
                                    }

                                }

                                // Check if initializer still null
                                if (initializer == null) {
                                    List<ConstructorDeclaration> listConstructor = processedClass.getConstructors();
                                    for (int j = 0; j < listConstructor.size(); j++) {
                                        ConstructorDeclaration currConstructor = listConstructor.get(j);
                                        BlockStmt block = currConstructor.getBody();
                                        List<AssignExpr> varDeclareList = currConstructor.findAll(AssignExpr.class);
                                        for (AssignExpr varDeclare : varDeclareList) {
                                            // if contain the variable name
                                            if (varDeclare.getTarget().toString().contains(varName)) {
                                                Expression value = varDeclare.getValue();
                                                initializer = recursiveNode(value);
                                            }
                                        }
                                        if (initializer != null) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if (initializer != null) {
                                returnValue[0] = initializer;
                            } else {
                                returnValue[0] = null;
                            }
                        });
                    });
                    return returnValue[0];
                } catch (Exception E) {
                    // if exception, meaning the object is static and out of the file
                    return toRecurse;
                }
            } else if (toRecurse.isNameExpr()) {
                // Case other variables
                // Recursively process the name expressions

                // Check if it is a locally declared variable
//                System.out.println("Is name expr: " + toRecurse);
                if ((!variableName.isEmpty()) && (variableName.get(variableName.size() - 1).contains(toRecurse.asNameExpr().getName().toString()))) {
                    return toRecurse;
                }
                try {
                    // Recurse the resolved value
                    JavaParserSymbolDeclaration nameDeclaration = (JavaParserSymbolDeclaration) toRecurse.asNameExpr().resolve();
                    Node wrappedNode = nameDeclaration.getWrappedNode();
                    // this try catch is created due to the weird bug that its not always able
                    // to find the first expression, even though it exist
                    try {
                        Expression exp = wrappedNode.findFirst(Expression.class).get();
                        if ((exp.toString().trim().charAt(0) == '{') && flagArgument) {
                            return toRecurse;
                        }
                        return recursiveNode(wrappedNode.findFirst(Expression.class).get());
                    } catch (Exception E5) {
                        if (wrappedNode.toString().contains("=")) {
                            String expStr = wrappedNode.toString().substring(wrappedNode.toString().indexOf("=") + 1);
                            if ((expStr.trim().charAt(0) == '{') && flagArgument) {
                                return toRecurse;
                            }
                            return StaticJavaParser.parseExpression(expStr);
                        }
                    }
                    return recursiveNode(wrappedNode.findFirst(Expression.class).get());
                } catch (Exception E) {
                    // If exception happened probably:
                    // Field Declaration or Parameter Declaration
                    // Check if parameter declaration
                    try {
                        // If parameter declaration, we can do nothing about it, just return null
                        JavaParserParameterDeclaration parameterDeclaration = (JavaParserParameterDeclaration) toRecurse.asNameExpr().resolve();
                        return toRecurse;
                    } catch (Exception E2) {
                        // Check if field declaration
                        Expression[] returnValue = new Expression[1];
//                        System.out.println("Return value[0] " + returnValue[0]);
                        try {

                            JavaParserFieldDeclaration fieldDeclaration = (JavaParserFieldDeclaration) toRecurse.asNameExpr().resolve();
//                            System.out.println("Field declaration: " + fieldDeclaration);
//                            System.out.println("Resolve toString: " + toRecurse.asNameExpr().resolve());
                            String varName = fieldDeclaration.getName();
                            Expression finalToRecurse = toRecurse;

                            final String variableName = toRecurse.asNameExpr().getNameAsString();
                            final String oldApiNameFinal = apiNameOld;
                            final String newApiNameFinal = apiNameNew;

                            toRecurse.findAncestor(MethodDeclaration.class).ifPresent(methodDeclare -> {
//                                System.out.println("Method declaration: " + methodDeclare);
//                                System.out.println("toRecurse name: " + variableName);
//                                System.out.println("Old api name: " + oldApiNameFinal);
//                                System.out.println("New api name: " + newApiNameFinal);
                                List<Statement> listStatement = methodDeclare.findAll(Statement.class);
                                int i = listStatement.size() - 1;
                                while ((i >= 0) && (returnValue[0] == null)) {
                                    Statement statement = listStatement.get(i);
                                    if (!(statement.isBlockStmt()) && !(statement.isIfStmt()) && !(statement.isForStmt()) && !(statement.isForEachStmt()) && !(statement.isWhileStmt()) && !(statement.isDoStmt())) {
                                        if (!(statement.toString().contains(oldApiNameFinal) || (statement.toString().contains(newApiNameFinal)))) {
                                            try {

                                                Expression expr = statement.findFirst(Expression.class).get();
//                                                System.out.println("Expression: " + expr);

                                                // Assignment, check if name is variableName
                                                if (expr.isAssignExpr()) {
                                                    if (expr.asAssignExpr().getTarget().toString().contains(variableName)) {
                                                        // Contain the variable name
                                                        returnValue[0] = expr.asAssignExpr().getValue();
//                                                        System.out.println("Assign expression: " + returnValue[0]);
                                                    }
                                                } else if (expr.isMethodCallExpr()) {
                                                    // Method call need to recurse and get the method call body
                                                    JavaParserMethodDeclaration method = (JavaParserMethodDeclaration) expr.asMethodCallExpr().resolve();
                                                    MethodDeclaration methodDeclaration = method.getWrappedNode();
//                                                    System.out.println("method call: " + methodDeclaration);
                                                    List<Statement> statementInMethod = methodDeclaration.findAll(Statement.class);
                                                    int j = statementInMethod.size() - 1;
                                                    while ((j >= 0) && (returnValue[0] == null)) {
                                                        Statement statementMethod = statementInMethod.get(j);
                                                        if (!(statementMethod.isBlockStmt()) && !(statementMethod.isIfStmt()) && !(statementMethod.isForStmt()) && !(statementMethod.isForEachStmt()) && !(statementMethod.isWhileStmt()) && !(statementMethod.isDoStmt())) {
                                                            if (!(statementMethod.toString().contains(oldApiNameFinal) || (statementMethod.toString().contains(newApiNameFinal)))) {
                                                                try {
                                                                    Expression exprMethod = statementMethod.findFirst(Expression.class).get();
//                                                                    System.out.println("ExpressionMethod: " + exprMethod);

                                                                    // Assignment, check if name is variableName
                                                                    if (exprMethod.isAssignExpr()) {
                                                                        if (exprMethod.asAssignExpr().getTarget().toString().contains(variableName)) {
                                                                            // Contain the variable name
                                                                            returnValue[0] = exprMethod.asAssignExpr().getValue();
//                                                                            System.out.println("Assign expression Method: " + returnValue[0]);
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    // e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                        j--;
                                                    }

                                                }

                                            } catch (Exception e) {
                                                // e.printStackTrace();
                                            }
                                        }
//                                        System.out.println("Statement: " + statement);

                                    }
                                    i--;
                                }
                            });

                            if (returnValue[0] == null) {
                                toRecurse.findAncestor(ClassOrInterfaceDeclaration.class).ifPresent(classDef -> {
                                    Expression initializer = null;

                                    // Also get all the field declarations
                                    List<AssignExpr> list_assignment = classDef.findAll(AssignExpr.class);
//                                for (AssignExpr assignment : list_assignment) {
//                                    System.out.println("Assignment: ");
//                                    System.out.println(assignment);
//                                }
                                    List<FieldDeclaration> listField = classDef.getFields();
                                    // Get all the constructor
                                    List<ConstructorDeclaration> listConstructor = classDef.getConstructors();
                                    // Loop through constructor to find applicable switch

                                    if (initializer == null) {
                                        for (int j = 0; j < listField.size(); j++) {
                                            // Check if contain assignment
                                            if (listField.get(j).toString().contains("=")) {
                                                VariableDeclarator temp = listField.get(j).findFirst(VariableDeclarator.class).get();
                                                if (temp.getName().toString().contains(varName)) {
                                                    initializer = temp.getInitializer().get();
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    // Default constructor case
                                    if (initializer == null) {
                                        try {
                                            ConstructorDeclaration defConstructor = classDef.getDefaultConstructor().get();
                                            List<AssignExpr> varDeclareList = defConstructor.findAll(AssignExpr.class);
                                            for (AssignExpr varDeclare : varDeclareList) {
                                                // if contain the variable name
                                                if (varDeclare.getTarget().toString().contains(varName)) {
                                                    Expression value = varDeclare.getValue();
                                                    initializer = recursiveNode(value);
                                                }
                                            }
                                        } catch (Exception E1) {
                                        }
                                    }

                                    // Check whether initializer is still null after check in field
                                    if (initializer == null) {
                                        for (int j = 0; j < listConstructor.size(); j++) {
                                            ConstructorDeclaration currConstructor = listConstructor.get(j);
                                            BlockStmt block = currConstructor.getBody();
                                            List<AssignExpr> varDeclareList = currConstructor.findAll(AssignExpr.class);

                                            for (AssignExpr varDeclare : varDeclareList) {
                                                // if contain the variable name
                                                if (varDeclare.getTarget().toString().contains(varName)) {
                                                    Expression value = varDeclare.getValue();
                                                    initializer = recursiveNode(value);
                                                }
                                            }
                                            if (initializer != null) {
                                                break;
                                            }
                                        }
                                    }

                                    // If constructor still cannot find relevant one, go search through the whole class
                                    if (initializer == null) {
                                        List<AssignExpr> listAssign = classDef.findAll(AssignExpr.class);
                                        for (int i = 0; i < listAssign.size(); i++) {
                                            AssignExpr value = listAssign.get(i);
                                            if (value.getTarget().toString().contains(varName)) {
                                                Expression expr = value.getValue();
                                                expr = recursiveNode(expr);
                                                if (expr != null) {
                                                    initializer = expr;
                                                    break;
                                                }
                                            }

                                        }
                                    }

                                    if (initializer != null) {
                                        returnValue[0] = initializer;
                                    } else {
                                        returnValue[0] = finalToRecurse;
                                    }
                                });
                            }

                            if ((returnValue[0].toString().trim().charAt(0) == '{') && flagArgument){
                                return toRecurse;
                            }
                            return returnValue[0];
                        } catch (Exception E3) {
                            return toRecurse;
                        }

                    }
                }

            }  else if (toRecurse.isMethodCallExpr()) {
                // Case method call expression
                // Copy the method then process the parameters of the method to make sure its closed/primitive
                // Then also process the content of the method
                MethodCallExpr expression = toRecurse.asMethodCallExpr();
                boolean hasScope = expression.getScope().isPresent();

                // If has scope, resolve the scope first
                if (hasScope) {
                    Expression scope = expression.getScope().get();
                    scope = recursiveNode(scope);
                    expression.setScope(scope);
                }




                // Then, resolve the arguments
                flagArgument = true;
                NodeList<Expression> arguments = expression.getArguments();
                for (int i = 0; i < arguments.size(); i++) {
                    Expression newArgument = recursiveNode(arguments.get(i));
                    // if it is a name expression, we might need to copy it
                    if (newArgument.isNameExpr()) {
                        flagArgument = false;
                        JavaParserSymbolDeclaration valueDeclaration = (JavaParserSymbolDeclaration) newArgument.asNameExpr().resolve();

                        ResolvedType type = newArgument.calculateResolvedType();
                        String statement = type.describe() + " " + valueDeclaration.getWrappedNode() + ";";
                        Statement stmt = StaticJavaParser.parseStatement(statement);
                        // add to the code block

                        BlockStmt blockStmt = expression.findAncestor(BlockStmt.class).get();
                        boolean isExist = false;
                        NodeList<Statement> listStmt = blockStmt.getStatements();

                        for (int z = 0; z < listStmt.size(); z++ ) {
                            if (listStmt.get(z).toString().contains(statement)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            blockStmt.addStatement(0, stmt);
                        }

                        flagArgument = true;
                    }

                    arguments.set(i, newArgument);
                }
                flagArgument = false;
                expression.setArguments(arguments);



                MethodDeclaration methodDeclare = null;
                boolean isOutOfScope = false;
                // Then process the method call content
                try {
                    JavaParserMethodDeclaration method = (JavaParserMethodDeclaration) toRecurse.asMethodCallExpr().resolve();
                    MethodDeclaration methodDeclaration = method.getWrappedNode();
                    // Check whether the method is already exist
                    if (!isMethodExist(methodDeclaration)) {
                        variableName.add(new ArrayList<String>());
                        try {
                            BlockStmt bodyBlock = methodDeclaration.getBody().get();
                            NodeList<Statement> listStatement = bodyBlock.getStatements();
                            // add parameters into the variable name first
                            NodeList<Parameter> listParam = methodDeclaration.getParameters();
                            for (int i = 0; i < listParam.size(); i++) {
                                variableName.get(variableName.size() - 1).add(listParam.get(i).getName().asString());
                            }
                            for (AtomicInteger i = new AtomicInteger(); i.get() < listStatement.size(); i.getAndIncrement()) {
                                Statement currStmt = listStatement.get(i.get());
                                boolean result = processStatement(currStmt);
                                if (result == false) {
                                    i.getAndDecrement();
                                }
                            }
                        } catch (Exception E) {
                        }
                        variableName.remove(variableName.size() - 1);
                        methodDeclare = methodDeclaration;
                    } else {
                        return expression;
                    }
                } catch (Exception E) {
                    isOutOfScope = true;
                    return expression;
                }
                if (!hasScope) {
                    listMethodName.add(methodDeclare.getNameAsString() + methodDeclare.getParameters().toString());
                    listMethodDeclaration.add(methodDeclare);
                } else {
                    if (expression.getScope().get().isThisExpr()) {
                        listMethodName.add(methodDeclare.getNameAsString() + methodDeclare.getParameters().toString());
                        listMethodDeclaration.add(methodDeclare);
                        expression.setScope(null);
                    } else {
                        // CONTINUE THIS ONE TOMMOROW
                        // I guess resolving class and removing unimportant stuff from the class will be hard since a lot of
                        // things need to be considered
                        // Therefore for now, just copy the whole class

                        // the bug is here, the method class is null because it has no parent
                        ClassOrInterfaceDeclaration methodClass = null;
                        if (methodDeclare.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
                            methodClass = methodDeclare.findAncestor(ClassOrInterfaceDeclaration.class).get();
                        } else {
                        }
                        if (listAddedClassName.contains(methodClass.getNameAsString())) {
                            // Case class already copied
                            // Simply fix the method
                            ClassOrInterfaceDeclaration copiedClass = null;
                            for (int i = 0; i < listAddedClass.size(); i++) {
                                // Find the copied class
                                if (listAddedClass.get(i).getNameAsString().equals(methodClass.getNameAsString())) {
                                    copiedClass = listAddedClass.get(i);
                                    // Change the method in the copied class
                                    List<MethodDeclaration> listMethod = copiedClass.getMethods();
                                    for (int j = 0; j < listMethod.size(); j++) {
                                        if ((listMethod.get(j).getNameAsString().equals(methodDeclare.getNameAsString())) &&
                                                (listMethod.get(j).getParameters().equals(methodDeclare.getParameters()))) {
                                            copiedClass.remove(listMethod.get(j));
                                            copiedClass.addMember(methodDeclare);
                                            listMethod.set(j, methodDeclare);
                                            break;
                                        }
                                    }
                                    listAddedClass.set(i, copiedClass);
                                    break;
                                }
                            }
                        } else {
                            String methodName = methodDeclare.getName().asString();
                            List<MethodDeclaration> listMethod = methodClass.getMethodsByName(methodName);
                            for (int i = 0; i < listMethod.size(); i++) {
                                MethodDeclaration currMethod = listMethod.get(i);
                                if (currMethod.getParameters().equals(methodDeclare.getParameters())) {
                                    methodClass.remove(currMethod);
                                    methodClass.addMember(methodDeclare);
                                    break;
                                }
                            }


                            listAddedClass.add(methodClass);
                            listAddedClassName.add(methodClass.getNameAsString());
                        }
                    }
                }

                for (int i = 0; i < listAddedClass.size(); i++) {
                    listAddedClass.get(i).setPublic(false);
                    listAddedClass.get(i).setPrivate(false);
                    listAddedClass.get(i).setStatic(false);
                    listAddedClass.get(i).setInterface(false);
                    listAddedClass.get(i).setAbstract(false);
                    listAddedClass.get(i).setFinal(false);
                }

                return expression;
            } else if (toRecurse.isLiteralExpr()) {
                // Case of literal expression, simply return
                return toRecurse;
            } else if (toRecurse.isBinaryExpr()) {
                try {
                    toRecurse.asBinaryExpr().setLeft(recursiveNode(toRecurse.asBinaryExpr().getLeft()));
                    toRecurse.asBinaryExpr().setRight(recursiveNode(toRecurse.asBinaryExpr().getRight()));
                } catch (Exception E3) {

                }
                return toRecurse;
            } else if (toRecurse.isObjectCreationExpr()) {
                ObjectCreationExpr objectCreate = toRecurse.asObjectCreationExpr();
                NodeList<Expression> expList = objectCreate.getArguments();
                for (int i = 0; i < expList.size(); i++) {
                    expList.set(i, recursiveNode(expList.get(i)));
                }
                objectCreate = objectCreate.setArguments(expList);
                return objectCreate;
            } else if(toRecurse.isCastExpr()) {
                Expression exp = toRecurse.asCastExpr().getExpression();
                toRecurse = toRecurse.asCastExpr().setExpression(recursiveNode(exp));
                return toRecurse;
            } else {
                return toRecurse;
            }
//            return null;
        }

        private static int flag = 0;
        @Override
        public void visit(IfStmt ifstmt, List<String> collector) {
            super.visit(ifstmt, collector);
            // Check if has else and contains Build.Version
            // also need to check if the if and else statement has the API invocation

            if ((ifstmt.getCondition().toString().contains("Build.VERSION") || ifstmt.getCondition().toString().contains("VERSION.SDK")) &&
                    ifstmt.hasElseBranch() && ifstmt.getThenStmt().toString().contains("classNameVariable") &&
                    ifstmt.getElseStmt().get().toString().contains("classNameVariable")) {
                Statement ifBranch = ifstmt.getThenStmt();

                Optional<Statement> elseBranchOptional = ifstmt.getElseStmt();
                if (!elseBranchOptional.isPresent()) {
                    return;
                }
                Statement elseBranch = elseBranchOptional.get();



                // Process to delete uninmportant and unrelated stuff first
                // Declare important and related code
                ArrayList<String> matches = new ArrayList<>(Arrays.asList("parameterVariable", "classNameVariable", apiNameNew, apiNameOld));
                // Delete unrelated statements
                BlockStmt ifBlock = ifBranch.asBlockStmt();
                NodeList<Statement> listIfBlock = ifBlock.getStatements();

                String ifClassName = "";
                for (int i = listIfBlock.size() - 1; i >= 0; i--) {
                    boolean contain = false;
                    for (String s : matches) {
                        // Case does not contain the important stuff, delete
                        if (listIfBlock.get(i).toString().contains(s)) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {
                        try {
                            VariableDeclarator temp = listIfBlock.get(i).findFirst(VariableDeclarator.class).get();
                            if (temp.getName().asString().contains("classNameVariable")) {
                                ifClassName = temp.getInitializer().get().toString();
                            }
                            matches.add(temp.getInitializer().get().toString().trim());
                            // SHOULD CHECK IF METHOD AND HAS SCOPE
                            if (temp.getInitializer().get().isMethodCallExpr()) {
                                if (temp.getInitializer().get().asMethodCallExpr().getScope().isPresent()) {
                                    matches.add(temp.getInitializer().get().asMethodCallExpr().getScope().get().toString().trim());
                                    NodeList<Expression> arguments = temp.getInitializer().get().asMethodCallExpr().getArguments();
                                    for (int z = 0; z < arguments.size(); z++) {
                                        matches.add(arguments.get(z).toString());
                                    }
                                }
                            }
                        } catch (Exception E10) {

                        }
                    }
                    if (!contain) {
                        ifBlock.remove(listIfBlock.get(i));
                    }
                }


                String elseClassName = "";
                matches = new ArrayList<>(Arrays.asList("parameterVariable", "classNameVariable", apiNameNew, apiNameOld)) ;
                BlockStmt elseBlock = elseBranch.asBlockStmt();
                NodeList<Statement> listElseBlock = elseBlock.getStatements();
                for (int i = listElseBlock.size() - 1; i >= 0; i--) {
                    boolean contain = false;
                    for (String s : matches) {
                        // Case does not contain the important stuff, delete
                        if (listElseBlock.get(i).toString().contains(s)) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {

                        try {
                            VariableDeclarator temp = listElseBlock.get(i).findFirst(VariableDeclarator.class).get();
                            if (temp.getName().asString().contains("classNameVariable")) {
                                elseClassName = temp.getInitializer().get().toString();
                            }
                            matches.add(temp.getInitializer().get().toString().trim());
                            // SHOULD CHECK IF METHOD AND HAS SCOPE
                            if (temp.getInitializer().get().isMethodCallExpr()) {
                                if (temp.getInitializer().get().asMethodCallExpr().getScope().isPresent()) {
                                    matches.add(temp.getInitializer().get().asMethodCallExpr().getScope().get().toString().trim());
                                    NodeList<Expression> arguments = temp.getInitializer().get().asMethodCallExpr().getArguments();
                                    for (int z = 0; z < arguments.size(); z++) {
                                        matches.add(arguments.get(z).toString());
                                    }
                                }
                            }
                        } catch (Exception E10) {

                        }
                    }
                    if (!contain) {
                        elseBlock.remove(listElseBlock.get(i));
                    }
                }

                boolean isSameClassName = false;
                if (ifClassName.contains(elseClassName) || elseClassName.contains(ifClassName)) {
                    isSameClassName = true;
                }

                // DELETE FOR NON DATA FLOW VERSION
                // Time to crawl for the related code
                for (int i = 0; i < listElseBlock.size() - 1; i++) {
                    Statement currStmt = listElseBlock.get(i);

                    boolean finalIsSameClassName1 = isSameClassName;
                    currStmt.findFirst(VariableDeclarator.class).ifPresent(variableDeclarator -> {
                        if (variableDeclarator.getName().asString().contains("classNameVariable") && finalIsSameClassName1) {

                        } else {
                            Expression toRecurse = variableDeclarator.getInitializer().get();
                            variableName = new ArrayList<>();
                            Expression result = recursiveNode(toRecurse);
                            if (result == null) {
                                variableDeclarator.setInitializer("null");
                            } else {
                                if (result.isMethodCallExpr()) {
                                    if (!result.asMethodCallExpr().getScope().isPresent()) {
                                        listAddedMethodName.add(result.asMethodCallExpr().getNameAsString());
                                    }
                                } else if (result.isNameExpr()) {
                                    listAddedName.add(result.asNameExpr().getNameAsString());
                                }
                                variableDeclarator.setInitializer(result);

                            }
                        }

                    });
                }

                List<MethodDeclaration> elseMethod = listMethodDeclaration;
                List<ClassOrInterfaceDeclaration> elseClass = listAddedClass;
                List<String> elseName = listAddedName;
                List<String> elseMethodName = new ArrayList<>();
                for (int i = 0; i < elseMethod.size(); i++) {
                    elseMethodName.add(elseMethod.get(i).getNameAsString());
                }


                listMethodDeclaration = new ArrayList<>();
                listMethodName = new ArrayList<>();
                listAddedClassName = new ArrayList<>();
                listAddedClass = new ArrayList<>();
                listAddedName = new ArrayList<>();
                listAddedMethodName = new ArrayList<>();




                // DELETE FOR NON DATA FLOW VERSION
                for (int i = 0; i < listIfBlock.size() - 1; i++) {
                    Statement currStmt = listIfBlock.get(i);
                    boolean finalIsSameClassName = isSameClassName;
                    currStmt.findFirst(VariableDeclarator.class).ifPresent(variableDeclarator -> {
                        if (variableDeclarator.getName().asString().contains("classNameVariable") && finalIsSameClassName) {
                        } else {
                            Expression toRecurse = variableDeclarator.getInitializer().get();
                            variableName = new ArrayList<>();
                            Expression result = recursiveNode(toRecurse);
                            if (result == null) {
                                variableDeclarator.setInitializer("null");
                            } else {
                                // To Add into the list of added name
                                if (result.isMethodCallExpr()) {
                                    if (!result.asMethodCallExpr().getScope().isPresent()) {
                                        listAddedMethodName.add(result.asMethodCallExpr().getNameAsString());
                                    }
                                } else if (result.isNameExpr()) {
                                    listAddedName.add(result.asNameExpr().getNameAsString());
                                }
                                variableDeclarator.setInitializer(result);
                            }
                        }

                    });
                }

                List<MethodDeclaration> ifMethod = listMethodDeclaration;
                List<ClassOrInterfaceDeclaration> ifClass = listAddedClass;
                List<String> ifName = listAddedName;
                List<String> ifMethodName = new ArrayList<>();
                for (int i = 0; i < ifMethod.size(); i++) {
                    ifMethodName.add(ifMethod.get(i).getNameAsString());
                }

                matches = new ArrayList<>(Arrays.asList("parameterVariable", "classNameVariable", apiNameNew, apiNameOld)) ;

                for (int i = listIfBlock.size() - 2; i >= 0; i--) {
                    boolean contain = false;
                    String toProcess = listIfBlock.get(i).toString();
                    if (toProcess.contains("=")) {
                        toProcess = toProcess.substring(0, toProcess.indexOf("="));
                        if (toProcess.contains(" ")) {
                            toProcess = toProcess.substring(toProcess.indexOf(" "));
                        }
                    }
                    for (String s : matches) {
                        // Case does not contain the important stuff, delete
                        if (toProcess.contains(s)) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {
                        try {
                            VariableDeclarator temp = listIfBlock.get(i).findFirst(VariableDeclarator.class).get();
                            if (temp.getName().asString().contains("classNameVariable")) {
                                ifClassName = temp.getInitializer().get().toString();
                            }
                            matches.add(temp.getInitializer().get().toString().trim());
                            // SHOULD CHECK IF METHOD AND HAS SCOPE
                            if (temp.getInitializer().get().isMethodCallExpr()) {
                                if (temp.getInitializer().get().asMethodCallExpr().getScope().isPresent()) {
                                    matches.add(temp.getInitializer().get().asMethodCallExpr().getScope().get().toString().trim());
                                    NodeList<Expression> arguments = temp.getInitializer().get().asMethodCallExpr().getArguments();
                                    for (int z = 0; z < arguments.size(); z++) {
                                        matches.add(arguments.get(z).toString());
                                    }
                                }
                            }
                        } catch (Exception E10) {

                        }
                    }
                    if (!contain) {
                        ifBlock.remove(listIfBlock.get(i));
                    }
                }


                matches = new ArrayList<>(Arrays.asList("parameterVariable", "classNameVariable", apiNameNew, apiNameOld)) ;
                for (int i = listElseBlock.size() - 2; i >= 0; i--) {
                    boolean contain = false;
                    String toProcess = listElseBlock.get(i).toString();
                    if (toProcess.contains("=")) {
                        toProcess = toProcess.substring(0, toProcess.indexOf("="));
                        if (toProcess.contains(" ")) {
                            toProcess = toProcess.substring(toProcess.indexOf(" "));
                        }
                    }

                    for (String s : matches) {
                        // Case does not contain the important stuff, delete
                        if (toProcess.contains(s)) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {

                        try {
                            VariableDeclarator temp = listElseBlock.get(i).findFirst(VariableDeclarator.class).get();
                            if (temp.getName().asString().contains("classNameVariable")) {
                                elseClassName = temp.getInitializer().get().toString();
                            }
                            matches.add(temp.getInitializer().get().toString().trim());
                            // SHOULD CHECK IF METHOD AND HAS SCOPE
                            if (temp.getInitializer().get().isMethodCallExpr()) {
                                if (temp.getInitializer().get().asMethodCallExpr().getScope().isPresent()) {
                                    matches.add(temp.getInitializer().get().asMethodCallExpr().getScope().get().toString().trim());
                                    NodeList<Expression> arguments = temp.getInitializer().get().asMethodCallExpr().getArguments();
                                    for (int z = 0; z < arguments.size(); z++) {
                                        matches.add(arguments.get(z).toString());
                                    }
                                }
                            }
                        } catch (Exception E10) {

                        }
                    }
                    if (!contain) {
                        elseBlock.remove(listElseBlock.get(i));
                    }
                }



                // Check the API name here
                if (ifBranch.toString().contains(apiNameNew)) {
                    if (elseBranch.toString().contains(apiNameOld)) {
                        collector.add(ifstmt.toString());
                        ParseResult temp = new ParseResult();
                        temp.ifCondition = ifstmt.getCondition().toString();
                        temp.upperApiBlock = ifBranch.toString();
                        temp.lowerApiBlock = elseBranch.toString();
                        temp.upperMethodList = ifMethod;
                        temp.lowerMethodList = elseMethod;
                        temp.upperClassList = ifClass;
                        temp.lowerClassList = elseClass;
                        temp.upperNameList = ifName;
                        temp.lowerNameList = elseName;
                        temp.upperMethodName = ifMethodName;
                        temp.lowerMethodName = elseMethodName;
                        parsingResult.add(temp);
                    }
                } else if (ifBranch.toString().contains(apiNameOld)) {
                    if (elseBranch.toString().contains(apiNameNew)) {
                        collector.add(ifstmt.toString());
                        ParseResult temp = new ParseResult();
                        temp.ifCondition = ifstmt.getCondition().toString();
                        temp.upperApiBlock = elseBranch.toString();
                        temp.lowerApiBlock = ifBranch.toString();
                        temp.upperMethodList = elseMethod;
                        temp.lowerMethodList = ifMethod;
                        temp.upperClassList = elseClass;
                        temp.lowerClassList = ifClass;
                        temp.upperNameList = elseName;
                        temp.lowerNameList = ifName;
                        temp.upperMethodName = ifMethodName;
                        temp.lowerMethodName = elseMethodName;
                        parsingResult.add(temp);
                    }
                }

            }
        }
    }

    public static List<ParseResult> getMatchingIf(String apiNameOld, String apiNameNew, String filepath) throws FileNotFoundException {
//        String tempFilePath = modifyFile(filepath);
        File file = new File(filepath);
        CompilationUnit cu = StaticJavaParser.parse(file);
        List<String> ifBlocks = new ArrayList<>();
        VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector(apiNameOld, apiNameNew);
        try {
            methodNameCollector.visit(cu, ifBlocks);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return MethodNameCollector.parsingResult;
    }

    private static boolean validateInput(String[] args) {
        String oldAPI = args[0];
        String newAPI = args[1];
        if (!validateAPI(oldAPI, 0)) {
            return false;
        }
        if (!validateAPI(newAPI, 1)) {
            return false;
        }
        return true;
    }

    private static String createVariablePullCocci(String fullyQualifiedName) {
        if (!validateAPI(fullyQualifiedName, -1)) {
            return "";
        }
        int numParam = getNumParameter(fullyQualifiedName);
        String apiName = getFunctionName(fullyQualifiedName);
        String fileName = "pullVariable-" + apiName + ".cocci";
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            String parameterString = getParamCocciString(fullyQualifiedName);
            writer.println("@variable_pull@");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression " + parameterString + ";");
            }
            writer.println("@@");
            writer.println();
            writer.println("<... when exists");
            for (int i = 0; i < numParam; i++) {
                String parameterType = getParameterType(fullyQualifiedName, i);
//                writer.println("+ " + parameterType + " parameterVariable" + apiName + i + " = param" + i + ";");
                writer.println("+ " + parameterType + " parameterVariable" + i + " = param" + i + ";");
            }
            writer.println("- " + apiName + "(" + parameterString + ");");
            StringBuilder newParamString = new StringBuilder("");
            for (int i = 0; i < numParam; i++) {
                newParamString.append("parameterVariable" + i);
                if (i != numParam - 1) {
                    newParamString.append(", ");
                }
            }
            writer.println("+ " + apiName + "(" + newParamString.toString() + ");");
            writer.println("...>");
            writer.println();

            // with Class
            writer.println("@variable_pullClass@");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression classname1, " + parameterString + ";");
            } else {
                writer.println("expression classname1;");
            }
            writer.println("@@");
            writer.println();
            writer.println("<... when exists");
            for (int i = 0; i < numParam; i++) {
                String parameterType = getParameterType(fullyQualifiedName, i);
                writer.println("+ " + parameterType + " parameterVariable" + i + " = param" + i + ";");
            }
            String classType = getFunctionClassNameType(fullyQualifiedName);
            writer.println("+ " + classType + " classNameVariable" + " = classname1;");
            writer.println("- classname1." + apiName + "(" + parameterString + ");");
            writer.println("+ classNameVariable" +"." + apiName + "(" + newParamString.toString() + ");");
            writer.println("...>");
            writer.println();


            // Assignment no class
            writer.println("@variable_pull_assignment@");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression " + parameterString + ";");
                writer.println("identifier iden1;");
            }
            writer.println("@@");
            writer.println();
            writer.println("<... when exists");
            for (int i = 0; i < numParam; i++) {
                String parameterType = getParameterType(fullyQualifiedName, i);
                writer.println("+ " + parameterType + " parameterVariable" + i + " = param" + i + ";");
            }
            writer.println("- iden1 = " + apiName + "(" + parameterString + ");");
            writer.println("+ iden1 = " + apiName + "(" + newParamString.toString() + ");");
            writer.println("...>");
            writer.println();
            // Assignment with Class
            writer.println("@variable_pullClass_assignment@");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression classname1, " + parameterString + ";");
            } else {
                writer.println("expression classname1;");
            }
            writer.println("identifier iden1;");
            writer.println("@@");
            writer.println();
            writer.println("<... when exists");
            for (int i = 0; i < numParam; i++) {
                String parameterType = getParameterType(fullyQualifiedName, i);
                writer.println("+ " + parameterType + " parameterVariable" + i + " = param" + i + ";");
            }
            writer.println("+ " + classType + " classNameVariable" + " = classname1;");
            writer.println("- iden1 = classname1." + apiName + "(" + parameterString + ");");
            writer.println("+ iden1 = classNameVariable" + "." + apiName + "(" + newParamString.toString() + ");");
            writer.println("...>");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        return fileName;
    }

    private static String createImportCocci(String fullyQualifiedName) {
        String apiName = getFunctionName(fullyQualifiedName);
        String fileName = "addImport-" + apiName + ".cocci";
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            // Import statement
            writer.println("@add_import@");
            writer.println("@@");
            // Loop through all import
            ArrayList<String> allImport = getAllImport(fullyQualifiedName);
            for (int i = 0; i < allImport.size(); i++) {
                writer.println("+ import " + allImport.get(i) + ";");
            }
            writer.println("import ...;");

            writer.println("...");
            writer.close();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        return fileName;
    }

    private static String createStatementPullCocci(String fullyQualifiedName) {
        // IMPORTANT
        // Need to incorporate return value later
        String returnValue = getReturnValue(fullyQualifiedName);
        if (!validateAPI(fullyQualifiedName, -1)) {
            return "";
        }
        int numParam = getNumParameter(fullyQualifiedName);
        String apiName = getFunctionName(fullyQualifiedName);
        String fileName = "pullStatement-" + apiName + ".cocci";
        // Start writing to file
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            String parameterString = getParamCocciString(fullyQualifiedName);
            // Cocci script for cases without classname
            writer.println("@pull@");
            writer.println("statement S;");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression " + parameterString + ";");
            }
            writer.println("@@");
            writer.println(apiName + "(" + parameterString + ")@S");
            writer.println();
            writer.println("@add@");
            writer.println("statement pull.S;");
            if (numParam > 0) {
                StringBuilder temp = new StringBuilder("");
                temp.append("expression ");
                for (int i = 0; i < numParam; i++) {
                    temp.append("pull.param" + i);
                    if (i != numParam - 1) {
                        temp.append(", ");
                    }
                }
                temp.append(";");
                writer.println(temp);
            }
            writer.println("@@");
            writer.println("+ " + returnValue + " tempFunctionReturnValue" + ";");
            writer.println("+ tempFunctionReturnValue" + " = " + apiName + "(" + parameterString + ");");
            writer.println("S");
            writer.println();
            writer.println("@@");
            writer.println("statement pull.S;");
            if (numParam > 0) {
                StringBuilder temp = new StringBuilder("");
                temp.append("expression ");
                for (int i = 0; i < numParam; i++) {
                    temp.append("pull.param" + i);
                    if (i != numParam - 1) {
                        temp.append(", ");
                    }
                }
                temp.append(";");
                writer.println(temp);
            }
            writer.println("@@");
            writer.println("- "+ apiName + "(" + parameterString + ")@S");
            writer.println("+ tempFunctionReturnValue");
            // Cocci script for cases with classname
            writer.println();
            writer.println("@pullClass@");
            writer.println("statement S;");
            if (numParam > 0) {
                // Write based on the number of parameters
                writer.println("expression " + parameterString + ";");
            }
            writer.println("expression classname1;");
            writer.println("@@");
            writer.println("classname1." + apiName + "(" + parameterString + ")@S");
            writer.println();
            writer.println("@addClass@");
            writer.println("statement pullClass.S;");
            if (numParam > 0) {
                StringBuilder temp = new StringBuilder("");
                temp.append("expression ");
                for (int i = 0; i < numParam; i++) {
                    temp.append("pullClass.param" + i);
                    if (i != numParam - 1) {
                        temp.append(", ");
                    }
                }
                temp.append(";");
                writer.println(temp);
            }
            writer.println("expression pullClass.classname1;");
            writer.println("@@");
            writer.println("+ " + returnValue + " tempFunctionReturnValue" + ";");
            writer.println("+ tempFunctionReturnValue" + " = classname1." + apiName + "(" + parameterString + ");");
            writer.println("S");
            writer.println();
            writer.println("@@");
            writer.println("statement pullClass.S;");
            if (numParam > 0) {
                StringBuilder temp = new StringBuilder("");
                temp.append("expression ");
                for (int i = 0; i < numParam; i++) {
                    temp.append("pullClass.param" + i);
                    if (i != numParam - 1) {
                        temp.append(", ");
                    }
                }
                temp.append(";");
                writer.println(temp);
            }
            writer.println("expression pullClass.classname1;");
            writer.println("@@");
            writer.println("- classname1."+ apiName + "(" + parameterString + ")@S");
            writer.println("+ tempFunctionReturnValue");
            writer.println();
            writer.println();
            writer.println("@@");
            writer.println("@@");
            writer.println("- tempFunctionReturnValue;");
            writer.close();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }

        return fileName;
    }


    public static void newNormalizeFileCocci(String fullyQualifiedName, String filepath) {
        File file = new File(filepath);
        String functionName = getFunctionName(fullyQualifiedName);
        NormalizeFileVisitor apiLocate = new NormalizeFileVisitor(functionName, fullyQualifiedName);
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        }

        List<String> temp = new ArrayList<>();
        apiLocate.visit(cu, temp);

        // Add new import
        String importCocci = createImportCocci(fullyQualifiedName);
        StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + importCocci + " " + filepath + " --in-place");
        try {
            Runtime.getRuntime().exec(spatchCommand.toString());
            Thread.sleep(1200);
            File delete = new File(importCocci);
            delete.delete();
        } catch (IOException | InterruptedException e) {
            // e.printStackTrace();
        }

        file.delete();
        try {
            Files.write(new File(filepath).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    public static String createUpdateApiCocci(List<ParseResult> listIfs, String oldApiName, String newApiName, boolean haveReturn, String outputPath) {
        if (listIfs.size() > 0) {
            String ifCondition = listIfs.get(0).ifCondition;

            String[] upperApiLines = listIfs.get(0).upperApiBlock.split("\\r?\\n");
            String[] lowerApiLines = listIfs.get(0).lowerApiBlock.split("\\r?\\n");

            List<MethodDeclaration> upperMethod = listIfs.get(0).upperMethodList;
            List<MethodDeclaration> lowerMethod = listIfs.get(0).lowerMethodList;

            List<ClassOrInterfaceDeclaration> upperClass = listIfs.get(0).upperClassList;
            List<ClassOrInterfaceDeclaration> lowerClass = listIfs.get(0).lowerClassList;

            List<String> upperName = listIfs.get(0).upperNameList;
            List<String> lowerName = listIfs.get(0).lowerNameList;

            List<String> upperMethodName = listIfs.get(0).upperMethodName;
            List<String> lowerMethodName = listIfs.get(0).lowerMethodName;


            List<String> listUpper = Arrays.asList(upperApiLines);
            List<String> listLower = Arrays.asList(lowerApiLines);

            List<String> allLine;
            List<String> diffLine;
            String apiLine;

            allLine = new ArrayList<>(listUpper);
            diffLine = new ArrayList<>();

            for (int i = 0; i < listLower.size(); i++) {
                if (!listUpper.contains(listLower.get(i))) {
                    diffLine.add(listLower.get(i));
                }
            }

            String[] matches = new String[] {"parameterVariable", "classNameVariable", oldApiName, newApiName};
            boolean alwaysDelete = false;
            for (int i = 0; i < allLine.size(); i++) {
                String temp = allLine.get(i);
                boolean needDelete = true;
                for (String s : matches)
                {
                    if (allLine.get(i).contains(s)) {
                        needDelete = false;
                        break;
                    }
                }
                if ((allLine.get(i).contains(newApiName) || allLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                    alwaysDelete = true;
                    continue;
                }
                if (alwaysDelete) {
                    allLine.remove(i);
                    i -= 1;
                }
            }
            alwaysDelete = false;
            for (int i = 0; i < diffLine.size(); i++) {
                String temp = diffLine.get(i);
                boolean needDelete = false;

                for (String s : matches)
                {
                    if (diffLine.get(i).contains(s)) {
                        needDelete = false;

                        break;
                    }
                }
                if ((diffLine.get(i).contains(newApiName) || diffLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                    alwaysDelete = true;
                    continue;
                }
                if (alwaysDelete) {
                    diffLine.remove(i);
                    i -= 1;
                } else if (needDelete) {
                    diffLine.remove(i);
                    i -= 1;
                }
            }
            for (int i = 0; i < diffLine.size(); i++) {
                // Check if assignment
                if (diffLine.get(i).contains("=")) {
                    boolean rhsExist = false;
                    String diff = diffLine.get(i);
                    String rhs = diff.substring(diffLine.get(i).indexOf("=") + 1).trim();
                    for (int j = 0; j < allLine.size(); j++) {
                        String allLhs = "";
                        if (allLine.get(j).contains(rhs)) {
                            rhsExist = true;
                            if (allLine.get(j).contains("=") && allLine.get(j).contains("parameter")) {
                                allLhs = allLine.get(j).substring(allLine.get(j).indexOf("parameter"), allLine.get(j).indexOf("=")).trim();
                                allLhs = allLhs.replace("parameterVariable", "REPLACE") + ";";
                            }
                        }
                        if ((rhsExist) && allLine.get(j).contains("parameter")) {
                            if (allLhs.length() > 0) {
                                String newDiff = diff.replace(rhs, allLhs);
                                diffLine.set(i, newDiff);
                            }
                            break;
                        }
                    }
                }
            }


            int expressionIterator = 0;
            int assignmentIterator = 0;
            boolean classNameAvailable = false;
            for (int i = 0; i < allLine.size() - 1; i++) {
                String tempValue = allLine.get(i);
                if (tempValue.contains("parameterVariable")) {
                    String replacementString = tempValue.replace("parameterVariable", "iden");
                    String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                    replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                    allLine.set(i, replacementString);

                    assignmentIterator++;
                    expressionIterator++;
                }
                if (tempValue.contains("classNameVariable")) {
                    String replacementString = tempValue.replace("classNameVariable", "classIden");
                    if (!(tempValue.contains(oldApiName) || tempValue.contains(newApiName))) {
                        String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                        replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                    }
                    allLine.set(i, replacementString);
                    classNameAvailable = true;
                }
            }
            String lastLineReplacement = allLine.get(allLine.size()-1);
            lastLineReplacement = lastLineReplacement.replace("classNameVariable", "classIden");
            lastLineReplacement = lastLineReplacement.replace("parameterVariable", "iden");
            allLine.set(allLine.size()-1, lastLineReplacement);


            boolean classChangeFlag = false;
            for (int i = 0; i < diffLine.size(); i++) {
                String replacementString = diffLine.get(i).replace("parameterVariable", "iden");
                if (diffLine.get(i).contains("classNameVariable") && !(diffLine.get(i).contains(oldApiName) || diffLine.get(i).contains(newApiName))) {
                    classChangeFlag = true;
                    replacementString = replacementString.replace("classNameVariable", "newClassName");
                }
                if (classChangeFlag) {
                    replacementString = replacementString.replace("classNameVariable", "newClassName");
                } else {
                    replacementString = replacementString.replace("classNameVariable", "classIden");
                }

                diffLine.set(i, replacementString);
            }
            // Writing the cocci file
            PrintWriter writer = null;
            String filename = outputPath;
//            String filename = oldApiName + "-" + newApiName + ".cocci";
            try {
                writer = new PrintWriter(filename);
                writer.println("@upperbottom_classname@");
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.write("expression ");
                }
                for (int i = 0; i < expressionIterator; i++) {
                    writer.write("exp" + i);
                    int temp = expressionIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("exp" + expressionIterator);
                }
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.write("identifier ");
                }
                for (int i = 0; i < assignmentIterator; i++) {
                    writer.write("iden" + i);
                    int temp = assignmentIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("classIden");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                writer.println("@@");
                writer.println("...");
                writer.println("+ if (" + ifCondition + ") {");
                // NON ASSIGNMENT CASE
                apiLine = allLine.get(allLine.size() - 1).trim();
                apiLine = apiLine.substring(apiLine.indexOf("=") + 1);

                writer.println(apiLine);
                writer.println("+ } else {");
                ArrayList<String> appearInDiff = new ArrayList<>();
                for (int i = 0; i < diffLine.size() - 1; i++) {
                    writer.write("+ ");
                    if (diffLine.get(i).contains("iden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("iden", "newParameterVariable"));
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("REPLACE")) {
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("classIden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("classIden", "newClassnameVariable"));
                    }
                    writer.println(diffLine.get(i).trim());
                }
                String otherLine = diffLine.get(diffLine.size() - 1);
                otherLine = otherLine.substring(otherLine.indexOf("=") + 1);
                String otherParameter = otherLine.substring(otherLine.indexOf("(") + 1, otherLine.indexOf(")"));
                String[] listParameter = otherParameter.split(",");
                for (int i = 0; i < listParameter.length; i++) {
                    boolean isInDiff = false;
                    for (int j = 0; j < appearInDiff.size(); j++) {
                        if (appearInDiff.get(j).trim().contains(listParameter[i].trim())) {
                            if (appearInDiff.get(j).contains("classIden")) {
                                otherLine = otherLine.replace(listParameter[i], "newClassnameVariable");
                            } else {
                                otherLine = otherLine.replace(listParameter[i], "newParameterVariable" + listParameter[i].substring(listParameter[i].length() - 1));
                            }
                            break;
                        }
                    }
                }
                writer.print("+ ");
                writer.println(otherLine.trim());
                writer.write("+ }");
                writer.println();
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            }

            // bottomupper version
            allLine = new ArrayList<>(listLower);
            diffLine = new ArrayList<>();

            for (int i = 0; i < listUpper.size(); i++) {
                if (!listLower.contains(listUpper.get(i))) {
                    diffLine.add(listUpper.get(i));
                }
            }
            alwaysDelete = false;
            for (int i = 0; i < allLine.size(); i++) {
                String temp = allLine.get(i);
                boolean needDelete = true;
                for (String s : matches)
                {
                    if (allLine.get(i).contains(s)) {
                        needDelete = false;

                        break;
                    }
                }
                if ((allLine.get(i).contains(newApiName) || allLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                    alwaysDelete = true;
                    continue;
                }
                if (alwaysDelete) {
                    allLine.remove(i);
                    i -= 1;
                }
            }
            alwaysDelete = false;
            for (int i = 0; i < diffLine.size(); i++) {
                String temp = diffLine.get(i);
                boolean needDelete = false;
                for (String s : matches)
                {
                    if (diffLine.get(i).contains(s)) {
                        needDelete = false;

                        break;
                    }
                }
                if ((diffLine.get(i).contains(newApiName) || diffLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                    alwaysDelete = true;
                    continue;
                }
                if (alwaysDelete) {
                    diffLine.remove(i);
                    i -= 1;
                } else if (needDelete) {
                    diffLine.remove(i);
                    i -= 1;
                }
            }

            for (int i = 0; i < diffLine.size(); i++) {
                // Check if assignment
                if (diffLine.get(i).contains("=")) {
                    boolean rhsExist = false;
                    String diff = diffLine.get(i);
                    String rhs = diff.substring(diffLine.get(i).indexOf("=") + 1).trim();
                    for (int j = 0; j < allLine.size(); j++) {
                        String allLhs = "";
                        if (allLine.get(j).contains(rhs)) {

                            rhsExist = true;
                            if (allLine.get(j).contains("parameter") && allLine.get(j).contains("=")) {
                                allLhs = allLine.get(j).substring(allLine.get(j).indexOf("parameter"), allLine.get(j).indexOf("=")).trim();
                                allLhs = allLhs.replace("parameterVariable", "REPLACE") + ";";
                            }
                        }
                        if ((rhsExist) && allLine.get(j).contains("parameter")) {
                            if (allLhs.length() > 0) {
                                String newDiff = diff.replace(rhs, allLhs);
                                diffLine.set(i, newDiff);
                            }

                            break;
                        }
                    }
                }
            }
            expressionIterator = 0;
            assignmentIterator = 0;
            classNameAvailable = false;
            // Need special processing for last line
            for (int i = 0; i < allLine.size() - 1; i++) {
                String tempValue = allLine.get(i);
                if (tempValue.contains("parameterVariable")) {
                    String replacementString = tempValue.replace("parameterVariable", "iden");
                    int end = tempValue.indexOf(";");
                    String rightHandSideAssignment;
                    if (end < tempValue.indexOf("=")) {
                        rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.length());
                    } else {
                        rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                    }

                    replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                    allLine.set(i, replacementString);
                    assignmentIterator++;
                    expressionIterator++;
                }
                if (tempValue.contains("classNameVariable")) {
                    String replacementString = tempValue.replace("classNameVariable", "classIden");
                    // Check if this is the line with api call
                    if (!(tempValue.contains(oldApiName) || tempValue.contains(newApiName))) {
                        String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                        replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                    }
                    allLine.set(i, replacementString);
                    classNameAvailable = true;
                }
            }
            lastLineReplacement = allLine.get(allLine.size()-1);
            lastLineReplacement = lastLineReplacement.replace("classNameVariable", "classIden");
            lastLineReplacement = lastLineReplacement.replace("parameterVariable", "iden");
            allLine.set(allLine.size()-1, lastLineReplacement);



            classChangeFlag = false;
            for (int i = 0; i < diffLine.size(); i++) {
                String replacementString = diffLine.get(i).replace("parameterVariable", "iden");
                if (diffLine.get(i).contains("classNameVariable") && !(diffLine.get(i).contains(oldApiName) || diffLine.get(i).contains(newApiName))) {
                    classChangeFlag = true;
                    replacementString = replacementString.replace("classNameVariable", "newClassName");
                }
                if (classChangeFlag) {
                    replacementString = replacementString.replace("classNameVariable", "newClassName");
                } else {
                    replacementString = replacementString.replace("classNameVariable", "classIden");
                }
                diffLine.set(i, replacementString);
            }

            // Writing the cocci file
            writer.println();
            writer.println("@bottomupper_classname@");
            if ((expressionIterator > 0) || (classNameAvailable)) {
                writer.write("expression ");
            }
            for (int i = 0; i < expressionIterator; i++) {
                writer.write("exp" + i);
                int temp = expressionIterator - 1;
                if (i != temp) {
                    writer.write(", ");
                } else {
                    if (classNameAvailable) {
                        writer.write(", ");
                    }
                }
            }
            if (classNameAvailable) {
                writer.write("exp" + expressionIterator);
            }
            if ((expressionIterator > 0) || (classNameAvailable)) {
                writer.println(";");
            }
            if ((assignmentIterator > 0) || (classNameAvailable)) {
                writer.write("identifier ");
            }
            for (int i = 0; i < assignmentIterator; i++) {
                writer.write("iden" + i);
                int temp = assignmentIterator - 1;
                if (i != temp) {
                    writer.write(", ");
                } else {
                    if (classNameAvailable) {
                        writer.write(", ");
                    }
                }
            }
            if (classNameAvailable) {
                writer.write("classIden");
            }
            if ((assignmentIterator > 0) || (classNameAvailable)) {
                writer.println(";");
            }
            writer.println("@@");
            writer.println("...");
            writer.println("+ if (" + ifCondition + ") {");


            ArrayList<String> appearInDiff = new ArrayList<>();
            for (int i = 0; i < diffLine.size() - 1; i++) {
                writer.write("+ ");
                if (diffLine.get(i).contains("iden")) {
                    String variableName = diffLine.get(i).substring(
                            diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                    appearInDiff.add(variableName);
                    diffLine.set(i, diffLine.get(i).replace("iden", "newParameterVariable"));
                    diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                }
                if (diffLine.get(i).contains("REPLACE")) {
                    diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                }
                if (diffLine.get(i).contains("classIden")) {
                    String variableName = diffLine.get(i).substring(
                            diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                    appearInDiff.add(variableName);
                    diffLine.set(i, diffLine.get(i).replace("classIden", "newClassnameVariable"));
                }
                writer.println(diffLine.get(i).trim());
            }
            String otherLine = diffLine.get(diffLine.size() - 1);
            otherLine = otherLine.substring(otherLine.indexOf("=") + 1);
            String otherParameter = otherLine.substring(otherLine.indexOf("(") + 1, otherLine.indexOf(")"));
            String[] listParameter = otherParameter.split(",");

            for (int i = 0; i < listParameter.length; i++) {
                boolean isInDiff = false;
                for (int j = 0; j < appearInDiff.size(); j++) {
                    if (appearInDiff.get(j).trim().contains(listParameter[i].trim())) {
                        if (appearInDiff.get(j).contains("classIden")) {
                            otherLine = otherLine.replace(listParameter[i], "newClassnameVariable");
                        } else {
                            otherLine = otherLine.replace(listParameter[i], "newParameterVariable" + listParameter[i].substring(listParameter[i].length() - 1));
                        }
                        break;
                    }
                }
            }
            writer.print("+ ");
            writer.println(otherLine.trim());
            writer.println("+ } else {");
            apiLine = allLine.get(allLine.size() - 1).trim();
            apiLine = apiLine.substring(apiLine.indexOf("=") + 1);
            writer.println(apiLine);
            writer.write("+ }");
            writer.println();

            if (haveReturn) {
                writer.println();
                writer.println();
                allLine = new ArrayList<>(listUpper);
                diffLine = new ArrayList<>();

                for (int i = 0; i < listLower.size(); i++) {
                    if (!listUpper.contains(listLower.get(i))) {
                        diffLine.add(listLower.get(i));
                    }
                }

                alwaysDelete = false;
                for (int i = 0; i < allLine.size(); i++) {
                    String temp = allLine.get(i);
                    boolean needDelete = true;
                    for (String s : matches)
                    {
                        if (allLine.get(i).contains(s)) {
                            needDelete = false;
                            break;
                        }
                    }
                    if ((allLine.get(i).contains(newApiName) || allLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                        alwaysDelete = true;
                        continue;
                    }
                    if (alwaysDelete) {
                        allLine.remove(i);
                        i -= 1;
                    }
                }
                alwaysDelete = false;
                for (int i = 0; i < diffLine.size(); i++) {
                    String temp = diffLine.get(i);
                    boolean needDelete = false;

                    for (String s : matches)
                    {
                        if (diffLine.get(i).contains(s)) {
                            needDelete = false;

                            break;
                        }
                    }
                    if ((diffLine.get(i).contains(newApiName) || diffLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                        alwaysDelete = true;
                        continue;
                    }
                    if (alwaysDelete) {
                        diffLine.remove(i);
                        i -= 1;
                    } else if (needDelete) {
                        diffLine.remove(i);
                        i -= 1;
                    }
                }
                // This is to fix the case of same variable but in different order
                for (int i = 0; i < diffLine.size(); i++) {
                    if (diffLine.get(i).contains("=")) {
                        boolean rhsExist = false;
                        String diff = diffLine.get(i);
                        String rhs = diff.substring(diffLine.get(i).indexOf("=") + 1).trim();
                        for (int j = 0; j < allLine.size(); j++) {
                            String allLhs = "";
                            if (allLine.get(j).contains(rhs)) {
                                rhsExist = true;
                                if (allLine.get(j).contains("parameter") && allLine.get(j).contains("=")) {
                                    allLhs = allLine.get(j).substring(allLine.get(j).indexOf("parameter"), allLine.get(j).indexOf("=")).trim();
                                    allLhs = allLhs.replace("parameterVariable", "REPLACE") + ";";
                                }

                            }
                            if ((rhsExist) && allLine.get(j).contains("parameter")) {
                                if (allLhs.length() > 0) {
                                    String newDiff = diff.replace(rhs, allLhs);
                                    diffLine.set(i, newDiff);
                                }

                                break;
                            }
                        }
                    }
                }

                expressionIterator = 0;
                assignmentIterator = 0;
                classNameAvailable = false;
                for (int i = 0; i < allLine.size() - 1; i++) {
                    String tempValue = allLine.get(i);
                    if (tempValue.contains("parameterVariable")) {
                        String replacementString = tempValue.replace("parameterVariable", "iden");
                        String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                        replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                        allLine.set(i, replacementString);

                        assignmentIterator++;
                        expressionIterator++;
                    }
                    if (tempValue.contains("classNameVariable")) {
                        String replacementString = tempValue.replace("classNameVariable", "classIden");
                        // Check if this is the line with api call
                        if (!(tempValue.contains(oldApiName) || tempValue.contains(newApiName))) {
                            String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                            replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                        }
                        allLine.set(i, replacementString);
                        classNameAvailable = true;
                    }
                }
                lastLineReplacement = allLine.get(allLine.size()-1);
                lastLineReplacement = lastLineReplacement.replace("classNameVariable", "classIden");
                lastLineReplacement = lastLineReplacement.replace("parameterVariable", "iden");
                allLine.set(allLine.size()-1, lastLineReplacement);


                classChangeFlag = false;
                for (int i = 0; i < diffLine.size(); i++) {
                    String replacementString = diffLine.get(i).replace("parameterVariable", "iden");
                    if (diffLine.get(i).contains("classNameVariable") && !(diffLine.get(i).contains(oldApiName) || diffLine.get(i).contains(newApiName))) {
                        classChangeFlag = true;
                        replacementString = replacementString.replace("classNameVariable", "newClassName");
                    }
                    if (classChangeFlag) {
                        replacementString = replacementString.replace("classNameVariable", "newClassName");
                    } else {
                        replacementString = replacementString.replace("classNameVariable", "classIden");
                    }
                    diffLine.set(i, replacementString);
                }

                // Writing the cocci file
                writer.println("@upperbottom_classname_assignment@");
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.write("expression ");
                }
                for (int i = 0; i < expressionIterator; i++) {
                    writer.write("exp" + i);
                    int temp = expressionIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("exp" + expressionIterator);
                }
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.write("identifier ");
                }
                for (int i = 0; i < assignmentIterator; i++) {
                    writer.write("iden" + i);
                    int temp = assignmentIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("classIden");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                writer.println("@@");
                writer.println("...");
                writer.println("+ if (" + ifCondition + ") {");
                // ASSIGNMENT CASE
                apiLine = allLine.get(allLine.size() - 1).trim();
                if (apiLine.indexOf("=") == -1) {
                    writer.print("tempFunctionReturnValue = ");
                }
                writer.println(apiLine);
                writer.println("+ } else {");

                appearInDiff = new ArrayList<>();
                for (int i = 0; i < diffLine.size() - 1; i++) {
                    writer.write("+ ");
                    if (diffLine.get(i).contains("iden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("iden", "newParameterVariable"));
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("REPLACE")) {
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("classIden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("classIden", "newClassnameVariable"));
                    }
                    writer.println(diffLine.get(i).trim());
                }
                otherLine = diffLine.get(diffLine.size() - 1);
                otherLine = otherLine.substring(otherLine.indexOf("=") + 1);
                otherParameter = otherLine.substring(otherLine.indexOf("(") + 1, otherLine.indexOf(")"));
                listParameter = otherParameter.split(",");

                for (int i = 0; i < listParameter.length; i++) {
                    boolean isInDiff = false;
                    for (int j = 0; j < appearInDiff.size(); j++) {
                        if (appearInDiff.get(j).trim().contains(listParameter[i].trim())) {
                            if (appearInDiff.get(j).contains("classIden")) {
                                otherLine = otherLine.replace(listParameter[i], "newClassnameVariable");
                            } else {
                                otherLine = otherLine.replace(listParameter[i], "newParameterVariable" + listParameter[i].substring(listParameter[i].length() - 1));
                            }
                            break;
                        }
                    }
                }

                writer.print("+ ");
                if (otherLine.indexOf("=") == -1) {
                    writer.print("tempFunctionReturnValue = ");
                }

                writer.println(otherLine.trim());
                writer.write("+ }");
                writer.println();
                // bottomupper version
                allLine = new ArrayList<>(listLower);
                diffLine = new ArrayList<>();

                for (int i = 0; i < listUpper.size(); i++) {
                    if (!listLower.contains(listUpper.get(i))) {
                        diffLine.add(listUpper.get(i));
                    }
                }
                alwaysDelete = false;
                for (int i = 0; i < allLine.size(); i++) {
                    String temp = allLine.get(i);
                    boolean needDelete = true;
                    for (String s : matches)
                    {
                        if (allLine.get(i).contains(s)) {
                            needDelete = false;

                            break;
                        }
                    }
                    if ((allLine.get(i).contains(newApiName) || allLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                        alwaysDelete = true;
                        continue;
                    }
                    if (alwaysDelete) {
                        allLine.remove(i);
                        i -= 1;
                    }
                }
                alwaysDelete = false;
                for (int i = 0; i < diffLine.size(); i++) {
                    String temp = diffLine.get(i);
                    boolean needDelete = false;
                    for (String s : matches)
                    {
                        if (diffLine.get(i).contains(s)) {
                            needDelete = false;
                            break;
                        }
                    }
                    if ((diffLine.get(i).contains(newApiName) || diffLine.get(i).contains(oldApiName)) && !alwaysDelete) {
                        alwaysDelete = true;
                        continue;
                    }
                    if (alwaysDelete) {
                        diffLine.remove(i);
                        i -= 1;
                    } else if (needDelete) {
                        diffLine.remove(i);
                        i -= 1;
                    }
                }

                // This is to fix the case of same variable but in different order
                for (int i = 0; i < diffLine.size(); i++) {
                    // Check if assignment
                    if (diffLine.get(i).contains("=")) {
                        boolean rhsExist = false;
                        String diff = diffLine.get(i);
                        String rhs = diff.substring(diffLine.get(i).indexOf("=") + 1).trim();
                        for (int j = 0; j < allLine.size(); j++) {
                            String allLhs = "";
                            if (allLine.get(j).contains(rhs)) {

                                rhsExist = true;
                                if (allLine.get(j).contains("parameter") && allLine.get(j).contains("=")) {
                                    allLhs = allLine.get(j).substring(allLine.get(j).indexOf("parameter"), allLine.get(j).indexOf("=")).trim();
                                    allLhs = allLhs.replace("parameterVariable", "REPLACE") + ";";
                                }
                            }
                            if ((rhsExist) && allLine.get(j).contains("parameter")) {
                                if (allLhs.length() > 0) {
                                    String newDiff = diff.replace(rhs, allLhs);
                                    diffLine.set(i, newDiff);
                                }
                                break;
                            }
                        }
                    }
                }
                expressionIterator = 0;
                assignmentIterator = 0;
                classNameAvailable = false;
                // Need special processing for last line
                for (int i = 0; i < allLine.size() - 1; i++) {
                    String tempValue = allLine.get(i);
                    if (tempValue.contains("parameterVariable")) {
                        String replacementString = tempValue.replace("parameterVariable", "iden");
                        int end = tempValue.indexOf(";");
                        String rightHandSideAssignment;
                        if (end < tempValue.indexOf("=")) {
                            rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.length());
                        } else {
                            rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                        }
                        replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                        allLine.set(i, replacementString);
                        assignmentIterator++;
                        expressionIterator++;
                    }
                    if (tempValue.contains("classNameVariable")) {
                        String replacementString = tempValue.replace("classNameVariable", "classIden");
                        // Check if this is the line with api call
                        if (!(tempValue.contains(oldApiName) || tempValue.contains(newApiName))) {
                            String rightHandSideAssignment = tempValue.substring(tempValue.indexOf("=") + 1, tempValue.indexOf(";"));
                            replacementString = replacementString.replace(rightHandSideAssignment, (" exp" + assignmentIterator));
                        }
                        allLine.set(i, replacementString);
                        classNameAvailable = true;
                    }
                }
                lastLineReplacement = allLine.get(allLine.size()-1);
                lastLineReplacement = lastLineReplacement.replace("classNameVariable", "classIden");
                lastLineReplacement = lastLineReplacement.replace("parameterVariable", "iden");
                allLine.set(allLine.size()-1, lastLineReplacement);
                classChangeFlag = false;
                for (int i = 0; i < diffLine.size(); i++) {
                    String replacementString = diffLine.get(i).replace("parameterVariable", "iden");
                    if (diffLine.get(i).contains("classNameVariable") && !(diffLine.get(i).contains(oldApiName) || diffLine.get(i).contains(newApiName))) {
                        classChangeFlag = true;
                        replacementString = replacementString.replace("classNameVariable", "newClassName");
                    }
                    if (classChangeFlag) {
                        replacementString = replacementString.replace("classNameVariable", "newClassName");
                    } else {
                        replacementString = replacementString.replace("classNameVariable", "classIden");
                    }
                    diffLine.set(i, replacementString);
                }

                // Writing the cocci file
                writer.println();
                writer.println("@bottomupper_classname_assignment@");
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.write("expression ");
                }
                for (int i = 0; i < expressionIterator; i++) {
                    writer.write("exp" + i);
                    int temp = expressionIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("exp" + expressionIterator);
                }
                if ((expressionIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.write("identifier ");
                }
                for (int i = 0; i < assignmentIterator; i++) {
                    writer.write("iden" + i);
                    int temp = assignmentIterator - 1;
                    if (i != temp) {
                        writer.write(", ");
                    } else {
                        if (classNameAvailable) {
                            writer.write(", ");
                        }
                    }
                }
                if (classNameAvailable) {
                    writer.write("classIden");
                }
                if ((assignmentIterator > 0) || (classNameAvailable)) {
                    writer.println(";");
                }
                writer.println("@@");
                writer.println("...");
                writer.println("+ if (" + ifCondition + ") {");

                appearInDiff = new ArrayList<>();
                for (int i = 0; i < diffLine.size() - 1; i++) {
                    writer.write("+ ");
                    if (diffLine.get(i).contains("iden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("iden", "newParameterVariable"));
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("REPLACE")) {
                        diffLine.set(i, diffLine.get(i).replace("REPLACE", "iden"));
                    }
                    if (diffLine.get(i).contains("classIden")) {
                        String variableName = diffLine.get(i).substring(
                                diffLine.get(i).indexOf(" ") + 1, diffLine.get(i).indexOf("=")).trim();
                        appearInDiff.add(variableName);
                        diffLine.set(i, diffLine.get(i).replace("classIden", "newClassnameVariable"));
                    }
                    writer.println(diffLine.get(i).trim());
                }
                otherLine = diffLine.get(diffLine.size() - 1);
                otherLine = otherLine.substring(otherLine.indexOf("=") + 1);
                otherParameter = otherLine.substring(otherLine.indexOf("(") + 1, otherLine.indexOf(")"));
                listParameter = otherParameter.split(",");

                for (int i = 0; i < listParameter.length; i++) {
                    boolean isInDiff = false;
                    for (int j = 0; j < appearInDiff.size(); j++) {
                        if (appearInDiff.get(j).trim().contains(listParameter[i].trim())) {
                            if (appearInDiff.get(j).contains("classIden")) {
                                otherLine = otherLine.replace(listParameter[i], "newClassnameVariable");
                            } else {
                                otherLine = otherLine.replace(listParameter[i], "newParameterVariable" + listParameter[i].substring(listParameter[i].length() - 1));
                            }
                            break;
                        }
                    }
                }
                writer.print("+ ");
                if (otherLine.indexOf("=") == -1) {
                    writer.print("tempFunctionReturnValue = ");
                }

                writer.println(otherLine.trim());
                writer.println("+ } else {");
                apiLine = allLine.get(allLine.size() - 1).trim();
                if (apiLine.indexOf("=") == -1) {
                    writer.print("tempFunctionReturnValue = ");
                }
                writer.println(apiLine);
                writer.write("+ }");
                writer.println();
            }

            try {
                String methodFilename = "bottomupper-" + oldApiName + "-" + newApiName + ".cocci";
                PrintWriter methodWriter = new PrintWriter(methodFilename);
                if (upperName.size() == 0) {
                    methodWriter.println("null");
                } else {
                    for (int i = 0; i < upperName.size(); i++) {
                        methodWriter.print(upperName.get(i));
                        if (i < upperName.size() - 1) {
                            methodWriter.print(",");
                        } else {
                            methodWriter.println();
                        }
                    }
                }

                if (upperMethodName.size() == 0) {
                    methodWriter.println("null");
                } else {
                    for (int i = 0; i < upperMethodName.size(); i++) {
                        methodWriter.print(upperMethodName.get(i));
                        if (i < upperMethodName.size() - 1) {
                            methodWriter.print(",");
                        } else {
                            methodWriter.println();
                        }
                    }
                }

                for (int i = 0; i < upperMethod.size(); i++) {
                    methodWriter.println(upperMethod.get(i));
                    methodWriter.println("###SEPARATOR###");
                }
                methodWriter.println("###CLASS_SEPARATOR###");
                for (int i = 0; i < upperClass.size(); i++) {
                    methodWriter.println(upperClass.get(i).toString());
                    methodWriter.println("###SEPARATOR###");
                }

                methodWriter.close();

                methodFilename = "upperbottom-" + oldApiName + "-" + newApiName + ".cocci";
                methodWriter = new PrintWriter(methodFilename);

                if (lowerName.size() == 0) {
                    methodWriter.println("null");
                } else {
                    for (int i = 0; i < lowerName.size(); i++) {
                        methodWriter.print(lowerName.get(i));
                        if (i < lowerName.size() - 1) {
                            methodWriter.print(",");
                        } else {
                            methodWriter.println();
                        }
                    }
                }

                if (lowerMethodName.size() == 0) {
                    methodWriter.println("null");
                } else {
                    for (int i = 0; i < lowerMethodName.size(); i++) {
                        methodWriter.print(lowerMethodName.get(i));
                        if (i < lowerMethodName.size() - 1) {
                            methodWriter.print(",");
                        } else {
                            methodWriter.println();
                        }
                    }
                }

                for (int i = 0; i < lowerMethod.size(); i++) {
                    methodWriter.println(lowerMethod.get(i));
                    methodWriter.println("###SEPARATOR###");
                }
                methodWriter.println("###CLASS_SEPARATOR###");
                for (int i = 0; i < lowerClass.size(); i++) {
                    methodWriter.println(lowerClass.get(i).toString());
                    methodWriter.println("###SEPARATOR###");
                }
                methodWriter.close();
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            }
            writer.close();
        } else {
            System.out.println("Error, no update example found");
            return "";
        }
        return "";
    }


    private static String methodClassName = "";
    private static ArrayList<String> newMethodList = new ArrayList<>();

//    private static class ReturnValueVisitor extends ModifierVisitor<List<String>> {
//        private static String apiName;
//        private static String fullyQualifiedName;
//        private static String updateCocciPath;
//
//        public ReturnValueVisitor(String apiName, String fullyQualifiedName, String updateCocciPath) {
//            this.apiName = apiName;
//            this.fullyQualifiedName = fullyQualifiedName;
//            this.updateCocciPath = updateCocciPath;
//        }
//
//        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
//            // Will change any remaining deprecated API invocation into tempReturnFunctionValue
//            // if the API has return value
//            super.visit(methodCall, collector);
//            if ((methodCall.getName().toString().contains(apiName)) && (!methodCall.toString().contains("classNameVariable"))) {
//                System.out.println("Method call: ");
//                System.out.println(methodCall.toString());
//
//                methodCall.getParentNode().ifPresent(parent -> {
//                    System.out.println("parent node: " + parent.toString());
//
//                    boolean isSuccess = parent.replace(methodCall, StaticJavaParser.parseExpression("tempFunctionReturnValue"));
//                    System.out.println("Is replace success: " + isSuccess);
//                    System.out.println("New parent node: " + parent.toString());
//                });
//
//            }
//            return methodCall;
//        }
//    }

    private static class UpdateFileVisitor extends ModifierVisitor<List<String>> {
        private static String apiName;
        private static String fullyQualifiedName;
        private static String updateCocciPath;

        public UpdateFileVisitor(String apiName, String fullyQualifiedName, String updateCocciPath) {
            this.apiName = apiName;
            this.fullyQualifiedName = fullyQualifiedName;
            this.updateCocciPath = updateCocciPath;
        }

        public MethodCallExpr visit (MethodCallExpr methodCall, List<String> collector) {
            super.visit(methodCall, collector);
            // add the method in here

            if (methodCall.getName().toString().contains(apiName)) {
                methodCall.findAncestor(Statement.class).ifPresent(statement -> {

                    try {
                        FileWriter fw = new FileWriter("updateEdit.txt");
                        PrintWriter pw = new PrintWriter(fw);
                        FileWriter fw1 = new FileWriter("updateEdit1.txt");
                        PrintWriter pw1 = new PrintWriter(fw1);
                        pw.println("class something {");
                        pw.println("public void somefunc() {");
                        pw.println(statement.toString());
                        pw.println("}");
                        pw.println("}");
                        pw.close();

                        pw1.println("class something {");
                        pw1.println("public void somefunc() {");
                        pw1.println(statement.toString());
                        pw1.println("}");
                        pw1.println("}");
                        pw1.close();

                        // PUT THE NORMALIZATION HERE
                        StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + updateCocciPath + " " + "updateEdit.txt" + " --in-place");
                        try {
                            Runtime.getRuntime().exec(spatchCommand.toString());
                            Thread.sleep(300);
                        } catch (IOException | InterruptedException e) {
                            // e.printStackTrace();
                        }
                        // END OF NORMALIZATION


                        Scanner s = new Scanner(new File("updateEdit.txt"));
                        ArrayList<String> listNewStmt = new ArrayList<>();
                        while (s.hasNextLine()) {
                            listNewStmt.add(s.nextLine());
                        }
                        s.close();
                        for (int j = 0; j < listNewStmt.size(); j++) {
                            String currentStmt = listNewStmt.get(j);
                            for (int i = 0; i < newMethodList.size(); i++) {
                                currentStmt = currentStmt.replace(newMethodList.get(i), methodClassName + "." + newMethodList.get(i));
                            }
                            listNewStmt.set(j, currentStmt);
                        }

                        try {
                            methodCall.findAncestor(BlockStmt.class).ifPresent(block -> {
                                NodeList<Statement> listOriginalStmt = block.getStatements();
                                for (int i = 0; i < listOriginalStmt.size(); i++) {
                                    Statement currStmt = listOriginalStmt.get(i);
                                    if (currStmt.toString().contains(statement.toString())) {
                                        String toReplace = currStmt.toString();

                                        StringBuilder newStatement = new StringBuilder();
                                        for (int j = listNewStmt.size() - 3; j > 1; j--) {
                                            newStatement.insert(0, listNewStmt.get(j));
                                        }
                                        toReplace = toReplace.replace(statement.toString(), newStatement.toString());
                                        block.setStatement(i, StaticJavaParser.parseStatement(toReplace));
                                    }
                                }
                            });
                        } catch (Exception E) {
//                            System.out.println(E);
                        }
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                });
            }
            return methodCall;
        }
    }

    public static void runUpdate(String fullyQualifiedNameOld, String fullyQualifiedNameNew, String targetFilePath, String updateCocciPath, String tempOutputName) {
        newMethodList = new ArrayList<>();
        File file = new File(targetFilePath);
        String functionNameOld = getFunctionName(fullyQualifiedNameOld);
        String functionNameNew = getFunctionName(fullyQualifiedNameNew);

        String filename = "bottomupper-" + functionNameOld + "-" + functionNameNew + ".cocci";
        BufferedReader reader;
        ArrayList<String> listMethodString = new ArrayList<>();
        ArrayList<String> listClassString = new ArrayList<>();
        ArrayList<String> listAddedName = new ArrayList<>();
        ArrayList<String> listAddedMethodName = new ArrayList<>();


        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            StringBuilder builder = new StringBuilder("");
            boolean isClass = false;
            boolean isNameLine = true;
            boolean isMethodLine = false;
            while (line != null) {
                if (isNameLine) {
                    if (!line.equals("null")) {
                        String[] listName = line.split(",");
                        listAddedName.addAll(Arrays.asList(listName));
                    }
                    isNameLine = false;
                    isMethodLine = true;
                } else if (isMethodLine) {
                    if (!line.equals("null")) {
                        String[] listName = line.split(",");
                        listAddedMethodName.addAll(Arrays.asList(listName));
                    }
                    isNameLine = false;
                    isMethodLine = false;
                } else {
                    if (line.equals("###SEPARATOR###")) {
                        if (isClass) {
                            listClassString.add(builder.toString());
                        } else {
                            listMethodString.add(builder.toString());
                        }
                        builder = new StringBuilder("");
                    } else if (line.equals("###CLASS_SEPARATOR###")) {
                        isClass = true;
                    } else {
                        builder.append(line);
                    }
                }

                line = reader.readLine();
            }
        } catch (Exception E) {
//            System.out.println("FILE NOT FOUND");
        }

        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        }

        List<String> temp = new ArrayList<>();

        String className = "CocciEvolveMethod";
        boolean isNameUsed = false;
        List<ClassOrInterfaceDeclaration> listClass = cu.findAll(ClassOrInterfaceDeclaration.class);
        do {
            for (int i = 0; i < listClass.size(); i++) {
                if (listClass.get(i).getNameAsString().equals(className)) {
                    isNameUsed = true;
                    className = className + "_1";
                    break;
                }
            }
        } while (isNameUsed);
        if (listMethodString.size() > 0) {
            ClassOrInterfaceDeclaration classForMethod = cu.addClass(className);
            classForMethod.setPublic(false);
            classForMethod.setPrivate(false);
            classForMethod.setStatic(false);
            classForMethod.setInterface(false);
            classForMethod.setAbstract(false);
            classForMethod.setFinal(false);

            List<MethodDeclaration> listMethod = classForMethod.getMethods();
            for (int i = 0; i < listMethodString.size(); i++) {
                MethodDeclaration newMethod = StaticJavaParser.parseMethodDeclaration(listMethodString.get(i));
                newMethod = newMethod.setStatic(true);
                newMethod = newMethod.setPublic(true);
                newMethod = newMethod.setPrivate(false);
                newMethodList.add(newMethod.getNameAsString());
                classForMethod.addMember(newMethod);
            }
        }

        // add to the list same name
        ArrayList<String> listSameName = new ArrayList<>();
        for (int i = 0; i < listAddedName.size(); i++) {
            String currentName = listAddedName.get(i);
            Scanner scan = new Scanner(targetFilePath);
            while (scan.hasNextLine()) {
                if (scan.nextLine().contains(currentName)) {
                    listSameName.add(currentName);
                    break;
                }
            }
        }
        StringBuilder newCocciString = new StringBuilder();
        Map<String, String> mapNewName = new HashMap<>();
        File tempFile = new File(updateCocciPath);
        Scanner scan = null;
        try {
            scan = new Scanner(tempFile);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        }




        while (scan.hasNextLine()) {
            String currentLine = scan.nextLine();
            if (currentLine.contains("+") && !currentLine.contains("classIden") && !currentLine.contains("{") && !currentLine.contains("}")) {
                for (int i = 0; i < listAddedMethodName.size(); i++) {
                    String afterEq = currentLine.substring(currentLine.indexOf("=") + 1).trim();
                    String currentMethod = listAddedMethodName.get(i);
                    boolean isMethod;

                    if (afterEq.contains(currentMethod)) {
                        int looper = currentMethod.length();
                        while (true) {
                            char currentChar = afterEq.charAt(afterEq.indexOf(currentMethod) + looper);
                            if (currentChar == ' ') {

                                looper += 1;
                            } else if (currentChar == '(') {
                                isMethod = true;
                                break;
                            } else {
                                isMethod = false;
                                break;
                            }
                        }

                        if (isMethod) {
                            String newAfterEq = afterEq.replace(currentMethod, className + "." + currentMethod);
                            currentLine = currentLine.replace(afterEq, newAfterEq);
                        }
                    }
                }

                for (int i = 0; i < listSameName.size(); i++) {
                    String currentName = listSameName.get(i);
                    if (currentLine.contains(currentName)) {
                        if (!mapNewName.containsKey(currentName)) {
                            // newName not in hashmap, create new name
                            String newName = currentName + "_1";
                            int nextIndex = 2;
                            // Check if the newName exist
                            while (true) {
                                if (currentLine.contains(newName)) {
                                    newName = currentName + "_" + nextIndex;
                                } else {
                                    break;
                                }
                            }
                            mapNewName.put(currentName, newName);
                        }
                        String newName = mapNewName.get(currentName);
                        currentLine = currentLine.replace(currentName, newName);
                    }
                }
            }

            newCocciString.append(currentLine);
            newCocciString.append(System.getProperty("line.separator"));
        }

        FileWriter fw_cocci = null;
        try {
            fw_cocci = new FileWriter("tempUpdate.cocci");
            PrintWriter pw_cocci = new PrintWriter(fw_cocci);
            pw_cocci.write(newCocciString.toString());
            pw_cocci.close();
            fw_cocci.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        for (int i = 0; i < listClassString.size(); i++) {
            String currentClass = listClassString.get(i);
            for (int j = 0; j < listAddedMethodName.size(); j++) {
                String currentMethodName = listAddedMethodName.get(j);
                currentClass = currentClass.replace(currentMethodName, className + "." + currentMethodName);
            }
            listClassString.set(i, currentClass);
        }







//        ReturnValueVisitor returnVisitor = new ReturnValueVisitor(functionNameOld, fullyQualifiedNameOld, "tempUpdate.cocci");
//        returnVisitor.visit(cu, temp);
        UpdateFileVisitor apiLocate = new UpdateFileVisitor(functionNameOld, fullyQualifiedNameOld, "tempUpdate.cocci");




        methodClassName = className;

        apiLocate.visit(cu, temp);

        // Traverse CU only if has return value!
        List<MethodCallExpr> listMethodCall = cu.findAll(MethodCallExpr.class);
        for (MethodCallExpr methodCall: listMethodCall) {
            if ((methodCall.getName().toString().contains(functionNameOld)) && (!methodCall.toString().contains("classNameVariable")) && (methodCall.toString().charAt(methodCall.toString().indexOf(functionNameOld)) == '.' )) {
//                System.out.println("Method call: ");
//                System.out.println(methodCall.toString());

                methodCall.getParentNode().ifPresent(parent -> {
//                    System.out.println("parent node: " + parent.toString());

                    boolean isSuccess = parent.replace(methodCall, StaticJavaParser.parseExpression("tempFunctionReturnValue"));
//                    System.out.println("Is replace success: " + isSuccess);
//                    System.out.println("New parent node: " + parent.toString());
                });

            }
        }


//        System.out.println("\n\n\n\nPrinting CU");
//        System.out.println(cu.toString());

        // Run the readability scoring here for the before normalization result
        ReadabilityScorer readabilityScorer = new ReadabilityScorer(functionNameOld, functionNameNew);


        readabilityScorer.runReadabilityScorer(cu, false, tempOutputName);

        VariableNameDenormalization.runVariableDenorm(cu, functionNameOld, functionNameNew);

        // Run the readability scoring here
        readabilityScorer.runReadabilityScorer(cu, true, tempOutputName);
        file.delete();
        try {
            Files.write(new File(targetFilePath).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // e.printStackTrace();
        }


        // Write the new class in the bottom of the file
        try {
            FileWriter fw = new FileWriter(targetFilePath, true);
            PrintWriter pw = new PrintWriter(fw);
            for (int i = 0; i < listClassString.size(); i++) {
                pw.println();
                pw.write(listClassString.get(i));
                pw.println();
            }
            pw.close();
        } catch (Exception E) {

        }

    }



    public static void main(String[] args) throws FileNotFoundException {
        SymbolPreprocess.initTypeSolver();
        // Parsing the input
        for (int i = 1; i < args.length; i++) {
            args[i] = args[i].replace("\\", "");
        }
        if (args.length == 7) {
            // Update Patch Creation
            if (!args[0].equals("--generate-patch")) {
                System.out.println("Run Argument Invalid");
                System.exit(1);
            }
            String oldApiName = args[1].trim();
            String newApiName = args[2].trim();
            String filePath = args[4].trim();
            String folderDirectory = modifyFile(filePath);
            String outputPath = args[6].trim();
            boolean flagSameName = false;
            String functionName = getFunctionName(oldApiName);
            if (getFunctionName(oldApiName).equals(getFunctionName(newApiName))) {
                if (getNumParameter(oldApiName) == getNumParameter(newApiName)) {
                    flagSameName = true;
                }
            }
            if (flagSameName) {
                SymbolPreprocess.modifyFunctionName(folderDirectory, oldApiName, newApiName);
                oldApiName = oldApiName.replace(functionName, functionName + "OLD");
                newApiName = newApiName.replace(functionName, functionName + "NEW");
            }

            // Normalize temporaryOutput.java
            SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(oldApiName), true);
            SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(newApiName), true);

            newNormalizeFileCocci(oldApiName, folderDirectory);
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
            newNormalizeFileCocci(newApiName, folderDirectory);
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
            List<Pipeline.ParseResult> listIfs = getMatchingIf(getFunctionName(oldApiName), getFunctionName(newApiName), folderDirectory);
            String oldReturnValue = getReturnValue(oldApiName);
            String newReturnValue = getReturnValue(newApiName);
            boolean haveReturn = false;
            if (!oldReturnValue.equals("void") || !newReturnValue.equals("void")) {
                haveReturn = true;
            }
//            System.out.println("Printing listIfs: " + listIfs);
            createUpdateApiCocci(listIfs, getFunctionName(oldApiName), getFunctionName(newApiName), haveReturn, outputPath);
            System.out.println("Update Patch Created!");
            File delete = new File("temporaryOutput.java");
            delete.delete();
            delete = new File("tempEdit.txt");
            delete.delete();
            delete = new File("tempEdit1.txt");
            delete.delete();
        } else if (args.length == 9) {
            // Update Patch Application
            if (!args[0].equals("--apply-patch")) {
                System.out.println("Run Argument Invalid");
                System.exit(1);
            }
            String oldApiName = args[1].trim();
            String newApiName = args[2].trim();
            String checkInput = args[3].trim();

            boolean isProcessFolder = false;
            if (checkInput.equals("--folder")) {
                isProcessFolder = true;
            }
            String targetPath = args[4].trim();
            String updateCocciPath = args[6].trim();
            String outputPath = args[8].trim();

            boolean flagSameName = false;
            // Create the processedFile (temporaryOutput.java)
            String functionName = getFunctionName(oldApiName);
            // Check if same number of parameter and same name of API
            if (getFunctionName(oldApiName).equals(getFunctionName(newApiName))) {
                if (getNumParameter(oldApiName) == getNumParameter(newApiName)) {
                    // Special processing
                    flagSameName = true;
                }
            }

            if (isProcessFolder) {
                File processedFolder = new File(targetPath);
                File[] fileList = processedFolder.listFiles();
                // Process each file
                File directory = new File(outputPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                for (File file : fileList) {
                    long startTime = System.currentTimeMillis();
                    String filepath = file.getPath();
                    targetPath = filepath;
                    System.out.println("\n\n\nProcessing file: " + file.getPath());
                    String final_output = outputPath + "/" + file.getName();

                    // Start the process
                    oldApiName = args[1].trim();
                    newApiName = args[2].trim();
                    String folderDirectory = modifyFile(targetPath);
//                    System.out.println("Old api name before: " + oldApiName);
//                    System.out.println("New api name before: " + newApiName);
//                    System.out.println("Function name before: " + functionName);
                    if (flagSameName) {
                        SymbolPreprocess.modifyFunctionName(folderDirectory, oldApiName, newApiName);
                        oldApiName = args[1].trim().replace(functionName, functionName + "OLD");
                        newApiName = args[2].trim().replace(functionName, functionName + "NEW");
                    }
//
//            // Normalize temporaryOutput.java
                    SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(oldApiName), false);



                    newNormalizeFileCocci(oldApiName, folderDirectory);
//
//
//                    System.out.println("Old api name: " + oldApiName);
//                    System.out.println("New api name: " + newApiName);
//                    System.out.println("Function name: " + functionName);
//                    System.out.println("This is folder directory content: ");
//                    BufferedReader br = new BufferedReader(new FileReader(folderDirectory));
//                    String line;
//                    try {
//                        while ((line = br.readLine()) != null) {
//                            System.out.println(line);
//                        }
//                    } catch (Exception E) {
//
//                    }


                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                    }
//            String outputPath = args[3].trim();
                    String tempOutputName = final_output.substring(final_output.lastIndexOf("/") + 1, final_output.lastIndexOf("."));
                    StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + updateCocciPath + " " + "temporaryOutput.java" + " --in-place");



                    runUpdate(oldApiName, newApiName, folderDirectory, updateCocciPath, tempOutputName);



                    if (flagSameName) {
                        SymbolPreprocess.revertOriginalName(folderDirectory, functionName);
                    }
                    DuplicateFix.fixDuplicate(folderDirectory);
//
                    // Copying file to the correct output path
                    Path originalFile = Paths.get("temporaryOutput.java");
                    Path copiedFile = Paths.get(final_output);

                    try {
                        Files.copy(originalFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                    File delete = new File("temporaryOutput.java");
                    delete.delete();
                    delete = new File("tempEdit.txt");
                    delete.delete();
                    delete = new File("tempEdit1.txt");
                    delete.delete();
                    delete = new File(tempOutputName);
                    delete.delete();
                    delete = new File("updateEdit.txt");
                    delete.delete();
                    delete = new File("updateEdit1.txt");
                    delete.delete();
                    delete = new File("tempUpdate.cocci");
                    delete.delete();

                    System.out.println("Update successfully applied!");
                    long stopTime = System.currentTimeMillis();
                    float elapsedTime = (stopTime - startTime);

                    System.err.println(elapsedTime);
                }
            } else {
                String folderDirectory = modifyFile(targetPath);
                if (flagSameName) {
                    SymbolPreprocess.modifyFunctionName(folderDirectory, oldApiName, newApiName);
                    oldApiName = oldApiName.replace(functionName, functionName + "OLD");
                    newApiName = newApiName.replace(functionName, functionName + "NEW");
                }
//
//            // Normalize temporaryOutput.java
                SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(oldApiName), false);
                newNormalizeFileCocci(oldApiName, folderDirectory);
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
//            String outputPath = args[3].trim();
                String tempOutputName = outputPath.substring(outputPath.lastIndexOf("/") + 1, outputPath.lastIndexOf("."));
                StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + updateCocciPath + " " + "temporaryOutput.java" + " --in-place");
                runUpdate(oldApiName, newApiName, folderDirectory, updateCocciPath, tempOutputName);
                if (flagSameName) {
                    SymbolPreprocess.revertOriginalName(folderDirectory, functionName);
                }
                DuplicateFix.fixDuplicate(folderDirectory);
//
                // Copying file to the correct output path
                Path originalFile = Paths.get("temporaryOutput.java");
                Path copiedFile = Paths.get(outputPath);
                try {
                    Files.copy(originalFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    // e.printStackTrace();
                }
                File delete = new File("temporaryOutput.java");
                delete.delete();
                delete = new File("tempEdit.txt");
                delete.delete();
                delete = new File("tempEdit1.txt");
                delete.delete();
                delete = new File(tempOutputName);
                delete.delete();
                delete = new File("updateEdit.txt");
                delete.delete();
                delete = new File("updateEdit1.txt");
                delete.delete();
                delete = new File("tempUpdate.cocci");
                delete.delete();

                System.out.println("Update successfully applied!");
            }


        } else {
            System.out.println("USAGE:");
            System.out.println("    Patch Creation   : java -jar AndroEvolve.jar --generate-patch <deprecated_api> <updated_api> --input <example_filepath> --output <output_path>");
            System.out.println("    Patch Application: java -jar AndroEvolve.jar --apply-patch <deprecated_api> <updated_api> --input <target_file> --patch <semantic_patch> --output <output_path>");
            System.exit(1);
        }
    }
//
//    public static void main(String[] args) throws FileNotFoundException {
//        SymbolPreprocess.initTypeSolver();
//        if (args.length == 5) {
//            // Run cocci update script
//            if (validateInput(args) == false) {
//                System.out.println("Provided API is incorrect, quitting...");
//                return;
//            }
//            String oldApiName = args[0].trim();
//            String newApiName = args[1].trim();
//
//
//            String folderDirectory = modifyFile(args[2].trim());
//
//            boolean flagSameName = false;
//            // Create the processedFile (temporaryOutput.java)
//            String functionName = getFunctionName(oldApiName);
//            // Check if same number of parameter and same name of API
//            if (getFunctionName(oldApiName).equals(getFunctionName(newApiName))) {
//                if (getNumParameter(oldApiName) == getNumParameter(newApiName)) {
//                    // Special processing
//                    flagSameName = true;
//
//                }
//            }
//            if (flagSameName) {
//                SymbolPreprocess.modifyFunctionName(folderDirectory, oldApiName, newApiName);
//                oldApiName = oldApiName.replace(functionName, functionName + "OLD");
//                newApiName = newApiName.replace(functionName, functionName + "NEW");
//            }
//
//            // Normalize temporaryOutput.java
//            SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(oldApiName), false);
//            newNormalizeFileCocci(oldApiName, folderDirectory);
//            try {
//                Thread.sleep(1200);
//            } catch (InterruptedException e) {
//                // e.printStackTrace();
//            }
//            String updateCocciPath = args[4].trim();
//            String outputPath = args[3].trim();
//            String tempOutputName = outputPath.substring(outputPath.lastIndexOf("/") + 1, outputPath.lastIndexOf("."));
//            StringBuilder spatchCommand = new StringBuilder("spatch --sp-file " + updateCocciPath + " " + "temporaryOutput.java" + " --in-place");
//            runUpdate(oldApiName, newApiName, folderDirectory, updateCocciPath, tempOutputName);
//
//
//            if (flagSameName) {
//                SymbolPreprocess.revertOriginalName(folderDirectory, functionName);
//            }
//            DuplicateFix.fixDuplicate(folderDirectory);
//
//            // Variable name denormalization
//
//
//
//            // Copying file to the correct output path
//            Path originalFile = Paths.get("temporaryOutput.java");
//            Path copiedFile = Paths.get(outputPath);
//            try {
//                Files.copy(originalFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException e) {
//                // e.printStackTrace();
//            }
//        } else if (args.length == 3) {
//
//            // Run cocci update script
//            if (validateInput(args) == false) {
//                System.out.println("Provided API is incorrect, quitting...");
//                return;
//            }
//            String oldApiName = args[0].trim();
//            String newApiName = args[1].trim();
//            // Create the processedFile (temporaryOutput.java)
//            String folderDirectory = modifyFile(args[2].trim());
//
//            // Special processing
//            boolean flagSameName = false;
//            String functionName = getFunctionName(oldApiName);
//            // Check if same number of parameter and same name of API
//            if (getFunctionName(oldApiName).equals(getFunctionName(newApiName))) {
//                if (getNumParameter(oldApiName) == getNumParameter(newApiName)) {
//                    // Special processing
//                    flagSameName = true;
//
//                }
//            }
//            if (flagSameName) {
//                SymbolPreprocess.modifyFunctionName(folderDirectory, oldApiName, newApiName);
//                oldApiName = oldApiName.replace(functionName, functionName + "OLD");
//                newApiName = newApiName.replace(functionName, functionName + "NEW");
//            }
//            // Normalize temporaryOutput.java
//            SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(oldApiName), true);
//            SeparateDeclaration.separateDeclaration(folderDirectory, getFunctionName(newApiName), true);
//            newNormalizeFileCocci(oldApiName, folderDirectory);
//            try {
//                Thread.sleep(1200);
//            } catch (InterruptedException e) {
//                // e.printStackTrace();
//            }
//            newNormalizeFileCocci(newApiName, folderDirectory);
//            try {
//                Thread.sleep(1200);
//            } catch (InterruptedException e) {
//                // e.printStackTrace();
//            }
//            List<ParseResult> listIfs = getMatchingIf(getFunctionName(oldApiName), getFunctionName(newApiName), folderDirectory);
//
//            String oldReturnValue = getReturnValue(oldApiName);
//            String newReturnValue = getReturnValue(newApiName);
//            boolean haveReturn = false;
//            if (!oldReturnValue.equals("void") || !newReturnValue.equals("void")) {
//                haveReturn = true;
//            }
//            createUpdateApiCocci(listIfs, getFunctionName(oldApiName), getFunctionName(newApiName), haveReturn);
//        } else {
//            System.out.println("No parameter given!!!");
//            System.out.println("USAGE PARAMETER: ");
//            System.out.println("<OLD_API_AND_PARAMETER> <NEW_API_AND_PARAMETER> <TARGET_PATH> <OUTPUT_PATH> <COCCI_PATH>");
//            return;
//        }
//
//    }
}


















