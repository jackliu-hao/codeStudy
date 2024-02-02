package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractFilterParams implements FilterParams {
   protected MysqlxCrud.Collection collection;
   protected Long limit;
   protected Long offset;
   protected boolean supportsOffset;
   protected String[] orderExpr;
   private List<MysqlxCrud.Order> order;
   protected String criteriaStr;
   private MysqlxExpr.Expr criteria;
   protected MysqlxDatatypes.Scalar[] args;
   private Map<String, Integer> placeholderNameToPosition;
   protected boolean isRelational;
   protected String[] groupBy;
   private List<MysqlxExpr.Expr> grouping;
   String having;
   private MysqlxExpr.Expr groupingCriteria;
   protected String[] projection;
   protected List<MysqlxCrud.Projection> fields;
   protected FilterParams.RowLock lock;
   protected FilterParams.RowLockOptions lockOption;

   public AbstractFilterParams(String schemaName, String collectionName, boolean supportsOffset, boolean isRelational) {
      this.collection = ExprUtil.buildCollection(schemaName, collectionName);
      this.supportsOffset = supportsOffset;
      this.isRelational = isRelational;
   }

   public Object getCollection() {
      return this.collection;
   }

   public Object getOrder() {
      return this.order;
   }

   public void setOrder(String... orderExpression) {
      this.orderExpr = orderExpression;
      this.order = (new ExprParser((String)Arrays.stream(orderExpression).collect(Collectors.joining(", ")), this.isRelational)).parseOrderSpec();
   }

   public Long getLimit() {
      return this.limit;
   }

   public void setLimit(Long limit) {
      this.limit = limit;
   }

   public Long getOffset() {
      return this.offset;
   }

   public void setOffset(Long offset) {
      this.offset = offset;
   }

   public boolean supportsOffset() {
      return this.supportsOffset;
   }

   public Object getCriteria() {
      return this.criteria;
   }

   public void setCriteria(String criteriaString) {
      this.criteriaStr = criteriaString;
      ExprParser parser = new ExprParser(criteriaString, this.isRelational);
      this.criteria = parser.parse();
      if (parser.getPositionalPlaceholderCount() > 0) {
         this.placeholderNameToPosition = parser.getPlaceholderNameToPositionMap();
         this.args = new MysqlxDatatypes.Scalar[parser.getPositionalPlaceholderCount()];
      }

   }

   public Object getArgs() {
      return this.args == null ? null : Arrays.asList(this.args);
   }

   public void addArg(String name, Object value) {
      if (this.args == null) {
         throw new WrongArgumentException("No placeholders");
      } else if (this.placeholderNameToPosition.get(name) == null) {
         throw new WrongArgumentException("Unknown placeholder: " + name);
      } else {
         this.args[(Integer)this.placeholderNameToPosition.get(name)] = ExprUtil.argObjectToScalar(value);
      }
   }

   public void verifyAllArgsBound() {
      if (this.args != null) {
         IntStream.range(0, this.args.length).filter((i) -> {
            return this.args[i] == null;
         }).mapToObj((i) -> {
            return (String)this.placeholderNameToPosition.entrySet().stream().filter((e) -> {
               return (Integer)e.getValue() == i;
            }).map(Map.Entry::getKey).findFirst().get();
         }).forEach((name) -> {
            throw new WrongArgumentException("Placeholder '" + name + "' is not bound");
         });
      }

   }

   public void clearArgs() {
      if (this.args != null) {
         IntStream.range(0, this.args.length).forEach((i) -> {
            this.args[i] = null;
         });
      }

   }

   public boolean isRelational() {
      return this.isRelational;
   }

   public abstract void setFields(String... var1);

   public Object getFields() {
      return this.fields;
   }

   public void setGrouping(String... groupBy) {
      this.groupBy = groupBy;
      this.grouping = (new ExprParser((String)Arrays.stream(groupBy).collect(Collectors.joining(", ")), this.isRelational())).parseExprList();
   }

   public Object getGrouping() {
      return this.grouping;
   }

   public void setGroupingCriteria(String having) {
      this.having = having;
      this.groupingCriteria = (new ExprParser(having, this.isRelational())).parse();
   }

   public Object getGroupingCriteria() {
      return this.groupingCriteria;
   }

   public FilterParams.RowLock getLock() {
      return this.lock;
   }

   public void setLock(FilterParams.RowLock rowLock) {
      this.lock = rowLock;
   }

   public FilterParams.RowLockOptions getLockOption() {
      return this.lockOption;
   }

   public void setLockOption(FilterParams.RowLockOptions lockOption) {
      this.lockOption = lockOption;
   }
}
