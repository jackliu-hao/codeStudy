/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.ClassUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import cn.hutool.core.util.StrUtil;
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
/*    */ public class FieldComparator<T>
/*    */   extends FuncComparator<T>
/*    */ {
/*    */   private static final long serialVersionUID = 9157326766723846313L;
/*    */   
/*    */   public FieldComparator(Class<T> beanClass, String fieldName) {
/* 27 */     this(getNonNullField(beanClass, fieldName));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FieldComparator(Field field) {
/* 36 */     this(true, field);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FieldComparator(boolean nullGreater, Field field) {
/* 46 */     super(nullGreater, bean -> (Comparable)ReflectUtil.getFieldValue(bean, (Field)Assert.notNull(field, "Field must be not null!", new Object[0])));
/*    */   }
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
/*    */   private static Field getNonNullField(Class<?> beanClass, String fieldName) {
/* 59 */     Field field = ClassUtil.getDeclaredField(beanClass, fieldName);
/* 60 */     if (field == null) {
/* 61 */       throw new IllegalArgumentException(StrUtil.format("Field [{}] not found in Class [{}]", new Object[] { fieldName, beanClass.getName() }));
/*    */     }
/* 63 */     return field;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\FieldComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */