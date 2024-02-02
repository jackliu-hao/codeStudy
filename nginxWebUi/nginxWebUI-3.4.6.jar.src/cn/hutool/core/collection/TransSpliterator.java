/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.Spliterator;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TransSpliterator<F, T>
/*    */   implements Spliterator<T>
/*    */ {
/*    */   private final Spliterator<F> fromSpliterator;
/*    */   private final Function<? super F, ? extends T> function;
/*    */   
/*    */   public TransSpliterator(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
/* 20 */     this.fromSpliterator = fromSpliterator;
/* 21 */     this.function = function;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean tryAdvance(Consumer<? super T> action) {
/* 26 */     return this.fromSpliterator.tryAdvance(fromElement -> action.accept(this.function.apply((F)fromElement)));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void forEachRemaining(Consumer<? super T> action) {
/* 32 */     this.fromSpliterator.forEachRemaining(fromElement -> action.accept(this.function.apply((F)fromElement)));
/*    */   }
/*    */ 
/*    */   
/*    */   public Spliterator<T> trySplit() {
/* 37 */     Spliterator<F> fromSplit = this.fromSpliterator.trySplit();
/* 38 */     return (fromSplit != null) ? new TransSpliterator(fromSplit, this.function) : null;
/*    */   }
/*    */ 
/*    */   
/*    */   public long estimateSize() {
/* 43 */     return this.fromSpliterator.estimateSize();
/*    */   }
/*    */ 
/*    */   
/*    */   public int characteristics() {
/* 48 */     return this.fromSpliterator.characteristics() & 0xFFFFFEFA;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\TransSpliterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */