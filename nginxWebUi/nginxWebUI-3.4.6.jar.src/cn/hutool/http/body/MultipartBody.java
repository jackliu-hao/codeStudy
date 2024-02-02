/*    */ package cn.hutool.http.body;
/*    */ 
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.http.ContentType;
/*    */ import cn.hutool.http.HttpGlobalConfig;
/*    */ import cn.hutool.http.MultipartOutputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.OutputStream;
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
/*    */ public class MultipartBody
/*    */   implements RequestBody
/*    */ {
/* 23 */   private static final String CONTENT_TYPE_MULTIPART_PREFIX = ContentType.MULTIPART.getValue() + "; boundary=";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final Map<String, Object> form;
/*    */ 
/*    */ 
/*    */   
/*    */   private final Charset charset;
/*    */ 
/*    */ 
/*    */   
/* 36 */   private final String boundary = HttpGlobalConfig.getBoundary();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static MultipartBody create(Map<String, Object> form, Charset charset) {
/* 46 */     return new MultipartBody(form, charset);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getContentType() {
/* 55 */     return CONTENT_TYPE_MULTIPART_PREFIX + this.boundary;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MultipartBody(Map<String, Object> form, Charset charset) {
/* 65 */     this.form = form;
/* 66 */     this.charset = charset;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(OutputStream out) {
/* 76 */     MultipartOutputStream stream = new MultipartOutputStream(out, this.charset, this.boundary);
/* 77 */     if (MapUtil.isNotEmpty(this.form)) {
/* 78 */       this.form.forEach(stream::write);
/*    */     }
/* 80 */     stream.finish();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 86 */     write(out);
/* 87 */     return IoUtil.toStr(out, this.charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\body\MultipartBody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */