/*    */ package cn.hutool.core.bean.copier.provider;
/*    */ 
/*    */ import cn.hutool.core.bean.DynaBean;
/*    */ import cn.hutool.core.bean.copier.ValueProvider;
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import java.lang.reflect.Type;
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
/*    */ public class DynaBeanValueProvider
/*    */   implements ValueProvider<String>
/*    */ {
/*    */   private final DynaBean dynaBean;
/*    */   private final boolean ignoreError;
/*    */   
/*    */   public DynaBeanValueProvider(DynaBean dynaBean, boolean ignoreError) {
/* 27 */     this.dynaBean = dynaBean;
/* 28 */     this.ignoreError = ignoreError;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object value(String key, Type valueType) {
/* 33 */     Object value = this.dynaBean.get(key);
/* 34 */     return Convert.convertWithCheck(valueType, value, null, this.ignoreError);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean containsKey(String key) {
/* 39 */     return this.dynaBean.containsProp(key);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\provider\DynaBeanValueProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */