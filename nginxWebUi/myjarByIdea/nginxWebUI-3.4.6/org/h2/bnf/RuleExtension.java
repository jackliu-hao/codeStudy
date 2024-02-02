package org.h2.bnf;

import java.util.HashMap;

public class RuleExtension implements Rule {
   private final Rule rule;
   private final boolean compatibility;
   private boolean mapSet;

   public RuleExtension(Rule var1, boolean var2) {
      this.rule = var1;
      this.compatibility = var2;
   }

   public void accept(BnfVisitor var1) {
      var1.visitRuleExtension(this.rule, this.compatibility);
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
      return (this.compatibility ? "@c@ " : "@h2@ ") + this.rule.toString();
   }
}
