/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.Trace;
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
/*     */ public class Plan
/*     */ {
/*     */   private final TableFilter[] filters;
/*  25 */   private final HashMap<TableFilter, PlanItem> planItems = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final Expression[] allConditions;
/*     */ 
/*     */   
/*     */   private final TableFilter[] allFilters;
/*     */ 
/*     */ 
/*     */   
/*     */   public Plan(TableFilter[] paramArrayOfTableFilter, int paramInt, Expression paramExpression) {
/*  37 */     this.filters = new TableFilter[paramInt];
/*  38 */     System.arraycopy(paramArrayOfTableFilter, 0, this.filters, 0, paramInt);
/*  39 */     ArrayList<Expression> arrayList = new ArrayList();
/*  40 */     ArrayList arrayList1 = new ArrayList();
/*  41 */     if (paramExpression != null) {
/*  42 */       arrayList.add(paramExpression);
/*     */     }
/*  44 */     for (byte b = 0; b < paramInt; b++) {
/*  45 */       TableFilter tableFilter = paramArrayOfTableFilter[b];
/*  46 */       tableFilter.visit(paramTableFilter -> {
/*     */             paramArrayList1.add(paramTableFilter);
/*     */             if (paramTableFilter.getJoinCondition() != null) {
/*     */               paramArrayList2.add(paramTableFilter.getJoinCondition());
/*     */             }
/*     */           });
/*     */     } 
/*  53 */     this.allConditions = arrayList.<Expression>toArray(new Expression[0]);
/*  54 */     this.allFilters = (TableFilter[])arrayList1.toArray((Object[])new TableFilter[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PlanItem getItem(TableFilter paramTableFilter) {
/*  64 */     return this.planItems.get(paramTableFilter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TableFilter[] getFilters() {
/*  73 */     return this.filters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeUnusableIndexConditions() {
/*  80 */     for (byte b = 0; b < this.allFilters.length; b++) {
/*  81 */       TableFilter tableFilter = this.allFilters[b];
/*  82 */       setEvaluatable(tableFilter, true);
/*  83 */       if (b < this.allFilters.length - 1)
/*     */       {
/*     */ 
/*     */         
/*  87 */         tableFilter.optimizeFullCondition();
/*     */       }
/*  89 */       tableFilter.removeUnusableIndexConditions();
/*     */     } 
/*  91 */     for (TableFilter tableFilter : this.allFilters) {
/*  92 */       setEvaluatable(tableFilter, false);
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
/*     */   public double calculateCost(SessionLocal paramSessionLocal, AllColumnsForPlan paramAllColumnsForPlan) {
/* 104 */     Trace trace = paramSessionLocal.getTrace();
/* 105 */     if (trace.isDebugEnabled()) {
/* 106 */       trace.debug("Plan       : calculate cost for plan {0}", new Object[] { Arrays.toString((Object[])this.allFilters) });
/*     */     }
/* 108 */     double d = 1.0D;
/* 109 */     boolean bool = false;
/* 110 */     for (byte b = 0; b < this.allFilters.length; b++) {
/* 111 */       TableFilter tableFilter = this.allFilters[b];
/* 112 */       if (trace.isDebugEnabled()) {
/* 113 */         trace.debug("Plan       :   for table filter {0}", new Object[] { tableFilter });
/*     */       }
/* 115 */       PlanItem planItem = tableFilter.getBestPlanItem(paramSessionLocal, this.allFilters, b, paramAllColumnsForPlan);
/* 116 */       this.planItems.put(tableFilter, planItem);
/* 117 */       if (trace.isDebugEnabled())
/* 118 */         trace.debug("Plan       :   best plan item cost {0} index {1}", new Object[] {
/* 119 */               Double.valueOf(planItem.cost), planItem.getIndex().getPlanSQL()
/*     */             }); 
/* 121 */       d += d * planItem.cost;
/* 122 */       setEvaluatable(tableFilter, true);
/* 123 */       Expression expression = tableFilter.getJoinCondition();
/* 124 */       if (expression != null && 
/* 125 */         !expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
/* 126 */         bool = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 131 */     if (bool) {
/* 132 */       d = Double.POSITIVE_INFINITY;
/*     */     }
/* 134 */     if (trace.isDebugEnabled()) {
/* 135 */       paramSessionLocal.getTrace().debug("Plan       : plan cost {0}", new Object[] { Double.valueOf(d) });
/*     */     }
/* 137 */     for (TableFilter tableFilter : this.allFilters) {
/* 138 */       setEvaluatable(tableFilter, false);
/*     */     }
/* 140 */     return d;
/*     */   }
/*     */   
/*     */   private void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 144 */     paramTableFilter.setEvaluatable(paramTableFilter, paramBoolean);
/* 145 */     for (Expression expression : this.allConditions)
/* 146 */       expression.setEvaluatable(paramTableFilter, paramBoolean); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\Plan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */