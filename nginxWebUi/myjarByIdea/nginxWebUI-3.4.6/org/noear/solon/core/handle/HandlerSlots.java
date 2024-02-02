package org.noear.solon.core.handle;

import java.util.Iterator;
import java.util.Set;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;

public interface HandlerSlots {
   default void before(String expr, MethodType method, int index, Handler handler) {
   }

   default void after(String expr, MethodType method, int index, Handler handler) {
   }

   void add(String expr, MethodType method, Handler handler);

   default void add(Mapping mapping, Set<MethodType> methodTypes, Handler handler) {
      String path = Utils.annoAlias(mapping.value(), mapping.path());
      Iterator var5 = methodTypes.iterator();

      while(true) {
         while(var5.hasNext()) {
            MethodType m1 = (MethodType)var5.next();
            if (!mapping.after() && !mapping.before()) {
               this.add(path, m1, handler);
            } else if (mapping.after()) {
               this.after(path, m1, mapping.index(), handler);
            } else {
               this.before(path, m1, mapping.index(), handler);
            }
         }

         return;
      }
   }
}
