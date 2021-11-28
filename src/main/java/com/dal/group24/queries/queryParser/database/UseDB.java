package com.dal.group24.queries.queryParser.database;

import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class UseDB {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public UseDB(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 3) {
            throw new RuntimeException("Invalid syntax");
        }
        int index = 0;
        checkTokenValidity(tokenList, index, "use");
        String databaseName = tokenList.get(index + 1);
        DBMetadata.getInstance().useDB(databaseName);
    }
}