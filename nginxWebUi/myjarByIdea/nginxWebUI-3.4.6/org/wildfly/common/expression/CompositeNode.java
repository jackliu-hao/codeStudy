package org.wildfly.common.expression;

import java.util.HashSet;
import java.util.List;
import org.wildfly.common.function.ExceptionBiConsumer;

final class CompositeNode extends Node {
   private final Node[] subNodes;

   CompositeNode(Node[] subNodes) {
      this.subNodes = subNodes;
   }

   CompositeNode(List<Node> subNodes) {
      this.subNodes = (Node[])subNodes.toArray(NO_NODES);
   }

   <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
      Node[] var3 = this.subNodes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Node subNode = var3[var5];
         subNode.emit(context, resolveFunction);
      }

   }

   void catalog(HashSet<String> strings) {
      Node[] var2 = this.subNodes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Node node = var2[var4];
         node.catalog(strings);
      }

   }

   public String toString() {
      StringBuilder b = new StringBuilder();
      b.append('*');
      Node[] var2 = this.subNodes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Node subNode = var2[var4];
         b.append('<').append(subNode.toString()).append('>');
      }

      return b.toString();
   }
}
