package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathWildcardElement extends XPathElement {
   public XPathWildcardElement() {
      super("*");
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      if (this.invert) {
         return new ArrayList();
      } else {
         List<ParseTree> kids = new ArrayList();
         Iterator i$ = Trees.getChildren(t).iterator();

         while(i$.hasNext()) {
            Tree c = (Tree)i$.next();
            kids.add((ParseTree)c);
         }

         return kids;
      }
   }
}
