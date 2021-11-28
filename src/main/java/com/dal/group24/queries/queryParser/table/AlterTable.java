package com.dal.group24.queries.queryParser.table;

import com.dal.group24.common.AlterTableParameters;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class AlterTable {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public AlterTable(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        int index = 0;
        checkTokenValidity(tokenList, index, "alter");
        checkTokenValidity(tokenList, index + 1, "table");
        String tableName = tokenList.get(index + 2);
        int i = index + 3;
        String userOp = tokenList.get(i);
        AlterTableParameters alterTableParameters = new AlterTableParameters();
        if (userOp.equalsIgnoreCase("add")) {
            alterTableParameters.setOperation(userOp);
            i++;
            checkTokenValidity(tokenList, i, "column");
            i++;
            String newColumnName = tokenList.get(i);
            i++;
            String newColumnType = tokenList.get(i);
            i++;
            alterTableParameters.setNewColumnName(newColumnName);
            alterTableParameters.setNewColumnType(newColumnType);
        } else if (userOp.equalsIgnoreCase("drop")) {
            alterTableParameters.setOperation(userOp);
            i++;
            checkTokenValidity(tokenList, i, "column");
            i++;
            String columnToBeDeleted = tokenList.get(i);
            i++;
            alterTableParameters.setOldColumnName(columnToBeDeleted);
        } else if (userOp.equalsIgnoreCase("rename")) {
            throw new RuntimeException("Not yet implemented");
        } else {
            throw new RuntimeException("Invalid sytanx");
        }
        checkTokenValidity(tokenList, i, ";");
        DBMetadata.getInstance().alterTable(tableName, alterTableParameters);
        System.out.println("Table altered successfully");
    }
}
