/*    */ package org.h2.util.json;
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
/*    */ public enum JSONItemType
/*    */ {
/* 16 */   VALUE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   ARRAY,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 26 */   OBJECT,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   SCALAR;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean includes(JSONItemType paramJSONItemType) {
/* 42 */     if (paramJSONItemType == null) {
/* 43 */       throw new NullPointerException();
/*    */     }
/* 45 */     return (this == VALUE || this == paramJSONItemType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */