package ch.qos.logback.classic.spi;

import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import sun.reflect.Reflection;

public class PackagingDataCalculator {
   static final StackTraceElementProxy[] STEP_ARRAY_TEMPLATE = new StackTraceElementProxy[0];
   HashMap<String, ClassPackagingData> cache = new HashMap();
   private static boolean GET_CALLER_CLASS_METHOD_AVAILABLE = false;

   public void calculate(IThrowableProxy tp) {
      for(; tp != null; tp = tp.getCause()) {
         this.populateFrames(tp.getStackTraceElementProxyArray());
         IThrowableProxy[] suppressed = tp.getSuppressed();
         if (suppressed != null) {
            IThrowableProxy[] var3 = suppressed;
            int var4 = suppressed.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               IThrowableProxy current = var3[var5];
               this.populateFrames(current.getStackTraceElementProxyArray());
            }
         }
      }

   }

   void populateFrames(StackTraceElementProxy[] stepArray) {
      Throwable t = new Throwable("local stack reference");
      StackTraceElement[] localteSTEArray = t.getStackTrace();
      int commonFrames = STEUtil.findNumberOfCommonFrames(localteSTEArray, stepArray);
      int localFirstCommon = localteSTEArray.length - commonFrames;
      int stepFirstCommon = stepArray.length - commonFrames;
      ClassLoader lastExactClassLoader = null;
      ClassLoader firsExactClassLoader = null;
      int missfireCount = 0;

      for(int i = 0; i < commonFrames; ++i) {
         Class callerClass = null;
         if (GET_CALLER_CLASS_METHOD_AVAILABLE) {
            callerClass = Reflection.getCallerClass(localFirstCommon + i - missfireCount + 1);
         }

         StackTraceElementProxy step = stepArray[stepFirstCommon + i];
         String stepClassname = step.ste.getClassName();
         ClassPackagingData pi;
         if (callerClass != null && stepClassname.equals(callerClass.getName())) {
            lastExactClassLoader = callerClass.getClassLoader();
            if (firsExactClassLoader == null) {
               firsExactClassLoader = lastExactClassLoader;
            }

            pi = this.calculateByExactType(callerClass);
            step.setClassPackagingData(pi);
         } else {
            ++missfireCount;
            pi = this.computeBySTEP(step, lastExactClassLoader);
            step.setClassPackagingData(pi);
         }
      }

      this.populateUncommonFrames(commonFrames, stepArray, firsExactClassLoader);
   }

   void populateUncommonFrames(int commonFrames, StackTraceElementProxy[] stepArray, ClassLoader firstExactClassLoader) {
      int uncommonFrames = stepArray.length - commonFrames;

      for(int i = 0; i < uncommonFrames; ++i) {
         StackTraceElementProxy step = stepArray[i];
         ClassPackagingData pi = this.computeBySTEP(step, firstExactClassLoader);
         step.setClassPackagingData(pi);
      }

   }

   private ClassPackagingData calculateByExactType(Class type) {
      String className = type.getName();
      ClassPackagingData cpd = (ClassPackagingData)this.cache.get(className);
      if (cpd != null) {
         return cpd;
      } else {
         String version = this.getImplementationVersion(type);
         String codeLocation = this.getCodeLocation(type);
         cpd = new ClassPackagingData(codeLocation, version);
         this.cache.put(className, cpd);
         return cpd;
      }
   }

   private ClassPackagingData computeBySTEP(StackTraceElementProxy step, ClassLoader lastExactClassLoader) {
      String className = step.ste.getClassName();
      ClassPackagingData cpd = (ClassPackagingData)this.cache.get(className);
      if (cpd != null) {
         return cpd;
      } else {
         Class type = this.bestEffortLoadClass(lastExactClassLoader, className);
         String version = this.getImplementationVersion(type);
         String codeLocation = this.getCodeLocation(type);
         cpd = new ClassPackagingData(codeLocation, version, false);
         this.cache.put(className, cpd);
         return cpd;
      }
   }

   String getImplementationVersion(Class type) {
      if (type == null) {
         return "na";
      } else {
         Package aPackage = type.getPackage();
         if (aPackage != null) {
            String v = aPackage.getImplementationVersion();
            return v == null ? "na" : v;
         } else {
            return "na";
         }
      }
   }

   String getCodeLocation(Class type) {
      try {
         if (type != null) {
            CodeSource codeSource = type.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
               URL resource = codeSource.getLocation();
               if (resource != null) {
                  String locationStr = resource.toString();
                  String result = this.getCodeLocation(locationStr, '/');
                  if (result != null) {
                     return result;
                  }

                  return this.getCodeLocation(locationStr, '\\');
               }
            }
         }
      } catch (Exception var6) {
      }

      return "na";
   }

   private String getCodeLocation(String locationStr, char separator) {
      int idx = locationStr.lastIndexOf(separator);
      if (this.isFolder(idx, locationStr)) {
         idx = locationStr.lastIndexOf(separator, idx - 1);
         return locationStr.substring(idx + 1);
      } else {
         return idx > 0 ? locationStr.substring(idx + 1) : null;
      }
   }

   private boolean isFolder(int idx, String text) {
      return idx != -1 && idx + 1 == text.length();
   }

   private Class loadClass(ClassLoader cl, String className) {
      if (cl == null) {
         return null;
      } else {
         try {
            return cl.loadClass(className);
         } catch (ClassNotFoundException var4) {
            return null;
         } catch (NoClassDefFoundError var5) {
            return null;
         } catch (Exception var6) {
            var6.printStackTrace();
            return null;
         }
      }
   }

   private Class bestEffortLoadClass(ClassLoader lastGuaranteedClassLoader, String className) {
      Class result = this.loadClass(lastGuaranteedClassLoader, className);
      if (result != null) {
         return result;
      } else {
         ClassLoader tccl = Thread.currentThread().getContextClassLoader();
         if (tccl != lastGuaranteedClassLoader) {
            result = this.loadClass(tccl, className);
         }

         if (result != null) {
            return result;
         } else {
            try {
               return Class.forName(className);
            } catch (ClassNotFoundException var6) {
               return null;
            } catch (NoClassDefFoundError var7) {
               return null;
            } catch (Exception var8) {
               var8.printStackTrace();
               return null;
            }
         }
      }
   }

   static {
      try {
         Reflection.getCallerClass(2);
         GET_CALLER_CLASS_METHOD_AVAILABLE = true;
      } catch (NoClassDefFoundError var1) {
      } catch (NoSuchMethodError var2) {
      } catch (UnsupportedOperationException var3) {
      } catch (Throwable var4) {
         System.err.println("Unexpected exception");
         var4.printStackTrace();
      }

   }
}
