package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class ThrowableProxy implements IThrowableProxy {
   static final StackTraceElementProxy[] EMPTY_STEP = new StackTraceElementProxy[0];
   private Throwable throwable;
   private String className;
   private String message;
   StackTraceElementProxy[] stackTraceElementProxyArray;
   int commonFrames;
   private ThrowableProxy cause;
   private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];
   private ThrowableProxy[] suppressed;
   private transient PackagingDataCalculator packagingDataCalculator;
   private boolean calculatedPackageData;
   private boolean circular;
   private static final Method GET_SUPPRESSED_METHOD;

   public ThrowableProxy(Throwable throwable) {
      this(throwable, Collections.newSetFromMap(new IdentityHashMap()));
   }

   private ThrowableProxy(Throwable circular, boolean isCircular) {
      this.suppressed = NO_SUPPRESSED;
      this.calculatedPackageData = false;
      this.throwable = circular;
      this.className = circular.getClass().getName();
      this.message = circular.getMessage();
      this.stackTraceElementProxyArray = EMPTY_STEP;
      this.circular = true;
   }

   public ThrowableProxy(Throwable throwable, Set<Throwable> alreadyProcessedSet) {
      this.suppressed = NO_SUPPRESSED;
      this.calculatedPackageData = false;
      this.throwable = throwable;
      this.className = throwable.getClass().getName();
      this.message = throwable.getMessage();
      this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
      this.circular = false;
      alreadyProcessedSet.add(throwable);
      Throwable nested = throwable.getCause();
      if (nested != null) {
         if (alreadyProcessedSet.contains(nested)) {
            this.cause = new ThrowableProxy(nested, true);
         } else {
            this.cause = new ThrowableProxy(nested, alreadyProcessedSet);
            this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
         }
      }

      if (GET_SUPPRESSED_METHOD != null) {
         Throwable[] throwableSuppressed = this.extractSupressedThrowables(throwable);
         if (throwableSuppressed.length > 0) {
            List<ThrowableProxy> suppressedList = new ArrayList(throwableSuppressed.length);
            Throwable[] var6 = throwableSuppressed;
            int var7 = throwableSuppressed.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Throwable sup = var6[var8];
               ThrowableProxy throwableProxy;
               if (alreadyProcessedSet.contains(sup)) {
                  throwableProxy = new ThrowableProxy(sup, true);
                  suppressedList.add(throwableProxy);
               } else {
                  throwableProxy = new ThrowableProxy(sup, alreadyProcessedSet);
                  throwableProxy.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(sup.getStackTrace(), this.stackTraceElementProxyArray);
                  suppressedList.add(throwableProxy);
               }
            }

            this.suppressed = (ThrowableProxy[])suppressedList.toArray(new ThrowableProxy[suppressedList.size()]);
         }
      }

   }

   private Throwable[] extractSupressedThrowables(Throwable t) {
      try {
         Object obj = GET_SUPPRESSED_METHOD.invoke(t);
         if (obj instanceof Throwable[]) {
            Throwable[] throwableSuppressed = (Throwable[])((Throwable[])obj);
            return throwableSuppressed;
         }

         return null;
      } catch (IllegalAccessException var4) {
      } catch (IllegalArgumentException var5) {
      } catch (InvocationTargetException var6) {
      }

      return null;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public String getMessage() {
      return this.message;
   }

   public String getClassName() {
      return this.className;
   }

   public StackTraceElementProxy[] getStackTraceElementProxyArray() {
      return this.stackTraceElementProxyArray;
   }

   public boolean isCyclic() {
      return this.circular;
   }

   public int getCommonFrames() {
      return this.commonFrames;
   }

   public IThrowableProxy getCause() {
      return this.cause;
   }

   public IThrowableProxy[] getSuppressed() {
      return this.suppressed;
   }

   public PackagingDataCalculator getPackagingDataCalculator() {
      if (this.throwable != null && this.packagingDataCalculator == null) {
         this.packagingDataCalculator = new PackagingDataCalculator();
      }

      return this.packagingDataCalculator;
   }

   public void calculatePackagingData() {
      if (!this.calculatedPackageData) {
         PackagingDataCalculator pdc = this.getPackagingDataCalculator();
         if (pdc != null) {
            this.calculatedPackageData = true;
            pdc.calculate(this);
         }

      }
   }

   public void fullDump() {
      StringBuilder builder = new StringBuilder();
      StackTraceElementProxy[] var2 = this.stackTraceElementProxyArray;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         StackTraceElementProxy step = var2[var4];
         String string = step.toString();
         builder.append('\t').append(string);
         ThrowableProxyUtil.subjoinPackagingData(builder, step);
         builder.append(CoreConstants.LINE_SEPARATOR);
      }

      System.out.println(builder.toString());
   }

   static {
      Method method = null;

      try {
         method = Throwable.class.getMethod("getSuppressed");
      } catch (NoSuchMethodException var2) {
      }

      GET_SUPPRESSED_METHOD = method;
   }
}
