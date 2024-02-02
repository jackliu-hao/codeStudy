/*     */ package cn.hutool.core.stream;
/*     */ 
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BinaryOperator;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collector;
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
/*     */ public class SimpleCollector<T, A, R>
/*     */   implements Collector<T, A, R>
/*     */ {
/*     */   private final Supplier<A> supplier;
/*     */   private final BiConsumer<A, T> accumulator;
/*     */   private final BinaryOperator<A> combiner;
/*     */   private final Function<A, R> finisher;
/*     */   private final Set<Collector.Characteristics> characteristics;
/*     */   
/*     */   public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Collector.Characteristics> characteristics) {
/*  61 */     this.supplier = supplier;
/*  62 */     this.accumulator = accumulator;
/*  63 */     this.combiner = combiner;
/*  64 */     this.finisher = finisher;
/*  65 */     this.characteristics = characteristics;
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
/*     */   public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Collector.Characteristics> characteristics) {
/*  81 */     this(supplier, accumulator, combiner, i -> i, characteristics);
/*     */   }
/*     */ 
/*     */   
/*     */   public BiConsumer<A, T> accumulator() {
/*  86 */     return this.accumulator;
/*     */   }
/*     */ 
/*     */   
/*     */   public Supplier<A> supplier() {
/*  91 */     return this.supplier;
/*     */   }
/*     */ 
/*     */   
/*     */   public BinaryOperator<A> combiner() {
/*  96 */     return this.combiner;
/*     */   }
/*     */ 
/*     */   
/*     */   public Function<A, R> finisher() {
/* 101 */     return this.finisher;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Collector.Characteristics> characteristics() {
/* 106 */     return this.characteristics;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\stream\SimpleCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */