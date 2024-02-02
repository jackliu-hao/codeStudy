/*    */ package cn.hutool.db.sql;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.Serializable;
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
/*    */ public class Order
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private String field;
/*    */   private Direction direction;
/*    */   
/*    */   public Order() {}
/*    */   
/*    */   public Order(String field) {
/* 29 */     this.field = field;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Order(String field, Direction direction) {
/* 38 */     this(field);
/* 39 */     this.direction = direction;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getField() {
/* 49 */     return this.field;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setField(String field) {
/* 56 */     this.field = field;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Direction getDirection() {
/* 63 */     return this.direction;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDirection(Direction direction) {
/* 70 */     this.direction = direction;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return StrUtil.builder().append(this.field).append(" ").append((null == this.direction) ? "" : this.direction).toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\Order.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */