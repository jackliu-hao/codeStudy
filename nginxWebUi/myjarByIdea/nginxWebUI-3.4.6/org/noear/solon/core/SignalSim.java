package org.noear.solon.core;

public class SignalSim implements Signal {
   private int port;
   private String protocol;
   private SignalType type;
   private String name;

   public String name() {
      return this.name;
   }

   public int port() {
      return this.port;
   }

   public String protocol() {
      return this.protocol;
   }

   public SignalType type() {
      return this.type;
   }

   public SignalSim(String name, int port, String protocol, SignalType type) {
      this.name = name;
      this.port = port;
      this.protocol = protocol.toLowerCase();
      this.type = type;
   }
}
