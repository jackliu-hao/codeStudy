/*      */ package com.sun.jna;
/*      */ 
/*      */ import java.awt.Component;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Window;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FilenameFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.IllegalCharsetNameException;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.WeakHashMap;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Native
/*      */   implements Version
/*      */ {
/*  114 */   private static final Logger LOG = Logger.getLogger(Native.class.getName());
/*      */   
/*  116 */   public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
/*  117 */   public static final String DEFAULT_ENCODING = DEFAULT_CHARSET.name();
/*  118 */   public static final boolean DEBUG_LOAD = Boolean.getBoolean("jna.debug_load");
/*  119 */   public static final boolean DEBUG_JNA_LOAD = Boolean.getBoolean("jna.debug_load.jna");
/*  120 */   private static final Level DEBUG_JNA_LOAD_LEVEL = DEBUG_JNA_LOAD ? Level.INFO : Level.FINE;
/*      */ 
/*      */   
/*  123 */   static String jnidispatchPath = null;
/*  124 */   private static final Map<Class<?>, Map<String, Object>> typeOptions = Collections.synchronizedMap(new WeakHashMap<Class<?>, Map<String, Object>>());
/*  125 */   private static final Map<Class<?>, Reference<?>> libraries = Collections.synchronizedMap(new WeakHashMap<Class<?>, Reference<?>>()); private static final String _OPTION_ENCLOSING_LIBRARY = "enclosing-library";
/*      */   
/*  127 */   private static final Callback.UncaughtExceptionHandler DEFAULT_HANDLER = new Callback.UncaughtExceptionHandler()
/*      */     {
/*      */       public void uncaughtException(Callback c, Throwable e)
/*      */       {
/*  131 */         Native.LOG.log(Level.WARNING, "JNA: Callback " + c + " threw the following exception", e);
/*      */       }
/*      */     };
/*  134 */   private static Callback.UncaughtExceptionHandler callbackExceptionHandler = DEFAULT_HANDLER;
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
/*      */   static boolean isCompatibleVersion(String expectedVersion, String nativeVersion) {
/*  172 */     String[] expectedVersionParts = expectedVersion.split("\\.");
/*  173 */     String[] nativeVersionParts = nativeVersion.split("\\.");
/*  174 */     if (expectedVersionParts.length < 3 || nativeVersionParts.length < 3) {
/*  175 */       return false;
/*      */     }
/*      */     
/*  178 */     int expectedMajor = Integer.parseInt(expectedVersionParts[0]);
/*  179 */     int nativeMajor = Integer.parseInt(nativeVersionParts[0]);
/*  180 */     int expectedMinor = Integer.parseInt(expectedVersionParts[1]);
/*  181 */     int nativeMinor = Integer.parseInt(nativeVersionParts[1]);
/*      */     
/*  183 */     if (expectedMajor != nativeMajor) {
/*  184 */       return false;
/*      */     }
/*      */     
/*  187 */     if (expectedMinor > nativeMinor) {
/*  188 */       return false;
/*      */     }
/*      */     
/*  191 */     return true;
/*      */   }
/*      */   
/*      */   static {
/*  195 */     loadNativeDispatchLibrary();
/*      */     
/*  197 */     if (!isCompatibleVersion("6.1.0", getNativeVersion())) {
/*  198 */       String LS = System.getProperty("line.separator");
/*  199 */       throw new Error(LS + LS + "There is an incompatible JNA native library installed on this system" + LS + "Expected: " + "6.1.0" + LS + "Found:    " + 
/*      */ 
/*      */           
/*  202 */           getNativeVersion() + LS + ((jnidispatchPath != null) ? ("(at " + jnidispatchPath + ")") : 
/*      */           
/*  204 */           System.getProperty("java.library.path")) + "." + LS + "To resolve this issue you may do one of the following:" + LS + " - remove or uninstall the offending library" + LS + " - set the system property jna.nosys=true" + LS + " - set jna.boot.library.path to include the path to the version of the " + LS + "   jnidispatch library included with the JNA jar file you are using" + LS);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  213 */   public static final int POINTER_SIZE = sizeof(0);
/*  214 */   public static final int LONG_SIZE = sizeof(1);
/*  215 */   public static final int WCHAR_SIZE = sizeof(2);
/*  216 */   public static final int SIZE_T_SIZE = sizeof(3);
/*  217 */   public static final int BOOL_SIZE = sizeof(4);
/*  218 */   public static final int LONG_DOUBLE_SIZE = sizeof(5); private static final int TYPE_VOIDP = 0; private static final int TYPE_LONG = 1; private static final int TYPE_WCHAR_T = 2; private static final int TYPE_SIZE_T = 3; private static final int TYPE_BOOL = 4;
/*      */   private static final int TYPE_LONG_DOUBLE = 5;
/*      */   
/*      */   static {
/*  222 */     initIDs();
/*  223 */     if (Boolean.getBoolean("jna.protected"))
/*  224 */       setProtected(true); 
/*      */   }
/*  226 */   static final int MAX_ALIGNMENT = (Platform.isSPARC() || Platform.isWindows() || (
/*  227 */     Platform.isLinux() && (Platform.isARM() || Platform.isPPC() || Platform.isMIPS())) || 
/*  228 */     Platform.isAIX() || (
/*  229 */     Platform.isAndroid() && !Platform.isIntel())) ? 8 : LONG_SIZE;
/*      */   
/*  231 */   static final int MAX_PADDING = (Platform.isMac() && Platform.isPPC()) ? 8 : MAX_ALIGNMENT; static {
/*  232 */     System.setProperty("jna.loaded", "true");
/*      */   }
/*      */ 
/*      */   
/*  236 */   private static final Object finalizer = new Object()
/*      */     {
/*      */       protected void finalize() throws Throwable {
/*  239 */         Native.dispose();
/*  240 */         super.finalize();
/*      */       }
/*      */     };
/*      */ 
/*      */   
/*      */   static final String JNA_TMPLIB_PREFIX = "jna";
/*      */ 
/*      */   
/*      */   private static void dispose() {
/*  249 */     CallbackReference.disposeAll();
/*  250 */     Memory.disposeAll();
/*  251 */     NativeLibrary.disposeAll();
/*  252 */     unregisterAll();
/*  253 */     jnidispatchPath = null;
/*  254 */     System.setProperty("jna.loaded", "false");
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
/*      */   static boolean deleteLibrary(File lib) {
/*  269 */     if (lib.delete()) {
/*  270 */       return true;
/*      */     }
/*      */ 
/*      */     
/*  274 */     markTemporaryFile(lib);
/*      */     
/*  276 */     return false;
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
/*      */   public static long getWindowID(Window w) throws HeadlessException {
/*  316 */     return AWT.getWindowID(w);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getComponentID(Component c) throws HeadlessException {
/*  326 */     return AWT.getComponentID(c);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pointer getWindowPointer(Window w) throws HeadlessException {
/*  336 */     return new Pointer(AWT.getWindowID(w));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pointer getComponentPointer(Component c) throws HeadlessException {
/*  346 */     return new Pointer(AWT.getComponentID(c));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Pointer getDirectBufferPointer(Buffer b) {
/*  355 */     long peer = _getDirectBufferPointer(b);
/*  356 */     return (peer == 0L) ? null : new Pointer(peer);
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
/*      */   private static Charset getCharset(String encoding) {
/*  369 */     Charset charset = null;
/*  370 */     if (encoding != null) {
/*      */       try {
/*  372 */         charset = Charset.forName(encoding);
/*      */       }
/*  374 */       catch (IllegalCharsetNameException e) {
/*  375 */         LOG.log(Level.WARNING, "JNA Warning: Encoding ''{0}'' is unsupported ({1})", new Object[] { encoding, e
/*  376 */               .getMessage() });
/*      */       }
/*  378 */       catch (UnsupportedCharsetException e) {
/*  379 */         LOG.log(Level.WARNING, "JNA Warning: Encoding ''{0}'' is unsupported ({1})", new Object[] { encoding, e
/*  380 */               .getMessage() });
/*      */       } 
/*      */     }
/*  383 */     if (charset == null) {
/*  384 */       LOG.log(Level.WARNING, "JNA Warning: Using fallback encoding {0}", DEFAULT_CHARSET);
/*  385 */       charset = DEFAULT_CHARSET;
/*      */     } 
/*  387 */     return charset;
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
/*      */   public static String toString(byte[] buf) {
/*  399 */     return toString(buf, getDefaultStringEncoding());
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
/*      */   public static String toString(byte[] buf, String encoding) {
/*  416 */     return toString(buf, getCharset(encoding));
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
/*      */   public static String toString(byte[] buf, Charset charset) {
/*  432 */     int len = buf.length;
/*      */     
/*  434 */     for (int index = 0; index < len; index++) {
/*  435 */       if (buf[index] == 0) {
/*  436 */         len = index;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  441 */     if (len == 0) {
/*  442 */       return "";
/*      */     }
/*      */     
/*  445 */     return new String(buf, 0, len, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toString(char[] buf) {
/*  455 */     int len = buf.length;
/*  456 */     for (int index = 0; index < len; index++) {
/*  457 */       if (buf[index] == '\000') {
/*  458 */         len = index;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  463 */     if (len == 0) {
/*  464 */       return "";
/*      */     }
/*  466 */     return new String(buf, 0, len);
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
/*      */   public static List<String> toStringList(char[] buf) {
/*  480 */     return toStringList(buf, 0, buf.length);
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
/*      */   public static List<String> toStringList(char[] buf, int offset, int len) {
/*  494 */     List<String> list = new ArrayList<String>();
/*  495 */     int lastPos = offset;
/*  496 */     int maxPos = offset + len;
/*  497 */     for (int curPos = offset; curPos < maxPos; curPos++) {
/*  498 */       if (buf[curPos] == '\000') {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  503 */         if (lastPos == curPos) {
/*  504 */           return list;
/*      */         }
/*      */         
/*  507 */         String value = new String(buf, lastPos, curPos - lastPos);
/*  508 */         list.add(value);
/*  509 */         lastPos = curPos + 1;
/*      */       } 
/*      */     } 
/*      */     
/*  513 */     if (lastPos < maxPos) {
/*  514 */       String value = new String(buf, lastPos, maxPos - lastPos);
/*  515 */       list.add(value);
/*      */     } 
/*      */     
/*  518 */     return list;
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
/*      */   public static <T extends Library> T load(Class<T> interfaceClass) {
/*  533 */     return load((String)null, interfaceClass);
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
/*      */   public static <T extends Library> T load(Class<T> interfaceClass, Map<String, ?> options) {
/*  552 */     return load(null, interfaceClass, options);
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
/*      */   public static <T extends Library> T load(String name, Class<T> interfaceClass) {
/*  570 */     return load(name, interfaceClass, Collections.emptyMap());
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
/*      */   public static <T extends Library> T load(String name, Class<T> interfaceClass, Map<String, ?> options) {
/*  590 */     if (!Library.class.isAssignableFrom(interfaceClass))
/*      */     {
/*  592 */       throw new IllegalArgumentException("Interface (" + interfaceClass.getSimpleName() + ") of library=" + name + " does not extend " + Library.class
/*  593 */           .getSimpleName());
/*      */     }
/*      */     
/*  596 */     Library.Handler handler = new Library.Handler(name, interfaceClass, options);
/*  597 */     ClassLoader loader = interfaceClass.getClassLoader();
/*  598 */     Object proxy = Proxy.newProxyInstance(loader, new Class[] { interfaceClass }, handler);
/*  599 */     cacheOptions(interfaceClass, options, proxy);
/*  600 */     return interfaceClass.cast(proxy);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T> T loadLibrary(Class<T> interfaceClass) {
/*  610 */     return loadLibrary((String)null, interfaceClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T> T loadLibrary(Class<T> interfaceClass, Map<String, ?> options) {
/*  620 */     return loadLibrary(null, interfaceClass, options);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T> T loadLibrary(String name, Class<T> interfaceClass) {
/*  630 */     return loadLibrary(name, interfaceClass, Collections.emptyMap());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T> T loadLibrary(String name, Class<T> interfaceClass, Map<String, ?> options) {
/*  640 */     if (!Library.class.isAssignableFrom(interfaceClass))
/*      */     {
/*  642 */       throw new IllegalArgumentException("Interface (" + interfaceClass.getSimpleName() + ") of library=" + name + " does not extend " + Library.class
/*  643 */           .getSimpleName());
/*      */     }
/*      */     
/*  646 */     Library.Handler handler = new Library.Handler(name, interfaceClass, options);
/*  647 */     ClassLoader loader = interfaceClass.getClassLoader();
/*  648 */     Object proxy = Proxy.newProxyInstance(loader, new Class[] { interfaceClass }, handler);
/*  649 */     cacheOptions(interfaceClass, options, proxy);
/*  650 */     return interfaceClass.cast(proxy);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void loadLibraryInstance(Class<?> cls) {
/*  659 */     if (cls != null && !libraries.containsKey(cls)) {
/*      */       try {
/*  661 */         Field[] fields = cls.getFields();
/*  662 */         for (int i = 0; i < fields.length; i++) {
/*  663 */           Field field = fields[i];
/*  664 */           if (field.getType() == cls && 
/*  665 */             Modifier.isStatic(field.getModifiers())) {
/*      */             
/*  667 */             libraries.put(cls, new WeakReference(field.get(null)));
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*  672 */       } catch (Exception e) {
/*  673 */         throw new IllegalArgumentException("Could not access instance of " + cls + " (" + e + ")");
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
/*      */   static Class<?> findEnclosingLibraryClass(Class<?> cls) {
/*  687 */     if (cls == null) {
/*  688 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  692 */     Map<String, ?> libOptions = typeOptions.get(cls);
/*  693 */     if (libOptions != null) {
/*  694 */       Class<?> enclosingClass = (Class)libOptions.get("enclosing-library");
/*  695 */       if (enclosingClass != null) {
/*  696 */         return enclosingClass;
/*      */       }
/*  698 */       return cls;
/*      */     } 
/*  700 */     if (Library.class.isAssignableFrom(cls)) {
/*  701 */       return cls;
/*      */     }
/*  703 */     if (Callback.class.isAssignableFrom(cls)) {
/*  704 */       cls = CallbackReference.findCallbackClass(cls);
/*      */     }
/*  706 */     Class<?> declaring = cls.getDeclaringClass();
/*  707 */     Class<?> fromDeclaring = findEnclosingLibraryClass(declaring);
/*  708 */     if (fromDeclaring != null) {
/*  709 */       return fromDeclaring;
/*      */     }
/*  711 */     return findEnclosingLibraryClass(cls.getSuperclass());
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
/*      */   public static Map<String, Object> getLibraryOptions(Class<?> type) {
/*  730 */     Map<String, Object> libraryOptions = typeOptions.get(type);
/*  731 */     if (libraryOptions != null) {
/*  732 */       return libraryOptions;
/*      */     }
/*      */     
/*  735 */     Class<?> mappingClass = findEnclosingLibraryClass(type);
/*  736 */     if (mappingClass != null) {
/*  737 */       loadLibraryInstance(mappingClass);
/*      */     } else {
/*  739 */       mappingClass = type;
/*      */     } 
/*      */     
/*  742 */     libraryOptions = typeOptions.get(mappingClass);
/*  743 */     if (libraryOptions != null) {
/*  744 */       typeOptions.put(type, libraryOptions);
/*  745 */       return libraryOptions;
/*      */     } 
/*      */     
/*      */     try {
/*  749 */       Field field = mappingClass.getField("OPTIONS");
/*  750 */       field.setAccessible(true);
/*  751 */       libraryOptions = (Map<String, Object>)field.get(null);
/*  752 */       if (libraryOptions == null) {
/*  753 */         throw new IllegalStateException("Null options field");
/*      */       }
/*  755 */     } catch (NoSuchFieldException e) {
/*  756 */       libraryOptions = Collections.emptyMap();
/*  757 */     } catch (Exception e) {
/*  758 */       throw new IllegalArgumentException("OPTIONS must be a public field of type java.util.Map (" + e + "): " + mappingClass);
/*      */     } 
/*      */     
/*  761 */     libraryOptions = new HashMap<String, Object>(libraryOptions);
/*  762 */     if (!libraryOptions.containsKey("type-mapper")) {
/*  763 */       libraryOptions.put("type-mapper", lookupField(mappingClass, "TYPE_MAPPER", TypeMapper.class));
/*      */     }
/*  765 */     if (!libraryOptions.containsKey("structure-alignment")) {
/*  766 */       libraryOptions.put("structure-alignment", lookupField(mappingClass, "STRUCTURE_ALIGNMENT", Integer.class));
/*      */     }
/*  768 */     if (!libraryOptions.containsKey("string-encoding")) {
/*  769 */       libraryOptions.put("string-encoding", lookupField(mappingClass, "STRING_ENCODING", String.class));
/*      */     }
/*  771 */     libraryOptions = cacheOptions(mappingClass, libraryOptions, null);
/*      */     
/*  773 */     if (type != mappingClass) {
/*  774 */       typeOptions.put(type, libraryOptions);
/*      */     }
/*  776 */     return libraryOptions;
/*      */   }
/*      */   
/*      */   private static Object lookupField(Class<?> mappingClass, String fieldName, Class<?> resultClass) {
/*      */     try {
/*  781 */       Field field = mappingClass.getField(fieldName);
/*  782 */       field.setAccessible(true);
/*  783 */       return field.get(null);
/*      */     }
/*  785 */     catch (NoSuchFieldException e) {
/*  786 */       return null;
/*      */     }
/*  788 */     catch (Exception e) {
/*  789 */       throw new IllegalArgumentException(fieldName + " must be a public field of type " + resultClass
/*  790 */           .getName() + " (" + e + "): " + mappingClass);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TypeMapper getTypeMapper(Class<?> cls) {
/*  799 */     Map<String, ?> options = getLibraryOptions(cls);
/*  800 */     return (TypeMapper)options.get("type-mapper");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getStringEncoding(Class<?> cls) {
/*  810 */     Map<String, ?> options = getLibraryOptions(cls);
/*  811 */     String encoding = (String)options.get("string-encoding");
/*  812 */     return (encoding != null) ? encoding : getDefaultStringEncoding();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getDefaultStringEncoding() {
/*  820 */     return System.getProperty("jna.encoding", DEFAULT_ENCODING);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getStructureAlignment(Class<?> cls) {
/*  829 */     Integer alignment = (Integer)getLibraryOptions(cls).get("structure-alignment");
/*  830 */     return (alignment == null) ? 0 : alignment.intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static byte[] getBytes(String s) {
/*  839 */     return getBytes(s, getDefaultStringEncoding());
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
/*      */   static byte[] getBytes(String s, String encoding) {
/*  851 */     return getBytes(s, getCharset(encoding));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static byte[] getBytes(String s, Charset charset) {
/*  861 */     return s.getBytes(charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toByteArray(String s) {
/*  871 */     return toByteArray(s, getDefaultStringEncoding());
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
/*      */   public static byte[] toByteArray(String s, String encoding) {
/*  883 */     return toByteArray(s, getCharset(encoding));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toByteArray(String s, Charset charset) {
/*  894 */     byte[] bytes = getBytes(s, charset);
/*  895 */     byte[] buf = new byte[bytes.length + 1];
/*  896 */     System.arraycopy(bytes, 0, buf, 0, bytes.length);
/*  897 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char[] toCharArray(String s) {
/*  905 */     char[] chars = s.toCharArray();
/*  906 */     char[] buf = new char[chars.length + 1];
/*  907 */     System.arraycopy(chars, 0, buf, 0, chars.length);
/*  908 */     return buf;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void loadNativeDispatchLibrary() {
/*  917 */     if (!Boolean.getBoolean("jna.nounpack")) {
/*      */       try {
/*  919 */         removeTemporaryFiles();
/*      */       }
/*  921 */       catch (IOException e) {
/*  922 */         LOG.log(Level.WARNING, "JNA Warning: IOException removing temporary files", e);
/*      */       } 
/*      */     }
/*      */     
/*  926 */     String libName = System.getProperty("jna.boot.library.name", "jnidispatch");
/*  927 */     String bootPath = System.getProperty("jna.boot.library.path");
/*  928 */     if (bootPath != null) {
/*      */       
/*  930 */       StringTokenizer dirs = new StringTokenizer(bootPath, File.pathSeparator);
/*  931 */       while (dirs.hasMoreTokens()) {
/*  932 */         String dir = dirs.nextToken();
/*  933 */         File file = new File(new File(dir), System.mapLibraryName(libName).replace(".dylib", ".jnilib"));
/*  934 */         String path = file.getAbsolutePath();
/*  935 */         LOG.log(DEBUG_JNA_LOAD_LEVEL, "Looking in {0}", path);
/*  936 */         if (file.exists()) {
/*      */           try {
/*  938 */             LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", path);
/*  939 */             System.setProperty("jnidispatch.path", path);
/*  940 */             System.load(path);
/*  941 */             jnidispatchPath = path;
/*  942 */             LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", path);
/*      */             return;
/*  944 */           } catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  950 */         if (Platform.isMac()) {
/*      */           String orig, ext;
/*  952 */           if (path.endsWith("dylib")) {
/*  953 */             orig = "dylib";
/*  954 */             ext = "jnilib";
/*      */           } else {
/*  956 */             orig = "jnilib";
/*  957 */             ext = "dylib";
/*      */           } 
/*  959 */           path = path.substring(0, path.lastIndexOf(orig)) + ext;
/*  960 */           LOG.log(DEBUG_JNA_LOAD_LEVEL, "Looking in {0}", path);
/*  961 */           if ((new File(path)).exists()) {
/*      */             try {
/*  963 */               LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", path);
/*  964 */               System.setProperty("jnidispatch.path", path);
/*  965 */               System.load(path);
/*  966 */               jnidispatchPath = path;
/*  967 */               LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", path);
/*      */               return;
/*  969 */             } catch (UnsatisfiedLinkError ex) {
/*  970 */               LOG.log(Level.WARNING, "File found at " + path + " but not loadable: " + ex.getMessage(), ex);
/*      */             } 
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/*  976 */     String jnaNosys = System.getProperty("jna.nosys", "true");
/*  977 */     if (!Boolean.parseBoolean(jnaNosys) || Platform.isAndroid()) {
/*      */       try {
/*  979 */         LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying (via loadLibrary) {0}", libName);
/*  980 */         System.loadLibrary(libName);
/*  981 */         LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch on system path");
/*      */         
/*      */         return;
/*  984 */       } catch (UnsatisfiedLinkError unsatisfiedLinkError) {}
/*      */     }
/*      */     
/*  987 */     if (!Boolean.getBoolean("jna.noclasspath")) {
/*  988 */       loadNativeDispatchLibraryFromClasspath();
/*      */     } else {
/*      */       
/*  991 */       throw new UnsatisfiedLinkError("Unable to locate JNA native support library");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void loadNativeDispatchLibraryFromClasspath() {
/*      */     try {
/* 1002 */       String mappedName = System.mapLibraryName("jnidispatch").replace(".dylib", ".jnilib");
/* 1003 */       if (Platform.isAIX())
/*      */       {
/*      */         
/* 1006 */         mappedName = "libjnidispatch.a";
/*      */       }
/* 1008 */       String libName = "/com/sun/jna/" + Platform.RESOURCE_PREFIX + "/" + mappedName;
/* 1009 */       File lib = extractFromResourcePath(libName, Native.class.getClassLoader());
/* 1010 */       if (lib == null && 
/* 1011 */         lib == null) {
/* 1012 */         throw new UnsatisfiedLinkError("Could not find JNA native support");
/*      */       }
/*      */ 
/*      */       
/* 1016 */       LOG.log(DEBUG_JNA_LOAD_LEVEL, "Trying {0}", lib.getAbsolutePath());
/* 1017 */       System.setProperty("jnidispatch.path", lib.getAbsolutePath());
/* 1018 */       System.load(lib.getAbsolutePath());
/* 1019 */       jnidispatchPath = lib.getAbsolutePath();
/* 1020 */       LOG.log(DEBUG_JNA_LOAD_LEVEL, "Found jnidispatch at {0}", jnidispatchPath);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1026 */       if (isUnpacked(lib) && 
/* 1027 */         !Boolean.getBoolean("jnidispatch.preserve")) {
/* 1028 */         deleteLibrary(lib);
/*      */       }
/*      */     }
/* 1031 */     catch (IOException e) {
/* 1032 */       throw new UnsatisfiedLinkError(e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   static boolean isUnpacked(File file) {
/* 1038 */     return file.getName().startsWith("jna");
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
/*      */   public static File extractFromResourcePath(String name) throws IOException {
/* 1053 */     return extractFromResourcePath(name, null);
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
/*      */   public static File extractFromResourcePath(String name, ClassLoader loader) throws IOException {
/* 1071 */     Level DEBUG = (DEBUG_LOAD || (DEBUG_JNA_LOAD && name.contains("jnidispatch"))) ? Level.INFO : Level.FINE;
/* 1072 */     if (loader == null) {
/* 1073 */       loader = Thread.currentThread().getContextClassLoader();
/*      */       
/* 1075 */       if (loader == null) {
/* 1076 */         loader = Native.class.getClassLoader();
/*      */       }
/*      */     } 
/* 1079 */     LOG.log(DEBUG, "Looking in classpath from {0} for {1}", new Object[] { loader, name });
/* 1080 */     String libname = name.startsWith("/") ? name : NativeLibrary.mapSharedLibraryName(name);
/* 1081 */     String resourcePath = name.startsWith("/") ? name : (Platform.RESOURCE_PREFIX + "/" + libname);
/* 1082 */     if (resourcePath.startsWith("/")) {
/* 1083 */       resourcePath = resourcePath.substring(1);
/*      */     }
/* 1085 */     URL url = loader.getResource(resourcePath);
/* 1086 */     if (url == null && resourcePath.startsWith(Platform.RESOURCE_PREFIX))
/*      */     {
/* 1088 */       url = loader.getResource(libname);
/*      */     }
/* 1090 */     if (url == null) {
/* 1091 */       String path = System.getProperty("java.class.path");
/* 1092 */       if (loader instanceof URLClassLoader) {
/* 1093 */         path = Arrays.<URL>asList(((URLClassLoader)loader).getURLs()).toString();
/*      */       }
/* 1095 */       throw new IOException("Native library (" + resourcePath + ") not found in resource path (" + path + ")");
/*      */     } 
/* 1097 */     LOG.log(DEBUG, "Found library resource at {0}", url);
/*      */     
/* 1099 */     File lib = null;
/* 1100 */     if (url.getProtocol().toLowerCase().equals("file")) {
/*      */       try {
/* 1102 */         lib = new File(new URI(url.toString()));
/*      */       }
/* 1104 */       catch (URISyntaxException e) {
/* 1105 */         lib = new File(url.getPath());
/*      */       } 
/* 1107 */       LOG.log(DEBUG, "Looking in {0}", lib.getAbsolutePath());
/* 1108 */       if (!lib.exists()) {
/* 1109 */         throw new IOException("File URL " + url + " could not be properly decoded");
/*      */       }
/*      */     }
/* 1112 */     else if (!Boolean.getBoolean("jna.nounpack")) {
/* 1113 */       InputStream is = loader.getResourceAsStream(resourcePath);
/* 1114 */       if (is == null) {
/* 1115 */         throw new IOException("Can't obtain InputStream for " + resourcePath);
/*      */       }
/*      */       
/* 1118 */       FileOutputStream fos = null;
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1123 */         File dir = getTempDir();
/* 1124 */         lib = File.createTempFile("jna", Platform.isWindows() ? ".dll" : null, dir);
/* 1125 */         if (!Boolean.getBoolean("jnidispatch.preserve")) {
/* 1126 */           lib.deleteOnExit();
/*      */         }
/* 1128 */         LOG.log(DEBUG, "Extracting library to {0}", lib.getAbsolutePath());
/* 1129 */         fos = new FileOutputStream(lib);
/*      */         
/* 1131 */         byte[] buf = new byte[1024]; int count;
/* 1132 */         while ((count = is.read(buf, 0, buf.length)) > 0) {
/* 1133 */           fos.write(buf, 0, count);
/*      */         }
/*      */       }
/* 1136 */       catch (IOException e) {
/* 1137 */         throw new IOException("Failed to create temporary file for " + name + " library: " + e.getMessage());
/*      */       } finally {
/*      */         
/* 1140 */         try { is.close(); } catch (IOException iOException) {}
/* 1141 */         if (fos != null) {
/* 1142 */           try { fos.close(); } catch (IOException iOException) {}
/*      */         }
/*      */       } 
/*      */     } 
/* 1146 */     return lib;
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
/*      */   public static Library synchronizedLibrary(final Library library) {
/* 1189 */     Class<?> cls = library.getClass();
/* 1190 */     if (!Proxy.isProxyClass(cls)) {
/* 1191 */       throw new IllegalArgumentException("Library must be a proxy class");
/*      */     }
/* 1193 */     InvocationHandler ih = Proxy.getInvocationHandler(library);
/* 1194 */     if (!(ih instanceof Library.Handler)) {
/* 1195 */       throw new IllegalArgumentException("Unrecognized proxy handler: " + ih);
/*      */     }
/* 1197 */     final Library.Handler handler = (Library.Handler)ih;
/* 1198 */     InvocationHandler newHandler = new InvocationHandler()
/*      */       {
/*      */         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 1201 */           synchronized (handler.getNativeLibrary()) {
/* 1202 */             return handler.invoke(library, method, args);
/*      */           } 
/*      */         }
/*      */       };
/* 1206 */     return (Library)Proxy.newProxyInstance(cls.getClassLoader(), cls
/* 1207 */         .getInterfaces(), newHandler);
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
/*      */   public static String getWebStartLibraryPath(String libName) {
/* 1227 */     if (System.getProperty("javawebstart.version") == null) {
/* 1228 */       return null;
/*      */     }
/*      */     try {
/* 1231 */       ClassLoader cl = Native.class.getClassLoader();
/* 1232 */       Method m = AccessController.<Method>doPrivileged(new PrivilegedAction<Method>()
/*      */           {
/*      */             public Method run() {
/*      */               try {
/* 1236 */                 Method m = ClassLoader.class.getDeclaredMethod("findLibrary", new Class[] { String.class });
/* 1237 */                 m.setAccessible(true);
/* 1238 */                 return m;
/*      */               }
/* 1240 */               catch (Exception e) {
/* 1241 */                 return null;
/*      */               } 
/*      */             }
/*      */           });
/* 1245 */       String libpath = (String)m.invoke(cl, new Object[] { libName });
/* 1246 */       if (libpath != null) {
/* 1247 */         return (new File(libpath)).getParent();
/*      */       }
/* 1249 */       return null;
/*      */     }
/* 1251 */     catch (Exception e) {
/* 1252 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void markTemporaryFile(File file) {
/*      */     try {
/* 1262 */       File marker = new File(file.getParentFile(), file.getName() + ".x");
/* 1263 */       marker.createNewFile();
/*      */     } catch (IOException e) {
/* 1265 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static File getTempDir() throws IOException {
/*      */     File jnatmp;
/* 1273 */     String prop = System.getProperty("jna.tmpdir");
/* 1274 */     if (prop != null) {
/* 1275 */       jnatmp = new File(prop);
/* 1276 */       jnatmp.mkdirs();
/*      */     } else {
/*      */       
/* 1279 */       File tmp = new File(System.getProperty("java.io.tmpdir"));
/* 1280 */       if (Platform.isMac()) {
/*      */         
/* 1282 */         jnatmp = new File(System.getProperty("user.home"), "Library/Caches/JNA/temp");
/* 1283 */       } else if (Platform.isLinux() || Platform.isSolaris() || Platform.isAIX() || Platform.isFreeBSD() || Platform.isNetBSD() || Platform.isOpenBSD() || Platform.iskFreeBSD()) {
/*      */         File xdgCacheFile;
/*      */         
/* 1286 */         String xdgCacheEnvironment = System.getenv("XDG_CACHE_HOME");
/*      */         
/* 1288 */         if (xdgCacheEnvironment == null || xdgCacheEnvironment.trim().isEmpty()) {
/* 1289 */           xdgCacheFile = new File(System.getProperty("user.home"), ".cache");
/*      */         } else {
/* 1291 */           xdgCacheFile = new File(xdgCacheEnvironment);
/*      */         } 
/* 1293 */         jnatmp = new File(xdgCacheFile, "JNA/temp");
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1298 */         jnatmp = new File(tmp, "jna-" + System.getProperty("user.name").hashCode());
/*      */       } 
/*      */       
/* 1301 */       jnatmp.mkdirs();
/* 1302 */       if (!jnatmp.exists() || !jnatmp.canWrite()) {
/* 1303 */         jnatmp = tmp;
/*      */       }
/*      */     } 
/* 1306 */     if (!jnatmp.exists()) {
/* 1307 */       throw new IOException("JNA temporary directory '" + jnatmp + "' does not exist");
/*      */     }
/* 1309 */     if (!jnatmp.canWrite()) {
/* 1310 */       throw new IOException("JNA temporary directory '" + jnatmp + "' is not writable");
/*      */     }
/* 1312 */     return jnatmp;
/*      */   }
/*      */ 
/*      */   
/*      */   static void removeTemporaryFiles() throws IOException {
/* 1317 */     File dir = getTempDir();
/* 1318 */     FilenameFilter filter = new FilenameFilter()
/*      */       {
/*      */         public boolean accept(File dir, String name) {
/* 1321 */           return (name.endsWith(".x") && name.startsWith("jna"));
/*      */         }
/*      */       };
/* 1324 */     File[] files = dir.listFiles(filter);
/* 1325 */     for (int i = 0; files != null && i < files.length; i++) {
/* 1326 */       File marker = files[i];
/* 1327 */       String name = marker.getName();
/* 1328 */       name = name.substring(0, name.length() - 2);
/* 1329 */       File target = new File(marker.getParentFile(), name);
/* 1330 */       if (!target.exists() || target.delete()) {
/* 1331 */         marker.delete();
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
/*      */   public static int getNativeSize(Class<?> type, Object value) {
/* 1343 */     if (type.isArray()) {
/* 1344 */       int len = Array.getLength(value);
/* 1345 */       if (len > 0) {
/* 1346 */         Object o = Array.get(value, 0);
/* 1347 */         return len * getNativeSize(type.getComponentType(), o);
/*      */       } 
/*      */       
/* 1350 */       throw new IllegalArgumentException("Arrays of length zero not allowed: " + type);
/*      */     } 
/* 1352 */     if (Structure.class.isAssignableFrom(type) && 
/* 1353 */       !Structure.ByReference.class.isAssignableFrom(type)) {
/* 1354 */       return Structure.size(type, value);
/*      */     }
/*      */     try {
/* 1357 */       return getNativeSize(type);
/*      */     }
/* 1359 */     catch (IllegalArgumentException e) {
/* 1360 */       throw new IllegalArgumentException("The type \"" + type.getName() + "\" is not supported: " + e
/*      */           
/* 1362 */           .getMessage());
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
/*      */   public static int getNativeSize(Class<?> cls) {
/* 1375 */     if (NativeMapped.class.isAssignableFrom(cls)) {
/* 1376 */       cls = NativeMappedConverter.getInstance(cls).nativeType();
/*      */     }
/*      */     
/* 1379 */     if (cls == boolean.class || cls == Boolean.class) return 4; 
/* 1380 */     if (cls == byte.class || cls == Byte.class) return 1; 
/* 1381 */     if (cls == short.class || cls == Short.class) return 2; 
/* 1382 */     if (cls == char.class || cls == Character.class) return WCHAR_SIZE; 
/* 1383 */     if (cls == int.class || cls == Integer.class) return 4; 
/* 1384 */     if (cls == long.class || cls == Long.class) return 8; 
/* 1385 */     if (cls == float.class || cls == Float.class) return 4; 
/* 1386 */     if (cls == double.class || cls == Double.class) return 8; 
/* 1387 */     if (Structure.class.isAssignableFrom(cls)) {
/* 1388 */       if (Structure.ByValue.class.isAssignableFrom(cls)) {
/* 1389 */         return Structure.size((Class)cls);
/*      */       }
/* 1391 */       return POINTER_SIZE;
/*      */     } 
/* 1393 */     if (Pointer.class.isAssignableFrom(cls) || (Platform.HAS_BUFFERS && 
/* 1394 */       Buffers.isBuffer(cls)) || Callback.class
/* 1395 */       .isAssignableFrom(cls) || String.class == cls || WString.class == cls)
/*      */     {
/*      */       
/* 1398 */       return POINTER_SIZE;
/*      */     }
/* 1400 */     throw new IllegalArgumentException("Native size for type \"" + cls.getName() + "\" is unknown");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSupportedNativeType(Class<?> cls) {
/* 1409 */     if (Structure.class.isAssignableFrom(cls)) {
/* 1410 */       return true;
/*      */     }
/*      */     try {
/* 1413 */       return (getNativeSize(cls) != 0);
/*      */     }
/* 1415 */     catch (IllegalArgumentException e) {
/* 1416 */       return false;
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
/*      */   public static void setCallbackExceptionHandler(Callback.UncaughtExceptionHandler eh) {
/* 1428 */     callbackExceptionHandler = (eh == null) ? DEFAULT_HANDLER : eh;
/*      */   }
/*      */ 
/*      */   
/*      */   public static Callback.UncaughtExceptionHandler getCallbackExceptionHandler() {
/* 1433 */     return callbackExceptionHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void register(String libName) {
/* 1443 */     register(findDirectMappedClass(getCallingClass()), libName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void register(NativeLibrary lib) {
/* 1453 */     register(findDirectMappedClass(getCallingClass()), lib);
/*      */   }
/*      */ 
/*      */   
/*      */   static Class<?> findDirectMappedClass(Class<?> cls) {
/* 1458 */     Method[] methods = cls.getDeclaredMethods();
/* 1459 */     for (Method m : methods) {
/* 1460 */       if ((m.getModifiers() & 0x100) != 0) {
/* 1461 */         return cls;
/*      */       }
/*      */     } 
/* 1464 */     int idx = cls.getName().lastIndexOf("$");
/* 1465 */     if (idx != -1) {
/* 1466 */       String name = cls.getName().substring(0, idx);
/*      */       try {
/* 1468 */         return findDirectMappedClass(Class.forName(name, true, cls.getClassLoader()));
/* 1469 */       } catch (ClassNotFoundException classNotFoundException) {}
/*      */     } 
/*      */ 
/*      */     
/* 1473 */     throw new IllegalArgumentException("Can't determine class with native methods from the current context (" + cls + ")");
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
/*      */   static Class<?> getCallingClass() {
/* 1485 */     Class<?>[] context = (new SecurityManager() { public Class<?>[] getClassContext() { return super.getClassContext(); } }).getClassContext();
/* 1486 */     if (context == null) {
/* 1487 */       throw new IllegalStateException("The SecurityManager implementation on this platform is broken; you must explicitly provide the class to register");
/*      */     }
/* 1489 */     if (context.length < 4) {
/* 1490 */       throw new IllegalStateException("This method must be called from the static initializer of a class");
/*      */     }
/* 1492 */     return context[3];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
/* 1502 */     CallbackReference.setCallbackThreadInitializer(cb, initializer);
/*      */   }
/*      */   
/* 1505 */   private static final Map<Class<?>, long[]> registeredClasses = (Map)new WeakHashMap<Class<?>, long>();
/* 1506 */   private static final Map<Class<?>, NativeLibrary> registeredLibraries = new WeakHashMap<Class<?>, NativeLibrary>(); static final int CB_HAS_INITIALIZER = 1; private static final int CVT_UNSUPPORTED = -1; private static final int CVT_DEFAULT = 0; private static final int CVT_POINTER = 1; private static final int CVT_STRING = 2; private static final int CVT_STRUCTURE = 3; private static final int CVT_STRUCTURE_BYVAL = 4; private static final int CVT_BUFFER = 5; private static final int CVT_ARRAY_BYTE = 6; private static final int CVT_ARRAY_SHORT = 7; private static final int CVT_ARRAY_CHAR = 8; private static final int CVT_ARRAY_INT = 9; private static final int CVT_ARRAY_LONG = 10; private static final int CVT_ARRAY_FLOAT = 11; private static final int CVT_ARRAY_DOUBLE = 12; private static final int CVT_ARRAY_BOOLEAN = 13; private static final int CVT_BOOLEAN = 14; private static final int CVT_CALLBACK = 15; private static final int CVT_FLOAT = 16; private static final int CVT_NATIVE_MAPPED = 17; private static final int CVT_NATIVE_MAPPED_STRING = 18; private static final int CVT_NATIVE_MAPPED_WSTRING = 19; private static final int CVT_WSTRING = 20; private static final int CVT_INTEGER_TYPE = 21; private static final int CVT_POINTER_TYPE = 22; private static final int CVT_TYPE_MAPPER = 23; private static final int CVT_TYPE_MAPPER_STRING = 24; private static final int CVT_TYPE_MAPPER_WSTRING = 25; private static final int CVT_OBJECT = 26; private static final int CVT_JNIENV = 27; static final int CB_OPTION_DIRECT = 1; static final int CB_OPTION_IN_DLL = 2;
/*      */   
/*      */   private static void unregisterAll() {
/* 1509 */     synchronized (registeredClasses) {
/* 1510 */       for (Map.Entry<Class<?>, long[]> e : registeredClasses.entrySet()) {
/* 1511 */         unregister(e.getKey(), e.getValue());
/*      */       }
/*      */       
/* 1514 */       registeredClasses.clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void unregister() {
/* 1523 */     unregister(findDirectMappedClass(getCallingClass()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void unregister(Class<?> cls) {
/* 1531 */     synchronized (registeredClasses) {
/* 1532 */       long[] handles = registeredClasses.get(cls);
/* 1533 */       if (handles != null) {
/* 1534 */         unregister(cls, handles);
/* 1535 */         registeredClasses.remove(cls);
/* 1536 */         registeredLibraries.remove(cls);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registered(Class<?> cls) {
/* 1546 */     synchronized (registeredClasses) {
/* 1547 */       return registeredClasses.containsKey(cls);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getSignature(Class<?> cls) {
/* 1555 */     if (cls.isArray()) {
/* 1556 */       return "[" + getSignature(cls.getComponentType());
/*      */     }
/* 1558 */     if (cls.isPrimitive()) {
/* 1559 */       if (cls == void.class) return "V"; 
/* 1560 */       if (cls == boolean.class) return "Z"; 
/* 1561 */       if (cls == byte.class) return "B"; 
/* 1562 */       if (cls == short.class) return "S"; 
/* 1563 */       if (cls == char.class) return "C"; 
/* 1564 */       if (cls == int.class) return "I"; 
/* 1565 */       if (cls == long.class) return "J"; 
/* 1566 */       if (cls == float.class) return "F"; 
/* 1567 */       if (cls == double.class) return "D"; 
/*      */     } 
/* 1569 */     return "L" + replace(".", "/", cls.getName()) + ";";
/*      */   }
/*      */ 
/*      */   
/*      */   static String replace(String s1, String s2, String str) {
/* 1574 */     StringBuilder buf = new StringBuilder();
/*      */     while (true) {
/* 1576 */       int idx = str.indexOf(s1);
/* 1577 */       if (idx == -1) {
/* 1578 */         buf.append(str);
/*      */         
/*      */         break;
/*      */       } 
/* 1582 */       buf.append(str.substring(0, idx));
/* 1583 */       buf.append(s2);
/* 1584 */       str = str.substring(idx + s1.length());
/*      */     } 
/*      */     
/* 1587 */     return buf.toString();
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
/*      */   private static int getConversion(Class<?> type, TypeMapper mapper, boolean allowObjects) {
/* 1624 */     if (type == Void.class) type = void.class;
/*      */     
/* 1626 */     if (mapper != null) {
/* 1627 */       FromNativeConverter fromNative = mapper.getFromNativeConverter(type);
/* 1628 */       ToNativeConverter toNative = mapper.getToNativeConverter(type);
/* 1629 */       if (fromNative != null) {
/* 1630 */         Class<?> nativeType = fromNative.nativeType();
/* 1631 */         if (nativeType == String.class) {
/* 1632 */           return 24;
/*      */         }
/* 1634 */         if (nativeType == WString.class) {
/* 1635 */           return 25;
/*      */         }
/* 1637 */         return 23;
/*      */       } 
/* 1639 */       if (toNative != null) {
/* 1640 */         Class<?> nativeType = toNative.nativeType();
/* 1641 */         if (nativeType == String.class) {
/* 1642 */           return 24;
/*      */         }
/* 1644 */         if (nativeType == WString.class) {
/* 1645 */           return 25;
/*      */         }
/* 1647 */         return 23;
/*      */       } 
/*      */     } 
/*      */     
/* 1651 */     if (Pointer.class.isAssignableFrom(type)) {
/* 1652 */       return 1;
/*      */     }
/* 1654 */     if (String.class == type) {
/* 1655 */       return 2;
/*      */     }
/* 1657 */     if (WString.class.isAssignableFrom(type)) {
/* 1658 */       return 20;
/*      */     }
/* 1660 */     if (Platform.HAS_BUFFERS && Buffers.isBuffer(type)) {
/* 1661 */       return 5;
/*      */     }
/* 1663 */     if (Structure.class.isAssignableFrom(type)) {
/* 1664 */       if (Structure.ByValue.class.isAssignableFrom(type)) {
/* 1665 */         return 4;
/*      */       }
/* 1667 */       return 3;
/*      */     } 
/* 1669 */     if (type.isArray()) {
/* 1670 */       switch (type.getName().charAt(1)) { case 'Z':
/* 1671 */           return 13;
/* 1672 */         case 'B': return 6;
/* 1673 */         case 'S': return 7;
/* 1674 */         case 'C': return 8;
/* 1675 */         case 'I': return 9;
/* 1676 */         case 'J': return 10;
/* 1677 */         case 'F': return 11;
/* 1678 */         case 'D': return 12; }
/*      */ 
/*      */     
/*      */     }
/* 1682 */     if (type.isPrimitive()) {
/* 1683 */       return (type == boolean.class) ? 14 : 0;
/*      */     }
/* 1685 */     if (Callback.class.isAssignableFrom(type)) {
/* 1686 */       return 15;
/*      */     }
/* 1688 */     if (IntegerType.class.isAssignableFrom(type)) {
/* 1689 */       return 21;
/*      */     }
/* 1691 */     if (PointerType.class.isAssignableFrom(type)) {
/* 1692 */       return 22;
/*      */     }
/* 1694 */     if (NativeMapped.class.isAssignableFrom(type)) {
/* 1695 */       Class<?> nativeType = NativeMappedConverter.getInstance(type).nativeType();
/* 1696 */       if (nativeType == String.class) {
/* 1697 */         return 18;
/*      */       }
/* 1699 */       if (nativeType == WString.class) {
/* 1700 */         return 19;
/*      */       }
/* 1702 */       return 17;
/*      */     } 
/* 1704 */     if (JNIEnv.class == type) {
/* 1705 */       return 27;
/*      */     }
/* 1707 */     return allowObjects ? 26 : -1;
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
/*      */   public static void register(Class<?> cls, String libName) {
/* 1722 */     NativeLibrary library = NativeLibrary.getInstance(libName, Collections.singletonMap("classloader", cls.getClassLoader()));
/* 1723 */     register(cls, library);
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
/*      */   public static void register(Class<?> cls, NativeLibrary lib) {
/* 1736 */     Method[] methods = cls.getDeclaredMethods();
/* 1737 */     List<Method> mlist = new ArrayList<Method>();
/* 1738 */     Map<String, ?> options = lib.getOptions();
/* 1739 */     TypeMapper mapper = (TypeMapper)options.get("type-mapper");
/* 1740 */     boolean allowObjects = Boolean.TRUE.equals(options.get("allow-objects"));
/* 1741 */     options = cacheOptions(cls, options, null);
/*      */     
/* 1743 */     for (Method m : methods) {
/* 1744 */       if ((m.getModifiers() & 0x100) != 0) {
/* 1745 */         mlist.add(m);
/*      */       }
/*      */     } 
/*      */     
/* 1749 */     long[] handles = new long[mlist.size()];
/* 1750 */     for (int i = 0; i < handles.length; i++) {
/* 1751 */       long rtype, closure_rtype; Method method = mlist.get(i);
/* 1752 */       String sig = "(";
/* 1753 */       Class<?> rclass = method.getReturnType();
/*      */       
/* 1755 */       Class<?>[] ptypes = method.getParameterTypes();
/* 1756 */       long[] atypes = new long[ptypes.length];
/* 1757 */       long[] closure_atypes = new long[ptypes.length];
/* 1758 */       int[] cvt = new int[ptypes.length];
/* 1759 */       ToNativeConverter[] toNative = new ToNativeConverter[ptypes.length];
/* 1760 */       FromNativeConverter fromNative = null;
/* 1761 */       int rcvt = getConversion(rclass, mapper, allowObjects);
/* 1762 */       boolean throwLastError = false;
/* 1763 */       switch (rcvt) {
/*      */         case -1:
/* 1765 */           throw new IllegalArgumentException(rclass + " is not a supported return type (in method " + method.getName() + " in " + cls + ")");
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/* 1769 */           fromNative = mapper.getFromNativeConverter(rclass);
/*      */ 
/*      */ 
/*      */           
/* 1773 */           closure_rtype = (Structure.FFIType.get(rclass.isPrimitive() ? rclass : Pointer.class).getPointer()).peer;
/* 1774 */           rtype = (Structure.FFIType.get(fromNative.nativeType()).getPointer()).peer;
/*      */           break;
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 21:
/*      */         case 22:
/* 1781 */           closure_rtype = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
/* 1782 */           rtype = (Structure.FFIType.get(NativeMappedConverter.getInstance(rclass).nativeType()).getPointer()).peer;
/*      */           break;
/*      */         case 3:
/*      */         case 26:
/* 1786 */           closure_rtype = rtype = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
/*      */         
/*      */         case 4:
/* 1789 */           closure_rtype = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
/* 1790 */           rtype = (Structure.FFIType.get(rclass).getPointer()).peer;
/*      */           break;
/*      */         default:
/* 1793 */           closure_rtype = rtype = (Structure.FFIType.get(rclass).getPointer()).peer;
/*      */           break;
/*      */       } 
/* 1796 */       for (int t = 0; t < ptypes.length; t++) {
/* 1797 */         Class<?> type = ptypes[t];
/* 1798 */         sig = sig + getSignature(type);
/* 1799 */         int conversionType = getConversion(type, mapper, allowObjects);
/* 1800 */         cvt[t] = conversionType;
/* 1801 */         if (conversionType == -1) {
/* 1802 */           throw new IllegalArgumentException(type + " is not a supported argument type (in method " + method.getName() + " in " + cls + ")");
/*      */         }
/* 1804 */         if (conversionType == 17 || conversionType == 18 || conversionType == 19 || conversionType == 21) {
/*      */ 
/*      */ 
/*      */           
/* 1808 */           type = NativeMappedConverter.getInstance(type).nativeType();
/* 1809 */         } else if (conversionType == 23 || conversionType == 24 || conversionType == 25) {
/*      */ 
/*      */           
/* 1812 */           toNative[t] = mapper.getToNativeConverter(type);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1818 */         switch (conversionType) {
/*      */           case 4:
/*      */           case 17:
/*      */           case 18:
/*      */           case 19:
/*      */           case 21:
/*      */           case 22:
/* 1825 */             atypes[t] = (Structure.FFIType.get(type).getPointer()).peer;
/* 1826 */             closure_atypes[t] = (Structure.FFIType.get(Pointer.class).getPointer()).peer;
/*      */             break;
/*      */           case 23:
/*      */           case 24:
/*      */           case 25:
/* 1831 */             closure_atypes[t] = (Structure.FFIType.get(type.isPrimitive() ? type : Pointer.class).getPointer()).peer;
/* 1832 */             atypes[t] = (Structure.FFIType.get(toNative[t].nativeType()).getPointer()).peer;
/*      */             break;
/*      */           case 0:
/* 1835 */             atypes[t] = (Structure.FFIType.get(type).getPointer()).peer; closure_atypes[t] = (Structure.FFIType.get(type).getPointer()).peer;
/*      */           
/*      */           default:
/* 1838 */             atypes[t] = (Structure.FFIType.get(Pointer.class).getPointer()).peer; closure_atypes[t] = (Structure.FFIType.get(Pointer.class).getPointer()).peer; break;
/*      */         } 
/*      */       } 
/* 1841 */       sig = sig + ")";
/* 1842 */       sig = sig + getSignature(rclass);
/*      */       
/* 1844 */       Class<?>[] etypes = method.getExceptionTypes();
/* 1845 */       for (int e = 0; e < etypes.length; e++) {
/* 1846 */         if (LastErrorException.class.isAssignableFrom(etypes[e])) {
/* 1847 */           throwLastError = true;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1852 */       Function f = lib.getFunction(method.getName(), method);
/*      */       try {
/* 1854 */         handles[i] = registerMethod(cls, method.getName(), sig, cvt, closure_atypes, atypes, rcvt, closure_rtype, rtype, method, f.peer, f
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1859 */             .getCallingConvention(), throwLastError, toNative, fromNative, f.encoding);
/*      */ 
/*      */       
/*      */       }
/* 1863 */       catch (NoSuchMethodError noSuchMethodError) {
/* 1864 */         throw new UnsatisfiedLinkError("No method " + method.getName() + " with signature " + sig + " in " + cls);
/*      */       } 
/*      */     } 
/* 1867 */     synchronized (registeredClasses) {
/* 1868 */       registeredClasses.put(cls, handles);
/* 1869 */       registeredLibraries.put(cls, lib);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Map<String, Object> cacheOptions(Class<?> cls, Map<String, ?> options, Object proxy) {
/* 1877 */     Map<String, Object> libOptions = new HashMap<String, Object>(options);
/* 1878 */     libOptions.put("enclosing-library", cls);
/* 1879 */     typeOptions.put(cls, libOptions);
/* 1880 */     if (proxy != null) {
/* 1881 */       libraries.put(cls, new WeakReference(proxy));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1887 */     if (!cls.isInterface() && Library.class
/* 1888 */       .isAssignableFrom(cls)) {
/* 1889 */       Class<?>[] ifaces = cls.getInterfaces();
/* 1890 */       for (Class<?> ifc : ifaces) {
/* 1891 */         if (Library.class.isAssignableFrom(ifc)) {
/* 1892 */           cacheOptions(ifc, libOptions, proxy);
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1897 */     return libOptions;
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
/*      */   private static NativeMapped fromNative(Class<?> cls, Object value) {
/* 1921 */     return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new FromNativeContext(cls));
/*      */   }
/*      */   
/*      */   private static NativeMapped fromNative(Method m, Object value) {
/* 1925 */     Class<?> cls = m.getReturnType();
/* 1926 */     return (NativeMapped)NativeMappedConverter.getInstance(cls).fromNative(value, new MethodResultContext(cls, null, null, m));
/*      */   }
/*      */   
/*      */   private static Class<?> nativeType(Class<?> cls) {
/* 1930 */     return NativeMappedConverter.getInstance(cls).nativeType();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static Object toNative(ToNativeConverter cvt, Object o) {
/* 1936 */     return cvt.toNative(o, new ToNativeContext());
/*      */   }
/*      */   
/*      */   private static Object fromNative(FromNativeConverter cvt, Object o, Method m) {
/* 1940 */     return cvt.fromNative(o, new MethodResultContext(m.getReturnType(), null, null, m));
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
/*      */   public static void main(String[] args) {
/* 1959 */     String DEFAULT_TITLE = "Java Native Access (JNA)";
/* 1960 */     String DEFAULT_VERSION = "5.6.0";
/* 1961 */     String DEFAULT_BUILD = "5.6.0 (package information missing)";
/* 1962 */     Package pkg = Native.class.getPackage();
/*      */     
/* 1964 */     String title = (pkg != null) ? pkg.getSpecificationTitle() : "Java Native Access (JNA)";
/* 1965 */     if (title == null) title = "Java Native Access (JNA)";
/*      */     
/* 1967 */     String version = (pkg != null) ? pkg.getSpecificationVersion() : "5.6.0";
/* 1968 */     if (version == null) version = "5.6.0"; 
/* 1969 */     title = title + " API Version " + version;
/* 1970 */     System.out.println(title);
/*      */     
/* 1972 */     version = (pkg != null) ? pkg.getImplementationVersion() : "5.6.0 (package information missing)";
/* 1973 */     if (version == null) version = "5.6.0 (package information missing)"; 
/* 1974 */     System.out.println("Version: " + version);
/* 1975 */     System.out.println(" Native: " + getNativeVersion() + " (" + 
/* 1976 */         getAPIChecksum() + ")");
/* 1977 */     System.out.println(" Prefix: " + Platform.RESOURCE_PREFIX);
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
/*      */   static Structure invokeStructure(Function function, long fp, int callFlags, Object[] args, Structure s) {
/* 2102 */     invokeStructure(function, fp, callFlags, args, (s.getPointer()).peer, 
/* 2103 */         (s.getTypeInfo()).peer);
/* 2104 */     return s;
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
/*      */   static long open(String name) {
/* 2122 */     return open(name, -1);
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
/*      */   
/*      */   static Pointer getPointer(long addr) {
/* 2211 */     long peer = _getPointer(addr);
/* 2212 */     return (peer == 0L) ? null : new Pointer(peer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getString(Pointer pointer, long offset) {
/* 2220 */     return getString(pointer, offset, getDefaultStringEncoding());
/*      */   }
/*      */   
/*      */   static String getString(Pointer pointer, long offset, String encoding) {
/* 2224 */     byte[] data = getStringBytes(pointer, pointer.peer, offset);
/* 2225 */     if (encoding != null) {
/*      */       try {
/* 2227 */         return new String(data, encoding);
/*      */       }
/* 2229 */       catch (UnsupportedEncodingException unsupportedEncodingException) {}
/*      */     }
/*      */     
/* 2232 */     return new String(data);
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
/* 2274 */   private static final ThreadLocal<Memory> nativeThreadTerminationFlag = new ThreadLocal<Memory>()
/*      */     {
/*      */       protected Memory initialValue()
/*      */       {
/* 2278 */         Memory m = new Memory(4L);
/* 2279 */         m.clear();
/* 2280 */         return m;
/*      */       }
/*      */     };
/* 2283 */   private static final Map<Thread, Pointer> nativeThreads = Collections.synchronizedMap(new WeakHashMap<Thread, Pointer>()); private static native void initIDs(); public static synchronized native void setProtected(boolean paramBoolean); public static synchronized native boolean isProtected(); static native long getWindowHandle0(Component paramComponent); private static native long _getDirectBufferPointer(Buffer paramBuffer); private static native int sizeof(int paramInt);
/*      */   private static native String getNativeVersion();
/*      */   private static native String getAPIChecksum();
/*      */   public static native int getLastError();
/*      */   public static native void setLastError(int paramInt);
/*      */   private static native void unregister(Class<?> paramClass, long[] paramArrayOflong);
/*      */   private static native long registerMethod(Class<?> paramClass, String paramString1, String paramString2, int[] paramArrayOfint, long[] paramArrayOflong1, long[] paramArrayOflong2, int paramInt1, long paramLong1, long paramLong2, Method paramMethod, long paramLong3, int paramInt2, boolean paramBoolean, ToNativeConverter[] paramArrayOfToNativeConverter, FromNativeConverter paramFromNativeConverter, String paramString3);
/*      */   public static native long ffi_prep_cif(int paramInt1, int paramInt2, long paramLong1, long paramLong2);
/*      */   public static native void ffi_call(long paramLong1, long paramLong2, long paramLong3, long paramLong4);
/*      */   public static native long ffi_prep_closure(long paramLong, ffi_callback paramffi_callback);
/*      */   public static native void ffi_free_closure(long paramLong);
/*      */   static native int initialize_ffi_type(long paramLong);
/*      */   static synchronized native void freeNativeCallback(long paramLong);
/*      */   public static void detach(boolean detach) {
/* 2297 */     Thread thread = Thread.currentThread();
/* 2298 */     if (detach) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2305 */       nativeThreads.remove(thread);
/* 2306 */       Pointer p = nativeThreadTerminationFlag.get();
/* 2307 */       setDetachState(true, 0L);
/*      */     
/*      */     }
/* 2310 */     else if (!nativeThreads.containsKey(thread)) {
/* 2311 */       Pointer p = nativeThreadTerminationFlag.get();
/* 2312 */       nativeThreads.put(thread, p);
/* 2313 */       setDetachState(false, p.peer);
/*      */     } 
/*      */   } static synchronized native long createNativeCallback(Callback paramCallback, Method paramMethod, Class<?>[] paramArrayOfClass, Class<?> paramClass, int paramInt1, int paramInt2, String paramString); static native int invokeInt(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native long invokeLong(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native void invokeVoid(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native float invokeFloat(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native double invokeDouble(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native long invokePointer(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); private static native void invokeStructure(Function paramFunction, long paramLong1, int paramInt, Object[] paramArrayOfObject, long paramLong2, long paramLong3); static native Object invokeObject(Function paramFunction, long paramLong, int paramInt, Object[] paramArrayOfObject); static native long open(String paramString, int paramInt); static native void close(long paramLong); static native long findSymbol(long paramLong, String paramString); static native long indexOf(Pointer paramPointer, long paramLong1, long paramLong2, byte paramByte); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, byte[] paramArrayOfbyte, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, short[] paramArrayOfshort, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, char[] paramArrayOfchar, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, int[] paramArrayOfint, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, float[] paramArrayOffloat, int paramInt1, int paramInt2); static native void read(Pointer paramPointer, long paramLong1, long paramLong2, double[] paramArrayOfdouble, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, byte[] paramArrayOfbyte, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, short[] paramArrayOfshort, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, char[] paramArrayOfchar, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, int[] paramArrayOfint, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, long[] paramArrayOflong, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, float[] paramArrayOffloat, int paramInt1, int paramInt2); static native void write(Pointer paramPointer, long paramLong1, long paramLong2, double[] paramArrayOfdouble, int paramInt1, int paramInt2); static native byte getByte(Pointer paramPointer, long paramLong1, long paramLong2); static native char getChar(Pointer paramPointer, long paramLong1, long paramLong2); static native short getShort(Pointer paramPointer, long paramLong1, long paramLong2); static native int getInt(Pointer paramPointer, long paramLong1, long paramLong2); static native long getLong(Pointer paramPointer, long paramLong1, long paramLong2); static native float getFloat(Pointer paramPointer, long paramLong1, long paramLong2);
/*      */   static native double getDouble(Pointer paramPointer, long paramLong1, long paramLong2);
/*      */   private static native long _getPointer(long paramLong);
/*      */   static Pointer getTerminationFlag(Thread t) {
/* 2319 */     return nativeThreads.get(t);
/*      */   } static native String getWideString(Pointer paramPointer, long paramLong1, long paramLong2); static native byte[] getStringBytes(Pointer paramPointer, long paramLong1, long paramLong2); static native void setMemory(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3, byte paramByte); static native void setByte(Pointer paramPointer, long paramLong1, long paramLong2, byte paramByte); static native void setShort(Pointer paramPointer, long paramLong1, long paramLong2, short paramShort); static native void setChar(Pointer paramPointer, long paramLong1, long paramLong2, char paramChar); static native void setInt(Pointer paramPointer, long paramLong1, long paramLong2, int paramInt); static native void setLong(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3); static native void setFloat(Pointer paramPointer, long paramLong1, long paramLong2, float paramFloat); static native void setDouble(Pointer paramPointer, long paramLong1, long paramLong2, double paramDouble); static native void setPointer(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3); static native void setWideString(Pointer paramPointer, long paramLong1, long paramLong2, String paramString); static native ByteBuffer getDirectByteBuffer(Pointer paramPointer, long paramLong1, long paramLong2, long paramLong3); public static native long malloc(long paramLong);
/*      */   public static native void free(long paramLong);
/*      */   private static native void setDetachState(boolean paramBoolean, long paramLong);
/*      */   public static interface ffi_callback {
/*      */     void invoke(long param1Long1, long param1Long2, long param1Long3); }
/*      */   private static class Buffers { static boolean isBuffer(Class<?> cls) {
/* 2326 */       return Buffer.class.isAssignableFrom(cls);
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class AWT
/*      */   {
/*      */     static long getWindowID(Window w) throws HeadlessException {
/* 2335 */       return getComponentID(w);
/*      */     }
/*      */ 
/*      */     
/*      */     static long getComponentID(Object o) throws HeadlessException {
/* 2340 */       if (GraphicsEnvironment.isHeadless()) {
/* 2341 */         throw new HeadlessException("No native windows when headless");
/*      */       }
/* 2343 */       Component c = (Component)o;
/* 2344 */       if (c.isLightweight()) {
/* 2345 */         throw new IllegalArgumentException("Component must be heavyweight");
/*      */       }
/* 2347 */       if (!c.isDisplayable()) {
/* 2348 */         throw new IllegalStateException("Component must be displayable");
/*      */       }
/* 2350 */       if (Platform.isX11() && 
/* 2351 */         System.getProperty("java.version").startsWith("1.4") && 
/* 2352 */         !c.isVisible()) {
/* 2353 */         throw new IllegalStateException("Component must be visible");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2359 */       return Native.getWindowHandle0(c);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Native.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */