package com.google.zxing.oned.rss.expanded;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.oned.rss.AbstractRSSReader;
import com.google.zxing.oned.rss.DataCharacter;
import com.google.zxing.oned.rss.FinderPattern;
import com.google.zxing.oned.rss.RSSUtils;
import com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class RSSExpandedReader extends AbstractRSSReader {
   private static final int[] SYMBOL_WIDEST = new int[]{7, 5, 4, 3, 1};
   private static final int[] EVEN_TOTAL_SUBSET = new int[]{4, 20, 52, 104, 204};
   private static final int[] GSUM = new int[]{0, 348, 1388, 2948, 3988};
   private static final int[][] FINDER_PATTERNS = new int[][]{{1, 8, 4, 1}, {3, 6, 4, 1}, {3, 4, 6, 1}, {3, 2, 8, 1}, {2, 6, 5, 1}, {2, 2, 9, 1}};
   private static final int[][] WEIGHTS = new int[][]{{1, 3, 9, 27, 81, 32, 96, 77}, {20, 60, 180, 118, 143, 7, 21, 63}, {189, 145, 13, 39, 117, 140, 209, 205}, {193, 157, 49, 147, 19, 57, 171, 91}, {62, 186, 136, 197, 169, 85, 44, 132}, {185, 133, 188, 142, 4, 12, 36, 108}, {113, 128, 173, 97, 80, 29, 87, 50}, {150, 28, 84, 41, 123, 158, 52, 156}, {46, 138, 203, 187, 139, 206, 196, 166}, {76, 17, 51, 153, 37, 111, 122, 155}, {43, 129, 176, 106, 107, 110, 119, 146}, {16, 48, 144, 10, 30, 90, 59, 177}, {109, 116, 137, 200, 178, 112, 125, 164}, {70, 210, 208, 202, 184, 130, 179, 115}, {134, 191, 151, 31, 93, 68, 204, 190}, {148, 22, 66, 198, 172, 94, 71, 2}, {6, 18, 54, 162, 64, 192, 154, 40}, {120, 149, 25, 75, 14, 42, 126, 167}, {79, 26, 78, 23, 69, 207, 199, 175}, {103, 98, 83, 38, 114, 131, 182, 124}, {161, 61, 183, 127, 170, 88, 53, 159}, {55, 165, 73, 8, 24, 72, 5, 15}, {45, 135, 194, 160, 58, 174, 100, 89}};
   private static final int FINDER_PAT_A = 0;
   private static final int FINDER_PAT_B = 1;
   private static final int FINDER_PAT_C = 2;
   private static final int FINDER_PAT_D = 3;
   private static final int FINDER_PAT_E = 4;
   private static final int FINDER_PAT_F = 5;
   private static final int[][] FINDER_PATTERN_SEQUENCES = new int[][]{{0, 0}, {0, 1, 1}, {0, 2, 1, 3}, {0, 4, 1, 3, 2}, {0, 4, 1, 3, 3, 5}, {0, 4, 1, 3, 4, 5, 5}, {0, 0, 1, 1, 2, 2, 3, 3}, {0, 0, 1, 1, 2, 2, 3, 4, 4}, {0, 0, 1, 1, 2, 2, 3, 4, 5, 5}, {0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5}};
   private static final int MAX_PAIRS = 11;
   private final List<ExpandedPair> pairs = new ArrayList(11);
   private final List<ExpandedRow> rows = new ArrayList();
   private final int[] startEnd = new int[2];
   private boolean startFromEven;

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
      this.pairs.clear();
      this.startFromEven = false;

      try {
         return constructResult(this.decodeRow2pairs(rowNumber, row));
      } catch (NotFoundException var4) {
         this.pairs.clear();
         this.startFromEven = true;
         return constructResult(this.decodeRow2pairs(rowNumber, row));
      }
   }

   public void reset() {
      this.pairs.clear();
      this.rows.clear();
   }

   List<ExpandedPair> decodeRow2pairs(int rowNumber, BitArray row) throws NotFoundException {
      try {
         while(true) {
            ExpandedPair nextPair = this.retrieveNextPair(row, this.pairs, rowNumber);
            this.pairs.add(nextPair);
         }
      } catch (NotFoundException var5) {
         if (this.pairs.isEmpty()) {
            throw var5;
         } else if (this.checkChecksum()) {
            return this.pairs;
         } else {
            boolean tryStackedDecode = !this.rows.isEmpty();
            this.storeRow(rowNumber, false);
            if (tryStackedDecode) {
               List ps;
               if ((ps = this.checkRows(false)) != null) {
                  return ps;
               }

               if ((ps = this.checkRows(true)) != null) {
                  return ps;
               }
            }

            throw NotFoundException.getNotFoundInstance();
         }
      }
   }

   private List<ExpandedPair> checkRows(boolean reverse) {
      if (this.rows.size() > 25) {
         this.rows.clear();
         return null;
      } else {
         this.pairs.clear();
         if (reverse) {
            Collections.reverse(this.rows);
         }

         List<ExpandedPair> ps = null;

         try {
            ps = this.checkRows(new ArrayList(), 0);
         } catch (NotFoundException var3) {
         }

         if (reverse) {
            Collections.reverse(this.rows);
         }

         return ps;
      }
   }

   private List<ExpandedPair> checkRows(List<ExpandedRow> collectedRows, int currentRow) throws NotFoundException {
      for(int i = currentRow; i < this.rows.size(); ++i) {
         ExpandedRow row = (ExpandedRow)this.rows.get(i);
         this.pairs.clear();
         Iterator var5 = collectedRows.iterator();

         while(var5.hasNext()) {
            ExpandedRow collectedRow = (ExpandedRow)var5.next();
            this.pairs.addAll(collectedRow.getPairs());
         }

         this.pairs.addAll(row.getPairs());
         if (isValidSequence(this.pairs)) {
            if (this.checkChecksum()) {
               return this.pairs;
            }

            ArrayList rs;
            (rs = new ArrayList()).addAll(collectedRows);
            rs.add(row);

            try {
               return this.checkRows(rs, i + 1);
            } catch (NotFoundException var7) {
            }
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static boolean isValidSequence(List<ExpandedPair> pairs) {
      int[][] var1;
      int var2 = (var1 = FINDER_PATTERN_SEQUENCES).length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] sequence = var1[var3];
         if (pairs.size() <= sequence.length) {
            boolean stop = true;

            for(int j = 0; j < pairs.size(); ++j) {
               if (((ExpandedPair)pairs.get(j)).getFinderPattern().getValue() != sequence[j]) {
                  stop = false;
                  break;
               }
            }

            if (stop) {
               return true;
            }
         }
      }

      return false;
   }

   private void storeRow(int rowNumber, boolean wasReversed) {
      int insertPos = 0;
      boolean prevIsSame = false;

      boolean nextIsSame;
      for(nextIsSame = false; insertPos < this.rows.size(); ++insertPos) {
         ExpandedRow erow;
         if ((erow = (ExpandedRow)this.rows.get(insertPos)).getRowNumber() > rowNumber) {
            nextIsSame = erow.isEquivalent(this.pairs);
            break;
         }

         prevIsSame = erow.isEquivalent(this.pairs);
      }

      if (!nextIsSame && !prevIsSame) {
         if (!isPartialRow(this.pairs, this.rows)) {
            this.rows.add(insertPos, new ExpandedRow(this.pairs, rowNumber, wasReversed));
            removePartialRows(this.pairs, this.rows);
         }
      }
   }

   private static void removePartialRows(List<ExpandedPair> pairs, List<ExpandedRow> rows) {
      Iterator<ExpandedRow> iterator = rows.iterator();

      while(true) {
         ExpandedRow r;
         do {
            if (!iterator.hasNext()) {
               return;
            }
         } while((r = (ExpandedRow)iterator.next()).getPairs().size() == pairs.size());

         boolean allFound = true;
         Iterator var5 = r.getPairs().iterator();

         while(var5.hasNext()) {
            ExpandedPair p = (ExpandedPair)var5.next();
            boolean found = false;
            Iterator var8 = pairs.iterator();

            while(var8.hasNext()) {
               ExpandedPair pp = (ExpandedPair)var8.next();
               if (p.equals(pp)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               allFound = false;
               break;
            }
         }

         if (allFound) {
            iterator.remove();
         }
      }
   }

   private static boolean isPartialRow(Iterable<ExpandedPair> pairs, Iterable<ExpandedRow> rows) {
      Iterator var2 = rows.iterator();

      boolean allFound;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         ExpandedRow r = (ExpandedRow)var2.next();
         allFound = true;
         Iterator var5 = pairs.iterator();

         while(var5.hasNext()) {
            ExpandedPair p = (ExpandedPair)var5.next();
            boolean found = false;
            Iterator var8 = r.getPairs().iterator();

            while(var8.hasNext()) {
               ExpandedPair pp = (ExpandedPair)var8.next();
               if (p.equals(pp)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               allFound = false;
               break;
            }
         }
      } while(!allFound);

      return true;
   }

   List<ExpandedRow> getRows() {
      return this.rows;
   }

   static Result constructResult(List<ExpandedPair> pairs) throws NotFoundException, FormatException {
      String resultingString = AbstractExpandedDecoder.createDecoder(BitArrayBuilder.buildBitArray(pairs)).parseInformation();
      ResultPoint[] firstPoints = ((ExpandedPair)pairs.get(0)).getFinderPattern().getResultPoints();
      ResultPoint[] lastPoints = ((ExpandedPair)pairs.get(pairs.size() - 1)).getFinderPattern().getResultPoints();
      return new Result(resultingString, (byte[])null, new ResultPoint[]{firstPoints[0], firstPoints[1], lastPoints[0], lastPoints[1]}, BarcodeFormat.RSS_EXPANDED);
   }

   private boolean checkChecksum() {
      ExpandedPair firstPair;
      DataCharacter checkCharacter = (firstPair = (ExpandedPair)this.pairs.get(0)).getLeftChar();
      DataCharacter firstCharacter;
      if ((firstCharacter = firstPair.getRightChar()) == null) {
         return false;
      } else {
         int checksum = firstCharacter.getChecksumPortion();
         int s = 2;

         for(int i = 1; i < this.pairs.size(); ++i) {
            ExpandedPair currentPair = (ExpandedPair)this.pairs.get(i);
            checksum += currentPair.getLeftChar().getChecksumPortion();
            ++s;
            DataCharacter currentRightChar;
            if ((currentRightChar = currentPair.getRightChar()) != null) {
               checksum += currentRightChar.getChecksumPortion();
               ++s;
            }
         }

         checksum %= 211;
         if (211 * (s - 4) + checksum == checkCharacter.getValue()) {
            return true;
         } else {
            return false;
         }
      }
   }

   private static int getNextSecondBar(BitArray row, int initialPos) {
      int currentPos;
      if (row.get(initialPos)) {
         currentPos = row.getNextUnset(initialPos);
         currentPos = row.getNextSet(currentPos);
      } else {
         currentPos = row.getNextSet(initialPos);
         currentPos = row.getNextUnset(currentPos);
      }

      return currentPos;
   }

   ExpandedPair retrieveNextPair(BitArray row, List<ExpandedPair> previousPairs, int rowNumber) throws NotFoundException {
      boolean isOddPattern = previousPairs.size() % 2 == 0;
      if (this.startFromEven) {
         isOddPattern = !isOddPattern;
      }

      boolean keepFinding = true;
      int forcedOffset = -1;

      FinderPattern pattern;
      do {
         this.findNextPair(row, previousPairs, forcedOffset);
         if ((pattern = this.parseFoundFinderPattern(row, rowNumber, isOddPattern)) == null) {
            forcedOffset = getNextSecondBar(row, this.startEnd[0]);
         } else {
            keepFinding = false;
         }
      } while(keepFinding);

      DataCharacter leftChar = this.decodeDataCharacter(row, pattern, isOddPattern, true);
      if (!previousPairs.isEmpty() && ((ExpandedPair)previousPairs.get(previousPairs.size() - 1)).mustBeLast()) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         DataCharacter rightChar;
         try {
            rightChar = this.decodeDataCharacter(row, pattern, isOddPattern, false);
         } catch (NotFoundException var10) {
            rightChar = null;
         }

         return new ExpandedPair(leftChar, rightChar, pattern, true);
      }
   }

   private void findNextPair(BitArray row, List<ExpandedPair> previousPairs, int forcedOffset) throws NotFoundException {
      int[] counters;
      (counters = this.getDecodeFinderCounters())[0] = 0;
      counters[1] = 0;
      counters[2] = 0;
      counters[3] = 0;
      int width = row.getSize();
      int rowOffset;
      if (forcedOffset >= 0) {
         rowOffset = forcedOffset;
      } else if (previousPairs.isEmpty()) {
         rowOffset = 0;
      } else {
         rowOffset = ((ExpandedPair)previousPairs.get(previousPairs.size() - 1)).getFinderPattern().getStartEnd()[1];
      }

      boolean searchingEvenPair = previousPairs.size() % 2 != 0;
      if (this.startFromEven) {
         searchingEvenPair = !searchingEvenPair;
      }

      boolean isWhite;
      for(isWhite = false; rowOffset < width && (isWhite = !row.get(rowOffset)); ++rowOffset) {
      }

      int counterPosition = 0;
      int patternStart = rowOffset;

      for(int x = rowOffset; x < width; ++x) {
         if (row.get(x) ^ isWhite) {
            int var10002 = counters[counterPosition]++;
         } else {
            if (counterPosition == 3) {
               if (searchingEvenPair) {
                  reverseCounters(counters);
               }

               if (isFinderPattern(counters)) {
                  this.startEnd[0] = patternStart;
                  this.startEnd[1] = x;
                  return;
               }

               if (searchingEvenPair) {
                  reverseCounters(counters);
               }

               patternStart += counters[0] + counters[1];
               counters[0] = counters[2];
               counters[1] = counters[3];
               counters[2] = 0;
               counters[3] = 0;
               --counterPosition;
            } else {
               ++counterPosition;
            }

            counters[counterPosition] = 1;
            isWhite = !isWhite;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static void reverseCounters(int[] counters) {
      int length = counters.length;

      for(int i = 0; i < length / 2; ++i) {
         int tmp = counters[i];
         counters[i] = counters[length - i - 1];
         counters[length - i - 1] = tmp;
      }

   }

   private FinderPattern parseFoundFinderPattern(BitArray row, int rowNumber, boolean oddPattern) {
      int firstCounter;
      int start;
      int end;
      if (oddPattern) {
         int firstElementStart;
         for(firstElementStart = this.startEnd[0] - 1; firstElementStart >= 0 && !row.get(firstElementStart); --firstElementStart) {
         }

         ++firstElementStart;
         firstCounter = this.startEnd[0] - firstElementStart;
         start = firstElementStart;
         end = this.startEnd[1];
      } else {
         start = this.startEnd[0];
         firstCounter = (end = row.getNextUnset(this.startEnd[1] + 1)) - this.startEnd[1];
      }

      int[] counters;
      System.arraycopy(counters = this.getDecodeFinderCounters(), 0, counters, 1, counters.length - 1);
      counters[0] = firstCounter;

      int value;
      try {
         value = parseFinderValue(counters, FINDER_PATTERNS);
      } catch (NotFoundException var9) {
         return null;
      }

      return new FinderPattern(value, new int[]{start, end}, start, end, rowNumber);
   }

   DataCharacter decodeDataCharacter(BitArray row, FinderPattern pattern, boolean isOddPattern, boolean leftChar) throws NotFoundException {
      int[] counters;
      (counters = this.getDataCharacterCounters())[0] = 0;
      counters[1] = 0;
      counters[2] = 0;
      counters[3] = 0;
      counters[4] = 0;
      counters[5] = 0;
      counters[6] = 0;
      counters[7] = 0;
      if (leftChar) {
         recordPatternInReverse(row, pattern.getStartEnd()[0], counters);
      } else {
         recordPattern(row, pattern.getStartEnd()[1], counters);
         int i = 0;

         for(int j = counters.length - 1; i < j; --j) {
            int temp = counters[i];
            counters[i] = counters[j];
            counters[j] = temp;
            ++i;
         }
      }

      float elementWidth = (float)MathUtils.sum(counters) / 17.0F;
      float expectedElementWidth = (float)(pattern.getStartEnd()[1] - pattern.getStartEnd()[0]) / 15.0F;
      if (Math.abs(elementWidth - expectedElementWidth) / expectedElementWidth > 0.3F) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         int[] oddCounts = this.getOddCounts();
         int[] evenCounts = this.getEvenCounts();
         float[] oddRoundingErrors = this.getOddRoundingErrors();
         float[] evenRoundingErrors = this.getEvenRoundingErrors();

         int weightRowNumber;
         int oddChecksumPortion;
         int evenChecksumPortion;
         for(weightRowNumber = 0; weightRowNumber < counters.length; ++weightRowNumber) {
            float value;
            if ((oddChecksumPortion = (int)((value = 1.0F * (float)counters[weightRowNumber] / elementWidth) + 0.5F)) <= 0) {
               if (value < 0.3F) {
                  throw NotFoundException.getNotFoundInstance();
               }

               oddChecksumPortion = 1;
            } else if (oddChecksumPortion > 8) {
               if (value > 8.7F) {
                  throw NotFoundException.getNotFoundInstance();
               }

               oddChecksumPortion = 8;
            }

            evenChecksumPortion = weightRowNumber / 2;
            if ((weightRowNumber & 1) == 0) {
               oddCounts[evenChecksumPortion] = oddChecksumPortion;
               oddRoundingErrors[evenChecksumPortion] = value - (float)oddChecksumPortion;
            } else {
               evenCounts[evenChecksumPortion] = oddChecksumPortion;
               evenRoundingErrors[evenChecksumPortion] = value - (float)oddChecksumPortion;
            }
         }

         this.adjustOddEvenCounts(17);
         weightRowNumber = 4 * pattern.getValue() + (isOddPattern ? 0 : 2) + (leftChar ? 0 : 1) - 1;
         int oddSum = 0;
         oddChecksumPortion = 0;

         int checksumPortion;
         for(evenChecksumPortion = oddCounts.length - 1; evenChecksumPortion >= 0; --evenChecksumPortion) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
               checksumPortion = WEIGHTS[weightRowNumber][2 * evenChecksumPortion];
               oddChecksumPortion += oddCounts[evenChecksumPortion] * checksumPortion;
            }

            oddSum += oddCounts[evenChecksumPortion];
         }

         evenChecksumPortion = 0;

         int group;
         for(checksumPortion = evenCounts.length - 1; checksumPortion >= 0; --checksumPortion) {
            if (isNotA1left(pattern, isOddPattern, leftChar)) {
               group = WEIGHTS[weightRowNumber][2 * checksumPortion + 1];
               evenChecksumPortion += evenCounts[checksumPortion] * group;
            }
         }

         checksumPortion = oddChecksumPortion + evenChecksumPortion;
         if ((oddSum & 1) == 0 && oddSum <= 13 && oddSum >= 4) {
            group = (13 - oddSum) / 2;
            int oddWidest = SYMBOL_WIDEST[group];
            int evenWidest = 9 - oddWidest;
            int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
            int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, false);
            int tEven = EVEN_TOTAL_SUBSET[group];
            int gSum = GSUM[group];
            int value = vOdd * tEven + vEven + gSum;
            return new DataCharacter(value, checksumPortion);
         } else {
            throw NotFoundException.getNotFoundInstance();
         }
      }
   }

   private static boolean isNotA1left(FinderPattern pattern, boolean isOddPattern, boolean leftChar) {
      return pattern.getValue() != 0 || !isOddPattern || !leftChar;
   }

   private void adjustOddEvenCounts(int numModules) throws NotFoundException {
      int oddSum = MathUtils.sum(this.getOddCounts());
      int evenSum = MathUtils.sum(this.getEvenCounts());
      boolean incrementOdd = false;
      boolean decrementOdd = false;
      if (oddSum > 13) {
         decrementOdd = true;
      } else if (oddSum < 4) {
         incrementOdd = true;
      }

      boolean incrementEven = false;
      boolean decrementEven = false;
      if (evenSum > 13) {
         decrementEven = true;
      } else if (evenSum < 4) {
         incrementEven = true;
      }

      int mismatch = oddSum + evenSum - numModules;
      boolean oddParityBad = (oddSum & 1) == 1;
      boolean evenParityBad = (evenSum & 1) == 0;
      if (mismatch == 1) {
         if (oddParityBad) {
            if (evenParityBad) {
               throw NotFoundException.getNotFoundInstance();
            }

            decrementOdd = true;
         } else {
            if (!evenParityBad) {
               throw NotFoundException.getNotFoundInstance();
            }

            decrementEven = true;
         }
      } else if (mismatch == -1) {
         if (oddParityBad) {
            if (evenParityBad) {
               throw NotFoundException.getNotFoundInstance();
            }

            incrementOdd = true;
         } else {
            if (!evenParityBad) {
               throw NotFoundException.getNotFoundInstance();
            }

            incrementEven = true;
         }
      } else {
         if (mismatch != 0) {
            throw NotFoundException.getNotFoundInstance();
         }

         if (oddParityBad) {
            if (!evenParityBad) {
               throw NotFoundException.getNotFoundInstance();
            }

            if (oddSum < evenSum) {
               incrementOdd = true;
               decrementEven = true;
            } else {
               decrementOdd = true;
               incrementEven = true;
            }
         } else if (evenParityBad) {
            throw NotFoundException.getNotFoundInstance();
         }
      }

      if (incrementOdd) {
         if (decrementOdd) {
            throw NotFoundException.getNotFoundInstance();
         }

         increment(this.getOddCounts(), this.getOddRoundingErrors());
      }

      if (decrementOdd) {
         decrement(this.getOddCounts(), this.getOddRoundingErrors());
      }

      if (incrementEven) {
         if (decrementEven) {
            throw NotFoundException.getNotFoundInstance();
         }

         increment(this.getEvenCounts(), this.getOddRoundingErrors());
      }

      if (decrementEven) {
         decrement(this.getEvenCounts(), this.getEvenRoundingErrors());
      }

   }
}
