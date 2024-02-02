package org.noear.solon.boot.prop;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerSignalProps;

public class WebSocketSignalProps implements ServerSignalProps {
   private String name = Solon.cfg().get("server.websocket.name");
   private int port = Solon.cfg().getInt("server.websocket.port", 0);
   private String host = Solon.cfg().get("server.websocket.host");

   public String getName() {
      return this.name;
   }

   public int getPort() {
      return this.port;
   }

   public String getHost() {
      return this.host;
   }

   public WebSocketSignalProps(int portBase) {
      if (this.port < 1) {
         this.port = portBase + Solon.cfg().serverPort();
      }

      if (Utils.isEmpty(this.host)) {
         this.host = Solon.cfg().serverHost();
      }

   }
}
