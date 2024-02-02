package com.sun.jna.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectionUtils {
   private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());
   private static final Method METHOD_IS_DEFAULT;
   private static final Method METHOD_HANDLES_LOOKUP;
   private static final Method METHOD_HANDLES_LOOKUP_IN;
   private static final Method METHOD_HANDLES_PRIVATE_LOOKUP_IN;
   private static final Method METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL;
   private static final Method METHOD_HANDLES_LOOKUP_FIND_SPECIAL;
   private static final Method METHOD_HANDLES_BIND_TO;
   private static final Method METHOD_HANDLES_INVOKE_WITH_ARGUMENTS;
   private static final Method METHOD_TYPE;
   private static Constructor CONSTRUCTOR_LOOKUP_CLASS;

   private static Constructor getConstructorLookupClass() {
      if (CONSTRUCTOR_LOOKUP_CLASS == null) {
         Class lookup = lookupClass("java.lang.invoke.MethodHandles$Lookup");
         CONSTRUCTOR_LOOKUP_CLASS = lookupDeclaredConstructor(lookup, Class.class);
      }

      return CONSTRUCTOR_LOOKUP_CLASS;
   }

   private static Constructor lookupDeclaredConstructor(Class clazz, Class... arguments) {
      if (clazz == null) {
         LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[]{clazz, Arrays.toString(arguments)});
         return null;
      } else {
         try {
            Constructor init = clazz.getDeclaredConstructor(arguments);
            init.setAccessible(true);
            return init;
         } catch (Exception var3) {
            LOG.log(Level.FINE, "Failed to lookup method: <init>#{1}({2})", new Object[]{clazz, Arrays.toString(arguments)});
            return null;
         }
      }
   }

   private static Method lookupMethod(Class clazz, String methodName, Class... arguments) {
      if (clazz == null) {
         LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[]{clazz, methodName, Arrays.toString(arguments)});
         return null;
      } else {
         try {
            return clazz.getMethod(methodName, arguments);
         } catch (Exception var4) {
            LOG.log(Level.FINE, "Failed to lookup method: {0}#{1}({2})", new Object[]{clazz, methodName, Arrays.toString(arguments)});
            return null;
         }
      }
   }

   private static Class lookupClass(String name) {
      try {
         return Class.forName(name);
      } catch (ClassNotFoundException var2) {
         LOG.log(Level.FINE, "Failed to lookup class: " + name, var2);
         return null;
      }
   }

   public static boolean isDefault(Method method) {
      if (METHOD_IS_DEFAULT == null) {
         return false;
      } else {
         try {
            return (Boolean)METHOD_IS_DEFAULT.invoke(method);
         } catch (IllegalAccessException var3) {
            throw new RuntimeException(var3);
         } catch (IllegalArgumentException var4) {
            throw new RuntimeException(var4);
         } catch (InvocationTargetException var5) {
            Throwable cause = var5.getCause();
            if (cause instanceof RuntimeException) {
               throw (RuntimeException)cause;
            } else if (cause instanceof Error) {
               throw (Error)cause;
            } else {
               throw new RuntimeException(cause);
            }
         }
      }
   }

   public static Object getMethodHandle(Method method) throws Exception {
      assert isDefault(method);

      Object baseLookup = createLookup();

      Object lookup;
      try {
         Object lookup = createPrivateLookupIn(method.getDeclaringClass(), baseLookup);
         lookup = mhViaFindSpecial(lookup, method);
         return lookup;
      } catch (Exception var5) {
         lookup = getConstructorLookupClass().newInstance(method.getDeclaringClass());
         Object mh = mhViaUnreflectSpecial(lookup, method);
         return mh;
      }
   }

   private static Object mhViaFindSpecial(Object lookup, Method method) throws Exception {
      return METHOD_HANDLES_LOOKUP_FIND_SPECIAL.invoke(lookup, method.getDeclaringClass(), method.getName(), METHOD_TYPE.invoke((Object)null, method.getReturnType(), method.getParameterTypes()), method.getDeclaringClass());
   }

   private static Object mhViaUnreflectSpecial(Object lookup, Method method) throws Exception {
      Object l2 = METHOD_HANDLES_LOOKUP_IN.invoke(lookup, method.getDeclaringClass());
      return METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL.invoke(l2, method, method.getDeclaringClass());
   }

   private static Object createPrivateLookupIn(Class type, Object lookup) throws Exception {
      return METHOD_HANDLES_PRIVATE_LOOKUP_IN.invoke((Object)null, type, lookup);
   }

   private static Object createLookup() throws Exception {
      return METHOD_HANDLES_LOOKUP.invoke((Object)null);
   }

   public static Object invokeDefaultMethod(Object target, Object methodHandle, Object... args) throws Throwable {
      Object boundMethodHandle = METHOD_HANDLES_BIND_TO.invoke(methodHandle, target);
      return METHOD_HANDLES_INVOKE_WITH_ARGUMENTS.invoke(boundMethodHandle, (Object)args);
   }

   static {
      Class methodHandles = lookupClass("java.lang.invoke.MethodHandles");
      Class methodHandle = lookupClass("java.lang.invoke.MethodHandle");
      Class lookup = lookupClass("java.lang.invoke.MethodHandles$Lookup");
      Class methodType = lookupClass("java.lang.invoke.MethodType");
      METHOD_IS_DEFAULT = lookupMethod(Method.class, "isDefault");
      METHOD_HANDLES_LOOKUP = lookupMethod(methodHandles, "lookup");
      METHOD_HANDLES_LOOKUP_IN = lookupMethod(lookup, "in", Class.class);
      METHOD_HANDLES_LOOKUP_UNREFLECT_SPECIAL = lookupMethod(lookup, "unreflectSpecial", Method.class, Class.class);
      METHOD_HANDLES_LOOKUP_FIND_SPECIAL = lookupMethod(lookup, "findSpecial", Class.class, String.class, methodType, Class.class);
      METHOD_HANDLES_BIND_TO = lookupMethod(methodHandle, "bindTo", Object.class);
      METHOD_HANDLES_INVOKE_WITH_ARGUMENTS = lookupMethod(methodHandle, "invokeWithArguments", Object[].class);
      METHOD_HANDLES_PRIVATE_LOOKUP_IN = lookupMethod(methodHandles, "privateLookupIn", Class.class, lookup);
      METHOD_TYPE = lookupMethod(methodType, "methodType", Class.class, Class[].class);
   }
}
