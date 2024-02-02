/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DbDocImpl
/*     */   extends TreeMap<String, JsonValue>
/*     */   implements DbDoc
/*     */ {
/*     */   private static final long serialVersionUID = 6557406141541247905L;
/*     */   
/*     */   public String toString() {
/*  98 */     StringBuilder sb = new StringBuilder("{");
/*  99 */     for (String key : keySet()) {
/* 100 */       if (sb.length() > 1) {
/* 101 */         sb.append(",");
/*     */       }
/* 103 */       sb.append("\"").append(key).append("\":").append(get(key).toString());
/*     */     } 
/* 105 */     sb.append("}");
/* 106 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toFormattedString() {
/* 111 */     StringBuilder sb = new StringBuilder("{");
/* 112 */     for (String key : keySet()) {
/* 113 */       if (sb.length() > 1) {
/* 114 */         sb.append(",");
/*     */       }
/* 116 */       sb.append("\n\"").append(key).append("\" : ").append(get(key).toFormattedString());
/*     */     } 
/* 118 */     if (size() > 0) {
/* 119 */       sb.append("\n");
/*     */     }
/* 121 */     sb.append("}");
/* 122 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public DbDoc add(String key, JsonValue val) {
/* 126 */     put(key, val);
/* 127 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DbDocImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */