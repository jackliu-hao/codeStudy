/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import org.objectweb.asm.Attribute;
/*     */ import org.objectweb.asm.ClassReader;
/*     */ import org.objectweb.asm.Label;
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
/*     */ public class NewAttribute
/*     */   extends Attribute
/*     */ {
/*     */   private boolean contextClass = false;
/*     */   private boolean contextMethod = false;
/*     */   private boolean contextField = false;
/*     */   private boolean contextCode = false;
/*     */   private final String layout;
/*     */   private byte[] contents;
/*     */   private int codeOff;
/*     */   private Label[] labels;
/*     */   private ClassReader classReader;
/*     */   private char[] buf;
/*     */   
/*     */   public NewAttribute(String type, String layout, int context) {
/*  42 */     super(type);
/*  43 */     this.layout = layout;
/*  44 */     addContext(context);
/*     */   }
/*     */ 
/*     */   
/*     */   public NewAttribute(ClassReader classReader, String type, String layout, byte[] contents, char[] buf, int codeOff, Label[] labels) {
/*  49 */     super(type);
/*  50 */     this.classReader = classReader;
/*  51 */     this.contents = contents;
/*  52 */     this.layout = layout;
/*  53 */     this.codeOff = codeOff;
/*  54 */     this.labels = labels;
/*  55 */     this.buf = buf;
/*     */   }
/*     */   
/*     */   public void addContext(int context) {
/*  59 */     switch (context) {
/*     */       case 0:
/*  61 */         this.contextClass = true;
/*     */         break;
/*     */       case 2:
/*  64 */         this.contextMethod = true;
/*     */         break;
/*     */       case 1:
/*  67 */         this.contextField = true;
/*     */         break;
/*     */       case 3:
/*  70 */         this.contextCode = true;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isContextClass() {
/*  76 */     return this.contextClass;
/*     */   }
/*     */   
/*     */   public boolean isContextMethod() {
/*  80 */     return this.contextMethod;
/*     */   }
/*     */   
/*     */   public boolean isContextField() {
/*  84 */     return this.contextField;
/*     */   }
/*     */   
/*     */   public boolean isContextCode() {
/*  88 */     return this.contextCode;
/*     */   }
/*     */   
/*     */   public String getLayout() {
/*  92 */     return this.layout;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnknown() {
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCodeAttribute() {
/* 102 */     return (this.codeOff != -1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
/* 108 */     byte[] attributeContents = new byte[len];
/* 109 */     System.arraycopy(cr.b, off, attributeContents, 0, len);
/* 110 */     return new NewAttribute(cr, this.type, this.layout, attributeContents, buf, codeOff, labels);
/*     */   }
/*     */   
/*     */   public boolean isUnknown(int context) {
/* 114 */     switch (context) {
/*     */       case 0:
/* 116 */         return !this.contextClass;
/*     */       case 2:
/* 118 */         return !this.contextMethod;
/*     */       case 1:
/* 120 */         return !this.contextField;
/*     */       case 3:
/* 122 */         return !this.contextCode;
/*     */     } 
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   public String readUTF8(int index) {
/* 128 */     return this.classReader.readUTF8(index, this.buf);
/*     */   }
/*     */   
/*     */   public String readClass(int index) {
/* 132 */     return this.classReader.readClass(index, this.buf);
/*     */   }
/*     */   
/*     */   public Object readConst(int index) {
/* 136 */     return this.classReader.readConst(index, this.buf);
/*     */   }
/*     */   
/*     */   public byte[] getBytes() {
/* 140 */     return this.contents;
/*     */   }
/*     */   
/*     */   public Label getLabel(int index) {
/* 144 */     return this.labels[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ErrorAttribute
/*     */     extends NewAttribute
/*     */   {
/*     */     public ErrorAttribute(String type, int context) {
/* 155 */       super(type, "", context);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
/* 161 */       throw new Error("Attribute " + this.type + " was found");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class StripAttribute
/*     */     extends NewAttribute
/*     */   {
/*     */     public StripAttribute(String type, int context) {
/* 174 */       super(type, "", context);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
/* 181 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PassAttribute
/*     */     extends NewAttribute
/*     */   {
/*     */     public PassAttribute(String type, int context) {
/* 193 */       super(type, "", context);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
/* 199 */       throw new Segment.PassException();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\NewAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */