package org.h2.bnf;

public class RuleHead {
   private final String section;
   private final String topic;
   private Rule rule;

   RuleHead(String var1, String var2, Rule var3) {
      this.section = var1;
      this.topic = var2;
      this.rule = var3;
   }

   public String getTopic() {
      return this.topic;
   }

   public Rule getRule() {
      return this.rule;
   }

   void setRule(Rule var1) {
      this.rule = var1;
   }

   public String getSection() {
      return this.section;
   }
}
