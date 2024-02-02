/*     */ package com.sun.mail.handlers;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.activation.ActivationDataFlavor;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class text_xml
/*     */   extends text_plain
/*     */ {
/*  68 */   private final DataFlavor[] flavors = new DataFlavor[] { (DataFlavor)new ActivationDataFlavor(String.class, "text/xml", "XML String"), (DataFlavor)new ActivationDataFlavor(String.class, "application/xml", "XML String"), (DataFlavor)new ActivationDataFlavor(StreamSource.class, "text/xml", "XML"), (DataFlavor)new ActivationDataFlavor(StreamSource.class, "application/xml", "XML") };
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
/*     */   public DataFlavor[] getTransferDataFlavors() {
/*  84 */     return (DataFlavor[])this.flavors.clone();
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
/*     */   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
/*  97 */     for (int i = 0; i < this.flavors.length; i++) {
/*  98 */       DataFlavor aFlavor = this.flavors[i];
/*  99 */       if (aFlavor.equals(df)) {
/* 100 */         if (aFlavor.getRepresentationClass() == String.class)
/* 101 */           return getContent(ds); 
/* 102 */         if (aFlavor.getRepresentationClass() == StreamSource.class) {
/* 103 */           return new StreamSource(ds.getInputStream());
/*     */         }
/* 105 */         return null;
/*     */       } 
/*     */     } 
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
/* 115 */     if (!isXmlType(mimeType)) {
/* 116 */       throw new IOException("Invalid content type \"" + mimeType + "\" for text/xml DCH");
/*     */     }
/* 118 */     if (obj instanceof String) {
/* 119 */       super.writeTo(obj, mimeType, os);
/*     */       return;
/*     */     } 
/* 122 */     if (!(obj instanceof DataSource) && !(obj instanceof Source)) {
/* 123 */       throw new IOException("Invalid Object type = " + obj.getClass() + ". XmlDCH can only convert DataSource or Source to XML.");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 128 */       Transformer transformer = TransformerFactory.newInstance().newTransformer();
/*     */       
/* 130 */       StreamResult result = new StreamResult(os);
/* 131 */       if (obj instanceof DataSource) {
/*     */ 
/*     */         
/* 134 */         transformer.transform(new StreamSource(((DataSource)obj).getInputStream()), result);
/*     */       }
/*     */       else {
/*     */         
/* 138 */         transformer.transform((Source)obj, result);
/*     */       } 
/* 140 */     } catch (Exception ex) {
/* 141 */       throw new IOException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isXmlType(String type) {
/*     */     try {
/* 149 */       ContentType ct = new ContentType(type);
/* 150 */       return (ct.getSubType().equals("xml") && (ct.getPrimaryType().equals("text") || ct.getPrimaryType().equals("application")));
/*     */     
/*     */     }
/* 153 */     catch (Exception ex) {
/* 154 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\text_xml.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */