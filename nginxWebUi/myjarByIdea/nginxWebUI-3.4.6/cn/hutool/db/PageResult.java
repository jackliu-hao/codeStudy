package cn.hutool.db;

import cn.hutool.core.util.PageUtil;
import java.util.ArrayList;

public class PageResult<T> extends ArrayList<T> {
   private static final long serialVersionUID = 9056411043515781783L;
   public static final int DEFAULT_PAGE_SIZE = 20;
   private int page;
   private int pageSize;
   private int totalPage;
   private int total;

   public PageResult() {
      this(0, 20);
   }

   public PageResult(int page, int pageSize) {
      super(pageSize <= 0 ? 20 : pageSize);
      this.page = Math.max(page, 0);
      this.pageSize = pageSize <= 0 ? 20 : pageSize;
   }

   public PageResult(int page, int pageSize, int total) {
      this(page, pageSize);
      this.total = total;
      this.totalPage = PageUtil.totalPage(total, pageSize);
   }

   public int getPage() {
      return this.page;
   }

   public void setPage(int page) {
      this.page = page;
   }

   public int getPageSize() {
      return this.pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public int getTotalPage() {
      return this.totalPage;
   }

   public void setTotalPage(int totalPage) {
      this.totalPage = totalPage;
   }

   public int getTotal() {
      return this.total;
   }

   public void setTotal(int total) {
      this.total = total;
   }

   public boolean isFirst() {
      return this.page == PageUtil.getFirstPageNo();
   }

   public boolean isLast() {
      return this.page >= this.totalPage - 1;
   }
}
