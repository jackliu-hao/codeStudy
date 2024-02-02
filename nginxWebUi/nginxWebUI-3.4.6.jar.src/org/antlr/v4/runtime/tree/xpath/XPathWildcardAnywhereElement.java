/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.Trees;
/*    */ 
/*    */ public class XPathWildcardAnywhereElement
/*    */   extends XPathElement {
/*    */   public XPathWildcardAnywhereElement() {
/* 11 */     super("*");
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 16 */     if (this.invert) return new ArrayList<ParseTree>(); 
/* 17 */     return Trees.getDescendants(t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathWildcardAnywhereElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */