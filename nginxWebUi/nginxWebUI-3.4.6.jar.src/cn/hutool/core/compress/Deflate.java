/*     */ package cn.hutool.core.compress;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Deflate
/*     */   implements Closeable
/*     */ {
/*     */   private final InputStream source;
/*     */   private OutputStream target;
/*     */   private final boolean nowrap;
/*     */   
/*     */   public static Deflate of(InputStream source, OutputStream target, boolean nowrap) {
/*  37 */     return new Deflate(source, target, nowrap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Deflate(InputStream source, OutputStream target, boolean nowrap) {
/*  48 */     this.source = source;
/*  49 */     this.target = target;
/*  50 */     this.nowrap = nowrap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getTarget() {
/*  59 */     return this.target;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Deflate deflater(int level) {
/*  69 */     this.target = (this.target instanceof DeflaterOutputStream) ? this.target : new DeflaterOutputStream(this.target, new Deflater(level, this.nowrap));
/*     */     
/*  71 */     IoUtil.copy(this.source, this.target);
/*     */     try {
/*  73 */       ((DeflaterOutputStream)this.target).finish();
/*  74 */     } catch (IOException e) {
/*  75 */       throw new IORuntimeException(e);
/*     */     } 
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Deflate inflater() {
/*  86 */     this.target = (this.target instanceof InflaterOutputStream) ? this.target : new InflaterOutputStream(this.target, new Inflater(this.nowrap));
/*     */     
/*  88 */     IoUtil.copy(this.source, this.target);
/*     */     try {
/*  90 */       ((InflaterOutputStream)this.target).finish();
/*  91 */     } catch (IOException e) {
/*  92 */       throw new IORuntimeException(e);
/*     */     } 
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  99 */     IoUtil.close(this.target);
/* 100 */     IoUtil.close(this.source);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\compress\Deflate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */