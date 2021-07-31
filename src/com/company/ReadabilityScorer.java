package com.company;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadabilityScorer {

    public static String oldApiName = "";
    public static String newApiName = "";
    public static boolean isNormalized = false;
    public static int currentApiNumber = 0;
    public static String outputName = "";
    public static String folderPath = "";

    public ReadabilityScorer(String oldApi, String newApi) {
        oldApiName = oldApi;
        newApiName = newApi;
        folderPath = oldApiName + "/";
    }

    public static boolean checkValidIfStatement(IfStmt ifStmt) {
        boolean isValid = false;
        // As a valid ifStatement, it must have else branch
        if (ifStmt.hasElseBranch()) {
            // must have version statement
            if (ifStmt.getCondition().toString().toLowerCase().contains("version")) {
                // and must contain both apiname
                if (ifStmt.getThenStmt().toString().toLowerCase().contains(oldApiName.toLowerCase()) || ifStmt.getThenStmt().toString().toLowerCase().contains(newApiName.toLowerCase())) {
                    if (ifStmt.getElseStmt().get().toString().toLowerCase().contains(oldApiName.toLowerCase()) || ifStmt.getElseStmt().get().toString().toLowerCase().contains(newApiName.toLowerCase())) {
                        isValid = true;
                    }
                }
            }
        }
        return isValid;
    }
    public static class ScorerVisitor extends ModifierVisitor<List<String>> {
        @Override
        public Visitable visit(IfStmt n, List<String> arg) {
            super.visit(n, arg);
            boolean isValid = checkValidIfStatement(n);
            // is a valid if statement that contains the updated API
            if (isValid) {
                List<Statement> listCopiedStatement = new ArrayList<>();
                // If valid, find the parent block
                if (n.findAncestor(BlockStmt.class).isPresent()) {
                    BlockStmt block = n.findAncestor(BlockStmt.class).get();
                    NodeList<Statement> listStmt = block.getStatements();
                    listCopiedStatement.add(0, n);
                    int lastIndex = listStmt.indexOf(n);
                    if (lastIndex < 0) {
                        lastIndex = listStmt.size();
                    }
                    ArrayList<String> matches = new ArrayList<>(Arrays.asList("parameterVariable", "classNameVariable"));
                    for (int i = lastIndex - 1; i >= 0; i--) {
                        boolean contain = false;
                        Statement currentStatement = listStmt.get(i);
                        for (String s : matches) {
                            // Case does not contain the important stuff, delete
                            if (currentStatement.toString().contains(s)) {
                                contain = true;
                            }
                        }
                        if (!contain) {
                            // break if does not contain the needed variables
                            break;
                        } else {
                            listCopiedStatement.add(0, currentStatement);
                        }
                    }

                    // Setup the filename
                    String fileName = "";
                    if (isNormalized) {
                        fileName += "norm_";
                    }
                    fileName += outputName;
                    fileName += "_" + currentApiNumber;
                    fileName += ".java";

                    currentApiNumber += 1;
                    // Setup the save path
                    String savePath = folderPath;
                    // Writing the files
                    try {
                        File folder = new File(folderPath);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        System.out.println("Readability Scorer File: " + folderPath + fileName);
                        FileWriter fw = new FileWriter(folderPath + fileName);
                        PrintWriter pw = new PrintWriter(fw);
                        pw.println("class MainActivity {");
                        pw.println("\tpublic static void main() {");
                        // Print the actual content here
                        for (int i = 0; i < listCopiedStatement.size(); i++) {
                            String statement = listCopiedStatement.get(i).toString();
                            statement = statement.replace("\n", "\n\t\t");
                            String toPrint = "\t\t" + statement;
                            pw.println(toPrint);
                        }
                        pw.println("\t}");
                        pw.println("}");
                        pw.close();
                        fw.close();
                    } catch (Exception E) {
                    }
                }
            }


            return n;
        }
    }

    public void runReadabilityScorer(CompilationUnit cu, boolean normalized, String tempOutputName) {
        isNormalized = normalized;
        outputName = tempOutputName;
        ScorerVisitor scorerVisitor = new ScorerVisitor();
        List<String> listStr = new ArrayList<>();
        scorerVisitor.visit(cu, listStr);
    }
}
