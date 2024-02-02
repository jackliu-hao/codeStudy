package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class RunCodec extends Codec {
   private int k;
   private final Codec aCodec;
   private final Codec bCodec;
   private int last;

   public RunCodec(int k, Codec aCodec, Codec bCodec) throws Pack200Exception {
      if (k <= 0) {
         throw new Pack200Exception("Cannot have a RunCodec for a negative number of numbers");
      } else if (aCodec != null && bCodec != null) {
         this.k = k;
         this.aCodec = aCodec;
         this.bCodec = bCodec;
      } else {
         throw new Pack200Exception("Must supply both codecs for a RunCodec");
      }
   }

   public int decode(InputStream in) throws IOException, Pack200Exception {
      return this.decode(in, (long)this.last);
   }

   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
      if (--this.k >= 0) {
         int value = this.aCodec.decode(in, (long)this.last);
         this.last = this.k == 0 ? 0 : value;
         return this.normalise(value, this.aCodec);
      } else {
         this.last = this.bCodec.decode(in, (long)this.last);
         return this.normalise(this.last, this.bCodec);
      }
   }

   private int normalise(int value, Codec codecUsed) {
      if (codecUsed instanceof BHSDCodec) {
         BHSDCodec bhsd = (BHSDCodec)codecUsed;
         if (bhsd.isDelta()) {
            long cardinality;
            for(cardinality = bhsd.cardinality(); (long)value > bhsd.largest(); value = (int)((long)value - cardinality)) {
            }

            while((long)value < bhsd.smallest()) {
               value = (int)((long)value + cardinality);
            }
         }
      }

      return value;
   }

   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
      int[] band = new int[n];
      int[] aValues = this.aCodec.decodeInts(this.k, in);
      this.normalise(aValues, this.aCodec);
      int[] bValues = this.bCodec.decodeInts(n - this.k, in);
      this.normalise(bValues, this.bCodec);
      System.arraycopy(aValues, 0, band, 0, this.k);
      System.arraycopy(bValues, 0, band, this.k, n - this.k);
      this.lastBandLength = this.aCodec.lastBandLength + this.bCodec.lastBandLength;
      return band;
   }

   private void normalise(int[] band, Codec codecUsed) {
      if (codecUsed instanceof BHSDCodec) {
         BHSDCodec bhsd = (BHSDCodec)codecUsed;
         if (bhsd.isDelta()) {
            long cardinality = bhsd.cardinality();

            for(int i = 0; i < band.length; ++i) {
               while((long)band[i] > bhsd.largest()) {
                  band[i] = (int)((long)band[i] - cardinality);
               }

               while((long)band[i] < bhsd.smallest()) {
                  band[i] = (int)((long)band[i] + cardinality);
               }
            }
         }
      } else if (codecUsed instanceof PopulationCodec) {
         PopulationCodec popCodec = (PopulationCodec)codecUsed;
         int[] favoured = (int[])((int[])popCodec.getFavoured().clone());
         Arrays.sort(favoured);

         for(int i = 0; i < band.length; ++i) {
            boolean favouredValue = Arrays.binarySearch(favoured, band[i]) > -1;
            Codec theCodec = favouredValue ? popCodec.getFavouredCodec() : popCodec.getUnfavouredCodec();
            if (theCodec instanceof BHSDCodec) {
               BHSDCodec bhsd = (BHSDCodec)theCodec;
               if (bhsd.isDelta()) {
                  long cardinality;
                  for(cardinality = bhsd.cardinality(); (long)band[i] > bhsd.largest(); band[i] = (int)((long)band[i] - cardinality)) {
                  }

                  while((long)band[i] < bhsd.smallest()) {
                     band[i] = (int)((long)band[i] + cardinality);
                  }
               }
            }
         }
      }

   }

   public String toString() {
      return "RunCodec[k=" + this.k + ";aCodec=" + this.aCodec + "bCodec=" + this.bCodec + "]";
   }

   public byte[] encode(int value, int last) throws Pack200Exception {
      throw new Pack200Exception("Must encode entire band at once with a RunCodec");
   }

   public byte[] encode(int value) throws Pack200Exception {
      throw new Pack200Exception("Must encode entire band at once with a RunCodec");
   }

   public int getK() {
      return this.k;
   }

   public Codec getACodec() {
      return this.aCodec;
   }

   public Codec getBCodec() {
      return this.bCodec;
   }
}
