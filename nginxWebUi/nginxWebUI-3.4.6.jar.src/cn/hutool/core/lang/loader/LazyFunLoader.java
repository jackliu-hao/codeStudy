/*    */ package cn.hutool.core.lang.loader;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Supplier;
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
/*    */ 
/*    */ public class LazyFunLoader<T>
/*    */   extends LazyLoader<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Supplier<T> supplier;
/*    */   
/*    */   public static <T> LazyFunLoader<T> on(Supplier<T> supplier) {
/* 35 */     Assert.notNull(supplier, "supplier must be not null!", new Object[0]);
/* 36 */     return new LazyFunLoader<>(supplier);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LazyFunLoader(Supplier<T> supplier) {
/* 45 */     Assert.notNull(supplier);
/* 46 */     this.supplier = supplier;
/*    */   }
/*    */ 
/*    */   
/*    */   protected T init() {
/* 51 */     T t = this.supplier.get();
/* 52 */     this.supplier = null;
/* 53 */     return t;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isInitialize() {
/* 62 */     return (this.supplier == null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void ifInitialized(Consumer<T> consumer) {
/* 71 */     Assert.notNull(consumer);
/*    */ 
/*    */     
/* 74 */     if (isInitialize())
/* 75 */       consumer.accept(get()); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\loader\LazyFunLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */