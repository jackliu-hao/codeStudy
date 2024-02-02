/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import com.zaxxer.hikari.pool.ProxyCallableStatement;
/*     */ import com.zaxxer.hikari.pool.ProxyConnection;
/*     */ import com.zaxxer.hikari.pool.ProxyDatabaseMetaData;
/*     */ import com.zaxxer.hikari.pool.ProxyPreparedStatement;
/*     */ import com.zaxxer.hikari.pool.ProxyResultSet;
/*     */ import com.zaxxer.hikari.pool.ProxyStatement;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javassist.CannotCompileException;
/*     */ import javassist.ClassPath;
/*     */ import javassist.ClassPool;
/*     */ import javassist.CtClass;
/*     */ import javassist.CtMethod;
/*     */ import javassist.CtNewMethod;
/*     */ import javassist.LoaderClassPath;
/*     */ import javassist.Modifier;
/*     */ import javassist.NotFoundException;
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
/*     */ public final class JavassistProxyFactory
/*     */ {
/*     */   private static ClassPool classPool;
/*  44 */   private static String genDirectory = "";
/*     */   
/*     */   public static void main(String... args) throws Exception {
/*  47 */     classPool = new ClassPool();
/*  48 */     classPool.importPackage("java.sql");
/*  49 */     classPool.appendClassPath((ClassPath)new LoaderClassPath(JavassistProxyFactory.class.getClassLoader()));
/*     */     
/*  51 */     if (args.length > 0) {
/*  52 */       genDirectory = args[0];
/*     */     }
/*     */ 
/*     */     
/*  56 */     String methodBody = "{ try { return delegate.method($$); } catch (SQLException e) { throw checkException(e); } }";
/*  57 */     generateProxyClass(Connection.class, ProxyConnection.class.getName(), methodBody);
/*  58 */     generateProxyClass(Statement.class, ProxyStatement.class.getName(), methodBody);
/*  59 */     generateProxyClass(ResultSet.class, ProxyResultSet.class.getName(), methodBody);
/*  60 */     generateProxyClass(DatabaseMetaData.class, ProxyDatabaseMetaData.class.getName(), methodBody);
/*     */ 
/*     */     
/*  63 */     methodBody = "{ try { return ((cast) delegate).method($$); } catch (SQLException e) { throw checkException(e); } }";
/*  64 */     generateProxyClass(PreparedStatement.class, ProxyPreparedStatement.class.getName(), methodBody);
/*  65 */     generateProxyClass(CallableStatement.class, ProxyCallableStatement.class.getName(), methodBody);
/*     */     
/*  67 */     modifyProxyFactory();
/*     */   }
/*     */   
/*     */   private static void modifyProxyFactory() throws NotFoundException, CannotCompileException, IOException {
/*  71 */     System.out.println("Generating method bodies for com.zaxxer.hikari.proxy.ProxyFactory");
/*     */     
/*  73 */     String packageName = ProxyConnection.class.getPackage().getName();
/*  74 */     CtClass proxyCt = classPool.getCtClass("com.zaxxer.hikari.pool.ProxyFactory");
/*  75 */     for (CtMethod method : proxyCt.getMethods()) {
/*  76 */       switch (method.getName()) {
/*     */         case "getProxyConnection":
/*  78 */           method.setBody("{return new " + packageName + ".HikariProxyConnection($$);}");
/*     */           break;
/*     */         case "getProxyStatement":
/*  81 */           method.setBody("{return new " + packageName + ".HikariProxyStatement($$);}");
/*     */           break;
/*     */         case "getProxyPreparedStatement":
/*  84 */           method.setBody("{return new " + packageName + ".HikariProxyPreparedStatement($$);}");
/*     */           break;
/*     */         case "getProxyCallableStatement":
/*  87 */           method.setBody("{return new " + packageName + ".HikariProxyCallableStatement($$);}");
/*     */           break;
/*     */         case "getProxyResultSet":
/*  90 */           method.setBody("{return new " + packageName + ".HikariProxyResultSet($$);}");
/*     */           break;
/*     */         case "getProxyDatabaseMetaData":
/*  93 */           method.setBody("{return new " + packageName + ".HikariProxyDatabaseMetaData($$);}");
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 101 */     proxyCt.writeFile(genDirectory + "target/classes");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> void generateProxyClass(Class<T> primaryInterface, String superClassName, String methodBody) throws Exception {
/* 109 */     String newClassName = superClassName.replaceAll("(.+)\\.(\\w+)", "$1.Hikari$2");
/*     */     
/* 111 */     CtClass superCt = classPool.getCtClass(superClassName);
/* 112 */     CtClass targetCt = classPool.makeClass(newClassName, superCt);
/* 113 */     targetCt.setModifiers(Modifier.setPublic(16));
/*     */     
/* 115 */     System.out.println("Generating " + newClassName);
/*     */ 
/*     */     
/* 118 */     Set<String> superSigs = new HashSet<>();
/* 119 */     for (CtMethod method : superCt.getMethods()) {
/* 120 */       if ((method.getModifiers() & 0x10) == 16) {
/* 121 */         superSigs.add(method.getName() + method.getSignature());
/*     */       }
/*     */     } 
/*     */     
/* 125 */     Set<String> methods = new HashSet<>();
/* 126 */     for (Class<?> intf : getAllInterfaces(primaryInterface)) {
/* 127 */       CtClass intfCt = classPool.getCtClass(intf.getName());
/* 128 */       targetCt.addInterface(intfCt);
/* 129 */       for (CtMethod intfMethod : intfCt.getDeclaredMethods()) {
/* 130 */         String signature = intfMethod.getName() + intfMethod.getSignature();
/*     */ 
/*     */         
/* 133 */         if (!superSigs.contains(signature))
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 138 */           if (!methods.contains(signature)) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 143 */             methods.add(signature);
/*     */ 
/*     */             
/* 146 */             CtMethod method = CtNewMethod.copy(intfMethod, targetCt, null);
/*     */             
/* 148 */             String modifiedBody = methodBody;
/*     */ 
/*     */             
/* 151 */             CtMethod superMethod = superCt.getMethod(intfMethod.getName(), intfMethod.getSignature());
/* 152 */             if ((superMethod.getModifiers() & 0x400) != 1024 && !isDefaultMethod(intf, intfMethod)) {
/* 153 */               modifiedBody = modifiedBody.replace("((cast) ", "");
/* 154 */               modifiedBody = modifiedBody.replace("delegate", "super");
/* 155 */               modifiedBody = modifiedBody.replace("super)", "super");
/*     */             } 
/*     */             
/* 158 */             modifiedBody = modifiedBody.replace("cast", primaryInterface.getName());
/*     */ 
/*     */             
/* 161 */             if (isThrowsSqlException(intfMethod)) {
/* 162 */               modifiedBody = modifiedBody.replace("method", method.getName());
/*     */             } else {
/*     */               
/* 165 */               modifiedBody = "{ return ((cast) delegate).method($$); }".replace("method", method.getName()).replace("cast", primaryInterface.getName());
/*     */             } 
/*     */             
/* 168 */             if (method.getReturnType() == CtClass.voidType) {
/* 169 */               modifiedBody = modifiedBody.replace("return", "");
/*     */             }
/*     */             
/* 172 */             method.setBody(modifiedBody);
/* 173 */             targetCt.addMethod(method);
/*     */           }  } 
/*     */       } 
/*     */     } 
/* 177 */     targetCt.getClassFile().setMajorVersion(52);
/* 178 */     targetCt.writeFile(genDirectory + "target/classes");
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isThrowsSqlException(CtMethod method) {
/*     */     try {
/* 184 */       for (CtClass clazz : method.getExceptionTypes()) {
/* 185 */         if (clazz.getSimpleName().equals("SQLException")) {
/* 186 */           return true;
/*     */         }
/*     */       }
/*     */     
/* 190 */     } catch (NotFoundException notFoundException) {}
/*     */ 
/*     */ 
/*     */     
/* 194 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isDefaultMethod(Class<?> intf, CtMethod intfMethod) throws Exception {
/* 199 */     List<Class<?>> paramTypes = new ArrayList<>();
/*     */     
/* 201 */     for (CtClass pt : intfMethod.getParameterTypes()) {
/* 202 */       paramTypes.add(toJavaClass(pt));
/*     */     }
/*     */     
/* 205 */     return intf.getDeclaredMethod(intfMethod.getName(), (Class[])paramTypes.<Class<?>[]>toArray((Class<?>[][])new Class[0])).toString().contains("default ");
/*     */   }
/*     */ 
/*     */   
/*     */   private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
/* 210 */     Set<Class<?>> interfaces = new LinkedHashSet<>();
/* 211 */     for (Class<?> intf : clazz.getInterfaces()) {
/* 212 */       if ((intf.getInterfaces()).length > 0) {
/* 213 */         interfaces.addAll(getAllInterfaces(intf));
/*     */       }
/* 215 */       interfaces.add(intf);
/*     */     } 
/* 217 */     if (clazz.getSuperclass() != null) {
/* 218 */       interfaces.addAll(getAllInterfaces(clazz.getSuperclass()));
/*     */     }
/*     */     
/* 221 */     if (clazz.isInterface()) {
/* 222 */       interfaces.add(clazz);
/*     */     }
/*     */     
/* 225 */     return interfaces;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Class<?> toJavaClass(CtClass cls) throws Exception {
/* 230 */     if (cls.getName().endsWith("[]")) {
/* 231 */       return Array.newInstance(toJavaClass(cls.getName().replace("[]", "")), 0).getClass();
/*     */     }
/*     */     
/* 234 */     return toJavaClass(cls.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<?> toJavaClass(String cn) throws Exception {
/* 240 */     switch (cn) {
/*     */       case "int":
/* 242 */         return int.class;
/*     */       case "long":
/* 244 */         return long.class;
/*     */       case "short":
/* 246 */         return short.class;
/*     */       case "byte":
/* 248 */         return byte.class;
/*     */       case "float":
/* 250 */         return float.class;
/*     */       case "double":
/* 252 */         return double.class;
/*     */       case "boolean":
/* 254 */         return boolean.class;
/*     */       case "char":
/* 256 */         return char.class;
/*     */       case "void":
/* 258 */         return void.class;
/*     */     } 
/* 260 */     return Class.forName(cn);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\JavassistProxyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */