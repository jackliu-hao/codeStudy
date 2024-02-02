package org.h2.result;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.Session;
import org.h2.engine.SessionRemote;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter;

public class UpdatableRow {
   private final JdbcConnection conn;
   private final ResultInterface result;
   private final int columnCount;
   private String schemaName;
   private String tableName;
   private ArrayList<String> key;
   private boolean isUpdatable;

   public UpdatableRow(JdbcConnection var1, ResultInterface var2) throws SQLException {
      this.conn = var1;
      this.result = var2;
      this.columnCount = var2.getVisibleColumnCount();
      if (this.columnCount != 0) {
         for(int var3 = 0; var3 < this.columnCount; ++var3) {
            String var4 = var2.getTableName(var3);
            String var5 = var2.getSchemaName(var3);
            if (var4 == null || var5 == null) {
               return;
            }

            if (this.tableName == null) {
               this.tableName = var4;
            } else if (!this.tableName.equals(var4)) {
               return;
            }

            if (this.schemaName == null) {
               this.schemaName = var5;
            } else if (!this.schemaName.equals(var5)) {
               return;
            }
         }

         String var11 = "BASE TABLE";
         Session var12 = var1.getSession();
         if (var12 instanceof SessionRemote && ((SessionRemote)var12).getClientVersion() <= 19) {
            var11 = "TABLE";
         }

         DatabaseMetaData var13 = var1.getMetaData();
         ResultSet var6 = var13.getTables((String)null, StringUtils.escapeMetaDataPattern(this.schemaName), StringUtils.escapeMetaDataPattern(this.tableName), new String[]{var11});
         if (var6.next()) {
            String var7 = var6.getString("TABLE_NAME");
            boolean var8 = !var7.equals(this.tableName) && var7.equalsIgnoreCase(this.tableName);
            this.key = Utils.newSmallArrayList();
            var6 = var13.getPrimaryKeys((String)null, StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName);

            while(var6.next()) {
               String var9 = var6.getString("COLUMN_NAME");
               this.key.add(var8 ? StringUtils.toUpperEnglish(var9) : var9);
            }

            if (this.isIndexUsable(this.key)) {
               this.isUpdatable = true;
            } else {
               this.key.clear();
               var6 = var13.getIndexInfo((String)null, StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName, true, true);

               while(var6.next()) {
                  short var14 = var6.getShort("ORDINAL_POSITION");
                  if (var14 == 1) {
                     if (this.isIndexUsable(this.key)) {
                        this.isUpdatable = true;
                        return;
                     }

                     this.key.clear();
                  }

                  String var10 = var6.getString("COLUMN_NAME");
                  this.key.add(var8 ? StringUtils.toUpperEnglish(var10) : var10);
               }

               if (this.isIndexUsable(this.key)) {
                  this.isUpdatable = true;
               } else {
                  this.key = null;
               }
            }
         }
      }
   }

   private boolean isIndexUsable(ArrayList<String> var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         Iterator var2 = var1.iterator();

         String var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (String)var2.next();
         } while(this.findColumnIndex(var3) >= 0);

