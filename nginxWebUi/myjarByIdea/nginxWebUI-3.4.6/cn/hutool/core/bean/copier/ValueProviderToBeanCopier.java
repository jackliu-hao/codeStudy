package cn.hutool.core.bean.copier;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

public class ValueProviderToBeanCopier<T> extends AbsCopier<ValueProvider<String>, T> {
   private final Type targetType;

   public ValueProviderToBeanCopier(ValueProvider<String> source, T target, Type targetType, CopyOptions copyOptions) {
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
      targetPropDescMap.forEach((tFieldName, tDesc) -> {
         if (null != tFieldName) {
            tFieldName = this.copyOptions.editFieldName(tFieldName);
            if (null != tFieldName) {
               if (((ValueProvider)this.source).containsKey(tFieldName)) {
                  if (null != tDesc && tDesc.isWritable(this.copyOptions.transientSupport)) {
                     Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
                     Object sValue = ((ValueProvider)this.source).value(tFieldName, fieldType);
                     if (this.copyOptions.testPropertyFilter(tDesc.getField(), sValue)) {
                        sValue = this.copyOptions.editFieldValue(tFieldName, sValue);
                        tDesc.setValue(this.target, sValue, this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
                     }
                  }
               }
            }
         }
      });
      return this.target;
   }
}
