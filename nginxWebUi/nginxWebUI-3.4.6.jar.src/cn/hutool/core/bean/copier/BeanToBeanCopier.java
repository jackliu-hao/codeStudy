/*    */ package cn.hutool.core.bean.copier;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ import cn.hutool.core.bean.PropDesc;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.TypeUtil;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanToBeanCopier<S, T>
/*    */   extends AbsCopier<S, T>
/*    */ {
/*    */   private final Type targetType;
/*    */   
/*    */   public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
/* 34 */     super(source, target, copyOptions);
/* 35 */     this.targetType = targetType;
/*    */   }
/*    */ 
/*    */   
/*    */   public T copy() {
/* 40 */     Class<?> actualEditable = this.target.getClass();
/* 41 */     if (null != this.copyOptions.editable) {
/*    */       
/* 43 */       Assert.isTrue(this.copyOptions.editable.isInstance(this.target), "Target class [{}] not assignable to Editable class [{}]", new Object[] { actualEditable
/* 44 */             .getName(), this.copyOptions.editable.getName() });
/* 45 */       actualEditable = this.copyOptions.editable;
/*    */     } 
/* 47 */     Map<String, PropDesc> targetPropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
/*    */     
/* 49 */     Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(this.source.getClass()).getPropMap(this.copyOptions.ignoreCase);
/* 50 */     sourcePropDescMap.forEach((sFieldName, sDesc) -> {
/*    */           if (null == sFieldName || false == sDesc.isReadable(this.copyOptions.transientSupport)) {
/*    */             return;
/*    */           }
/*    */ 
/*    */           
/*    */           sFieldName = this.copyOptions.editFieldName(sFieldName);
/*    */ 
/*    */           
/*    */           if (null == sFieldName) {
/*    */             return;
/*    */           }
/*    */           
/*    */           PropDesc tDesc = (PropDesc)targetPropDescMap.get(sFieldName);
/*    */           
/*    */           if (null == tDesc || false == tDesc.isWritable(this.copyOptions.transientSupport)) {
/*    */             return;
/*    */           }
/*    */           
/*    */           Object sValue = sDesc.getValue(this.source);
/*    */           
/*    */           if (false == this.copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
/*    */             return;
/*    */           }
/*    */           
/*    */           Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
/*    */           
/*    */           sValue = this.copyOptions.convertField(fieldType, sValue);
/*    */           
/*    */           sValue = this.copyOptions.editFieldValue(sFieldName, sValue);
/*    */           
/*    */           tDesc.setValue(this.target, sValue, this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
/*    */         });
/*    */     
/* 84 */     return this.target;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\BeanToBeanCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */