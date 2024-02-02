package org.h2.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.h2.engine.Constants;
import org.h2.server.web.ConnectionInfo;
import org.h2.util.JdbcUtils;
import org.h2.util.ScriptReader;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;

public class Shell extends Tool implements Runnable {
   private static final int MAX_ROW_BUFFER = 5000;
   private static final int HISTORY_COUNT = 20;
   private static final char BOX_VERTICAL = '|';
   private PrintStream err;
   private InputStream in;
   private BufferedReader reader;
   private Connection conn;
   private Statement stat;
   private boolean listMode;
   private int maxColumnSize;
   private final ArrayList<String> history;
   private boolean stopHide;
   private String serverPropertiesDir;

   public Shell() {
      this.err = System.err;
      this.in = System.in;
      this.maxColumnSize = 100;
      this.history = new ArrayList();
      this.serverPropertiesDir = "~";
   }

   public static void main(String... var0) throws SQLException {
      (new Shell()).runTool(var0);
   }

   public void setErr(PrintStream var1) {
      this.err = var1;
   }

   public void setIn(InputStream var1) {
      this.in = var1;
   }

   public void setInReader(BufferedReader var1) {
      this.reader = var1;
   }

   public void runTool(String... var1) throws SQLException {
      String var2 = null;
      String var3 = null;
      String var4 = "";
      String var5 = "";
      String var6 = null;

      String var8;
      for(int var7 = 0; var1 != null && var7 < var1.length; ++var7) {
         var8 = var1[var7];
         if (var8.equals("-url")) {
            ++var7;
            var3 = var1[var7];
         } else if (var8.equals("-user")) {
            ++var7;
            var4 = var1[var7];
         } else if (var8.equals("-password")) {
            ++var7;
            var5 = var1[var7];
         } else if (var8.equals("-driver")) {
            ++var7;
            var2 = var1[var7];
         } else if (var8.equals("-sql")) {
            ++var7;
            var6 = var1[var7];
         } else if (var8.equals("-properties")) {
            ++var7;
            this.serverPropertiesDir = var1[var7];
         } else {
            if (var8.equals("-help") || var8.equals("-?")) {
               this.showUsage();
               return;
            }

            if (var8.equals("-list")) {
               this.listMode = true;
            } else {
               this.showUsageAndThrowUnsupportedOption(var8);
            }
         }
      }

      if (var3 != null) {
         this.conn = JdbcUtils.getConnection(var2, var3, var4, var5);
         this.stat = this.conn.createStatement();
      }

      if (var6 == null) {
         this.promptLoop();
      } else {
         ScriptReader var9 = new ScriptReader(new StringReader(var6));

         while(true) {
            var8 = var9.readStatement();
            if (var8 == null) {
               if (this.conn != null) {
                  this.conn.close();
               }
               break;
            }

            this.execute(var8);
         }
      }

   }

   public void runTool(Connection var1, String... var2) throws SQLException {
      this.conn = var1;
      this.stat = var1.createStatement();
      this.runTool(var2);
   }

   private void showHelp() {
      this.println("Commands are case insensitive; SQL statements end with ';'");
      this.println("help or ?      Display this help");
      this.println("list           Toggle result list / stack trace mode");
      this.println("maxwidth       Set maximum column width (default is 100)");
      this.println("autocommit     Enable or disable autocommit");
      this.println("history        Show the last 20 statements");
      this.println("quit or exit   Close the connection and exit");
      this.println("");
   }

   private void promptLoop() {
      this.println("");
      this.println("Welcome to H2 Shell " + Constants.FULL_VERSION);
      this.println("Exit with Ctrl+C");
      if (this.conn != null) {
         this.showHelp();
      }

      String var1 = null;
      if (this.reader == null) {
         this.reader = new BufferedReader(new InputStreamReader(this.in));
      }

      label157:
      while(true) {
         while(true) {
            try {
               if (this.conn == null) {
                  this.connect();
                  this.showHelp();
               }

               if (var1 == null) {
                  this.print("sql> ");
               } else {
                  this.print("...> ");
               }

               String var2 = this.readLine();
               if (var2 == null) {
                  break label157;
               }

               String var3 = var2.trim();
               if (!var3.isEmpty()) {
                  boolean var4 = var3.endsWith(";");
                  if (var4) {
                     var2 = var2.substring(0, var2.lastIndexOf(59));
                     var3 = var3.substring(0, var3.length() - 1);
                  }

                  String var5 = StringUtils.toLowerEnglish(var3);
                  if ("exit".equals(var5) || "quit".equals(var5)) {
                     break label157;
                  }

                  if (!"help".equals(var5) && !"?".equals(var5)) {
                     if ("list".equals(var5)) {
                        this.listMode = !this.listMode;
                        this.println("Result list mode is now " + (this.listMode ? "on" : "off"));
                     } else {
                        int var7;
                        if ("history".equals(var5)) {
                           int var6 = 0;

                           for(var7 = this.history.size(); var6 < var7; ++var6) {
                              String var8 = (String)this.history.get(var6);
                              var8 = var8.replace('\n', ' ').replace('\r', ' ');
                              this.println("#" + (1 + var6) + ": " + var8);
                           }

                           if (!this.history.isEmpty()) {
                              this.println("To re-run a statement, type the number and press and enter");
                           } else {
                              this.println("No history");
                           }
                        } else if (var5.startsWith("autocommit")) {
                           var5 = StringUtils.trimSubstring(var5, "autocommit".length());
                           if ("true".equals(var5)) {
                              this.conn.setAutoCommit(true);
                           } else if ("false".equals(var5)) {
                              this.conn.setAutoCommit(false);
                           } else {
                              this.println("Usage: autocommit [true|false]");
                           }

                           this.println("Autocommit is now " + this.conn.getAutoCommit());
                        } else if (var5.startsWith("maxwidth")) {
                           var5 = StringUtils.trimSubstring(var5, "maxwidth".length());

                           try {
                              this.maxColumnSize = Integer.parseInt(var5);
                           } catch (NumberFormatException var10) {
                              this.println("Usage: maxwidth <integer value>");
                           }

                           this.println("Maximum column width is now " + this.maxColumnSize);
                        } else {
                           boolean var14 = true;
                           if (var1 == null) {
                              if (StringUtils.isNumber(var2)) {
                                 var7 = Integer.parseInt(var2);
                                 if (var7 != 0 && var7 <= this.history.size()) {
                                    var1 = (String)this.history.get(var7 - 1);
                                    var14 = false;
                                    this.println(var1);
                                    var4 = true;
                                 } else {
                                    this.println("Not found");
                                 }
                              } else {
                                 var1 = var2;
                              }
                           } else {
                              var1 = var1 + "\n" + var2;
                           }

                           if (var4) {
                              if (var14) {
                                 this.history.add(0, var1);
                                 if (this.history.size() > 20) {
                                    this.history.remove(20);
                                 }
                              }

                              this.execute(var1);
                              var1 = null;
                           }
                        }
                     }
                  } else {
                     this.showHelp();
                  }
               }
            } catch (SQLException var11) {
               this.println("SQL Exception: " + var11.getMessage());
               var1 = null;
            } catch (IOException var12) {
               this.println(var12.getMessage());
               break label157;
            } catch (Exception var13) {
               this.println("Exception: " + var13.toString());
               var13.printStackTrace(this.err);
               break label157;
            }
         }
      }

      if (this.conn != null) {
         try {
            this.conn.close();
            this.println("Connection closed");
         } catch (SQLException var9) {
            this.println("SQL Exception: " + var9.getMessage());
            var9.printStackTrace(this.err);
         }
      }

   }

   private void connect() throws IOException, SQLException {
      String var1 = "jdbc:h2:~/test";
      String var2 = "";
      String var3 = null;

      try {
         Object var4;
         if ("null".equals(this.serverPropertiesDir)) {
            var4 = new Properties();
         } else {
            var4 = SortedProperties.loadProperties(this.serverPropertiesDir + "/" + ".h2.server.properties");
         }

         String var5 = null;
         boolean var6 = false;
         int var7 = 0;

         while(true) {
            String var8 = ((Properties)var4).getProperty(Integer.toString(var7));
            if (var8 == null) {
               if (var6) {
                  ConnectionInfo var10 = new ConnectionInfo(var5);
                  var1 = var10.url;
                  var2 = var10.user;
                  var3 = var10.driver;
               }
               break;
            }

            var6 = true;
            var5 = var8;
            ++var7;
         }
      } catch (IOException var9) {
      }

      this.println("[Enter]   " + var1);
      this.print("URL       ");
      var1 = this.readLine(var1).trim();
      if (var3 == null) {
         var3 = JdbcUtils.getDriver(var1);
      }

      if (var3 != null) {
         this.println("[Enter]   " + var3);
      }

      this.print("Driver    ");
      var3 = this.readLine(var3).trim();
      this.println("[Enter]   " + var2);
      this.print("User      ");
      var2 = this.readLine(var2);
      this.conn = var1.startsWith("jdbc:h2:") ? this.connectH2(var3, var1, var2) : JdbcUtils.getConnection(var3, var1, var2, this.readPassword());
      this.stat = this.conn.createStatement();
      this.println("Connected");
   }

   private Connection connectH2(String var1, String var2, String var3) throws IOException, SQLException {
      while(true) {
         String var4 = this.readPassword();

         try {
            return JdbcUtils.getConnection(var1, var2 + ";IFEXISTS=TRUE", var3, var4);
         } catch (SQLException var7) {
            if (var7.getErrorCode() != 90146) {
               throw var7;
            }

            this.println("Type the same password again to confirm database creation.");
            String var6 = this.readPassword();
            if (var4.equals(var6)) {
               return JdbcUtils.getConnection(var1, var2, var3, var4);
            }

            this.println("Passwords don't match. Try again.");
         }
      }
   }

   protected void print(String var1) {
      this.out.print(var1);
      this.out.flush();
   }

   private void println(String var1) {
      this.out.println(var1);
      this.out.flush();
   }

   private String readPassword() throws IOException {
      try {
         Object var6 = Utils.callStaticMethod("java.lang.System.console");
         this.print("Password  ");
         char[] var7 = (char[])((char[])Utils.callMethod(var6, "readPassword"));
         return var7 == null ? null : new String(var7);
      } catch (Exception var5) {
         Thread var1 = new Thread(this, "Password hider");
         this.stopHide = false;
         var1.start();
         this.print("Password  > ");
         String var2 = this.readLine();
         this.stopHide = true;

         try {
            var1.join();
         } catch (InterruptedException var4) {
         }

         this.print("\b\b");
         return var2;
      }
   }

   public void run() {
      while(!this.stopHide) {
         this.print("\b\b><");

         try {
            Thread.sleep(10L);
         } catch (InterruptedException var2) {
         }
      }

   }

   private String readLine(String var1) throws IOException {
      String var2 = this.readLine();
      return var2.isEmpty() ? var1 : var2;
   }

   private String readLine() throws IOException {
      String var1 = this.reader.readLine();
      if (var1 == null) {
         throw new IOException("Aborted");
      } else {
         return var1;
      }
   }

   private void execute(String var1) {
      if (!StringUtils.isWhitespaceOrEmpty(var1)) {
         long var2 = System.nanoTime();

         try {
            ResultSet var4 = null;

            try {
               if (var1.startsWith("@")) {
                  var4 = JdbcUtils.getMetaResultSet(this.conn, var1);
                  this.printResult(var4, this.listMode);
               } else if (this.stat.execute(var1)) {
                  var4 = this.stat.getResultSet();
                  int var5 = this.printResult(var4, this.listMode);
                  var2 = System.nanoTime() - var2;
                  this.println("(" + var5 + (var5 == 1 ? " row, " : " rows, ") + TimeUnit.NANOSECONDS.toMillis(var2) + " ms)");
               } else {
                  long var15;
                  try {
                     var15 = this.stat.getLargeUpdateCount();
                  } catch (UnsupportedOperationException var12) {
                     var15 = (long)this.stat.getUpdateCount();
                  }

                  var2 = System.nanoTime() - var2;
                  this.println("(Update count: " + var15 + ", " + TimeUnit.NANOSECONDS.toMillis(var2) + " ms)");
               }
            } finally {
               JdbcUtils.closeSilently(var4);
            }
         } catch (SQLException var14) {
            this.println("Error: " + var14.toString());
            if (this.listMode) {
               var14.printStackTrace(this.err);
            }
         }

      }
   }

   private int printResult(ResultSet var1, boolean var2) throws SQLException {
      return var2 ? this.printResultAsList(var1) : this.printResultAsTable(var1);
   }

   private int printResultAsTable(ResultSet var1) throws SQLException {
      ResultSetMetaData var2 = var1.getMetaData();
      int var3 = var2.getColumnCount();
      boolean var4 = false;
      ArrayList var5 = new ArrayList();
      String[] var6 = new String[var3];

      int var7;
      for(var7 = 0; var7 < var3; ++var7) {
         String var8 = var2.getColumnLabel(var7 + 1);
         var6[var7] = var8 == null ? "" : var8;
      }

      var5.add(var6);
      var7 = 0;

      while(var1.next()) {
         ++var7;
         var4 |= this.loadRow(var1, var3, var5);
         if (var7 > 5000) {
            this.printRows(var5, var3);
            var5.clear();
         }
      }

      this.printRows(var5, var3);
      var5.clear();
      if (var4) {
         this.println("(data is partially truncated)");
      }

      return var7;
   }

   private boolean loadRow(ResultSet var1, int var2, ArrayList<String[]> var3) throws SQLException {
      boolean var4 = false;
      String[] var5 = new String[var2];

      for(int var6 = 0; var6 < var2; ++var6) {
         String var7 = var1.getString(var6 + 1);
         if (var7 == null) {
            var7 = "null";
         }

         if (var2 > 1 && var7.length() > this.maxColumnSize) {
            var7 = var7.substring(0, this.maxColumnSize);
            var4 = true;
         }

         var5[var6] = var7;
      }

      var3.add(var5);
      return var4;
   }

   private int[] printRows(ArrayList<String[]> var1, int var2) {
      int[] var3 = new int[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         int var5 = 0;

         String[] var7;
         for(Iterator var6 = var1.iterator(); var6.hasNext(); var5 = Math.max(var5, var7[var4].length())) {
            var7 = (String[])var6.next();
         }

         if (var2 > 1) {
            var5 = Math.min(this.maxColumnSize, var5);
         }

         var3[var4] = var5;
      }

      Iterator var10 = var1.iterator();

      while(var10.hasNext()) {
         String[] var11 = (String[])var10.next();
         StringBuilder var12 = new StringBuilder();

         for(int var13 = 0; var13 < var2; ++var13) {
            if (var13 > 0) {
               var12.append(' ').append('|').append(' ');
            }

            String var8 = var11[var13];
            var12.append(var8);
            if (var13 < var2 - 1) {
               for(int var9 = var8.length(); var9 < var3[var13]; ++var9) {
                  var12.append(' ');
               }
            }
         }

         this.println(var12.toString());
      }

      return var3;
   }

   private int printResultAsList(ResultSet var1) throws SQLException {
      ResultSetMetaData var2 = var1.getMetaData();
      int var3 = 0;
      int var4 = var2.getColumnCount();
      String[] var5 = new String[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         String var7 = var2.getColumnLabel(var6 + 1);
         var5[var6] = var7;
         var3 = Math.max(var3, var7.length());
      }

      StringBuilder var11 = new StringBuilder();
      int var12 = 0;

      int var8;
      String var9;
      while(var1.next()) {
         ++var12;
         var11.setLength(0);
         if (var12 > 1) {
            this.println("");
         }

         for(var8 = 0; var8 < var4; ++var8) {
            if (var8 > 0) {
               var11.append('\n');
            }

            var9 = var5[var8];
            var11.append(var9);

            for(int var10 = var9.length(); var10 < var3; ++var10) {
               var11.append(' ');
            }

            var11.append(": ").append(var1.getString(var8 + 1));
         }

         this.println(var11.toString());
      }

      if (var12 == 0) {
         for(var8 = 0; var8 < var4; ++var8) {
            if (var8 > 0) {
               var11.append('\n');
            }

            var9 = var5[var8];
            var11.append(var9);
         }

         this.println(var11.toString());
      }

      return var12;
   }
}
