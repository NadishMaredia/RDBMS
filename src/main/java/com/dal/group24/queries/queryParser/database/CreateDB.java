package com.dal.group24.queries.queryParser.database;

import com.dal.group24.backend.Database;
import com.dal.group24.common.Printer;
import com.dal.group24.manager.StateManager;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

/*
create database 'testDB';
 */
public class CreateDB {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public CreateDB(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "create");
        checkTokenValidity(tokenList, index + 1, "database");
        String databaseName = tokenList.get(index + 2);
        Database database = new Database(StateManager.getCurrentUser().getUserName(), databaseName);
        DBMetadata.getInstance().createDB(database);
        Printer.printSuccess("Database created successfully");
    }
}
