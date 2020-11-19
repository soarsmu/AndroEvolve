package com.company;

public class ApiDescription {
    private int numParameter;
    private String apiName;
    public ApiDescription(String name, int numParam) {
        this.apiName = name;
        this.numParameter = numParam;
    }
    public String getName() {
        return this.apiName;
    }

    public int getNumParam() {
        return this.numParameter;
    }
}
