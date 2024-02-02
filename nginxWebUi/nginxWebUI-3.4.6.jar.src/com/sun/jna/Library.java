/*     */ package com.sun.jna;
/*     */ 
/*     */ import com.sun.jna.internal.ReflectionUtils;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
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
/*     */ public interface Library
/*     */ {
/*     */   public static final String OPTION_TYPE_MAPPER = "type-mapper";
/*     */   public static final String OPTION_FUNCTION_MAPPER = "function-mapper";
/*     */   public static final String OPTION_INVOCATION_MAPPER = "invocation-mapper";
/*     */   public static final String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
/*     */   public static final String OPTION_STRING_ENCODING = "string-encoding";
/*     */   public static final String OPTION_ALLOW_OBJECTS = "allow-objects";
/*     */   public static final String OPTION_CALLING_CONVENTION = "calling-convention";
/*     */   public static final String OPTION_OPEN_FLAGS = "open-flags";
/*     */   public static final String OPTION_CLASSLOADER = "classloader";
/*     */   
/*     */   public static class Handler
/*     */     implements InvocationHandler
/*     */   {
/*     */     static final Method OBJECT_TOSTRING;
/*     */     static final Method OBJECT_HASHCODE;
/*     */     static final Method OBJECT_EQUALS;
/*     */     private final NativeLibrary nativeLibrary;
/*     */     private final Class<?> interfaceClass;
/*     */     private final Map<String, Object> options;
/*     */     private final InvocationMapper invocationMapper;
/*     */     
/*     */     static {
/*     */       try {
/* 125 */         OBJECT_TOSTRING = Object.class.getMethod("toString", new Class[0]);
/* 126 */         OBJECT_HASHCODE = Object.class.getMethod("hashCode", new Class[0]);
/* 127 */         OBJECT_EQUALS = Object.class.getMethod("equals", new Class[] { Object.class });
/* 128 */       } catch (Exception e) {
/* 129 */         throw new Error("Error retrieving Object.toString() method");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static final class FunctionInfo
/*     */     {
/*     */       final InvocationHandler handler;
/*     */       
/*     */       final Function function;
/*     */       
/*     */       final boolean isVarArgs;
/*     */       
/*     */       final Object methodHandle;
/*     */       final Map<String, ?> options;
/*     */       final Class<?>[] parameterTypes;
/*     */       
/*     */       FunctionInfo(Object mh) {
/* 147 */         this.handler = null;
/* 148 */         this.function = null;
/* 149 */         this.isVarArgs = false;
/* 150 */         this.options = null;
/* 151 */         this.parameterTypes = null;
/* 152 */         this.methodHandle = mh;
/*     */       }
/*     */       
/*     */       FunctionInfo(InvocationHandler handler, Function function, Class<?>[] parameterTypes, boolean isVarArgs, Map<String, ?> options) {
/* 156 */         this.handler = handler;
/* 157 */         this.function = function;
/* 158 */         this.isVarArgs = isVarArgs;
/* 159 */         this.options = options;
/* 160 */         this.parameterTypes = parameterTypes;
/* 161 */         this.methodHandle = null;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     private final Map<Method, FunctionInfo> functions = new WeakHashMap<Method, FunctionInfo>();
/*     */     
/*     */     public Handler(String libname, Class<?> interfaceClass, Map<String, ?> options) {
/* 173 */       if (libname != null && "".equals(libname.trim())) {
/* 174 */         throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
/*     */       }
/*     */       
/* 177 */       if (!interfaceClass.isInterface()) {
/* 178 */         throw new IllegalArgumentException(libname + " does not implement an interface: " + interfaceClass.getName());
/*     */       }
/*     */       
/* 181 */       this.interfaceClass = interfaceClass;
/* 182 */       this.options = new HashMap<String, Object>(options);
/* 183 */       int callingConvention = AltCallingConvention.class.isAssignableFrom(interfaceClass) ? 63 : 0;
/*     */ 
/*     */       
/* 186 */       if (this.options.get("calling-convention") == null) {
/* 187 */         this.options.put("calling-convention", Integer.valueOf(callingConvention));
/*     */       }
/* 189 */       if (this.options.get("classloader") == null) {
/* 190 */         this.options.put("classloader", interfaceClass.getClassLoader());
/*     */       }
/* 192 */       this.nativeLibrary = NativeLibrary.getInstance(libname, this.options);
/* 193 */       this.invocationMapper = (InvocationMapper)this.options.get("invocation-mapper");
/*     */     }
/*     */     
/*     */     public NativeLibrary getNativeLibrary() {
/* 197 */       return this.nativeLibrary;
/*     */     }
/*     */     
/*     */     public String getLibraryName() {
/* 201 */       return this.nativeLibrary.getName();
/*     */     }
/*     */     
/*     */     public Class<?> getInterfaceClass() {
/* 205 */       return this.interfaceClass;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] inArgs) throws Throwable {
/* 213 */       if (OBJECT_TOSTRING.equals(method))
/* 214 */         return "Proxy interface to " + this.nativeLibrary; 
/* 215 */       if (OBJECT_HASHCODE.equals(method))
/* 216 */         return Integer.valueOf(hashCode()); 
/* 217 */       if (OBJECT_EQUALS.equals(method)) {
/* 218 */         Object o = inArgs[0];
/* 219 */         if (o != null && Proxy.isProxyClass(o.getClass())) {
/* 220 */           return Function.valueOf((Proxy.getInvocationHandler(o) == this));
/*     */         }
/* 222 */         return Boolean.FALSE;
/*     */       } 
/*     */ 
/*     */       
/* 226 */       FunctionInfo f = this.functions.get(method);
/* 227 */       if (f == null) {
/* 228 */         synchronized (this.functions) {
/* 229 */           f = this.functions.get(method);
/* 230 */           if (f == null) {
/* 231 */             boolean isDefault = ReflectionUtils.isDefault(method);
/* 232 */             if (!isDefault) {
/* 233 */               boolean isVarArgs = Function.isVarArgs(method);
/* 234 */               InvocationHandler handler = null;
/* 235 */               if (this.invocationMapper != null) {
/* 236 */                 handler = this.invocationMapper.getInvocationHandler(this.nativeLibrary, method);
/*     */               }
/* 238 */               Function function = null;
/* 239 */               Class<?>[] parameterTypes = null;
/* 240 */               Map<String, Object> options = null;
/* 241 */               if (handler == null) {
/*     */                 
/* 243 */                 function = this.nativeLibrary.getFunction(method.getName(), method);
/* 244 */                 parameterTypes = method.getParameterTypes();
/* 245 */                 options = new HashMap<String, Object>(this.options);
/* 246 */                 options.put("invoking-method", method);
/*     */               } 
/* 248 */               f = new FunctionInfo(handler, function, parameterTypes, isVarArgs, options);
/*     */             } else {
/* 250 */               f = new FunctionInfo(ReflectionUtils.getMethodHandle(method));
/*     */             } 
/* 252 */             this.functions.put(method, f);
/*     */           } 
/*     */         } 
/*     */       }
/* 256 */       if (f.methodHandle != null) {
/* 257 */         return ReflectionUtils.invokeDefaultMethod(proxy, f.methodHandle, inArgs);
/*     */       }
/* 259 */       if (f.isVarArgs) {
/* 260 */         inArgs = Function.concatenateVarArgs(inArgs);
/*     */       }
/* 262 */       if (f.handler != null) {
/* 263 */         return f.handler.invoke(proxy, method, inArgs);
/*     */       }
/* 265 */       return f.function.invoke(method, f.parameterTypes, method.getReturnType(), inArgs, f.options);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Library.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */