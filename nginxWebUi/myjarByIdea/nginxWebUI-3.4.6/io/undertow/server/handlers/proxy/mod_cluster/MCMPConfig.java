package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import java.net.InetSocketAddress;

public class MCMPConfig {
   private final InetSocketAddress managementSocketAddress;
   private final AdvertiseConfig advertiseConfig;

   public static Builder builder() {
      return new Builder();
   }

   public static WebBuilder webBuilder() {
      return new WebBuilder();
   }

   public MCMPConfig(Builder builder) {
      this.managementSocketAddress = new InetSocketAddress(builder.managementHost, builder.managementPort);
      if (this.managementSocketAddress.isUnresolved()) {
         throw UndertowLogger.PROXY_REQUEST_LOGGER.unableToResolveModClusterManagementHost(builder.managementHost);
      } else if (this.managementSocketAddress.getAddress().isAnyLocalAddress()) {
         throw UndertowLogger.PROXY_REQUEST_LOGGER.cannotUseWildcardAddressAsModClusterManagementHost(builder.managementHost);
      } else {
         if (builder.advertiseBuilder != null) {
            this.advertiseConfig = new AdvertiseConfig(builder.advertiseBuilder, this);
         } else {
            this.advertiseConfig = null;
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public String getManagementHost() {
      return this.managementSocketAddress.getHostString();
   }

   /** @deprecated */
   @Deprecated
   public int getManagementPort() {
      return this.managementSocketAddress.getPort();
   }

   public InetSocketAddress getManagementSocketAddress() {
      return this.managementSocketAddress;
   }

   public AdvertiseConfig getAdvertiseConfig() {
      return this.advertiseConfig;
   }

   public HttpHandler create(ModCluster modCluster, HttpHandler next) {
      return new MCMPHandler(this, modCluster, next);
   }

   public static class AdvertiseBuilder {
      String advertiseGroup = "224.0.1.105";
      String advertiseAddress = "127.0.0.1";
      int advertisePort = 23364;
      int advertiseTtl = 10;
      int advertiseFrequency = 10000;
      String securityKey;
      String protocol = "http";
      String path = "/";
      private final Builder parent;

      public AdvertiseBuilder(Builder parent) {
         this.parent = parent;
      }

      public AdvertiseBuilder setAdvertiseGroup(String advertiseGroup) {
         this.advertiseGroup = advertiseGroup;
         return this;
      }

      public AdvertiseBuilder setAdvertiseAddress(String advertiseAddress) {
         this.advertiseAddress = advertiseAddress;
         return this;
      }

      public AdvertiseBuilder setAdvertisePort(int advertisePort) {
         this.advertisePort = advertisePort;
         return this;
      }

      public AdvertiseBuilder setAdvertiseTtl(int advertiseTtl) {
         this.advertiseTtl = advertiseTtl;
         return this;
      }

      public AdvertiseBuilder setAdvertiseFrequency(int advertiseFrequency) {
         this.advertiseFrequency = advertiseFrequency;
         return this;
      }

      public AdvertiseBuilder setSecurityKey(String securityKey) {
         this.securityKey = securityKey;
         return this;
      }

      public AdvertiseBuilder setProtocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public AdvertiseBuilder setPath(String path) {
         if (path.startsWith("/")) {
            this.path = path;
         } else {
            this.path = "/" + path;
         }

         return this;
      }

      public Builder getParent() {
         return this.parent;
      }
   }

   public static class WebBuilder extends Builder {
      boolean checkNonce = true;
      boolean reduceDisplay = false;
      boolean allowCmd = true;

      public WebBuilder setCheckNonce(boolean checkNonce) {
         this.checkNonce = checkNonce;
         return this;
      }

      public WebBuilder setReduceDisplay(boolean reduceDisplay) {
         this.reduceDisplay = reduceDisplay;
         return this;
      }

      public WebBuilder setAllowCmd(boolean allowCmd) {
         this.allowCmd = allowCmd;
         return this;
      }

      public MCMPConfig build() {
         return new MCMPWebManagerConfig(this);
      }
   }

   public static class Builder {
      String managementHost;
      int managementPort;
      AdvertiseBuilder advertiseBuilder;

      public Builder setManagementHost(String managementHost) {
         this.managementHost = managementHost;
         return this;
      }

      public Builder setManagementPort(int managementPort) {
         this.managementPort = managementPort;
         return this;
      }

      public AdvertiseBuilder enableAdvertise() {
         this.advertiseBuilder = new AdvertiseBuilder(this);
         return this.advertiseBuilder;
      }

      public MCMPConfig build() {
         return new MCMPConfig(this);
      }

      public HttpHandler create(ModCluster modCluster, HttpHandler next) {
         MCMPConfig config = this.build();
         return config.create(modCluster, next);
      }
   }

   static class AdvertiseConfig {
      private final String advertiseGroup;
      private final String advertiseAddress;
      private final int advertisePort;
      private final int advertiseTtl;
      private final int advertiseFrequency;
      private final String securityKey;
      private final String protocol;
      private final String path;
      private final InetSocketAddress managementSocketAddress;

      AdvertiseConfig(AdvertiseBuilder builder, MCMPConfig config) {
         this.advertiseGroup = builder.advertiseGroup;
         this.advertiseAddress = builder.advertiseAddress;
         this.advertisePort = builder.advertisePort;
         this.advertiseTtl = builder.advertiseTtl;
         this.advertiseFrequency = builder.advertiseFrequency;
         this.securityKey = builder.securityKey;
         this.protocol = builder.protocol;
         this.path = builder.path;
         this.managementSocketAddress = config.getManagementSocketAddress();
      }

      public String getAdvertiseGroup() {
         return this.advertiseGroup;
      }

      public String getAdvertiseAddress() {
         return this.advertiseAddress;
      }

      public int getAdvertisePort() {
         return this.advertisePort;
      }

      public int getAdvertiseTtl() {
         return this.advertiseTtl;
      }

      public int getAdvertiseFrequency() {
         return this.advertiseFrequency;
      }

      public String getSecurityKey() {
         return this.securityKey;
      }

      public String getProtocol() {
         return this.protocol;
      }

      public String getPath() {
         return this.path;
      }

      public InetSocketAddress getManagementSocketAddress() {
         return this.managementSocketAddress;
      }
   }

   static class MCMPWebManagerConfig extends MCMPConfig {
      private final boolean allowCmd;
      private final boolean checkNonce;
      private final boolean reduceDisplay;

      MCMPWebManagerConfig(WebBuilder builder) {
         super(builder);
         this.allowCmd = builder.allowCmd;
         this.checkNonce = builder.checkNonce;
         this.reduceDisplay = builder.reduceDisplay;
      }

      public boolean isAllowCmd() {
         return this.allowCmd;
      }

      public boolean isCheckNonce() {
         return this.checkNonce;
      }

      public boolean isReduceDisplay() {
         return this.reduceDisplay;
      }

      public HttpHandler create(ModCluster modCluster, HttpHandler next) {
         return new MCMPWebManager(this, modCluster, next);
      }
   }
}
