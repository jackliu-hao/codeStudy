/*    */ package org.h2.util.json;
/*    */ 
/*    */ import java.util.AbstractMap;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONObject
/*    */   extends JSONValue
/*    */ {
/* 17 */   private final ArrayList<AbstractMap.SimpleImmutableEntry<String, JSONValue>> members = new ArrayList<>();
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
/*    */   void addMember(String paramString, JSONValue paramJSONValue) {
/* 29 */     this.members.add(new AbstractMap.SimpleImmutableEntry<>(paramString, paramJSONValue));
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTo(JSONTarget<?> paramJSONTarget) {
/* 34 */     paramJSONTarget.startObject();
/* 35 */     for (AbstractMap.SimpleImmutableEntry<String, JSONValue> simpleImmutableEntry : this.members) {
/* 36 */       paramJSONTarget.member((String)simpleImmutableEntry.getKey());
/* 37 */       ((JSONValue)simpleImmutableEntry.getValue()).addTo(paramJSONTarget);
/*    */     } 
/* 39 */     paramJSONTarget.endObject();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map.Entry<String, JSONValue>[] getMembers() {
/* 49 */     return this.members.<Map.Entry<String, JSONValue>>toArray((Map.Entry<String, JSONValue>[])new Map.Entry[0]);
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
/*    */   public JSONValue getFirst(String paramString) {
/* 61 */     for (AbstractMap.SimpleImmutableEntry<String, JSONValue> simpleImmutableEntry : this.members) {
/* 62 */       if (paramString.equals(simpleImmutableEntry.getKey())) {
/* 63 */         return (JSONValue)simpleImmutableEntry.getValue();
/*    */       }
/*    */     } 
/* 66 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\json\JSONObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */