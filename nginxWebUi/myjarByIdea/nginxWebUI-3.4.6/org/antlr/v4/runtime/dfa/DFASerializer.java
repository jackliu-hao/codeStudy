package org.antlr.v4.runtime.dfa;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

public class DFASerializer {
   private final DFA dfa;
   private final Vocabulary vocabulary;

   /** @deprecated */
   @Deprecated
   public DFASerializer(DFA dfa, String[] tokenNames) {
      this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
   }

   public DFASerializer(DFA dfa, Vocabulary vocabulary) {
      this.dfa = dfa;
      this.vocabulary = vocabulary;
   }

   public String toString() {
      if (this.dfa.s0 == null) {
         return null;
      } else {
         StringBuilder buf = new StringBuilder();
         List<DFAState> states = this.dfa.getStates();
         Iterator i$ = states.iterator();

         while(i$.hasNext()) {
            DFAState s = (DFAState)i$.next();
            int n = 0;
            if (s.edges != null) {
               n = s.edges.length;
            }

            for(int i = 0; i < n; ++i) {
               DFAState t = s.edges[i];
               if (t != null && t.stateNumber != Integer.MAX_VALUE) {
                  buf.append(this.getStateString(s));
                  String label = this.getEdgeLabel(i);
                  buf.append("-").append(label).append("->").append(this.getStateString(t)).append('\n');
               }
            }
         }

         String output = buf.toString();
         if (output.length() == 0) {
            return null;
         } else {
            return output;
         }
      }
   }

   protected String getEdgeLabel(int i) {
      return this.vocabulary.getDisplayName(i - 1);
   }

   protected String getStateString(DFAState s) {
      int n = s.stateNumber;
      String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
      if (s.isAcceptState) {
         return s.predicates != null ? baseStateStr + "=>" + Arrays.toString(s.predicates) : baseStateStr + "=>" + s.prediction;
      } else {
         return baseStateStr;
      }
   }
}
