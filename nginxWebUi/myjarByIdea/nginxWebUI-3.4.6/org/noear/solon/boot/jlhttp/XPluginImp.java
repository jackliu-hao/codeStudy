package org.noear.solon.boot.jlhttp;

import java.util.concurrent.Executors;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.HttpSignalProps;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.NamedThreadFactory;

public final class XPluginImp implements Plugin {
   private static Signal _signal;
   private HTTPServer _server = null;

   public static Signal signal() {
      return _signal;
   }

   public static String solon_boot_ver() {
      return "jlhttp 2.6/" + Solon.cfg().version();
   }

   public void start(AopContext context) {
      if (Solon.app().enableHttp()) {
         if (Utils.loadClass("org.noear.solon.boot.jetty.XPluginImp") == null) {
            if (Utils.loadClass("org.noear.solon.boot.undertow.XPluginImp") == null) {
               if (Utils.loadClass("org.noear.solon.boot.smarthttp.XPluginImp") == null) {
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
         }
      }
   }

   private void start0(SolonApp app) throws Throwable {
      ServerProps.init();
      this._server = new HTTPServer();
      HttpSignalProps props = new HttpSignalProps();
      String _host = props.getHost();
      int _port = props.getPort();
      String _name = props.getName();
      long time_start = System.currentTimeMillis();
      if (ServerProps.request_maxHeaderSize > 0) {
         HTTPServer.MAX_HEADER_SIZE = ServerProps.request_maxHeaderSize;
      }

      if (ServerProps.request_maxBodySize > 0) {
         HTTPServer.MAX_BODY_SIZE = ServerProps.request_maxBodySize;
      }

      JlHttpContextHandler _handler = new JlHttpContextHandler();
      if (System.getProperty("javax.net.ssl.keyStore") != null) {
         this._server.setServerSocketFactory(SslContextFactory.create().getServerSocketFactory());
      }

      HTTPServer.VirtualHost host = this._server.getVirtualHost((String)null);
      host.setDirectoryIndex((String)null);
      host.addContext("/", _handler, MethodType.HEAD.name, MethodType.GET.name, MethodType.POST.name, MethodType.PUT.name, MethodType.DELETE.name, MethodType.PATCH.name, MethodType.OPTIONS.name);
      PrintUtil.info("Server:main: JlHttpServer 2.6(jlhttp)");
      this._server.setExecutor(Executors.newCachedThreadPool(new NamedThreadFactory("jlhttp-")));
      this._server.setPort(_port);
      if (Utils.isNotEmpty(_host)) {
         this._server.setHost(_host);
      }

      this._server.start();
      _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);
      app.signalAdd(_signal);
      long time_end = System.currentTimeMillis();
      PrintUtil.info("Connector:main: jlhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
      PrintUtil.info("Server:main: jlhttp: Started @" + (time_end - time_start) + "ms");
   }

   public void stop() throws Throwable {
      if (this._server != null) {
         this._server.stop();
         this._server = null;
         PrintUtil.info("Server:main: jlhttp: Has Stopped " + solon_boot_ver());
      }

   }
}
