package org.h2.bnf;

import java.util.HashMap;

public interface Rule {
  void setLinks(HashMap<String, RuleHead> paramHashMap);
  
  boolean autoComplete(Sentence paramSentence);
  
  void accept(BnfVisitor paramBnfVisitor);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\Rule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */