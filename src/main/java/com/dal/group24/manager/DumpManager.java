package com.dal.group24.manager;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Table;
import com.dal.group24.backend.TableContent;
import com.dal.group24.backend.User;
import com.dal.group24.queries.queryParser.QueryProcessor;

public class DumpManager {
    public static final String SQL_DUMP = "SQLDump";

    public void dumpDatabase(String databaseName, boolean withData) throws IOException {
        User user = StateManager.getCurrentUser();
        Database db = user.getDatabase(databaseName);
        if (db != null) {
            createDirIfNotExist(SQL_DUMP);
            List<Table> tableList = db.getTableList();
            if (tableList.size() > 0) {
                String fileName = SQL_DUMP + "/sqldump_"
                        + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";
                File file = new File(fileName);
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                String dumpData = "";
                dumpData += getTableCreateQueries(tableList);
                if (withData) {
                    dumpData += getTableDataInsertQueries(tableList);
                }
                writer.write(dumpData);
                writer.close();
            } else {
                throw new RuntimeException("No table found to dump.");
            }
        } else {
            throw new RuntimeException("No such database found for the user.");
        }
    }

    private File createDirIfNotExist(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private String getTableCreateQueries(List<Table> tableList) {
        String dumpData = "";
        for (Table table : tableList) {
            dumpData += "create table " + table.getTableName() + "(";
            Map<String, String> columnAndItsTypes = table.getColumnAndItsTypes();
            List<String> primaryKeys = table.getPrimaryKeyColumns();
            List<String> foreignKeys = table.getForeignKeysForeignTableAndColumn();
            for (String columnName : columnAndItsTypes.keySet()) {
                String primary = "";
                if (isPrimary(columnName, primaryKeys)) {
                    primary = " primary key";
                }
                String foreign = "";
                String temp = isForeign(columnName, foreignKeys);
                if (temp != null) {
                    foreign = " " + temp;
                }
                dumpData += columnName + " " + columnAndItsTypes.get(columnName) + primary + foreign + ",";
            }
            dumpData = dumpData.substring(0, dumpData.length() - 1) + ");\n";
        }
        return dumpData + "\n";
    }

    private String getTableDataInsertQueries(List<Table> tableList) {
        String dumpData = "";
        try {
            DBContents.init();
            DBContents db = new DBContents();
            Map<String, TableContent> tableContent = db.getTableContentMap();
            for (Table table : tableList) {
                Map<String, String> columns = table.getColumnAndItsTypes();
                if (tableContent.containsKey(table.getTableId())) {
                    List<Map<String, Object>> tableData = tableContent.get(table.getTableId()).getRows();
                    for (Map<String, Object> row : tableData) {
                        dumpData += "insert into " + table.getTableName() + "(";
                        for (String columnName : columns.keySet()) {
                            dumpData += row.get(columnName).toString() + ",";
                        }
                        dumpData = dumpData.substring(0, dumpData.length() - 1) + ");\n";
                    }
                }
                dumpData += "\n";
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Unable to dump insert queries.");
        }
        return dumpData;
    }

    private boolean isPrimary(String key, List<String> keyList) {
        for (String k : keyList) {
            if (k.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private String isForeign(String key, List<String> keyList) {
        try {
            int counter = 0;
            for (String k : keyList) {
                if (k.equalsIgnoreCase(key)) {
                    return "references " + keyList.get(counter + 1) + "(" + keyList.get(counter + 2) + ")";
                }
                counter++;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void sourceDump(String fileName) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        File file = new File(fileName);
        if(!file.exists()) {
            throw new RuntimeException(String.format("Source file: %s doesn't exists", fileName));
        }
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String query;
        while ((query=br.readLine()) != null) {
            if(query.trim().isEmpty()) {
                continue;
            }
            System.out.println(query);
            QueryProcessor queryProcessor = new QueryProcessor(query);
            queryProcessor.parseQuery();
        }
    }
}