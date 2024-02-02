/*     */ package cn.hutool.core.bean.copier;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.bean.PropDesc;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapWrapper;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapToBeanCopier<T>
/*     */   extends AbsCopier<Map<?, ?>, T>
/*     */ {
/*     */   private final Type targetType;
/*     */   
/*     */   public MapToBeanCopier(Map<?, ?> source, T target, Type targetType, CopyOptions copyOptions) {
/*  36 */     super(source, target, copyOptions);
/*     */ 
/*     */     
/*  39 */     if (source instanceof MapWrapper) {
/*  40 */       Map<?, ?> raw = ((MapWrapper)source).getRaw();
/*  41 */       if (raw instanceof cn.hutool.core.map.CaseInsensitiveMap) {
/*  42 */         copyOptions.setIgnoreCase(true);
/*     */       }
/*     */     } 
/*     */     
/*  46 */     this.targetType = targetType;
/*     */   }
/*     */ 
/*     */   
/*     */   public T copy() {
/*  51 */     Class<?> actualEditable = this.target.getClass();
/*  52 */     if (null != this.copyOptions.editable) {
/*     */       
/*  54 */       Assert.isTrue(this.copyOptions.editable.isInstance(this.target), "Target class [{}] not assignable to Editable class [{}]", new Object[] { actualEditable
/*  55 */             .getName(), this.copyOptions.editable.getName() });
/*  56 */       actualEditable = this.copyOptions.editable;
/*     */     } 
/*  58 */     Map<String, PropDesc> targetPropDescMap = BeanUtil.getBeanDesc(actualEditable).getPropMap(this.copyOptions.ignoreCase);
/*     */     
/*  60 */     this.source.forEach((sKey, sValue) -> {
/*     */           if (null == sKey) {
/*     */             return;
/*     */           }
/*     */           
/*     */           String sKeyStr = this.copyOptions.editFieldName(sKey.toString());
/*     */           
/*     */           if (null == sKeyStr) {
/*     */             return;
/*     */           }
/*     */           
/*     */           PropDesc tDesc = findPropDesc(targetPropDescMap, sKeyStr);
/*     */           
/*     */           if (null == tDesc || false == tDesc.isWritable(this.copyOptions.transientSupport)) {
/*     */             return;
/*     */           }
/*     */           
/*     */           sKeyStr = tDesc.getFieldName();
/*     */           
/*     */           if (false == this.copyOptions.testPropertyFilter(tDesc.getField(), sValue)) {
/*     */             return;
/*     */           }
/*     */           
/*     */           Type fieldType = TypeUtil.getActualType(this.targetType, tDesc.getFieldType());
/*     */           
/*     */           Object newValue = this.copyOptions.convertField(fieldType, sValue);
/*     */           
/*     */           newValue = this.copyOptions.editFieldValue(sKeyStr, newValue);
/*     */           
/*     */           tDesc.setValue(this.target, newValue, this.copyOptions.ignoreNullValue, this.copyOptions.ignoreError, this.copyOptions.override);
/*     */         });
/*     */     
/*  92 */     return this.target;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PropDesc findPropDesc(Map<String, PropDesc> targetPropDescMap, String sKeyStr) {
/* 104 */     PropDesc propDesc = targetPropDescMap.get(sKeyStr);
/* 105 */     if (null != propDesc) {
/* 106 */       return propDesc;
/*     */     }
/*     */ 
/*     */     
/* 110 */     sKeyStr = StrUtil.toCamelCase(sKeyStr);
/* 111 */     propDesc = targetPropDescMap.get(sKeyStr);
/* 112 */     if (null != propDesc) {
/* 113 */       return propDesc;
/*     */     }
/*     */ 
/*     */     
/* 117 */     if (sKeyStr.startsWith("is")) {
/* 118 */       sKeyStr = StrUtil.removePreAndLowerFirst(sKeyStr, 2);
/* 119 */       propDesc = targetPropDescMap.get(sKeyStr);
/* 120 */       return propDesc;
/*     */     } 
/*     */     
/* 123 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\MapToBeanCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */