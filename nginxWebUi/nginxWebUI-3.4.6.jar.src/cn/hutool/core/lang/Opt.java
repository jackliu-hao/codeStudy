/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.lang.func.VoidFunc0;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Stream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Opt<T>
/*     */ {
/*  54 */   private static final Opt<?> EMPTY = new Opt(null);
/*     */ 
/*     */   
/*     */   private final T value;
/*     */   
/*     */   private Exception exception;
/*     */ 
/*     */   
/*     */   public static <T> Opt<T> empty() {
/*  63 */     Opt<T> t = (Opt)EMPTY;
/*  64 */     return t;
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
/*     */   public static <T> Opt<T> of(T value) {
/*  76 */     return new Opt<>(Objects.requireNonNull(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Opt<T> ofNullable(T value) {
/*  87 */     return (value == null) ? empty() : new Opt<>(value);
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
/*     */   public static <T> Opt<T> ofBlankAble(T value) {
/*  99 */     return StrUtil.isBlankIfStr(value) ? empty() : new Opt<>(value);
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
/*     */   public static <T, R extends Collection<T>> Opt<R> ofEmptyAble(R value) {
/* 112 */     return CollectionUtil.isEmpty((Collection)value) ? empty() : new Opt<>(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Opt<T> ofTry(Func0<T> supplier) {
/*     */     try {
/* 122 */       return ofNullable((T)supplier.call());
/* 123 */     } catch (Exception e) {
/* 124 */       Opt<T> empty = new Opt<>(null);
/* 125 */       empty.exception = e;
/* 126 */       return empty;
/*     */     } 
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
/*     */   private Opt(T value) {
/* 142 */     this.value = value;
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
/*     */   public T get() {
/* 157 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 167 */     return (this.value == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Exception getException() {
/* 178 */     return this.exception;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFail() {
/* 189 */     return (null != this.exception);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPresent() {
/* 198 */     return (this.value != null);
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
/*     */   
/*     */   public Opt<T> ifPresent(Consumer<? super T> action) {
/* 214 */     if (isPresent()) {
/* 215 */       action.accept(this.value);
/*     */     }
/* 217 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Opt<T> ifPresentOrElse(Consumer<? super T> action, VoidFunc0 emptyAction) {
/* 236 */     if (isPresent()) {
/* 237 */       action.accept(this.value);
/*     */     } else {
/* 239 */       emptyAction.callWithRuntimeException();
/*     */     } 
/* 241 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <U> Opt<U> mapOrElse(Function<? super T, ? extends U> mapper, VoidFunc0 emptyAction) {
/* 262 */     if (isPresent()) {
/* 263 */       return ofNullable(mapper.apply(this.value));
/*     */     }
/* 265 */     emptyAction.callWithRuntimeException();
/* 266 */     return empty();
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
/*     */   public Opt<T> filter(Predicate<? super T> predicate) {
/* 280 */     Objects.requireNonNull(predicate);
/* 281 */     if (isEmpty()) {
/* 282 */       return this;
/*     */     }
/* 284 */     return predicate.test(this.value) ? this : empty();
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
/*     */   public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
/* 299 */     Objects.requireNonNull(mapper);
/* 300 */     if (isEmpty()) {
/* 301 */       return empty();
/*     */     }
/* 303 */     return ofNullable(mapper.apply(this.value));
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
/*     */   
/*     */   public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
/* 319 */     Objects.requireNonNull(mapper);
/* 320 */     if (isEmpty()) {
/* 321 */       return empty();
/*     */     }
/*     */     
/* 324 */     Opt<U> r = (Opt<U>)mapper.apply(this.value);
/* 325 */     return Objects.<Opt<U>>requireNonNull(r);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public <U> Opt<U> flattedMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
/* 343 */     Objects.requireNonNull(mapper);
/* 344 */     if (isEmpty()) {
/* 345 */       return empty();
/*     */     }
/* 347 */     return ofNullable(((Optional<U>)mapper.apply(this.value)).orElse(null));
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
/*     */   
/*     */   public Opt<T> peek(Consumer<T> action) throws NullPointerException {
/* 363 */     Objects.requireNonNull(action);
/* 364 */     if (isEmpty()) {
/* 365 */       return empty();
/*     */     }
/* 367 */     action.accept(this.value);
/* 368 */     return this;
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
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final Opt<T> peeks(Consumer<T>... actions) throws NullPointerException {
/* 387 */     return (Opt<T>)Stream.<Consumer<T>>of(actions).reduce(this, Opt::peek, (opts, opt) -> null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Opt<T> or(Supplier<? extends Opt<? extends T>> supplier) {
/* 398 */     Objects.requireNonNull(supplier);
/* 399 */     if (isPresent()) {
/* 400 */       return this;
/*     */     }
/* 402 */     Opt<T> r = (Opt<T>)supplier.get();
/* 403 */     return Objects.<Opt<T>>requireNonNull(r);
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
/*     */ 
/*     */   
/*     */   public Stream<T> stream() {
/* 420 */     if (isEmpty()) {
/* 421 */       return Stream.empty();
/*     */     }
/* 423 */     return Stream.of(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T orElse(T other) {
/* 434 */     return isPresent() ? this.value : other;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T exceptionOrElse(T other) {
/* 445 */     return isFail() ? other : this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T orElseGet(Supplier<? extends T> supplier) {
/* 456 */     return isPresent() ? this.value : supplier.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T orElseThrow() {
/* 466 */     return orElseThrow(java.util.NoSuchElementException::new, "No value present");
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
/*     */   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
/* 480 */     if (isPresent()) {
/* 481 */       return this.value;
/*     */     }
/* 483 */     throw (X)exceptionSupplier.get();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <X extends Throwable> T orElseThrow(Function<String, ? extends X> exceptionFunction, String message) throws X {
/* 504 */     if (isPresent()) {
/* 505 */       return this.value;
/*     */     }
/* 507 */     throw (X)exceptionFunction.apply(message);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<T> toOptional() {
/* 518 */     return Optional.ofNullable(this.value);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 537 */     if (this == obj) {
/* 538 */       return true;
/*     */     }
/*     */     
/* 541 */     if (!(obj instanceof Opt)) {
/* 542 */       return false;
/*     */     }
/*     */     
/* 545 */     Opt<?> other = (Opt)obj;
/* 546 */     return Objects.equals(this.value, other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 556 */     return Objects.hashCode(this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 566 */     return StrUtil.toStringOrNull(this.value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Opt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */