package org.antlr.v4.runtime.atn;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.UUID;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.IntervalSet;

public abstract class ATNSimulator {
   /** @deprecated */
   @Deprecated
   public static final int SERIALIZED_VERSION;
   /** @deprecated */
   @Deprecated
   public static final UUID SERIALIZED_UUID;
   public static final DFAState ERROR;
   public final ATN atn;
   protected final PredictionContextCache sharedContextCache;

   public ATNSimulator(ATN atn, PredictionContextCache sharedContextCache) {
      this.atn = atn;
      this.sharedContextCache = sharedContextCache;
   }

   public abstract void reset();

   public void clearDFA() {
      throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
   }

   public PredictionContextCache getSharedContextCache() {
      return this.sharedContextCache;
   }

   public PredictionContext getCachedContext(PredictionContext context) {
      if (this.sharedContextCache == null) {
         return context;
      } else {
         synchronized(this.sharedContextCache) {
            IdentityHashMap<PredictionContext, PredictionContext> visited = new IdentityHashMap();
            return PredictionContext.getCachedContext(context, this.sharedContextCache, visited);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public static ATN deserialize(char[] data) {
      return (new ATNDeserializer()).deserialize(data);
   }

   /** @deprecated */
   @Deprecated
   public static void checkCondition(boolean condition) {
      (new ATNDeserializer()).checkCondition(condition);
   }

   /** @deprecated */
   @Deprecated
   public static void checkCondition(boolean condition, String message) {
      (new ATNDeserializer()).checkCondition(condition, message);
   }

   /** @deprecated */
   @Deprecated
   public static int toInt(char c) {
      return ATNDeserializer.toInt(c);
   }

   /** @deprecated */
   @Deprecated
   public static int toInt32(char[] data, int offset) {
      return ATNDeserializer.toInt32(data, offset);
   }

   /** @deprecated */
   @Deprecated
   public static long toLong(char[] data, int offset) {
      return ATNDeserializer.toLong(data, offset);
   }

   /** @deprecated */
   @Deprecated
   public static UUID toUUID(char[] data, int offset) {
      return ATNDeserializer.toUUID(data, offset);
   }

   /** @deprecated */
   @Deprecated
   public static Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
      return (new ATNDeserializer()).edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
   }

   /** @deprecated */
   @Deprecated
   public static ATNState stateFactory(int type, int ruleIndex) {
      return (new ATNDeserializer()).stateFactory(type, ruleIndex);
   }

   static {
      SERIALIZED_VERSION = ATNDeserializer.SERIALIZED_VERSION;
      SERIALIZED_UUID = ATNDeserializer.SERIALIZED_UUID;
      ERROR = new DFAState(new ATNConfigSet());
      ERROR.stateNumber = Integer.MAX_VALUE;
   }
}
