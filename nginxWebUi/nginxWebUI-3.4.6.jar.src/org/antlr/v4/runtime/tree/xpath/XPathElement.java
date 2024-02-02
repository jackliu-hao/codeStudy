/*    */ package org.antlr.v4.runtime.tree.xpath;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class XPathElement
/*    */ {
/*    */   protected String nodeName;
/*    */   protected boolean invert;
/*    */   
/*    */   public XPathElement(String nodeName) {
/* 15 */     this.nodeName = nodeName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract Collection<ParseTree> evaluate(ParseTree paramParseTree);
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 26 */     String inv = this.invert ? "!" : "";
/* 27 */     return getClass().getSimpleName() + "[" + inv + this.nodeName + "]";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */