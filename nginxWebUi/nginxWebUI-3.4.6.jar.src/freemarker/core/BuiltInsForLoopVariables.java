/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.util.List;
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
/*     */ class BuiltInsForLoopVariables
/*     */ {
/*     */   static class indexBI
/*     */     extends BuiltInForLoopVariable
/*     */   {
/*     */     TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/*  39 */       return (TemplateModel)new SimpleNumber(iterCtx.getIndex());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class counterBI
/*     */     extends BuiltInForLoopVariable
/*     */   {
/*     */     TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/*  48 */       return (TemplateModel)new SimpleNumber(iterCtx.getIndex() + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static abstract class BooleanBuiltInForLoopVariable
/*     */     extends BuiltInForLoopVariable
/*     */   {
/*     */     final TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/*  57 */       return calculateBooleanResult(iterCtx, env) ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract boolean calculateBooleanResult(IteratorBlock.IterationContext param1IterationContext, Environment param1Environment);
/*     */   }
/*     */   
/*     */   static class has_nextBI
/*     */     extends BooleanBuiltInForLoopVariable
/*     */   {
/*     */     protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
/*  68 */       return iterCtx.hasNext();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class is_lastBI
/*     */     extends BooleanBuiltInForLoopVariable
/*     */   {
/*     */     protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
/*  77 */       return !iterCtx.hasNext();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class is_firstBI
/*     */     extends BooleanBuiltInForLoopVariable
/*     */   {
/*     */     protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
/*  86 */       return (iterCtx.getIndex() == 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class is_odd_itemBI
/*     */     extends BooleanBuiltInForLoopVariable
/*     */   {
/*     */     protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
/*  95 */       return (iterCtx.getIndex() % 2 == 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class is_even_itemBI
/*     */     extends BooleanBuiltInForLoopVariable
/*     */   {
/*     */     protected boolean calculateBooleanResult(IteratorBlock.IterationContext iterCtx, Environment env) {
/* 104 */       return (iterCtx.getIndex() % 2 != 0);
/*     */     }
/*     */   }
/*     */   
/*     */   static class item_parityBI
/*     */     extends BuiltInForLoopVariable
/*     */   {
/* 111 */     private static final SimpleScalar ODD = new SimpleScalar("odd");
/* 112 */     private static final SimpleScalar EVEN = new SimpleScalar("even");
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/* 116 */       return (iterCtx.getIndex() % 2 == 0) ? (TemplateModel)ODD : (TemplateModel)EVEN;
/*     */     }
/*     */   }
/*     */   
/*     */   static class item_parity_capBI
/*     */     extends BuiltInForLoopVariable
/*     */   {
/* 123 */     private static final SimpleScalar ODD = new SimpleScalar("Odd");
/* 124 */     private static final SimpleScalar EVEN = new SimpleScalar("Even");
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/* 128 */       return (iterCtx.getIndex() % 2 == 0) ? (TemplateModel)ODD : (TemplateModel)EVEN;
/*     */     }
/*     */   }
/*     */   
/*     */   static class item_cycleBI
/*     */     extends BuiltInForLoopVariable
/*     */   {
/*     */     private class BIMethod
/*     */       implements TemplateMethodModelEx {
/*     */       private final IteratorBlock.IterationContext iterCtx;
/*     */       
/*     */       private BIMethod(IteratorBlock.IterationContext iterCtx) {
/* 140 */         this.iterCtx = iterCtx;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List args) throws TemplateModelException {
/* 145 */         BuiltInsForLoopVariables.item_cycleBI.this.checkMethodArgCount(args, 1, 2147483647);
/* 146 */         return args.get(this.iterCtx.getIndex() % args.size());
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     TemplateModel calculateResult(IteratorBlock.IterationContext iterCtx, Environment env) throws TemplateException {
/* 152 */       return (TemplateModel)new BIMethod(iterCtx);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForLoopVariables.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */