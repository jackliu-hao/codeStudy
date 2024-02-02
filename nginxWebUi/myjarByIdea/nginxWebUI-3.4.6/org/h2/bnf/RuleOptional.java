package org.h2.bnf;

import java.util.HashMap;

public class RuleOptional implements Rule {
   private final Rule rule;
   private boolean mapSet;

   public RuleOptional(Rule var1) {
      this.rule = var1;
   }

   public void accept(BnfVisitor var1) {
      if (this.rule instanceof RuleList) {
         RuleList var2 = (RuleList)this.rule;
         if (var2.or) {
            var1.visitRuleOptional(var2.list);
            return;
         }
      }

      var1.visitRuleOptional(this.rule);
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
      if (!this.mapSet) {
         this.rule.setLinks(var1);
         this.mapSet = true;
      }

   }

   public boolean autoComplete(Sentence var1) {
      var1.stopIfRequired();
      this.rule.autoComplete(var1);
      return true;
   }

   public String toString() {
      return '[' + this.rule.toString() + ']';
   }
}
