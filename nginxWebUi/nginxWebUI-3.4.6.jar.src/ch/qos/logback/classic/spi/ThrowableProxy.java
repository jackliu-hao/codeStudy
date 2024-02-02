/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class ThrowableProxy
/*     */   implements IThrowableProxy
/*     */ {
/*  28 */   static final StackTraceElementProxy[] EMPTY_STEP = new StackTraceElementProxy[0];
/*     */   
/*     */   private Throwable throwable;
/*     */   
/*     */   private String className;
/*     */   
/*     */   private String message;
/*     */   StackTraceElementProxy[] stackTraceElementProxyArray;
/*     */   int commonFrames;
/*     */   private ThrowableProxy cause;
/*  38 */   private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];
/*  39 */   private ThrowableProxy[] suppressed = NO_SUPPRESSED;
/*     */   
/*     */   private transient PackagingDataCalculator packagingDataCalculator;
/*     */   
/*     */   private boolean calculatedPackageData = false;
/*     */   
/*     */   private boolean circular;
/*     */   
/*     */   private static final Method GET_SUPPRESSED_METHOD;
/*     */ 
/*     */   
/*     */   static {
/*  51 */     Method method = null;
/*     */     try {
/*  53 */       method = Throwable.class.getMethod("getSuppressed", new Class[0]);
/*  54 */     } catch (NoSuchMethodException noSuchMethodException) {}
/*     */ 
/*     */     
/*  57 */     GET_SUPPRESSED_METHOD = method;
/*     */   }
/*     */ 
/*     */   
/*     */   public ThrowableProxy(Throwable throwable) {
/*  62 */     this(throwable, Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>()));
/*     */   }
/*     */ 
/*     */   
/*     */   private ThrowableProxy(Throwable circular, boolean isCircular) {
/*  67 */     this.throwable = circular;
/*  68 */     this.className = circular.getClass().getName();
/*  69 */     this.message = circular.getMessage();
/*  70 */     this.stackTraceElementProxyArray = EMPTY_STEP;
/*  71 */     this.circular = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ThrowableProxy(Throwable throwable, Set<Throwable> alreadyProcessedSet) {
/*  76 */     this.throwable = throwable;
/*  77 */     this.className = throwable.getClass().getName();
/*  78 */     this.message = throwable.getMessage();
/*  79 */     this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());
/*  80 */     this.circular = false;
/*     */     
/*  82 */     alreadyProcessedSet.add(throwable);
/*     */     
/*  84 */     Throwable nested = throwable.getCause();
/*  85 */     if (nested != null) {
/*  86 */       if (alreadyProcessedSet.contains(nested)) {
/*  87 */         this.cause = new ThrowableProxy(nested, true);
/*     */       } else {
/*  89 */         this.cause = new ThrowableProxy(nested, alreadyProcessedSet);
/*  90 */         this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(), this.stackTraceElementProxyArray);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  95 */     if (GET_SUPPRESSED_METHOD != null) {
/*     */       
/*  97 */       Throwable[] throwableSuppressed = extractSupressedThrowables(throwable);
/*     */       
/*  99 */       if (throwableSuppressed.length > 0) {
/* 100 */         List<ThrowableProxy> suppressedList = new ArrayList<ThrowableProxy>(throwableSuppressed.length);
/* 101 */         for (Throwable sup : throwableSuppressed) {
/* 102 */           if (alreadyProcessedSet.contains(sup)) {
/* 103 */             ThrowableProxy throwableProxy = new ThrowableProxy(sup, true);
/* 104 */             suppressedList.add(throwableProxy);
/*     */           } else {
/* 106 */             ThrowableProxy throwableProxy = new ThrowableProxy(sup, alreadyProcessedSet);
/* 107 */             throwableProxy.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(sup.getStackTrace(), this.stackTraceElementProxyArray);
/*     */             
/* 109 */             suppressedList.add(throwableProxy);
/*     */           } 
/*     */         } 
/* 112 */         this.suppressed = suppressedList.<ThrowableProxy>toArray(new ThrowableProxy[suppressedList.size()]);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private Throwable[] extractSupressedThrowables(Throwable t) {
/*     */     try {
/* 119 */       Object obj = GET_SUPPRESSED_METHOD.invoke(t, new Object[0]);
/* 120 */       if (obj instanceof Throwable[]) {
/* 121 */         Throwable[] throwableSuppressed = (Throwable[])obj;
/* 122 */         return throwableSuppressed;
/*     */       } 
/* 124 */       return null;
/*     */     }
/* 126 */     catch (IllegalAccessException illegalAccessException) {
/*     */     
/* 128 */     } catch (IllegalArgumentException illegalArgumentException) {
/*     */     
/* 130 */     } catch (InvocationTargetException invocationTargetException) {}
/*     */ 
/*     */ 
/*     */     
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Throwable getThrowable() {
/* 139 */     return this.throwable;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 143 */     return this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getClassName() {
/* 152 */     return this.className;
/*     */   }
/*     */   
/*     */   public StackTraceElementProxy[] getStackTraceElementProxyArray() {
/* 156 */     return this.stackTraceElementProxyArray;
/*     */   }
/*     */   
/*     */   public boolean isCyclic() {
/* 160 */     return this.circular;
/*     */   }
/*     */   
/*     */   public int getCommonFrames() {
/* 164 */     return this.commonFrames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IThrowableProxy getCause() {
/* 173 */     return this.cause;
/*     */   }
/*     */   
/*     */   public IThrowableProxy[] getSuppressed() {
/* 177 */     return (IThrowableProxy[])this.suppressed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PackagingDataCalculator getPackagingDataCalculator() {
/* 184 */     if (this.throwable != null && this.packagingDataCalculator == null) {
/* 185 */       this.packagingDataCalculator = new PackagingDataCalculator();
/*     */     }
/* 187 */     return this.packagingDataCalculator;
/*     */   }
/*     */   
/*     */   public void calculatePackagingData() {
/* 191 */     if (this.calculatedPackageData) {
/*     */       return;
/*     */     }
/* 194 */     PackagingDataCalculator pdc = getPackagingDataCalculator();
/* 195 */     if (pdc != null) {
/* 196 */       this.calculatedPackageData = true;
/* 197 */       pdc.calculate(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void fullDump() {
/* 202 */     StringBuilder builder = new StringBuilder();
/* 203 */     for (StackTraceElementProxy step : this.stackTraceElementProxyArray) {
/* 204 */       String string = step.toString();
/* 205 */       builder.append('\t').append(string);
/* 206 */       ThrowableProxyUtil.subjoinPackagingData(builder, step);
/* 207 */       builder.append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/* 209 */     System.out.println(builder.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\ThrowableProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */