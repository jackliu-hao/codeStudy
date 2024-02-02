package org.apache.commons.compress.archivers.sevenz;

import java.util.BitSet;

class Archive {
   long packPos;
   long[] packSizes = new long[0];
   BitSet packCrcsDefined;
   long[] packCrcs;
   Folder[] folders;
   SubStreamsInfo subStreamsInfo;
   SevenZArchiveEntry[] files;
   StreamMap streamMap;

   Archive() {
      this.folders = Folder.EMPTY_FOLDER_ARRAY;
      this.files = SevenZArchiveEntry.EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY;
   }

   public String toString() {
      return "Archive with packed streams starting at offset " + this.packPos + ", " + lengthOf(this.packSizes) + " pack sizes, " + lengthOf(this.packCrcs) + " CRCs, " + lengthOf((Object[])this.folders) + " folders, " + lengthOf((Object[])this.files) + " files and " + this.streamMap;
   }

   private static String lengthOf(long[] a) {
      return a == null ? "(null)" : String.valueOf(a.length);
   }

   private static String lengthOf(Object[] a) {
      return a == null ? "(null)" : String.valueOf(a.length);
   }
}
