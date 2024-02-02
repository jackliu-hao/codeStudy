package org.noear.solon.boot.undertow;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerLifecycle;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.boot.undertow.http.UtHandlerJspHandler;
import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
import org.noear.solon.boot.undertow.websocket._SessionManagerImpl;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.socketd.SessionManager;

class UndertowServer extends UndertowServerBase implements ServerLifecycle {
   protected Undertow _server;

   public void start(String host, int port) {
      try {
         this.setup(Solon.app(), host, port);
         this._server.start();
      } catch (RuntimeException var4) {
         throw var4;
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public void stop() throws Throwable {
      if (this._server != null) {
         this._server.stop();
         this._server = null;
      }

   }

   protected void setup(SolonApp app, String host, int port) throws Throwable {
      HttpHandler httpHandler = this.buildHandler();
      Undertow.Builder builder = Undertow.builder();
      builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);
      if (ServerProps.request_maxHeaderSize != 0) {
         builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, ServerProps.request_maxHeaderSize);
      }

      if (ServerProps.request_maxFileSize != 0) {
         builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, (long)ServerProps.request_maxFileSize);
      }

      if (Utils.isEmpty(host)) {
         host = "0.0.0.0";
      }

      if (System.getProperty("javax.net.ssl.keyStore") == null) {
         builder.addHttpListener(port, host);
      } else {
         builder.addHttpsListener(port, host, SslContextFactory.create());
      }

      if (app.enableWebSocket()) {
         builder.setHandler(Handlers.websocket(new UtWsConnectionCallback(), httpHandler));
         SessionManager.register(new _SessionManagerImpl());
      } else {
         builder.setHandler(httpHandler);
      }

      EventBus.push(builder);
      this._server = builder.build();
   }

   protected HttpHandler buildHandler() throws Exception {
      DeploymentInfo builder = this.initDeploymentInfo();
      builder.addServlet((new ServletInfo("ACTServlet", UtHandlerJspHandler.class)).addMapping("/"));
      ServletContainer container = Servlets.defaultContainer();
      DeploymentManager manager = container.addDeployment(builder);
      manager.deploy();
      return manager.start();
   }
}
