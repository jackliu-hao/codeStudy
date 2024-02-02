/*    */ package cn.hutool.core.io.watch;
/*    */ 
/*    */ import cn.hutool.core.exceptions.ExceptionUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WatchException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8068509879445395353L;
/*    */   
/*    */   public WatchException(Throwable e) {
/* 15 */     super(ExceptionUtil.getMessage(e), e);
/*    */   }
/*    */   
/*    */   public WatchException(String message) {
/* 19 */     super(message);
/*    */   }
/*    */   
/*    */   public WatchException(String messageTemplate, Object... params) {
/* 23 */     super(StrUtil.format(messageTemplate, params));
/*    */   }
/*    */   
/*    */   public WatchException(String message, Throwable throwable) {
/* 27 */     super(message, throwable);
/*    */   }
/*    */   
/*    */   public WatchException(Throwable throwable, String messageTemplate, Object... params) {
/* 31 */     super(StrUtil.format(messageTemplate, params), throwable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\watch\WatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */