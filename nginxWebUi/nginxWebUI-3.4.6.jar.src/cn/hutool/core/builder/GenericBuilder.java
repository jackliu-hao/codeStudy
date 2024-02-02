/*     */ package cn.hutool.core.builder;
/*     */ 
/*     */ import cn.hutool.core.lang.func.Consumer3;
/*     */ import cn.hutool.core.lang.func.Supplier1;
/*     */ import cn.hutool.core.lang.func.Supplier2;
/*     */ import cn.hutool.core.lang.func.Supplier3;
/*     */ import cn.hutool.core.lang.func.Supplier4;
/*     */ import cn.hutool.core.lang.func.Supplier5;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
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
/*     */ public class GenericBuilder<T>
/*     */   implements Builder<T>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Supplier<T> instant;
/*  75 */   private final List<Consumer<T>> modifiers = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GenericBuilder(Supplier<T> instant) {
/*  83 */     this.instant = instant;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> GenericBuilder<T> of(Supplier<T> instant) {
/*  94 */     return new GenericBuilder<>(instant);
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
/*     */   public static <T, P1> GenericBuilder<T> of(Supplier1<T, P1> instant, P1 p1) {
/* 107 */     return of(instant.toSupplier(p1));
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
/*     */   public static <T, P1, P2> GenericBuilder<T> of(Supplier2<T, P1, P2> instant, P1 p1, P2 p2) {
/* 122 */     return of(instant.toSupplier(p1, p2));
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
/*     */   public static <T, P1, P2, P3> GenericBuilder<T> of(Supplier3<T, P1, P2, P3> instant, P1 p1, P2 p2, P3 p3) {
/* 139 */     return of(instant.toSupplier(p1, p2, p3));
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
/*     */   public static <T, P1, P2, P3, P4> GenericBuilder<T> of(Supplier4<T, P1, P2, P3, P4> instant, P1 p1, P2 p2, P3 p3, P4 p4) {
/* 158 */     return of(instant.toSupplier(p1, p2, p3, p4));
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
/*     */   public static <T, P1, P2, P3, P4, P5> GenericBuilder<T> of(Supplier5<T, P1, P2, P3, P4, P5> instant, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
/* 179 */     return of(instant.toSupplier(p1, p2, p3, p4, p5));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GenericBuilder<T> with(Consumer<T> consumer) {
/* 190 */     this.modifiers.add(consumer);
/* 191 */     return this;
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
/*     */   public <P1> GenericBuilder<T> with(BiConsumer<T, P1> consumer, P1 p1) {
/* 204 */     this.modifiers.add(instant -> consumer.accept(instant, p1));
/* 205 */     return this;
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
/*     */   public <P1, P2> GenericBuilder<T> with(Consumer3<T, P1, P2> consumer, P1 p1, P2 p2) {
/* 219 */     this.modifiers.add(instant -> consumer.accept(instant, p1, p2));
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T build() {
/* 230 */     T value = this.instant.get();
/* 231 */     this.modifiers.forEach(modifier -> modifier.accept(value));
/* 232 */     this.modifiers.clear();
/* 233 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\builder\GenericBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */