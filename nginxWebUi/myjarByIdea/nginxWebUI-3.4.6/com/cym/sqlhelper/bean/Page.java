package com.cym.sqlhelper.bean;

import java.util.Collections;
import java.util.List;

public class Page<T> {
   Long count = 0L;
   Integer curr = 1;
   Integer limit = 10;
   List records = Collections.emptyList();

   public List getRecords() {
      return this.records;
   }

   public void setRecords(List records) {
      this.records = records;
   }

   public Long getCount() {
      return this.count;
   }

   public void setCount(Long count) {
      this.count = count;
   }

   public Integer getCurr() {
      return this.curr;
   }

   public void setCurr(Integer curr) {
      this.curr = curr;
   }

   public Integer getLimit() {
      return this.limit;
   }

   public void setLimit(Integer limit) {
      this.limit = limit;
   }
}
