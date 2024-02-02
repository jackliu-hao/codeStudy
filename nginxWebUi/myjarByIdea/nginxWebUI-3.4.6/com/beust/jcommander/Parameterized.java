package com.beust.jcommander;

import com.beust.jcommander.internal.Lists;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class Parameterized {
   private Field m_field;
   private Method m_method;
   private Method m_getter;
   private WrappedParameter m_wrappedParameter;
   private ParametersDelegate m_parametersDelegate;

   public Parameterized(WrappedParameter wp, ParametersDelegate pd, Field field, Method method) {
      this.m_wrappedParameter = wp;
      this.m_method = method;
      this.m_field = field;
      if (this.m_field != null) {
         this.m_field.setAccessible(true);
      }

      this.m_parametersDelegate = pd;
   }

   public static List<Parameterized> parseArg(Object arg) {
      List<Parameterized> result = Lists.newArrayList();

      Class cls;
      int len$;
      int i$;
      Annotation annotation;
      Annotation delegateAnnotation;
      Annotation dynamicParameter;
      for(cls = arg.getClass(); !Object.class.equals(cls); cls = cls.getSuperclass()) {
         Field[] arr$ = cls.getDeclaredFields();
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            Field f = arr$[i$];
            annotation = f.getAnnotation(Parameter.class);
            delegateAnnotation = f.getAnnotation(ParametersDelegate.class);
            dynamicParameter = f.getAnnotation(DynamicParameter.class);
            if (annotation != null) {
               result.add(new Parameterized(new WrappedParameter((Parameter)annotation), (ParametersDelegate)null, f, (Method)null));
            } else if (dynamicParameter != null) {
               result.add(new Parameterized(new WrappedParameter((DynamicParameter)dynamicParameter), (ParametersDelegate)null, f, (Method)null));
            } else if (delegateAnnotation != null) {
               result.add(new Parameterized((WrappedParameter)null, (ParametersDelegate)delegateAnnotation, f, (Method)null));
            }
         }
      }

      for(cls = arg.getClass(); !Object.class.equals(cls); cls = cls.getSuperclass()) {
         Method[] arr$ = cls.getDeclaredMethods();
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            Method m = arr$[i$];
            annotation = m.getAnnotation(Parameter.class);
            delegateAnnotation = m.getAnnotation(ParametersDelegate.class);
            dynamicParameter = m.getAnnotation(DynamicParameter.class);
            if (annotation != null) {
               result.add(new Parameterized(new WrappedParameter((Parameter)annotation), (ParametersDelegate)null, (Field)null, m));
            } else if (dynamicParameter != null) {
               result.add(new Parameterized(new WrappedParameter((DynamicParameter)annotation), (ParametersDelegate)null, (Field)null, m));
            } else if (delegateAnnotation != null) {
               result.add(new Parameterized((WrappedParameter)null, (ParametersDelegate)delegateAnnotation, (Field)null, m));
            }
         }
      }

      return result;
   }

   public WrappedParameter getWrappedParameter() {
      return this.m_wrappedParameter;
   }

   public Class<?> getType() {
      return this.m_method != null ? this.m_method.getParameterTypes()[0] : this.m_field.getType();
   }

   public String getName() {
      return this.m_method != null ? this.m_method.getName() : this.m_field.getName();
   }

   public Object get(Object object) {
      try {
         if (this.m_method != null) {
            if (this.m_getter == null) {
               this.m_getter = this.m_method.getDeclaringClass().getMethod("g" + this.m_method.getName().substring(1));
            }

            return this.m_getter.invoke(object);
         } else {
            return this.m_field.get(object);
         }
      } catch (SecurityException var9) {
         throw new ParameterException(var9);
      } catch (NoSuchMethodException var10) {
         String name = this.m_method.getName();
         String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
         Object result = null;

         try {
            Field field = this.m_method.getDeclaringClass().getDeclaredField(fieldName);
            if (field != null) {
               field.setAccessible(true);
               result = field.get(object);
            }
         } catch (NoSuchFieldException var7) {
         } catch (IllegalAccessException var8) {
         }

         return result;
      } catch (IllegalArgumentException var11) {
         throw new ParameterException(var11);
      } catch (IllegalAccessException var12) {
         throw new ParameterException(var12);
      } catch (InvocationTargetException var13) {
         throw new ParameterException(var13);
      }
   }

   public int hashCode() {
      int prime = true;
      int result = 1;
      result = 31 * result + (this.m_field == null ? 0 : this.m_field.hashCode());
      result = 31 * result + (this.m_method == null ? 0 : this.m_method.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Parameterized other = (Parameterized)obj;
         if (this.m_field == null) {
            if (other.m_field != null) {
               return false;
            }
         } else if (!this.m_field.equals(other.m_field)) {
            return false;
         }

         if (this.m_method == null) {
            if (other.m_method != null) {
               return false;
            }
         } else if (!this.m_method.equals(other.m_method)) {
            return false;
         }

         return true;
      }
   }

   public boolean isDynamicParameter(Field field) {
      if (this.m_method != null) {
         return this.m_method.getAnnotation(DynamicParameter.class) != null;
      } else {
         return this.m_field.getAnnotation(DynamicParameter.class) != null;
      }
   }

   public void set(Object object, Object value) {
      try {
         if (this.m_method != null) {
            this.m_method.invoke(object, value);
         } else {
            this.m_field.set(object, value);
         }

      } catch (IllegalArgumentException var4) {
         throw new ParameterException(var4);
      } catch (IllegalAccessException var5) {
         throw new ParameterException(var5);
      } catch (InvocationTargetException var6) {
         if (var6.getTargetException() instanceof ParameterException) {
            throw (ParameterException)var6.getTargetException();
         } else {
            throw new ParameterException(var6);
         }
      }
   }

   public ParametersDelegate getDelegateAnnotation() {
      return this.m_parametersDelegate;
   }

   public Type getGenericType() {
      return this.m_method != null ? this.m_method.getGenericParameterTypes()[0] : this.m_field.getGenericType();
   }

   public Parameter getParameter() {
      return this.m_wrappedParameter.getParameter();
   }

   public Type findFieldGenericType() {
      if (this.m_method != null) {
         return null;
      } else {
         if (this.m_field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)this.m_field.getGenericType();
            Type cls = p.getActualTypeArguments()[0];
            if (cls instanceof Class) {
               return cls;
            }
         }

         return null;
      }
   }

   public boolean isDynamicParameter() {
      return this.m_wrappedParameter.getDynamicParameter() != null;
   }
}
