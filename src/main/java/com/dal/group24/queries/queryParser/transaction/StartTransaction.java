package com.dal.group24.queries.queryParser.transaction;

import com.dal.group24.manager.DBContents;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class StartTransaction {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public StartTransaction(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 3) {
            throw new RuntimeException("Invalid syntax");
        }
        int index = 0;
        checkTokenValidity(tokenList, index, "start");
        checkTokenValidity(tokenList, index+1, "transaction");
        checkTokenValidity(tokenList, index+2, ";");
        DBContents.startTransaction();
    }
}
