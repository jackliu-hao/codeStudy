package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodMap {
   private static final int MORE_SPECIFIC = 0;
   private static final int LESS_SPECIFIC = 1;
   private static final int INCOMPARABLE = 2;
   Map methodByNameMap = new Hashtable();
   // $FF: synthetic field
   static Class class$java$lang$Boolean;
   // $FF: synthetic field
   static Class class$java$lang$Character;
   // $FF: synthetic field
   static Class class$java$lang$Byte;
   // $FF: synthetic field
   static Class class$java$lang$Short;
   // $FF: synthetic field
   static Class class$java$lang$Integer;
   // $FF: synthetic field
   static Class class$java$lang$Long;
   // $FF: synthetic field
   static Class class$java$lang$Float;
   // $FF: synthetic field
   static Class class$java$lang$Double;

   public void add(Method method) {
      String methodName = method.getName();
      List l = this.get(methodName);
      if (l == null) {
         l = new ArrayList();
         this.methodByNameMap.put(methodName, l);
      }

      ((List)l).add(method);
   }

   public List get(String key) {
      return (List)this.methodByNameMap.get(key);
   }

   public Method find(String methodName, Object[] args) throws AmbiguousException {
      List methodList = this.get(methodName);
      if (methodList == null) {
         return null;
      } else {
         int l = args.length;
         Class[] classes = new Class[l];

         for(int i = 0; i < l; ++i) {
            Object arg = args[i];
            classes[i] = arg == null ? null : arg.getClass();
         }

         return getMostSpecific(methodList, classes);
      }
   }

   private static Method getMostSpecific(List var0, Class[] var1) throws AmbiguousException {
      // $FF: Couldn't be decompiled
   }

   private static int moreSpecific(Class[] c1, Class[] c2) {
      boolean c1MoreSpecific = false;
      boolean c2MoreSpecific = false;

      for(int i = 0; i < c1.length; ++i) {
         if (c1[i] != c2[i]) {
            c1MoreSpecific = c1MoreSpecific || isStrictMethodInvocationConvertible(c2[i], c1[i]);
            c2MoreSpecific = c2MoreSpecific || isStrictMethodInvocationConvertible(c1[i], c2[i]);
         }
      }

      if (c1MoreSpecific) {
         if (c2MoreSpecific) {
            return 2;
         } else {
            return 0;
         }
      } else if (c2MoreSpecific) {
         return 1;
      } else {
         return 2;
      }
   }

   private static LinkedList getApplicables(List methods, Class[] classes) {
      LinkedList list = new LinkedList();
      Iterator imethod = methods.iterator();

      while(imethod.hasNext()) {
         Method method = (Method)imethod.next();
         if (isApplicable(method, classes)) {
            list.add(method);
         }
      }

      return list;
   }

   private static boolean isApplicable(Method method, Class[] classes) {
      Class[] methodArgs = method.getParameterTypes();
      if (methodArgs.length != classes.length) {
         return false;
      } else {
         for(int i = 0; i < classes.length; ++i) {
            if (!isMethodInvocationConvertible(methodArgs[i], classes[i])) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isMethodInvocationConvertible(Class formal, Class actual) {
      if (actual == null && !formal.isPrimitive()) {
         return true;
      } else if (actual != null && formal.isAssignableFrom(actual)) {
         return true;
      } else {
         if (formal.isPrimitive()) {
            if (formal == Boolean.TYPE && actual == (class$java$lang$Boolean == null ? (class$java$lang$Boolean = class$("java.lang.Boolean")) : class$java$lang$Boolean)) {
               return true;
            }

            if (formal == Character.TYPE && actual == (class$java$lang$Character == null ? (class$java$lang$Character = class$("java.lang.Character")) : class$java$lang$Character)) {
               return true;
            }

            if (formal == Byte.TYPE && actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte)) {
               return true;
            }

            if (formal == Short.TYPE && (actual == (class$java$lang$Short == null ? (class$java$lang$Short = class$("java.lang.Short")) : class$java$lang$Short) || actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte))) {
               return true;
            }

            if (formal == Integer.TYPE && (actual == (class$java$lang$Integer == null ? (class$java$lang$Integer = class$("java.lang.Integer")) : class$java$lang$Integer) || actual == (class$java$lang$Short == null ? (class$java$lang$Short = class$("java.lang.Short")) : class$java$lang$Short) || actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte))) {
               return true;
            }

            if (formal == Long.TYPE && (actual == (class$java$lang$Long == null ? (class$java$lang$Long = class$("java.lang.Long")) : class$java$lang$Long) || actual == (class$java$lang$Integer == null ? (class$java$lang$Integer = class$("java.lang.Integer")) : class$java$lang$Integer) || actual == (class$java$lang$Short == null ? (class$java$lang$Short = class$("java.lang.Short")) : class$java$lang$Short) || actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte))) {
               return true;
            }

            if (formal == Float.TYPE && (actual == (class$java$lang$Float == null ? (class$java$lang$Float = class$("java.lang.Float")) : class$java$lang$Float) || actual == (class$java$lang$Long == null ? (class$java$lang$Long = class$("java.lang.Long")) : class$java$lang$Long) || actual == (class$java$lang$Integer == null ? (class$java$lang$Integer = class$("java.lang.Integer")) : class$java$lang$Integer) || actual == (class$java$lang$Short == null ? (class$java$lang$Short = class$("java.lang.Short")) : class$java$lang$Short) || actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte))) {
               return true;
            }

            if (formal == Double.TYPE && (actual == (class$java$lang$Double == null ? (class$java$lang$Double = class$("java.lang.Double")) : class$java$lang$Double) || actual == (class$java$lang$Float == null ? (class$java$lang$Float = class$("java.lang.Float")) : class$java$lang$Float) || actual == (class$java$lang$Long == null ? (class$java$lang$Long = class$("java.lang.Long")) : class$java$lang$Long) || actual == (class$java$lang$Integer == null ? (class$java$lang$Integer = class$("java.lang.Integer")) : class$java$lang$Integer) || actual == (class$java$lang$Short == null ? (class$java$lang$Short = class$("java.lang.Short")) : class$java$lang$Short) || actual == (class$java$lang$Byte == null ? (class$java$lang$Byte = class$("java.lang.Byte")) : class$java$lang$Byte))) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isStrictMethodInvocationConvertible(Class formal, Class actual) {
      if (actual == null && !formal.isPrimitive()) {
         return true;
      } else if (formal.isAssignableFrom(actual)) {
         return true;
      } else if (formal.isPrimitive()) {
         if (formal == Short.TYPE && actual == Byte.TYPE) {
            return true;
         } else if (formal == Integer.TYPE && (actual == Short.TYPE || actual == Byte.TYPE)) {
            return true;
         } else if (formal != Long.TYPE || actual != Integer.TYPE && actual != Short.TYPE && actual != Byte.TYPE) {
            if (formal == Float.TYPE && (actual == Long.TYPE || actual == Integer.TYPE || actual == Short.TYPE || actual == Byte.TYPE)) {
               return true;
            } else if (formal != Double.TYPE || actual != Float.TYPE && actual != Long.TYPE && actual != Integer.TYPE && actual != Short.TYPE && actual != Byte.TYPE) {
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public static class AmbiguousException extends Exception {
   }
}
