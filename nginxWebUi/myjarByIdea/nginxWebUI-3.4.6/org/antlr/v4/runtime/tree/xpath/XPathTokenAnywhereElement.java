package org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathTokenAnywhereElement extends XPathElement {
   protected int tokenType;

   public XPathTokenAnywhereElement(String tokenName, int tokenType) {
      super(tokenName);
      this.tokenType = tokenType;
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      return Trees.findAllTokenNodes(t, this.tokenType);
   }
}
