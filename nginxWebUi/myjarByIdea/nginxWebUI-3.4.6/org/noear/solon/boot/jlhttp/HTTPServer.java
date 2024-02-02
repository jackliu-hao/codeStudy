package org.noear.solon.boot.jlhttp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class HTTPServer {
   protected static int MAX_BODY_SIZE = 2097152;
   protected static int MAX_HEADER_SIZE = 8192;
   public static final String[] DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss z", "EEEE, dd-MMM-yy HH:mm:ss z", "EEE MMM d HH:mm:ss yyyy"};
   protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");
   protected static final char[] DAYS = "Sun Mon Tue Wed Thu Fri Sat".toCharArray();
   protected static final char[] MONTHS = "Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec".toCharArray();
   public static final byte[] CRLF = new byte[]{13, 10};
   protected static final String[] statuses = new String[600];
   protected static final Map<String, String> contentTypes;
   protected static String[] compressibleContentTypes;
   protected volatile int port;
   protected volatile String host;
   protected volatile int socketTimeout;
   protected volatile ServerSocketFactory serverSocketFactory;
   protected volatile boolean secure;
   protected volatile Executor executor;
   protected volatile ServerSocket serv;
   protected final Map<String, VirtualHost> hosts;

   public HTTPServer(int port) {
      this.socketTimeout = 10000;
      this.hosts = new ConcurrentHashMap();
      this.setPort(port);
      this.addVirtualHost(new VirtualHost((String)null));
   }

   public HTTPServer(String host, int port) {
      this.socketTimeout = 10000;
      this.hosts = new ConcurrentHashMap();
      this.setPort(port);
      this.setHost(host);
      this.addVirtualHost(new VirtualHost((String)null));
   }

   public HTTPServer() {
      this(80);
   }

   public void setPort(int port) {
      this.port = port;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public void setServerSocketFactory(ServerSocketFactory factory) {
      this.serverSocketFactory = factory;
      this.secure = factory instanceof SSLServerSocketFactory;
   }

   public void setSocketTimeout(int timeout) {
      this.socketTimeout = timeout;
   }

   public void setExecutor(Executor executor) {
      this.executor = executor;
   }

   public VirtualHost getVirtualHost(String name) {
      return (VirtualHost)this.hosts.get(name == null ? "" : name);
   }

   public Set<VirtualHost> getVirtualHosts() {
      return Collections.unmodifiableSet(new HashSet(this.hosts.values()));
   }

   public void addVirtualHost(VirtualHost host) {
      String name = host.getName();
      this.hosts.put(name == null ? "" : name, host);
   }

   protected ServerSocket createServerSocket() throws IOException {
      ServerSocket serv = this.serverSocketFactory.createServerSocket();
      serv.setReuseAddress(true);
      InetSocketAddress address = null;
      if (this.host == null) {
         address = new InetSocketAddress(this.port);
      } else {
         address = new InetSocketAddress(this.host, this.port);
      }

      serv.bind(address);
      return serv;
   }

   public synchronized void start() throws IOException {
      if (this.serv == null) {
         if (this.serverSocketFactory == null) {
            this.serverSocketFactory = ServerSocketFactory.getDefault();
         }

         this.serv = this.createServerSocket();
         if (this.executor == null) {
            this.executor = Executors.newCachedThreadPool();
         }

         Iterator var1 = this.getVirtualHosts().iterator();

         while(var1.hasNext()) {
            VirtualHost host = (VirtualHost)var1.next();
            Iterator var3 = host.getAliases().iterator();

            while(var3.hasNext()) {
               String alias = (String)var3.next();
               this.hosts.put(alias, host);
            }
         }

         (new SocketHandlerThread()).start();
      }
   }

   public synchronized void stop() {
      try {
         if (this.serv != null) {
            this.serv.close();
         }
      } catch (IOException var2) {
      }

      this.serv = null;
   }

   protected void handleConnection(InputStream in, OutputStream out, Socket sock) throws IOException {
      InputStream in = new BufferedInputStream(in, 4096);
      OutputStream out = new BufferedOutputStream(out, 4096);

      Request req;
      Response resp;
      do {
         req = null;
         resp = new Response(out);

         try {
            req = new Request(in, sock);
            this.handleTransaction(req, resp);
         } catch (Throwable var10) {
            if (req == null) {
               if (!(var10 instanceof IOException) || !var10.getMessage().contains("missing request line")) {
                  resp.getHeaders().add("Connection", "close");
                  if (var10 instanceof InterruptedIOException) {
                     resp.sendError(408, "Timeout waiting for client request");
                  } else {
                     resp.sendError(400, "Invalid request: " + var10.getMessage());
                  }
               }
            } else if (!resp.headersSent()) {
               resp = new Response(out);
               resp.getHeaders().add("Connection", "close");
               resp.sendError(500, "Error processing request: " + var10.getMessage());
            }
            break;
         } finally {
            resp.close();
         }

         transfer(req.getBody(), (OutputStream)null, -1L);
      } while(!"close".equalsIgnoreCase(req.getHeaders().get("Connection")) && !"close".equalsIgnoreCase(resp.getHeaders().get("Connection")) && req.getVersion().endsWith("1.1"));

   }

   protected void handleTransaction(Request req, Response resp) throws IOException {
      resp.setClientCapabilities(req);
      if (this.preprocessTransaction(req, resp)) {
         this.handleMethod(req, resp);
      }

   }

   protected boolean preprocessTransaction(Request req, Response resp) throws IOException {
      Headers reqHeaders = req.getHeaders();
      String version = req.getVersion();
      if (version.equals("HTTP/1.1")) {
         if (!reqHeaders.contains("Host")) {
            resp.sendError(400, "Missing required Host header");
            return false;
         }

         String expect = reqHeaders.get("Expect");
         if (expect != null) {
            if (!expect.equalsIgnoreCase("100-continue")) {
               resp.sendError(417);
               return false;
            }

            Response tempResp = new Response(resp.getOutputStream());
            tempResp.sendHeaders(100);
            resp.getOutputStream().flush();
         }
      } else {
         if (!version.equals("HTTP/1.0") && !version.equals("HTTP/0.9")) {
            resp.sendError(400, "Unknown version: " + version);
            return false;
         }

         String[] var9 = splitElements(reqHeaders.get("Connection"), false);
         int var10 = var9.length;

         for(int var7 = 0; var7 < var10; ++var7) {
            String token = var9[var7];
            reqHeaders.remove(token);
         }
      }

      return true;
   }

   protected void handleMethod(Request req, Response resp) throws IOException {
      String method = req.getMethod();
      Map<String, ContextHandler> handlers = req.getContext().getHandlers();
      if (!method.equals("GET") && !handlers.containsKey(method)) {
         if (method.equals("HEAD")) {
            req.method = "GET";
            resp.setDiscardBody(true);
            this.serve(req, resp);
         } else if (method.equals("TRACE")) {
            this.handleTrace(req, resp);
         } else {
            Set<String> methods = new LinkedHashSet();
            methods.addAll(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));
            boolean isServerOptions = req.getPath().equals("*") && method.equals("OPTIONS");
            methods.addAll(isServerOptions ? req.getVirtualHost().getMethods() : handlers.keySet());
            resp.getHeaders().add("Allow", join(", ", methods));
            if (method.equals("OPTIONS")) {
               resp.getHeaders().add("Content-Length", "0");
               resp.sendHeaders(200);
            } else if (req.getVirtualHost().getMethods().contains(method)) {
               resp.sendHeaders(405);
            } else {
               resp.sendError(501);
            }
         }
      } else {
         this.serve(req, resp);
      }

   }

   public void handleTrace(Request req, Response resp) throws IOException {
      resp.sendHeaders(200, -1L, -1L, (String)null, "message/http", (long[])null);
      OutputStream out = resp.getBody();
      out.write(getBytes("TRACE ", req.getURI().toString(), " ", req.getVersion()));
      out.write(CRLF);
      req.getHeaders().writeTo(out);
      transfer(req.getBody(), out, -1L);
   }

   protected void serve(Request req, Response resp) throws IOException {
      ContextHandler handler = (ContextHandler)req.getContext().getHandlers().get(req.getMethod());
      if (handler == null) {
         resp.sendError(404);
      } else {
         int status = 404;
         String path = req.getPath();
         if (path.endsWith("/")) {
            String index = req.getVirtualHost().getDirectoryIndex();
            if (index != null) {
               req.setPath(path + index);
               status = handler.serve(req, resp);
               req.setPath(path);
            }
         }

         if (status == 404) {
            status = handler.serve(req, resp);
         }

         if (status > 0) {
            resp.sendError(status);
         }

      }
   }

   public static void addContentType(String contentType, String... suffixes) {
      String[] var2 = suffixes;
      int var3 = suffixes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String suffix = var2[var4];
         contentTypes.put(suffix.toLowerCase(Locale.US), contentType.toLowerCase(Locale.US));
      }

   }

   public static void addContentTypes(InputStream in) throws IOException {
      try {
         while(true) {
            String line = readLine(in).trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
               String[] tokens = split(line, " \t", -1);

               for(int i = 1; i < tokens.length; ++i) {
                  addContentType(tokens[0], tokens[i]);
               }
            }
         }
      } catch (EOFException var7) {
      } finally {
         in.close();
      }

   }

   public static String getContentType(String path, String def) {
      int dot = path.lastIndexOf(46);
      String type = dot < 0 ? def : (String)contentTypes.get(path.substring(dot + 1).toLowerCase(Locale.US));
      return type != null ? type : def;
   }

   public static boolean isCompressible(String contentType) {
      int pos = contentType.indexOf(59);
      String ct = pos < 0 ? contentType : contentType.substring(0, pos);
      String[] var3 = compressibleContentTypes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String s = var3[var5];
         if (s.equals(ct) || s.charAt(0) == '*' && ct.endsWith(s.substring(1)) || s.charAt(s.length() - 1) == '*' && ct.startsWith(s.substring(0, s.length() - 1))) {
            return true;
         }
      }

      return false;
   }

   public static String detectLocalHostName() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException var1) {
         return "localhost";
      }
   }

   public static List<String[]> parseParamsList(String s) {
      if (s != null && s.length() != 0) {
         List<String[]> params = new ArrayList(8);
         String[] var2 = split(s, "&", -1);
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String pair = var2[var4];
            int pos = pair.indexOf(61);
            String name = pos < 0 ? pair : pair.substring(0, pos);
            String val = pos < 0 ? "" : pair.substring(pos + 1);

            try {
               name = URLDecoder.decode(name.trim(), "UTF-8");
               val = URLDecoder.decode(val.trim(), "UTF-8");
               if (name.length() > 0) {
                  params.add(new String[]{name, val});
               }
            } catch (UnsupportedEncodingException var10) {
            }
         }

         return params;
      } else {
         return Collections.emptyList();
      }
   }

   public static <K, V> Map<K, V> toMap(Collection<? extends Object[]> pairs) {
      if (pairs != null && !pairs.isEmpty()) {
         Map<K, V> map = new LinkedHashMap(pairs.size());
         Iterator var2 = pairs.iterator();

         while(var2.hasNext()) {
            Object[] pair = (Object[])var2.next();
            if (!map.containsKey(pair[0])) {
               map.put(pair[0], pair[1]);
            }
         }

         return map;
      } else {
         return Collections.emptyMap();
      }
   }

   public static long[] parseRange(String range, long length) {
      long min = Long.MAX_VALUE;
      long max = Long.MIN_VALUE;

      try {
         String[] var7 = splitElements(range, false);
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String token = var7[var9];
            int dash = token.indexOf(45);
            long start;
            long end;
            if (dash == 0) {
               start = length - parseULong(token.substring(1), 10);
               end = length - 1L;
            } else if (dash == token.length() - 1) {
               start = parseULong(token.substring(0, dash), 10);
               end = length - 1L;
            } else {
               start = parseULong(token.substring(0, dash), 10);
               end = parseULong(token.substring(dash + 1), 10);
            }

            if (end < start) {
               throw new RuntimeException();
            }

            if (start < min) {
               min = start;
            }

            if (end > max) {
               max = end;
            }
         }

         if (max < 0L) {
            throw new RuntimeException();
         } else {
            if (max >= length && min < length) {
               max = length - 1L;
            }

            return new long[]{min, max};
         }
      } catch (RuntimeException var16) {
         return null;
      }
   }

   public static long parseULong(String s, int radix) throws NumberFormatException {
      long val = Long.parseLong(s, radix);
      if (s.charAt(0) != '-' && s.charAt(0) != '+') {
         return val;
      } else {
         throw new NumberFormatException("invalid digit: " + s.charAt(0));
      }
   }

   public static Date parseDate(String time) {
      String[] var1 = DATE_PATTERNS;
      int var2 = var1.length;
      int var3 = 0;

      while(var3 < var2) {
         String pattern = var1[var3];

         try {
            SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
            df.setLenient(false);
            df.setTimeZone(GMT);
            return df.parse(time);
         } catch (ParseException var6) {
            ++var3;
         }
      }

      throw new IllegalArgumentException("invalid date format: " + time);
   }

   public static String formatDate(long time) {
      if (time >= -62167392000000L && time <= 253402300799999L) {
         char[] s = "DAY, 00 MON 0000 00:00:00 GMT".toCharArray();
         Calendar cal = new GregorianCalendar(GMT, Locale.US);
         cal.setTimeInMillis(time);
         System.arraycopy(DAYS, 4 * (cal.get(7) - 1), s, 0, 3);
         System.arraycopy(MONTHS, 4 * cal.get(2), s, 8, 3);
         int n = cal.get(5);
         s[5] = (char)(s[5] + n / 10);
         s[6] = (char)(s[6] + n % 10);
         n = cal.get(1);
         s[12] = (char)(s[12] + n / 1000);
         s[13] = (char)(s[13] + n / 100 % 10);
         s[14] = (char)(s[14] + n / 10 % 10);
         s[15] = (char)(s[15] + n % 10);
         n = cal.get(11);
         s[17] = (char)(s[17] + n / 10);
         s[18] = (char)(s[18] + n % 10);
         n = cal.get(12);
         s[20] = (char)(s[20] + n / 10);
         s[21] = (char)(s[21] + n % 10);
         n = cal.get(13);
         s[23] = (char)(s[23] + n / 10);
         s[24] = (char)(s[24] + n % 10);
         return new String(s);
      } else {
         throw new IllegalArgumentException("year out of range (0001-9999): " + time);
      }
   }

   public static String[] splitElements(String list, boolean lower) {
      return split(lower && list != null ? list.toLowerCase(Locale.US) : list, ",", -1);
   }

   public static String[] split(String str, String delimiters, int limit) {
      if (str == null) {
         return new String[0];
      } else {
         Collection<String> elements = new ArrayList();
         int len = str.length();

         int end;
         for(int start = 0; start < len; start = end + 1) {
            --limit;

            for(end = limit == 0 ? len : start; end < len && delimiters.indexOf(str.charAt(end)) < 0; ++end) {
            }

            String element = str.substring(start, end).trim();
            if (element.length() > 0) {
               elements.add(element);
            }
         }

         return (String[])elements.toArray(new String[0]);
      }
   }

   public static <T> String join(String delim, Iterable<T> items) {
      StringBuilder sb = new StringBuilder();
      Iterator<T> it = items.iterator();

      while(it.hasNext()) {
         sb.append(it.next()).append(it.hasNext() ? delim : "");
      }

      return sb.toString();
   }

   public static String getParentPath(String path) {
      path = trimRight(path, '/');
      int slash = path.lastIndexOf(47);
      return slash < 0 ? null : path.substring(0, slash);
   }

   public static String trimRight(String s, char c) {
      int len = s.length() - 1;

      int end;
      for(end = len; end >= 0 && s.charAt(end) == c; --end) {
      }

      return end == len ? s : s.substring(0, end + 1);
   }

   public static String trimLeft(String s, char c) {
      int len = s.length();

      int start;
      for(start = 0; start < len && s.charAt(start) == c; ++start) {
      }

      return start == 0 ? s : s.substring(start);
   }

   public static String trimDuplicates(String s, char c) {
      int start = 0;

      while((start = s.indexOf(c, start) + 1) > 0) {
         int end;
         for(end = start; end < s.length() && s.charAt(end) == c; ++end) {
         }

         if (end > start) {
            s = s.substring(0, start) + s.substring(end);
         }
      }

      return s;
   }

   public static String toSizeApproxString(long size) {
      char[] units = new char[]{' ', 'K', 'M', 'G', 'T', 'P', 'E'};
      int u = 0;

      double s;
      for(s = (double)size; s >= 1000.0; s /= 1024.0) {
         ++u;
      }

      return String.format(s < 10.0 ? "%.1f%c" : "%.0f%c", s, units[u]);
   }

   public static String escapeHTML(String s) {
      int len = s.length();
      StringBuilder sb = new StringBuilder(len + 30);
      int start = 0;

      for(int i = 0; i < len; ++i) {
         String ref = null;
         switch (s.charAt(i)) {
            case '"':
               ref = "&quot;";
               break;
            case '&':
               ref = "&amp;";
               break;
            case '\'':
               ref = "&#39;";
               break;
            case '<':
               ref = "&lt;";
               break;
            case '>':
               ref = "&gt;";
         }

         if (ref != null) {
            sb.append(s.substring(start, i)).append(ref);
            start = i + 1;
         }
      }

      return start == 0 ? s : sb.append(s.substring(start)).toString();
   }

   public static byte[] getBytes(String... strings) {
      int n = 0;
      String[] var2 = strings;
      int var3 = strings.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         String s = var2[var4];
         n += s.length();
      }

      byte[] b = new byte[n];
      n = 0;
      String[] var10 = strings;
      var4 = strings.length;

      for(int var11 = 0; var11 < var4; ++var11) {
         String s = var10[var11];
         int i = 0;

         for(int len = s.length(); i < len; ++i) {
            b[n++] = (byte)s.charAt(i);
         }
      }

      return b;
   }

   public static void transfer(InputStream in, OutputStream out, long len) throws IOException {
      if (len != 0L && (out != null || len >= 0L || in.read() >= 0)) {
         int count;
         for(byte[] buf = new byte[4096]; len != 0L; len -= len > 0L ? (long)count : 0L) {
            count = len >= 0L && (long)buf.length >= len ? (int)len : buf.length;
            count = in.read(buf, 0, count);
            if (count < 0) {
               if (len > 0L) {
                  throw new IOException("unexpected end of stream");
               }
               break;
            }

            if (out != null) {
               out.write(buf, 0, count);
            }
         }

      }
   }

   public static String readToken(InputStream in, int delim, String enc, int maxLength) throws IOException {
      int len = 0;
      int count = 0;

      int b;
      byte[] buf;
      for(buf = null; (b = in.read()) != -1 && b != delim; buf[count++] = (byte)b) {
         if (count == len) {
            if (count == maxLength) {
               throw new IOException("token too large (" + count + ")");
            }

            len = len > 0 ? 2 * len : 256;
            len = maxLength < len ? maxLength : len;
            byte[] expanded = new byte[len];
            if (buf != null) {
               System.arraycopy(buf, 0, expanded, 0, count);
            }

            buf = expanded;
         }
      }

      if (b < 0 && delim != -1) {
         throw new EOFException("unexpected end of stream");
      } else {
         if (delim == 10 && count > 0 && buf[count - 1] == 13) {
            --count;
         }

         return count > 0 ? new String(buf, 0, count, enc) : "";
      }
   }

   public static String readLine(InputStream in) throws IOException {
      return readToken(in, 10, "UTF-8", MAX_HEADER_SIZE);
   }

   public static Headers readHeaders(InputStream in) throws IOException {
      Headers headers = new Headers();
      String prevLine = "";
      int count = 0;

      do {
         String line;
         if ((line = readLine(in)).length() <= 0) {
            return headers;
         }

         int start;
         for(start = 0; start < line.length() && Character.isWhitespace(line.charAt(start)); ++start) {
         }

         if (start > 0) {
            line = prevLine + ' ' + line.substring(start);
         }

         int separator = line.indexOf(58);
         if (separator < 0) {
            throw new IOException("invalid header: \"" + line + "\"");
         }

         String name = line.substring(0, separator);
         String value = line.substring(separator + 1).trim();
         Header replaced = headers.replace(name, value);
         if (replaced != null && start == 0) {
            value = replaced.getValue() + ", " + value;
            line = name + ": " + value;
            headers.replace(name, value);
         }

         prevLine = line;
         ++count;
      } while(count <= 100);

      throw new IOException("too many header lines");
   }

   public static boolean match(boolean strong, String[] etags, String etag) {
      if (etag != null && (!strong || !etag.startsWith("W/"))) {
         String[] var3 = etags;
         int var4 = etags.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String e = var3[var5];
            if (e.equals("*") || e.equals(etag) && (!strong || !e.startsWith("W/"))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int getConditionalStatus(Request req, long lastModified, String etag) {
      Headers headers = req.getHeaders();
      String header = headers.get("If-Match");
      if (header != null && !match(true, splitElements(header, false), etag)) {
         return 412;
      } else {
         Date date = headers.getDate("If-Unmodified-Since");
         if (date != null && lastModified > date.getTime()) {
            return 412;
         } else {
            int status = 200;
            boolean force = false;
            date = headers.getDate("If-Modified-Since");
            if (date != null && date.getTime() <= System.currentTimeMillis()) {
               if (lastModified > date.getTime()) {
                  force = true;
               } else {
                  status = 304;
               }
            }

            header = headers.get("If-None-Match");
            if (header != null) {
               if (match(false, splitElements(header, false), etag)) {
                  status = !req.getMethod().equals("GET") && !req.getMethod().equals("HEAD") ? 412 : 304;
               } else {
                  force = true;
               }
            }

            return force ? 200 : status;
         }
      }
   }

   public static int serveFile(File base, String context, Request req, Response resp) throws IOException {
      String relativePath = req.getPath().substring(context.length());
      File file = (new File(base, relativePath)).getCanonicalFile();
      if (file.exists() && !file.isHidden() && !file.getName().startsWith(".")) {
         if (file.canRead() && file.getPath().startsWith(base.getPath())) {
            if (file.isDirectory()) {
               if (relativePath.endsWith("/")) {
                  if (!req.getVirtualHost().isAllowGeneratedIndex()) {
                     return 403;
                  }

                  resp.send(200, createIndex(file, req.getPath()));
               } else {
                  resp.redirect(req.getBaseURL() + req.getPath() + "/", true);
               }
            } else {
               if (relativePath.endsWith("/")) {
                  return 404;
               }

               serveFileContent(file, req, resp);
            }

            return 0;
         } else {
            return 403;
         }
      } else {
         return 404;
      }
   }

   public static void serveFileContent(File file, Request req, Response resp) throws IOException {
      long len = file.length();
      long lastModified = file.lastModified();
      String etag = "W/\"" + lastModified + "\"";
      int status = 200;
      long[] range = req.getRange(len);
      if (range != null && len != 0L) {
         String ifRange = req.getHeaders().get("If-Range");
         if (ifRange == null) {
            if (range[0] >= len) {
               status = 416;
            } else {
               status = getConditionalStatus(req, lastModified, etag);
            }
         } else if (range[0] >= len) {
            range = null;
         } else if (!ifRange.startsWith("\"") && !ifRange.startsWith("W/")) {
            Date date = req.getHeaders().getDate("If-Range");
            if (date != null && lastModified > date.getTime()) {
               range = null;
            }
         } else if (!ifRange.equals(etag)) {
            range = null;
         }
      } else {
         status = getConditionalStatus(req, lastModified, etag);
      }

      Headers respHeaders = resp.getHeaders();
      switch (status) {
         case 200:
            resp.sendHeaders(200, len, lastModified, etag, getContentType(file.getName(), "application/octet-stream"), range);
            InputStream in = new FileInputStream(file);

            try {
               resp.sendBody(in, len, range);
               break;
            } finally {
               in.close();
            }
         case 304:
            respHeaders.add("ETag", etag);
            respHeaders.add("Vary", "Accept-Encoding");
            respHeaders.add("Last-Modified", formatDate(lastModified));
            resp.sendHeaders(304);
            break;
         case 412:
            resp.sendHeaders(412);
            break;
         case 416:
            respHeaders.add("Content-Range", "bytes */" + len);
            resp.sendHeaders(416);
            break;
         default:
            resp.sendHeaders(500);
      }

   }

   public static String createIndex(File dir, String path) {
      if (!path.endsWith("/")) {
         path = path + "/";
      }

      int w = 21;
      String[] var3 = dir.list();
      int var4 = var3.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         String name = var3[var5];
         if (name.length() > w) {
            w = name.length();
         }
      }

      w += 2;
      Formatter f = new Formatter(Locale.US);
      f.format("<!DOCTYPE html>%n<html><head><title>Index of %s</title></head>%n<body><h1>Index of %s</h1>%n<pre> Name%" + (w - 5) + "s Last modified      Size<hr>", path, path, "");
      if (path.length() > 1) {
         f.format(" <a href=\"%s/\">Parent Directory</a>%" + (w + 5) + "s-%n", getParentPath(path), "");
      }

      File[] var13 = dir.listFiles();
      var5 = var13.length;

      for(int var14 = 0; var14 < var5; ++var14) {
         File file = var13[var14];

         try {
            String name = file.getName() + (file.isDirectory() ? "/" : "");
            String size = file.isDirectory() ? "- " : toSizeApproxString(file.length());
            String link = (new URI((String)null, path + name, (String)null)).toASCIIString();
            if (!file.isHidden() && !name.startsWith(".")) {
               f.format(" <a href=\"%s\">%s</a>%-" + (w - name.length()) + "s&#8206;%td-%<tb-%<tY %<tR%6s%n", link, name, "", file.lastModified(), size);
            }
         } catch (URISyntaxException var11) {
         }
      }

      f.format("</pre></body></html>");
      return f.toString();
   }

   public static void main(String[] args) {
      try {
         if (args.length == 0) {
            System.err.printf("Usage: java [-options] %s <directory> [port]%nTo enable SSL: specify options -Djavax.net.ssl.keyStore, -Djavax.net.ssl.keyStorePassword, etc.%n", HTTPServer.class.getName());
            return;
         }

         File dir = new File(args[0]);
         if (!dir.canRead()) {
            throw new FileNotFoundException(dir.getAbsolutePath());
         }

         int port = args.length < 2 ? 80 : (int)parseULong(args[1], 10);
         Iterator var3 = Arrays.asList(new File("/etc/mime.types"), new File(dir, ".mime.types")).iterator();

         while(var3.hasNext()) {
            File f = (File)var3.next();
            if (f.exists()) {
               addContentTypes(new FileInputStream(f));
            }
         }

         HTTPServer server = new HTTPServer(port);
         if (System.getProperty("javax.net.ssl.keyStore") != null) {
            server.setServerSocketFactory(SSLServerSocketFactory.getDefault());
         }

         VirtualHost host = server.getVirtualHost((String)null);
         host.setAllowGeneratedIndex(true);
         host.addContext("/", new FileContextHandler(dir));
         host.addContext("/api/time", new ContextHandler() {
            public int serve(Request req, Response resp) throws IOException {
               long now = System.currentTimeMillis();
               resp.getHeaders().add("Content-Type", "text/plain");
               resp.send(200, String.format("%tF %<tT", now));
               return 0;
            }
         });
         server.start();
         System.out.println("HTTPServer is listening on port " + port);
      } catch (Exception var5) {
         System.err.println("error: " + var5);
      }

   }

   static {
      Arrays.fill(statuses, "Unknown Status");
      statuses[100] = "Continue";
      statuses[200] = "OK";
      statuses[204] = "No Content";
      statuses[206] = "Partial Content";
      statuses[301] = "Moved Permanently";
      statuses[302] = "Found";
      statuses[304] = "Not Modified";
      statuses[307] = "Temporary Redirect";
      statuses[400] = "Bad Request";
      statuses[401] = "Unauthorized";
      statuses[403] = "Forbidden";
      statuses[404] = "Not Found";
      statuses[405] = "Method Not Allowed";
      statuses[408] = "Request Timeout";
      statuses[412] = "Precondition Failed";
      statuses[413] = "Request Entity Too Large";
      statuses[414] = "Request-URI Too Large";
      statuses[416] = "Requested Range Not Satisfiable";
      statuses[417] = "Expectation Failed";
      statuses[500] = "Internal Server Error";
      statuses[501] = "Not Implemented";
      statuses[502] = "Bad Gateway";
      statuses[503] = "Service Unavailable";
      statuses[504] = "Gateway Time-out";
      contentTypes = new ConcurrentHashMap();
      addContentType("application/font-woff", "woff");
      addContentType("application/font-woff2", "woff2");
      addContentType("application/java-archive", "jar");
      addContentType("application/javascript", "js");
      addContentType("application/json", "json");
      addContentType("application/octet-stream", "exe");
      addContentType("application/pdf", "pdf");
      addContentType("application/x-7z-compressed", "7z");
      addContentType("application/x-compressed", "tgz");
      addContentType("application/x-gzip", "gz");
      addContentType("application/x-tar", "tar");
      addContentType("application/xhtml+xml", "xhtml");
      addContentType("application/zip", "zip");
      addContentType("audio/mpeg", "mp3");
      addContentType("image/gif", "gif");
      addContentType("image/jpeg", "jpg", "jpeg");
      addContentType("image/png", "png");
      addContentType("image/svg+xml", "svg");
      addContentType("image/x-icon", "ico");
      addContentType("text/css", "css");
      addContentType("text/csv", "csv");
      addContentType("text/html; charset=utf-8", "htm", "html");
      addContentType("text/plain", "txt", "text", "log");
      addContentType("text/xml", "xml");
      compressibleContentTypes = new String[]{"text/*", "*/javascript", "*icon", "*+xml", "*/json"};
   }

   protected class SocketHandlerThread extends Thread {
      public void run() {
         this.setName(this.getClass().getSimpleName() + "-" + HTTPServer.this.port);

         try {
            ServerSocket serv = HTTPServer.this.serv;

            while(serv != null && !serv.isClosed()) {
               final Socket sock = serv.accept();
               HTTPServer.this.executor.execute(new Runnable() {
                  public void run() {
                     try {
                        try {
                           sock.setSoTimeout(HTTPServer.this.socketTimeout);
                           sock.setTcpNoDelay(true);
                           HTTPServer.this.handleConnection(sock.getInputStream(), sock.getOutputStream(), sock);
                        } finally {
                           try {
                              if (!(sock instanceof SSLSocket)) {
                                 sock.shutdownOutput();
                                 HTTPServer.transfer(sock.getInputStream(), (OutputStream)null, -1L);
                              }
                           } finally {
                              sock.close();
                           }

                        }
                     } catch (IOException var18) {
                     }

                  }
               });
            }
         } catch (IOException var3) {
         }

      }
   }

   public class Response implements Closeable {
      protected OutputStream out;
      protected OutputStream encodedOut;
      protected Headers headers;
      protected boolean discardBody;
      protected int state;
      protected Request req;

      public Response(OutputStream out) {
         this.out = out;
         this.headers = new Headers();
      }

      public void setDiscardBody(boolean discardBody) {
         this.discardBody = discardBody;
      }

      public void setClientCapabilities(Request req) {
         this.req = req;
      }

      public Headers getHeaders() {
         return this.headers;
      }

      public OutputStream getOutputStream() {
         return this.out;
      }

      public boolean headersSent() {
         return this.state == 1;
      }

      public OutputStream getBody() throws IOException {
         if (this.encodedOut == null && !this.discardBody) {
            List<String> te = Arrays.asList(HTTPServer.splitElements(this.headers.get("Transfer-Encoding"), true));
            List<String> ce = Arrays.asList(HTTPServer.splitElements(this.headers.get("Content-Encoding"), true));
            this.encodedOut = new ResponseOutputStream(this.out);
            if (te.contains("chunked")) {
               this.encodedOut = new ChunkedOutputStream(this.encodedOut);
            }

            if (!ce.contains("gzip") && !te.contains("gzip")) {
               if (ce.contains("deflate") || te.contains("deflate")) {
                  this.encodedOut = new DeflaterOutputStream(this.encodedOut);
               }
            } else {
               this.encodedOut = new GZIPOutputStream(this.encodedOut, 4096);
            }

            return this.encodedOut;
         } else {
            return this.encodedOut;
         }
      }

      public void close() throws IOException {
         this.state = -1;
         if (this.encodedOut != null) {
            this.encodedOut.close();
         }

         this.out.flush();
      }

      public void sendHeaders(int status) throws IOException {
         if (this.headersSent()) {
            throw new IOException("headers were already sent");
         } else {
            if (!this.headers.contains("Date")) {
               this.headers.add("Date", HTTPServer.formatDate(System.currentTimeMillis()));
            }

            this.headers.add("Server", "JLHTTP/2.6");
            this.out.write(HTTPServer.getBytes("HTTP/1.1 ", Integer.toString(status), " ", HTTPServer.statuses[status]));
            this.out.write(HTTPServer.CRLF);
            this.headers.writeTo(this.out);
            this.state = 1;
         }
      }

      public void sendHeaders(int status, long length, long lastModified, String etag, String contentType, long[] range) throws IOException {
         if (range != null) {
            this.headers.add("Content-Range", "bytes " + range[0] + "-" + range[1] + "/" + (length >= 0L ? length : "*"));
            length = range[1] - range[0] + 1L;
            if (status == 200) {
               status = 206;
            }
         }

         String ct = this.headers.get("Content-Type");
         if (ct == null) {
            ct = contentType != null ? contentType : "application/octet-stream";
            this.headers.add("Content-Type", ct);
         } else if (contentType != null) {
            ct = contentType;
            this.headers.replace("Content-Type", contentType);
         }

         if (!this.headers.contains("Content-Length") && !this.headers.contains("Transfer-Encoding")) {
            boolean modern = this.req != null && this.req.getVersion().endsWith("1.1");
            String accepted = this.req == null ? null : this.req.getHeaders().get("Accept-Encoding");
            List<String> encodings = Arrays.asList(HTTPServer.splitElements(accepted, true));
            String compression = encodings.contains("gzip") ? "gzip" : (encodings.contains("deflate") ? "deflate" : null);
            if (compression != null && (length < 0L || length > 300L) && HTTPServer.isCompressible(ct) && modern) {
               this.headers.replace("Transfer-Encoding", "chunked");
               this.headers.replace("Content-Encoding", compression);
            } else if (length < 0L && modern) {
               this.headers.replace("Transfer-Encoding", "chunked");
            } else if (length >= 0L) {
               this.headers.replace("Content-Length", Long.toString(length));
            }
         }

         if (!this.headers.contains("Vary")) {
            this.headers.add("Vary", "Accept-Encoding");
         }

         if (lastModified > 0L && !this.headers.contains("Last-Modified")) {
            this.headers.add("Last-Modified", HTTPServer.formatDate(Math.min(lastModified, System.currentTimeMillis())));
         }

         if (etag != null && !this.headers.contains("ETag")) {
            this.headers.add("ETag", etag);
         }

         if (this.req != null && "close".equalsIgnoreCase(this.req.getHeaders().get("Connection")) && !this.headers.contains("Connection")) {
            this.headers.add("Connection", "close");
         }

         this.sendHeaders(status);
      }

      public void send(int status, String text) throws IOException {
         byte[] content = text.getBytes("UTF-8");
         this.sendHeaders(status, (long)content.length, -1L, "W/\"" + Integer.toHexString(text.hashCode()) + "\"", "text/html; charset=utf-8", (long[])null);
         OutputStream out = this.getBody();
         if (out != null) {
            out.write(content);
         }

      }

      public void sendError(int status, String text) throws IOException {
         this.send(status, String.format("<!DOCTYPE html>%n<html>%n<head><title>%d %s</title></head>%n<body><h1>%d %s</h1>%n<p>%s</p>%n</body></html>", status, HTTPServer.statuses[status], status, HTTPServer.statuses[status], HTTPServer.escapeHTML(text)));
      }

      public void sendError(int status) throws IOException {
         String text = status < 400 ? ":)" : "sorry it didn't work out :(";
         this.sendError(status, text);
      }

      public void sendBody(InputStream body, long length, long[] range) throws IOException {
         OutputStream out = this.getBody();
         if (out != null) {
            if (range != null) {
               long offset = range[0];

               long skip;
               for(length = range[1] - range[0] + 1L; offset > 0L; offset -= skip) {
                  skip = body.skip(offset);
                  if (skip == 0L) {
                     throw new IOException("can't skip to " + range[0]);
                  }
               }
            }

            HTTPServer.transfer(body, out, length);
         }

      }

      public void redirect(String url, boolean permanent) throws IOException {
         try {
            url = (new URI(url)).toASCIIString();
         } catch (URISyntaxException var4) {
            throw new IOException("malformed URL: " + url);
         }

         this.headers.add("Location", url);
         if (permanent) {
            this.sendError(301, "Permanently moved to " + url);
         } else {
            this.sendError(302, "Temporarily moved to " + url);
         }

      }
   }

   public class Request {
      protected String method;
      protected URI uri;
      protected URL baseURL;
      protected String version;
      protected Headers headers;
      protected InputStream body;
      protected Socket sock;
      protected Map<String, String> params;
      protected VirtualHost host;
      protected VirtualHost.ContextInfo context;
      private List<String[]> _paramsList;

      public Request(InputStream in, Socket sock) throws IOException {
         this.sock = sock;
         this.readRequestLine(in);
         this.headers = HTTPServer.readHeaders(in);
         String header = this.headers.get("Transfer-Encoding");
         if (header != null && !header.toLowerCase(Locale.US).equals("identity")) {
            if (Arrays.asList(HTTPServer.splitElements(header, true)).contains("chunked")) {
               this.body = new ChunkedInputStream(in, this.headers);
            } else {
               this.body = in;
            }
         } else {
            header = this.headers.get("Content-Length");
            long len = header == null ? 0L : HTTPServer.parseULong(header, 10);
            this.body = new LimitedInputStream(in, len, false);
         }

      }

      public String getMethod() {
         return this.method;
      }

      public URI getURI() {
         return this.uri;
      }

      public String getVersion() {
         return this.version;
      }

      public Headers getHeaders() {
         return this.headers;
      }

      public InputStream getBody() {
         return this.body;
      }

      public Socket getSocket() {
         return this.sock;
      }

      public String getPath() {
         return this.uri.getPath();
      }

      public void setPath(String path) {
         try {
            this.uri = new URI(this.uri.getScheme(), this.uri.getUserInfo(), this.uri.getHost(), this.uri.getPort(), HTTPServer.trimDuplicates(path, '/'), this.uri.getQuery(), this.uri.getFragment());
            this.context = null;
         } catch (URISyntaxException var3) {
            throw new IllegalArgumentException("error setting path", var3);
         }
      }

      public URL getBaseURL() {
         if (this.baseURL != null) {
            return this.baseURL;
         } else {
            String host = this.uri.getHost();
            if (host == null) {
               host = this.headers.get("Host");
               if (host == null) {
                  host = HTTPServer.detectLocalHostName();
               }
            }

            int pos = host.indexOf(58);
            host = pos < 0 ? host : host.substring(0, pos);

            try {
               return this.baseURL = new URL(HTTPServer.this.secure ? "https" : "http", host, HTTPServer.this.port, "");
            } catch (MalformedURLException var4) {
               return null;
            }
         }
      }

      public List<String[]> getParamsList() throws IOException {
         if (this._paramsList == null) {
            List<String[]> queryParams = HTTPServer.parseParamsList(this.uri.getRawQuery());
            List<String[]> bodyParams = Collections.emptyList();
            String ct = this.headers.get("Content-Type");
            if (ct != null && ct.toLowerCase(Locale.US).startsWith("application/x-www-form-urlencoded")) {
               bodyParams = HTTPServer.parseParamsList(HTTPServer.readToken(this.body, -1, "UTF-8", HTTPServer.MAX_BODY_SIZE));
            }

            this._paramsList = new ArrayList();
            if (!queryParams.isEmpty()) {
               this._paramsList.addAll(queryParams);
            }

            if (!bodyParams.isEmpty()) {
               this._paramsList.addAll(bodyParams);
            }
         }

         return this._paramsList;
      }

      public Map<String, String> getParams() throws IOException {
         if (this.params == null) {
            this.params = HTTPServer.toMap(this.getParamsList());
         }

         return this.params;
      }

      public long[] getRange(long length) {
         String header = this.headers.get("Range");
         return header != null && header.startsWith("bytes=") ? HTTPServer.parseRange(header.substring(6), length) : null;
      }

      protected void readRequestLine(InputStream in) throws IOException {
         while(true) {
            String line;
            try {
               line = HTTPServer.readLine(in);
               if (line.length() == 0) {
                  continue;
               }
            } catch (IOException var6) {
               throw new IOException("missing request line");
            }

            String[] tokens = HTTPServer.split(line, " ", -1);
            if (tokens.length != 3) {
               throw new IOException("invalid request line: \"" + line + "\"");
            }

            try {
               this.method = tokens[0];
               this.uri = new URI(tokens[1]);
               this.version = tokens[2];
               return;
            } catch (URISyntaxException var5) {
               throw new IOException("invalid URI: " + var5.getMessage());
            }
         }
      }

      public VirtualHost getVirtualHost() {
         return this.host != null ? this.host : ((this.host = HTTPServer.this.getVirtualHost(this.getBaseURL().getHost())) != null ? this.host : (this.host = HTTPServer.this.getVirtualHost((String)null)));
      }

      public VirtualHost.ContextInfo getContext() {
         return this.context != null ? this.context : (this.context = this.getVirtualHost().getContext(this.getPath()));
      }
   }

   public static class Headers implements Iterable<Header> {
      protected Header[] headers = new Header[12];
      protected int count;

      public int size() {
         return this.count;
      }

      public String get(String name) {
         for(int i = 0; i < this.count; ++i) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
               return this.headers[i].getValue();
            }
         }

         return null;
      }

      public Date getDate(String name) {
         try {
            String header = this.get(name);
            return header == null ? null : HTTPServer.parseDate(header);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }

      public boolean contains(String name) {
         return this.get(name) != null;
      }

      public void add(String name, String value) {
         Header header = new Header(name, value);
         if (this.count == this.headers.length) {
            Header[] expanded = new Header[2 * this.count];
            System.arraycopy(this.headers, 0, expanded, 0, this.count);
            this.headers = expanded;
         }

         this.headers[this.count++] = header;
      }

      public void addAll(Headers headers) {
         Iterator var2 = headers.iterator();

         while(var2.hasNext()) {
            Header header = (Header)var2.next();
            this.add(header.getName(), header.getValue());
         }

      }

      public Header replace(String name, String value) {
         for(int i = 0; i < this.count; ++i) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
               Header prev = this.headers[i];
               this.headers[i] = new Header(name, value);
               return prev;
            }
         }

         this.add(name, value);
         return null;
      }

      public void remove(String name) {
         int j = 0;

         for(int i = 0; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) {
               this.headers[j++] = this.headers[i];
            }
         }

         while(this.count > j) {
            this.headers[--this.count] = null;
         }

      }

      public void writeTo(OutputStream out) throws IOException {
         for(int i = 0; i < this.count; ++i) {
            out.write(HTTPServer.getBytes(this.headers[i].getName(), ": ", this.headers[i].getValue()));
            out.write(HTTPServer.CRLF);
         }

         out.write(HTTPServer.CRLF);
      }

      public Map<String, String> getParams(String name) {
         Map<String, String> params = new LinkedHashMap();
         String[] var3 = HTTPServer.split(this.get(name), ";", -1);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String param = var3[var5];
            String[] pair = HTTPServer.split(param, "=", 2);
            String val = pair.length == 1 ? "" : HTTPServer.trimLeft(HTTPServer.trimRight(pair[1], '"'), '"');
            params.put(pair[0], val);
         }

         return params;
      }

      public Iterator<Header> iterator() {
         return Arrays.asList(this.headers).subList(0, this.count).iterator();
      }
   }

   public static class Header {
      protected final String name;
      protected final String value;

      public Header(String name, String value) {
         this.name = name.trim();
         this.value = value.trim();
         if (this.name.length() == 0) {
            throw new IllegalArgumentException("name cannot be empty");
         }
      }

      public String getName() {
         return this.name;
      }

      public String getValue() {
         return this.value;
      }
   }

   public static class MethodContextHandler implements ContextHandler {
      protected final Method m;
      protected final Object obj;

      public MethodContextHandler(Method m, Object obj) throws IllegalArgumentException {
         this.m = m;
         this.obj = obj;
         Class<?>[] params = m.getParameterTypes();
         if (params.length != 2 || !Request.class.isAssignableFrom(params[0]) || !Response.class.isAssignableFrom(params[1]) || !Integer.TYPE.isAssignableFrom(m.getReturnType())) {
            throw new IllegalArgumentException("invalid method signature: " + m);
         }
      }

      public int serve(Request req, Response resp) throws IOException {
         try {
            return (Integer)this.m.invoke(this.obj, req, resp);
         } catch (InvocationTargetException var4) {
            throw new IOException("error: " + var4.getCause().getMessage());
         } catch (Exception var5) {
            throw new IOException("error: " + var5);
         }
      }
   }

   public static class FileContextHandler implements ContextHandler {
      protected final File base;

      public FileContextHandler(File dir) throws IOException {
         this.base = dir.getCanonicalFile();
      }

      public int serve(Request req, Response resp) throws IOException {
         return HTTPServer.serveFile(this.base, req.getContext().getPath(), req, resp);
      }
   }

   public interface ContextHandler {
      int serve(Request req, Response resp) throws IOException;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.METHOD})
   public @interface Context {
      String value();

      String[] methods() default {"GET"};
   }

   public static class VirtualHost {
      protected final String name;
      protected final Set<String> aliases = new CopyOnWriteArraySet();
      protected volatile String directoryIndex = "index.html";
      protected volatile boolean allowGeneratedIndex;
      protected final Set<String> methods = new CopyOnWriteArraySet();
      protected final ContextInfo emptyContext = new ContextInfo((String)null);
      protected final ConcurrentMap<String, ContextInfo> contexts = new ConcurrentHashMap();

      public VirtualHost(String name) {
         this.name = name;
         this.contexts.put("*", new ContextInfo((String)null));
      }

      public String getName() {
         return this.name;
      }

      public void addAlias(String alias) {
         this.aliases.add(alias);
      }

      public Set<String> getAliases() {
         return Collections.unmodifiableSet(this.aliases);
      }

      public void setDirectoryIndex(String directoryIndex) {
         this.directoryIndex = directoryIndex;
      }

      public String getDirectoryIndex() {
         return this.directoryIndex;
      }

      public void setAllowGeneratedIndex(boolean allowed) {
         this.allowGeneratedIndex = allowed;
      }

      public boolean isAllowGeneratedIndex() {
         return this.allowGeneratedIndex;
      }

      public Set<String> getMethods() {
         return this.methods;
      }

      public ContextInfo getContext(String path) {
         for(path = HTTPServer.trimRight(path, '/'); path != null; path = HTTPServer.getParentPath(path)) {
            ContextInfo info = (ContextInfo)this.contexts.get(path);
            if (info != null) {
               return info;
            }
         }

         return this.emptyContext;
      }

      public void addContext(String path, ContextHandler handler, String... methods) {
         if (path != null && (path.startsWith("/") || path.equals("*"))) {
            path = HTTPServer.trimRight(path, '/');
            ContextInfo info = new ContextInfo(path);
            ContextInfo existing = (ContextInfo)this.contexts.putIfAbsent(path, info);
            info = existing != null ? existing : info;
            info.addHandler(handler, methods);
         } else {
            throw new IllegalArgumentException("invalid path: " + path);
         }
      }

      public void addContexts(Object o) throws IllegalArgumentException {
         for(Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
            Method[] var3 = c.getDeclaredMethods();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Method m = var3[var5];
               Context context = (Context)m.getAnnotation(Context.class);
               if (context != null) {
                  m.setAccessible(true);
                  ContextHandler handler = new MethodContextHandler(m, o);
                  this.addContext(context.value(), handler, context.methods());
               }
            }
         }

      }

      public class ContextInfo {
         protected final String path;
         protected final Map<String, ContextHandler> handlers = new ConcurrentHashMap(2);

         public ContextInfo(String path) {
            this.path = path;
         }

         public String getPath() {
            return this.path;
         }

         public Map<String, ContextHandler> getHandlers() {
            return this.handlers;
         }

         public void addHandler(ContextHandler handler, String... methods) {
            if (methods.length == 0) {
               methods = new String[]{"GET"};
            }

            String[] var3 = methods;
            int var4 = methods.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String method = var3[var5];
               this.handlers.put(method, handler);
               VirtualHost.this.methods.add(method);
            }

         }
      }
   }

   public static class MultipartIterator implements Iterator<Part> {
      protected final MultipartInputStream in;
      protected boolean next;

      public MultipartIterator(Request req) throws IOException {
         Map<String, String> ct = req.getHeaders().getParams("Content-Type");
         if (!ct.containsKey("multipart/form-data")) {
            throw new IllegalArgumentException("Content-Type is not multipart/form-data");
         } else {
            String boundary = (String)ct.get("boundary");
            if (boundary == null) {
               throw new IllegalArgumentException("Content-Type is missing boundary");
            } else {
               this.in = new MultipartInputStream(req.getBody(), HTTPServer.getBytes(boundary));
            }
         }
      }

      public boolean hasNext() {
         try {
            return this.next || (this.next = this.in.nextPart());
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }

      public Part next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.next = false;
            Part p = new Part();

            try {
               p.headers = HTTPServer.readHeaders(this.in);
            } catch (IOException var3) {
               throw new RuntimeException(var3);
            }

            Map<String, String> cd = p.headers.getParams("Content-Disposition");
            p.name = (String)cd.get("name");
            p.filename = (String)cd.get("filename");
            p.body = this.in;
            return p;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public static class Part {
         public String name;
         public String filename;
         public Headers headers;
         public InputStream body;

         public String getName() {
            return this.name;
         }

         public String getFilename() {
            return this.filename;
         }

         public Headers getHeaders() {
            return this.headers;
         }

         public InputStream getBody() {
            return this.body;
         }

         public String getString() throws IOException {
            String charset = (String)this.headers.getParams("Content-Type").get("charset");
            return HTTPServer.readToken(this.body, -1, charset == null ? "UTF-8" : charset, HTTPServer.MAX_HEADER_SIZE);
         }
      }
   }

   public static class MultipartInputStream extends FilterInputStream {
      protected final byte[] boundary;
      protected final byte[] buf = new byte[4096];
      protected int head;
      protected int tail;
      protected int end;
      protected int len;
      protected int state;

      protected MultipartInputStream(InputStream in, byte[] boundary) {
         super(in);
         int len = boundary.length;
         if (len != 0 && len <= 70) {
            this.boundary = new byte[len + 4];
            System.arraycopy(HTTPServer.CRLF, 0, this.boundary, 0, 2);
            this.boundary[2] = this.boundary[3] = 45;
            System.arraycopy(boundary, 0, this.boundary, 4, len);
         } else {
            throw new IllegalArgumentException("invalid boundary length");
         }
      }

      public int read() throws IOException {
         return !this.fill() ? -1 : this.buf[this.head++] & 255;
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (!this.fill()) {
            return -1;
         } else {
            len = Math.min(this.tail - this.head, len);
            System.arraycopy(this.buf, this.head, b, off, len);
            this.head += len;
            return len;
         }
      }

      public long skip(long len) throws IOException {
         if (len > 0L && this.fill()) {
            len = Math.min((long)(this.tail - this.head), len);
            this.head = (int)((long)this.head + len);
            return len;
         } else {
            return 0L;
         }
      }

      public int available() throws IOException {
         return this.tail - this.head;
      }

      public boolean markSupported() {
         return false;
      }

      public boolean nextPart() throws IOException {
         while(this.skip((long)this.buf.length) != 0L) {
         }

         this.head = this.tail += this.len;
         this.state |= 1;
         if (this.state >= 8) {
            this.state |= 16;
            return false;
         } else {
            this.findBoundary();
            return true;
         }
      }

      protected boolean fill() throws IOException {
         if (this.head != this.tail) {
            return true;
         } else {
            if (this.tail > this.buf.length - 256) {
               System.arraycopy(this.buf, this.tail, this.buf, 0, this.end -= this.tail);
               this.head = this.tail = 0;
            }

            int read;
            do {
               read = super.read(this.buf, this.end, this.buf.length - this.end);
               if (read < 0) {
                  this.state |= 4;
               } else {
                  this.end += read;
               }

               this.findBoundary();
            } while(read > 0 && this.tail == this.head && this.len == 0);

            if (this.tail != 0) {
               this.state |= 1;
            }

            if (this.state < 8 && this.len > 0) {
               this.state |= 2;
            }

            if ((this.state & 6) == 4 || this.len == 0 && ((this.state & 252) == 4 || read == 0 && this.tail == this.head)) {
               throw new IOException("missing boundary");
            } else {
               if (this.state >= 16) {
                  this.tail = this.end;
               }

               return this.tail > this.head;
            }
         }
      }

      protected void findBoundary() throws IOException {
         this.len = 0;
         int off = this.tail - ((this.state & 1) == 0 && this.buf[0] == 45 ? 2 : 0);

         for(int end = this.end; this.tail < end; off = this.tail) {
            int j;
            for(j = this.tail; j < end && j - off < this.boundary.length && this.buf[j] == this.boundary[j - off]; ++j) {
            }

            if (j + 1 >= end) {
               return;
            }

            if (j - off == this.boundary.length) {
               if (this.buf[j] == 45 && this.buf[j + 1] == 45) {
                  j += 2;
                  this.state |= 8;
               }

               while(j < end && (this.buf[j] == 32 || this.buf[j] == 9)) {
                  ++j;
               }

               if (j + 1 < end && this.buf[j] == 13 && this.buf[j + 1] == 10) {
                  this.len = j - this.tail + 2;
               } else {
                  if (j + 1 < end || (this.state & 4) != 0 && j + 1 == end) {
                     throw new IOException("boundary must end with CRLF");
                  }

                  if ((this.state & 4) != 0) {
                     this.len = j - this.tail;
                  }
               }

               return;
            }

            ++this.tail;
         }

      }
   }

   public static class ResponseOutputStream extends FilterOutputStream {
      public ResponseOutputStream(OutputStream out) {
         super(out);
      }

      public void close() {
      }

      public void write(byte[] b, int off, int len) throws IOException {
         this.out.write(b, off, len);
      }
   }

   public static class ChunkedOutputStream extends FilterOutputStream {
      protected int state;

      public ChunkedOutputStream(OutputStream out) {
         super(out);
         if (out == null) {
            throw new NullPointerException("output stream is null");
         }
      }

      protected void initChunk(long size) throws IOException {
         if (size < 0L) {
            throw new IllegalArgumentException("invalid size: " + size);
         } else {
            if (this.state > 0) {
               this.out.write(HTTPServer.CRLF);
            } else {
               if (this.state != 0) {
                  throw new IOException("chunked stream has already ended");
               }

               this.state = 1;
            }

            this.out.write(HTTPServer.getBytes(Long.toHexString(size)));
            this.out.write(HTTPServer.CRLF);
         }
      }

      public void writeTrailingChunk(Headers headers) throws IOException {
         this.initChunk(0L);
         if (headers == null) {
            this.out.write(HTTPServer.CRLF);
         } else {
            headers.writeTo(this.out);
         }

         this.state = -1;
      }

      public void write(int b) throws IOException {
         this.write(new byte[]{(byte)b}, 0, 1);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         if (len > 0) {
            this.initChunk((long)len);
         }

         this.out.write(b, off, len);
      }

      public void close() throws IOException {
         if (this.state > -1) {
            this.writeTrailingChunk((Headers)null);
         }

         super.close();
      }
   }

   public static class ChunkedInputStream extends LimitedInputStream {
      protected Headers headers;
      protected boolean initialized;

      public ChunkedInputStream(InputStream in, Headers headers) {
         super(in, 0L, true);
         this.headers = headers;
      }

      public int read() throws IOException {
         return this.limit <= 0L && this.initChunk() < 0L ? -1 : super.read();
      }

      public int read(byte[] b, int off, int len) throws IOException {
         return this.limit <= 0L && this.initChunk() < 0L ? -1 : super.read(b, off, len);
      }

      protected long initChunk() throws IOException {
         if (this.limit == 0L) {
            if (this.initialized && HTTPServer.readLine(this.in).length() > 0) {
               throw new IOException("chunk data must end with CRLF");
            }

            this.initialized = true;
            this.limit = parseChunkSize(HTTPServer.readLine(this.in));
            if (this.limit == 0L) {
               this.limit = -1L;
               Headers trailingHeaders = HTTPServer.readHeaders(this.in);
               if (this.headers != null) {
                  this.headers.addAll(trailingHeaders);
               }
            }
         }

         return this.limit;
      }

      protected static long parseChunkSize(String line) throws IllegalArgumentException {
         int pos = line.indexOf(59);
         line = pos < 0 ? line : line.substring(0, pos);

         try {
            return HTTPServer.parseULong(line, 16);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("invalid chunk size line: \"" + line + "\"");
         }
      }
   }

   public static class LimitedInputStream extends FilterInputStream {
      protected long limit;
      protected boolean prematureEndException;

      public LimitedInputStream(InputStream in, long limit, boolean prematureEndException) {
         super(in);
         if (in == null) {
            throw new NullPointerException("input stream is null");
         } else {
            this.limit = limit < 0L ? 0L : limit;
            this.prematureEndException = prematureEndException;
         }
      }

      public int read() throws IOException {
         int res = this.limit == 0L ? -1 : this.in.read();
         if (res < 0 && this.limit > 0L && this.prematureEndException) {
            throw new IOException("unexpected end of stream");
         } else {
            this.limit = res < 0 ? 0L : this.limit - 1L;
            return res;
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         int res = this.limit == 0L ? -1 : this.in.read(b, off, (long)len > this.limit ? (int)this.limit : len);
         if (res < 0 && this.limit > 0L && this.prematureEndException) {
            throw new IOException("unexpected end of stream");
         } else {
            this.limit = res < 0 ? 0L : this.limit - (long)res;
            return res;
         }
      }

      public long skip(long len) throws IOException {
         long res = this.in.skip(len > this.limit ? this.limit : len);
         this.limit -= res;
         return res;
      }

      public int available() throws IOException {
         int res = this.in.available();
         return (long)res > this.limit ? (int)this.limit : res;
      }

      public boolean markSupported() {
         return false;
      }

      public void close() {
         this.limit = 0L;
      }
   }
}
