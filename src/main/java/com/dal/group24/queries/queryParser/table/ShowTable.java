package com.dal.group24.queries.queryParser.table;

import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class ShowTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public ShowTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 3) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "show");
        checkTokenValidity(tokenList, index + 1, "tables");
        DBMetadata.getInstance().showTable();
    }
}
