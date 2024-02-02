/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.slf4j.Marker;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicMarker
/*     */   implements Marker
/*     */ {
/*     */   private static final long serialVersionUID = -2849567615646933777L;
/*     */   private final String name;
/*  43 */   private List<Marker> referenceList = new CopyOnWriteArrayList<Marker>();
/*     */   
/*     */   BasicMarker(String name) {
/*  46 */     if (name == null) {
/*  47 */       throw new IllegalArgumentException("A marker name cannot be null");
/*     */     }
/*  49 */     this.name = name;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  53 */     return this.name;
/*     */   }
/*     */   
/*     */   public void add(Marker reference) {
/*  57 */     if (reference == null) {
/*  58 */       throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
/*     */     }
/*     */ 
/*     */     
/*  62 */     if (contains(reference)) {
/*     */       return;
/*     */     }
/*  65 */     if (reference.contains(this)) {
/*     */       return;
/*     */     }
/*     */     
/*  69 */     this.referenceList.add(reference);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasReferences() {
/*  74 */     return (this.referenceList.size() > 0);
/*     */   }
/*     */   
/*     */   public boolean hasChildren() {
/*  78 */     return hasReferences();
/*     */   }
/*     */   
/*     */   public Iterator<Marker> iterator() {
/*  82 */     return this.referenceList.iterator();
/*     */   }
/*     */   
/*     */   public boolean remove(Marker referenceToRemove) {
/*  86 */     return this.referenceList.remove(referenceToRemove);
/*     */   }
/*     */   
/*     */   public boolean contains(Marker other) {
/*  90 */     if (other == null) {
/*  91 */       throw new IllegalArgumentException("Other cannot be null");
/*     */     }
/*     */     
/*  94 */     if (equals(other)) {
/*  95 */       return true;
/*     */     }
/*     */     
/*  98 */     if (hasReferences()) {
/*  99 */       for (Marker ref : this.referenceList) {
/* 100 */         if (ref.contains(other)) {
/* 101 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(String name) {
/* 112 */     if (name == null) {
/* 113 */       throw new IllegalArgumentException("Other cannot be null");
/*     */     }
/*     */     
/* 116 */     if (this.name.equals(name)) {
/* 117 */       return true;
/*     */     }
/*     */     
/* 120 */     if (hasReferences()) {
/* 121 */       for (Marker ref : this.referenceList) {
/* 122 */         if (ref.contains(name)) {
/* 123 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 127 */     return false;
/*     */   }
/*     */   
/* 130 */   private static String OPEN = "[ ";
/* 131 */   private static String CLOSE = " ]";
/* 132 */   private static String SEP = ", ";
/*     */   
/*     */   public boolean equals(Object obj) {
/* 135 */     if (this == obj)
/* 136 */       return true; 
/* 137 */     if (obj == null)
/* 138 */       return false; 
/* 139 */     if (!(obj instanceof Marker)) {
/* 140 */       return false;
/*     */     }
/* 142 */     Marker other = (Marker)obj;
/* 143 */     return this.name.equals(other.getName());
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 147 */     return this.name.hashCode();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 151 */     if (!hasReferences()) {
/* 152 */       return getName();
/*     */     }
/* 154 */     Iterator<Marker> it = iterator();
/*     */     
/* 156 */     StringBuilder sb = new StringBuilder(getName());
/* 157 */     sb.append(' ').append(OPEN);
/* 158 */     while (it.hasNext()) {
/* 159 */       Marker reference = it.next();
/* 160 */       sb.append(reference.getName());
/* 161 */       if (it.hasNext()) {
/* 162 */         sb.append(SEP);
/*     */       }
/*     */     } 
/* 165 */     sb.append(CLOSE);
/*     */     
/* 167 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\BasicMarker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */