/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.Deque;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.function.Supplier;
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
/*     */ public class AbbaDetector
/*     */ {
/*     */   private static final boolean TRACE = false;
/*  22 */   private static final ThreadLocal<Deque<Object>> STACK = ThreadLocal.withInitial(java.util.ArrayDeque::new);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   private static final Map<Object, Map<Object, Exception>> LOCK_ORDERING = new WeakHashMap<>();
/*     */ 
/*     */   
/*  32 */   private static final Set<String> KNOWN_DEADLOCKS = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object begin(Object<?> paramObject) {
/*  42 */     if (paramObject == null) {
/*  43 */       paramObject = (Object<?>)(new SecurityManager() {
/*  44 */           Class<?> clazz = getClassContext()[2];
/*     */         }).clazz;
/*     */     }
/*  47 */     Deque<Object> deque = STACK.get();
/*  48 */     if (!deque.isEmpty()) {
/*     */ 
/*     */       
/*  51 */       if (deque.contains(paramObject))
/*     */       {
/*  53 */         return paramObject;
/*     */       }
/*  55 */       while (!deque.isEmpty()) {
/*  56 */         Object object = deque.peek();
/*  57 */         if (Thread.holdsLock(object)) {
/*     */           break;
/*     */         }
/*  60 */         deque.pop();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     if (!deque.isEmpty()) {
/*  70 */       markHigher(paramObject, deque);
/*     */     }
/*  72 */     deque.push(paramObject);
/*  73 */     return paramObject;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Object getTest(Object paramObject) {
/*  78 */     return paramObject;
/*     */   }
/*     */   
/*     */   private static String getObjectName(Object paramObject) {
/*  82 */     return paramObject.getClass().getSimpleName() + "@" + System.identityHashCode(paramObject);
/*     */   }
/*     */   
/*     */   private static synchronized void markHigher(Object paramObject, Deque<Object> paramDeque) {
/*  86 */     Object object = getTest(paramObject);
/*  87 */     Map<Object, Object> map = (Map)LOCK_ORDERING.get(object);
/*  88 */     if (map == null) {
/*  89 */       map = new WeakHashMap<>();
/*  90 */       LOCK_ORDERING.put(object, map);
/*     */     } 
/*  92 */     Exception exception = null;
/*  93 */     for (Object object1 : paramDeque) {
/*  94 */       Object object2 = getTest(object1);
/*  95 */       if (object2 == object) {
/*     */         continue;
/*     */       }
/*  98 */       Map map1 = LOCK_ORDERING.get(object2);
/*  99 */       if (map1 != null) {
/* 100 */         Exception exception1 = (Exception)map1.get(object);
/* 101 */         if (exception1 != null) {
/* 102 */           String str = object.getClass() + " " + object2.getClass();
/* 103 */           if (!KNOWN_DEADLOCKS.contains(str)) {
/*     */             
/* 105 */             String str1 = getObjectName(object) + " synchronized after \n " + getObjectName(object2) + ", but in the past before";
/*     */             
/* 107 */             RuntimeException runtimeException = new RuntimeException(str1);
/* 108 */             runtimeException.initCause(exception1);
/* 109 */             runtimeException.printStackTrace(System.out);
/*     */             
/* 111 */             KNOWN_DEADLOCKS.add(str);
/*     */           } 
/*     */         } 
/*     */       } 
/* 115 */       if (!map.containsKey(object2)) {
/* 116 */         if (exception == null) {
/* 117 */           exception = new Exception("Before");
/*     */         }
/* 119 */         map.put(object2, exception);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\AbbaDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */