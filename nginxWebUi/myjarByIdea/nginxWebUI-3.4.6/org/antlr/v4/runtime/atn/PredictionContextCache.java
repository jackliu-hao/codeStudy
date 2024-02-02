package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Map;

public class PredictionContextCache {
   protected final Map<PredictionContext, PredictionContext> cache = new HashMap();

   public PredictionContext add(PredictionContext ctx) {
      if (ctx == PredictionContext.EMPTY) {
         return PredictionContext.EMPTY;
      } else {
         PredictionContext existing = (PredictionContext)this.cache.get(ctx);
         if (existing != null) {
            return existing;
         } else {
            this.cache.put(ctx, ctx);
            return ctx;
         }
      }
   }

   public PredictionContext get(PredictionContext ctx) {
      return (PredictionContext)this.cache.get(ctx);
   }

   public int size() {
      return this.cache.size();
   }
}
