package com.dal.group24.queries.queryParser.erd;

import com.dal.group24.common.Printer;
import com.dal.group24.manager.DumpManager;
import com.dal.group24.manager.StateManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class SourceDump {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public SourceDump(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        StateManager.getCurrentDB();
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "source");
        checkTokenValidity(tokenList, index + 1, "dump");
        String fileName = tokenList.get(index + 2);
        DumpManager dumpManager = new DumpManager();
        dumpManager.sourceDump(fileName);
        Printer.printSuccess("Database dump restored successfully");
    }
}
