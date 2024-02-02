package org.antlr.v4.runtime;

import java.util.BitSet;
import java.util.Iterator;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

public class DiagnosticErrorListener extends BaseErrorListener {
   protected final boolean exactOnly;

   public DiagnosticErrorListener() {
      this(true);
   }

   public DiagnosticErrorListener(boolean exactOnly) {
      this.exactOnly = exactOnly;
   }

   public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      if (!this.exactOnly || exact) {
         String format = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
         String decision = this.getDecisionDescription(recognizer, dfa);
         BitSet conflictingAlts = this.getConflictingAlts(ambigAlts, configs);
         String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
         String message = String.format(format, decision, conflictingAlts, text);
         recognizer.notifyErrorListeners(message);
      }
   }

   public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
      String format = "reportAttemptingFullContext d=%s, input='%s'";
      String decision = this.getDecisionDescription(recognizer, dfa);
      String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
      String message = String.format(format, decision, text);
      recognizer.notifyErrorListeners(message);
   }

   public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
      String format = "reportContextSensitivity d=%s, input='%s'";
      String decision = this.getDecisionDescription(recognizer, dfa);
      String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
      String message = String.format(format, decision, text);
      recognizer.notifyErrorListeners(message);
   }

   protected String getDecisionDescription(Parser recognizer, DFA dfa) {
      int decision = dfa.decision;
      int ruleIndex = dfa.atnStartState.ruleIndex;
      String[] ruleNames = recognizer.getRuleNames();
      if (ruleIndex >= 0 && ruleIndex < ruleNames.length) {
         String ruleName = ruleNames[ruleIndex];
         return ruleName != null && !ruleName.isEmpty() ? String.format("%d (%s)", decision, ruleName) : String.valueOf(decision);
      } else {
         return String.valueOf(decision);
      }
   }

   protected BitSet getConflictingAlts(BitSet reportedAlts, ATNConfigSet configs) {
      if (reportedAlts != null) {
         return reportedAlts;
      } else {
         BitSet result = new BitSet();
         Iterator i$ = configs.iterator();

         while(i$.hasNext()) {
            ATNConfig config = (ATNConfig)i$.next();
            result.set(config.alt);
         }

         return result;
      }
   }
}
