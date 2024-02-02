package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.Detector;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MultiDetector extends Detector {
   private static final DetectorResult[] EMPTY_DETECTOR_RESULTS = new DetectorResult[0];

   public MultiDetector(BitMatrix image) {
      super(image);
   }

   public DetectorResult[] detectMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
      BitMatrix image = this.getImage();
      ResultPointCallback resultPointCallback = hints == null ? null : (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
      FinderPatternInfo[] infos;
      if ((infos = (new MultiFinderPatternFinder(image, resultPointCallback)).findMulti(hints)).length == 0) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         List<DetectorResult> result = new ArrayList();
         FinderPatternInfo[] var6 = infos;
         int var7 = infos.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            FinderPatternInfo info = var6[var8];

            try {
               result.add(this.processFinderPatternInfo(info));
            } catch (ReaderException var10) {
            }
         }

         return result.isEmpty() ? EMPTY_DETECTOR_RESULTS : (DetectorResult[])result.toArray(new DetectorResult[result.size()]);
      }
   }
}
