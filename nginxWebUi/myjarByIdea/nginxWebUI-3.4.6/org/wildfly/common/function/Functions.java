package org.wildfly.common.function;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.wildfly.common.Assert;

public final class Functions {
   private Functions() {
   }

   public static Consumer<Runnable> runnableConsumer() {
      return Functions.RunnableConsumer.INSTANCE;
   }

   public static <E extends Exception> ExceptionConsumer<ExceptionRunnable<E>, E> exceptionRunnableConsumer() {
      return Functions.ExceptionRunnableConsumer.INSTANCE;
   }

   public static ExceptionConsumer<Runnable, RuntimeException> runnableExceptionConsumer() {
      return Functions.RunnableExceptionConsumer.INSTANCE;
   }

   public static <T> BiConsumer<Consumer<T>, T> consumerBiConsumer() {
      return Functions.ConsumerBiConsumer.INSTANCE;
   }

   public static <T, E extends Exception> ExceptionBiConsumer<ExceptionConsumer<T, E>, T, E> exceptionConsumerBiConsumer() {
      return Functions.ExceptionConsumerBiConsumer.INSTANCE;
   }

   public static <T> ExceptionBiConsumer<Consumer<T>, T, RuntimeException> consumerExceptionBiConsumer() {
      return Functions.ConsumerExceptionBiConsumer.INSTANCE;
   }

   public static <R> Function<Supplier<R>, R> supplierFunction() {
      return Functions.SupplierFunction.INSTANCE;
   }

   public static <R, E extends Exception> ExceptionFunction<ExceptionSupplier<R, E>, R, E> exceptionSupplierFunction() {
      return Functions.ExceptionSupplierFunction.INSTANCE;
   }

   public static <R> ExceptionFunction<Supplier<R>, R, RuntimeException> supplierExceptionFunction() {
      return Functions.SupplierExceptionFunction.INSTANCE;
   }

   public static <R> BiFunction<Function<Supplier<R>, R>, Supplier<R>, R> supplierFunctionBiFunction() {
      return Functions.FunctionSupplierBiFunction.INSTANCE;
   }

   public static <R, E extends Exception> ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<R, E>, R, E>, ExceptionSupplier<R, E>, R, E> exceptionSupplierFunctionBiFunction() {
      return Functions.ExceptionFunctionSupplierBiFunction.INSTANCE;
   }

   public static <T, R> BiFunction<Function<T, R>, T, R> functionBiFunction() {
      return Functions.FunctionBiFunction.INSTANCE;
   }

   public static <T, R, E extends Exception> ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> exceptionFunctionBiFunction() {
      return Functions.ExceptionFunctionBiFunction.INSTANCE;
   }

   public static <T, R> ExceptionBiFunction<Function<T, R>, T, R, RuntimeException> functionExceptionBiFunction() {
      return Functions.FunctionExceptionBiFunction.INSTANCE;
   }

   public static <T> Supplier<T> constantSupplier(T value) {
      return value == null ? Functions.ConstantSupplier.NULL : new ConstantSupplier(value);
   }

   public static <T, E extends Exception> ExceptionSupplier<T, E> constantExceptionSupplier(T value) {
      return value == null ? Functions.ConstantSupplier.NULL : new ConstantSupplier(value);
   }

   public static <T, U> Runnable capturingRunnable(BiConsumer<T, U> consumer, T param1, U param2) {
      Assert.checkNotNullParam("consumer", consumer);
      return new BiConsumerRunnable(consumer, param1, param2);
   }

   public static <T> Runnable capturingRunnable(Consumer<T> consumer, T param) {
      Assert.checkNotNullParam("consumer", consumer);
      return new ConsumerRunnable(consumer, param);
   }

   public static <T, U, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) {
      Assert.checkNotNullParam("consumer", consumer);
      return new ExceptionBiConsumerRunnable(consumer, param1, param2);
   }

   public static <T, E extends Exception> ExceptionRunnable<E> exceptionCapturingRunnable(ExceptionConsumer<T, E> consumer, T param) {
      Assert.checkNotNullParam("consumer", consumer);
      return new ExceptionConsumerRunnable(consumer, param);
   }

   public static <T> Consumer<T> discardingConsumer() {
      return Functions.DiscardingConsumer.INSTANCE;
   }

   public static <T, E extends Exception> ExceptionConsumer<T, E> discardingExceptionConsumer() {
      return Functions.DiscardingConsumer.INSTANCE;
   }

   public static <T, U> BiConsumer<T, U> discardingBiConsumer() {
      return Functions.DiscardingBiConsumer.INSTANCE;
   }

   public static <T, U, E extends Exception> ExceptionBiConsumer<T, U, E> discardingExceptionBiConsumer() {
      return Functions.DiscardingBiConsumer.INSTANCE;
   }

   static class DiscardingBiConsumer<T, U, E extends Exception> implements BiConsumer<T, U>, ExceptionBiConsumer<T, U, E> {
      static final DiscardingBiConsumer INSTANCE = new DiscardingBiConsumer();

      private DiscardingBiConsumer() {
      }

      public void accept(T t, U u) {
      }
   }

   static class DiscardingConsumer<T, E extends Exception> implements Consumer<T>, ExceptionConsumer<T, E> {
      static final DiscardingConsumer INSTANCE = new DiscardingConsumer();

      private DiscardingConsumer() {
      }

      public void accept(T t) {
      }
   }

   static class ExceptionConsumerRunnable<T, E extends Exception> implements ExceptionRunnable<E> {
      private final ExceptionConsumer<T, E> consumer;
      private final T param;

      ExceptionConsumerRunnable(ExceptionConsumer<T, E> consumer, T param) {
         this.consumer = consumer;
         this.param = param;
      }

      public void run() throws E {
         this.consumer.accept(this.param);
      }

      public String toString() {
         return String.format("%s(%s)", this.consumer, this.param);
      }
   }

   static class ExceptionBiConsumerRunnable<T, U, E extends Exception> implements ExceptionRunnable<E> {
      private final ExceptionBiConsumer<T, U, E> consumer;
      private final T param1;
      private final U param2;

      ExceptionBiConsumerRunnable(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) {
         this.consumer = consumer;
         this.param1 = param1;
         this.param2 = param2;
      }

      public void run() throws E {
         this.consumer.accept(this.param1, this.param2);
      }

      public String toString() {
         return String.format("%s(%s,%s)", this.consumer, this.param1, this.param2);
      }
   }

   static class ConsumerRunnable<T> implements Runnable {
      private final Consumer<T> consumer;
      private final T param;

      ConsumerRunnable(Consumer<T> consumer, T param) {
         this.consumer = consumer;
         this.param = param;
      }

      public void run() {
         this.consumer.accept(this.param);
      }

      public String toString() {
         return String.format("%s(%s)", this.consumer, this.param);
      }
   }

   static class BiConsumerRunnable<T, U> implements Runnable {
      private final BiConsumer<T, U> consumer;
      private final T param1;
      private final U param2;

      BiConsumerRunnable(BiConsumer<T, U> consumer, T param1, U param2) {
         this.consumer = consumer;
         this.param1 = param1;
         this.param2 = param2;
      }

      public void run() {
         this.consumer.accept(this.param1, this.param2);
      }

      public String toString() {
         return String.format("%s(%s,%s)", this.consumer, this.param1, this.param2);
      }
   }

   static class ConstantSupplier<T> implements Supplier<T>, ExceptionSupplier<T, RuntimeException> {
      static final ConstantSupplier NULL = new ConstantSupplier((Object)null);
      private final T arg1;

      ConstantSupplier(T arg1) {
         this.arg1 = arg1;
      }

      public T get() {
         return this.arg1;
      }

      public String toString() {
         return String.format("supplier(%s)", this.arg1);
      }
   }

   static class ExceptionFunctionBiFunction<T, R, E extends Exception> implements ExceptionBiFunction<ExceptionFunction<T, R, E>, T, R, E> {
      static final ExceptionBiFunction INSTANCE = new ExceptionFunctionBiFunction();

      private ExceptionFunctionBiFunction() {
      }

      public R apply(ExceptionFunction<T, R, E> function, T t) throws E {
         return function.apply(t);
      }
   }

   static class FunctionBiFunction<T, R> implements BiFunction<Function<T, R>, T, R> {
      static final BiFunction INSTANCE = new FunctionBiFunction();

      private FunctionBiFunction() {
      }

      public R apply(Function<T, R> function, T t) {
         return function.apply(t);
      }
   }

   static class FunctionExceptionBiFunction<T, R> implements ExceptionBiFunction<Function<T, R>, T, R, RuntimeException> {
      static final FunctionExceptionBiFunction INSTANCE = new FunctionExceptionBiFunction();

      private FunctionExceptionBiFunction() {
      }

      public R apply(Function<T, R> function, T t) throws RuntimeException {
         return function.apply(t);
      }
   }

   static class ExceptionFunctionSupplierBiFunction<E extends Exception> implements ExceptionBiFunction<ExceptionFunction<ExceptionSupplier<Object, E>, Object, E>, ExceptionSupplier<Object, E>, Object, E> {
      static final ExceptionBiFunction INSTANCE = new ExceptionFunctionSupplierBiFunction();

      private ExceptionFunctionSupplierBiFunction() {
      }

      public Object apply(ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> function, ExceptionSupplier<Object, E> supplier) throws E {
         return function.apply(supplier);
      }
   }

   static class FunctionSupplierBiFunction implements BiFunction<Function<Supplier<Object>, Object>, Supplier<Object>, Object> {
      static final BiFunction INSTANCE = new FunctionSupplierBiFunction();

      private FunctionSupplierBiFunction() {
      }

      public Object apply(Function<Supplier<Object>, Object> function, Supplier<Object> supplier) {
         return function.apply(supplier);
      }
   }

   static class SupplierExceptionFunction<R> implements ExceptionFunction<Supplier<R>, R, RuntimeException> {
      static final SupplierExceptionFunction INSTANCE = new SupplierExceptionFunction();

      private SupplierExceptionFunction() {
      }

      public R apply(Supplier<R> supplier) throws RuntimeException {
         return supplier.get();
      }
   }

   static class ExceptionSupplierFunction<E extends Exception> implements ExceptionFunction<ExceptionSupplier<Object, E>, Object, E> {
      static final ExceptionFunction INSTANCE = new ExceptionSupplierFunction();

      private ExceptionSupplierFunction() {
      }

      public Object apply(ExceptionSupplier<Object, E> supplier) throws E {
         return supplier.get();
      }
   }

   static class SupplierFunction implements Function<Supplier<Object>, Object> {
      static final Function INSTANCE = new SupplierFunction();

      private SupplierFunction() {
      }

      public Object apply(Supplier<Object> supplier) {
         return supplier.get();
      }
   }

   static class ConsumerExceptionBiConsumer<T> implements ExceptionBiConsumer<Consumer<T>, T, RuntimeException> {
      static final ExceptionBiConsumer INSTANCE = new ConsumerExceptionBiConsumer();

      private ConsumerExceptionBiConsumer() {
      }

      public void accept(Consumer<T> consumer, T t) throws RuntimeException {
         consumer.accept(t);
      }
   }

   static class ExceptionConsumerBiConsumer<E extends Exception> implements ExceptionBiConsumer<ExceptionConsumer<Object, E>, Object, E> {
      static final ExceptionBiConsumer INSTANCE = new ExceptionConsumerBiConsumer();

      private ExceptionConsumerBiConsumer() {
      }

      public void accept(ExceptionConsumer<Object, E> consumer, Object o) throws E {
         consumer.accept(o);
      }
   }

   static class ConsumerBiConsumer implements BiConsumer<Consumer<Object>, Object> {
      static final BiConsumer INSTANCE = new ConsumerBiConsumer();

      private ConsumerBiConsumer() {
      }

      public void accept(Consumer<Object> consumer, Object o) {
         consumer.accept(o);
      }
   }

   static class RunnableExceptionConsumer implements ExceptionConsumer<Runnable, RuntimeException> {
      static final RunnableExceptionConsumer INSTANCE = new RunnableExceptionConsumer();

      private RunnableExceptionConsumer() {
      }

      public void accept(Runnable runnable) throws RuntimeException {
         runnable.run();
      }
   }

   static class ExceptionRunnableConsumer<E extends Exception> implements ExceptionConsumer<ExceptionRunnable<E>, E> {
      static final ExceptionConsumer INSTANCE = new ExceptionRunnableConsumer();

      private ExceptionRunnableConsumer() {
      }

      public void accept(ExceptionRunnable<E> ExceptionRunnable) throws E {
         ExceptionRunnable.run();
      }
   }

   static class RunnableConsumer implements Consumer<Runnable> {
      static final Consumer<Runnable> INSTANCE = new RunnableConsumer();

      private RunnableConsumer() {
      }

      public void accept(Runnable runnable) {
         runnable.run();
      }
   }
}
