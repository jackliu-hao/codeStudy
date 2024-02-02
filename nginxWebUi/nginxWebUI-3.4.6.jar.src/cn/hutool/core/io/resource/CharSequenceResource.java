/*    */ package cn.hutool.core.io.resource;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.StringReader;
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
/*    */ public class CharSequenceResource
/*    */   implements Resource, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final CharSequence data;
/*    */   private final CharSequence name;
/*    */   private final Charset charset;
/*    */   
/*    */   public CharSequenceResource(CharSequence data) {
/* 35 */     this(data, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CharSequenceResource(CharSequence data, String name) {
/* 45 */     this(data, name, CharsetUtil.CHARSET_UTF_8);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CharSequenceResource(CharSequence data, CharSequence name, Charset charset) {
/* 56 */     this.data = data;
/* 57 */     this.name = name;
/* 58 */     this.charset = charset;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 63 */     return StrUtil.str(this.name);
/*    */   }
/*    */ 
/*    */   
/*    */   public URL getUrl() {
/* 68 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputStream getStream() {
/* 73 */     return new ByteArrayInputStream(readBytes());
/*    */   }
/*    */ 
/*    */   
/*    */   public BufferedReader getReader(Charset charset) {
/* 78 */     return IoUtil.getReader(new StringReader(this.data.toString()));
/*    */   }
/*    */ 
/*    */   
/*    */   public String readStr(Charset charset) throws IORuntimeException {
/* 83 */     return this.data.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] readBytes() throws IORuntimeException {
/* 88 */     return this.data.toString().getBytes(this.charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\resource\CharSequenceResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */