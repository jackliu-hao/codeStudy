/*    */ package org.h2.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface HasSQL
/*    */ {
/*    */   public static final int QUOTE_ONLY_WHEN_REQUIRED = 1;
/*    */   public static final int REPLACE_LOBS_FOR_TRACE = 2;
/*    */   public static final int NO_CASTS = 4;
/*    */   public static final int ADD_PLAN_INFORMATION = 8;
/*    */   public static final int DEFAULT_SQL_FLAGS = 0;
/*    */   public static final int TRACE_SQL_FLAGS = 3;
/*    */   
/*    */   default String getTraceSQL() {
/* 50 */     return getSQL(3);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default String getSQL(int paramInt) {
/* 62 */     return getSQL(new StringBuilder(), paramInt).toString();
/*    */   }
/*    */   
/*    */   StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\HasSQL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */