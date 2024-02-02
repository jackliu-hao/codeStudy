package cn.hutool.core.lang;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Opt<T> {
   private static final Opt<?> EMPTY = new Opt((Object)null);
   private final T value;
   private Exception exception;

   public static <T> Opt<T> empty() {
      Opt<T> t = EMPTY;
      return t;
   }

   public static <T> Opt<T> of(T value) {
      return new Opt(Objects.requireNonNull(value));
   }

   public static <T> Opt<T> ofNullable(T value) {
      return value == null ? empty() : new Opt(value);
   }

   public static <T> Opt<T> ofBlankAble(T value) {
      return StrUtil.isBlankIfStr(value) ? empty() : new Opt(value);
   }

   public static <T, R extends Collection<T>> Opt<R> ofEmptyAble(R value) {
      return CollectionUtil.isEmpty(value) ? empty() : new Opt(value);
   }

   public static <T> Opt<T> ofTry(Func0<T> supplier) {
      try {
         return ofNullable(supplier.call());
      } catch (Exception var3) {
         Opt<T> empty = new Opt((Object)null);
         empty.exception = var3;
         return empty;
      }
   }

   private Opt(T value) {
      this.value = value;
   }

   public T get() {
      return this.value;
   }

   public boolean isEmpty() {
      return this.value == null;
   }

   public Exception getException() {
      return this.exception;
   }

   public boolean isFail() {
      return null != this.exception;
   }

   public boolean isPresent() {
      return this.value != null;
   }

   public Opt<T> ifPresent(Consumer<? super T> action) {
      if (this.isPresent()) {
         action.accept(this.value);
      }

      return this;
   }

   public Opt<T> ifPresentOrElse(Consumer<? super T> action, VoidFunc0 emptyAction) {
      if (this.isPresent()) {
         action.accept(this.value);
      } else {
         emptyAction.callWithRuntimeException();
      }

      return this;
   }

   public <U> Opt<U> mapOrElse(Function<? super T, ? extends U> mapper, VoidFunc0 emptyAction) {
      if (this.isPresent()) {
         return ofNullable(mapper.apply(this.value));
      } else {
         emptyAction.callWithRuntimeException();
         return empty();
      }
   }

   public Opt<T> filter(Predicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      if (this.isEmpty()) {
         return this;
      } else {
         return predicate.test(this.value) ? this : empty();
      }
   }

   public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return this.isEmpty() ? empty() : ofNullable(mapper.apply(this.value));
   }

   public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
      Objects.requireNonNull(mapper);
      if (this.isEmpty()) {
         return empty();
      } else {
         Opt<U> r = (Opt)mapper.apply(this.value);
         return (Opt)Objects.requireNonNull(r);
      }
   }

   public <U> Opt<U> flattedMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
      Objects.requireNonNull(mapper);
      return this.isEmpty() ? empty() : ofNullable(((Optional)mapper.apply(this.value)).orElse((Object)null));
   }

   public Opt<T> peek(Consumer<T> action) throws NullPointerException {
      Objects.requireNonNull(action);
      if (this.isEmpty()) {
         return empty();
      } else {
         action.accept(this.value);
         return this;
      }
   }

   @SafeVarargs
   public final Opt<T> peeks(Consumer<T>... actions) throws NullPointerException {
      return (Opt)Stream.of(actions).reduce(this, Opt::peek, (opts, opt) -> {
         return null;
      });
   }

   public Opt<T> or(Supplier<? extends Opt<? extends T>> supplier) {
      Objects.requireNonNull(supplier);
      if (this.isPresent()) {
         return this;
      } else {
         Opt<T> r = (Opt)supplier.get();
         return (Opt)Objects.requireNonNull(r);
      }
   }

   public Stream<T> stream() {
      return this.isEmpty() ? Stream.empty() : Stream.of(this.value);
   }

   public T orElse(T other) {
      return this.isPresent() ? this.value : other;
   }

   public T exceptionOrElse(T other) {
      return this.isFail() ? other : this.value;
   }

   public T orElseGet(Supplier<? extends T> supplier) {
      return this.isPresent() ? this.value : supplier.get();
   }

   public T orElseThrow() {
      return this.orElseThrow(NoSuchElementException::new, "No value present");
   }

   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
      if (this.isPresent()) {
         return this.value;
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public <X extends Throwable> T orElseThrow(Function<String, ? extends X> exceptionFunction, String message) throws X {
      if (this.isPresent()) {
         return this.value;
      } else {
         throw (Throwable)exceptionFunction.apply(message);
      }
   }

   public Optional<T> toOptional() {
      return Optional.ofNullable(this.value);
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Opt)) {
         return false;
      } else {
         Opt<?> other = (Opt)obj;
         return Objects.equals(this.value, other.value);
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.value);
   }

   public String toString() {
      return StrUtil.toStringOrNull(this.value);
   }
}
