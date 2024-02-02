package org.noear.solon.core.route;

import java.util.Collection;
import java.util.List;
import org.noear.solon.core.handle.MethodType;

public interface RoutingTable<T> {
   void add(Routing<T> routing);

   void add(int index, Routing<T> routing);

   void remove(String pathPrefix);

   Collection<Routing<T>> getAll();

   T matchOne(String path, MethodType method);

   List<T> matchAll(String path, MethodType method);

   void clear();
}
