/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class TolerantMap<K, V>
/*     */   extends MapWrapper<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = -4158133823263496197L;
/*     */   private final V defaultValue;
/*     */   
/*     */   public TolerantMap(V defaultValue) {
/*  25 */     this(new HashMap<>(), defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TolerantMap(int initialCapacity, float loadFactor, V defaultValue) {
/*  36 */     this(new HashMap<>(initialCapacity, loadFactor), defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TolerantMap(int initialCapacity, V defaultValue) {
/*  46 */     this(new HashMap<>(initialCapacity), defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TolerantMap(Map<K, V> map, V defaultValue) {
/*  56 */     super(map);
/*  57 */     this.defaultValue = defaultValue;
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
/*     */   public static <K, V> TolerantMap<K, V> of(Map<K, V> map, V defaultValue) {
/*  70 */     return new TolerantMap<>(map, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  75 */     return getOrDefault(key, this.defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  80 */     if (this == o) {
/*  81 */       return true;
/*     */     }
/*  83 */     if (o == null || getClass() != o.getClass()) {
/*  84 */       return false;
/*     */     }
/*  86 */     if (false == super.equals(o)) {
/*  87 */       return false;
/*     */     }
/*  89 */     TolerantMap<?, ?> that = (TolerantMap<?, ?>)o;
/*  90 */     return (getRaw().equals(that.getRaw()) && 
/*  91 */       Objects.equals(this.defaultValue, that.defaultValue));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  96 */     return Objects.hash(new Object[] { getRaw(), this.defaultValue });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 101 */     return "TolerantMap{map=" + getRaw() + ", defaultValue=" + this.defaultValue + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\TolerantMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */