package com.company;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Util {
    public static String getReturnValue(String fullyQualifiedName) {
        String returnValue;
        int lastHashtagPosition = fullyQualifiedName.lastIndexOf('#');
        returnValue = fullyQualifiedName.substring(lastHashtagPosition + 1);
        // Check if has dot
        int dotIndex = returnValue.indexOf(".");
//        return returnValue.substring(returnValue.lastIndexOf(".") + 1);
        if (dotIndex == -1) {
            return returnValue;
        } else {
            returnValue = returnValue.substring(returnValue.lastIndexOf(".") + 1);
            // Special processing for autoboxing and auto unboxing (e.g.: Integer and int)
            if (returnValue.equals("Integer")) {
                return "int";
            }
            return returnValue;
        }

    }

    public static String getFunctionName(String fullyQualifiedName) {
        if (!validateAPI(fullyQualifiedName, -1)) {
            return "";
        }
        int hashtagPosition = fullyQualifiedName.indexOf('#');
        int openBracketPosition = fullyQualifiedName.indexOf('(');
        String functionName = fullyQualifiedName.substring(hashtagPosition + 1, openBracketPosition);
        return functionName;
    }

    public static int getNumParameter(String fullyQualifiedName) {
        int result = 0;
        int startBracketPos = fullyQualifiedName.indexOf('(');
        int endBracketPos = fullyQualifiedName.indexOf(')');
        String parameterString = fullyQualifiedName.substring(startBracketPos + 1, endBracketPos).trim();
        if (parameterString.length() > 0) {
            result += 1;
            int commaCount = parameterString.length() - parameterString.replace(",", "").length();
            result += commaCount;
        }
        return result;
    }

    public static String getFunctionClassNameType (String fullyQualifiedName) {
        String beforeHash = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("#"));
        return beforeHash.substring(beforeHash.lastIndexOf(".") + 1);
    }

    public static String getParameterType (String fullyQualifiedName, int paramNumber) {
        int numParam = getNumParameter(fullyQualifiedName);
        if (paramNumber > numParam + 1) {
            return "";
        } else {
            String parameters = fullyQualifiedName.substring(fullyQualifiedName.indexOf("(") + 1, fullyQualifiedName.indexOf(")"));
            String[] parameterSplit = parameters.split(",");
            String fullParameter = parameterSplit[paramNumber];
            if (fullParameter.indexOf(".") == -1) {
                return fullParameter;
            } else {
                fullParameter = fullParameter.substring(fullParameter.lastIndexOf(".") + 1);
                // Special case for autoboxing and auto unboxing (e.g.: Integer to int and vice versa)
                if (fullParameter.equals("Integer")) {
                    return "int";
                } else {
                    return fullParameter;
                }

            }
        }
    }

    public static String getParamCocciString(int start, int end) {
        int numParam = end - start;
        StringBuilder paramCocciString = new StringBuilder("");
        if (numParam > 0) {
            for (int i = 0; i < numParam; i++) {
                int temp = i + start;
                paramCocciString.append("param" + temp);
                if (i != numParam - 1) {
                    paramCocciString.append(", ");
                }
            }
            return paramCocciString.toString();
        } else {
            return "";
        }
    }

    public static String getParamCocciString(String fullyQualifiedName) {
        int numParam = getNumParameter(fullyQualifiedName);
        StringBuilder paramCocciString = new StringBuilder("");
        if (numParam > 0) {
            for (int i = 0; i < numParam; i++) {
                paramCocciString.append("param" + i);
                if (i != numParam - 1) {
                    paramCocciString.append(", ");
                }
            }
            return paramCocciString.toString();
        } else {
            return "";
        }
    }



    public static ArrayList<String> getAllImport (String fullyQualifiedName) {
        ArrayList<String> returnValue = new ArrayList<>();
        // android.os.FileUtils#copy(java.io.File,java.io.File)#int
        String className = fullyQualifiedName.substring(0, fullyQualifiedName.indexOf("#"));
        returnValue.add(className);
        if (getNumParameter(fullyQualifiedName) > 0) {
            String parameterString = fullyQualifiedName.substring(fullyQualifiedName.indexOf("(") + 1, fullyQualifiedName.indexOf(")"));
            String[] parameterSplit = parameterString.split(",");
            for (int i = 0; i < parameterSplit.length; i++) {
                if (parameterSplit[i].indexOf(".") != -1) {
                    returnValue.add(parameterSplit[i]);
                }
            }
        }

        return returnValue;
    }

    public static boolean validateAPI(String arg, int numAPI) {
        // android.os.FileUtils#copyFile(java.io.File, java.io.File)&android.os.FileUtils#copy(java.io.File,java.io.File)
        // First check the length as a valid API should've at least 3 character, e.g: a()
        if (arg.length() < 3) {
            System.out.println("Not a valid API call");
            return false;
        }
        // Check location of bracket
        int startBracketLocation = arg.indexOf('(');
        int endBracketLocation = arg.indexOf(')');
        if (endBracketLocation < startBracketLocation) {
            System.out.println("Not a valid API call");
            return false;
        }

        // Check if there is a whitespace in the function name
        String functionName = arg.substring(0,startBracketLocation).trim();
        Pattern pattern = Pattern.compile("\\s");
        if (pattern.matcher(functionName).find()) {
            System.out.println("Whitespace in function name is invalid");
            return false;
        }

        // Check if the provided parameter is correct
        String[] parameterList;
        String parameter = arg.substring(startBracketLocation + 1, endBracketLocation).trim();
        if (parameter.length() == 0) {
            parameterList = new String[0];
        } else {
            parameterList = parameter.split(",");
            for (int i = 0; i < parameterList.length; i++) {
                parameterList[i] = parameterList[i].trim();
                if (pattern.matcher(parameterList[i]).find()) {
                    return false;
                }
            }
        }
        String cleanedArgument = functionName + "(";
        for (int i = 0; i < parameterList.length; i++) {
            cleanedArgument = cleanedArgument.concat(parameterList[i]);
            if (i < parameterList.length - 1) {
                cleanedArgument = cleanedArgument.concat(", ");
            }
        }
        cleanedArgument = cleanedArgument.concat(")");
//        if (numAPI == 0) {
//            oldAPI = new ApiDescription(functionName, parameterList.length);
//        } else if (numAPI == 1){
//            newAPI = new ApiDescription(functionName, parameterList.length);
//        }
        return true;
    }
    
}
