package org.noear.solon.aspect.asm;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.noear.solon.core.AopContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class AsmProxy {
   public static final int ASM_VERSION = 524288;
   public static final int ASM_JDK_VERSION = 52;
   public static final String PROXY_CLASSNAME_PREFIX = "$Proxy_";
   private static final String FIELD_INVOCATIONHANDLER = "invocationHandler";
   private static final String METHOD_SETTER = "setInvocationHandler";
   private static final String METHOD_INVOKE = "invokeInvocationHandler";
   private static final String METHOD_INVOKE_DESC = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
   private static final String METHOD_FIELD_PREFIX = "method";
   private static final Map<String, Class<?>> proxyClassCache = new HashMap();

   private static void saveProxyClassCache(ClassLoader classLoader, Class<?> targetClass, Class<?> proxyClass) {
      String key = classLoader.toString() + "_" + targetClass.getName();
      proxyClassCache.put(key, proxyClass);
   }

   private static Class<?> getProxyClassCache(ClassLoader classLoader, Class<?> targetClass) {
      String key = classLoader.toString() + "_" + targetClass.getName();
      return (Class)proxyClassCache.get(key);
   }

   public static Object newProxyInstance(AopContext context, InvocationHandler invocationHandler, Class<?> targetClass, Constructor<?> targetConstructor, Object... targetParam) {
      if (targetClass != null && invocationHandler != null) {
         AsmProxyClassLoader classLoader = (AsmProxyClassLoader)context.getAttrs().get(AsmProxyClassLoader.class);
         if (classLoader == null) {
            classLoader = new AsmProxyClassLoader(context.getClassLoader());
            context.getAttrs().put(AsmProxyClassLoader.class, classLoader);
         }

         try {
            Class<?> proxyClass = getProxyClassCache(classLoader, targetClass);
            if (proxyClass != null) {
               return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
            } else {
               ClassReader reader = null;
               String resourceName = targetClass.getName().replace('.', '/') + ".class";
               InputStream resourceStream = classLoader.getResourceAsStream(resourceName);
               Throwable var10 = null;

               try {
                  reader = new ClassReader(resourceStream);
               } catch (Throwable var28) {
                  var10 = var28;
                  throw var28;
               } finally {
                  if (resourceStream != null) {
                     if (var10 != null) {
                        try {
                           resourceStream.close();
                        } catch (Throwable var27) {
                           var10.addSuppressed(var27);
                        }
                     } else {
                        resourceStream.close();
                     }
                  }

               }

               TargetClassVisitor targetClassVisitor = new TargetClassVisitor();
               reader.accept(targetClassVisitor, 2);
               if (targetClassVisitor.isFinal()) {
                  throw new IllegalArgumentException("class is final");
               } else {
                  ClassWriter writer = new ClassWriter(3);
                  String newClassName = generateProxyClassName(targetClass);
                  String newClassInnerName = newClassName.replace(".", "/");
                  String targetClassName = targetClass.getName();
                  String targetClassInnerName = Type.getInternalName(targetClass);
                  newClass(writer, newClassInnerName, targetClassInnerName);
                  addField(writer);
                  addSetterMethod(writer, newClassInnerName);
                  List<MethodBean> constructors = targetClassVisitor.getConstructors();
                  addConstructor(writer, constructors, targetClassInnerName);
                  addInvokeMethod(writer, newClassInnerName);
                  List<MethodBean> methods = targetClassVisitor.getMethods();
                  List<MethodBean> declaredMethods = targetClassVisitor.getDeclaredMethods();
                  Map<Integer, Integer> methodsMap = new HashMap();
                  Map<Integer, Integer> declaredMethodsMap = new HashMap();
                  int methodNameIndex = 0;
                  methodNameIndex = addMethod(writer, newClassInnerName, targetClass.getMethods(), methods, true, methodNameIndex, methodsMap);
                  addMethod(writer, newClassInnerName, targetClass.getDeclaredMethods(), declaredMethods, false, methodNameIndex, declaredMethodsMap);
                  addStaticInitBlock(writer, targetClassName, newClassInnerName, methodsMap, declaredMethodsMap);
                  byte[] bytes = writer.toByteArray();
                  proxyClass = classLoader.transfer2Class(bytes);
                  saveProxyClassCache(classLoader, targetClass, proxyClass);
                  return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
               }
            }
         } catch (Exception var30) {
            var30.printStackTrace();
            return null;
         }
      } else {
         throw new IllegalArgumentException("argument is null");
      }
   }

   private static String generateProxyClassName(Class<?> targetClass) {
      return targetClass.getPackage().getName() + "." + "$Proxy_" + targetClass.getSimpleName();
   }

   private static Object newInstance(Class<?> proxyClass, InvocationHandler invocationHandler, Constructor<?> targetConstructor, Object... targetParam) throws Exception {
      Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
      Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
      Object instance = constructor.newInstance(targetParam);
      Method setterMethod = proxyClass.getDeclaredMethod("setInvocationHandler", InvocationHandler.class);
      setterMethod.setAccessible(true);
      setterMethod.invoke(instance, invocationHandler);
      return instance;
   }

   private static void newClass(ClassWriter writer, String newClassName, String targetClassName) throws Exception {
      int access = 17;
      writer.visit(52, access, newClassName, (String)null, targetClassName, (String[])null);
   }

   private static void addField(ClassWriter writer) throws Exception {
      FieldVisitor fieldVisitor = writer.visitField(2, "invocationHandler", Type.getDescriptor(InvocationHandler.class), (String)null, (Object)null);
      fieldVisitor.visitEnd();
   }

   private static void addSetterMethod(ClassWriter writer, String owner) throws Exception {
      String methodDesc = "(" + Type.getDescriptor(InvocationHandler.class) + ")V";
      MethodVisitor methodVisitor = writer.visitMethod(1, "setInvocationHandler", methodDesc, (String)null, (String[])null);
      methodVisitor.visitCode();
      methodVisitor.visitVarInsn(25, 0);
      methodVisitor.visitVarInsn(25, 1);
      methodVisitor.visitFieldInsn(181, owner, "invocationHandler", Type.getDescriptor(InvocationHandler.class));
      methodVisitor.visitInsn(177);
      methodVisitor.visitMaxs(2, 2);
      methodVisitor.visitEnd();
   }

   private static void addConstructor(ClassWriter writer, List<MethodBean> constructors, String targetClassInnerName) throws Exception {
      Iterator var3 = constructors.iterator();

      while(var3.hasNext()) {
         MethodBean constructor = (MethodBean)var3.next();
         Type[] argumentTypes = Type.getArgumentTypes(constructor.methodDesc);
         MethodVisitor methodVisitor = writer.visitMethod(1, "<init>", constructor.methodDesc, (String)null, (String[])null);
         methodVisitor.visitCode();
         methodVisitor.visitVarInsn(25, 0);

         for(int i = 0; i < argumentTypes.length; ++i) {
            Type argumentType = argumentTypes[i];
            if (!argumentType.equals(Type.BYTE_TYPE) && !argumentType.equals(Type.BOOLEAN_TYPE) && !argumentType.equals(Type.CHAR_TYPE) && !argumentType.equals(Type.SHORT_TYPE) && !argumentType.equals(Type.INT_TYPE)) {
               if (argumentType.equals(Type.LONG_TYPE)) {
                  methodVisitor.visitVarInsn(22, i + 1);
               } else if (argumentType.equals(Type.FLOAT_TYPE)) {
                  methodVisitor.visitVarInsn(23, i + 1);
               } else if (argumentType.equals(Type.DOUBLE_TYPE)) {
                  methodVisitor.visitVarInsn(24, i + 1);
               } else {
                  methodVisitor.visitVarInsn(25, i + 1);
               }
            } else {
               methodVisitor.visitVarInsn(21, i + 1);
            }
         }

         methodVisitor.visitMethodInsn(183, targetClassInnerName, "<init>", constructor.methodDesc, false);
         methodVisitor.visitInsn(177);
         methodVisitor.visitMaxs(argumentTypes.length + 1, argumentTypes.length + 1);
         methodVisitor.visitEnd();
      }

   }

   private static void addInvokeMethod(ClassWriter writer, String owner) throws Exception {
      MethodVisitor methodVisitor = writer.visitMethod(130, "invokeInvocationHandler", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", (String)null, (String[])null);
      methodVisitor.visitCode();
      Label start0 = new Label();
      Label end0 = new Label();
      methodVisitor.visitLabel(start0);
      methodVisitor.visitVarInsn(25, 0);
      methodVisitor.visitFieldInsn(180, owner, "invocationHandler", Type.getDescriptor(InvocationHandler.class));
      methodVisitor.visitVarInsn(25, 1);
      methodVisitor.visitVarInsn(25, 2);
      methodVisitor.visitVarInsn(25, 3);
      String handlerName = Type.getInternalName(InvocationHandler.class);
      String handlerMethodName = "invoke";
      String handlerDesc = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
      methodVisitor.visitMethodInsn(185, handlerName, handlerMethodName, handlerDesc, true);
      methodVisitor.visitInsn(176);
      methodVisitor.visitLabel(end0);
      methodVisitor.visitMaxs(4, 5);
      methodVisitor.visitEnd();
   }

   private static int addMethod(ClassWriter writer, String newClassInnerName, Method[] methods, List<MethodBean> methodBeans, boolean isPublic, int methodNameIndex, Map<Integer, Integer> map) throws Exception {
      for(int i = 0; i < methodBeans.size(); ++i) {
         MethodBean methodBean = (MethodBean)methodBeans.get(i);
         if ((methodBean.access & 16) != 16 && (methodBean.access & 8) != 8) {
            int access = -1;
            if (isPublic) {
               if ((methodBean.access & 1) == 1) {
                  access = 1;
               }
            } else if ((methodBean.access & 4) == 4) {
               access = 4;
            } else if ((methodBean.access & 1) == 0 && (methodBean.access & 4) == 0 && (methodBean.access & 2) == 0) {
               access = 0;
            }

            if (access != -1) {
               int methodIndex = findSomeMethod(methods, methodBean);
               if (methodIndex != -1) {
                  map.put(methodNameIndex, methodIndex);
                  String fieldName = "method" + methodNameIndex;
                  FieldVisitor fieldVisitor = writer.visitField(10, fieldName, Type.getDescriptor(Method.class), (String)null, (Object)null);
                  fieldVisitor.visitEnd();
                  addMethod(writer, newClassInnerName, methodBean, access, methodNameIndex);
                  ++methodNameIndex;
               }
            }
         }
      }

      return methodNameIndex;
   }

   private static void addMethod(ClassWriter writer, String newClassInnerName, MethodBean methodBean, int access, int methodNameIndex) throws Exception {
      MethodVisitor methodVisitor = writer.visitMethod(access, methodBean.methodName, methodBean.methodDesc, (String)null, (String[])null);
      methodVisitor.visitCode();
      methodVisitor.visitVarInsn(25, 0);
      if ((methodBean.access & 8) == 8) {
         methodVisitor.visitInsn(1);
      } else {
         methodVisitor.visitVarInsn(25, 0);
      }

      methodVisitor.visitFieldInsn(178, newClassInnerName, "method" + methodNameIndex, Type.getDescriptor(Method.class));
      Type[] argumentTypes = Type.getArgumentTypes(methodBean.methodDesc);
      methodVisitor.visitIntInsn(16, argumentTypes.length);
      methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
      int start = 1;

      for(int i = 0; i < argumentTypes.length; ++i) {
         Type type = argumentTypes[i];
         int stop;
         if (type.equals(Type.BYTE_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(21, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.SHORT_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(21, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.CHAR_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(21, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Character.class), "valueOf", "(C)Ljava/lang/Character;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.INT_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(21, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.FLOAT_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(23, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.DOUBLE_TYPE)) {
            stop = start + 2;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(24, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.LONG_TYPE)) {
            stop = start + 2;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(22, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
            methodVisitor.visitInsn(83);
         } else if (type.equals(Type.BOOLEAN_TYPE)) {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(21, start);
            methodVisitor.visitMethodInsn(184, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
            methodVisitor.visitInsn(83);
         } else {
            stop = start + 1;
            methodVisitor.visitInsn(89);
            methodVisitor.visitIntInsn(16, i);
            methodVisitor.visitVarInsn(25, start);
            methodVisitor.visitInsn(83);
         }

         start = stop;
      }

      methodVisitor.visitMethodInsn(183, newClassInnerName, "invokeInvocationHandler", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", false);
      Type returnType = Type.getReturnType(methodBean.methodDesc);
      if (returnType.equals(Type.BYTE_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Byte.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Byte.class), "byteValue", "()B", false);
         methodVisitor.visitInsn(172);
      } else if (returnType.equals(Type.BOOLEAN_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Boolean.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false);
         methodVisitor.visitInsn(172);
      } else if (returnType.equals(Type.CHAR_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Character.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Character.class), "charValue", "()C", false);
         methodVisitor.visitInsn(172);
      } else if (returnType.equals(Type.SHORT_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Short.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Short.class), "shortValue", "()S", false);
         methodVisitor.visitInsn(172);
      } else if (returnType.equals(Type.INT_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Integer.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Integer.class), "intValue", "()I", false);
         methodVisitor.visitInsn(172);
      } else if (returnType.equals(Type.LONG_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Long.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Long.class), "longValue", "()J", false);
         methodVisitor.visitInsn(173);
      } else if (returnType.equals(Type.FLOAT_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Float.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Float.class), "floatValue", "()F", false);
         methodVisitor.visitInsn(174);
      } else if (returnType.equals(Type.DOUBLE_TYPE)) {
         methodVisitor.visitTypeInsn(192, Type.getInternalName(Double.class));
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Double.class), "doubleValue", "()D", false);
         methodVisitor.visitInsn(175);
      } else if (returnType.equals(Type.VOID_TYPE)) {
         methodVisitor.visitInsn(177);
      } else {
         methodVisitor.visitTypeInsn(192, returnType.getInternalName());
         methodVisitor.visitInsn(176);
      }

      methodVisitor.visitMaxs(8, 37);
      methodVisitor.visitEnd();
   }

   private static void addStaticInitBlock(ClassWriter writer, String targetClassName, String newClassInnerName, Map<Integer, Integer> methodsMap, Map<Integer, Integer> declaredMethodsMap) throws Exception {
      String exceptionClassName = Type.getInternalName(ClassNotFoundException.class);
      MethodVisitor methodVisitor = writer.visitMethod(8, "<clinit>", "()V", (String)null, (String[])null);
      methodVisitor.visitCode();
      Label label0 = new Label();
      Label label1 = new Label();
      Label label2 = new Label();
      methodVisitor.visitTryCatchBlock(label0, label1, label2, exceptionClassName);
      methodVisitor.visitLabel(label0);
      Iterator var10 = methodsMap.entrySet().iterator();

      Map.Entry entry;
      Integer key;
      Integer value;
      while(var10.hasNext()) {
         entry = (Map.Entry)var10.next();
         key = (Integer)entry.getKey();
         value = (Integer)entry.getValue();
         methodVisitor.visitLdcInsn(targetClassName);
         methodVisitor.visitMethodInsn(184, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Class.class), "getMethods", "()[Ljava/lang/reflect/Method;", false);
         methodVisitor.visitIntInsn(16, value);
         methodVisitor.visitInsn(50);
         methodVisitor.visitFieldInsn(179, newClassInnerName, "method" + key, Type.getDescriptor(Method.class));
      }

      var10 = declaredMethodsMap.entrySet().iterator();

      while(var10.hasNext()) {
         entry = (Map.Entry)var10.next();
         key = (Integer)entry.getKey();
         value = (Integer)entry.getValue();
         methodVisitor.visitLdcInsn(targetClassName);
         methodVisitor.visitMethodInsn(184, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
         methodVisitor.visitMethodInsn(182, Type.getInternalName(Class.class), "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
         methodVisitor.visitIntInsn(16, value);
         methodVisitor.visitInsn(50);
         methodVisitor.visitFieldInsn(179, newClassInnerName, "method" + key, Type.getDescriptor(Method.class));
      }

      methodVisitor.visitLabel(label1);
      Label label3 = new Label();
      methodVisitor.visitJumpInsn(167, label3);
      methodVisitor.visitLabel(label2);
      methodVisitor.visitFrame(4, 0, (Object[])null, 1, new Object[]{exceptionClassName});
      methodVisitor.visitVarInsn(58, 0);
      methodVisitor.visitVarInsn(25, 0);
      methodVisitor.visitMethodInsn(182, exceptionClassName, "printStackTrace", "()V", false);
      methodVisitor.visitLabel(label3);
      methodVisitor.visitFrame(3, 0, (Object[])null, 0, (Object[])null);
      methodVisitor.visitInsn(177);
      methodVisitor.visitMaxs(2, 1);
      methodVisitor.visitEnd();
   }

   private static int findSomeMethod(Method[] methods, MethodBean methodBean) {
      for(int i = 0; i < methods.length; ++i) {
         if (equalsMethod(methods[i], methodBean)) {
            return i;
         }
      }

      return -1;
   }

   private static boolean equalsMethod(Method method, MethodBean methodBean) {
      if (method == null && methodBean == null) {
         return true;
      } else if (method != null && methodBean != null) {
         try {
            if (!method.getName().equals(methodBean.methodName)) {
               return false;
            } else if (!Type.getReturnType(method).equals(Type.getReturnType(methodBean.methodDesc))) {
               return false;
            } else {
               Type[] argumentTypes1 = Type.getArgumentTypes(method);
               Type[] argumentTypes2 = Type.getArgumentTypes(methodBean.methodDesc);
               if (argumentTypes1.length != argumentTypes2.length) {
                  return false;
               } else {
                  for(int i = 0; i < argumentTypes1.length; ++i) {
                     if (!argumentTypes1[i].equals(argumentTypes2[i])) {
                        return false;
                     }
                  }

                  return true;
               }
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }
}
