package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.aztec.decoder.Decoder;
import com.google.zxing.aztec.detector.Detector;
import com.google.zxing.common.DecoderResult;
import java.util.List;
import java.util.Map;

public final class AztecReader implements Reader {
   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
      return this.decode(image, (Map)null);
   }

   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
      NotFoundException notFoundException = null;
      FormatException formatException = null;
      Detector detector = new Detector(image.getBlackMatrix());
      ResultPoint[] points = null;
      DecoderResult decoderResult = null;

      AztecDetectorResult detectorResult;
      try {
         points = (detectorResult = detector.detect(false)).getPoints();
         decoderResult = (new Decoder()).decode(detectorResult);
      } catch (NotFoundException var13) {
         notFoundException = var13;
      } catch (FormatException var14) {
         formatException = var14;
      }

      if (decoderResult == null) {
         try {
            points = (detectorResult = detector.detect(true)).getPoints();
            decoderResult = (new Decoder()).decode(detectorResult);
         } catch (FormatException | NotFoundException var15) {
            if (notFoundException != null) {
               throw notFoundException;
            }

            if (formatException != null) {
               throw formatException;
            }

            throw var15;
         }
      }

      ResultPointCallback rpcb;
      if (hints != null && (rpcb = (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) != null) {
         ResultPoint[] var9 = points;
         int var10 = points.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            ResultPoint point = var9[var11];
            rpcb.foundPossibleResultPoint(point);
         }
      }

      Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), decoderResult.getNumBits(), points, BarcodeFormat.AZTEC, System.currentTimeMillis());
      List byteSegments;
      if ((byteSegments = decoderResult.getByteSegments()) != null) {
         result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
      }

      String ecLevel;
      if ((ecLevel = decoderResult.getECLevel()) != null) {
         result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
      }

      return result;
   }

   public void reset() {
   }
}
