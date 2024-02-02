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
/*    */ public class JSONNull
/*    */   extends JSONValue
/*    */ {
/* 16 */   public static final JSONNull NULL = new JSONNull();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 23 */     paramJSONTarget.valueNull();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */