/*     */ package org.codehaus.plexus.util.introspection;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class MethodMap
/*     */ {
/*     */   private static final int MORE_SPECIFIC = 0;
/*     */   private static final int LESS_SPECIFIC = 1;
/*     */   private static final int INCOMPARABLE = 2;
/*  45 */   Map methodByNameMap = new Hashtable();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Method method) {
/*  54 */     String methodName = method.getName();
/*     */     
/*  56 */     List l = get(methodName);
/*     */     
/*  58 */     if (l == null) {
/*     */       
/*  60 */       l = new ArrayList();
/*  61 */       this.methodByNameMap.put(methodName, l);
/*     */     } 
/*     */     
/*  64 */     l.add(method);
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
/*     */   public List get(String key) {
/*  77 */     return (List)this.methodByNameMap.get(key);
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
/*     */   public Method find(String methodName, Object[] args) throws AmbiguousException {
/* 111 */     List methodList = get(methodName);
/*     */     
/* 113 */     if (methodList == null)
/*     */     {
/* 115 */       return null;
/*     */     }
/*     */     
/* 118 */     int l = args.length;
/* 119 */     Class[] classes = new Class[l];
/*     */     
/* 121 */     for (int i = 0; i < l; i++) {
/*     */       
/* 123 */       Object arg = args[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 129 */       classes[i] = (arg == null) ? null : arg.getClass();
/*     */     } 
/*     */ 
/*     */     
/* 133 */     return getMostSpecific(methodList, classes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class AmbiguousException
/*     */     extends Exception {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method getMostSpecific(List methods, Class[] classes) throws AmbiguousException {
/* 148 */     LinkedList applicables = getApplicables(methods, classes);
/*     */     
/* 150 */     if (applicables.isEmpty())
/*     */     {
/* 152 */       return null;
/*     */     }
/*     */     
/* 155 */     if (applicables.size() == 1)
/*     */     {
/* 157 */       return applicables.getFirst();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 166 */     LinkedList maximals = new LinkedList();
/*     */     
/* 168 */     Iterator applicable = applicables.iterator();
/* 169 */     while (applicable.hasNext()) {
/*     */       
/* 171 */       Method app = applicable.next();
/* 172 */       Class[] appArgs = app.getParameterTypes();
/* 173 */       boolean lessSpecific = false;
/*     */       
/* 175 */       Iterator maximal = maximals.iterator();
/* 176 */       while (!lessSpecific && maximal.hasNext()) {
/*     */         
/* 178 */         Method max = maximal.next();
/*     */         
/* 180 */         switch (moreSpecific(appArgs, max.getParameterTypes())) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case 0:
/* 189 */             maximal.remove();
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
/*     */           case 1:
/* 202 */             lessSpecific = true;
/*     */         } 
/*     */ 
/*     */ 
/*     */       
/*     */       } 
/* 208 */       if (!lessSpecific)
/*     */       {
/* 210 */         maximals.addLast(app);
/*     */       }
/*     */     } 
/*     */     
/* 214 */     if (maximals.size() > 1)
/*     */     {
/*     */       
/* 217 */       throw new AmbiguousException();
/*     */     }
/*     */     
/* 220 */     return maximals.getFirst();
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
/*     */   private static int moreSpecific(Class[] c1, Class[] c2) {
/* 233 */     boolean c1MoreSpecific = false;
/* 234 */     boolean c2MoreSpecific = false;
/*     */     
/* 236 */     for (int i = 0; i < c1.length; i++) {
/*     */       
/* 238 */       if (c1[i] != c2[i]) {
/*     */         
/* 240 */         c1MoreSpecific = (c1MoreSpecific || isStrictMethodInvocationConvertible(c2[i], c1[i]));
/*     */ 
/*     */         
/* 243 */         c2MoreSpecific = (c2MoreSpecific || isStrictMethodInvocationConvertible(c1[i], c2[i]));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 249 */     if (c1MoreSpecific) {
/*     */       
/* 251 */       if (c2MoreSpecific)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 258 */         return 2;
/*     */       }
/*     */       
/* 261 */       return 0;
/*     */     } 
/*     */     
/* 264 */     if (c2MoreSpecific)
/*     */     {
/* 266 */       return 1;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 274 */     return 2;
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
/*     */   private static LinkedList getApplicables(List methods, Class[] classes) {
/* 287 */     LinkedList list = new LinkedList();
/*     */     
/* 289 */     for (Iterator imethod = methods.iterator(); imethod.hasNext(); ) {
/*     */       
/* 291 */       Method method = imethod.next();
/*     */       
/* 293 */       if (isApplicable(method, classes))
/*     */       {
/* 295 */         list.add(method);
/*     */       }
/*     */     } 
/*     */     
/* 299 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isApplicable(Method method, Class[] classes) {
/* 308 */     Class[] methodArgs = method.getParameterTypes();
/*     */     
/* 310 */     if (methodArgs.length != classes.length)
/*     */     {
/* 312 */       return false;
/*     */     }
/*     */     
/* 315 */     for (int i = 0; i < classes.length; i++) {
/*     */       
/* 317 */       if (!isMethodInvocationConvertible(methodArgs[i], classes[i]))
/*     */       {
/* 319 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 323 */     return true;
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
/*     */   private static boolean isMethodInvocationConvertible(Class formal, Class actual) {
/* 350 */     if (actual == null && !formal.isPrimitive())
/*     */     {
/* 352 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 359 */     if (actual != null && formal.isAssignableFrom(actual))
/*     */     {
/* 361 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 369 */     if (formal.isPrimitive()) {
/*     */       
/* 371 */       if (formal == boolean.class && actual == Boolean.class)
/* 372 */         return true; 
/* 373 */       if (formal == char.class && actual == Character.class)
/* 374 */         return true; 
/* 375 */       if (formal == byte.class && actual == Byte.class)
/* 376 */         return true; 
/* 377 */       if (formal == short.class && (actual == Short.class || actual == Byte.class))
/*     */       {
/* 379 */         return true; } 
/* 380 */       if (formal == int.class && (actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */         
/* 383 */         return true; } 
/* 384 */       if (formal == long.class && (actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */         
/* 387 */         return true; } 
/* 388 */       if (formal == float.class && (actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */ 
/*     */         
/* 392 */         return true; } 
/* 393 */       if (formal == double.class && (actual == Double.class || actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */ 
/*     */         
/* 397 */         return true;
/*     */       }
/*     */     } 
/* 400 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isStrictMethodInvocationConvertible(Class formal, Class actual) {
/* 423 */     if (actual == null && !formal.isPrimitive())
/*     */     {
/* 425 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 432 */     if (formal.isAssignableFrom(actual))
/*     */     {
/* 434 */       return true;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 441 */     if (formal.isPrimitive()) {
/*     */       
/* 443 */       if (formal == short.class && actual == byte.class)
/* 444 */         return true; 
/* 445 */       if (formal == int.class && (actual == short.class || actual == byte.class))
/*     */       {
/* 447 */         return true; } 
/* 448 */       if (formal == long.class && (actual == int.class || actual == short.class || actual == byte.class))
/*     */       {
/*     */         
/* 451 */         return true; } 
/* 452 */       if (formal == float.class && (actual == long.class || actual == int.class || actual == short.class || actual == byte.class))
/*     */       {
/*     */         
/* 455 */         return true; } 
/* 456 */       if (formal == double.class && (actual == float.class || actual == long.class || actual == int.class || actual == short.class || actual == byte.class))
/*     */       {
/*     */ 
/*     */         
/* 460 */         return true; } 
/*     */     } 
/* 462 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\introspection\MethodMap.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */