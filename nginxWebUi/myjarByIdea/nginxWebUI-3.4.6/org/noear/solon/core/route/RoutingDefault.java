package org.noear.solon.core.route;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PathAnalyzer;

public class RoutingDefault<T> implements Routing<T> {
   private final PathAnalyzer rule;
   private final int index;
   private final String path;
   private final T target;
   private final MethodType method;

   public RoutingDefault(String path, MethodType method, T target) {
      this(path, method, 0, target);
   }

   public RoutingDefault(String path, MethodType method, int index, T target) {
      this.rule = PathAnalyzer.get(path);
      this.method = method;
      this.path = path;
      this.index = index;
      this.target = target;
   }

   public int index() {
      return this.index;
   }

   public String path() {
      return this.path;
   }

   public T target() {
      return this.target;
   }

   public MethodType method() {
      return this.method;
   }

   public boolean matches(MethodType method2, String path2) {
      if (MethodType.ALL == this.method) {
         return this.matches0(path2);
      } else {
         if (MethodType.HTTP == this.method) {
            if (method2.signal == SignalType.HTTP) {
               return this.matches0(path2);
            }
         } else if (method2 == this.method) {
            return this.matches0(path2);
         }

         return false;
      }
   }

   private boolean matches0(String path2) {
      if (!"**".equals(this.path) && !"/**".equals(this.path)) {
         return this.path.equals(path2) ? true : this.rule.matches(path2);
      } else {
         return true;
      }
   }
}
