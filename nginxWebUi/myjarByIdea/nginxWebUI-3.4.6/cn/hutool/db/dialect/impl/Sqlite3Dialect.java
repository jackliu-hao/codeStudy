package cn.hutool.db.dialect.impl;

import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Wrapper;

public class Sqlite3Dialect extends AnsiSqlDialect {
   private static final long serialVersionUID = -3527642408849291634L;

   public Sqlite3Dialect() {
      this.wrapper = new Wrapper('[', ']');
   }

   public String dialectName() {
      return DialectName.SQLITE3.name();
   }
}
