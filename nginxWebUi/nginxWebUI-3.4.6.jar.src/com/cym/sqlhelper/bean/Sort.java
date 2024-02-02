/*    */ package com.cym.sqlhelper.bean;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.sqlhelper.reflection.ReflectionUtil;
/*    */ import com.cym.sqlhelper.reflection.SerializableFunction;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Sort {
/*    */   public Sort() {}
/*    */   
/* 12 */   List<Order> orderList = new ArrayList<>();
/*    */   
/*    */   public enum Direction {
/* 15 */     ASC, DESC;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Sort(String column, Direction direction) {
/* 23 */     Order order = new Order();
/* 24 */     order.setColumn(column);
/* 25 */     order.setDirection(direction);
/*    */     
/* 27 */     this.orderList.add(order);
/*    */   }
/*    */   
/*    */   public Sort(List<Order> orderList) {
/* 31 */     this.orderList.addAll(orderList);
/*    */   }
/*    */   
/*    */   public <T, R> Sort(SerializableFunction<T, R> column, Direction direction) {
/* 35 */     Order order = new Order();
/* 36 */     order.setColumn(ReflectionUtil.getFieldName(column));
/* 37 */     order.setDirection(direction);
/*    */     
/* 39 */     this.orderList.add(order);
/*    */   }
/*    */   
/*    */   public Sort add(String column, Direction direction) {
/* 43 */     Order order = new Order();
/* 44 */     order.setColumn(column);
/* 45 */     order.setDirection(direction);
/*    */     
/* 47 */     this.orderList.add(order);
/*    */     
/* 49 */     return this;
/*    */   }
/*    */   
/*    */   public <T, R> Sort add(SerializableFunction<T, R> column, Direction direction) {
/* 53 */     Order order = new Order();
/* 54 */     order.setColumn(ReflectionUtil.getFieldName(column));
/* 55 */     order.setDirection(direction);
/*    */     
/* 57 */     this.orderList.add(order);
/*    */     
/* 59 */     return this;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 63 */     List<String> sqlList = new ArrayList<>();
/* 64 */     for (Order order : this.orderList) {
/*    */       
/* 66 */       String sql = StrUtil.toUnderlineCase(order.getColumn());
/*    */       
/* 68 */       if (order.getDirection() == Direction.ASC) {
/* 69 */         sql = sql + " ASC";
/*    */       }
/* 71 */       if (order.getDirection() == Direction.DESC) {
/* 72 */         sql = sql + " DESC";
/*    */       }
/*    */       
/* 75 */       sqlList.add(sql);
/*    */     } 
/*    */     
/* 78 */     return " ORDER BY " + StrUtil.join(",", sqlList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\bean\Sort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */