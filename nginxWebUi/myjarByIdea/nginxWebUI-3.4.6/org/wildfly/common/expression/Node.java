package org.wildfly.common.expression;

import java.util.HashSet;
import java.util.List;
import org.wildfly.common.function.ExceptionBiConsumer;

abstract class Node {
   static final Node[] NO_NODES = new Node[0];
   static final Node NULL = new Node() {
      <E extends Exception> void emit(ResolveContext<E> context, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
      }

      void catalog(HashSet<String> strings) {
      }

      public String toString() {
         return "<<null>>";
      }
   };

   static Node fromList(List<Node> list) {
      if (list != null && !list.isEmpty()) {
         return (Node)(list.size() == 1 ? (Node)list.get(0) : new CompositeNode(list));
      } else {
         return NULL;
      }
   }

   abstract <E extends Exception> void emit(ResolveContext<E> var1, ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> var2) throws E;

   abstract void catalog(HashSet<String> var1);
}
