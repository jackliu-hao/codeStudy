/*     */ package org.apache.commons.compress.changes;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipFile;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChangeSetPerformer
/*     */ {
/*     */   private final Set<Change> changes;
/*     */   
/*     */   public ChangeSetPerformer(ChangeSet changeSet) {
/*  52 */     this.changes = changeSet.getChanges();
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
/*     */   public ChangeSetResults perform(ArchiveInputStream in, ArchiveOutputStream out) throws IOException {
/*  72 */     return perform(new ArchiveInputStreamIterator(in), out);
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
/*     */   public ChangeSetResults perform(ZipFile in, ArchiveOutputStream out) throws IOException {
/*  93 */     return perform(new ZipFileIterator(in), out);
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
/*     */   private ChangeSetResults perform(ArchiveEntryIterator entryIterator, ArchiveOutputStream out) throws IOException {
/* 114 */     ChangeSetResults results = new ChangeSetResults();
/*     */     
/* 116 */     Set<Change> workingSet = new LinkedHashSet<>(this.changes);
/*     */     Iterator<Change> it;
/* 118 */     for (it = workingSet.iterator(); it.hasNext(); ) {
/* 119 */       Change change = it.next();
/*     */       
/* 121 */       if (change.type() == 2 && change.isReplaceMode()) {
/* 122 */         copyStream(change.getInput(), out, change.getEntry());
/* 123 */         it.remove();
/* 124 */         results.addedFromChangeSet(change.getEntry().getName());
/*     */       } 
/*     */     } 
/*     */     
/* 128 */     while (entryIterator.hasNext()) {
/* 129 */       ArchiveEntry entry = entryIterator.next();
/* 130 */       boolean copy = true;
/*     */       
/* 132 */       for (Iterator<Change> iterator = workingSet.iterator(); iterator.hasNext(); ) {
/* 133 */         Change change = iterator.next();
/*     */         
/* 135 */         int type = change.type();
/* 136 */         String name = entry.getName();
/* 137 */         if (type == 1 && name != null) {
/* 138 */           if (name.equals(change.targetFile())) {
/* 139 */             copy = false;
/* 140 */             iterator.remove();
/* 141 */             results.deleted(name); break;
/*     */           }  continue;
/*     */         } 
/* 144 */         if (type == 4 && name != null)
/*     */         {
/* 146 */           if (name.startsWith(change.targetFile() + "/")) {
/* 147 */             copy = false;
/* 148 */             results.deleted(name);
/*     */             
/*     */             break;
/*     */           } 
/*     */         }
/*     */       } 
/* 154 */       if (copy && 
/* 155 */         !isDeletedLater(workingSet, entry) && 
/* 156 */         !results.hasBeenAdded(entry.getName())) {
/* 157 */         copyStream(entryIterator.getInputStream(), out, entry);
/* 158 */         results.addedFromStream(entry.getName());
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 163 */     for (it = workingSet.iterator(); it.hasNext(); ) {
/* 164 */       Change change = it.next();
/*     */       
/* 166 */       if (change.type() == 2 && 
/* 167 */         !change.isReplaceMode() && 
/* 168 */         !results.hasBeenAdded(change.getEntry().getName())) {
/* 169 */         copyStream(change.getInput(), out, change.getEntry());
/* 170 */         it.remove();
/* 171 */         results.addedFromChangeSet(change.getEntry().getName());
/*     */       } 
/*     */     } 
/* 174 */     out.finish();
/* 175 */     return results;
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
/*     */   private boolean isDeletedLater(Set<Change> workingSet, ArchiveEntry entry) {
/* 188 */     String source = entry.getName();
/*     */     
/* 190 */     if (!workingSet.isEmpty()) {
/* 191 */       for (Change change : workingSet) {
/* 192 */         int type = change.type();
/* 193 */         String target = change.targetFile();
/* 194 */         if (type == 1 && source.equals(target)) {
/* 195 */           return true;
/*     */         }
/*     */         
/* 198 */         if (type == 4 && source.startsWith(target + "/")) {
/* 199 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 203 */     return false;
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
/*     */   private void copyStream(InputStream in, ArchiveOutputStream out, ArchiveEntry entry) throws IOException {
/* 220 */     out.putArchiveEntry(entry);
/* 221 */     IOUtils.copy(in, (OutputStream)out);
/* 222 */     out.closeArchiveEntry();
/*     */   }
/*     */ 
/*     */   
/*     */   static interface ArchiveEntryIterator
/*     */   {
/*     */     boolean hasNext() throws IOException;
/*     */ 
/*     */     
/*     */     ArchiveEntry next();
/*     */ 
/*     */     
/*     */     InputStream getInputStream() throws IOException;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ArchiveInputStreamIterator
/*     */     implements ArchiveEntryIterator
/*     */   {
/*     */     private final ArchiveInputStream in;
/*     */     private ArchiveEntry next;
/*     */     
/*     */     ArchiveInputStreamIterator(ArchiveInputStream in) {
/* 245 */       this.in = in;
/*     */     }
/*     */     
/*     */     public boolean hasNext() throws IOException {
/* 249 */       return ((this.next = this.in.getNextEntry()) != null);
/*     */     }
/*     */     
/*     */     public ArchiveEntry next() {
/* 253 */       return this.next;
/*     */     }
/*     */     
/*     */     public InputStream getInputStream() {
/* 257 */       return (InputStream)this.in;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ZipFileIterator implements ArchiveEntryIterator {
/*     */     private final ZipFile in;
/*     */     private final Enumeration<ZipArchiveEntry> nestedEnum;
/*     */     private ZipArchiveEntry current;
/*     */     
/*     */     ZipFileIterator(ZipFile in) {
/* 267 */       this.in = in;
/* 268 */       this.nestedEnum = in.getEntriesInPhysicalOrder();
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/* 272 */       return this.nestedEnum.hasMoreElements();
/*     */     }
/*     */     
/*     */     public ArchiveEntry next() {
/* 276 */       this.current = this.nestedEnum.nextElement();
/* 277 */       return (ArchiveEntry)this.current;
/*     */     }
/*     */     
/*     */     public InputStream getInputStream() throws IOException {
/* 281 */       return this.in.getInputStream(this.current);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\changes\ChangeSetPerformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */