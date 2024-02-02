/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.security.CodeSource;
/*     */ import java.util.HashMap;
/*     */ import sun.reflect.Reflection;
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
/*     */ public class PackagingDataCalculator
/*     */ {
/*  31 */   static final StackTraceElementProxy[] STEP_ARRAY_TEMPLATE = new StackTraceElementProxy[0];
/*     */   
/*  33 */   HashMap<String, ClassPackagingData> cache = new HashMap<String, ClassPackagingData>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean GET_CALLER_CLASS_METHOD_AVAILABLE = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*     */     
/*  45 */     try { Reflection.getCallerClass(2);
/*  46 */       GET_CALLER_CLASS_METHOD_AVAILABLE = true; }
/*  47 */     catch (NoClassDefFoundError noClassDefFoundError) {  }
/*  48 */     catch (NoSuchMethodError noSuchMethodError) {  }
/*  49 */     catch (UnsupportedOperationException unsupportedOperationException) {  }
/*  50 */     catch (Throwable e)
/*  51 */     { System.err.println("Unexpected exception");
/*  52 */       e.printStackTrace(); }
/*     */   
/*     */   }
/*     */   
/*     */   public void calculate(IThrowableProxy tp) {
/*  57 */     while (tp != null) {
/*  58 */       populateFrames(tp.getStackTraceElementProxyArray());
/*  59 */       IThrowableProxy[] suppressed = tp.getSuppressed();
/*  60 */       if (suppressed != null) {
/*  61 */         for (IThrowableProxy current : suppressed) {
/*  62 */           populateFrames(current.getStackTraceElementProxyArray());
/*     */         }
/*     */       }
/*  65 */       tp = tp.getCause();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void populateFrames(StackTraceElementProxy[] stepArray) {
/*  72 */     Throwable t = new Throwable("local stack reference");
/*  73 */     StackTraceElement[] localteSTEArray = t.getStackTrace();
/*  74 */     int commonFrames = STEUtil.findNumberOfCommonFrames(localteSTEArray, stepArray);
/*  75 */     int localFirstCommon = localteSTEArray.length - commonFrames;
/*  76 */     int stepFirstCommon = stepArray.length - commonFrames;
/*     */     
/*  78 */     ClassLoader lastExactClassLoader = null;
/*  79 */     ClassLoader firsExactClassLoader = null;
/*     */     
/*  81 */     int missfireCount = 0;
/*  82 */     for (int i = 0; i < commonFrames; i++) {
/*  83 */       Class callerClass = null;
/*  84 */       if (GET_CALLER_CLASS_METHOD_AVAILABLE) {
/*  85 */         callerClass = Reflection.getCallerClass(localFirstCommon + i - missfireCount + 1);
/*     */       }
/*  87 */       StackTraceElementProxy step = stepArray[stepFirstCommon + i];
/*  88 */       String stepClassname = step.ste.getClassName();
/*     */       
/*  90 */       if (callerClass != null && stepClassname.equals(callerClass.getName())) {
/*     */         
/*  92 */         lastExactClassLoader = callerClass.getClassLoader();
/*  93 */         if (firsExactClassLoader == null) {
/*  94 */           firsExactClassLoader = lastExactClassLoader;
/*     */         }
/*  96 */         ClassPackagingData pi = calculateByExactType(callerClass);
/*  97 */         step.setClassPackagingData(pi);
/*     */       } else {
/*  99 */         missfireCount++;
/* 100 */         ClassPackagingData pi = computeBySTEP(step, lastExactClassLoader);
/* 101 */         step.setClassPackagingData(pi);
/*     */       } 
/*     */     } 
/* 104 */     populateUncommonFrames(commonFrames, stepArray, firsExactClassLoader);
/*     */   }
/*     */   
/*     */   void populateUncommonFrames(int commonFrames, StackTraceElementProxy[] stepArray, ClassLoader firstExactClassLoader) {
/* 108 */     int uncommonFrames = stepArray.length - commonFrames;
/* 109 */     for (int i = 0; i < uncommonFrames; i++) {
/* 110 */       StackTraceElementProxy step = stepArray[i];
/* 111 */       ClassPackagingData pi = computeBySTEP(step, firstExactClassLoader);
/* 112 */       step.setClassPackagingData(pi);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ClassPackagingData calculateByExactType(Class type) {
/* 117 */     String className = type.getName();
/* 118 */     ClassPackagingData cpd = this.cache.get(className);
/* 119 */     if (cpd != null) {
/* 120 */       return cpd;
/*     */     }
/* 122 */     String version = getImplementationVersion(type);
/* 123 */     String codeLocation = getCodeLocation(type);
/* 124 */     cpd = new ClassPackagingData(codeLocation, version);
/* 125 */     this.cache.put(className, cpd);
/* 126 */     return cpd;
/*     */   }
/*     */   
/*     */   private ClassPackagingData computeBySTEP(StackTraceElementProxy step, ClassLoader lastExactClassLoader) {
/* 130 */     String className = step.ste.getClassName();
/* 131 */     ClassPackagingData cpd = this.cache.get(className);
/* 132 */     if (cpd != null) {
/* 133 */       return cpd;
/*     */     }
/* 135 */     Class type = bestEffortLoadClass(lastExactClassLoader, className);
/* 136 */     String version = getImplementationVersion(type);
/* 137 */     String codeLocation = getCodeLocation(type);
/* 138 */     cpd = new ClassPackagingData(codeLocation, version, false);
/* 139 */     this.cache.put(className, cpd);
/* 140 */     return cpd;
/*     */   }
/*     */   
/*     */   String getImplementationVersion(Class type) {
/* 144 */     if (type == null) {
/* 145 */       return "na";
/*     */     }
/* 147 */     Package aPackage = type.getPackage();
/* 148 */     if (aPackage != null) {
/* 149 */       String v = aPackage.getImplementationVersion();
/* 150 */       if (v == null) {
/* 151 */         return "na";
/*     */       }
/* 153 */       return v;
/*     */     } 
/*     */     
/* 156 */     return "na";
/*     */   }
/*     */ 
/*     */   
/*     */   String getCodeLocation(Class type) {
/*     */     try {
/* 162 */       if (type != null) {
/*     */         
/* 164 */         CodeSource codeSource = type.getProtectionDomain().getCodeSource();
/* 165 */         if (codeSource != null) {
/* 166 */           URL resource = codeSource.getLocation();
/* 167 */           if (resource != null) {
/* 168 */             String locationStr = resource.toString();
/*     */             
/* 170 */             String result = getCodeLocation(locationStr, '/');
/* 171 */             if (result != null) {
/* 172 */               return result;
/*     */             }
/* 174 */             return getCodeLocation(locationStr, '\\');
/*     */           } 
/*     */         } 
/*     */       } 
/* 178 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 181 */     return "na";
/*     */   }
/*     */   
/*     */   private String getCodeLocation(String locationStr, char separator) {
/* 185 */     int idx = locationStr.lastIndexOf(separator);
/* 186 */     if (isFolder(idx, locationStr)) {
/* 187 */       idx = locationStr.lastIndexOf(separator, idx - 1);
/* 188 */       return locationStr.substring(idx + 1);
/* 189 */     }  if (idx > 0) {
/* 190 */       return locationStr.substring(idx + 1);
/*     */     }
/* 192 */     return null;
/*     */   }
/*     */   
/*     */   private boolean isFolder(int idx, String text) {
/* 196 */     return (idx != -1 && idx + 1 == text.length());
/*     */   }
/*     */   
/*     */   private Class loadClass(ClassLoader cl, String className) {
/* 200 */     if (cl == null) {
/* 201 */       return null;
/*     */     }
/*     */     try {
/* 204 */       return cl.loadClass(className);
/* 205 */     } catch (ClassNotFoundException e1) {
/* 206 */       return null;
/* 207 */     } catch (NoClassDefFoundError e1) {
/* 208 */       return null;
/* 209 */     } catch (Exception e) {
/* 210 */       e.printStackTrace();
/* 211 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class bestEffortLoadClass(ClassLoader lastGuaranteedClassLoader, String className) {
/* 222 */     Class result = loadClass(lastGuaranteedClassLoader, className);
/* 223 */     if (result != null) {
/* 224 */       return result;
/*     */     }
/* 226 */     ClassLoader tccl = Thread.currentThread().getContextClassLoader();
/* 227 */     if (tccl != lastGuaranteedClassLoader) {
/* 228 */       result = loadClass(tccl, className);
/*     */     }
/* 230 */     if (result != null) {
/* 231 */       return result;
/*     */     }
/*     */     
/*     */     try {
/* 235 */       return Class.forName(className);
/* 236 */     } catch (ClassNotFoundException e1) {
/* 237 */       return null;
/* 238 */     } catch (NoClassDefFoundError e1) {
/* 239 */       return null;
/* 240 */     } catch (Exception e) {
/* 241 */       e.printStackTrace();
/* 242 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\PackagingDataCalculator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */