package org.h2.bnf;

import java.util.ArrayList;

public interface BnfVisitor {
   void visitRuleElement(boolean var1, String var2, Rule var3);

   void visitRuleRepeat(boolean var1, Rule var2);

   void visitRuleFixed(int var1);

   void visitRuleList(boolean var1, ArrayList<Rule> var2);

   void visitRuleOptional(Rule var1);

   void visitRuleOptional(ArrayList<Rule> var1);

   void visitRuleExtension(Rule var1, boolean var2);
}
