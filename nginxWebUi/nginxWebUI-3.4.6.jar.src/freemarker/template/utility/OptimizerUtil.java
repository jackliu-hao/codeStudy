/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ public class OptimizerUtil
/*    */ {
/* 31 */   private static final BigInteger INTEGER_MIN = new BigInteger(Integer.toString(-2147483648));
/* 32 */   private static final BigInteger INTEGER_MAX = new BigInteger(Integer.toString(2147483647));
/* 33 */   private static final BigInteger LONG_MIN = new BigInteger(Long.toString(Long.MIN_VALUE));
/* 34 */   private static final BigInteger LONG_MAX = new BigInteger(Long.toString(Long.MAX_VALUE));
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List optimizeListStorage(List list) {
/* 40 */     switch (list.size()) {
/*    */ 
/*    */       
/*    */       case 0:
/* 44 */         return Collections.EMPTY_LIST;
/*    */ 
/*    */       
/*    */       case 1:
/* 48 */         return Collections.singletonList(list.get(0));
/*    */     } 
/*    */ 
/*    */     
/* 52 */     if (list instanceof ArrayList) {
/* 53 */       ((ArrayList)list).trimToSize();
/*    */     }
/* 55 */     return list;
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
/*    */ 
/*    */ 
/*    */   
/*    */   public static Number optimizeNumberRepresentation(Number number) {
/* 71 */     if (number instanceof BigDecimal) {
/* 72 */       BigDecimal bd = (BigDecimal)number;
/* 73 */       if (bd.scale() == 0) {
/*    */         
/* 75 */         number = bd.unscaledValue();
/*    */       } else {
/* 77 */         double d = bd.doubleValue();
/* 78 */         if (d != Double.POSITIVE_INFINITY && d != Double.NEGATIVE_INFINITY)
/*    */         {
/* 80 */           return Double.valueOf(d);
/*    */         }
/*    */       } 
/*    */     } 
/* 84 */     if (number instanceof BigInteger) {
/* 85 */       BigInteger bi = (BigInteger)number;
/* 86 */       if (bi.compareTo(INTEGER_MAX) <= 0 && bi.compareTo(INTEGER_MIN) >= 0)
/*    */       {
/* 88 */         return Integer.valueOf(bi.intValue());
/*    */       }
/* 90 */       if (bi.compareTo(LONG_MAX) <= 0 && bi.compareTo(LONG_MIN) >= 0)
/*    */       {
/* 92 */         return Long.valueOf(bi.longValue());
/*    */       }
/*    */     } 
/* 95 */     return number;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\OptimizerUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */