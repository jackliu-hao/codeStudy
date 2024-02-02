package org.noear.solon.core.route;

import java.util.Collection;
import java.util.List;
import org.noear.solon.core.handle.MethodType;

public interface RoutingTable<T> {
  void add(Routing<T> paramRouting);
  
  void add(int paramInt, Routing<T> paramRouting);
  
  void remove(String paramString);
  
  Collection<Routing<T>> getAll();
  
  T matchOne(String paramString, MethodType paramMethodType);
  
  List<T> matchAll(String paramString, MethodType paramMethodType);
  
  void clear();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RoutingTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */