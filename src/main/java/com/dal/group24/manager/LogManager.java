package com.dal.group24.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
    private static final String ERROR_LOG = "logs/error.log";
    private static final String GENERAL_QUERY_LOG = "logs/general_query.log";
    private static final String TRANSACTION_LOG = "logs/transaction.log";

    public static void init() throws IOException {
        createFileIfNotExists(ERROR_LOG);
        createFileIfNotExists(GENERAL_QUERY_LOG);
        createFileIfNotExists(TRANSACTION_LOG);
    }
    private static void createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void writeErrorLog(String query, String errMsg) throws IOException {
        String lineToBeWritten = String.format("Query: %s , Error: %s\n", query, errMsg);
        writeIntofile(ERROR_LOG, lineToBeWritten);
    }

    public static void writeGeneralOrTrasactionQueryLog(String query) throws IOException {
        String lineToBeWritten = String.format("Query: %s , Status: SUCCESS\n", query);
        writeIntofile(GENERAL_QUERY_LOG, lineToBeWritten);
        if(StateManager.is_transaction_on) {
            writeTrasactionLog(query);
        } else if(query.contains("transaction")) {
            writeTrasactionLog(query);
        }
    }

    public static void writeGeneralQueryLog(String query) throws IOException {
        String lineToBeWritten = String.format("Query: %s , Status: SUCCESS", query);
        writeIntofile(GENERAL_QUERY_LOG, lineToBeWritten);
    }

    public static void writeTrasactionLog(String query) throws IOException {
        String lineToBeWritten = String.format("Query: %s , Status: SUCCESS\n", query);
        writeIntofile(TRANSACTION_LOG, lineToBeWritten);
    }

    private static void writeIntofile(String filePath, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath, true);
        fileWriter.write(content);
        fileWriter.close();
    }
}
