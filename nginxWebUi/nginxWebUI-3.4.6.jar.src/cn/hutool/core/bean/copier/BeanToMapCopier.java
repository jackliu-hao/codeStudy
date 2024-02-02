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
/*    */ public class BeanToMapCopier
/*    */   extends AbsCopier<Object, Map>
/*    */ {
/*    */   private final Type targetType;
/*    */   
/*    */   public BeanToMapCopier(Object source, Map target, Type targetType, CopyOptions copyOptions) {
/* 33 */     super(source, target, copyOptions);
/* 34 */     this.targetType = targetType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Map copy() {
/* 39 */     Class<?> actualEditable = this.source.getClass();
/* 40 */     if (null != this.copyOptions.editable) {
/*    */       
/* 42 */       Assert.isTrue(this.copyOptions.editable.isInstance(this.source), "Source class [{}] not assignable to Editable class [{}]", new Object[] { actualEditable
/* 43 */             .getName(), this.copyOptions.editable.getName() });
/* 44 */       actualEditable = this.copyOptions.editable;
/*    */     } 
/*    */     
/* 47 */     Map<String, PropDesc> sourcePropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
/* 48 */     sourcePropDescMap.forEach((sFieldName, sDesc) -> {
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
/*    */           Object sValue = sDesc.getValue(this.source);
/*    */           
/*    */           if (false == this.copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
/*    */             return;
/*    */           }
/*    */           
/*    */           Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
/*    */           
/*    */           if (null != typeArguments) {
/*    */             sValue = this.copyOptions.convertField(typeArguments[1], sValue);
/*    */             
/*    */             sValue = this.copyOptions.editFieldValue(sFieldName, sValue);
/*    */           } 
/*    */           
/*    */           if (null != sValue || false == this.copyOptions.ignoreNullValue) {
/*    */             this.target.put(sFieldName, sValue);
/*    */           }
/*    */         });
/*    */     
/* 80 */     return this.target;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\BeanToMapCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */