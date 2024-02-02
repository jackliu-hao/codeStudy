/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.BooleanUtil;
/*     */ import cn.hutool.core.util.ClassLoaderUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class MapProxy
/*     */   implements Map<Object, Object>, OptNullBasicTypeFromObjectGetter<Object>, InvocationHandler, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   Map map;
/*     */   
/*     */   public static MapProxy create(Map<?, ?> map) {
/*  38 */     return (map instanceof MapProxy) ? (MapProxy)map : new MapProxy(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapProxy(Map<?, ?> map) {
/*  47 */     this.map = map;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getObj(Object key, Object defaultValue) {
/*  52 */     Object value = this.map.get(key);
/*  53 */     return (null != value) ? value : defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  58 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  63 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  68 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  73 */     return this.map.containsValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/*  78 */     return this.map.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object put(Object key, Object value) {
/*  84 */     return this.map.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object remove(Object key) {
/*  89 */     return this.map.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAll(Map<?, ?> m) {
/*  95 */     this.map.putAll(m);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 100 */     this.map.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Object> keySet() {
/* 106 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Object> values() {
/* 112 */     return this.map.values();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<Object, Object>> entrySet() {
/* 118 */     return this.map.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object invoke(Object proxy, Method method, Object[] args) {
/* 123 */     Class<?>[] parameterTypes = method.getParameterTypes();
/* 124 */     if (ArrayUtil.isEmpty((Object[])parameterTypes)) {
/* 125 */       Class<?> returnType = method.getReturnType();
/* 126 */       if (void.class != returnType) {
/*     */         
/* 128 */         String methodName = method.getName();
/* 129 */         String fieldName = null;
/* 130 */         if (methodName.startsWith("get"))
/*     */         
/* 132 */         { fieldName = StrUtil.removePreAndLowerFirst(methodName, 3); }
/* 133 */         else if (BooleanUtil.isBoolean(returnType) && methodName.startsWith("is"))
/*     */         
/* 135 */         { fieldName = StrUtil.removePreAndLowerFirst(methodName, 2); }
/* 136 */         else { if ("hashCode".equals(methodName))
/* 137 */             return Integer.valueOf(hashCode()); 
/* 138 */           if ("toString".equals(methodName)) {
/* 139 */             return toString();
/*     */           } }
/*     */         
/* 142 */         if (StrUtil.isNotBlank(fieldName)) {
/* 143 */           if (false == containsKey(fieldName))
/*     */           {
/* 145 */             fieldName = StrUtil.toUnderlineCase(fieldName);
/*     */           }
/* 147 */           return Convert.convert(method.getGenericReturnType(), get(fieldName));
/*     */         }
/*     */       
/*     */       } 
/* 151 */     } else if (1 == parameterTypes.length) {
/*     */       
/* 153 */       String methodName = method.getName();
/* 154 */       if (methodName.startsWith("set")) {
/* 155 */         String fieldName = StrUtil.removePreAndLowerFirst(methodName, 3);
/* 156 */         if (StrUtil.isNotBlank(fieldName)) {
/* 157 */           put(fieldName, args[0]);
/* 158 */           Class<?> returnType = method.getReturnType();
/* 159 */           if (returnType.isInstance(proxy)) {
/* 160 */             return proxy;
/*     */           }
/*     */         } 
/* 163 */       } else if ("equals".equals(methodName)) {
/* 164 */         return Boolean.valueOf(equals(args[0]));
/*     */       } 
/*     */     } 
/*     */     
/* 168 */     throw new UnsupportedOperationException(method.toGenericString());
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
/*     */   public <T> T toProxyBean(Class<T> interfaceClass) {
/* 181 */     return (T)Proxy.newProxyInstance(ClassLoaderUtil.getClassLoader(), new Class[] { interfaceClass }, this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\MapProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */