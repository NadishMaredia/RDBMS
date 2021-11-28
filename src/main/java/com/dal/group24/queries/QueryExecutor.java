package com.dal.group24.queries;

import com.dal.group24.common.Printer;
import com.dal.group24.common.Util;
import com.dal.group24.inputs.IInputs;
import com.dal.group24.inputs.StoredInputs;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.manager.LogManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.dal.group24.manager.StateManager.SLEEP_TIME;

public class QueryExecutor {
    private final IInputs inputs;

    public QueryExecutor(IInputs inputs) {
        this.inputs = inputs;
    }

    public void startApplication() throws IOException, ClassNotFoundException {
        LogManager.init();
        DBMetadata.getInstance();
        System.out.println("Welcome to NNDDb.");
        System.out.println("Please login to continue using NND database using this simple syntax: ");
        System.out.println("-u <userName> -p <password>");
        executeQuery();
    }

    private void executeQuery() throws IOException {
        while (true) {
            if (SLEEP_TIME != 0) {
                justSleep();
            }
            Util.printCursor();
            String userQuery = inputs.getStringInput();
            if (userQuery.equals("q") || userQuery.equalsIgnoreCase("exit")) {
                System.out.println("Exit from the database. Bye!");
                break;
            }
            try {
                if (inputs instanceof StoredInputs) {
                    System.out.println(userQuery);
                }
                processQuery(userQuery);
                LogManager.writeGeneralOrTrasactionQueryLog(userQuery);
                System.out.println("\n");
            } catch (Exception e) {
                Printer.printError(e.getMessage());
                LogManager.writeErrorLog(userQuery, e.getMessage());
                System.out.println("\n");
                //e.printStackTrace();
            }
        }
    }

    void processQuery(String q) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        QueryProcessor queryProcessor = new QueryProcessor(q, inputs);
        queryProcessor.parseQuery();
    }

    void justSleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
