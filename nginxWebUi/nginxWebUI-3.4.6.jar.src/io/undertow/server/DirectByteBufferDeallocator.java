/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DirectByteBufferDeallocator
/*     */ {
/*     */   private static final boolean SUPPORTED;
/*     */   private static final Method cleaner;
/*     */   private static final Method cleanerClean;
/*     */   private static final Unsafe UNSAFE;
/*     */   
/*     */   static {
/*     */     boolean supported;
/*  24 */     String versionString = System.getProperty("java.specification.version");
/*  25 */     if (versionString.startsWith("1.")) {
/*  26 */       versionString = versionString.substring(2);
/*     */     }
/*  28 */     int version = Integer.parseInt(versionString);
/*     */     
/*  30 */     Method tmpCleaner = null;
/*  31 */     Method tmpCleanerClean = null;
/*     */     
/*  33 */     Unsafe tmpUnsafe = null;
/*  34 */     if (version < 9) {
/*     */       try {
/*  36 */         tmpCleaner = getAccesibleMethod("java.nio.DirectByteBuffer", "cleaner");
/*  37 */         tmpCleanerClean = getAccesibleMethod("sun.misc.Cleaner", "clean");
/*  38 */         supported = true;
/*  39 */       } catch (Throwable t) {
/*  40 */         UndertowLogger.ROOT_LOGGER.directBufferDeallocatorInitializationFailed(t);
/*  41 */         supported = false;
/*     */       } 
/*     */     } else {
/*     */       try {
/*  45 */         tmpUnsafe = getUnsafe();
/*  46 */         tmpCleanerClean = getDeclaredMethod(tmpUnsafe, "invokeCleaner", new Class[] { ByteBuffer.class });
/*  47 */         supported = true;
/*  48 */       } catch (Throwable t) {
/*  49 */         UndertowLogger.ROOT_LOGGER.directBufferDeallocatorInitializationFailed(t);
/*  50 */         supported = false;
/*     */       } 
/*     */     } 
/*  53 */     SUPPORTED = supported;
/*  54 */     cleaner = tmpCleaner;
/*  55 */     cleanerClean = tmpCleanerClean;
/*  56 */     UNSAFE = tmpUnsafe;
/*     */   }
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
/*     */   public static void free(ByteBuffer buffer) {
/*  71 */     if (SUPPORTED && buffer != null && buffer.isDirect()) {
/*     */       try {
/*  73 */         if (UNSAFE != null) {
/*     */           
/*  75 */           cleanerClean.invoke(UNSAFE, new Object[] { buffer });
/*     */         } else {
/*  77 */           Object cleaner = DirectByteBufferDeallocator.cleaner.invoke(buffer, new Object[0]);
/*  78 */           cleanerClean.invoke(cleaner, new Object[0]);
/*     */         } 
/*  80 */       } catch (Throwable t) {
/*  81 */         UndertowLogger.ROOT_LOGGER.directBufferDeallocationFailed(t);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static Unsafe getUnsafe() {
/*  87 */     if (System.getSecurityManager() != null) {
/*  88 */       return AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*     */             public Unsafe run() {
/*  90 */               return DirectByteBufferDeallocator.getUnsafe0();
/*     */             }
/*     */           });
/*     */     }
/*  94 */     return getUnsafe0();
/*     */   }
/*     */   
/*     */   private static Unsafe getUnsafe0() {
/*     */     try {
/*  99 */       Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
/* 100 */       theUnsafe.setAccessible(true);
/* 101 */       return (Unsafe)theUnsafe.get(null);
/* 102 */     } catch (Throwable t) {
/* 103 */       throw new RuntimeException("JDK did not allow accessing unsafe", t);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Method getAccesibleMethod(final String className, final String methodName) {
/* 108 */     if (System.getSecurityManager() != null) {
/* 109 */       return AccessController.<Method>doPrivileged(new PrivilegedAction<Method>()
/*     */           {
/*     */             public Method run() {
/* 112 */               return DirectByteBufferDeallocator.getAccesibleMethod0(className, methodName);
/*     */             }
/*     */           });
/*     */     }
/* 116 */     return getAccesibleMethod0(className, methodName);
/*     */   }
/*     */   
/*     */   private static Method getAccesibleMethod0(String className, String methodName) {
/*     */     try {
/* 121 */       Method method = Class.forName(className).getMethod(methodName, new Class[0]);
/* 122 */       method.setAccessible(true);
/* 123 */       return method;
/* 124 */     } catch (Throwable t) {
/* 125 */       throw new RuntimeException("JDK did not allow accessing method", t);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Method getDeclaredMethod(final Unsafe tmpUnsafe, final String methodName, Class<?>... parameterTypes) {
/* 130 */     if (System.getSecurityManager() != null) {
/* 131 */       return AccessController.<Method>doPrivileged(new PrivilegedAction<Method>()
/*     */           {
/*     */             public Method run() {
/* 134 */               return DirectByteBufferDeallocator.getDeclaredMethod0(tmpUnsafe, methodName, parameterTypes);
/*     */             }
/*     */           });
/*     */     }
/* 138 */     return getDeclaredMethod0(tmpUnsafe, methodName, parameterTypes);
/*     */   }
/*     */   
/*     */   private static Method getDeclaredMethod0(Unsafe tmpUnsafe, String methodName, Class<?>... parameterTypes) {
/*     */     try {
/* 143 */       Method method = tmpUnsafe.getClass().getDeclaredMethod(methodName, parameterTypes);
/* 144 */       method.setAccessible(true);
/* 145 */       return method;
/* 146 */     } catch (Throwable t) {
/* 147 */       throw new RuntimeException("JDK did not allow accessing method", t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\DirectByteBufferDeallocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */