/*    */ package freemarker.template;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MapKeyValuePairIterator
/*    */   implements TemplateHashModelEx2.KeyValuePairIterator
/*    */ {
/*    */   private final Iterator<Map.Entry<?, ?>> entrySetIterator;
/*    */   private final ObjectWrapper objectWrapper;
/*    */   
/*    */   public <K, V> MapKeyValuePairIterator(Map<?, ?> map, ObjectWrapper objectWrapper) {
/* 42 */     this.entrySetIterator = map.entrySet().iterator();
/* 43 */     this.objectWrapper = objectWrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 48 */     return this.entrySetIterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateHashModelEx2.KeyValuePair next() {
/* 53 */     final Map.Entry<?, ?> entry = this.entrySetIterator.next();
/* 54 */     return new TemplateHashModelEx2.KeyValuePair()
/*    */       {
/*    */         public TemplateModel getKey() throws TemplateModelException
/*    */         {
/* 58 */           return MapKeyValuePairIterator.this.wrap(entry.getKey());
/*    */         }
/*    */ 
/*    */         
/*    */         public TemplateModel getValue() throws TemplateModelException {
/* 63 */           return MapKeyValuePairIterator.this.wrap(entry.getValue());
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */   
/*    */   private TemplateModel wrap(Object obj) throws TemplateModelException {
/* 70 */     return (obj instanceof TemplateModel) ? (TemplateModel)obj : this.objectWrapper.wrap(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\MapKeyValuePairIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */