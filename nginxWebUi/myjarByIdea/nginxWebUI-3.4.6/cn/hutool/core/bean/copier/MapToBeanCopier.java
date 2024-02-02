package cn.hutool.core.bean.copier;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

public class MapToBeanCopier<T> extends AbsCopier<Map<?, ?>, T> {
   private final Type targetType;

   public MapToBeanCopier(Map<?, ?> source, T target, Type targetType, CopyOptions copyOptions) {
      super(source, target, copyOptions);
      if (source instanceof MapWrapper) {
         Map<?, ?> raw = ((MapWrapper)source).getRaw();
         if (raw instanceof CaseInsensitiveMap) {
            copyOptions.setIgnoreCase(true);
         }
      }

      this.targetType = targetType;
   }

   public T copy() {
      Class<?> actualEditable = this.target.getClass();
      if (null != this.copyOptions.editable) {
         Assert.isTrue(this.copyOptions.editable.isInstance(this.target), "Target class [{}] not assignable to Editable class [{}]", actualEditable.getName(), this.copyOptions.editable.getName());
         actualEditable = this.copyOptions.editable;
      }

      Map<String, PropDesc> targetPropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
      ((Map)this.source).forEach((sKey, sValue) -> {
         if (null != sKey) {
            String sKeyStr = this.copyOptions.editFieldName(sKey.toString());
            if (null != sKeyStr) {
               PropDesc tDesc = this.findPropDesc(targetPropDescMap, sKeyStr);
               if (null != tDesc && tDesc.isWritable(this.copyOptions.transientSupport)) {
                  sKeyStr = tDesc.getFieldName();
                  if (this.copyOptions.testPropertyFilter(tDesc.getField(), sValue)) {
                     Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
                     Object newValue = this.copyOptions.convertField(fieldType, sValue);
                     newValue = this.copyOptions.editFieldValue(sKeyStr, newValue);
                     tDesc.setValue(this.target, newValue, this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
                  }
               }
            }
         }
      });
      return this.target;
   }

   private PropDesc findPropDesc(Map<String, PropDesc> targetPropDescMap, String sKeyStr) {
      PropDesc propDesc = (PropDesc)targetPropDescMap.get(sKeyStr);
      if (null != propDesc) {
         return propDesc;
      } else {
         sKeyStr = StrUtil.toCamelCase(sKeyStr);
         propDesc = (PropDesc)targetPropDescMap.get(sKeyStr);
         if (null != propDesc) {
            return propDesc;
         } else if (sKeyStr.startsWith("is")) {
            sKeyStr = StrUtil.removePreAndLowerFirst(sKeyStr, 2);
            propDesc = (PropDesc)targetPropDescMap.get(sKeyStr);
            return propDesc;
         } else {
            return null;
         }
      }
   }
}
