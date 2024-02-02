package cn.hutool.core.bean.copier;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

public class BeanToMapCopier extends AbsCopier<Object, Map> {
   private final Type targetType;

   public BeanToMapCopier(Object source, Map target, Type targetType, CopyOptions copyOptions) {
      super(source, target, copyOptions);
      this.targetType = targetType;
   }

   public Map copy() {
      Class<?> actualEditable = this.source.getClass();
      if (null != this.copyOptions.editable) {
         Assert.isTrue(this.copyOptions.editable.isInstance(this.source), "Source class [{}] not assignable to Editable class [{}]", actualEditable.getName(), this.copyOptions.editable.getName());
         actualEditable = this.copyOptions.editable;
      }

      Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
      sourcePropDescMap.forEach((sFieldName, sDesc) -> {
         if (null != sFieldName && sDesc.isReadable(this.copyOptions.transientSupport)) {
            sFieldName = this.copyOptions.editFieldName(sFieldName);
            if (null != sFieldName) {
               Object sValue = sDesc.getValue(this.source);
               if (this.copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
                  Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
                  if (null != typeArguments) {
                     sValue = this.copyOptions.convertField(typeArguments[1], sValue);
                     sValue = this.copyOptions.editFieldValue(sFieldName, sValue);
                  }

                  if (null != sValue || !this.copyOptions.ignoreNullValue) {
                     ((Map)this.target).put(sFieldName, sValue);
                  }

               }
            }
         }
      });
      return (Map)this.target;
   }
}
