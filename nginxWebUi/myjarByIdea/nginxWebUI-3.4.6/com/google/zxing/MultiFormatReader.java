package com.google.zxing;

import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.qrcode.QRCodeReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class MultiFormatReader implements Reader {
   private Map<DecodeHintType, ?> hints;
   private Reader[] readers;

   public Result decode(BinaryBitmap image) throws NotFoundException {
      this.setHints((Map)null);
      return this.decodeInternal(image);
   }

   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
      this.setHints(hints);
      return this.decodeInternal(image);
   }

   public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
      if (this.readers == null) {
         this.setHints((Map)null);
      }

      return this.decodeInternal(image);
   }

   public void setHints(Map<DecodeHintType, ?> hints) {
      this.hints = hints;
      boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
      Collection<BarcodeFormat> formats = hints == null ? null : (Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS);
      Collection<Reader> readers = new ArrayList();
      if (formats != null) {
         boolean addOneDReader;
         if ((addOneDReader = formats.contains(BarcodeFormat.UPC_A) || formats.contains(BarcodeFormat.UPC_E) || formats.contains(BarcodeFormat.EAN_13) || formats.contains(BarcodeFormat.EAN_8) || formats.contains(BarcodeFormat.CODABAR) || formats.contains(BarcodeFormat.CODE_39) || formats.contains(BarcodeFormat.CODE_93) || formats.contains(BarcodeFormat.CODE_128) || formats.contains(BarcodeFormat.ITF) || formats.contains(BarcodeFormat.RSS_14) || formats.contains(BarcodeFormat.RSS_EXPANDED)) && !tryHarder) {
            readers.add(new MultiFormatOneDReader(hints));
         }

         if (formats.contains(BarcodeFormat.QR_CODE)) {
            readers.add(new QRCodeReader());
         }

         if (formats.contains(BarcodeFormat.DATA_MATRIX)) {
            readers.add(new DataMatrixReader());
         }

         if (formats.contains(BarcodeFormat.AZTEC)) {
            readers.add(new AztecReader());
         }

         if (formats.contains(BarcodeFormat.PDF_417)) {
            readers.add(new PDF417Reader());
         }

         if (formats.contains(BarcodeFormat.MAXICODE)) {
            readers.add(new MaxiCodeReader());
         }

         if (addOneDReader && tryHarder) {
            readers.add(new MultiFormatOneDReader(hints));
         }
      }

      if (readers.isEmpty()) {
         if (!tryHarder) {
            readers.add(new MultiFormatOneDReader(hints));
         }

         readers.add(new QRCodeReader());
         readers.add(new DataMatrixReader());
         readers.add(new AztecReader());
         readers.add(new PDF417Reader());
         readers.add(new MaxiCodeReader());
         if (tryHarder) {
            readers.add(new MultiFormatOneDReader(hints));
         }
      }

      this.readers = (Reader[])readers.toArray(new Reader[readers.size()]);
   }

   public void reset() {
      if (this.readers != null) {
         Reader[] var1;
         int var2 = (var1 = this.readers).length;

         for(int var3 = 0; var3 < var2; ++var3) {
            var1[var3].reset();
         }
      }

   }

   private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
      if (this.readers != null) {
         Reader[] var2;
         int var3 = (var2 = this.readers).length;
         int var4 = 0;

         while(var4 < var3) {
            Reader reader = var2[var4];

            try {
               return reader.decode(image, this.hints);
            } catch (ReaderException var6) {
               ++var4;
            }
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }
}
