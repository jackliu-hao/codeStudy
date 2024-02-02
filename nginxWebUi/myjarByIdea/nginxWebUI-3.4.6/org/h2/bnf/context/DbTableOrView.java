package org.h2.bnf.context;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DbTableOrView {
   private final DbSchema schema;
   private final String name;
   private final String quotedName;
   private final boolean isView;
   private DbColumn[] columns;

   public DbTableOrView(DbSchema var1, ResultSet var2) throws SQLException {
      this.schema = var1;
      this.name = var2.getString("TABLE_NAME");
      String var3 = var2.getString("TABLE_TYPE");
      this.isView = "VIEW".equals(var3);
      this.quotedName = var1.getContents().quoteIdentifier(this.name);
   }

   public DbSchema getSchema() {
      return this.schema;
   }

   public DbColumn[] getColumns() {
      return this.columns;
   }

   public String getName() {
      return this.name;
   }

   public boolean isView() {
      return this.isView;
   }

   public String getQuotedName() {
      return this.quotedName;
   }

   public void readColumns(DatabaseMetaData var1, PreparedStatement var2) throws SQLException {
      ResultSet var3;
      if (this.schema.getContents().isH2()) {
         var2.setString(1, this.schema.name);
         var2.setString(2, this.name);
         var3 = var2.executeQuery();
      } else {
         var3 = var1.getColumns((String)null, this.schema.name, this.name, (String)null);
      }

      ArrayList var4 = new ArrayList();

      while(var3.next()) {
         DbColumn var5 = DbColumn.getColumn(this.schema.getContents(), var3);
         var4.add(var5);
      }

      var3.close();
      this.columns = (DbColumn[])var4.toArray(new DbColumn[0]);
   }
}
