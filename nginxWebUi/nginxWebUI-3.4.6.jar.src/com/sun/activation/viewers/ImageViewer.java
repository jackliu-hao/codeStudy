/*     */ package com.sun.activation.viewers;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Image;
/*     */ import java.awt.MediaTracker;
/*     */ import java.awt.Panel;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import javax.activation.CommandObject;
/*     */ import javax.activation.DataHandler;
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
/*     */ public class ImageViewer
/*     */   extends Panel
/*     */   implements CommandObject
/*     */ {
/*  37 */   private ImageViewerCanvas canvas = null;
/*     */ 
/*     */ 
/*     */   
/*  41 */   private Image image = null;
/*  42 */   private DataHandler _dh = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean DEBUG = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageViewer() {
/*  51 */     this.canvas = new ImageViewerCanvas();
/*  52 */     add(this.canvas);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCommandContext(String verb, DataHandler dh) throws IOException {
/*  59 */     this._dh = dh;
/*  60 */     setInputStream(this._dh.getInputStream());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setInputStream(InputStream ins) throws IOException {
/*  69 */     MediaTracker mt = new MediaTracker(this);
/*  70 */     int bytes_read = 0;
/*  71 */     byte[] data = new byte[1024];
/*  72 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     
/*  74 */     while ((bytes_read = ins.read(data)) > 0)
/*  75 */       baos.write(data, 0, bytes_read); 
/*  76 */     ins.close();
/*     */ 
/*     */     
/*  79 */     this.image = getToolkit().createImage(baos.toByteArray());
/*     */     
/*  81 */     mt.addImage(this.image, 0);
/*     */     
/*     */     try {
/*  84 */       mt.waitForID(0);
/*  85 */       mt.waitForAll();
/*  86 */       if (mt.statusID(0, true) != 8) {
/*  87 */         System.out.println("Error occured in image loading = " + mt.getErrorsID(0));
/*     */ 
/*     */       
/*     */       }
/*     */     
/*     */     }
/*  93 */     catch (InterruptedException e) {
/*  94 */       throw new IOException("Error reading image data");
/*     */     } 
/*     */     
/*  97 */     this.canvas.setImage(this.image);
/*  98 */     if (this.DEBUG) {
/*  99 */       System.out.println("calling invalidate");
/*     */     }
/*     */   }
/*     */   
/*     */   public void addNotify() {
/* 104 */     super.addNotify();
/* 105 */     invalidate();
/* 106 */     validate();
/* 107 */     doLayout();
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 111 */     return this.canvas.getPreferredSize();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\viewers\ImageViewer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */