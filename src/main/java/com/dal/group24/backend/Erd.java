package com.dal.group24.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Erd implements Serializable {
  private final String PkTableName;
  private final String Pk;
  private final String FKTableName;
  private final String Fk;

  public Erd(String PkTableName, String Pk, String FKTableName, String Fk) {
    this.PkTableName = PkTableName;
    this.Pk = Pk;
    this.FKTableName = FKTableName;
    this.Fk = Fk;
  }

  public String getPkTableName() {
    return PkTableName;
  }

  public String getPk() {
    return Pk;
  }

  public String getFKTableName() {
    return FKTableName;
  }

  public String getFk() {
    return Fk;
  }
}
