package org.h2.expression.function.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.ValueVarchar;

public final class LinkSchemaFunction extends TableFunction {
   public LinkSchemaFunction() {
      super(new Expression[6]);
   }

   public ResultInterface getValue(SessionLocal var1) {
      var1.getUser().checkAdmin();
      String var2 = this.getValue(var1, 0);
      String var3 = this.getValue(var1, 1);
      String var4 = this.getValue(var1, 2);
      String var5 = this.getValue(var1, 3);
      String var6 = this.getValue(var1, 4);
      String var7 = this.getValue(var1, 5);
      if (var2 != null && var3 != null && var4 != null && var5 != null && var6 != null && var7 != null) {
         JdbcConnection var8 = var1.createConnection(false);
         Connection var9 = null;
         Statement var10 = null;
         ResultSet var11 = null;
         SimpleResult var12 = new SimpleResult();
         var12.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);

         try {
            var9 = JdbcUtils.getConnection(var3, var4, var5, var6);
            var10 = var8.createStatement();
            var10.execute(StringUtils.quoteIdentifier(new StringBuilder("CREATE SCHEMA IF NOT EXISTS "), var2).toString());
            if (var4.startsWith("jdbc:postgresql:")) {
               var11 = var9.getMetaData().getTables((String)null, var7, (String)null, new String[]{"TABLE", "LINKED TABLE", "VIEW", "EXTERNAL"});
            } else {
               var11 = var9.getMetaData().getTables((String)null, var7, (String)null, (String[])null);
            }

            while(var11.next()) {
               String var13 = var11.getString("TABLE_NAME");
               StringBuilder var14 = new StringBuilder();
               var14.append("DROP TABLE IF EXISTS ");
               StringUtils.quoteIdentifier(var14, var2).append('.');
               StringUtils.quoteIdentifier(var14, var13);
               var10.execute(var14.toString());
               var14.setLength(0);
               var14.append("CREATE LINKED TABLE ");
               StringUtils.quoteIdentifier(var14, var2).append('.');
               StringUtils.quoteIdentifier(var14, var13).append('(');
               StringUtils.quoteStringSQL(var14, var3).append(", ");
               StringUtils.quoteStringSQL(var14, var4).append(", ");
               StringUtils.quoteStringSQL(var14, var5).append(", ");
               StringUtils.quoteStringSQL(var14, var6).append(", ");
               StringUtils.quoteStringSQL(var14, var7).append(", ");
               StringUtils.quoteStringSQL(var14, var13).append(')');
               var10.execute(var14.toString());
               var12.addRow(ValueVarchar.get(var13, var1));
            }
         } catch (SQLException var18) {
            var12.close();
            throw DbException.convert(var18);
         } finally {
            JdbcUtils.closeSilently(var11);
            JdbcUtils.closeSilently(var9);
            JdbcUtils.closeSilently(var10);
         }

         return var12;
      } else {
         return this.getValueTemplate(var1);
      }
   }

   private String getValue(SessionLocal var1, int var2) {
      return this.args[var2].getValue(var1).getString();
   }

   public void optimize(SessionLocal var1) {
      super.optimize(var1);
      int var2 = this.args.length;
      if (var2 != 6) {
         throw DbException.get(7001, (String[])(this.getName(), "6"));
      }
   }

   public ResultInterface getValueTemplate(SessionLocal var1) {
      SimpleResult var2 = new SimpleResult();
      var2.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      return var2;
   }

   public String getName() {
      return "LINK_SCHEMA";
   }

   public boolean isDeterministic() {
      return false;
   }
}
