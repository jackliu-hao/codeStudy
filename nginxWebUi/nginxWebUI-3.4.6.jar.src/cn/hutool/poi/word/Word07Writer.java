/*     */ package cn.hutool.poi.word;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.poi.exceptions.POIException;
/*     */ import java.awt.Font;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
/*     */ import org.apache.poi.util.Units;
/*     */ import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFDocument;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFParagraph;
/*     */ import org.apache.poi.xwpf.usermodel.XWPFRun;
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
/*     */ public class Word07Writer
/*     */   implements Closeable
/*     */ {
/*     */   private final XWPFDocument doc;
/*     */   protected File destFile;
/*     */   protected boolean isClosed;
/*     */   
/*     */   public Word07Writer() {
/*  43 */     this(new XWPFDocument());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer(File destFile) {
/*  52 */     this(DocUtil.create(destFile), destFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer(XWPFDocument doc) {
/*  61 */     this(doc, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer(XWPFDocument doc, File destFile) {
/*  71 */     this.doc = doc;
/*  72 */     this.destFile = destFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XWPFDocument getDoc() {
/*  83 */     return this.doc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer setDestFile(File destFile) {
/*  93 */     this.destFile = destFile;
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer addText(Font font, String... texts) {
/* 105 */     return addText(null, font, texts);
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
/*     */   public Word07Writer addText(ParagraphAlignment align, Font font, String... texts) {
/* 117 */     XWPFParagraph p = this.doc.createParagraph();
/* 118 */     if (null != align) {
/* 119 */       p.setAlignment(align);
/*     */     }
/* 121 */     if (ArrayUtil.isNotEmpty((Object[])texts))
/*     */     {
/* 123 */       for (String text : texts) {
/* 124 */         XWPFRun run = p.createRun();
/* 125 */         run.setText(text);
/* 126 */         if (null != font) {
/* 127 */           run.setFontFamily(font.getFamily());
/* 128 */           run.setFontSize(font.getSize());
/* 129 */           run.setBold(font.isBold());
/* 130 */           run.setItalic(font.isItalic());
/*     */         } 
/*     */       } 
/*     */     }
/* 134 */     return this;
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
/*     */   public Word07Writer addTable(Iterable<?> data) {
/* 146 */     TableUtil.createTable(this.doc, data);
/* 147 */     return this;
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
/*     */   public Word07Writer addPicture(File picFile, int width, int height) {
/*     */     PicType picType;
/* 160 */     String fileName = picFile.getName();
/* 161 */     String extName = FileUtil.extName(fileName).toUpperCase();
/*     */     
/*     */     try {
/* 164 */       picType = PicType.valueOf(extName);
/* 165 */     } catch (IllegalArgumentException e) {
/*     */       
/* 167 */       picType = PicType.JPEG;
/*     */     } 
/* 169 */     return addPicture(FileUtil.getInputStream(picFile), picType, fileName, width, height);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height) {
/* 184 */     return addPicture(in, picType, fileName, width, height, ParagraphAlignment.CENTER);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer addPicture(InputStream in, PicType picType, String fileName, int width, int height, ParagraphAlignment align) {
/* 200 */     XWPFParagraph paragraph = this.doc.createParagraph();
/* 201 */     paragraph.setAlignment(align);
/* 202 */     XWPFRun run = paragraph.createRun();
/*     */     try {
/* 204 */       run.addPicture(in, picType.getValue(), fileName, Units.toEMU(width), Units.toEMU(height));
/* 205 */     } catch (InvalidFormatException e) {
/* 206 */       throw new POIException(e);
/* 207 */     } catch (IOException e) {
/* 208 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 210 */       IoUtil.close(in);
/*     */     } 
/*     */     
/* 213 */     return this;
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
/*     */   public Word07Writer flush() throws IORuntimeException {
/* 225 */     return flush(this.destFile);
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
/*     */   public Word07Writer flush(File destFile) throws IORuntimeException {
/* 237 */     Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).", new Object[0]);
/* 238 */     return flush(FileUtil.getOutputStream(destFile), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Word07Writer flush(OutputStream out) throws IORuntimeException {
/* 249 */     return flush(out, false);
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
/*     */   public Word07Writer flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
/* 261 */     Assert.isFalse(this.isClosed, "WordWriter has been closed!", new Object[0]);
/*     */     try {
/* 263 */       this.doc.write(out);
/* 264 */       out.flush();
/* 265 */     } catch (IOException e) {
/* 266 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 268 */       if (isCloseOut) {
/* 269 */         IoUtil.close(out);
/*     */       }
/*     */     } 
/* 272 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 281 */     if (null != this.destFile) {
/* 282 */       flush();
/*     */     }
/* 284 */     closeWithoutFlush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void closeWithoutFlush() {
/* 291 */     IoUtil.close((Closeable)this.doc);
/* 292 */     this.isClosed = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\word\Word07Writer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */