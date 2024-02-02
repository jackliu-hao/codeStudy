/*     */ package cn.hutool.core.bean;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.CaseInsensitiveMap;
/*     */ import cn.hutool.core.util.BooleanUtil;
/*     */ import cn.hutool.core.util.ModifierUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public class BeanDesc
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<?> beanClass;
/*  41 */   private final Map<String, PropDesc> propMap = new LinkedHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanDesc(Class<?> beanClass) {
/*  49 */     Assert.notNull(beanClass);
/*  50 */     this.beanClass = beanClass;
/*  51 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  60 */     return this.beanClass.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSimpleName() {
/*  69 */     return this.beanClass.getSimpleName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, PropDesc> getPropMap(boolean ignoreCase) {
/*  79 */     return ignoreCase ? (Map<String, PropDesc>)new CaseInsensitiveMap(1.0F, this.propMap) : this.propMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<PropDesc> getProps() {
/*  88 */     return this.propMap.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PropDesc getProp(String fieldName) {
/*  98 */     return this.propMap.get(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Field getField(String fieldName) {
/* 108 */     PropDesc desc = this.propMap.get(fieldName);
/* 109 */     return (null == desc) ? null : desc.getField();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getGetter(String fieldName) {
/* 119 */     PropDesc desc = this.propMap.get(fieldName);
/* 120 */     return (null == desc) ? null : desc.getGetter();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getSetter(String fieldName) {
/* 130 */     PropDesc desc = this.propMap.get(fieldName);
/* 131 */     return (null == desc) ? null : desc.getSetter();
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
/*     */   private BeanDesc init() {
/* 143 */     Method[] gettersAndSetters = ReflectUtil.getMethods(this.beanClass, ReflectUtil::isGetterOrSetterIgnoreCase);
/*     */     
/* 145 */     for (Field field : ReflectUtil.getFields(this.beanClass)) {
/*     */       
/* 147 */       if (false == ModifierUtil.isStatic(field) && false == ReflectUtil.isOuterClassField(field)) {
/* 148 */         PropDesc prop = createProp(field, gettersAndSetters);
/*     */         
/* 150 */         this.propMap.putIfAbsent(prop.getFieldName(), prop);
/*     */       } 
/*     */     } 
/* 153 */     return this;
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
/*     */   private PropDesc createProp(Field field, Method[] methods) {
/* 173 */     PropDesc prop = findProp(field, methods, false);
/*     */     
/* 175 */     if (null == prop.getter || null == prop.setter) {
/* 176 */       PropDesc propIgnoreCase = findProp(field, methods, true);
/* 177 */       if (null == prop.getter) {
/* 178 */         prop.getter = propIgnoreCase.getter;
/*     */       }
/* 180 */       if (null == prop.setter) {
/* 181 */         prop.setter = propIgnoreCase.setter;
/*     */       }
/*     */     } 
/*     */     
/* 185 */     return prop;
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
/*     */   private PropDesc findProp(Field field, Method[] gettersOrSetters, boolean ignoreCase) {
/* 197 */     String fieldName = field.getName();
/* 198 */     Class<?> fieldType = field.getType();
/* 199 */     boolean isBooleanField = BooleanUtil.isBoolean(fieldType);
/*     */     
/* 201 */     Method getter = null;
/* 202 */     Method setter = null;
/*     */     
/* 204 */     for (Method method : gettersOrSetters) {
/* 205 */       String methodName = method.getName();
/* 206 */       if (method.getParameterCount() == 0) {
/*     */         
/* 208 */         if (isMatchGetter(methodName, fieldName, isBooleanField, ignoreCase))
/*     */         {
/* 210 */           getter = method;
/*     */         }
/* 212 */       } else if (isMatchSetter(methodName, fieldName, isBooleanField, ignoreCase)) {
/*     */         
/* 214 */         if (fieldType.isAssignableFrom(method.getParameterTypes()[0])) {
/* 215 */           setter = method;
/*     */         }
/*     */       } 
/* 218 */       if (null != getter && null != setter) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 224 */     return new PropDesc(field, getter, setter);
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
/*     */   private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
/*     */     String handledFieldName;
/* 248 */     if (ignoreCase) {
/*     */       
/* 250 */       methodName = methodName.toLowerCase();
/* 251 */       handledFieldName = fieldName.toLowerCase();
/* 252 */       fieldName = handledFieldName;
/*     */     } else {
/* 254 */       handledFieldName = StrUtil.upperFirst(fieldName);
/*     */     } 
/*     */ 
/*     */     
/* 258 */     if (isBooleanField) {
/* 259 */       if (fieldName.startsWith("is")) {
/*     */         
/* 261 */         if (methodName.equals(fieldName) || ("get" + handledFieldName)
/* 262 */           .equals(methodName) || ("is" + handledFieldName)
/* 263 */           .equals(methodName))
/*     */         {
/* 265 */           return true;
/*     */         }
/* 267 */       } else if (("is" + handledFieldName).equals(methodName)) {
/*     */         
/* 269 */         return true;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 274 */     return ("get" + handledFieldName).equals(methodName);
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
/*     */   private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
/*     */     String handledFieldName;
/* 296 */     if (ignoreCase) {
/*     */       
/* 298 */       methodName = methodName.toLowerCase();
/* 299 */       handledFieldName = fieldName.toLowerCase();
/* 300 */       fieldName = handledFieldName;
/*     */     } else {
/* 302 */       handledFieldName = StrUtil.upperFirst(fieldName);
/*     */     } 
/*     */ 
/*     */     
/* 306 */     if (false == methodName.startsWith("set")) {
/* 307 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 311 */     if (isBooleanField && fieldName.startsWith("is"))
/*     */     {
/* 313 */       if (("set" + StrUtil.removePrefix(fieldName, "is")).equals(methodName) || ("set" + handledFieldName)
/* 314 */         .equals(methodName))
/*     */       {
/* 316 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 321 */     return ("set" + handledFieldName).equals(methodName);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */