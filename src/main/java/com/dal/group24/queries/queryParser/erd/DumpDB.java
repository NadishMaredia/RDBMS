package com.dal.group24.queries.queryParser.erd;

import com.dal.group24.common.Printer;
import com.dal.group24.manager.DumpManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class DumpDB {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public DumpDB(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 5) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "dump");
        checkTokenValidity(tokenList, index + 1, "database");
        String databaseName = tokenList.get(index + 2);
        String withDataStr = tokenList.get(index + 3);
        boolean withData = Boolean.parseBoolean(withDataStr);
        DumpManager dumpManager = new DumpManager();
        dumpManager.dumpDatabase(databaseName, withData);
        Printer.printSuccess("Database dump taken successfully");
    }
}
