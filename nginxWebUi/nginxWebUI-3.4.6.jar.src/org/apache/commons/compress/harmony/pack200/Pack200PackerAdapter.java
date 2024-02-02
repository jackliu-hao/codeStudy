/*    */ package org.apache.commons.compress.harmony.pack200;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.jar.JarFile;
/*    */ import java.util.jar.JarInputStream;
/*    */ import org.apache.commons.compress.java.util.jar.Pack200;
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
/*    */ 
/*    */ public class Pack200PackerAdapter
/*    */   extends Pack200Adapter
/*    */   implements Pack200.Packer
/*    */ {
/* 33 */   private final PackingOptions options = new PackingOptions();
/*    */ 
/*    */   
/*    */   public void pack(JarFile file, OutputStream out) throws IOException {
/* 37 */     if (file == null || out == null) {
/* 38 */       throw new IllegalArgumentException("Must specify both input and output streams");
/*    */     }
/* 40 */     completed(0.0D);
/*    */     try {
/* 42 */       (new Archive(file, out, this.options)).pack();
/* 43 */     } catch (Pack200Exception e) {
/* 44 */       throw new IOException("Failed to pack Jar:" + String.valueOf(e));
/*    */     } 
/* 46 */     completed(1.0D);
/*    */   }
/*    */ 
/*    */   
/*    */   public void pack(JarInputStream in, OutputStream out) throws IOException {
/* 51 */     if (in == null || out == null) {
/* 52 */       throw new IllegalArgumentException("Must specify both input and output streams");
/*    */     }
/* 54 */     completed(0.0D);
/* 55 */     PackingOptions options = new PackingOptions();
/*    */     
/*    */     try {
/* 58 */       (new Archive(in, out, options)).pack();
/* 59 */     } catch (Pack200Exception e) {
/* 60 */       throw new IOException("Failed to pack Jar:" + String.valueOf(e));
/*    */     } 
/* 62 */     completed(1.0D);
/* 63 */     in.close();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
/* 68 */     super.firePropertyChange(propertyName, oldValue, newValue);
/* 69 */     if (newValue != null && !newValue.equals(oldValue))
/* 70 */       if (propertyName.startsWith("pack.class.attribute.")) {
/* 71 */         String attributeName = propertyName.substring("pack.class.attribute.".length());
/* 72 */         this.options.addClassAttributeAction(attributeName, (String)newValue);
/* 73 */       } else if (propertyName.startsWith("pack.code.attribute.")) {
/* 74 */         String attributeName = propertyName.substring("pack.code.attribute.".length());
/* 75 */         this.options.addCodeAttributeAction(attributeName, (String)newValue);
/* 76 */       } else if (propertyName.equals("pack.deflate.hint")) {
/* 77 */         this.options.setDeflateHint((String)newValue);
/* 78 */       } else if (propertyName.equals("pack.effort")) {
/* 79 */         this.options.setEffort(Integer.parseInt((String)newValue));
/* 80 */       } else if (propertyName.startsWith("pack.field.attribute.")) {
/* 81 */         String attributeName = propertyName.substring("pack.field.attribute.".length());
/* 82 */         this.options.addFieldAttributeAction(attributeName, (String)newValue);
/* 83 */       } else if (propertyName.equals("pack.keep.file.order")) {
/* 84 */         this.options.setKeepFileOrder(Boolean.parseBoolean((String)newValue));
/* 85 */       } else if (propertyName.startsWith("pack.method.attribute.")) {
/* 86 */         String attributeName = propertyName.substring("pack.method.attribute.".length());
/* 87 */         this.options.addMethodAttributeAction(attributeName, (String)newValue);
/* 88 */       } else if (propertyName.equals("pack.modification.time")) {
/* 89 */         this.options.setModificationTime((String)newValue);
/* 90 */       } else if (propertyName.startsWith("pack.pass.file.")) {
/* 91 */         if (oldValue != null && !oldValue.equals("")) {
/* 92 */           this.options.removePassFile((String)oldValue);
/*    */         }
/* 94 */         this.options.addPassFile((String)newValue);
/* 95 */       } else if (propertyName.equals("pack.segment.limit")) {
/* 96 */         this.options.setSegmentLimit(Long.parseLong((String)newValue));
/* 97 */       } else if (propertyName.equals("pack.unknown.attribute")) {
/* 98 */         this.options.setUnknownAttributeAction((String)newValue);
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Pack200PackerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */