/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Xpp3DomUtils
/*     */ {
/*     */   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
/*     */   public static final String CHILDREN_COMBINATION_MERGE = "merge";
/*     */   public static final String CHILDREN_COMBINATION_APPEND = "append";
/*     */   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
/*     */   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
/*     */   public static final String SELF_COMBINATION_OVERRIDE = "override";
/*     */   public static final String SELF_COMBINATION_MERGE = "merge";
/*     */   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
/*     */   
/*     */   public void writeToSerializer(String namespace, XmlSerializer serializer, Xpp3Dom dom) throws IOException {
/*  59 */     SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
/*  60 */     Xpp3DomWriter.write(xmlWriter, dom);
/*  61 */     if (xmlWriter.getExceptions().size() > 0)
/*     */     {
/*  63 */       throw (IOException)xmlWriter.getExceptions().get(0);
/*     */     }
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
/*     */   private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
/* 110 */     if (recessive == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 115 */     boolean mergeSelf = true;
/*     */     
/* 117 */     String selfMergeMode = dominant.getAttribute("combine.self");
/*     */     
/* 119 */     if (isNotEmpty(selfMergeMode) && "override".equals(selfMergeMode))
/*     */     {
/* 121 */       mergeSelf = false;
/*     */     }
/*     */     
/* 124 */     if (mergeSelf) {
/*     */       
/* 126 */       if (isEmpty(dominant.getValue()))
/*     */       {
/* 128 */         dominant.setValue(recessive.getValue());
/*     */       }
/*     */       
/* 131 */       String[] recessiveAttrs = recessive.getAttributeNames();
/* 132 */       for (int i = 0; i < recessiveAttrs.length; i++) {
/*     */         
/* 134 */         String attr = recessiveAttrs[i];
/*     */         
/* 136 */         if (isEmpty(dominant.getAttribute(attr)))
/*     */         {
/* 138 */           dominant.setAttribute(attr, recessive.getAttribute(attr));
/*     */         }
/*     */       } 
/*     */       
/* 142 */       boolean mergeChildren = true;
/*     */       
/* 144 */       if (childMergeOverride != null) {
/*     */         
/* 146 */         mergeChildren = childMergeOverride.booleanValue();
/*     */       }
/*     */       else {
/*     */         
/* 150 */         String childMergeMode = dominant.getAttribute("combine.children");
/*     */         
/* 152 */         if (isNotEmpty(childMergeMode) && "append".equals(childMergeMode))
/*     */         {
/* 154 */           mergeChildren = false;
/*     */         }
/*     */       } 
/*     */       
/* 158 */       Xpp3Dom[] children = recessive.getChildren();
/* 159 */       for (int j = 0; j < children.length; j++) {
/*     */         
/* 161 */         Xpp3Dom child = children[j];
/* 162 */         Xpp3Dom childDom = dominant.getChild(child.getName());
/* 163 */         if (mergeChildren && childDom != null) {
/*     */           
/* 165 */           mergeIntoXpp3Dom(childDom, child, childMergeOverride);
/*     */         }
/*     */         else {
/*     */           
/* 169 */           dominant.addChild(new Xpp3Dom(child));
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
/* 188 */     if (dominant != null) {
/*     */       
/* 190 */       mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
/* 191 */       return dominant;
/*     */     } 
/* 193 */     return recessive;
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
/*     */   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive) {
/* 209 */     if (dominant != null) {
/*     */       
/* 211 */       mergeIntoXpp3Dom(dominant, recessive, null);
/* 212 */       return dominant;
/*     */     } 
/* 214 */     return recessive;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNotEmpty(String str) {
/* 219 */     return (str != null && str.length() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isEmpty(String str) {
/* 224 */     return (str == null || str.trim().length() == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\Xpp3DomUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */