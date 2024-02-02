/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import org.h2.util.JdbcUtils;
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
/*     */ public class Script
/*     */   extends Tool
/*     */ {
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  44 */     (new Script()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  49 */     String str1 = null;
/*  50 */     String str2 = "";
/*  51 */     String str3 = "";
/*  52 */     String str4 = "backup.sql";
/*  53 */     String str5 = "";
/*  54 */     String str6 = "";
/*  55 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  56 */       String str = paramVarArgs[b];
/*  57 */       if (str.equals("-url"))
/*  58 */       { str1 = paramVarArgs[++b]; }
/*  59 */       else if (str.equals("-user"))
/*  60 */       { str2 = paramVarArgs[++b]; }
/*  61 */       else if (str.equals("-password"))
/*  62 */       { str3 = paramVarArgs[++b]; }
/*  63 */       else if (str.equals("-script"))
/*  64 */       { str4 = paramVarArgs[++b]; }
/*  65 */       else if (str.equals("-options"))
/*  66 */       { StringBuilder stringBuilder1 = new StringBuilder();
/*  67 */         StringBuilder stringBuilder2 = new StringBuilder();
/*  68 */         b++;
/*  69 */         for (; b < paramVarArgs.length; b++) {
/*  70 */           String str7 = paramVarArgs[b];
/*  71 */           String str8 = StringUtils.toUpperEnglish(str7);
/*  72 */           if ("SIMPLE".equals(str8) || str8.startsWith("NO") || "DROP".equals(str8)) {
/*  73 */             stringBuilder1.append(' ');
/*  74 */             stringBuilder1.append(paramVarArgs[b]);
/*  75 */           } else if ("BLOCKSIZE".equals(str8)) {
/*  76 */             stringBuilder1.append(' ');
/*  77 */             stringBuilder1.append(paramVarArgs[b]);
/*  78 */             b++;
/*  79 */             stringBuilder1.append(' ');
/*  80 */             stringBuilder1.append(paramVarArgs[b]);
/*     */           } else {
/*  82 */             stringBuilder2.append(' ');
/*  83 */             stringBuilder2.append(paramVarArgs[b]);
/*     */           } 
/*     */         } 
/*  86 */         str5 = stringBuilder1.toString();
/*  87 */         str6 = stringBuilder2.toString(); }
/*  88 */       else { if (str.equals("-help") || str.equals("-?")) {
/*  89 */           showUsage();
/*     */           return;
/*     */         } 
/*  92 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/*  95 */     if (str1 == null) {
/*  96 */       showUsage();
/*  97 */       throw new SQLException("URL not set");
/*     */     } 
/*  99 */     process(str1, str2, str3, str4, str5, str6);
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
/*     */   public static void process(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) throws SQLException {
/* 115 */     try (Connection null = JdbcUtils.getConnection(null, paramString1, paramString2, paramString3)) {
/* 116 */       process(connection, paramString4, paramString5, paramString6);
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
/*     */   public static void process(Connection paramConnection, String paramString1, String paramString2, String paramString3) throws SQLException {
/* 133 */     try (Statement null = paramConnection.createStatement()) {
/* 134 */       String str = "SCRIPT " + paramString2 + " TO '" + paramString1 + "' " + paramString3;
/* 135 */       statement.execute(str);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Script.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */