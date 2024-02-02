package org.h2.server.web;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.h2.bnf.Bnf;
import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.command.Parser;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcException;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.tools.Backup;
import org.h2.tools.ChangeFileEncryption;
import org.h2.tools.ConvertTraceFile;
import org.h2.tools.CreateCluster;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Recover;
import org.h2.tools.Restore;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.h2.tools.SimpleResultSet;
import org.h2.util.JdbcUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.Profiler;
import org.h2.util.ScriptReader;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.value.DataType;

public class WebApp {
   private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR;
   protected final WebServer server;
   protected WebSession session;
   protected Properties attributes;
   protected String mimeType;
   protected boolean cache;
   protected boolean stop;
   protected String headerLanguage;
   private Profiler profiler;

   WebApp(WebServer var1) {
      this.server = var1;
   }

   void setSession(WebSession var1, Properties var2) {
      this.session = var1;
      this.attributes = var2;
   }

   String processRequest(String var1, NetworkConnectionInfo var2) {
      int var3 = var1.lastIndexOf(46);
      String var4;
      if (var3 >= 0) {
         var4 = var1.substring(var3 + 1);
      } else {
         var4 = "";
      }

      if ("ico".equals(var4)) {
         this.mimeType = "image/x-icon";
         this.cache = true;
      } else if ("gif".equals(var4)) {
         this.mimeType = "image/gif";
         this.cache = true;
      } else if ("css".equals(var4)) {
         this.cache = true;
         this.mimeType = "text/css";
      } else if (!"html".equals(var4) && !"do".equals(var4) && !"jsp".equals(var4)) {
         if ("js".equals(var4)) {
            this.cache = true;
            this.mimeType = "text/javascript";
         } else {
            this.cache = true;
            this.mimeType = "application/octet-stream";
         }
      } else {
         this.cache = false;
         this.mimeType = "text/html";
         if (this.session == null) {
            this.session = this.server.createNewSession(NetUtils.ipToShortForm((StringBuilder)null, var2.getClientAddr(), false).toString());
            if (!"notAllowed.jsp".equals(var1)) {
               var1 = "index.do";
            }
         }
      }

      this.trace("mimeType=" + this.mimeType);
      this.trace(var1);
      if (var1.endsWith(".do")) {
         var1 = this.process(var1, var2);
      } else if (var1.endsWith(".jsp")) {
         switch (var1) {
            case "admin.jsp":
            case "tools.jsp":
               if (!this.checkAdmin(var1)) {
                  var1 = this.process("adminLogin.do", var2);
               }
         }
      }

      return var1;
   }

   private static String getComboBox(String[] var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      String[] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var2.append("<option value=\"").append(PageParser.escapeHtmlData(var6)).append('"');
         if (var6.equals(var1)) {
            var2.append(" selected");
         }

         var2.append('>').append(PageParser.escapeHtml(var6)).append("</option>");
      }

      return var2.toString();
   }

   private static String getComboBox(String[][] var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      String[][] var3 = var0;
      int var4 = var0.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String[] var6 = var3[var5];
         var2.append("<option value=\"").append(PageParser.escapeHtmlData(var6[0])).append('"');
         if (var6[0].equals(var1)) {
            var2.append(" selected");
         }

         var2.append('>').append(PageParser.escapeHtml(var6[1])).append("</option>");
      }

      return var2.toString();
   }

   private String process(String var1, NetworkConnectionInfo var2) {
      this.trace("process " + var1);

      while(var1.endsWith(".do")) {
         switch (var1) {
            case "login.do":
               var1 = this.login(var2);
               break;
            case "index.do":
               var1 = this.index();
               break;
            case "logout.do":
               var1 = this.logout();
               break;
            case "settingRemove.do":
               var1 = this.settingRemove();
               break;
            case "settingSave.do":
               var1 = this.settingSave();
               break;
            case "test.do":
               var1 = this.test(var2);
               break;
            case "query.do":
               var1 = this.query();
               break;
            case "tables.do":
               var1 = this.tables();
               break;
            case "editResult.do":
               var1 = this.editResult();
               break;
            case "getHistory.do":
               var1 = this.getHistory();
               break;
            case "admin.do":
               var1 = this.checkAdmin(var1) ? this.admin() : "adminLogin.do";
               break;
            case "adminSave.do":
               var1 = this.checkAdmin(var1) ? this.adminSave() : "adminLogin.do";
               break;
            case "adminStartTranslate.do":
               var1 = this.checkAdmin(var1) ? this.adminStartTranslate() : "adminLogin.do";
               break;
            case "adminShutdown.do":
               var1 = this.checkAdmin(var1) ? this.adminShutdown() : "adminLogin.do";
               break;
            case "autoCompleteList.do":
               var1 = this.autoCompleteList();
               break;
            case "tools.do":
               var1 = this.checkAdmin(var1) ? this.tools() : "adminLogin.do";
               break;
            case "adminLogin.do":
               var1 = this.adminLogin();
               break;
            default:
               var1 = "error.jsp";
         }
      }

      this.trace("return " + var1);
      return var1;
   }

   private boolean checkAdmin(String var1) {
      Boolean var2 = (Boolean)this.session.get("admin");
      if (var2 != null && var2) {
         return true;
      } else {
         String var3 = this.server.getKey();
         if (var3 != null && var3.equals(this.session.get("key"))) {
            return true;
         } else {
            this.session.put("adminBack", var1);
            return false;
         }
      }
   }

   private String adminLogin() {
      String var1 = this.attributes.getProperty("password");
      if (var1 != null && !var1.isEmpty() && this.server.checkAdminPassword(var1)) {
         String var2 = (String)this.session.remove("adminBack");
         this.session.put("admin", true);
         return var2 != null ? var2 : "admin.do";
      } else {
         return "adminLogin.jsp";
      }
   }

   private String autoCompleteList() {
      String var1 = (String)this.attributes.get("query");
      boolean var2 = false;
      String var3 = var1.trim();
      if (!var3.isEmpty() && Character.isLowerCase(var3.charAt(0))) {
         var2 = true;
      }

      try {
         String var4 = var1;
         if (var1.endsWith(";")) {
            var4 = var1 + " ";
         }

         ScriptReader var5 = new ScriptReader(new StringReader(var4));
         var5.setSkipRemarks(true);
         String var6 = "";

         while(true) {
            String var7 = var5.readStatement();
            if (var7 == null) {
               var7 = "";
               if (var5.isInsideRemark()) {
                  if (var5.isBlockRemark()) {
                     var7 = "1#(End Remark)# */\n" + var7;
                  } else {
                     var7 = "1#(Newline)#\n" + var7;
                  }
               } else {
                  for(var4 = var6; var4.length() > 0 && var4.charAt(0) <= ' '; var4 = var4.substring(1)) {
                  }

                  String var8 = var4.trim();
                  if (!var8.isEmpty() && Character.isLowerCase(var8.charAt(0))) {
                     var2 = true;
                  }

                  Bnf var9 = this.session.getBnf();
                  if (var9 == null) {
                     return "autoCompleteList.jsp";
                  }

                  HashMap var10 = var9.getNextTokenList(var4);
                  String var11 = "";
                  if (var4.length() > 0) {
                     char var12 = var4.charAt(var4.length() - 1);
                     if (!Character.isWhitespace(var12) && var12 != '.' && var12 >= ' ' && var12 != '\'' && var12 != '"') {
                        var11 = " ";
                     }
                  }

                  ArrayList var19 = new ArrayList(var10.size());
                  Iterator var13 = var10.entrySet().iterator();

                  while(var13.hasNext()) {
                     Map.Entry var14 = (Map.Entry)var13.next();
                     String var15 = (String)var14.getKey();
                     String var16 = (String)var14.getValue();
                     String var17 = String.valueOf(var15.charAt(0));
                     if (Integer.parseInt(var17) <= 2) {
                        var15 = var15.substring(2);
                        if (Character.isLetter(var15.charAt(0)) && var2) {
                           var15 = StringUtils.toLowerEnglish(var15);
                           var16 = StringUtils.toLowerEnglish(var16);
                        }

                        if (var15.equals(var16) && !".".equals(var16)) {
                           var16 = var11 + var16;
                        }

                        var15 = StringUtils.urlEncode(var15);
                        var15 = var15.replace('+', ' ');
                        var16 = StringUtils.urlEncode(var16);
                        var16 = var16.replace('+', ' ');
                        var19.add(var17 + "#" + var15 + "#" + var16);
                     }
                  }

                  Collections.sort(var19);
                  if (var1.endsWith("\n") || var3.endsWith(";")) {
                     var19.add(0, "1#(Newline)#\n");
                  }

                  var7 = String.join("|", var19);
               }

               this.session.put("autoCompleteList", var7);
               break;
            }

            var6 = var7;
         }
      } catch (Throwable var18) {
         this.server.traceError(var18);
      }

      return "autoCompleteList.jsp";
   }

   private String admin() {
      this.session.put("port", Integer.toString(this.server.getPort()));
      this.session.put("allowOthers", Boolean.toString(this.server.getAllowOthers()));
      this.session.put("webExternalNames", this.server.getExternalNames());
      this.session.put("ssl", String.valueOf(this.server.getSSL()));
      this.session.put("sessions", this.server.getSessions());
      return "admin.jsp";
   }

   private String adminSave() {
      try {
         SortedProperties var1 = new SortedProperties();
         int var2 = Integer.decode((String)this.attributes.get("port"));
         var1.setProperty("webPort", Integer.toString(var2));
         this.server.setPort(var2);
         boolean var3 = Utils.parseBoolean((String)this.attributes.get("allowOthers"), false, false);
         var1.setProperty("webAllowOthers", String.valueOf(var3));
         this.server.setAllowOthers(var3);
         String var4 = (String)this.attributes.get("webExternalNames");
         var1.setProperty("webExternalNames", var4);
         this.server.setExternalNames(var4);
         boolean var5 = Utils.parseBoolean((String)this.attributes.get("ssl"), false, false);
         var1.setProperty("webSSL", String.valueOf(var5));
         this.server.setSSL(var5);
         byte[] var6 = this.server.getAdminPassword();
         if (var6 != null) {
            var1.setProperty("webAdminPassword", StringUtils.convertBytesToHex(var6));
         }

         this.server.saveProperties(var1);
      } catch (Exception var7) {
         this.trace(var7.toString());
      }

      return this.admin();
   }

   private String tools() {
      try {
         String var1 = (String)this.attributes.get("tool");
         this.session.put("tool", var1);
         String var2 = (String)this.attributes.get("args");
         String[] var3 = StringUtils.arraySplit(var2, ',', false);
         Object var4 = null;
         if ("Backup".equals(var1)) {
            var4 = new Backup();
         } else if ("Restore".equals(var1)) {
            var4 = new Restore();
         } else if ("Recover".equals(var1)) {
            var4 = new Recover();
         } else if ("DeleteDbFiles".equals(var1)) {
            var4 = new DeleteDbFiles();
         } else if ("ChangeFileEncryption".equals(var1)) {
            var4 = new ChangeFileEncryption();
         } else if ("Script".equals(var1)) {
            var4 = new Script();
         } else if ("RunScript".equals(var1)) {
            var4 = new RunScript();
         } else if ("ConvertTraceFile".equals(var1)) {
            var4 = new ConvertTraceFile();
         } else {
            if (!"CreateCluster".equals(var1)) {
               throw DbException.getInternalError(var1);
            }

            var4 = new CreateCluster();
         }

         ByteArrayOutputStream var5 = new ByteArrayOutputStream();
         PrintStream var6 = new PrintStream(var5, false, "UTF-8");
         ((Tool)var4).setOut(var6);

         try {
            ((Tool)var4).runTool(var3);
            var6.flush();
            String var7 = Utils10.byteArrayOutputStreamToString(var5, StandardCharsets.UTF_8);
            String var8 = PageParser.escapeHtml(var7);
            this.session.put("toolResult", var8);
         } catch (Exception var9) {
            this.session.put("toolResult", this.getStackTrace(0, var9, true));
         }
      } catch (Exception var10) {
         this.server.traceError(var10);
      }

      return "tools.jsp";
   }

   private String adminStartTranslate() {
      Map var1 = (Map)Map.class.cast(this.session.map.get("text"));
      String var3 = this.server.startTranslate(var1);
      this.session.put("translationFile", var3);
      return "helpTranslate.jsp";
   }

   protected String adminShutdown() {
      this.server.shutdown();
      return "admin.jsp";
   }

   private String index() {
      String[][] var1 = WebServer.LANGUAGES;
      String var2 = (String)this.attributes.get("language");
      Locale var3 = this.session.locale;
      if (var2 != null) {
         if (var3 == null || !StringUtils.toLowerEnglish(var3.getLanguage()).equals(var2)) {
            var3 = new Locale(var2, "");
            this.server.readTranslations(this.session, var3.getLanguage());
            this.session.put("language", var2);
            this.session.locale = var3;
         }
      } else {
         var2 = (String)this.session.get("language");
      }

      if (var2 == null) {
         var2 = this.headerLanguage;
      }

      this.session.put("languageCombo", getComboBox(var1, var2));
      String[] var4 = this.server.getSettingNames();
      String var5 = this.attributes.getProperty("setting");
      if (var5 == null && var4.length > 0) {
         var5 = var4[0];
      }

      String var6 = getComboBox(var4, var5);
      this.session.put("settingsList", var6);
      ConnectionInfo var7 = this.server.getSetting(var5);
      if (var7 == null) {
         var7 = new ConnectionInfo();
      }

      this.session.put("setting", PageParser.escapeHtmlData(var5));
      this.session.put("name", PageParser.escapeHtmlData(var5));
      this.session.put("driver", PageParser.escapeHtmlData(var7.driver));
      this.session.put("url", PageParser.escapeHtmlData(var7.url));
      this.session.put("user", PageParser.escapeHtmlData(var7.user));
      return "index.jsp";
   }

   private String getHistory() {
      int var1 = Integer.parseInt(this.attributes.getProperty("id"));
      String var2 = this.session.getCommand(var1);
      this.session.put("query", PageParser.escapeHtmlData(var2));
      return "query.jsp";
   }

   private static int addColumns(boolean var0, DbTableOrView var1, StringBuilder var2, int var3, boolean var4, StringBuilder var5) {
      DbColumn[] var6 = var1.getColumns();

      for(int var7 = 0; var6 != null && var7 < var6.length; ++var7) {
         DbColumn var8 = var6[var7];
         if (var5.length() > 0) {
            var5.append(' ');
         }

         var5.append(var8.getName());
         String var9 = escapeIdentifier(var8.getName());
         String var10 = var0 ? ", 1, 1" : ", 2, 2";
         var2.append("setNode(").append(var3).append(var10).append(", 'column', '").append(PageParser.escapeJavaScript(var8.getName())).append("', 'javascript:ins(\\'").append(var9).append("\\')');\n");
         ++var3;
         if (var0 && var4) {
            var2.append("setNode(").append(var3).append(", 2, 2, 'type', '").append(PageParser.escapeJavaScript(var8.getDataType())).append("', null);\n");
            ++var3;
         }
      }

      return var3;
   }

   private static String escapeIdentifier(String var0) {
      return StringUtils.urlEncode(PageParser.escapeJavaScript(var0)).replace('+', ' ');
   }

   private static int addIndexes(boolean var0, DatabaseMetaData var1, String var2, String var3, StringBuilder var4, int var5) throws SQLException {
      ResultSet var6;
      try {
         var6 = var1.getIndexInfo((String)null, var3, var2, false, true);
      } catch (SQLException var13) {
         return var5;
      }

      HashMap var7 = new HashMap();

      String var8;
      while(var6.next()) {
         var8 = var6.getString("INDEX_NAME");
         IndexInfo var9 = (IndexInfo)var7.get(var8);
         if (var9 == null) {
            int var10 = var6.getInt("TYPE");
            String var11;
            if (var10 == 1) {
               var11 = "";
            } else if (var10 == 2) {
               var11 = " (${text.tree.hashed})";
            } else if (var10 == 3) {
               var11 = "";
            } else {
               var11 = null;
            }

            if (var8 != null && var11 != null) {
               var9 = new IndexInfo();
               var9.name = var8;
               var11 = (var6.getBoolean("NON_UNIQUE") ? "${text.tree.nonUnique}" : "${text.tree.unique}") + var11;
               var9.type = var11;
               var9.columns = var6.getString("COLUMN_NAME");
               var7.put(var8, var9);
            }
         } else {
            var9.columns = var9.columns + ", " + var6.getString("COLUMN_NAME");
         }
      }

      var6.close();
      if (var7.size() > 0) {
         var8 = var0 ? ", 1, 1" : ", 2, 1";
         String var14 = var0 ? ", 2, 1" : ", 3, 1";
         String var15 = var0 ? ", 3, 2" : ", 4, 2";
         var4.append("setNode(").append(var5).append(var8).append(", 'index_az', '${text.tree.indexes}', null);\n");
         ++var5;

         for(Iterator var16 = var7.values().iterator(); var16.hasNext(); ++var5) {
            IndexInfo var12 = (IndexInfo)var16.next();
            var4.append("setNode(").append(var5).append(var14).append(", 'index', '").append(PageParser.escapeJavaScript(var12.name)).append("', null);\n");
            ++var5;
            var4.append("setNode(").append(var5).append(var15).append(", 'type', '").append(var12.type).append("', null);\n");
            ++var5;
            var4.append("setNode(").append(var5).append(var15).append(", 'type', '").append(PageParser.escapeJavaScript(var12.columns)).append("', null);\n");
         }
      }

      return var5;
   }

   private int addTablesAndViews(DbSchema var1, boolean var2, StringBuilder var3, int var4) throws SQLException {
      if (var1 == null) {
         return var4;
      } else {
         Connection var5 = this.session.getConnection();
         DatabaseMetaData var6 = this.session.getMetaData();
         int var7 = var2 ? 0 : 1;
         boolean var8 = var2 || !var1.isSystem;
         String var9 = ", " + var7 + ", " + (var8 ? "1" : "2") + ", ";
         String var10 = ", " + (var7 + 1) + ", 2, ";
         DbTableOrView[] var11 = var1.getTables();
         if (var11 == null) {
            return var4;
         } else {
            DbContents var12 = var1.getContents();
            boolean var13 = var12.isOracle();
            boolean var14 = var11.length < SysProperties.CONSOLE_MAX_TABLES_LIST_INDEXES;
            PreparedStatement var15 = var8 ? prepareViewDefinitionQuery(var5, var12) : null;
            Throwable var16 = null;

            try {
               if (var15 != null) {
                  var15.setString(1, var1.name);
               }

               DbTableOrView[] var17;
               int var18;
               int var19;
               DbTableOrView var20;
               if (var1.isSystem) {
                  Arrays.sort(var11, SYSTEM_SCHEMA_COMPARATOR);
                  var17 = var11;
                  var18 = var11.length;

                  for(var19 = 0; var19 < var18; ++var19) {
                     var20 = var17[var19];
                     var4 = addTableOrView(var1, var2, var3, var4, var6, false, var9, var13, var14, var20, var20.isView(), var15, var10);
                  }
               } else {
                  var17 = var11;
                  var18 = var11.length;

                  for(var19 = 0; var19 < var18; ++var19) {
                     var20 = var17[var19];
                     if (!var20.isView()) {
                        var4 = addTableOrView(var1, var2, var3, var4, var6, var8, var9, var13, var14, var20, false, (PreparedStatement)null, var10);
                     }
                  }

                  var17 = var11;
                  var18 = var11.length;

                  for(var19 = 0; var19 < var18; ++var19) {
                     var20 = var17[var19];
                     if (var20.isView()) {
                        var4 = addTableOrView(var1, var2, var3, var4, var6, var8, var9, var13, var14, var20, true, var15, var10);
                     }
                  }
               }
            } catch (Throwable var28) {
               var16 = var28;
               throw var28;
            } finally {
               if (var15 != null) {
                  if (var16 != null) {
                     try {
                        var15.close();
                     } catch (Throwable var27) {
                        var16.addSuppressed(var27);
                     }
                  } else {
                     var15.close();
                  }
               }

            }

            return var4;
         }
      }
   }

   private static PreparedStatement prepareViewDefinitionQuery(Connection var0, DbContents var1) {
      if (var1.mayHaveStandardViews()) {
         try {
            return var0.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
         } catch (SQLException var3) {
            var1.setMayHaveStandardViews(false);
         }
      }

      return null;
   }

   private static int addTableOrView(DbSchema var0, boolean var1, StringBuilder var2, int var3, DatabaseMetaData var4, boolean var5, String var6, boolean var7, boolean var8, DbTableOrView var9, boolean var10, PreparedStatement var11, String var12) throws SQLException {
      int var13 = var3;
      String var14 = var9.getQuotedName();
      if (!var1) {
         var14 = var0.quotedName + '.' + var14;
      }

      var14 = escapeIdentifier(var14);
      var2.append("setNode(").append(var3).append(var6).append(" '").append(var10 ? "view" : "table").append("', '").append(PageParser.escapeJavaScript(var9.getName())).append("', 'javascript:ins(\\'").append(var14).append("\\',true)');\n");
      ++var3;
      if (var5) {
         StringBuilder var15 = new StringBuilder();
         var3 = addColumns(var1, var9, var2, var3, var8, var15);
         if (var10) {
            if (var11 != null) {
               var11.setString(2, var9.getName());
               ResultSet var16 = var11.executeQuery();
               Throwable var17 = null;

               try {
                  if (var16.next()) {
                     String var18 = var16.getString(1);
                     if (var18 != null) {
                        var2.append("setNode(").append(var3).append(var12).append(" 'type', '").append(PageParser.escapeJavaScript(var18)).append("', null);\n");
                        ++var3;
                     }
                  }
               } catch (Throwable var26) {
                  var17 = var26;
                  throw var26;
               } finally {
                  if (var16 != null) {
                     if (var17 != null) {
                        try {
                           var16.close();
                        } catch (Throwable var25) {
                           var17.addSuppressed(var25);
                        }
                     } else {
                        var16.close();
                     }
                  }

               }
            }
         } else if (!var7 && var8) {
            var3 = addIndexes(var1, var4, var9.getName(), var0.name, var2, var3);
         }

         var2.append("addTable('").append(PageParser.escapeJavaScript(var9.getName())).append("', '").append(PageParser.escapeJavaScript(var15.toString())).append("', ").append(var13).append(");\n");
      }

      return var3;
   }

   private String tables() {
      DbContents var1 = this.session.getContents();
      boolean var2 = false;

      try {
         String var3 = (String)this.session.get("url");
         Connection var4 = this.session.getConnection();
         var1.readContents(var3, var4);
         this.session.loadBnf();
         var2 = var1.isH2();
         StringBuilder var5 = (new StringBuilder()).append("setNode(0, 0, 0, 'database', '").append(PageParser.escapeJavaScript(var3)).append("', null);\n");
         int var6 = 1;
         DbSchema var7 = var1.getDefaultSchema();
         var6 = this.addTablesAndViews(var7, true, var5, var6);
         DbSchema[] var8 = var1.getSchemas();
         DbSchema[] var9 = var8;
         int var10 = var8.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            DbSchema var12 = var9[var11];
            if (var12 != var7 && var12 != null) {
               var5.append("setNode(").append(var6).append(", 0, 1, 'folder', '").append(PageParser.escapeJavaScript(var12.name)).append("', null);\n");
               ++var6;
               var6 = this.addTablesAndViews(var12, false, var5, var6);
            }
         }

         if (var2) {
            Statement var31 = var4.createStatement();
            Throwable var33 = null;

            try {
               ResultSet var35;
               try {
                  var35 = var31.executeQuery("SELECT SEQUENCE_NAME, BASE_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
               } catch (SQLException var27) {
                  var35 = var31.executeQuery("SELECT SEQUENCE_NAME, CURRENT_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
               }

               String var13;
               String var14;
               int var36;
               for(var36 = 0; var35.next(); ++var36) {
                  if (var36 == 0) {
                     var5.append("setNode(").append(var6).append(", 0, 1, 'sequences', '${text.tree.sequences}', null);\n");
                     ++var6;
                  }

                  var13 = var35.getString(1);
                  var14 = var35.getString(2);
                  String var15 = var35.getString(3);
                  var5.append("setNode(").append(var6).append(", 1, 1, 'sequence', '").append(PageParser.escapeJavaScript(var13)).append("', null);\n");
                  ++var6;
                  var5.append("setNode(").append(var6).append(", 2, 2, 'type', '${text.tree.current}: ").append(PageParser.escapeJavaScript(var14)).append("', null);\n");
                  ++var6;
                  if (!"1".equals(var15)) {
                     var5.append("setNode(").append(var6).append(", 2, 2, 'type', '${text.tree.increment}: ").append(PageParser.escapeJavaScript(var15)).append("', null);\n");
                     ++var6;
                  }
               }

               var35.close();

               try {
                  var35 = var31.executeQuery("SELECT USER_NAME, IS_ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY USER_NAME");
               } catch (SQLException var26) {
                  var35 = var31.executeQuery("SELECT NAME, ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY NAME");
               }

               for(var36 = 0; var35.next(); ++var36) {
                  if (var36 == 0) {
                     var5.append("setNode(").append(var6).append(", 0, 1, 'users', '${text.tree.users}', null);\n");
                     ++var6;
                  }

                  var13 = var35.getString(1);
                  var14 = var35.getString(2);
                  var5.append("setNode(").append(var6).append(", 1, 1, 'user', '").append(PageParser.escapeJavaScript(var13)).append("', null);\n");
                  ++var6;
                  if (var14.equalsIgnoreCase("TRUE")) {
                     var5.append("setNode(").append(var6).append(", 2, 2, 'type', '${text.tree.admin}', null);\n");
                     ++var6;
                  }
               }

               var35.close();
            } catch (Throwable var28) {
               var33 = var28;
               throw var28;
            } finally {
               if (var31 != null) {
                  if (var33 != null) {
                     try {
                        var31.close();
                     } catch (Throwable var25) {
                        var33.addSuppressed(var25);
                     }
                  } else {
                     var31.close();
                  }
               }

            }
         }

         DatabaseMetaData var32 = this.session.getMetaData();
         String var34 = var32.getDatabaseProductName() + " " + var32.getDatabaseProductVersion();
         var5.append("setNode(").append(var6).append(", 0, 0, 'info', '").append(PageParser.escapeJavaScript(var34)).append("', null);\n").append("refreshQueryTables();");
         this.session.put("tree", var5.toString());
      } catch (Exception var30) {
         this.session.put("tree", "");
         this.session.put("error", this.getStackTrace(0, var30, var2));
      }

      return "tables.jsp";
   }

   private String getStackTrace(int var1, Throwable var2, boolean var3) {
      try {
         StringWriter var4 = new StringWriter();
         var2.printStackTrace(new PrintWriter(var4));
         String var5 = var4.toString();
         var5 = PageParser.escapeHtml(var5);
         if (var3) {
            var5 = linkToSource(var5);
         }

         var5 = StringUtils.replaceAll(var5, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
         String var6 = PageParser.escapeHtml(var2.getMessage());
         String var7 = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('st" + var1 + "').style;x.display=x.display==''?'none':'';\">" + var6 + "</a>";
         if (var2 instanceof SQLException) {
            SQLException var8 = (SQLException)var2;
            var7 = var7 + " " + var8.getSQLState() + "/" + var8.getErrorCode();
            if (var3) {
               int var9 = var8.getErrorCode();
               var7 = var7 + " <a href=\"https://h2database.com/javadoc/org/h2/api/ErrorCode.html#c" + var9 + "\">(${text.a.help})</a>";
            }
         }

         var7 = var7 + "<span style=\"display: none;\" id=\"st" + var1 + "\"><br />" + var5 + "</span>";
         var7 = formatAsError(var7);
         return var7;
      } catch (OutOfMemoryError var10) {
         this.server.traceError(var2);
         return var2.toString();
      }
   }

   private static String linkToSource(String var0) {
      try {
         StringBuilder var1 = new StringBuilder(var0.length());
         int var2 = var0.indexOf("<br />");
         var1.append(var0, 0, var2);

         while(true) {
            int var3 = var0.indexOf("org.h2.", var2);
            if (var3 < 0) {
               var1.append(var0.substring(var2));
               break;
            }

            var1.append(var0, var2, var3);
            int var4 = var0.indexOf(41, var3);
            if (var4 < 0) {
               var1.append(var0.substring(var2));
               break;
            }

            String var5 = var0.substring(var3, var4);
            int var6 = var5.lastIndexOf(40);
            int var7 = var5.lastIndexOf(46, var6 - 1);
            int var8 = var5.lastIndexOf(46, var7 - 1);
            String var9 = var5.substring(0, var8);
            int var10 = var5.lastIndexOf(58);
            String var11 = var5.substring(var6 + 1, var10);
            String var12 = var5.substring(var10 + 1, var5.length());
            String var13 = var9.replace('.', '/') + "/" + var11;
            var1.append("<a href=\"https://h2database.com/html/source.html?file=");
            var1.append(var13);
            var1.append("&line=");
            var1.append(var12);
            var1.append("&build=");
            var1.append(210);
            var1.append("\">");
            var1.append(var5);
            var1.append("</a>");
            var2 = var4;
         }

         return var1.toString();
      } catch (Throwable var14) {
         return var0;
      }
   }

   private static String formatAsError(String var0) {
      return "<div class=\"error\">" + var0 + "</div>";
   }

   private String test(NetworkConnectionInfo var1) {
      String var2 = this.attributes.getProperty("driver", "");
      String var3 = this.attributes.getProperty("url", "");
      String var4 = this.attributes.getProperty("user", "");
      String var5 = this.attributes.getProperty("password", "");
      this.session.put("driver", var2);
      this.session.put("url", var3);
      this.session.put("user", var4);
      boolean var6 = var3.startsWith("jdbc:h2:");

      try {
         long var7 = System.currentTimeMillis();
         String var9 = "";
         String var10 = "";
         Profiler var11 = new Profiler();
         var11.startCollecting();

         Connection var12;
         try {
            var12 = this.server.getConnection(var2, var3, var4, var5, (String)null, var1);
         } finally {
            var11.stopCollecting();
            var9 = var11.getTop(3);
         }

         var11 = new Profiler();
         var11.startCollecting();

         try {
            JdbcUtils.closeSilently(var12);
         } finally {
            var11.stopCollecting();
            var10 = var11.getTop(3);
         }

         long var13 = System.currentTimeMillis() - var7;
         String var15;
         if (var13 > 1000L) {
            var15 = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('prof').style;x.display=x.display==''?'none':'';\">${text.login.testSuccessful}</a><span style=\"display: none;\" id=\"prof\"><br />" + PageParser.escapeHtml(var9) + "<br />" + PageParser.escapeHtml(var10) + "</span>";
         } else {
            var15 = "<div class=\"success\">${text.login.testSuccessful}</div>";
         }

         this.session.put("error", var15);
         return "login.jsp";
      } catch (Exception var24) {
         this.session.put("error", this.getLoginError(var24, var6));
         return "login.jsp";
      }
   }

   private String getLoginError(Exception var1, boolean var2) {
      return var1 instanceof JdbcException && ((JdbcException)var1).getErrorCode() == 90086 ? "${text.login.driverNotFound}<br />" + this.getStackTrace(0, var1, var2) : this.getStackTrace(0, var1, var2);
   }

   private String login(NetworkConnectionInfo var1) {
      String var2 = this.attributes.getProperty("driver", "");
      String var3 = this.attributes.getProperty("url", "");
      String var4 = this.attributes.getProperty("user", "");
      String var5 = this.attributes.getProperty("password", "");
      this.session.put("autoCommit", "checked");
      this.session.put("autoComplete", "1");
      this.session.put("maxrows", "1000");
      boolean var6 = var3.startsWith("jdbc:h2:");

      try {
         Connection var7 = this.server.getConnection(var2, var3, var4, var5, (String)this.session.get("key"), var1);
         this.session.setConnection(var7);
         this.session.put("url", var3);
         this.session.put("user", var4);
         this.session.remove("error");
         this.settingSave();
         return "frame.jsp";
      } catch (Exception var8) {
         this.session.put("error", this.getLoginError(var8, var6));
         return "login.jsp";
      }
   }

   private String logout() {
      try {
         Connection var1 = this.session.getConnection();
         this.session.setConnection((Connection)null);
         this.session.remove("conn");
         this.session.remove("result");
         this.session.remove("tables");
         this.session.remove("user");
         this.session.remove("tool");
         if (var1 != null) {
            if (this.session.getShutdownServerOnDisconnect()) {
               this.server.shutdown();
            } else {
               var1.close();
            }
         }
      } catch (Exception var2) {
         this.trace(var2.toString());
      }

      this.session.remove("admin");
      return "index.do";
   }

   private String query() {
      String var1 = this.attributes.getProperty("sql").trim();

      try {
         ScriptReader var2 = new ScriptReader(new StringReader(var1));
         final ArrayList var3 = new ArrayList();

         while(true) {
            String var4 = var2.readStatement();
            if (var4 == null) {
               final Connection var10 = this.session.getConnection();
               String var5;
               if (SysProperties.CONSOLE_STREAM && this.server.getAllowChunked()) {
                  var5 = new String(this.server.getFile("result.jsp"), StandardCharsets.UTF_8);
                  int var11 = var5.indexOf("${result}");
                  var3.add(0, var5.substring(0, var11));
                  var3.add(var5.substring(var11 + "${result}".length()));
                  this.session.put("chunks", new Iterator<String>() {
                     private int i;

                     public boolean hasNext() {
                        return this.i < var3.size();
                     }

                     public String next() {
                        String var1 = (String)var3.get(this.i++);
                        if (this.i != 1 && this.i != var3.size()) {
                           StringBuilder var2 = new StringBuilder();
                           WebApp.this.query(var10, var1, this.i - 1, var3.size() - 2, var2);
                           return var2.toString();
                        } else {
                           return var1;
                        }
                     }
                  });
                  return "result.jsp";
               }

               StringBuilder var6 = new StringBuilder();

               for(int var7 = 0; var7 < var3.size(); ++var7) {
                  String var8 = (String)var3.get(var7);
                  this.query(var10, var8, var7, var3.size(), var6);
               }

               var5 = var6.toString();
               this.session.put("result", var5);
               break;
            }

            var3.add(var4);
         }
      } catch (Throwable var9) {
         this.session.put("result", this.getStackTrace(0, var9, this.session.getContents().isH2()));
      }

      return "result.jsp";
   }

   void query(Connection var1, String var2, int var3, int var4, StringBuilder var5) {
      if (!var2.startsWith("@") || !var2.endsWith(".")) {
         var5.append(PageParser.escapeHtml(var2 + ";")).append("<br />");
      }

      boolean var6 = var2.startsWith("@edit");
      var5.append(this.getResult(var1, var3 + 1, var2, var4 == 1, var6)).append("<br />");
   }

   private String editResult() {
      ResultSet var1 = this.session.result;
      int var2 = Integer.parseInt(this.attributes.getProperty("row"));
      int var3 = Integer.parseInt(this.attributes.getProperty("op"));
      String var4 = "";
      String var5 = "";

      try {
         if (var3 == 1) {
            boolean var6 = var2 < 0;
            if (var6) {
               var1.moveToInsertRow();
            } else {
               var1.absolute(var2);
            }

            for(int var7 = 0; var7 < var1.getMetaData().getColumnCount(); ++var7) {
               String var8 = this.attributes.getProperty("r" + var2 + "c" + (var7 + 1));
               this.unescapeData(var8, var1, var7 + 1);
            }

            if (var6) {
               var1.insertRow();
            } else {
               var1.updateRow();
            }
         } else if (var3 == 2) {
            var1.absolute(var2);
            var1.deleteRow();
         } else if (var3 == 3) {
         }
      } catch (Throwable var9) {
         var4 = "<br />" + this.getStackTrace(0, var9, this.session.getContents().isH2());
         var5 = formatAsError(var9.getMessage());
      }

      String var10 = "@edit " + (String)this.session.get("resultSetSQL");
      Connection var11 = this.session.getConnection();
      var4 = var5 + this.getResult(var11, -1, var10, true, true) + var4;
      this.session.put("result", var4);
      return "result.jsp";
   }

   private int getMaxrows() {
      String var1 = (String)this.session.get("maxrows");
      return var1 == null ? 0 : Integer.parseInt(var1);
   }

   private String getResult(Connection var1, int var2, String var3, boolean var4, boolean var5) {
      String var17;
      try {
         String var7;
         try {
            var3 = var3.trim();
            StringBuilder var6 = new StringBuilder();
            var7 = StringUtils.toUpperEnglish(var3);
            if (var7.contains("CREATE") || var7.contains("DROP") || var7.contains("ALTER") || var7.contains("RUNSCRIPT")) {
               String var8 = this.attributes.getProperty("jsessionid");
               var6.append("<script type=\"text/javascript\">parent['h2menu'].location='tables.do?jsessionid=").append(var8).append("';</script>");
            }

            DbContents var9 = this.session.getContents();
            Statement var29;
            if (!var5 && (!var4 || !var9.isH2())) {
               var29 = var1.createStatement();
            } else {
               var29 = var1.createStatement(1004, 1008);
            }

            long var11 = System.currentTimeMillis();
            boolean var13 = false;
            Object var14 = null;
            boolean var15 = false;
            boolean var16 = false;
            if (JdbcUtils.isBuiltIn(var3, "@autocommit_true")) {
               var1.setAutoCommit(true);
               var17 = "${text.result.autoCommitOn}";
               return var17;
            }

            if (!JdbcUtils.isBuiltIn(var3, "@autocommit_false")) {
               if (JdbcUtils.isBuiltIn(var3, "@cancel")) {
                  var29 = this.session.executingStatement;
                  if (var29 != null) {
                     var29.cancel();
                     var6.append("${text.result.statementWasCanceled}");
                  } else {
                     var6.append("${text.result.noRunningStatement}");
                  }

                  var17 = var6.toString();
                  return var17;
               }

               if (JdbcUtils.isBuiltIn(var3, "@edit")) {
                  var15 = true;
                  var3 = StringUtils.trimSubstring(var3, "@edit".length());
                  this.session.put("resultSetSQL", var3);
               }

               if (JdbcUtils.isBuiltIn(var3, "@list")) {
                  var16 = true;
                  var3 = StringUtils.trimSubstring(var3, "@list".length());
               }

               if (JdbcUtils.isBuiltIn(var3, "@meta")) {
                  var13 = true;
                  var3 = StringUtils.trimSubstring(var3, "@meta".length());
               }

               int var18;
               int var30;
               if (JdbcUtils.isBuiltIn(var3, "@generated")) {
                  var14 = true;
                  var30 = "@generated".length();

                  for(var18 = var3.length(); var30 < var18; ++var30) {
                     char var19 = var3.charAt(var30);
                     if (var19 == '(') {
                        Parser var20 = new Parser();
                        var14 = var20.parseColumnList(var3, var30);
                        var30 = var20.getLastParseIndex();
                        break;
                     }

                     if (!Character.isWhitespace(var19)) {
                        break;
                     }
                  }

                  var3 = StringUtils.trimSubstring(var3, var30);
               } else {
                  if (JdbcUtils.isBuiltIn(var3, "@history")) {
                     var6.append(this.getCommandHistoryString());
                     var17 = var6.toString();
                     return var17;
                  }

                  String var34;
                  if (JdbcUtils.isBuiltIn(var3, "@loop")) {
                     var3 = StringUtils.trimSubstring(var3, "@loop".length());
                     var30 = var3.indexOf(32);
                     var18 = Integer.decode(var3.substring(0, var30));
                     var3 = StringUtils.trimSubstring(var3, var30);
                     var34 = this.executeLoop(var1, var18, var3);
                     return var34;
                  }

                  String var36;
                  if (JdbcUtils.isBuiltIn(var3, "@maxrows")) {
                     var30 = (int)Double.parseDouble(StringUtils.trimSubstring(var3, "@maxrows".length()));
                     this.session.put("maxrows", Integer.toString(var30));
                     var36 = "${text.result.maxrowsSet}";
                     return var36;
                  }

                  if (JdbcUtils.isBuiltIn(var3, "@parameter_meta")) {
                     var3 = StringUtils.trimSubstring(var3, "@parameter_meta".length());
                     PreparedStatement var37 = var1.prepareStatement(var3);
                     var6.append(getParameterResultSet(var37.getParameterMetaData()));
                     var36 = var6.toString();
                     return var36;
                  }

                  if (JdbcUtils.isBuiltIn(var3, "@password_hash")) {
                     var3 = StringUtils.trimSubstring(var3, "@password_hash".length());
                     String[] var35 = JdbcUtils.split(var3);
                     var36 = StringUtils.convertBytesToHex(SHA256.getKeyPasswordHash(var35[0], var35[1].toCharArray()));
                     return var36;
                  }

                  if (JdbcUtils.isBuiltIn(var3, "@prof_start")) {
                     if (this.profiler != null) {
                        this.profiler.stopCollecting();
                     }

                     this.profiler = new Profiler();
                     this.profiler.startCollecting();
                     var17 = "Ok";
                     return var17;
                  }

                  if (JdbcUtils.isBuiltIn(var3, "@sleep")) {
                     var17 = StringUtils.trimSubstring(var3, "@sleep".length());
                     var18 = 1;
                     if (var17.length() > 0) {
                        var18 = Integer.parseInt(var17);
                     }

                     Thread.sleep((long)(var18 * 1000));
                     var34 = "Ok";
                     return var34;
                  }

                  if (JdbcUtils.isBuiltIn(var3, "@transaction_isolation")) {
                     var17 = StringUtils.trimSubstring(var3, "@transaction_isolation".length());
                     if (var17.length() > 0) {
                        var18 = Integer.parseInt(var17);
                        var1.setTransactionIsolation(var18);
                     }

                     var6.append("Transaction Isolation: ").append(var1.getTransactionIsolation()).append("<br />");
                     var6.append(1).append(": read_uncommitted<br />");
                     var6.append(2).append(": read_committed<br />");
                     var6.append(4).append(": repeatable_read<br />");
                     var6.append(6).append(": snapshot<br />");
                     var6.append(8).append(": serializable");
                  }
               }

               Object var10;
               if (var3.startsWith("@")) {
                  var10 = JdbcUtils.getMetaResultSet(var1, var3);
                  if (var10 == null && JdbcUtils.isBuiltIn(var3, "@prof_stop") && this.profiler != null) {
                     this.profiler.stopCollecting();
                     SimpleResultSet var32 = new SimpleResultSet();
                     var32.addColumn("Top Stack Trace(s)", 12, 0, 0);
                     var32.addRow(this.profiler.getTop(3));
                     var10 = var32;
                     this.profiler = null;
                  }

                  if (var10 == null) {
                     var6.append("?: ").append(var3);
                     var17 = var6.toString();
                     return var17;
                  }
               } else {
                  var30 = this.getMaxrows();
                  var29.setMaxRows(var30);
                  this.session.executingStatement = var29;
                  boolean var31;
                  if (var14 == null) {
                     var31 = var29.execute(var3);
                  } else if (var14 instanceof Boolean) {
                     var31 = var29.execute(var3, (Boolean)var14 ? 1 : 2);
                  } else if (var14 instanceof String[]) {
                     var31 = var29.execute(var3, (String[])((String[])var14));
                  } else {
                     var31 = var29.execute(var3, (int[])((int[])var14));
                  }

                  this.session.addCommand(var3);
                  if (var14 != null) {
                     var10 = null;
                     var10 = var29.getGeneratedKeys();
                  } else {
                     if (!var31) {
                        long var33;
                        try {
                           var33 = var29.getLargeUpdateCount();
                        } catch (UnsupportedOperationException var26) {
                           var33 = (long)var29.getUpdateCount();
                        }

                        var6.append("${text.result.updateCount}: ").append(var33);
                        var11 = System.currentTimeMillis() - var11;
                        var6.append("<br />(").append(var11).append(" ms)");
                        var29.close();
                        String var21 = var6.toString();
                        return var21;
                     }

                     var10 = var29.getResultSet();
                  }
               }

               var11 = System.currentTimeMillis() - var11;
               var6.append(this.getResultSet(var3, (ResultSet)var10, var13, var16, var15, var11, var4));
               if (!var15) {
                  var29.close();
               }

               var17 = var6.toString();
               return var17;
            }

            var1.setAutoCommit(false);
            var17 = "${text.result.autoCommitOff}";
         } catch (Throwable var27) {
            var7 = this.getStackTrace(var2, var27, this.session.getContents().isH2());
            return var7;
         }
      } finally {
         this.session.executingStatement = null;
      }

      return var17;
   }

   private String executeLoop(Connection var1, int var2, String var3) throws SQLException {
      ArrayList var4 = new ArrayList();

      int var5;
      for(var5 = 0; !this.stop; ++var5) {
         var5 = var3.indexOf(63, var5);
         if (var5 < 0) {
            break;
         }

         if (JdbcUtils.isBuiltIn(var3.substring(var5), "?/*rnd*/")) {
            var4.add(1);
            var3 = var3.substring(0, var5) + "?" + var3.substring(var5 + "/*rnd*/".length() + 1);
         } else {
            var4.add(0);
         }
      }

      Random var7 = new Random(1L);
      long var8 = System.currentTimeMillis();
      boolean var6;
      int var11;
      int var17;
      if (JdbcUtils.isBuiltIn(var3, "@statement")) {
         var3 = StringUtils.trimSubstring(var3, "@statement".length());
         var6 = false;
         Statement var10 = var1.createStatement();

         for(var11 = 0; !this.stop && var11 < var2; ++var11) {
            String var12 = var3;
            Iterator var13 = var4.iterator();

            while(var13.hasNext()) {
               Integer var14 = (Integer)var13.next();
               var5 = var12.indexOf(63);
               if (var14 == 1) {
                  var12 = var12.substring(0, var5) + var7.nextInt(var2) + var12.substring(var5 + 1);
               } else {
                  var12 = var12.substring(0, var5) + var11 + var12.substring(var5 + 1);
               }
            }

            if (var10.execute(var12)) {
               ResultSet var18 = var10.getResultSet();

               while(!this.stop && var18.next()) {
               }

               var18.close();
            }
         }
      } else {
         var6 = true;
         PreparedStatement var15 = var1.prepareStatement(var3);

         for(var11 = 0; !this.stop && var11 < var2; ++var11) {
            for(var17 = 0; var17 < var4.size(); ++var17) {
               Integer var19 = (Integer)var4.get(var17);
               if (var19 == 1) {
                  var15.setInt(var17 + 1, var7.nextInt(var2));
               } else {
                  var15.setInt(var17 + 1, var11);
               }
            }

            if (this.session.getContents().isSQLite()) {
               var15.executeUpdate();
            } else if (var15.execute()) {
               ResultSet var20 = var15.getResultSet();

               while(!this.stop && var20.next()) {
               }

               var20.close();
            }
         }
      }

      var8 = System.currentTimeMillis() - var8;
      StringBuilder var16 = (new StringBuilder()).append(var8).append(" ms: ").append(var2).append(" * ").append(var6 ? "(Prepared) " : "(Statement) ").append('(');
      var11 = 0;

      for(var17 = var4.size(); var11 < var17; ++var11) {
         if (var11 > 0) {
            var16.append(", ");
         }

         var16.append((Integer)var4.get(var11) == 0 ? "i" : "rnd");
      }

      return var16.append(") ").append(var3).toString();
   }

   private String getCommandHistoryString() {
      StringBuilder var1 = new StringBuilder();
      ArrayList var2 = this.session.getCommandHistory();
      var1.append("<table cellspacing=0 cellpadding=0><tr><th></th><th>Command</th></tr>");

      for(int var3 = var2.size() - 1; var3 >= 0; --var3) {
         String var4 = (String)var2.get(var3);
         var1.append("<tr><td><a href=\"getHistory.do?id=").append(var3).append("&jsessionid=${sessionId}\" target=\"h2query\" >").append("<img width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" ").append("onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" ").append("title=\"${text.resultEdit.edit}\" border=\"1\"/></a>").append("</td><td>").append(PageParser.escapeHtml(var4)).append("</td></tr>");
      }

      var1.append("</table>");
      return var1.toString();
   }

   private static String getParameterResultSet(ParameterMetaData var0) throws SQLException {
      StringBuilder var1 = new StringBuilder();
      if (var0 == null) {
         return "No parameter meta data";
      } else {
         var1.append("<table cellspacing=0 cellpadding=0>").append("<tr><th>className</th><th>mode</th><th>type</th>").append("<th>typeName</th><th>precision</th><th>scale</th></tr>");

         for(int var2 = 0; var2 < var0.getParameterCount(); ++var2) {
            var1.append("</tr><td>").append(var0.getParameterClassName(var2 + 1)).append("</td><td>").append(var0.getParameterMode(var2 + 1)).append("</td><td>").append(var0.getParameterType(var2 + 1)).append("</td><td>").append(var0.getParameterTypeName(var2 + 1)).append("</td><td>").append(var0.getPrecision(var2 + 1)).append("</td><td>").append(var0.getScale(var2 + 1)).append("</td></tr>");
         }

         var1.append("</table>");
         return var1.toString();
      }
   }

   private String getResultSet(String var1, ResultSet var2, boolean var3, boolean var4, boolean var5, long var6, boolean var8) throws SQLException {
      int var9 = this.getMaxrows();
      var6 = System.currentTimeMillis() - var6;
      StringBuilder var10 = new StringBuilder();
      if (var5) {
         var10.append("<form id=\"editing\" name=\"editing\" method=\"post\" action=\"editResult.do?jsessionid=${sessionId}\" id=\"mainForm\" target=\"h2result\"><input type=\"hidden\" name=\"op\" value=\"1\" /><input type=\"hidden\" name=\"row\" value=\"\" /><table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\" id=\"editTable\">");
      } else {
         var10.append("<table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\">");
      }

      int var13;
      if (var3) {
         SimpleResultSet var11 = new SimpleResultSet();
         var11.addColumn("#", 4, 0, 0);
         var11.addColumn("label", 12, 0, 0);
         var11.addColumn("catalog", 12, 0, 0);
         var11.addColumn("schema", 12, 0, 0);
         var11.addColumn("table", 12, 0, 0);
         var11.addColumn("column", 12, 0, 0);
         var11.addColumn("type", 4, 0, 0);
         var11.addColumn("typeName", 12, 0, 0);
         var11.addColumn("class", 12, 0, 0);
         var11.addColumn("precision", 4, 0, 0);
         var11.addColumn("scale", 4, 0, 0);
         var11.addColumn("displaySize", 4, 0, 0);
         var11.addColumn("autoIncrement", 16, 0, 0);
         var11.addColumn("caseSensitive", 16, 0, 0);
         var11.addColumn("currency", 16, 0, 0);
         var11.addColumn("nullable", 4, 0, 0);
         var11.addColumn("readOnly", 16, 0, 0);
         var11.addColumn("searchable", 16, 0, 0);
         var11.addColumn("signed", 16, 0, 0);
         var11.addColumn("writable", 16, 0, 0);
         var11.addColumn("definitelyWritable", 16, 0, 0);
         ResultSetMetaData var12 = ((ResultSet)var2).getMetaData();

         for(var13 = 1; var13 <= var12.getColumnCount(); ++var13) {
            var11.addRow(var13, var12.getColumnLabel(var13), var12.getCatalogName(var13), var12.getSchemaName(var13), var12.getTableName(var13), var12.getColumnName(var13), var12.getColumnType(var13), var12.getColumnTypeName(var13), var12.getColumnClassName(var13), var12.getPrecision(var13), var12.getScale(var13), var12.getColumnDisplaySize(var13), var12.isAutoIncrement(var13), var12.isCaseSensitive(var13), var12.isCurrency(var13), var12.isNullable(var13), var12.isReadOnly(var13), var12.isSearchable(var13), var12.isSigned(var13), var12.isWritable(var13), var12.isDefinitelyWritable(var13));
         }

         var2 = var11;
      }

      ResultSetMetaData var17 = ((ResultSet)var2).getMetaData();
      int var18 = var17.getColumnCount();
      var13 = 0;
      int var14;
      if (var4) {
         var10.append("<tr><th>Column</th><th>Data</th></tr><tr>");

         while(((ResultSet)var2).next() && (var9 <= 0 || var13 < var9)) {
            ++var13;
            var10.append("<tr><td>Row #</td><td>").append(var13).append("</tr>");

            for(var14 = 0; var14 < var18; ++var14) {
               var10.append("<tr><td>").append(PageParser.escapeHtml(var17.getColumnLabel(var14 + 1))).append("</td><td>").append(escapeData((ResultSet)var2, var14 + 1)).append("</td></tr>");
            }
         }
      } else {
         var10.append("<tr>");
         if (var5) {
            var10.append("<th>${text.resultEdit.action}</th>");
         }

         for(var14 = 0; var14 < var18; ++var14) {
            var10.append("<th>").append(PageParser.escapeHtml(var17.getColumnLabel(var14 + 1))).append("</th>");
         }

         var10.append("</tr>");

         while(((ResultSet)var2).next() && (var9 <= 0 || var13 < var9)) {
            ++var13;
            var10.append("<tr>");
            if (var5) {
               var10.append("<td>").append("<img onclick=\"javascript:editRow(").append(((ResultSet)var2).getRow()).append(",'${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" title=\"${text.resultEdit.edit}\" border=\"1\"/>").append("<img onclick=\"javascript:deleteRow(").append(((ResultSet)var2).getRow()).append(",'${sessionId}', '${text.resultEdit.delete}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_remove.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.delete}\" title=\"${text.resultEdit.delete}\" border=\"1\" /></a>").append("</td>");
            }

            for(var14 = 0; var14 < var18; ++var14) {
               var10.append("<td>").append(escapeData((ResultSet)var2, var14 + 1)).append("</td>");
            }

            var10.append("</tr>");
         }
      }

      boolean var20 = false;

      try {
         if (!this.session.getContents().isDB2()) {
            var20 = ((ResultSet)var2).getConcurrency() == 1008 && ((ResultSet)var2).getType() != 1003;
         }
      } catch (NullPointerException var16) {
      }

      if (var5) {
         ResultSet var15 = this.session.result;
         if (var15 != null) {
            var15.close();
         }

         this.session.result = (ResultSet)var2;
      } else {
         ((ResultSet)var2).close();
      }

      if (var5) {
         var10.append("<tr><td>").append("<img onclick=\"javascript:editRow(-1, '${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_add.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.add}\" title=\"${text.resultEdit.add}\" border=\"1\"/>").append("</td>");

         for(int var19 = 0; var19 < var18; ++var19) {
            var10.append("<td></td>");
         }

         var10.append("</tr>");
      }

      var10.append("</table>");
      if (var5) {
         var10.append("</form>");
      }

      if (var13 == 0) {
         var10.append("(${text.result.noRows}");
      } else if (var13 == 1) {
         var10.append("(${text.result.1row}");
      } else {
         var10.append('(').append(var13).append(" ${text.result.rows}");
      }

      var10.append(", ");
      var6 = System.currentTimeMillis() - var6;
      var10.append(var6).append(" ms)");
      if (!var5 && var20 && var8) {
         var10.append("<br /><br /><form name=\"editResult\" method=\"post\" action=\"query.do?jsessionid=${sessionId}\" target=\"h2result\"><input type=\"submit\" class=\"button\" value=\"${text.resultEdit.editResult}\" /><input type=\"hidden\" name=\"sql\" value=\"@edit ").append(PageParser.escapeHtmlData(var1)).append("\" /></form>");
      }

      return var10.toString();
   }

   private String settingSave() {
      ConnectionInfo var1 = new ConnectionInfo();
      var1.name = this.attributes.getProperty("name", "");
      var1.driver = this.attributes.getProperty("driver", "");
      var1.url = this.attributes.getProperty("url", "");
      var1.user = this.attributes.getProperty("user", "");
      this.server.updateSetting(var1);
      this.attributes.put("setting", var1.name);
      this.server.saveProperties((Properties)null);
      return "index.do";
   }

   private static String escapeData(ResultSet var0, int var1) throws SQLException {
      if (DataType.isBinaryColumn(var0.getMetaData(), var1)) {
         byte[] var3 = var0.getBytes(var1);
         if (var3 == null) {
            return "<i>null</i>";
         } else {
            return var3.length > 50000 ? "<div style='display: none'>=+</div>" + StringUtils.convertBytesToHex(var3, 3) + "... (" + var3.length + " ${text.result.bytes})" : StringUtils.convertBytesToHex(var3);
         }
      } else {
         String var2 = var0.getString(var1);
         if (var2 == null) {
            return "<i>null</i>";
         } else if (var2.length() > 100000) {
            return "<div style='display: none'>=+</div>" + PageParser.escapeHtml(var2.substring(0, 100)) + "... (" + var2.length() + " ${text.result.characters})";
         } else if (!var2.equals("null") && !var2.startsWith("= ") && !var2.startsWith("=+")) {
            return var2.equals("") ? "" : PageParser.escapeHtml(var2);
         } else {
            return "<div style='display: none'>= </div>" + PageParser.escapeHtml(var2);
         }
      }
   }

   private void unescapeData(String var1, ResultSet var2, int var3) throws SQLException {
      if (var1.equals("null")) {
         var2.updateNull(var3);
      } else if (!var1.startsWith("=+")) {
         if (var1.equals("=*")) {
            int var6 = var2.getMetaData().getColumnType(var3);
            switch (var6) {
               case 91:
               case 93:
                  var2.updateString(var3, "2001-01-01");
                  break;
               case 92:
                  var2.updateString(var3, "12:00:00");
                  break;
               default:
                  var2.updateString(var3, "1");
            }

         } else {
            if (var1.startsWith("= ")) {
               var1 = var1.substring(2);
            }

            ResultSetMetaData var4 = var2.getMetaData();
            if (DataType.isBinaryColumn(var4, var3)) {
               var2.updateBytes(var3, StringUtils.convertHexToBytes(var1));
            } else {
               int var5 = var4.getColumnType(var3);
               if (this.session.getContents().isH2()) {
                  var2.updateString(var3, var1);
               } else {
                  switch (var5) {
                     case -6:
                        var2.updateShort(var3, Short.decode(var1));
                        break;
                     case -5:
                        var2.updateLong(var3, Long.decode(var1));
                        break;
                     case -4:
                     case -3:
                     case -2:
                     case -1:
                     case 0:
                     case 1:
                     case 2:
                     case 5:
                     default:
                        var2.updateString(var3, var1);
                        break;
                     case 3:
                        var2.updateBigDecimal(var3, new BigDecimal(var1));
                        break;
                     case 4:
                        var2.updateInt(var3, Integer.decode(var1));
                        break;
                     case 6:
                     case 8:
                        var2.updateDouble(var3, Double.parseDouble(var1));
                        break;
                     case 7:
                        var2.updateFloat(var3, Float.parseFloat(var1));
                  }

               }
            }
         }
      }
   }

   private String settingRemove() {
      String var1 = this.attributes.getProperty("name", "");
      this.server.removeSetting(var1);
      ArrayList var2 = this.server.getSettings();
      if (!var2.isEmpty()) {
         this.attributes.put("setting", var2.get(0));
      }

      this.server.saveProperties((Properties)null);
      return "index.do";
   }

   String getMimeType() {
      return this.mimeType;
   }

   boolean getCache() {
      return this.cache;
   }

   WebSession getSession() {
      return this.session;
   }

   private void trace(String var1) {
      this.server.trace(var1);
   }

   static {
      SYSTEM_SCHEMA_COMPARATOR = Comparator.comparing(DbTableOrView::getName, String.CASE_INSENSITIVE_ORDER);
   }

   static class IndexInfo {
      String name;
      String type;
      String columns;
   }
}