         return false;
      }
   }

   public boolean isUpdatable() {
      return this.isUpdatable;
   }

   private int findColumnIndex(String var1) {
      for(int var2 = 0; var2 < this.columnCount; ++var2) {
         String var3 = this.result.getColumnName(var2);
         if (var3.equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private int getColumnIndex(String var1) {
      int var2 = this.findColumnIndex(var1);
      if (var2 < 0) {
         throw DbException.get(42122, (String)var1);
      } else {
         return var2;
      }
   }

   private void appendColumnList(StringBuilder var1, boolean var2) {
      for(int var3 = 0; var3 < this.columnCount; ++var3) {
         if (var3 > 0) {
            var1.append(',');
         }

         String var4 = this.result.getColumnName(var3);
         StringUtils.quoteIdentifier(var1, var4);
         if (var2) {
            var1.append("=? ");
         }
      }

   }

   private void appendKeyCondition(StringBuilder var1) {
      var1.append(" WHERE ");

      for(int var2 = 0; var2 < this.key.size(); ++var2) {
         if (var2 > 0) {
            var1.append(" AND ");
         }

         StringUtils.quoteIdentifier(var1, (String)this.key.get(var2)).append("=?");
      }

   }

   private void setKey(PreparedStatement var1, int var2, Value[] var3) throws SQLException {
      int var4 = 0;

      for(int var5 = this.key.size(); var4 < var5; ++var4) {
         String var6 = (String)this.key.get(var4);
         int var7 = this.getColumnIndex(var6);
         Value var8 = var3[var7];
         if (var8 == null || var8 == ValueNull.INSTANCE) {
            throw DbException.get(2000);
         }

         JdbcUtils.set(var1, var2 + var4, var8, this.conn);
      }

   }

   private void appendTableName(StringBuilder var1) {
      if (this.schemaName != null && this.schemaName.length() > 0) {
         StringUtils.quoteIdentifier(var1, this.schemaName).append('.');
      }

      StringUtils.quoteIdentifier(var1, this.tableName);
   }

   public Value[] readRow(Value[] var1) throws SQLException {
      StringBuilder var2 = new StringBuilder("SELECT ");
      this.appendColumnList(var2, false);
      var2.append(" FROM ");
      this.appendTableName(var2);
      this.appendKeyCondition(var2);
      PreparedStatement var3 = this.conn.prepareStatement(var2.toString());
      this.setKey(var3, 1, var1);
      JdbcResultSet var4 = (JdbcResultSet)var3.executeQuery();
      if (!var4.next()) {
         throw DbException.get(2000);
      } else {
         Value[] var5 = new Value[this.columnCount];

         for(int var6 = 0; var6 < this.columnCount; ++var6) {
            var5[var6] = ValueToObjectConverter.readValue(this.conn.getSession(), var4, var6 + 1);
         }

         return var5;
      }
   }

   public void deleteRow(Value[] var1) throws SQLException {
      StringBuilder var2 = new StringBuilder("DELETE FROM ");
      this.appendTableName(var2);
      this.appendKeyCondition(var2);
      PreparedStatement var3 = this.conn.prepareStatement(var2.toString());
      this.setKey(var3, 1, var1);
      int var4 = var3.executeUpdate();
      if (var4 != 1) {
         throw DbException.get(2000);
      }
   }

   public void updateRow(Value[] var1, Value[] var2) throws SQLException {
      StringBuilder var3 = new StringBuilder("UPDATE ");
      this.appendTableName(var3);
      var3.append(" SET ");
      this.appendColumnList(var3, true);
      this.appendKeyCondition(var3);
      PreparedStatement var4 = this.conn.prepareStatement(var3.toString());
      int var5 = 1;

      int var6;
      for(var6 = 0; var6 < this.columnCount; ++var6) {
         Value var7 = var2[var6];
         if (var7 == null) {
            var7 = var1[var6];
         }

         JdbcUtils.set(var4, var5++, var7, this.conn);
      }

      this.setKey(var4, var5, var1);
      var6 = var4.executeUpdate();
      if (var6 != 1) {
         throw DbException.get(2000);
      }
   }

   public void insertRow(Value[] var1) throws SQLException {
      StringBuilder var2 = new StringBuilder("INSERT INTO ");
      this.appendTableName(var2);
      var2.append('(');
      this.appendColumnList(var2, false);
      var2.append(")VALUES(");

      for(int var3 = 0; var3 < this.columnCount; ++var3) {
         if (var3 > 0) {
            var2.append(',');
         }

         Value var4 = var1[var3];
         if (var4 == null) {
            var2.append("DEFAULT");
         } else {
            var2.append('?');
         }
      }

      var2.append(')');
      PreparedStatement var7 = this.conn.prepareStatement(var2.toString());
      int var8 = 0;

      for(int var5 = 0; var8 < this.columnCount; ++var8) {
         Value var6 = var1[var8];
         if (var6 != null) {
            JdbcUtils.set(var7, var5++ + 1, var6, this.conn);
         }
      }

      var8 = var7.executeUpdate();
      if (var8 != 1) {
         throw DbException.get(2000);
      }
   }
}
