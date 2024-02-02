/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import javax.imageio.metadata.IIOMetadataFormat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TIFFMetadataFormat
/*     */   implements IIOMetadataFormat
/*     */ {
/*  57 */   protected Map elementInfoMap = new HashMap<Object, Object>();
/*  58 */   protected Map attrInfoMap = new HashMap<Object, Object>();
/*     */   
/*     */   protected String resourceBaseName;
/*     */   protected String rootName;
/*     */   
/*     */   public String getRootName() {
/*  64 */     return this.rootName;
/*     */   }
/*     */   
/*     */   private String getResource(String key, Locale locale) {
/*  68 */     if (locale == null) {
/*  69 */       locale = Locale.getDefault();
/*     */     }
/*     */     
/*     */     try {
/*  73 */       ResourceBundle bundle = ResourceBundle.getBundle(this.resourceBaseName, locale);
/*  74 */       return bundle.getString(key);
/*  75 */     } catch (MissingResourceException e) {
/*  76 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private TIFFElementInfo getElementInfo(String elementName) {
/*  81 */     if (elementName == null) {
/*  82 */       throw new IllegalArgumentException("elementName == null!");
/*     */     }
/*     */     
/*  85 */     TIFFElementInfo info = (TIFFElementInfo)this.elementInfoMap.get(elementName);
/*  86 */     if (info == null) {
/*  87 */       throw new IllegalArgumentException("No such element: " + elementName);
/*     */     }
/*     */     
/*  90 */     return info;
/*     */   }
/*     */   
/*     */   private TIFFAttrInfo getAttrInfo(String elementName, String attrName) {
/*  94 */     if (elementName == null) {
/*  95 */       throw new IllegalArgumentException("elementName == null!");
/*     */     }
/*  97 */     if (attrName == null) {
/*  98 */       throw new IllegalArgumentException("attrName == null!");
/*     */     }
/* 100 */     String key = elementName + "/" + attrName;
/* 101 */     TIFFAttrInfo info = (TIFFAttrInfo)this.attrInfoMap.get(key);
/* 102 */     if (info == null) {
/* 103 */       throw new IllegalArgumentException("No such attribute: " + key);
/*     */     }
/* 105 */     return info;
/*     */   }
/*     */   
/*     */   public int getElementMinChildren(String elementName) {
/* 109 */     TIFFElementInfo info = getElementInfo(elementName);
/* 110 */     return info.minChildren;
/*     */   }
/*     */   
/*     */   public int getElementMaxChildren(String elementName) {
/* 114 */     TIFFElementInfo info = getElementInfo(elementName);
/* 115 */     return info.maxChildren;
/*     */   }
/*     */   
/*     */   public String getElementDescription(String elementName, Locale locale) {
/* 119 */     if (!this.elementInfoMap.containsKey(elementName)) {
/* 120 */       throw new IllegalArgumentException("No such element: " + elementName);
/*     */     }
/*     */     
/* 123 */     return getResource(elementName, locale);
/*     */   }
/*     */   
/*     */   public int getChildPolicy(String elementName) {
/* 127 */     TIFFElementInfo info = getElementInfo(elementName);
/* 128 */     return info.childPolicy;
/*     */   }
/*     */   
/*     */   public String[] getChildNames(String elementName) {
/* 132 */     TIFFElementInfo info = getElementInfo(elementName);
/* 133 */     return info.childNames;
/*     */   }
/*     */   
/*     */   public String[] getAttributeNames(String elementName) {
/* 137 */     TIFFElementInfo info = getElementInfo(elementName);
/* 138 */     return info.attributeNames;
/*     */   }
/*     */   
/*     */   public int getAttributeValueType(String elementName, String attrName) {
/* 142 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 143 */     return info.valueType;
/*     */   }
/*     */   
/*     */   public int getAttributeDataType(String elementName, String attrName) {
/* 147 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 148 */     return info.dataType;
/*     */   }
/*     */   
/*     */   public boolean isAttributeRequired(String elementName, String attrName) {
/* 152 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 153 */     return info.isRequired;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeDefaultValue(String elementName, String attrName) {
/* 158 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 159 */     return info.defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getAttributeEnumerations(String elementName, String attrName) {
/* 164 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 165 */     return info.enumerations;
/*     */   }
/*     */   
/*     */   public String getAttributeMinValue(String elementName, String attrName) {
/* 169 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 170 */     return info.minValue;
/*     */   }
/*     */   
/*     */   public String getAttributeMaxValue(String elementName, String attrName) {
/* 174 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 175 */     return info.maxValue;
/*     */   }
/*     */   
/*     */   public int getAttributeListMinLength(String elementName, String attrName) {
/* 179 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 180 */     return info.listMinLength;
/*     */   }
/*     */   
/*     */   public int getAttributeListMaxLength(String elementName, String attrName) {
/* 184 */     TIFFAttrInfo info = getAttrInfo(elementName, attrName);
/* 185 */     return info.listMaxLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeDescription(String elementName, String attrName, Locale locale) {
/* 190 */     String key = elementName + "/" + attrName;
/* 191 */     if (!this.attrInfoMap.containsKey(key)) {
/* 192 */       throw new IllegalArgumentException("No such attribute: " + key);
/*     */     }
/* 194 */     return getResource(key, locale);
/*     */   }
/*     */   
/*     */   public int getObjectValueType(String elementName) {
/* 198 */     TIFFElementInfo info = getElementInfo(elementName);
/* 199 */     return info.objectValueType;
/*     */   }
/*     */   
/*     */   public Class getObjectClass(String elementName) {
/* 203 */     TIFFElementInfo info = getElementInfo(elementName);
/* 204 */     if (info.objectValueType == 0) {
/* 205 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 208 */     return info.objectClass;
/*     */   }
/*     */   
/*     */   public Object getObjectDefaultValue(String elementName) {
/* 212 */     TIFFElementInfo info = getElementInfo(elementName);
/* 213 */     if (info.objectValueType == 0) {
/* 214 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 217 */     return info.objectDefaultValue;
/*     */   }
/*     */   
/*     */   public Object[] getObjectEnumerations(String elementName) {
/* 221 */     TIFFElementInfo info = getElementInfo(elementName);
/* 222 */     if (info.objectValueType == 0) {
/* 223 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 226 */     return info.objectEnumerations;
/*     */   }
/*     */   
/*     */   public Comparable getObjectMinValue(String elementName) {
/* 230 */     TIFFElementInfo info = getElementInfo(elementName);
/* 231 */     if (info.objectValueType == 0) {
/* 232 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 235 */     return info.objectMinValue;
/*     */   }
/*     */   
/*     */   public Comparable getObjectMaxValue(String elementName) {
/* 239 */     TIFFElementInfo info = getElementInfo(elementName);
/* 240 */     if (info.objectValueType == 0) {
/* 241 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 244 */     return info.objectMaxValue;
/*     */   }
/*     */   
/*     */   public int getObjectArrayMinLength(String elementName) {
/* 248 */     TIFFElementInfo info = getElementInfo(elementName);
/* 249 */     if (info.objectValueType == 0) {
/* 250 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 253 */     return info.objectArrayMinLength;
/*     */   }
/*     */   
/*     */   public int getObjectArrayMaxLength(String elementName) {
/* 257 */     TIFFElementInfo info = getElementInfo(elementName);
/* 258 */     if (info.objectValueType == 0) {
/* 259 */       throw new IllegalArgumentException("Element cannot contain an object value: " + elementName);
/*     */     }
/*     */     
/* 262 */     return info.objectArrayMaxLength;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFMetadataFormat.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */