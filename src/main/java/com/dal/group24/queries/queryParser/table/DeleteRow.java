package com.dal.group24.queries.queryParser.table;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Table;
import com.dal.group24.common.*;
import com.dal.group24.manager.StateManager;
import com.dal.group24.manager.DBContents;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.*;

import static com.dal.group24.common.Util.checkTokenValidity;

public class DeleteRow {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public DeleteRow(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        int index = 0;

        checkTokenValidity(tokenList, index, "delete");
        checkTokenValidity(tokenList, index + 1, "from");
        String tableName = tokenList.get(index + 2);
        if(tokenList.get(index+3).equals(";")) {
            DBContents.truncateTable(tableName);
            Printer.success();
            return;
        }
        int i = index + 3;
        List<Condition> conditions = new ArrayList<>();
        Operator operator = null;
        if (tokenList.get(i).equals("where")) {
            i++;
            String conditionColumnName1 = tokenList.get(i);
            i++;
            SIGN operator1 = MathOperators.valueOf(tokenList.get(i));
            i++;
            String columnValue1 = tokenList.get(i);
            conditions.add(new Condition(conditionColumnName1, columnValue1, operator1));
            i++;
            if(!tokenList.get(i).equals(";")) {
                if(tokenList.get(i).equalsIgnoreCase("and")) {
                    operator = Operators.valueOf(tokenList.get(i));
                } else if(tokenList.get(i).equalsIgnoreCase("or")) {
                    operator = Operators.valueOf(tokenList.get(i));
                } else {
                    throw new RuntimeException("Invalid syntax");
                }
                i++;
                String conditionColumnName2 = tokenList.get(i);
                i++;
                SIGN operator2 = MathOperators.valueOf(tokenList.get(i));
                i++;
                String columnValue2 = tokenList.get(i);
                conditions.add(new Condition(conditionColumnName2, columnValue2, operator2));
            }
        } else {
            throw new RuntimeException("Invalid synctax");
        }

        DBContents.deleteRows(tableName, conditions, operator);
    }
}
