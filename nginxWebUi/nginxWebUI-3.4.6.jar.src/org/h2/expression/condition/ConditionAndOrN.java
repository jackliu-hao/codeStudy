/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConditionAndOrN
/*     */   extends Condition
/*     */ {
/*     */   private final int andOrType;
/*     */   private final List<Expression> expressions;
/*     */   private List<Expression> added;
/*     */   
/*     */   public ConditionAndOrN(int paramInt, Expression paramExpression1, Expression paramExpression2, Expression paramExpression3) {
/*  42 */     this.andOrType = paramInt;
/*  43 */     this.expressions = new ArrayList<>(3);
/*  44 */     this.expressions.add(paramExpression1);
/*  45 */     this.expressions.add(paramExpression2);
/*  46 */     this.expressions.add(paramExpression3);
/*     */   }
/*     */   
/*     */   public ConditionAndOrN(int paramInt, List<Expression> paramList) {
/*  50 */     this.andOrType = paramInt;
/*  51 */     this.expressions = paramList;
/*     */   }
/*     */   
/*     */   int getAndOrType() {
/*  55 */     return this.andOrType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addFirst(Expression paramExpression) {
/*  64 */     this.expressions.add(0, paramExpression);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  69 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  74 */     Iterator<Expression> iterator = this.expressions.iterator();
/*  75 */     ((Expression)iterator.next()).getSQL(paramStringBuilder, paramInt, 0);
/*  76 */     while (iterator.hasNext()) {
/*  77 */       switch (this.andOrType) {
/*     */         case 0:
/*  79 */           paramStringBuilder.append("\n    AND ");
/*     */           break;
/*     */         case 1:
/*  82 */           paramStringBuilder.append("\n    OR ");
/*     */           break;
/*     */         default:
/*  85 */           throw DbException.getInternalError("andOrType=" + this.andOrType);
/*     */       } 
/*  87 */       ((Expression)iterator.next()).getSQL(paramStringBuilder, paramInt, 0);
/*     */     } 
/*  89 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/*  94 */     if (this.andOrType == 0) {
/*  95 */       for (Expression expression : this.expressions) {
/*  96 */         expression.createIndexConditions(paramSessionLocal, paramTableFilter);
/*     */       }
/*  98 */       if (this.added != null) {
/*  99 */         for (Expression expression : this.added) {
/* 100 */           expression.createIndexConditions(paramSessionLocal, paramTableFilter);
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 110 */     ArrayList<Expression> arrayList = new ArrayList(this.expressions.size());
/* 111 */     for (Expression expression1 : this.expressions) {
/* 112 */       Expression expression2 = expression1.getNotIfPossible(paramSessionLocal);
/* 113 */       if (expression2 == null) {
/* 114 */         expression2 = new ConditionNot(expression1);
/*     */       }
/* 116 */       arrayList.add(expression2);
/*     */     } 
/* 118 */     boolean bool = (this.andOrType == 0) ? true : false;
/* 119 */     return new ConditionAndOrN(bool, arrayList);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 124 */     boolean bool = false;
/* 125 */     switch (this.andOrType) {
/*     */       case 0:
/* 127 */         for (Expression expression : this.expressions) {
/* 128 */           Value value = expression.getValue(paramSessionLocal);
/* 129 */           if (value == ValueNull.INSTANCE) {
/* 130 */             bool = true; continue;
/* 131 */           }  if (!value.getBoolean()) {
/* 132 */             return (Value)ValueBoolean.FALSE;
/*     */           }
/*     */         } 
/* 135 */         return bool ? (Value)ValueNull.INSTANCE : (Value)ValueBoolean.TRUE;
/*     */       
/*     */       case 1:
/* 138 */         for (Expression expression : this.expressions) {
/* 139 */           Value value = expression.getValue(paramSessionLocal);
/* 140 */           if (value == ValueNull.INSTANCE) {
/* 141 */             bool = true; continue;
/* 142 */           }  if (value.getBoolean()) {
/* 143 */             return (Value)ValueBoolean.TRUE;
/*     */           }
/*     */         } 
/* 146 */         return bool ? (Value)ValueNull.INSTANCE : (Value)ValueBoolean.FALSE;
/*     */     } 
/*     */     
/* 149 */     throw DbException.getInternalError("type=" + this.andOrType);
/*     */   }
/*     */ 
/*     */   
/* 153 */   private static final Comparator<Expression> COMPARE_BY_COST = new Comparator<Expression>()
/*     */     {
/*     */       public int compare(Expression param1Expression1, Expression param1Expression2) {
/* 156 */         return param1Expression1.getCost() - param1Expression2.getCost();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*     */     byte b;
/* 167 */     for (b = 0; b < this.expressions.size(); b++) {
/* 168 */       this.expressions.set(b, ((Expression)this.expressions.get(b)).optimize(paramSessionLocal));
/*     */     }
/*     */     
/* 171 */     Collections.sort(this.expressions, COMPARE_BY_COST);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 177 */     optimizeMerge(0);
/* 178 */     for (b = 1; b < this.expressions.size(); ) {
/* 179 */       Expression expression1 = this.expressions.get(b - 1);
/* 180 */       Expression expression2 = this.expressions.get(b);
/* 181 */       switch (this.andOrType) {
/*     */         case 0:
/* 183 */           if (!(paramSessionLocal.getDatabase().getSettings()).optimizeTwoEquals) {
/*     */             break;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 194 */           if (expression1 instanceof Comparison && expression2 instanceof Comparison) {
/*     */             
/* 196 */             expression3 = ((Comparison)expression1).getAdditionalAnd(paramSessionLocal, (Comparison)expression2);
/* 197 */             if (expression3 != null) {
/* 198 */               if (this.added == null) {
/* 199 */                 this.added = new ArrayList<>();
/*     */               }
/* 201 */               this.added.add(expression3.optimize(paramSessionLocal));
/*     */             } 
/*     */           } 
/*     */           break;
/*     */         case 1:
/* 206 */           if (!(paramSessionLocal.getDatabase().getSettings()).optimizeOr) {
/*     */             break;
/*     */           }
/*     */           
/* 210 */           if (expression1 instanceof Comparison && expression2 instanceof Comparison) {
/* 211 */             expression3 = ((Comparison)expression1).optimizeOr(paramSessionLocal, (Comparison)expression2);
/* 212 */           } else if (expression1 instanceof ConditionIn && expression2 instanceof Comparison) {
/* 213 */             expression3 = ((ConditionIn)expression1).getAdditional((Comparison)expression2);
/* 214 */           } else if (expression2 instanceof ConditionIn && expression1 instanceof Comparison) {
/* 215 */             expression3 = ((ConditionIn)expression2).getAdditional((Comparison)expression1);
/* 216 */           } else if (expression1 instanceof ConditionInConstantSet && expression2 instanceof Comparison) {
/* 217 */             expression3 = ((ConditionInConstantSet)expression1).getAdditional(paramSessionLocal, (Comparison)expression2);
/* 218 */           } else if (expression2 instanceof ConditionInConstantSet && expression1 instanceof Comparison) {
/* 219 */             expression3 = ((ConditionInConstantSet)expression2).getAdditional(paramSessionLocal, (Comparison)expression1);
/* 220 */           } else if (expression1 instanceof ConditionAndOr && expression2 instanceof ConditionAndOr) {
/* 221 */             expression3 = ConditionAndOr.optimizeConditionAndOr((ConditionAndOr)expression1, (ConditionAndOr)expression2);
/*     */           } else {
/*     */             break;
/*     */           } 
/*     */ 
/*     */           
/* 227 */           if (expression3 != null) {
/* 228 */             this.expressions.remove(b);
/* 229 */             this.expressions.set(b - 1, expression3.optimize(paramSessionLocal));
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */       } 
/* 234 */       Expression expression3 = ConditionAndOr.optimizeIfConstant(paramSessionLocal, this.andOrType, expression1, expression2);
/* 235 */       if (expression3 != null) {
/* 236 */         this.expressions.remove(b);
/* 237 */         this.expressions.set(b - 1, expression3);
/*     */         
/*     */         continue;
/*     */       } 
/* 241 */       if (optimizeMerge(b)) {
/*     */         continue;
/*     */       }
/*     */       
/* 245 */       b++;
/*     */     } 
/*     */     
/* 248 */     Collections.sort(this.expressions, COMPARE_BY_COST);
/*     */     
/* 250 */     if (this.expressions.size() == 1) {
/* 251 */       return Condition.castToBoolean(paramSessionLocal, this.expressions.get(0));
/*     */     }
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean optimizeMerge(int paramInt) {
/* 258 */     Expression expression = this.expressions.get(paramInt);
/*     */ 
/*     */     
/* 261 */     if (expression instanceof ConditionAndOrN) {
/* 262 */       ConditionAndOrN conditionAndOrN = (ConditionAndOrN)expression;
/* 263 */       if (this.andOrType == conditionAndOrN.andOrType) {
/* 264 */         this.expressions.remove(paramInt);
/* 265 */         this.expressions.addAll(paramInt, conditionAndOrN.expressions);
/* 266 */         return true;
/*     */       }
/*     */     
/* 269 */     } else if (expression instanceof ConditionAndOr) {
/* 270 */       ConditionAndOr conditionAndOr = (ConditionAndOr)expression;
/* 271 */       if (this.andOrType == conditionAndOr.getAndOrType()) {
/* 272 */         this.expressions.set(paramInt, conditionAndOr.getSubexpression(0));
/* 273 */         this.expressions.add(paramInt + 1, conditionAndOr.getSubexpression(1));
/* 274 */         return true;
/*     */       } 
/*     */     } 
/* 277 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addFilterConditions(TableFilter paramTableFilter) {
/* 282 */     if (this.andOrType == 0) {
/* 283 */       for (Expression expression : this.expressions) {
/* 284 */         expression.addFilterConditions(paramTableFilter);
/*     */       }
/*     */     } else {
/* 287 */       super.addFilterConditions(paramTableFilter);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 293 */     for (Expression expression : this.expressions) {
/* 294 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 300 */     for (Expression expression : this.expressions) {
/* 301 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 307 */     for (Expression expression : this.expressions) {
/* 308 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 314 */     for (Expression expression : this.expressions) {
/* 315 */       if (!expression.isEverything(paramExpressionVisitor)) {
/* 316 */         return false;
/*     */       }
/*     */     } 
/* 319 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 324 */     int i = 0;
/* 325 */     for (Expression expression : this.expressions) {
/* 326 */       i += expression.getCost();
/*     */     }
/* 328 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 333 */     return this.expressions.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 338 */     return this.expressions.get(paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionAndOrN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */