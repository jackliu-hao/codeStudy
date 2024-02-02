/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.lang.Segment;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.PageUtil;
/*     */ import cn.hutool.db.sql.Order;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
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
/*     */ public class Page
/*     */   implements Segment<Integer>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 97792549823353462L;
/*     */   public static final int DEFAULT_PAGE_SIZE = 20;
/*     */   private int pageNumber;
/*     */   private int pageSize;
/*     */   private Order[] orders;
/*     */   
/*     */   public static Page of(int pageNumber, int pageSize) {
/*  43 */     return new Page(pageNumber, pageSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Page() {
/*  54 */     this(0, 20);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Page(int pageNumber, int pageSize) {
/*  64 */     this.pageNumber = Math.max(pageNumber, 0);
/*  65 */     this.pageSize = (pageSize <= 0) ? 20 : pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Page(int pageNumber, int pageSize, Order order) {
/*  76 */     this(pageNumber, pageSize);
/*  77 */     this.orders = new Order[] { order };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPageNumber() {
/*  87 */     return this.pageNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPageNumber(int pageNumber) {
/*  96 */     this.pageNumber = Math.max(pageNumber, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPageSize() {
/* 103 */     return this.pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPageSize(int pageSize) {
/* 112 */     this.pageSize = (pageSize <= 0) ? 20 : pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Order[] getOrders() {
/* 119 */     return this.orders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOrder(Order... orders) {
/* 128 */     this.orders = orders;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addOrder(Order... orders) {
/* 137 */     this.orders = (Order[])ArrayUtil.append((Object[])this.orders, (Object[])orders);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStartPosition() {
/* 146 */     return getStartIndex().intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getStartIndex() {
/* 151 */     return Integer.valueOf(PageUtil.getStart(this.pageNumber, this.pageSize));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEndPosition() {
/* 159 */     return getEndIndex().intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getEndIndex() {
/* 164 */     return Integer.valueOf(PageUtil.getEnd(this.pageNumber, this.pageSize));
/*     */   }
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
/*     */   public int[] getStartEnd() {
/* 181 */     return PageUtil.transToStartEnd(this.pageNumber, this.pageSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 186 */     return "Page [page=" + this.pageNumber + ", pageSize=" + this.pageSize + ", order=" + Arrays.toString((Object[])this.orders) + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\Page.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */