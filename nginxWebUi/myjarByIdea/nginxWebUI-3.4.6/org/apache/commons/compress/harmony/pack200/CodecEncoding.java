package org.apache.commons.compress.harmony.pack200;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CodecEncoding {
   private static final BHSDCodec[] canonicalCodec = new BHSDCodec[]{null, new BHSDCodec(1, 256), new BHSDCodec(1, 256, 1), new BHSDCodec(1, 256, 0, 1), new BHSDCodec(1, 256, 1, 1), new BHSDCodec(2, 256), new BHSDCodec(2, 256, 1), new BHSDCodec(2, 256, 0, 1), new BHSDCodec(2, 256, 1, 1), new BHSDCodec(3, 256), new BHSDCodec(3, 256, 1), new BHSDCodec(3, 256, 0, 1), new BHSDCodec(3, 256, 1, 1), new BHSDCodec(4, 256), new BHSDCodec(4, 256, 1), new BHSDCodec(4, 256, 0, 1), new BHSDCodec(4, 256, 1, 1), new BHSDCodec(5, 4), new BHSDCodec(5, 4, 1), new BHSDCodec(5, 4, 2), new BHSDCodec(5, 16), new BHSDCodec(5, 16, 1), new BHSDCodec(5, 16, 2), new BHSDCodec(5, 32), new BHSDCodec(5, 32, 1), new BHSDCodec(5, 32, 2), new BHSDCodec(5, 64), new BHSDCodec(5, 64, 1), new BHSDCodec(5, 64, 2), new BHSDCodec(5, 128), new BHSDCodec(5, 128, 1), new BHSDCodec(5, 128, 2), new BHSDCodec(5, 4, 0, 1), new BHSDCodec(5, 4, 1, 1), new BHSDCodec(5, 4, 2, 1), new BHSDCodec(5, 16, 0, 1), new BHSDCodec(5, 16, 1, 1), new BHSDCodec(5, 16, 2, 1), new BHSDCodec(5, 32, 0, 1), new BHSDCodec(5, 32, 1, 1), new BHSDCodec(5, 32, 2, 1), new BHSDCodec(5, 64, 0, 1), new BHSDCodec(5, 64, 1, 1), new BHSDCodec(5, 64, 2, 1), new BHSDCodec(5, 128, 0, 1), new BHSDCodec(5, 128, 1, 1), new BHSDCodec(5, 128, 2, 1), new BHSDCodec(2, 192), new BHSDCodec(2, 224), new BHSDCodec(2, 240), new BHSDCodec(2, 248), new BHSDCodec(2, 252), new BHSDCodec(2, 8, 0, 1), new BHSDCodec(2, 8, 1, 1), new BHSDCodec(2, 16, 0, 1), new BHSDCodec(2, 16, 1, 1), new BHSDCodec(2, 32, 0, 1), new BHSDCodec(2, 32, 1, 1), new BHSDCodec(2, 64, 0, 1), new BHSDCodec(2, 64, 1, 1), new BHSDCodec(2, 128, 0, 1), new BHSDCodec(2, 128, 1, 1), new BHSDCodec(2, 192, 0, 1), new BHSDCodec(2, 192, 1, 1), new BHSDCodec(2, 224, 0, 1), new BHSDCodec(2, 224, 1, 1), new BHSDCodec(2, 240, 0, 1), new BHSDCodec(2, 240, 1, 1), new BHSDCodec(2, 248, 0, 1), new BHSDCodec(2, 248, 1, 1), new BHSDCodec(3, 192), new BHSDCodec(3, 224), new BHSDCodec(3, 240), new BHSDCodec(3, 248), new BHSDCodec(3, 252), new BHSDCodec(3, 8, 0, 1), new BHSDCodec(3, 8, 1, 1), new BHSDCodec(3, 16, 0, 1), new BHSDCodec(3, 16, 1, 1), new BHSDCodec(3, 32, 0, 1), new BHSDCodec(3, 32, 1, 1), new BHSDCodec(3, 64, 0, 1), new BHSDCodec(3, 64, 1, 1), new BHSDCodec(3, 128, 0, 1), new BHSDCodec(3, 128, 1, 1), new BHSDCodec(3, 192, 0, 1), new BHSDCodec(3, 192, 1, 1), new BHSDCodec(3, 224, 0, 1), new BHSDCodec(3, 224, 1, 1), new BHSDCodec(3, 240, 0, 1), new BHSDCodec(3, 240, 1, 1), new BHSDCodec(3, 248, 0, 1), new BHSDCodec(3, 248, 1, 1), new BHSDCodec(4, 192), new BHSDCodec(4, 224), new BHSDCodec(4, 240), new BHSDCodec(4, 248), new BHSDCodec(4, 252), new BHSDCodec(4, 8, 0, 1), new BHSDCodec(4, 8, 1, 1), new BHSDCodec(4, 16, 0, 1), new BHSDCodec(4, 16, 1, 1), new BHSDCodec(4, 32, 0, 1), new BHSDCodec(4, 32, 1, 1), new BHSDCodec(4, 64, 0, 1), new BHSDCodec(4, 64, 1, 1), new BHSDCodec(4, 128, 0, 1), new BHSDCodec(4, 128, 1, 1), new BHSDCodec(4, 192, 0, 1), new BHSDCodec(4, 192, 1, 1), new BHSDCodec(4, 224, 0, 1), new BHSDCodec(4, 224, 1, 1), new BHSDCodec(4, 240, 0, 1), new BHSDCodec(4, 240, 1, 1), new BHSDCodec(4, 248, 0, 1), new BHSDCodec(4, 248, 1, 1)};
   private static Map canonicalCodecsToSpecifiers;

   public static Codec getCodec(int value, InputStream in, Codec defaultCodec) throws IOException, Pack200Exception {
      if (canonicalCodec.length != 116) {
         throw new Error("Canonical encodings have been incorrectly modified");
      } else if (value < 0) {
         throw new IllegalArgumentException("Encoding cannot be less than zero");
      } else if (value == 0) {
         return defaultCodec;
      } else if (value <= 115) {
         return canonicalCodec[value];
      } else {
         int offset;
         int tdefl;
         int kx;
         if (value == 116) {
            offset = in.read();
            if (offset == -1) {
               throw new EOFException("End of buffer read whilst trying to decode codec");
            } else {
               kx = offset & 1;
               int s = offset >> 1 & 3;
               tdefl = (offset >> 3 & 7) + 1;
               offset = in.read();
               if (offset == -1) {
                  throw new EOFException("End of buffer read whilst trying to decode codec");
               } else {
                  int h = offset + 1;
                  return new BHSDCodec(tdefl, h, s, kx);
               }
            }
         } else {
            boolean udef;
            boolean bdef;
            int k;
            Codec aCodec;
            Codec bCodec;
            if (value >= 117 && value <= 140) {
               offset = value - 117;
               kx = offset & 3;
               udef = (offset >> 2 & 1) == 1;
               boolean adef = (offset >> 3 & 1) == 1;
               bdef = (offset >> 4 & 1) == 1;
               if (adef && bdef) {
                  throw new Pack200Exception("ADef and BDef should never both be true");
               } else {
                  int kb = udef ? in.read() : 3;
                  k = (kb + 1) * (int)Math.pow(16.0, (double)kx);
                  if (adef) {
                     aCodec = defaultCodec;
                  } else {
                     aCodec = getCodec(in.read(), in, defaultCodec);
                  }

                  if (bdef) {
                     bCodec = defaultCodec;
                  } else {
                     bCodec = getCodec(in.read(), in, defaultCodec);
                  }

                  return new RunCodec(k, aCodec, bCodec);
               }
            } else if (value >= 141 && value <= 188) {
               offset = value - 141;
               boolean fdef = (offset & 1) == 1;
               udef = (offset >> 1 & 1) == 1;
               tdefl = offset >> 2;
               bdef = tdefl != 0;
               int[] tdefToL = new int[]{0, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252};
               k = tdefToL[tdefl];
               if (bdef) {
                  aCodec = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                  bCodec = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                  return new PopulationCodec(aCodec, k, bCodec);
               } else {
                  aCodec = fdef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                  bCodec = getCodec(in.read(), in, defaultCodec);
                  Codec uCodec = udef ? defaultCodec : getCodec(in.read(), in, defaultCodec);
                  return new PopulationCodec(aCodec, bCodec, uCodec);
               }
            } else {
               throw new Pack200Exception("Invalid codec encoding byte (" + value + ") found");
            }
         }
      }
   }

   public static int getSpecifierForDefaultCodec(BHSDCodec defaultCodec) {
      return getSpecifier(defaultCodec, (Codec)null)[0];
   }

   public static int[] getSpecifier(Codec codec, Codec defaultForBand) {
      int k;
      if (canonicalCodecsToSpecifiers == null) {
         HashMap reverseMap = new HashMap(canonicalCodec.length);

         for(k = 0; k < canonicalCodec.length; ++k) {
            reverseMap.put(canonicalCodec[k], k);
         }

         canonicalCodecsToSpecifiers = reverseMap;
      }

      if (canonicalCodecsToSpecifiers.containsKey(codec)) {
         return new int[]{(Integer)canonicalCodecsToSpecifiers.get(codec)};
      } else if (codec instanceof BHSDCodec) {
         BHSDCodec bhsdCodec = (BHSDCodec)codec;
         int[] specifiers = new int[]{116, (bhsdCodec.isDelta() ? 1 : 0) + 2 * bhsdCodec.getS() + 8 * (bhsdCodec.getB() - 1), bhsdCodec.getH() - 1};
         return specifiers;
      } else {
         int[] specifier;
         int index;
         int i;
         int[] bSpecifier;
         if (codec instanceof RunCodec) {
            RunCodec runCodec = (RunCodec)codec;
            k = runCodec.getK();
            byte kb;
            int kx;
            if (k <= 256) {
               kb = 0;
               kx = k - 1;
            } else if (k <= 4096) {
               kb = 1;
               kx = k / 16 - 1;
            } else if (k <= 65536) {
               kb = 2;
               kx = k / 256 - 1;
            } else {
               kb = 3;
               kx = k / 4096 - 1;
            }

            Codec aCodec = runCodec.getACodec();
            Codec bCodec = runCodec.getBCodec();
            int abDef = 0;
            if (aCodec.equals(defaultForBand)) {
               abDef = 1;
            } else if (bCodec.equals(defaultForBand)) {
               abDef = 2;
            }

            int first = 117 + kb + (kx == 3 ? 0 : 4) + 8 * abDef;
            int[] aSpecifier = abDef == 1 ? new int[0] : getSpecifier(aCodec, defaultForBand);
            bSpecifier = abDef == 2 ? new int[0] : getSpecifier(bCodec, defaultForBand);
            specifier = new int[1 + (kx == 3 ? 0 : 1) + aSpecifier.length + bSpecifier.length];
            specifier[0] = first;
            index = 1;
            if (kx != 3) {
               specifier[1] = kx;
               ++index;
            }

            for(i = 0; i < aSpecifier.length; ++i) {
               specifier[index] = aSpecifier[i];
               ++index;
            }

            for(i = 0; i < bSpecifier.length; ++i) {
               specifier[index] = bSpecifier[i];
               ++index;
            }

            return specifier;
         } else if (!(codec instanceof PopulationCodec)) {
            return null;
         } else {
            PopulationCodec populationCodec = (PopulationCodec)codec;
            Codec tokenCodec = populationCodec.getTokenCodec();
            Codec favouredCodec = populationCodec.getFavouredCodec();
            Codec unfavouredCodec = populationCodec.getUnfavouredCodec();
            int fDef = favouredCodec.equals(defaultForBand) ? 1 : 0;
            int uDef = unfavouredCodec.equals(defaultForBand) ? 1 : 0;
            int tDefL = 0;
            int[] favoured = populationCodec.getFavoured();
            int first;
            if (favoured != null) {
               first = favoured.length;
               if (tokenCodec == Codec.BYTE1) {
                  tDefL = 1;
               } else if (tokenCodec instanceof BHSDCodec) {
                  BHSDCodec tokenBHSD = (BHSDCodec)tokenCodec;
                  if (tokenBHSD.getS() == 0) {
                     specifier = new int[]{4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252};
                     index = 256 - tokenBHSD.getH();
                     i = Arrays.binarySearch(specifier, index);
                     if (i != -1) {
                        tDefL = i++;
                     }
                  }
               }
            }

            first = 141 + fDef + 2 * uDef + 4 * tDefL;
            bSpecifier = fDef == 1 ? new int[0] : getSpecifier(favouredCodec, defaultForBand);
            specifier = tDefL != 0 ? new int[0] : getSpecifier(tokenCodec, defaultForBand);
            int[] unfavouredSpecifier = uDef == 1 ? new int[0] : getSpecifier(unfavouredCodec, defaultForBand);
            int[] specifier = new int[1 + bSpecifier.length + unfavouredSpecifier.length + specifier.length];
            specifier[0] = first;
            int index = 1;

            int i;
            for(i = 0; i < bSpecifier.length; ++i) {
               specifier[index] = bSpecifier[i];
               ++index;
            }

            for(i = 0; i < specifier.length; ++i) {
               specifier[index] = specifier[i];
               ++index;
            }

            for(i = 0; i < unfavouredSpecifier.length; ++i) {
               specifier[index] = unfavouredSpecifier[i];
               ++index;
            }

            return specifier;
         }
      }
   }

   public static BHSDCodec getCanonicalCodec(int i) {
      return canonicalCodec[i];
   }
}
