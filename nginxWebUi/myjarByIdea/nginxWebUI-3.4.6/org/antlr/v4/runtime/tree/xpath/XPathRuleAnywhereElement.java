package org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathRuleAnywhereElement extends XPathElement {
   protected int ruleIndex;

   public XPathRuleAnywhereElement(String ruleName, int ruleIndex) {
      super(ruleName);
      this.ruleIndex = ruleIndex;
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      return Trees.findAllRuleNodes(t, this.ruleIndex);
   }
}
