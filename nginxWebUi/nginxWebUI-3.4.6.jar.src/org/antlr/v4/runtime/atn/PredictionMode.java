/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
/*     */ import org.antlr.v4.runtime.misc.FlexibleHashMap;
/*     */ import org.antlr.v4.runtime.misc.MurmurHash;
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
/*     */ public enum PredictionMode
/*     */ {
/*  70 */   SLL,
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
/*  89 */   LL,
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
/* 107 */   LL_EXACT_AMBIG_DETECTION;
/*     */   
/*     */   static class AltAndContextMap
/*     */     extends FlexibleHashMap<ATNConfig, BitSet> {
/*     */     public AltAndContextMap() {
/* 112 */       super(PredictionMode.AltAndContextConfigEqualityComparator.INSTANCE);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class AltAndContextConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig> {
/* 117 */     public static final AltAndContextConfigEqualityComparator INSTANCE = new AltAndContextConfigEqualityComparator();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode(ATNConfig o) {
/* 128 */       int hashCode = MurmurHash.initialize(7);
/* 129 */       hashCode = MurmurHash.update(hashCode, o.state.stateNumber);
/* 130 */       hashCode = MurmurHash.update(hashCode, o.context);
/* 131 */       hashCode = MurmurHash.finish(hashCode, 2);
/* 132 */       return hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(ATNConfig a, ATNConfig b) {
/* 137 */       if (a == b) return true; 
/* 138 */       if (a == null || b == null) return false; 
/* 139 */       return (a.state.stateNumber == b.state.stateNumber && a.context.equals(b.context));
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
/*     */   public static boolean hasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet configs) {
/* 242 */     if (allConfigsInRuleStopStates(configs)) {
/* 243 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 247 */     if (mode == SLL)
/*     */     {
/*     */ 
/*     */       
/* 251 */       if (configs.hasSemanticContext) {
/*     */         
/* 253 */         ATNConfigSet dup = new ATNConfigSet();
/* 254 */         for (ATNConfig c : configs) {
/* 255 */           c = new ATNConfig(c, SemanticContext.NONE);
/* 256 */           dup.add(c);
/*     */         } 
/* 258 */         configs = dup;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 265 */     Collection<BitSet> altsets = getConflictingAltSubsets(configs);
/* 266 */     boolean heuristic = (hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs));
/*     */     
/* 268 */     return heuristic;
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
/*     */   public static boolean hasConfigInRuleStopState(ATNConfigSet configs) {
/* 282 */     for (ATNConfig c : configs) {
/* 283 */       if (c.state instanceof RuleStopState) {
/* 284 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 288 */     return false;
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
/*     */   public static boolean allConfigsInRuleStopStates(ATNConfigSet configs) {
/* 302 */     for (ATNConfig config : configs) {
/* 303 */       if (!(config.state instanceof RuleStopState)) {
/* 304 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 308 */     return true;
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
/*     */   public static int resolvesToJustOneViableAlt(Collection<BitSet> altsets) {
/* 453 */     return getSingleViableAlt(altsets);
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
/*     */   public static boolean allSubsetsConflict(Collection<BitSet> altsets) {
/* 465 */     return !hasNonConflictingAltSet(altsets);
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
/*     */   public static boolean hasNonConflictingAltSet(Collection<BitSet> altsets) {
/* 477 */     for (BitSet alts : altsets) {
/* 478 */       if (alts.cardinality() == 1) {
/* 479 */         return true;
/*     */       }
/*     */     } 
/* 482 */     return false;
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
/*     */   public static boolean hasConflictingAltSet(Collection<BitSet> altsets) {
/* 494 */     for (BitSet alts : altsets) {
/* 495 */       if (alts.cardinality() > 1) {
/* 496 */         return true;
/*     */       }
/*     */     } 
/* 499 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean allSubsetsEqual(Collection<BitSet> altsets) {
/* 510 */     Iterator<BitSet> it = altsets.iterator();
/* 511 */     BitSet first = it.next();
/* 512 */     while (it.hasNext()) {
/* 513 */       BitSet next = it.next();
/* 514 */       if (!next.equals(first)) return false; 
/*     */     } 
/* 516 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getUniqueAlt(Collection<BitSet> altsets) {
/* 527 */     BitSet all = getAlts(altsets);
/* 528 */     if (all.cardinality() == 1) return all.nextSetBit(0); 
/* 529 */     return 0;
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
/*     */   public static BitSet getAlts(Collection<BitSet> altsets) {
/* 541 */     BitSet all = new BitSet();
/* 542 */     for (BitSet alts : altsets) {
/* 543 */       all.or(alts);
/*     */     }
/* 545 */     return all;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BitSet getAlts(ATNConfigSet configs) {
/* 550 */     BitSet alts = new BitSet();
/* 551 */     for (ATNConfig config : configs) {
/* 552 */       alts.set(config.alt);
/*     */     }
/* 554 */     return alts;
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
/*     */   public static Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
/* 567 */     AltAndContextMap configToAlts = new AltAndContextMap();
/* 568 */     for (ATNConfig c : configs) {
/* 569 */       BitSet alts = configToAlts.get(c);
/* 570 */       if (alts == null) {
/* 571 */         alts = new BitSet();
/* 572 */         configToAlts.put(c, alts);
/*     */       } 
/* 574 */       alts.set(c.alt);
/*     */     } 
/* 576 */     return configToAlts.values();
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
/*     */   public static Map<ATNState, BitSet> getStateToAltMap(ATNConfigSet configs) {
/* 588 */     Map<ATNState, BitSet> m = new HashMap<ATNState, BitSet>();
/* 589 */     for (ATNConfig c : configs) {
/* 590 */       BitSet alts = m.get(c.state);
/* 591 */       if (alts == null) {
/* 592 */         alts = new BitSet();
/* 593 */         m.put(c.state, alts);
/*     */       } 
/* 595 */       alts.set(c.alt);
/*     */     } 
/* 597 */     return m;
/*     */   }
/*     */   
/*     */   public static boolean hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
/* 601 */     Map<ATNState, BitSet> x = getStateToAltMap(configs);
/* 602 */     for (BitSet alts : x.values()) {
/* 603 */       if (alts.cardinality() == 1) return true; 
/*     */     } 
/* 605 */     return false;
/*     */   }
/*     */   
/*     */   public static int getSingleViableAlt(Collection<BitSet> altsets) {
/* 609 */     BitSet viableAlts = new BitSet();
/* 610 */     for (BitSet alts : altsets) {
/* 611 */       int minAlt = alts.nextSetBit(0);
/* 612 */       viableAlts.set(minAlt);
/* 613 */       if (viableAlts.cardinality() > 1) {
/* 614 */         return 0;
/*     */       }
/*     */     } 
/* 617 */     return viableAlts.nextSetBit(0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PredictionMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */