/*     */ package cn.hutool.captcha;
/*     */ 
/*     */ import cn.hutool.captcha.generator.CodeGenerator;
/*     */ import cn.hutool.captcha.generator.RandomGenerator;
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.img.ImgUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public abstract class AbstractCaptcha
/*     */   implements ICaptcha
/*     */ {
/*     */   private static final long serialVersionUID = 3180820918087507254L;
/*     */   protected int width;
/*     */   protected int height;
/*     */   protected int interfereCount;
/*     */   protected Font font;
/*     */   protected String code;
/*     */   protected byte[] imageBytes;
/*     */   protected CodeGenerator generator;
/*     */   protected Color background;
/*     */   protected AlphaComposite textAlpha;
/*     */   
/*     */   public AbstractCaptcha(int width, int height, int codeCount, int interfereCount) {
/*  78 */     this(width, height, (CodeGenerator)new RandomGenerator(codeCount), interfereCount);
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
/*     */   public AbstractCaptcha(int width, int height, CodeGenerator generator, int interfereCount) {
/*  90 */     this.width = width;
/*  91 */     this.height = height;
/*  92 */     this.generator = generator;
/*  93 */     this.interfereCount = interfereCount;
/*     */     
/*  95 */     this.font = new Font("SansSerif", 0, (int)(this.height * 0.75D));
/*     */   }
/*     */ 
/*     */   
/*     */   public void createCode() {
/* 100 */     generateCode();
/*     */     
/* 102 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 103 */     ImgUtil.writePng(createImage(this.code), out);
/* 104 */     this.imageBytes = out.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void generateCode() {
/* 113 */     this.code = this.generator.generate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Image createImage(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCode() {
/* 126 */     if (null == this.code) {
/* 127 */       createCode();
/*     */     }
/* 129 */     return this.code;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean verify(String userInputCode) {
/* 134 */     return this.generator.verify(getCode(), userInputCode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(String path) throws IORuntimeException {
/* 144 */     write(FileUtil.touch(path));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(File file) throws IORuntimeException {
/* 154 */     try (OutputStream out = FileUtil.getOutputStream(file)) {
/* 155 */       write(out);
/* 156 */     } catch (IOException e) {
/* 157 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(OutputStream out) {
/* 163 */     IoUtil.write(out, false, getImageBytes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getImageBytes() {
/* 173 */     if (null == this.imageBytes) {
/* 174 */       createCode();
/*     */     }
/* 176 */     return this.imageBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedImage getImage() {
/* 185 */     return ImgUtil.read(IoUtil.toStream(getImageBytes()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getImageBase64() {
/* 195 */     return Base64.encode(getImageBytes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getImageBase64Data() {
/* 205 */     return URLUtil.getDataUriBase64("image/png", getImageBase64());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFont(Font font) {
/* 214 */     this.font = font;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodeGenerator getGenerator() {
/* 223 */     return this.generator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGenerator(CodeGenerator generator) {
/* 232 */     this.generator = generator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBackground(Color background) {
/* 242 */     this.background = background;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTextAlpha(float textAlpha) {
/* 252 */     this.textAlpha = AlphaComposite.getInstance(3, textAlpha);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\AbstractCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */