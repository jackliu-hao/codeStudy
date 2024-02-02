package org.h2.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class AbbaDetector {
   private static final boolean TRACE = false;
   private static final ThreadLocal<Deque<Object>> STACK = ThreadLocal.withInitial(ArrayDeque::new);
   private static final Map<Object, Map<Object, Exception>> LOCK_ORDERING = new WeakHashMap();
   private static final Set<String> KNOWN_DEADLOCKS = new HashSet();

   public static Object begin(Object var0) {
      if (var0 == null) {
         var0 = (new SecurityManager() {
            Class<?> clazz = this.getClassContext()[2];
         }).clazz;
      }

      Deque var1 = (Deque)STACK.get();
      if (!var1.isEmpty()) {
         if (var1.contains(var0)) {
            return var0;
         }

         while(!var1.isEmpty()) {
            Object var2 = var1.peek();
            if (Thread.holdsLock(var2)) {
               break;
            }

            var1.pop();
         }
      }

      if (!var1.isEmpty()) {
         markHigher(var0, var1);
      }

      var1.push(var0);
      return var0;
   }

   private static Object getTest(Object var0) {
      return var0;
   }

   private static String getObjectName(Object var0) {
      return var0.getClass().getSimpleName() + "@" + System.identityHashCode(var0);
   }

   private static synchronized void markHigher(Object var0, Deque<Object> var1) {
      Object var2 = getTest(var0);
      Object var3 = (Map)LOCK_ORDERING.get(var2);
      if (var3 == null) {
         var3 = new WeakHashMap();
         LOCK_ORDERING.put(var2, var3);
      }

      Exception var4 = null;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Object var6 = var5.next();
         Object var7 = getTest(var6);
         if (var7 != var2) {
            Map var8 = (Map)LOCK_ORDERING.get(var7);
            if (var8 != null) {
               Exception var9 = (Exception)var8.get(var2);
               if (var9 != null) {
                  String var10 = var2.getClass() + " " + var7.getClass();
                  if (!KNOWN_DEADLOCKS.contains(var10)) {
                     String var11 = getObjectName(var2) + " synchronized after \n " + getObjectName(var7) + ", but in the past before";
                     RuntimeException var12 = new RuntimeException(var11);
                     var12.initCause(var9);
                     var12.printStackTrace(System.out);
                     KNOWN_DEADLOCKS.add(var10);
                  }
               }
            }

            if (!((Map)var3).containsKey(var7)) {
               if (var4 == null) {
                  var4 = new Exception("Before");
               }

               ((Map)var3).put(var7, var4);
            }
         }
      }

   }
}
