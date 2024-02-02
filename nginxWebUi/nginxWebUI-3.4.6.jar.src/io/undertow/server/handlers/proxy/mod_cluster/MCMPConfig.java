/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import java.net.InetSocketAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MCMPConfig
/*     */ {
/*     */   private final InetSocketAddress managementSocketAddress;
/*     */   private final AdvertiseConfig advertiseConfig;
/*     */   
/*     */   public static Builder builder() {
/*  34 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static WebBuilder webBuilder() {
/*  38 */     return new WebBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MCMPConfig(Builder builder) {
/*  45 */     this.managementSocketAddress = new InetSocketAddress(builder.managementHost, builder.managementPort);
/*  46 */     if (this.managementSocketAddress.isUnresolved()) {
/*  47 */       throw UndertowLogger.PROXY_REQUEST_LOGGER.unableToResolveModClusterManagementHost(builder.managementHost);
/*     */     }
/*  49 */     if (this.managementSocketAddress.getAddress().isAnyLocalAddress()) {
/*  50 */       throw UndertowLogger.PROXY_REQUEST_LOGGER.cannotUseWildcardAddressAsModClusterManagementHost(builder.managementHost);
/*     */     }
/*  52 */     if (builder.advertiseBuilder != null) {
/*  53 */       this.advertiseConfig = new AdvertiseConfig(builder.advertiseBuilder, this);
/*     */     } else {
/*  55 */       this.advertiseConfig = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getManagementHost() {
/*  61 */     return this.managementSocketAddress.getHostString();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public int getManagementPort() {
/*  66 */     return this.managementSocketAddress.getPort();
/*     */   }
/*     */   public InetSocketAddress getManagementSocketAddress() {
/*  69 */     return this.managementSocketAddress;
/*     */   }
/*     */   
/*     */   public AdvertiseConfig getAdvertiseConfig() {
/*  73 */     return this.advertiseConfig;
/*     */   }
/*     */   
/*     */   public HttpHandler create(ModCluster modCluster, HttpHandler next) {
/*  77 */     return new MCMPHandler(this, modCluster, next);
/*     */   }
/*     */   
/*     */   static class MCMPWebManagerConfig
/*     */     extends MCMPConfig {
/*     */     private final boolean allowCmd;
/*     */     private final boolean checkNonce;
/*     */     private final boolean reduceDisplay;
/*     */     
/*     */     MCMPWebManagerConfig(MCMPConfig.WebBuilder builder) {
/*  87 */       super(builder);
/*  88 */       this.allowCmd = builder.allowCmd;
/*  89 */       this.checkNonce = builder.checkNonce;
/*  90 */       this.reduceDisplay = builder.reduceDisplay;
/*     */     }
/*     */     
/*     */     public boolean isAllowCmd() {
/*  94 */       return this.allowCmd;
/*     */     }
/*     */     
/*     */     public boolean isCheckNonce() {
/*  98 */       return this.checkNonce;
/*     */     }
/*     */     
/*     */     public boolean isReduceDisplay() {
/* 102 */       return this.reduceDisplay;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler create(ModCluster modCluster, HttpHandler next) {
/* 107 */       return new MCMPWebManager(this, modCluster, next);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class AdvertiseConfig
/*     */   {
/*     */     private final String advertiseGroup;
/*     */     
/*     */     private final String advertiseAddress;
/*     */     private final int advertisePort;
/*     */     private final int advertiseTtl;
/*     */     private final int advertiseFrequency;
/*     */     private final String securityKey;
/*     */     private final String protocol;
/*     */     private final String path;
/*     */     private final InetSocketAddress managementSocketAddress;
/*     */     
/*     */     AdvertiseConfig(MCMPConfig.AdvertiseBuilder builder, MCMPConfig config) {
/* 126 */       this.advertiseGroup = builder.advertiseGroup;
/* 127 */       this.advertiseAddress = builder.advertiseAddress;
/* 128 */       this.advertisePort = builder.advertisePort;
/* 129 */       this.advertiseTtl = builder.advertiseTtl;
/* 130 */       this.advertiseFrequency = builder.advertiseFrequency;
/* 131 */       this.securityKey = builder.securityKey;
/* 132 */       this.protocol = builder.protocol;
/* 133 */       this.path = builder.path;
/* 134 */       this.managementSocketAddress = config.getManagementSocketAddress();
/*     */     }
/*     */     
/*     */     public String getAdvertiseGroup() {
/* 138 */       return this.advertiseGroup;
/*     */     }
/*     */     
/*     */     public String getAdvertiseAddress() {
/* 142 */       return this.advertiseAddress;
/*     */     }
/*     */     
/*     */     public int getAdvertisePort() {
/* 146 */       return this.advertisePort;
/*     */     }
/*     */     
/*     */     public int getAdvertiseTtl() {
/* 150 */       return this.advertiseTtl;
/*     */     }
/*     */     
/*     */     public int getAdvertiseFrequency() {
/* 154 */       return this.advertiseFrequency;
/*     */     }
/*     */     
/*     */     public String getSecurityKey() {
/* 158 */       return this.securityKey;
/*     */     }
/*     */     
/*     */     public String getProtocol() {
/* 162 */       return this.protocol;
/*     */     }
/*     */     
/*     */     public String getPath() {
/* 166 */       return this.path;
/*     */     }
/*     */     
/*     */     public InetSocketAddress getManagementSocketAddress() {
/* 170 */       return this.managementSocketAddress;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     String managementHost;
/*     */     int managementPort;
/*     */     MCMPConfig.AdvertiseBuilder advertiseBuilder;
/*     */     
/*     */     public Builder setManagementHost(String managementHost) {
/* 181 */       this.managementHost = managementHost;
/* 182 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setManagementPort(int managementPort) {
/* 186 */       this.managementPort = managementPort;
/* 187 */       return this;
/*     */     }
/*     */     
/*     */     public MCMPConfig.AdvertiseBuilder enableAdvertise() {
/* 191 */       this.advertiseBuilder = new MCMPConfig.AdvertiseBuilder(this);
/* 192 */       return this.advertiseBuilder;
/*     */     }
/*     */     
/*     */     public MCMPConfig build() {
/* 196 */       return new MCMPConfig(this);
/*     */     }
/*     */     
/*     */     public HttpHandler create(ModCluster modCluster, HttpHandler next) {
/* 200 */       MCMPConfig config = build();
/* 201 */       return config.create(modCluster, next);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class WebBuilder
/*     */     extends Builder
/*     */   {
/*     */     boolean checkNonce = true;
/*     */     boolean reduceDisplay = false;
/*     */     boolean allowCmd = true;
/*     */     
/*     */     public WebBuilder setCheckNonce(boolean checkNonce) {
/* 213 */       this.checkNonce = checkNonce;
/* 214 */       return this;
/*     */     }
/*     */     
/*     */     public WebBuilder setReduceDisplay(boolean reduceDisplay) {
/* 218 */       this.reduceDisplay = reduceDisplay;
/* 219 */       return this;
/*     */     }
/*     */     
/*     */     public WebBuilder setAllowCmd(boolean allowCmd) {
/* 223 */       this.allowCmd = allowCmd;
/* 224 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public MCMPConfig build() {
/* 229 */       return new MCMPConfig.MCMPWebManagerConfig(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class AdvertiseBuilder
/*     */   {
/* 236 */     String advertiseGroup = "224.0.1.105";
/* 237 */     String advertiseAddress = "127.0.0.1";
/* 238 */     int advertisePort = 23364;
/* 239 */     int advertiseTtl = 10;
/* 240 */     int advertiseFrequency = 10000;
/*     */     
/*     */     String securityKey;
/* 243 */     String protocol = "http";
/* 244 */     String path = "/";
/*     */     private final MCMPConfig.Builder parent;
/*     */     
/*     */     public AdvertiseBuilder(MCMPConfig.Builder parent) {
/* 248 */       this.parent = parent;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setAdvertiseGroup(String advertiseGroup) {
/* 252 */       this.advertiseGroup = advertiseGroup;
/* 253 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setAdvertiseAddress(String advertiseAddress) {
/* 257 */       this.advertiseAddress = advertiseAddress;
/* 258 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setAdvertisePort(int advertisePort) {
/* 262 */       this.advertisePort = advertisePort;
/* 263 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setAdvertiseTtl(int advertiseTtl) {
/* 267 */       this.advertiseTtl = advertiseTtl;
/* 268 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setAdvertiseFrequency(int advertiseFrequency) {
/* 272 */       this.advertiseFrequency = advertiseFrequency;
/* 273 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setSecurityKey(String securityKey) {
/* 277 */       this.securityKey = securityKey;
/* 278 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setProtocol(String protocol) {
/* 282 */       this.protocol = protocol;
/* 283 */       return this;
/*     */     }
/*     */     
/*     */     public AdvertiseBuilder setPath(String path) {
/* 287 */       if (path.startsWith("/")) {
/* 288 */         this.path = path;
/*     */       } else {
/* 290 */         this.path = "/" + path;
/*     */       } 
/* 292 */       return this;
/*     */     }
/*     */     
/*     */     public MCMPConfig.Builder getParent() {
/* 296 */       return this.parent;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */