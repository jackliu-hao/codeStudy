/*    */ package cn.hutool.extra.pinyin;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PinyinException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public PinyinException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public PinyinException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public PinyinException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public PinyinException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public PinyinException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
/* 31 */     super(message, throwable, enableSuppression, writableStackTrace);
/*    */   }
/*    */   
/*    */   public PinyinException(Throwable throwable, String messageTemplate, Object... params) {
/* 35 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\PinyinException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */