/*     */ package com.sun.activation.viewers;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class TextEditor
/*     */   extends Panel
/*     */   implements CommandObject, ActionListener
/*     */ {
/*  39 */   private TextArea text_area = null;
/*  40 */   private GridBagLayout panel_gb = null;
/*  41 */   private Panel button_panel = null;
/*  42 */   private Button save_button = null;
/*     */   
/*  44 */   private File text_file = null;
/*  45 */   private String text_buffer = null;
/*  46 */   private InputStream data_ins = null;
/*  47 */   private FileInputStream fis = null;
/*     */   
/*  49 */   private DataHandler _dh = null;
/*     */   
/*     */   private boolean DEBUG = false;
/*     */ 
/*     */   
/*     */   public TextEditor() {
/*  55 */     this.panel_gb = new GridBagLayout();
/*  56 */     setLayout(this.panel_gb);
/*     */     
/*  58 */     this.button_panel = new Panel();
/*     */     
/*  60 */     this.button_panel.setLayout(new FlowLayout());
/*  61 */     this.save_button = new Button("SAVE");
/*  62 */     this.button_panel.add(this.save_button);
/*  63 */     addGridComponent(this, this.button_panel, this.panel_gb, 0, 0, 1, 1, 1, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     this.text_area = new TextArea("This is text", 24, 80, 1);
/*     */ 
/*     */     
/*  74 */     this.text_area.setEditable(true);
/*     */     
/*  76 */     addGridComponent(this, this.text_area, this.panel_gb, 0, 1, 1, 2, 1, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     this.save_button.addActionListener(this);
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
/*     */   
/*     */   private void addGridComponent(Container cont, Component comp, GridBagLayout mygb, int gridx, int gridy, int gridw, int gridh, int weightx, int weighty) {
/* 101 */     GridBagConstraints c = new GridBagConstraints();
/* 102 */     c.gridx = gridx;
/* 103 */     c.gridy = gridy;
/* 104 */     c.gridwidth = gridw;
/* 105 */     c.gridheight = gridh;
/* 106 */     c.fill = 1;
/* 107 */     c.weighty = weighty;
/* 108 */     c.weightx = weightx;
/* 109 */     c.anchor = 10;
/* 110 */     mygb.setConstraints(comp, c);
/* 111 */     cont.add(comp);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCommandContext(String verb, DataHandler dh) throws IOException {
/* 116 */     this._dh = dh;
/* 117 */     setInputStream(this._dh.getInputStream());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInputStream(InputStream ins) throws IOException {
/* 128 */     byte[] data = new byte[1024];
/* 129 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 130 */     int bytes_read = 0;
/*     */ 
/*     */     
/* 133 */     while ((bytes_read = ins.read(data)) > 0)
/* 134 */       baos.write(data, 0, bytes_read); 
/* 135 */     ins.close();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     this.text_buffer = baos.toString();
/*     */ 
/*     */     
/* 143 */     this.text_area.setText(this.text_buffer);
/*     */   }
/*     */   
/*     */   private void performSaveOperation() {
/* 147 */     OutputStream fos = null;
/*     */     try {
/* 149 */       fos = this._dh.getOutputStream();
/* 150 */     } catch (Exception e) {}
/*     */     
/* 152 */     String buffer = this.text_area.getText();
/*     */ 
/*     */     
/* 155 */     if (fos == null) {
/* 156 */       System.out.println("Invalid outputstream in TextEditor!");
/* 157 */       System.out.println("not saving!");
/*     */     } 
/*     */     
/*     */     try {
/* 161 */       fos.write(buffer.getBytes());
/* 162 */       fos.flush();
/* 163 */       fos.close();
/* 164 */     } catch (IOException e) {
/*     */       
/* 166 */       System.out.println("TextEditor Save Operation failed with: " + e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addNotify() {
/* 172 */     super.addNotify();
/* 173 */     invalidate();
/*     */   }
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 177 */     return this.text_area.getMinimumSize(24, 80);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent evt) {
/* 182 */     if (evt.getSource() == this.save_button)
/*     */     {
/*     */       
/* 185 */       performSaveOperation();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\viewers\TextEditor.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */