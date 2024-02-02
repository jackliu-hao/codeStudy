/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import cn.hutool.core.codec.PercentCodec;
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
/*    */ public class FormUrlencoded
/*    */ {
/* 17 */   public static final PercentCodec ALL = PercentCodec.of(RFC3986.UNRESERVED)
/* 18 */     .removeSafe('~').addSafe('*').setEncodeSpaceAsPlus(true);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\FormUrlencoded.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */