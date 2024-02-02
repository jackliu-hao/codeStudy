/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PipedReader;
/*     */ import java.io.PipedWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import org.h2.jdbc.JdbcConnection;
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
/*     */ public class CreateCluster
/*     */   extends Tool
/*     */ {
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  50 */     (new CreateCluster()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  55 */     String str1 = null;
/*  56 */     String str2 = null;
/*  57 */     String str3 = "";
/*  58 */     String str4 = "";
/*  59 */     String str5 = null;
/*  60 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  61 */       String str = paramVarArgs[b];
/*  62 */       if (str.equals("-urlSource"))
/*  63 */       { str1 = paramVarArgs[++b]; }
/*  64 */       else if (str.equals("-urlTarget"))
/*  65 */       { str2 = paramVarArgs[++b]; }
/*  66 */       else if (str.equals("-user"))
/*  67 */       { str3 = paramVarArgs[++b]; }
/*  68 */       else if (str.equals("-password"))
/*  69 */       { str4 = paramVarArgs[++b]; }
/*  70 */       else if (str.equals("-serverList"))
/*  71 */       { str5 = paramVarArgs[++b]; }
/*  72 */       else { if (str.equals("-help") || str.equals("-?")) {
/*  73 */           showUsage();
/*     */           return;
/*     */         } 
/*  76 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/*  79 */     if (str1 == null || str2 == null || str5 == null) {
/*  80 */       showUsage();
/*  81 */       throw new SQLException("Source URL, target URL, or server list not set");
/*     */     } 
/*  83 */     process(str1, str2, str3, str4, str5);
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
/*     */   public void execute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SQLException {
/*  98 */     process(paramString1, paramString2, paramString3, paramString4, paramString5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void process(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SQLException {
/* 105 */     try(JdbcConnection null = new JdbcConnection(paramString1 + ";CLUSTER=''", null, paramString3, paramString4, false); 
/* 106 */         Statement null = jdbcConnection.createStatement()) {
/*     */ 
/*     */       
/* 109 */       statement.execute("SET EXCLUSIVE 2");
/*     */       try {
/* 111 */         performTransfer(statement, paramString2, paramString3, paramString4, paramString5);
/*     */       } finally {
/*     */         
/* 114 */         statement.execute("SET EXCLUSIVE FALSE");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void performTransfer(Statement paramStatement, String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/* 123 */     try(JdbcConnection null = new JdbcConnection(paramString1 + ";CLUSTER=''", null, paramString2, paramString3, false); 
/* 124 */         Statement null = jdbcConnection.createStatement()) {
/* 125 */       statement.execute("DROP ALL OBJECTS DELETE FILES");
/*     */     } 
/*     */     
/* 128 */     try (PipedReader null = new PipedReader()) {
/* 129 */       Future<?> future = startWriter(pipedReader, paramStatement);
/*     */ 
/*     */       
/* 132 */       try(JdbcConnection null = new JdbcConnection(paramString1, null, paramString2, paramString3, false); 
/* 133 */           Statement null = jdbcConnection1.createStatement()) {
/* 134 */         RunScript.execute((Connection)jdbcConnection1, pipedReader);
/*     */ 
/*     */         
/*     */         try {
/* 138 */           future.get();
/* 139 */         } catch (ExecutionException executionException) {
/* 140 */           throw new SQLException(executionException.getCause());
/* 141 */         } catch (InterruptedException interruptedException) {
/* 142 */           throw new SQLException(interruptedException);
/*     */         } 
/*     */ 
/*     */         
/* 146 */         paramStatement.executeUpdate("SET CLUSTER '" + paramString4 + "'");
/* 147 */         statement.executeUpdate("SET CLUSTER '" + paramString4 + "'");
/*     */       } 
/* 149 */     } catch (IOException iOException) {
/* 150 */       throw new SQLException(iOException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Future<?> startWriter(PipedReader paramPipedReader, Statement paramStatement) throws IOException {
/* 156 */     ExecutorService executorService = Executors.newFixedThreadPool(1);
/* 157 */     PipedWriter pipedWriter = new PipedWriter(paramPipedReader);
/*     */ 
/*     */     
/* 160 */     Future<?> future = executorService.submit(() -> {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           try(PipedWriter null = paramPipedWriter; ResultSet null = paramStatement.executeQuery("SCRIPT")) {
/*     */             
/*     */             while (resultSet.next()) {
/*     */               pipedWriter.write(resultSet.getString(1) + "\n");
/*     */             }
/* 171 */           } catch (SQLException|IOException sQLException) {
/*     */             throw new IllegalStateException("Producing script from the source DB is failing.", sQLException);
/*     */           } 
/*     */         });
/*     */     
/* 176 */     executorService.shutdown();
/*     */     
/* 178 */     return future;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\CreateCluster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */