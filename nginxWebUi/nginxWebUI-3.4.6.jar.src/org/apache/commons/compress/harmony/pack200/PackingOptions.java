/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ public class PackingOptions
/*     */ {
/*     */   public static final String STRIP = "strip";
/*     */   public static final String ERROR = "error";
/*     */   public static final String PASS = "pass";
/*     */   public static final String KEEP = "keep";
/*     */   private boolean gzip = true;
/*     */   private boolean stripDebug = false;
/*     */   private boolean keepFileOrder = true;
/*  41 */   private long segmentLimit = 1000000L;
/*  42 */   private int effort = 5;
/*  43 */   private String deflateHint = "keep";
/*  44 */   private String modificationTime = "keep";
/*     */   private List passFiles;
/*  46 */   private String unknownAttributeAction = "pass";
/*     */   
/*     */   private Map classAttributeActions;
/*     */   private Map fieldAttributeActions;
/*     */   private Map methodAttributeActions;
/*     */   private Map codeAttributeActions;
/*     */   private boolean verbose = false;
/*     */   private String logFile;
/*     */   private Attribute[] unknownAttributeTypes;
/*     */   
/*     */   public boolean isGzip() {
/*  57 */     return this.gzip;
/*     */   }
/*     */   
/*     */   public void setGzip(boolean gzip) {
/*  61 */     this.gzip = gzip;
/*     */   }
/*     */   
/*     */   public boolean isStripDebug() {
/*  65 */     return this.stripDebug;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStripDebug(boolean stripDebug) {
/*  76 */     this.stripDebug = stripDebug;
/*     */   }
/*     */   
/*     */   public boolean isKeepFileOrder() {
/*  80 */     return this.keepFileOrder;
/*     */   }
/*     */   
/*     */   public void setKeepFileOrder(boolean keepFileOrder) {
/*  84 */     this.keepFileOrder = keepFileOrder;
/*     */   }
/*     */   
/*     */   public long getSegmentLimit() {
/*  88 */     return this.segmentLimit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSegmentLimit(long segmentLimit) {
/*  97 */     this.segmentLimit = segmentLimit;
/*     */   }
/*     */   
/*     */   public int getEffort() {
/* 101 */     return this.effort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEffort(int effort) {
/* 110 */     this.effort = effort;
/*     */   }
/*     */   
/*     */   public String getDeflateHint() {
/* 114 */     return this.deflateHint;
/*     */   }
/*     */   
/*     */   public boolean isKeepDeflateHint() {
/* 118 */     return "keep".equals(this.deflateHint);
/*     */   }
/*     */   
/*     */   public void setDeflateHint(String deflateHint) {
/* 122 */     if (!"keep".equals(deflateHint) && !"true".equals(deflateHint) && !"false".equals(deflateHint)) {
/* 123 */       throw new IllegalArgumentException("Bad argument: -H " + deflateHint + " ? deflate hint should be either true, false or keep (default)");
/*     */     }
/*     */     
/* 126 */     this.deflateHint = deflateHint;
/*     */   }
/*     */   
/*     */   public String getModificationTime() {
/* 130 */     return this.modificationTime;
/*     */   }
/*     */   
/*     */   public void setModificationTime(String modificationTime) {
/* 134 */     if (!"keep".equals(modificationTime) && !"latest".equals(modificationTime)) {
/* 135 */       throw new IllegalArgumentException("Bad argument: -m " + modificationTime + " ? transmit modtimes should be either latest or keep (default)");
/*     */     }
/*     */     
/* 138 */     this.modificationTime = modificationTime;
/*     */   }
/*     */   
/*     */   public boolean isPassFile(String passFileName) {
/* 142 */     if (this.passFiles != null) {
/* 143 */       for (Iterator<String> iterator = this.passFiles.iterator(); iterator.hasNext(); ) {
/* 144 */         String pass = iterator.next();
/* 145 */         if (passFileName.equals(pass)) {
/* 146 */           return true;
/*     */         }
/* 148 */         if (!pass.endsWith(".class")) {
/*     */           
/* 150 */           if (!pass.endsWith("/"))
/*     */           {
/*     */ 
/*     */             
/* 154 */             pass = pass + "/";
/*     */           }
/* 156 */           return passFileName.startsWith(pass);
/*     */         } 
/*     */       } 
/*     */     }
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPassFile(String passFileName) {
/* 170 */     if (this.passFiles == null) {
/* 171 */       this.passFiles = new ArrayList();
/*     */     }
/* 173 */     String fileSeparator = System.getProperty("file.separator");
/* 174 */     if (fileSeparator.equals("\\"))
/*     */     {
/* 176 */       fileSeparator = fileSeparator + "\\";
/*     */     }
/* 178 */     passFileName = passFileName.replaceAll(fileSeparator, "/");
/* 179 */     this.passFiles.add(passFileName);
/*     */   }
/*     */   
/*     */   public void removePassFile(String passFileName) {
/* 183 */     this.passFiles.remove(passFileName);
/*     */   }
/*     */   
/*     */   public String getUnknownAttributeAction() {
/* 187 */     return this.unknownAttributeAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUnknownAttributeAction(String unknownAttributeAction) {
/* 196 */     this.unknownAttributeAction = unknownAttributeAction;
/* 197 */     if (!"pass".equals(unknownAttributeAction) && !"error".equals(unknownAttributeAction) && 
/* 198 */       !"strip".equals(unknownAttributeAction)) {
/* 199 */       throw new RuntimeException("Incorrect option for -U, " + unknownAttributeAction);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addClassAttributeAction(String attributeName, String action) {
/* 204 */     if (this.classAttributeActions == null) {
/* 205 */       this.classAttributeActions = new HashMap<>();
/*     */     }
/* 207 */     this.classAttributeActions.put(attributeName, action);
/*     */   }
/*     */   
/*     */   public void addFieldAttributeAction(String attributeName, String action) {
/* 211 */     if (this.fieldAttributeActions == null) {
/* 212 */       this.fieldAttributeActions = new HashMap<>();
/*     */     }
/* 214 */     this.fieldAttributeActions.put(attributeName, action);
/*     */   }
/*     */   
/*     */   public void addMethodAttributeAction(String attributeName, String action) {
/* 218 */     if (this.methodAttributeActions == null) {
/* 219 */       this.methodAttributeActions = new HashMap<>();
/*     */     }
/* 221 */     this.methodAttributeActions.put(attributeName, action);
/*     */   }
/*     */   
/*     */   public void addCodeAttributeAction(String attributeName, String action) {
/* 225 */     if (this.codeAttributeActions == null) {
/* 226 */       this.codeAttributeActions = new HashMap<>();
/*     */     }
/* 228 */     this.codeAttributeActions.put(attributeName, action);
/*     */   }
/*     */   
/*     */   public boolean isVerbose() {
/* 232 */     return this.verbose;
/*     */   }
/*     */   
/*     */   public void setVerbose(boolean verbose) {
/* 236 */     this.verbose = verbose;
/*     */   }
/*     */   
/*     */   public void setQuiet(boolean quiet) {
/* 240 */     this.verbose = !quiet;
/*     */   }
/*     */   
/*     */   public String getLogFile() {
/* 244 */     return this.logFile;
/*     */   }
/*     */   
/*     */   public void setLogFile(String logFile) {
/* 248 */     this.logFile = logFile;
/*     */   }
/*     */   
/*     */   private void addOrUpdateAttributeActions(List<NewAttribute> prototypes, Map attributeActions, int tag) {
/* 252 */     if (attributeActions != null && attributeActions.size() > 0)
/*     */     {
/*     */ 
/*     */       
/* 256 */       for (Iterator<String> iteratorI = attributeActions.keySet().iterator(); iteratorI.hasNext(); ) {
/* 257 */         String name = iteratorI.next();
/* 258 */         String action = (String)attributeActions.get(name);
/* 259 */         boolean prototypeExists = false;
/* 260 */         for (Iterator<NewAttribute> iteratorJ = prototypes.iterator(); iteratorJ.hasNext(); ) {
/* 261 */           NewAttribute newAttribute = iteratorJ.next();
/* 262 */           if (newAttribute.type.equals(name)) {
/*     */             
/* 264 */             newAttribute.addContext(tag);
/* 265 */             prototypeExists = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 270 */         if (!prototypeExists) {
/* 271 */           NewAttribute newAttribute; if ("error".equals(action)) {
/* 272 */             newAttribute = new NewAttribute.ErrorAttribute(name, tag);
/* 273 */           } else if ("strip".equals(action)) {
/* 274 */             newAttribute = new NewAttribute.StripAttribute(name, tag);
/* 275 */           } else if ("pass".equals(action)) {
/* 276 */             newAttribute = new NewAttribute.PassAttribute(name, tag);
/*     */           } else {
/* 278 */             newAttribute = new NewAttribute(name, action, tag);
/*     */           } 
/* 280 */           prototypes.add(newAttribute);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public Attribute[] getUnknownAttributePrototypes() {
/* 287 */     if (this.unknownAttributeTypes == null) {
/* 288 */       List prototypes = new ArrayList();
/* 289 */       addOrUpdateAttributeActions(prototypes, this.classAttributeActions, 0);
/*     */       
/* 291 */       addOrUpdateAttributeActions(prototypes, this.methodAttributeActions, 2);
/*     */       
/* 293 */       addOrUpdateAttributeActions(prototypes, this.fieldAttributeActions, 1);
/*     */       
/* 295 */       addOrUpdateAttributeActions(prototypes, this.codeAttributeActions, 3);
/*     */       
/* 297 */       this.unknownAttributeTypes = (Attribute[])prototypes.toArray((Object[])new Attribute[0]);
/*     */     } 
/* 299 */     return this.unknownAttributeTypes;
/*     */   }
/*     */   
/*     */   public String getUnknownClassAttributeAction(String type) {
/* 303 */     if (this.classAttributeActions == null) {
/* 304 */       return this.unknownAttributeAction;
/*     */     }
/* 306 */     String action = (String)this.classAttributeActions.get(type);
/* 307 */     if (action == null) {
/* 308 */       action = this.unknownAttributeAction;
/*     */     }
/* 310 */     return action;
/*     */   }
/*     */   
/*     */   public String getUnknownMethodAttributeAction(String type) {
/* 314 */     if (this.methodAttributeActions == null) {
/* 315 */       return this.unknownAttributeAction;
/*     */     }
/* 317 */     String action = (String)this.methodAttributeActions.get(type);
/* 318 */     if (action == null) {
/* 319 */       action = this.unknownAttributeAction;
/*     */     }
/* 321 */     return action;
/*     */   }
/*     */   
/*     */   public String getUnknownFieldAttributeAction(String type) {
/* 325 */     if (this.fieldAttributeActions == null) {
/* 326 */       return this.unknownAttributeAction;
/*     */     }
/* 328 */     String action = (String)this.fieldAttributeActions.get(type);
/* 329 */     if (action == null) {
/* 330 */       action = this.unknownAttributeAction;
/*     */     }
/* 332 */     return action;
/*     */   }
/*     */   
/*     */   public String getUnknownCodeAttributeAction(String type) {
/* 336 */     if (this.codeAttributeActions == null) {
/* 337 */       return this.unknownAttributeAction;
/*     */     }
/* 339 */     String action = (String)this.codeAttributeActions.get(type);
/* 340 */     if (action == null) {
/* 341 */       action = this.unknownAttributeAction;
/*     */     }
/* 343 */     return action;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\PackingOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */