/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.Serializable;
/*    */ import java.net.URL;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class BytesResource
/*    */   implements Resource, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final byte[] bytes;
/*    */   private final String name;
/*    */   
/*    */   public BytesResource(byte[] bytes) {
/* 31 */     this(bytes, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BytesResource(byte[] bytes, String name) {
/* 41 */     this.bytes = bytes;
/* 42 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 47 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public URL getUrl() {
/* 52 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getStream() {
/* 57 */     return new ByteArrayInputStream(this.bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public String readStr(Charset charset) throws IORuntimeException {
/* 62 */     return StrUtil.str(this.bytes, charset);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] readBytes() throws IORuntimeException {
/* 67 */     return this.bytes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\BytesResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */