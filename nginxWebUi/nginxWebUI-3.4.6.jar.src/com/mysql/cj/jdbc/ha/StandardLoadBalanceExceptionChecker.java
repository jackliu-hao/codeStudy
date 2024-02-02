/*     */ package com.mysql.cj.jdbc.ha;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
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
/*     */ public class StandardLoadBalanceExceptionChecker
/*     */   implements LoadBalanceExceptionChecker
/*     */ {
/*     */   private List<String> sqlStateList;
/*     */   private List<Class<?>> sqlExClassList;
/*     */   
/*     */   public boolean shouldExceptionTriggerFailover(Throwable ex) {
/*  50 */     String sqlState = (ex instanceof SQLException) ? ((SQLException)ex).getSQLState() : null;
/*     */     
/*  52 */     if (sqlState != null) {
/*  53 */       if (sqlState.startsWith("08"))
/*     */       {
/*  55 */         return true;
/*     */       }
/*  57 */       if (this.sqlStateList != null)
/*     */       {
/*  59 */         for (Iterator<String> i = this.sqlStateList.iterator(); i.hasNext();) {
/*  60 */           if (sqlState.startsWith(((String)i.next()).toString())) {
/*  61 */             return true;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  68 */     if (ex instanceof com.mysql.cj.jdbc.exceptions.CommunicationsException || ex instanceof com.mysql.cj.exceptions.CJCommunicationsException) {
/*  69 */       return true;
/*     */     }
/*     */     
/*  72 */     if (this.sqlExClassList != null)
/*     */     {
/*  74 */       for (Iterator<Class<?>> i = this.sqlExClassList.iterator(); i.hasNext();) {
/*  75 */         if (((Class)i.next()).isInstance(ex)) {
/*  76 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {}
/*     */ 
/*     */   
/*     */   public void init(Properties props) {
/*  90 */     configureSQLStateList(props.getProperty(PropertyKey.loadBalanceSQLStateFailover.getKeyName(), null));
/*  91 */     configureSQLExceptionSubclassList(props.getProperty(PropertyKey.loadBalanceSQLExceptionSubclassFailover.getKeyName(), null));
/*     */   }
/*     */   
/*     */   private void configureSQLStateList(String sqlStates) {
/*  95 */     if (sqlStates == null || "".equals(sqlStates)) {
/*     */       return;
/*     */     }
/*  98 */     List<String> states = StringUtils.split(sqlStates, ",", true);
/*  99 */     List<String> newStates = new ArrayList<>();
/*     */     
/* 101 */     for (String state : states) {
/* 102 */       if (state.length() > 0) {
/* 103 */         newStates.add(state);
/*     */       }
/*     */     } 
/* 106 */     if (newStates.size() > 0) {
/* 107 */       this.sqlStateList = newStates;
/*     */     }
/*     */   }
/*     */   
/*     */   private void configureSQLExceptionSubclassList(String sqlExClasses) {
/* 112 */     if (sqlExClasses == null || "".equals(sqlExClasses)) {
/*     */       return;
/*     */     }
/* 115 */     List<String> classes = StringUtils.split(sqlExClasses, ",", true);
/* 116 */     List<Class<?>> newClasses = new ArrayList<>();
/*     */     
/* 118 */     for (String exClass : classes) {
/*     */       try {
/* 120 */         Class<?> c = Class.forName(exClass);
/* 121 */         newClasses.add(c);
/* 122 */       } catch (Exception exception) {}
/*     */     } 
/*     */ 
/*     */     
/* 126 */     if (newClasses.size() > 0)
/* 127 */       this.sqlExClassList = newClasses; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\ha\StandardLoadBalanceExceptionChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */