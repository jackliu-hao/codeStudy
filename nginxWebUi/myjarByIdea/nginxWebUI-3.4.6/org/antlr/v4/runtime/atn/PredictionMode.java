package org.antlr.v4.runtime.atn;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import org.antlr.v4.runtime.misc.FlexibleHashMap;
import org.antlr.v4.runtime.misc.MurmurHash;

public enum PredictionMode {
   SLL,
   LL,
   LL_EXACT_AMBIG_DETECTION;

   public static boolean hasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet configs) {
      if (allConfigsInRuleStopStates(configs)) {
         return true;
      } else {
         if (mode == SLL && configs.hasSemanticContext) {
            ATNConfigSet dup = new ATNConfigSet();
            Iterator i$ = configs.iterator();

            while(i$.hasNext()) {
               ATNConfig c = (ATNConfig)i$.next();
               c = new ATNConfig(c, SemanticContext.NONE);
               dup.add(c);
            }

            configs = dup;
         }

         Collection<BitSet> altsets = getConflictingAltSubsets(configs);
         boolean heuristic = hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
         return heuristic;
      }
   }

   public static boolean hasConfigInRuleStopState(ATNConfigSet configs) {
      Iterator i$ = configs.iterator();

      ATNConfig c;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         c = (ATNConfig)i$.next();
      } while(!(c.state instanceof RuleStopState));

      return true;
   }

   public static boolean allConfigsInRuleStopStates(ATNConfigSet configs) {
      Iterator i$ = configs.iterator();

      ATNConfig config;
      do {
         if (!i$.hasNext()) {
            return true;
         }

         config = (ATNConfig)i$.next();
      } while(config.state instanceof RuleStopState);

      return false;
   }

   public static int resolvesToJustOneViableAlt(Collection<BitSet> altsets) {
      return getSingleViableAlt(altsets);
   }

   public static boolean allSubsetsConflict(Collection<BitSet> altsets) {
      return !hasNonConflictingAltSet(altsets);
   }

   public static boolean hasNonConflictingAltSet(Collection<BitSet> altsets) {
      Iterator i$ = altsets.iterator();

      BitSet alts;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         alts = (BitSet)i$.next();
      } while(alts.cardinality() != 1);

      return true;
   }

   public static boolean hasConflictingAltSet(Collection<BitSet> altsets) {
      Iterator i$ = altsets.iterator();

      BitSet alts;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         alts = (BitSet)i$.next();
      } while(alts.cardinality() <= 1);

      return true;
   }

   public static boolean allSubsetsEqual(Collection<BitSet> altsets) {
      Iterator<BitSet> it = altsets.iterator();
      BitSet first = (BitSet)it.next();

      BitSet next;
      do {
         if (!it.hasNext()) {
            return true;
         }

         next = (BitSet)it.next();
      } while(next.equals(first));

      return false;
   }

   public static int getUniqueAlt(Collection<BitSet> altsets) {
      BitSet all = getAlts(altsets);
      return all.cardinality() == 1 ? all.nextSetBit(0) : 0;
   }

   public static BitSet getAlts(Collection<BitSet> altsets) {
      BitSet all = new BitSet();
      Iterator i$ = altsets.iterator();

      while(i$.hasNext()) {
         BitSet alts = (BitSet)i$.next();
         all.or(alts);
      }

      return all;
   }

   public static BitSet getAlts(ATNConfigSet configs) {
      BitSet alts = new BitSet();
      Iterator i$ = configs.iterator();

      while(i$.hasNext()) {
         ATNConfig config = (ATNConfig)i$.next();
         alts.set(config.alt);
      }

      return alts;
   }

   public static Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
      AltAndContextMap configToAlts = new AltAndContextMap();

      ATNConfig c;
      BitSet alts;
      for(Iterator i$ = configs.iterator(); i$.hasNext(); alts.set(c.alt)) {
         c = (ATNConfig)i$.next();
         alts = (BitSet)configToAlts.get(c);
         if (alts == null) {
            alts = new BitSet();
            configToAlts.put(c, alts);
         }
      }

      return configToAlts.values();
   }

   public static Map<ATNState, BitSet> getStateToAltMap(ATNConfigSet configs) {
      Map<ATNState, BitSet> m = new HashMap();

      ATNConfig c;
      BitSet alts;
      for(Iterator i$ = configs.iterator(); i$.hasNext(); alts.set(c.alt)) {
         c = (ATNConfig)i$.next();
         alts = (BitSet)m.get(c.state);
         if (alts == null) {
            alts = new BitSet();
            m.put(c.state, alts);
         }
      }

      return m;
   }

   public static boolean hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
      Map<ATNState, BitSet> x = getStateToAltMap(configs);
      Iterator i$ = x.values().iterator();

      BitSet alts;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         alts = (BitSet)i$.next();
      } while(alts.cardinality() != 1);

      return true;
   }

   public static int getSingleViableAlt(Collection<BitSet> altsets) {
      BitSet viableAlts = new BitSet();
      Iterator i$ = altsets.iterator();

      do {
         if (!i$.hasNext()) {
            return viableAlts.nextSetBit(0);
         }

         BitSet alts = (BitSet)i$.next();
         int minAlt = alts.nextSetBit(0);
         viableAlts.set(minAlt);
      } while(viableAlts.cardinality() <= 1);

      return 0;
   }

   private static final class AltAndContextConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig> {
      public static final AltAndContextConfigEqualityComparator INSTANCE = new AltAndContextConfigEqualityComparator();

      public int hashCode(ATNConfig o) {
         int hashCode = MurmurHash.initialize(7);
         hashCode = MurmurHash.update(hashCode, o.state.stateNumber);
         hashCode = MurmurHash.update(hashCode, o.context);
         hashCode = MurmurHash.finish(hashCode, 2);
         return hashCode;
      }

      public boolean equals(ATNConfig a, ATNConfig b) {
         if (a == b) {
            return true;
         } else if (a != null && b != null) {
            return a.state.stateNumber == b.state.stateNumber && a.context.equals(b.context);
         } else {
            return false;
         }
      }
   }

   static class AltAndContextMap extends FlexibleHashMap<ATNConfig, BitSet> {
      public AltAndContextMap() {
         super(PredictionMode.AltAndContextConfigEqualityComparator.INSTANCE);
      }
   }
}
