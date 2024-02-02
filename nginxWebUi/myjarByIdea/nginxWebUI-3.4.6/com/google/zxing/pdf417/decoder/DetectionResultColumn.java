package com.google.zxing.pdf417.decoder;

import java.util.Formatter;

class DetectionResultColumn {
   private static final int MAX_NEARBY_DISTANCE = 5;
   private final BoundingBox boundingBox;
   private final Codeword[] codewords;

   DetectionResultColumn(BoundingBox boundingBox) {
      this.boundingBox = new BoundingBox(boundingBox);
      this.codewords = new Codeword[boundingBox.getMaxY() - boundingBox.getMinY() + 1];
   }

   final Codeword getCodewordNearby(int imageRow) {
      Codeword codeword;
      if ((codeword = this.getCodeword(imageRow)) != null) {
         return codeword;
      } else {
         for(int i = 1; i < 5; ++i) {
            int nearImageRow;
            if ((nearImageRow = this.imageRowToCodewordIndex(imageRow) - i) >= 0 && (codeword = this.codewords[nearImageRow]) != null) {
               return codeword;
            }

            if ((nearImageRow = this.imageRowToCodewordIndex(imageRow) + i) < this.codewords.length && (codeword = this.codewords[nearImageRow]) != null) {
               return codeword;
            }
         }

         return null;
      }
   }

   final int imageRowToCodewordIndex(int imageRow) {
      return imageRow - this.boundingBox.getMinY();
   }

   final void setCodeword(int imageRow, Codeword codeword) {
      this.codewords[this.imageRowToCodewordIndex(imageRow)] = codeword;
   }

   final Codeword getCodeword(int imageRow) {
      return this.codewords[this.imageRowToCodewordIndex(imageRow)];
   }

   final BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   final Codeword[] getCodewords() {
      return this.codewords;
   }

   public String toString() {
      Formatter formatter = new Formatter();
      int row = 0;
      Codeword[] var3;
      int var4 = (var3 = this.codewords).length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Codeword codeword;
         if ((codeword = var3[var5]) == null) {
            formatter.format("%3d:    |   %n", row++);
         } else {
            formatter.format("%3d: %3d|%3d%n", row++, codeword.getRowNumber(), codeword.getValue());
         }
      }

      String result = formatter.toString();
      formatter.close();
      return result;
   }
}
