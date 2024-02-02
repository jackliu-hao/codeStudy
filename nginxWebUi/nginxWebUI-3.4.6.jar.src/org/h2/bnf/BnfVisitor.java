package org.h2.bnf;

import java.util.ArrayList;

public interface BnfVisitor {
  void visitRuleElement(boolean paramBoolean, String paramString, Rule paramRule);
  
  void visitRuleRepeat(boolean paramBoolean, Rule paramRule);
  
  void visitRuleFixed(int paramInt);
  
  void visitRuleList(boolean paramBoolean, ArrayList<Rule> paramArrayList);
  
  void visitRuleOptional(Rule paramRule);
  
  void visitRuleOptional(ArrayList<Rule> paramArrayList);
  
  void visitRuleExtension(Rule paramRule, boolean paramBoolean);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\BnfVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */