/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.Map;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
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
/*    */ public class CaseInsensitiveTreeMap<K, V>
/*    */   extends CaseInsensitiveMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CaseInsensitiveTreeMap() {
/* 26 */     this((Comparator<? super K>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveTreeMap(Map<? extends K, ? extends V> m) {
/* 36 */     this();
/* 37 */     putAll(m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveTreeMap(SortedMap<? extends K, ? extends V> m) {
/* 47 */     super(new TreeMap<>(m));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveTreeMap(Comparator<? super K> comparator) {
/* 56 */     super(new TreeMap<>(comparator));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CaseInsensitiveTreeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */