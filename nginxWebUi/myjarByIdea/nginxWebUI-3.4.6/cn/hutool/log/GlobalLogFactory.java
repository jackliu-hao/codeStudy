package cn.hutool.log;

public class GlobalLogFactory {
   private static volatile LogFactory currentLogFactory;
   private static final Object lock = new Object();

   public static LogFactory get() {
      if (null == currentLogFactory) {
         synchronized(lock) {
            if (null == currentLogFactory) {
               currentLogFactory = LogFactory.create();
            }
         }
      }

      return currentLogFactory;
   }

   public static LogFactory set(Class<? extends LogFactory> logFactoryClass) {
      try {
         return set((LogFactory)logFactoryClass.newInstance());
      } catch (Exception var2) {
         throw new IllegalArgumentException("Can not instance LogFactory class!", var2);
      }
   }

   public static LogFactory set(LogFactory logFactory) {
      logFactory.getLog(GlobalLogFactory.class).debug("Custom Use [{}] Logger.", new Object[]{logFactory.name});
      currentLogFactory = logFactory;
      return currentLogFactory;
   }
}
