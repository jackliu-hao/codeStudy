/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.antlr.v4.runtime.Recognizer;
/*     */ import org.antlr.v4.runtime.RuleContext;
/*     */ import org.antlr.v4.runtime.misc.DoubleKeyMap;
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
/*     */ public abstract class PredictionContext
/*     */ {
/*  52 */   public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EMPTY_RETURN_STATE = 2147483647;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int INITIAL_HASH = 1;
/*     */ 
/*     */   
/*  63 */   public static int globalNodeCount = 0;
/*  64 */   public final int id = globalNodeCount++;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int cachedHashCode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PredictionContext(int cachedHashCode) {
/*  90 */     this.cachedHashCode = cachedHashCode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PredictionContext fromRuleContext(ATN atn, RuleContext outerContext) {
/*  97 */     if (outerContext == null) outerContext = RuleContext.EMPTY;
/*     */ 
/*     */ 
/*     */     
/* 101 */     if (outerContext.parent == null || outerContext == RuleContext.EMPTY) {
/* 102 */       return EMPTY;
/*     */     }
/*     */ 
/*     */     
/* 106 */     PredictionContext parent = EMPTY;
/* 107 */     parent = fromRuleContext(atn, outerContext.parent);
/*     */     
/* 109 */     ATNState state = atn.states.get(outerContext.invokingState);
/* 110 */     RuleTransition transition = (RuleTransition)state.transition(0);
/* 111 */     return SingletonPredictionContext.create(parent, transition.followState.stateNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract int size();
/*     */   
/*     */   public abstract PredictionContext getParent(int paramInt);
/*     */   
/*     */   public abstract int getReturnState(int paramInt);
/*     */   
/*     */   public boolean isEmpty() {
/* 122 */     return (this == EMPTY);
/*     */   }
/*     */   
/*     */   public boolean hasEmptyPath() {
/* 126 */     return (getReturnState(size() - 1) == Integer.MAX_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 131 */     return this.cachedHashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract boolean equals(Object paramObject);
/*     */   
/*     */   protected static int calculateEmptyHashCode() {
/* 138 */     int hash = MurmurHash.initialize(1);
/* 139 */     hash = MurmurHash.finish(hash, 0);
/* 140 */     return hash;
/*     */   }
/*     */   
/*     */   protected static int calculateHashCode(PredictionContext parent, int returnState) {
/* 144 */     int hash = MurmurHash.initialize(1);
/* 145 */     hash = MurmurHash.update(hash, parent);
/* 146 */     hash = MurmurHash.update(hash, returnState);
/* 147 */     hash = MurmurHash.finish(hash, 2);
/* 148 */     return hash;
/*     */   }
/*     */   
/*     */   protected static int calculateHashCode(PredictionContext[] parents, int[] returnStates) {
/* 152 */     int hash = MurmurHash.initialize(1);
/*     */     
/* 154 */     for (PredictionContext parent : parents) {
/* 155 */       hash = MurmurHash.update(hash, parent);
/*     */     }
/*     */     
/* 158 */     for (int returnState : returnStates) {
/* 159 */       hash = MurmurHash.update(hash, returnState);
/*     */     }
/*     */     
/* 162 */     hash = MurmurHash.finish(hash, 2 * parents.length);
/* 163 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PredictionContext merge(PredictionContext a, PredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
/* 172 */     assert a != null && b != null;
/*     */ 
/*     */     
/* 175 */     if (a == b || a.equals(b)) return a;
/*     */     
/* 177 */     if (a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
/* 178 */       return mergeSingletons((SingletonPredictionContext)a, (SingletonPredictionContext)b, rootIsWildcard, mergeCache);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 185 */     if (rootIsWildcard) {
/* 186 */       if (a instanceof EmptyPredictionContext) return a; 
/* 187 */       if (b instanceof EmptyPredictionContext) return b;
/*     */     
/*     */     } 
/*     */     
/* 191 */     if (a instanceof SingletonPredictionContext) {
/* 192 */       a = new ArrayPredictionContext((SingletonPredictionContext)a);
/*     */     }
/* 194 */     if (b instanceof SingletonPredictionContext) {
/* 195 */       b = new ArrayPredictionContext((SingletonPredictionContext)b);
/*     */     }
/* 197 */     return mergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b, rootIsWildcard, mergeCache);
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
/*     */   public static PredictionContext mergeSingletons(SingletonPredictionContext a, SingletonPredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
/* 234 */     if (mergeCache != null) {
/* 235 */       PredictionContext previous = mergeCache.get(a, b);
/* 236 */       if (previous != null) return previous; 
/* 237 */       previous = mergeCache.get(b, a);
/* 238 */       if (previous != null) return previous;
/*     */     
/*     */     } 
/* 241 */     PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
/* 242 */     if (rootMerge != null) {
/* 243 */       if (mergeCache != null) mergeCache.put(a, b, rootMerge); 
/* 244 */       return rootMerge;
/*     */     } 
/*     */     
/* 247 */     if (a.returnState == b.returnState) {
/* 248 */       PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard, mergeCache);
/*     */       
/* 250 */       if (parent == a.parent) return a; 
/* 251 */       if (parent == b.parent) return b;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 256 */       PredictionContext predictionContext1 = SingletonPredictionContext.create(parent, a.returnState);
/* 257 */       if (mergeCache != null) mergeCache.put(a, b, predictionContext1); 
/* 258 */       return predictionContext1;
/*     */     } 
/*     */ 
/*     */     
/* 262 */     PredictionContext singleParent = null;
/* 263 */     if (a == b || (a.parent != null && a.parent.equals(b.parent))) {
/* 264 */       singleParent = a.parent;
/*     */     }
/* 266 */     if (singleParent != null) {
/*     */       
/* 268 */       int[] arrayOfInt = { a.returnState, b.returnState };
/* 269 */       if (a.returnState > b.returnState) {
/* 270 */         arrayOfInt[0] = b.returnState;
/* 271 */         arrayOfInt[1] = a.returnState;
/*     */       } 
/* 273 */       PredictionContext[] arrayOfPredictionContext = { singleParent, singleParent };
/* 274 */       PredictionContext predictionContext = new ArrayPredictionContext(arrayOfPredictionContext, arrayOfInt);
/* 275 */       if (mergeCache != null) mergeCache.put(a, b, predictionContext); 
/* 276 */       return predictionContext;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 281 */     int[] payloads = { a.returnState, b.returnState };
/* 282 */     PredictionContext[] parents = { a.parent, b.parent };
/* 283 */     if (a.returnState > b.returnState) {
/* 284 */       payloads[0] = b.returnState;
/* 285 */       payloads[1] = a.returnState;
/* 286 */       parents = new PredictionContext[] { b.parent, a.parent };
/*     */     } 
/* 288 */     PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
/* 289 */     if (mergeCache != null) mergeCache.put(a, b, a_); 
/* 290 */     return a_;
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
/*     */   public static PredictionContext mergeRoot(SingletonPredictionContext a, SingletonPredictionContext b, boolean rootIsWildcard) {
/* 336 */     if (rootIsWildcard) {
/* 337 */       if (a == EMPTY) return EMPTY; 
/* 338 */       if (b == EMPTY) return EMPTY;
/*     */     
/*     */     } else {
/* 341 */       if (a == EMPTY && b == EMPTY) return EMPTY; 
/* 342 */       if (a == EMPTY) {
/* 343 */         int[] payloads = { b.returnState, Integer.MAX_VALUE };
/* 344 */         PredictionContext[] parents = { b.parent, null };
/* 345 */         PredictionContext joined = new ArrayPredictionContext(parents, payloads);
/*     */         
/* 347 */         return joined;
/*     */       } 
/* 349 */       if (b == EMPTY) {
/* 350 */         int[] payloads = { a.returnState, Integer.MAX_VALUE };
/* 351 */         PredictionContext[] parents = { a.parent, null };
/* 352 */         PredictionContext joined = new ArrayPredictionContext(parents, payloads);
/*     */         
/* 354 */         return joined;
/*     */       } 
/*     */     } 
/* 357 */     return null;
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
/*     */   public static PredictionContext mergeArrays(ArrayPredictionContext a, ArrayPredictionContext b, boolean rootIsWildcard, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
/* 385 */     if (mergeCache != null) {
/* 386 */       PredictionContext previous = mergeCache.get(a, b);
/* 387 */       if (previous != null) return previous; 
/* 388 */       previous = mergeCache.get(b, a);
/* 389 */       if (previous != null) return previous;
/*     */     
/*     */     } 
/*     */     
/* 393 */     int i = 0;
/* 394 */     int j = 0;
/* 395 */     int k = 0;
/*     */     
/* 397 */     int[] mergedReturnStates = new int[a.returnStates.length + b.returnStates.length];
/*     */     
/* 399 */     PredictionContext[] mergedParents = new PredictionContext[a.returnStates.length + b.returnStates.length];
/*     */ 
/*     */     
/* 402 */     while (i < a.returnStates.length && j < b.returnStates.length) {
/* 403 */       PredictionContext a_parent = a.parents[i];
/* 404 */       PredictionContext b_parent = b.parents[j];
/* 405 */       if (a.returnStates[i] == b.returnStates[j]) {
/*     */         
/* 407 */         int payload = a.returnStates[i];
/*     */         
/* 409 */         boolean both$ = (payload == Integer.MAX_VALUE && a_parent == null && b_parent == null);
/*     */         
/* 411 */         boolean ax_ax = (a_parent != null && b_parent != null && a_parent.equals(b_parent));
/*     */         
/* 413 */         if (both$ || ax_ax) {
/* 414 */           mergedParents[k] = a_parent;
/* 415 */           mergedReturnStates[k] = payload;
/*     */         } else {
/*     */           
/* 418 */           PredictionContext mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache);
/*     */           
/* 420 */           mergedParents[k] = mergedParent;
/* 421 */           mergedReturnStates[k] = payload;
/*     */         } 
/* 423 */         i++;
/* 424 */         j++;
/*     */       }
/* 426 */       else if (a.returnStates[i] < b.returnStates[j]) {
/* 427 */         mergedParents[k] = a_parent;
/* 428 */         mergedReturnStates[k] = a.returnStates[i];
/* 429 */         i++;
/*     */       } else {
/*     */         
/* 432 */         mergedParents[k] = b_parent;
/* 433 */         mergedReturnStates[k] = b.returnStates[j];
/* 434 */         j++;
/*     */       } 
/* 436 */       k++;
/*     */     } 
/*     */ 
/*     */     
/* 440 */     if (i < a.returnStates.length) {
/* 441 */       for (int p = i; p < a.returnStates.length; p++) {
/* 442 */         mergedParents[k] = a.parents[p];
/* 443 */         mergedReturnStates[k] = a.returnStates[p];
/* 444 */         k++;
/*     */       } 
/*     */     } else {
/*     */       
/* 448 */       for (int p = j; p < b.returnStates.length; p++) {
/* 449 */         mergedParents[k] = b.parents[p];
/* 450 */         mergedReturnStates[k] = b.returnStates[p];
/* 451 */         k++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 456 */     if (k < mergedParents.length) {
/* 457 */       if (k == 1) {
/* 458 */         PredictionContext a_ = SingletonPredictionContext.create(mergedParents[0], mergedReturnStates[0]);
/*     */ 
/*     */         
/* 461 */         if (mergeCache != null) mergeCache.put(a, b, a_); 
/* 462 */         return a_;
/*     */       } 
/* 464 */       mergedParents = Arrays.<PredictionContext>copyOf(mergedParents, k);
/* 465 */       mergedReturnStates = Arrays.copyOf(mergedReturnStates, k);
/*     */     } 
/*     */     
/* 468 */     PredictionContext M = new ArrayPredictionContext(mergedParents, mergedReturnStates);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 473 */     if (M.equals(a)) {
/* 474 */       if (mergeCache != null) mergeCache.put(a, b, a); 
/* 475 */       return a;
/*     */     } 
/* 477 */     if (M.equals(b)) {
/* 478 */       if (mergeCache != null) mergeCache.put(a, b, b); 
/* 479 */       return b;
/*     */     } 
/*     */     
/* 482 */     combineCommonParents(mergedParents);
/*     */     
/* 484 */     if (mergeCache != null) mergeCache.put(a, b, M); 
/* 485 */     return M;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void combineCommonParents(PredictionContext[] parents) {
/* 493 */     Map<PredictionContext, PredictionContext> uniqueParents = new HashMap<PredictionContext, PredictionContext>();
/*     */     
/*     */     int p;
/* 496 */     for (p = 0; p < parents.length; p++) {
/* 497 */       PredictionContext parent = parents[p];
/* 498 */       if (!uniqueParents.containsKey(parent)) {
/* 499 */         uniqueParents.put(parent, parent);
/*     */       }
/*     */     } 
/*     */     
/* 503 */     for (p = 0; p < parents.length; p++) {
/* 504 */       parents[p] = uniqueParents.get(parents[p]);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String toDOTString(PredictionContext context) {
/* 509 */     if (context == null) return ""; 
/* 510 */     StringBuilder buf = new StringBuilder();
/* 511 */     buf.append("digraph G {\n");
/* 512 */     buf.append("rankdir=LR;\n");
/*     */     
/* 514 */     List<PredictionContext> nodes = getAllContextNodes(context);
/* 515 */     Collections.sort(nodes, new Comparator<PredictionContext>()
/*     */         {
/*     */           public int compare(PredictionContext o1, PredictionContext o2) {
/* 518 */             return o1.id - o2.id;
/*     */           }
/*     */         });
/*     */     
/* 522 */     for (PredictionContext current : nodes) {
/* 523 */       if (current instanceof SingletonPredictionContext) {
/* 524 */         String s = String.valueOf(current.id);
/* 525 */         buf.append("  s").append(s);
/* 526 */         String returnState = String.valueOf(current.getReturnState(0));
/* 527 */         if (current instanceof EmptyPredictionContext) returnState = "$"; 
/* 528 */         buf.append(" [label=\"").append(returnState).append("\"];\n");
/*     */         continue;
/*     */       } 
/* 531 */       ArrayPredictionContext arr = (ArrayPredictionContext)current;
/* 532 */       buf.append("  s").append(arr.id);
/* 533 */       buf.append(" [shape=box, label=\"");
/* 534 */       buf.append("[");
/* 535 */       boolean first = true;
/* 536 */       for (int inv : arr.returnStates) {
/* 537 */         if (!first) buf.append(", "); 
/* 538 */         if (inv == Integer.MAX_VALUE) { buf.append("$"); }
/* 539 */         else { buf.append(inv); }
/* 540 */          first = false;
/*     */       } 
/* 542 */       buf.append("]");
/* 543 */       buf.append("\"];\n");
/*     */     } 
/*     */     
/* 546 */     for (PredictionContext current : nodes) {
/* 547 */       if (current == EMPTY)
/* 548 */         continue;  for (int i = 0; i < current.size(); i++) {
/* 549 */         if (current.getParent(i) != null) {
/* 550 */           String s = String.valueOf(current.id);
/* 551 */           buf.append("  s").append(s);
/* 552 */           buf.append("->");
/* 553 */           buf.append("s");
/* 554 */           buf.append((current.getParent(i)).id);
/* 555 */           if (current.size() > 1) { buf.append(" [label=\"parent[" + i + "]\"];\n"); }
/* 556 */           else { buf.append(";\n"); }
/*     */         
/*     */         } 
/*     */       } 
/* 560 */     }  buf.append("}\n");
/* 561 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PredictionContext getCachedContext(PredictionContext context, PredictionContextCache contextCache, IdentityHashMap<PredictionContext, PredictionContext> visited) {
/*     */     PredictionContext updated;
/* 570 */     if (context.isEmpty()) {
/* 571 */       return context;
/*     */     }
/*     */     
/* 574 */     PredictionContext existing = visited.get(context);
/* 575 */     if (existing != null) {
/* 576 */       return existing;
/*     */     }
/*     */     
/* 579 */     existing = contextCache.get(context);
/* 580 */     if (existing != null) {
/* 581 */       visited.put(context, existing);
/* 582 */       return existing;
/*     */     } 
/*     */     
/* 585 */     boolean changed = false;
/* 586 */     PredictionContext[] parents = new PredictionContext[context.size()];
/* 587 */     for (int i = 0; i < parents.length; i++) {
/* 588 */       PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
/* 589 */       if (changed || parent != context.getParent(i)) {
/* 590 */         if (!changed) {
/* 591 */           parents = new PredictionContext[context.size()];
/* 592 */           for (int j = 0; j < context.size(); j++) {
/* 593 */             parents[j] = context.getParent(j);
/*     */           }
/*     */           
/* 596 */           changed = true;
/*     */         } 
/*     */         
/* 599 */         parents[i] = parent;
/*     */       } 
/*     */     } 
/*     */     
/* 603 */     if (!changed) {
/* 604 */       contextCache.add(context);
/* 605 */       visited.put(context, context);
/* 606 */       return context;
/*     */     } 
/*     */ 
/*     */     
/* 610 */     if (parents.length == 0) {
/* 611 */       updated = EMPTY;
/*     */     }
/* 613 */     else if (parents.length == 1) {
/* 614 */       updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0));
/*     */     } else {
/*     */       
/* 617 */       ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
/* 618 */       updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
/*     */     } 
/*     */     
/* 621 */     contextCache.add(updated);
/* 622 */     visited.put(updated, updated);
/* 623 */     visited.put(context, updated);
/*     */     
/* 625 */     return updated;
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
/*     */   public static List<PredictionContext> getAllContextNodes(PredictionContext context) {
/* 652 */     List<PredictionContext> nodes = new ArrayList<PredictionContext>();
/* 653 */     Map<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
/*     */     
/* 655 */     getAllContextNodes_(context, nodes, visited);
/* 656 */     return nodes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void getAllContextNodes_(PredictionContext context, List<PredictionContext> nodes, Map<PredictionContext, PredictionContext> visited) {
/* 663 */     if (context == null || visited.containsKey(context))
/* 664 */       return;  visited.put(context, context);
/* 665 */     nodes.add(context);
/* 666 */     for (int i = 0; i < context.size(); i++) {
/* 667 */       getAllContextNodes_(context.getParent(i), nodes, visited);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString(Recognizer<?, ?> recog) {
/* 672 */     return toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] toStrings(Recognizer<?, ?> recognizer, int currentState) {
/* 677 */     return toStrings(recognizer, EMPTY, currentState);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] toStrings(Recognizer<?, ?> recognizer, PredictionContext stop, int currentState) {
/* 682 */     List<String> result = new ArrayList<String>();
/*     */     
/*     */     int perm;
/* 685 */     label40: for (perm = 0;; perm++) {
/* 686 */       int i, offset = 0;
/* 687 */       boolean last = true;
/* 688 */       PredictionContext p = this;
/* 689 */       int stateNumber = currentState;
/* 690 */       StringBuilder localBuffer = new StringBuilder();
/* 691 */       localBuffer.append("[");
/* 692 */       while (!p.isEmpty() && p != stop) {
/* 693 */         int index = 0;
/* 694 */         if (p.size() > 0) {
/* 695 */           int bits = 1;
/* 696 */           while (1 << bits < p.size()) {
/* 697 */             bits++;
/*     */           }
/*     */           
/* 700 */           int mask = (1 << bits) - 1;
/* 701 */           index = perm >> offset & mask;
/* 702 */           i = last & ((index >= p.size() - 1) ? 1 : 0);
/* 703 */           if (index >= p.size()) {
/*     */             continue label40;
/*     */           }
/* 706 */           offset += bits;
/*     */         } 
/*     */         
/* 709 */         if (recognizer != null) {
/* 710 */           if (localBuffer.length() > 1)
/*     */           {
/* 712 */             localBuffer.append(' ');
/*     */           }
/*     */           
/* 715 */           ATN atn = recognizer.getATN();
/* 716 */           ATNState s = atn.states.get(stateNumber);
/* 717 */           String ruleName = recognizer.getRuleNames()[s.ruleIndex];
/* 718 */           localBuffer.append(ruleName);
/*     */         }
/* 720 */         else if (p.getReturnState(index) != Integer.MAX_VALUE && 
/* 721 */           !p.isEmpty()) {
/* 722 */           if (localBuffer.length() > 1)
/*     */           {
/* 724 */             localBuffer.append(' ');
/*     */           }
/*     */           
/* 727 */           localBuffer.append(p.getReturnState(index));
/*     */         } 
/*     */         
/* 730 */         stateNumber = p.getReturnState(index);
/* 731 */         p = p.getParent(index);
/*     */       } 
/* 733 */       localBuffer.append("]");
/* 734 */       result.add(localBuffer.toString());
/*     */       
/* 736 */       if (i != 0) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 741 */     return result.<String>toArray(new String[result.size()]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\PredictionContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */