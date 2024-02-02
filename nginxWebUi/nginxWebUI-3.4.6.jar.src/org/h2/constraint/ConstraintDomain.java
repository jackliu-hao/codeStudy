/*     */ package org.h2.constraint;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.command.ddl.AlterDomain;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.PlanItem;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class ConstraintDomain
/*     */   extends Constraint
/*     */ {
/*     */   private Domain domain;
/*     */   private Expression expr;
/*     */   private DomainColumnResolver resolver;
/*     */   
/*     */   public ConstraintDomain(Schema paramSchema, int paramInt, String paramString, Domain paramDomain) {
/*  42 */     super(paramSchema, paramInt, paramString, (Table)null);
/*  43 */     this.domain = paramDomain;
/*  44 */     this.resolver = new DomainColumnResolver(paramDomain.getDataType());
/*     */   }
/*     */ 
/*     */   
/*     */   public Constraint.Type getConstraintType() {
/*  49 */     return Constraint.Type.DOMAIN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Domain getDomain() {
/*  58 */     return this.domain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExpression(SessionLocal paramSessionLocal, Expression paramExpression) {
/*  68 */     paramExpression.mapColumns(this.resolver, 0, 0);
/*  69 */     paramExpression = paramExpression.optimize(paramSessionLocal);
/*     */     
/*  71 */     synchronized (this) {
/*  72 */       this.resolver.setValue((Value)ValueNull.INSTANCE);
/*  73 */       paramExpression.getValue(paramSessionLocal);
/*     */     } 
/*  75 */     this.expr = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  80 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLWithoutIndexes() {
/*  85 */     return getCreateSQL();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  90 */     StringBuilder stringBuilder = new StringBuilder("ALTER DOMAIN ");
/*  91 */     this.domain.getSQL(stringBuilder, 0).append(" ADD CONSTRAINT ");
/*  92 */     getSQL(stringBuilder, 0);
/*  93 */     if (this.comment != null) {
/*  94 */       stringBuilder.append(" COMMENT ");
/*  95 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/*  97 */     stringBuilder.append(" CHECK");
/*  98 */     this.expr.getEnclosedSQL(stringBuilder, 0).append(" NOCHECK");
/*  99 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 104 */     this.domain.removeConstraint(this);
/* 105 */     this.database.removeMeta(paramSessionLocal, getId());
/* 106 */     this.domain = null;
/* 107 */     this.expr = null;
/* 108 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2) {
/* 113 */     throw DbException.getInternalError(toString());
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
/*     */   public void check(SessionLocal paramSessionLocal, Value paramValue) {
/*     */     Value value;
/* 126 */     synchronized (this) {
/* 127 */       this.resolver.setValue(paramValue);
/* 128 */       value = this.expr.getValue(paramSessionLocal);
/*     */     } 
/*     */     
/* 131 */     if (value.isFalse()) {
/* 132 */       throw DbException.get(23513, this.expr.getTraceSQL());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getCheckConstraint(SessionLocal paramSessionLocal, String paramString) {
/*     */     String str;
/* 145 */     if (paramString != null) {
/* 146 */       synchronized (this) {
/*     */         try {
/* 148 */           this.resolver.setColumnName(paramString);
/* 149 */           str = this.expr.getSQL(0);
/*     */         } finally {
/* 151 */           this.resolver.resetColumnName();
/*     */         } 
/*     */       } 
/* 154 */       return (new Parser(paramSessionLocal)).parseExpression(str);
/*     */     } 
/* 156 */     synchronized (this) {
/* 157 */       str = this.expr.getSQL(0);
/*     */     } 
/* 159 */     return (new Parser(paramSessionLocal)).parseDomainConstraintExpression(str);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean usesIndex(Index paramIndex) {
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIndexOwner(Index paramIndex) {
/* 170 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Column> getReferencedColumns(Table paramTable) {
/* 175 */     HashSet<Column> hashSet = new HashSet();
/* 176 */     this.expr.isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, paramTable));
/* 177 */     return hashSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getExpression() {
/* 182 */     return this.expr;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBefore() {
/* 187 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkExistingData(SessionLocal paramSessionLocal) {
/* 192 */     if (paramSessionLocal.getDatabase().isStarting()) {
/*     */       return;
/*     */     }
/*     */     
/* 196 */     new CheckExistingData(paramSessionLocal, this.domain);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rebuild() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 206 */     return this.expr.isEverything(paramExpressionVisitor);
/*     */   }
/*     */   
/*     */   private class CheckExistingData
/*     */   {
/*     */     private final SessionLocal session;
/*     */     
/*     */     CheckExistingData(SessionLocal param1SessionLocal, Domain param1Domain) {
/* 214 */       this.session = param1SessionLocal;
/* 215 */       checkDomain(null, param1Domain);
/*     */     }
/*     */     
/*     */     private boolean checkColumn(Domain param1Domain, Column param1Column) {
/* 219 */       Table table = param1Column.getTable();
/* 220 */       TableFilter tableFilter = new TableFilter(this.session, table, null, true, null, 0, null);
/* 221 */       TableFilter[] arrayOfTableFilter = { tableFilter };
/* 222 */       PlanItem planItem = tableFilter.getBestPlanItem(this.session, arrayOfTableFilter, 0, new AllColumnsForPlan(arrayOfTableFilter));
/* 223 */       tableFilter.setPlanItem(planItem);
/* 224 */       tableFilter.prepare();
/* 225 */       tableFilter.startQuery(this.session);
/* 226 */       tableFilter.reset();
/* 227 */       while (tableFilter.next()) {
/* 228 */         ConstraintDomain.this.check(this.session, tableFilter.getValue(param1Column));
/*     */       }
/* 230 */       return false;
/*     */     }
/*     */     
/*     */     private boolean checkDomain(Domain param1Domain1, Domain param1Domain2) {
/* 234 */       AlterDomain.forAllDependencies(this.session, param1Domain2, this::checkColumn, this::checkDomain, false);
/* 235 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\ConstraintDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */