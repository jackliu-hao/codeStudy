package org.noear.solon.core.route;

import org.noear.solon.core.handle.MethodType;

public interface Routing<T> {
  int index();
  
  String path();
  
  MethodType method();
  
  T target();
  
  boolean matches(MethodType paramMethodType, String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\Routing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */