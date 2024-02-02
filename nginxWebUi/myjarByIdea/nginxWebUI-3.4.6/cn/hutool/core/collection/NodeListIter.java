package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.NoSuchElementException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIter implements ResettableIter<Node> {
   private final NodeList nodeList;
   private int index = 0;

   public NodeListIter(NodeList nodeList) {
      this.nodeList = (NodeList)Assert.notNull(nodeList, "NodeList must not be null.");
   }

   public boolean hasNext() {
      return this.nodeList != null && this.index < this.nodeList.getLength();
   }

   public Node next() {
      if (this.nodeList != null && this.index < this.nodeList.getLength()) {
         return this.nodeList.item(this.index++);
      } else {
         throw new NoSuchElementException("underlying nodeList has no more elements");
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
   }

   public void reset() {
      this.index = 0;
   }
}
