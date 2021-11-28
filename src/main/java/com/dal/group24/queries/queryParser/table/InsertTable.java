package com.dal.group24.queries.queryParser.table;

import com.dal.group24.manager.DBContents;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class InsertTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public InsertTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        int index = 0;
        checkTokenValidity(tokenList, index, "insert");
        checkTokenValidity(tokenList, index + 1, "into");
        String tableName = tokenList.get(index + 2);
        checkTokenValidity(tokenList, index + 3, "(");
        int i = index + 4;
        List<String> columnValues = new ArrayList<>();
        while (i < tokenList.size()) {
            String columnValue = tokenList.get(i);
            columnValues.add(columnValue);
            i++;
            if (tokenList.get(i).equals(")")) {
                i++;
                break;
            } else if (tokenList.get(i).equals(",")) {
                i++;
            } else {
                throw new RuntimeException("Invalid syntax");
            }
        }
        DBContents.insertRowInTable(tableName, columnValues);
        System.out.println("Row inserted successfully");
    }
}
