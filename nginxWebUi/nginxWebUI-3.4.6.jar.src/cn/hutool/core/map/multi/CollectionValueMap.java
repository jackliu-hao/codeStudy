/*     */ package cn.hutool.core.map.multi;
/*     */ 
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class CollectionValueMap<K, V>
/*     */   extends AbsCollValueMap<K, V, Collection<V>>
/*     */ {
/*     */   private static final long serialVersionUID = 9012989578038102983L;
/*     */   private final Func0<Collection<V>> collectionCreateFunc;
/*     */   
/*     */   public CollectionValueMap() {
/*  30 */     this(16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollectionValueMap(int initialCapacity) {
/*  39 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollectionValueMap(Map<? extends K, ? extends Collection<V>> m) {
/*  48 */     this(0.75F, m);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
/*  58 */     this(loadFactor, m, java.util.ArrayList::new);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollectionValueMap(int initialCapacity, float loadFactor) {
/*  68 */     this(initialCapacity, loadFactor, java.util.ArrayList::new);
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
/*     */   public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m, Func0<Collection<V>> collectionCreateFunc) {
/*  80 */     this(m.size(), loadFactor, collectionCreateFunc);
/*  81 */     putAll(m);
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
/*     */   public CollectionValueMap(int initialCapacity, float loadFactor, Func0<Collection<V>> collectionCreateFunc) {
/*  93 */     super(new HashMap<>(initialCapacity, loadFactor));
/*  94 */     this.collectionCreateFunc = collectionCreateFunc;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Collection<V> createCollection() {
/* 100 */     return (Collection<V>)this.collectionCreateFunc.callWithRuntimeException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\CollectionValueMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */