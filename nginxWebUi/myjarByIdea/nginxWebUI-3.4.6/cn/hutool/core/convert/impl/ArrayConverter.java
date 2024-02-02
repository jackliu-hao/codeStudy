package cn.hutool.core.convert.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ArrayConverter extends AbstractConverter<Object> {
   private static final long serialVersionUID = 1L;
   private final Class<?> targetType;
   private final Class<?> targetComponentType;
   private boolean ignoreElementError;

   public ArrayConverter(Class<?> targetType) {
      this(targetType, false);
   }

   public ArrayConverter(Class<?> targetType, boolean ignoreElementError) {
      if (null == targetType) {
         targetType = Object[].class;
      }

      if (targetType.isArray()) {
         this.targetType = targetType;
         this.targetComponentType = targetType.getComponentType();
      } else {
         this.targetComponentType = targetType;
         this.targetType = ArrayUtil.getArrayType(targetType);
      }

      this.ignoreElementError = ignoreElementError;
   }

   protected Object convertInternal(Object value) {
      return value.getClass().isArray() ? this.convertArrayToArray(value) : this.convertObjectToArray(value);
   }

   public Class getTargetType() {
      return this.targetType;
   }

   public void setIgnoreElementError(boolean ignoreElementError) {
      this.ignoreElementError = ignoreElementError;
   }

   private Object convertArrayToArray(Object array) {
      Class<?> valueComponentType = ArrayUtil.getComponentType(array);
      if (valueComponentType == this.targetComponentType) {
         return array;
      } else {
         int len = ArrayUtil.length(array);
         Object result = Array.newInstance(this.targetComponentType, len);

         for(int i = 0; i < len; ++i) {
            Array.set(result, i, this.convertComponentType(Array.get(array, i)));
         }

         return result;
      }
   }

   private Object convertObjectToArray(Object value) {
      if (value instanceof CharSequence) {
         if (this.targetComponentType != Character.TYPE && this.targetComponentType != Character.class) {
            if (this.targetComponentType == Byte.TYPE) {
               String str = value.toString();
               return Base64.isBase64((CharSequence)str) ? Base64.decode((CharSequence)value.toString()) : str.getBytes();
            } else {
               String[] strings = StrUtil.splitToArray(value.toString(), ',');
               return this.convertArrayToArray(strings);
            }
         } else {
            return this.convertArrayToArray(value.toString().toCharArray());
         }
      } else {
         Object result;
         List list;
         int i;
         if (value instanceof List) {
            list = (List)value;
            result = Array.newInstance(this.targetComponentType, list.size());

            for(i = 0; i < list.size(); ++i) {
               Array.set(result, i, this.convertComponentType(list.get(i)));
            }
         } else if (value instanceof Collection) {
            Collection<?> collection = (Collection)value;
            result = Array.newInstance(this.targetComponentType, collection.size());
            i = 0;

            for(Iterator var5 = collection.iterator(); var5.hasNext(); ++i) {
               Object element = var5.next();
               Array.set(result, i, this.convertComponentType(element));
            }
         } else if (value instanceof Iterable) {
            list = IterUtil.toList((Iterable)value);
            result = Array.newInstance(this.targetComponentType, list.size());

            for(i = 0; i < list.size(); ++i) {
               Array.set(result, i, this.convertComponentType(list.get(i)));
            }
         } else if (value instanceof Iterator) {
            list = IterUtil.toList((Iterator)value);
            result = Array.newInstance(this.targetComponentType, list.size());

            for(i = 0; i < list.size(); ++i) {
               Array.set(result, i, this.convertComponentType(list.get(i)));
            }
         } else if (value instanceof Number && Byte.TYPE == this.targetComponentType) {
            result = ByteUtil.numberToBytes((Number)value);
         } else if (value instanceof Serializable && Byte.TYPE == this.targetComponentType) {
            result = ObjectUtil.serialize(value);
         } else {
            result = this.convertToSingleElementArray(value);
         }

         return result;
      }
   }

   private Object[] convertToSingleElementArray(Object value) {
      Object[] singleElementArray = ArrayUtil.newArray(this.targetComponentType, 1);
      singleElementArray[0] = this.convertComponentType(value);
      return singleElementArray;
   }

   private Object convertComponentType(Object value) {
      return Convert.convertWithCheck(this.targetComponentType, value, (Object)null, this.ignoreElementError);
   }
}
