/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
/*     */ import org.codehaus.plexus.util.xml.pull.XmlSerializer;
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
/*     */ public class SerializerXMLWriter
/*     */   implements XMLWriter
/*     */ {
/*     */   private final XmlSerializer serializer;
/*     */   private final String namespace;
/*  40 */   private final Stack elements = new Stack();
/*     */   
/*     */   private List exceptions;
/*     */ 
/*     */   
/*     */   public SerializerXMLWriter(String namespace, XmlSerializer serializer) {
/*  46 */     this.serializer = serializer;
/*  47 */     this.namespace = namespace;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void startElement(String name) {
/*     */     try {
/*  54 */       this.serializer.startTag(this.namespace, name);
/*  55 */       this.elements.push(name);
/*     */     }
/*  57 */     catch (IOException e) {
/*     */       
/*  59 */       storeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAttribute(String key, String value) {
/*     */     try {
/*  67 */       this.serializer.attribute(this.namespace, key, value);
/*     */     }
/*  69 */     catch (IOException e) {
/*     */       
/*  71 */       storeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeText(String text) {
/*     */     try {
/*  79 */       this.serializer.text(text);
/*     */     }
/*  81 */     catch (IOException e) {
/*     */       
/*  83 */       storeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeMarkup(String text) {
/*     */     try {
/*  91 */       this.serializer.cdsect(text);
/*     */     }
/*  93 */     catch (IOException e) {
/*     */       
/*  95 */       storeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void endElement() {
/*     */     try {
/* 103 */       this.serializer.endTag(this.namespace, this.elements.pop());
/*     */     }
/* 105 */     catch (IOException e) {
/*     */       
/* 107 */       storeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void storeException(IOException e) {
/* 116 */     if (this.exceptions == null)
/*     */     {
/* 118 */       this.exceptions = new ArrayList();
/*     */     }
/* 120 */     this.exceptions.add(e);
/*     */   }
/*     */ 
/*     */   
/*     */   public List getExceptions() {
/* 125 */     return (this.exceptions == null) ? Collections.EMPTY_LIST : this.exceptions;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\SerializerXMLWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */