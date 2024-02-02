/*     */ package org.wildfly.common.function;
/*     */ 
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import org.wildfly.common.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Functions
/*     */ {
/*     */   public static Consumer<Runnable> runnableConsumer() {
/*  41 */     return RunnableConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E extends Exception> ExceptionConsumer<ExceptionRunnable<E>, E> exceptionRunnableConsumer() {
/*  52 */     return ExceptionRunnableConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExceptionConsumer<Runnable, RuntimeException> runnableExceptionConsumer() {
/*  61 */     return RunnableExceptionConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> BiConsumer<Consumer<T>, T> consumerBiConsumer() {
/*  72 */     return ConsumerBiConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> ExceptionBiConsumer<ExceptionConsumer<T, E>, T, E> exceptionConsumerBiConsumer() {
/*  84 */     return ExceptionConsumerBiConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ExceptionBiConsumer<Consumer<T>, T, RuntimeException> consumerExceptionBiConsumer() {
/*  95 */     return ConsumerExceptionBiConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <R> Function<Supplier<R>, R> supplierFunction() {
/* 106 */     return SupplierFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <R, E extends Exception> ExceptionFunction<ExceptionSupplier<R, E>, R, E> exceptionSupplierFunction() {
/* 118 */     return ExceptionSupplierFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <R> ExceptionFunction<Supplier<R>, R, RuntimeException> supplierExceptionFunction() {
/* 129 */     return SupplierExceptionFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <R> BiFunction<Function<Supplier<R>, R>, Supplier<R>, R> supplierFunctionBiFunction() {
/* 141 */     return FunctionSupplierBiFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <R, E extends Exception> ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<R, E>, R, E>, ExceptionSupplier<R, E>, R, E> exceptionSupplierFunctionBiFunction() {
/* 154 */     return ExceptionFunctionSupplierBiFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, R> BiFunction<Function<T, R>, T, R> functionBiFunction() {
/* 167 */     return FunctionBiFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, R, E extends Exception> ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> exceptionFunctionBiFunction() {
/* 181 */     return ExceptionFunctionBiFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, R> ExceptionBiFunction<Function<T, R>, T, R, RuntimeException> functionExceptionBiFunction() {
/* 194 */     return FunctionExceptionBiFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Supplier<T> constantSupplier(T value) {
/* 206 */     return (value == null) ? ConstantSupplier.NULL : new ConstantSupplier<>(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> ExceptionSupplier<T, E> constantExceptionSupplier(T value) {
/* 219 */     return (value == null) ? ConstantSupplier.NULL : (ExceptionSupplier)new ConstantSupplier<>(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, U> Runnable capturingRunnable(BiConsumer<T, U> consumer, T param1, U param2) {
/* 233 */     Assert.checkNotNullParam("consumer", consumer);
/* 234 */     return new BiConsumerRunnable<>(consumer, param1, param2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Runnable capturingRunnable(Consumer<T> consumer, T param) {
/* 246 */     Assert.checkNotNullParam("consumer", consumer);
/* 247 */     return new ConsumerRunnable<>(consumer, param);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, U, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) {
/* 262 */     Assert.checkNotNullParam("consumer", consumer);
/* 263 */     return new ExceptionBiConsumerRunnable<>(consumer, param1, param2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionConsumer<T, E> consumer, T param) {
/* 276 */     Assert.checkNotNullParam("consumer", consumer);
/* 277 */     return new ExceptionConsumerRunnable<>(consumer, param);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Consumer<T> discardingConsumer() {
/* 288 */     return DiscardingConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, E extends Exception> ExceptionConsumer<T, E> discardingExceptionConsumer() {
/* 300 */     return DiscardingConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, U> BiConsumer<T, U> discardingBiConsumer() {
/* 312 */     return DiscardingBiConsumer.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, U, E extends Exception> ExceptionBiConsumer<T, U, E> discardingExceptionBiConsumer() {
/* 325 */     return DiscardingBiConsumer.INSTANCE;
/*     */   }
/*     */   
/*     */   static class RunnableConsumer implements Consumer<Runnable> {
/* 329 */     static final Consumer<Runnable> INSTANCE = new RunnableConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(Runnable runnable) {
/* 334 */       runnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionRunnableConsumer<E extends Exception> implements ExceptionConsumer<ExceptionRunnable<E>, E> {
/* 339 */     static final ExceptionConsumer INSTANCE = new ExceptionRunnableConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(ExceptionRunnable<E> exceptionRunnable) throws E {
/* 344 */       exceptionRunnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   static class RunnableExceptionConsumer implements ExceptionConsumer<Runnable, RuntimeException> {
/* 349 */     static final RunnableExceptionConsumer INSTANCE = new RunnableExceptionConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(Runnable runnable) throws RuntimeException {
/* 354 */       runnable.run();
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConsumerBiConsumer implements BiConsumer<Consumer<Object>, Object> {
/* 359 */     static final BiConsumer INSTANCE = new ConsumerBiConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(Consumer<Object> consumer, Object o) {
/* 364 */       consumer.accept(o);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionConsumerBiConsumer<E extends Exception> implements ExceptionBiConsumer<ExceptionConsumer<Object, E>, Object, E> {
/* 369 */     static final ExceptionBiConsumer INSTANCE = new ExceptionConsumerBiConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(ExceptionConsumer<Object, E> consumer, Object o) throws E {
/* 374 */       consumer.accept(o);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConsumerExceptionBiConsumer<T> implements ExceptionBiConsumer<Consumer<T>, T, RuntimeException> {
/* 379 */     static final ExceptionBiConsumer INSTANCE = new ConsumerExceptionBiConsumer();
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(Consumer<T> consumer, T t) throws RuntimeException {
/* 384 */       consumer.accept(t);
/*     */     }
/*     */   }
/*     */   
/*     */   static class SupplierFunction implements Function<Supplier<Object>, Object> {
/* 389 */     static final Function INSTANCE = new SupplierFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public Object apply(Supplier<Object> supplier) {
/* 394 */       return supplier.get();
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionSupplierFunction<E extends Exception> implements ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> {
/* 399 */     static final ExceptionFunction INSTANCE = new ExceptionSupplierFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public Object apply(ExceptionSupplier<Object, E> supplier) throws E {
/* 404 */       return supplier.get();
/*     */     }
/*     */   }
/*     */   
/*     */   static class SupplierExceptionFunction<R> implements ExceptionFunction<Supplier<R>, R, RuntimeException> {
/* 409 */     static final SupplierExceptionFunction INSTANCE = new SupplierExceptionFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public R apply(Supplier<R> supplier) throws RuntimeException {
/* 414 */       return supplier.get();
/*     */     }
/*     */   }
/*     */   
/*     */   static class FunctionSupplierBiFunction implements BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> {
/* 419 */     static final BiFunction INSTANCE = new FunctionSupplierBiFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public Object apply(Function<Supplier<Object>, Object> function, Supplier<Object> supplier) {
/* 424 */       return function.apply(supplier);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionFunctionSupplierBiFunction<E extends Exception> implements ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<Object, E>, Object, E>, ExceptionSupplier<Object, E>, Object, E> {
/* 429 */     static final ExceptionBiFunction INSTANCE = new ExceptionFunctionSupplierBiFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public Object apply(ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> function, ExceptionSupplier<Object, E> supplier) throws E {
/* 434 */       return function.apply(supplier);
/*     */     }
/*     */   }
/*     */   
/*     */   static class FunctionExceptionBiFunction<T, R> implements ExceptionBiFunction<Function<T, R>, T, R, RuntimeException> {
/* 439 */     static final FunctionExceptionBiFunction INSTANCE = new FunctionExceptionBiFunction();
/*     */ 
/*     */ 
/*     */     
/*     */     public R apply(Function<T, R> function, T t) throws RuntimeException {
/* 444 */       return function.apply(t);
/*     */     }
/*     */   }
/*     */   
/*     */   static class FunctionBiFunction<T, R> implements BiFunction<Function<T, R>, T, R> {
/* 449 */     static final BiFunction INSTANCE = new FunctionBiFunction();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public R apply(Function<T, R> function, T t) {
/* 455 */       return function.apply(t);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionFunctionBiFunction<T, R, E extends Exception> implements ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> {
/* 460 */     static final ExceptionBiFunction INSTANCE = new ExceptionFunctionBiFunction();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public R apply(ExceptionFunction<T, R, E> function, T t) throws E {
/* 466 */       return function.apply(t);
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConstantSupplier<T> implements Supplier<T>, ExceptionSupplier<T, RuntimeException> {
/* 471 */     static final ConstantSupplier NULL = new ConstantSupplier(null);
/*     */     
/*     */     private final T arg1;
/*     */     
/*     */     ConstantSupplier(T arg1) {
/* 476 */       this.arg1 = arg1;
/*     */     }
/*     */     
/*     */     public T get() {
/* 480 */       return this.arg1;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 484 */       return String.format("supplier(%s)", new Object[] { this.arg1 });
/*     */     }
/*     */   }
/*     */   
/*     */   static class BiConsumerRunnable<T, U> implements Runnable {
/*     */     private final BiConsumer<T, U> consumer;
/*     */     private final T param1;
/*     */     private final U param2;
/*     */     
/*     */     BiConsumerRunnable(BiConsumer<T, U> consumer, T param1, U param2) {
/* 494 */       this.consumer = consumer;
/* 495 */       this.param1 = param1;
/* 496 */       this.param2 = param2;
/*     */     }
/*     */     
/*     */     public void run() {
/* 500 */       this.consumer.accept(this.param1, this.param2);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 504 */       return String.format("%s(%s,%s)", new Object[] { this.consumer, this.param1, this.param2 });
/*     */     }
/*     */   }
/*     */   
/*     */   static class ConsumerRunnable<T> implements Runnable {
/*     */     private final Consumer<T> consumer;
/*     */     private final T param;
/*     */     
/*     */     ConsumerRunnable(Consumer<T> consumer, T param) {
/* 513 */       this.consumer = consumer;
/* 514 */       this.param = param;
/*     */     }
/*     */     
/*     */     public void run() {
/* 518 */       this.consumer.accept(this.param);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 522 */       return String.format("%s(%s)", new Object[] { this.consumer, this.param });
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionBiConsumerRunnable<T, U, E extends Exception> implements ExceptionRunnable<E> {
/*     */     private final ExceptionBiConsumer<T, U, E> consumer;
/*     */     private final T param1;
/*     */     private final U param2;
/*     */     
/*     */     ExceptionBiConsumerRunnable(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) {
/* 532 */       this.consumer = consumer;
/* 533 */       this.param1 = param1;
/* 534 */       this.param2 = param2;
/*     */     }
/*     */     
/*     */     public void run() throws E {
/* 538 */       this.consumer.accept(this.param1, this.param2);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 542 */       return String.format("%s(%s,%s)", new Object[] { this.consumer, this.param1, this.param2 });
/*     */     }
/*     */   }
/*     */   
/*     */   static class ExceptionConsumerRunnable<T, E extends Exception> implements ExceptionRunnable<E> {
/*     */     private final ExceptionConsumer<T, E> consumer;
/*     */     private final T param;
/*     */     
/*     */     ExceptionConsumerRunnable(ExceptionConsumer<T, E> consumer, T param) {
/* 551 */       this.consumer = consumer;
/* 552 */       this.param = param;
/*     */     }
/*     */     
/*     */     public void run() throws E {
/* 556 */       this.consumer.accept(this.param);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 560 */       return String.format("%s(%s)", new Object[] { this.consumer, this.param });
/*     */     }
/*     */   }
/*     */   
/*     */   static class DiscardingConsumer<T, E extends Exception> implements Consumer<T>, ExceptionConsumer<T, E> {
/* 565 */     static final DiscardingConsumer INSTANCE = new DiscardingConsumer();
/*     */ 
/*     */     
/*     */     public void accept(T t) {}
/*     */   }
/*     */ 
/*     */   
/*     */   static class DiscardingBiConsumer<T, U, E extends Exception>
/*     */     implements BiConsumer<T, U>, ExceptionBiConsumer<T, U, E>
/*     */   {
/* 575 */     static final DiscardingBiConsumer INSTANCE = new DiscardingBiConsumer();
/*     */     
/*     */     public void accept(T t, U u) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\Functions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */