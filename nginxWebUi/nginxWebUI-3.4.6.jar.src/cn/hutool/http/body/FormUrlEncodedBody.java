/*    */ package cn.hutool.http.body;
/*    */ 
/*    */ import cn.hutool.core.net.url.UrlQuery;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class FormUrlEncodedBody
/*    */   extends BytesBody
/*    */ {
/*    */   public static FormUrlEncodedBody create(Map<String, Object> form, Charset charset) {
/* 25 */     return new FormUrlEncodedBody(form, charset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FormUrlEncodedBody(Map<String, Object> form, Charset charset) {
/* 35 */     super(StrUtil.bytes(UrlQuery.of(form, true).build(charset), charset));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\body\FormUrlEncodedBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */