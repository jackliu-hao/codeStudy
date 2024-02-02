package com.cym.sqlhelper.bean;

import cn.hutool.core.util.StrUtil;
import com.cym.sqlhelper.reflection.ReflectionUtil;
import com.cym.sqlhelper.reflection.SerializableFunction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sort {
   List<Order> orderList = new ArrayList();

   public Sort() {
   }

   public Sort(String column, Direction direction) {
      Order order = new Order();
      order.setColumn(column);
      order.setDirection(direction);
      this.orderList.add(order);
   }

   public Sort(List<Order> orderList) {
      this.orderList.addAll(orderList);
   }

   public <T, R> Sort(SerializableFunction<T, R> column, Direction direction) {
      Order order = new Order();
      order.setColumn(ReflectionUtil.getFieldName(column));
      order.setDirection(direction);
      this.orderList.add(order);
   }

   public Sort add(String column, Direction direction) {
      Order order = new Order();
      order.setColumn(column);
      order.setDirection(direction);
      this.orderList.add(order);
      return this;
   }

   public <T, R> Sort add(SerializableFunction<T, R> column, Direction direction) {
      Order order = new Order();
      order.setColumn(ReflectionUtil.getFieldName(column));
      order.setDirection(direction);
      this.orderList.add(order);
      return this;
   }

   public String toString() {
      List<String> sqlList = new ArrayList();

      String sql;
      for(Iterator var2 = this.orderList.iterator(); var2.hasNext(); sqlList.add(sql)) {
         Order order = (Order)var2.next();
         sql = StrUtil.toUnderlineCase(order.getColumn());
         if (order.getDirection() == Sort.Direction.ASC) {
            sql = sql + " ASC";
         }

         if (order.getDirection() == Sort.Direction.DESC) {
            sql = sql + " DESC";
         }
      }

      return " ORDER BY " + StrUtil.join(",", sqlList);
   }

   public static enum Direction {
      ASC,
      DESC;
   }
}
