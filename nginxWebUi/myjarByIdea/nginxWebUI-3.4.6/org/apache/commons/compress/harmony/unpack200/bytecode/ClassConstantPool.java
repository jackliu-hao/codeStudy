package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.compress.harmony.unpack200.Segment;

public class ClassConstantPool {
   protected HashSet entriesContainsSet = new HashSet();
   protected HashSet othersContainsSet = new HashSet();
   private final HashSet mustStartClassPool = new HashSet();
   protected Map indexCache;
   private final List others = new ArrayList(500);
   private final List entries = new ArrayList(500);
   private boolean resolved;

   public ClassFileEntry add(ClassFileEntry entry) {
      if (entry instanceof ByteCode) {
         return null;
      } else {
         if (entry instanceof ConstantPoolEntry) {
            if (this.entriesContainsSet.add(entry)) {
               this.entries.add(entry);
            }
         } else if (this.othersContainsSet.add(entry)) {
            this.others.add(entry);
         }

         return entry;
      }
   }

   public void addNestedEntries() {
      boolean added = true;
      ArrayList parents = new ArrayList(512);
      ArrayList children = new ArrayList(512);
      parents.addAll(this.entries);
      parents.addAll(this.others);

      while(added || parents.size() > 0) {
         children.clear();
         int entriesOriginalSize = this.entries.size();
         int othersOriginalSize = this.others.size();

         for(int indexParents = 0; indexParents < parents.size(); ++indexParents) {
            ClassFileEntry entry = (ClassFileEntry)parents.get(indexParents);
            ClassFileEntry[] entryChildren = entry.getNestedClassFileEntries();
            children.addAll(Arrays.asList(entryChildren));
            boolean isAtStart = entry instanceof ByteCode && ((ByteCode)entry).nestedMustStartClassPool();
            if (isAtStart) {
               this.mustStartClassPool.addAll(Arrays.asList(entryChildren));
            }

            this.add(entry);
         }

         added = this.entries.size() != entriesOriginalSize || this.others.size() != othersOriginalSize;
         parents.clear();
         parents.addAll(children);
      }

   }

   public int indexOf(ClassFileEntry entry) {
      if (!this.resolved) {
         throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
      } else if (null == this.indexCache) {
         throw new IllegalStateException("Index cache is not initialized!");
      } else {
         Integer entryIndex = (Integer)this.indexCache.get(entry);
         return entryIndex != null ? entryIndex + 1 : -1;
      }
   }

   public int size() {
      return this.entries.size();
   }

   public ClassFileEntry get(int i) {
      if (!this.resolved) {
         throw new IllegalStateException("Constant pool is not yet resolved; this does not make any sense");
      } else {
         --i;
         return (ClassFileEntry)this.entries.get(i);
      }
   }

   public void resolve(Segment segment) {
      this.initialSort();
      this.sortClassPool();
      this.resolved = true;

      int it;
      ClassFileEntry entry;
      for(it = 0; it < this.entries.size(); ++it) {
         entry = (ClassFileEntry)this.entries.get(it);
         entry.resolve(this);
      }

      for(it = 0; it < this.others.size(); ++it) {
         entry = (ClassFileEntry)this.others.get(it);
         entry.resolve(this);
      }

   }

   private void initialSort() {
      TreeSet inCpAll = new TreeSet((arg0, arg1) -> {
         return ((ConstantPoolEntry)arg0).getGlobalIndex() - ((ConstantPoolEntry)arg1).getGlobalIndex();
      });
      TreeSet cpUtf8sNotInCpAll = new TreeSet((arg0, arg1) -> {
         return ((CPUTF8)arg0).underlyingString().compareTo(((CPUTF8)arg1).underlyingString());
      });
      TreeSet cpClassesNotInCpAll = new TreeSet((arg0, arg1) -> {
         return ((CPClass)arg0).getName().compareTo(((CPClass)arg1).getName());
      });

      for(int index = 0; index < this.entries.size(); ++index) {
         ConstantPoolEntry entry = (ConstantPoolEntry)this.entries.get(index);
         if (entry.getGlobalIndex() == -1) {
            if (entry instanceof CPUTF8) {
               cpUtf8sNotInCpAll.add(entry);
            } else {
               if (!(entry instanceof CPClass)) {
                  throw new Error("error");
               }

               cpClassesNotInCpAll.add(entry);
            }
         } else {
            inCpAll.add(entry);
         }
      }

      this.entries.clear();
      this.entries.addAll(inCpAll);
      this.entries.addAll(cpUtf8sNotInCpAll);
      this.entries.addAll(cpClassesNotInCpAll);
   }

   public List entries() {
      return Collections.unmodifiableList(this.entries);
   }

   protected void sortClassPool() {
      ArrayList startOfPool = new ArrayList(this.entries.size());
      ArrayList finalSort = new ArrayList(this.entries.size());

      int index;
      for(index = 0; index < this.entries.size(); ++index) {
         ClassFileEntry nextEntry = (ClassFileEntry)this.entries.get(index);
         if (this.mustStartClassPool.contains(nextEntry)) {
            startOfPool.add(nextEntry);
         } else {
            finalSort.add(nextEntry);
         }
      }

      this.indexCache = new HashMap(this.entries.size());
      index = 0;
      this.entries.clear();

      ClassFileEntry entry;
      int itFinal;
      for(itFinal = 0; itFinal < startOfPool.size(); ++itFinal) {
         entry = (ClassFileEntry)startOfPool.get(itFinal);
         this.indexCache.put(entry, index);
         if (!(entry instanceof CPLong) && !(entry instanceof CPDouble)) {
            this.entries.add(entry);
            ++index;
         } else {
            this.entries.add(entry);
            this.entries.add(entry);
            index += 2;
         }
      }

      for(itFinal = 0; itFinal < finalSort.size(); ++itFinal) {
         entry = (ClassFileEntry)finalSort.get(itFinal);
         this.indexCache.put(entry, index);
         if (!(entry instanceof CPLong) && !(entry instanceof CPDouble)) {
            this.entries.add(entry);
            ++index;
         } else {
            this.entries.add(entry);
            this.entries.add(entry);
            index += 2;
         }
      }

   }

   public ClassFileEntry addWithNestedEntries(ClassFileEntry entry) {
      this.add(entry);
      ClassFileEntry[] nestedEntries = entry.getNestedClassFileEntries();

      for(int i = 0; i < nestedEntries.length; ++i) {
         this.addWithNestedEntries(nestedEntries[i]);
      }

      return entry;
   }
}
