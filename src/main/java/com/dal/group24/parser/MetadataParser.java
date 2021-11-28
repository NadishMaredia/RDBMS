package com.dal.group24.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Table;
import com.dal.group24.backend.User;

public class MetadataParser {
    public String toString(Map<String, User> users) {
        String data = "";
        if (users.size() > 0) {
            for (String key : users.keySet()) {
                data += "<" + key + ":[";
                data += "databases:{{";
                List<Database> databases = users.get(key).getDatabaseList();
                if (databases.size() > 0) {
                    for (Database database : databases) {
                        data += database.toString() + ";";
                    }
                    // data = data.substring(0, data.length() - 1);
                }
                data += "}},";
                data += "userName:" + users.get(key).getUserName() + ",";
                data += "userId:" + users.get(key).getUserId() + ",";
                data += "password:" + users.get(key).getPassword() + ",";
                data += "isMultifactorEnabled:" + users.get(key).isMultiFactorEnabled() + ",";
                data += "securityAnswer:" + users.get(key).getSecurityAnswer();
                data += "]>";
            }
        }
        return data;
    }

    public Map<String, User> parse(String data) {
        Map<String, User> users = new HashMap<>();
        int dataLength = data.length();
        int pointer = 0;
        while (pointer < dataLength) {
            int startIndex = data.indexOf('<', pointer);
            int endIndex = data.indexOf('>', pointer);
            if (startIndex > -1 && endIndex > -1) {
                int innerCursor = 0;
                String userData = data.substring(startIndex, endIndex);
                innerCursor = userData.indexOf(':');
                String userName = userData.substring(1, innerCursor);
                innerCursor++;

                innerCursor = userData.indexOf("}}");
                String databases = userData.substring(userData.indexOf("databases:{{") + 12, innerCursor);
                innerCursor += 3;

                String[] databasesArr = databases.split(";");
                List<Database> databasesList = new ArrayList<>();
                if (databasesArr.length > 0) {
                    for (String database : databasesArr) {
                        if (!database.equals("")) {
                            databasesList.add(parseDatabase(database));
                        }
                    }
                }

                innerCursor = userData.indexOf(",", innerCursor);
                innerCursor++;
                innerCursor = userData.indexOf(",", innerCursor);
                String userId = userData.substring(userData.indexOf("userId:") + 7, innerCursor);
                innerCursor++;
                innerCursor = userData.indexOf(",", innerCursor);
                String password = userData.substring(userData.indexOf("password:") + 9, innerCursor);
                innerCursor++;

                innerCursor = userData.indexOf(",", innerCursor);
                Boolean isMultifactorEnabled = Boolean
                        .parseBoolean(userData.substring(userData.indexOf("isMultifactorEnabled:") + 21, innerCursor));
                innerCursor++;

                innerCursor = userData.indexOf("]", innerCursor);
                String securityAnswer = userData.substring(userData.indexOf("securityAnswer:") + 15, innerCursor);
                innerCursor++;

                User userObject = new User();
                userObject.setUserName(userName);
                userObject.setUserId(userId);
                userObject.setPassword(password);
                userObject.setIsMultiFactorEnabled(isMultifactorEnabled);
                userObject.setSecurityAnswer(securityAnswer.equals("null") ? null : securityAnswer);
                userObject.setDatabaseList(databasesList);
                users.put(userName, userObject);
            } else {
                break;
            }
            pointer = endIndex + 1;
        }
        return users;
    }

    private Database parseDatabase(String database) {
        Database dataBase = new Database();
        int dataLength = database.length();
        database = database.substring(1, dataLength - 1);
        int pointer = database.indexOf(",", 0);
        String databaseId = database.substring(database.indexOf("databaseId:") + 11, pointer);
        dataBase.setDatabaseId(databaseId);
        pointer++;
        pointer = database.indexOf(",", pointer);
        String databaseName = database.substring(database.indexOf("databaseName:") + 13, pointer);
        dataBase.setDatabaseName(databaseName);
        pointer++;
        pointer = database.indexOf(",", pointer);
        String userName = database.substring(database.indexOf("userName:") + 9, pointer);
        dataBase.setUserName(userName);
        pointer++;
        String tableList = database.substring(database.indexOf("tableList:") + 11, dataLength - 3);
        List<Table> tablesList = new ArrayList<>();
        String[] tables = tableList.split("::");
        for (String table : tables) {
            if (!table.equals("")) {
                tablesList.add(parseTable(table));
            }
        }
        dataBase.setTableList(tablesList);
        return dataBase;
    }

    private Table parseTable(String tableData) {
        Table tableObject = new Table();
        int dataLength = tableData.length();
        tableData = tableData.substring(1, dataLength - 1);
        int pointer = tableData.indexOf(",", 0);
        String parentDatabase = tableData.substring(tableData.indexOf("parentDatabase:") + 15, pointer);
        tableObject.setParentDatabase(parentDatabase.equals("null") ? null : parentDatabase);
        pointer++;
        pointer = tableData.indexOf(",", pointer);
        String tableName = tableData.substring(tableData.indexOf("tableName:") + 10, pointer);
        tableObject.setTableName(tableName);
        pointer++;
        pointer = tableData.indexOf(",", pointer);
        String tableId = tableData.substring(tableData.indexOf("tableId:") + 8, pointer);
        tableObject.setTableId(tableId);
        ;
        pointer++;
        pointer = tableData.indexOf("},", pointer);
        String columnAndItsTypes = tableData.substring(tableData.indexOf("columnAndItsTypes:") + 19, pointer);
        Map<String, String> columnAndItsTypesMap = new LinkedHashMap<>();
        if (!columnAndItsTypes.equals("")) {
            String[] pairs = columnAndItsTypes.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                columnAndItsTypesMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        tableObject.setColumnAndItsTypes(columnAndItsTypesMap);
        pointer += 2;
        pointer = tableData.indexOf("],", pointer);
        String primaryKeyColumns = tableData.substring(tableData.indexOf("primaryKeyColumns:") + 19, pointer);
        String[] primaryKeyColumnsArr = primaryKeyColumns.split(",");
        List<String> primaryKeyColumnsList = new ArrayList<>();
        if (primaryKeyColumnsArr.length > 0) {
            for (String key : primaryKeyColumnsArr) {
                if (!key.trim().equals("")) {
                    primaryKeyColumnsList.add(key.trim());
                }
            }
        }
        tableObject.setPrimaryKeyColumns(primaryKeyColumnsList);
        pointer += 2;
        pointer = tableData.indexOf("]", pointer);
        String foreignKeysForeignTableAndColumn = tableData
                .substring(tableData.indexOf("foreignKeysForeignTableAndColumn:") + 34, pointer);
        String[] foreignKeysForeignTableAndColumnArr = foreignKeysForeignTableAndColumn.split(",");
        List<String> foreignKeysForeignTableAndColumnList = new ArrayList<>();
        if (foreignKeysForeignTableAndColumnArr.length > 0) {
            for (String key : foreignKeysForeignTableAndColumnArr) {
                if (!key.trim().equals("")) {
                    foreignKeysForeignTableAndColumnList.add(key.trim());
                }
            }
        }
        tableObject.setForeignKeysForeignTableAndColumn(foreignKeysForeignTableAndColumnList);
        return tableObject;
    }
}
