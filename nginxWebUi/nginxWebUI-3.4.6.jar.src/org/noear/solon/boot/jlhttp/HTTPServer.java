/*      */ package org.noear.solon.boot.jlhttp;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.EOFException;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.FilterOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.annotation.ElementType;
/*      */ import java.lang.annotation.Retention;
/*      */ import java.lang.annotation.RetentionPolicy;
/*      */ import java.lang.annotation.Target;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Formatter;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.CopyOnWriteArraySet;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.Executors;
/*      */ import java.util.zip.DeflaterOutputStream;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import javax.net.ServerSocketFactory;
/*      */ import javax.net.ssl.SSLServerSocketFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HTTPServer
/*      */ {
/*  127 */   protected static int MAX_BODY_SIZE = 2097152;
/*  128 */   protected static int MAX_HEADER_SIZE = 8192;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  135 */   public static final String[] DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss z", "EEEE, dd-MMM-yy HH:mm:ss z", "EEE MMM d HH:mm:ss yyyy" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  142 */   protected static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*      */ 
/*      */ 
/*      */   
/*  146 */   protected static final char[] DAYS = "Sun Mon Tue Wed Thu Fri Sat".toCharArray();
/*  147 */   protected static final char[] MONTHS = "Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec".toCharArray();
/*      */ 
/*      */   
/*  150 */   public static final byte[] CRLF = new byte[] { 13, 10 };
/*      */ 
/*      */   
/*  153 */   protected static final String[] statuses = new String[600];
/*      */ 
/*      */   
/*      */   static {
/*  157 */     Arrays.fill((Object[])statuses, "Unknown Status");
/*  158 */     statuses[100] = "Continue";
/*  159 */     statuses[200] = "OK";
/*  160 */     statuses[204] = "No Content";
/*  161 */     statuses[206] = "Partial Content";
/*  162 */     statuses[301] = "Moved Permanently";
/*  163 */     statuses[302] = "Found";
/*  164 */     statuses[304] = "Not Modified";
/*  165 */     statuses[307] = "Temporary Redirect";
/*  166 */     statuses[400] = "Bad Request";
/*  167 */     statuses[401] = "Unauthorized";
/*  168 */     statuses[403] = "Forbidden";
/*  169 */     statuses[404] = "Not Found";
/*  170 */     statuses[405] = "Method Not Allowed";
/*  171 */     statuses[408] = "Request Timeout";
/*  172 */     statuses[412] = "Precondition Failed";
/*  173 */     statuses[413] = "Request Entity Too Large";
/*  174 */     statuses[414] = "Request-URI Too Large";
/*  175 */     statuses[416] = "Requested Range Not Satisfiable";
/*  176 */     statuses[417] = "Expectation Failed";
/*  177 */     statuses[500] = "Internal Server Error";
/*  178 */     statuses[501] = "Not Implemented";
/*  179 */     statuses[502] = "Bad Gateway";
/*  180 */     statuses[503] = "Service Unavailable";
/*  181 */     statuses[504] = "Gateway Time-out";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  188 */   protected static final Map<String, String> contentTypes = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*  194 */     addContentType("application/font-woff", new String[] { "woff" });
/*  195 */     addContentType("application/font-woff2", new String[] { "woff2" });
/*  196 */     addContentType("application/java-archive", new String[] { "jar" });
/*  197 */     addContentType("application/javascript", new String[] { "js" });
/*  198 */     addContentType("application/json", new String[] { "json" });
/*  199 */     addContentType("application/octet-stream", new String[] { "exe" });
/*  200 */     addContentType("application/pdf", new String[] { "pdf" });
/*  201 */     addContentType("application/x-7z-compressed", new String[] { "7z" });
/*  202 */     addContentType("application/x-compressed", new String[] { "tgz" });
/*  203 */     addContentType("application/x-gzip", new String[] { "gz" });
/*  204 */     addContentType("application/x-tar", new String[] { "tar" });
/*  205 */     addContentType("application/xhtml+xml", new String[] { "xhtml" });
/*  206 */     addContentType("application/zip", new String[] { "zip" });
/*  207 */     addContentType("audio/mpeg", new String[] { "mp3" });
/*  208 */     addContentType("image/gif", new String[] { "gif" });
/*  209 */     addContentType("image/jpeg", new String[] { "jpg", "jpeg" });
/*  210 */     addContentType("image/png", new String[] { "png" });
/*  211 */     addContentType("image/svg+xml", new String[] { "svg" });
/*  212 */     addContentType("image/x-icon", new String[] { "ico" });
/*  213 */     addContentType("text/css", new String[] { "css" });
/*  214 */     addContentType("text/csv", new String[] { "csv" });
/*  215 */     addContentType("text/html; charset=utf-8", new String[] { "htm", "html" });
/*  216 */     addContentType("text/plain", new String[] { "txt", "text", "log" });
/*  217 */     addContentType("text/xml", new String[] { "xml" });
/*      */   }
/*      */ 
/*      */   
/*  221 */   protected static String[] compressibleContentTypes = new String[] { "text/*", "*/javascript", "*icon", "*+xml", "*/json" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected volatile int port;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected volatile String host;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class LimitedInputStream
/*      */     extends FilterInputStream
/*      */   {
/*      */     protected long limit;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean prematureEndException;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public LimitedInputStream(InputStream in, long limit, boolean prematureEndException) {
/*  251 */       super(in);
/*  252 */       if (in == null)
/*  253 */         throw new NullPointerException("input stream is null"); 
/*  254 */       this.limit = (limit < 0L) ? 0L : limit;
/*  255 */       this.prematureEndException = prematureEndException;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/*  260 */       int res = (this.limit == 0L) ? -1 : this.in.read();
/*  261 */       if (res < 0 && this.limit > 0L && this.prematureEndException)
/*  262 */         throw new IOException("unexpected end of stream"); 
/*  263 */       this.limit = (res < 0) ? 0L : (this.limit - 1L);
/*  264 */       return res;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/*  269 */       int res = (this.limit == 0L) ? -1 : this.in.read(b, off, (len > this.limit) ? (int)this.limit : len);
/*  270 */       if (res < 0 && this.limit > 0L && this.prematureEndException)
/*  271 */         throw new IOException("unexpected end of stream"); 
/*  272 */       this.limit = (res < 0) ? 0L : (this.limit - res);
/*  273 */       return res;
/*      */     }
/*      */ 
/*      */     
/*      */     public long skip(long len) throws IOException {
/*  278 */       long res = this.in.skip((len > this.limit) ? this.limit : len);
/*  279 */       this.limit -= res;
/*  280 */       return res;
/*      */     }
/*      */ 
/*      */     
/*      */     public int available() throws IOException {
/*  285 */       int res = this.in.available();
/*  286 */       return (res > this.limit) ? (int)this.limit : res;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean markSupported() {
/*  291 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() {
/*  296 */       this.limit = 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ChunkedInputStream
/*      */     extends LimitedInputStream
/*      */   {
/*      */     protected HTTPServer.Headers headers;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean initialized;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ChunkedInputStream(InputStream in, HTTPServer.Headers headers) {
/*  320 */       super(in, 0L, true);
/*  321 */       this.headers = headers;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/*  326 */       return (this.limit <= 0L && initChunk() < 0L) ? -1 : super.read();
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/*  331 */       return (this.limit <= 0L && initChunk() < 0L) ? -1 : super.read(b, off, len);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected long initChunk() throws IOException {
/*  343 */       if (this.limit == 0L) {
/*      */         
/*  345 */         if (this.initialized && HTTPServer.readLine(this.in).length() > 0)
/*  346 */           throw new IOException("chunk data must end with CRLF"); 
/*  347 */         this.initialized = true;
/*  348 */         this.limit = parseChunkSize(HTTPServer.readLine(this.in));
/*  349 */         if (this.limit == 0L) {
/*  350 */           this.limit = -1L;
/*      */           
/*  352 */           HTTPServer.Headers trailingHeaders = HTTPServer.readHeaders(this.in);
/*  353 */           if (this.headers != null)
/*  354 */             this.headers.addAll(trailingHeaders); 
/*      */         } 
/*      */       } 
/*  357 */       return this.limit;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected static long parseChunkSize(String line) throws IllegalArgumentException {
/*  368 */       int pos = line.indexOf(';');
/*  369 */       line = (pos < 0) ? line : line.substring(0, pos);
/*      */       try {
/*  371 */         return HTTPServer.parseULong(line, 16);
/*  372 */       } catch (NumberFormatException nfe) {
/*  373 */         throw new IllegalArgumentException("invalid chunk size line: \"" + line + "\"");
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ChunkedOutputStream
/*      */     extends FilterOutputStream
/*      */   {
/*      */     protected int state;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ChunkedOutputStream(OutputStream out) {
/*  401 */       super(out);
/*  402 */       if (out == null) {
/*  403 */         throw new NullPointerException("output stream is null");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void initChunk(long size) throws IOException {
/*  415 */       if (size < 0L)
/*  416 */         throw new IllegalArgumentException("invalid size: " + size); 
/*  417 */       if (this.state > 0) {
/*  418 */         this.out.write(HTTPServer.CRLF);
/*  419 */       } else if (this.state == 0) {
/*  420 */         this.state = 1;
/*      */       } else {
/*  422 */         throw new IOException("chunked stream has already ended");
/*  423 */       }  this.out.write(HTTPServer.getBytes(new String[] { Long.toHexString(size) }));
/*  424 */       this.out.write(HTTPServer.CRLF);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTrailingChunk(HTTPServer.Headers headers) throws IOException {
/*  434 */       initChunk(0L);
/*  435 */       if (headers == null) {
/*  436 */         this.out.write(HTTPServer.CRLF);
/*      */       } else {
/*  438 */         headers.writeTo(this.out);
/*  439 */       }  this.state = -1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(int b) throws IOException {
/*  451 */       write(new byte[] { (byte)b }, 0, 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void write(byte[] b, int off, int len) throws IOException {
/*  467 */       if (len > 0)
/*  468 */         initChunk(len); 
/*  469 */       this.out.write(b, off, len);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/*  479 */       if (this.state > -1)
/*  480 */         writeTrailingChunk(null); 
/*  481 */       super.close();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ResponseOutputStream
/*      */     extends FilterOutputStream
/*      */   {
/*      */     public ResponseOutputStream(OutputStream out) {
/*  497 */       super(out);
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() {}
/*      */     
/*      */     public void write(byte[] b, int off, int len) throws IOException {
/*  504 */       this.out.write(b, off, len);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class MultipartInputStream
/*      */     extends FilterInputStream
/*      */   {
/*      */     protected final byte[] boundary;
/*      */ 
/*      */ 
/*      */     
/*  519 */     protected final byte[] buf = new byte[4096];
/*      */ 
/*      */     
/*      */     protected int head;
/*      */ 
/*      */     
/*      */     protected int tail;
/*      */     
/*      */     protected int end;
/*      */     
/*      */     protected int len;
/*      */     
/*      */     protected int state;
/*      */ 
/*      */     
/*      */     protected MultipartInputStream(InputStream in, byte[] boundary) {
/*  535 */       super(in);
/*  536 */       int len = boundary.length;
/*  537 */       if (len == 0 || len > 70)
/*  538 */         throw new IllegalArgumentException("invalid boundary length"); 
/*  539 */       this.boundary = new byte[len + 4];
/*  540 */       System.arraycopy(HTTPServer.CRLF, 0, this.boundary, 0, 2);
/*  541 */       this.boundary[3] = 45; this.boundary[2] = 45;
/*  542 */       System.arraycopy(boundary, 0, this.boundary, 4, len);
/*      */     }
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/*  547 */       if (!fill())
/*  548 */         return -1; 
/*  549 */       return this.buf[this.head++] & 0xFF;
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/*  554 */       if (!fill())
/*  555 */         return -1; 
/*  556 */       len = Math.min(this.tail - this.head, len);
/*  557 */       System.arraycopy(this.buf, this.head, b, off, len);
/*  558 */       this.head += len;
/*  559 */       return len;
/*      */     }
/*      */ 
/*      */     
/*      */     public long skip(long len) throws IOException {
/*  564 */       if (len <= 0L || !fill())
/*  565 */         return 0L; 
/*  566 */       len = Math.min((this.tail - this.head), len);
/*  567 */       this.head = (int)(this.head + len);
/*  568 */       return len;
/*      */     }
/*      */ 
/*      */     
/*      */     public int available() throws IOException {
/*  573 */       return this.tail - this.head;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean markSupported() {
/*  578 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean nextPart() throws IOException {
/*  590 */       while (skip(this.buf.length) != 0L);
/*  591 */       this.head = this.tail += this.len;
/*  592 */       this.state |= 0x1;
/*  593 */       if (this.state >= 8) {
/*  594 */         this.state |= 0x10;
/*  595 */         return false;
/*      */       } 
/*  597 */       findBoundary();
/*  598 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean fill() throws IOException {
/*      */       int read;
/*  610 */       if (this.head != this.tail) {
/*  611 */         return true;
/*      */       }
/*  613 */       if (this.tail > this.buf.length - 256) {
/*  614 */         System.arraycopy(this.buf, this.tail, this.buf, 0, this.end -= this.tail);
/*  615 */         this.head = this.tail = 0;
/*      */       } 
/*      */ 
/*      */       
/*      */       do {
/*  620 */         read = super.read(this.buf, this.end, this.buf.length - this.end);
/*  621 */         if (read < 0) {
/*  622 */           this.state |= 0x4;
/*      */         } else {
/*  624 */           this.end += read;
/*  625 */         }  findBoundary();
/*      */       
/*      */       }
/*  628 */       while (read > 0 && this.tail == this.head && this.len == 0);
/*      */       
/*  630 */       if (this.tail != 0)
/*  631 */         this.state |= 0x1; 
/*  632 */       if (this.state < 8 && this.len > 0)
/*  633 */         this.state |= 0x2; 
/*  634 */       if ((this.state & 0x6) == 4 || (this.len == 0 && ((this.state & 0xFC) == 4 || (read == 0 && this.tail == this.head))))
/*      */       {
/*      */         
/*  637 */         throw new IOException("missing boundary"); } 
/*  638 */       if (this.state >= 16)
/*  639 */         this.tail = this.end; 
/*  640 */       return (this.tail > this.head);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void findBoundary() throws IOException {
/*  651 */       this.len = 0;
/*  652 */       int off = this.tail - (((this.state & 0x1) != 0 || this.buf[0] != 45) ? 0 : 2);
/*  653 */       for (int end = this.end; this.tail < end; off = ++this.tail) {
/*  654 */         int j = this.tail;
/*      */         
/*  656 */         while (j < end && j - off < this.boundary.length && this.buf[j] == this.boundary[j - off]) {
/*  657 */           j++;
/*      */         }
/*  659 */         if (j + 1 >= end) {
/*      */           return;
/*      */         }
/*  662 */         if (j - off == this.boundary.length) {
/*      */           
/*  664 */           if (this.buf[j] == 45 && this.buf[j + 1] == 45) {
/*  665 */             j += 2;
/*  666 */             this.state |= 0x8;
/*      */           } 
/*      */           
/*  669 */           while (j < end && (this.buf[j] == 32 || this.buf[j] == 9)) {
/*  670 */             j++;
/*      */           }
/*  672 */           if (j + 1 < end && this.buf[j] == 13 && this.buf[j + 1] == 10)
/*  673 */           { this.len = j - this.tail + 2; }
/*  674 */           else { if (j + 1 < end || ((this.state & 0x4) != 0 && j + 1 == end))
/*  675 */               throw new IOException("boundary must end with CRLF"); 
/*  676 */             if ((this.state & 0x4) != 0) {
/*  677 */               this.len = j - this.tail;
/*      */             } }
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class MultipartIterator
/*      */     implements Iterator<MultipartIterator.Part>
/*      */   {
/*      */     protected final HTTPServer.MultipartInputStream in;
/*      */ 
/*      */ 
/*      */     
/*      */     protected boolean next;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static class Part
/*      */     {
/*      */       public String name;
/*      */ 
/*      */ 
/*      */       
/*      */       public String filename;
/*      */ 
/*      */       
/*      */       public HTTPServer.Headers headers;
/*      */ 
/*      */       
/*      */       public InputStream body;
/*      */ 
/*      */ 
/*      */       
/*      */       public String getName() {
/*  718 */         return this.name;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getFilename() {
/*  725 */         return this.filename;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public HTTPServer.Headers getHeaders() {
/*  732 */         return this.headers;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public InputStream getBody() {
/*  739 */         return this.body;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getString() throws IOException {
/*  749 */         String charset = this.headers.getParams("Content-Type").get("charset");
/*  750 */         return HTTPServer.readToken(this.body, -1, (charset == null) ? "UTF-8" : charset, HTTPServer.MAX_HEADER_SIZE);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public MultipartIterator(HTTPServer.Request req) throws IOException {
/*  766 */       Map<String, String> ct = req.getHeaders().getParams("Content-Type");
/*  767 */       if (!ct.containsKey("multipart/form-data"))
/*  768 */         throw new IllegalArgumentException("Content-Type is not multipart/form-data"); 
/*  769 */       String boundary = ct.get("boundary");
/*  770 */       if (boundary == null)
/*  771 */         throw new IllegalArgumentException("Content-Type is missing boundary"); 
/*  772 */       this.in = new HTTPServer.MultipartInputStream(req.getBody(), HTTPServer.getBytes(new String[] { boundary }));
/*      */     }
/*      */     
/*      */     public boolean hasNext() {
/*      */       try {
/*  777 */         return (this.next || (this.next = this.in.nextPart()));
/*  778 */       } catch (IOException ioe) {
/*  779 */         throw new RuntimeException(ioe);
/*      */       } 
/*      */     }
/*      */     
/*      */     public Part next() {
/*  784 */       if (!hasNext())
/*  785 */         throw new NoSuchElementException(); 
/*  786 */       this.next = false;
/*  787 */       Part p = new Part();
/*      */       try {
/*  789 */         p.headers = HTTPServer.readHeaders(this.in);
/*  790 */       } catch (IOException ioe) {
/*  791 */         throw new RuntimeException(ioe);
/*      */       } 
/*  793 */       Map<String, String> cd = p.headers.getParams("Content-Disposition");
/*  794 */       p.name = cd.get("name");
/*  795 */       p.filename = cd.get("filename");
/*  796 */       p.body = this.in;
/*  797 */       return p;
/*      */     }
/*      */     
/*      */     public void remove() {
/*  801 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class VirtualHost
/*      */   {
/*      */     protected final String name;
/*      */ 
/*      */     
/*      */     public class ContextInfo
/*      */     {
/*      */       protected final String path;
/*      */       
/*  816 */       protected final Map<String, HTTPServer.ContextHandler> handlers = new ConcurrentHashMap<>(2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public ContextInfo(String path) {
/*  825 */         this.path = path;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public String getPath() {
/*  834 */         return this.path;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Map<String, HTTPServer.ContextHandler> getHandlers() {
/*  843 */         return this.handlers;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public void addHandler(HTTPServer.ContextHandler handler, String... methods) {
/*  853 */         if (methods.length == 0)
/*  854 */           methods = new String[] { "GET" }; 
/*  855 */         for (String method : methods) {
/*  856 */           this.handlers.put(method, handler);
/*  857 */           HTTPServer.VirtualHost.this.methods.add(method);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*  863 */     protected final Set<String> aliases = new CopyOnWriteArraySet<>();
/*  864 */     protected volatile String directoryIndex = "index.html";
/*      */     protected volatile boolean allowGeneratedIndex;
/*  866 */     protected final Set<String> methods = new CopyOnWriteArraySet<>();
/*  867 */     protected final ContextInfo emptyContext = new ContextInfo(null);
/*  868 */     protected final ConcurrentMap<String, ContextInfo> contexts = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public VirtualHost(String name) {
/*  877 */       this.name = name;
/*  878 */       this.contexts.put("*", new ContextInfo(null));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/*  887 */       return this.name;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addAlias(String alias) {
/*  896 */       this.aliases.add(alias);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Set<String> getAliases() {
/*  905 */       return Collections.unmodifiableSet(this.aliases);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setDirectoryIndex(String directoryIndex) {
/*  921 */       this.directoryIndex = directoryIndex;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getDirectoryIndex() {
/*  930 */       return this.directoryIndex;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setAllowGeneratedIndex(boolean allowed) {
/*  940 */       this.allowGeneratedIndex = allowed;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isAllowGeneratedIndex() {
/*  949 */       return this.allowGeneratedIndex;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Set<String> getMethods() {
/*  959 */       return this.methods;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ContextInfo getContext(String path) {
/*  974 */       for (path = HTTPServer.trimRight(path, '/'); path != null; path = HTTPServer.getParentPath(path)) {
/*  975 */         ContextInfo info = this.contexts.get(path);
/*  976 */         if (info != null)
/*  977 */           return info; 
/*      */       } 
/*  979 */       return this.emptyContext;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addContext(String path, HTTPServer.ContextHandler handler, String... methods) {
/*  992 */       if (path == null || (!path.startsWith("/") && !path.equals("*")))
/*  993 */         throw new IllegalArgumentException("invalid path: " + path); 
/*  994 */       path = HTTPServer.trimRight(path, '/');
/*  995 */       ContextInfo info = new ContextInfo(path);
/*  996 */       ContextInfo existing = this.contexts.putIfAbsent(path, info);
/*  997 */       info = (existing != null) ? existing : info;
/*  998 */       info.addHandler(handler, methods);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addContexts(Object o) throws IllegalArgumentException {
/* 1010 */       for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
/*      */         
/* 1012 */         for (Method m : c.getDeclaredMethods()) {
/* 1013 */           HTTPServer.Context context = m.<HTTPServer.Context>getAnnotation(HTTPServer.Context.class);
/* 1014 */           if (context != null) {
/* 1015 */             m.setAccessible(true);
/* 1016 */             HTTPServer.ContextHandler handler = new HTTPServer.MethodContextHandler(m, o);
/* 1017 */             addContext(context.value(), handler, context.methods());
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public class ContextInfo
/*      */   {
/*      */     protected final String path;
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Map<String, HTTPServer.ContextHandler> handlers = new ConcurrentHashMap<>(2);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ContextInfo(String path) {
/*      */       this.path = path;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getPath() {
/*      */       return this.path;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<String, HTTPServer.ContextHandler> getHandlers() {
/*      */       return this.handlers;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addHandler(HTTPServer.ContextHandler handler, String... methods) {
/*      */       if (methods.length == 0) {
/*      */         methods = new String[] { "GET" };
/*      */       }
/*      */       for (String method : methods) {
/*      */         this.handlers.put(method, handler);
/*      */         HTTPServer.VirtualHost.this.methods.add(method);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class FileContextHandler
/*      */     implements ContextHandler
/*      */   {
/*      */     protected final File base;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public FileContextHandler(File dir) throws IOException {
/* 1083 */       this.base = dir.getCanonicalFile();
/*      */     }
/*      */     
/*      */     public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
/* 1087 */       return HTTPServer.serveFile(this.base, req.getContext().getPath(), req, resp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class MethodContextHandler
/*      */     implements ContextHandler
/*      */   {
/*      */     protected final Method m;
/*      */ 
/*      */ 
/*      */     
/*      */     protected final Object obj;
/*      */ 
/*      */ 
/*      */     
/*      */     public MethodContextHandler(Method m, Object obj) throws IllegalArgumentException {
/* 1106 */       this.m = m;
/* 1107 */       this.obj = obj;
/* 1108 */       Class<?>[] params = m.getParameterTypes();
/* 1109 */       if (params.length != 2 || 
/* 1110 */         !HTTPServer.Request.class.isAssignableFrom(params[0]) || 
/* 1111 */         !HTTPServer.Response.class.isAssignableFrom(params[1]) || 
/* 1112 */         !int.class.isAssignableFrom(m.getReturnType()))
/* 1113 */         throw new IllegalArgumentException("invalid method signature: " + m); 
/*      */     }
/*      */     
/*      */     public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
/*      */       try {
/* 1118 */         return ((Integer)this.m.invoke(this.obj, new Object[] { req, resp })).intValue();
/* 1119 */       } catch (InvocationTargetException ite) {
/* 1120 */         throw new IOException("error: " + ite.getCause().getMessage());
/* 1121 */       } catch (Exception e) {
/* 1122 */         throw new IOException("error: " + e);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Header
/*      */   {
/*      */     protected final String name;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected final String value;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Header(String name, String value) {
/* 1145 */       this.name = name.trim();
/* 1146 */       this.value = value.trim();
/*      */       
/* 1148 */       if (this.name.length() == 0) {
/* 1149 */         throw new IllegalArgumentException("name cannot be empty");
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getName() {
/* 1157 */       return this.name;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getValue() {
/* 1164 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Headers
/*      */     implements Iterable<Header>
/*      */   {
/* 1181 */     protected HTTPServer.Header[] headers = new HTTPServer.Header[12];
/*      */ 
/*      */ 
/*      */     
/*      */     protected int count;
/*      */ 
/*      */ 
/*      */     
/*      */     public int size() {
/* 1190 */       return this.count;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String get(String name) {
/* 1200 */       for (int i = 0; i < this.count; i++) {
/* 1201 */         if (this.headers[i].getName().equalsIgnoreCase(name))
/* 1202 */           return this.headers[i].getValue(); 
/* 1203 */       }  return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Date getDate(String name) {
/*      */       try {
/* 1215 */         String header = get(name);
/* 1216 */         return (header == null) ? null : HTTPServer.parseDate(header);
/* 1217 */       } catch (IllegalArgumentException iae) {
/* 1218 */         return null;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean contains(String name) {
/* 1229 */       return (get(name) != null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void add(String name, String value) {
/* 1240 */       HTTPServer.Header header = new HTTPServer.Header(name, value);
/*      */       
/* 1242 */       if (this.count == this.headers.length) {
/* 1243 */         HTTPServer.Header[] expanded = new HTTPServer.Header[2 * this.count];
/* 1244 */         System.arraycopy(this.headers, 0, expanded, 0, this.count);
/* 1245 */         this.headers = expanded;
/*      */       } 
/* 1247 */       this.headers[this.count++] = header;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void addAll(Headers headers) {
/* 1257 */       for (HTTPServer.Header header : headers) {
/* 1258 */         add(header.getName(), header.getValue());
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HTTPServer.Header replace(String name, String value) {
/* 1271 */       for (int i = 0; i < this.count; i++) {
/* 1272 */         if (this.headers[i].getName().equalsIgnoreCase(name)) {
/* 1273 */           HTTPServer.Header prev = this.headers[i];
/* 1274 */           this.headers[i] = new HTTPServer.Header(name, value);
/* 1275 */           return prev;
/*      */         } 
/*      */       } 
/* 1278 */       add(name, value);
/* 1279 */       return null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void remove(String name) {
/* 1288 */       int j = 0;
/* 1289 */       for (int i = 0; i < this.count; i++) {
/* 1290 */         if (!this.headers[i].getName().equalsIgnoreCase(name))
/* 1291 */           this.headers[j++] = this.headers[i]; 
/* 1292 */       }  while (this.count > j) {
/* 1293 */         this.headers[--this.count] = null;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void writeTo(OutputStream out) throws IOException {
/* 1303 */       for (int i = 0; i < this.count; i++) {
/* 1304 */         out.write(HTTPServer.getBytes(new String[] { this.headers[i].getName(), ": ", this.headers[i].getValue() }));
/* 1305 */         out.write(HTTPServer.CRLF);
/*      */       } 
/* 1307 */       out.write(HTTPServer.CRLF);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<String, String> getParams(String name) {
/* 1319 */       Map<String, String> params = new LinkedHashMap<>();
/* 1320 */       for (String param : HTTPServer.split(get(name), ";", -1)) {
/* 1321 */         String[] pair = HTTPServer.split(param, "=", 2);
/* 1322 */         String val = (pair.length == 1) ? "" : HTTPServer.trimLeft(HTTPServer.trimRight(pair[1], '"'), '"');
/* 1323 */         params.put(pair[0], val);
/*      */       } 
/* 1325 */       return params;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Iterator<HTTPServer.Header> iterator() {
/* 1338 */       return Arrays.<HTTPServer.Header>asList(this.headers).subList(0, this.count).iterator();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class Request
/*      */   {
/*      */     protected String method;
/*      */     
/*      */     protected URI uri;
/*      */     
/*      */     protected URL baseURL;
/*      */     
/*      */     protected String version;
/*      */     
/*      */     protected HTTPServer.Headers headers;
/*      */     
/*      */     protected InputStream body;
/*      */     
/*      */     protected Socket sock;
/*      */     
/*      */     protected Map<String, String> params;
/*      */     
/*      */     protected HTTPServer.VirtualHost host;
/*      */     protected HTTPServer.VirtualHost.ContextInfo context;
/*      */     private List<String[]> _paramsList;
/*      */     
/*      */     public Request(InputStream in, Socket sock) throws IOException {
/* 1366 */       this.sock = sock;
/* 1367 */       readRequestLine(in);
/* 1368 */       this.headers = HTTPServer.readHeaders(in);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1375 */       String header = this.headers.get("Transfer-Encoding");
/* 1376 */       if (header != null && !header.toLowerCase(Locale.US).equals("identity"))
/* 1377 */       { if (Arrays.<String>asList(HTTPServer.splitElements(header, true)).contains("chunked")) {
/* 1378 */           this.body = new HTTPServer.ChunkedInputStream(in, this.headers);
/*      */         } else {
/* 1380 */           this.body = in;
/*      */         }  }
/* 1382 */       else { header = this.headers.get("Content-Length");
/* 1383 */         long len = (header == null) ? 0L : HTTPServer.parseULong(header, 10);
/* 1384 */         this.body = new HTTPServer.LimitedInputStream(in, len, false); }
/*      */     
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getMethod() {
/* 1393 */       return this.method;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public URI getURI() {
/* 1400 */       return this.uri;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getVersion() {
/* 1407 */       return this.version;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HTTPServer.Headers getHeaders() {
/* 1414 */       return this.headers;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public InputStream getBody() {
/* 1421 */       return this.body;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Socket getSocket() {
/* 1428 */       return this.sock;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getPath() {
/* 1437 */       return this.uri.getPath();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setPath(String path) {
/*      */       try {
/* 1449 */         this
/* 1450 */           .uri = new URI(this.uri.getScheme(), this.uri.getUserInfo(), this.uri.getHost(), this.uri.getPort(), HTTPServer.trimDuplicates(path, '/'), this.uri.getQuery(), this.uri.getFragment());
/* 1451 */         this.context = null;
/* 1452 */       } catch (URISyntaxException use) {
/* 1453 */         throw new IllegalArgumentException("error setting path", use);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public URL getBaseURL() {
/* 1466 */       if (this.baseURL != null) {
/* 1467 */         return this.baseURL;
/*      */       }
/* 1469 */       String host = this.uri.getHost();
/* 1470 */       if (host == null) {
/* 1471 */         host = this.headers.get("Host");
/* 1472 */         if (host == null)
/* 1473 */           host = HTTPServer.detectLocalHostName(); 
/*      */       } 
/* 1475 */       int pos = host.indexOf(':');
/* 1476 */       host = (pos < 0) ? host : host.substring(0, pos);
/*      */       try {
/* 1478 */         return this.baseURL = new URL(HTTPServer.this.secure ? "https" : "http", host, HTTPServer.this.port, "");
/* 1479 */       } catch (MalformedURLException mue) {
/* 1480 */         return null;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public List<String[]> getParamsList() throws IOException {
/* 1503 */       if (this._paramsList == null) {
/* 1504 */         List<String[]> queryParams = HTTPServer.parseParamsList(this.uri.getRawQuery());
/* 1505 */         List<String[]> bodyParams = (List)Collections.emptyList();
/* 1506 */         String ct = this.headers.get("Content-Type");
/* 1507 */         if (ct != null && ct.toLowerCase(Locale.US).startsWith("application/x-www-form-urlencoded")) {
/* 1508 */           bodyParams = HTTPServer.parseParamsList(HTTPServer.readToken(this.body, -1, "UTF-8", HTTPServer.MAX_BODY_SIZE));
/*      */         }
/*      */         
/* 1511 */         this._paramsList = (List)new ArrayList<>();
/*      */         
/* 1513 */         if (!queryParams.isEmpty()) {
/* 1514 */           this._paramsList.addAll(queryParams);
/*      */         }
/* 1516 */         if (!bodyParams.isEmpty()) {
/* 1517 */           this._paramsList.addAll(bodyParams);
/*      */         }
/*      */       } 
/* 1520 */       return this._paramsList;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Map<String, String> getParams() throws IOException {
/* 1541 */       if (this.params == null)
/* 1542 */         this.params = HTTPServer.toMap((Collection)getParamsList()); 
/* 1543 */       return this.params;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public long[] getRange(long length) {
/* 1556 */       String header = this.headers.get("Range");
/* 1557 */       return (header == null || !header.startsWith("bytes=")) ? null : 
/* 1558 */         HTTPServer.parseRange(header.substring(6), length);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void readRequestLine(InputStream in) throws IOException {
/*      */       String line;
/*      */       try {
/*      */         
/* 1572 */         do { line = HTTPServer.readLine(in); } while (line.length() == 0);
/* 1573 */       } catch (IOException ioe) {
/* 1574 */         throw new IOException("missing request line");
/*      */       } 
/* 1576 */       String[] tokens = HTTPServer.split(line, " ", -1);
/* 1577 */       if (tokens.length != 3)
/* 1578 */         throw new IOException("invalid request line: \"" + line + "\""); 
/*      */       try {
/* 1580 */         this.method = tokens[0];
/*      */         
/* 1582 */         this.uri = new URI(tokens[1]);
/* 1583 */         this.version = tokens[2];
/* 1584 */       } catch (URISyntaxException use) {
/* 1585 */         throw new IOException("invalid URI: " + use.getMessage());
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HTTPServer.VirtualHost getVirtualHost() {
/* 1597 */       return (this.host != null) ? this.host : (
/* 1598 */         ((this.host = HTTPServer.this.getVirtualHost(getBaseURL().getHost())) != null) ? this.host : (this
/* 1599 */         .host = HTTPServer.this.getVirtualHost(null)));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HTTPServer.VirtualHost.ContextInfo getContext() {
/* 1608 */       return (this.context != null) ? this.context : (this.context = getVirtualHost().getContext(getPath()));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public class Response
/*      */     implements Closeable
/*      */   {
/*      */     protected OutputStream out;
/*      */     
/*      */     protected OutputStream encodedOut;
/*      */     
/*      */     protected HTTPServer.Headers headers;
/*      */     
/*      */     protected boolean discardBody;
/*      */     
/*      */     protected int state;
/*      */     
/*      */     protected HTTPServer.Request req;
/*      */ 
/*      */     
/*      */     public Response(OutputStream out) {
/* 1630 */       this.out = out;
/* 1631 */       this.headers = new HTTPServer.Headers();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setDiscardBody(boolean discardBody) {
/* 1640 */       this.discardBody = discardBody;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setClientCapabilities(HTTPServer.Request req) {
/* 1649 */       this.req = req;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public HTTPServer.Headers getHeaders() {
/* 1656 */       return this.headers;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public OutputStream getOutputStream() {
/* 1664 */       return this.out;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean headersSent() {
/* 1671 */       return (this.state == 1);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public OutputStream getBody() throws IOException {
/* 1687 */       if (this.encodedOut != null || this.discardBody) {
/* 1688 */         return this.encodedOut;
/*      */       }
/* 1690 */       List<String> te = Arrays.asList(HTTPServer.splitElements(this.headers.get("Transfer-Encoding"), true));
/* 1691 */       List<String> ce = Arrays.asList(HTTPServer.splitElements(this.headers.get("Content-Encoding"), true));
/* 1692 */       this.encodedOut = new HTTPServer.ResponseOutputStream(this.out);
/* 1693 */       if (te.contains("chunked"))
/* 1694 */         this.encodedOut = new HTTPServer.ChunkedOutputStream(this.encodedOut); 
/* 1695 */       if (ce.contains("gzip") || te.contains("gzip")) {
/* 1696 */         this.encodedOut = new GZIPOutputStream(this.encodedOut, 4096);
/* 1697 */       } else if (ce.contains("deflate") || te.contains("deflate")) {
/* 1698 */         this.encodedOut = new DeflaterOutputStream(this.encodedOut);
/*      */       } 
/* 1700 */       return this.encodedOut;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/* 1709 */       this.state = -1;
/* 1710 */       if (this.encodedOut != null)
/* 1711 */         this.encodedOut.close(); 
/* 1712 */       this.out.flush();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void sendHeaders(int status) throws IOException {
/* 1726 */       if (headersSent())
/* 1727 */         throw new IOException("headers were already sent"); 
/* 1728 */       if (!this.headers.contains("Date"))
/* 1729 */         this.headers.add("Date", HTTPServer.formatDate(System.currentTimeMillis())); 
/* 1730 */       this.headers.add("Server", "JLHTTP/2.6");
/* 1731 */       this.out.write(HTTPServer.getBytes(new String[] { "HTTP/1.1 ", Integer.toString(status), " ", HTTPServer.statuses[status] }));
/* 1732 */       this.out.write(HTTPServer.CRLF);
/* 1733 */       this.headers.writeTo(this.out);
/* 1734 */       this.state = 1;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void sendHeaders(int status, long length, long lastModified, String etag, String contentType, long[] range) throws IOException {
/* 1761 */       if (range != null) {
/* 1762 */         this.headers.add("Content-Range", "bytes " + range[0] + "-" + range[1] + "/" + ((length >= 0L) ? 
/* 1763 */             (String)Long.valueOf(length) : "*"));
/* 1764 */         length = range[1] - range[0] + 1L;
/* 1765 */         if (status == 200)
/* 1766 */           status = 206; 
/*      */       } 
/* 1768 */       String ct = this.headers.get("Content-Type");
/* 1769 */       if (ct == null) {
/* 1770 */         ct = (contentType != null) ? contentType : "application/octet-stream";
/* 1771 */         this.headers.add("Content-Type", ct);
/*      */       }
/* 1773 */       else if (contentType != null) {
/* 1774 */         ct = contentType;
/* 1775 */         this.headers.replace("Content-Type", ct);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1780 */       if (!this.headers.contains("Content-Length") && !this.headers.contains("Transfer-Encoding")) {
/*      */         
/* 1782 */         boolean modern = (this.req != null && this.req.getVersion().endsWith("1.1"));
/* 1783 */         String accepted = (this.req == null) ? null : this.req.getHeaders().get("Accept-Encoding");
/* 1784 */         List<String> encodings = Arrays.asList(HTTPServer.splitElements(accepted, true));
/*      */         
/* 1786 */         String compression = encodings.contains("gzip") ? "gzip" : (encodings.contains("deflate") ? "deflate" : null);
/* 1787 */         if (compression != null && (length < 0L || length > 300L) && HTTPServer.isCompressible(ct) && modern) {
/*      */           
/* 1789 */           this.headers.replace("Transfer-Encoding", "chunked");
/* 1790 */           this.headers.replace("Content-Encoding", compression);
/* 1791 */         } else if (length < 0L && modern) {
/* 1792 */           this.headers.replace("Transfer-Encoding", "chunked");
/* 1793 */         } else if (length >= 0L) {
/* 1794 */           this.headers.replace("Content-Length", Long.toString(length));
/*      */         } 
/*      */       } 
/* 1797 */       if (!this.headers.contains("Vary"))
/* 1798 */         this.headers.add("Vary", "Accept-Encoding"); 
/* 1799 */       if (lastModified > 0L && !this.headers.contains("Last-Modified"))
/* 1800 */         this.headers.add("Last-Modified", HTTPServer.formatDate(Math.min(lastModified, System.currentTimeMillis()))); 
/* 1801 */       if (etag != null && !this.headers.contains("ETag"))
/* 1802 */         this.headers.add("ETag", etag); 
/* 1803 */       if (this.req != null && "close".equalsIgnoreCase(this.req.getHeaders().get("Connection")) && 
/* 1804 */         !this.headers.contains("Connection"))
/* 1805 */         this.headers.add("Connection", "close"); 
/* 1806 */       sendHeaders(status);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void send(int status, String text) throws IOException {
/* 1821 */       byte[] content = text.getBytes("UTF-8");
/* 1822 */       sendHeaders(status, content.length, -1L, "W/\"" + 
/* 1823 */           Integer.toHexString(text.hashCode()) + "\"", "text/html; charset=utf-8", null);
/*      */       
/* 1825 */       OutputStream out = getBody();
/* 1826 */       if (out != null) {
/* 1827 */         out.write(content);
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void sendError(int status, String text) throws IOException {
/* 1841 */       send(status, String.format("<!DOCTYPE html>%n<html>%n<head><title>%d %s</title></head>%n<body><h1>%d %s</h1>%n<p>%s</p>%n</body></html>", new Object[] {
/*      */ 
/*      */               
/* 1844 */               Integer.valueOf(status), HTTPServer.statuses[status], Integer.valueOf(status), HTTPServer.statuses[status], HTTPServer.escapeHTML(text)
/*      */             }));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void sendError(int status) throws IOException {
/* 1854 */       String text = (status < 400) ? ":)" : "sorry it didn't work out :(";
/* 1855 */       sendError(status, text);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void sendBody(InputStream body, long length, long[] range) throws IOException {
/* 1869 */       OutputStream out = getBody();
/* 1870 */       if (out != null) {
/* 1871 */         if (range != null) {
/* 1872 */           long offset = range[0];
/* 1873 */           length = range[1] - range[0] + 1L;
/* 1874 */           while (offset > 0L) {
/* 1875 */             long skip = body.skip(offset);
/* 1876 */             if (skip == 0L)
/* 1877 */               throw new IOException("can't skip to " + range[0]); 
/* 1878 */             offset -= skip;
/*      */           } 
/*      */         } 
/* 1881 */         HTTPServer.transfer(body, out, length);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void redirect(String url, boolean permanent) throws IOException {
/*      */       try {
/* 1895 */         url = (new URI(url)).toASCIIString();
/* 1896 */       } catch (URISyntaxException e) {
/* 1897 */         throw new IOException("malformed URL: " + url);
/*      */       } 
/* 1899 */       this.headers.add("Location", url);
/*      */       
/* 1901 */       if (permanent) {
/* 1902 */         sendError(301, "Permanently moved to " + url);
/*      */       } else {
/* 1904 */         sendError(302, "Temporarily moved to " + url);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected class SocketHandlerThread
/*      */     extends Thread
/*      */   {
/*      */     public void run() {
/* 1914 */       setName(getClass().getSimpleName() + "-" + HTTPServer.this.port);
/*      */       try {
/* 1916 */         ServerSocket serv = HTTPServer.this.serv;
/* 1917 */         while (serv != null && !serv.isClosed()) {
/* 1918 */           final Socket sock = serv.accept();
/* 1919 */           HTTPServer.this.executor.execute(new Runnable() {
/*      */                 public void run() {
/*      */                   try {
/*      */                     try {
/* 1923 */                       sock.setSoTimeout(HTTPServer.this.socketTimeout);
/* 1924 */                       sock.setTcpNoDelay(true);
/* 1925 */                       HTTPServer.this.handleConnection(sock.getInputStream(), sock.getOutputStream(), sock);
/*      */                     } finally {
/*      */ 
/*      */                       
/*      */                       try {
/* 1930 */                         if (!(sock instanceof javax.net.ssl.SSLSocket)) {
/* 1931 */                           sock.shutdownOutput();
/* 1932 */                           HTTPServer.transfer(sock.getInputStream(), null, -1L);
/*      */                         } 
/*      */                       } finally {
/* 1935 */                         sock.close();
/*      */                       } 
/*      */                     } 
/* 1938 */                   } catch (IOException iOException) {}
/*      */                 }
/*      */               });
/*      */         } 
/* 1942 */       } catch (IOException iOException) {}
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 1948 */   protected volatile int socketTimeout = 10000;
/*      */   protected volatile ServerSocketFactory serverSocketFactory;
/*      */   protected volatile boolean secure;
/*      */   protected volatile Executor executor;
/*      */   protected volatile ServerSocket serv;
/* 1953 */   protected final Map<String, VirtualHost> hosts = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HTTPServer(int port) {
/* 1963 */     setPort(port);
/* 1964 */     addVirtualHost(new VirtualHost(null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HTTPServer(String host, int port) {
/* 1976 */     setPort(port);
/* 1977 */     setHost(host);
/* 1978 */     addVirtualHost(new VirtualHost(null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HTTPServer() {
/* 1985 */     this(80);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPort(int port) {
/* 1994 */     this.port = port;
/*      */   }
/*      */   public void setHost(String host) {
/* 1997 */     this.host = host;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setServerSocketFactory(ServerSocketFactory factory) {
/* 2013 */     this.serverSocketFactory = factory;
/* 2014 */     this.secure = factory instanceof SSLServerSocketFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSocketTimeout(int timeout) {
/* 2022 */     this.socketTimeout = timeout;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExecutor(Executor executor) {
/* 2032 */     this.executor = executor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public VirtualHost getVirtualHost(String name) {
/* 2043 */     return this.hosts.get((name == null) ? "" : name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<VirtualHost> getVirtualHosts() {
/* 2052 */     return Collections.unmodifiableSet(new HashSet<>(this.hosts.values()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addVirtualHost(VirtualHost host) {
/* 2062 */     String name = host.getName();
/* 2063 */     this.hosts.put((name == null) ? "" : name, host);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ServerSocket createServerSocket() throws IOException {
/* 2076 */     ServerSocket serv = this.serverSocketFactory.createServerSocket();
/* 2077 */     serv.setReuseAddress(true);
/* 2078 */     InetSocketAddress address = null;
/* 2079 */     if (this.host == null) {
/* 2080 */       address = new InetSocketAddress(this.port);
/*      */     } else {
/* 2082 */       address = new InetSocketAddress(this.host, this.port);
/*      */     } 
/* 2084 */     serv.bind(address);
/*      */     
/* 2086 */     return serv;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void start() throws IOException {
/* 2098 */     if (this.serv != null)
/*      */       return; 
/* 2100 */     if (this.serverSocketFactory == null)
/* 2101 */       this.serverSocketFactory = ServerSocketFactory.getDefault(); 
/* 2102 */     this.serv = createServerSocket();
/* 2103 */     if (this.executor == null) {
/* 2104 */       this.executor = Executors.newCachedThreadPool();
/*      */     }
/* 2106 */     for (VirtualHost host : getVirtualHosts()) {
/* 2107 */       for (String alias : host.getAliases())
/* 2108 */         this.hosts.put(alias, host); 
/*      */     } 
/* 2110 */     (new SocketHandlerThread()).start();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void stop() {
/*      */     try {
/* 2119 */       if (this.serv != null)
/* 2120 */         this.serv.close(); 
/* 2121 */     } catch (IOException iOException) {}
/* 2122 */     this.serv = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handleConnection(InputStream in, OutputStream out, Socket sock) throws IOException {
/*      */     Request req;
/*      */     Response resp;
/* 2138 */     in = new BufferedInputStream(in, 4096);
/* 2139 */     out = new BufferedOutputStream(out, 4096);
/*      */ 
/*      */ 
/*      */     
/*      */     do {
/* 2144 */       req = null;
/* 2145 */       resp = new Response(out);
/*      */       
/* 2147 */       try { req = new Request(in, sock);
/* 2148 */         handleTransaction(req, resp); }
/* 2149 */       catch (Throwable t)
/* 2150 */       { if (req == null)
/* 2151 */         { if (t instanceof IOException && t.getMessage().contains("missing request line"))
/*      */           
/*      */           { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2165 */             resp.close(); break; }  resp.getHeaders().add("Connection", "close"); if (t instanceof java.io.InterruptedIOException) { resp.sendError(408, "Timeout waiting for client request"); } else { resp.sendError(400, "Invalid request: " + t.getMessage()); }  } else if (!resp.headersSent()) { resp = new Response(out); resp.getHeaders().add("Connection", "close"); resp.sendError(500, "Error processing request: " + t.getMessage()); }  break; } finally { resp.close(); }
/*      */ 
/*      */       
/* 2168 */       transfer(req.getBody(), null, -1L);
/*      */     }
/* 2170 */     while (!"close".equalsIgnoreCase(req.getHeaders().get("Connection")) && 
/* 2171 */       !"close".equalsIgnoreCase(resp.getHeaders().get("Connection")) && req.getVersion().endsWith("1.1"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handleTransaction(Request req, Response resp) throws IOException {
/* 2186 */     resp.setClientCapabilities(req);
/* 2187 */     if (preprocessTransaction(req, resp)) {
/* 2188 */       handleMethod(req, resp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean preprocessTransaction(Request req, Response resp) throws IOException {
/* 2202 */     Headers reqHeaders = req.getHeaders();
/*      */     
/* 2204 */     String version = req.getVersion();
/* 2205 */     if (version.equals("HTTP/1.1")) {
/* 2206 */       if (!reqHeaders.contains("Host")) {
/*      */         
/* 2208 */         resp.sendError(400, "Missing required Host header");
/* 2209 */         return false;
/*      */       } 
/*      */       
/* 2212 */       String expect = reqHeaders.get("Expect");
/* 2213 */       if (expect != null) {
/* 2214 */         if (expect.equalsIgnoreCase("100-continue")) {
/* 2215 */           Response tempResp = new Response(resp.getOutputStream());
/* 2216 */           tempResp.sendHeaders(100);
/* 2217 */           resp.getOutputStream().flush();
/*      */         } else {
/*      */           
/* 2220 */           resp.sendError(417);
/* 2221 */           return false;
/*      */         } 
/*      */       }
/* 2224 */     } else if (version.equals("HTTP/1.0") || version.equals("HTTP/0.9")) {
/*      */       
/* 2226 */       for (String token : splitElements(reqHeaders.get("Connection"), false))
/* 2227 */         reqHeaders.remove(token); 
/*      */     } else {
/* 2229 */       resp.sendError(400, "Unknown version: " + version);
/* 2230 */       return false;
/*      */     } 
/* 2232 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handleMethod(Request req, Response resp) throws IOException {
/* 2243 */     String method = req.getMethod();
/* 2244 */     Map<String, ContextHandler> handlers = req.getContext().getHandlers();
/*      */     
/* 2246 */     if (method.equals("GET") || handlers.containsKey(method)) {
/* 2247 */       serve(req, resp);
/* 2248 */     } else if (method.equals("HEAD")) {
/* 2249 */       req.method = "GET";
/* 2250 */       resp.setDiscardBody(true);
/* 2251 */       serve(req, resp);
/* 2252 */     } else if (method.equals("TRACE")) {
/* 2253 */       handleTrace(req, resp);
/*      */     } else {
/* 2255 */       Set<String> methods = new LinkedHashSet<>();
/* 2256 */       methods.addAll(Arrays.asList(new String[] { "GET", "HEAD", "TRACE", "OPTIONS" }));
/*      */       
/* 2258 */       boolean isServerOptions = (req.getPath().equals("*") && method.equals("OPTIONS"));
/* 2259 */       methods.addAll(isServerOptions ? req.getVirtualHost().getMethods() : handlers.keySet());
/* 2260 */       resp.getHeaders().add("Allow", join(", ", methods));
/* 2261 */       if (method.equals("OPTIONS")) {
/* 2262 */         resp.getHeaders().add("Content-Length", "0");
/* 2263 */         resp.sendHeaders(200);
/* 2264 */       } else if (req.getVirtualHost().getMethods().contains(method)) {
/* 2265 */         resp.sendHeaders(405);
/*      */       } else {
/* 2267 */         resp.sendError(501);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handleTrace(Request req, Response resp) throws IOException {
/* 2280 */     resp.sendHeaders(200, -1L, -1L, null, "message/http", null);
/* 2281 */     OutputStream out = resp.getBody();
/* 2282 */     out.write(getBytes(new String[] { "TRACE ", req.getURI().toString(), " ", req.getVersion() }));
/* 2283 */     out.write(CRLF);
/* 2284 */     req.getHeaders().writeTo(out);
/* 2285 */     transfer(req.getBody(), out, -1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void serve(Request req, Response resp) throws IOException {
/* 2298 */     ContextHandler handler = req.getContext().getHandlers().get(req.getMethod());
/* 2299 */     if (handler == null) {
/* 2300 */       resp.sendError(404);
/*      */       
/*      */       return;
/*      */     } 
/* 2304 */     int status = 404;
/*      */     
/* 2306 */     String path = req.getPath();
/* 2307 */     if (path.endsWith("/")) {
/* 2308 */       String index = req.getVirtualHost().getDirectoryIndex();
/* 2309 */       if (index != null) {
/* 2310 */         req.setPath(path + index);
/* 2311 */         status = handler.serve(req, resp);
/* 2312 */         req.setPath(path);
/*      */       } 
/*      */     } 
/* 2315 */     if (status == 404)
/* 2316 */       status = handler.serve(req, resp); 
/* 2317 */     if (status > 0) {
/* 2318 */       resp.sendError(status);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addContentType(String contentType, String... suffixes) {
/* 2334 */     for (String suffix : suffixes) {
/* 2335 */       contentTypes.put(suffix.toLowerCase(Locale.US), contentType.toLowerCase(Locale.US));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void addContentTypes(InputStream in) throws IOException {
/*      */     
/*      */     try { while (true)
/*      */       { 
/* 2348 */         try { String line = readLine(in).trim();
/* 2349 */           if (line.length() > 0 && line.charAt(0) != '#') {
/* 2350 */             String[] tokens = split(line, " \t", -1);
/* 2351 */             for (int i = 1; i < tokens.length; i++) {
/* 2352 */               addContentType(tokens[0], new String[] { tokens[i] });
/*      */             } 
/*      */           }  }
/* 2355 */         catch (EOFException eOFException)
/*      */         
/* 2357 */         { in.close(); break; }  }  } finally { in.close(); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getContentType(String path, String def) {
/* 2371 */     int dot = path.lastIndexOf('.');
/* 2372 */     String type = (dot < 0) ? def : contentTypes.get(path.substring(dot + 1).toLowerCase(Locale.US));
/* 2373 */     return (type != null) ? type : def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isCompressible(String contentType) {
/* 2383 */     int pos = contentType.indexOf(';');
/* 2384 */     String ct = (pos < 0) ? contentType : contentType.substring(0, pos);
/* 2385 */     for (String s : compressibleContentTypes) {
/* 2386 */       if (s.equals(ct) || (s.charAt(0) == '*' && ct.endsWith(s.substring(1))) || (s
/* 2387 */         .charAt(s.length() - 1) == '*' && ct.startsWith(s.substring(0, s.length() - 1))))
/* 2388 */         return true; 
/* 2389 */     }  return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String detectLocalHostName() {
/*      */     try {
/* 2399 */       return InetAddress.getLocalHost().getCanonicalHostName();
/* 2400 */     } catch (UnknownHostException uhe) {
/* 2401 */       return "localhost";
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String[]> parseParamsList(String s) {
/* 2424 */     if (s == null || s.length() == 0)
/* 2425 */       return (List)Collections.emptyList(); 
/* 2426 */     List<String[]> params = (List)new ArrayList<>(8);
/* 2427 */     for (String pair : split(s, "&", -1)) {
/* 2428 */       int pos = pair.indexOf('=');
/* 2429 */       String name = (pos < 0) ? pair : pair.substring(0, pos);
/* 2430 */       String val = (pos < 0) ? "" : pair.substring(pos + 1);
/*      */       try {
/* 2432 */         name = URLDecoder.decode(name.trim(), "UTF-8");
/* 2433 */         val = URLDecoder.decode(val.trim(), "UTF-8");
/* 2434 */         if (name.length() > 0)
/* 2435 */           params.add(new String[] { name, val }); 
/* 2436 */       } catch (UnsupportedEncodingException unsupportedEncodingException) {}
/*      */     } 
/* 2438 */     return params;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> Map<K, V> toMap(Collection<? extends Object[]> pairs) {
/* 2454 */     if (pairs == null || pairs.isEmpty())
/* 2455 */       return Collections.emptyMap(); 
/* 2456 */     Map<K, V> map = new LinkedHashMap<>(pairs.size());
/* 2457 */     for (Object[] pair : pairs) {
/* 2458 */       if (!map.containsKey(pair[0]))
/* 2459 */         map.put((K)pair[0], (V)pair[1]); 
/* 2460 */     }  return map;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] parseRange(String range, long length) {
/* 2473 */     long min = Long.MAX_VALUE;
/* 2474 */     long max = Long.MIN_VALUE;
/*      */     try {
/* 2476 */       for (String token : splitElements(range, false)) {
/*      */         long start, end;
/* 2478 */         int dash = token.indexOf('-');
/* 2479 */         if (dash == 0) {
/* 2480 */           start = length - parseULong(token.substring(1), 10);
/* 2481 */           end = length - 1L;
/* 2482 */         } else if (dash == token.length() - 1) {
/* 2483 */           start = parseULong(token.substring(0, dash), 10);
/* 2484 */           end = length - 1L;
/*      */         } else {
/* 2486 */           start = parseULong(token.substring(0, dash), 10);
/* 2487 */           end = parseULong(token.substring(dash + 1), 10);
/*      */         } 
/* 2489 */         if (end < start)
/* 2490 */           throw new RuntimeException(); 
/* 2491 */         if (start < min)
/* 2492 */           min = start; 
/* 2493 */         if (end > max)
/* 2494 */           max = end; 
/*      */       } 
/* 2496 */       if (max < 0L)
/* 2497 */         throw new RuntimeException(); 
/* 2498 */       if (max >= length && min < length)
/* 2499 */         max = length - 1L; 
/* 2500 */       return new long[] { min, max };
/* 2501 */     } catch (RuntimeException re) {
/* 2502 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseULong(String s, int radix) throws NumberFormatException {
/* 2518 */     long val = Long.parseLong(s, radix);
/* 2519 */     if (s.charAt(0) == '-' || s.charAt(0) == '+')
/* 2520 */       throw new NumberFormatException("invalid digit: " + s.charAt(0)); 
/* 2521 */     return val;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Date parseDate(String time) {
/* 2538 */     for (String pattern : DATE_PATTERNS) {
/*      */       try {
/* 2540 */         SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.US);
/* 2541 */         df.setLenient(false);
/* 2542 */         df.setTimeZone(GMT);
/* 2543 */         return df.parse(time);
/* 2544 */       } catch (ParseException parseException) {}
/*      */     } 
/* 2546 */     throw new IllegalArgumentException("invalid date format: " + time);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatDate(long time) {
/* 2558 */     if (time < -62167392000000L || time > 253402300799999L)
/* 2559 */       throw new IllegalArgumentException("year out of range (0001-9999): " + time); 
/* 2560 */     char[] s = "DAY, 00 MON 0000 00:00:00 GMT".toCharArray();
/* 2561 */     Calendar cal = new GregorianCalendar(GMT, Locale.US);
/* 2562 */     cal.setTimeInMillis(time);
/* 2563 */     System.arraycopy(DAYS, 4 * (cal.get(7) - 1), s, 0, 3);
/* 2564 */     System.arraycopy(MONTHS, 4 * cal.get(2), s, 8, 3);
/* 2565 */     int n = cal.get(5); s[5] = (char)(s[5] + n / 10); s[6] = (char)(s[6] + n % 10);
/* 2566 */     n = cal.get(1); s[12] = (char)(s[12] + n / 1000); s[13] = (char)(s[13] + n / 100 % 10);
/* 2567 */     s[14] = (char)(s[14] + n / 10 % 10); s[15] = (char)(s[15] + n % 10);
/* 2568 */     n = cal.get(11); s[17] = (char)(s[17] + n / 10); s[18] = (char)(s[18] + n % 10);
/* 2569 */     n = cal.get(12); s[20] = (char)(s[20] + n / 10); s[21] = (char)(s[21] + n % 10);
/* 2570 */     n = cal.get(13); s[23] = (char)(s[23] + n / 10); s[24] = (char)(s[24] + n % 10);
/* 2571 */     return new String(s);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] splitElements(String list, boolean lower) {
/* 2585 */     return split((lower && list != null) ? list.toLowerCase(Locale.US) : list, ",", -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] split(String str, String delimiters, int limit) {
/* 2600 */     if (str == null)
/* 2601 */       return new String[0]; 
/* 2602 */     Collection<String> elements = new ArrayList<>();
/* 2603 */     int len = str.length();
/* 2604 */     int start = 0;
/*      */     
/* 2606 */     while (start < len) {
/* 2607 */       int end = (--limit == 0) ? len : start;
/* 2608 */       for (; end < len && delimiters.indexOf(str.charAt(end)) < 0; end++);
/* 2609 */       String element = str.substring(start, end).trim();
/* 2610 */       if (element.length() > 0)
/* 2611 */         elements.add(element); 
/* 2612 */       start = end + 1;
/*      */     } 
/* 2614 */     return elements.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> String join(String delim, Iterable<T> items) {
/* 2627 */     StringBuilder sb = new StringBuilder();
/* 2628 */     for (Iterator<T> it = items.iterator(); it.hasNext();)
/* 2629 */       sb.append(it.next()).append(it.hasNext() ? delim : ""); 
/* 2630 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getParentPath(String path) {
/* 2641 */     path = trimRight(path, '/');
/* 2642 */     int slash = path.lastIndexOf('/');
/* 2643 */     return (slash < 0) ? null : path.substring(0, slash);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimRight(String s, char c) {
/* 2655 */     int len = s.length() - 1;
/*      */     int end;
/* 2657 */     for (end = len; end >= 0 && s.charAt(end) == c; end--);
/* 2658 */     return (end == len) ? s : s.substring(0, end + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimLeft(String s, char c) {
/* 2670 */     int len = s.length();
/*      */     int start;
/* 2672 */     for (start = 0; start < len && s.charAt(start) == c; start++);
/* 2673 */     return (start == 0) ? s : s.substring(start);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String trimDuplicates(String s, char c) {
/* 2686 */     int start = 0;
/* 2687 */     while ((start = s.indexOf(c, start) + 1) > 0) {
/*      */       int end;
/* 2689 */       for (end = start; end < s.length() && s.charAt(end) == c; end++);
/* 2690 */       if (end > start)
/* 2691 */         s = s.substring(0, start) + s.substring(end); 
/*      */     } 
/* 2693 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toSizeApproxString(long size) {
/* 2704 */     char[] units = { ' ', 'K', 'M', 'G', 'T', 'P', 'E' };
/*      */     int u;
/*      */     double s;
/* 2707 */     for (u = 0, s = size; s >= 1000.0D; ) { u++; s /= 1024.0D; }
/* 2708 */      return String.format((s < 10.0D) ? "%.1f%c" : "%.0f%c", new Object[] { Double.valueOf(s), Character.valueOf(units[u]) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String escapeHTML(String s) {
/* 2723 */     int len = s.length();
/* 2724 */     StringBuilder sb = new StringBuilder(len + 30);
/* 2725 */     int start = 0;
/* 2726 */     for (int i = 0; i < len; i++) {
/* 2727 */       String ref = null;
/* 2728 */       switch (s.charAt(i)) { case '&':
/* 2729 */           ref = "&amp;"; break;
/* 2730 */         case '>': ref = "&gt;"; break;
/* 2731 */         case '<': ref = "&lt;"; break;
/* 2732 */         case '"': ref = "&quot;"; break;
/* 2733 */         case '\'': ref = "&#39;"; break; }
/*      */       
/* 2735 */       if (ref != null) {
/* 2736 */         sb.append(s.substring(start, i)).append(ref);
/* 2737 */         start = i + 1;
/*      */       } 
/*      */     } 
/* 2740 */     return (start == 0) ? s : sb.append(s.substring(start)).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytes(String... strings) {
/* 2752 */     int n = 0;
/* 2753 */     for (String s : strings)
/* 2754 */       n += s.length(); 
/* 2755 */     byte[] b = new byte[n];
/* 2756 */     n = 0;
/* 2757 */     for (String s : strings) {
/* 2758 */       for (int i = 0, len = s.length(); i < len; i++)
/* 2759 */         b[n++] = (byte)s.charAt(i); 
/* 2760 */     }  return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void transfer(InputStream in, OutputStream out, long len) throws IOException {
/* 2774 */     if (len == 0L || (out == null && len < 0L && in.read() < 0))
/*      */       return; 
/* 2776 */     byte[] buf = new byte[4096];
/* 2777 */     while (len != 0L) {
/* 2778 */       int count = (len < 0L || buf.length < len) ? buf.length : (int)len;
/* 2779 */       count = in.read(buf, 0, count);
/* 2780 */       if (count < 0) {
/* 2781 */         if (len > 0L)
/* 2782 */           throw new IOException("unexpected end of stream"); 
/*      */         break;
/*      */       } 
/* 2785 */       if (out != null)
/* 2786 */         out.write(buf, 0, count); 
/* 2787 */       len -= (len > 0L) ? count : 0L;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readToken(InputStream in, int delim, String enc, int maxLength) throws IOException {
/* 2812 */     int len = 0;
/* 2813 */     int count = 0;
/* 2814 */     byte[] buf = null; int b;
/* 2815 */     while ((b = in.read()) != -1 && b != delim) {
/* 2816 */       if (count == len) {
/* 2817 */         if (count == maxLength)
/* 2818 */           throw new IOException("token too large (" + count + ")"); 
/* 2819 */         len = (len > 0) ? (2 * len) : 256;
/* 2820 */         len = (maxLength < len) ? maxLength : len;
/* 2821 */         byte[] expanded = new byte[len];
/* 2822 */         if (buf != null)
/* 2823 */           System.arraycopy(buf, 0, expanded, 0, count); 
/* 2824 */         buf = expanded;
/*      */       } 
/* 2826 */       buf[count++] = (byte)b;
/*      */     } 
/* 2828 */     if (b < 0 && delim != -1)
/* 2829 */       throw new EOFException("unexpected end of stream"); 
/* 2830 */     if (delim == 10 && count > 0 && buf[count - 1] == 13)
/* 2831 */       count--; 
/* 2832 */     return (count > 0) ? new String(buf, 0, count, enc) : "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readLine(InputStream in) throws IOException {
/* 2848 */     return readToken(in, 10, "UTF-8", MAX_HEADER_SIZE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Headers readHeaders(InputStream in) throws IOException {
/* 2863 */     Headers headers = new Headers();
/*      */     
/* 2865 */     String prevLine = "";
/* 2866 */     int count = 0; String line;
/* 2867 */     while ((line = readLine(in)).length() > 0) {
/*      */       int start;
/* 2869 */       for (start = 0; start < line.length() && 
/* 2870 */         Character.isWhitespace(line.charAt(start));) start++; 
/* 2871 */       if (start > 0)
/* 2872 */         line = prevLine + ' ' + line.substring(start); 
/* 2873 */       int separator = line.indexOf(':');
/* 2874 */       if (separator < 0)
/* 2875 */         throw new IOException("invalid header: \"" + line + "\""); 
/* 2876 */       String name = line.substring(0, separator);
/* 2877 */       String value = line.substring(separator + 1).trim();
/* 2878 */       Header replaced = headers.replace(name, value);
/*      */       
/* 2880 */       if (replaced != null && start == 0) {
/* 2881 */         value = replaced.getValue() + ", " + value;
/* 2882 */         line = name + ": " + value;
/* 2883 */         headers.replace(name, value);
/*      */       } 
/* 2885 */       prevLine = line;
/* 2886 */       if (++count > 100)
/* 2887 */         throw new IOException("too many header lines"); 
/*      */     } 
/* 2889 */     return headers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean match(boolean strong, String[] etags, String etag) {
/* 2906 */     if (etag == null || (strong && etag.startsWith("W/")))
/* 2907 */       return false; 
/* 2908 */     for (String e : etags) {
/* 2909 */       if (e.equals("*") || (e.equals(etag) && (!strong || !e.startsWith("W/"))))
/* 2910 */         return true; 
/* 2911 */     }  return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getConditionalStatus(Request req, long lastModified, String etag) {
/* 2925 */     Headers headers = req.getHeaders();
/*      */     
/* 2927 */     String header = headers.get("If-Match");
/* 2928 */     if (header != null && !match(true, splitElements(header, false), etag)) {
/* 2929 */       return 412;
/*      */     }
/* 2931 */     Date date = headers.getDate("If-Unmodified-Since");
/* 2932 */     if (date != null && lastModified > date.getTime()) {
/* 2933 */       return 412;
/*      */     }
/* 2935 */     int status = 200;
/* 2936 */     boolean force = false;
/* 2937 */     date = headers.getDate("If-Modified-Since");
/* 2938 */     if (date != null && date.getTime() <= System.currentTimeMillis()) {
/* 2939 */       if (lastModified > date.getTime()) {
/* 2940 */         force = true;
/*      */       } else {
/* 2942 */         status = 304;
/*      */       } 
/*      */     }
/* 2945 */     header = headers.get("If-None-Match");
/* 2946 */     if (header != null)
/* 2947 */       if (match(false, splitElements(header, false), etag)) {
/*      */         
/* 2949 */         status = (req.getMethod().equals("GET") || req.getMethod().equals("HEAD")) ? 304 : 412;
/*      */       } else {
/* 2951 */         force = true;
/*      */       }  
/* 2953 */     return force ? 200 : status;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int serveFile(File base, String context, Request req, Response resp) throws IOException {
/* 2977 */     String relativePath = req.getPath().substring(context.length());
/* 2978 */     File file = (new File(base, relativePath)).getCanonicalFile();
/* 2979 */     if (!file.exists() || file.isHidden() || file.getName().startsWith("."))
/* 2980 */       return 404; 
/* 2981 */     if (!file.canRead() || !file.getPath().startsWith(base.getPath()))
/* 2982 */       return 403; 
/* 2983 */     if (file.isDirectory())
/* 2984 */     { if (relativePath.endsWith("/")) {
/* 2985 */         if (!req.getVirtualHost().isAllowGeneratedIndex())
/* 2986 */           return 403; 
/* 2987 */         resp.send(200, createIndex(file, req.getPath()));
/*      */       } else {
/* 2989 */         resp.redirect(req.getBaseURL() + req.getPath() + "/", true);
/*      */       }  }
/* 2991 */     else { if (relativePath.endsWith("/")) {
/* 2992 */         return 404;
/*      */       }
/* 2994 */       serveFileContent(file, req, resp); }
/*      */     
/* 2996 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void serveFileContent(File file, Request req, Response resp) throws IOException {
/*      */     InputStream in;
/* 3010 */     long len = file.length();
/* 3011 */     long lastModified = file.lastModified();
/* 3012 */     String etag = "W/\"" + lastModified + "\"";
/* 3013 */     int status = 200;
/*      */     
/* 3015 */     long[] range = req.getRange(len);
/* 3016 */     if (range == null || len == 0L) {
/* 3017 */       status = getConditionalStatus(req, lastModified, etag);
/*      */     } else {
/* 3019 */       String ifRange = req.getHeaders().get("If-Range");
/* 3020 */       if (ifRange == null) {
/* 3021 */         if (range[0] >= len)
/* 3022 */         { status = 416; }
/*      */         else
/* 3024 */         { status = getConditionalStatus(req, lastModified, etag); } 
/* 3025 */       } else if (range[0] >= len) {
/*      */         
/* 3027 */         range = null;
/*      */       }
/* 3029 */       else if (!ifRange.startsWith("\"") && !ifRange.startsWith("W/")) {
/* 3030 */         Date date = req.getHeaders().getDate("If-Range");
/* 3031 */         if (date != null && lastModified > date.getTime())
/* 3032 */           range = null; 
/* 3033 */       } else if (!ifRange.equals(etag)) {
/* 3034 */         range = null;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 3039 */     Headers respHeaders = resp.getHeaders();
/* 3040 */     switch (status) {
/*      */       case 304:
/* 3042 */         respHeaders.add("ETag", etag);
/* 3043 */         respHeaders.add("Vary", "Accept-Encoding");
/* 3044 */         respHeaders.add("Last-Modified", formatDate(lastModified));
/* 3045 */         resp.sendHeaders(304);
/*      */         return;
/*      */       case 412:
/* 3048 */         resp.sendHeaders(412);
/*      */         return;
/*      */       case 416:
/* 3051 */         respHeaders.add("Content-Range", "bytes */" + len);
/* 3052 */         resp.sendHeaders(416);
/*      */         return;
/*      */       
/*      */       case 200:
/* 3056 */         resp.sendHeaders(200, len, lastModified, etag, 
/* 3057 */             getContentType(file.getName(), "application/octet-stream"), range);
/*      */         
/* 3059 */         in = new FileInputStream(file);
/*      */         try {
/* 3061 */           resp.sendBody(in, len, range);
/*      */         } finally {
/* 3063 */           in.close();
/*      */         } 
/*      */         return;
/*      */     } 
/* 3067 */     resp.sendHeaders(500);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String createIndex(File dir, String path) {
/* 3080 */     if (!path.endsWith("/")) {
/* 3081 */       path = path + "/";
/*      */     }
/* 3083 */     int w = 21;
/* 3084 */     for (String name : dir.list()) {
/* 3085 */       if (name.length() > w)
/* 3086 */         w = name.length(); 
/* 3087 */     }  w += 2;
/*      */     
/* 3089 */     Formatter f = new Formatter(Locale.US);
/* 3090 */     f.format("<!DOCTYPE html>%n<html><head><title>Index of %s</title></head>%n<body><h1>Index of %s</h1>%n<pre> Name%" + (w - 5) + "s Last modified      Size<hr>", new Object[] { path, path, "" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3095 */     if (path.length() > 1)
/* 3096 */       f.format(" <a href=\"%s/\">Parent Directory</a>%" + (w + 5) + "s-%n", new Object[] {
/* 3097 */             getParentPath(path), "" }); 
/* 3098 */     for (File file : dir.listFiles()) {
/*      */       try {
/* 3100 */         String name = file.getName() + (file.isDirectory() ? "/" : "");
/* 3101 */         String size = file.isDirectory() ? "- " : toSizeApproxString(file.length());
/*      */         
/* 3103 */         String link = (new URI(null, path + name, null)).toASCIIString();
/* 3104 */         if (!file.isHidden() && !name.startsWith("."))
/* 3105 */           f.format(" <a href=\"%s\">%s</a>%-" + (w - name.length()) + "s&#8206;%td-%<tb-%<tY %<tR%6s%n", new Object[] { link, name, "", 
/*      */                 
/* 3107 */                 Long.valueOf(file.lastModified()), size }); 
/* 3108 */       } catch (URISyntaxException uRISyntaxException) {}
/*      */     } 
/* 3110 */     f.format("</pre></body></html>", new Object[0]);
/* 3111 */     return f.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void main(String[] args) {
/*      */     try {
/* 3121 */       if (args.length == 0) {
/* 3122 */         System.err.printf("Usage: java [-options] %s <directory> [port]%nTo enable SSL: specify options -Djavax.net.ssl.keyStore, -Djavax.net.ssl.keyStorePassword, etc.%n", new Object[] { HTTPServer.class
/*      */               
/* 3124 */               .getName() });
/*      */         return;
/*      */       } 
/* 3127 */       File dir = new File(args[0]);
/* 3128 */       if (!dir.canRead())
/* 3129 */         throw new FileNotFoundException(dir.getAbsolutePath()); 
/* 3130 */       int port = (args.length < 2) ? 80 : (int)parseULong(args[1], 10);
/*      */       
/* 3132 */       for (File f : Arrays.<File>asList(new File[] { new File("/etc/mime.types"), new File(dir, ".mime.types") })) {
/* 3133 */         if (f.exists())
/* 3134 */           addContentTypes(new FileInputStream(f)); 
/* 3135 */       }  HTTPServer server = new HTTPServer(port);
/* 3136 */       if (System.getProperty("javax.net.ssl.keyStore") != null)
/* 3137 */         server.setServerSocketFactory(SSLServerSocketFactory.getDefault()); 
/* 3138 */       VirtualHost host = server.getVirtualHost(null);
/* 3139 */       host.setAllowGeneratedIndex(true);
/* 3140 */       host.addContext("/", new FileContextHandler(dir), new String[0]);
/* 3141 */       host.addContext("/api/time", new ContextHandler() {
/*      */             public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
/* 3143 */               long now = System.currentTimeMillis();
/* 3144 */               resp.getHeaders().add("Content-Type", "text/plain");
/* 3145 */               resp.send(200, String.format("%tF %<tT", new Object[] { Long.valueOf(now) }));
/* 3146 */               return 0;
/*      */             }
/*      */           }new String[0]);
/* 3149 */       server.start();
/* 3150 */       System.out.println("HTTPServer is listening on port " + port);
/* 3151 */     } catch (Exception e) {
/* 3152 */       System.err.println("error: " + e);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static interface ContextHandler {
/*      */     int serve(HTTPServer.Request param1Request, HTTPServer.Response param1Response) throws IOException;
/*      */   }
/*      */   
/*      */   @Retention(RetentionPolicy.RUNTIME)
/*      */   @Target({ElementType.METHOD})
/*      */   public static @interface Context {
/*      */     String value();
/*      */     
/*      */     String[] methods() default {"GET"};
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\jlhttp\HTTPServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */