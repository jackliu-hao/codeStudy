/*    */ package ch.qos.logback.core.pattern.parser;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompositeNode
/*    */   extends SimpleKeywordNode
/*    */ {
/*    */   Node childNode;
/*    */   
/*    */   CompositeNode(String keyword) {
/* 20 */     super(2, keyword);
/*    */   }
/*    */ 
/*    */   
/*    */   public Node getChildNode() {
/* 25 */     return this.childNode;
/*    */   }
/*    */   
/*    */   public void setChildNode(Node childNode) {
/* 29 */     this.childNode = childNode;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 33 */     if (!super.equals(o)) {
/* 34 */       return false;
/*    */     }
/* 36 */     if (!(o instanceof CompositeNode)) {
/* 37 */       return false;
/*    */     }
/* 39 */     CompositeNode r = (CompositeNode)o;
/*    */     
/* 41 */     return (this.childNode != null) ? this.childNode.equals(r.childNode) : ((r.childNode == null));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 46 */     return super.hashCode();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 50 */     StringBuilder buf = new StringBuilder();
/* 51 */     if (this.childNode != null) {
/* 52 */       buf.append("CompositeNode(" + this.childNode + ")");
/*    */     } else {
/* 54 */       buf.append("CompositeNode(no child)");
/*    */     } 
/* 56 */     buf.append(printNext());
/* 57 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\CompositeNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */