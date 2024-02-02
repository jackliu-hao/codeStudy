/*    */ package org.noear.solon.data.tranImp;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.data.tran.TranNode;
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
/*    */ public abstract class DbTranNode
/*    */   implements TranNode
/*    */ {
/*    */   protected DbTranNode parent;
/* 23 */   protected List<DbTranNode> children = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(TranNode slave) {
/* 30 */     if (slave instanceof DbTranNode) {
/* 31 */       DbTranNode node = (DbTranNode)slave;
/*    */       
/* 33 */       node.parent = this;
/* 34 */       this.children.add(node);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void commit() throws Throwable {
/* 42 */     for (DbTranNode n1 : this.children) {
/* 43 */       n1.commit();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void rollback() throws Throwable {
/* 53 */     for (DbTranNode n1 : this.children) {
/*    */       try {
/* 55 */         n1.rollback();
/* 56 */       } catch (Throwable ex) {
/* 57 */         EventBus.push(ex);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() throws Throwable {
/* 68 */     for (DbTranNode n1 : this.children) {
/*    */       try {
/* 70 */         n1.close();
/* 71 */       } catch (Throwable ex) {
/* 72 */         EventBus.push(ex);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tranImp\DbTranNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */