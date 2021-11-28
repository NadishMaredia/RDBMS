package com.dal.group24.queries.queryParser;

import com.dal.group24.inputs.IInputs;
import com.dal.group24.queries.queryParser.database.CreateDB;
import com.dal.group24.queries.queryParser.database.DropDB;
import com.dal.group24.queries.queryParser.database.ShowDB;
import com.dal.group24.queries.queryParser.database.UseDB;
import com.dal.group24.queries.queryParser.erd.CreateErd;
import com.dal.group24.queries.queryParser.erd.DumpDB;
import com.dal.group24.queries.queryParser.erd.SourceDump;
import com.dal.group24.queries.queryParser.table.*;
import com.dal.group24.queries.queryParser.transaction.CommitTrasaction;
import com.dal.group24.queries.queryParser.transaction.RollbackTransaction;
import com.dal.group24.queries.queryParser.transaction.StartTransaction;
import com.dal.group24.queries.queryParser.user.CreateUser;
import com.dal.group24.queries.queryParser.user.DeleteUser;
import com.dal.group24.queries.queryParser.user.Userlogin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class QueryProcessor {
    String query;
    List<String> tokenList;

    private IInputs inputs=null;

    public QueryProcessor(String query) {
        this.query = query;
    }

    public QueryProcessor(String query, IInputs inputs) {
        this.query = query;
        this.inputs = inputs;
        validateSemicolonSuffix(query);
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public void parseQuery() throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        Tokenizer tokenizer = new Tokenizer(query);
        tokenList = tokenizer.tokenizeQuery();
        processTokens(tokenList);
    }

    private void processTokens(List<String> tokens) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        int index = 0;

        String firstWord = tokens.get(index);
        String secondWord = tokens.get(index + 1);
        String firstTwoWord = String.format("%s %s", firstWord, secondWord);

        if (firstWord.equalsIgnoreCase("-u")) {
            new Userlogin(this).processTokens();
        } else if (firstWord.equalsIgnoreCase("select")) {
            new SelectTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("insert into")) {
            new InsertTable(this).processTokens();
        } else if (firstWord.equalsIgnoreCase("update")) {
            new UpdateTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("create table")) {
            CreateTable createTable = new CreateTable(this);
            createTable.processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("drop table")) {
            new DropTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("show tables")) {
            new ShowTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("delete from")) {
            new DeleteRow(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("alter table")) {
            new AlterTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("delete user")) {
            DeleteUser deleteUser = new DeleteUser(this);
            deleteUser.processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("create database")) {
            new CreateDB(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("create user")) {
            new CreateUser(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("drop database")) {
            new DropDB(this).processTokens();
        } else if (firstWord.equalsIgnoreCase("truncate")) {
            new TruncateTable(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("show databases")) {
            new ShowDB(this).processTokens();
        } else if (firstWord.equalsIgnoreCase("use")) {
            new UseDB(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("start transaction")) {
            new StartTransaction(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("commit transaction")) {
            new CommitTrasaction(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("rollback transaction")) {
            new RollbackTransaction(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("create erd")) {
            new CreateErd(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("dump database")) {
            new DumpDB(this).processTokens();
        } else if (firstTwoWord.equalsIgnoreCase("source dump")) {
            new SourceDump(this).processTokens();
        } else {
            throw new RuntimeException("Invalid Syntax");
        }
    }

    private void validateSemicolonSuffix(String query) {
        if (query.startsWith("-u")) {
            return;
        }
        if (!query.endsWith(";")) {
            throw new RuntimeException("Invalid syntax(semi colon missing in the query)");
        }
    }

    public IInputs getInputs() {
        return inputs;
    }
}
