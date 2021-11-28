package com.dal.group24.manager;

import java.io.*;
import java.util.*;

import com.dal.group24.parser.LockParser;

/*
This class will be responsible for locking the table which is
involved in the transaction.
 */
public class LockManager {
    private static Map<String, Boolean> tableIdLockMap;
    private static List<String> listOfTableLocked;
    private static final String lockFile = "dumps/locks.txt";
    private static String lockOwner="--";

    public static void init() throws IOException, ClassNotFoundException {
        lockOwner = UUID.randomUUID().toString();
        fetchContent();
        listOfTableLocked = new ArrayList<>();
    }

    public static void lockTableIfTrasactionIsOn(String tableId) throws IOException, ClassNotFoundException {
        if (isTableLocked(tableId)) {
            throw new RuntimeException("Table is already locked");
        }
        if (StateManager.is_transaction_on) {
            lockTable(tableId);
        }
    }

    public static void lockTable(String tableId) throws IOException {
        tableIdLockMap.put(tableId, true);
        listOfTableLocked.add(tableId);
        updateContent();
        lockOwner = StateManager.getetCurrentSession();
    }

    public static void unlockAllTable() throws IOException {
        for (String tableId : listOfTableLocked) {
            unlockTable(tableId);
        }
        lockOwner="--";
    }

    private static void unlockTable(String tableId) throws IOException {
        tableIdLockMap.put(tableId, false);
        updateContent();
        lockOwner="--";
    }

    public static boolean isTableLocked(String tableId) throws IOException, ClassNotFoundException {
        fetchContent();
        if (!tableIdLockMap.containsKey(tableId)) {
            return false;
        }
        if(lockOwner.equals(StateManager.getetCurrentSession())) {
            return false;
        }
        return tableIdLockMap.get(tableId);
    }

    private static void updateContent() throws IOException {
        FileWriter fileWriter = new FileWriter(lockFile);
        fileWriter.write(new LockParser().toString(tableIdLockMap));
        fileWriter.close();
    }

    private static void fetchContent() throws IOException, ClassNotFoundException {
        File file = new File(lockFile);
        if (!file.exists()) {
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new RuntimeException("Failed to create file: " + lockFile);
            }
        }
        if (file.exists() && file.length() == 0) {
            tableIdLockMap = new HashMap<>();
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String data = reader.readLine();
        tableIdLockMap = new LockParser().parse(data);
        reader.close();
    }
}
