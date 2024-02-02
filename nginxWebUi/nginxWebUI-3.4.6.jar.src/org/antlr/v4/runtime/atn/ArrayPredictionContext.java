/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ public class ArrayPredictionContext
/*     */   extends PredictionContext
/*     */ {
/*     */   public final PredictionContext[] parents;
/*     */   public final int[] returnStates;
/*     */   
/*     */   public ArrayPredictionContext(SingletonPredictionContext a) {
/*  48 */     this(new PredictionContext[] { a.parent }, new int[] { a.returnState });
/*     */   }
/*     */   
/*     */   public ArrayPredictionContext(PredictionContext[] parents, int[] returnStates) {
/*  52 */     super(calculateHashCode(parents, returnStates));
/*  53 */     assert parents != null && parents.length > 0;
/*  54 */     assert returnStates != null && returnStates.length > 0;
/*     */     
/*  56 */     this.parents = parents;
/*  57 */     this.returnStates = returnStates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  64 */     return (this.returnStates[0] == Integer.MAX_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  69 */     return this.returnStates.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public PredictionContext getParent(int index) {
/*  74 */     return this.parents[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getReturnState(int index) {
/*  79 */     return this.returnStates[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  89 */     if (this == o) {
/*  90 */       return true;
/*     */     }
/*  92 */     if (!(o instanceof ArrayPredictionContext)) {
/*  93 */       return false;
/*     */     }
/*     */     
/*  96 */     if (hashCode() != o.hashCode()) {
/*  97 */       return false;
/*     */     }
/*     */     
/* 100 */     ArrayPredictionContext a = (ArrayPredictionContext)o;
/* 101 */     return (Arrays.equals(this.returnStates, a.returnStates) && Arrays.equals((Object[])this.parents, (Object[])a.parents));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 107 */     if (isEmpty()) return "[]"; 
/* 108 */     StringBuilder buf = new StringBuilder();
/* 109 */     buf.append("[");
/* 110 */     for (int i = 0; i < this.returnStates.length; i++) {
/* 111 */       if (i > 0) buf.append(", "); 
/* 112 */       if (this.returnStates[i] == Integer.MAX_VALUE) {
/* 113 */         buf.append("$");
/*     */       } else {
/*     */         
/* 116 */         buf.append(this.returnStates[i]);
/* 117 */         if (this.parents[i] != null) {
/* 118 */           buf.append(' ');
/* 119 */           buf.append(this.parents[i].toString());
/*     */         } else {
/*     */           
/* 122 */           buf.append("null");
/*     */         } 
/*     */       } 
/* 125 */     }  buf.append("]");
/* 126 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ArrayPredictionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */