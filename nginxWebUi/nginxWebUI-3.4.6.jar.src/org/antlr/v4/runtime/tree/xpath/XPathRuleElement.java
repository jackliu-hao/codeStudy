/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.antlr.v4.runtime.ParserRuleContext;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.Tree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ public class XPathRuleElement extends XPathElement {
/*    */   protected int ruleIndex;
/*    */   
/*    */   public XPathRuleElement(String ruleName, int ruleIndex) {
/* 15 */     super(ruleName);
/* 16 */     this.ruleIndex = ruleIndex;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 22 */     List<ParseTree> nodes = new ArrayList<ParseTree>();
/* 23 */     for (Tree c : Trees.getChildren(t)) {
/* 24 */       if (c instanceof ParserRuleContext) {
/* 25 */         ParserRuleContext ctx = (ParserRuleContext)c;
/* 26 */         if ((ctx.getRuleIndex() == this.ruleIndex && !this.invert) || (ctx.getRuleIndex() != this.ruleIndex && this.invert))
/*    */         {
/*    */           
/* 29 */           nodes.add(ctx);
/*    */         }
/*    */       } 
/*    */     } 
/* 33 */     return nodes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathRuleElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */