package cn.hutool.core.lang;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType, Serializable {
   private static final long serialVersionUID = 1L;
   private final Type[] actualTypeArguments;
   private final Type ownerType;
   private final Type rawType;

   public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
      this.actualTypeArguments = actualTypeArguments;
      this.ownerType = ownerType;
      this.rawType = rawType;
   }

   public Type[] getActualTypeArguments() {
      return this.actualTypeArguments;
   }

   public Type getOwnerType() {
      return this.ownerType;
   }

   public Type getRawType() {
      return this.rawType;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      Type useOwner = this.ownerType;
      Class<?> raw = (Class)this.rawType;
      if (useOwner == null) {
         buf.append(raw.getName());
      } else {
         if (useOwner instanceof Class) {
            buf.append(((Class)useOwner).getName());
         } else {
            buf.append(useOwner.toString());
         }

         buf.append('.').append(raw.getSimpleName());
      }

      appendAllTo(buf.append('<'), ", ", this.actualTypeArguments).append('>');
      return buf.toString();
   }

   private static StringBuilder appendAllTo(StringBuilder buf, String sep, Type... types) {
      if (ArrayUtil.isNotEmpty((Object[])types)) {
         boolean isFirst = true;
         Type[] var4 = types;
         int var5 = types.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Type type = var4[var6];
            if (isFirst) {
               isFirst = false;
            } else {
               buf.append(sep);
            }

            String typeStr;
            if (type instanceof Class) {
               typeStr = ((Class)type).getName();
            } else {
               typeStr = StrUtil.toString(type);
            }

            buf.append(typeStr);
         }
      }

      return buf;
   }
}
