package cn.hutool.core.bean.copier;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

public class BeanToBeanCopier<S, T> extends AbsCopier<S, T> {
   private final Type targetType;

   public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
      super(source, target, copyOptions);
      this.targetType = targetType;
   }

   public T copy() {
      Class<?> actualEditable = this.target.getClass();
      if (null != this.copyOptions.editable) {
         Assert.isTrue(this.copyOptions.editable.isInstance(this.target), "Target class [{}] not assignable to Editable class [{}]", actualEditable.getName(), this.copyOptions.editable.getName());
         actualEditable = this.copyOptions.editable;
      }

      Map<String, PropDesc> targetPropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
      Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(this.source.getClass()).getPropMap(this.copyOptions.ignoreCase);
      sourcePropDescMap.forEach((sFieldName, sDesc) -> {
         if (null != sFieldName && sDesc.isReadable(this.copyOptions.transientSupport)) {
            sFieldName = this.copyOptions.editFieldName(sFieldName);
            if (null != sFieldName) {
               PropDesc tDesc = (PropDesc)targetPropDescMap.get(sFieldName);
               if (null != tDesc && tDesc.isWritable(this.copyOptions.transientSupport)) {
                  Object sValue = sDesc.getValue(this.source);
                  if (this.copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
                     Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
                     sValue = this.copyOptions.convertField(fieldType, sValue);
                     sValue = this.copyOptions.editFieldValue(sFieldName, sValue);
                     tDesc.setValue(this.target, sValue, this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
                  }
               }
            }
         }
      });
      return this.target;
   }
}
