/*     */ package org.apache.commons.compress.harmony.unpack200;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.harmony.pack200.Pack200Exception;
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
/*     */ public class AttributeLayoutMap
/*     */ {
/*     */   private static AttributeLayout[] getDefaultAttributeLayouts() throws Pack200Exception {
/*  36 */     return new AttributeLayout[] { new AttributeLayout("ACC_PUBLIC", 0, "", 0), new AttributeLayout("ACC_PUBLIC", 1, "", 0), new AttributeLayout("ACC_PUBLIC", 2, "", 0), new AttributeLayout("ACC_PRIVATE", 0, "", 1), new AttributeLayout("ACC_PRIVATE", 1, "", 1), new AttributeLayout("ACC_PRIVATE", 2, "", 1), new AttributeLayout("LineNumberTable", 3, "NH[PHH]", 1), new AttributeLayout("ACC_PROTECTED", 0, "", 2), new AttributeLayout("ACC_PROTECTED", 1, "", 2), new AttributeLayout("ACC_PROTECTED", 2, "", 2), new AttributeLayout("LocalVariableTable", 3, "NH[PHOHRUHRSHH]", 2), new AttributeLayout("ACC_STATIC", 0, "", 3), new AttributeLayout("ACC_STATIC", 1, "", 3), new AttributeLayout("ACC_STATIC", 2, "", 3), new AttributeLayout("LocalVariableTypeTable", 3, "NH[PHOHRUHRSHH]", 3), new AttributeLayout("ACC_FINAL", 0, "", 4), new AttributeLayout("ACC_FINAL", 1, "", 4), new AttributeLayout("ACC_FINAL", 2, "", 4), new AttributeLayout("ACC_SYNCHRONIZED", 0, "", 5), new AttributeLayout("ACC_SYNCHRONIZED", 1, "", 5), new AttributeLayout("ACC_SYNCHRONIZED", 2, "", 5), new AttributeLayout("ACC_VOLATILE", 0, "", 6), new AttributeLayout("ACC_VOLATILE", 1, "", 6), new AttributeLayout("ACC_VOLATILE", 2, "", 6), new AttributeLayout("ACC_TRANSIENT", 0, "", 7), new AttributeLayout("ACC_TRANSIENT", 1, "", 7), new AttributeLayout("ACC_TRANSIENT", 2, "", 7), new AttributeLayout("ACC_NATIVE", 0, "", 8), new AttributeLayout("ACC_NATIVE", 1, "", 8), new AttributeLayout("ACC_NATIVE", 2, "", 8), new AttributeLayout("ACC_INTERFACE", 0, "", 9), new AttributeLayout("ACC_INTERFACE", 1, "", 9), new AttributeLayout("ACC_INTERFACE", 2, "", 9), new AttributeLayout("ACC_ABSTRACT", 0, "", 10), new AttributeLayout("ACC_ABSTRACT", 1, "", 10), new AttributeLayout("ACC_ABSTRACT", 2, "", 10), new AttributeLayout("ACC_STRICT", 0, "", 11), new AttributeLayout("ACC_STRICT", 1, "", 11), new AttributeLayout("ACC_STRICT", 2, "", 11), new AttributeLayout("ACC_SYNTHETIC", 0, "", 12), new AttributeLayout("ACC_SYNTHETIC", 1, "", 12), new AttributeLayout("ACC_SYNTHETIC", 2, "", 12), new AttributeLayout("ACC_ANNOTATION", 0, "", 13), new AttributeLayout("ACC_ANNOTATION", 1, "", 13), new AttributeLayout("ACC_ANNOTATION", 2, "", 13), new AttributeLayout("ACC_ENUM", 0, "", 14), new AttributeLayout("ACC_ENUM", 1, "", 14), new AttributeLayout("ACC_ENUM", 2, "", 14), new AttributeLayout("SourceFile", 0, "RUNH", 17), new AttributeLayout("ConstantValue", 1, "KQH", 17), new AttributeLayout("Code", 2, "", 17), new AttributeLayout("EnclosingMethod", 0, "RCHRDNH", 18), new AttributeLayout("Exceptions", 2, "NH[RCH]", 18), new AttributeLayout("Signature", 0, "RSH", 19), new AttributeLayout("Signature", 1, "RSH", 19), new AttributeLayout("Signature", 2, "RSH", 19), new AttributeLayout("Deprecated", 0, "", 20), new AttributeLayout("Deprecated", 1, "", 20), new AttributeLayout("Deprecated", 2, "", 20), new AttributeLayout("RuntimeVisibleAnnotations", 0, "*", 21), new AttributeLayout("RuntimeVisibleAnnotations", 1, "*", 21), new AttributeLayout("RuntimeVisibleAnnotations", 2, "*", 21), new AttributeLayout("RuntimeInvisibleAnnotations", 0, "*", 22), new AttributeLayout("RuntimeInvisibleAnnotations", 1, "*", 22), new AttributeLayout("RuntimeInvisibleAnnotations", 2, "*", 22), new AttributeLayout("InnerClasses", 0, "", 23), new AttributeLayout("RuntimeVisibleParameterAnnotations", 2, "*", 23), new AttributeLayout("class-file version", 0, "", 24), new AttributeLayout("RuntimeInvisibleParameterAnnotations", 2, "*", 24), new AttributeLayout("AnnotationDefault", 2, "*", 25) };
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
/* 124 */   private final Map classLayouts = new HashMap<>();
/* 125 */   private final Map fieldLayouts = new HashMap<>();
/* 126 */   private final Map methodLayouts = new HashMap<>();
/* 127 */   private final Map codeLayouts = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 132 */   private final Map[] layouts = new Map[] { this.classLayouts, this.fieldLayouts, this.methodLayouts, this.codeLayouts };
/*     */   
/* 134 */   private final Map layoutsToBands = new HashMap<>();
/*     */   
/*     */   public AttributeLayoutMap() throws Pack200Exception {
/* 137 */     AttributeLayout[] defaultAttributeLayouts = getDefaultAttributeLayouts();
/* 138 */     for (int i = 0; i < defaultAttributeLayouts.length; i++) {
/* 139 */       add(defaultAttributeLayouts[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void add(AttributeLayout layout) {
/* 144 */     this.layouts[layout.getContext()].put(Integer.valueOf(layout.getIndex()), layout);
/*     */   }
/*     */   
/*     */   public void add(AttributeLayout layout, NewAttributeBands newBands) {
/* 148 */     add(layout);
/* 149 */     this.layoutsToBands.put(layout, newBands);
/*     */   }
/*     */   
/*     */   public AttributeLayout getAttributeLayout(String name, int context) throws Pack200Exception {
/* 153 */     Map map = this.layouts[context];
/* 154 */     for (Iterator<AttributeLayout> iter = map.values().iterator(); iter.hasNext(); ) {
/* 155 */       AttributeLayout layout = iter.next();
/* 156 */       if (layout.getName().equals(name)) {
/* 157 */         return layout;
/*     */       }
/*     */     } 
/* 160 */     return null;
/*     */   }
/*     */   
/*     */   public AttributeLayout getAttributeLayout(int index, int context) throws Pack200Exception {
/* 164 */     Map map = this.layouts[context];
/* 165 */     return (AttributeLayout)map.get(Integer.valueOf(index));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkMap() throws Pack200Exception {
/* 174 */     for (int i = 0; i < this.layouts.length; i++) {
/* 175 */       Map map = this.layouts[i];
/* 176 */       Collection<?> c = map.values();
/* 177 */       if (!(c instanceof List)) {
/* 178 */         c = new ArrayList(c);
/*     */       }
/* 180 */       List<AttributeLayout> l = (List)c;
/* 181 */       for (int j = 0; j < l.size(); j++) {
/* 182 */         AttributeLayout layout1 = l.get(j);
/* 183 */         for (int j2 = j + 1; j2 < l.size(); j2++) {
/* 184 */           AttributeLayout layout2 = l.get(j2);
/* 185 */           if (layout1.getName().equals(layout2.getName()) && layout1
/* 186 */             .getLayout().equals(layout2.getLayout())) {
/* 187 */             throw new Pack200Exception("Same layout/name combination: " + layout1
/* 188 */                 .getLayout() + "/" + layout1.getName() + " exists twice for context: " + AttributeLayout.contextNames[layout1
/* 189 */                   .getContext()]);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public NewAttributeBands getAttributeBands(AttributeLayout layout) {
/* 197 */     return (NewAttributeBands)this.layoutsToBands.get(layout);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\AttributeLayoutMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */