/*      */ package org.h2.server.web;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Random;
/*      */ import org.h2.bnf.Bnf;
/*      */ import org.h2.bnf.context.DbColumn;
/*      */ import org.h2.bnf.context.DbContents;
/*      */ import org.h2.bnf.context.DbSchema;
/*      */ import org.h2.bnf.context.DbTableOrView;
/*      */ import org.h2.command.Parser;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.jdbc.JdbcException;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.security.SHA256;
/*      */ import org.h2.tools.Backup;
/*      */ import org.h2.tools.ChangeFileEncryption;
/*      */ import org.h2.tools.ConvertTraceFile;
/*      */ import org.h2.tools.CreateCluster;
/*      */ import org.h2.tools.DeleteDbFiles;
/*      */ import org.h2.tools.Recover;
/*      */ import org.h2.tools.Restore;
/*      */ import org.h2.tools.RunScript;
/*      */ import org.h2.tools.Script;
/*      */ import org.h2.tools.SimpleResultSet;
/*      */ import org.h2.util.JdbcUtils;
/*      */ import org.h2.util.NetUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.Profiler;
/*      */ import org.h2.util.ScriptReader;
/*      */ import org.h2.util.SortedProperties;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.util.Utils10;
/*      */ import org.h2.value.DataType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class WebApp
/*      */ {
/*   76 */   private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR = Comparator.comparing(DbTableOrView::getName, String.CASE_INSENSITIVE_ORDER);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final WebServer server;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected WebSession session;
/*      */ 
/*      */ 
/*      */   
/*      */   protected Properties attributes;
/*      */ 
/*      */ 
/*      */   
/*      */   protected String mimeType;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean cache;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean stop;
/*      */ 
/*      */ 
/*      */   
/*      */   protected String headerLanguage;
/*      */ 
/*      */ 
/*      */   
/*      */   private Profiler profiler;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   WebApp(WebServer paramWebServer) {
/*  116 */     this.server = paramWebServer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setSession(WebSession paramWebSession, Properties paramProperties) {
/*  126 */     this.session = paramWebSession;
/*  127 */     this.attributes = paramProperties;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String processRequest(String paramString, NetworkConnectionInfo paramNetworkConnectionInfo) {
/*      */     String str;
/*  138 */     int i = paramString.lastIndexOf('.');
/*      */     
/*  140 */     if (i >= 0) {
/*  141 */       str = paramString.substring(i + 1);
/*      */     } else {
/*  143 */       str = "";
/*      */     } 
/*  145 */     if ("ico".equals(str)) {
/*  146 */       this.mimeType = "image/x-icon";
/*  147 */       this.cache = true;
/*  148 */     } else if ("gif".equals(str)) {
/*  149 */       this.mimeType = "image/gif";
/*  150 */       this.cache = true;
/*  151 */     } else if ("css".equals(str)) {
/*  152 */       this.cache = true;
/*  153 */       this.mimeType = "text/css";
/*  154 */     } else if ("html".equals(str) || "do"
/*  155 */       .equals(str) || "jsp"
/*  156 */       .equals(str)) {
/*  157 */       this.cache = false;
/*  158 */       this.mimeType = "text/html";
/*  159 */       if (this.session == null) {
/*  160 */         this.session = this.server.createNewSession(
/*  161 */             NetUtils.ipToShortForm(null, paramNetworkConnectionInfo.getClientAddr(), false).toString());
/*  162 */         if (!"notAllowed.jsp".equals(paramString)) {
/*  163 */           paramString = "index.do";
/*      */         }
/*      */       } 
/*  166 */     } else if ("js".equals(str)) {
/*  167 */       this.cache = true;
/*  168 */       this.mimeType = "text/javascript";
/*      */     } else {
/*  170 */       this.cache = true;
/*  171 */       this.mimeType = "application/octet-stream";
/*      */     } 
/*  173 */     trace("mimeType=" + this.mimeType);
/*  174 */     trace(paramString);
/*  175 */     if (paramString.endsWith(".do")) {
/*  176 */       paramString = process(paramString, paramNetworkConnectionInfo);
/*  177 */     } else if (paramString.endsWith(".jsp")) {
/*  178 */       switch (paramString) {
/*      */         case "admin.jsp":
/*      */         case "tools.jsp":
/*  181 */           if (!checkAdmin(paramString))
/*  182 */             paramString = process("adminLogin.do", paramNetworkConnectionInfo); 
/*      */           break;
/*      */       } 
/*      */     } 
/*  186 */     return paramString;
/*      */   }
/*      */   
/*      */   private static String getComboBox(String[] paramArrayOfString, String paramString) {
/*  190 */     StringBuilder stringBuilder = new StringBuilder();
/*  191 */     for (String str : paramArrayOfString) {
/*  192 */       stringBuilder.append("<option value=\"")
/*  193 */         .append(PageParser.escapeHtmlData(str))
/*  194 */         .append('"');
/*  195 */       if (str.equals(paramString)) {
/*  196 */         stringBuilder.append(" selected");
/*      */       }
/*  198 */       stringBuilder.append('>')
/*  199 */         .append(PageParser.escapeHtml(str))
/*  200 */         .append("</option>");
/*      */     } 
/*  202 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   private static String getComboBox(String[][] paramArrayOfString, String paramString) {
/*  206 */     StringBuilder stringBuilder = new StringBuilder();
/*  207 */     for (String[] arrayOfString : paramArrayOfString) {
/*  208 */       stringBuilder.append("<option value=\"")
/*  209 */         .append(PageParser.escapeHtmlData(arrayOfString[0]))
/*  210 */         .append('"');
/*  211 */       if (arrayOfString[0].equals(paramString)) {
/*  212 */         stringBuilder.append(" selected");
/*      */       }
/*  214 */       stringBuilder.append('>')
/*  215 */         .append(PageParser.escapeHtml(arrayOfString[1]))
/*  216 */         .append("</option>");
/*      */     } 
/*  218 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   private String process(String paramString, NetworkConnectionInfo paramNetworkConnectionInfo) {
/*  222 */     trace("process " + paramString);
/*  223 */     while (paramString.endsWith(".do")) {
/*  224 */       switch (paramString) {
/*      */         case "login.do":
/*  226 */           paramString = login(paramNetworkConnectionInfo);
/*      */           continue;
/*      */         case "index.do":
/*  229 */           paramString = index();
/*      */           continue;
/*      */         case "logout.do":
/*  232 */           paramString = logout();
/*      */           continue;
/*      */         case "settingRemove.do":
/*  235 */           paramString = settingRemove();
/*      */           continue;
/*      */         case "settingSave.do":
/*  238 */           paramString = settingSave();
/*      */           continue;
/*      */         case "test.do":
/*  241 */           paramString = test(paramNetworkConnectionInfo);
/*      */           continue;
/*      */         case "query.do":
/*  244 */           paramString = query();
/*      */           continue;
/*      */         case "tables.do":
/*  247 */           paramString = tables();
/*      */           continue;
/*      */         case "editResult.do":
/*  250 */           paramString = editResult();
/*      */           continue;
/*      */         case "getHistory.do":
/*  253 */           paramString = getHistory();
/*      */           continue;
/*      */         case "admin.do":
/*  256 */           paramString = checkAdmin(paramString) ? admin() : "adminLogin.do";
/*      */           continue;
/*      */         case "adminSave.do":
/*  259 */           paramString = checkAdmin(paramString) ? adminSave() : "adminLogin.do";
/*      */           continue;
/*      */         case "adminStartTranslate.do":
/*  262 */           paramString = checkAdmin(paramString) ? adminStartTranslate() : "adminLogin.do";
/*      */           continue;
/*      */         case "adminShutdown.do":
/*  265 */           paramString = checkAdmin(paramString) ? adminShutdown() : "adminLogin.do";
/*      */           continue;
/*      */         case "autoCompleteList.do":
/*  268 */           paramString = autoCompleteList();
/*      */           continue;
/*      */         case "tools.do":
/*  271 */           paramString = checkAdmin(paramString) ? tools() : "adminLogin.do";
/*      */           continue;
/*      */         case "adminLogin.do":
/*  274 */           paramString = adminLogin();
/*      */           continue;
/*      */       } 
/*  277 */       paramString = "error.jsp";
/*      */     } 
/*      */ 
/*      */     
/*  281 */     trace("return " + paramString);
/*  282 */     return paramString;
/*      */   }
/*      */   
/*      */   private boolean checkAdmin(String paramString) {
/*  286 */     Boolean bool = (Boolean)this.session.get("admin");
/*  287 */     if (bool != null && bool.booleanValue()) {
/*  288 */       return true;
/*      */     }
/*  290 */     String str = this.server.getKey();
/*  291 */     if (str != null && str.equals(this.session.get("key"))) {
/*  292 */       return true;
/*      */     }
/*  294 */     this.session.put("adminBack", paramString);
/*  295 */     return false;
/*      */   }
/*      */   
/*      */   private String adminLogin() {
/*  299 */     String str1 = this.attributes.getProperty("password");
/*  300 */     if (str1 == null || str1.isEmpty() || !this.server.checkAdminPassword(str1)) {
/*  301 */       return "adminLogin.jsp";
/*      */     }
/*  303 */     String str2 = (String)this.session.remove("adminBack");
/*  304 */     this.session.put("admin", Boolean.valueOf(true));
/*  305 */     return (str2 != null) ? str2 : "admin.do";
/*      */   }
/*      */   
/*      */   private String autoCompleteList() {
/*  309 */     String str1 = (String)this.attributes.get("query");
/*  310 */     boolean bool = false;
/*  311 */     String str2 = str1.trim();
/*  312 */     if (!str2.isEmpty() && Character.isLowerCase(str2.charAt(0))) {
/*  313 */       bool = true;
/*      */     }
/*      */     try {
/*  316 */       String str3 = str1;
/*  317 */       if (str3.endsWith(";")) {
/*  318 */         str3 = str3 + " ";
/*      */       }
/*  320 */       ScriptReader scriptReader = new ScriptReader(new StringReader(str3));
/*  321 */       scriptReader.setSkipRemarks(true);
/*  322 */       String str4 = "";
/*      */       while (true) {
/*  324 */         String str = scriptReader.readStatement();
/*  325 */         if (str == null) {
/*      */           break;
/*      */         }
/*  328 */         str4 = str;
/*      */       } 
/*  330 */       String str5 = "";
/*  331 */       if (scriptReader.isInsideRemark()) {
/*  332 */         if (scriptReader.isBlockRemark()) {
/*  333 */           str5 = "1#(End Remark)# */\n" + str5;
/*      */         } else {
/*  335 */           str5 = "1#(Newline)#\n" + str5;
/*      */         } 
/*      */       } else {
/*  338 */         str3 = str4;
/*  339 */         while (str3.length() > 0 && str3.charAt(0) <= ' ') {
/*  340 */           str3 = str3.substring(1);
/*      */         }
/*  342 */         String str6 = str3.trim();
/*  343 */         if (!str6.isEmpty() && Character.isLowerCase(str6.charAt(0))) {
/*  344 */           bool = true;
/*      */         }
/*  346 */         Bnf bnf = this.session.getBnf();
/*  347 */         if (bnf == null) {
/*  348 */           return "autoCompleteList.jsp";
/*      */         }
/*  350 */         HashMap hashMap = bnf.getNextTokenList(str3);
/*  351 */         String str7 = "";
/*  352 */         if (str3.length() > 0) {
/*  353 */           char c = str3.charAt(str3.length() - 1);
/*  354 */           if (!Character.isWhitespace(c) && c != '.' && c >= ' ' && c != '\'' && c != '"')
/*      */           {
/*  356 */             str7 = " ";
/*      */           }
/*      */         } 
/*  359 */         ArrayList<String> arrayList = new ArrayList(hashMap.size());
/*  360 */         for (Map.Entry entry : hashMap.entrySet()) {
/*  361 */           String str8 = (String)entry.getKey();
/*  362 */           String str9 = (String)entry.getValue();
/*  363 */           String str10 = String.valueOf(str8.charAt(0));
/*  364 */           if (Integer.parseInt(str10) > 2) {
/*      */             continue;
/*      */           }
/*  367 */           str8 = str8.substring(2);
/*  368 */           if (Character.isLetter(str8.charAt(0)) && bool) {
/*  369 */             str8 = StringUtils.toLowerEnglish(str8);
/*  370 */             str9 = StringUtils.toLowerEnglish(str9);
/*      */           } 
/*  372 */           if (str8.equals(str9) && !".".equals(str9)) {
/*  373 */             str9 = str7 + str9;
/*      */           }
/*  375 */           str8 = StringUtils.urlEncode(str8);
/*  376 */           str8 = str8.replace('+', ' ');
/*  377 */           str9 = StringUtils.urlEncode(str9);
/*  378 */           str9 = str9.replace('+', ' ');
/*  379 */           arrayList.add(str10 + "#" + str8 + "#" + str9);
/*      */         } 
/*  381 */         Collections.sort(arrayList);
/*  382 */         if (str1.endsWith("\n") || str2.endsWith(";")) {
/*  383 */           arrayList.add(0, "1#(Newline)#\n");
/*      */         }
/*  385 */         str5 = String.join("|", (Iterable)arrayList);
/*      */       } 
/*  387 */       this.session.put("autoCompleteList", str5);
/*  388 */     } catch (Throwable throwable) {
/*  389 */       this.server.traceError(throwable);
/*      */     } 
/*  391 */     return "autoCompleteList.jsp";
/*      */   }
/*      */   
/*      */   private String admin() {
/*  395 */     this.session.put("port", Integer.toString(this.server.getPort()));
/*  396 */     this.session.put("allowOthers", Boolean.toString(this.server.getAllowOthers()));
/*  397 */     this.session.put("webExternalNames", this.server.getExternalNames());
/*  398 */     this.session.put("ssl", String.valueOf(this.server.getSSL()));
/*  399 */     this.session.put("sessions", this.server.getSessions());
/*  400 */     return "admin.jsp";
/*      */   }
/*      */   
/*      */   private String adminSave() {
/*      */     try {
/*  405 */       SortedProperties sortedProperties = new SortedProperties();
/*  406 */       int i = Integer.decode((String)this.attributes.get("port")).intValue();
/*  407 */       sortedProperties.setProperty("webPort", Integer.toString(i));
/*  408 */       this.server.setPort(i);
/*  409 */       boolean bool1 = Utils.parseBoolean((String)this.attributes.get("allowOthers"), false, false);
/*  410 */       sortedProperties.setProperty("webAllowOthers", String.valueOf(bool1));
/*  411 */       this.server.setAllowOthers(bool1);
/*  412 */       String str = (String)this.attributes.get("webExternalNames");
/*  413 */       sortedProperties.setProperty("webExternalNames", str);
/*  414 */       this.server.setExternalNames(str);
/*  415 */       boolean bool2 = Utils.parseBoolean((String)this.attributes.get("ssl"), false, false);
/*  416 */       sortedProperties.setProperty("webSSL", String.valueOf(bool2));
/*  417 */       this.server.setSSL(bool2);
/*  418 */       byte[] arrayOfByte = this.server.getAdminPassword();
/*  419 */       if (arrayOfByte != null) {
/*  420 */         sortedProperties.setProperty("webAdminPassword", StringUtils.convertBytesToHex(arrayOfByte));
/*      */       }
/*  422 */       this.server.saveProperties((Properties)sortedProperties);
/*  423 */     } catch (Exception exception) {
/*  424 */       trace(exception.toString());
/*      */     } 
/*  426 */     return admin();
/*      */   }
/*      */   private String tools() {
/*      */     try {
/*      */       CreateCluster createCluster;
/*  431 */       String str1 = (String)this.attributes.get("tool");
/*  432 */       this.session.put("tool", str1);
/*  433 */       String str2 = (String)this.attributes.get("args");
/*  434 */       String[] arrayOfString = StringUtils.arraySplit(str2, ',', false);
/*  435 */       Backup backup = null;
/*  436 */       if ("Backup".equals(str1)) {
/*  437 */         backup = new Backup();
/*  438 */       } else if ("Restore".equals(str1)) {
/*  439 */         Restore restore = new Restore();
/*  440 */       } else if ("Recover".equals(str1)) {
/*  441 */         Recover recover = new Recover();
/*  442 */       } else if ("DeleteDbFiles".equals(str1)) {
/*  443 */         DeleteDbFiles deleteDbFiles = new DeleteDbFiles();
/*  444 */       } else if ("ChangeFileEncryption".equals(str1)) {
/*  445 */         ChangeFileEncryption changeFileEncryption = new ChangeFileEncryption();
/*  446 */       } else if ("Script".equals(str1)) {
/*  447 */         Script script = new Script();
/*  448 */       } else if ("RunScript".equals(str1)) {
/*  449 */         RunScript runScript = new RunScript();
/*  450 */       } else if ("ConvertTraceFile".equals(str1)) {
/*  451 */         ConvertTraceFile convertTraceFile = new ConvertTraceFile();
/*  452 */       } else if ("CreateCluster".equals(str1)) {
/*  453 */         createCluster = new CreateCluster();
/*      */       } else {
/*  455 */         throw DbException.getInternalError(str1);
/*      */       } 
/*  457 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*  458 */       PrintStream printStream = new PrintStream(byteArrayOutputStream, false, "UTF-8");
/*  459 */       createCluster.setOut(printStream);
/*      */       try {
/*  461 */         createCluster.runTool(arrayOfString);
/*  462 */         printStream.flush();
/*  463 */         String str3 = Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8);
/*  464 */         String str4 = PageParser.escapeHtml(str3);
/*  465 */         this.session.put("toolResult", str4);
/*  466 */       } catch (Exception exception) {
/*  467 */         this.session.put("toolResult", getStackTrace(0, exception, true));
/*      */       } 
/*  469 */     } catch (Exception exception) {
/*  470 */       this.server.traceError(exception);
/*      */     } 
/*  472 */     return "tools.jsp";
/*      */   }
/*      */   
/*      */   private String adminStartTranslate() {
/*  476 */     Map<Object, Object> map1 = Map.class.cast(this.session.map.get("text"));
/*      */     
/*  478 */     Map<Object, Object> map2 = map1;
/*  479 */     String str = this.server.startTranslate(map2);
/*  480 */     this.session.put("translationFile", str);
/*  481 */     return "helpTranslate.jsp";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String adminShutdown() {
/*  490 */     this.server.shutdown();
/*  491 */     return "admin.jsp";
/*      */   }
/*      */   
/*      */   private String index() {
/*  495 */     String[][] arrayOfString = WebServer.LANGUAGES;
/*  496 */     String str1 = (String)this.attributes.get("language");
/*  497 */     Locale locale = this.session.locale;
/*  498 */     if (str1 != null) {
/*  499 */       if (locale == null || 
/*  500 */         !StringUtils.toLowerEnglish(locale.getLanguage()).equals(str1)) {
/*  501 */         locale = new Locale(str1, "");
/*  502 */         this.server.readTranslations(this.session, locale.getLanguage());
/*  503 */         this.session.put("language", str1);
/*  504 */         this.session.locale = locale;
/*      */       } 
/*      */     } else {
/*  507 */       str1 = (String)this.session.get("language");
/*      */     } 
/*  509 */     if (str1 == null)
/*      */     {
/*      */       
/*  512 */       str1 = this.headerLanguage;
/*      */     }
/*  514 */     this.session.put("languageCombo", getComboBox(arrayOfString, str1));
/*  515 */     String[] arrayOfString1 = this.server.getSettingNames();
/*  516 */     String str2 = this.attributes.getProperty("setting");
/*  517 */     if (str2 == null && arrayOfString1.length > 0) {
/*  518 */       str2 = arrayOfString1[0];
/*      */     }
/*  520 */     String str3 = getComboBox(arrayOfString1, str2);
/*  521 */     this.session.put("settingsList", str3);
/*  522 */     ConnectionInfo connectionInfo = this.server.getSetting(str2);
/*  523 */     if (connectionInfo == null) {
/*  524 */       connectionInfo = new ConnectionInfo();
/*      */     }
/*  526 */     this.session.put("setting", PageParser.escapeHtmlData(str2));
/*  527 */     this.session.put("name", PageParser.escapeHtmlData(str2));
/*  528 */     this.session.put("driver", PageParser.escapeHtmlData(connectionInfo.driver));
/*  529 */     this.session.put("url", PageParser.escapeHtmlData(connectionInfo.url));
/*  530 */     this.session.put("user", PageParser.escapeHtmlData(connectionInfo.user));
/*  531 */     return "index.jsp";
/*      */   }
/*      */   
/*      */   private String getHistory() {
/*  535 */     int i = Integer.parseInt(this.attributes.getProperty("id"));
/*  536 */     String str = this.session.getCommand(i);
/*  537 */     this.session.put("query", PageParser.escapeHtmlData(str));
/*  538 */     return "query.jsp";
/*      */   }
/*      */ 
/*      */   
/*      */   private static int addColumns(boolean paramBoolean1, DbTableOrView paramDbTableOrView, StringBuilder paramStringBuilder1, int paramInt, boolean paramBoolean2, StringBuilder paramStringBuilder2) {
/*  543 */     DbColumn[] arrayOfDbColumn = paramDbTableOrView.getColumns();
/*  544 */     for (byte b = 0; arrayOfDbColumn != null && b < arrayOfDbColumn.length; b++) {
/*  545 */       DbColumn dbColumn = arrayOfDbColumn[b];
/*  546 */       if (paramStringBuilder2.length() > 0) {
/*  547 */         paramStringBuilder2.append(' ');
/*      */       }
/*  549 */       paramStringBuilder2.append(dbColumn.getName());
/*  550 */       String str1 = escapeIdentifier(dbColumn.getName());
/*  551 */       String str2 = paramBoolean1 ? ", 1, 1" : ", 2, 2";
/*  552 */       paramStringBuilder1.append("setNode(").append(paramInt).append(str2)
/*  553 */         .append(", 'column', '")
/*  554 */         .append(PageParser.escapeJavaScript(dbColumn.getName()))
/*  555 */         .append("', 'javascript:ins(\\'").append(str1).append("\\')');\n");
/*  556 */       paramInt++;
/*  557 */       if (paramBoolean1 && paramBoolean2) {
/*  558 */         paramStringBuilder1.append("setNode(").append(paramInt)
/*  559 */           .append(", 2, 2, 'type', '")
/*  560 */           .append(PageParser.escapeJavaScript(dbColumn.getDataType()))
/*  561 */           .append("', null);\n");
/*  562 */         paramInt++;
/*      */       } 
/*      */     } 
/*  565 */     return paramInt;
/*      */   }
/*      */   
/*      */   private static String escapeIdentifier(String paramString) {
/*  569 */     return StringUtils.urlEncode(
/*  570 */         PageParser.escapeJavaScript(paramString)).replace('+', ' ');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class IndexInfo
/*      */   {
/*      */     String name;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String type;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String columns;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int addIndexes(boolean paramBoolean, DatabaseMetaData paramDatabaseMetaData, String paramString1, String paramString2, StringBuilder paramStringBuilder, int paramInt) throws SQLException {
/*      */     ResultSet resultSet;
/*      */     try {
/*  599 */       resultSet = paramDatabaseMetaData.getIndexInfo(null, paramString2, paramString1, false, true);
/*  600 */     } catch (SQLException sQLException) {
/*      */       
/*  602 */       return paramInt;
/*      */     } 
/*  604 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  605 */     while (resultSet.next()) {
/*  606 */       String str = resultSet.getString("INDEX_NAME");
/*  607 */       IndexInfo indexInfo = (IndexInfo)hashMap.get(str);
/*  608 */       if (indexInfo == null) {
/*  609 */         String str1; int i = resultSet.getInt("TYPE");
/*      */         
/*  611 */         if (i == 1) {
/*  612 */           str1 = "";
/*  613 */         } else if (i == 2) {
/*  614 */           str1 = " (${text.tree.hashed})";
/*  615 */         } else if (i == 3) {
/*  616 */           str1 = "";
/*      */         } else {
/*  618 */           str1 = null;
/*      */         } 
/*  620 */         if (str != null && str1 != null) {
/*  621 */           indexInfo = new IndexInfo();
/*  622 */           indexInfo.name = str;
/*  623 */           str1 = (resultSet.getBoolean("NON_UNIQUE") ? "${text.tree.nonUnique}" : "${text.tree.unique}") + str1;
/*      */           
/*  625 */           indexInfo.type = str1;
/*  626 */           indexInfo.columns = resultSet.getString("COLUMN_NAME");
/*  627 */           hashMap.put(str, indexInfo);
/*      */         }  continue;
/*      */       } 
/*  630 */       indexInfo.columns += ", " + resultSet.getString("COLUMN_NAME");
/*      */     } 
/*      */     
/*  633 */     resultSet.close();
/*  634 */     if (hashMap.size() > 0) {
/*  635 */       String str1 = paramBoolean ? ", 1, 1" : ", 2, 1";
/*  636 */       String str2 = paramBoolean ? ", 2, 1" : ", 3, 1";
/*  637 */       String str3 = paramBoolean ? ", 3, 2" : ", 4, 2";
/*  638 */       paramStringBuilder.append("setNode(").append(paramInt).append(str1)
/*  639 */         .append(", 'index_az', '${text.tree.indexes}', null);\n");
/*  640 */       paramInt++;
/*  641 */       for (IndexInfo indexInfo : hashMap.values()) {
/*  642 */         paramStringBuilder.append("setNode(").append(paramInt).append(str2)
/*  643 */           .append(", 'index', '")
/*  644 */           .append(PageParser.escapeJavaScript(indexInfo.name))
/*  645 */           .append("', null);\n");
/*  646 */         paramInt++;
/*  647 */         paramStringBuilder.append("setNode(").append(paramInt).append(str3)
/*  648 */           .append(", 'type', '").append(indexInfo.type).append("', null);\n");
/*  649 */         paramInt++;
/*  650 */         paramStringBuilder.append("setNode(").append(paramInt).append(str3)
/*  651 */           .append(", 'type', '")
/*  652 */           .append(PageParser.escapeJavaScript(indexInfo.columns))
/*  653 */           .append("', null);\n");
/*  654 */         paramInt++;
/*      */       } 
/*      */     } 
/*  657 */     return paramInt;
/*      */   }
/*      */ 
/*      */   
/*      */   private int addTablesAndViews(DbSchema paramDbSchema, boolean paramBoolean, StringBuilder paramStringBuilder, int paramInt) throws SQLException {
/*  662 */     if (paramDbSchema == null) {
/*  663 */       return paramInt;
/*      */     }
/*  665 */     Connection connection = this.session.getConnection();
/*  666 */     DatabaseMetaData databaseMetaData = this.session.getMetaData();
/*  667 */     byte b = paramBoolean ? 0 : 1;
/*  668 */     boolean bool1 = (paramBoolean || !paramDbSchema.isSystem) ? true : false;
/*  669 */     String str1 = ", " + b + ", " + (bool1 ? "1" : "2") + ", ";
/*  670 */     String str2 = ", " + (b + 1) + ", 2, ";
/*  671 */     DbTableOrView[] arrayOfDbTableOrView = paramDbSchema.getTables();
/*  672 */     if (arrayOfDbTableOrView == null) {
/*  673 */       return paramInt;
/*      */     }
/*  675 */     DbContents dbContents = paramDbSchema.getContents();
/*  676 */     boolean bool = dbContents.isOracle();
/*  677 */     boolean bool2 = (arrayOfDbTableOrView.length < SysProperties.CONSOLE_MAX_TABLES_LIST_INDEXES) ? true : false;
/*  678 */     try (PreparedStatement null = bool1 ? prepareViewDefinitionQuery(connection, dbContents) : null) {
/*  679 */       if (preparedStatement != null) {
/*  680 */         preparedStatement.setString(1, paramDbSchema.name);
/*      */       }
/*  682 */       if (paramDbSchema.isSystem) {
/*  683 */         Arrays.sort(arrayOfDbTableOrView, SYSTEM_SCHEMA_COMPARATOR);
/*  684 */         for (DbTableOrView dbTableOrView : arrayOfDbTableOrView) {
/*  685 */           paramInt = addTableOrView(paramDbSchema, paramBoolean, paramStringBuilder, paramInt, databaseMetaData, false, str1, bool, bool2, dbTableOrView, dbTableOrView
/*  686 */               .isView(), preparedStatement, str2);
/*      */         }
/*      */       } else {
/*  689 */         for (DbTableOrView dbTableOrView : arrayOfDbTableOrView) {
/*  690 */           if (!dbTableOrView.isView())
/*      */           {
/*      */             
/*  693 */             paramInt = addTableOrView(paramDbSchema, paramBoolean, paramStringBuilder, paramInt, databaseMetaData, bool1, str1, bool, bool2, dbTableOrView, false, null, str2);
/*      */           }
/*      */         } 
/*  696 */         for (DbTableOrView dbTableOrView : arrayOfDbTableOrView) {
/*  697 */           if (dbTableOrView.isView())
/*      */           {
/*      */             
/*  700 */             paramInt = addTableOrView(paramDbSchema, paramBoolean, paramStringBuilder, paramInt, databaseMetaData, bool1, str1, bool, bool2, dbTableOrView, true, preparedStatement, str2);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  705 */     return paramInt;
/*      */   }
/*      */   
/*      */   private static PreparedStatement prepareViewDefinitionQuery(Connection paramConnection, DbContents paramDbContents) {
/*  709 */     if (paramDbContents.mayHaveStandardViews()) {
/*      */       try {
/*  711 */         return paramConnection.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
/*      */       }
/*  713 */       catch (SQLException sQLException) {
/*  714 */         paramDbContents.setMayHaveStandardViews(false);
/*      */       } 
/*      */     }
/*  717 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int addTableOrView(DbSchema paramDbSchema, boolean paramBoolean1, StringBuilder paramStringBuilder, int paramInt, DatabaseMetaData paramDatabaseMetaData, boolean paramBoolean2, String paramString1, boolean paramBoolean3, boolean paramBoolean4, DbTableOrView paramDbTableOrView, boolean paramBoolean5, PreparedStatement paramPreparedStatement, String paramString2) throws SQLException {
/*  723 */     int i = paramInt;
/*  724 */     String str = paramDbTableOrView.getQuotedName();
/*  725 */     if (!paramBoolean1) {
/*  726 */       str = paramDbSchema.quotedName + '.' + str;
/*      */     }
/*  728 */     str = escapeIdentifier(str);
/*  729 */     paramStringBuilder.append("setNode(").append(paramInt).append(paramString1)
/*  730 */       .append(" '").append(paramBoolean5 ? "view" : "table").append("', '")
/*  731 */       .append(PageParser.escapeJavaScript(paramDbTableOrView.getName()))
/*  732 */       .append("', 'javascript:ins(\\'").append(str).append("\\',true)');\n");
/*  733 */     paramInt++;
/*  734 */     if (paramBoolean2) {
/*  735 */       StringBuilder stringBuilder = new StringBuilder();
/*  736 */       paramInt = addColumns(paramBoolean1, paramDbTableOrView, paramStringBuilder, paramInt, paramBoolean4, stringBuilder);
/*  737 */       if (paramBoolean5) {
/*  738 */         if (paramPreparedStatement != null) {
/*  739 */           paramPreparedStatement.setString(2, paramDbTableOrView.getName());
/*  740 */           try (ResultSet null = paramPreparedStatement.executeQuery()) {
/*  741 */             if (resultSet.next()) {
/*  742 */               String str1 = resultSet.getString(1);
/*  743 */               if (str1 != null) {
/*  744 */                 paramStringBuilder.append("setNode(").append(paramInt).append(paramString2).append(" 'type', '")
/*  745 */                   .append(PageParser.escapeJavaScript(str1)).append("', null);\n");
/*  746 */                 paramInt++;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*  751 */       } else if (!paramBoolean3 && paramBoolean4) {
/*  752 */         paramInt = addIndexes(paramBoolean1, paramDatabaseMetaData, paramDbTableOrView.getName(), paramDbSchema.name, paramStringBuilder, paramInt);
/*      */       } 
/*  754 */       paramStringBuilder.append("addTable('")
/*  755 */         .append(PageParser.escapeJavaScript(paramDbTableOrView.getName())).append("', '")
/*  756 */         .append(PageParser.escapeJavaScript(stringBuilder.toString())).append("', ")
/*  757 */         .append(i).append(");\n");
/*      */     } 
/*  759 */     return paramInt;
/*      */   }
/*      */   
/*      */   private String tables() {
/*  763 */     DbContents dbContents = this.session.getContents();
/*  764 */     boolean bool = false;
/*      */     try {
/*  766 */       String str1 = (String)this.session.get("url");
/*  767 */       Connection connection = this.session.getConnection();
/*  768 */       dbContents.readContents(str1, connection);
/*  769 */       this.session.loadBnf();
/*  770 */       bool = dbContents.isH2();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  775 */       StringBuilder stringBuilder = (new StringBuilder()).append("setNode(0, 0, 0, 'database', '").append(PageParser.escapeJavaScript(str1)).append("', null);\n");
/*  776 */       int i = 1;
/*      */       
/*  778 */       DbSchema dbSchema = dbContents.getDefaultSchema();
/*  779 */       i = addTablesAndViews(dbSchema, true, stringBuilder, i);
/*  780 */       DbSchema[] arrayOfDbSchema = dbContents.getSchemas();
/*  781 */       for (DbSchema dbSchema1 : arrayOfDbSchema) {
/*  782 */         if (dbSchema1 != dbSchema && dbSchema1 != null) {
/*      */ 
/*      */           
/*  785 */           stringBuilder.append("setNode(").append(i).append(", 0, 1, 'folder', '")
/*  786 */             .append(PageParser.escapeJavaScript(dbSchema1.name))
/*  787 */             .append("', null);\n");
/*  788 */           i++;
/*  789 */           i = addTablesAndViews(dbSchema1, false, stringBuilder, i);
/*      */         } 
/*  791 */       }  if (bool) {
/*  792 */         try (Statement null = connection.createStatement()) {
/*      */           ResultSet resultSet;
/*      */           try {
/*  795 */             resultSet = statement.executeQuery("SELECT SEQUENCE_NAME, BASE_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
/*      */           }
/*  797 */           catch (SQLException sQLException) {
/*  798 */             resultSet = statement.executeQuery("SELECT SEQUENCE_NAME, CURRENT_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
/*      */           } 
/*      */           byte b;
/*  801 */           for (b = 0; resultSet.next(); b++) {
/*  802 */             if (!b) {
/*  803 */               stringBuilder.append("setNode(").append(i)
/*  804 */                 .append(", 0, 1, 'sequences', '${text.tree.sequences}', null);\n");
/*  805 */               i++;
/*      */             } 
/*  807 */             String str3 = resultSet.getString(1);
/*  808 */             String str4 = resultSet.getString(2);
/*  809 */             String str5 = resultSet.getString(3);
/*  810 */             stringBuilder.append("setNode(").append(i)
/*  811 */               .append(", 1, 1, 'sequence', '")
/*  812 */               .append(PageParser.escapeJavaScript(str3))
/*  813 */               .append("', null);\n");
/*  814 */             i++;
/*  815 */             stringBuilder.append("setNode(").append(i)
/*  816 */               .append(", 2, 2, 'type', '${text.tree.current}: ")
/*  817 */               .append(PageParser.escapeJavaScript(str4))
/*  818 */               .append("', null);\n");
/*  819 */             i++;
/*  820 */             if (!"1".equals(str5)) {
/*  821 */               stringBuilder.append("setNode(").append(i)
/*  822 */                 .append(", 2, 2, 'type', '${text.tree.increment}: ")
/*  823 */                 .append(PageParser.escapeJavaScript(str5))
/*  824 */                 .append("', null);\n");
/*  825 */               i++;
/*      */             } 
/*      */           } 
/*  828 */           resultSet.close();
/*      */           try {
/*  830 */             resultSet = statement.executeQuery("SELECT USER_NAME, IS_ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY USER_NAME");
/*      */           }
/*  832 */           catch (SQLException sQLException) {
/*  833 */             resultSet = statement.executeQuery("SELECT NAME, ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY NAME");
/*      */           } 
/*  835 */           for (b = 0; resultSet.next(); b++) {
/*  836 */             if (b == 0) {
/*  837 */               stringBuilder.append("setNode(").append(i)
/*  838 */                 .append(", 0, 1, 'users', '${text.tree.users}', null);\n");
/*  839 */               i++;
/*      */             } 
/*  841 */             String str3 = resultSet.getString(1);
/*  842 */             String str4 = resultSet.getString(2);
/*  843 */             stringBuilder.append("setNode(").append(i)
/*  844 */               .append(", 1, 1, 'user', '")
/*  845 */               .append(PageParser.escapeJavaScript(str3))
/*  846 */               .append("', null);\n");
/*  847 */             i++;
/*  848 */             if (str4.equalsIgnoreCase("TRUE")) {
/*  849 */               stringBuilder.append("setNode(").append(i)
/*  850 */                 .append(", 2, 2, 'type', '${text.tree.admin}', null);\n");
/*  851 */               i++;
/*      */             } 
/*      */           } 
/*  854 */           resultSet.close();
/*      */         } 
/*      */       }
/*  857 */       DatabaseMetaData databaseMetaData = this.session.getMetaData();
/*      */       
/*  859 */       String str2 = databaseMetaData.getDatabaseProductName() + " " + databaseMetaData.getDatabaseProductVersion();
/*  860 */       stringBuilder.append("setNode(").append(i)
/*  861 */         .append(", 0, 0, 'info', '")
/*  862 */         .append(PageParser.escapeJavaScript(str2))
/*  863 */         .append("', null);\n")
/*  864 */         .append("refreshQueryTables();");
/*  865 */       this.session.put("tree", stringBuilder.toString());
/*  866 */     } catch (Exception exception) {
/*  867 */       this.session.put("tree", "");
/*  868 */       this.session.put("error", getStackTrace(0, exception, bool));
/*      */     } 
/*  870 */     return "tables.jsp";
/*      */   }
/*      */   
/*      */   private String getStackTrace(int paramInt, Throwable paramThrowable, boolean paramBoolean) {
/*      */     try {
/*  875 */       StringWriter stringWriter = new StringWriter();
/*  876 */       paramThrowable.printStackTrace(new PrintWriter(stringWriter));
/*  877 */       String str1 = stringWriter.toString();
/*  878 */       str1 = PageParser.escapeHtml(str1);
/*  879 */       if (paramBoolean) {
/*  880 */         str1 = linkToSource(str1);
/*      */       }
/*  882 */       str1 = StringUtils.replaceAll(str1, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
/*      */       
/*  884 */       String str2 = PageParser.escapeHtml(paramThrowable.getMessage());
/*  885 */       String str3 = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('st" + paramInt + "').style;x.display=x.display==''?'none':'';\">" + str2 + "</a>";
/*      */ 
/*      */ 
/*      */       
/*  889 */       if (paramThrowable instanceof SQLException) {
/*  890 */         SQLException sQLException = (SQLException)paramThrowable;
/*  891 */         str3 = str3 + " " + sQLException.getSQLState() + "/" + sQLException.getErrorCode();
/*  892 */         if (paramBoolean) {
/*  893 */           int i = sQLException.getErrorCode();
/*  894 */           str3 = str3 + " <a href=\"https://h2database.com/javadoc/org/h2/api/ErrorCode.html#c" + i + "\">(${text.a.help})</a>";
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  899 */       str3 = str3 + "<span style=\"display: none;\" id=\"st" + paramInt + "\"><br />" + str1 + "</span>";
/*      */       
/*  901 */       str3 = formatAsError(str3);
/*  902 */       return str3;
/*  903 */     } catch (OutOfMemoryError outOfMemoryError) {
/*  904 */       this.server.traceError(paramThrowable);
/*  905 */       return paramThrowable.toString();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String linkToSource(String paramString) {
/*      */     try {
/*  911 */       StringBuilder stringBuilder = new StringBuilder(paramString.length());
/*  912 */       int i = paramString.indexOf("<br />");
/*  913 */       stringBuilder.append(paramString, 0, i);
/*      */       while (true) {
/*  915 */         int j = paramString.indexOf("org.h2.", i);
/*  916 */         if (j < 0) {
/*  917 */           stringBuilder.append(paramString.substring(i));
/*      */           break;
/*      */         } 
/*  920 */         stringBuilder.append(paramString, i, j);
/*  921 */         int k = paramString.indexOf(')', j);
/*  922 */         if (k < 0) {
/*  923 */           stringBuilder.append(paramString.substring(i));
/*      */           break;
/*      */         } 
/*  926 */         String str1 = paramString.substring(j, k);
/*  927 */         int m = str1.lastIndexOf('(');
/*  928 */         int n = str1.lastIndexOf('.', m - 1);
/*  929 */         int i1 = str1.lastIndexOf('.', n - 1);
/*  930 */         String str2 = str1.substring(0, i1);
/*  931 */         int i2 = str1.lastIndexOf(':');
/*  932 */         String str3 = str1.substring(m + 1, i2);
/*  933 */         String str4 = str1.substring(i2 + 1, str1.length());
/*  934 */         String str5 = str2.replace('.', '/') + "/" + str3;
/*  935 */         stringBuilder.append("<a href=\"https://h2database.com/html/source.html?file=");
/*  936 */         stringBuilder.append(str5);
/*  937 */         stringBuilder.append("&line=");
/*  938 */         stringBuilder.append(str4);
/*  939 */         stringBuilder.append("&build=");
/*  940 */         stringBuilder.append(210);
/*  941 */         stringBuilder.append("\">");
/*  942 */         stringBuilder.append(str1);
/*  943 */         stringBuilder.append("</a>");
/*  944 */         i = k;
/*      */       } 
/*  946 */       return stringBuilder.toString();
/*  947 */     } catch (Throwable throwable) {
/*  948 */       return paramString;
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String formatAsError(String paramString) {
/*  953 */     return "<div class=\"error\">" + paramString + "</div>";
/*      */   }
/*      */   
/*      */   private String test(NetworkConnectionInfo paramNetworkConnectionInfo) {
/*  957 */     String str1 = this.attributes.getProperty("driver", "");
/*  958 */     String str2 = this.attributes.getProperty("url", "");
/*  959 */     String str3 = this.attributes.getProperty("user", "");
/*  960 */     String str4 = this.attributes.getProperty("password", "");
/*  961 */     this.session.put("driver", str1);
/*  962 */     this.session.put("url", str2);
/*  963 */     this.session.put("user", str3);
/*  964 */     boolean bool = str2.startsWith("jdbc:h2:"); try {
/*      */       Connection connection; String str7;
/*  966 */       long l1 = System.currentTimeMillis();
/*  967 */       String str5 = "", str6 = "";
/*  968 */       Profiler profiler = new Profiler();
/*  969 */       profiler.startCollecting();
/*      */       
/*      */       try {
/*  972 */         connection = this.server.getConnection(str1, str2, str3, str4, null, paramNetworkConnectionInfo);
/*      */       } finally {
/*  974 */         profiler.stopCollecting();
/*  975 */         str5 = profiler.getTop(3);
/*      */       } 
/*  977 */       profiler = new Profiler();
/*  978 */       profiler.startCollecting();
/*      */       try {
/*  980 */         JdbcUtils.closeSilently(connection);
/*      */       } finally {
/*  982 */         profiler.stopCollecting();
/*  983 */         str6 = profiler.getTop(3);
/*      */       } 
/*  985 */       long l2 = System.currentTimeMillis() - l1;
/*      */       
/*  987 */       if (l2 > 1000L) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  995 */         str7 = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('prof').style;x.display=x.display==''?'none':'';\">${text.login.testSuccessful}</a><span style=\"display: none;\" id=\"prof\"><br />" + PageParser.escapeHtml(str5) + "<br />" + PageParser.escapeHtml(str6) + "</span>";
/*      */       } else {
/*      */         
/*  998 */         str7 = "<div class=\"success\">${text.login.testSuccessful}</div>";
/*      */       } 
/* 1000 */       this.session.put("error", str7);
/*      */       
/* 1002 */       return "login.jsp";
/* 1003 */     } catch (Exception exception) {
/* 1004 */       this.session.put("error", getLoginError(exception, bool));
/* 1005 */       return "login.jsp";
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String getLoginError(Exception paramException, boolean paramBoolean) {
/* 1017 */     if (paramException instanceof JdbcException && ((JdbcException)paramException).getErrorCode() == 90086) {
/* 1018 */       return "${text.login.driverNotFound}<br />" + getStackTrace(0, paramException, paramBoolean);
/*      */     }
/* 1020 */     return getStackTrace(0, paramException, paramBoolean);
/*      */   }
/*      */   
/*      */   private String login(NetworkConnectionInfo paramNetworkConnectionInfo) {
/* 1024 */     String str1 = this.attributes.getProperty("driver", "");
/* 1025 */     String str2 = this.attributes.getProperty("url", "");
/* 1026 */     String str3 = this.attributes.getProperty("user", "");
/* 1027 */     String str4 = this.attributes.getProperty("password", "");
/* 1028 */     this.session.put("autoCommit", "checked");
/* 1029 */     this.session.put("autoComplete", "1");
/* 1030 */     this.session.put("maxrows", "1000");
/* 1031 */     boolean bool = str2.startsWith("jdbc:h2:");
/*      */     try {
/* 1033 */       Connection connection = this.server.getConnection(str1, str2, str3, str4, (String)this.session.get("key"), paramNetworkConnectionInfo);
/*      */       
/* 1035 */       this.session.setConnection(connection);
/* 1036 */       this.session.put("url", str2);
/* 1037 */       this.session.put("user", str3);
/* 1038 */       this.session.remove("error");
/* 1039 */       settingSave();
/* 1040 */       return "frame.jsp";
/* 1041 */     } catch (Exception exception) {
/* 1042 */       this.session.put("error", getLoginError(exception, bool));
/* 1043 */       return "login.jsp";
/*      */     } 
/*      */   }
/*      */   
/*      */   private String logout() {
/*      */     try {
/* 1049 */       Connection connection = this.session.getConnection();
/* 1050 */       this.session.setConnection(null);
/* 1051 */       this.session.remove("conn");
/* 1052 */       this.session.remove("result");
/* 1053 */       this.session.remove("tables");
/* 1054 */       this.session.remove("user");
/* 1055 */       this.session.remove("tool");
/* 1056 */       if (connection != null) {
/* 1057 */         if (this.session.getShutdownServerOnDisconnect()) {
/* 1058 */           this.server.shutdown();
/*      */         } else {
/* 1060 */           connection.close();
/*      */         } 
/*      */       }
/* 1063 */     } catch (Exception exception) {
/* 1064 */       trace(exception.toString());
/*      */     } 
/* 1066 */     this.session.remove("admin");
/* 1067 */     return "index.do";
/*      */   }
/*      */   
/*      */   private String query() {
/* 1071 */     String str = this.attributes.getProperty("sql").trim();
/*      */     try {
/* 1073 */       ScriptReader scriptReader = new ScriptReader(new StringReader(str));
/* 1074 */       final ArrayList<String> list = new ArrayList();
/*      */       while (true) {
/* 1076 */         String str2 = scriptReader.readStatement();
/* 1077 */         if (str2 == null) {
/*      */           break;
/*      */         }
/* 1080 */         arrayList.add(str2);
/*      */       } 
/* 1082 */       final Connection conn = this.session.getConnection();
/* 1083 */       if (SysProperties.CONSOLE_STREAM && this.server.getAllowChunked()) {
/* 1084 */         String str2 = new String(this.server.getFile("result.jsp"), StandardCharsets.UTF_8);
/* 1085 */         int i = str2.indexOf("${result}");
/*      */ 
/*      */         
/* 1088 */         arrayList.add(0, str2.substring(0, i));
/* 1089 */         arrayList.add(str2.substring(i + "${result}".length()));
/* 1090 */         this.session.put("chunks", new Iterator<String>() {
/*      */               private int i;
/*      */               
/*      */               public boolean hasNext() {
/* 1094 */                 return (this.i < list.size());
/*      */               }
/*      */               
/*      */               public String next() {
/* 1098 */                 String str = list.get(this.i++);
/* 1099 */                 if (this.i == 1 || this.i == list.size()) {
/* 1100 */                   return str;
/*      */                 }
/* 1102 */                 StringBuilder stringBuilder = new StringBuilder();
/* 1103 */                 WebApp.this.query(conn, str, this.i - 1, list.size() - 2, stringBuilder);
/* 1104 */                 return stringBuilder.toString();
/*      */               }
/*      */             });
/* 1107 */         return "result.jsp";
/*      */       } 
/*      */       
/* 1110 */       StringBuilder stringBuilder = new StringBuilder();
/* 1111 */       for (byte b = 0; b < arrayList.size(); b++) {
/* 1112 */         String str2 = arrayList.get(b);
/* 1113 */         query(connection, str2, b, arrayList.size(), stringBuilder);
/*      */       } 
/* 1115 */       String str1 = stringBuilder.toString();
/* 1116 */       this.session.put("result", str1);
/* 1117 */     } catch (Throwable throwable) {
/* 1118 */       this.session.put("result", getStackTrace(0, throwable, this.session.getContents().isH2()));
/*      */     } 
/* 1120 */     return "result.jsp";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void query(Connection paramConnection, String paramString, int paramInt1, int paramInt2, StringBuilder paramStringBuilder) {
/* 1133 */     if (!paramString.startsWith("@") || !paramString.endsWith(".")) {
/* 1134 */       paramStringBuilder.append(PageParser.escapeHtml(paramString + ";")).append("<br />");
/*      */     }
/* 1136 */     boolean bool = paramString.startsWith("@edit");
/* 1137 */     paramStringBuilder.append(getResult(paramConnection, paramInt1 + 1, paramString, (paramInt2 == 1), bool))
/* 1138 */       .append("<br />");
/*      */   }
/*      */   
/*      */   private String editResult() {
/* 1142 */     ResultSet resultSet = this.session.result;
/* 1143 */     int i = Integer.parseInt(this.attributes.getProperty("row"));
/* 1144 */     int j = Integer.parseInt(this.attributes.getProperty("op"));
/* 1145 */     String str1 = "", str2 = "";
/*      */     try {
/* 1147 */       if (j == 1) {
/* 1148 */         boolean bool = (i < 0) ? true : false;
/* 1149 */         if (bool) {
/* 1150 */           resultSet.moveToInsertRow();
/*      */         } else {
/* 1152 */           resultSet.absolute(i);
/*      */         } 
/* 1154 */         for (byte b = 0; b < resultSet.getMetaData().getColumnCount(); b++) {
/* 1155 */           String str = this.attributes.getProperty("r" + i + "c" + (b + 1));
/* 1156 */           unescapeData(str, resultSet, b + 1);
/*      */         } 
/* 1158 */         if (bool) {
/* 1159 */           resultSet.insertRow();
/*      */         } else {
/* 1161 */           resultSet.updateRow();
/*      */         } 
/* 1163 */       } else if (j == 2) {
/* 1164 */         resultSet.absolute(i);
/* 1165 */         resultSet.deleteRow();
/* 1166 */       } else if (j == 3) {
/*      */       
/*      */       } 
/* 1169 */     } catch (Throwable throwable) {
/* 1170 */       str1 = "<br />" + getStackTrace(0, throwable, this.session.getContents().isH2());
/* 1171 */       str2 = formatAsError(throwable.getMessage());
/*      */     } 
/* 1173 */     String str3 = "@edit " + (String)this.session.get("resultSetSQL");
/* 1174 */     Connection connection = this.session.getConnection();
/* 1175 */     str1 = str2 + getResult(connection, -1, str3, true, true) + str1;
/* 1176 */     this.session.put("result", str1);
/* 1177 */     return "result.jsp";
/*      */   }
/*      */   
/*      */   private int getMaxrows() {
/* 1181 */     String str = (String)this.session.get("maxrows");
/* 1182 */     return (str == null) ? 0 : Integer.parseInt(str);
/*      */   }
/*      */   private String getResult(Connection paramConnection, int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     try {
/*      */       Statement statement;
/*      */       ResultSet resultSet;
/* 1188 */       paramString = paramString.trim();
/* 1189 */       StringBuilder stringBuilder = new StringBuilder();
/* 1190 */       String str = StringUtils.toUpperEnglish(paramString);
/* 1191 */       if (str.contains("CREATE") || str
/* 1192 */         .contains("DROP") || str
/* 1193 */         .contains("ALTER") || str
/* 1194 */         .contains("RUNSCRIPT")) {
/* 1195 */         String str1 = this.attributes.getProperty("jsessionid");
/* 1196 */         stringBuilder.append("<script type=\"text/javascript\">parent['h2menu'].location='tables.do?jsessionid=")
/* 1197 */           .append(str1).append("';</script>");
/*      */       } 
/*      */       
/* 1200 */       DbContents dbContents = this.session.getContents();
/* 1201 */       if (paramBoolean2 || (paramBoolean1 && dbContents.isH2())) {
/* 1202 */         statement = paramConnection.createStatement(1004, 1008);
/*      */       }
/*      */       else {
/*      */         
/* 1206 */         statement = paramConnection.createStatement();
/*      */       } 
/*      */       
/* 1209 */       long l = System.currentTimeMillis();
/* 1210 */       boolean bool1 = false;
/* 1211 */       Object object = null;
/* 1212 */       boolean bool2 = false;
/* 1213 */       boolean bool3 = false;
/* 1214 */       if (JdbcUtils.isBuiltIn(paramString, "@autocommit_true")) {
/* 1215 */         paramConnection.setAutoCommit(true);
/* 1216 */         return "${text.result.autoCommitOn}";
/* 1217 */       }  if (JdbcUtils.isBuiltIn(paramString, "@autocommit_false")) {
/* 1218 */         paramConnection.setAutoCommit(false);
/* 1219 */         return "${text.result.autoCommitOff}";
/* 1220 */       }  if (JdbcUtils.isBuiltIn(paramString, "@cancel")) {
/* 1221 */         statement = this.session.executingStatement;
/* 1222 */         if (statement != null) {
/* 1223 */           statement.cancel();
/* 1224 */           stringBuilder.append("${text.result.statementWasCanceled}");
/*      */         } else {
/* 1226 */           stringBuilder.append("${text.result.noRunningStatement}");
/*      */         } 
/* 1228 */         return stringBuilder.toString();
/* 1229 */       }  if (JdbcUtils.isBuiltIn(paramString, "@edit")) {
/* 1230 */         bool2 = true;
/* 1231 */         paramString = StringUtils.trimSubstring(paramString, "@edit".length());
/* 1232 */         this.session.put("resultSetSQL", paramString);
/*      */       } 
/* 1234 */       if (JdbcUtils.isBuiltIn(paramString, "@list")) {
/* 1235 */         bool3 = true;
/* 1236 */         paramString = StringUtils.trimSubstring(paramString, "@list".length());
/*      */       } 
/* 1238 */       if (JdbcUtils.isBuiltIn(paramString, "@meta")) {
/* 1239 */         bool1 = true;
/* 1240 */         paramString = StringUtils.trimSubstring(paramString, "@meta".length());
/*      */       } 
/* 1242 */       if (JdbcUtils.isBuiltIn(paramString, "@generated"))
/* 1243 */       { object = Boolean.valueOf(true);
/* 1244 */         int i = "@generated".length();
/* 1245 */         int j = paramString.length();
/* 1246 */         for (; i < j; i++) {
/* 1247 */           char c = paramString.charAt(i);
/* 1248 */           if (c == '(') {
/* 1249 */             Parser parser = new Parser();
/* 1250 */             object = parser.parseColumnList(paramString, i);
/* 1251 */             i = parser.getLastParseIndex();
/*      */             break;
/*      */           } 
/* 1254 */           if (!Character.isWhitespace(c)) {
/*      */             break;
/*      */           }
/*      */         } 
/* 1258 */         paramString = StringUtils.trimSubstring(paramString, i); }
/* 1259 */       else { if (JdbcUtils.isBuiltIn(paramString, "@history")) {
/* 1260 */           stringBuilder.append(getCommandHistoryString());
/* 1261 */           return stringBuilder.toString();
/* 1262 */         }  if (JdbcUtils.isBuiltIn(paramString, "@loop")) {
/* 1263 */           paramString = StringUtils.trimSubstring(paramString, "@loop".length());
/* 1264 */           int i = paramString.indexOf(' ');
/* 1265 */           int j = Integer.decode(paramString.substring(0, i)).intValue();
/* 1266 */           paramString = StringUtils.trimSubstring(paramString, i);
/* 1267 */           return executeLoop(paramConnection, j, paramString);
/* 1268 */         }  if (JdbcUtils.isBuiltIn(paramString, "@maxrows")) {
/* 1269 */           int i = (int)Double.parseDouble(StringUtils.trimSubstring(paramString, "@maxrows".length()));
/* 1270 */           this.session.put("maxrows", Integer.toString(i));
/* 1271 */           return "${text.result.maxrowsSet}";
/* 1272 */         }  if (JdbcUtils.isBuiltIn(paramString, "@parameter_meta")) {
/* 1273 */           paramString = StringUtils.trimSubstring(paramString, "@parameter_meta".length());
/* 1274 */           PreparedStatement preparedStatement = paramConnection.prepareStatement(paramString);
/* 1275 */           stringBuilder.append(getParameterResultSet(preparedStatement.getParameterMetaData()));
/* 1276 */           return stringBuilder.toString();
/* 1277 */         }  if (JdbcUtils.isBuiltIn(paramString, "@password_hash")) {
/* 1278 */           paramString = StringUtils.trimSubstring(paramString, "@password_hash".length());
/* 1279 */           String[] arrayOfString = JdbcUtils.split(paramString);
/* 1280 */           return StringUtils.convertBytesToHex(
/* 1281 */               SHA256.getKeyPasswordHash(arrayOfString[0], arrayOfString[1].toCharArray()));
/* 1282 */         }  if (JdbcUtils.isBuiltIn(paramString, "@prof_start")) {
/* 1283 */           if (this.profiler != null) {
/* 1284 */             this.profiler.stopCollecting();
/*      */           }
/* 1286 */           this.profiler = new Profiler();
/* 1287 */           this.profiler.startCollecting();
/* 1288 */           return "Ok";
/* 1289 */         }  if (JdbcUtils.isBuiltIn(paramString, "@sleep")) {
/* 1290 */           String str1 = StringUtils.trimSubstring(paramString, "@sleep".length());
/* 1291 */           int i = 1;
/* 1292 */           if (str1.length() > 0) {
/* 1293 */             i = Integer.parseInt(str1);
/*      */           }
/* 1295 */           Thread.sleep((i * 1000));
/* 1296 */           return "Ok";
/* 1297 */         }  if (JdbcUtils.isBuiltIn(paramString, "@transaction_isolation")) {
/* 1298 */           String str1 = StringUtils.trimSubstring(paramString, "@transaction_isolation".length());
/* 1299 */           if (str1.length() > 0) {
/* 1300 */             int i = Integer.parseInt(str1);
/* 1301 */             paramConnection.setTransactionIsolation(i);
/*      */           } 
/* 1303 */           stringBuilder.append("Transaction Isolation: ")
/* 1304 */             .append(paramConnection.getTransactionIsolation())
/* 1305 */             .append("<br />");
/* 1306 */           stringBuilder.append(1)
/* 1307 */             .append(": read_uncommitted<br />");
/* 1308 */           stringBuilder.append(2)
/* 1309 */             .append(": read_committed<br />");
/* 1310 */           stringBuilder.append(4)
/* 1311 */             .append(": repeatable_read<br />");
/* 1312 */           stringBuilder.append(6)
/* 1313 */             .append(": snapshot<br />");
/* 1314 */           stringBuilder.append(8)
/* 1315 */             .append(": serializable");
/*      */         }  }
/* 1317 */        if (paramString.startsWith("@")) {
/* 1318 */         SimpleResultSet simpleResultSet; ResultSet resultSet1 = JdbcUtils.getMetaResultSet(paramConnection, paramString);
/* 1319 */         if (resultSet1 == null && JdbcUtils.isBuiltIn(paramString, "@prof_stop") && 
/* 1320 */           this.profiler != null) {
/* 1321 */           this.profiler.stopCollecting();
/* 1322 */           SimpleResultSet simpleResultSet1 = new SimpleResultSet();
/* 1323 */           simpleResultSet1.addColumn("Top Stack Trace(s)", 12, 0, 0);
/* 1324 */           simpleResultSet1.addRow(new Object[] { this.profiler.getTop(3) });
/* 1325 */           simpleResultSet = simpleResultSet1;
/* 1326 */           this.profiler = null;
/*      */         } 
/*      */         
/* 1329 */         if (simpleResultSet == null) {
/* 1330 */           stringBuilder.append("?: ").append(paramString);
/* 1331 */           return stringBuilder.toString();
/*      */         } 
/*      */       } else {
/* 1334 */         boolean bool; int i = getMaxrows();
/* 1335 */         statement.setMaxRows(i);
/* 1336 */         this.session.executingStatement = statement;
/*      */         
/* 1338 */         if (object == null) {
/* 1339 */           bool = statement.execute(paramString);
/* 1340 */         } else if (object instanceof Boolean) {
/* 1341 */           bool = statement.execute(paramString, 
/* 1342 */               ((Boolean)object).booleanValue() ? 1 : 2);
/* 1343 */         } else if (object instanceof String[]) {
/* 1344 */           bool = statement.execute(paramString, (String[])object);
/*      */         } else {
/* 1346 */           bool = statement.execute(paramString, (int[])object);
/*      */         } 
/* 1348 */         this.session.addCommand(paramString);
/* 1349 */         if (object != null) {
/* 1350 */           resultSet = null;
/* 1351 */           resultSet = statement.getGeneratedKeys();
/*      */         } else {
/* 1353 */           if (!bool) {
/*      */             long l1;
/*      */             try {
/* 1356 */               l1 = statement.getLargeUpdateCount();
/* 1357 */             } catch (UnsupportedOperationException unsupportedOperationException) {
/* 1358 */               l1 = statement.getUpdateCount();
/*      */             } 
/* 1360 */             stringBuilder.append("${text.result.updateCount}: ").append(l1);
/* 1361 */             l = System.currentTimeMillis() - l;
/* 1362 */             stringBuilder.append("<br />(").append(l).append(" ms)");
/* 1363 */             statement.close();
/* 1364 */             return stringBuilder.toString();
/*      */           } 
/* 1366 */           resultSet = statement.getResultSet();
/*      */         } 
/*      */       } 
/* 1369 */       l = System.currentTimeMillis() - l;
/* 1370 */       stringBuilder.append(getResultSet(paramString, resultSet, bool1, bool3, bool2, l, paramBoolean1));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1376 */       if (!bool2) {
/* 1377 */         statement.close();
/*      */       }
/* 1379 */       return stringBuilder.toString();
/* 1380 */     } catch (Throwable throwable) {
/*      */       
/* 1382 */       return getStackTrace(paramInt, throwable, this.session.getContents().isH2());
/*      */     } finally {
/* 1384 */       this.session.executingStatement = null;
/*      */     } 
/*      */   }
/*      */   
/*      */   private String executeLoop(Connection paramConnection, int paramInt, String paramString) throws SQLException {
/*      */     boolean bool;
/* 1390 */     ArrayList<Integer> arrayList = new ArrayList();
/* 1391 */     int i = 0;
/* 1392 */     while (!this.stop) {
/* 1393 */       i = paramString.indexOf('?', i);
/* 1394 */       if (i < 0) {
/*      */         break;
/*      */       }
/* 1397 */       if (JdbcUtils.isBuiltIn(paramString.substring(i), "?/*rnd*/")) {
/* 1398 */         arrayList.add(Integer.valueOf(1));
/* 1399 */         paramString = paramString.substring(0, i) + "?" + paramString.substring(i + "/*rnd*/".length() + 1);
/*      */       } else {
/* 1401 */         arrayList.add(Integer.valueOf(0));
/*      */       } 
/* 1403 */       i++;
/*      */     } 
/*      */     
/* 1406 */     Random random = new Random(1L);
/* 1407 */     long l = System.currentTimeMillis();
/* 1408 */     if (JdbcUtils.isBuiltIn(paramString, "@statement")) {
/* 1409 */       paramString = StringUtils.trimSubstring(paramString, "@statement".length());
/* 1410 */       bool = false;
/* 1411 */       Statement statement = paramConnection.createStatement();
/* 1412 */       for (byte b1 = 0; !this.stop && b1 < paramInt; b1++) {
/* 1413 */         String str = paramString;
/* 1414 */         for (Integer integer : arrayList) {
/* 1415 */           i = str.indexOf('?');
/* 1416 */           if (integer.intValue() == 1) {
/* 1417 */             str = str.substring(0, i) + random.nextInt(paramInt) + str.substring(i + 1); continue;
/*      */           } 
/* 1419 */           str = str.substring(0, i) + b1 + str.substring(i + 1);
/*      */         } 
/*      */         
/* 1422 */         if (statement.execute(str)) {
/* 1423 */           ResultSet resultSet = statement.getResultSet();
/* 1424 */           while (!this.stop && resultSet.next());
/*      */ 
/*      */           
/* 1427 */           resultSet.close();
/*      */         } 
/*      */       } 
/*      */     } else {
/* 1431 */       bool = true;
/* 1432 */       PreparedStatement preparedStatement = paramConnection.prepareStatement(paramString);
/* 1433 */       for (byte b1 = 0; !this.stop && b1 < paramInt; b1++) {
/* 1434 */         for (byte b2 = 0; b2 < arrayList.size(); b2++) {
/* 1435 */           Integer integer = arrayList.get(b2);
/* 1436 */           if (integer.intValue() == 1) {
/* 1437 */             preparedStatement.setInt(b2 + 1, random.nextInt(paramInt));
/*      */           } else {
/* 1439 */             preparedStatement.setInt(b2 + 1, b1);
/*      */           } 
/*      */         } 
/* 1442 */         if (this.session.getContents().isSQLite()) {
/*      */           
/* 1444 */           preparedStatement.executeUpdate();
/*      */         }
/* 1446 */         else if (preparedStatement.execute()) {
/* 1447 */           ResultSet resultSet = preparedStatement.getResultSet();
/* 1448 */           while (!this.stop && resultSet.next());
/*      */ 
/*      */           
/* 1451 */           resultSet.close();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1456 */     l = System.currentTimeMillis() - l;
/*      */     
/* 1458 */     StringBuilder stringBuilder = (new StringBuilder()).append(l).append(" ms: ").append(paramInt).append(" * ").append(bool ? "(Prepared) " : "(Statement) ").append('('); byte b; int j;
/* 1459 */     for (b = 0, j = arrayList.size(); b < j; b++) {
/* 1460 */       if (b > 0) {
/* 1461 */         stringBuilder.append(", ");
/*      */       }
/* 1463 */       stringBuilder.append((((Integer)arrayList.get(b)).intValue() == 0) ? "i" : "rnd");
/*      */     } 
/* 1465 */     return stringBuilder.append(") ").append(paramString).toString();
/*      */   }
/*      */   
/*      */   private String getCommandHistoryString() {
/* 1469 */     StringBuilder stringBuilder = new StringBuilder();
/* 1470 */     ArrayList<String> arrayList = this.session.getCommandHistory();
/* 1471 */     stringBuilder.append("<table cellspacing=0 cellpadding=0><tr><th></th><th>Command</th></tr>");
/*      */     
/* 1473 */     for (int i = arrayList.size() - 1; i >= 0; i--) {
/* 1474 */       String str = arrayList.get(i);
/* 1475 */       stringBuilder.append("<tr><td><a href=\"getHistory.do?id=")
/* 1476 */         .append(i)
/* 1477 */         .append("&jsessionid=${sessionId}\" target=\"h2query\" >")
/* 1478 */         .append("<img width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" ")
/*      */         
/* 1480 */         .append("onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" ")
/*      */         
/* 1482 */         .append("title=\"${text.resultEdit.edit}\" border=\"1\"/></a>")
/* 1483 */         .append("</td><td>")
/* 1484 */         .append(PageParser.escapeHtml(str))
/* 1485 */         .append("</td></tr>");
/*      */     } 
/* 1487 */     stringBuilder.append("</table>");
/* 1488 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static String getParameterResultSet(ParameterMetaData paramParameterMetaData) throws SQLException {
/* 1493 */     StringBuilder stringBuilder = new StringBuilder();
/* 1494 */     if (paramParameterMetaData == null) {
/* 1495 */       return "No parameter meta data";
/*      */     }
/* 1497 */     stringBuilder.append("<table cellspacing=0 cellpadding=0>")
/* 1498 */       .append("<tr><th>className</th><th>mode</th><th>type</th>")
/* 1499 */       .append("<th>typeName</th><th>precision</th><th>scale</th></tr>");
/* 1500 */     for (byte b = 0; b < paramParameterMetaData.getParameterCount(); b++) {
/* 1501 */       stringBuilder.append("</tr><td>")
/* 1502 */         .append(paramParameterMetaData.getParameterClassName(b + 1))
/* 1503 */         .append("</td><td>")
/* 1504 */         .append(paramParameterMetaData.getParameterMode(b + 1))
/* 1505 */         .append("</td><td>")
/* 1506 */         .append(paramParameterMetaData.getParameterType(b + 1))
/* 1507 */         .append("</td><td>")
/* 1508 */         .append(paramParameterMetaData.getParameterTypeName(b + 1))
/* 1509 */         .append("</td><td>")
/* 1510 */         .append(paramParameterMetaData.getPrecision(b + 1))
/* 1511 */         .append("</td><td>")
/* 1512 */         .append(paramParameterMetaData.getScale(b + 1))
/* 1513 */         .append("</td></tr>");
/*      */     }
/* 1515 */     stringBuilder.append("</table>");
/* 1516 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private String getResultSet(String paramString, ResultSet paramResultSet, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, long paramLong, boolean paramBoolean4) throws SQLException {
/*      */     SimpleResultSet simpleResultSet;
/* 1522 */     int i = getMaxrows();
/* 1523 */     paramLong = System.currentTimeMillis() - paramLong;
/* 1524 */     StringBuilder stringBuilder = new StringBuilder();
/* 1525 */     if (paramBoolean3) {
/* 1526 */       stringBuilder.append("<form id=\"editing\" name=\"editing\" method=\"post\" action=\"editResult.do?jsessionid=${sessionId}\" id=\"mainForm\" target=\"h2result\"><input type=\"hidden\" name=\"op\" value=\"1\" /><input type=\"hidden\" name=\"row\" value=\"\" /><table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\" id=\"editTable\">");
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1533 */       stringBuilder.append("<table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\">");
/*      */     } 
/* 1535 */     if (paramBoolean1) {
/* 1536 */       SimpleResultSet simpleResultSet1 = new SimpleResultSet();
/* 1537 */       simpleResultSet1.addColumn("#", 4, 0, 0);
/* 1538 */       simpleResultSet1.addColumn("label", 12, 0, 0);
/* 1539 */       simpleResultSet1.addColumn("catalog", 12, 0, 0);
/* 1540 */       simpleResultSet1.addColumn("schema", 12, 0, 0);
/* 1541 */       simpleResultSet1.addColumn("table", 12, 0, 0);
/* 1542 */       simpleResultSet1.addColumn("column", 12, 0, 0);
/* 1543 */       simpleResultSet1.addColumn("type", 4, 0, 0);
/* 1544 */       simpleResultSet1.addColumn("typeName", 12, 0, 0);
/* 1545 */       simpleResultSet1.addColumn("class", 12, 0, 0);
/* 1546 */       simpleResultSet1.addColumn("precision", 4, 0, 0);
/* 1547 */       simpleResultSet1.addColumn("scale", 4, 0, 0);
/* 1548 */       simpleResultSet1.addColumn("displaySize", 4, 0, 0);
/* 1549 */       simpleResultSet1.addColumn("autoIncrement", 16, 0, 0);
/* 1550 */       simpleResultSet1.addColumn("caseSensitive", 16, 0, 0);
/* 1551 */       simpleResultSet1.addColumn("currency", 16, 0, 0);
/* 1552 */       simpleResultSet1.addColumn("nullable", 4, 0, 0);
/* 1553 */       simpleResultSet1.addColumn("readOnly", 16, 0, 0);
/* 1554 */       simpleResultSet1.addColumn("searchable", 16, 0, 0);
/* 1555 */       simpleResultSet1.addColumn("signed", 16, 0, 0);
/* 1556 */       simpleResultSet1.addColumn("writable", 16, 0, 0);
/* 1557 */       simpleResultSet1.addColumn("definitelyWritable", 16, 0, 0);
/* 1558 */       ResultSetMetaData resultSetMetaData1 = paramResultSet.getMetaData();
/* 1559 */       for (byte b1 = 1; b1 <= resultSetMetaData1.getColumnCount(); b1++) {
/* 1560 */         simpleResultSet1.addRow(new Object[] { Integer.valueOf(b1), resultSetMetaData1
/* 1561 */               .getColumnLabel(b1), resultSetMetaData1
/* 1562 */               .getCatalogName(b1), resultSetMetaData1
/* 1563 */               .getSchemaName(b1), resultSetMetaData1
/* 1564 */               .getTableName(b1), resultSetMetaData1
/* 1565 */               .getColumnName(b1), 
/* 1566 */               Integer.valueOf(resultSetMetaData1.getColumnType(b1)), resultSetMetaData1
/* 1567 */               .getColumnTypeName(b1), resultSetMetaData1
/* 1568 */               .getColumnClassName(b1), 
/* 1569 */               Integer.valueOf(resultSetMetaData1.getPrecision(b1)), 
/* 1570 */               Integer.valueOf(resultSetMetaData1.getScale(b1)), 
/* 1571 */               Integer.valueOf(resultSetMetaData1.getColumnDisplaySize(b1)), 
/* 1572 */               Boolean.valueOf(resultSetMetaData1.isAutoIncrement(b1)), 
/* 1573 */               Boolean.valueOf(resultSetMetaData1.isCaseSensitive(b1)), 
/* 1574 */               Boolean.valueOf(resultSetMetaData1.isCurrency(b1)), 
/* 1575 */               Integer.valueOf(resultSetMetaData1.isNullable(b1)), 
/* 1576 */               Boolean.valueOf(resultSetMetaData1.isReadOnly(b1)), 
/* 1577 */               Boolean.valueOf(resultSetMetaData1.isSearchable(b1)), 
/* 1578 */               Boolean.valueOf(resultSetMetaData1.isSigned(b1)), 
/* 1579 */               Boolean.valueOf(resultSetMetaData1.isWritable(b1)), 
/* 1580 */               Boolean.valueOf(resultSetMetaData1.isDefinitelyWritable(b1)) });
/*      */       } 
/* 1582 */       simpleResultSet = simpleResultSet1;
/*      */     } 
/* 1584 */     ResultSetMetaData resultSetMetaData = simpleResultSet.getMetaData();
/* 1585 */     int j = resultSetMetaData.getColumnCount();
/* 1586 */     byte b = 0;
/* 1587 */     if (paramBoolean2) {
/* 1588 */       stringBuilder.append("<tr><th>Column</th><th>Data</th></tr><tr>");
/* 1589 */       while (simpleResultSet.next() && (
/* 1590 */         i <= 0 || b < i)) {
/*      */ 
/*      */         
/* 1593 */         b++;
/* 1594 */         stringBuilder.append("<tr><td>Row #</td><td>")
/* 1595 */           .append(b).append("</tr>");
/* 1596 */         for (byte b1 = 0; b1 < j; b1++) {
/* 1597 */           stringBuilder.append("<tr><td>")
/* 1598 */             .append(PageParser.escapeHtml(resultSetMetaData.getColumnLabel(b1 + 1)))
/* 1599 */             .append("</td><td>")
/* 1600 */             .append(escapeData((ResultSet)simpleResultSet, b1 + 1))
/* 1601 */             .append("</td></tr>");
/*      */         }
/*      */       } 
/*      */     } else {
/* 1605 */       stringBuilder.append("<tr>");
/* 1606 */       if (paramBoolean3)
/* 1607 */         stringBuilder.append("<th>${text.resultEdit.action}</th>"); 
/*      */       byte b1;
/* 1609 */       for (b1 = 0; b1 < j; b1++) {
/* 1610 */         stringBuilder.append("<th>")
/* 1611 */           .append(PageParser.escapeHtml(resultSetMetaData.getColumnLabel(b1 + 1)))
/* 1612 */           .append("</th>");
/*      */       }
/* 1614 */       stringBuilder.append("</tr>");
/* 1615 */       while (simpleResultSet.next() && (
/* 1616 */         i <= 0 || b < i)) {
/*      */ 
/*      */         
/* 1619 */         b++;
/* 1620 */         stringBuilder.append("<tr>");
/* 1621 */         if (paramBoolean3) {
/* 1622 */           stringBuilder.append("<td>")
/* 1623 */             .append("<img onclick=\"javascript:editRow(")
/* 1624 */             .append(simpleResultSet.getRow())
/* 1625 */             .append(",'${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'")
/*      */             
/* 1627 */             .append(")\" width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" title=\"${text.resultEdit.edit}\" border=\"1\"/>")
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1632 */             .append("<img onclick=\"javascript:deleteRow(")
/* 1633 */             .append(simpleResultSet.getRow())
/* 1634 */             .append(",'${sessionId}', '${text.resultEdit.delete}', '${text.resultEdit.cancel}'")
/*      */             
/* 1636 */             .append(")\" width=16 height=16 src=\"ico_remove.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.delete}\" title=\"${text.resultEdit.delete}\" border=\"1\" /></a>")
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1641 */             .append("</td>");
/*      */         }
/* 1643 */         for (b1 = 0; b1 < j; b1++) {
/* 1644 */           stringBuilder.append("<td>")
/* 1645 */             .append(escapeData((ResultSet)simpleResultSet, b1 + 1))
/* 1646 */             .append("</td>");
/*      */         }
/* 1648 */         stringBuilder.append("</tr>");
/*      */       } 
/*      */     } 
/* 1651 */     boolean bool = false;
/*      */     try {
/* 1653 */       if (!this.session.getContents().isDB2())
/*      */       {
/* 1655 */         bool = (simpleResultSet.getConcurrency() == 1008 && simpleResultSet.getType() != 1003) ? true : false;
/*      */       }
/* 1657 */     } catch (NullPointerException nullPointerException) {}
/*      */ 
/*      */ 
/*      */     
/* 1661 */     if (paramBoolean3) {
/* 1662 */       ResultSet resultSet = this.session.result;
/* 1663 */       if (resultSet != null) {
/* 1664 */         resultSet.close();
/*      */       }
/* 1666 */       this.session.result = (ResultSet)simpleResultSet;
/*      */     } else {
/* 1668 */       simpleResultSet.close();
/*      */     } 
/* 1670 */     if (paramBoolean3) {
/* 1671 */       stringBuilder.append("<tr><td>")
/* 1672 */         .append("<img onclick=\"javascript:editRow(-1, '${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'")
/*      */         
/* 1674 */         .append(")\" width=16 height=16 src=\"ico_add.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.add}\" title=\"${text.resultEdit.add}\" border=\"1\"/>")
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1679 */         .append("</td>");
/* 1680 */       for (byte b1 = 0; b1 < j; b1++) {
/* 1681 */         stringBuilder.append("<td></td>");
/*      */       }
/* 1683 */       stringBuilder.append("</tr>");
/*      */     } 
/* 1685 */     stringBuilder.append("</table>");
/* 1686 */     if (paramBoolean3) {
/* 1687 */       stringBuilder.append("</form>");
/*      */     }
/* 1689 */     if (b == 0) {
/* 1690 */       stringBuilder.append("(${text.result.noRows}");
/* 1691 */     } else if (b == 1) {
/* 1692 */       stringBuilder.append("(${text.result.1row}");
/*      */     } else {
/* 1694 */       stringBuilder.append('(').append(b).append(" ${text.result.rows}");
/*      */     } 
/* 1696 */     stringBuilder.append(", ");
/* 1697 */     paramLong = System.currentTimeMillis() - paramLong;
/* 1698 */     stringBuilder.append(paramLong).append(" ms)");
/* 1699 */     if (!paramBoolean3 && bool && paramBoolean4) {
/* 1700 */       stringBuilder.append("<br /><br /><form name=\"editResult\" method=\"post\" action=\"query.do?jsessionid=${sessionId}\" target=\"h2result\"><input type=\"submit\" class=\"button\" value=\"${text.resultEdit.editResult}\" /><input type=\"hidden\" name=\"sql\" value=\"@edit ")
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1706 */         .append(PageParser.escapeHtmlData(paramString))
/* 1707 */         .append("\" /></form>");
/*      */     }
/* 1709 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String settingSave() {
/* 1718 */     ConnectionInfo connectionInfo = new ConnectionInfo();
/* 1719 */     connectionInfo.name = this.attributes.getProperty("name", "");
/* 1720 */     connectionInfo.driver = this.attributes.getProperty("driver", "");
/* 1721 */     connectionInfo.url = this.attributes.getProperty("url", "");
/* 1722 */     connectionInfo.user = this.attributes.getProperty("user", "");
/* 1723 */     this.server.updateSetting(connectionInfo);
/* 1724 */     this.attributes.put("setting", connectionInfo.name);
/* 1725 */     this.server.saveProperties(null);
/* 1726 */     return "index.do";
/*      */   }
/*      */   
/*      */   private static String escapeData(ResultSet paramResultSet, int paramInt) throws SQLException {
/* 1730 */     if (DataType.isBinaryColumn(paramResultSet.getMetaData(), paramInt)) {
/* 1731 */       byte[] arrayOfByte = paramResultSet.getBytes(paramInt);
/* 1732 */       if (arrayOfByte == null)
/* 1733 */         return "<i>null</i>"; 
/* 1734 */       if (arrayOfByte.length > 50000) {
/* 1735 */         return "<div style='display: none'>=+</div>" + StringUtils.convertBytesToHex(arrayOfByte, 3) + "... (" + arrayOfByte.length + " ${text.result.bytes})";
/*      */       }
/*      */       
/* 1738 */       return StringUtils.convertBytesToHex(arrayOfByte);
/*      */     } 
/* 1740 */     String str = paramResultSet.getString(paramInt);
/* 1741 */     if (str == null)
/* 1742 */       return "<i>null</i>"; 
/* 1743 */     if (str.length() > 100000)
/* 1744 */       return "<div style='display: none'>=+</div>" + PageParser.escapeHtml(str.substring(0, 100)) + "... (" + str
/* 1745 */         .length() + " ${text.result.characters})"; 
/* 1746 */     if (str.equals("null") || str.startsWith("= ") || str.startsWith("=+"))
/* 1747 */       return "<div style='display: none'>= </div>" + PageParser.escapeHtml(str); 
/* 1748 */     if (str.equals(""))
/*      */     {
/* 1750 */       return "";
/*      */     }
/* 1752 */     return PageParser.escapeHtml(str);
/*      */   }
/*      */ 
/*      */   
/*      */   private void unescapeData(String paramString, ResultSet paramResultSet, int paramInt) throws SQLException {
/* 1757 */     if (paramString.equals("null")) {
/* 1758 */       paramResultSet.updateNull(paramInt); return;
/*      */     } 
/* 1760 */     if (paramString.startsWith("=+")) {
/*      */       return;
/*      */     }
/* 1763 */     if (paramString.equals("=*")) {
/*      */       
/* 1765 */       int j = paramResultSet.getMetaData().getColumnType(paramInt);
/* 1766 */       switch (j) {
/*      */         case 92:
/* 1768 */           paramResultSet.updateString(paramInt, "12:00:00");
/*      */           return;
/*      */         case 91:
/*      */         case 93:
/* 1772 */           paramResultSet.updateString(paramInt, "2001-01-01");
/*      */           return;
/*      */       } 
/* 1775 */       paramResultSet.updateString(paramInt, "1");
/*      */       
/*      */       return;
/*      */     } 
/* 1779 */     if (paramString.startsWith("= ")) {
/* 1780 */       paramString = paramString.substring(2);
/*      */     }
/* 1782 */     ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
/* 1783 */     if (DataType.isBinaryColumn(resultSetMetaData, paramInt)) {
/* 1784 */       paramResultSet.updateBytes(paramInt, StringUtils.convertHexToBytes(paramString));
/*      */       return;
/*      */     } 
/* 1787 */     int i = resultSetMetaData.getColumnType(paramInt);
/* 1788 */     if (this.session.getContents().isH2()) {
/* 1789 */       paramResultSet.updateString(paramInt, paramString);
/*      */       return;
/*      */     } 
/* 1792 */     switch (i) {
/*      */       case -5:
/* 1794 */         paramResultSet.updateLong(paramInt, Long.decode(paramString).longValue());
/*      */         return;
/*      */       case 3:
/* 1797 */         paramResultSet.updateBigDecimal(paramInt, new BigDecimal(paramString));
/*      */         return;
/*      */       case 6:
/*      */       case 8:
/* 1801 */         paramResultSet.updateDouble(paramInt, Double.parseDouble(paramString));
/*      */         return;
/*      */       case 7:
/* 1804 */         paramResultSet.updateFloat(paramInt, Float.parseFloat(paramString));
/*      */         return;
/*      */       case 4:
/* 1807 */         paramResultSet.updateInt(paramInt, Integer.decode(paramString).intValue());
/*      */         return;
/*      */       case -6:
/* 1810 */         paramResultSet.updateShort(paramInt, Short.decode(paramString).shortValue());
/*      */         return;
/*      */     } 
/* 1813 */     paramResultSet.updateString(paramInt, paramString);
/*      */   }
/*      */ 
/*      */   
/*      */   private String settingRemove() {
/* 1818 */     String str = this.attributes.getProperty("name", "");
/* 1819 */     this.server.removeSetting(str);
/* 1820 */     ArrayList<ConnectionInfo> arrayList = this.server.getSettings();
/* 1821 */     if (!arrayList.isEmpty()) {
/* 1822 */       this.attributes.put("setting", arrayList.get(0));
/*      */     }
/* 1824 */     this.server.saveProperties(null);
/* 1825 */     return "index.do";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getMimeType() {
/* 1834 */     return this.mimeType;
/*      */   }
/*      */   
/*      */   boolean getCache() {
/* 1838 */     return this.cache;
/*      */   }
/*      */   
/*      */   WebSession getSession() {
/* 1842 */     return this.session;
/*      */   }
/*      */   
/*      */   private void trace(String paramString) {
/* 1846 */     this.server.trace(paramString);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\WebApp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */