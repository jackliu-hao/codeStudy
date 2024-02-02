/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Field;
/*    */ import java.util.Comparator;
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
/*    */ @Deprecated
/*    */ public abstract class BaseFieldComparator<T>
/*    */   implements Comparator<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -3482464782340308755L;
/*    */   
/*    */   protected int compareItem(T o1, T o2, Field field) {
/*    */     Comparable<?> v1;
/*    */     Comparable<?> v2;
/* 31 */     if (o1 == o2)
/* 32 */       return 0; 
/* 33 */     if (null == o1)
/* 34 */       return 1; 
/* 35 */     if (null == o2) {
/* 36 */       return -1;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 42 */       v1 = (Comparable)ReflectUtil.getFieldValue(o1, field);
/* 43 */       v2 = (Comparable)ReflectUtil.getFieldValue(o2, field);
/* 44 */     } catch (Exception e) {
/* 45 */       throw new ComparatorException(e);
/*    */     } 
/*    */     
/* 48 */     return compare(o1, o2, v1, v2);
/*    */   }
/*    */ 
/*    */   
/*    */   private int compare(T o1, T o2, Comparable fieldValue1, Comparable fieldValue2) {
/* 53 */     int result = ObjectUtil.compare(fieldValue1, fieldValue2);
/* 54 */     if (0 == result)
/*    */     {
/* 56 */       result = CompareUtil.compare(o1, o2, true);
/*    */     }
/* 58 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\BaseFieldComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */