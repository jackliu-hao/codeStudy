package org.noear.solon.core.handle;

import org.noear.solon.core.wrap.MethodWrap;

public interface ActionExecutor {
   boolean matched(Context ctx, String ct);

   Object execute(Context ctx, Object obj, MethodWrap mWrap) throws Throwable;
}
