/*     */ package com.github.jaiimageio.plugins.tiff;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFTagSet
/*     */ {
/*  73 */   private SortedMap allowedTagsByNumber = new TreeMap<Object, Object>();
/*     */   
/*  75 */   private SortedMap allowedTagsByName = new TreeMap<Object, Object>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TIFFTagSet() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFTagSet(List tags) {
/*  94 */     if (tags == null) {
/*  95 */       throw new IllegalArgumentException("tags == null!");
/*     */     }
/*  97 */     Iterator iter = tags.iterator();
/*  98 */     while (iter.hasNext()) {
/*  99 */       Object o = iter.next();
/* 100 */       if (!(o instanceof TIFFTag)) {
/* 101 */         throw new IllegalArgumentException("tags contains a non-TIFFTag!");
/*     */       }
/*     */       
/* 104 */       TIFFTag tag = (TIFFTag)o;
/*     */       
/* 106 */       this.allowedTagsByNumber.put(new Integer(tag.getNumber()), tag);
/* 107 */       this.allowedTagsByName.put(tag.getName(), tag);
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
/*     */   public TIFFTag getTag(int tagNumber) {
/* 121 */     return (TIFFTag)this.allowedTagsByNumber.get(new Integer(tagNumber));
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
/*     */   public TIFFTag getTag(String tagName) {
/* 137 */     if (tagName == null) {
/* 138 */       throw new IllegalArgumentException("tagName == null!");
/*     */     }
/* 140 */     return (TIFFTag)this.allowedTagsByName.get(tagName);
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
/*     */   public SortedSet getTagNumbers() {
/*     */     SortedSet<?> sortedTagNumbers;
/* 154 */     Set<?> tagNumbers = this.allowedTagsByNumber.keySet();
/*     */     
/* 156 */     if (tagNumbers instanceof SortedSet) {
/* 157 */       sortedTagNumbers = (SortedSet)tagNumbers;
/*     */     } else {
/* 159 */       sortedTagNumbers = new TreeSet(tagNumbers);
/*     */     } 
/*     */     
/* 162 */     return Collections.unmodifiableSortedSet(sortedTagNumbers);
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
/*     */   public SortedSet getTagNames() {
/*     */     SortedSet<?> sortedTagNames;
/* 176 */     Set<?> tagNames = this.allowedTagsByName.keySet();
/*     */     
/* 178 */     if (tagNames instanceof SortedSet) {
/* 179 */       sortedTagNames = (SortedSet)tagNames;
/*     */     } else {
/* 181 */       sortedTagNames = new TreeSet(tagNames);
/*     */     } 
/*     */     
/* 184 */     return Collections.unmodifiableSortedSet(sortedTagNames);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\plugins\tiff\TIFFTagSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */