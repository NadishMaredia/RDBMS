package com.dal.group24.queries.queryParser.table;

import com.dal.group24.backend.Table;
import com.dal.group24.manager.StateManager;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;
import com.dal.group24.queries.queryParser.Tokenizer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.dal.group24.common.Util.checkTokenValidity;

public class CreateTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public CreateTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        int index = 0;
        checkTokenValidity(tokenList, index, "create");
        checkTokenValidity(tokenList, index+1, "table");
        String tableName = tokenList.get(index+2);
        checkTokenValidity(tokenList,index+3, "(");
        int i = index+4;
        Map<String, String> columnAndItsTypes = new LinkedHashMap<>();
        List<String> primaryKeyColumns = new ArrayList<>();
        List<String> foreignKeysForeignTableAndColumn = new ArrayList<>();
        while(i<tokenList.size()) {
            String columnName = tokenList.get(i);
            i++;
            String type = tokenList.get(i);
            i++;
            columnAndItsTypes.put(columnName, type);
            if(tokenList.get(i).equalsIgnoreCase("primary")) {
                i++;
                checkTokenValidity(tokenList, i, "key");
                i++;
                primaryKeyColumns.add(columnName);
            } else if(tokenList.get(i).equals("references")) {
                i++;
                String foreignTable = tokenList.get(i);
                i++;
                checkTokenValidity(tokenList, i, "(");
                i++;
                String foreignColumn = tokenList.get(i);
                i++;
                checkTokenValidity(tokenList, i, ")");
                i++;
                foreignKeysForeignTableAndColumn.add(columnName);
                foreignKeysForeignTableAndColumn.add(foreignTable);
                foreignKeysForeignTableAndColumn.add(foreignColumn);
            }
            if(tokenList.get(i).equals(")")) {
                i++;
                break;
            } else if (tokenList.get(i).equals(",")) {
                i++;
            } else {
                throw new RuntimeException("Invalid syntax");
            }
        }
        Table table = new Table(tableName, columnAndItsTypes, primaryKeyColumns, foreignKeysForeignTableAndColumn);
        DBMetadata.getInstance().createTable(table);
        System.out.println("Table created successfully");
    }
}
