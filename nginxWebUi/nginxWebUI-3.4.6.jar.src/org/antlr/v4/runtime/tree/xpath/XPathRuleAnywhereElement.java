/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ 
/*    */ public class XPathRuleAnywhereElement
/*    */   extends XPathElement
/*    */ {
/*    */   protected int ruleIndex;
/*    */   
/*    */   public XPathRuleAnywhereElement(String ruleName, int ruleIndex) {
/* 14 */     super(ruleName);
/* 15 */     this.ruleIndex = ruleIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 20 */     return Trees.findAllRuleNodes(t, this.ruleIndex);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathRuleAnywhereElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */