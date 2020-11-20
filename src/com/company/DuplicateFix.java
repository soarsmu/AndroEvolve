package com.company;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuplicateFix {

    private static class DuplicateDeclarationFix extends ModifierVisitor<List<String>> {
        private static String apiName;

        DuplicateDeclarationFix() {
//            this.lineNumberList = new ArrayList<>();
        }

        public VariableDeclarationExpr visit (VariableDeclarationExpr varDecExpr, List<String> collector) {
            super.visit(varDecExpr, collector);
            boolean containImportant = false;
            String[] keyPhrases = {"parameterVariable", "classNameVariable", "tempFunctionReturnValue"};
            String currentDeclaration = "";
            for (String keyPhrase : keyPhrases) {
                if (varDecExpr.toString().contains(keyPhrase)) {
                    containImportant = true;
                    currentDeclaration = varDecExpr.toString();
                    break;
                }
            }


            if (containImportant) {
                String finalCurrentDeclaration = currentDeclaration;
                varDecExpr.findAncestor(BlockStmt.class).ifPresent(blockStmt -> {
                    NodeList<Statement> stmtList = blockStmt.getStatements();
                    boolean alreadyPresent = false;
                    for (int i = 0; i < stmtList.size(); i++) {
                        Statement currStmt = stmtList.get(i);
                        if (currStmt.toString().contains(finalCurrentDeclaration)) {
                            if (!alreadyPresent) {
                                alreadyPresent = true;
                            } else {
                                String newline = System.getProperty("line.separator");
                                Pattern p2 = Pattern.compile(newline);
                                Matcher m2 = p2.matcher(currStmt.toString());
                                int occur = 0;
                                while (m2.find()) {
                                    occur++;
                                }
                                if (occur < 2) {
                                    if (finalCurrentDeclaration.contains("tempFunctionReturnValue")) {
                                        blockStmt.remove(currStmt);
                                    } else {
                                        String toParse = currStmt.toString().trim().substring(currStmt.toString().trim().indexOf(" "));
                                        Statement newStmt = StaticJavaParser.parseStatement(toParse);
                                        blockStmt.setStatement(i, newStmt);
                                    }
                                }

                            }
                        }
                    }
                });
            }

            return varDecExpr;
        }
    }

    public static void listf(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listf(file.getAbsolutePath(), files);
                }
            }
        }
    }


    public static void fixDuplicate(String FILEPATH) {
        File file = new File(FILEPATH);
//        File file = new File(FILEPATH);
        CompilationUnit cu = null;
        try {
            cu = StaticJavaParser.parse(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> assignmentBlock = new ArrayList<>();
        ModifierVisitor<List<String>> assignmentVisitor = new DuplicateDeclarationFix();
        assignmentVisitor.visit(cu, assignmentBlock);
        file.delete();
        try {
            Files.write(new File(FILEPATH).toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
