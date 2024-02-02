package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.Map;

public final class ByQuadrantReader implements Reader {
   private final Reader delegate;

   public ByQuadrantReader(Reader delegate) {
      this.delegate = delegate;
   }

   public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
      return this.decode(image, (Map)null);
   }

   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
      int width = image.getWidth();
      int height = image.getHeight();
      int halfWidth = width / 2;
      int halfHeight = height / 2;

      try {
         return this.delegate.decode(image.crop(0, 0, halfWidth, halfHeight), hints);
      } catch (NotFoundException var14) {
         Result result;
         try {
            makeAbsolute((result = this.delegate.decode(image.crop(halfWidth, 0, halfWidth, halfHeight), hints)).getResultPoints(), halfWidth, 0);
            return result;
         } catch (NotFoundException var13) {
            try {
               makeAbsolute((result = this.delegate.decode(image.crop(0, halfHeight, halfWidth, halfHeight), hints)).getResultPoints(), 0, halfHeight);
               return result;
            } catch (NotFoundException var12) {
               try {
                  makeAbsolute((result = this.delegate.decode(image.crop(halfWidth, halfHeight, halfWidth, halfHeight), hints)).getResultPoints(), halfWidth, halfHeight);
                  return result;
               } catch (NotFoundException var11) {
                  int quarterWidth = halfWidth / 2;
                  int quarterHeight = halfHeight / 2;
                  BinaryBitmap center = image.crop(quarterWidth, quarterHeight, halfWidth, halfHeight);
                  Result result;
                  makeAbsolute((result = this.delegate.decode(center, hints)).getResultPoints(), quarterWidth, quarterHeight);
                  return result;
               }
            }
         }
      }
   }

   public void reset() {
      this.delegate.reset();
   }

   private static void makeAbsolute(ResultPoint[] points, int leftOffset, int topOffset) {
      if (points != null) {
         for(int i = 0; i < points.length; ++i) {
            ResultPoint relative = points[i];
            points[i] = new ResultPoint(relative.getX() + (float)leftOffset, relative.getY() + (float)topOffset);
         }
      }

   }
}
