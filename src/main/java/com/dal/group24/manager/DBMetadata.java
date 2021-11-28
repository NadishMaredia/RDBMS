package com.dal.group24.manager;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Erd;
import com.dal.group24.backend.Table;
import com.dal.group24.backend.User;
import com.dal.group24.common.AlterTableParameters;
import com.dal.group24.parser.MetadataParser;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBMetadata {
    private Map<String, User> mapOfUsernameAndUser;
    private List<Erd> erdList;
    private final String metafile = "dumps/metadata.txt";
    private final String metaERDfile = "dumps/erd/";
    private static DBMetadata dbMetadata;

    public static DBMetadata getInstance() throws IOException, ClassNotFoundException {
        if (dbMetadata == null) {
            dbMetadata = new DBMetadata();
        }
        return dbMetadata;
    }

    private DBMetadata() throws IOException, ClassNotFoundException {
        File file = new File(metafile);
        if (file.exists() && file.length() == 0) {
            mapOfUsernameAndUser = new HashMap<>();
            erdList = new ArrayList<Erd>();
            createRootUser();
        }
        erdList = new ArrayList<Erd>();
        fetchContent();
    }

    public void createUser(User user) throws IOException {
        if (doesUserExists(user.getUserName())) {
            throw new RuntimeException(String.format("Username: %s already exists", user.getUserName()));
        }
        mapOfUsernameAndUser.put(user.getUserName(), user);
        updateContent();
    }

    public void deleteUser(String userName) throws IOException {
        if (!doesUserExists(userName)) {
            throw new RuntimeException(String.format("Username: %s doesn't exists", userName));
        }
        mapOfUsernameAndUser.remove(userName);
        updateContent();
    }

    public void createRootUser() throws IOException {
        String userName = "root";
        String sha1Password = "cf2e875d70c402e4aaf32ceb64b1fa6f7396af59";
        User user = new User(userName, sha1Password);
        createUser(user);
    }

    public void createDB(Database database) throws IOException {
        User user = StateManager.getCurrentUser();
        user.addDatabaseUnderUser(database);
        mapOfUsernameAndUser.put(user.getUserName(), user);
        updateContent();
    }

    public void dropDB(String dbName) throws IOException {
        User user = StateManager.getCurrentUser();
        user.removeDatabaseUnderUser(dbName);
        mapOfUsernameAndUser.put(user.getUserName(), user);
        updateContent();
    }

    public void showDB() {
        String currentUserName = StateManager.getCurrentUser().getUserName();
        Map<String, User> mapOfUsernameAndUser = dbMetadata.getMapOfUsernameAndUser();
        User user = mapOfUsernameAndUser.get(currentUserName);
        List<Database> databaseList = user.getDatabaseList();
        if (databaseList.size() == 0) {
            System.out.printf("There is no database under the user: %s\n", currentUserName);
            return;
        }
        System.out.printf("List of database under the username: %s\n", currentUserName);
        System.out.println("========================");
        for (Database database : databaseList) {
            System.out.println(database.getDatabaseName());
        }
    }

    public void useDB(String dbName) {
        String currentUserName = StateManager.getCurrentUser().getUserName();
        Map<String, User> mapOfUsernameAndUser = dbMetadata.getMapOfUsernameAndUser();
        User user = mapOfUsernameAndUser.get(currentUserName);
        List<Database> databaseList = user.getDatabaseList();
        if (databaseList.size() == 0) {
            throw new RuntimeException("No database of name: " + dbName);
        }
        boolean switched = false;
        for (Database database : databaseList) {
            if (database.getDatabaseName().equals(dbName)) {
                StateManager.setCurrentDB(database);
                System.out.println("Switched to database: " + dbName);
                switched = true;
            }
        }
        if (!switched) {
            System.out.printf("There is no database with name: %s under username: %s\n", dbName, currentUserName);
        }
    }

    public void createTable(Table table) throws IOException {
        Database database = StateManager.getCurrentDB();
        Helper.isColumnTypeSupported(table.getColumnAndItsTypes());
        database.addTable(table);
        updateContent();
    }

    public void alterTable(String tableName, AlterTableParameters alterTableParameters) throws IOException {
        Table table = getTable(tableName);
        Map<String, String> columnAndItsTypes = table.getColumnAndItsTypes();
        if (alterTableParameters.getOperation().equalsIgnoreCase("add")) {
            Helper.isColumnTypeSupported(alterTableParameters.getNewColumnType());
            columnAndItsTypes.put(alterTableParameters.getNewColumnName(), alterTableParameters.getNewColumnType());
        } else if (alterTableParameters.getOperation().equalsIgnoreCase("drop")) {
            if (!columnAndItsTypes.containsKey(alterTableParameters.getOldColumnName())) {
                throw new RuntimeException(
                        alterTableParameters.getOldColumnName() + " doesn't exists in the table: " + tableName);
            }
            columnAndItsTypes.remove(alterTableParameters.getOldColumnName());
        } else {
            throw new RuntimeException("Invalid operation");
        }
        updateContent();
    }

    public void dropTable(String tableName) throws IOException {
        Database database = StateManager.getCurrentDB();
        database.removeTable(tableName);
        updateContent();
    }

    public void showTable() {
        String currentUserName = StateManager.getCurrentUser().getUserName();
        Database currentDB = StateManager.getCurrentDB();
        List<Table> tableList = currentDB.getTableList();
        if (tableList.size() == 0) {
            System.out.printf("There is no table under the database: %s and user: %s\n", currentDB.getDatabaseName(),
                    currentUserName);
            return;
        }
        System.out.printf("List of tables under the database: %s and username: %s\n", currentDB.getDatabaseName(),
                currentUserName);
        System.out.println("========================");
        for (Table table : tableList) {
            System.out.println(table.asString());
        }
    }

    public void updateContent() throws IOException {
        FileWriter fileWriter = new FileWriter(metafile);
        fileWriter.write(new MetadataParser().toString(mapOfUsernameAndUser));
        fileWriter.close();
    }

    private void fetchContent() throws IOException, ClassNotFoundException {
        File file = new File(metafile);
        if (file.exists() && file.length() == 0) {
            throw new RuntimeException("Metadata file is empty");
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String data = reader.readLine();
        mapOfUsernameAndUser = new MetadataParser().parse(data);
        reader.close();
    }

    public void createERD(String databaseName) {

        User user = StateManager.getCurrentUser();
        Database db = user.getDatabase(databaseName);
        List<Table> tableslIst = db.getTableList();
        for (Table table : tableslIst) {
            List<String> keys = table.getForeignKeysForeignTableAndColumn();
            if (keys.size() == 0) {
                try {
                    updateContentERD(databaseName, tableslIst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Erd erd = new Erd(keys.get(1), keys.get(2), table.getTableName(), keys.get(0));
            erdList.add(erd);
            try {
                updateContentERD(databaseName, tableslIst);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // System.out.println(table.getTableName() +" ---> " +keys.get(0) +"(FK) <--->"
            // +keys.get(1) +"----> " +keys.get(2) +"(PK)");
        }
        erdList.clear();
        System.out.println("ERD generated!!");
    }

    public void updateContentERD(String databaseName, List<Table> tableList) throws IOException {

        String fileName = metaERDfile + databaseName + ".txt";
        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }

        FileWriter fileWriter = new FileWriter(metaERDfile + databaseName + ".txt");
        fileWriter.write("Database = " + databaseName + "\n");

        for (Table table : tableList) {
            fileWriter.write("Table Name = " + table.getTableName() + "\n");
            /*for (String key : table.getColumnAndItsTypes().keySet()) {
                fileWriter.write(key + " : " + table.getColumnAndItsTypes().get(key) + "\n");
            }*/
            fileWriter.write(table.asString());
            fileWriter.write("\n\n");
        }

        fileWriter.write("Relationships\n");
        for (Erd erd : erdList) {
            fileWriter.write(erd.getPkTableName() + " -> " + erd.getPk() + "(PK) --->" + erd.getFKTableName() + " -> "
                    + erd.getFk() + "(FK)\n");
        }
        fileWriter.close();
    }

    public Map<String, User> getMapOfUsernameAndUser() {
        return mapOfUsernameAndUser;
    }

    private Table getTable(String tableName) {
        User user = StateManager.getCurrentUser();
        String userName = user.getUserName();
        Database currentDb = mapOfUsernameAndUser.get(userName)
                .getDatabase(StateManager.getCurrentDB().getDatabaseName());
        return currentDb.getTable(tableName);
    }

    private boolean doesUserExists(String userName) {
        return mapOfUsernameAndUser.containsKey(userName);
    }
}