/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.harmony.pack200.Codec;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.AnnotationDefaultAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantValueAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.DeprecatedAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.EnclosingMethodAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionsAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.InnerClassesAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.LineNumberTableAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTableAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTypeTableAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.SignatureAttribute;
/*     */ import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;
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
/*     */ public class AttrDefinitionBands
/*     */   extends BandSet
/*     */ {
/*     */   private int[] attributeDefinitionHeader;
/*     */   private String[] attributeDefinitionLayout;
/*     */   private String[] attributeDefinitionName;
/*     */   private AttributeLayoutMap attributeDefinitionMap;
/*     */   private final String[] cpUTF8;
/*     */   
/*     */   public AttrDefinitionBands(Segment segment) {
/*  53 */     super(segment);
/*  54 */     this.cpUTF8 = segment.getCpBands().getCpUTF8();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(InputStream in) throws IOException, Pack200Exception {
/*  64 */     int attributeDefinitionCount = this.header.getAttributeDefinitionCount();
/*  65 */     this.attributeDefinitionHeader = decodeBandInt("attr_definition_headers", in, Codec.BYTE1, attributeDefinitionCount);
/*  66 */     this.attributeDefinitionName = parseReferences("attr_definition_name", in, Codec.UNSIGNED5, attributeDefinitionCount, this.cpUTF8);
/*     */     
/*  68 */     this.attributeDefinitionLayout = parseReferences("attr_definition_layout", in, Codec.UNSIGNED5, attributeDefinitionCount, this.cpUTF8);
/*     */ 
/*     */     
/*  71 */     this.attributeDefinitionMap = new AttributeLayoutMap();
/*     */     
/*  73 */     int overflowIndex = 32;
/*  74 */     if (this.segment.getSegmentHeader().getOptions().hasClassFlagsHi()) {
/*  75 */       overflowIndex = 63;
/*     */     }
/*  77 */     for (int i = 0; i < attributeDefinitionCount; i++) {
/*  78 */       int context = this.attributeDefinitionHeader[i] & 0x3;
/*  79 */       int index = (this.attributeDefinitionHeader[i] >> 2) - 1;
/*  80 */       if (index == -1) {
/*  81 */         index = overflowIndex++;
/*     */       }
/*  83 */       AttributeLayout layout = new AttributeLayout(this.attributeDefinitionName[i], context, this.attributeDefinitionLayout[i], index, false);
/*     */       
/*  85 */       NewAttributeBands newBands = new NewAttributeBands(this.segment, layout);
/*  86 */       this.attributeDefinitionMap.add(layout, newBands);
/*     */     } 
/*  88 */     this.attributeDefinitionMap.checkMap();
/*  89 */     setupDefaultAttributeNames();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void unpack() throws Pack200Exception, IOException {}
/*     */ 
/*     */   
/*     */   private void setupDefaultAttributeNames() {
/*  98 */     AnnotationDefaultAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("AnnotationDefault"));
/*  99 */     CodeAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("Code"));
/* 100 */     ConstantValueAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("ConstantValue"));
/* 101 */     DeprecatedAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("Deprecated"));
/* 102 */     EnclosingMethodAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("EnclosingMethod"));
/* 103 */     ExceptionsAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("Exceptions"));
/* 104 */     InnerClassesAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("InnerClasses"));
/* 105 */     LineNumberTableAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("LineNumberTable"));
/* 106 */     LocalVariableTableAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("LocalVariableTable"));
/* 107 */     LocalVariableTypeTableAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("LocalVariableTypeTable"));
/* 108 */     SignatureAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("Signature"));
/* 109 */     SourceFileAttribute.setAttributeName(this.segment.getCpBands().cpUTF8Value("SourceFile"));
/* 110 */     MetadataBandGroup.setRvaAttributeName(this.segment.getCpBands().cpUTF8Value("RuntimeVisibleAnnotations"));
/* 111 */     MetadataBandGroup.setRiaAttributeName(this.segment.getCpBands().cpUTF8Value("RuntimeInvisibleAnnotations"));
/* 112 */     MetadataBandGroup.setRvpaAttributeName(this.segment.getCpBands().cpUTF8Value("RuntimeVisibleParameterAnnotations"));
/*     */     
/* 114 */     MetadataBandGroup.setRipaAttributeName(this.segment.getCpBands().cpUTF8Value("RuntimeInvisibleParameterAnnotations"));
/*     */   }
/*     */   
/*     */   public AttributeLayoutMap getAttributeDefinitionMap() {
/* 118 */     return this.attributeDefinitionMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\AttrDefinitionBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */