/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.Tree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ public class XPathWildcardElement
/*    */   extends XPathElement {
/*    */   public XPathWildcardElement() {
/* 13 */     super("*");
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 18 */     if (this.invert) return new ArrayList<ParseTree>(); 
/* 19 */     List<ParseTree> kids = new ArrayList<ParseTree>();
/* 20 */     for (Tree c : Trees.getChildren(t)) {
/* 21 */       kids.add((ParseTree)c);
/*    */     }
/* 23 */     return kids;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathWildcardElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */