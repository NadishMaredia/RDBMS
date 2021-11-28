package com.dal.group24.queries.queryParser.database;

import com.dal.group24.common.Printer;
import com.dal.group24.manager.StateManager;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

/*
drop database 'testDB';
*/

public class DropDB {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public DropDB(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "drop");
        checkTokenValidity(tokenList, index + 1, "database");
        String databaseName = tokenList.get(index + 2);
        DBMetadata.getInstance().dropDB(databaseName);
        Printer.printSuccess("Database dropped successfully");
    }
}
