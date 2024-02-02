/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class Xpp3Dom
/*     */ {
/*     */   protected String name;
/*     */   protected String value;
/*     */   protected Map attributes;
/*     */   protected final List childList;
/*     */   protected final Map childMap;
/*     */   protected Xpp3Dom parent;
/*  48 */   private static final String[] EMPTY_STRING_ARRAY = new String[0];
/*     */   
/*  50 */   private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String CHILDREN_COMBINATION_MERGE = "merge";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String CHILDREN_COMBINATION_APPEND = "append";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
/*     */ 
/*     */   
/*     */   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
/*     */ 
/*     */   
/*     */   public static final String SELF_COMBINATION_OVERRIDE = "override";
/*     */ 
/*     */   
/*     */   public static final String SELF_COMBINATION_MERGE = "merge";
/*     */ 
/*     */   
/*     */   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom(String name) {
/*  83 */     this.name = name;
/*  84 */     this.childList = new ArrayList();
/*  85 */     this.childMap = new HashMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom(Xpp3Dom src) {
/*  93 */     this(src, src.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom(Xpp3Dom src, String name) {
/* 101 */     this.name = name;
/*     */     
/* 103 */     int childCount = src.getChildCount();
/*     */     
/* 105 */     this.childList = new ArrayList(childCount);
/* 106 */     this.childMap = new HashMap(childCount << 1);
/*     */     
/* 108 */     setValue(src.getValue());
/*     */     
/* 110 */     String[] attributeNames = src.getAttributeNames(); int i;
/* 111 */     for (i = 0; i < attributeNames.length; i++) {
/*     */       
/* 113 */       String attributeName = attributeNames[i];
/* 114 */       setAttribute(attributeName, src.getAttribute(attributeName));
/*     */     } 
/*     */     
/* 117 */     for (i = 0; i < childCount; i++)
/*     */     {
/* 119 */       addChild(new Xpp3Dom(src.getChild(i)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 129 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValue() {
/* 138 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) {
/* 143 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getAttributeNames() {
/* 152 */     if (null == this.attributes || this.attributes.isEmpty())
/*     */     {
/* 154 */       return EMPTY_STRING_ARRAY;
/*     */     }
/*     */ 
/*     */     
/* 158 */     return (String[])this.attributes.keySet().toArray((Object[])new String[this.attributes.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAttribute(String name) {
/* 164 */     return (null != this.attributes) ? (String)this.attributes.get(name) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttribute(String name, String value) {
/* 174 */     if (null == value) {
/* 175 */       throw new NullPointerException("Attribute value can not be null");
/*     */     }
/* 177 */     if (null == name) {
/* 178 */       throw new NullPointerException("Attribute name can not be null");
/*     */     }
/* 180 */     if (null == this.attributes)
/*     */     {
/* 182 */       this.attributes = new HashMap();
/*     */     }
/*     */     
/* 185 */     this.attributes.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom getChild(int i) {
/* 194 */     return this.childList.get(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public Xpp3Dom getChild(String name) {
/* 199 */     return (Xpp3Dom)this.childMap.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addChild(Xpp3Dom xpp3Dom) {
/* 204 */     xpp3Dom.setParent(this);
/* 205 */     this.childList.add(xpp3Dom);
/* 206 */     this.childMap.put(xpp3Dom.getName(), xpp3Dom);
/*     */   }
/*     */ 
/*     */   
/*     */   public Xpp3Dom[] getChildren() {
/* 211 */     if (null == this.childList || this.childList.isEmpty())
/*     */     {
/* 213 */       return EMPTY_DOM_ARRAY;
/*     */     }
/*     */ 
/*     */     
/* 217 */     return (Xpp3Dom[])this.childList.toArray((Object[])new Xpp3Dom[this.childList.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom[] getChildren(String name) {
/* 223 */     if (null == this.childList)
/*     */     {
/* 225 */       return EMPTY_DOM_ARRAY;
/*     */     }
/*     */ 
/*     */     
/* 229 */     ArrayList children = new ArrayList();
/* 230 */     int size = this.childList.size();
/*     */     
/* 232 */     for (int i = 0; i < size; i++) {
/*     */       
/* 234 */       Xpp3Dom configuration = this.childList.get(i);
/* 235 */       if (name.equals(configuration.getName()))
/*     */       {
/* 237 */         children.add(configuration);
/*     */       }
/*     */     } 
/*     */     
/* 241 */     return children.<Xpp3Dom>toArray(new Xpp3Dom[children.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChildCount() {
/* 247 */     if (null == this.childList)
/*     */     {
/* 249 */       return 0;
/*     */     }
/*     */     
/* 252 */     return this.childList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChild(int i) {
/* 257 */     Xpp3Dom child = getChild(i);
/* 258 */     this.childMap.values().remove(child);
/* 259 */     this.childList.remove(i);
/*     */     
/* 261 */     child.setParent(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Xpp3Dom getParent() {
/* 270 */     return this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParent(Xpp3Dom parent) {
/* 275 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeToSerializer(String namespace, XmlSerializer serializer) throws IOException {
/* 286 */     SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
/* 287 */     Xpp3DomWriter.write(xmlWriter, this);
/* 288 */     if (xmlWriter.getExceptions().size() > 0)
/*     */     {
/* 290 */       throw (IOException)xmlWriter.getExceptions().get(0);
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
/* 337 */     if (recessive == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 342 */     boolean mergeSelf = true;
/*     */     
/* 344 */     String selfMergeMode = dominant.getAttribute("combine.self");
/*     */     
/* 346 */     if ("override".equals(selfMergeMode))
/*     */     {
/* 348 */       mergeSelf = false;
/*     */     }
/*     */     
/* 351 */     if (mergeSelf) {
/*     */       
/* 353 */       if (isEmpty(dominant.getValue()))
/*     */       {
/* 355 */         dominant.setValue(recessive.getValue());
/*     */       }
/*     */       
/* 358 */       String[] recessiveAttrs = recessive.getAttributeNames();
/* 359 */       for (int i = 0; i < recessiveAttrs.length; i++) {
/*     */         
/* 361 */         String attr = recessiveAttrs[i];
/*     */         
/* 363 */         if (isEmpty(dominant.getAttribute(attr)))
/*     */         {
/* 365 */           dominant.setAttribute(attr, recessive.getAttribute(attr));
/*     */         }
/*     */       } 
/*     */       
/* 369 */       if (recessive.getChildCount() > 0) {
/*     */         
/* 371 */         boolean mergeChildren = true;
/*     */         
/* 373 */         if (childMergeOverride != null) {
/*     */           
/* 375 */           mergeChildren = childMergeOverride.booleanValue();
/*     */         }
/*     */         else {
/*     */           
/* 379 */           String childMergeMode = dominant.getAttribute("combine.children");
/*     */           
/* 381 */           if ("append".equals(childMergeMode))
/*     */           {
/* 383 */             mergeChildren = false;
/*     */           }
/*     */         } 
/*     */         
/* 387 */         if (!mergeChildren) {
/*     */           
/* 389 */           Xpp3Dom[] dominantChildren = dominant.getChildren();
/*     */           
/* 391 */           dominant.childList.clear();
/*     */           int j, recessiveChildCount;
/* 393 */           for (j = 0, recessiveChildCount = recessive.getChildCount(); j < recessiveChildCount; j++) {
/*     */             
/* 395 */             Xpp3Dom recessiveChild = recessive.getChild(j);
/* 396 */             dominant.addChild(new Xpp3Dom(recessiveChild));
/*     */           } 
/*     */ 
/*     */           
/* 400 */           for (j = 0; j < dominantChildren.length; j++)
/*     */           {
/* 402 */             dominant.addChild(dominantChildren[j]);
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 407 */           Map commonChildren = new HashMap();
/*     */           
/* 409 */           for (Iterator it = recessive.childMap.keySet().iterator(); it.hasNext(); ) {
/*     */             
/* 411 */             String childName = it.next();
/* 412 */             Xpp3Dom[] dominantChildren = dominant.getChildren(childName);
/* 413 */             if (dominantChildren.length > 0)
/*     */             {
/* 415 */               commonChildren.put(childName, Arrays.<Xpp3Dom>asList(dominantChildren).iterator());
/*     */             }
/*     */           } 
/*     */           
/* 419 */           for (int j = 0, recessiveChildCount = recessive.getChildCount(); j < recessiveChildCount; j++) {
/*     */             
/* 421 */             Xpp3Dom recessiveChild = recessive.getChild(j);
/* 422 */             Iterator iterator = (Iterator)commonChildren.get(recessiveChild.getName());
/* 423 */             if (iterator == null) {
/*     */               
/* 425 */               dominant.addChild(new Xpp3Dom(recessiveChild));
/*     */             }
/* 427 */             else if (iterator.hasNext()) {
/*     */               
/* 429 */               Xpp3Dom dominantChild = iterator.next();
/* 430 */               mergeIntoXpp3Dom(dominantChild, recessiveChild, childMergeOverride);
/*     */             } 
/*     */           } 
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
/* 451 */     if (dominant != null) {
/*     */       
/* 453 */       mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
/* 454 */       return dominant;
/*     */     } 
/* 456 */     return recessive;
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
/* 472 */     if (dominant != null) {
/*     */       
/* 474 */       mergeIntoXpp3Dom(dominant, recessive, null);
/* 475 */       return dominant;
/*     */     } 
/* 477 */     return recessive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 486 */     if (obj == this)
/*     */     {
/* 488 */       return true;
/*     */     }
/*     */     
/* 491 */     if (!(obj instanceof Xpp3Dom))
/*     */     {
/* 493 */       return false;
/*     */     }
/*     */     
/* 496 */     Xpp3Dom dom = (Xpp3Dom)obj;
/*     */     
/* 498 */     if ((this.name == null) ? (dom.name != null) : !this.name.equals(dom.name))
/*     */     {
/* 500 */       return false;
/*     */     }
/* 502 */     if ((this.value == null) ? (dom.value != null) : !this.value.equals(dom.value))
/*     */     {
/* 504 */       return false;
/*     */     }
/* 506 */     if ((this.attributes == null) ? (dom.attributes != null) : !this.attributes.equals(dom.attributes))
/*     */     {
/* 508 */       return false;
/*     */     }
/* 510 */     if ((this.childList == null) ? (dom.childList != null) : !this.childList.equals(dom.childList))
/*     */     {
/* 512 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 516 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 522 */     int result = 17;
/* 523 */     result = 37 * result + ((this.name != null) ? this.name.hashCode() : 0);
/* 524 */     result = 37 * result + ((this.value != null) ? this.value.hashCode() : 0);
/* 525 */     result = 37 * result + ((this.attributes != null) ? this.attributes.hashCode() : 0);
/* 526 */     result = 37 * result + ((this.childList != null) ? this.childList.hashCode() : 0);
/* 527 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 533 */     StringWriter writer = new StringWriter();
/* 534 */     XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
/* 535 */     Xpp3DomWriter.write(xmlWriter, this);
/* 536 */     return writer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toUnescapedString() {
/* 542 */     StringWriter writer = new StringWriter();
/* 543 */     XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
/* 544 */     Xpp3DomWriter.write(xmlWriter, this, false);
/* 545 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isNotEmpty(String str) {
/* 550 */     return (str != null && str.length() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isEmpty(String str) {
/* 555 */     return (str == null || str.trim().length() == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\Xpp3Dom.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */