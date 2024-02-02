/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class PopulationCodec
/*     */   extends Codec
/*     */ {
/*     */   private final Codec favouredCodec;
/*     */   private Codec tokenCodec;
/*     */   private final Codec unfavouredCodec;
/*     */   private int l;
/*     */   private int[] favoured;
/*     */   
/*     */   public PopulationCodec(Codec favouredCodec, Codec tokenCodec, Codec unvafouredCodec) {
/*  35 */     this.favouredCodec = favouredCodec;
/*  36 */     this.tokenCodec = tokenCodec;
/*  37 */     this.unfavouredCodec = unvafouredCodec;
/*     */   }
/*     */   
/*     */   public PopulationCodec(Codec favouredCodec, int l, Codec unfavouredCodec) {
/*  41 */     if (l >= 256 || l <= 0) {
/*  42 */       throw new IllegalArgumentException("L must be between 1..255");
/*     */     }
/*  44 */     this.favouredCodec = favouredCodec;
/*  45 */     this.l = l;
/*  46 */     this.unfavouredCodec = unfavouredCodec;
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in) throws IOException, Pack200Exception {
/*  51 */     throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
/*     */   }
/*     */ 
/*     */   
/*     */   public int decode(InputStream in, long last) throws IOException, Pack200Exception {
/*  56 */     throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] decodeInts(int n, InputStream in) throws IOException, Pack200Exception {
/*  61 */     this.lastBandLength = 0;
/*  62 */     this.favoured = new int[n];
/*     */ 
/*     */ 
/*     */     
/*  66 */     int smallest = Integer.MAX_VALUE;
/*  67 */     int last = 0;
/*  68 */     int value = 0;
/*  69 */     int k = -1;
/*     */     while (true) {
/*  71 */       value = this.favouredCodec.decode(in, last);
/*  72 */       if (k > -1 && (value == smallest || value == last)) {
/*     */         break;
/*     */       }
/*  75 */       this.favoured[++k] = value;
/*  76 */       int absoluteSmallest = Math.abs(smallest);
/*  77 */       int absoluteValue = Math.abs(value);
/*  78 */       if (absoluteSmallest > absoluteValue) {
/*  79 */         smallest = value;
/*  80 */       } else if (absoluteSmallest == absoluteValue) {
/*     */         
/*  82 */         smallest = absoluteSmallest;
/*     */       } 
/*  84 */       last = value;
/*     */     } 
/*  86 */     this.lastBandLength += k;
/*     */     
/*  88 */     if (this.tokenCodec == null) {
/*  89 */       if (k < 256) {
/*  90 */         this.tokenCodec = Codec.BYTE1;
/*     */       } else {
/*     */         
/*  93 */         int b = 1;
/*  94 */         BHSDCodec codec = null;
/*  95 */         while (++b < 5) {
/*  96 */           codec = new BHSDCodec(b, 256 - this.l, 0);
/*  97 */           if (codec.encodes(k)) {
/*  98 */             this.tokenCodec = codec;
/*     */             break;
/*     */           } 
/*     */         } 
/* 102 */         if (this.tokenCodec == null) {
/* 103 */           throw new Pack200Exception("Cannot calculate token codec from " + k + " and " + this.l);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 108 */     this.lastBandLength += n;
/* 109 */     int[] result = this.tokenCodec.decodeInts(n, in);
/*     */     
/* 111 */     last = 0;
/* 112 */     for (int i = 0; i < n; i++) {
/* 113 */       int index = result[i];
/* 114 */       if (index == 0) {
/* 115 */         this.lastBandLength++;
/* 116 */         result[i] = last = this.unfavouredCodec.decode(in, last);
/*     */       } else {
/* 118 */         result[i] = this.favoured[index - 1];
/*     */       } 
/*     */     } 
/* 121 */     return result;
/*     */   }
/*     */   
/*     */   public int[] getFavoured() {
/* 125 */     return this.favoured;
/*     */   }
/*     */   
/*     */   public Codec getFavouredCodec() {
/* 129 */     return this.favouredCodec;
/*     */   }
/*     */   
/*     */   public Codec getUnfavouredCodec() {
/* 133 */     return this.unfavouredCodec;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value, int last) throws Pack200Exception {
/* 138 */     throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] encode(int value) throws Pack200Exception {
/* 143 */     throw new Pack200Exception("Population encoding does not work unless the number of elements are known");
/*     */   }
/*     */   
/*     */   public byte[] encode(int[] favoured, int[] tokens, int[] unfavoured) throws Pack200Exception {
/* 147 */     int[] favoured2 = new int[favoured.length + 1];
/* 148 */     System.arraycopy(favoured, 0, favoured2, 0, favoured.length);
/* 149 */     favoured2[favoured2.length - 1] = favoured[favoured.length - 1];
/* 150 */     byte[] favouredEncoded = this.favouredCodec.encode(favoured2);
/* 151 */     byte[] tokensEncoded = this.tokenCodec.encode(tokens);
/* 152 */     byte[] unfavouredEncoded = this.unfavouredCodec.encode(unfavoured);
/* 153 */     byte[] band = new byte[favouredEncoded.length + tokensEncoded.length + unfavouredEncoded.length];
/* 154 */     System.arraycopy(favouredEncoded, 0, band, 0, favouredEncoded.length);
/* 155 */     System.arraycopy(tokensEncoded, 0, band, favouredEncoded.length, tokensEncoded.length);
/* 156 */     System.arraycopy(unfavouredEncoded, 0, band, favouredEncoded.length + tokensEncoded.length, unfavouredEncoded.length);
/*     */     
/* 158 */     return band;
/*     */   }
/*     */   
/*     */   public Codec getTokenCodec() {
/* 162 */     return this.tokenCodec;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\PopulationCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */