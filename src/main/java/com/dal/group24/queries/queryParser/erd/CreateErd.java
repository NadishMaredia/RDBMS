package com.dal.group24.queries.queryParser.erd;

import com.dal.group24.backend.Database;
import com.dal.group24.backend.Erd;
import com.dal.group24.common.Util;
import com.dal.group24.manager.DBContents;
import com.dal.group24.manager.DBMetadata;
import com.dal.group24.manager.StateManager;
import com.dal.group24.queries.queryParser.QueryProcessor;

import java.io.IOException;
import java.util.List;

import static com.dal.group24.common.Util.checkTokenValidity;

public class CreateErd {
  QueryProcessor queryProcessor;
  List<String> tokenList;

  public CreateErd(QueryProcessor queryProcessor) {
    this.queryProcessor = queryProcessor;
    this.tokenList = queryProcessor.getTokenList();
  }

  public void processTokens() throws IOException, ClassNotFoundException {
    if (tokenList.size() != 4) {
      throw new RuntimeException("Invalid syntax");
    }

    // check if user has selected the db or not
    if(StateManager.getCurrentDB().equals("")) {
      throw new RuntimeException("Please select database first");
    }

    int index = 0;
    checkTokenValidity(tokenList, index, "create");
    checkTokenValidity(tokenList, index + 1, "erd");
    String databaseName = tokenList.get(index + 2);


    if(!databaseName.equals(StateManager.getCurrentDB().getDatabaseName())) {
      System.out.println("This is not the database you are currently on!");
      return;
    }

    DBMetadata.getInstance().createERD(databaseName);

  }
}
