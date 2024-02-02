package com.google.zxing.oned.rss;

public final class RSSUtils {
   private RSSUtils() {
   }

   public static int getRSSvalue(int[] widths, int maxWidth, boolean noNarrow) {
      int n = 0;
      int[] var4 = widths;
      int narrowMask = widths.length;

      int elements;
      int bar;
      for(elements = 0; elements < narrowMask; ++elements) {
         bar = var4[elements];
         n += bar;
      }

      int val = 0;
      narrowMask = 0;
      elements = widths.length;

      for(bar = 0; bar < elements - 1; ++bar) {
         int elmWidth = 1;

         for(narrowMask |= 1 << bar; elmWidth < widths[bar]; narrowMask &= ~(1 << bar)) {
            int subVal = combins(n - elmWidth - 1, elements - bar - 2);
            if (noNarrow && narrowMask == 0 && n - elmWidth - (elements - bar - 1) >= elements - bar - 1) {
               subVal -= combins(n - elmWidth - (elements - bar), elements - bar - 2);
            }

            if (elements - bar - 1 <= 1) {
               if (n - elmWidth > maxWidth) {
                  --subVal;
               }
            } else {
               int lessVal = 0;

               for(int mxwElement = n - elmWidth - (elements - bar - 2); mxwElement > maxWidth; --mxwElement) {
                  lessVal += combins(n - elmWidth - mxwElement - 1, elements - bar - 3);
               }

               subVal -= lessVal * (elements - 1 - bar);
            }

            val += subVal;
            ++elmWidth;
         }

         n -= elmWidth;
      }

      return val;
   }

   private static int combins(int n, int r) {
      int maxDenom;
      int minDenom;
      if (n - r > r) {
         minDenom = r;
         maxDenom = n - r;
      } else {
         minDenom = n - r;
         maxDenom = r;
      }

      int val = 1;
      int j = 1;

      for(int i = n; i > maxDenom; --i) {
         val *= i;
         if (j <= minDenom) {
            val /= j;
            ++j;
         }
      }

      while(j <= minDenom) {
         val /= j;
         ++j;
      }

      return val;
   }
}
