/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.TerminalNode;
/*    */ import org.antlr.v4.runtime.tree.Tree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ public class XPathTokenElement extends XPathElement {
/*    */   protected int tokenType;
/*    */   
/*    */   public XPathTokenElement(String tokenName, int tokenType) {
/* 15 */     super(tokenName);
/* 16 */     this.tokenType = tokenType;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 22 */     List<ParseTree> nodes = new ArrayList<ParseTree>();
/* 23 */     for (Tree c : Trees.getChildren(t)) {
/* 24 */       if (c instanceof TerminalNode) {
/* 25 */         TerminalNode tnode = (TerminalNode)c;
/* 26 */         if ((tnode.getSymbol().getType() == this.tokenType && !this.invert) || (tnode.getSymbol().getType() != this.tokenType && this.invert))
/*    */         {
/*    */           
/* 29 */           nodes.add(tnode);
/*    */         }
/*    */       } 
/*    */     } 
/* 33 */     return nodes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathTokenElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */