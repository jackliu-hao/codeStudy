/*     */ package cn.hutool.poi.ofd;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.file.PathUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.file.Path;
/*     */ import org.ofdrw.font.Font;
/*     */ import org.ofdrw.layout.OFDDoc;
/*     */ import org.ofdrw.layout.edit.Annotation;
/*     */ import org.ofdrw.layout.element.Div;
/*     */ import org.ofdrw.layout.element.Img;
/*     */ import org.ofdrw.layout.element.Paragraph;
/*     */ import org.ofdrw.reader.OFDReader;
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
/*     */ public class OfdWriter
/*     */   implements Serializable, Closeable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final OFDDoc doc;
/*     */   
/*     */   public OfdWriter(File file) {
/*  38 */     this(file.toPath());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter(Path file) {
/*     */     try {
/*  48 */       if (PathUtil.exists(file, true)) {
/*  49 */         this.doc = new OFDDoc(new OFDReader(file), file);
/*     */       } else {
/*  51 */         this.doc = new OFDDoc(file);
/*     */       } 
/*  53 */     } catch (IOException e) {
/*  54 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter(OutputStream out) {
/*  64 */     this.doc = new OFDDoc(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter addText(Font font, String... texts) {
/*  75 */     Paragraph paragraph = new Paragraph();
/*  76 */     if (null != font) {
/*  77 */       paragraph.setDefaultFont(font);
/*     */     }
/*  79 */     for (String text : texts) {
/*  80 */       paragraph.add(text);
/*     */     }
/*  82 */     return add((Div)paragraph);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter addPicture(File picFile, int width, int height) {
/*  94 */     return addPicture(picFile.toPath(), width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter addPicture(Path picFile, int width, int height) {
/*     */     Img img;
/*     */     try {
/* 108 */       img = new Img(width, height, picFile);
/* 109 */     } catch (IOException e) {
/* 110 */       throw new IORuntimeException(e);
/*     */     } 
/* 112 */     return add((Div)img);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter add(Div div) {
/* 123 */     this.doc.add(div);
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OfdWriter add(int page, Annotation annotation) {
/*     */     try {
/* 136 */       this.doc.addAnnotation(page, annotation);
/* 137 */     } catch (IOException e) {
/* 138 */       throw new IORuntimeException(e);
/*     */     } 
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 145 */     IoUtil.close((Closeable)this.doc);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\ofd\OfdWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */