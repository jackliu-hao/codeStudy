/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.DefaultObjectWrapper;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateException;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template._TemplateAPI;
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
/*    */ class APINotSupportedTemplateException
/*    */   extends TemplateException
/*    */ {
/*    */   APINotSupportedTemplateException(Environment env, Expression blamedExpr, TemplateModel model) {
/* 36 */     super(null, env, blamedExpr, buildDescription(env, blamedExpr, model));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected static _ErrorDescriptionBuilder buildDescription(Environment env, Expression blamedExpr, TemplateModel tm) {
/* 46 */     _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "The value doesn't support ?api. See requirements in the FreeMarker Manual. (FTL type: ", new _DelayedFTLTypeDescription(tm), ", TemplateModel class: ", new _DelayedShortClassName(tm.getClass()), ", ObjectWapper: ", new _DelayedToString(env.getObjectWrapper()), ")" })).blame(blamedExpr);
/*    */     
/* 48 */     if (blamedExpr.isLiteral()) {
/* 49 */       desc.tip("Only adapted Java objects can possibly have API, not values created inside templates.");
/*    */     } else {
/* 51 */       ObjectWrapper ow = env.getObjectWrapper();
/* 52 */       if (ow instanceof DefaultObjectWrapper && (tm instanceof freemarker.template.SimpleHash || tm instanceof freemarker.template.SimpleSequence)) {
/*    */         
/* 54 */         DefaultObjectWrapper dow = (DefaultObjectWrapper)ow;
/* 55 */         if (!dow.getUseAdaptersForContainers()) {
/* 56 */           desc.tip(new Object[] { "In the FreeMarker configuration, \"", "object_wrapper", "\" is a DefaultObjectWrapper with its \"useAdaptersForContainers\" property set to false. Setting it to true might solves this problem." });
/*    */ 
/*    */           
/* 59 */           if (dow.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_22) {
/* 60 */             desc.tip("Setting DefaultObjectWrapper's \"incompatibleImprovements\" to 2.3.22 or higher will change the default value of \"useAdaptersForContainers\" to true.");
/*    */           }
/*    */         }
/* 63 */         else if (tm instanceof freemarker.template.SimpleSequence && dow.getForceLegacyNonListCollections()) {
/* 64 */           desc.tip(new Object[] { "In the FreeMarker configuration, \"", "object_wrapper", "\" is a DefaultObjectWrapper with its \"forceLegacyNonListCollections\" property set to true. If you are trying to access the API of a non-List Collection, setting the \"forceLegacyNonListCollections\" property to false might solves this problem." });
/*    */         } 
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 73 */     return desc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\APINotSupportedTemplateException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */