/*    */ package cn.hutool.core.bean.copier;
/*    */ 
/*    */ import cn.hutool.core.lang.copier.Copier;
/*    */ import cn.hutool.core.util.ObjectUtil;
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
/*    */ public abstract class AbsCopier<S, T>
/*    */   implements Copier<T>
/*    */ {
/*    */   protected final S source;
/*    */   protected final T target;
/*    */   protected final CopyOptions copyOptions;
/*    */   
/*    */   public AbsCopier(S source, T target, CopyOptions copyOptions) {
/* 24 */     this.source = source;
/* 25 */     this.target = target;
/* 26 */     this.copyOptions = (CopyOptions)ObjectUtil.defaultIfNull(copyOptions, CopyOptions::create);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\AbsCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */