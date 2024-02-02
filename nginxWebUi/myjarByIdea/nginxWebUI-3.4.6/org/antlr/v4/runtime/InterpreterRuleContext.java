package org.antlr.v4.runtime;

public class InterpreterRuleContext extends ParserRuleContext {
   protected int ruleIndex = -1;

   public InterpreterRuleContext() {
   }

   public InterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
      super(parent, invokingStateNumber);
      this.ruleIndex = ruleIndex;
   }

   public int getRuleIndex() {
      return this.ruleIndex;
   }
}
