/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public class JsonString
/*    */   implements JsonValue
/*    */ {
/* 41 */   static HashMap<Character, String> escapeChars = new HashMap<>();
/*    */   
/*    */   static {
/* 44 */     for (JsonParser.EscapeChar ec : JsonParser.EscapeChar.values()) {
/* 45 */       escapeChars.put(Character.valueOf(ec.CHAR), ec.ESCAPED);
/*    */     }
/*    */   }
/*    */   
/* 49 */   private String val = "";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getString() {
/* 57 */     return this.val;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonString setValue(String value) {
/* 68 */     this.val = value;
/* 69 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     StringBuilder sb = new StringBuilder("\"");
/*    */     
/* 81 */     for (int i = 0; i < this.val.length(); i++) {
/* 82 */       if (escapeChars.containsKey(Character.valueOf(this.val.charAt(i)))) {
/* 83 */         sb.append(escapeChars.get(Character.valueOf(this.val.charAt(i))));
/*    */       } else {
/* 85 */         sb.append(this.val.charAt(i));
/*    */       } 
/*    */     } 
/*    */     
/* 89 */     sb.append("\"");
/* 90 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\JsonString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */