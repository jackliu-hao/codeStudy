/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.log.Log;
/*    */ import com.mysql.cj.util.Util;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ import java.util.stream.Collectors;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExceptionInterceptorChain
/*    */   implements ExceptionInterceptor
/*    */ {
/*    */   List<ExceptionInterceptor> interceptors;
/*    */   
/*    */   public ExceptionInterceptorChain(String interceptorClasses, Properties props, Log log) {
/* 44 */     this
/* 45 */       .interceptors = (List<ExceptionInterceptor>)Util.loadClasses(interceptorClasses, "Connection.BadExceptionInterceptor", this).stream().map(o -> o.init(props, log)).collect(Collectors.toList());
/*    */   }
/*    */   
/*    */   public void addRingZero(ExceptionInterceptor interceptor) {
/* 49 */     this.interceptors.add(0, interceptor);
/*    */   }
/*    */   
/*    */   public Exception interceptException(Exception sqlEx) {
/* 53 */     if (this.interceptors != null) {
/* 54 */       Iterator<ExceptionInterceptor> iter = this.interceptors.iterator();
/*    */       
/* 56 */       while (iter.hasNext()) {
/* 57 */         sqlEx = ((ExceptionInterceptor)iter.next()).interceptException(sqlEx);
/*    */       }
/*    */     } 
/*    */     
/* 61 */     return sqlEx;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 65 */     if (this.interceptors != null) {
/* 66 */       Iterator<ExceptionInterceptor> iter = this.interceptors.iterator();
/*    */       
/* 68 */       while (iter.hasNext()) {
/* 69 */         ((ExceptionInterceptor)iter.next()).destroy();
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ExceptionInterceptor init(Properties properties, Log log) {
/* 76 */     if (this.interceptors != null) {
/* 77 */       Iterator<ExceptionInterceptor> iter = this.interceptors.iterator();
/*    */       
/* 79 */       while (iter.hasNext()) {
/* 80 */         ((ExceptionInterceptor)iter.next()).init(properties, log);
/*    */       }
/*    */     } 
/* 83 */     return this;
/*    */   }
/*    */   
/*    */   public List<ExceptionInterceptor> getInterceptors() {
/* 87 */     return this.interceptors;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\ExceptionInterceptorChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */