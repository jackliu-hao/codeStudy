/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.antlr.v4.runtime.dfa.DFAState;
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
/*     */ public abstract class ATNSimulator
/*     */ {
/*     */   @Deprecated
/*  47 */   public static final int SERIALIZED_VERSION = ATNDeserializer.SERIALIZED_VERSION;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  57 */   public static final UUID SERIALIZED_UUID = ATNDeserializer.SERIALIZED_UUID;
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
/*  89 */   public static final DFAState ERROR = new DFAState(new ATNConfigSet()); static {
/*  90 */     ERROR.stateNumber = Integer.MAX_VALUE;
/*     */   }
/*     */   public final ATN atn;
/*     */   protected final PredictionContextCache sharedContextCache;
/*     */   
/*     */   public ATNSimulator(ATN atn, PredictionContextCache sharedContextCache) {
/*  96 */     this.atn = atn;
/*  97 */     this.sharedContextCache = sharedContextCache;
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
/*     */   public void clearDFA() {
/* 114 */     throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
/*     */   }
/*     */   
/*     */   public PredictionContextCache getSharedContextCache() {
/* 118 */     return this.sharedContextCache;
/*     */   }
/*     */   
/*     */   public PredictionContext getCachedContext(PredictionContext context) {
/* 122 */     if (this.sharedContextCache == null) return context;
/*     */     
/* 124 */     synchronized (this.sharedContextCache) {
/* 125 */       IdentityHashMap<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
/*     */       
/* 127 */       return PredictionContext.getCachedContext(context, this.sharedContextCache, visited);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static ATN deserialize(char[] data) {
/* 138 */     return (new ATNDeserializer()).deserialize(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void checkCondition(boolean condition) {
/* 146 */     (new ATNDeserializer()).checkCondition(condition);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void checkCondition(boolean condition, String message) {
/* 154 */     (new ATNDeserializer()).checkCondition(condition, message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static int toInt(char c) {
/* 162 */     return ATNDeserializer.toInt(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static int toInt32(char[] data, int offset) {
/* 170 */     return ATNDeserializer.toInt32(data, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static long toLong(char[] data, int offset) {
/* 178 */     return ATNDeserializer.toLong(data, offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static UUID toUUID(char[] data, int offset) {
/* 186 */     return ATNDeserializer.toUUID(data, offset);
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
/*     */   @Deprecated
/*     */   public static Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
/* 199 */     return (new ATNDeserializer()).edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static ATNState stateFactory(int type, int ruleIndex) {
/* 207 */     return (new ATNDeserializer()).stateFactory(type, ruleIndex);
/*     */   }
/*     */   
/*     */   public abstract void reset();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNSimulator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */