package org.noear.snack.core.exts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.noear.snack.annotation.NodeName;
import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.core.utils.StringUtil;
import org.noear.snack.exception.SnackException;

public class FieldWrap {
   public final Field field;
   public final Class<?> type;
   public final Type genericType;
   public final boolean readonly;
   private String name;
   private String format;
   private boolean serialize = true;
   private boolean deserialize = true;
   private boolean incNull = true;
   private Method _setter;

   public FieldWrap(Class<?> clz, Field f, boolean isFinal) {
      this.field = f;
      this.type = f.getType();
      this.genericType = f.getGenericType();
      this.readonly = isFinal;
      this.field.setAccessible(true);
      NodeName anno = (NodeName)f.getAnnotation(NodeName.class);
      if (anno != null) {
         this.name = anno.value();
      }

      ONodeAttr attr = (ONodeAttr)f.getAnnotation(ONodeAttr.class);
      if (attr != null) {
         this.name = attr.name();
         this.format = attr.format();
         this.incNull = attr.incNull();
         if (attr.ignore()) {
            this.serialize = false;
            this.deserialize = false;
         } else {
            this.serialize = attr.serialize();
            this.deserialize = attr.deserialize();
         }
      }

      if (StringUtil.isEmpty(this.name)) {
         this.name = this.field.getName();
      }

      this._setter = doFindSetter(clz, f);
   }

   /** @deprecated */
   @Deprecated
   public String name() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public String getFormat() {
      return this.format;
   }

   public boolean isDeserialize() {
      return this.deserialize;
   }

   public boolean isSerialize() {
      return this.serialize;
   }

   public boolean isIncNull() {
      return this.incNull;
   }

   public void setValue(Object tObj, Object val) {
      this.setValue(tObj, val, true);
   }

   public void setValue(Object tObj, Object val, boolean disFun) {
      if (!this.readonly) {
         try {
            if (this._setter != null && !disFun) {
               this._setter.invoke(tObj, val);
            } else {
               this.field.set(tObj, val);
            }

         } catch (IllegalArgumentException var5) {
            if (val == null) {
               throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failur!", var5);
            } else {
               throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure ：val(" + val.getClass().getSimpleName() + ")", var5);
            }
         } catch (IllegalAccessException var6) {
            throw new SnackException(var6);
         } catch (RuntimeException var7) {
            throw var7;
         } catch (Throwable var8) {
            throw new RuntimeException(var8);
         }
      }
   }

   public Object getValue(Object tObj) {
      try {
         return this.field.get(tObj);
      } catch (IllegalAccessException var3) {
         throw new SnackException(var3);
      }
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
      } catch (RuntimeException var7) {
         throw var7;
      } catch (Throwable var8) {
         throw new RuntimeException(var8);
      }

      return null;
   }
}
