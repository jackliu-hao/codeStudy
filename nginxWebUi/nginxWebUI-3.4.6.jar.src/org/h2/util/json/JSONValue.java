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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class JSONValue
/*    */ {
/*    */   public abstract void addTo(JSONTarget<?> paramJSONTarget);
/*    */   
/*    */   public final String toString() {
/* 26 */     JSONStringTarget jSONStringTarget = new JSONStringTarget();
/* 27 */     addTo(jSONStringTarget);
/* 28 */     return jSONStringTarget.getResult();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */