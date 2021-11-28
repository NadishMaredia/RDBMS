package com.dal.group24.queries.queryParser.user;

import com.dal.group24.backend.User;
import com.dal.group24.common.Printer;
import com.dal.group24.manager.StateManager;
import com.dal.group24.common.Util;
import com.dal.group24.manager.DBContents;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.manager.LockManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class Userlogin {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public Userlogin(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        if (tokenList.size() != 4) {
            throw new RuntimeException("Invalid syntax");
        }
        int index = 0;
        String userName = "", password = "";
        while (index < tokenList.size() - 1) {
            String p = tokenList.get(index);
            if (p.equals("-u")) {
                userName = tokenList.get(index + 1);
            } else if (p.equals("-p")) {
                password = tokenList.get(index + 1);
            } else {
                throw new RuntimeException("Invalid syntax for user login query");
            }
            index = index + 2;
        }
        DBMetadata dbMetadata = DBMetadata.getInstance();
        Map<String, User> usersMap = dbMetadata.getMapOfUsernameAndUser();
        if (!usersMap.containsKey(userName)) {
            throw new RuntimeException(String.format("Username: %s not found", userName));
        }

        User user = usersMap.get(userName);
        String sha1Password = Util.getSha1(password);
        if (userName.equals(user.getUserName()) && (sha1Password.equals(user.getPassword()))) {
            if(user.isMultiFactorEnabled()) {
                System.out.println("Security check question\nEnter your pet name: ");
                String ans = queryProcessor.getInputs().getStringInput();
                if(!user.getSecurityAnswer().equals(ans)) {
                    Printer.printError("Login attempt failed due to wrong security answer");
                    throw new RuntimeException("Login failed");
                }
            }
            StateManager.setCurrentUser(user);
            Printer.printSuccess("Login successful");
        } else {
            Printer.printError("Login attempt failed due to invalid credentials");
            throw new RuntimeException("Login failed");
        }
        StateManager.setCurrentSession();
        LockManager.init();
        DBContents.init();
    }
}
