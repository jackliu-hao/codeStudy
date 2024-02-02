/*    */ package freemarker.ext.dom;
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
/*    */ enum AtAtKey
/*    */ {
/* 26 */   MARKUP("@@markup"),
/* 27 */   NESTED_MARKUP("@@nested_markup"),
/* 28 */   ATTRIBUTES_MARKUP("@@attributes_markup"),
/* 29 */   TEXT("@@text"),
/* 30 */   START_TAG("@@start_tag"),
/* 31 */   END_TAG("@@end_tag"),
/* 32 */   QNAME("@@qname"),
/* 33 */   NAMESPACE("@@namespace"),
/* 34 */   LOCAL_NAME("@@local_name"),
/* 35 */   ATTRIBUTES("@@"),
/* 36 */   PREVIOUS_SIBLING_ELEMENT("@@previous_sibling_element"),
/* 37 */   NEXT_SIBLING_ELEMENT("@@next_sibling_element");
/*    */   
/*    */   private final String key;
/*    */   
/*    */   public String getKey() {
/* 42 */     return this.key;
/*    */   }
/*    */   
/*    */   AtAtKey(String key) {
/* 46 */     this.key = key;
/*    */   }
/*    */   
/*    */   public static boolean containsKey(String key) {
/* 50 */     for (AtAtKey item : values()) {
/* 51 */       if (item.getKey().equals(key)) {
/* 52 */         return true;
/*    */       }
/*    */     } 
/* 55 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\AtAtKey.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */