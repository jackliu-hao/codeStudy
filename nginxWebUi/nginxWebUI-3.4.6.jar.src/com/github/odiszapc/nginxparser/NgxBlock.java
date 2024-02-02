/*     */ package com.github.odiszapc.nginxparser;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NgxBlock
/*     */   extends NgxAbstractEntry
/*     */   implements Iterable<NgxEntry>
/*     */ {
/*     */   private Collection<NgxEntry> entries;
/*     */   
/*     */   public NgxBlock() {
/*  27 */     super(new String[0]);
/*  28 */     this.entries = new ArrayList<NgxEntry>();
/*     */   }
/*     */   public Collection<NgxEntry> getEntries() {
/*  31 */     return this.entries;
/*     */   }
/*     */   
/*     */   public void addEntry(NgxEntry entry) {
/*  35 */     this.entries.add(entry);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  40 */     return super.toString() + " {";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<NgxEntry> iterator() {
/*  46 */     return getEntries().iterator();
/*     */   }
/*     */   
/*     */   public void remove(NgxEntry itemToRemove) {
/*  50 */     if (null == itemToRemove) {
/*  51 */       throw new NullPointerException("Item can not be null");
/*     */     }
/*  53 */     Iterator<NgxEntry> it = this.entries.iterator();
/*  54 */     while (it.hasNext()) {
/*  55 */       NgxBlock block; NgxEntry entry = it.next();
/*  56 */       switch (NgxEntryType.fromClass((Class)entry.getClass())) {
/*     */         case PARAM:
/*  58 */           if (entry.equals(itemToRemove)) {
/*  59 */             it.remove();
/*     */           }
/*     */         case BLOCK:
/*  62 */           if (entry.equals(itemToRemove)) {
/*  63 */             it.remove(); continue;
/*     */           } 
/*  65 */           block = (NgxBlock)entry;
/*  66 */           block.remove(itemToRemove);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAll(Iterable<NgxEntry> itemsToRemove) {
/*  74 */     if (null == itemsToRemove)
/*  75 */       throw new NullPointerException("Items can not be null"); 
/*  76 */     for (NgxEntry itemToRemove : itemsToRemove) {
/*  77 */       remove(itemToRemove);
/*     */     }
/*     */   }
/*     */   
/*     */   public <T extends NgxEntry> T find(Class<T> clazz, String... params) {
/*  82 */     List<NgxEntry> all = findAll(clazz, new ArrayList<NgxEntry>(), params);
/*  83 */     if (all.isEmpty()) {
/*  84 */       return null;
/*     */     }
/*  86 */     return (T)all.get(0);
/*     */   }
/*     */   
/*     */   public NgxBlock findBlock(String... params) {
/*  90 */     NgxEntry entry = find((Class)NgxConfig.BLOCK, params);
/*  91 */     if (null == entry)
/*  92 */       return null; 
/*  93 */     return (NgxBlock)entry;
/*     */   }
/*     */   
/*     */   public NgxParam findParam(String... params) {
/*  97 */     NgxEntry entry = find((Class)NgxConfig.PARAM, params);
/*  98 */     if (null == entry)
/*  99 */       return null; 
/* 100 */     return (NgxParam)entry;
/*     */   }
/*     */   
/*     */   public <T extends NgxEntry> List<NgxEntry> findAll(Class<T> clazz, String... params) {
/* 104 */     return findAll(clazz, new ArrayList<NgxEntry>(), params);
/*     */   }
/*     */   
/*     */   public <T extends NgxEntry> List<NgxEntry> findAll(Class<T> clazz, List<NgxEntry> result, String... params) {
/* 108 */     List<NgxEntry> res = new ArrayList<NgxEntry>();
/*     */     
/* 110 */     if (0 == params.length) {
/* 111 */       return res;
/*     */     }
/*     */     
/* 114 */     String head = params[0];
/* 115 */     String[] tail = (params.length > 1) ? Arrays.<String>copyOfRange(params, 1, params.length) : new String[0];
/*     */     
/* 117 */     for (NgxEntry entry : getEntries()) {
/* 118 */       NgxParam param; NgxBlock block; switch (NgxEntryType.fromClass((Class)entry.getClass())) {
/*     */         case PARAM:
/* 120 */           param = (NgxParam)entry;
/* 121 */           if (param.getName().equals(head) && param.getClass() == clazz) {
/* 122 */             res.add(param);
/*     */           }
/*     */ 
/*     */         
/*     */         case BLOCK:
/* 127 */           block = (NgxBlock)entry;
/* 128 */           if (tail.length > 0) {
/* 129 */             if (block.getName().equals(head))
/* 130 */               res.addAll(block.findAll(clazz, result, tail)); 
/*     */             continue;
/*     */           } 
/* 133 */           if (block.getName().equals(head) && clazz.equals(NgxBlock.class)) {
/* 134 */             res.add(block);
/*     */           }
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 141 */     return res;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */