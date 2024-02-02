/*     */ package cn.hutool.system;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Singleton;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.SystemPropsUtil;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.management.ClassLoadingMXBean;
/*     */ import java.lang.management.CompilationMXBean;
/*     */ import java.lang.management.GarbageCollectorMXBean;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryMXBean;
/*     */ import java.lang.management.MemoryManagerMXBean;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.lang.management.OperatingSystemMXBean;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SystemUtil
/*     */   extends SystemPropsUtil
/*     */ {
/*     */   public static final String SPECIFICATION_NAME = "java.specification.name";
/*     */   public static final String VERSION = "java.version";
/*     */   public static final String SPECIFICATION_VERSION = "java.specification.version";
/*     */   public static final String VENDOR = "java.vendor";
/*     */   public static final String SPECIFICATION_VENDOR = "java.specification.vendor";
/*     */   public static final String VENDOR_URL = "java.vendor.url";
/*     */   public static final String HOME = "java.home";
/*     */   public static final String LIBRARY_PATH = "java.library.path";
/*     */   public static final String TMPDIR = "java.io.tmpdir";
/*     */   public static final String COMPILER = "java.compiler";
/*     */   public static final String EXT_DIRS = "java.ext.dirs";
/*     */   public static final String VM_NAME = "java.vm.name";
/*     */   public static final String VM_SPECIFICATION_NAME = "java.vm.specification.name";
/*     */   public static final String VM_VERSION = "java.vm.version";
/*     */   public static final String VM_SPECIFICATION_VERSION = "java.vm.specification.version";
/*     */   public static final String VM_VENDOR = "java.vm.vendor";
/*     */   public static final String VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
/*     */   public static final String CLASS_VERSION = "java.class.version";
/*     */   public static final String CLASS_PATH = "java.class.path";
/*     */   public static final String OS_NAME = "os.name";
/*     */   public static final String OS_ARCH = "os.arch";
/*     */   public static final String OS_VERSION = "os.version";
/*     */   public static final String FILE_SEPARATOR = "file.separator";
/*     */   public static final String PATH_SEPARATOR = "path.separator";
/*     */   public static final String LINE_SEPARATOR = "line.separator";
/*     */   public static final String USER_NAME = "user.name";
/*     */   public static final String USER_HOME = "user.home";
/*     */   public static final String USER_DIR = "user.dir";
/*     */   
/*     */   public static long getCurrentPID() {
/* 157 */     return Long.parseLong(getRuntimeMXBean().getName().split("@")[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoadingMXBean getClassLoadingMXBean() {
/* 167 */     return ManagementFactory.getClassLoadingMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MemoryMXBean getMemoryMXBean() {
/* 177 */     return ManagementFactory.getMemoryMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ThreadMXBean getThreadMXBean() {
/* 187 */     return ManagementFactory.getThreadMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeMXBean getRuntimeMXBean() {
/* 197 */     return ManagementFactory.getRuntimeMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CompilationMXBean getCompilationMXBean() {
/* 208 */     return ManagementFactory.getCompilationMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OperatingSystemMXBean getOperatingSystemMXBean() {
/* 218 */     return ManagementFactory.getOperatingSystemMXBean();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
/* 228 */     return ManagementFactory.getMemoryPoolMXBeans();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
/* 238 */     return ManagementFactory.getMemoryManagerMXBeans();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
/* 247 */     return ManagementFactory.getGarbageCollectorMXBeans();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JvmSpecInfo getJvmSpecInfo() {
/* 256 */     return (JvmSpecInfo)Singleton.get(JvmSpecInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JvmInfo getJvmInfo() {
/* 265 */     return (JvmInfo)Singleton.get(JvmInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaSpecInfo getJavaSpecInfo() {
/* 274 */     return (JavaSpecInfo)Singleton.get(JavaSpecInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaInfo getJavaInfo() {
/* 283 */     return (JavaInfo)Singleton.get(JavaInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JavaRuntimeInfo getJavaRuntimeInfo() {
/* 292 */     return (JavaRuntimeInfo)Singleton.get(JavaRuntimeInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OsInfo getOsInfo() {
/* 301 */     return (OsInfo)Singleton.get(OsInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UserInfo getUserInfo() {
/* 310 */     return (UserInfo)Singleton.get(UserInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HostInfo getHostInfo() {
/* 319 */     return (HostInfo)Singleton.get(HostInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeInfo getRuntimeInfo() {
/* 328 */     return (RuntimeInfo)Singleton.get(RuntimeInfo.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getTotalMemory() {
/* 338 */     return Runtime.getRuntime().totalMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getFreeMemory() {
/* 348 */     return Runtime.getRuntime().freeMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getMaxMemory() {
/* 358 */     return Runtime.getRuntime().maxMemory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getTotalThreadCount() {
/* 367 */     ThreadGroup parentThread = Thread.currentThread().getThreadGroup();
/* 368 */     while (null != parentThread.getParent()) {
/* 369 */       parentThread = parentThread.getParent();
/*     */     }
/* 371 */     return parentThread.activeCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dumpSystemInfo() {
/* 380 */     dumpSystemInfo(new PrintWriter(System.out));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dumpSystemInfo(PrintWriter out) {
/* 389 */     out.println("--------------");
/* 390 */     out.println(getJvmSpecInfo());
/* 391 */     out.println("--------------");
/* 392 */     out.println(getJvmInfo());
/* 393 */     out.println("--------------");
/* 394 */     out.println(getJavaSpecInfo());
/* 395 */     out.println("--------------");
/* 396 */     out.println(getJavaInfo());
/* 397 */     out.println("--------------");
/* 398 */     out.println(getJavaRuntimeInfo());
/* 399 */     out.println("--------------");
/* 400 */     out.println(getOsInfo());
/* 401 */     out.println("--------------");
/* 402 */     out.println(getUserInfo());
/* 403 */     out.println("--------------");
/* 404 */     out.println(getHostInfo());
/* 405 */     out.println("--------------");
/* 406 */     out.println(getRuntimeInfo());
/* 407 */     out.println("--------------");
/* 408 */     out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void append(StringBuilder builder, String caption, Object value) {
/* 419 */     builder.append(caption).append(StrUtil.nullToDefault(Convert.toStr(value), "[n/a]")).append("\n");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\SystemUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */