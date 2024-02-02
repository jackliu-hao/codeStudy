package org.noear.solon.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class ParameterizedTypeImpl implements ParameterizedType {
   private final Type[] actualTypeArguments;
   private final Class<?> rawType;
   private final Type ownerType;

   public ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
      this.actualTypeArguments = actualTypeArguments;
      this.rawType = rawType;
      this.ownerType = (Type)(ownerType != null ? ownerType : rawType.getDeclaringClass());
   }

   public Type[] getActualTypeArguments() {
      return this.actualTypeArguments;
   }

   public Type getRawType() {
      return this.rawType;
   }

   public Type getOwnerType() {
      return this.ownerType;
   }

   public boolean equals(Object o) {
      if (o instanceof ParameterizedType) {
         ParameterizedType that = (ParameterizedType)o;
         if (this == that) {
            return true;
         } else {
            Type thatOwner = that.getOwnerType();
            Type thatRawType = that.getRawType();
            return Objects.equals(this.ownerType, thatOwner) && Objects.equals(this.rawType, thatRawType) && Arrays.equals(this.actualTypeArguments, that.getActualTypeArguments());
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      if (this.ownerType != null) {
         sb.append(this.ownerType.getTypeName());
         sb.append("$");
         if (this.ownerType instanceof ParameterizedTypeImpl) {
            sb.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
         } else {
            sb.append(this.rawType.getSimpleName());
         }
      } else {
         sb.append(this.rawType.getName());
      }

      if (this.actualTypeArguments != null) {
         StringJoiner sj = new StringJoiner(", ", "<", ">");
         sj.setEmptyValue("");
         Type[] var3 = this.actualTypeArguments;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Type t = var3[var5];
            sj.add(t.getTypeName());
         }

         sb.append(sj.toString());
      }

      return sb.toString();
   }
}
