/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.Spliterator;
/*    */ import java.util.function.Function;
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
/*    */ public class SpliteratorUtil
/*    */ {
/*    */   public static <F, T> Spliterator<T> trans(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
/* 24 */     return new TransSpliterator<>(fromSpliterator, function);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\SpliteratorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */