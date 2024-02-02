package org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public abstract class XPathElement {
   protected String nodeName;
   protected boolean invert;

   public XPathElement(String nodeName) {
      this.nodeName = nodeName;
   }

   public abstract Collection<ParseTree> evaluate(ParseTree var1);

   public String toString() {
      String inv = this.invert ? "!" : "";
      return this.getClass().getSimpleName() + "[" + inv + this.nodeName + "]";
   }
}
