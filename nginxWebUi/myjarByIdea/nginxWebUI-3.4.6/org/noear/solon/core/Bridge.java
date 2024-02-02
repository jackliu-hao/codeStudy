package org.noear.solon.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.ActionExecutor;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.handle.SessionStateEmpty;
import org.noear.solon.core.handle.SessionStateFactory;

public class Bridge {
   private static SessionStateFactory _sessionStateFactory = (ctx) -> {
      return new SessionStateEmpty();
   };
   private static boolean sessionStateUpdated;
   private static LoadBalance.Factory _upstreamFactory = (g, s) -> {
      return null;
   };
   private static ActionExecutor _actionExecutorDef = new ActionExecutorDefault();
   private static Map<Class<?>, ActionExecutor> _actionExecutors = new HashMap();

   public static SessionStateFactory sessionStateFactory() {
      return _sessionStateFactory;
   }

   @Note("设置Session状态管理器")
   public static void sessionStateFactorySet(SessionStateFactory ssf) {
      if (ssf != null) {
         _sessionStateFactory = ssf;
         if (!sessionStateUpdated) {
            sessionStateUpdated = true;
            Solon.app().before("**", MethodType.HTTP, (c) -> {
               c.sessionState().sessionRefresh();
            });
         }
      }

   }

   @Note("获取Session状态管理器")
   public static SessionState sessionState(Context ctx) {
      return _sessionStateFactory.create(ctx);
   }

   @Note("获取负载工厂")
   public static LoadBalance.Factory upstreamFactory() {
      return _upstreamFactory;
   }

   @Note("设置负载工厂")
   public static void upstreamFactorySet(LoadBalance.Factory uf) {
      if (uf != null) {
         _upstreamFactory = uf;
      }

   }

   @Note("获取默认的Action执行器")
   public static ActionExecutor actionExecutorDef() {
      return _actionExecutorDef;
   }

   @Note("设置默认的Action执行器")
   public static void actionExecutorDefSet(ActionExecutor ae) {
      if (ae != null) {
         _actionExecutorDef = ae;
      }

   }

   @Note("获取所有Action执行器")
   public static Collection<ActionExecutor> actionExecutors() {
      return Collections.unmodifiableCollection(_actionExecutors.values());
   }

   @Note("添加Action执行器")
   public static void actionExecutorAdd(ActionExecutor e) {
      if (e != null) {
         _actionExecutors.put(e.getClass(), e);
      }

   }

   @Note("移除Action执行器")
   public static void actionExecutorRemove(Class<?> clz) {
      _actionExecutors.remove(clz);
   }
}
