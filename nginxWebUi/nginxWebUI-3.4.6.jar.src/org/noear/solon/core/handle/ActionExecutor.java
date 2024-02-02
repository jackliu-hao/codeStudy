package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;

public interface ActionExecutor {
  boolean matched(Context paramContext, String paramString);
  
  Object execute(Context paramContext, Object paramObject, MethodWrap paramMethodWrap) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\ActionExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */