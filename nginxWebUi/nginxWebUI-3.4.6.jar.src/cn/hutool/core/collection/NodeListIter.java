/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.NoSuchElementException;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
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
/*    */ 
/*    */ 
/*    */ public class NodeListIter
/*    */   implements ResettableIter<Node>
/*    */ {
/*    */   private final NodeList nodeList;
/* 25 */   private int index = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public NodeListIter(NodeList nodeList) {
/* 33 */     this.nodeList = (NodeList)Assert.notNull(nodeList, "NodeList must not be null.", new Object[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 38 */     return (this.nodeList != null && this.index < this.nodeList.getLength());
/*    */   }
/*    */ 
/*    */   
/*    */   public Node next() {
/* 43 */     if (this.nodeList != null && this.index < this.nodeList.getLength()) {
/* 44 */       return this.nodeList.item(this.index++);
/*    */     }
/* 46 */     throw new NoSuchElementException("underlying nodeList has no more elements");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove() {
/* 56 */     throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 61 */     this.index = 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\NodeListIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */