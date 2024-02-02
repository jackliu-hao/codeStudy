/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class InputLocation
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*  28 */   private int lineNumber = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   private int columnNumber = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private InputSource source;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<Object, InputLocation> locations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputLocation(int lineNumber, int columnNumber) {
/*  53 */     this.lineNumber = lineNumber;
/*  54 */     this.columnNumber = columnNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputLocation(int lineNumber, int columnNumber, InputSource source) {
/*  59 */     this.lineNumber = lineNumber;
/*  60 */     this.columnNumber = columnNumber;
/*  61 */     this.source = source;
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
/*     */   public InputLocation clone() {
/*     */     try {
/*  78 */       InputLocation copy = (InputLocation)super.clone();
/*     */       
/*  80 */       if (copy.locations != null)
/*     */       {
/*  82 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  85 */       return copy;
/*     */     }
/*  87 */     catch (Exception ex) {
/*     */       
/*  89 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public int getColumnNumber() {
/* 102 */     return this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLineNumber() {
/* 113 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputLocation getLocation(Object key) {
/* 124 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<Object, InputLocation> getLocations() {
/* 134 */     return this.locations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputSource getSource() {
/* 144 */     return this.source;
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
/*     */   public static InputLocation merge(InputLocation target, InputLocation source, boolean sourceDominant) {
/*     */     Map<Object, InputLocation> locations;
/* 157 */     if (source == null)
/*     */     {
/* 159 */       return target;
/*     */     }
/* 161 */     if (target == null)
/*     */     {
/* 163 */       return source;
/*     */     }
/*     */     
/* 166 */     InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
/*     */ 
/*     */ 
/*     */     
/* 170 */     Map<Object, InputLocation> sourceLocations = source.getLocations();
/* 171 */     Map<Object, InputLocation> targetLocations = target.getLocations();
/* 172 */     if (sourceLocations == null) {
/*     */       
/* 174 */       locations = targetLocations;
/*     */     }
/* 176 */     else if (targetLocations == null) {
/*     */       
/* 178 */       locations = sourceLocations;
/*     */     }
/*     */     else {
/*     */       
/* 182 */       locations = new LinkedHashMap<Object, InputLocation>();
/* 183 */       locations.putAll(sourceDominant ? targetLocations : sourceLocations);
/* 184 */       locations.putAll(sourceDominant ? sourceLocations : targetLocations);
/*     */     } 
/* 186 */     result.setLocations(locations);
/*     */     
/* 188 */     return result;
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
/*     */   public static InputLocation merge(InputLocation target, InputLocation source, Collection<Integer> indices) {
/*     */     Map<Object, InputLocation> locations;
/* 201 */     if (source == null)
/*     */     {
/* 203 */       return target;
/*     */     }
/* 205 */     if (target == null)
/*     */     {
/* 207 */       return source;
/*     */     }
/*     */     
/* 210 */     InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
/*     */ 
/*     */ 
/*     */     
/* 214 */     Map<Object, InputLocation> sourceLocations = source.getLocations();
/* 215 */     Map<Object, InputLocation> targetLocations = target.getLocations();
/* 216 */     if (sourceLocations == null) {
/*     */       
/* 218 */       locations = targetLocations;
/*     */     }
/* 220 */     else if (targetLocations == null) {
/*     */       
/* 222 */       locations = sourceLocations;
/*     */     }
/*     */     else {
/*     */       
/* 226 */       locations = new LinkedHashMap<Object, InputLocation>();
/* 227 */       for (Iterator<Integer> it = indices.iterator(); it.hasNext(); ) {
/*     */         InputLocation location;
/*     */         
/* 230 */         Integer index = it.next();
/* 231 */         if (index.intValue() < 0) {
/*     */           
/* 233 */           location = sourceLocations.get(Integer.valueOf(index.intValue() ^ 0xFFFFFFFF));
/*     */         }
/*     */         else {
/*     */           
/* 237 */           location = targetLocations.get(index);
/*     */         } 
/* 239 */         locations.put(Integer.valueOf(locations.size()), location);
/*     */       } 
/*     */     } 
/* 242 */     result.setLocations(locations);
/*     */     
/* 244 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocation(Object key, InputLocation location) {
/* 255 */     if (location != null) {
/*     */       
/* 257 */       if (this.locations == null)
/*     */       {
/* 259 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 261 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocations(Map<Object, InputLocation> locations) {
/* 272 */     this.locations = locations;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\InputLocation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */