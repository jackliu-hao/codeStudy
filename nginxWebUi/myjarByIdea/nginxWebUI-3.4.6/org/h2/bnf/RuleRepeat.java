package org.h2.bnf;

import java.util.HashMap;

public class RuleRepeat implements Rule {
   private final Rule rule;
   private final boolean comma;

   public RuleRepeat(Rule var1, boolean var2) {
      this.rule = var1;
      this.comma = var2;
   }

   public void accept(BnfVisitor var1) {
      var1.visitRuleRepeat(this.comma, this.rule);
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
   }

   public boolean autoComplete(Sentence var1) {
      var1.stopIfRequired();

      while(this.rule.autoComplete(var1)) {
      }

      String var2;
      for(var2 = var1.getQuery(); Bnf.startWithSpace(var2); var2 = var2.substring(1)) {
      }

      var1.setQuery(var2);
      return true;
   }

   public String toString() {
      return this.comma ? ", ..." : " ...";
   }
}
