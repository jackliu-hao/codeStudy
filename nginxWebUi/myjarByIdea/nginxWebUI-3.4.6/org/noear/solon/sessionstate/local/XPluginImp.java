package org.noear.solon.sessionstate.local;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.PrintUtil;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      if (Solon.app().enableSessionState()) {
         if (Bridge.sessionStateFactory().priority() < 1) {
            SessionProp.init();
            Bridge.sessionStateFactorySet(LocalSessionStateFactory.getInstance());
            PrintUtil.info("Session", "solon: Local session state plugin is loaded");
         }
      }
   }
}
