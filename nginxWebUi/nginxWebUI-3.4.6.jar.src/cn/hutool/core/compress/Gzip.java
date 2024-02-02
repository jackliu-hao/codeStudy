/*    */ package cn.hutool.core.compress;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.zip.GZIPInputStream;
/*    */ import java.util.zip.GZIPOutputStream;
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
/*    */ public class Gzip
/*    */   implements Closeable
/*    */ {
/*    */   private InputStream source;
/*    */   private OutputStream target;
/*    */   
/*    */   public static Gzip of(InputStream source, OutputStream target) {
/* 33 */     return new Gzip(source, target);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Gzip(InputStream source, OutputStream target) {
/* 43 */     this.source = source;
/* 44 */     this.target = target;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OutputStream getTarget() {
/* 53 */     return this.target;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Gzip gzip() {
/*    */     try {
/* 63 */       this.target = (this.target instanceof GZIPOutputStream) ? this.target : new GZIPOutputStream(this.target);
/*    */       
/* 65 */       IoUtil.copy(this.source, this.target);
/* 66 */       ((GZIPOutputStream)this.target).finish();
/* 67 */     } catch (IOException e) {
/* 68 */       throw new IORuntimeException(e);
/*    */     } 
/* 70 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Gzip unGzip() {
/*    */     try {
/* 80 */       this.source = (this.source instanceof GZIPInputStream) ? this.source : new GZIPInputStream(this.source);
/*    */       
/* 82 */       IoUtil.copy(this.source, this.target);
/* 83 */     } catch (IOException e) {
/* 84 */       throw new IORuntimeException(e);
/*    */     } 
/* 86 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 91 */     IoUtil.close(this.target);
/* 92 */     IoUtil.close(this.source);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compress\Gzip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */