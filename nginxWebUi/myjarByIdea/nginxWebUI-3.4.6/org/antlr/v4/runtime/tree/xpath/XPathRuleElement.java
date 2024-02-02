package org.antlr.v4.runtime.tree.xpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

public class XPathRuleElement extends XPathElement {
   protected int ruleIndex;

   public XPathRuleElement(String ruleName, int ruleIndex) {
      super(ruleName);
      this.ruleIndex = ruleIndex;
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      List<ParseTree> nodes = new ArrayList();
      Iterator i$ = Trees.getChildren(t).iterator();

      while(true) {
         ParserRuleContext ctx;
         do {
            Tree c;
            do {
               if (!i$.hasNext()) {
                  return nodes;
               }

               c = (Tree)i$.next();
            } while(!(c instanceof ParserRuleContext));

            ctx = (ParserRuleContext)c;
         } while((ctx.getRuleIndex() != this.ruleIndex || this.invert) && (ctx.getRuleIndex() == this.ruleIndex || !this.invert));

         nodes.add(ctx);
      }
   }
}
