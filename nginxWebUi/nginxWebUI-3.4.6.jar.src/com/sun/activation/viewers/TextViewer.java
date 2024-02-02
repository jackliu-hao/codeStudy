/*    */ package com.sun.activation.viewers;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.GridLayout;
/*    */ import java.awt.Panel;
/*    */ import java.awt.TextArea;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.activation.CommandObject;
/*    */ import javax.activation.DataHandler;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TextViewer
/*    */   extends Panel
/*    */   implements CommandObject
/*    */ {
/* 37 */   private TextArea text_area = null;
/*    */ 
/*    */   
/* 40 */   private File text_file = null;
/* 41 */   private String text_buffer = null;
/*    */   
/* 43 */   private DataHandler _dh = null;
/*    */   
/*    */   private boolean DEBUG = false;
/*    */ 
/*    */   
/*    */   public TextViewer() {
/* 49 */     setLayout(new GridLayout(1, 1));
/*    */     
/* 51 */     this.text_area = new TextArea("", 24, 80, 1);
/*    */     
/* 53 */     this.text_area.setEditable(false);
/*    */     
/* 55 */     add(this.text_area);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCommandContext(String verb, DataHandler dh) throws IOException {
/* 60 */     this._dh = dh;
/* 61 */     setInputStream(this._dh.getInputStream());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setInputStream(InputStream ins) throws IOException {
/* 71 */     int bytes_read = 0;
/*    */     
/* 73 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 74 */     byte[] data = new byte[1024];
/*    */     
/* 76 */     while ((bytes_read = ins.read(data)) > 0) {
/* 77 */       baos.write(data, 0, bytes_read);
/*    */     }
/* 79 */     ins.close();
/*    */ 
/*    */ 
/*    */     
/* 83 */     this.text_buffer = baos.toString();
/*    */ 
/*    */     
/* 86 */     this.text_area.setText(this.text_buffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void addNotify() {
/* 91 */     super.addNotify();
/* 92 */     invalidate();
/*    */   }
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 96 */     return this.text_area.getMinimumSize(24, 80);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\viewers\TextViewer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */