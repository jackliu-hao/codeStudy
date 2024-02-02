/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.antlr.v4.runtime.misc.IntervalSet;
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
/*     */ public abstract class Transition
/*     */ {
/*     */   public static final int EPSILON = 1;
/*     */   public static final int RANGE = 2;
/*     */   public static final int RULE = 3;
/*     */   public static final int PREDICATE = 4;
/*     */   public static final int ATOM = 5;
/*     */   public static final int ACTION = 6;
/*     */   public static final int SET = 7;
/*     */   public static final int NOT_SET = 8;
/*     */   public static final int WILDCARD = 9;
/*     */   public static final int PRECEDENCE = 10;
/*  67 */   public static final List<String> serializationNames = Collections.unmodifiableList(Arrays.asList(new String[] { "INVALID", "EPSILON", "RANGE", "RULE", "PREDICATE", "ATOM", "ACTION", "SET", "NOT_SET", "WILDCARD", "PRECEDENCE" }));
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
/*  82 */   public static final Map<Class<? extends Transition>, Integer> serializationTypes = Collections.unmodifiableMap(new HashMap<Class<? extends Transition>, Integer>()
/*     */       {
/*     */       
/*     */       });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNState target;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Transition(ATNState target) {
/* 101 */     if (target == null) {
/* 102 */       throw new NullPointerException("target cannot be null.");
/*     */     }
/*     */     
/* 105 */     this.target = target;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getSerializationType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEpsilon() {
/* 120 */     return false;
/*     */   }
/*     */   
/*     */   public IntervalSet label() {
/* 124 */     return null;
/*     */   }
/*     */   
/*     */   public abstract boolean matches(int paramInt1, int paramInt2, int paramInt3);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\Transition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */