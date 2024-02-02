/*     */ package com.mysql.cj.jdbc.interceptors;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.Query;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class ResultSetScannerInterceptor
/*     */   implements QueryInterceptor
/*     */ {
/*     */   public static final String PNAME_resultSetScannerRegex = "resultSetScannerRegex";
/*     */   protected Pattern regexP;
/*     */   
/*     */   public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
/*  60 */     String regexFromUser = props.getProperty("resultSetScannerRegex");
/*     */     
/*  62 */     if (regexFromUser == null || regexFromUser.length() == 0) {
/*  63 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.0"));
/*     */     }
/*     */     
/*     */     try {
/*  67 */       this.regexP = Pattern.compile(regexFromUser);
/*  68 */     } catch (Throwable t) {
/*  69 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.1"), t);
/*     */     } 
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
/*  80 */     final T finalResultSet = originalResultSet;
/*     */     
/*  82 */     return (T)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[] { Resultset.class, ResultSetInternalMethods.class }, new InvocationHandler()
/*     */         {
/*     */           
/*     */           public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */           {
/*  87 */             if ("equals".equals(method.getName()))
/*     */             {
/*  89 */               return Boolean.valueOf(args[0].equals(this));
/*     */             }
/*     */             
/*  92 */             Object invocationResult = method.invoke(finalResultSet, args);
/*     */             
/*  94 */             String methodName = method.getName();
/*     */             
/*  96 */             if ((invocationResult != null && invocationResult instanceof String) || "getString".equals(methodName) || "getObject".equals(methodName) || "getObjectStoredProc"
/*  97 */               .equals(methodName)) {
/*  98 */               Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());
/*     */               
/* 100 */               if (matcher.matches()) {
/* 101 */                 throw new SQLException(Messages.getString("ResultSetScannerInterceptor.2"));
/*     */               }
/*     */             } 
/*     */             
/* 105 */             return invocationResult;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean executeTopLevelOnly() {
/* 121 */     return false;
/*     */   }
/*     */   
/*     */   public void destroy() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\interceptors\ResultSetScannerInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */