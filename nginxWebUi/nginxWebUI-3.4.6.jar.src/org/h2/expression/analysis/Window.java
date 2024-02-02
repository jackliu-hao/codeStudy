/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.ListIterator;
/*     */ import org.h2.command.query.QueryOrderBy;
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueRow;
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
/*     */ public final class Window
/*     */ {
/*     */   private ArrayList<Expression> partitionBy;
/*     */   private ArrayList<QueryOrderBy> orderBy;
/*     */   private WindowFrame frame;
/*     */   private String parent;
/*     */   
/*     */   public static void appendOrderBy(StringBuilder paramStringBuilder, ArrayList<QueryOrderBy> paramArrayList, int paramInt, boolean paramBoolean) {
/*  53 */     if (paramArrayList != null && !paramArrayList.isEmpty()) {
/*  54 */       appendOrderByStart(paramStringBuilder);
/*  55 */       for (byte b = 0; b < paramArrayList.size(); b++) {
/*  56 */         QueryOrderBy queryOrderBy = paramArrayList.get(b);
/*  57 */         if (b > 0) {
/*  58 */           paramStringBuilder.append(", ");
/*     */         }
/*  60 */         queryOrderBy.expression.getUnenclosedSQL(paramStringBuilder, paramInt);
/*  61 */         SortOrder.typeToString(paramStringBuilder, queryOrderBy.sortType);
/*     */       } 
/*  63 */     } else if (paramBoolean) {
/*  64 */       appendOrderByStart(paramStringBuilder);
/*  65 */       paramStringBuilder.append("NULL");
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void appendOrderByStart(StringBuilder paramStringBuilder) {
/*  70 */     if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) != '(') {
/*  71 */       paramStringBuilder.append(' ');
/*     */     }
/*  73 */     paramStringBuilder.append("ORDER BY ");
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
/*     */   public Window(String paramString, ArrayList<Expression> paramArrayList, ArrayList<QueryOrderBy> paramArrayList1, WindowFrame paramWindowFrame) {
/*  90 */     this.parent = paramString;
/*  91 */     this.partitionBy = paramArrayList;
/*  92 */     this.orderBy = paramArrayList1;
/*  93 */     this.frame = paramWindowFrame;
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
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt) {
/* 106 */     resolveWindows(paramColumnResolver);
/* 107 */     if (this.partitionBy != null) {
/* 108 */       for (Expression expression : this.partitionBy) {
/* 109 */         expression.mapColumns(paramColumnResolver, paramInt, 1);
/*     */       }
/*     */     }
/* 112 */     if (this.orderBy != null) {
/* 113 */       for (QueryOrderBy queryOrderBy : this.orderBy) {
/* 114 */         queryOrderBy.expression.mapColumns(paramColumnResolver, paramInt, 1);
/*     */       }
/*     */     }
/* 117 */     if (this.frame != null) {
/* 118 */       this.frame.mapColumns(paramColumnResolver, paramInt, 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private void resolveWindows(ColumnResolver paramColumnResolver) {
/* 123 */     if (this.parent != null) {
/* 124 */       Select select = paramColumnResolver.getSelect();
/*     */       Window window;
/* 126 */       while ((window = select.getWindow(this.parent)) == null) {
/* 127 */         select = select.getParentSelect();
/* 128 */         if (select == null) {
/* 129 */           throw DbException.get(90136, this.parent);
/*     */         }
/*     */       } 
/* 132 */       window.resolveWindows(paramColumnResolver);
/* 133 */       if (this.partitionBy == null) {
/* 134 */         this.partitionBy = window.partitionBy;
/*     */       }
/* 136 */       if (this.orderBy == null) {
/* 137 */         this.orderBy = window.orderBy;
/*     */       }
/* 139 */       if (this.frame == null) {
/* 140 */         this.frame = window.frame;
/*     */       }
/* 142 */       this.parent = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void optimize(SessionLocal paramSessionLocal) {
/* 153 */     if (this.partitionBy != null) {
/* 154 */       for (ListIterator<Expression> listIterator = this.partitionBy.listIterator(); listIterator.hasNext(); ) {
/* 155 */         Expression expression = ((Expression)listIterator.next()).optimize(paramSessionLocal);
/* 156 */         if (expression.isConstant()) {
/* 157 */           listIterator.remove(); continue;
/*     */         } 
/* 159 */         listIterator.set(expression);
/*     */       } 
/*     */       
/* 162 */       if (this.partitionBy.isEmpty()) {
/* 163 */         this.partitionBy = null;
/*     */       }
/*     */     } 
/* 166 */     if (this.orderBy != null) {
/* 167 */       for (Iterator<QueryOrderBy> iterator = this.orderBy.iterator(); iterator.hasNext(); ) {
/* 168 */         QueryOrderBy queryOrderBy = iterator.next();
/* 169 */         Expression expression = queryOrderBy.expression.optimize(paramSessionLocal);
/* 170 */         if (expression.isConstant()) {
/* 171 */           iterator.remove(); continue;
/*     */         } 
/* 173 */         queryOrderBy.expression = expression;
/*     */       } 
/*     */       
/* 176 */       if (this.orderBy.isEmpty()) {
/* 177 */         this.orderBy = null;
/*     */       }
/*     */     } 
/* 180 */     if (this.frame != null) {
/* 181 */       this.frame.optimize(paramSessionLocal);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 196 */     if (this.partitionBy != null) {
/* 197 */       for (Expression expression : this.partitionBy) {
/* 198 */         expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/*     */     }
/* 201 */     if (this.orderBy != null) {
/* 202 */       for (QueryOrderBy queryOrderBy : this.orderBy) {
/* 203 */         queryOrderBy.expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<QueryOrderBy> getOrderBy() {
/* 214 */     return this.orderBy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrame getWindowFrame() {
/* 223 */     return this.frame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOrdered() {
/* 234 */     if (this.orderBy != null) {
/* 235 */       return true;
/*     */     }
/* 237 */     if (this.frame != null && this.frame.getUnits() == WindowFrameUnits.ROWS) {
/* 238 */       if (this.frame.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING) {
/* 239 */         WindowFrameBound windowFrameBound = this.frame.getFollowing();
/* 240 */         if (windowFrameBound != null && windowFrameBound.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING) {
/* 241 */           return false;
/*     */         }
/*     */       } 
/* 244 */       return true;
/*     */     } 
/* 246 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getCurrentKey(SessionLocal paramSessionLocal) {
/* 257 */     if (this.partitionBy == null) {
/* 258 */       return null;
/*     */     }
/* 260 */     int i = this.partitionBy.size();
/* 261 */     if (i == 1) {
/* 262 */       return ((Expression)this.partitionBy.get(0)).getValue(paramSessionLocal);
/*     */     }
/* 264 */     Value[] arrayOfValue = new Value[i];
/*     */     
/* 266 */     for (byte b = 0; b < i; b++) {
/* 267 */       Expression expression = this.partitionBy.get(b);
/* 268 */       arrayOfValue[b] = expression.getValue(paramSessionLocal);
/*     */     } 
/* 270 */     return (Value)ValueRow.get(arrayOfValue);
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
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean) {
/* 288 */     paramStringBuilder.append("OVER (");
/* 289 */     if (this.partitionBy != null) {
/* 290 */       paramStringBuilder.append("PARTITION BY ");
/* 291 */       for (byte b = 0; b < this.partitionBy.size(); b++) {
/* 292 */         if (b > 0) {
/* 293 */           paramStringBuilder.append(", ");
/*     */         }
/* 295 */         ((Expression)this.partitionBy.get(b)).getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */       } 
/*     */     } 
/* 298 */     appendOrderBy(paramStringBuilder, this.orderBy, paramInt, paramBoolean);
/* 299 */     if (this.frame != null) {
/* 300 */       if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) != '(') {
/* 301 */         paramStringBuilder.append(' ');
/*     */       }
/* 303 */       this.frame.getSQL(paramStringBuilder, paramInt);
/*     */     } 
/* 305 */     return paramStringBuilder.append(')');
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
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 318 */     if (this.partitionBy != null) {
/* 319 */       for (Expression expression : this.partitionBy) {
/* 320 */         expression.updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/*     */     }
/* 323 */     if (this.orderBy != null) {
/* 324 */       for (QueryOrderBy queryOrderBy : this.orderBy) {
/* 325 */         queryOrderBy.expression.updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/*     */     }
/* 328 */     if (this.frame != null) {
/* 329 */       this.frame.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 335 */     return getSQL(new StringBuilder(), 3, false).toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\Window.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */