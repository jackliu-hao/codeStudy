/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*    */ import com.mysql.cj.x.protobuf.MysqlxExpr;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateSpec
/*    */ {
/*    */   private MysqlxCrud.UpdateOperation.UpdateType updateType;
/*    */   private MysqlxExpr.ColumnIdentifier source;
/*    */   private MysqlxExpr.Expr value;
/*    */   
/*    */   public UpdateSpec(UpdateType updateType, String source) {
/* 55 */     this.updateType = MysqlxCrud.UpdateOperation.UpdateType.valueOf(updateType.name());
/*    */     
/* 57 */     if (source.length() > 0 && source.charAt(0) == '$') {
/* 58 */       source = source.substring(1);
/*    */     }
/* 60 */     this.source = (new ExprParser(source, false)).documentField().getIdentifier();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getUpdateType() {
/* 69 */     return this.updateType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getSource() {
/* 78 */     return this.source;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UpdateSpec setValue(Object value) {
/* 89 */     this.value = ExprUtil.argObjectToExpr(value, false);
/* 90 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getValue() {
/* 99 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */