/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PatternSet
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private List<String> includes;
/*     */   private List<String> excludes;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public void addExclude(String string) {
/*  51 */     getExcludes().add(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addInclude(String string) {
/*  61 */     getIncludes().add(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PatternSet clone() {
/*     */     try {
/*  73 */       PatternSet copy = (PatternSet)super.clone();
/*     */       
/*  75 */       if (this.includes != null) {
/*     */         
/*  77 */         copy.includes = new ArrayList<String>();
/*  78 */         copy.includes.addAll(this.includes);
/*     */       } 
/*     */       
/*  81 */       if (this.excludes != null) {
/*     */         
/*  83 */         copy.excludes = new ArrayList<String>();
/*  84 */         copy.excludes.addAll(this.excludes);
/*     */       } 
/*     */       
/*  87 */       if (copy.locations != null)
/*     */       {
/*  89 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  92 */       return copy;
/*     */     }
/*  94 */     catch (Exception ex) {
/*     */       
/*  96 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public List<String> getExcludes() {
/* 108 */     if (this.excludes == null)
/*     */     {
/* 110 */       this.excludes = new ArrayList<String>();
/*     */     }
/*     */     
/* 113 */     return this.excludes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getIncludes() {
/* 123 */     if (this.includes == null)
/*     */     {
/* 125 */       this.includes = new ArrayList<String>();
/*     */     }
/*     */     
/* 128 */     return this.includes;
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
/* 139 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeExclude(String string) {
/* 149 */     getExcludes().remove(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeInclude(String string) {
/* 159 */     getIncludes().remove(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExcludes(List<String> excludes) {
/* 170 */     this.excludes = excludes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncludes(List<String> includes) {
/* 181 */     this.includes = includes;
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
/* 192 */     if (location != null) {
/*     */       
/* 194 */       if (this.locations == null)
/*     */       {
/* 196 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 198 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 209 */     StringBuilder sb = new StringBuilder(128);
/*     */     
/* 211 */     sb.append("PatternSet [includes: {");
/* 212 */     for (Iterator<String> iterator1 = getIncludes().iterator(); iterator1.hasNext(); ) {
/*     */       
/* 214 */       String str = iterator1.next();
/* 215 */       sb.append(str).append(", ");
/*     */     } 
/* 217 */     if (sb.substring(sb.length() - 2).equals(", ")) sb.delete(sb.length() - 2, sb.length());
/*     */     
/* 219 */     sb.append("}, excludes: {");
/* 220 */     for (Iterator<String> i = getExcludes().iterator(); i.hasNext(); ) {
/*     */       
/* 222 */       String str = i.next();
/* 223 */       sb.append(str).append(", ");
/*     */     } 
/* 225 */     if (sb.substring(sb.length() - 2).equals(", ")) sb.delete(sb.length() - 2, sb.length());
/*     */     
/* 227 */     sb.append("}]");
/* 228 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\PatternSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */