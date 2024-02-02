package org.noear.solon.boot.undertow;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.HttpSignalProps;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.util.PrintUtil;

public final class XPluginImp implements Plugin {
   private static Signal _signal;
   private ServerLifecycle _server = null;

   public static Signal signal() {
      return _signal;
   }

   public static String solon_boot_ver() {
      return "undertow 2.1/" + Solon.cfg().version();
   }

   public void start(AopContext context) {
      if (Solon.app().enableHttp()) {
         context.beanBuilderAdd(WebFilter.class, (clz, bw, ano) -> {
         });
         context.beanBuilderAdd(WebServlet.class, (clz, bw, ano) -> {
         });
         context.beanBuilderAdd(WebListener.class, (clz, bw, ano) -> {
         });
         context.beanOnloaded((ctx) -> {
            try {
               this.start0(Solon.app());
            } catch (RuntimeException var3) {
               throw var3;
            } catch (Throwable var4) {
               throw new RuntimeException(var4);
            }
         });
      }
   }

   private void start0(SolonApp app) throws Throwable {
      ServerProps.init();
      HttpSignalProps props = new HttpSignalProps();
      String _host = props.getHost();
      int _port = props.getPort();
      String _name = props.getName();
      long time_start = System.currentTimeMillis();
      PrintUtil.info("Server:main: Undertow 2.2.17(undertow)");
      Class<?> jspClz = Utils.loadClass("io.undertow.jsp.JspServletBuilder");
      if (jspClz == null) {
         this._server = new UndertowServer();
      } else {
         this._server = new UndertowServerAddJsp();
      }

      this._server.start(_host, _port);
      _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);
      app.signalAdd(_signal);
      long time_end = System.currentTimeMillis();
      String connectorInfo = "solon.connector:main: undertow: Started ServerConnector@{HTTP/1.1,[http/1.1]";
      if (app.enableWebSocket()) {
         System.out.println(connectorInfo + "[WebSocket]}{0.0.0.0:" + _port + "}");
      }

      System.out.println(connectorInfo + "}{http://localhost:" + _port + "}");
      PrintUtil.info("Server:main: undertow: Started @" + (time_end - time_start) + "ms");
   }

   public void stop() throws Throwable {
      if (this._server != null) {
         this._server.stop();
         this._server = null;
         PrintUtil.info("Server:main: undertow: Has Stopped " + solon_boot_ver());
      }

   }
}
