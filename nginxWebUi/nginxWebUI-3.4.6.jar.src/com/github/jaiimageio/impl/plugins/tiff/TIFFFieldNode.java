/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFTag;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.imageio.metadata.IIOMetadataNode;
/*     */ import org.w3c.dom.Node;
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
/*     */ public class TIFFFieldNode
/*     */   extends IIOMetadataNode
/*     */ {
/*     */   private boolean isIFD;
/*     */   
/*     */   private static String getNodeName(TIFFField f) {
/*  67 */     return (f.getData() instanceof TIFFDirectory) ? "TIFFIFD" : "TIFFField";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   private Boolean isInitialized = Boolean.FALSE;
/*     */   
/*     */   private TIFFField field;
/*     */ 
/*     */   
/*     */   public TIFFFieldNode(TIFFField field) {
/*  80 */     super(getNodeName(field));
/*     */     
/*  82 */     this.isIFD = field.getData() instanceof TIFFDirectory;
/*     */     
/*  84 */     this.field = field;
/*     */     
/*  86 */     TIFFTag tag = field.getTag();
/*  87 */     int tagNumber = tag.getNumber();
/*  88 */     String tagName = tag.getName();
/*     */     
/*  90 */     if (this.isIFD) {
/*  91 */       if (tagNumber != 0) {
/*  92 */         setAttribute("parentTagNumber", Integer.toString(tagNumber));
/*     */       }
/*  94 */       if (tagName != null) {
/*  95 */         setAttribute("parentTagName", tagName);
/*     */       }
/*     */       
/*  98 */       TIFFDirectory dir = (TIFFDirectory)field.getData();
/*  99 */       TIFFTagSet[] tagSets = dir.getTagSets();
/* 100 */       if (tagSets != null) {
/* 101 */         String tagSetNames = "";
/* 102 */         for (int i = 0; i < tagSets.length; i++) {
/* 103 */           tagSetNames = tagSetNames + tagSets[i].getClass().getName();
/* 104 */           if (i != tagSets.length - 1) {
/* 105 */             tagSetNames = tagSetNames + ",";
/*     */           }
/*     */         } 
/* 108 */         setAttribute("tagSets", tagSetNames);
/*     */       } 
/*     */     } else {
/* 111 */       setAttribute("number", Integer.toString(tagNumber));
/* 112 */       setAttribute("name", tagName);
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void initialize() {
/* 117 */     if (this.isInitialized == Boolean.TRUE)
/*     */       return; 
/* 119 */     if (this.isIFD) {
/* 120 */       TIFFDirectory dir = (TIFFDirectory)this.field.getData();
/* 121 */       TIFFField[] fields = dir.getTIFFFields();
/* 122 */       if (fields != null) {
/* 123 */         TIFFTagSet[] tagSets = dir.getTagSets();
/* 124 */         List<TIFFTagSet> tagSetList = Arrays.asList(tagSets);
/* 125 */         int numFields = fields.length;
/* 126 */         for (int i = 0; i < numFields; i++) {
/* 127 */           TIFFField f = fields[i];
/* 128 */           int tagNumber = f.getTagNumber();
/* 129 */           TIFFTag tag = TIFFIFD.getTag(tagNumber, tagSetList);
/*     */           
/* 131 */           Node node = f.getAsNativeNode();
/*     */           
/* 133 */           if (node != null) {
/* 134 */             appendChild(node);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       IIOMetadataNode child;
/* 140 */       int count = this.field.getCount();
/* 141 */       if (this.field.getType() == 7) {
/* 142 */         child = new IIOMetadataNode("TIFFUndefined");
/*     */         
/* 144 */         byte[] data = this.field.getAsBytes();
/* 145 */         StringBuffer sb = new StringBuffer();
/* 146 */         for (int i = 0; i < count; i++) {
/* 147 */           sb.append(Integer.toString(data[i] & 0xFF));
/* 148 */           if (i < count - 1) {
/* 149 */             sb.append(",");
/*     */           }
/*     */         } 
/* 152 */         child.setAttribute("value", sb.toString());
/*     */       } else {
/*     */         
/* 155 */         child = new IIOMetadataNode("TIFF" + TIFFField.getTypeName(this.field.getType()) + "s");
/*     */ 
/*     */         
/* 158 */         TIFFTag tag = this.field.getTag();
/*     */         
/* 160 */         for (int i = 0; i < count; i++) {
/*     */ 
/*     */           
/* 163 */           IIOMetadataNode cchild = new IIOMetadataNode("TIFF" + TIFFField.getTypeName(this.field.getType()));
/*     */           
/* 165 */           cchild.setAttribute("value", this.field.getValueAsString(i));
/* 166 */           if (tag.hasValueNames() && this.field.isIntegral()) {
/* 167 */             int value = this.field.getAsInt(i);
/* 168 */             String name = tag.getValueName(value);
/* 169 */             if (name != null) {
/* 170 */               cchild.setAttribute("description", name);
/*     */             }
/*     */           } 
/*     */           
/* 174 */           child.appendChild(cchild);
/*     */         } 
/*     */       } 
/* 177 */       appendChild(child);
/*     */     } 
/*     */     
/* 180 */     this.isInitialized = Boolean.TRUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Node appendChild(Node newChild) {
/* 186 */     if (newChild == null) {
/* 187 */       throw new IllegalArgumentException("newChild == null!");
/*     */     }
/*     */     
/* 190 */     return super.insertBefore(newChild, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasChildNodes() {
/* 196 */     initialize();
/* 197 */     return super.hasChildNodes();
/*     */   }
/*     */   
/*     */   public int getLength() {
/* 201 */     initialize();
/* 202 */     return super.getLength();
/*     */   }
/*     */   
/*     */   public Node getFirstChild() {
/* 206 */     initialize();
/* 207 */     return super.getFirstChild();
/*     */   }
/*     */   
/*     */   public Node getLastChild() {
/* 211 */     initialize();
/* 212 */     return super.getLastChild();
/*     */   }
/*     */   
/*     */   public Node getPreviousSibling() {
/* 216 */     initialize();
/* 217 */     return super.getPreviousSibling();
/*     */   }
/*     */   
/*     */   public Node getNextSibling() {
/* 221 */     initialize();
/* 222 */     return super.getNextSibling();
/*     */   }
/*     */ 
/*     */   
/*     */   public Node insertBefore(Node newChild, Node refChild) {
/* 227 */     initialize();
/* 228 */     return super.insertBefore(newChild, refChild);
/*     */   }
/*     */ 
/*     */   
/*     */   public Node replaceChild(Node newChild, Node oldChild) {
/* 233 */     initialize();
/* 234 */     return super.replaceChild(newChild, oldChild);
/*     */   }
/*     */   
/*     */   public Node removeChild(Node oldChild) {
/* 238 */     initialize();
/* 239 */     return super.removeChild(oldChild);
/*     */   }
/*     */   
/*     */   public Node cloneNode(boolean deep) {
/* 243 */     initialize();
/* 244 */     return super.cloneNode(deep);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFFieldNode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */