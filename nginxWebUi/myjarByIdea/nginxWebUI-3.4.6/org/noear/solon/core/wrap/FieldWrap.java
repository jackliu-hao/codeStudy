package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.ParameterizedTypeImpl;

public class FieldWrap {
   public final Class<?> entityClz;
   public final Field field;
   public final Annotation[] annoS;
   public final Class<?> type;
   public final ParameterizedType genericType;
   public final boolean readonly;
   private Method _setter;
   private Method _getter;

   protected FieldWrap(Class<?> clz, Field f1, boolean isFinal) {
      this.entityClz = clz;
      this.field = f1;
      this.annoS = f1.getDeclaredAnnotations();
      this.readonly = isFinal;
      this.field.setAccessible(true);
      Type tmp = f1.getGenericType();
      if (tmp instanceof TypeVariable) {
         Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
         Type typeH = (Type)gMap.get(tmp.getTypeName());
         if (typeH instanceof ParameterizedType) {
            this.genericType = (ParameterizedType)typeH;
            this.type = (Class)((ParameterizedType)typeH).getRawType();
         } else {
            this.genericType = null;
            this.type = (Class)typeH;
         }
      } else {
         this.type = f1.getType();
         if (tmp instanceof ParameterizedType) {
            ParameterizedType gt0 = (ParameterizedType)tmp;
            Map<String, Type> gMap = GenericUtil.getGenericInfo(clz);
            Type[] gArgs = gt0.getActualTypeArguments();
            boolean gChanged = false;

            for(int i = 0; i < gArgs.length; ++i) {
               Type t1 = gArgs[i];
               if (t1 instanceof TypeVariable) {
                  gArgs[i] = (Type)gMap.get(t1.getTypeName());
                  gChanged = true;
               }
            }

            if (gChanged) {
               this.genericType = new ParameterizedTypeImpl((Class)gt0.getRawType(), gArgs, gt0.getOwnerType());
            } else {
               this.genericType = gt0;
            }
         } else {
            this.genericType = null;
         }
      }

      this._setter = doFindSetter(clz, f1);
      this._getter = dofindGetter(clz, f1);
   }

   public VarHolder holder(AopContext ctx, Object obj) {
      return new VarHolderOfField(ctx, this, obj);
   }

   public Object getValue(Object tObj) throws ReflectiveOperationException {
      return this._getter == null ? this.field.get(tObj) : this._getter.invoke(tObj);
   }

   public void setValue(Object tObj, Object val) {
      this.setValue(tObj, val, false);
   }

   public void setValue(Object tObj, Object val, boolean disFun) {
      if (!this.readonly) {
         try {
            if (val != null) {
               if (this._setter != null && !disFun) {
                  this._setter.invoke(tObj, val);
               } else {
                  this.field.set(tObj, val);
               }

            }
         } catch (IllegalArgumentException var5) {
            if (val == null) {
               throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failur!", var5);
            } else {
               throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure ：val(" + val.getClass().getSimpleName() + ")", var5);
            }
         } catch (RuntimeException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new RuntimeException(var7);
         }
      }
   }

   private static Method dofindGetter(Class<?> tCls, Field field) {
      String fieldName = field.getName();
      String firstLetter = fieldName.substring(0, 1).toUpperCase();
      String setMethodName = "get" + firstLetter + fieldName.substring(1);

      try {
         Method getFun = tCls.getMethod(setMethodName);
         if (getFun != null) {
            return getFun;
         }
      } catch (NoSuchMethodException var6) {
      } catch (Throwable var7) {
         var7.printStackTrace();
      }

      return null;
   }

   private static Method doFindSetter(Class<?> tCls, Field field) {
      String fieldName = field.getName();
      String firstLetter = fieldName.substring(0, 1).toUpperCase();
      String setMethodName = "set" + firstLetter + fieldName.substring(1);

      try {
         Method setFun = tCls.getMethod(setMethodName, field.getType());
         if (setFun != null) {
            return setFun;
         }
      } catch (NoSuchMethodException var6) {
      } catch (Throwable var7) {
         EventBus.push(var7);
      }

      return null;
   }
}
