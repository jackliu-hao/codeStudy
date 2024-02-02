/*     */ package com.beust.jcommander;
/*     */ 
/*     */ import com.beust.jcommander.internal.Lists;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Parameterized
/*     */ {
/*     */   private Field m_field;
/*     */   private Method m_method;
/*     */   private Method m_getter;
/*     */   private WrappedParameter m_wrappedParameter;
/*     */   private ParametersDelegate m_parametersDelegate;
/*     */   
/*     */   public Parameterized(WrappedParameter wp, ParametersDelegate pd, Field field, Method method) {
/*  29 */     this.m_wrappedParameter = wp;
/*  30 */     this.m_method = method;
/*  31 */     this.m_field = field;
/*  32 */     if (this.m_field != null) {
/*  33 */       this.m_field.setAccessible(true);
/*     */     }
/*  35 */     this.m_parametersDelegate = pd;
/*     */   }
/*     */   
/*     */   public static List<Parameterized> parseArg(Object arg) {
/*  39 */     List<Parameterized> result = Lists.newArrayList();
/*     */     
/*  41 */     Class<? extends Object> cls = (Class)arg.getClass();
/*  42 */     while (!Object.class.equals(cls)) {
/*  43 */       for (Field f : cls.getDeclaredFields()) {
/*  44 */         Annotation annotation = f.getAnnotation((Class)Parameter.class);
/*  45 */         Annotation delegateAnnotation = f.getAnnotation((Class)ParametersDelegate.class);
/*  46 */         Annotation dynamicParameter = f.getAnnotation((Class)DynamicParameter.class);
/*  47 */         if (annotation != null) {
/*  48 */           result.add(new Parameterized(new WrappedParameter((Parameter)annotation), null, f, null));
/*     */         }
/*  50 */         else if (dynamicParameter != null) {
/*  51 */           result.add(new Parameterized(new WrappedParameter((DynamicParameter)dynamicParameter), null, f, null));
/*     */         }
/*  53 */         else if (delegateAnnotation != null) {
/*  54 */           result.add(new Parameterized(null, (ParametersDelegate)delegateAnnotation, f, null));
/*     */         } 
/*     */       } 
/*     */       
/*  58 */       cls = (Class)cls.getSuperclass();
/*     */     } 
/*     */ 
/*     */     
/*  62 */     cls = (Class)arg.getClass();
/*  63 */     while (!Object.class.equals(cls)) {
/*  64 */       for (Method m : cls.getDeclaredMethods()) {
/*  65 */         Annotation annotation = m.getAnnotation((Class)Parameter.class);
/*  66 */         Annotation delegateAnnotation = m.getAnnotation((Class)ParametersDelegate.class);
/*  67 */         Annotation dynamicParameter = m.getAnnotation((Class)DynamicParameter.class);
/*  68 */         if (annotation != null) {
/*  69 */           result.add(new Parameterized(new WrappedParameter((Parameter)annotation), null, null, m));
/*     */         }
/*  71 */         else if (dynamicParameter != null) {
/*  72 */           result.add(new Parameterized(new WrappedParameter((DynamicParameter)annotation), null, null, m));
/*     */         }
/*  74 */         else if (delegateAnnotation != null) {
/*  75 */           result.add(new Parameterized(null, (ParametersDelegate)delegateAnnotation, null, m));
/*     */         } 
/*     */       } 
/*     */       
/*  79 */       cls = (Class)cls.getSuperclass();
/*     */     } 
/*     */     
/*  82 */     return result;
/*     */   }
/*     */   
/*     */   public WrappedParameter getWrappedParameter() {
/*  86 */     return this.m_wrappedParameter;
/*     */   }
/*     */   
/*     */   public Class<?> getType() {
/*  90 */     if (this.m_method != null) {
/*  91 */       return this.m_method.getParameterTypes()[0];
/*     */     }
/*  93 */     return this.m_field.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  98 */     if (this.m_method != null) {
/*  99 */       return this.m_method.getName();
/*     */     }
/* 101 */     return this.m_field.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(Object object) {
/*     */     try {
/* 107 */       if (this.m_method != null) {
/* 108 */         if (this.m_getter == null) {
/* 109 */           this.m_getter = this.m_method.getDeclaringClass().getMethod("g" + this.m_method.getName().substring(1), new Class[0]);
/*     */         }
/*     */ 
/*     */         
/* 113 */         return this.m_getter.invoke(object, new Object[0]);
/*     */       } 
/* 115 */       return this.m_field.get(object);
/*     */     }
/* 117 */     catch (SecurityException e) {
/* 118 */       throw new ParameterException(e);
/* 119 */     } catch (NoSuchMethodException e) {
/*     */       
/* 121 */       String name = this.m_method.getName();
/* 122 */       String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
/* 123 */       Object result = null;
/*     */       try {
/* 125 */         Field field = this.m_method.getDeclaringClass().getDeclaredField(fieldName);
/* 126 */         if (field != null) {
/* 127 */           field.setAccessible(true);
/* 128 */           result = field.get(object);
/*     */         } 
/* 130 */       } catch (NoSuchFieldException ex) {
/*     */       
/* 132 */       } catch (IllegalAccessException ex) {}
/*     */ 
/*     */       
/* 135 */       return result;
/* 136 */     } catch (IllegalArgumentException e) {
/* 137 */       throw new ParameterException(e);
/* 138 */     } catch (IllegalAccessException e) {
/* 139 */       throw new ParameterException(e);
/* 140 */     } catch (InvocationTargetException e) {
/* 141 */       throw new ParameterException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 147 */     int prime = 31;
/* 148 */     int result = 1;
/* 149 */     result = 31 * result + ((this.m_field == null) ? 0 : this.m_field.hashCode());
/* 150 */     result = 31 * result + ((this.m_method == null) ? 0 : this.m_method.hashCode());
/* 151 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 156 */     if (this == obj)
/* 157 */       return true; 
/* 158 */     if (obj == null)
/* 159 */       return false; 
/* 160 */     if (getClass() != obj.getClass())
/* 161 */       return false; 
/* 162 */     Parameterized other = (Parameterized)obj;
/* 163 */     if (this.m_field == null) {
/* 164 */       if (other.m_field != null)
/* 165 */         return false; 
/* 166 */     } else if (!this.m_field.equals(other.m_field)) {
/* 167 */       return false;
/* 168 */     }  if (this.m_method == null) {
/* 169 */       if (other.m_method != null)
/* 170 */         return false; 
/* 171 */     } else if (!this.m_method.equals(other.m_method)) {
/* 172 */       return false;
/* 173 */     }  return true;
/*     */   }
/*     */   
/*     */   public boolean isDynamicParameter(Field field) {
/* 177 */     if (this.m_method != null) {
/* 178 */       return (this.m_method.getAnnotation(DynamicParameter.class) != null);
/*     */     }
/* 180 */     return (this.m_field.getAnnotation(DynamicParameter.class) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Object object, Object value) {
/*     */     try {
/* 186 */       if (this.m_method != null) {
/* 187 */         this.m_method.invoke(object, new Object[] { value });
/*     */       } else {
/* 189 */         this.m_field.set(object, value);
/*     */       } 
/* 191 */     } catch (IllegalArgumentException ex) {
/* 192 */       throw new ParameterException(ex);
/* 193 */     } catch (IllegalAccessException ex) {
/* 194 */       throw new ParameterException(ex);
/* 195 */     } catch (InvocationTargetException ex) {
/*     */       
/* 197 */       if (ex.getTargetException() instanceof ParameterException) {
/* 198 */         throw (ParameterException)ex.getTargetException();
/*     */       }
/* 200 */       throw new ParameterException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ParametersDelegate getDelegateAnnotation() {
/* 206 */     return this.m_parametersDelegate;
/*     */   }
/*     */   
/*     */   public Type getGenericType() {
/* 210 */     if (this.m_method != null) {
/* 211 */       return this.m_method.getGenericParameterTypes()[0];
/*     */     }
/* 213 */     return this.m_field.getGenericType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Parameter getParameter() {
/* 218 */     return this.m_wrappedParameter.getParameter();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type findFieldGenericType() {
/* 225 */     if (this.m_method != null) {
/* 226 */       return null;
/*     */     }
/* 228 */     if (this.m_field.getGenericType() instanceof ParameterizedType) {
/* 229 */       ParameterizedType p = (ParameterizedType)this.m_field.getGenericType();
/* 230 */       Type cls = p.getActualTypeArguments()[0];
/* 231 */       if (cls instanceof Class) {
/* 232 */         return cls;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 237 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isDynamicParameter() {
/* 241 */     return (this.m_wrappedParameter.getDynamicParameter() != null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\Parameterized.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */