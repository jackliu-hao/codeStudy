package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathTokenElement extends XPathElement {
   protected int tokenType;

   public XPathTokenElement(String tokenName, int tokenType) {
      super(tokenName);
      this.tokenType = tokenType;
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      List<ParseTree> nodes = new ArrayList();
      Iterator i$ = Trees.getChildren(t).iterator();

      while(true) {
         TerminalNode tnode;
         do {
            Tree c;
            do {
               if (!i$.hasNext()) {
                  return nodes;
               }

               c = (Tree)i$.next();
            } while(!(c instanceof TerminalNode));

            tnode = (TerminalNode)c;
         } while((tnode.getSymbol().getType() != this.tokenType || this.invert) && (tnode.getSymbol().getType() == this.tokenType || !this.invert));

         nodes.add(tnode);
      }
   }
}
