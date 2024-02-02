package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class OrderedATNConfigSet extends ATNConfigSet {
   public OrderedATNConfigSet() {
      this.configLookup = new LexerConfigHashSet();
   }

   public static class LexerConfigHashSet extends ATNConfigSet.AbstractConfigHashSet {
      public LexerConfigHashSet() {
         super(ObjectEqualityComparator.INSTANCE);
      }
   }
}
