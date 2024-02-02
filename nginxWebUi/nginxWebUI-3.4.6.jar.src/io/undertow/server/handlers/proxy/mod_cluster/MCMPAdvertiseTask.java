/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.MulticastMessageChannel;
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
/*     */ 
/*     */ class MCMPAdvertiseTask
/*     */   implements Runnable
/*     */ {
/*     */   public static final String RFC_822_FMT = "EEE, d MMM yyyy HH:mm:ss Z";
/*  47 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
/*     */   private static final boolean linuxLike;
/*     */   private static final boolean windows;
/*     */   
/*     */   static {
/*  52 */     String value = System.getProperty("os.name");
/*  53 */     linuxLike = (value != null && (value.toLowerCase().startsWith("linux") || value.toLowerCase().startsWith("mac") || value.toLowerCase().startsWith("hp")));
/*  54 */     windows = (value != null && value.toLowerCase().contains("win"));
/*     */   }
/*     */   
/*  57 */   private volatile int seq = 0;
/*     */   
/*     */   private final String protocol;
/*     */   
/*     */   private final String host;
/*     */   
/*     */   private final int port;
/*     */   
/*     */   private final String path;
/*     */   private final byte[] ssalt;
/*     */   
/*     */   static void advertise(ModClusterContainer container, MCMPConfig.AdvertiseConfig config, XnioWorker worker) throws IOException {
/*     */     InetSocketAddress bindAddress;
/*     */     MulticastMessageChannel channel;
/*  71 */     InetAddress address = InetAddress.getByName(config.getAdvertiseAddress());
/*     */     
/*  73 */     if (address == null) {
/*  74 */       bindAddress = new InetSocketAddress(config.getAdvertisePort());
/*     */     } else {
/*  76 */       bindAddress = new InetSocketAddress(address, config.getAdvertisePort());
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  81 */       channel = worker.createUdpServer(bindAddress, OptionMap.EMPTY);
/*  82 */     } catch (IOException e) {
/*  83 */       if (address != null && (linuxLike || windows)) {
/*     */ 
/*     */         
/*  86 */         UndertowLogger.ROOT_LOGGER.potentialCrossTalking(address, (address instanceof java.net.Inet4Address) ? "IPv4" : "IPv6", e.getLocalizedMessage());
/*  87 */         bindAddress = new InetSocketAddress(config.getAdvertisePort());
/*  88 */         channel = worker.createUdpServer(bindAddress, OptionMap.EMPTY);
/*     */       } else {
/*  90 */         throw e;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  95 */     channel.setOption(Options.MULTICAST_TTL, Integer.valueOf(config.getAdvertiseTtl()));
/*     */     
/*  97 */     MCMPAdvertiseTask task = new MCMPAdvertiseTask(container, config, channel);
/*     */     
/*  99 */     channel.getIoThread().execute(task);
/* 100 */     channel.getIoThread().executeAtInterval(task, config.getAdvertiseFrequency(), TimeUnit.MILLISECONDS);
/*     */   }
/*     */   private final MessageDigest md; private final InetSocketAddress address; private final ModClusterContainer container; private final MulticastMessageChannel channel; private static final String CRLF = "\r\n";
/*     */   
/*     */   MCMPAdvertiseTask(ModClusterContainer container, MCMPConfig.AdvertiseConfig config, MulticastMessageChannel channel) throws IOException {
/* 105 */     this.container = container;
/* 106 */     this.protocol = config.getProtocol();
/*     */     
/* 108 */     String host = config.getManagementSocketAddress().getHostString();
/* 109 */     int zoneIndex = host.indexOf("%");
/* 110 */     this.host = (zoneIndex < 0) ? host : host.substring(0, zoneIndex);
/* 111 */     this.port = config.getManagementSocketAddress().getPort();
/* 112 */     this.path = config.getPath();
/* 113 */     this.channel = channel;
/*     */     
/* 115 */     InetAddress group = InetAddress.getByName(config.getAdvertiseGroup());
/* 116 */     this.address = new InetSocketAddress(group, config.getAdvertisePort());
/*     */     try {
/* 118 */       this.md = MessageDigest.getInstance("MD5");
/* 119 */     } catch (NoSuchAlgorithmException e) {
/* 120 */       throw new RuntimeException(e);
/*     */     } 
/* 122 */     String securityKey = config.getSecurityKey();
/* 123 */     if (securityKey == null) {
/*     */       
/* 125 */       this.ssalt = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */     } else {
/* 127 */       this.md.reset();
/* 128 */       digestString(this.md, securityKey);
/* 129 */       this.ssalt = this.md.digest();
/*     */     } 
/*     */     
/* 132 */     UndertowLogger.ROOT_LOGGER.proxyAdvertisementsStarted(this.address.toString(), config.getAdvertiseFrequency());
/*     */   }
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
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 160 */       byte[] digest, ssalt = this.ssalt;
/* 161 */       String server = this.container.getServerID();
/* 162 */       String date = DATE_FORMAT.format(new Date(System.currentTimeMillis()));
/* 163 */       String seq = "" + this.seq++;
/*     */ 
/*     */       
/* 166 */       synchronized (this.md) {
/* 167 */         this.md.reset();
/* 168 */         this.md.update(ssalt);
/* 169 */         digestString(this.md, date);
/* 170 */         digestString(this.md, seq);
/* 171 */         digestString(this.md, server);
/* 172 */         digest = this.md.digest();
/*     */       } 
/* 174 */       String digestString = bytesToHexString(digest);
/*     */       
/* 176 */       StringBuilder builder = new StringBuilder();
/* 177 */       builder.append("HTTP/1.0 200 OK").append("\r\n")
/* 178 */         .append("Date: ").append(date).append("\r\n")
/* 179 */         .append("Sequence: ").append(seq).append("\r\n")
/* 180 */         .append("Digest: ").append(digestString).append("\r\n")
/* 181 */         .append("Server: ").append(server).append("\r\n")
/* 182 */         .append("X-Manager-Address: ").append(NetworkUtils.formatPossibleIpv6Address(this.host)).append(":").append(this.port).append("\r\n")
/* 183 */         .append("X-Manager-Url: ").append(this.path).append("\r\n")
/* 184 */         .append("X-Manager-Protocol: ").append(this.protocol).append("\r\n")
/* 185 */         .append("X-Manager-Host: ").append(this.host).append("\r\n");
/*     */       
/* 187 */       String payload = builder.toString();
/* 188 */       ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes(StandardCharsets.US_ASCII));
/* 189 */       UndertowLogger.ROOT_LOGGER.proxyAdvertiseMessagePayload(payload);
/* 190 */       this.channel.sendTo(this.address, byteBuffer);
/* 191 */     } catch (Exception e) {
/* 192 */       UndertowLogger.ROOT_LOGGER.proxyAdvertiseCannotSendMessage(e, this.address);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void digestString(MessageDigest md, String securityKey) {
/* 197 */     byte[] buf = securityKey.getBytes(StandardCharsets.UTF_8);
/* 198 */     md.update(buf);
/*     */   }
/*     */   
/* 201 */   private static final char[] TABLE = "0123456789abcdef".toCharArray();
/*     */   static String bytesToHexString(byte[] bytes) {
/* 203 */     StringBuilder builder = new StringBuilder(bytes.length * 2);
/* 204 */     for (byte b : bytes) {
/* 205 */       builder.append(TABLE[b >> 4 & 0xF]).append(TABLE[b & 0xF]);
/*     */     }
/* 207 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPAdvertiseTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */