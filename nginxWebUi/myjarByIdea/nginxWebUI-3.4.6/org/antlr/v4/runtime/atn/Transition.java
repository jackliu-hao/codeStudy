package org.antlr.v4.runtime.atn;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.misc.IntervalSet;

public abstract class Transition {
   public static final int EPSILON = 1;
   public static final int RANGE = 2;
   public static final int RULE = 3;
   public static final int PREDICATE = 4;
   public static final int ATOM = 5;
   public static final int ACTION = 6;
   public static final int SET = 7;
   public static final int NOT_SET = 8;
   public static final int WILDCARD = 9;
   public static final int PRECEDENCE = 10;
   public static final List<String> serializationNames = Collections.unmodifiableList(Arrays.asList("INVALID", "EPSILON", "RANGE", "RULE", "PREDICATE", "ATOM", "ACTION", "SET", "NOT_SET", "WILDCARD", "PRECEDENCE"));
   public static final Map<Class<? extends Transition>, Integer> serializationTypes = Collections.unmodifiableMap(new HashMap<Class<? extends Transition>, Integer>() {
      {
         this.put(EpsilonTransition.class, 1);
         this.put(RangeTransition.class, 2);
         this.put(RuleTransition.class, 3);
         this.put(PredicateTransition.class, 4);
         this.put(AtomTransition.class, 5);
         this.put(ActionTransition.class, 6);
         this.put(SetTransition.class, 7);
         this.put(NotSetTransition.class, 8);
         this.put(WildcardTransition.class, 9);
         this.put(PrecedencePredicateTransition.class, 10);
      }
   });
   public ATNState target;

   protected Transition(ATNState target) {
      if (target == null) {
         throw new NullPointerException("target cannot be null.");
      } else {
         this.target = target;
      }
   }

   public abstract int getSerializationType();

   public boolean isEpsilon() {
      return false;
   }

   public IntervalSet label() {
      return null;
   }

   public abstract boolean matches(int var1, int var2, int var3);
}
