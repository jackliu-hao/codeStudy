/*     */ package org.noear.solon;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URL;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.function.Function;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.PropsLoader;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ 
/*     */ @Note("内部专用工具（外部项目不建议使用，随时可能会变动）")
/*     */ public class Utils {
/*  28 */   public static final FileNameMap mimeMap = URLConnection.getFileNameMap();
/*  29 */   public static final ExecutorService pools = Executors.newCachedThreadPool();
/*  30 */   public static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
/*     */   
/*  32 */   private static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean ping(String address) throws Exception {
/*  41 */     if (address.contains(":")) {
/*  42 */       String host = address.split(":")[0];
/*  43 */       int port = Integer.parseInt(address.split(":")[1]);
/*     */       
/*  45 */       try (Socket socket = new Socket()) {
/*  46 */         SocketAddress addr = new InetSocketAddress(host, port);
/*  47 */         socket.connect(addr, 3000);
/*  48 */         return true;
/*  49 */       } catch (IOException e) {
/*  50 */         return false;
/*     */       } 
/*     */     } 
/*  53 */     return InetAddress.getByName(address).isReachable(3000);
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
/*     */   public static String mime(String fileName) {
/*  65 */     return mimeMap.getContentTypeFor(fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String annoAlias(String v1, String v2) {
/*  75 */     if (isEmpty(v1)) {
/*  76 */       return v2;
/*     */     }
/*  78 */     return v1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String guid() {
/*  86 */     return UUID.randomUUID().toString().replaceAll("-", "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String md5(String str) {
/*     */     try {
/*  96 */       byte[] btInput = str.getBytes("UTF-8");
/*     */       
/*  98 */       MessageDigest mdInst = MessageDigest.getInstance("MD5");
/*  99 */       mdInst.update(btInput);
/* 100 */       byte[] md = mdInst.digest();
/* 101 */       int j = md.length;
/* 102 */       char[] chars = new char[j * 2];
/* 103 */       int k = 0;
/*     */       
/* 105 */       for (int i = 0; i < j; i++) {
/* 106 */         byte byte0 = md[i];
/* 107 */         chars[k++] = HEX_DIGITS[byte0 >>> 4 & 0xF];
/* 108 */         chars[k++] = HEX_DIGITS[byte0 & 0xF];
/*     */       } 
/*     */       
/* 111 */       return new String(chars);
/* 112 */     } catch (Exception ex) {
/* 113 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String throwableToString(Throwable ex) {
/* 123 */     StringWriter sw = new StringWriter();
/* 124 */     ex.printStackTrace(new PrintWriter(sw));
/*     */     
/* 126 */     return sw.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static RuntimeException throwableWrap(Throwable ex) {
/* 136 */     if (ex instanceof RuntimeException)
/* 137 */       return (RuntimeException)ex; 
/* 138 */     if (ex instanceof Error) {
/* 139 */       throw (Error)ex;
/*     */     }
/* 141 */     return new RuntimeException(ex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable throwableUnwrap(Throwable ex) {
/* 151 */     Throwable th = ex;
/*     */     
/*     */     while (true) {
/* 154 */       while (th instanceof InvocationTargetException)
/* 155 */         th = ((InvocationTargetException)th).getTargetException(); 
/* 156 */       if (th instanceof UndeclaredThrowableException) {
/* 157 */         th = ((UndeclaredThrowableException)th).getUndeclaredThrowable(); continue;
/* 158 */       }  if (th.getClass() == RuntimeException.class && 
/* 159 */         th.getCause() != null) {
/* 160 */         th = th.getCause();
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*     */       break;
/*     */     } 
/*     */     
/* 169 */     return th;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean throwableHas(Throwable ex, Class<? extends Throwable> clz) {
/* 179 */     Throwable th = ex;
/*     */     
/*     */     while (true) {
/* 182 */       if (clz.isAssignableFrom(th.getClass())) {
/* 183 */         return true;
/*     */       }
/*     */       
/* 186 */       if (th instanceof InvocationTargetException) {
/* 187 */         th = ((InvocationTargetException)th).getTargetException(); continue;
/* 188 */       }  if (th instanceof UndeclaredThrowableException) {
/* 189 */         th = ((UndeclaredThrowableException)th).getUndeclaredThrowable(); continue;
/* 190 */       }  if (th.getCause() != null) {
/* 191 */         th = th.getCause();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String trimDuplicates(String str, char c) {
/* 204 */     int start = 0;
/* 205 */     while ((start = str.indexOf(c, start) + 1) > 0) {
/*     */       int end;
/* 207 */       for (end = start; end < str.length() && str.charAt(end) == c; end++);
/* 208 */       if (end > start)
/* 209 */         str = str.substring(0, start) + str.substring(end); 
/*     */     } 
/* 211 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEmpty(String s) {
/* 220 */     return (s == null || s.length() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNotEmpty(String s) {
/* 229 */     return !isEmpty(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBlank(String s) {
/* 239 */     if (isEmpty(s)) {
/* 240 */       return true;
/*     */     }
/* 242 */     for (int i = 0, l = s.length(); i < l; i++) {
/* 243 */       if (!isWhitespace(s.codePointAt(i))) {
/* 244 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 248 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNotBlank(String s) {
/* 258 */     return !isBlank(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isWhitespace(int c) {
/* 267 */     return (c == 32 || c == 9 || c == 10 || c == 12 || c == 13);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T firstOrNull(List<T> list) {
/* 275 */     if (list.size() > 0) {
/* 276 */       return list.get(0);
/*     */     }
/* 278 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Locale toLocale(String lang) {
/* 283 */     if (lang == null) {
/* 284 */       return null;
/*     */     }
/*     */     
/* 287 */     String[] ss = lang.split("_|-");
/*     */     
/* 289 */     if (ss.length >= 3) {
/* 290 */       if (ss[1].length() > 2) {
/* 291 */         return new Locale(ss[0], ss[2], ss[1]);
/*     */       }
/* 293 */       return new Locale(ss[0], ss[1], ss[2]);
/*     */     } 
/* 295 */     if (ss.length == 2) {
/* 296 */       if (ss[1].length() > 2)
/*     */       {
/* 298 */         return new Locale(ss[0], "", ss[1]);
/*     */       }
/* 300 */       return new Locale(ss[0], ss[1]);
/*     */     } 
/*     */     
/* 303 */     return new Locale(ss[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> loadClass(String className) {
/*     */     try {
/* 314 */       return loadClass(null, className);
/* 315 */     } catch (Throwable ex) {
/* 316 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> loadClass(ClassLoader classLoader, String className) {
/*     */     try {
/* 328 */       if (classLoader == null) {
/* 329 */         return Class.forName(className);
/*     */       }
/* 331 */       return classLoader.loadClass(className);
/*     */     }
/* 333 */     catch (Throwable ex) {
/* 334 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T newInstance(String className) {
/* 344 */     return newInstance(className, (Properties)null);
/*     */   }
/*     */   
/*     */   public static <T> T newInstance(String className, Properties prop) {
/* 348 */     return newInstance((ClassLoader)JarClassLoader.global(), className, prop);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T newInstance(ClassLoader classLoader, String className) {
/* 358 */     return newInstance(classLoader, className, null);
/*     */   }
/*     */   
/*     */   public static <T> T newInstance(ClassLoader classLoader, String className, Properties prop) {
/*     */     try {
/* 363 */       Class<?> clz = loadClass(classLoader, className);
/* 364 */       if (clz == null) {
/* 365 */         return null;
/*     */       }
/* 367 */       return newInstance(clz, prop);
/*     */     }
/* 369 */     catch (Exception ex) {
/* 370 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static <T> T newInstance(Class<?> clz, Properties prop) throws Exception {
/* 375 */     if (prop == null) {
/* 376 */       return (T)clz.newInstance();
/*     */     }
/* 378 */     Constructor<?> cos = clz.getConstructor(new Class[] { Properties.class });
/* 379 */     if (cos == null) {
/* 380 */       return null;
/*     */     }
/* 382 */     return (T)cos.newInstance(new Object[] { prop });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Enumeration<URL> getResources(String name) throws IOException {
/* 393 */     return getResources((ClassLoader)JarClassLoader.global(), name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
/* 403 */     return classLoader.getResources(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getResource(String name) {
/* 412 */     return getResource((ClassLoader)JarClassLoader.global(), name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static URL getResource(ClassLoader classLoader, String name) {
/* 422 */     return classLoader.getResource(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getResourceAsString(String name) throws IOException {
/* 431 */     return getResourceAsString((ClassLoader)JarClassLoader.global(), name, Solon.encoding());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getResourceAsString(String name, String charset) throws IOException {
/* 441 */     return getResourceAsString((ClassLoader)JarClassLoader.global(), name, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getResourceAsString(ClassLoader classLoader, String name, String charset) throws IOException {
/* 452 */     URL url = getResource(classLoader, name);
/* 453 */     if (url != null) {
/* 454 */       return transferToString(url.openStream(), charset);
/*     */     }
/* 456 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String transferToString(InputStream ins, String charset) throws IOException {
/* 467 */     if (ins == null) {
/* 468 */       return null;
/*     */     }
/*     */     
/* 471 */     ByteArrayOutputStream outs = transferTo(ins, new ByteArrayOutputStream());
/*     */     
/* 473 */     if (charset == null) {
/* 474 */       return outs.toString();
/*     */     }
/* 476 */     return outs.toString(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] transferToBytes(InputStream ins) throws IOException {
/* 486 */     if (ins == null) {
/* 487 */       return null;
/*     */     }
/*     */     
/* 490 */     return ((ByteArrayOutputStream)transferTo(ins, new ByteArrayOutputStream())).toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends java.io.OutputStream> T transferTo(InputStream ins, T out) throws IOException {
/* 500 */     if (ins == null || out == null) {
/* 501 */       return null;
/*     */     }
/*     */     
/* 504 */     int len = 0;
/* 505 */     byte[] buf = new byte[512];
/* 506 */     while ((len = ins.read(buf)) != -1) {
/* 507 */       out.write(buf, 0, len);
/*     */     }
/*     */     
/* 510 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties loadProperties(URL url) {
/* 520 */     if (url == null) {
/* 521 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 525 */       return PropsLoader.global().load(url);
/* 526 */     } catch (RuntimeException ex) {
/* 527 */       throw ex;
/* 528 */     } catch (Throwable ex) {
/* 529 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties loadProperties(String url) {
/* 539 */     return loadProperties(getResource(url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties buildProperties(String txt) {
/*     */     try {
/* 549 */       return PropsLoader.global().build(txt);
/* 550 */     } catch (RuntimeException ex) {
/* 551 */       throw ex;
/* 552 */     } catch (Throwable ex) {
/* 553 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T injectProperties(T obj, Properties propS) {
/* 564 */     return (T)PropsConverter.global().convert(propS, obj, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getFullStackTrace(Throwable ex) {
/* 573 */     StringWriter sw = new StringWriter();
/* 574 */     ex.printStackTrace(new PrintWriter(sw, true));
/* 575 */     return sw.getBuffer().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String buildExt(String extend, boolean autoMake) {
/* 586 */     if (extend == null) {
/* 587 */       return null;
/*     */     }
/*     */     
/* 590 */     if (extend.contains("/"))
/*     */     {
/* 592 */       return extend;
/*     */     }
/*     */     
/* 595 */     PrintUtil.info("Extend org: " + extend);
/*     */     
/* 597 */     URL temp = getResource("");
/*     */     
/* 599 */     if (temp == null) {
/* 600 */       return null;
/*     */     }
/* 602 */     String uri = temp.toString();
/*     */     
/* 604 */     PrintUtil.info("Resource root: " + uri);
/*     */     
/* 606 */     if (uri.startsWith("file:/")) {
/* 607 */       int idx = uri.lastIndexOf("/target/");
/* 608 */       if (idx > 0) {
/* 609 */         idx += 8;
/*     */       } else {
/* 611 */         idx = uri.lastIndexOf("/", idx) + 1;
/*     */       } 
/*     */       
/* 614 */       uri = uri.substring(5, idx);
/*     */     } else {
/* 616 */       int idx = uri.indexOf("jar!/");
/* 617 */       idx = uri.lastIndexOf("/", idx) + 1;
/*     */       
/* 619 */       uri = uri.substring(9, idx);
/*     */     } 
/*     */     
/* 622 */     uri = uri + extend + "/";
/* 623 */     File dir = new File(uri);
/*     */     
/* 625 */     if (!dir.exists()) {
/* 626 */       if (autoMake) {
/* 627 */         dir.mkdir();
/*     */       } else {
/* 629 */         return null;
/*     */       } 
/*     */     }
/*     */     
/* 633 */     return uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void bindTo(Map<String, String> source, Object target) {
/* 641 */     bindTo(k -> (String)source.get(k), target);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void bindTo(Properties source, Object target) {
/* 648 */     injectProperties(target, source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void bindTo(Function<String, String> source, Object target) {
/* 655 */     if (target == null) {
/*     */       return;
/*     */     }
/*     */     
/* 659 */     ClassWrap.get(target.getClass()).fill(target, source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getContextClassLoader() {
/* 666 */     return Thread.currentThread().getContextClassLoader();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getClassLoader() {
/* 673 */     ClassLoader classLoader = getContextClassLoader();
/* 674 */     if (classLoader == null) {
/* 675 */       classLoader = Utils.class.getClassLoader();
/* 676 */       if (null == classLoader) {
/* 677 */         classLoader = ClassLoader.getSystemClassLoader();
/*     */       }
/*     */     } 
/*     */     
/* 681 */     return classLoader;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */