/*     */ package org.h2.command.query;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.Random;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.table.Plan;
/*     */ import org.h2.table.PlanItem;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.Permutations;
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
/*     */ class Optimizer
/*     */ {
/*     */   private static final int MAX_BRUTE_FORCE_FILTERS = 7;
/*     */   private static final int MAX_BRUTE_FORCE = 2000;
/*     */   private static final int MAX_GENETIC = 500;
/*     */   private long startNs;
/*     */   private BitSet switched;
/*     */   private final TableFilter[] filters;
/*     */   private final Expression condition;
/*     */   private final SessionLocal session;
/*     */   private Plan bestPlan;
/*     */   private TableFilter topFilter;
/*     */   private double cost;
/*     */   private Random random;
/*     */   private final AllColumnsForPlan allColumnsSet;
/*     */   
/*     */   Optimizer(TableFilter[] paramArrayOfTableFilter, Expression paramExpression, SessionLocal paramSessionLocal) {
/*  52 */     this.filters = paramArrayOfTableFilter;
/*  53 */     this.condition = paramExpression;
/*  54 */     this.session = paramSessionLocal;
/*  55 */     this.allColumnsSet = new AllColumnsForPlan(paramArrayOfTableFilter);
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
/*     */   private static int getMaxBruteForceFilters(int paramInt) {
/*  69 */     byte b = 0; int i = paramInt, j = paramInt;
/*  70 */     while (i > 0 && j * i * (i - 1) / 2 < 2000) {
/*  71 */       i--;
/*  72 */       j *= i;
/*  73 */       b++;
/*     */     } 
/*  75 */     return b;
/*     */   }
/*     */   
/*     */   private void calculateBestPlan() {
/*  79 */     this.cost = -1.0D;
/*  80 */     if (this.filters.length == 1) {
/*  81 */       testPlan(this.filters);
/*     */     } else {
/*  83 */       this.startNs = System.nanoTime();
/*  84 */       if (this.filters.length <= 7) {
/*  85 */         calculateBruteForceAll();
/*     */       } else {
/*  87 */         calculateBruteForceSome();
/*  88 */         this.random = new Random(0L);
/*  89 */         calculateGenetic();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void calculateFakePlan() {
/*  95 */     this.cost = -1.0D;
/*  96 */     this.bestPlan = new Plan(this.filters, this.filters.length, this.condition);
/*     */   }
/*     */   
/*     */   private boolean canStop(int paramInt) {
/* 100 */     if ((paramInt & 0x7F) == 0 && this.cost >= 0.0D)
/*     */     {
/*     */ 
/*     */       
/* 104 */       if ((System.nanoTime() - this.startNs) > this.cost * 100000.0D); } 
/*     */     return false;
/*     */   }
/*     */   private void calculateBruteForceAll() {
/* 108 */     TableFilter[] arrayOfTableFilter = new TableFilter[this.filters.length];
/* 109 */     Permutations permutations = Permutations.create((Object[])this.filters, (Object[])arrayOfTableFilter);
/* 110 */     for (byte b = 0; !canStop(b) && permutations.next(); b++) {
/* 111 */       testPlan(arrayOfTableFilter);
/*     */     }
/*     */   }
/*     */   
/*     */   private void calculateBruteForceSome() {
/* 116 */     int i = getMaxBruteForceFilters(this.filters.length);
/* 117 */     TableFilter[] arrayOfTableFilter = new TableFilter[this.filters.length];
/* 118 */     Permutations permutations = Permutations.create((Object[])this.filters, (Object[])arrayOfTableFilter, i);
/* 119 */     for (byte b = 0; !canStop(b) && permutations.next(); b++) {
/*     */       
/* 121 */       for (TableFilter tableFilter : this.filters)
/* 122 */         tableFilter.setUsed(false); 
/*     */       int j;
/* 124 */       for (j = 0; j < i; j++) {
/* 125 */         arrayOfTableFilter[j].setUsed(true);
/*     */       }
/*     */       
/* 128 */       for (j = i; j < this.filters.length; j++) {
/* 129 */         double d = -1.0D;
/* 130 */         byte b1 = -1;
/* 131 */         for (byte b2 = 0; b2 < this.filters.length; b2++) {
/* 132 */           if (!this.filters[b2].isUsed()) {
/* 133 */             if (j == this.filters.length - 1) {
/* 134 */               b1 = b2;
/*     */               break;
/*     */             } 
/* 137 */             arrayOfTableFilter[j] = this.filters[b2];
/* 138 */             Plan plan = new Plan(arrayOfTableFilter, j + 1, this.condition);
/* 139 */             double d1 = plan.calculateCost(this.session, this.allColumnsSet);
/* 140 */             if (d < 0.0D || d1 < d) {
/* 141 */               d = d1;
/* 142 */               b1 = b2;
/*     */             } 
/*     */           } 
/*     */         } 
/* 146 */         this.filters[b1].setUsed(true);
/* 147 */         arrayOfTableFilter[j] = this.filters[b1];
/*     */       } 
/* 149 */       testPlan(arrayOfTableFilter);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void calculateGenetic() {
/* 154 */     TableFilter[] arrayOfTableFilter1 = new TableFilter[this.filters.length];
/* 155 */     TableFilter[] arrayOfTableFilter2 = new TableFilter[this.filters.length];
/* 156 */     for (byte b = 0; b < 'Ǵ' && 
/* 157 */       !canStop(b); b++) {
/*     */ 
/*     */       
/* 160 */       boolean bool = ((b & 0x7F) == 0) ? true : false;
/* 161 */       if (!bool) {
/* 162 */         System.arraycopy(arrayOfTableFilter1, 0, arrayOfTableFilter2, 0, this.filters.length);
/* 163 */         if (!shuffleTwo(arrayOfTableFilter2)) {
/* 164 */           bool = true;
/*     */         }
/*     */       } 
/* 167 */       if (bool) {
/* 168 */         this.switched = new BitSet();
/* 169 */         System.arraycopy(this.filters, 0, arrayOfTableFilter1, 0, this.filters.length);
/* 170 */         shuffleAll(arrayOfTableFilter1);
/* 171 */         System.arraycopy(arrayOfTableFilter1, 0, arrayOfTableFilter2, 0, this.filters.length);
/*     */       } 
/* 173 */       if (testPlan(arrayOfTableFilter2)) {
/* 174 */         this.switched = new BitSet();
/* 175 */         System.arraycopy(arrayOfTableFilter2, 0, arrayOfTableFilter1, 0, this.filters.length);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean testPlan(TableFilter[] paramArrayOfTableFilter) {
/* 181 */     Plan plan = new Plan(paramArrayOfTableFilter, paramArrayOfTableFilter.length, this.condition);
/* 182 */     double d = plan.calculateCost(this.session, this.allColumnsSet);
/* 183 */     if (this.cost < 0.0D || d < this.cost) {
/* 184 */       this.cost = d;
/* 185 */       this.bestPlan = plan;
/* 186 */       return true;
/*     */     } 
/* 188 */     return false;
/*     */   }
/*     */   
/*     */   private void shuffleAll(TableFilter[] paramArrayOfTableFilter) {
/* 192 */     for (byte b = 0; b < paramArrayOfTableFilter.length - 1; b++) {
/* 193 */       int i = b + this.random.nextInt(paramArrayOfTableFilter.length - b);
/* 194 */       if (i != b) {
/* 195 */         TableFilter tableFilter = paramArrayOfTableFilter[b];
/* 196 */         paramArrayOfTableFilter[b] = paramArrayOfTableFilter[i];
/* 197 */         paramArrayOfTableFilter[i] = tableFilter;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean shuffleTwo(TableFilter[] paramArrayOfTableFilter) {
/* 203 */     int i = 0, j = 0; byte b = 0;
/* 204 */     for (; b < 20; b++) {
/* 205 */       i = this.random.nextInt(paramArrayOfTableFilter.length);
/* 206 */       j = this.random.nextInt(paramArrayOfTableFilter.length);
/* 207 */       if (i != j) {
/*     */ 
/*     */         
/* 210 */         if (i < j) {
/* 211 */           int m = i;
/* 212 */           i = j;
/* 213 */           j = m;
/*     */         } 
/* 215 */         int k = i * paramArrayOfTableFilter.length + j;
/* 216 */         if (!this.switched.get(k)) {
/*     */ 
/*     */           
/* 219 */           this.switched.set(k); break;
/*     */         } 
/*     */       } 
/* 222 */     }  if (b == 20) {
/* 223 */       return false;
/*     */     }
/* 225 */     TableFilter tableFilter = paramArrayOfTableFilter[i];
/* 226 */     paramArrayOfTableFilter[i] = paramArrayOfTableFilter[j];
/* 227 */     paramArrayOfTableFilter[j] = tableFilter;
/* 228 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void optimize(boolean paramBoolean) {
/* 238 */     if (paramBoolean) {
/* 239 */       calculateFakePlan();
/*     */     } else {
/* 241 */       calculateBestPlan();
/* 242 */       this.bestPlan.removeUnusableIndexConditions();
/*     */     } 
/* 244 */     TableFilter[] arrayOfTableFilter = this.bestPlan.getFilters();
/* 245 */     this.topFilter = arrayOfTableFilter[0];
/* 246 */     for (byte b = 0; b < arrayOfTableFilter.length - 1; b++) {
/* 247 */       arrayOfTableFilter[b].addJoin(arrayOfTableFilter[b + 1], false, null);
/*     */     }
/* 249 */     if (paramBoolean) {
/*     */       return;
/*     */     }
/* 252 */     for (TableFilter tableFilter : arrayOfTableFilter) {
/* 253 */       PlanItem planItem = this.bestPlan.getItem(tableFilter);
/* 254 */       tableFilter.setPlanItem(planItem);
/*     */     } 
/*     */   }
/*     */   
/*     */   public TableFilter getTopFilter() {
/* 259 */     return this.topFilter;
/*     */   }
/*     */   
/*     */   double getCost() {
/* 263 */     return this.cost;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\Optimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */