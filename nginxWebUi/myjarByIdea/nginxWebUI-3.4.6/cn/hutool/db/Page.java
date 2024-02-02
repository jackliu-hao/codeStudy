package cn.hutool.db;

import cn.hutool.core.lang.Segment;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.db.sql.Order;
import java.io.Serializable;
import java.util.Arrays;

public class Page implements Segment<Integer>, Serializable {
   private static final long serialVersionUID = 97792549823353462L;
   public static final int DEFAULT_PAGE_SIZE = 20;
   private int pageNumber;
   private int pageSize;
   private Order[] orders;

   public static Page of(int pageNumber, int pageSize) {
      return new Page(pageNumber, pageSize);
   }

   public Page() {
      this(0, 20);
   }

   public Page(int pageNumber, int pageSize) {
      this.pageNumber = Math.max(pageNumber, 0);
      this.pageSize = pageSize <= 0 ? 20 : pageSize;
   }

   public Page(int pageNumber, int pageSize, Order order) {
      this(pageNumber, pageSize);
      this.orders = new Order[]{order};
   }

   public int getPageNumber() {
      return this.pageNumber;
   }

   public void setPageNumber(int pageNumber) {
      this.pageNumber = Math.max(pageNumber, 0);
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize <= 0 ? 20 : pageSize;
   }

   public Order[] getOrders() {
      return this.orders;
   }

   public void setOrder(Order... orders) {
      this.orders = orders;
   }

   public void addOrder(Order... orders) {
      this.orders = (Order[])ArrayUtil.append((Object[])this.orders, orders);
   }

   public int getStartPosition() {
      return this.getStartIndex();
   }

   public Integer getStartIndex() {
      return PageUtil.getStart(this.pageNumber, this.pageSize);
   }

   public int getEndPosition() {
      return this.getEndIndex();
   }

   public Integer getEndIndex() {
      return PageUtil.getEnd(this.pageNumber, this.pageSize);
   }

   public int[] getStartEnd() {
      return PageUtil.transToStartEnd(this.pageNumber, this.pageSize);
   }

   public String toString() {
      return "Page [page=" + this.pageNumber + ", pageSize=" + this.pageSize + ", order=" + Arrays.toString(this.orders) + "]";
   }
}
