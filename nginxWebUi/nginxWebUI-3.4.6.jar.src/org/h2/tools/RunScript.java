/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.ScriptReader;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
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
/*     */ public class RunScript
/*     */   extends Tool
/*     */ {
/*     */   private boolean showResults;
/*     */   private boolean checkResults;
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  66 */     (new RunScript()).runTool(paramVarArgs);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  89 */     String str1 = null;
/*  90 */     String str2 = "";
/*  91 */     String str3 = "";
/*  92 */     String str4 = "backup.sql";
/*  93 */     String str5 = null;
/*  94 */     boolean bool1 = false;
/*  95 */     boolean bool2 = false;
/*  96 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  97 */       String str = paramVarArgs[b];
/*  98 */       if (str.equals("-url"))
/*  99 */       { str1 = paramVarArgs[++b]; }
/* 100 */       else if (str.equals("-user"))
/* 101 */       { str2 = paramVarArgs[++b]; }
/* 102 */       else if (str.equals("-password"))
/* 103 */       { str3 = paramVarArgs[++b]; }
/* 104 */       else if (str.equals("-continueOnError"))
/* 105 */       { bool1 = true; }
/* 106 */       else if (str.equals("-checkResults"))
/* 107 */       { this.checkResults = true; }
/* 108 */       else if (str.equals("-showResults"))
/* 109 */       { this.showResults = true; }
/* 110 */       else if (str.equals("-script"))
/* 111 */       { str4 = paramVarArgs[++b]; }
/* 112 */       else if (str.equals("-time"))
/* 113 */       { bool2 = true; }
/* 114 */       else if (str.equals("-driver"))
/* 115 */       { String str6 = paramVarArgs[++b];
/* 116 */         JdbcUtils.loadUserClass(str6); }
/* 117 */       else if (str.equals("-options"))
/* 118 */       { StringBuilder stringBuilder = new StringBuilder();
/* 119 */         b++;
/* 120 */         for (; b < paramVarArgs.length; b++) {
/* 121 */           stringBuilder.append(' ').append(paramVarArgs[b]);
/*     */         }
/* 123 */         str5 = stringBuilder.toString(); }
/* 124 */       else { if (str.equals("-help") || str.equals("-?")) {
/* 125 */           showUsage();
/*     */           return;
/*     */         } 
/* 128 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/* 131 */     if (str1 == null) {
/* 132 */       showUsage();
/* 133 */       throw new SQLException("URL not set");
/*     */     } 
/* 135 */     long l = System.nanoTime();
/* 136 */     if (str5 != null) {
/* 137 */       processRunscript(str1, str2, str3, str4, str5);
/*     */     } else {
/* 139 */       process(str1, str2, str3, str4, (Charset)null, bool1);
/*     */     } 
/* 141 */     if (bool2) {
/* 142 */       l = System.nanoTime() - l;
/* 143 */       this.out.println("Done in " + TimeUnit.NANOSECONDS.toMillis(l) + " ms");
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
/*     */   public static ResultSet execute(Connection paramConnection, Reader paramReader) throws SQLException {
/* 158 */     Statement statement = paramConnection.createStatement();
/* 159 */     ResultSet resultSet = null;
/* 160 */     ScriptReader scriptReader = new ScriptReader(paramReader);
/*     */     while (true) {
/* 162 */       String str = scriptReader.readStatement();
/* 163 */       if (str == null) {
/*     */         break;
/*     */       }
/* 166 */       if (StringUtils.isWhitespaceOrEmpty(str)) {
/*     */         continue;
/*     */       }
/* 169 */       boolean bool = statement.execute(str);
/* 170 */       if (bool) {
/* 171 */         if (resultSet != null) {
/* 172 */           resultSet.close();
/* 173 */           resultSet = null;
/*     */         } 
/* 175 */         resultSet = statement.getResultSet();
/*     */       } 
/*     */     } 
/* 178 */     return resultSet;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void process(Connection paramConnection, String paramString, boolean paramBoolean, Charset paramCharset) throws SQLException, IOException {
/* 184 */     BufferedReader bufferedReader = FileUtils.newBufferedReader(paramString, paramCharset);
/*     */     try {
/* 186 */       process(paramConnection, paramBoolean, FileUtils.getParent(paramString), bufferedReader, paramCharset);
/*     */     } finally {
/* 188 */       IOUtils.closeSilently(bufferedReader);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void process(Connection paramConnection, boolean paramBoolean, String paramString, Reader paramReader, Charset paramCharset) throws SQLException, IOException {
/* 194 */     Statement statement = paramConnection.createStatement();
/* 195 */     ScriptReader scriptReader = new ScriptReader(paramReader);
/*     */     while (true) {
/* 197 */       String str1 = scriptReader.readStatement();
/* 198 */       if (str1 == null) {
/*     */         break;
/*     */       }
/* 201 */       String str2 = str1.trim();
/* 202 */       if (str2.isEmpty()) {
/*     */         continue;
/*     */       }
/* 205 */       if (str2.startsWith("@") && StringUtils.toUpperEnglish(str2)
/* 206 */         .startsWith("@INCLUDE")) {
/* 207 */         str1 = StringUtils.trimSubstring(str1, "@INCLUDE".length());
/* 208 */         if (!FileUtils.isAbsolute(str1)) {
/* 209 */           str1 = paramString + File.separatorChar + str1;
/*     */         }
/* 211 */         process(paramConnection, str1, paramBoolean, paramCharset); continue;
/*     */       } 
/*     */       try {
/* 214 */         if (this.showResults && !str2.startsWith("-->")) {
/* 215 */           this.out.print(str1 + ";");
/*     */         }
/* 217 */         if (this.showResults || this.checkResults) {
/* 218 */           boolean bool = statement.execute(str1);
/* 219 */           if (bool) {
/* 220 */             ResultSet resultSet = statement.getResultSet();
/* 221 */             int i = resultSet.getMetaData().getColumnCount();
/* 222 */             StringBuilder stringBuilder = new StringBuilder();
/* 223 */             while (resultSet.next()) {
/* 224 */               stringBuilder.append("\n-->");
/* 225 */               for (byte b = 0; b < i; b++) {
/* 226 */                 String str3 = resultSet.getString(b + 1);
/* 227 */                 if (str3 != null) {
/* 228 */                   str3 = StringUtils.replaceAll(str3, "\r\n", "\n");
/* 229 */                   str3 = StringUtils.replaceAll(str3, "\n", "\n-->    ");
/* 230 */                   str3 = StringUtils.replaceAll(str3, "\r", "\r-->    ");
/*     */                 } 
/* 232 */                 stringBuilder.append(' ').append(str3);
/*     */               } 
/*     */             } 
/* 235 */             stringBuilder.append("\n;");
/* 236 */             String str = stringBuilder.toString();
/* 237 */             if (this.showResults) {
/* 238 */               this.out.print(str);
/*     */             }
/* 240 */             if (this.checkResults) {
/* 241 */               String str3 = scriptReader.readStatement() + ";";
/* 242 */               str3 = StringUtils.replaceAll(str3, "\r\n", "\n");
/* 243 */               str3 = StringUtils.replaceAll(str3, "\r", "\n");
/* 244 */               if (!str3.equals(str)) {
/* 245 */                 str3 = StringUtils.replaceAll(str3, " ", "+");
/* 246 */                 str = StringUtils.replaceAll(str, " ", "+");
/* 247 */                 throw new SQLException("Unexpected output for:\n" + str1
/* 248 */                     .trim() + "\nGot:\n" + str + "\nExpected:\n" + str3);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */           
/*     */           continue;
/*     */         } 
/* 255 */         statement.execute(str1);
/*     */       }
/* 257 */       catch (Exception exception) {
/* 258 */         if (paramBoolean) {
/* 259 */           exception.printStackTrace(this.out); continue;
/*     */         } 
/* 261 */         throw DbException.toSQLException(exception);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void processRunscript(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SQLException {
/* 270 */     try(Connection null = JdbcUtils.getConnection(null, paramString1, paramString2, paramString3); 
/* 271 */         Statement null = connection.createStatement()) {
/* 272 */       String str = "RUNSCRIPT FROM '" + paramString4 + "' " + paramString5;
/* 273 */       statement.execute(str);
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
/*     */   
/*     */   public static void execute(String paramString1, String paramString2, String paramString3, String paramString4, Charset paramCharset, boolean paramBoolean) throws SQLException {
/* 292 */     (new RunScript()).process(paramString1, paramString2, paramString3, paramString4, paramCharset, paramBoolean);
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
/*     */   void process(String paramString1, String paramString2, String paramString3, String paramString4, Charset paramCharset, boolean paramBoolean) throws SQLException {
/* 309 */     if (paramCharset == null) {
/* 310 */       paramCharset = StandardCharsets.UTF_8;
/*     */     }
/* 312 */     try (Connection null = JdbcUtils.getConnection(null, paramString1, paramString2, paramString3)) {
/* 313 */       process(connection, paramString4, paramBoolean, paramCharset);
/* 314 */     } catch (IOException iOException) {
/* 315 */       throw DbException.convertIOException(iOException, paramString4);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\RunScript.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */