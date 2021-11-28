package com.dal.group24.parser;

import java.util.HashMap;
import java.util.Map;

public class LockParser {
    public String toString(Map<String, Boolean> lockList) {
        return lockList.toString();
    }

    public Map<String, Boolean> parse(String locks) {
        locks = locks.substring(1, locks.length() - 1);
        String[] keyValues = locks.split(", ");
        Map<String, Boolean> lockList = new HashMap<>();
        for (String keyValue : keyValues) {
            String[] pair = keyValue.split("=");
            lockList.put(pair[0], Boolean.valueOf(pair[1]));
        }
        return lockList;
    }
}
