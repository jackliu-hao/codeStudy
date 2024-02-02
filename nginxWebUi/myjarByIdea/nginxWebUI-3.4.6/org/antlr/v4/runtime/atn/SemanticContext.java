package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.Utils;

public abstract class SemanticContext {
   public static final SemanticContext NONE = new Predicate();

   public abstract boolean eval(Recognizer<?, ?> var1, RuleContext var2);

   public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
      return this;
   }

   public static SemanticContext and(SemanticContext a, SemanticContext b) {
      if (a != null && a != NONE) {
         if (b != null && b != NONE) {
            AND result = new AND(a, b);
            return (SemanticContext)(result.opnds.length == 1 ? result.opnds[0] : result);
         } else {
            return a;
         }
      } else {
         return b;
      }
   }

   public static SemanticContext or(SemanticContext a, SemanticContext b) {
      if (a == null) {
         return b;
      } else if (b == null) {
         return a;
      } else if (a != NONE && b != NONE) {
         OR result = new OR(a, b);
         return (SemanticContext)(result.opnds.length == 1 ? result.opnds[0] : result);
      } else {
         return NONE;
      }
   }

   private static List<PrecedencePredicate> filterPrecedencePredicates(Collection<? extends SemanticContext> collection) {
      ArrayList<PrecedencePredicate> result = null;
      Iterator<? extends SemanticContext> iterator = collection.iterator();

      while(iterator.hasNext()) {
         SemanticContext context = (SemanticContext)iterator.next();
         if (context instanceof PrecedencePredicate) {
            if (result == null) {
               result = new ArrayList();
            }

            result.add((PrecedencePredicate)context);
            iterator.remove();
         }
      }

      if (result == null) {
         return Collections.emptyList();
      } else {
         return result;
      }
   }

   public static class OR extends Operator {
      public final SemanticContext[] opnds;

      public OR(SemanticContext a, SemanticContext b) {
         Set<SemanticContext> operands = new HashSet();
         if (a instanceof OR) {
            operands.addAll(Arrays.asList(((OR)a).opnds));
         } else {
            operands.add(a);
         }

         if (b instanceof OR) {
            operands.addAll(Arrays.asList(((OR)b).opnds));
         } else {
            operands.add(b);
         }

         List<PrecedencePredicate> precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
         if (!precedencePredicates.isEmpty()) {
            PrecedencePredicate reduced = (PrecedencePredicate)Collections.max(precedencePredicates);
            operands.add(reduced);
         }

         this.opnds = (SemanticContext[])operands.toArray(new SemanticContext[operands.size()]);
      }

      public Collection<SemanticContext> getOperands() {
         return Arrays.asList(this.opnds);
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof OR)) {
            return false;
         } else {
            OR other = (OR)obj;
            return Arrays.equals(this.opnds, other.opnds);
         }
      }

      public int hashCode() {
         return MurmurHash.hashCode(this.opnds, OR.class.hashCode());
      }

      public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         SemanticContext[] arr$ = this.opnds;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            SemanticContext opnd = arr$[i$];
            if (opnd.eval(parser, parserCallStack)) {
               return true;
            }
         }

         return false;
      }

      public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         boolean differs = false;
         List<SemanticContext> operands = new ArrayList();
         SemanticContext[] arr$ = this.opnds;
         int i = arr$.length;

         for(int i$ = 0; i$ < i; ++i$) {
            SemanticContext context = arr$[i$];
            SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
            differs |= evaluated != context;
            if (evaluated == NONE) {
               return NONE;
            }

            if (evaluated != null) {
               operands.add(evaluated);
            }
         }

         if (!differs) {
            return this;
         } else if (operands.isEmpty()) {
            return null;
         } else {
            SemanticContext result = (SemanticContext)operands.get(0);

            for(i = 1; i < operands.size(); ++i) {
               result = SemanticContext.or(result, (SemanticContext)operands.get(i));
            }

            return result;
         }
      }

      public String toString() {
         return Utils.join(Arrays.asList(this.opnds).iterator(), "||");
      }
   }

   public static class AND extends Operator {
      public final SemanticContext[] opnds;

      public AND(SemanticContext a, SemanticContext b) {
         Set<SemanticContext> operands = new HashSet();
         if (a instanceof AND) {
            operands.addAll(Arrays.asList(((AND)a).opnds));
         } else {
            operands.add(a);
         }

         if (b instanceof AND) {
            operands.addAll(Arrays.asList(((AND)b).opnds));
         } else {
            operands.add(b);
         }

         List<PrecedencePredicate> precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
         if (!precedencePredicates.isEmpty()) {
            PrecedencePredicate reduced = (PrecedencePredicate)Collections.min(precedencePredicates);
            operands.add(reduced);
         }

         this.opnds = (SemanticContext[])operands.toArray(new SemanticContext[operands.size()]);
      }

      public Collection<SemanticContext> getOperands() {
         return Arrays.asList(this.opnds);
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof AND)) {
            return false;
         } else {
            AND other = (AND)obj;
            return Arrays.equals(this.opnds, other.opnds);
         }
      }

      public int hashCode() {
         return MurmurHash.hashCode(this.opnds, AND.class.hashCode());
      }

      public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         SemanticContext[] arr$ = this.opnds;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            SemanticContext opnd = arr$[i$];
            if (!opnd.eval(parser, parserCallStack)) {
               return false;
            }
         }

         return true;
      }

      public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         boolean differs = false;
         List<SemanticContext> operands = new ArrayList();
         SemanticContext[] arr$ = this.opnds;
         int i = arr$.length;

         for(int i$ = 0; i$ < i; ++i$) {
            SemanticContext context = arr$[i$];
            SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
            differs |= evaluated != context;
            if (evaluated == null) {
               return null;
            }

            if (evaluated != NONE) {
               operands.add(evaluated);
            }
         }

         if (!differs) {
            return this;
         } else if (operands.isEmpty()) {
            return NONE;
         } else {
            SemanticContext result = (SemanticContext)operands.get(0);

            for(i = 1; i < operands.size(); ++i) {
               result = SemanticContext.and(result, (SemanticContext)operands.get(i));
            }

            return result;
         }
      }

      public String toString() {
         return Utils.join(Arrays.asList(this.opnds).iterator(), "&&");
      }
   }

   public abstract static class Operator extends SemanticContext {
      public abstract Collection<SemanticContext> getOperands();
   }

   public static class PrecedencePredicate extends SemanticContext implements Comparable<PrecedencePredicate> {
      public final int precedence;

      protected PrecedencePredicate() {
         this.precedence = 0;
      }

      public PrecedencePredicate(int precedence) {
         this.precedence = precedence;
      }

      public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         return parser.precpred(parserCallStack, this.precedence);
      }

      public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         return parser.precpred(parserCallStack, this.precedence) ? SemanticContext.NONE : null;
      }

      public int compareTo(PrecedencePredicate o) {
         return this.precedence - o.precedence;
      }

      public int hashCode() {
         int hashCode = 1;
         hashCode = 31 * hashCode + this.precedence;
         return hashCode;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof PrecedencePredicate)) {
            return false;
         } else if (this == obj) {
            return true;
         } else {
            PrecedencePredicate other = (PrecedencePredicate)obj;
            return this.precedence == other.precedence;
         }
      }

      public String toString() {
         return "{" + this.precedence + ">=prec}?";
      }
   }

   public static class Predicate extends SemanticContext {
      public final int ruleIndex;
      public final int predIndex;
      public final boolean isCtxDependent;

      protected Predicate() {
         this.ruleIndex = -1;
         this.predIndex = -1;
         this.isCtxDependent = false;
      }

      public Predicate(int ruleIndex, int predIndex, boolean isCtxDependent) {
         this.ruleIndex = ruleIndex;
         this.predIndex = predIndex;
         this.isCtxDependent = isCtxDependent;
      }

      public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
         RuleContext localctx = this.isCtxDependent ? parserCallStack : null;
         return parser.sempred(localctx, this.ruleIndex, this.predIndex);
      }

      public int hashCode() {
         int hashCode = MurmurHash.initialize();
         hashCode = MurmurHash.update(hashCode, this.ruleIndex);
         hashCode = MurmurHash.update(hashCode, this.predIndex);
         hashCode = MurmurHash.update(hashCode, this.isCtxDependent ? 1 : 0);
         hashCode = MurmurHash.finish(hashCode, 3);
         return hashCode;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Predicate)) {
            return false;
         } else if (this == obj) {
            return true;
         } else {
            Predicate p = (Predicate)obj;
            return this.ruleIndex == p.ruleIndex && this.predIndex == p.predIndex && this.isCtxDependent == p.isCtxDependent;
         }
      }

      public String toString() {
         return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
      }
   }
}
