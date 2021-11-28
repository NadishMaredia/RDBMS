package com.dal.group24.queries.queryParser.transaction;

import com.dal.group24.manager.DBContents;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class RollbackTransaction {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public RollbackTransaction(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 3) {
            throw new RuntimeException("Invalid syntax");
        }
        int index = 0;
        checkTokenValidity(tokenList, index, "rollback");
        checkTokenValidity(tokenList, index+1, "transaction");
        checkTokenValidity(tokenList, index+2, ";");
        DBContents.rollBackTransaction();
    }
}
