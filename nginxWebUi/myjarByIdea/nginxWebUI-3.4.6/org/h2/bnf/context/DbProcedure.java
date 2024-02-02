package org.h2.bnf.context;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.util.Utils;

public class DbProcedure {
   private final DbSchema schema;
   private final String name;
   private final String quotedName;
   private final boolean returnsResult;
   private DbColumn[] parameters;

   public DbProcedure(DbSchema var1, ResultSet var2) throws SQLException {
      this.schema = var1;
      this.name = var2.getString("PROCEDURE_NAME");
      this.returnsResult = var2.getShort("PROCEDURE_TYPE") == 2;
      this.quotedName = var1.getContents().quoteIdentifier(this.name);
   }

   public DbSchema getSchema() {
      return this.schema;
   }

   public DbColumn[] getParameters() {
      return this.parameters;
   }

   public String getName() {
      return this.name;
   }

   public String getQuotedName() {
      return this.quotedName;
   }

   public boolean isReturnsResult() {
      return this.returnsResult;
   }

   void readParameters(DatabaseMetaData var1) throws SQLException {
      ResultSet var2 = var1.getProcedureColumns((String)null, this.schema.name, this.name, (String)null);
      ArrayList var3 = Utils.newSmallArrayList();

      while(var2.next()) {
         DbColumn var4 = DbColumn.getProcedureColumn(this.schema.getContents(), var2);
         if (var4.getPosition() > 0) {
            var3.add(var4);
         }
      }

      var2.close();
      this.parameters = new DbColumn[var3.size()];

      for(int var6 = 0; var6 < this.parameters.length; ++var6) {
         DbColumn var5 = (DbColumn)var3.get(var6);
         if (var5.getPosition() > 0 && var5.getPosition() <= this.parameters.length) {
            this.parameters[var5.getPosition() - 1] = var5;
         }
      }

   }
}
