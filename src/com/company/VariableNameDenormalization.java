package com.company;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserSymbolDeclaration;
import com.sun.javafx.tools.packager.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VariableNameDenormalization {
    // valueMap to save the denormalization value
    HashMap<String, String> valueMap = new HashMap<>();
    static List<String>  listVariable = new ArrayList<>();
    static List<Node> listToDelete = new ArrayList<>();
    public static String oldApiName = "";
    public static String newApiName = "";

    public static class MethodParameterVisitor extends ModifierVisitor<List<String>> {
        private Expression recurse(Expression exp) {
            try {
                if (exp.isNameExpr()) {
                    JavaParserSymbolDeclaration resolvedValue = (JavaParserSymbolDeclaration) exp.asNameExpr().resolve();
                    // add to delete later
                    listToDelete.add(resolvedValue.getWrappedNode().findAncestor(Statement.class).get());
                    try {
                        Expression newExp = resolvedValue.getWrappedNode().findFirst(Expression.class).get();
                        return recurse(newExp);
                    } catch (Exception E2){
                        VariableDeclarator variableDeclarator = resolvedValue.getWrappedNode().findFirst(VariableDeclarator.class).get();
                        Expression newExp = variableDeclarator.getInitializer().get();
                        return newExp;
                    }

                } else if (exp.isMethodCallExpr()) {
                    MethodCallExpr methodCallExpr = exp.asMethodCallExpr();
                    NodeList<Expression> listArguments = methodCallExpr.getArguments();
                    for (int i = 0; i < listArguments.size(); i++) {
                        Expression currArguments = listArguments.get(i);
                        Expression newArguments = recurse(currArguments);
                        listArguments.set(i, newArguments);
                    }
                    methodCallExpr.setArguments(listArguments);
                    return methodCallExpr;
                } else {
                    return exp;
                }
            } catch (Exception E) {
                System.out.println("Exception happened: ");
                System.out.println(E);
                return exp;
            }

        }

        @Override
        public Visitable visit(MethodCallExpr n, List<String> arg) {
            super.visit(n, arg);
            if (n.getName().toString().contains(oldApiName) || n.getName().toString().contains(newApiName)) {
                if (n.getScope().isPresent() && n.getScope().get().toString().contains("classNameVariable")) {
                    NodeList<Expression> listArguments = n.getArguments();
                    // Loop through all the arguments
                    for (int i = 0; i < listArguments.size(); i++) {
                        Expression currentArgument = listArguments.get(i);
                        Expression newArgument = recurse(currentArgument);
                        listArguments.set(i, newArgument);
                    }
                    n.setArguments(listArguments);
                }

            }
            return n;
        }


    }

    public static class ClassNameVisitor extends ModifierVisitor<List<String>> {
        @Override
        public Visitable visit(MethodCallExpr n, List<String> arg) {
            super.visit(n, arg);
            if (n.getName().toString().contains(oldApiName) || n.getName().toString().contains(newApiName)) {
                // Make sure that it has scope
                if (n.getScope().isPresent() && n.getScope().get().toString().contains("classNameVariable")) {
                    Expression classNameVariable = n.getScope().get();
                    JavaParserSymbolDeclaration resolvedValue = (JavaParserSymbolDeclaration) classNameVariable.asNameExpr().resolve();
                    Node toDelete = resolvedValue.getWrappedNode();
                    VariableDeclarator assignmentExpr = toDelete.findFirst(VariableDeclarator.class).get();
                    Expression initializer = assignmentExpr.getInitializer().get();
                    n = n.setScope(initializer);
                    listToDelete.add(toDelete.findAncestor(Statement.class).get());
                }
            }

            return n;
        }
    }

    public static class ReturnValueVisitor extends ModifierVisitor<List<String>> {
        @Override
        public Visitable visit(VariableDeclarationExpr n, List<String> arg) {
            super.visit(n, arg);
            VariableDeclarator variableDeclarator = n.getVariable(0);
            String variableType = variableDeclarator.getType().asString();
            if (variableDeclarator.getName().toString().contains("tempFunctionReturnValue")) {
                if (variableDeclarator.findAncestor(BlockStmt.class).isPresent()) {
                    BlockStmt blockStmt = variableDeclarator.findAncestor(BlockStmt.class).get();
                    NodeList<Statement> listStmt = blockStmt.getStatements();
                    boolean isFirstOccurence = true;
                    List<VariableDeclarationExpr> listVarDec = blockStmt.findAll(VariableDeclarationExpr.class);
                    for (int i = 0; i < listVarDec.size(); i++) {
                        VariableDeclarationExpr varDec = listVarDec.get(i);
                        if (varDec.getVariable(0).getNameAsString().contains("tempFunctionReturnValue")) {
                            if (isFirstOccurence) {
                                isFirstOccurence = false;
                            } else {
                                Statement toDelete = varDec.findAncestor(Statement.class).get();
                            }
                        }
                    }
                }
            }
            return n;
        }
    }

    public static class VariableDenormVisitor extends ModifierVisitor<List<String>> {

    }

    public static void runVariableDenorm(CompilationUnit cu, String oldApi, String newApi) {
        // Initialize the visitors
        ReturnValueVisitor valueVisitor = new ReturnValueVisitor();
        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
        MethodParameterVisitor methodParameterVisitor = new MethodParameterVisitor();


        // Initialize other value
        List<String> listStr = new ArrayList<>();
        oldApiName = oldApi;
        newApiName = newApi;

        // Openning the file
//        File file = new File(filename);
//        CompilationUnit cu = null;
//        try {
//            cu = StaticJavaParser.parse(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        // Return value denormalization
        valueVisitor.visit(cu, listStr);

        // Method parameter denormalization
        methodParameterVisitor.visit(cu, listStr);
        // Delete the parameterVariable initializer since they are no longer needed
        for (int i = 0; i < listToDelete.size(); i++) {
            Node toDelete = listToDelete.get(i);
            try {
                System.out.println("Is the parameter deleted: " +  toDelete.remove());
            } catch (Exception E) {
                System.out.println("Parameter might be already deleted before");
            }
        }
        listToDelete = new ArrayList<>();


        // Class name denormalization
        classNameVisitor.visit(cu, listStr);
        // Delete the classNameVariables initializer since they are no longer needed
        for (int i = 0; i < listToDelete.size(); i++) {
            Node toDelete = listToDelete.get(i);

        }
        listToDelete = new ArrayList<>();

    }
}
