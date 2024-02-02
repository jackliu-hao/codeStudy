/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
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
/*    */ public class PropertyComparator<T>
/*    */   extends FuncComparator<T>
/*    */ {
/*    */   private static final long serialVersionUID = 9157326766723846313L;
/*    */   
/*    */   public PropertyComparator(String property) {
/* 22 */     this(property, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PropertyComparator(String property, boolean isNullGreater) {
/* 32 */     super(isNullGreater, bean -> (Comparable)BeanUtil.getProperty(bean, property));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\PropertyComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */