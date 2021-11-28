package com.dal.group24.queries.queryParser.table;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Table;
import com.dal.group24.common.*;
import com.dal.group24.manager.DBContents;
import com.dal.group24.manager.StateManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dal.group24.common.Util.checkTokenValidity;

public class SelectTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public SelectTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        int index = 0;
        boolean ALL_COLUMN = false;
        checkTokenValidity(tokenList, index, "select");
        List<String> columns = new ArrayList<>();
        int i = index+1;
        if (tokenList.get(i).equals("*")) {
            ALL_COLUMN = true;
            i++;
        } else {
            while (i<tokenList.size()) {
                String column = tokenList.get(i);
                columns.add(column);
                i++;
                if(tokenList.get(i).equals(",")) {
                    i++;
                } else if(tokenList.get(i).equals("from")) {
                    break;
                } else {
                    String errorMsg = String.format("Error: Token: %s found at index: %d", tokenList.get(i), index);
                    throw new RuntimeException(errorMsg);
                }
            }
        }
        checkTokenValidity(tokenList, i, "from");
        i++;
        String tableName = tokenList.get(i);
        i++;
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
        }

        DBContents.displayTableContent(tableName, columns, conditions, operator, ALL_COLUMN);
    }

}
