/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.text.Collator;
/*    */ import java.util.Comparator;
/*    */ import java.util.Locale;
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
/*    */ public class PinyinComparator
/*    */   implements Comparator<String>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 23 */   final Collator collator = Collator.getInstance(Locale.CHINESE);
/*    */ 
/*    */ 
/*    */   
/*    */   public int compare(String o1, String o2) {
/* 28 */     return this.collator.compare(o1, o2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\PinyinComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */