package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.InputStream;

public class PopulationCodec extends Codec {
   private final Codec favouredCodec;
   private Codec tokenCodec;
   private final Codec unfavouredCodec;
   private int l;
   private int[] favoured;

   public PopulationCodec(Codec favouredCodec, Codec tokenCodec, Codec unvafouredCodec) {
      this.favouredCodec = favouredCodec;
      this.tokenCodec = tokenCodec;
      this.unfavouredCodec = unvafouredCodec;
   }

   public PopulationCodec(Codec favouredCodec, int l, Codec unfavouredCodec) {
      if (l < 256 && l > 0) {
         this.favouredCodec = favouredCodec;
         this.l = l;
         this.unfavouredCodec = unfavouredCodec;
      } else {
         throw new IllegalArgumentException("L must be between 1..255");
      }
   }

   public int decode(InputStream in) throws IOException, Pack200Exception {
      throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
   }

   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
      throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
   }

   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
      this.lastBandLength = 0;
      this.favoured = new int[n];
      int smallest = Integer.MAX_VALUE;
      int last = 0;
      int value = false;
      int k = -1;

      while(true) {
         int value = this.favouredCodec.decode(in, (long)last);
         if (k > -1 && (value == smallest || value == last)) {
            this.lastBandLength += k;
            int i;
            if (this.tokenCodec == null) {
               if (k < 256) {
                  this.tokenCodec = Codec.BYTE1;
               } else {
                  i = 1;
                  BHSDCodec codec = null;

                  while(true) {
                     ++i;
                     if (i >= 5) {
                        break;
                     }

                     codec = new BHSDCodec(i, 256 - this.l, 0);
                     if (codec.encodes((long)k)) {
                        this.tokenCodec = codec;
                        break;
                     }
                  }

                  if (this.tokenCodec == null) {
                     throw new Pack200Exception("Cannot calculate token codec from " + k + " and " + this.l);
                  }
               }
            }

            this.lastBandLength += n;
            int[] result = this.tokenCodec.decodeInts(n, in);
            last = 0;

            for(i = 0; i < n; ++i) {
               int index = result[i];
               if (index == 0) {
                  ++this.lastBandLength;
                  result[i] = last = this.unfavouredCodec.decode(in, (long)last);
               } else {
                  result[i] = this.favoured[index - 1];
               }
            }

            return result;
         }

         ++k;
         this.favoured[k] = value;
         int absoluteSmallest = Math.abs(smallest);
         int absoluteValue = Math.abs(value);
         if (absoluteSmallest > absoluteValue) {
            smallest = value;
         } else if (absoluteSmallest == absoluteValue) {
            smallest = absoluteSmallest;
         }

         last = value;
      }
   }

   public int[] getFavoured() {
      return this.favoured;
   }

   public Codec getFavouredCodec() {
      return this.favouredCodec;
   }

   public Codec getUnfavouredCodec() {
      return this.unfavouredCodec;
   }

   public byte[] encode(int value, int last) throws Pack200Exception {
      throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
   }

   public byte[] encode(int value) throws Pack200Exception {
      throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
   }

   public byte[] encode(int[] favoured, int[] tokens, int[] unfavoured) throws Pack200Exception {
      int[] favoured2 = new int[favoured.length + 1];
      System.arraycopy(favoured, 0, favoured2, 0, favoured.length);
      favoured2[favoured2.length - 1] = favoured[favoured.length - 1];
      byte[] favouredEncoded = this.favouredCodec.encode(favoured2);
      byte[] tokensEncoded = this.tokenCodec.encode(tokens);
      byte[] unfavouredEncoded = this.unfavouredCodec.encode(unfavoured);
      byte[] band = new byte[favouredEncoded.length + tokensEncoded.length + unfavouredEncoded.length];
      System.arraycopy(favouredEncoded, 0, band, 0, favouredEncoded.length);
      System.arraycopy(tokensEncoded, 0, band, favouredEncoded.length, tokensEncoded.length);
      System.arraycopy(unfavouredEncoded, 0, band, favouredEncoded.length + tokensEncoded.length, unfavouredEncoded.length);
      return band;
   }

   public Codec getTokenCodec() {
      return this.tokenCodec;
   }
}
