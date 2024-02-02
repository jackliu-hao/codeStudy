/*    */ package com.mysql.cj.xdevapi;
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
/*    */ public class JsonArray
/*    */   extends ArrayList<JsonValue>
/*    */   implements JsonValue
/*    */ {
/*    */   private static final long serialVersionUID = 6557406141541247905L;
/*    */   
/*    */   public String toString() {
/* 43 */     StringBuilder sb = new StringBuilder("[");
/* 44 */     for (JsonValue val : this) {
/* 45 */       if (sb.length() > 1) {
/* 46 */         sb.append(",");
/*    */       }
/* 48 */       sb.append(val.toString());
/*    */     } 
/* 50 */     sb.append("]");
/* 51 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toFormattedString() {
/* 56 */     StringBuilder sb = new StringBuilder("[");
/* 57 */     for (JsonValue val : this) {
/* 58 */       if (sb.length() > 1) {
/* 59 */         sb.append(", ");
/*    */       }
/* 61 */       sb.append(val.toFormattedString());
/*    */     } 
/* 63 */     sb.append("]");
/* 64 */     return sb.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonArray addValue(JsonValue val) {
/* 75 */     add(val);
/* 76 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\JsonArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */