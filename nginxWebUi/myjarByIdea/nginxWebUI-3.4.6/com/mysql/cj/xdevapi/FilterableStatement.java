package com.mysql.cj.xdevapi;

public abstract class FilterableStatement<STMT_T, RES_T> extends PreparableStatement<RES_T> implements Statement<STMT_T, RES_T> {
   protected FilterParams filterParams;

   public FilterableStatement(FilterParams filterParams) {
      this.filterParams = filterParams;
   }

   public STMT_T where(String searchCondition) {
      this.resetPrepareState();
      this.filterParams.setCriteria(searchCondition);
      return this;
   }

   public STMT_T sort(String... sortFields) {
      return this.orderBy(sortFields);
   }

   public STMT_T orderBy(String... sortFields) {
      this.resetPrepareState();
      this.filterParams.setOrder(sortFields);
      return this;
   }

   public STMT_T limit(long numberOfRows) {
      if (this.filterParams.getLimit() == null) {
         this.setReprepareState();
      }

      this.filterParams.setLimit(numberOfRows);
      return this;
   }

   public STMT_T offset(long limitOffset) {
      this.filterParams.setOffset(limitOffset);
      return this;
   }

   public boolean isRelational() {
      return this.filterParams.isRelational();
   }

   public STMT_T clearBindings() {
      this.filterParams.clearArgs();
      return this;
   }

   public STMT_T bind(String argName, Object value) {
      this.filterParams.addArg(argName, value);
      return this;
   }
}
