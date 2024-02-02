/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ public class XPathTokenAnywhereElement extends XPathElement {
/*    */   protected int tokenType;
/*    */   
/*    */   public XPathTokenAnywhereElement(String tokenName, int tokenType) {
/* 11 */     super(tokenName);
/* 12 */     this.tokenType = tokenType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 17 */     return Trees.findAllTokenNodes(t, this.tokenType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathTokenAnywhereElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */