/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.FileLister;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ public abstract class Tool
/*     */ {
/*  27 */   protected PrintStream out = System.out;
/*     */ 
/*     */ 
/*     */   
/*     */   private Properties resources;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOut(PrintStream paramPrintStream) {
/*  37 */     this.out = paramPrintStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void runTool(String... paramVarArgs) throws SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SQLException showUsageAndThrowUnsupportedOption(String paramString) throws SQLException {
/*  57 */     showUsage();
/*  58 */     throw throwUnsupportedOption(paramString);
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
/*     */   protected SQLException throwUnsupportedOption(String paramString) throws SQLException {
/*  70 */     throw DbException.getJdbcSQLException(50100, paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void printNoDatabaseFilesFound(String paramString1, String paramString2) {
/*     */     StringBuilder stringBuilder;
/*  82 */     paramString1 = FileLister.getDir(paramString1);
/*  83 */     if (!FileUtils.isDirectory(paramString1)) {
/*  84 */       stringBuilder = new StringBuilder("Directory not found: ");
/*  85 */       stringBuilder.append(paramString1);
/*     */     } else {
/*  87 */       stringBuilder = new StringBuilder("No database files have been found");
/*  88 */       stringBuilder.append(" in directory ").append(paramString1);
/*  89 */       if (paramString2 != null) {
/*  90 */         stringBuilder.append(" for the database ").append(paramString2);
/*     */       }
/*     */     } 
/*  93 */     this.out.println(stringBuilder.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void showUsage() {
/* 101 */     if (this.resources == null) {
/* 102 */       this.resources = new Properties();
/* 103 */       String str1 = "/org/h2/res/javadoc.properties";
/*     */       try {
/* 105 */         byte[] arrayOfByte = Utils.getResource(str1);
/* 106 */         if (arrayOfByte != null) {
/* 107 */           this.resources.load(new ByteArrayInputStream(arrayOfByte));
/*     */         }
/* 109 */       } catch (IOException iOException) {
/* 110 */         this.out.println("Cannot load " + str1);
/*     */       } 
/*     */     } 
/* 113 */     String str = getClass().getName();
/* 114 */     this.out.println(this.resources.get(str));
/* 115 */     this.out.println("Usage: java " + getClass().getName() + " <options>");
/* 116 */     this.out.println(this.resources.get(str + ".main"));
/* 117 */     this.out.println("See also https://h2database.com/javadoc/" + str
/* 118 */         .replace('.', '/') + ".html");
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
/*     */   public static boolean isOption(String paramString1, String paramString2) {
/* 131 */     if (paramString1.equals(paramString2))
/* 132 */       return true; 
/* 133 */     if (paramString1.startsWith(paramString2)) {
/* 134 */       throw DbException.getUnsupportedException("expected: " + paramString2 + " got: " + paramString1);
/*     */     }
/*     */     
/* 137 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Tool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */