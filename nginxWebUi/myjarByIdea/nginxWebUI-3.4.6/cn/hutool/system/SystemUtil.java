package cn.hutool.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import java.io.PrintWriter;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class SystemUtil extends SystemPropsUtil {
   public static final String SPECIFICATION_NAME = "java.specification.name";
   public static final String VERSION = "java.version";
   public static final String SPECIFICATION_VERSION = "java.specification.version";
   public static final String VENDOR = "java.vendor";
   public static final String SPECIFICATION_VENDOR = "java.specification.vendor";
   public static final String VENDOR_URL = "java.vendor.url";
   public static final String HOME = "java.home";
   public static final String LIBRARY_PATH = "java.library.path";
   public static final String TMPDIR = "java.io.tmpdir";
   public static final String COMPILER = "java.compiler";
   public static final String EXT_DIRS = "java.ext.dirs";
   public static final String VM_NAME = "java.vm.name";
   public static final String VM_SPECIFICATION_NAME = "java.vm.specification.name";
   public static final String VM_VERSION = "java.vm.version";
   public static final String VM_SPECIFICATION_VERSION = "java.vm.specification.version";
   public static final String VM_VENDOR = "java.vm.vendor";
   public static final String VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
   public static final String CLASS_VERSION = "java.class.version";
   public static final String CLASS_PATH = "java.class.path";
   public static final String OS_NAME = "os.name";
   public static final String OS_ARCH = "os.arch";
   public static final String OS_VERSION = "os.version";
   public static final String FILE_SEPARATOR = "file.separator";
   public static final String PATH_SEPARATOR = "path.separator";
   public static final String LINE_SEPARATOR = "line.separator";
   public static final String USER_NAME = "user.name";
   public static final String USER_HOME = "user.home";
   public static final String USER_DIR = "user.dir";

   public static long getCurrentPID() {
      return Long.parseLong(getRuntimeMXBean().getName().split("@")[0]);
   }

   public static ClassLoadingMXBean getClassLoadingMXBean() {
      return ManagementFactory.getClassLoadingMXBean();
   }

   public static MemoryMXBean getMemoryMXBean() {
      return ManagementFactory.getMemoryMXBean();
   }

   public static ThreadMXBean getThreadMXBean() {
      return ManagementFactory.getThreadMXBean();
   }

   public static RuntimeMXBean getRuntimeMXBean() {
      return ManagementFactory.getRuntimeMXBean();
   }

   public static CompilationMXBean getCompilationMXBean() {
      return ManagementFactory.getCompilationMXBean();
   }

   public static OperatingSystemMXBean getOperatingSystemMXBean() {
      return ManagementFactory.getOperatingSystemMXBean();
   }

   public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
      return ManagementFactory.getMemoryPoolMXBeans();
   }

   public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
      return ManagementFactory.getMemoryManagerMXBeans();
   }

   public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
      return ManagementFactory.getGarbageCollectorMXBeans();
   }

   public static JvmSpecInfo getJvmSpecInfo() {
      return (JvmSpecInfo)Singleton.get(JvmSpecInfo.class);
   }

   public static JvmInfo getJvmInfo() {
      return (JvmInfo)Singleton.get(JvmInfo.class);
   }

   public static JavaSpecInfo getJavaSpecInfo() {
      return (JavaSpecInfo)Singleton.get(JavaSpecInfo.class);
   }

   public static JavaInfo getJavaInfo() {
      return (JavaInfo)Singleton.get(JavaInfo.class);
   }

   public static JavaRuntimeInfo getJavaRuntimeInfo() {
      return (JavaRuntimeInfo)Singleton.get(JavaRuntimeInfo.class);
   }

   public static OsInfo getOsInfo() {
      return (OsInfo)Singleton.get(OsInfo.class);
   }

   public static UserInfo getUserInfo() {
      return (UserInfo)Singleton.get(UserInfo.class);
   }

   public static HostInfo getHostInfo() {
      return (HostInfo)Singleton.get(HostInfo.class);
   }

   public static RuntimeInfo getRuntimeInfo() {
      return (RuntimeInfo)Singleton.get(RuntimeInfo.class);
   }

   public static long getTotalMemory() {
      return Runtime.getRuntime().totalMemory();
   }

   public static long getFreeMemory() {
      return Runtime.getRuntime().freeMemory();
   }

   public static long getMaxMemory() {
      return Runtime.getRuntime().maxMemory();
   }

   public static int getTotalThreadCount() {
      ThreadGroup parentThread;
      for(parentThread = Thread.currentThread().getThreadGroup(); null != parentThread.getParent(); parentThread = parentThread.getParent()) {
      }

      return parentThread.activeCount();
   }

   public static void dumpSystemInfo() {
      dumpSystemInfo(new PrintWriter(System.out));
   }

   public static void dumpSystemInfo(PrintWriter out) {
      out.println("--------------");
      out.println(getJvmSpecInfo());
      out.println("--------------");
      out.println(getJvmInfo());
      out.println("--------------");
      out.println(getJavaSpecInfo());
      out.println("--------------");
      out.println(getJavaInfo());
      out.println("--------------");
      out.println(getJavaRuntimeInfo());
      out.println("--------------");
      out.println(getOsInfo());
      out.println("--------------");
      out.println(getUserInfo());
      out.println("--------------");
      out.println(getHostInfo());
      out.println("--------------");
      out.println(getRuntimeInfo());
      out.println("--------------");
      out.flush();
   }

   protected static void append(StringBuilder builder, String caption, Object value) {
      builder.append(caption).append(StrUtil.nullToDefault(Convert.toStr(value), "[n/a]")).append("\n");
   }
}
