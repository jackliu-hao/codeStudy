/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
/*     */ import org.antlr.v4.runtime.misc.Array2DHashSet;
/*     */ import org.antlr.v4.runtime.misc.DoubleKeyMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ATNConfigSet
/*     */   implements Set<ATNConfig>
/*     */ {
/*     */   public static class ConfigHashSet
/*     */     extends AbstractConfigHashSet
/*     */   {
/*     */     public ConfigHashSet() {
/*  60 */       super(ATNConfigSet.ConfigEqualityComparator.INSTANCE);
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class ConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig> {
/*  65 */     public static final ConfigEqualityComparator INSTANCE = new ConfigEqualityComparator();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode(ATNConfig o) {
/*  72 */       int hashCode = 7;
/*  73 */       hashCode = 31 * hashCode + o.state.stateNumber;
/*  74 */       hashCode = 31 * hashCode + o.alt;
/*  75 */       hashCode = 31 * hashCode + o.semanticContext.hashCode();
/*  76 */       return hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(ATNConfig a, ATNConfig b) {
/*  81 */       if (a == b) return true; 
/*  82 */       if (a == null || b == null) return false; 
/*  83 */       return (a.state.stateNumber == b.state.stateNumber && a.alt == b.alt && a.semanticContext.equals(b.semanticContext));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean readonly = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractConfigHashSet configLookup;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   public final ArrayList<ATNConfig> configs = new ArrayList<ATNConfig>(7);
/*     */ 
/*     */ 
/*     */   
/*     */   public int uniqueAlt;
/*     */ 
/*     */ 
/*     */   
/*     */   protected BitSet conflictingAlts;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasSemanticContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean dipsIntoOuterContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean fullCtx;
/*     */ 
/*     */   
/* 127 */   private int cachedHashCode = -1;
/*     */   
/*     */   public ATNConfigSet(boolean fullCtx) {
/* 130 */     this.configLookup = new ConfigHashSet();
/* 131 */     this.fullCtx = fullCtx;
/*     */   } public ATNConfigSet() {
/* 133 */     this(true);
/*     */   }
/*     */   public ATNConfigSet(ATNConfigSet old) {
/* 136 */     this(old.fullCtx);
/* 137 */     addAll(old);
/* 138 */     this.uniqueAlt = old.uniqueAlt;
/* 139 */     this.conflictingAlts = old.conflictingAlts;
/* 140 */     this.hasSemanticContext = old.hasSemanticContext;
/* 141 */     this.dipsIntoOuterContext = old.dipsIntoOuterContext;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(ATNConfig config) {
/* 146 */     return add(config, null);
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
/*     */   public boolean add(ATNConfig config, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
/* 163 */     if (this.readonly) throw new IllegalStateException("This set is readonly"); 
/* 164 */     if (config.semanticContext != SemanticContext.NONE) {
/* 165 */       this.hasSemanticContext = true;
/*     */     }
/* 167 */     if (config.getOuterContextDepth() > 0) {
/* 168 */       this.dipsIntoOuterContext = true;
/*     */     }
/* 170 */     ATNConfig existing = this.configLookup.getOrAdd(config);
/* 171 */     if (existing == config) {
/* 172 */       this.cachedHashCode = -1;
/* 173 */       this.configs.add(config);
/* 174 */       return true;
/*     */     } 
/*     */     
/* 177 */     boolean rootIsWildcard = !this.fullCtx;
/* 178 */     PredictionContext merged = PredictionContext.merge(existing.context, config.context, rootIsWildcard, mergeCache);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     existing.reachesIntoOuterContext = Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
/*     */ 
/*     */ 
/*     */     
/* 187 */     if (config.isPrecedenceFilterSuppressed()) {
/* 188 */       existing.setPrecedenceFilterSuppressed(true);
/*     */     }
/*     */     
/* 191 */     existing.context = merged;
/* 192 */     return true;
/*     */   }
/*     */   
/*     */   public List<ATNConfig> elements() {
/* 196 */     return this.configs;
/*     */   }
/*     */   public Set<ATNState> getStates() {
/* 199 */     Set<ATNState> states = new HashSet<ATNState>();
/* 200 */     for (ATNConfig c : this.configs) {
/* 201 */       states.add(c.state);
/*     */     }
/* 203 */     return states;
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
/*     */   public BitSet getAlts() {
/* 216 */     BitSet alts = new BitSet();
/* 217 */     for (ATNConfig config : this.configs) {
/* 218 */       alts.set(config.alt);
/*     */     }
/* 220 */     return alts;
/*     */   }
/*     */   
/*     */   public List<SemanticContext> getPredicates() {
/* 224 */     List<SemanticContext> preds = new ArrayList<SemanticContext>();
/* 225 */     for (ATNConfig c : this.configs) {
/* 226 */       if (c.semanticContext != SemanticContext.NONE) {
/* 227 */         preds.add(c.semanticContext);
/*     */       }
/*     */     } 
/* 230 */     return preds;
/*     */   }
/*     */   public ATNConfig get(int i) {
/* 233 */     return this.configs.get(i);
/*     */   }
/*     */   public void optimizeConfigs(ATNSimulator interpreter) {
/* 236 */     if (this.readonly) throw new IllegalStateException("This set is readonly"); 
/* 237 */     if (this.configLookup.isEmpty())
/*     */       return; 
/* 239 */     for (ATNConfig config : this.configs)
/*     */     {
/* 241 */       config.context = interpreter.getCachedContext(config.context);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends ATNConfig> coll) {
/* 249 */     for (ATNConfig c : coll) add(c); 
/* 250 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 255 */     if (o == this) {
/* 256 */       return true;
/*     */     }
/* 258 */     if (!(o instanceof ATNConfigSet)) {
/* 259 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 263 */     ATNConfigSet other = (ATNConfigSet)o;
/* 264 */     boolean same = (this.configs != null && this.configs.equals(other.configs) && this.fullCtx == other.fullCtx && this.uniqueAlt == other.uniqueAlt && this.conflictingAlts == other.conflictingAlts && this.hasSemanticContext == other.hasSemanticContext && this.dipsIntoOuterContext == other.dipsIntoOuterContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 273 */     return same;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 278 */     if (isReadonly()) {
/* 279 */       if (this.cachedHashCode == -1) {
/* 280 */         this.cachedHashCode = this.configs.hashCode();
/*     */       }
/*     */       
/* 283 */       return this.cachedHashCode;
/*     */     } 
/*     */     
/* 286 */     return this.configs.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 291 */     return this.configs.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 296 */     return this.configs.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 301 */     if (this.configLookup == null) {
/* 302 */       throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
/*     */     }
/*     */     
/* 305 */     return this.configLookup.contains(o);
/*     */   }
/*     */   
/*     */   public boolean containsFast(ATNConfig obj) {
/* 309 */     if (this.configLookup == null) {
/* 310 */       throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
/*     */     }
/*     */     
/* 313 */     return this.configLookup.containsFast(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<ATNConfig> iterator() {
/* 318 */     return this.configs.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 323 */     if (this.readonly) throw new IllegalStateException("This set is readonly"); 
/* 324 */     this.configs.clear();
/* 325 */     this.cachedHashCode = -1;
/* 326 */     this.configLookup.clear();
/*     */   }
/*     */   
/*     */   public boolean isReadonly() {
/* 330 */     return this.readonly;
/*     */   }
/*     */   
/*     */   public void setReadonly(boolean readonly) {
/* 334 */     this.readonly = readonly;
/* 335 */     this.configLookup = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 340 */     StringBuilder buf = new StringBuilder();
/* 341 */     buf.append(elements().toString());
/* 342 */     if (this.hasSemanticContext) buf.append(",hasSemanticContext=").append(this.hasSemanticContext); 
/* 343 */     if (this.uniqueAlt != 0) buf.append(",uniqueAlt=").append(this.uniqueAlt); 
/* 344 */     if (this.conflictingAlts != null) buf.append(",conflictingAlts=").append(this.conflictingAlts); 
/* 345 */     if (this.dipsIntoOuterContext) buf.append(",dipsIntoOuterContext"); 
/* 346 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ATNConfig[] toArray() {
/* 353 */     return this.configLookup.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/* 358 */     return (T[])this.configLookup.toArray((Object[])a);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 363 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/* 368 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 373 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 378 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   public static abstract class AbstractConfigHashSet
/*     */     extends Array2DHashSet<ATNConfig> {
/*     */     public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator) {
/* 384 */       this(comparator, 16, 2);
/*     */     }
/*     */     
/*     */     public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator, int initialCapacity, int initialBucketCapacity) {
/* 388 */       super(comparator, initialCapacity, initialBucketCapacity);
/*     */     }
/*     */ 
/*     */     
/*     */     protected final ATNConfig asElementType(Object o) {
/* 393 */       if (!(o instanceof ATNConfig)) {
/* 394 */         return null;
/*     */       }
/*     */       
/* 397 */       return (ATNConfig)o;
/*     */     }
/*     */ 
/*     */     
/*     */     protected final ATNConfig[][] createBuckets(int capacity) {
/* 402 */       return new ATNConfig[capacity][];
/*     */     }
/*     */ 
/*     */     
/*     */     protected final ATNConfig[] createBucket(int capacity) {
/* 407 */       return new ATNConfig[capacity];
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNConfigSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */