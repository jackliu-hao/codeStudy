/*    */ package org.h2.value;
/*    */ 
/*    */ import org.h2.util.HasSQL;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ExtTypeInfo
/*    */   implements HasSQL
/*    */ {
/*    */   public String toString() {
/* 17 */     return getSQL(1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ExtTypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */