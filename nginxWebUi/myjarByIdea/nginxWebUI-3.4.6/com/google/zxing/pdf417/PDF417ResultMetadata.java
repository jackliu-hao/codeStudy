package com.google.zxing.pdf417;

public final class PDF417ResultMetadata {
   private int segmentIndex;
   private String fileId;
   private int[] optionalData;
   private boolean lastSegment;

   public int getSegmentIndex() {
      return this.segmentIndex;
   }

   public void setSegmentIndex(int segmentIndex) {
      this.segmentIndex = segmentIndex;
   }

   public String getFileId() {
      return this.fileId;
   }

   public void setFileId(String fileId) {
      this.fileId = fileId;
   }

   public int[] getOptionalData() {
      return this.optionalData;
   }

   public void setOptionalData(int[] optionalData) {
      this.optionalData = optionalData;
   }

   public boolean isLastSegment() {
      return this.lastSegment;
   }

   public void setLastSegment(boolean lastSegment) {
      this.lastSegment = lastSegment;
   }
}
