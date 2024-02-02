package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathWildcardAnywhereElement extends XPathElement {
   public XPathWildcardAnywhereElement() {
      super("*");
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      return (Collection)(this.invert ? new ArrayList() : Trees.getDescendants(t));
   }
}
