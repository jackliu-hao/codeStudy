package cn.hutool.core.lang;

import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;

public abstract class TypeReference<T> implements Type {
   private final Type type = TypeUtil.getTypeArgument(this.getClass());

   public Type getType() {
      return this.type;
   }

   public String toString() {
      return this.type.toString();
   }
}
