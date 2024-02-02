package org.h2.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetworkConnectionInfo {
   private final String server;
   private final byte[] clientAddr;
   private final int clientPort;
   private final String clientInfo;

   public NetworkConnectionInfo(String var1, String var2, int var3) throws UnknownHostException {
      this(var1, InetAddress.getByName(var2).getAddress(), var3, (String)null);
   }

   public NetworkConnectionInfo(String var1, byte[] var2, int var3, String var4) {
      this.server = var1;
      this.clientAddr = var2;
      this.clientPort = var3;
      this.clientInfo = var4;
   }

   public String getServer() {
      return this.server;
   }

   public byte[] getClientAddr() {
      return this.clientAddr;
   }

   public int getClientPort() {
      return this.clientPort;
   }

   public String getClientInfo() {
      return this.clientInfo;
   }

   public String getClient() {
      return NetUtils.ipToShortForm(new StringBuilder(), this.clientAddr, true).append(':').append(this.clientPort).toString();
   }
}
