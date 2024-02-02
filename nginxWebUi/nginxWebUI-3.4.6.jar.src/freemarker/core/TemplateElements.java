/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.CollectionUtils;
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
/*    */ class TemplateElements
/*    */ {
/* 31 */   static final TemplateElements EMPTY = new TemplateElements(null, 0);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final TemplateElement[] buffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final int count;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   TemplateElements(TemplateElement[] buffer, int count) {
/* 50 */     this.buffer = buffer;
/* 51 */     this.count = count;
/*    */   }
/*    */   
/*    */   TemplateElement[] getBuffer() {
/* 55 */     return this.buffer;
/*    */   }
/*    */   
/*    */   int getCount() {
/* 59 */     return this.count;
/*    */   }
/*    */   
/*    */   TemplateElement getFirst() {
/* 63 */     return (this.buffer != null) ? this.buffer[0] : null;
/*    */   }
/*    */   
/*    */   TemplateElement getLast() {
/* 67 */     return (this.buffer != null) ? this.buffer[this.count - 1] : null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   TemplateElement asSingleElement() {
/* 74 */     if (this.count == 0) {
/* 75 */       return new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false);
/*    */     }
/* 77 */     TemplateElement first = this.buffer[0];
/* 78 */     if (this.count == 1) {
/* 79 */       return first;
/*    */     }
/* 81 */     MixedContent mixedContent = new MixedContent();
/* 82 */     mixedContent.setChildren(this);
/* 83 */     mixedContent.setLocation(first.getTemplate(), first, getLast());
/* 84 */     return mixedContent;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   MixedContent asMixedContent() {
/* 93 */     MixedContent mixedContent = new MixedContent();
/* 94 */     if (this.count != 0) {
/* 95 */       TemplateElement first = this.buffer[0];
/* 96 */       mixedContent.setChildren(this);
/* 97 */       mixedContent.setLocation(first.getTemplate(), first, getLast());
/*    */     } 
/* 99 */     return mixedContent;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateElements.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */