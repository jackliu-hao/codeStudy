/*    */ package freemarker.template;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ @Deprecated
/*    */ public class EmptyMap
/*    */   implements Map, Cloneable
/*    */ {
/* 36 */   public static final EmptyMap instance = new EmptyMap();
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean containsKey(Object arg0) {
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean containsValue(Object arg0) {
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Set entrySet() {
/* 55 */     return Collections.EMPTY_SET;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(Object arg0) {
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 65 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public Set keySet() {
/* 70 */     return Collections.EMPTY_SET;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object put(Object arg0, Object arg1) {
/* 75 */     throw new UnsupportedOperationException("This Map is read-only.");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void putAll(Map arg0) {
/* 82 */     if (arg0.entrySet().iterator().hasNext()) {
/* 83 */       throw new UnsupportedOperationException("This Map is read-only.");
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Object remove(Object arg0) {
/* 89 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 94 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection values() {
/* 99 */     return Collections.EMPTY_LIST;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\EmptyMap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */