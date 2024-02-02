/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.Dispatch;
/*     */ import com.sun.jna.platform.win32.COM.IDispatch;
/*     */ import com.sun.jna.platform.win32.COM.IUnknown;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Date;
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
/*     */ class Convert
/*     */ {
/*     */   public static Variant.VARIANT toVariant(Object value) {
/*  92 */     if (value instanceof Variant.VARIANT)
/*  93 */       return (Variant.VARIANT)value; 
/*  94 */     if (value instanceof Byte)
/*  95 */       return new Variant.VARIANT(((Byte)value).byteValue()); 
/*  96 */     if (value instanceof Character)
/*  97 */       return new Variant.VARIANT(((Character)value).charValue()); 
/*  98 */     if (value instanceof Short)
/*  99 */       return new Variant.VARIANT(((Short)value).shortValue()); 
/* 100 */     if (value instanceof Integer)
/* 101 */       return new Variant.VARIANT(((Integer)value).intValue()); 
/* 102 */     if (value instanceof Long)
/* 103 */       return new Variant.VARIANT(((Long)value).longValue()); 
/* 104 */     if (value instanceof Float)
/* 105 */       return new Variant.VARIANT(((Float)value).floatValue()); 
/* 106 */     if (value instanceof Double)
/* 107 */       return new Variant.VARIANT(((Double)value).doubleValue()); 
/* 108 */     if (value instanceof String)
/* 109 */       return new Variant.VARIANT((String)value); 
/* 110 */     if (value instanceof Boolean)
/* 111 */       return new Variant.VARIANT(((Boolean)value).booleanValue()); 
/* 112 */     if (value instanceof Dispatch)
/* 113 */       return new Variant.VARIANT((Dispatch)value); 
/* 114 */     if (value instanceof Date)
/* 115 */       return new Variant.VARIANT((Date)value); 
/* 116 */     if (value instanceof Proxy) {
/* 117 */       InvocationHandler ih = Proxy.getInvocationHandler(value);
/* 118 */       ProxyObject pobj = (ProxyObject)ih;
/* 119 */       return new Variant.VARIANT(pobj.getRawDispatch());
/* 120 */     }  if (value instanceof IComEnum) {
/* 121 */       IComEnum enm = (IComEnum)value;
/* 122 */       return new Variant.VARIANT(new WinDef.LONG(enm.getValue()));
/*     */     } 
/* 124 */     Constructor<Variant.VARIANT> constructor = null;
/* 125 */     if (value != null) {
/* 126 */       for (Constructor<Variant.VARIANT> m : (Constructor[])Variant.VARIANT.class.getConstructors()) {
/* 127 */         Class<?>[] parameters = m.getParameterTypes();
/* 128 */         if (parameters.length == 1 && parameters[0]
/* 129 */           .isAssignableFrom(value.getClass())) {
/* 130 */           constructor = m;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 135 */     if (constructor != null) {
/*     */       try {
/* 137 */         return constructor.newInstance(new Object[] { value });
/* 138 */       } catch (Exception ex) {
/* 139 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */   
/*     */   public static Object toJavaObject(Variant.VARIANT value, Class<?> targetClass, ObjectFactory factory, boolean addReference, boolean freeValue) {
/*     */     Object result;
/* 147 */     if (null == value || value
/* 148 */       .getVarType().intValue() == 0 || value
/* 149 */       .getVarType().intValue() == 1) {
/* 150 */       return null;
/*     */     }
/*     */     
/* 153 */     if (targetClass != null && !targetClass.isAssignableFrom(Object.class)) {
/* 154 */       if (targetClass.isAssignableFrom(value.getClass())) {
/* 155 */         return value;
/*     */       }
/*     */       
/* 158 */       Object vobj = value.getValue();
/* 159 */       if (vobj != null && targetClass.isAssignableFrom(vobj.getClass())) {
/* 160 */         return vobj;
/*     */       }
/*     */     } 
/*     */     
/* 164 */     Variant.VARIANT inputValue = value;
/*     */     
/* 166 */     if (value.getVarType().intValue() == 16396) {
/* 167 */       value = (Variant.VARIANT)value.getValue();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 172 */     if (targetClass == null || targetClass.isAssignableFrom(Object.class)) {
/*     */       
/* 174 */       targetClass = null;
/*     */       
/* 176 */       int varType = value.getVarType().intValue();
/*     */       
/* 178 */       switch (value.getVarType().intValue()) {
/*     */         case 16:
/*     */         case 17:
/* 181 */           targetClass = Byte.class;
/*     */           break;
/*     */         case 2:
/* 184 */           targetClass = Short.class;
/*     */           break;
/*     */         case 18:
/* 187 */           targetClass = Character.class;
/*     */           break;
/*     */         case 3:
/*     */         case 19:
/*     */         case 22:
/*     */         case 23:
/* 193 */           targetClass = Integer.class;
/*     */           break;
/*     */         case 20:
/*     */         case 21:
/* 197 */           targetClass = Long.class;
/*     */           break;
/*     */         case 4:
/* 200 */           targetClass = Float.class;
/*     */           break;
/*     */         case 5:
/* 203 */           targetClass = Double.class;
/*     */           break;
/*     */         case 11:
/* 206 */           targetClass = Boolean.class;
/*     */           break;
/*     */         case 10:
/* 209 */           targetClass = WinDef.SCODE.class;
/*     */           break;
/*     */         case 6:
/* 212 */           targetClass = OaIdl.CURRENCY.class;
/*     */           break;
/*     */         case 7:
/* 215 */           targetClass = Date.class;
/*     */           break;
/*     */         case 8:
/* 218 */           targetClass = String.class;
/*     */           break;
/*     */         case 13:
/* 221 */           targetClass = IUnknown.class;
/*     */           break;
/*     */         case 9:
/* 224 */           targetClass = IDispatch.class;
/*     */           break;
/*     */         case 16396:
/* 227 */           targetClass = Variant.class;
/*     */           break;
/*     */         case 16384:
/* 230 */           targetClass = WinDef.PVOID.class;
/*     */           break;
/*     */         case 16398:
/* 233 */           targetClass = OaIdl.DECIMAL.class;
/*     */           break;
/*     */         
/*     */         default:
/* 237 */           if ((varType & 0x2000) > 0) {
/* 238 */             targetClass = OaIdl.SAFEARRAY.class;
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 244 */     if (Byte.class.equals(targetClass) || byte.class.equals(targetClass)) {
/* 245 */       result = Byte.valueOf(value.byteValue());
/* 246 */     } else if (Short.class.equals(targetClass) || short.class.equals(targetClass)) {
/* 247 */       result = Short.valueOf(value.shortValue());
/* 248 */     } else if (Character.class.equals(targetClass) || char.class.equals(targetClass)) {
/* 249 */       result = Character.valueOf((char)value.intValue());
/* 250 */     } else if (Integer.class.equals(targetClass) || int.class.equals(targetClass)) {
/* 251 */       result = Integer.valueOf(value.intValue());
/* 252 */     } else if (Long.class.equals(targetClass) || long.class.equals(targetClass) || IComEnum.class.isAssignableFrom(targetClass)) {
/* 253 */       result = Long.valueOf(value.longValue());
/* 254 */     } else if (Float.class.equals(targetClass) || float.class.equals(targetClass)) {
/* 255 */       result = Float.valueOf(value.floatValue());
/* 256 */     } else if (Double.class.equals(targetClass) || double.class.equals(targetClass)) {
/* 257 */       result = Double.valueOf(value.doubleValue());
/* 258 */     } else if (Boolean.class.equals(targetClass) || boolean.class.equals(targetClass)) {
/* 259 */       result = Boolean.valueOf(value.booleanValue());
/* 260 */     } else if (Date.class.equals(targetClass)) {
/* 261 */       result = value.dateValue();
/* 262 */     } else if (String.class.equals(targetClass)) {
/* 263 */       result = value.stringValue();
/* 264 */     } else if (value.getValue() instanceof Dispatch) {
/* 265 */       Dispatch d = (Dispatch)value.getValue();
/* 266 */       if (targetClass != null && targetClass.isInterface()) {
/* 267 */         Object proxy = factory.createProxy(targetClass, (IDispatch)d);
/*     */ 
/*     */         
/* 270 */         if (!addReference) {
/* 271 */           int i = d.Release();
/*     */         }
/* 273 */         result = proxy;
/*     */       } else {
/* 275 */         result = d;
/*     */ 
/*     */       
/*     */       }
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */ 
/*     */       
/* 287 */       result = value.getValue();
/*     */     } 
/*     */     
/* 290 */     if (IComEnum.class.isAssignableFrom(targetClass)) {
/* 291 */       result = targetClass.cast(toComEnum(targetClass, result));
/*     */     }
/*     */     
/* 294 */     if (freeValue) {
/* 295 */       free(inputValue, result);
/*     */     }
/*     */     
/* 298 */     return result;
/*     */   }
/*     */   
/*     */   public static <T extends IComEnum> T toComEnum(Class<T> enumType, Object value) {
/*     */     
/* 303 */     try { Method m = enumType.getMethod("values", new Class[0]);
/* 304 */       IComEnum[] arrayOfIComEnum = (IComEnum[])m.invoke(null, new Object[0]);
/* 305 */       for (IComEnum iComEnum : arrayOfIComEnum) {
/* 306 */         if (value.equals(Long.valueOf(iComEnum.getValue()))) {
/* 307 */           return (T)iComEnum;
/*     */         }
/*     */       }  }
/* 310 */     catch (NoSuchMethodException noSuchMethodException) {  }
/* 311 */     catch (IllegalAccessException illegalAccessException) {  }
/* 312 */     catch (IllegalArgumentException illegalArgumentException) {  }
/* 313 */     catch (InvocationTargetException invocationTargetException) {}
/*     */     
/* 315 */     return null;
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
/*     */   public static void free(Variant.VARIANT variant, Class<?> javaType) {
/* 329 */     if ((javaType == null || !WTypes.BSTR.class.isAssignableFrom(javaType)) && variant != null && variant
/*     */       
/* 331 */       .getVarType().intValue() == 8) {
/* 332 */       Object value = variant.getValue();
/* 333 */       if (value instanceof WTypes.BSTR) {
/* 334 */         OleAuto.INSTANCE.SysFreeString((WTypes.BSTR)value);
/*     */       }
/*     */     } 
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
/*     */   public static void free(Variant.VARIANT variant, Object value) {
/* 350 */     free(variant, (value == null) ? null : value.getClass());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\Convert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */