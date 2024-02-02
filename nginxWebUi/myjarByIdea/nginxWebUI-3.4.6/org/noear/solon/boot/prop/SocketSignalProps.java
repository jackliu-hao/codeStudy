package org.noear.solon.boot.prop;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerSignalProps;

public class SocketSignalProps implements ServerSignalProps {
   private String name = Solon.cfg().get("server.socket.name");
   private int port = Solon.cfg().getInt("server.socket.port", 0);
   private String host = Solon.cfg().get("server.socket.host");

   public String getName() {
      return this.name;
   }

   public int getPort() {
      return this.port;
   }

   public String getHost() {
      return this.host;
   }

   public SocketSignalProps(int portBase) {
      if (this.port < 1) {
         this.port = portBase + Solon.cfg().serverPort();
      }

      if (Utils.isEmpty(this.host)) {
         this.host = Solon.cfg().serverHost();
      }

   }
}
