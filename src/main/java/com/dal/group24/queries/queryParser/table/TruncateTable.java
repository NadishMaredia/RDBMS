package com.dal.group24.queries.queryParser.table;

import com.dal.group24.manager.DBContents;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

/*
truncate table player;
 */
public class TruncateTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public TruncateTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "truncate");
        checkTokenValidity(tokenList, index + 1, "table");
        String tablename = tokenList.get(index + 2);
        DBContents.truncateTable(tablename);
        System.out.println("Table truncated successfully");
    }
}
