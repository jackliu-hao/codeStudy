/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.AbstractCollection;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.Spliterator;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Predicate;
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
/*    */ public class TransCollection<F, T>
/*    */   extends AbstractCollection<T>
/*    */ {
/*    */   private final Collection<F> fromCollection;
/*    */   private final Function<? super F, ? extends T> function;
/*    */   
/*    */   public TransCollection(Collection<F> fromCollection, Function<? super F, ? extends T> function) {
/* 33 */     this.fromCollection = (Collection<F>)Assert.notNull(fromCollection);
/* 34 */     this.function = (Function<? super F, ? extends T>)Assert.notNull(function);
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<T> iterator() {
/* 39 */     return IterUtil.trans(this.fromCollection.iterator(), this.function);
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 44 */     this.fromCollection.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 49 */     return this.fromCollection.isEmpty();
/*    */   }
/*    */ 
/*    */   
/*    */   public void forEach(Consumer<? super T> action) {
/* 54 */     Assert.notNull(action);
/* 55 */     this.fromCollection.forEach(f -> action.accept(this.function.apply((F)f)));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean removeIf(Predicate<? super T> filter) {
/* 60 */     Assert.notNull(filter);
/* 61 */     return this.fromCollection.removeIf(element -> filter.test(this.function.apply((F)element)));
/*    */   }
/*    */ 
/*    */   
/*    */   public Spliterator<T> spliterator() {
/* 66 */     return SpliteratorUtil.trans(this.fromCollection.spliterator(), this.function);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 71 */     return this.fromCollection.size();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\TransCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */