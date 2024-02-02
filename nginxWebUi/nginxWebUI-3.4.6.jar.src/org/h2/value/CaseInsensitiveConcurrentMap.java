/*    */ package org.h2.value;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.h2.util.StringUtils;
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
/*    */ public class CaseInsensitiveConcurrentMap<V>
/*    */   extends ConcurrentHashMap<String, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public V get(Object paramObject) {
/* 23 */     return super.get(StringUtils.toUpperEnglish((String)paramObject));
/*    */   }
/*    */ 
/*    */   
/*    */   public V put(String paramString, V paramV) {
/* 28 */     return super.put(StringUtils.toUpperEnglish(paramString), paramV);
/*    */   }
/*    */ 
/*    */   
/*    */   public V putIfAbsent(String paramString, V paramV) {
/* 33 */     return super.putIfAbsent(StringUtils.toUpperEnglish(paramString), paramV);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean containsKey(Object paramObject) {
/* 38 */     return super.containsKey(StringUtils.toUpperEnglish((String)paramObject));
/*    */   }
/*    */ 
/*    */   
/*    */   public V remove(Object paramObject) {
/* 43 */     return super.remove(StringUtils.toUpperEnglish((String)paramObject));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\CaseInsensitiveConcurrentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */