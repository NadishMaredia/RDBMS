package com.dal.group24.queries.queryParser.user;

import com.dal.group24.backend.User;
import com.dal.group24.common.Util;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

/*
create user with (ben, ben123);
 */
public class CreateUser {
    QueryProcessor queryProcessor;
    List<String> tokenList;

    public CreateUser(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
        this.tokenList = queryProcessor.getTokenList();
    }

    public void processTokens() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        if (tokenList.size() != 9) {
            throw new RuntimeException("Invalid syntax");
        }

        int index = 0;
        checkTokenValidity(tokenList, index, "create");
        checkTokenValidity(tokenList, index + 1, "user");
        checkTokenValidity(tokenList, index + 2, "with");
        checkTokenValidity(tokenList, index + 3, "(");
        checkTokenValidity(tokenList, index + 5, ",");
        checkTokenValidity(tokenList, index + 7, ")");
        String userName = tokenList.get(index + 4);
        String password = tokenList.get(index + 6);
        String sha1Password = Util.getSha1(password);
        System.out.println("Security question: Enter your pet name: ");
        String securityAns = queryProcessor.getInputs().getStringInput();
        User user = new User(userName, sha1Password, securityAns);
        DBMetadata dbMetadata = DBMetadata.getInstance();
        dbMetadata.createUser(user);
    }
}
