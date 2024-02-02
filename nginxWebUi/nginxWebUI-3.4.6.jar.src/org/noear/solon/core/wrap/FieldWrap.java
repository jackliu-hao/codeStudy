/*     */ package org.noear.solon.core.wrap;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.core.AopContext;
/*     */ import org.noear.solon.core.VarHolder;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.util.GenericUtil;
/*     */ import org.noear.solon.core.util.ParameterizedTypeImpl;
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
/*     */ public class FieldWrap
/*     */ {
/*     */   public final Class<?> entityClz;
/*     */   public final Field field;
/*     */   public final Annotation[] annoS;
/*     */   public final Class<?> type;
/*     */   public final ParameterizedType genericType;
/*     */   public final boolean readonly;
/*     */   private Method _setter;
/*     */   private Method _getter;
/*     */   
/*     */   protected FieldWrap(Class<?> clz, Field f1, boolean isFinal) {
/*  57 */     this.entityClz = clz;
/*  58 */     this.field = f1;
/*  59 */     this.annoS = f1.getDeclaredAnnotations();
/*  60 */     this.readonly = isFinal;
/*     */     
/*  62 */     this.field.setAccessible(true);
/*     */     
/*  64 */     Type tmp = f1.getGenericType();
/*  65 */     if (tmp instanceof java.lang.reflect.TypeVariable) {
/*     */       
/*  67 */       Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
/*  68 */       Type typeH = gMap.get(tmp.getTypeName());
/*     */       
/*  70 */       if (typeH instanceof ParameterizedType) {
/*     */         
/*  72 */         this.genericType = (ParameterizedType)typeH;
/*  73 */         this.type = (Class)((ParameterizedType)typeH).getRawType();
/*     */       } else {
/*  75 */         this.genericType = null;
/*  76 */         this.type = (Class)typeH;
/*     */       } 
/*     */     } else {
/*  79 */       this.type = f1.getType();
/*     */       
/*  81 */       if (tmp instanceof ParameterizedType) {
/*  82 */         ParameterizedType gt0 = (ParameterizedType)tmp;
/*     */         
/*  84 */         Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
/*  85 */         Type[] gArgs = gt0.getActualTypeArguments();
/*  86 */         boolean gChanged = false;
/*     */         
/*  88 */         for (int i = 0; i < gArgs.length; i++) {
/*  89 */           Type t1 = gArgs[i];
/*  90 */           if (t1 instanceof java.lang.reflect.TypeVariable) {
/*     */             
/*  92 */             gArgs[i] = gMap.get(t1.getTypeName());
/*  93 */             gChanged = true;
/*     */           } 
/*     */         } 
/*     */         
/*  97 */         if (gChanged) {
/*  98 */           this.genericType = (ParameterizedType)new ParameterizedTypeImpl((Class)gt0.getRawType(), gArgs, gt0.getOwnerType());
/*     */         } else {
/* 100 */           this.genericType = gt0;
/*     */         } 
/*     */       } else {
/* 103 */         this.genericType = null;
/*     */       } 
/*     */     } 
/*     */     
/* 107 */     this._setter = doFindSetter(clz, f1);
/* 108 */     this._getter = dofindGetter(clz, f1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VarHolder holder(AopContext ctx, Object obj) {
/* 115 */     return new VarHolderOfField(ctx, this, obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue(Object tObj) throws ReflectiveOperationException {
/* 122 */     if (this._getter == null) {
/* 123 */       return this.field.get(tObj);
/*     */     }
/* 125 */     return this._getter.invoke(tObj, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Object tObj, Object val) {
/* 133 */     setValue(tObj, val, false);
/*     */   }
/*     */   public void setValue(Object tObj, Object val, boolean disFun) {
/* 136 */     if (this.readonly) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 141 */       if (val == null) {
/*     */         return;
/*     */       }
/*     */       
/* 145 */       if (this._setter == null || disFun) {
/* 146 */         this.field.set(tObj, val);
/*     */       } else {
/* 148 */         this._setter.invoke(tObj, new Object[] { val });
/*     */       } 
/* 150 */     } catch (IllegalArgumentException ex) {
/* 151 */       if (val == null) {
/* 152 */         throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failur!", ex);
/*     */       }
/*     */       
/* 155 */       throw new IllegalArgumentException(this.field
/* 156 */           .getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure ：val(" + val
/* 157 */           .getClass().getSimpleName() + ")", ex);
/* 158 */     } catch (RuntimeException ex) {
/* 159 */       throw ex;
/* 160 */     } catch (Exception ex) {
/* 161 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Method dofindGetter(Class<?> tCls, Field field) {
/* 166 */     String fieldName = field.getName();
/* 167 */     String firstLetter = fieldName.substring(0, 1).toUpperCase();
/* 168 */     String setMethodName = "get" + firstLetter + fieldName.substring(1);
/*     */     
/*     */     try {
/* 171 */       Method getFun = tCls.getMethod(setMethodName, new Class[0]);
/* 172 */       if (getFun != null) {
/* 173 */         return getFun;
/*     */       }
/* 175 */     } catch (NoSuchMethodException noSuchMethodException) {
/*     */     
/* 177 */     } catch (Throwable ex) {
/* 178 */       ex.printStackTrace();
/*     */     } 
/*     */     
/* 181 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method doFindSetter(Class<?> tCls, Field field) {
/* 189 */     String fieldName = field.getName();
/* 190 */     String firstLetter = fieldName.substring(0, 1).toUpperCase();
/* 191 */     String setMethodName = "set" + firstLetter + fieldName.substring(1);
/*     */     
/*     */     try {
/* 194 */       Method setFun = tCls.getMethod(setMethodName, new Class[] { field.getType() });
/* 195 */       if (setFun != null) {
/* 196 */         return setFun;
/*     */       }
/* 198 */     } catch (NoSuchMethodException noSuchMethodException) {
/*     */     
/* 200 */     } catch (Throwable ex) {
/* 201 */       EventBus.push(ex);
/*     */     } 
/* 203 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\FieldWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */