package com.google.zxing.multi.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.multi.qrcode.detector.MultiDetector;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {
   private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];
   private static final ResultPoint[] NO_POINTS = new ResultPoint[0];

   public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
      return this.decodeMultiple(image, (Map)null);
   }

   public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
      List<Result> results = new ArrayList();
      DetectorResult[] var4;
      int var5 = (var4 = (new MultiDetector(image.getBlackMatrix())).detectMulti(hints)).length;

      for(int var6 = 0; var6 < var5; ++var6) {
         DetectorResult detectorResult = var4[var6];

         try {
            DecoderResult decoderResult = this.getDecoder().decode(detectorResult.getBits(), hints);
            ResultPoint[] points = detectorResult.getPoints();
            if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
               ((QRCodeDecoderMetaData)decoderResult.getOther()).applyMirroredCorrection(points);
            }

            Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
            List byteSegments;
            if ((byteSegments = decoderResult.getByteSegments()) != null) {
               result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
            }

            String ecLevel;
            if ((ecLevel = decoderResult.getECLevel()) != null) {
               result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
            }

            if (decoderResult.hasStructuredAppend()) {
               result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE, decoderResult.getStructuredAppendSequenceNumber());
               result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_PARITY, decoderResult.getStructuredAppendParity());
            }

            results.add(result);
         } catch (ReaderException var13) {
         }
      }

      if (results.isEmpty()) {
         return EMPTY_RESULT_ARRAY;
      } else {
         List var10000 = processStructuredAppend(results);
         return (Result[])var10000.toArray(new Result[var10000.size()]);
      }
   }

   private static List<Result> processStructuredAppend(List<Result> results) {
      boolean hasSA = false;
      Iterator var2 = results.iterator();

      while(var2.hasNext()) {
         if (((Result)var2.next()).getResultMetadata().containsKey(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)) {
            hasSA = true;
            break;
         }
      }

      if (!hasSA) {
         return results;
      } else {
         List<Result> newResults = new ArrayList();
         List<Result> saResults = new ArrayList();
         Iterator var4 = results.iterator();

         while(var4.hasNext()) {
            Result result = (Result)var4.next();
            newResults.add(result);
            if (result.getResultMetadata().containsKey(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)) {
               saResults.add(result);
            }
         }

         Collections.sort(saResults, new SAComparator());
         StringBuilder concatedText = new StringBuilder();
         int rawBytesLen = 0;
         int byteSegmentLength = 0;
         Iterator var7 = saResults.iterator();

         while(true) {
            Result saResult;
            do {
               if (!var7.hasNext()) {
                  byte[] newRawBytes = new byte[rawBytesLen];
                  byte[] newByteSegment = new byte[byteSegmentLength];
                  int newRawBytesIndex = 0;
                  int byteSegmentIndex = 0;
                  Iterator var21 = saResults.iterator();

                  while(true) {
                     Result saResult;
                     do {
                        if (!var21.hasNext()) {
                           Result newResult = new Result(concatedText.toString(), newRawBytes, NO_POINTS, BarcodeFormat.QR_CODE);
                           if (byteSegmentLength > 0) {
                              ArrayList byteSegmentList;
                              (byteSegmentList = new ArrayList()).add(newByteSegment);
                              newResult.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegmentList);
                           }

                           newResults.add(newResult);
                           return newResults;
                        }

                        System.arraycopy((saResult = (Result)var21.next()).getRawBytes(), 0, newRawBytes, newRawBytesIndex, saResult.getRawBytes().length);
                        newRawBytesIndex += saResult.getRawBytes().length;
                     } while(!saResult.getResultMetadata().containsKey(ResultMetadataType.BYTE_SEGMENTS));

                     byte[] segment;
                     for(Iterator var13 = ((Iterable)saResult.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS)).iterator(); var13.hasNext(); byteSegmentIndex += segment.length) {
                        System.arraycopy(segment = (byte[])var13.next(), 0, newByteSegment, byteSegmentIndex, segment.length);
                     }
                  }
               }

               saResult = (Result)var7.next();
               concatedText.append(saResult.getText());
               rawBytesLen += saResult.getRawBytes().length;
            } while(!saResult.getResultMetadata().containsKey(ResultMetadataType.BYTE_SEGMENTS));

            byte[] segment;
            for(Iterator var10 = ((Iterable)saResult.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS)).iterator(); var10.hasNext(); byteSegmentLength += segment.length) {
               segment = (byte[])var10.next();
            }
         }
      }
   }

   private static final class SAComparator implements Serializable, Comparator<Result> {
      private SAComparator() {
      }

      public int compare(Result a, Result b) {
         int aNumber = (Integer)a.getResultMetadata().get(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE);
         int bNumber = (Integer)b.getResultMetadata().get(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE);
         if (aNumber < bNumber) {
            return -1;
         } else {
            return aNumber > bNumber ? 1 : 0;
         }
      }

      // $FF: synthetic method
      SAComparator(Object x0) {
         this();
      }
   }
}
