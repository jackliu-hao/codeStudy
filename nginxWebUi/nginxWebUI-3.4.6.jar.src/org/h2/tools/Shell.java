/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringReader;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.server.web.ConnectionInfo;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.ScriptReader;
/*     */ import org.h2.util.SortedProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Shell
/*     */   extends Tool
/*     */   implements Runnable
/*     */ {
/*     */   private static final int MAX_ROW_BUFFER = 5000;
/*     */   private static final int HISTORY_COUNT = 20;
/*     */   private static final char BOX_VERTICAL = '|';
/*  43 */   private PrintStream err = System.err;
/*  44 */   private InputStream in = System.in;
/*     */   private BufferedReader reader;
/*     */   private Connection conn;
/*     */   private Statement stat;
/*     */   private boolean listMode;
/*  49 */   private int maxColumnSize = 100;
/*  50 */   private final ArrayList<String> history = new ArrayList<>();
/*     */   private boolean stopHide;
/*  52 */   private String serverPropertiesDir = "~";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  80 */     (new Shell()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setErr(PrintStream paramPrintStream) {
/*  89 */     this.err = paramPrintStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIn(InputStream paramInputStream) {
/*  98 */     this.in = paramInputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInReader(BufferedReader paramBufferedReader) {
/* 107 */     this.reader = paramBufferedReader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/* 117 */     String str1 = null;
/* 118 */     String str2 = null;
/* 119 */     String str3 = "";
/* 120 */     String str4 = "";
/* 121 */     String str5 = null;
/* 122 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/* 123 */       String str = paramVarArgs[b];
/* 124 */       if (str.equals("-url"))
/* 125 */       { str2 = paramVarArgs[++b]; }
/* 126 */       else if (str.equals("-user"))
/* 127 */       { str3 = paramVarArgs[++b]; }
/* 128 */       else if (str.equals("-password"))
/* 129 */       { str4 = paramVarArgs[++b]; }
/* 130 */       else if (str.equals("-driver"))
/* 131 */       { str1 = paramVarArgs[++b]; }
/* 132 */       else if (str.equals("-sql"))
/* 133 */       { str5 = paramVarArgs[++b]; }
/* 134 */       else if (str.equals("-properties"))
/* 135 */       { this.serverPropertiesDir = paramVarArgs[++b]; }
/* 136 */       else { if (str.equals("-help") || str.equals("-?")) {
/* 137 */           showUsage(); return;
/*     */         } 
/* 139 */         if (str.equals("-list")) {
/* 140 */           this.listMode = true;
/*     */         } else {
/* 142 */           showUsageAndThrowUnsupportedOption(str);
/*     */         }  }
/*     */     
/* 145 */     }  if (str2 != null) {
/* 146 */       this.conn = JdbcUtils.getConnection(str1, str2, str3, str4);
/* 147 */       this.stat = this.conn.createStatement();
/*     */     } 
/* 149 */     if (str5 == null) {
/* 150 */       promptLoop();
/*     */     } else {
/* 152 */       ScriptReader scriptReader = new ScriptReader(new StringReader(str5));
/*     */       while (true) {
/* 154 */         String str = scriptReader.readStatement();
/* 155 */         if (str == null) {
/*     */           break;
/*     */         }
/* 158 */         execute(str);
/*     */       } 
/* 160 */       if (this.conn != null) {
/* 161 */         this.conn.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void runTool(Connection paramConnection, String... paramVarArgs) throws SQLException {
/* 180 */     this.conn = paramConnection;
/* 181 */     this.stat = paramConnection.createStatement();
/* 182 */     runTool(paramVarArgs);
/*     */   }
/*     */   
/*     */   private void showHelp() {
/* 186 */     println("Commands are case insensitive; SQL statements end with ';'");
/* 187 */     println("help or ?      Display this help");
/* 188 */     println("list           Toggle result list / stack trace mode");
/* 189 */     println("maxwidth       Set maximum column width (default is 100)");
/* 190 */     println("autocommit     Enable or disable autocommit");
/* 191 */     println("history        Show the last 20 statements");
/* 192 */     println("quit or exit   Close the connection and exit");
/* 193 */     println("");
/*     */   }
/*     */   
/*     */   private void promptLoop() {
/* 197 */     println("");
/* 198 */     println("Welcome to H2 Shell " + Constants.FULL_VERSION);
/* 199 */     println("Exit with Ctrl+C");
/* 200 */     if (this.conn != null) {
/* 201 */       showHelp();
/*     */     }
/* 203 */     String str = null;
/* 204 */     if (this.reader == null) {
/* 205 */       this.reader = new BufferedReader(new InputStreamReader(this.in));
/*     */     }
/*     */     while (true) {
/*     */       try {
/* 209 */         if (this.conn == null) {
/* 210 */           connect();
/* 211 */           showHelp();
/*     */         } 
/* 213 */         if (str == null) {
/* 214 */           print("sql> ");
/*     */         } else {
/* 216 */           print("...> ");
/*     */         } 
/* 218 */         String str1 = readLine();
/* 219 */         if (str1 == null) {
/*     */           break;
/*     */         }
/* 222 */         String str2 = str1.trim();
/* 223 */         if (str2.isEmpty()) {
/*     */           continue;
/*     */         }
/* 226 */         boolean bool = str2.endsWith(";");
/* 227 */         if (bool) {
/* 228 */           str1 = str1.substring(0, str1.lastIndexOf(';'));
/* 229 */           str2 = str2.substring(0, str2.length() - 1);
/*     */         } 
/* 231 */         String str3 = StringUtils.toLowerEnglish(str2);
/* 232 */         if ("exit".equals(str3) || "quit".equals(str3))
/*     */           break; 
/* 234 */         if ("help".equals(str3) || "?".equals(str3)) {
/* 235 */           showHelp(); continue;
/* 236 */         }  if ("list".equals(str3)) {
/* 237 */           this.listMode = !this.listMode;
/* 238 */           println("Result list mode is now " + (this.listMode ? "on" : "off")); continue;
/* 239 */         }  if ("history".equals(str3)) {
/* 240 */           byte b; int i; for (b = 0, i = this.history.size(); b < i; b++) {
/* 241 */             String str4 = this.history.get(b);
/* 242 */             str4 = str4.replace('\n', ' ').replace('\r', ' ');
/* 243 */             println("#" + (1 + b) + ": " + str4);
/*     */           } 
/* 245 */           if (!this.history.isEmpty()) {
/* 246 */             println("To re-run a statement, type the number and press and enter"); continue;
/*     */           } 
/* 248 */           println("No history"); continue;
/*     */         } 
/* 250 */         if (str3.startsWith("autocommit")) {
/* 251 */           str3 = StringUtils.trimSubstring(str3, "autocommit".length());
/* 252 */           if ("true".equals(str3)) {
/* 253 */             this.conn.setAutoCommit(true);
/* 254 */           } else if ("false".equals(str3)) {
/* 255 */             this.conn.setAutoCommit(false);
/*     */           } else {
/* 257 */             println("Usage: autocommit [true|false]");
/*     */           } 
/* 259 */           println("Autocommit is now " + this.conn.getAutoCommit()); continue;
/* 260 */         }  if (str3.startsWith("maxwidth")) {
/* 261 */           str3 = StringUtils.trimSubstring(str3, "maxwidth".length());
/*     */           try {
/* 263 */             this.maxColumnSize = Integer.parseInt(str3);
/* 264 */           } catch (NumberFormatException numberFormatException) {
/* 265 */             println("Usage: maxwidth <integer value>");
/*     */           } 
/* 267 */           println("Maximum column width is now " + this.maxColumnSize); continue;
/*     */         } 
/* 269 */         boolean bool1 = true;
/* 270 */         if (str == null) {
/* 271 */           if (StringUtils.isNumber(str1)) {
/* 272 */             int i = Integer.parseInt(str1);
/* 273 */             if (i == 0 || i > this.history.size()) {
/* 274 */               println("Not found");
/*     */             } else {
/* 276 */               str = this.history.get(i - 1);
/* 277 */               bool1 = false;
/* 278 */               println(str);
/* 279 */               bool = true;
/*     */             } 
/*     */           } else {
/* 282 */             str = str1;
/*     */           } 
/*     */         } else {
/* 285 */           str = str + "\n" + str1;
/*     */         } 
/* 287 */         if (bool) {
/* 288 */           if (bool1) {
/* 289 */             this.history.add(0, str);
/* 290 */             if (this.history.size() > 20) {
/* 291 */               this.history.remove(20);
/*     */             }
/*     */           } 
/* 294 */           execute(str);
/* 295 */           str = null;
/*     */         }
/*     */       
/* 298 */       } catch (SQLException sQLException) {
/* 299 */         println("SQL Exception: " + sQLException.getMessage());
/* 300 */         str = null;
/* 301 */       } catch (IOException iOException) {
/* 302 */         println(iOException.getMessage());
/*     */         break;
/* 304 */       } catch (Exception exception) {
/* 305 */         println("Exception: " + exception.toString());
/* 306 */         exception.printStackTrace(this.err);
/*     */         break;
/*     */       } 
/*     */     } 
/* 310 */     if (this.conn != null) {
/*     */       try {
/* 312 */         this.conn.close();
/* 313 */         println("Connection closed");
/* 314 */       } catch (SQLException sQLException) {
/* 315 */         println("SQL Exception: " + sQLException.getMessage());
/* 316 */         sQLException.printStackTrace(this.err);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void connect() throws IOException, SQLException {
/* 322 */     String str1 = "jdbc:h2:~/test";
/* 323 */     String str2 = "";
/* 324 */     String str3 = null;
/*     */     try {
/*     */       SortedProperties sortedProperties;
/* 327 */       if ("null".equals(this.serverPropertiesDir)) {
/* 328 */         Properties properties = new Properties();
/*     */       } else {
/* 330 */         sortedProperties = SortedProperties.loadProperties(this.serverPropertiesDir + "/" + ".h2.server.properties");
/*     */       } 
/*     */       
/* 333 */       String str = null;
/* 334 */       boolean bool = false;
/* 335 */       for (byte b = 0;; b++) {
/* 336 */         String str4 = sortedProperties.getProperty(Integer.toString(b));
/* 337 */         if (str4 == null) {
/*     */           break;
/*     */         }
/* 340 */         bool = true;
/* 341 */         str = str4;
/*     */       } 
/* 343 */       if (bool) {
/* 344 */         ConnectionInfo connectionInfo = new ConnectionInfo(str);
/* 345 */         str1 = connectionInfo.url;
/* 346 */         str2 = connectionInfo.user;
/* 347 */         str3 = connectionInfo.driver;
/*     */       } 
/* 349 */     } catch (IOException iOException) {}
/*     */ 
/*     */     
/* 352 */     println("[Enter]   " + str1);
/* 353 */     print("URL       ");
/* 354 */     str1 = readLine(str1).trim();
/* 355 */     if (str3 == null) {
/* 356 */       str3 = JdbcUtils.getDriver(str1);
/*     */     }
/* 358 */     if (str3 != null) {
/* 359 */       println("[Enter]   " + str3);
/*     */     }
/* 361 */     print("Driver    ");
/* 362 */     str3 = readLine(str3).trim();
/* 363 */     println("[Enter]   " + str2);
/* 364 */     print("User      ");
/* 365 */     str2 = readLine(str2);
/* 366 */     this
/* 367 */       .conn = str1.startsWith("jdbc:h2:") ? connectH2(str3, str1, str2) : JdbcUtils.getConnection(str3, str1, str2, readPassword());
/* 368 */     this.stat = this.conn.createStatement();
/* 369 */     println("Connected");
/*     */   }
/*     */   
/*     */   private Connection connectH2(String paramString1, String paramString2, String paramString3) throws IOException, SQLException {
/*     */     while (true) {
/* 374 */       String str = readPassword();
/*     */       try {
/* 376 */         return JdbcUtils.getConnection(paramString1, paramString2 + ";IFEXISTS=TRUE", paramString3, str);
/* 377 */       } catch (SQLException sQLException) {
/* 378 */         if (sQLException.getErrorCode() == 90146) {
/* 379 */           println("Type the same password again to confirm database creation.");
/* 380 */           String str1 = readPassword();
/* 381 */           if (str.equals(str1)) {
/* 382 */             return JdbcUtils.getConnection(paramString1, paramString2, paramString3, str);
/*     */           }
/* 384 */           println("Passwords don't match. Try again."); continue;
/*     */         }  break;
/*     */       } 
/* 387 */     }  throw sQLException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void print(String paramString) {
/* 399 */     this.out.print(paramString);
/* 400 */     this.out.flush();
/*     */   }
/*     */   
/*     */   private void println(String paramString) {
/* 404 */     this.out.println(paramString);
/* 405 */     this.out.flush();
/*     */   }
/*     */   
/*     */   private String readPassword() throws IOException {
/*     */     try {
/* 410 */       Object object = Utils.callStaticMethod("java.lang.System.console", new Object[0]);
/* 411 */       print("Password  ");
/* 412 */       char[] arrayOfChar = (char[])Utils.callMethod(object, "readPassword", new Object[0]);
/* 413 */       return (arrayOfChar == null) ? null : new String(arrayOfChar);
/* 414 */     } catch (Exception exception) {
/*     */ 
/*     */       
/* 417 */       Thread thread = new Thread(this, "Password hider");
/* 418 */       this.stopHide = false;
/* 419 */       thread.start();
/* 420 */       print("Password  > ");
/* 421 */       String str = readLine();
/* 422 */       this.stopHide = true;
/*     */       try {
/* 424 */         thread.join();
/* 425 */       } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */       
/* 428 */       print("\b\b");
/* 429 */       return str;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 439 */     while (!this.stopHide) {
/* 440 */       print("\b\b><");
/*     */       try {
/* 442 */         Thread.sleep(10L);
/* 443 */       } catch (InterruptedException interruptedException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String readLine(String paramString) throws IOException {
/* 451 */     String str = readLine();
/* 452 */     return str.isEmpty() ? paramString : str;
/*     */   }
/*     */   
/*     */   private String readLine() throws IOException {
/* 456 */     String str = this.reader.readLine();
/* 457 */     if (str == null) {
/* 458 */       throw new IOException("Aborted");
/*     */     }
/* 460 */     return str;
/*     */   }
/*     */   
/*     */   private void execute(String paramString) {
/* 464 */     if (StringUtils.isWhitespaceOrEmpty(paramString)) {
/*     */       return;
/*     */     }
/* 467 */     long l = System.nanoTime();
/*     */     try {
/* 469 */       ResultSet resultSet = null;
/*     */       try {
/* 471 */         if (paramString.startsWith("@")) {
/* 472 */           resultSet = JdbcUtils.getMetaResultSet(this.conn, paramString);
/* 473 */           printResult(resultSet, this.listMode);
/* 474 */         } else if (this.stat.execute(paramString)) {
/* 475 */           resultSet = this.stat.getResultSet();
/* 476 */           int i = printResult(resultSet, this.listMode);
/* 477 */           l = System.nanoTime() - l;
/* 478 */           println("(" + i + ((i == 1) ? " row, " : " rows, ") + TimeUnit.NANOSECONDS
/* 479 */               .toMillis(l) + " ms)");
/*     */         } else {
/*     */           long l1;
/*     */           try {
/* 483 */             l1 = this.stat.getLargeUpdateCount();
/* 484 */           } catch (UnsupportedOperationException unsupportedOperationException) {
/* 485 */             l1 = this.stat.getUpdateCount();
/*     */           } 
/* 487 */           l = System.nanoTime() - l;
/* 488 */           println("(Update count: " + l1 + ", " + TimeUnit.NANOSECONDS
/* 489 */               .toMillis(l) + " ms)");
/*     */         } 
/*     */       } finally {
/* 492 */         JdbcUtils.closeSilently(resultSet);
/*     */       } 
/* 494 */     } catch (SQLException sQLException) {
/* 495 */       println("Error: " + sQLException.toString());
/* 496 */       if (this.listMode) {
/* 497 */         sQLException.printStackTrace(this.err);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private int printResult(ResultSet paramResultSet, boolean paramBoolean) throws SQLException {
/* 503 */     if (paramBoolean) {
/* 504 */       return printResultAsList(paramResultSet);
/*     */     }
/* 506 */     return printResultAsTable(paramResultSet);
/*     */   }
/*     */   
/*     */   private int printResultAsTable(ResultSet paramResultSet) throws SQLException {
/* 510 */     ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
/* 511 */     int i = resultSetMetaData.getColumnCount();
/* 512 */     boolean bool = false;
/* 513 */     ArrayList<String[]> arrayList = new ArrayList();
/*     */     
/* 515 */     String[] arrayOfString = new String[i]; byte b;
/* 516 */     for (b = 0; b < i; b++) {
/* 517 */       String str = resultSetMetaData.getColumnLabel(b + 1);
/* 518 */       arrayOfString[b] = (str == null) ? "" : str;
/*     */     } 
/* 520 */     arrayList.add(arrayOfString);
/* 521 */     b = 0;
/* 522 */     while (paramResultSet.next()) {
/* 523 */       b++;
/* 524 */       bool |= loadRow(paramResultSet, i, arrayList);
/* 525 */       if (b > 'ᎈ') {
/* 526 */         printRows(arrayList, i);
/* 527 */         arrayList.clear();
/*     */       } 
/*     */     } 
/* 530 */     printRows(arrayList, i);
/* 531 */     arrayList.clear();
/* 532 */     if (bool) {
/* 533 */       println("(data is partially truncated)");
/*     */     }
/* 535 */     return b;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean loadRow(ResultSet paramResultSet, int paramInt, ArrayList<String[]> paramArrayList) throws SQLException {
/* 540 */     boolean bool = false;
/* 541 */     String[] arrayOfString = new String[paramInt];
/* 542 */     for (byte b = 0; b < paramInt; b++) {
/* 543 */       String str = paramResultSet.getString(b + 1);
/* 544 */       if (str == null) {
/* 545 */         str = "null";
/*     */       }
/*     */       
/* 548 */       if (paramInt > 1 && str.length() > this.maxColumnSize) {
/* 549 */         str = str.substring(0, this.maxColumnSize);
/* 550 */         bool = true;
/*     */       } 
/* 552 */       arrayOfString[b] = str;
/*     */     } 
/* 554 */     paramArrayList.add(arrayOfString);
/* 555 */     return bool;
/*     */   }
/*     */   
/*     */   private int[] printRows(ArrayList<String[]> paramArrayList, int paramInt) {
/* 559 */     int[] arrayOfInt = new int[paramInt];
/* 560 */     for (byte b = 0; b < paramInt; b++) {
/* 561 */       int i = 0;
/* 562 */       for (String[] arrayOfString : paramArrayList) {
/* 563 */         i = Math.max(i, arrayOfString[b].length());
/*     */       }
/* 565 */       if (paramInt > 1) {
/* 566 */         i = Math.min(this.maxColumnSize, i);
/*     */       }
/* 568 */       arrayOfInt[b] = i;
/*     */     } 
/* 570 */     for (String[] arrayOfString : paramArrayList) {
/* 571 */       StringBuilder stringBuilder = new StringBuilder();
/* 572 */       for (byte b1 = 0; b1 < paramInt; b1++) {
/* 573 */         if (b1 > 0) {
/* 574 */           stringBuilder.append(' ').append('|').append(' ');
/*     */         }
/* 576 */         String str = arrayOfString[b1];
/* 577 */         stringBuilder.append(str);
/* 578 */         if (b1 < paramInt - 1) {
/* 579 */           for (int i = str.length(); i < arrayOfInt[b1]; i++) {
/* 580 */             stringBuilder.append(' ');
/*     */           }
/*     */         }
/*     */       } 
/* 584 */       println(stringBuilder.toString());
/*     */     } 
/* 586 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */   private int printResultAsList(ResultSet paramResultSet) throws SQLException {
/* 590 */     ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
/* 591 */     int i = 0;
/* 592 */     int j = resultSetMetaData.getColumnCount();
/* 593 */     String[] arrayOfString = new String[j];
/* 594 */     for (byte b1 = 0; b1 < j; b1++) {
/* 595 */       String str = resultSetMetaData.getColumnLabel(b1 + 1);
/* 596 */       arrayOfString[b1] = str;
/* 597 */       i = Math.max(i, str.length());
/*     */     } 
/* 599 */     StringBuilder stringBuilder = new StringBuilder();
/* 600 */     byte b2 = 0;
/* 601 */     while (paramResultSet.next()) {
/* 602 */       b2++;
/* 603 */       stringBuilder.setLength(0);
/* 604 */       if (b2 > 1) {
/* 605 */         println("");
/*     */       }
/* 607 */       for (byte b = 0; b < j; b++) {
/* 608 */         if (b > 0) {
/* 609 */           stringBuilder.append('\n');
/*     */         }
/* 611 */         String str = arrayOfString[b];
/* 612 */         stringBuilder.append(str);
/* 613 */         for (int k = str.length(); k < i; k++) {
/* 614 */           stringBuilder.append(' ');
/*     */         }
/* 616 */         stringBuilder.append(": ").append(paramResultSet.getString(b + 1));
/*     */       } 
/* 618 */       println(stringBuilder.toString());
/*     */     } 
/* 620 */     if (b2 == 0) {
/* 621 */       for (byte b = 0; b < j; b++) {
/* 622 */         if (b > 0) {
/* 623 */           stringBuilder.append('\n');
/*     */         }
/* 625 */         String str = arrayOfString[b];
/* 626 */         stringBuilder.append(str);
/*     */       } 
/* 628 */       println(stringBuilder.toString());
/*     */     } 
/* 630 */     return b2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Shell.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */