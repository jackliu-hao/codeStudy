/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.objectweb.asm.Attribute;
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
/*     */ public class AttributeDefinitionBands
/*     */   extends BandSet
/*     */ {
/*     */   public static final int CONTEXT_CLASS = 0;
/*     */   public static final int CONTEXT_CODE = 3;
/*     */   public static final int CONTEXT_FIELD = 1;
/*     */   public static final int CONTEXT_METHOD = 2;
/*  39 */   private final List classAttributeLayouts = new ArrayList();
/*  40 */   private final List methodAttributeLayouts = new ArrayList();
/*  41 */   private final List fieldAttributeLayouts = new ArrayList();
/*  42 */   private final List codeAttributeLayouts = new ArrayList();
/*     */   
/*  44 */   private final List attributeDefinitions = new ArrayList();
/*     */   
/*     */   private final CpBands cpBands;
/*     */   private final Segment segment;
/*     */   
/*     */   public AttributeDefinitionBands(Segment segment, int effort, Attribute[] attributePrototypes) {
/*  50 */     super(effort, segment.getSegmentHeader());
/*  51 */     this.cpBands = segment.getCpBands();
/*  52 */     this.segment = segment;
/*  53 */     Map<Object, Object> classLayouts = new HashMap<>();
/*  54 */     Map<Object, Object> methodLayouts = new HashMap<>();
/*  55 */     Map<Object, Object> fieldLayouts = new HashMap<>();
/*  56 */     Map<Object, Object> codeLayouts = new HashMap<>();
/*     */     
/*  58 */     for (int i = 0; i < attributePrototypes.length; i++) {
/*  59 */       NewAttribute newAttribute = (NewAttribute)attributePrototypes[i];
/*  60 */       if (!(newAttribute instanceof NewAttribute.ErrorAttribute) && !(newAttribute instanceof NewAttribute.PassAttribute) && !(newAttribute instanceof NewAttribute.StripAttribute)) {
/*     */ 
/*     */         
/*  63 */         if (newAttribute.isContextClass()) {
/*  64 */           classLayouts.put(newAttribute.type, newAttribute.getLayout());
/*     */         }
/*  66 */         if (newAttribute.isContextMethod()) {
/*  67 */           methodLayouts.put(newAttribute.type, newAttribute.getLayout());
/*     */         }
/*  69 */         if (newAttribute.isContextField()) {
/*  70 */           fieldLayouts.put(newAttribute.type, newAttribute.getLayout());
/*     */         }
/*  72 */         if (newAttribute.isContextCode()) {
/*  73 */           codeLayouts.put(newAttribute.type, newAttribute.getLayout());
/*     */         }
/*     */       } 
/*     */     } 
/*  77 */     if (classLayouts.size() > 7) {
/*  78 */       this.segmentHeader.setHave_class_flags_hi(true);
/*     */     }
/*  80 */     if (methodLayouts.size() > 6) {
/*  81 */       this.segmentHeader.setHave_method_flags_hi(true);
/*     */     }
/*  83 */     if (fieldLayouts.size() > 10) {
/*  84 */       this.segmentHeader.setHave_field_flags_hi(true);
/*     */     }
/*  86 */     if (codeLayouts.size() > 15) {
/*  87 */       this.segmentHeader.setHave_code_flags_hi(true);
/*     */     }
/*  89 */     int[] availableClassIndices = { 25, 26, 27, 28, 29, 30, 31 };
/*  90 */     if (classLayouts.size() > 7) {
/*  91 */       availableClassIndices = addHighIndices(availableClassIndices);
/*     */     }
/*  93 */     addAttributeDefinitions(classLayouts, availableClassIndices, 0);
/*  94 */     int[] availableMethodIndices = { 26, 27, 28, 29, 30, 31 };
/*  95 */     if (this.methodAttributeLayouts.size() > 6) {
/*  96 */       availableMethodIndices = addHighIndices(availableMethodIndices);
/*     */     }
/*  98 */     addAttributeDefinitions(methodLayouts, availableMethodIndices, 2);
/*  99 */     int[] availableFieldIndices = { 18, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
/* 100 */     if (this.fieldAttributeLayouts.size() > 10) {
/* 101 */       availableFieldIndices = addHighIndices(availableFieldIndices);
/*     */     }
/* 103 */     addAttributeDefinitions(fieldLayouts, availableFieldIndices, 1);
/* 104 */     int[] availableCodeIndices = { 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
/* 105 */     if (this.codeAttributeLayouts.size() > 15) {
/* 106 */       availableCodeIndices = addHighIndices(availableCodeIndices);
/*     */     }
/* 108 */     addAttributeDefinitions(codeLayouts, availableCodeIndices, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finaliseBands() {
/* 116 */     addSyntheticDefinitions();
/* 117 */     this.segmentHeader.setAttribute_definition_count(this.attributeDefinitions.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public void pack(OutputStream out) throws IOException, Pack200Exception {
/* 122 */     PackingUtils.log("Writing attribute definition bands...");
/* 123 */     int[] attributeDefinitionHeader = new int[this.attributeDefinitions.size()];
/* 124 */     int[] attributeDefinitionName = new int[this.attributeDefinitions.size()];
/* 125 */     int[] attributeDefinitionLayout = new int[this.attributeDefinitions.size()];
/* 126 */     for (int i = 0; i < attributeDefinitionLayout.length; i++) {
/* 127 */       AttributeDefinition def = this.attributeDefinitions.get(i);
/* 128 */       attributeDefinitionHeader[i] = def.contextType | def.index + 1 << 2;
/* 129 */       attributeDefinitionName[i] = def.name.getIndex();
/* 130 */       attributeDefinitionLayout[i] = def.layout.getIndex();
/*     */     } 
/*     */     
/* 133 */     byte[] encodedBand = encodeBandInt("attributeDefinitionHeader", attributeDefinitionHeader, Codec.BYTE1);
/* 134 */     out.write(encodedBand);
/* 135 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionHeader[" + attributeDefinitionHeader.length + "]");
/*     */ 
/*     */     
/* 138 */     encodedBand = encodeBandInt("attributeDefinitionName", attributeDefinitionName, Codec.UNSIGNED5);
/* 139 */     out.write(encodedBand);
/* 140 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionName[" + attributeDefinitionName.length + "]");
/*     */ 
/*     */     
/* 143 */     encodedBand = encodeBandInt("attributeDefinitionLayout", attributeDefinitionLayout, Codec.UNSIGNED5);
/* 144 */     out.write(encodedBand);
/* 145 */     PackingUtils.log("Wrote " + encodedBand.length + " bytes from attributeDefinitionLayout[" + attributeDefinitionLayout.length + "]");
/*     */   }
/*     */ 
/*     */   
/*     */   private void addSyntheticDefinitions() {
/* 150 */     boolean anySytheticClasses = this.segment.getClassBands().isAnySyntheticClasses();
/* 151 */     boolean anySyntheticMethods = this.segment.getClassBands().isAnySyntheticMethods();
/* 152 */     boolean anySyntheticFields = this.segment.getClassBands().isAnySyntheticFields();
/* 153 */     if (anySytheticClasses || anySyntheticMethods || anySyntheticFields) {
/* 154 */       CPUTF8 syntheticUTF = this.cpBands.getCPUtf8("Synthetic");
/* 155 */       CPUTF8 emptyUTF = this.cpBands.getCPUtf8("");
/* 156 */       if (anySytheticClasses) {
/* 157 */         this.attributeDefinitions.add(new AttributeDefinition(12, 0, syntheticUTF, emptyUTF));
/*     */       }
/* 159 */       if (anySyntheticMethods) {
/* 160 */         this.attributeDefinitions.add(new AttributeDefinition(12, 2, syntheticUTF, emptyUTF));
/*     */       }
/* 162 */       if (anySyntheticFields) {
/* 163 */         this.attributeDefinitions.add(new AttributeDefinition(12, 1, syntheticUTF, emptyUTF));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private int[] addHighIndices(int[] availableIndices) {
/* 169 */     int[] temp = new int[availableIndices.length + 32];
/* 170 */     for (int i = 0; i < availableIndices.length; i++) {
/* 171 */       temp[i] = availableIndices[i];
/*     */     }
/* 173 */     int j = 32;
/* 174 */     for (int k = availableIndices.length; k < temp.length; k++) {
/* 175 */       temp[k] = j;
/* 176 */       j++;
/*     */     } 
/* 178 */     return temp;
/*     */   }
/*     */   
/*     */   private void addAttributeDefinitions(Map layouts, int[] availableIndices, int contextType) {
/* 182 */     int i = 0;
/* 183 */     for (Iterator<String> iterator = layouts.keySet().iterator(); iterator.hasNext(); ) {
/* 184 */       String name = iterator.next();
/* 185 */       String layout = (String)layouts.get(name);
/* 186 */       int index = availableIndices[0];
/*     */       
/* 188 */       AttributeDefinition definition = new AttributeDefinition(index, contextType, this.cpBands.getCPUtf8(name), this.cpBands.getCPUtf8(layout));
/* 189 */       this.attributeDefinitions.add(definition);
/* 190 */       switch (contextType) {
/*     */         case 0:
/* 192 */           this.classAttributeLayouts.add(definition);
/*     */         
/*     */         case 2:
/* 195 */           this.methodAttributeLayouts.add(definition);
/*     */         
/*     */         case 1:
/* 198 */           this.fieldAttributeLayouts.add(definition);
/*     */         
/*     */         case 3:
/* 201 */           this.codeAttributeLayouts.add(definition);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public List getClassAttributeLayouts() {
/* 207 */     return this.classAttributeLayouts;
/*     */   }
/*     */   
/*     */   public List getMethodAttributeLayouts() {
/* 211 */     return this.methodAttributeLayouts;
/*     */   }
/*     */   
/*     */   public List getFieldAttributeLayouts() {
/* 215 */     return this.fieldAttributeLayouts;
/*     */   }
/*     */   
/*     */   public List getCodeAttributeLayouts() {
/* 219 */     return this.codeAttributeLayouts;
/*     */   }
/*     */   
/*     */   public static class AttributeDefinition
/*     */   {
/*     */     public int index;
/*     */     public int contextType;
/*     */     public CPUTF8 name;
/*     */     public CPUTF8 layout;
/*     */     
/*     */     public AttributeDefinition(int index, int contextType, CPUTF8 name, CPUTF8 layout) {
/* 230 */       this.index = index;
/* 231 */       this.contextType = contextType;
/* 232 */       this.name = name;
/* 233 */       this.layout = layout;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\AttributeDefinitionBands.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */