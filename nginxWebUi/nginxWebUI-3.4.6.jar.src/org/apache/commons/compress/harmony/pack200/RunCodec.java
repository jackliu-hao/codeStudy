/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RunCodec
/*     */   extends Codec
/*     */ {
/*     */   private int k;
/*     */   private final Codec aCodec;
/*     */   private final Codec bCodec;
/*     */   private int last;
/*     */   
/*     */   public RunCodec(int k, Codec aCodec, Codec bCodec) throws Pack200Exception {
/*  35 */     if (k <= 0) {
/*  36 */       throw new Pack200Exception("Cannot have a RunCodec for a negative number of numbers");
/*     */     }
/*  38 */     if (aCodec == null || bCodec == null) {
/*  39 */       throw new Pack200Exception("Must supply both codecs for a RunCodec");
/*     */     }
/*  41 */     this.k = k;
/*  42 */     this.aCodec = aCodec;
/*  43 */     this.bCodec = bCodec;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in) throws IOException, Pack200Exception {
/*  48 */     return decode(in, this.last);
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
/*  53 */     if (--this.k >= 0) {
/*  54 */       int value = this.aCodec.decode(in, this.last);
/*  55 */       this.last = (this.k == 0) ? 0 : value;
/*  56 */       return normalise(value, this.aCodec);
/*     */     } 
/*  58 */     this.last = this.bCodec.decode(in, this.last);
/*  59 */     return normalise(this.last, this.bCodec);
/*     */   }
/*     */   
/*     */   private int normalise(int value, Codec codecUsed) {
/*  63 */     if (codecUsed instanceof BHSDCodec) {
/*  64 */       BHSDCodec bhsd = (BHSDCodec)codecUsed;
/*  65 */       if (bhsd.isDelta()) {
/*  66 */         long cardinality = bhsd.cardinality();
/*  67 */         while (value > bhsd.largest()) {
/*  68 */           value = (int)(value - cardinality);
/*     */         }
/*  70 */         while (value < bhsd.smallest()) {
/*  71 */           value = (int)(value + cardinality);
/*     */         }
/*     */       } 
/*     */     } 
/*  75 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
/*  80 */     int[] band = new int[n];
/*  81 */     int[] aValues = this.aCodec.decodeInts(this.k, in);
/*  82 */     normalise(aValues, this.aCodec);
/*  83 */     int[] bValues = this.bCodec.decodeInts(n - this.k, in);
/*  84 */     normalise(bValues, this.bCodec);
/*  85 */     System.arraycopy(aValues, 0, band, 0, this.k);
/*  86 */     System.arraycopy(bValues, 0, band, this.k, n - this.k);
/*  87 */     this.lastBandLength = this.aCodec.lastBandLength + this.bCodec.lastBandLength;
/*  88 */     return band;
/*     */   }
/*     */   
/*     */   private void normalise(int[] band, Codec codecUsed) {
/*  92 */     if (codecUsed instanceof BHSDCodec) {
/*  93 */       BHSDCodec bhsd = (BHSDCodec)codecUsed;
/*  94 */       if (bhsd.isDelta()) {
/*  95 */         long cardinality = bhsd.cardinality();
/*  96 */         for (int i = 0; i < band.length; i++) {
/*  97 */           while (band[i] > bhsd.largest()) {
/*  98 */             band[i] = (int)(band[i] - cardinality);
/*     */           }
/* 100 */           while (band[i] < bhsd.smallest()) {
/* 101 */             band[i] = (int)(band[i] + cardinality);
/*     */           }
/*     */         } 
/*     */       } 
/* 105 */     } else if (codecUsed instanceof PopulationCodec) {
/* 106 */       PopulationCodec popCodec = (PopulationCodec)codecUsed;
/* 107 */       int[] favoured = (int[])popCodec.getFavoured().clone();
/* 108 */       Arrays.sort(favoured);
/* 109 */       for (int i = 0; i < band.length; i++) {
/* 110 */         boolean favouredValue = (Arrays.binarySearch(favoured, band[i]) > -1);
/* 111 */         Codec theCodec = favouredValue ? popCodec.getFavouredCodec() : popCodec.getUnfavouredCodec();
/* 112 */         if (theCodec instanceof BHSDCodec) {
/* 113 */           BHSDCodec bhsd = (BHSDCodec)theCodec;
/* 114 */           if (bhsd.isDelta()) {
/* 115 */             long cardinality = bhsd.cardinality();
/* 116 */             while (band[i] > bhsd.largest()) {
/* 117 */               band[i] = (int)(band[i] - cardinality);
/*     */             }
/* 119 */             while (band[i] < bhsd.smallest()) {
/* 120 */               band[i] = (int)(band[i] + cardinality);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 130 */     return "RunCodec[k=" + this.k + ";aCodec=" + this.aCodec + "bCodec=" + this.bCodec + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value, int last) throws Pack200Exception {
/* 135 */     throw new Pack200Exception("Must encode entire band at once with a RunCodec");
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value) throws Pack200Exception {
/* 140 */     throw new Pack200Exception("Must encode entire band at once with a RunCodec");
/*     */   }
/*     */   
/*     */   public int getK() {
/* 144 */     return this.k;
/*     */   }
/*     */   
/*     */   public Codec getACodec() {
/* 148 */     return this.aCodec;
/*     */   }
/*     */   
/*     */   public Codec getBCodec() {
/* 152 */     return this.bCodec;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\RunCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */