package com.dal.group24.queries.queryParser.user;

import com.dal.group24.common.Util;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

/*
delete user ben;
 */
public class DeleteUser {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public DeleteUser(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException {
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }
        int index = 0;
        checkTokenValidity(tokenList, index, "delete");
        checkTokenValidity(tokenList, index+1, "user");
        String userName = tokenList.get(index + 2);
        DBMetadata.getInstance().deleteUser(userName);
    }
}
