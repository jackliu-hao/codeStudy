package org.h2.server.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.IOUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

class WebThread extends WebApp implements Runnable {
   private static final byte[] RN = new byte[]{13, 10};
   private static final byte[] RNRN = new byte[]{13, 10, 13, 10};
   protected OutputStream output;
   protected final Socket socket;
   private final Thread thread;
   private InputStream input;
   private String host;
   private int dataLength;
   private String ifModifiedSince;

   WebThread(Socket var1, WebServer var2) {
      super(var2);
      this.socket = var1;
      this.thread = new Thread(this, "H2 Console thread");
   }

   void start() {
      this.thread.start();
   }

   void join(int var1) throws InterruptedException {
      this.thread.join((long)var1);
   }

   void stopNow() {
      this.stop = true;

      try {
         this.socket.close();
      } catch (IOException var2) {
      }

   }

   private String getAllowedFile(String var1) {
      if (!this.allow()) {
         return "notAllowed.jsp";
      } else if (var1.length() == 0) {
         return "index.do";
      } else {
         return var1.charAt(0) == '?' ? "index.do" + var1 : var1;
      }
   }

   public void run() {
      try {
         this.input = new BufferedInputStream(this.socket.getInputStream());
         this.output = new BufferedOutputStream(this.socket.getOutputStream());

         while(!this.stop && this.process()) {
         }
      } catch (Exception var8) {
         DbException.traceThrowable(var8);
      }

      IOUtils.closeSilently(this.output);
      IOUtils.closeSilently(this.input);

      try {
         this.socket.close();
      } catch (IOException var6) {
      } finally {
         this.server.remove(this);
      }

   }

   private boolean process() throws IOException {
      String var1 = this.readHeaderLine();
      boolean var2 = var1.startsWith("GET ");
      if ((var2 || var1.startsWith("POST ")) && var1.endsWith(" HTTP/1.1")) {
         String var3 = StringUtils.trimSubstring(var1, var2 ? 4 : 5, var1.length() - 9);
         if (!var3.isEmpty() && var3.charAt(0) == '/') {
            this.attributes = new Properties();
            boolean var4 = this.parseHeader();
            if (!this.checkHost(this.host)) {
               return false;
            } else {
               var3 = var3.substring(1);
               this.trace(var1 + ": " + var3);
               var3 = this.getAllowedFile(var3);
               int var5 = var3.indexOf(63);
               this.session = null;
               String var6 = null;
               String var7;
               if (var5 >= 0) {
                  var7 = var3.substring(var5 + 1);
                  this.parseAttributes(var7);
                  String var8 = this.attributes.getProperty("jsessionid");
                  var6 = this.attributes.getProperty("key");
                  var3 = var3.substring(0, var5);
                  this.session = this.server.getSession(var8);
               }

               this.parseBodyAttributes();
               var3 = this.processRequest(var3, new NetworkConnectionInfo(NetUtils.ipToShortForm(new StringBuilder(this.server.getSSL() ? "https://" : "http://"), this.socket.getLocalAddress().getAddress(), true).append(':').append(this.socket.getLocalPort()).toString(), this.socket.getInetAddress().getAddress(), this.socket.getPort(), (String)null));
               if (var3.length() == 0) {
                  return true;
               } else if (this.cache && this.ifModifiedSince != null && this.ifModifiedSince.equals(this.server.getStartDateTime())) {
                  this.writeSimple("HTTP/1.1 304 Not Modified", (byte[])null);
                  return var4;
               } else {
                  byte[] var12 = this.server.getFile(var3);
                  if (var12 == null) {
                     this.writeSimple("HTTP/1.1 404 Not Found", "File not found: " + var3);
                     return var4;
                  } else {
                     if (this.session != null && var3.endsWith(".jsp")) {
                        if (var6 != null) {
                           this.session.put("key", var6);
                        }

                        String var9 = new String(var12, StandardCharsets.UTF_8);
                        if (SysProperties.CONSOLE_STREAM) {
                           Iterator var10 = (Iterator)this.session.map.remove("chunks");
                           if (var10 != null) {
                              var7 = "HTTP/1.1 200 OK\r\n";
                              var7 = var7 + "Content-Type: " + this.mimeType + "\r\n";
                              var7 = var7 + "Cache-Control: no-cache\r\n";
                              var7 = var7 + "Transfer-Encoding: chunked\r\n";
                              var7 = var7 + "\r\n";
                              this.trace(var7);
                              this.output.write(var7.getBytes(StandardCharsets.ISO_8859_1));

                              while(var10.hasNext()) {
                                 String var11 = (String)var10.next();
                                 var11 = PageParser.parse(var11, this.session.map);
                                 var12 = var11.getBytes(StandardCharsets.UTF_8);
                                 if (var12.length != 0) {
                                    this.output.write(Integer.toHexString(var12.length).getBytes(StandardCharsets.ISO_8859_1));
                                    this.output.write(RN);
                                    this.output.write(var12);
                                    this.output.write(RN);
                                    this.output.flush();
                                 }
                              }

                              this.output.write(48);
                              this.output.write(RNRN);
                              this.output.flush();
                              return var4;
                           }
                        }

                        var9 = PageParser.parse(var9, this.session.map);
                        var12 = var9.getBytes(StandardCharsets.UTF_8);
                     }

                     var7 = "HTTP/1.1 200 OK\r\n";
                     var7 = var7 + "Content-Type: " + this.mimeType + "\r\n";
                     if (!this.cache) {
                        var7 = var7 + "Cache-Control: no-cache\r\n";
                     } else {
                        var7 = var7 + "Cache-Control: max-age=10\r\n";
                        var7 = var7 + "Last-Modified: " + this.server.getStartDateTime() + "\r\n";
                     }

                     var7 = var7 + "Content-Length: " + var12.length + "\r\n";
                     var7 = var7 + "\r\n";
                     this.trace(var7);
                     this.output.write(var7.getBytes(StandardCharsets.ISO_8859_1));
                     this.output.write(var12);
                     this.output.flush();
                     return var4;
                  }
               }
            }
         } else {
            this.writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
            return false;
         }
      } else {
         this.writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
         return false;
      }
   }

   private void writeSimple(String var1, String var2) throws IOException {
      this.writeSimple(var1, var2 != null ? var2.getBytes(StandardCharsets.UTF_8) : null);
   }

   private void writeSimple(String var1, byte[] var2) throws IOException {
      this.trace(var1);
      this.output.write(var1.getBytes(StandardCharsets.ISO_8859_1));
      if (var2 != null) {
         this.output.write(RN);
         String var3 = "Content-Length: " + var2.length;
         this.trace(var3);
         this.output.write(var3.getBytes(StandardCharsets.ISO_8859_1));
         this.output.write(RNRN);
         this.output.write(var2);
      } else {
         this.output.write(RNRN);
      }

      this.output.flush();
   }

   private boolean checkHost(String var1) throws IOException {
      if (var1 == null) {
         this.writeSimple("HTTP/1.1 400 Bad Request", "Bad request");
         return false;
      } else {
         int var2 = var1.indexOf(58);
         if (var2 >= 0) {
            var1 = var1.substring(0, var2);
         }

         if (var1.isEmpty()) {
            return false;
         } else {
            var1 = StringUtils.toLowerEnglish(var1);
            if (!var1.equals(this.server.getHost()) && !var1.equals("localhost") && !var1.equals("127.0.0.1")) {
               String var3 = this.server.getExternalNames();
               if (var3 != null && !var3.isEmpty()) {
                  String[] var4 = var3.split(",");
                  int var5 = var4.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     String var7 = var4[var6];
                     if (var1.equals(var7.trim())) {
                        return true;
                     }
                  }
               }

               this.writeSimple("HTTP/1.1 404 Not Found", "Host " + var1 + " not found");
               return false;
            } else {
               return true;
            }
         }
      }
   }

   private String readHeaderLine() throws IOException {
      StringBuilder var1 = new StringBuilder();

      while(true) {
         int var2 = this.input.read();
         if (var2 == -1) {
            throw new IOException("Unexpected EOF");
         }

         if (var2 == 13) {
            if (this.input.read() == 10) {
               return var1.length() > 0 ? var1.toString() : null;
            }
         } else {
            if (var2 == 10) {
               return var1.length() > 0 ? var1.toString() : null;
            }

            var1.append((char)var2);
         }
      }
   }

   private void parseBodyAttributes() throws IOException {
      if (this.dataLength > 0) {
         byte[] var1 = Utils.newBytes(this.dataLength);

         for(int var2 = 0; var2 < this.dataLength; var2 += this.input.read(var1, var2, this.dataLength - var2)) {
         }

         String var3 = new String(var1, StandardCharsets.UTF_8);
         this.parseAttributes(var3);
      }

   }

   private void parseAttributes(String var1) {
      this.trace("data=" + var1);

      while(var1 != null) {
         int var2 = var1.indexOf(61);
         if (var2 < 0) {
            break;
         }

         String var3 = var1.substring(0, var2);
         var1 = var1.substring(var2 + 1);
         var2 = var1.indexOf(38);
         String var4;
         if (var2 >= 0) {
            var4 = var1.substring(0, var2);
            var1 = var1.substring(var2 + 1);
         } else {
            var4 = var1;
         }

         String var5 = StringUtils.urlDecode(var4);
         this.attributes.put(var3, var5);
      }

      this.trace(this.attributes.toString());
   }

   private boolean parseHeader() throws IOException {
      boolean var1 = false;
      this.trace("parseHeader");
      int var2 = 0;
      this.host = null;
      this.ifModifiedSince = null;
      boolean var3 = false;

      String var4;
      while((var4 = this.readHeaderLine()) != null) {
         this.trace(" " + var4);
         String var5 = StringUtils.toLowerEnglish(var4);
         if (var5.startsWith("host")) {
            this.host = getHeaderLineValue(var4);
         } else if (var5.startsWith("if-modified-since")) {
            this.ifModifiedSince = getHeaderLineValue(var4);
         } else {
            String var14;
            if (var5.startsWith("connection")) {
               var14 = getHeaderLineValue(var4);
               if ("keep-alive".equals(var14)) {
                  var1 = true;
               }
            } else if (var5.startsWith("content-type")) {
               var14 = getHeaderLineValue(var4);
               if (var14.startsWith("multipart/form-data")) {
                  var3 = true;
               }
            } else if (var5.startsWith("content-length")) {
               var2 = Integer.parseInt(getHeaderLineValue(var4));
               this.trace("len=" + var2);
            } else if (var5.startsWith("user-agent")) {
               boolean var13 = var5.contains("webkit/");
               if (var13 && this.session != null) {
                  this.session.put("frame-border", "1");
                  this.session.put("frameset-border", "2");
               }
            } else if (var5.startsWith("accept-language")) {
               Locale var6 = this.session == null ? null : this.session.locale;
               if (var6 == null) {
                  String var7 = getHeaderLineValue(var4);
                  StringTokenizer var8 = new StringTokenizer(var7, ",;");

                  while(var8.hasMoreTokens()) {
                     String var9 = var8.nextToken();
                     if (!var9.startsWith("q=") && this.server.supportsLanguage(var9)) {
                        int var10 = var9.indexOf(45);
                        if (var10 >= 0) {
                           String var11 = var9.substring(0, var10);
                           String var12 = var9.substring(var10 + 1);
                           var6 = new Locale(var11, var12);
                        } else {
                           var6 = new Locale(var9, "");
                        }

                        this.headerLanguage = var6.getLanguage();
                        if (this.session != null) {
                           this.session.locale = var6;
                           this.session.put("language", this.headerLanguage);
                           this.server.readTranslations(this.session, this.headerLanguage);
                        }
                        break;
                     }
                  }
               }
            } else if (StringUtils.isWhitespaceOrEmpty(var4)) {
               break;
            }
         }
      }

      this.dataLength = 0;
      if (!var3 && var2 > 0) {
         this.dataLength = var2;
      }

      return var1;
   }

   private static String getHeaderLineValue(String var0) {
      return StringUtils.trimSubstring(var0, var0.indexOf(58) + 1);
   }

   protected String adminShutdown() {
      this.stopNow();
      return super.adminShutdown();
   }

   private boolean allow() {
      if (this.server.getAllowOthers()) {
         return true;
      } else {
         try {
            return NetUtils.isLocalAddress(this.socket);
         } catch (UnknownHostException var2) {
            this.server.traceError(var2);
            return false;
         }
      }
   }

   private void trace(String var1) {
      this.server.trace(var1);
   }
}
