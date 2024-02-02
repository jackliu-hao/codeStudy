/*    */ package cn.hutool.core.bean.copier;
/*    */ 
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
/*    */ public class MapToMapCopier
/*    */   extends AbsCopier<Map, Map>
/*    */ {
/*    */   private final Type targetType;
/*    */   
/*    */   public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
/* 30 */     super(source, target, copyOptions);
/* 31 */     this.targetType = targetType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Map copy() {
/* 36 */     this.source.forEach((sKey, sValue) -> {
/*    */           if (null == sKey) {
/*    */             return;
/*    */           }
/*    */           
/*    */           String sKeyStr = this.copyOptions.editFieldName(sKey.toString());
/*    */           
/*    */           if (null == sKeyStr) {
/*    */             return;
/*    */           }
/*    */           
/*    */           Object targetValue = this.target.get(sKeyStr);
/*    */           
/*    */           if (false == this.copyOptions.override && null != targetValue) {
/*    */             return;
/*    */           }
/*    */           
/*    */           Type[] typeArguments = TypeUtil.getTypeArguments(this.targetType);
/*    */           
/*    */           if (null != typeArguments) {
/*    */             sValue = this.copyOptions.convertField(typeArguments[1], sValue);
/*    */             
/*    */             sValue = this.copyOptions.editFieldValue(sKeyStr, sValue);
/*    */           } 
/*    */           
/*    */           this.target.put(sKeyStr, sValue);
/*    */         });
/* 63 */     return this.target;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\MapToMapCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */