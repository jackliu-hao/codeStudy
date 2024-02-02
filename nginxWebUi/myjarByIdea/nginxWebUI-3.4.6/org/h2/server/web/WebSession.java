package org.h2.server.web;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.h2.bnf.Bnf;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbContextRule;
import org.h2.message.DbException;

class WebSession {
   private static final int MAX_HISTORY = 1000;
   long lastAccess;
   final HashMap<String, Object> map = new HashMap();
   Locale locale;
   Statement executingStatement;
   ResultSet result;
   private final WebServer server;
   private final ArrayList<String> commandHistory;
   private Connection conn;
   private DatabaseMetaData meta;
   private DbContents contents = new DbContents();
   private Bnf bnf;
   private boolean shutdownServerOnDisconnect;

   WebSession(WebServer var1) {
      this.server = var1;
      this.commandHistory = var1.getCommandHistoryList();
   }

   void put(String var1, Object var2) {
      this.map.put(var1, var2);
   }

   Object get(String var1) {
      return "sessions".equals(var1) ? this.server.getSessions() : this.map.get(var1);
   }

   Object remove(String var1) {
      return this.map.remove(var1);
   }

   Bnf getBnf() {
      return this.bnf;
   }

   void loadBnf() {
      try {
         Bnf var1 = Bnf.getInstance((Reader)null);
         DbContextRule var2 = new DbContextRule(this.contents, 0);
         DbContextRule var3 = new DbContextRule(this.contents, 3);
         DbContextRule var4 = new DbContextRule(this.contents, 2);
         DbContextRule var5 = new DbContextRule(this.contents, 1);
         DbContextRule var6 = new DbContextRule(this.contents, 5);
         DbContextRule var7 = new DbContextRule(this.contents, 4);
         DbContextRule var8 = new DbContextRule(this.contents, 6);
         var1.updateTopic("procedure", var8);
         var1.updateTopic("column_name", var2);
         var1.updateTopic("new_table_alias", var3);
         var1.updateTopic("table_alias", var4);
         var1.updateTopic("column_alias", var7);
         var1.updateTopic("table_name", var5);
         var1.updateTopic("schema_name", var6);
         var1.linkStatements();
         this.bnf = var1;
      } catch (Exception var9) {
         this.server.traceError(var9);
      }

   }

   String getCommand(int var1) {
      return (String)this.commandHistory.get(var1);
   }

   void addCommand(String var1) {
      if (var1 != null) {
         var1 = var1.trim();
         if (!var1.isEmpty()) {
            if (this.commandHistory.size() > 1000) {
               this.commandHistory.remove(0);
            }

            int var2 = this.commandHistory.indexOf(var1);
            if (var2 >= 0) {
               this.commandHistory.remove(var2);
            }

            this.commandHistory.add(var1);
            if (this.server.isCommandHistoryAllowed()) {
               this.server.saveCommandHistoryList(this.commandHistory);
            }

         }
      }
   }

   ArrayList<String> getCommandHistory() {
      return this.commandHistory;
   }

   HashMap<String, Object> getInfo() {
      HashMap var1 = new HashMap();
      var1.putAll(this.map);
      var1.put("lastAccess", (new Timestamp(this.lastAccess)).toString());

      try {
         var1.put("url", this.conn == null ? "${text.admin.notConnected}" : this.conn.getMetaData().getURL());
         var1.put("user", this.conn == null ? "-" : this.conn.getMetaData().getUserName());
         var1.put("lastQuery", this.commandHistory.isEmpty() ? "" : this.commandHistory.get(0));
         var1.put("executing", this.executingStatement == null ? "${text.admin.no}" : "${text.admin.yes}");
      } catch (SQLException var3) {
         DbException.traceThrowable(var3);
      }

      return var1;
   }

   void setConnection(Connection var1) throws SQLException {
      this.conn = var1;
      if (var1 == null) {
         this.meta = null;
      } else {
         this.meta = var1.getMetaData();
      }

      this.contents = new DbContents();
   }

   DatabaseMetaData getMetaData() {
      return this.meta;
   }

   Connection getConnection() {
      return this.conn;
   }

   DbContents getContents() {
      return this.contents;
   }

   void setShutdownServerOnDisconnect() {
      this.shutdownServerOnDisconnect = true;
   }

   boolean getShutdownServerOnDisconnect() {
      return this.shutdownServerOnDisconnect;
   }

   void close() {
      if (this.executingStatement != null) {
         try {
            this.executingStatement.cancel();
         } catch (Exception var3) {
         }
      }

      if (this.conn != null) {
         try {
            this.conn.close();
         } catch (Exception var2) {
         }
      }

   }
}
