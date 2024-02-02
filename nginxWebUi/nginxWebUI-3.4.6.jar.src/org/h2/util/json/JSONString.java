/*    */ package org.h2.util.json;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONString
/*    */   extends JSONValue
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   JSONString(String paramString) {
/* 16 */     this.value = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 21 */     paramJSONTarget.valueString(this.value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getString() {
/* 30 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */