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
/*    */ public class JSONBoolean
/*    */   extends JSONValue
/*    */ {
/* 16 */   public static final JSONBoolean FALSE = new JSONBoolean(false);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 21 */   public static final JSONBoolean TRUE = new JSONBoolean(true);
/*    */   
/*    */   private final boolean value;
/*    */   
/*    */   private JSONBoolean(boolean paramBoolean) {
/* 26 */     this.value = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 31 */     if (this.value) {
/* 32 */       paramJSONTarget.valueTrue();
/*    */     } else {
/* 34 */       paramJSONTarget.valueFalse();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean getBoolean() {
/* 44 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */