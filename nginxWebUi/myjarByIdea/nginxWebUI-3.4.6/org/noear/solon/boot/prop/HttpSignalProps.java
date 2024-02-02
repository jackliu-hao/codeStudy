package org.noear.solon.boot.prop;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerSignalProps;

public class HttpSignalProps implements ServerSignalProps {
   private String name = Solon.cfg().get("server.http.name");
   private int port = Solon.cfg().getInt("server.http.port", 0);
   private String host = Solon.cfg().get("server.http.host");

   public String getName() {
      return this.name;
   }

   public int getPort() {
      return this.port;
   }

   public String getHost() {
      return this.host;
   }

   public HttpSignalProps() {
      if (this.port < 1) {
         this.port = Solon.cfg().serverPort();
      }

      if (Utils.isEmpty(this.host)) {
         this.host = Solon.cfg().serverHost();
      }

   }
}
