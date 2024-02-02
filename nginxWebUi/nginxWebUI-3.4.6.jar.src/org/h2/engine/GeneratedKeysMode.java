/*    */ package org.h2.engine;
/*    */ 
/*    */ import org.h2.message.DbException;
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
/*    */ public final class GeneratedKeysMode
/*    */ {
/*    */   public static final int NONE = 0;
/*    */   public static final int AUTO = 1;
/*    */   public static final int COLUMN_NUMBERS = 2;
/*    */   public static final int COLUMN_NAMES = 3;
/*    */   
/*    */   public static int valueOf(Object paramObject) {
/* 47 */     if (paramObject == null || Boolean.FALSE.equals(paramObject)) {
/* 48 */       return 0;
/*    */     }
/* 50 */     if (Boolean.TRUE.equals(paramObject)) {
/* 51 */       return 1;
/*    */     }
/* 53 */     if (paramObject instanceof int[]) {
/* 54 */       return (((int[])paramObject).length > 0) ? 2 : 0;
/*    */     }
/* 56 */     if (paramObject instanceof String[]) {
/* 57 */       return (((String[])paramObject).length > 0) ? 3 : 0;
/*    */     }
/* 59 */     throw DbException.getInternalError();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\GeneratedKeysMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */