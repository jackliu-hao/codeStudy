package org.h2.bnf;

import java.util.HashMap;

public interface Rule {
   void setLinks(HashMap<String, RuleHead> var1);

   boolean autoComplete(Sentence var1);

   void accept(BnfVisitor var1);
}
