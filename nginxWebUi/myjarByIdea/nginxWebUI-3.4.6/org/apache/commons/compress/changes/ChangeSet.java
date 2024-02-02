package org.apache.commons.compress.changes;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.compress.archivers.ArchiveEntry;

public final class ChangeSet {
   private final Set<Change> changes = new LinkedHashSet();

   public void delete(String fileName) {
      this.addDeletion(new Change(fileName, 1));
   }

   public void deleteDir(String dirName) {
      this.addDeletion(new Change(dirName, 4));
   }

   public void add(ArchiveEntry pEntry, InputStream pInput) {
      this.add(pEntry, pInput, true);
   }

   public void add(ArchiveEntry pEntry, InputStream pInput, boolean replace) {
      this.addAddition(new Change(pEntry, pInput, replace));
   }

   private void addAddition(Change pChange) {
      if (2 == pChange.type() && pChange.getInput() != null) {
         if (!this.changes.isEmpty()) {
            Iterator<Change> it = this.changes.iterator();

            while(it.hasNext()) {
               Change change = (Change)it.next();
               if (change.type() == 2 && change.getEntry() != null) {
                  ArchiveEntry entry = change.getEntry();
                  if (entry.equals(pChange.getEntry())) {
                     if (pChange.isReplaceMode()) {
                        it.remove();
                        this.changes.add(pChange);
                     }

                     return;
                  }
               }
            }
         }

         this.changes.add(pChange);
      }
   }

   private void addDeletion(Change pChange) {
      if ((1 == pChange.type() || 4 == pChange.type()) && pChange.targetFile() != null) {
         String source = pChange.targetFile();
         if (source != null && !this.changes.isEmpty()) {
            Iterator<Change> it = this.changes.iterator();

            label47:
            while(true) {
               String target;
               do {
                  do {
                     Change change;
                     do {
                        do {
                           if (!it.hasNext()) {
                              break label47;
                           }

                           change = (Change)it.next();
                        } while(change.type() != 2);
                     } while(change.getEntry() == null);

                     target = change.getEntry().getName();
                  } while(target == null);
               } while((1 != pChange.type() || !source.equals(target)) && (4 != pChange.type() || !target.matches(source + "/.*")));

               it.remove();
            }
         }

         this.changes.add(pChange);
      }
   }

   Set<Change> getChanges() {
      return new LinkedHashSet(this.changes);
   }
}
