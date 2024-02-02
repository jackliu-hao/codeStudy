/*     */ package cn.hutool.bloomfilter;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.HashUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.BitSet;
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
/*     */ public class BitSetBloomFilter
/*     */   implements BloomFilter
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final BitSet bitSet;
/*     */   private final int bitSetSize;
/*     */   private final int addedElements;
/*     */   private final int hashFunctionNumber;
/*     */   
/*     */   public BitSetBloomFilter(int c, int n, int k) {
/*  35 */     this.hashFunctionNumber = k;
/*  36 */     this.bitSetSize = (int)Math.ceil((c * k));
/*  37 */     this.addedElements = n;
/*  38 */     this.bitSet = new BitSet(this.bitSetSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void init(String path, String charsetName) throws IOException {
/*  51 */     init(path, CharsetUtil.charset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(String path, Charset charset) throws IOException {
/*  63 */     BufferedReader reader = FileUtil.getReader(path, charset);
/*     */     
/*     */     try {
/*     */       while (true) {
/*  67 */         String line = reader.readLine();
/*  68 */         if (line == null) {
/*     */           break;
/*     */         }
/*  71 */         add(line);
/*     */       } 
/*     */     } finally {
/*  74 */       IoUtil.close(reader);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(String str) {
/*  80 */     if (contains(str)) {
/*  81 */       return false;
/*     */     }
/*     */     
/*  84 */     int[] positions = createHashes(str, this.hashFunctionNumber);
/*  85 */     for (int value : positions) {
/*  86 */       int position = Math.abs(value % this.bitSetSize);
/*  87 */       this.bitSet.set(position, true);
/*     */     } 
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(String str) {
/* 100 */     int[] positions = createHashes(str, this.hashFunctionNumber);
/* 101 */     for (int i : positions) {
/* 102 */       int position = Math.abs(i % this.bitSetSize);
/* 103 */       if (!this.bitSet.get(position)) {
/* 104 */         return false;
/*     */       }
/*     */     } 
/* 107 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getFalsePositiveProbability() {
/* 115 */     return Math.pow(1.0D - Math.exp(-this.hashFunctionNumber * this.addedElements / this.bitSetSize), this.hashFunctionNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] createHashes(String str, int hashNumber) {
/* 126 */     int[] result = new int[hashNumber];
/* 127 */     for (int i = 0; i < hashNumber; i++) {
/* 128 */       result[i] = hash(str, i);
/*     */     }
/*     */     
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hash(String str, int k) {
/* 142 */     switch (k) {
/*     */       case 0:
/* 144 */         return HashUtil.rsHash(str);
/*     */       case 1:
/* 146 */         return HashUtil.jsHash(str);
/*     */       case 2:
/* 148 */         return HashUtil.elfHash(str);
/*     */       case 3:
/* 150 */         return HashUtil.bkdrHash(str);
/*     */       case 4:
/* 152 */         return HashUtil.apHash(str);
/*     */       case 5:
/* 154 */         return HashUtil.djbHash(str);
/*     */       case 6:
/* 156 */         return HashUtil.sdbmHash(str);
/*     */       case 7:
/* 158 */         return HashUtil.pjwHash(str);
/*     */     } 
/* 160 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\BitSetBloomFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */