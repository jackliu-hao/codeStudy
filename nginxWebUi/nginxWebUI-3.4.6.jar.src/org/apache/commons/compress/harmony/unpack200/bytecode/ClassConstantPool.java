/*     */ package org.apache.commons.compress.harmony.unpack200.bytecode;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeSet;
/*     */ import org.apache.commons.compress.harmony.unpack200.Segment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassConstantPool
/*     */ {
/*  35 */   protected HashSet entriesContainsSet = new HashSet();
/*  36 */   protected HashSet othersContainsSet = new HashSet();
/*     */   
/*  38 */   private final HashSet mustStartClassPool = new HashSet();
/*     */   
/*     */   protected Map indexCache;
/*     */   
/*  42 */   private final List others = new ArrayList(500);
/*  43 */   private final List entries = new ArrayList(500);
/*     */   
/*     */   private boolean resolved;
/*     */   
/*     */   public ClassFileEntry add(ClassFileEntry entry) {
/*  48 */     if (entry instanceof ByteCode) {
/*  49 */       return null;
/*     */     }
/*  51 */     if (entry instanceof ConstantPoolEntry) {
/*  52 */       if (this.entriesContainsSet.add(entry)) {
/*  53 */         this.entries.add(entry);
/*     */       }
/*  55 */     } else if (this.othersContainsSet.add(entry)) {
/*  56 */       this.others.add(entry);
/*     */     } 
/*     */     
/*  59 */     return entry;
/*     */   }
/*     */   
/*     */   public void addNestedEntries() {
/*  63 */     boolean added = true;
/*     */ 
/*     */     
/*  66 */     ArrayList<ClassFileEntry> parents = new ArrayList(512);
/*  67 */     ArrayList<? extends ClassFileEntry> children = new ArrayList(512);
/*     */ 
/*     */     
/*  70 */     parents.addAll(this.entries);
/*  71 */     parents.addAll(this.others);
/*     */ 
/*     */ 
/*     */     
/*  75 */     while (added || parents.size() > 0) {
/*     */       
/*  77 */       children.clear();
/*     */       
/*  79 */       int entriesOriginalSize = this.entries.size();
/*  80 */       int othersOriginalSize = this.others.size();
/*     */ 
/*     */ 
/*     */       
/*  84 */       for (int indexParents = 0; indexParents < parents.size(); indexParents++) {
/*  85 */         ClassFileEntry entry = parents.get(indexParents);
/*     */ 
/*     */         
/*  88 */         ClassFileEntry[] entryChildren = entry.getNestedClassFileEntries();
/*  89 */         children.addAll(Arrays.asList(entryChildren));
/*     */         
/*  91 */         boolean isAtStart = (entry instanceof ByteCode && ((ByteCode)entry).nestedMustStartClassPool());
/*     */         
/*  93 */         if (isAtStart) {
/*  94 */           this.mustStartClassPool.addAll(Arrays.asList(entryChildren));
/*     */         }
/*     */ 
/*     */         
/*  98 */         add(entry);
/*     */       } 
/*     */       
/* 101 */       added = (this.entries.size() != entriesOriginalSize || this.others.size() != othersOriginalSize);
/*     */ 
/*     */ 
/*     */       
/* 105 */       parents.clear();
/* 106 */       parents.addAll(children);
/*     */     } 
/*     */   }
/*     */   
/*     */   public int indexOf(ClassFileEntry entry) {
/* 111 */     if (!this.resolved) {
/* 112 */       throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
/*     */     }
/* 114 */     if (null == this.indexCache) {
/* 115 */       throw new IllegalStateException("Index cache is not initialized!");
/*     */     }
/* 117 */     Integer entryIndex = (Integer)this.indexCache.get(entry);
/*     */     
/* 119 */     if (entryIndex != null) {
/* 120 */       return entryIndex.intValue() + 1;
/*     */     }
/* 122 */     return -1;
/*     */   }
/*     */   
/*     */   public int size() {
/* 126 */     return this.entries.size();
/*     */   }
/*     */   
/*     */   public ClassFileEntry get(int i) {
/* 130 */     if (!this.resolved) {
/* 131 */       throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
/*     */     }
/* 133 */     return this.entries.get(--i);
/*     */   }
/*     */   
/*     */   public void resolve(Segment segment) {
/* 137 */     initialSort();
/* 138 */     sortClassPool();
/*     */     
/* 140 */     this.resolved = true;
/*     */     int it;
/* 142 */     for (it = 0; it < this.entries.size(); it++) {
/* 143 */       ClassFileEntry entry = this.entries.get(it);
/* 144 */       entry.resolve(this);
/*     */     } 
/*     */     
/* 147 */     for (it = 0; it < this.others.size(); it++) {
/* 148 */       ClassFileEntry entry = this.others.get(it);
/* 149 */       entry.resolve(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void initialSort() {
/* 155 */     TreeSet<ConstantPoolEntry> inCpAll = new TreeSet((arg0, arg1) -> ((ConstantPoolEntry)arg0).getGlobalIndex() - ((ConstantPoolEntry)arg1).getGlobalIndex());
/*     */     
/* 157 */     TreeSet<ConstantPoolEntry> cpUtf8sNotInCpAll = new TreeSet((arg0, arg1) -> ((CPUTF8)arg0).underlyingString().compareTo(((CPUTF8)arg1).underlyingString()));
/*     */     
/* 159 */     TreeSet<ConstantPoolEntry> cpClassesNotInCpAll = new TreeSet((arg0, arg1) -> ((CPClass)arg0).getName().compareTo(((CPClass)arg1).getName()));
/*     */ 
/*     */     
/* 162 */     for (int index = 0; index < this.entries.size(); index++) {
/* 163 */       ConstantPoolEntry entry = this.entries.get(index);
/* 164 */       if (entry.getGlobalIndex() == -1) {
/* 165 */         if (entry instanceof CPUTF8) {
/* 166 */           cpUtf8sNotInCpAll.add(entry);
/* 167 */         } else if (entry instanceof CPClass) {
/* 168 */           cpClassesNotInCpAll.add(entry);
/*     */         } else {
/* 170 */           throw new Error("error");
/*     */         } 
/*     */       } else {
/* 173 */         inCpAll.add(entry);
/*     */       } 
/*     */     } 
/* 176 */     this.entries.clear();
/* 177 */     this.entries.addAll(inCpAll);
/* 178 */     this.entries.addAll(cpUtf8sNotInCpAll);
/* 179 */     this.entries.addAll(cpClassesNotInCpAll);
/*     */   }
/*     */   
/*     */   public List entries() {
/* 183 */     return Collections.unmodifiableList(this.entries);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void sortClassPool() {
/* 192 */     ArrayList<ClassFileEntry> startOfPool = new ArrayList(this.entries.size());
/* 193 */     ArrayList<ClassFileEntry> finalSort = new ArrayList(this.entries.size());
/*     */     
/* 195 */     for (int i = 0; i < this.entries.size(); i++) {
/* 196 */       ClassFileEntry nextEntry = this.entries.get(i);
/* 197 */       if (this.mustStartClassPool.contains(nextEntry)) {
/* 198 */         startOfPool.add(nextEntry);
/*     */       } else {
/* 200 */         finalSort.add(nextEntry);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 206 */     this.indexCache = new HashMap<>(this.entries.size());
/* 207 */     int index = 0;
/*     */     
/* 209 */     this.entries.clear();
/*     */     
/* 211 */     for (int itIndex = 0; itIndex < startOfPool.size(); itIndex++) {
/* 212 */       ClassFileEntry entry = startOfPool.get(itIndex);
/* 213 */       this.indexCache.put(entry, Integer.valueOf(index));
/*     */       
/* 215 */       if (entry instanceof CPLong || entry instanceof CPDouble) {
/* 216 */         this.entries.add(entry);
/* 217 */         this.entries.add(entry);
/* 218 */         index += 2;
/*     */       } else {
/* 220 */         this.entries.add(entry);
/* 221 */         index++;
/*     */       } 
/*     */     } 
/*     */     
/* 225 */     for (int itFinal = 0; itFinal < finalSort.size(); itFinal++) {
/* 226 */       ClassFileEntry entry = finalSort.get(itFinal);
/* 227 */       this.indexCache.put(entry, Integer.valueOf(index));
/*     */       
/* 229 */       if (entry instanceof CPLong || entry instanceof CPDouble) {
/* 230 */         this.entries.add(entry);
/* 231 */         this.entries.add(entry);
/* 232 */         index += 2;
/*     */       } else {
/* 234 */         this.entries.add(entry);
/* 235 */         index++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassFileEntry addWithNestedEntries(ClassFileEntry entry) {
/* 242 */     add(entry);
/* 243 */     ClassFileEntry[] nestedEntries = entry.getNestedClassFileEntries();
/* 244 */     for (int i = 0; i < nestedEntries.length; i++) {
/* 245 */       addWithNestedEntries(nestedEntries[i]);
/*     */     }
/* 247 */     return entry;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmon\\unpack200\bytecode\ClassConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */