package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.util.NetworkUtils;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.XnioWorker;
import org.xnio.channels.MulticastMessageChannel;

class MCMPAdvertiseTask implements Runnable {
   public static final String RFC_822_FMT = "EEE, d MMM yyyy HH:mm:ss Z";
   private static final SimpleDateFormat DATE_FORMAT;
   private static final boolean linuxLike;
   private static final boolean windows;
   private volatile int seq = 0;
   private final String protocol;
   private final String host;
   private final int port;
   private final String path;
   private final byte[] ssalt;
   private final MessageDigest md;
   private final InetSocketAddress address;
   private final ModClusterContainer container;
   private final MulticastMessageChannel channel;
   private static final String CRLF = "\r\n";
   private static final char[] TABLE;

   static void advertise(ModClusterContainer container, MCMPConfig.AdvertiseConfig config, XnioWorker worker) throws IOException {
      InetAddress address = InetAddress.getByName(config.getAdvertiseAddress());
      InetSocketAddress bindAddress;
      if (address == null) {
         bindAddress = new InetSocketAddress(config.getAdvertisePort());
      } else {
         bindAddress = new InetSocketAddress(address, config.getAdvertisePort());
      }

      MulticastMessageChannel channel;
      try {
         channel = worker.createUdpServer(bindAddress, OptionMap.EMPTY);
      } catch (IOException var7) {
         if (address == null || !linuxLike && !windows) {
            throw var7;
         }

         UndertowLogger.ROOT_LOGGER.potentialCrossTalking(address, address instanceof Inet4Address ? "IPv4" : "IPv6", var7.getLocalizedMessage());
         bindAddress = new InetSocketAddress(config.getAdvertisePort());
         channel = worker.createUdpServer(bindAddress, OptionMap.EMPTY);
      }

      channel.setOption(Options.MULTICAST_TTL, config.getAdvertiseTtl());
      MCMPAdvertiseTask task = new MCMPAdvertiseTask(container, config, channel);
      channel.getIoThread().execute(task);
      channel.getIoThread().executeAtInterval(task, (long)config.getAdvertiseFrequency(), TimeUnit.MILLISECONDS);
   }

   MCMPAdvertiseTask(ModClusterContainer container, MCMPConfig.AdvertiseConfig config, MulticastMessageChannel channel) throws IOException {
      this.container = container;
      this.protocol = config.getProtocol();
      String host = config.getManagementSocketAddress().getHostString();
      int zoneIndex = host.indexOf("%");
      this.host = zoneIndex < 0 ? host : host.substring(0, zoneIndex);
      this.port = config.getManagementSocketAddress().getPort();
      this.path = config.getPath();
      this.channel = channel;
      InetAddress group = InetAddress.getByName(config.getAdvertiseGroup());
      this.address = new InetSocketAddress(group, config.getAdvertisePort());

      try {
         this.md = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var8) {
         throw new RuntimeException(var8);
      }

      String securityKey = config.getSecurityKey();
      if (securityKey == null) {
         this.ssalt = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      } else {
         this.md.reset();
         this.digestString(this.md, securityKey);
         this.ssalt = this.md.digest();
      }

      UndertowLogger.ROOT_LOGGER.proxyAdvertisementsStarted(this.address.toString(), config.getAdvertiseFrequency());
   }

   public void run() {
      try {
         byte[] ssalt = this.ssalt;
         String server = this.container.getServerID();
         String date = DATE_FORMAT.format(new Date(System.currentTimeMillis()));
         String seq = "" + this.seq++;
         byte[] digest;
         synchronized(this.md) {
            this.md.reset();
            this.md.update(ssalt);
            this.digestString(this.md, date);
            this.digestString(this.md, seq);
            this.digestString(this.md, server);
            digest = this.md.digest();
         }

         String digestString = bytesToHexString(digest);
         StringBuilder builder = new StringBuilder();
         builder.append("HTTP/1.0 200 OK").append("\r\n").append("Date: ").append(date).append("\r\n").append("Sequence: ").append(seq).append("\r\n").append("Digest: ").append(digestString).append("\r\n").append("Server: ").append(server).append("\r\n").append("X-Manager-Address: ").append(NetworkUtils.formatPossibleIpv6Address(this.host)).append(":").append(this.port).append("\r\n").append("X-Manager-Url: ").append(this.path).append("\r\n").append("X-Manager-Protocol: ").append(this.protocol).append("\r\n").append("X-Manager-Host: ").append(this.host).append("\r\n");
         String payload = builder.toString();
         ByteBuffer byteBuffer = ByteBuffer.wrap(payload.getBytes(StandardCharsets.US_ASCII));
         UndertowLogger.ROOT_LOGGER.proxyAdvertiseMessagePayload(payload);
         this.channel.sendTo(this.address, byteBuffer);
      } catch (Exception var11) {
         UndertowLogger.ROOT_LOGGER.proxyAdvertiseCannotSendMessage(var11, this.address);
      }

   }

   private void digestString(MessageDigest md, String securityKey) {
      byte[] buf = securityKey.getBytes(StandardCharsets.UTF_8);
      md.update(buf);
   }

   static String bytesToHexString(byte[] bytes) {
      StringBuilder builder = new StringBuilder(bytes.length * 2);
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         builder.append(TABLE[b >> 4 & 15]).append(TABLE[b & 15]);
      }

      return builder.toString();
   }

   static {
      DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
      String value = System.getProperty("os.name");
      linuxLike = value != null && (value.toLowerCase().startsWith("linux") || value.toLowerCase().startsWith("mac") || value.toLowerCase().startsWith("hp"));
      windows = value != null && value.toLowerCase().contains("win");
      TABLE = "0123456789abcdef".toCharArray();
   }
}
