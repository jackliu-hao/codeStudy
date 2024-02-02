package org.antlr.v4.runtime;

public class RuleContextWithAltNum extends ParserRuleContext {
   public int altNum;

   public RuleContextWithAltNum() {
      this.altNum = 0;
   }

   public RuleContextWithAltNum(ParserRuleContext parent, int invokingStateNumber) {
      super(parent, invokingStateNumber);
   }

   public int getAltNumber() {
      return this.altNum;
   }

   public void setAltNumber(int altNum) {
      this.altNum = altNum;
   }
}
