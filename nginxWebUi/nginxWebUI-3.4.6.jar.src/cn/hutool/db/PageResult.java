/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.util.PageUtil;
/*     */ import java.util.ArrayList;
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
/*     */ public class PageResult<T>
/*     */   extends ArrayList<T>
/*     */ {
/*     */   private static final long serialVersionUID = 9056411043515781783L;
/*     */   public static final int DEFAULT_PAGE_SIZE = 20;
/*     */   private int page;
/*     */   private int pageSize;
/*     */   private int totalPage;
/*     */   private int total;
/*     */   
/*     */   public PageResult() {
/*  41 */     this(0, 20);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PageResult(int page, int pageSize) {
/*  51 */     super((pageSize <= 0) ? 20 : pageSize);
/*     */     
/*  53 */     this.page = Math.max(page, 0);
/*  54 */     this.pageSize = (pageSize <= 0) ? 20 : pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PageResult(int page, int pageSize, int total) {
/*  65 */     this(page, pageSize);
/*     */     
/*  67 */     this.total = total;
/*  68 */     this.totalPage = PageUtil.totalPage(total, pageSize);
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
/*     */   public int getPage() {
/*  80 */     return this.page;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPage(int page) {
/*  89 */     this.page = page;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPageSize() {
/*  96 */     return this.pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPageSize(int pageSize) {
/* 105 */     this.pageSize = pageSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTotalPage() {
/* 112 */     return this.totalPage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTotalPage(int totalPage) {
/* 121 */     this.totalPage = totalPage;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTotal() {
/* 128 */     return this.total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTotal(int total) {
/* 137 */     this.total = total;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFirst() {
/* 145 */     return (this.page == PageUtil.getFirstPageNo());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLast() {
/* 152 */     return (this.page >= this.totalPage - 1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\PageResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */