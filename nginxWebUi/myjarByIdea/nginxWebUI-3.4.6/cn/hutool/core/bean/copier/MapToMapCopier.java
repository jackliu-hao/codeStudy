package cn.hutool.core.bean.copier;

import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

public class MapToMapCopier extends AbsCopier<Map, Map> {
   private final Type targetType;

   public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
      super(source, target, copyOptions);
      this.targetType = targetType;
   }

   public Map copy() {
      ((Map)this.source).forEach((sKey, sValue) -> {
         if (null != sKey) {
            String sKeyStr = this.copyOptions.editFieldName(sKey.toString());
            if (null != sKeyStr) {
               Object targetValue = ((Map)this.target).get(sKeyStr);
               if (this.copyOptions.override || null == targetValue) {
                  Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
                  if (null != typeArguments) {
                     sValue = this.copyOptions.convertField(typeArguments[1], sValue);
                     sValue = this.copyOptions.editFieldValue(sKeyStr, sValue);
                  }

                  ((Map)this.target).put(sKeyStr, sValue);
               }
            }
         }
      });
      return (Map)this.target;
   }
}
