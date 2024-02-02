package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.core._DelayedConversionToString;
import freemarker.core._DelayedJQuote;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class _MethodUtil {
   private _MethodUtil() {
   }

   public static int isMoreOrSameSpecificParameterType(Class specific, Class generic, boolean bugfixed, int ifHigherThan) {
      if (ifHigherThan >= 4) {
         return 0;
      } else if (generic.isAssignableFrom(specific)) {
         return generic == specific ? 1 : 4;
      } else {
         boolean specificIsPrim = specific.isPrimitive();
         boolean genericIsPrim = generic.isPrimitive();
         if (specificIsPrim) {
            if (genericIsPrim) {
               if (ifHigherThan >= 3) {
                  return 0;
               } else {
                  return isWideningPrimitiveNumberConversion(specific, generic) ? 3 : 0;
               }
            } else if (bugfixed) {
               Class specificAsBoxed = ClassUtil.primitiveClassToBoxingClass(specific);
               if (specificAsBoxed == generic) {
                  return 2;
               } else if (generic.isAssignableFrom(specificAsBoxed)) {
                  return 4;
               } else if (ifHigherThan >= 3) {
                  return 0;
               } else if (Number.class.isAssignableFrom(specificAsBoxed) && Number.class.isAssignableFrom(generic)) {
                  return isWideningBoxedNumberConversion(specificAsBoxed, generic) ? 3 : 0;
               } else {
                  return 0;
               }
            } else {
               return 0;
            }
         } else if (ifHigherThan >= 3) {
            return 0;
         } else if (bugfixed && !genericIsPrim && Number.class.isAssignableFrom(specific) && Number.class.isAssignableFrom(generic)) {
            return isWideningBoxedNumberConversion(specific, generic) ? 3 : 0;
         } else {
            return 0;
         }
      }
   }

   private static boolean isWideningPrimitiveNumberConversion(Class source, Class target) {
      if (target == Short.TYPE && source == Byte.TYPE) {
         return true;
      } else if (target == Integer.TYPE && (source == Short.TYPE || source == Byte.TYPE)) {
         return true;
      } else if (target == Long.TYPE && (source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE)) {
         return true;
      } else if (target != Float.TYPE || source != Long.TYPE && source != Integer.TYPE && source != Short.TYPE && source != Byte.TYPE) {
         return target == Double.TYPE && (source == Float.TYPE || source == Long.TYPE || source == Integer.TYPE || source == Short.TYPE || source == Byte.TYPE);
      } else {
         return true;
      }
   }

   private static boolean isWideningBoxedNumberConversion(Class source, Class target) {
      if (target == Short.class && source == Byte.class) {
         return true;
      } else if (target == Integer.class && (source == Short.class || source == Byte.class)) {
         return true;
      } else if (target == Long.class && (source == Integer.class || source == Short.class || source == Byte.class)) {
         return true;
      } else if (target != Float.class || source != Long.class && source != Integer.class && source != Short.class && source != Byte.class) {
         return target == Double.class && (source == Float.class || source == Long.class || source == Integer.class || source == Short.class || source == Byte.class);
      } else {
         return true;
      }
   }

   public static Set getAssignables(Class c1, Class c2) {
      Set s = new HashSet();
      collectAssignables(c1, c2, s);
      return s;
   }

   private static void collectAssignables(Class c1, Class c2, Set s) {
      if (c1.isAssignableFrom(c2)) {
         s.add(c1);
      }

      Class sc = c1.getSuperclass();
      if (sc != null) {
         collectAssignables(sc, c2, s);
      }

      Class[] itf = c1.getInterfaces();

      for(int i = 0; i < itf.length; ++i) {
         collectAssignables(itf[i], c2, s);
      }

   }

   public static Class[] getParameterTypes(Member member) {
      if (member instanceof Method) {
         return ((Method)member).getParameterTypes();
      } else if (member instanceof Constructor) {
         return ((Constructor)member).getParameterTypes();
      } else {
         throw new IllegalArgumentException("\"member\" must be Method or Constructor");
      }
   }

   public static boolean isVarargs(Member member) {
      if (member instanceof Method) {
         return ((Method)member).isVarArgs();
      } else if (member instanceof Constructor) {
         return ((Constructor)member).isVarArgs();
      } else {
         throw new BugException();
      }
   }

   public static String toString(Member member) {
      if (!(member instanceof Method) && !(member instanceof Constructor)) {
         throw new IllegalArgumentException("\"member\" must be a Method or Constructor");
      } else {
         StringBuilder sb = new StringBuilder();
         if ((member.getModifiers() & 8) != 0) {
            sb.append("static ");
         }

         String className = ClassUtil.getShortClassName(member.getDeclaringClass());
         if (className != null) {
            sb.append(className);
            sb.append('.');
         }

         sb.append(member.getName());
         sb.append('(');
         Class[] paramTypes = getParameterTypes(member);

         for(int i = 0; i < paramTypes.length; ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            String paramTypeDecl = ClassUtil.getShortClassName(paramTypes[i]);
            if (i == paramTypes.length - 1 && paramTypeDecl.endsWith("[]") && isVarargs(member)) {
               sb.append(paramTypeDecl.substring(0, paramTypeDecl.length() - 2));
               sb.append("...");
            } else {
               sb.append(paramTypeDecl);
            }
         }

         sb.append(')');
         return sb.toString();
      }
   }

   public static Object[] invocationErrorMessageStart(Member member) {
      return invocationErrorMessageStart(member, member instanceof Constructor);
   }

   private static Object[] invocationErrorMessageStart(Object member, boolean isConstructor) {
      return new Object[]{"Java ", isConstructor ? "constructor " : "method ", new _DelayedJQuote(member)};
   }

   public static TemplateModelException newInvocationTemplateModelException(Object object, Member member, Throwable e) {
      return newInvocationTemplateModelException(object, member, (member.getModifiers() & 8) != 0, member instanceof Constructor, e);
   }

   public static TemplateModelException newInvocationTemplateModelException(Object object, CallableMemberDescriptor callableMemberDescriptor, Throwable e) {
      return newInvocationTemplateModelException(object, new _DelayedConversionToString(callableMemberDescriptor) {
         protected String doConversion(Object callableMemberDescriptor) {
            return ((CallableMemberDescriptor)callableMemberDescriptor).getDeclaration();
         }
      }, callableMemberDescriptor.isStatic(), callableMemberDescriptor.isConstructor(), e);
   }

   private static TemplateModelException newInvocationTemplateModelException(Object parentObject, Object member, boolean isStatic, boolean isConstructor, Throwable e) {
      while(true) {
         if (e instanceof InvocationTargetException) {
            Throwable cause = ((InvocationTargetException)e).getTargetException();
            if (cause != null) {
               e = cause;
               continue;
            }
         }

         return new _TemplateModelException(e, new Object[]{invocationErrorMessageStart(member, isConstructor), " threw an exception", !isStatic && !isConstructor ? new Object[]{" when invoked on ", parentObject.getClass(), " object ", new _DelayedJQuote(parentObject)} : "", "; see cause exception in the Java stack trace."});
      }
   }

   public static String getBeanPropertyNameFromReaderMethodName(String name, Class<?> returnType) {
      byte start;
      if (name.startsWith("get")) {
         start = 3;
      } else {
         if (returnType != Boolean.TYPE || !name.startsWith("is")) {
            return null;
         }

         start = 2;
      }

      int ln = name.length();
      if (start == ln) {
         return null;
      } else {
         char c1 = name.charAt(start);
         return start + 1 < ln && Character.isUpperCase(name.charAt(start + 1)) && Character.isUpperCase(c1) ? name.substring(start) : (new StringBuilder(ln - start)).append(Character.toLowerCase(c1)).append(name, start + 1, ln).toString();
      }
   }

   public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Method method, Class<T> annotationClass) {
      T result = method.getAnnotation(annotationClass);
      return result != null ? result : getInheritableMethodAnnotation(contextClass, method.getName(), method.getParameterTypes(), true, annotationClass);
   }

   private static <T extends Annotation> T getInheritableMethodAnnotation(Class<?> contextClass, String methodName, Class<?>[] methodParamTypes, boolean skipCheckingDirectMethod, Class<T> annotationClass) {
      if (!skipCheckingDirectMethod) {
         Method similarMethod;
         try {
            similarMethod = contextClass.getMethod(methodName, methodParamTypes);
         } catch (NoSuchMethodException var12) {
            similarMethod = null;
         }

         if (similarMethod != null) {
            T result = similarMethod.getAnnotation(annotationClass);
            if (result != null) {
               return result;
            }
         }
      }

      Class[] var13 = contextClass.getInterfaces();
      int var14 = var13.length;

      for(int var7 = 0; var7 < var14; ++var7) {
         Class<?> anInterface = var13[var7];
         if (!anInterface.getName().startsWith("java.")) {
            Method similarInterfaceMethod;
            try {
               similarInterfaceMethod = anInterface.getMethod(methodName, methodParamTypes);
            } catch (NoSuchMethodException var11) {
               similarInterfaceMethod = null;
            }

            if (similarInterfaceMethod != null) {
               T result = similarInterfaceMethod.getAnnotation(annotationClass);
               if (result != null) {
                  return result;
               }
            }
         }
      }

      Class<?> superClass = contextClass.getSuperclass();
      if (superClass != Object.class && superClass != null) {
         return getInheritableMethodAnnotation(superClass, methodName, methodParamTypes, false, annotationClass);
      } else {
         return null;
      }
   }

   public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Constructor<?> constructor, Class<T> annotationClass) {
      T result = constructor.getAnnotation(annotationClass);
      if (result != null) {
         return result;
      } else {
         Class<?>[] paramTypes = constructor.getParameterTypes();

         while(true) {
            contextClass = contextClass.getSuperclass();
            if (contextClass == Object.class || contextClass == null) {
               return null;
            }

            try {
               constructor = contextClass.getConstructor(paramTypes);
            } catch (NoSuchMethodException var6) {
               constructor = null;
            }

            if (constructor != null) {
               result = constructor.getAnnotation(annotationClass);
               if (result != null) {
                  return result;
               }
            }
         }
      }
   }

   public static <T extends Annotation> T getInheritableAnnotation(Class<?> contextClass, Field field, Class<T> annotationClass) {
      T result = field.getAnnotation(annotationClass);
      return result != null ? result : getInheritableFieldAnnotation(contextClass, field.getName(), true, annotationClass);
   }

   private static <T extends Annotation> T getInheritableFieldAnnotation(Class<?> contextClass, String fieldName, boolean skipCheckingDirectField, Class<T> annotationClass) {
      if (!skipCheckingDirectField) {
         Field similarField;
         try {
            similarField = contextClass.getField(fieldName);
         } catch (NoSuchFieldException var11) {
            similarField = null;
         }

         if (similarField != null) {
            T result = similarField.getAnnotation(annotationClass);
            if (result != null) {
               return result;
            }
         }
      }

      Class[] var12 = contextClass.getInterfaces();
      int var13 = var12.length;

      for(int var6 = 0; var6 < var13; ++var6) {
         Class<?> anInterface = var12[var6];
         if (!anInterface.getName().startsWith("java.")) {
            Field similarInterfaceField;
            try {
               similarInterfaceField = anInterface.getField(fieldName);
            } catch (NoSuchFieldException var10) {
               similarInterfaceField = null;
            }

            if (similarInterfaceField != null) {
               T result = similarInterfaceField.getAnnotation(annotationClass);
               if (result != null) {
                  return result;
               }
            }
         }
      }

      Class<?> superClass = contextClass.getSuperclass();
      if (superClass != Object.class && superClass != null) {
         return getInheritableFieldAnnotation(superClass, fieldName, false, annotationClass);
      } else {
         return null;
      }
   }

   public static Method getMethodWithClosestNonSubReturnType(Class<?> returnType, Collection<Method> methods) {
      Iterator var2 = methods.iterator();

      Method result;
      while(var2.hasNext()) {
         result = (Method)var2.next();
         if (result.getReturnType() == returnType) {
            return result;
         }
      }

      if (returnType != Object.class && !returnType.isPrimitive()) {
         for(Class<?> superClass = returnType.getSuperclass(); superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            Iterator var7 = methods.iterator();

            while(var7.hasNext()) {
               Method method = (Method)var7.next();
               if (method.getReturnType() == superClass) {
                  return method;
               }
            }
         }

         result = getMethodWithClosestNonSubInterfaceReturnType(returnType, methods);
         if (result != null) {
            return result;
         } else {
            Iterator var8 = methods.iterator();

            Method method;
            do {
               if (!var8.hasNext()) {
                  return null;
               }

               method = (Method)var8.next();
            } while(method.getReturnType() != Object.class);

            return method;
         }
      } else {
         return null;
      }
   }

   private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods) {
      HashSet<Class<?>> nullResultReturnTypeInterfaces = new HashSet();

      do {
         Method result = getMethodWithClosestNonSubInterfaceReturnType(returnType, methods, nullResultReturnTypeInterfaces);
         if (result != null) {
            return result;
         }

         returnType = returnType.getSuperclass();
      } while(returnType != null);

      return null;
   }

   private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods, Set<Class<?>> nullResultReturnTypeInterfaces) {
      boolean returnTypeIsInterface = returnType.isInterface();
      if (returnTypeIsInterface) {
         if (nullResultReturnTypeInterfaces.contains(returnType)) {
            return null;
         }

         Iterator var4 = methods.iterator();

         while(var4.hasNext()) {
            Method method = (Method)var4.next();
            if (method.getReturnType() == returnType) {
               return method;
            }
         }
      }

      Class[] var9 = returnType.getInterfaces();
      int var10 = var9.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         Class<?> subInterface = var9[var6];
         Method result = getMethodWithClosestNonSubInterfaceReturnType(subInterface, methods, nullResultReturnTypeInterfaces);
         if (result != null) {
            return result;
         }
      }

      if (returnTypeIsInterface) {
         nullResultReturnTypeInterfaces.add(returnType);
      }

      return null;
   }
}
