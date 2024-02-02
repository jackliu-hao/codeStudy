package com.google.zxing.multi;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class GenericMultipleBarcodeReader implements MultipleBarcodeReader {
   private static final int MIN_DIMENSION_TO_RECUR = 100;
   private static final int MAX_DEPTH = 4;
   private final Reader delegate;

   public GenericMultipleBarcodeReader(Reader delegate) {
      this.delegate = delegate;
   }

   public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
      return this.decodeMultiple(image, (Map)null);
   }

   public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
      List<Result> results = new ArrayList();
      this.doDecodeMultiple(image, hints, results, 0, 0, 0);
      if (results.isEmpty()) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         return (Result[])results.toArray(new Result[results.size()]);
      }
   }

   private void doDecodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints, List<Result> results, int xOffset, int yOffset, int currentDepth) {
      if (currentDepth <= 4) {
         Result result;
         try {
            result = this.delegate.decode(image, hints);
         } catch (ReaderException var22) {
            return;
         }

         boolean alreadyFound = false;
         Iterator var9 = results.iterator();

         while(var9.hasNext()) {
            if (((Result)var9.next()).getText().equals(result.getText())) {
               alreadyFound = true;
               break;
            }
         }

         if (!alreadyFound) {
            results.add(translateResultPoints(result, xOffset, yOffset));
         }

         ResultPoint[] resultPoints;
         if ((resultPoints = result.getResultPoints()) != null && resultPoints.length != 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float minX = (float)width;
            float minY = (float)height;
            float maxX = 0.0F;
            float maxY = 0.0F;
            ResultPoint[] var16 = resultPoints;
            int var17 = resultPoints.length;

            for(int var18 = 0; var18 < var17; ++var18) {
               ResultPoint point;
               if ((point = var16[var18]) != null) {
                  float x = point.getX();
                  float y = point.getY();
                  if (x < minX) {
                     minX = x;
                  }

                  if (y < minY) {
                     minY = y;
                  }

                  if (x > maxX) {
                     maxX = x;
                  }

                  if (y > maxY) {
                     maxY = y;
                  }
               }
            }

            if (minX > 100.0F) {
               this.doDecodeMultiple(image.crop(0, 0, (int)minX, height), hints, results, xOffset, yOffset, currentDepth + 1);
            }

            if (minY > 100.0F) {
               this.doDecodeMultiple(image.crop(0, 0, width, (int)minY), hints, results, xOffset, yOffset, currentDepth + 1);
            }

            if (maxX < (float)(width - 100)) {
               this.doDecodeMultiple(image.crop((int)maxX, 0, width - (int)maxX, height), hints, results, xOffset + (int)maxX, yOffset, currentDepth + 1);
            }

            if (maxY < (float)(height - 100)) {
               this.doDecodeMultiple(image.crop(0, (int)maxY, width, height - (int)maxY), hints, results, xOffset, yOffset + (int)maxY, currentDepth + 1);
            }

         }
      }
   }

   private static Result translateResultPoints(Result result, int xOffset, int yOffset) {
      ResultPoint[] oldResultPoints;
      if ((oldResultPoints = result.getResultPoints()) == null) {
         return result;
      } else {
         ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];

         for(int i = 0; i < oldResultPoints.length; ++i) {
            ResultPoint oldPoint;
            if ((oldPoint = oldResultPoints[i]) != null) {
               newResultPoints[i] = new ResultPoint(oldPoint.getX() + (float)xOffset, oldPoint.getY() + (float)yOffset);
            }
         }

         Result newResult;
         (newResult = new Result(result.getText(), result.getRawBytes(), result.getNumBits(), newResultPoints, result.getBarcodeFormat(), result.getTimestamp())).putAllMetadata(result.getResultMetadata());
         return newResult;
      }
   }
}
