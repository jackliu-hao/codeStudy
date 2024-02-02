/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import java.lang.reflect.Field;
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
/*    */ public class FieldsComparator<T>
/*    */   extends NullComparator<T>
/*    */ {
/*    */   private static final long serialVersionUID = 8649196282886500803L;
/*    */   
/*    */   public FieldsComparator(Class<T> beanClass, String... fieldNames) {
/* 25 */     this(true, beanClass, fieldNames);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FieldsComparator(boolean nullGreater, Class<T> beanClass, String... fieldNames) {
/* 36 */     super(nullGreater, (a, b) -> {
/*    */           for (String fieldName : fieldNames) {
/*    */             Field field = ClassUtil.getDeclaredField(beanClass, fieldName);
/*    */             Assert.notNull(field, "Field [{}] not found in Class [{}]", new Object[] { fieldName, beanClass.getName() });
/*    */             int compare = (new FieldComparator(field)).compare(a, b);
/*    */             if (0 != compare)
/*    */               return compare; 
/*    */           } 
/*    */           return 0;
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\FieldsComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */