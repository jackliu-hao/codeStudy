package org.noear.solon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.FileNameMap;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.PropsConverter;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.core.wrap.ClassWrap;

@Note("内部专用工具（外部项目不建议使用，随时可能会变动）")
public class Utils {
   public static final FileNameMap mimeMap = URLConnection.getFileNameMap();
   public static final ExecutorService pools = Executors.newCachedThreadPool();
   public static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
   private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static boolean ping(String address) throws Exception {
      if (address.contains(":")) {
         String host = address.split(":")[0];
         int port = Integer.parseInt(address.split(":")[1]);

         try {
            Socket socket = new Socket();
            Throwable var4 = null;

            boolean var6;
            try {
               SocketAddress addr = new InetSocketAddress(host, port);
               socket.connect(addr, 3000);
               var6 = true;
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (socket != null) {
                  if (var4 != null) {
                     try {
                        socket.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     socket.close();
                  }
               }

            }

            return var6;
         } catch (IOException var18) {
            return false;
         }
      } else {
         return InetAddress.getByName(address).isReachable(3000);
      }
   }

   public static String mime(String fileName) {
      return mimeMap.getContentTypeFor(fileName);
   }

   public static String annoAlias(String v1, String v2) {
      return isEmpty(v1) ? v2 : v1;
   }

   public static String guid() {
      return UUID.randomUUID().toString().replaceAll("-", "");
   }

   public static String md5(String str) {
      try {
         byte[] btInput = str.getBytes("UTF-8");
         MessageDigest mdInst = MessageDigest.getInstance("MD5");
         mdInst.update(btInput);
         byte[] md = mdInst.digest();
         int j = md.length;
         char[] chars = new char[j * 2];
         int k = 0;

         for(int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            chars[k++] = HEX_DIGITS[byte0 >>> 4 & 15];
            chars[k++] = HEX_DIGITS[byte0 & 15];
         }

         return new String(chars);
      } catch (Exception var9) {
         throw new RuntimeException(var9);
      }
   }

   public static String throwableToString(Throwable ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      return sw.toString();
   }

   /** @deprecated */
   @Deprecated
   public static RuntimeException throwableWrap(Throwable ex) {
      if (ex instanceof RuntimeException) {
         return (RuntimeException)ex;
      } else if (ex instanceof Error) {
         throw (Error)ex;
      } else {
         return new RuntimeException(ex);
      }
   }

   public static Throwable throwableUnwrap(Throwable ex) {
      Throwable th = ex;

      while(true) {
         while(true) {
            while(th instanceof InvocationTargetException) {
               th = ((InvocationTargetException)th).getTargetException();
            }

            if (!(th instanceof UndeclaredThrowableException)) {
               if (th.getClass() != RuntimeException.class || th.getCause() == null) {
                  return th;
               }

               th = th.getCause();
            } else {
               th = ((UndeclaredThrowableException)th).getUndeclaredThrowable();
            }
         }
      }
   }

   public static boolean throwableHas(Throwable ex, Class<? extends Throwable> clz) {
      Throwable th = ex;

      while(!clz.isAssignableFrom(th.getClass())) {
         if (th instanceof InvocationTargetException) {
            th = ((InvocationTargetException)th).getTargetException();
         } else if (th instanceof UndeclaredThrowableException) {
            th = ((UndeclaredThrowableException)th).getUndeclaredThrowable();
         } else {
            if (th.getCause() == null) {
               return false;
            }

            th = th.getCause();
         }
      }

      return true;
   }

   public static String trimDuplicates(String str, char c) {
      int start = 0;

      while((start = str.indexOf(c, start) + 1) > 0) {
         int end;
         for(end = start; end < str.length() && str.charAt(end) == c; ++end) {
         }

         if (end > start) {
            str = str.substring(0, start) + str.substring(end);
         }
      }

      return str;
   }

   public static boolean isEmpty(String s) {
      return s == null || s.length() == 0;
   }

   public static boolean isNotEmpty(String s) {
      return !isEmpty(s);
   }

   public static boolean isBlank(String s) {
      if (isEmpty(s)) {
         return true;
      } else {
         int i = 0;

         for(int l = s.length(); i < l; ++i) {
            if (!isWhitespace(s.codePointAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNotBlank(String s) {
      return !isBlank(s);
   }

   public static boolean isWhitespace(int c) {
      return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
   }

   public static <T> T firstOrNull(List<T> list) {
      return list.size() > 0 ? list.get(0) : null;
   }

   public static Locale toLocale(String lang) {
      if (lang == null) {
         return null;
      } else {
         String[] ss = lang.split("_|-");
         if (ss.length >= 3) {
            return ss[1].length() > 2 ? new Locale(ss[0], ss[2], ss[1]) : new Locale(ss[0], ss[1], ss[2]);
         } else if (ss.length == 2) {
            return ss[1].length() > 2 ? new Locale(ss[0], "", ss[1]) : new Locale(ss[0], ss[1]);
         } else {
            return new Locale(ss[0]);
         }
      }
   }

   public static Class<?> loadClass(String className) {
      try {
         return loadClass((ClassLoader)null, className);
      } catch (Throwable var2) {
         return null;
      }
   }

   public static Class<?> loadClass(ClassLoader classLoader, String className) {
      try {
         return classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
      } catch (Throwable var3) {
         return null;
      }
   }

   public static <T> T newInstance(String className) {
      return newInstance((String)className, (Properties)null);
   }

   public static <T> T newInstance(String className, Properties prop) {
      return newInstance(JarClassLoader.global(), className, prop);
   }

   public static <T> T newInstance(ClassLoader classLoader, String className) {
      return newInstance(classLoader, className, (Properties)null);
   }

   public static <T> T newInstance(ClassLoader classLoader, String className, Properties prop) {
      try {
         Class<?> clz = loadClass(classLoader, className);
         return clz == null ? null : newInstance(clz, prop);
      } catch (Exception var4) {
         return null;
      }
   }

   public static <T> T newInstance(Class<?> clz, Properties prop) throws Exception {
      if (prop == null) {
         return clz.newInstance();
      } else {
         Constructor<?> cos = clz.getConstructor(Properties.class);
         return cos == null ? null : cos.newInstance(prop);
      }
   }

   public static Enumeration<URL> getResources(String name) throws IOException {
      return getResources(JarClassLoader.global(), name);
   }

   public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
      return classLoader.getResources(name);
   }

   public static URL getResource(String name) {
      return getResource(JarClassLoader.global(), name);
   }

   public static URL getResource(ClassLoader classLoader, String name) {
      return classLoader.getResource(name);
   }

   public static String getResourceAsString(String name) throws IOException {
      return getResourceAsString(JarClassLoader.global(), name, Solon.encoding());
   }

   public static String getResourceAsString(String name, String charset) throws IOException {
      return getResourceAsString(JarClassLoader.global(), name, charset);
   }

   public static String getResourceAsString(ClassLoader classLoader, String name, String charset) throws IOException {
      URL url = getResource(classLoader, name);
      return url != null ? transferToString(url.openStream(), charset) : null;
   }

   public static String transferToString(InputStream ins, String charset) throws IOException {
      if (ins == null) {
         return null;
      } else {
         ByteArrayOutputStream outs = (ByteArrayOutputStream)transferTo(ins, new ByteArrayOutputStream());
         return charset == null ? outs.toString() : outs.toString(charset);
      }
   }

   public static byte[] transferToBytes(InputStream ins) throws IOException {
      return ins == null ? null : ((ByteArrayOutputStream)transferTo(ins, new ByteArrayOutputStream())).toByteArray();
   }

   public static <T extends OutputStream> T transferTo(InputStream ins, T out) throws IOException {
      if (ins != null && out != null) {
         int len = false;
         byte[] buf = new byte[512];

         int len;
         while((len = ins.read(buf)) != -1) {
            out.write(buf, 0, len);
         }

         return out;
      } else {
         return null;
      }
   }

   public static Properties loadProperties(URL url) {
      if (url == null) {
         return null;
      } else {
         try {
            return PropsLoader.global().load(url);
         } catch (RuntimeException var2) {
            throw var2;
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static Properties loadProperties(String url) {
      return loadProperties(getResource(url));
   }

   public static Properties buildProperties(String txt) {
      try {
         return PropsLoader.global().build(txt);
      } catch (RuntimeException var2) {
         throw var2;
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static <T> T injectProperties(T obj, Properties propS) {
      return PropsConverter.global().convert(propS, obj, (Class)null, (Type)null);
   }

   public static String getFullStackTrace(Throwable ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw, true));
      return sw.getBuffer().toString();
   }

   public static String buildExt(String extend, boolean autoMake) {
      if (extend == null) {
         return null;
      } else if (extend.contains("/")) {
         return extend;
      } else {
         PrintUtil.info("Extend org: " + extend);
         URL temp = getResource("");
         if (temp == null) {
            return null;
         } else {
            String uri = temp.toString();
            PrintUtil.info("Resource root: " + uri);
            int idx;
            if (uri.startsWith("file:/")) {
               idx = uri.lastIndexOf("/target/");
               if (idx > 0) {
                  idx += 8;
               } else {
                  idx = uri.lastIndexOf("/", idx) + 1;
               }

               uri = uri.substring(5, idx);
            } else {
               idx = uri.indexOf("jar!/");
               idx = uri.lastIndexOf("/", idx) + 1;
               uri = uri.substring(9, idx);
            }

            uri = uri + extend + "/";
            File dir = new File(uri);
            if (!dir.exists()) {
               if (!autoMake) {
                  return null;
               }

               dir.mkdir();
            }

            return uri;
         }
      }
   }

   public static void bindTo(Map<String, String> source, Object target) {
      bindTo((k) -> {
         return (String)source.get(k);
      }, target);
   }

   public static void bindTo(Properties source, Object target) {
      injectProperties(target, source);
   }

   public static void bindTo(Function<String, String> source, Object target) {
      if (target != null) {
         ClassWrap.get(target.getClass()).fill(target, source);
      }
   }

   public static ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
   }

   public static ClassLoader getClassLoader() {
      ClassLoader classLoader = getContextClassLoader();
      if (classLoader == null) {
         classLoader = Utils.class.getClassLoader();
         if (null == classLoader) {
            classLoader = ClassLoader.getSystemClassLoader();
         }
      }

      return classLoader;
   }
}
