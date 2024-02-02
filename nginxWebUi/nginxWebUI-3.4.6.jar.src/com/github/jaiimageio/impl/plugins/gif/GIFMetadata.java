/*     */ package com.github.jaiimageio.impl.plugins.gif;
/*     */ 
/*     */ import javax.imageio.metadata.IIOInvalidTreeException;
/*     */ import javax.imageio.metadata.IIOMetadata;
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
/*     */ abstract class GIFMetadata
/*     */   extends IIOMetadata
/*     */ {
/*     */   static final int UNDEFINED_INTEGER_VALUE = -1;
/*     */   
/*     */   protected static void fatal(Node node, String reason) throws IIOInvalidTreeException {
/*  73 */     throw new IIOInvalidTreeException(reason, node);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getStringAttribute(Node node, String name, String defaultValue, boolean required, String[] range) throws IIOInvalidTreeException {
/*  82 */     Node attr = node.getAttributes().getNamedItem(name);
/*  83 */     if (attr == null) {
/*  84 */       if (!required) {
/*  85 */         return defaultValue;
/*     */       }
/*  87 */       fatal(node, "Required attribute " + name + " not present!");
/*     */     } 
/*     */     
/*  90 */     String value = attr.getNodeValue();
/*     */     
/*  92 */     if (range != null) {
/*  93 */       if (value == null) {
/*  94 */         fatal(node, "Null value for " + node
/*  95 */             .getNodeName() + " attribute " + name + "!");
/*     */       }
/*     */       
/*  98 */       boolean validValue = false;
/*  99 */       int len = range.length;
/* 100 */       for (int i = 0; i < len; i++) {
/* 101 */         if (value.equals(range[i])) {
/* 102 */           validValue = true;
/*     */           break;
/*     */         } 
/*     */       } 
/* 106 */       if (!validValue) {
/* 107 */         fatal(node, "Bad value for " + node
/* 108 */             .getNodeName() + " attribute " + name + "!");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 113 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int getIntAttribute(Node node, String name, int defaultValue, boolean required, boolean bounded, int min, int max) throws IIOInvalidTreeException {
/* 122 */     String value = getStringAttribute(node, name, null, required, null);
/* 123 */     if (value == null || "".equals(value)) {
/* 124 */       return defaultValue;
/*     */     }
/*     */     
/* 127 */     int intValue = defaultValue;
/*     */     try {
/* 129 */       intValue = Integer.parseInt(value);
/* 130 */     } catch (NumberFormatException e) {
/* 131 */       fatal(node, "Bad value for " + node
/* 132 */           .getNodeName() + " attribute " + name + "!");
/*     */     } 
/*     */     
/* 135 */     if (bounded && (intValue < min || intValue > max)) {
/* 136 */       fatal(node, "Bad value for " + node
/* 137 */           .getNodeName() + " attribute " + name + "!");
/*     */     }
/*     */     
/* 140 */     return intValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static float getFloatAttribute(Node node, String name, float defaultValue, boolean required) throws IIOInvalidTreeException {
/* 148 */     String value = getStringAttribute(node, name, null, required, null);
/* 149 */     if (value == null) {
/* 150 */       return defaultValue;
/*     */     }
/* 152 */     return Float.parseFloat(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int getIntAttribute(Node node, String name, boolean bounded, int min, int max) throws IIOInvalidTreeException {
/* 159 */     return getIntAttribute(node, name, -1, true, bounded, min, max);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static float getFloatAttribute(Node node, String name) throws IIOInvalidTreeException {
/* 165 */     return getFloatAttribute(node, name, -1.0F, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean getBooleanAttribute(Node node, String name, boolean defaultValue, boolean required) throws IIOInvalidTreeException {
/* 173 */     Node attr = node.getAttributes().getNamedItem(name);
/* 174 */     if (attr == null) {
/* 175 */       if (!required) {
/* 176 */         return defaultValue;
/*     */       }
/* 178 */       fatal(node, "Required attribute " + name + " not present!");
/*     */     } 
/*     */     
/* 181 */     String value = attr.getNodeValue();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 186 */     if (value.equalsIgnoreCase("TRUE"))
/* 187 */       return true; 
/* 188 */     if (value.equalsIgnoreCase("FALSE")) {
/* 189 */       return false;
/*     */     }
/* 191 */     fatal(node, "Attribute " + name + " must be 'TRUE' or 'FALSE'!");
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean getBooleanAttribute(Node node, String name) throws IIOInvalidTreeException {
/* 199 */     return getBooleanAttribute(node, name, false, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int getEnumeratedAttribute(Node node, String name, String[] legalNames, int defaultValue, boolean required) throws IIOInvalidTreeException {
/* 209 */     Node attr = node.getAttributes().getNamedItem(name);
/* 210 */     if (attr == null) {
/* 211 */       if (!required) {
/* 212 */         return defaultValue;
/*     */       }
/* 214 */       fatal(node, "Required attribute " + name + " not present!");
/*     */     } 
/*     */     
/* 217 */     String value = attr.getNodeValue();
/* 218 */     for (int i = 0; i < legalNames.length; i++) {
/* 219 */       if (value.equals(legalNames[i])) {
/* 220 */         return i;
/*     */       }
/*     */     } 
/*     */     
/* 224 */     fatal(node, "Illegal value for attribute " + name + "!");
/* 225 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int getEnumeratedAttribute(Node node, String name, String[] legalNames) throws IIOInvalidTreeException {
/* 233 */     return getEnumeratedAttribute(node, name, legalNames, -1, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getAttribute(Node node, String name, String defaultValue, boolean required) throws IIOInvalidTreeException {
/* 240 */     Node attr = node.getAttributes().getNamedItem(name);
/* 241 */     if (attr == null) {
/* 242 */       if (!required) {
/* 243 */         return defaultValue;
/*     */       }
/* 245 */       fatal(node, "Required attribute " + name + " not present!");
/*     */     } 
/*     */     
/* 248 */     return attr.getNodeValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getAttribute(Node node, String name) throws IIOInvalidTreeException {
/* 254 */     return getAttribute(node, name, null, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected GIFMetadata(boolean standardMetadataFormatSupported, String nativeMetadataFormatName, String nativeMetadataFormatClassName, String[] extraMetadataFormatNames, String[] extraMetadataFormatClassNames) {
/* 262 */     super(standardMetadataFormatSupported, nativeMetadataFormatName, nativeMetadataFormatClassName, extraMetadataFormatNames, extraMetadataFormatClassNames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
/* 271 */     if (formatName.equals(this.nativeMetadataFormatName)) {
/* 272 */       if (root == null) {
/* 273 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/* 275 */       mergeNativeTree(root);
/* 276 */     } else if (formatName
/* 277 */       .equals("javax_imageio_1.0")) {
/* 278 */       if (root == null) {
/* 279 */         throw new IllegalArgumentException("root == null!");
/*     */       }
/* 281 */       mergeStandardTree(root);
/*     */     } else {
/* 283 */       throw new IllegalArgumentException("Not a recognized format!");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] getColorTable(Node colorTableNode, String entryNodeName, boolean lengthExpected, int expectedLength) throws IIOInvalidTreeException {
/* 292 */     byte[] red = new byte[256];
/* 293 */     byte[] green = new byte[256];
/* 294 */     byte[] blue = new byte[256];
/* 295 */     int maxIndex = -1;
/*     */     
/* 297 */     Node entry = colorTableNode.getFirstChild();
/* 298 */     if (entry == null) {
/* 299 */       fatal(colorTableNode, "Palette has no entries!");
/*     */     }
/*     */     
/* 302 */     while (entry != null) {
/* 303 */       if (!entry.getNodeName().equals(entryNodeName)) {
/* 304 */         fatal(colorTableNode, "Only a " + entryNodeName + " may be a child of a " + entry
/*     */             
/* 306 */             .getNodeName() + "!");
/*     */       }
/*     */       
/* 309 */       int index = getIntAttribute(entry, "index", true, 0, 255);
/* 310 */       if (index > maxIndex) {
/* 311 */         maxIndex = index;
/*     */       }
/* 313 */       red[index] = (byte)getIntAttribute(entry, "red", true, 0, 255);
/* 314 */       green[index] = (byte)getIntAttribute(entry, "green", true, 0, 255);
/* 315 */       blue[index] = (byte)getIntAttribute(entry, "blue", true, 0, 255);
/*     */       
/* 317 */       entry = entry.getNextSibling();
/*     */     } 
/*     */     
/* 320 */     int numEntries = maxIndex + 1;
/*     */     
/* 322 */     if (lengthExpected && numEntries != expectedLength) {
/* 323 */       fatal(colorTableNode, "Unexpected length for palette!");
/*     */     }
/*     */     
/* 326 */     byte[] colorTable = new byte[3 * numEntries];
/* 327 */     for (int i = 0, j = 0; i < numEntries; i++) {
/* 328 */       colorTable[j++] = red[i];
/* 329 */       colorTable[j++] = green[i];
/* 330 */       colorTable[j++] = blue[i];
/*     */     } 
/*     */     
/* 333 */     return colorTable;
/*     */   }
/*     */   
/*     */   protected abstract void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException;
/*     */   
/*     */   protected abstract void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\gif\GIFMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */