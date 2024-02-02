/*    */ package io.undertow.servlet.compat.rewrite;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ public class RewriteConfig
/*    */ {
/*    */   private final RewriteRule[] rules;
/*    */   private final Map<String, RewriteMap> maps;
/*    */   
/*    */   public RewriteConfig(RewriteRule[] rules, Map<String, RewriteMap> maps) {
/* 42 */     this.rules = rules;
/* 43 */     this.maps = maps;
/*    */   }
/*    */   
/*    */   public RewriteRule[] getRules() {
/* 47 */     return this.rules;
/*    */   }
/*    */   
/*    */   public Map<String, RewriteMap> getMaps() {
/* 51 */     return this.maps;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     StringBuffer buffer = new StringBuffer();
/*    */     
/* 58 */     for (int i = 0; i < this.rules.length; i++) {
/* 59 */       for (int j = 0; j < (this.rules[i].getConditions()).length; j++) {
/* 60 */         buffer.append(this.rules[i].getConditions()[j].toString()).append("\r\n");
/*    */       }
/* 62 */       buffer.append(this.rules[i].toString()).append("\r\n").append("\r\n");
/*    */     } 
/* 64 */     return buffer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */