package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.antlr.v4.runtime.misc.DoubleKeyMap;

public class ATNConfigSet implements Set<ATNConfig> {
   protected boolean readonly;
   public AbstractConfigHashSet configLookup;
   public final ArrayList<ATNConfig> configs;
   public int uniqueAlt;
   protected BitSet conflictingAlts;
   public boolean hasSemanticContext;
   public boolean dipsIntoOuterContext;
   public final boolean fullCtx;
   private int cachedHashCode;

   public ATNConfigSet(boolean fullCtx) {
      this.readonly = false;
      this.configs = new ArrayList(7);
      this.cachedHashCode = -1;
      this.configLookup = new ConfigHashSet();
      this.fullCtx = fullCtx;
   }

   public ATNConfigSet() {
      this(true);
   }

   public ATNConfigSet(ATNConfigSet old) {
      this(old.fullCtx);
      this.addAll(old);
      this.uniqueAlt = old.uniqueAlt;
      this.conflictingAlts = old.conflictingAlts;
      this.hasSemanticContext = old.hasSemanticContext;
      this.dipsIntoOuterContext = old.dipsIntoOuterContext;
   }

   public boolean add(ATNConfig config) {
      return this.add(config, (DoubleKeyMap)null);
   }

   public boolean add(ATNConfig config, DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache) {
      if (this.readonly) {
         throw new IllegalStateException("This set is readonly");
      } else {
         if (config.semanticContext != SemanticContext.NONE) {
            this.hasSemanticContext = true;
         }

         if (config.getOuterContextDepth() > 0) {
            this.dipsIntoOuterContext = true;
         }

         ATNConfig existing = (ATNConfig)this.configLookup.getOrAdd(config);
         if (existing == config) {
            this.cachedHashCode = -1;
            this.configs.add(config);
            return true;
         } else {
            boolean rootIsWildcard = !this.fullCtx;
            PredictionContext merged = PredictionContext.merge(existing.context, config.context, rootIsWildcard, mergeCache);
            existing.reachesIntoOuterContext = Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
            if (config.isPrecedenceFilterSuppressed()) {
               existing.setPrecedenceFilterSuppressed(true);
            }

            existing.context = merged;
            return true;
         }
      }
   }

   public List<ATNConfig> elements() {
      return this.configs;
   }

   public Set<ATNState> getStates() {
      Set<ATNState> states = new HashSet();
      Iterator i$ = this.configs.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         states.add(c.state);
      }

      return states;
   }

   public BitSet getAlts() {
      BitSet alts = new BitSet();
      Iterator i$ = this.configs.iterator();

      while(i$.hasNext()) {
         ATNConfig config = (ATNConfig)i$.next();
         alts.set(config.alt);
      }

      return alts;
   }

   public List<SemanticContext> getPredicates() {
      List<SemanticContext> preds = new ArrayList();
      Iterator i$ = this.configs.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         if (c.semanticContext != SemanticContext.NONE) {
            preds.add(c.semanticContext);
         }
      }

      return preds;
   }

   public ATNConfig get(int i) {
      return (ATNConfig)this.configs.get(i);
   }

   public void optimizeConfigs(ATNSimulator interpreter) {
      if (this.readonly) {
         throw new IllegalStateException("This set is readonly");
      } else if (!this.configLookup.isEmpty()) {
         ATNConfig config;
         for(Iterator i$ = this.configs.iterator(); i$.hasNext(); config.context = interpreter.getCachedContext(config.context)) {
            config = (ATNConfig)i$.next();
         }

      }
   }

   public boolean addAll(Collection<? extends ATNConfig> coll) {
      Iterator i$ = coll.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         this.add(c);
      }

      return false;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ATNConfigSet)) {
         return false;
      } else {
         ATNConfigSet other = (ATNConfigSet)o;
         boolean same = this.configs != null && this.configs.equals(other.configs) && this.fullCtx == other.fullCtx && this.uniqueAlt == other.uniqueAlt && this.conflictingAlts == other.conflictingAlts && this.hasSemanticContext == other.hasSemanticContext && this.dipsIntoOuterContext == other.dipsIntoOuterContext;
         return same;
      }
   }

   public int hashCode() {
      if (this.isReadonly()) {
         if (this.cachedHashCode == -1) {
            this.cachedHashCode = this.configs.hashCode();
         }

         return this.cachedHashCode;
      } else {
         return this.configs.hashCode();
      }
   }

   public int size() {
      return this.configs.size();
   }

   public boolean isEmpty() {
      return this.configs.isEmpty();
   }

   public boolean contains(Object o) {
      if (this.configLookup == null) {
         throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
      } else {
         return this.configLookup.contains(o);
      }
   }

   public boolean containsFast(ATNConfig obj) {
      if (this.configLookup == null) {
         throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
      } else {
         return this.configLookup.containsFast(obj);
      }
   }

   public Iterator<ATNConfig> iterator() {
      return this.configs.iterator();
   }

   public void clear() {
      if (this.readonly) {
         throw new IllegalStateException("This set is readonly");
      } else {
         this.configs.clear();
         this.cachedHashCode = -1;
         this.configLookup.clear();
      }
   }

   public boolean isReadonly() {
      return this.readonly;
   }

   public void setReadonly(boolean readonly) {
      this.readonly = readonly;
      this.configLookup = null;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.elements().toString());
      if (this.hasSemanticContext) {
         buf.append(",hasSemanticContext=").append(this.hasSemanticContext);
      }

      if (this.uniqueAlt != 0) {
         buf.append(",uniqueAlt=").append(this.uniqueAlt);
      }

      if (this.conflictingAlts != null) {
         buf.append(",conflictingAlts=").append(this.conflictingAlts);
      }

      if (this.dipsIntoOuterContext) {
         buf.append(",dipsIntoOuterContext");
      }

      return buf.toString();
   }

   public ATNConfig[] toArray() {
      return (ATNConfig[])this.configLookup.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return this.configLookup.toArray(a);
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   public boolean containsAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public abstract static class AbstractConfigHashSet extends Array2DHashSet<ATNConfig> {
      public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator) {
         this(comparator, 16, 2);
      }

      public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator, int initialCapacity, int initialBucketCapacity) {
         super(comparator, initialCapacity, initialBucketCapacity);
      }

      protected final ATNConfig asElementType(Object o) {
         return !(o instanceof ATNConfig) ? null : (ATNConfig)o;
      }

      protected final ATNConfig[][] createBuckets(int capacity) {
         return new ATNConfig[capacity][];
      }

      protected final ATNConfig[] createBucket(int capacity) {
         return new ATNConfig[capacity];
      }
   }

   public static final class ConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig> {
      public static final ConfigEqualityComparator INSTANCE = new ConfigEqualityComparator();

      private ConfigEqualityComparator() {
      }

      public int hashCode(ATNConfig o) {
         int hashCode = 7;
         hashCode = 31 * hashCode + o.state.stateNumber;
         hashCode = 31 * hashCode + o.alt;
         hashCode = 31 * hashCode + o.semanticContext.hashCode();
         return hashCode;
      }

      public boolean equals(ATNConfig a, ATNConfig b) {
         if (a == b) {
            return true;
         } else if (a != null && b != null) {
            return a.state.stateNumber == b.state.stateNumber && a.alt == b.alt && a.semanticContext.equals(b.semanticContext);
         } else {
            return false;
         }
      }
   }

   public static class ConfigHashSet extends AbstractConfigHashSet {
      public ConfigHashSet() {
         super(ATNConfigSet.ConfigEqualityComparator.INSTANCE);
      }
   }
}
