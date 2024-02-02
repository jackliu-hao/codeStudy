package com.sun.jna;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeLibrary {
   private static final Logger LOG = Logger.getLogger(NativeLibrary.class.getName());
   private static final Level DEBUG_LOAD_LEVEL;
   private long handle;
   private final String libraryName;
   private final String libraryPath;
   private final Map<String, Function> functions = new HashMap();
   final int callFlags;
   private String encoding;
   final Map<String, ?> options;
   private static final Map<String, Reference<NativeLibrary>> libraries;
   private static final Map<String, List<String>> searchPaths;
   private static final LinkedHashSet<String> librarySearchPath;
   private static final int DEFAULT_OPEN_OPTIONS = -1;
   private static Method addSuppressedMethod;

   private static String functionKey(String name, int flags, String encoding) {
      return name + "|" + flags + "|" + encoding;
   }

   private NativeLibrary(String libraryName, String libraryPath, long handle, Map<String, ?> options) {
      this.libraryName = this.getLibraryName(libraryName);
      this.libraryPath = libraryPath;
      this.handle = handle;
      Object option = options.get("calling-convention");
      int callingConvention = option instanceof Number ? ((Number)option).intValue() : 0;
      this.callFlags = callingConvention;
      this.options = options;
      this.encoding = (String)options.get("string-encoding");
      if (this.encoding == null) {
         this.encoding = Native.getDefaultStringEncoding();
      }

      if (Platform.isWindows() && "kernel32".equals(this.libraryName.toLowerCase())) {
         synchronized(this.functions) {
            Function f = new Function(this, "GetLastError", 63, this.encoding) {
               Object invoke(Object[] args, Class<?> returnType, boolean b, int fixedArgs) {
                  return Native.getLastError();
               }

               Object invoke(Method invokingMethod, Class<?>[] paramTypes, Class<?> returnType, Object[] inArgs, Map<String, ?> options) {
                  return Native.getLastError();
               }
            };
            this.functions.put(functionKey("GetLastError", this.callFlags, this.encoding), f);
         }
      }

   }

   private static int openFlags(Map<String, ?> options) {
      Object opt = options.get("open-flags");
      return opt instanceof Number ? ((Number)opt).intValue() : -1;
   }

   private static NativeLibrary loadLibrary(String libraryName, Map<String, ?> options) {
      LOG.log(DEBUG_LOAD_LEVEL, "Looking for library '" + libraryName + "'");
      List<Throwable> exceptions = new ArrayList();
      boolean isAbsolutePath = (new File(libraryName)).isAbsolute();
      LinkedHashSet<String> searchPath = new LinkedHashSet();
      int openFlags = openFlags(options);
      List<String> customPaths = (List)searchPaths.get(libraryName);
      if (customPaths != null) {
         synchronized(customPaths) {
            searchPath.addAll(customPaths);
         }
      }

      String webstartPath = Native.getWebStartLibraryPath(libraryName);
      if (webstartPath != null) {
         LOG.log(DEBUG_LOAD_LEVEL, "Adding web start path " + webstartPath);
         searchPath.add(webstartPath);
      }

      LOG.log(DEBUG_LOAD_LEVEL, "Adding paths from jna.library.path: " + System.getProperty("jna.library.path"));
      searchPath.addAll(initPaths("jna.library.path"));
      String libraryPath = findLibraryPath(libraryName, searchPath);
      long handle = 0L;

      try {
         LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
         handle = Native.open(libraryPath, openFlags);
      } catch (UnsatisfiedLinkError var30) {
         LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var30.getMessage());
         LOG.log(DEBUG_LOAD_LEVEL, "Adding system paths: " + librarySearchPath);
         exceptions.add(var30);
         searchPath.addAll(librarySearchPath);
      }

      try {
         if (handle == 0L) {
            libraryPath = findLibraryPath(libraryName, searchPath);
            LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);
            handle = Native.open(libraryPath, openFlags);
            if (handle == 0L) {
               throw new UnsatisfiedLinkError("Failed to load library '" + libraryName + "'");
            }
         }
      } catch (UnsatisfiedLinkError var35) {
         LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var35.getMessage());
         exceptions.add(var35);
         if (Platform.isAndroid()) {
            try {
               LOG.log(DEBUG_LOAD_LEVEL, "Preload (via System.loadLibrary) " + libraryName);
               System.loadLibrary(libraryName);
               handle = Native.open(libraryPath, openFlags);
            } catch (UnsatisfiedLinkError var29) {
               LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var29.getMessage());
               exceptions.add(var29);
            }
         } else if (!Platform.isLinux() && !Platform.isFreeBSD()) {
            if (Platform.isMac() && !libraryName.endsWith(".dylib")) {
               String[] var12 = matchFramework(libraryName);
               int var13 = var12.length;
               int var14 = 0;

               while(var14 < var13) {
                  String frameworkName = var12[var14];

                  try {
                     LOG.log(DEBUG_LOAD_LEVEL, "Trying " + frameworkName);
                     handle = Native.open(frameworkName, openFlags);
                     break;
                  } catch (UnsatisfiedLinkError var34) {
                     LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var34.getMessage());
                     exceptions.add(var34);
                     ++var14;
                  }
               }
            } else if (Platform.isWindows() && !isAbsolutePath) {
               LOG.log(DEBUG_LOAD_LEVEL, "Looking for lib- prefix");
               libraryPath = findLibraryPath("lib" + libraryName, searchPath);
               if (libraryPath != null) {
                  LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);

                  try {
                     handle = Native.open(libraryPath, openFlags);
                  } catch (UnsatisfiedLinkError var27) {
                     LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var27.getMessage());
                     exceptions.add(var27);
                  }
               }
            }
         } else {
            LOG.log(DEBUG_LOAD_LEVEL, "Looking for version variants");
            libraryPath = matchLibrary(libraryName, searchPath);
            if (libraryPath != null) {
               LOG.log(DEBUG_LOAD_LEVEL, "Trying " + libraryPath);

               try {
                  handle = Native.open(libraryPath, openFlags);
               } catch (UnsatisfiedLinkError var28) {
                  LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var28.getMessage());
                  exceptions.add(var28);
               }
            }
         }

         if (handle == 0L) {
            try {
               File embedded = Native.extractFromResourcePath(libraryName, (ClassLoader)options.get("classloader"));

               try {
                  handle = Native.open(embedded.getAbsolutePath(), openFlags);
                  libraryPath = embedded.getAbsolutePath();
               } finally {
                  if (Native.isUnpacked(embedded)) {
                     Native.deleteLibrary(embedded);
                  }

               }
            } catch (IOException var33) {
               LOG.log(DEBUG_LOAD_LEVEL, "Loading failed with message: " + var33.getMessage());
               exceptions.add(var33);
            }
         }

         if (handle == 0L) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to load library '");
            sb.append(libraryName);
            sb.append("':");
            Iterator var38 = exceptions.iterator();

            while(var38.hasNext()) {
               Throwable t = (Throwable)var38.next();
               sb.append("\n");
               sb.append(t.getMessage());
            }

            UnsatisfiedLinkError res = new UnsatisfiedLinkError(sb.toString());
            Iterator var41 = exceptions.iterator();

            while(var41.hasNext()) {
               Throwable t = (Throwable)var41.next();
               addSuppressedReflected(res, t);
            }

            throw res;
         }
      }

      LOG.log(DEBUG_LOAD_LEVEL, "Found library '" + libraryName + "' at " + libraryPath);
      return new NativeLibrary(libraryName, libraryPath, handle, options);
   }

   private static void addSuppressedReflected(Throwable target, Throwable suppressed) {
      if (addSuppressedMethod != null) {
         try {
            addSuppressedMethod.invoke(target, suppressed);
         } catch (IllegalAccessException var3) {
            throw new RuntimeException("Failed to call addSuppressedMethod", var3);
         } catch (IllegalArgumentException var4) {
            throw new RuntimeException("Failed to call addSuppressedMethod", var4);
         } catch (InvocationTargetException var5) {
            throw new RuntimeException("Failed to call addSuppressedMethod", var5);
         }
      }
   }

   static String[] matchFramework(String libraryName) {
      Set<String> paths = new LinkedHashSet();
      File framework = new File(libraryName);
      if (framework.isAbsolute()) {
         if (libraryName.contains(".framework")) {
            if (framework.exists()) {
               return new String[]{framework.getAbsolutePath()};
            }

            paths.add(framework.getAbsolutePath());
         } else {
            framework = new File(new File(framework.getParentFile(), framework.getName() + ".framework"), framework.getName());
            if (framework.exists()) {
               return new String[]{framework.getAbsolutePath()};
            }

            paths.add(framework.getAbsolutePath());
         }
      } else {
         String[] PREFIXES = new String[]{System.getProperty("user.home"), "", "/System"};
         String suffix = !libraryName.contains(".framework") ? libraryName + ".framework/" + libraryName : libraryName;
         String[] var5 = PREFIXES;
         int var6 = PREFIXES.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String prefix = var5[var7];
            framework = new File(prefix + "/Library/Frameworks/" + suffix);
            if (framework.exists()) {
               return new String[]{framework.getAbsolutePath()};
            }

            paths.add(framework.getAbsolutePath());
         }
      }

      return (String[])paths.toArray(new String[0]);
   }

   private String getLibraryName(String libraryName) {
      String simplified = libraryName;
      String BASE = "---";
      String template = mapSharedLibraryName("---");
      int prefixEnd = template.indexOf("---");
      if (prefixEnd > 0 && libraryName.startsWith(template.substring(0, prefixEnd))) {
         simplified = libraryName.substring(prefixEnd);
      }

      String suffix = template.substring(prefixEnd + "---".length());
      int suffixStart = simplified.indexOf(suffix);
      if (suffixStart != -1) {
         simplified = simplified.substring(0, suffixStart);
      }

      return simplified;
   }

   public static final NativeLibrary getInstance(String libraryName) {
      return getInstance(libraryName, Collections.emptyMap());
   }

   public static final NativeLibrary getInstance(String libraryName, ClassLoader classLoader) {
      return getInstance(libraryName, Collections.singletonMap("classloader", classLoader));
   }

   public static final NativeLibrary getInstance(String libraryName, Map<String, ?> libraryOptions) {
      Map<String, Object> options = new HashMap(libraryOptions);
      if (options.get("calling-convention") == null) {
         options.put("calling-convention", 0);
      }

      if ((Platform.isLinux() || Platform.isFreeBSD() || Platform.isAIX()) && Platform.C_LIBRARY_NAME.equals(libraryName)) {
         libraryName = null;
      }

      synchronized(libraries) {
         Reference<NativeLibrary> ref = (Reference)libraries.get(libraryName + options);
         NativeLibrary library = ref != null ? (NativeLibrary)ref.get() : null;
         if (library == null) {
            if (libraryName == null) {
               library = new NativeLibrary("<process>", (String)null, Native.open((String)null, openFlags(options)), options);
            } else {
               library = loadLibrary(libraryName, options);
            }

            Reference<NativeLibrary> ref = new WeakReference(library);
            libraries.put(library.getName() + options, ref);
            File file = library.getFile();
            if (file != null) {
               libraries.put(file.getAbsolutePath() + options, ref);
               libraries.put(file.getName() + options, ref);
            }
         }

         return library;
      }
   }

   public static final synchronized NativeLibrary getProcess() {
      return getInstance((String)null);
   }

   public static final synchronized NativeLibrary getProcess(Map<String, ?> options) {
      return getInstance((String)null, (Map)options);
   }

   public static final void addSearchPath(String libraryName, String path) {
      synchronized(searchPaths) {
         List<String> customPaths = (List)searchPaths.get(libraryName);
         if (customPaths == null) {
            customPaths = Collections.synchronizedList(new ArrayList());
            searchPaths.put(libraryName, customPaths);
         }

         customPaths.add(path);
      }
   }

   public Function getFunction(String functionName) {
      return this.getFunction(functionName, this.callFlags);
   }

   Function getFunction(String name, Method method) {
      FunctionMapper mapper = (FunctionMapper)this.options.get("function-mapper");
      if (mapper != null) {
         name = mapper.getFunctionName(this, method);
      }

      String prefix = System.getProperty("jna.profiler.prefix", "$$YJP$$");
      if (name.startsWith(prefix)) {
         name = name.substring(prefix.length());
      }

      int flags = this.callFlags;
      Class<?>[] etypes = method.getExceptionTypes();

      for(int i = 0; i < etypes.length; ++i) {
         if (LastErrorException.class.isAssignableFrom(etypes[i])) {
            flags |= 64;
         }
      }

      return this.getFunction(name, flags);
   }

   public Function getFunction(String functionName, int callFlags) {
      return this.getFunction(functionName, callFlags, this.encoding);
   }

   public Function getFunction(String functionName, int callFlags, String encoding) {
      if (functionName == null) {
         throw new NullPointerException("Function name may not be null");
      } else {
         synchronized(this.functions) {
            String key = functionKey(functionName, callFlags, encoding);
            Function function = (Function)this.functions.get(key);
            if (function == null) {
               function = new Function(this, functionName, callFlags, encoding);
               this.functions.put(key, function);
            }

            return function;
         }
      }
   }

   public Map<String, ?> getOptions() {
      return this.options;
   }

   public Pointer getGlobalVariableAddress(String symbolName) {
      try {
         return new Pointer(this.getSymbolAddress(symbolName));
      } catch (UnsatisfiedLinkError var3) {
         throw new UnsatisfiedLinkError("Error looking up '" + symbolName + "': " + var3.getMessage());
      }
   }

   long getSymbolAddress(String name) {
      if (this.handle == 0L) {
         throw new UnsatisfiedLinkError("Library has been unloaded");
      } else {
         return Native.findSymbol(this.handle, name);
      }
   }

   public String toString() {
      return "Native Library <" + this.libraryPath + "@" + this.handle + ">";
   }

   public String getName() {
      return this.libraryName;
   }

   public File getFile() {
      return this.libraryPath == null ? null : new File(this.libraryPath);
   }

   protected void finalize() {
      this.dispose();
   }

   static void disposeAll() {
      LinkedHashSet values;
      synchronized(libraries) {
         values = new LinkedHashSet(libraries.values());
      }

      Iterator var1 = values.iterator();

      while(var1.hasNext()) {
         Reference<NativeLibrary> ref = (Reference)var1.next();
         NativeLibrary lib = (NativeLibrary)ref.get();
         if (lib != null) {
            lib.dispose();
         }
      }

   }

   public void dispose() {
      Set<String> keys = new HashSet();
      synchronized(libraries) {
         Iterator var3 = libraries.entrySet().iterator();

         while(true) {
            if (!var3.hasNext()) {
               var3 = keys.iterator();

               while(var3.hasNext()) {
                  String k = (String)var3.next();
                  libraries.remove(k);
               }
               break;
            }

            Map.Entry<String, Reference<NativeLibrary>> e = (Map.Entry)var3.next();
            Reference<NativeLibrary> ref = (Reference)e.getValue();
            if (ref.get() == this) {
               keys.add(e.getKey());
            }
         }
      }

      synchronized(this) {
         if (this.handle != 0L) {
            Native.close(this.handle);
            this.handle = 0L;
         }

      }
   }

   private static List<String> initPaths(String key) {
      String value = System.getProperty(key, "");
      if ("".equals(value)) {
         return Collections.emptyList();
      } else {
         StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
         List<String> list = new ArrayList();

         while(st.hasMoreTokens()) {
            String path = st.nextToken();
            if (!"".equals(path)) {
               list.add(path);
            }
         }

         return list;
      }
   }

   private static String findLibraryPath(String libName, Collection<String> searchPath) {
      if ((new File(libName)).isAbsolute()) {
         return libName;
      } else {
         String name = mapSharedLibraryName(libName);
         Iterator var3 = searchPath.iterator();

         while(var3.hasNext()) {
            String path = (String)var3.next();
            File file = new File(path, name);
            if (file.exists()) {
               return file.getAbsolutePath();
            }

            if (Platform.isMac() && name.endsWith(".dylib")) {
               file = new File(path, name.substring(0, name.lastIndexOf(".dylib")) + ".jnilib");
               if (file.exists()) {
                  return file.getAbsolutePath();
               }
            }
         }

         return name;
      }
   }

   static String mapSharedLibraryName(String libName) {
      if (Platform.isMac()) {
         if (!libName.startsWith("lib") || !libName.endsWith(".dylib") && !libName.endsWith(".jnilib")) {
            String name = System.mapLibraryName(libName);
            return name.endsWith(".jnilib") ? name.substring(0, name.lastIndexOf(".jnilib")) + ".dylib" : name;
         } else {
            return libName;
         }
      } else {
         if (!Platform.isLinux() && !Platform.isFreeBSD()) {
            if (Platform.isAIX()) {
               if (libName.startsWith("lib")) {
                  return libName;
               }
            } else if (Platform.isWindows() && (libName.endsWith(".drv") || libName.endsWith(".dll") || libName.endsWith(".ocx"))) {
               return libName;
            }
         } else if (isVersionedName(libName) || libName.endsWith(".so")) {
            return libName;
         }

         return System.mapLibraryName(libName);
      }
   }

   private static boolean isVersionedName(String name) {
      if (name.startsWith("lib")) {
         int so = name.lastIndexOf(".so.");
         if (so != -1 && so + 4 < name.length()) {
            for(int i = so + 4; i < name.length(); ++i) {
               char ch = name.charAt(i);
               if (!Character.isDigit(ch) && ch != '.') {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   static String matchLibrary(final String libName, Collection<String> searchPath) {
      File lib = new File(libName);
      if (lib.isAbsolute()) {
         searchPath = Arrays.asList(lib.getParent());
      }

      FilenameFilter filter = new FilenameFilter() {
         public boolean accept(File dir, String filename) {
            return (filename.startsWith("lib" + libName + ".so") || filename.startsWith(libName + ".so") && libName.startsWith("lib")) && NativeLibrary.isVersionedName(filename);
         }
      };
      Collection<File> matches = new LinkedList();
      Iterator var5 = ((Collection)searchPath).iterator();

      while(var5.hasNext()) {
         String path = (String)var5.next();
         File[] files = (new File(path)).listFiles(filter);
         if (files != null && files.length > 0) {
            matches.addAll(Arrays.asList(files));
         }
      }

      double bestVersion = -1.0;
      String bestMatch = null;
      Iterator var8 = matches.iterator();

      while(var8.hasNext()) {
         File f = (File)var8.next();
         String path = f.getAbsolutePath();
         String ver = path.substring(path.lastIndexOf(".so.") + 4);
         double version = parseVersion(ver);
         if (version > bestVersion) {
            bestVersion = version;
            bestMatch = path;
         }
      }

      return bestMatch;
   }

   static double parseVersion(String ver) {
      double v = 0.0;
      double divisor = 1.0;

      for(int dot = ver.indexOf("."); ver != null; divisor *= 100.0) {
         String num;
         if (dot != -1) {
            num = ver.substring(0, dot);
            ver = ver.substring(dot + 1);
            dot = ver.indexOf(".");
         } else {
            num = ver;
            ver = null;
         }

         try {
            v += (double)Integer.parseInt(num) / divisor;
         } catch (NumberFormatException var8) {
            return 0.0;
         }
      }

      return v;
   }

   private static String getMultiArchPath() {
      String cpu = Platform.ARCH;
      String kernel = Platform.iskFreeBSD() ? "-kfreebsd" : (Platform.isGNU() ? "" : "-linux");
      String libc = "-gnu";
      if (Platform.isIntel()) {
         cpu = Platform.is64Bit() ? "x86_64" : "i386";
      } else if (Platform.isPPC()) {
         cpu = Platform.is64Bit() ? "powerpc64" : "powerpc";
      } else if (Platform.isARM()) {
         cpu = "arm";
         libc = "-gnueabi";
      } else if (Platform.ARCH.equals("mips64el")) {
         libc = "-gnuabi64";
      }

      return cpu + kernel + libc;
   }

   private static ArrayList<String> getLinuxLdPaths() {
      ArrayList<String> ldPaths = new ArrayList();
      Process process = null;
      BufferedReader reader = null;

      try {
         process = Runtime.getRuntime().exec("/sbin/ldconfig -p");
         reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

         String buffer;
         while((buffer = reader.readLine()) != null) {
            int startPath = buffer.indexOf(" => ");
            int endPath = buffer.lastIndexOf(47);
            if (startPath != -1 && endPath != -1 && startPath < endPath) {
               String path = buffer.substring(startPath + 4, endPath);
               if (!ldPaths.contains(path)) {
                  ldPaths.add(path);
               }
            }
         }
      } catch (Exception var19) {
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (IOException var18) {
            }
         }

         if (process != null) {
            try {
               process.waitFor();
            } catch (InterruptedException var17) {
            }
         }

      }

      return ldPaths;
   }

   static {
      DEBUG_LOAD_LEVEL = Native.DEBUG_LOAD ? Level.INFO : Level.FINE;
      libraries = new HashMap();
      searchPaths = Collections.synchronizedMap(new HashMap());
      librarySearchPath = new LinkedHashSet();
      if (Native.POINTER_SIZE == 0) {
         throw new Error("Native library not initialized");
      } else {
         addSuppressedMethod = null;

         try {
            addSuppressedMethod = Throwable.class.getMethod("addSuppressed", Throwable.class);
         } catch (NoSuchMethodException var8) {
         } catch (SecurityException var9) {
            Logger.getLogger(NativeLibrary.class.getName()).log(Level.SEVERE, "Failed to initialize 'addSuppressed' method", var9);
         }

         String webstartPath = Native.getWebStartLibraryPath("jnidispatch");
         if (webstartPath != null) {
            librarySearchPath.add(webstartPath);
         }

         if (System.getProperty("jna.platform.library.path") == null && !Platform.isWindows()) {
            String platformPath = "";
            String sep = "";
            String archPath = "";
            if (Platform.isLinux() || Platform.isSolaris() || Platform.isFreeBSD() || Platform.iskFreeBSD()) {
               archPath = (Platform.isSolaris() ? "/" : "") + Native.POINTER_SIZE * 8;
            }

            String[] paths = new String[]{"/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            if (Platform.isLinux() || Platform.iskFreeBSD() || Platform.isGNU()) {
               String multiArchPath = getMultiArchPath();
               paths = new String[]{"/usr/lib/" + multiArchPath, "/lib/" + multiArchPath, "/usr/lib" + archPath, "/lib" + archPath, "/usr/lib", "/lib"};
            }

            if (Platform.isLinux()) {
               ArrayList<String> ldPaths = getLinuxLdPaths();

               for(int i = paths.length - 1; 0 <= i; --i) {
                  int found = ldPaths.indexOf(paths[i]);
                  if (found != -1) {
                     ldPaths.remove(found);
                  }

                  ldPaths.add(0, paths[i]);
               }

               paths = (String[])ldPaths.toArray(new String[0]);
            }

            for(int i = 0; i < paths.length; ++i) {
               File dir = new File(paths[i]);
               if (dir.exists() && dir.isDirectory()) {
                  platformPath = platformPath + sep + paths[i];
                  sep = File.pathSeparator;
               }
            }

            if (!"".equals(platformPath)) {
               System.setProperty("jna.platform.library.path", platformPath);
            }
         }

         librarySearchPath.addAll(initPaths("jna.platform.library.path"));
      }
   }
}
