package org.apache.commons.compress.archivers;

public interface EntryStreamOffsets {
   long OFFSET_UNKNOWN = -1L;

   long getDataOffset();

   boolean isStreamContiguous();
}
