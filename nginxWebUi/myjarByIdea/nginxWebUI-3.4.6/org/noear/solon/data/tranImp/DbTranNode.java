package org.noear.solon.data.tranImp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.tran.TranNode;

public abstract class DbTranNode implements TranNode {
   protected DbTranNode parent;
   protected List<DbTranNode> children = new ArrayList();

   public void add(TranNode slave) {
      if (slave instanceof DbTranNode) {
         DbTranNode node = (DbTranNode)slave;
         node.parent = this;
         this.children.add(node);
      }

   }

   public void commit() throws Throwable {
      Iterator var1 = this.children.iterator();

      while(var1.hasNext()) {
         DbTranNode n1 = (DbTranNode)var1.next();
         n1.commit();
      }

   }

   public void rollback() throws Throwable {
      Iterator var1 = this.children.iterator();

      while(var1.hasNext()) {
         DbTranNode n1 = (DbTranNode)var1.next();

         try {
            n1.rollback();
         } catch (Throwable var4) {
            EventBus.push(var4);
         }
      }

   }

   public void close() throws Throwable {
      Iterator var1 = this.children.iterator();

      while(var1.hasNext()) {
         DbTranNode n1 = (DbTranNode)var1.next();

         try {
            n1.close();
         } catch (Throwable var4) {
            EventBus.push(var4);
         }
      }

   }
}
