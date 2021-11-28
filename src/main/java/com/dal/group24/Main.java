package com.dal.group24;

import com.dal.group24.inputs.IInputs;
import com.dal.group24.inputs.UserInput;
import com.dal.group24.queries.QueryExecutor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        IInputs inputs = new UserInput();
        QueryExecutor queryExecutor = new QueryExecutor(inputs);
        queryExecutor.startApplication();
    }
}
