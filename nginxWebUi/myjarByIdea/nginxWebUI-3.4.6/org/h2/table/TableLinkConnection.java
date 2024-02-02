package org.h2.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;

public class TableLinkConnection {
   private final HashMap<TableLinkConnection, TableLinkConnection> map;
   private final String driver;
   private final String url;
   private final String user;
   private final String password;
   private Connection conn;
   private int useCounter;
   private boolean autocommit = true;

   private TableLinkConnection(HashMap<TableLinkConnection, TableLinkConnection> var1, String var2, String var3, String var4, String var5) {
      this.map = var1;
      this.driver = var2;
      this.url = var3;
      this.user = var4;
      this.password = var5;
   }

   public static TableLinkConnection open(HashMap<TableLinkConnection, TableLinkConnection> var0, String var1, String var2, String var3, String var4, boolean var5) {
      TableLinkConnection var6 = new TableLinkConnection(var0, var1, var2, var3, var4);
      if (!var5) {
         var6.open();
         return var6;
      } else {
         synchronized(var0) {
            TableLinkConnection var8 = (TableLinkConnection)var0.get(var6);
            if (var8 == null) {
               var6.open();
               var0.put(var6, var6);
               var8 = var6;
            }

            ++var8.useCounter;
            return var8;
         }
      }
   }

   private void open() {
      try {
         this.conn = JdbcUtils.getConnection(this.driver, this.url, this.user, this.password);
      } catch (SQLException var2) {
         throw DbException.convert(var2);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.driver) ^ Objects.hashCode(this.url) ^ Objects.hashCode(this.user) ^ Objects.hashCode(this.password);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TableLinkConnection)) {
         return false;
      } else {
         TableLinkConnection var2 = (TableLinkConnection)var1;
         return Objects.equals(this.driver, var2.driver) && Objects.equals(this.url, var2.url) && Objects.equals(this.user, var2.user) && Objects.equals(this.password, var2.password);
      }
   }

   Connection getConnection() {
      return this.conn;
   }

   void close(boolean var1) {
      boolean var2 = false;
      synchronized(this.map) {
         if (--this.useCounter <= 0 || var1) {
            var2 = true;
            this.map.remove(this);
         }
      }

      if (var2) {
         JdbcUtils.closeSilently(this.conn);
      }

   }

   public void setAutoCommit(boolean var1) {
      this.autocommit = var1;
   }

   public boolean getAutocommit() {
      return this.autocommit;
   }
}
