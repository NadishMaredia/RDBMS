package com.dal.group24.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dal.group24.backend.TableContent;

public class ContentParser {
    public String toString(Map<String, TableContent> database) {
        String data = "";
        if (database.size() > 0) {
            for (String key : database.keySet()) {
                data += "<" + key + ":[";
                data += "columns:" + database.get(key).getColumnAndItsTypes().toString() + ";";
                data += "rows:[";
                List<Map<String, Object>> rows = database.get(key).getRows();
                if (rows.size() > 0) {
                    for (Map<String, Object> row : rows) {
                        data += row.toString() + ";";
                    }
                }
                data += "]]>";
            }
        }
        return data;
    }

    public Map<String, TableContent> parse(String data) {
        Map<String, TableContent> database = new HashMap<>();
        int dataLength = data.length();
        int pointer = 0;
        while (pointer < dataLength) {
            int startIndex = data.indexOf('<', pointer);
            int endIndex = data.indexOf('>', pointer);
            if (startIndex > -1 && endIndex > -1) {
                String table = data.substring(startIndex, endIndex);
                String tableId = table.substring(1, table.indexOf(':'));
                String columns = table.substring(table.indexOf("columns:{") + 9, table.indexOf("};"));
                String[] columnPairs = columns.split(",");
                Map<String, String> columnAndItsTypes = new LinkedHashMap<>();
                for (int i = 0; i < columnPairs.length; i++) {
                    String pair = columnPairs[i];
                    String[] keyValue = pair.split("=");
                    columnAndItsTypes.put(keyValue[0].trim(), keyValue[1].trim());
                }
                String rowData = table.substring(table.indexOf("rows:[") + 6, table.indexOf("]]"));
                String[] rows = rowData.split(";");
                List<Map<String, Object>> rowsList = new ArrayList<>();
                for (int i = 0; i < rows.length; i++) {
                    String singleRowData = rows[i].substring(1, rows[i].length() - 1);
                    Map<String, Object> innerColumns = new HashMap<>();
                    String[] rowColumnKeyValues = singleRowData.split(",");
                    for (int j = 0; j < rowColumnKeyValues.length; j++) {
                        String pair = rowColumnKeyValues[j];
                        String[] keyValue = pair.split("=");
                        String key = keyValue[0].trim();
                        Object val = (Object) keyValue[1].trim();
                        innerColumns.put(key, val);
                    }
                    rowsList.add(innerColumns);
                }
                TableContent tableObject = new TableContent(tableId, columnAndItsTypes);
                tableObject.setRows(rowsList);
                database.put(tableId, tableObject);
                pointer = endIndex + 1;
            } else {
                break;
            }
        }
        return database;
    }
}
