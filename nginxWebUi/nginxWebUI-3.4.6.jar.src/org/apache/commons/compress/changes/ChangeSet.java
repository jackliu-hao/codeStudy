/*     */ package org.apache.commons.compress.changes;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ChangeSet
/*     */ {
/*  37 */   private final Set<Change> changes = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void delete(String fileName) {
/*  46 */     addDeletion(new Change(fileName, 1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteDir(String dirName) {
/*  56 */     addDeletion(new Change(dirName, 4));
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
/*     */   public void add(ArchiveEntry pEntry, InputStream pInput) {
/*  68 */     add(pEntry, pInput, true);
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
/*     */   public void add(ArchiveEntry pEntry, InputStream pInput, boolean replace) {
/*  84 */     addAddition(new Change(pEntry, pInput, replace));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addAddition(Change pChange) {
/*  94 */     if (2 != pChange.type() || pChange
/*  95 */       .getInput() == null) {
/*     */       return;
/*     */     }
/*     */     
/*  99 */     if (!this.changes.isEmpty()) {
/* 100 */       for (Iterator<Change> it = this.changes.iterator(); it.hasNext(); ) {
/* 101 */         Change change = it.next();
/* 102 */         if (change.type() == 2 && change
/* 103 */           .getEntry() != null) {
/* 104 */           ArchiveEntry entry = change.getEntry();
/*     */           
/* 106 */           if (entry.equals(pChange.getEntry())) {
/* 107 */             if (pChange.isReplaceMode()) {
/* 108 */               it.remove();
/* 109 */               this.changes.add(pChange);
/*     */             } 
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 117 */     this.changes.add(pChange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addDeletion(Change pChange) {
/* 127 */     if ((1 != pChange.type() && 4 != pChange
/* 128 */       .type()) || pChange
/* 129 */       .targetFile() == null) {
/*     */       return;
/*     */     }
/* 132 */     String source = pChange.targetFile();
/*     */     
/* 134 */     if (source != null && !this.changes.isEmpty()) {
/* 135 */       for (Iterator<Change> it = this.changes.iterator(); it.hasNext(); ) {
/* 136 */         Change change = it.next();
/* 137 */         if (change.type() == 2 && change
/* 138 */           .getEntry() != null) {
/* 139 */           String target = change.getEntry().getName();
/*     */           
/* 141 */           if (target == null) {
/*     */             continue;
/*     */           }
/*     */           
/* 145 */           if ((1 == pChange.type() && source.equals(target)) || (4 == pChange
/* 146 */             .type() && target.matches(source + "/.*"))) {
/* 147 */             it.remove();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/* 152 */     this.changes.add(pChange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Set<Change> getChanges() {
/* 161 */     return new LinkedHashSet<>(this.changes);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\changes\ChangeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */