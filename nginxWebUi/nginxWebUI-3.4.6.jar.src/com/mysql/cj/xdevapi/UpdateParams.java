/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*    */ import java.util.HashMap;
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
/*    */ 
/*    */ 
/*    */ public class UpdateParams
/*    */ {
/* 42 */   private Map<MysqlxExpr.ColumnIdentifier, MysqlxExpr.Expr> updateOps = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setUpdates(Map<String, Object> updates) {
/* 51 */     updates.entrySet().forEach(e -> addUpdate((String)e.getKey(), e.getValue()));
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
/*    */   public void addUpdate(String path, Object value) {
/* 63 */     this.updateOps.put((new ExprParser(path, true)).parseTableUpdateField(), ExprUtil.argObjectToExpr(value, true));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getUpdates() {
/* 72 */     return this.updateOps;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */