/*    */ package org.h2.util.json;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONArray
/*    */   extends JSONValue
/*    */ {
/* 15 */   private final ArrayList<JSONValue> elements = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void addElement(JSONValue paramJSONValue) {
/* 26 */     this.elements.add(paramJSONValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 31 */     paramJSONTarget.startArray();
/* 32 */     for (JSONValue jSONValue : this.elements) {
/* 33 */       jSONValue.addTo(paramJSONTarget);
/*    */     }
/* 35 */     paramJSONTarget.endArray();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JSONValue[] getArray() {
/* 44 */     return this.elements.<JSONValue>toArray(new JSONValue[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */