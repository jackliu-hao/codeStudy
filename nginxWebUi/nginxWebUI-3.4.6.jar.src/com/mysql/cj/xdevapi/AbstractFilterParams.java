/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.IntStream;
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
/*     */ public abstract class AbstractFilterParams
/*     */   implements FilterParams
/*     */ {
/*     */   protected MysqlxCrud.Collection collection;
/*     */   protected Long limit;
/*     */   protected Long offset;
/*     */   protected boolean supportsOffset;
/*     */   protected String[] orderExpr;
/*     */   private List<MysqlxCrud.Order> order;
/*     */   protected String criteriaStr;
/*     */   private MysqlxExpr.Expr criteria;
/*     */   protected MysqlxDatatypes.Scalar[] args;
/*     */   private Map<String, Integer> placeholderNameToPosition;
/*     */   protected boolean isRelational;
/*     */   protected String[] groupBy;
/*     */   private List<MysqlxExpr.Expr> grouping;
/*     */   String having;
/*     */   private MysqlxExpr.Expr groupingCriteria;
/*     */   protected String[] projection;
/*     */   protected List<MysqlxCrud.Projection> fields;
/*     */   protected FilterParams.RowLock lock;
/*     */   protected FilterParams.RowLockOptions lockOption;
/*     */   
/*     */   public AbstractFilterParams(String schemaName, String collectionName, boolean supportsOffset, boolean isRelational) {
/*  83 */     this.collection = ExprUtil.buildCollection(schemaName, collectionName);
/*  84 */     this.supportsOffset = supportsOffset;
/*  85 */     this.isRelational = isRelational;
/*     */   }
/*     */   
/*     */   public Object getCollection() {
/*  89 */     return this.collection;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getOrder() {
/*  94 */     return this.order;
/*     */   }
/*     */   
/*     */   public void setOrder(String... orderExpression) {
/*  98 */     this.orderExpr = orderExpression;
/*     */     
/* 100 */     this.order = (new ExprParser(Arrays.<CharSequence>stream((CharSequence[])orderExpression).collect(Collectors.joining(", ")), this.isRelational)).parseOrderSpec();
/*     */   }
/*     */   
/*     */   public Long getLimit() {
/* 104 */     return this.limit;
/*     */   }
/*     */   
/*     */   public void setLimit(Long limit) {
/* 108 */     this.limit = limit;
/*     */   }
/*     */   
/*     */   public Long getOffset() {
/* 112 */     return this.offset;
/*     */   }
/*     */   
/*     */   public void setOffset(Long offset) {
/* 116 */     this.offset = offset;
/*     */   }
/*     */   
/*     */   public boolean supportsOffset() {
/* 120 */     return this.supportsOffset;
/*     */   }
/*     */   
/*     */   public Object getCriteria() {
/* 124 */     return this.criteria;
/*     */   }
/*     */   
/*     */   public void setCriteria(String criteriaString) {
/* 128 */     this.criteriaStr = criteriaString;
/* 129 */     ExprParser parser = new ExprParser(criteriaString, this.isRelational);
/* 130 */     this.criteria = parser.parse();
/* 131 */     if (parser.getPositionalPlaceholderCount() > 0) {
/* 132 */       this.placeholderNameToPosition = parser.getPlaceholderNameToPositionMap();
/* 133 */       this.args = new MysqlxDatatypes.Scalar[parser.getPositionalPlaceholderCount()];
/*     */     } 
/*     */   }
/*     */   
/*     */   public Object getArgs() {
/* 138 */     if (this.args == null) {
/* 139 */       return null;
/*     */     }
/* 141 */     return Arrays.asList(this.args);
/*     */   }
/*     */   
/*     */   public void addArg(String name, Object value) {
/* 145 */     if (this.args == null) {
/* 146 */       throw new WrongArgumentException("No placeholders");
/*     */     }
/* 148 */     if (this.placeholderNameToPosition.get(name) == null) {
/* 149 */       throw new WrongArgumentException("Unknown placeholder: " + name);
/*     */     }
/* 151 */     this.args[((Integer)this.placeholderNameToPosition.get(name)).intValue()] = ExprUtil.argObjectToScalar(value);
/*     */   }
/*     */   
/*     */   public void verifyAllArgsBound() {
/* 155 */     if (this.args != null) {
/* 156 */       IntStream.range(0, this.args.length)
/*     */         
/* 158 */         .filter(i -> (this.args[i] == null))
/*     */         
/* 160 */         .mapToObj(i -> (String)this.placeholderNameToPosition.entrySet().stream().filter(()).map(Map.Entry::getKey).findFirst().get())
/* 161 */         .forEach(name -> {
/*     */             throw new WrongArgumentException("Placeholder '" + name + "' is not bound");
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearArgs() {
/* 168 */     if (this.args != null) {
/* 169 */       IntStream.range(0, this.args.length).forEach(i -> this.args[i] = null);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isRelational() {
/* 174 */     return this.isRelational;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getFields() {
/* 180 */     return this.fields;
/*     */   }
/*     */   
/*     */   public void setGrouping(String... groupBy) {
/* 184 */     this.groupBy = groupBy;
/* 185 */     this.grouping = (new ExprParser(Arrays.<CharSequence>stream((CharSequence[])groupBy).collect(Collectors.joining(", ")), isRelational())).parseExprList();
/*     */   }
/*     */   
/*     */   public Object getGrouping() {
/* 189 */     return this.grouping;
/*     */   }
/*     */   
/*     */   public void setGroupingCriteria(String having) {
/* 193 */     this.having = having;
/* 194 */     this.groupingCriteria = (new ExprParser(having, isRelational())).parse();
/*     */   }
/*     */   
/*     */   public Object getGroupingCriteria() {
/* 198 */     return this.groupingCriteria;
/*     */   }
/*     */   
/*     */   public FilterParams.RowLock getLock() {
/* 202 */     return this.lock;
/*     */   }
/*     */   
/*     */   public void setLock(FilterParams.RowLock rowLock) {
/* 206 */     this.lock = rowLock;
/*     */   }
/*     */   
/*     */   public FilterParams.RowLockOptions getLockOption() {
/* 210 */     return this.lockOption;
/*     */   }
/*     */   
/*     */   public void setLockOption(FilterParams.RowLockOptions lockOption) {
/* 214 */     this.lockOption = lockOption;
/*     */   }
/*     */   
/*     */   public abstract void setFields(String... paramVarArgs);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\AbstractFilterParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */