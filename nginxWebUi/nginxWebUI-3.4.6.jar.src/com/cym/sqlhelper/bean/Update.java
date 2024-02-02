/*    */ package com.cym.sqlhelper.bean;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Update
/*    */ {
/* 11 */   Map<String, Object> sets = new HashMap<>();
/*    */ 
/*    */   
/*    */   public Update set(String key, Object value) {
/* 15 */     this.sets.put(key, value);
/* 16 */     return this;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getSets() {
/* 20 */     return this.sets;
/*    */   }
/*    */   
/*    */   public void setSets(Map<String, Object> sets) {
/* 24 */     this.sets = sets;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\bean\Update.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */