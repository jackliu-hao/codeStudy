/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.SimpleNumber;
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import java.math.BigInteger;
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
/*    */ final class ListableRightUnboundedRangeModel
/*    */   extends RightUnboundedRangeModel
/*    */   implements TemplateCollectionModel
/*    */ {
/*    */   ListableRightUnboundedRangeModel(int begin) {
/* 38 */     super(begin);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 43 */     return Integer.MAX_VALUE;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModelIterator iterator() throws TemplateModelException {
/* 48 */     return new TemplateModelIterator() {
/*    */         boolean needInc;
/* 50 */         int nextType = 1;
/* 51 */         int nextInt = ListableRightUnboundedRangeModel.this.getBegining();
/*    */         
/*    */         long nextLong;
/*    */         BigInteger nextBigInteger;
/*    */         
/*    */         public TemplateModel next() throws TemplateModelException {
/* 57 */           if (this.needInc)
/* 58 */             switch (this.nextType) {
/*    */               case 1:
/* 60 */                 if (this.nextInt < Integer.MAX_VALUE) {
/* 61 */                   this.nextInt++; break;
/*    */                 } 
/* 63 */                 this.nextType = 2;
/* 64 */                 this.nextLong = this.nextInt + 1L;
/*    */                 break;
/*    */ 
/*    */               
/*    */               case 2:
/* 69 */                 if (this.nextLong < Long.MAX_VALUE) {
/* 70 */                   this.nextLong++; break;
/*    */                 } 
/* 72 */                 this.nextType = 3;
/* 73 */                 this.nextBigInteger = BigInteger.valueOf(this.nextLong);
/* 74 */                 this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
/*    */                 break;
/*    */ 
/*    */               
/*    */               default:
/* 79 */                 this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
/*    */                 break;
/*    */             }  
/* 82 */           this.needInc = true;
/* 83 */           return (this.nextType == 1) ? (TemplateModel)new SimpleNumber(this.nextInt) : ((this.nextType == 2) ? (TemplateModel)new SimpleNumber(this.nextLong) : (TemplateModel)new SimpleNumber(this.nextBigInteger));
/*    */         }
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         public boolean hasNext() throws TemplateModelException {
/* 90 */           return true;
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ListableRightUnboundedRangeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */