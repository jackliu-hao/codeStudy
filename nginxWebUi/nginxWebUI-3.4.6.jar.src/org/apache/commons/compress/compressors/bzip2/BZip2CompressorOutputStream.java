/*      */ package org.apache.commons.compress.compressors.bzip2;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BZip2CompressorOutputStream
/*      */   extends CompressorOutputStream
/*      */   implements BZip2Constants
/*      */ {
/*      */   public static final int MIN_BLOCKSIZE = 1;
/*      */   public static final int MAX_BLOCKSIZE = 9;
/*      */   private static final int GREATER_ICOST = 15;
/*      */   private static final int LESSER_ICOST = 0;
/*      */   private int last;
/*      */   private final int blockSize100k;
/*      */   private int bsBuff;
/*      */   private int bsLive;
/*      */   
/*      */   private static void hbMakeCodeLengths(byte[] len, int[] freq, Data dat, int alphaSize, int maxLen) {
/*  149 */     int[] heap = dat.heap;
/*  150 */     int[] weight = dat.weight;
/*  151 */     int[] parent = dat.parent;
/*      */     
/*  153 */     for (int i = alphaSize; --i >= 0;) {
/*  154 */       weight[i + 1] = ((freq[i] == 0) ? 1 : freq[i]) << 8;
/*      */     }
/*      */     
/*  157 */     for (boolean tooLong = true; tooLong; ) {
/*  158 */       tooLong = false;
/*      */       
/*  160 */       int nNodes = alphaSize;
/*  161 */       int nHeap = 0;
/*  162 */       heap[0] = 0;
/*  163 */       weight[0] = 0;
/*  164 */       parent[0] = -2;
/*      */       int j;
/*  166 */       for (j = 1; j <= alphaSize; j++) {
/*  167 */         parent[j] = -1;
/*  168 */         nHeap++;
/*  169 */         heap[nHeap] = j;
/*      */         
/*  171 */         int zz = nHeap;
/*  172 */         int tmp = heap[zz];
/*  173 */         while (weight[tmp] < weight[heap[zz >> 1]]) {
/*  174 */           heap[zz] = heap[zz >> 1];
/*  175 */           zz >>= 1;
/*      */         } 
/*  177 */         heap[zz] = tmp;
/*      */       } 
/*      */       
/*  180 */       while (nHeap > 1) {
/*  181 */         int n1 = heap[1];
/*  182 */         heap[1] = heap[nHeap];
/*  183 */         nHeap--;
/*      */         
/*  185 */         int yy = 0;
/*  186 */         int zz = 1;
/*  187 */         int tmp = heap[1];
/*      */         
/*      */         while (true) {
/*  190 */           yy = zz << 1;
/*      */           
/*  192 */           if (yy > nHeap) {
/*      */             break;
/*      */           }
/*      */           
/*  196 */           if (yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]])
/*      */           {
/*  198 */             yy++;
/*      */           }
/*      */           
/*  201 */           if (weight[tmp] < weight[heap[yy]]) {
/*      */             break;
/*      */           }
/*      */           
/*  205 */           heap[zz] = heap[yy];
/*  206 */           zz = yy;
/*      */         } 
/*      */         
/*  209 */         heap[zz] = tmp;
/*      */         
/*  211 */         int n2 = heap[1];
/*  212 */         heap[1] = heap[nHeap];
/*  213 */         nHeap--;
/*      */         
/*  215 */         yy = 0;
/*  216 */         zz = 1;
/*  217 */         tmp = heap[1];
/*      */         
/*      */         while (true) {
/*  220 */           yy = zz << 1;
/*      */           
/*  222 */           if (yy > nHeap) {
/*      */             break;
/*      */           }
/*      */           
/*  226 */           if (yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]])
/*      */           {
/*  228 */             yy++;
/*      */           }
/*      */           
/*  231 */           if (weight[tmp] < weight[heap[yy]]) {
/*      */             break;
/*      */           }
/*      */           
/*  235 */           heap[zz] = heap[yy];
/*  236 */           zz = yy;
/*      */         } 
/*      */         
/*  239 */         heap[zz] = tmp;
/*  240 */         nNodes++;
/*  241 */         parent[n2] = nNodes; parent[n1] = nNodes;
/*      */         
/*  243 */         int weight_n1 = weight[n1];
/*  244 */         int weight_n2 = weight[n2];
/*  245 */         weight[nNodes] = (weight_n1 & 0xFFFFFF00) + (weight_n2 & 0xFFFFFF00) | 1 + (((weight_n1 & 0xFF) > (weight_n2 & 0xFF)) ? (weight_n1 & 0xFF) : (weight_n2 & 0xFF));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  252 */         parent[nNodes] = -1;
/*  253 */         nHeap++;
/*  254 */         heap[nHeap] = nNodes;
/*      */         
/*  256 */         tmp = 0;
/*  257 */         zz = nHeap;
/*  258 */         tmp = heap[zz];
/*  259 */         int weight_tmp = weight[tmp];
/*  260 */         while (weight_tmp < weight[heap[zz >> 1]]) {
/*  261 */           heap[zz] = heap[zz >> 1];
/*  262 */           zz >>= 1;
/*      */         } 
/*  264 */         heap[zz] = tmp;
/*      */       } 
/*      */ 
/*      */       
/*  268 */       for (j = 1; j <= alphaSize; j++) {
/*  269 */         int m = 0;
/*  270 */         int k = j;
/*      */         int parent_k;
/*  272 */         while ((parent_k = parent[k]) >= 0) {
/*  273 */           k = parent_k;
/*  274 */           m++;
/*      */         } 
/*      */         
/*  277 */         len[j - 1] = (byte)m;
/*  278 */         if (m > maxLen) {
/*  279 */           tooLong = true;
/*      */         }
/*      */       } 
/*      */       
/*  283 */       if (tooLong) {
/*  284 */         for (j = 1; j < alphaSize; j++) {
/*  285 */           int k = weight[j] >> 8;
/*  286 */           k = 1 + (k >> 1);
/*  287 */           weight[j] = k << 8;
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  306 */   private final CRC crc = new CRC();
/*      */   
/*      */   private int nInUse;
/*      */   
/*      */   private int nMTF;
/*      */   
/*  312 */   private int currentChar = -1;
/*      */ 
/*      */   
/*      */   private int runLength;
/*      */ 
/*      */   
/*      */   private int blockCRC;
/*      */ 
/*      */   
/*      */   private int combinedCRC;
/*      */ 
/*      */   
/*      */   private final int allowableBlockSize;
/*      */ 
/*      */   
/*      */   private Data data;
/*      */ 
/*      */   
/*      */   private BlockSort blockSorter;
/*      */ 
/*      */   
/*      */   private OutputStream out;
/*      */ 
/*      */   
/*      */   private volatile boolean closed;
/*      */ 
/*      */ 
/*      */   
/*      */   public static int chooseBlockSize(long inputLength) {
/*  341 */     return (inputLength > 0L) ? 
/*  342 */       (int)Math.min(inputLength / 132000L + 1L, 9L) : 9;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BZip2CompressorOutputStream(OutputStream out) throws IOException {
/*  358 */     this(out, 9);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BZip2CompressorOutputStream(OutputStream out, int blockSize) throws IOException {
/*  380 */     if (blockSize < 1) {
/*  381 */       throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
/*      */     }
/*  383 */     if (blockSize > 9) {
/*  384 */       throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
/*      */     }
/*      */     
/*  387 */     this.blockSize100k = blockSize;
/*  388 */     this.out = out;
/*      */ 
/*      */     
/*  391 */     this.allowableBlockSize = this.blockSize100k * 100000 - 20;
/*  392 */     init();
/*      */   }
/*      */ 
/*      */   
/*      */   public void write(int b) throws IOException {
/*  397 */     if (this.closed) {
/*  398 */       throw new IOException("Closed");
/*      */     }
/*  400 */     write0(b);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeRun() throws IOException {
/*  417 */     int lastShadow = this.last;
/*      */     
/*  419 */     if (lastShadow < this.allowableBlockSize) {
/*  420 */       int currentCharShadow = this.currentChar;
/*  421 */       Data dataShadow = this.data;
/*  422 */       dataShadow.inUse[currentCharShadow] = true;
/*  423 */       byte ch = (byte)currentCharShadow;
/*      */       
/*  425 */       int runLengthShadow = this.runLength;
/*  426 */       this.crc.updateCRC(currentCharShadow, runLengthShadow);
/*      */       
/*  428 */       switch (runLengthShadow) {
/*      */         case 1:
/*  430 */           dataShadow.block[lastShadow + 2] = ch;
/*  431 */           this.last = lastShadow + 1;
/*      */           return;
/*      */         
/*      */         case 2:
/*  435 */           dataShadow.block[lastShadow + 2] = ch;
/*  436 */           dataShadow.block[lastShadow + 3] = ch;
/*  437 */           this.last = lastShadow + 2;
/*      */           return;
/*      */         
/*      */         case 3:
/*  441 */           block = dataShadow.block;
/*  442 */           block[lastShadow + 2] = ch;
/*  443 */           block[lastShadow + 3] = ch;
/*  444 */           block[lastShadow + 4] = ch;
/*  445 */           this.last = lastShadow + 3;
/*      */           return;
/*      */       } 
/*      */ 
/*      */       
/*  450 */       runLengthShadow -= 4;
/*  451 */       dataShadow.inUse[runLengthShadow] = true;
/*  452 */       byte[] block = dataShadow.block;
/*  453 */       block[lastShadow + 2] = ch;
/*  454 */       block[lastShadow + 3] = ch;
/*  455 */       block[lastShadow + 4] = ch;
/*  456 */       block[lastShadow + 5] = ch;
/*  457 */       block[lastShadow + 6] = (byte)runLengthShadow;
/*  458 */       this.last = lastShadow + 5;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  464 */       endBlock();
/*  465 */       initBlock();
/*  466 */       writeRun();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void finalize() throws Throwable {
/*  475 */     if (!this.closed) {
/*  476 */       System.err.println("Unclosed BZip2CompressorOutputStream detected, will *not* close it");
/*      */     }
/*  478 */     super.finalize();
/*      */   }
/*      */ 
/*      */   
/*      */   public void finish() throws IOException {
/*  483 */     if (!this.closed) {
/*  484 */       this.closed = true;
/*      */       try {
/*  486 */         if (this.runLength > 0) {
/*  487 */           writeRun();
/*      */         }
/*  489 */         this.currentChar = -1;
/*  490 */         endBlock();
/*  491 */         endCompression();
/*      */       } finally {
/*  493 */         this.out = null;
/*  494 */         this.blockSorter = null;
/*  495 */         this.data = null;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  502 */     if (!this.closed) {
/*  503 */       try (OutputStream outShadow = this.out) {
/*  504 */         finish();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void flush() throws IOException {
/*  511 */     OutputStream outShadow = this.out;
/*  512 */     if (outShadow != null) {
/*  513 */       outShadow.flush();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void init() throws IOException {
/*  524 */     bsPutUByte(66);
/*  525 */     bsPutUByte(90);
/*      */     
/*  527 */     this.data = new Data(this.blockSize100k);
/*  528 */     this.blockSorter = new BlockSort(this.data);
/*      */ 
/*      */     
/*  531 */     bsPutUByte(104);
/*  532 */     bsPutUByte(48 + this.blockSize100k);
/*      */     
/*  534 */     this.combinedCRC = 0;
/*  535 */     initBlock();
/*      */   }
/*      */ 
/*      */   
/*      */   private void initBlock() {
/*  540 */     this.crc.initializeCRC();
/*  541 */     this.last = -1;
/*      */ 
/*      */     
/*  544 */     boolean[] inUse = this.data.inUse;
/*  545 */     for (int i = 256; --i >= 0;) {
/*  546 */       inUse[i] = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void endBlock() throws IOException {
/*  552 */     this.blockCRC = this.crc.getFinalCRC();
/*  553 */     this.combinedCRC = this.combinedCRC << 1 | this.combinedCRC >>> 31;
/*  554 */     this.combinedCRC ^= this.blockCRC;
/*      */ 
/*      */     
/*  557 */     if (this.last == -1) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  562 */     blockSort();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  575 */     bsPutUByte(49);
/*  576 */     bsPutUByte(65);
/*  577 */     bsPutUByte(89);
/*  578 */     bsPutUByte(38);
/*  579 */     bsPutUByte(83);
/*  580 */     bsPutUByte(89);
/*      */ 
/*      */     
/*  583 */     bsPutInt(this.blockCRC);
/*      */ 
/*      */     
/*  586 */     bsW(1, 0);
/*      */ 
/*      */     
/*  589 */     moveToFrontCodeAndSend();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void endCompression() throws IOException {
/*  599 */     bsPutUByte(23);
/*  600 */     bsPutUByte(114);
/*  601 */     bsPutUByte(69);
/*  602 */     bsPutUByte(56);
/*  603 */     bsPutUByte(80);
/*  604 */     bsPutUByte(144);
/*      */     
/*  606 */     bsPutInt(this.combinedCRC);
/*  607 */     bsFinishedWithStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int getBlockSize() {
/*  615 */     return this.blockSize100k;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void write(byte[] buf, int offs, int len) throws IOException {
/*  621 */     if (offs < 0) {
/*  622 */       throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
/*      */     }
/*  624 */     if (len < 0) {
/*  625 */       throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
/*      */     }
/*  627 */     if (offs + len > buf.length) {
/*  628 */       throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
/*      */     }
/*      */ 
/*      */     
/*  632 */     if (this.closed) {
/*  633 */       throw new IOException("Stream closed");
/*      */     }
/*      */     
/*  636 */     for (int hi = offs + len; offs < hi;) {
/*  637 */       write0(buf[offs++]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void write0(int b) throws IOException {
/*  646 */     if (this.currentChar != -1) {
/*  647 */       b &= 0xFF;
/*  648 */       if (this.currentChar == b) {
/*  649 */         if (++this.runLength > 254) {
/*  650 */           writeRun();
/*  651 */           this.currentChar = -1;
/*  652 */           this.runLength = 0;
/*      */         } 
/*      */       } else {
/*      */         
/*  656 */         writeRun();
/*  657 */         this.runLength = 1;
/*  658 */         this.currentChar = b;
/*      */       } 
/*      */     } else {
/*  661 */       this.currentChar = b & 0xFF;
/*  662 */       this.runLength++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize) {
/*  669 */     int vec = 0;
/*  670 */     for (int n = minLen; n <= maxLen; n++) {
/*  671 */       for (int i = 0; i < alphaSize; i++) {
/*  672 */         if ((length[i] & 0xFF) == n) {
/*  673 */           code[i] = vec;
/*  674 */           vec++;
/*      */         } 
/*      */       } 
/*  677 */       vec <<= 1;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void bsFinishedWithStream() throws IOException {
/*  682 */     while (this.bsLive > 0) {
/*  683 */       int ch = this.bsBuff >> 24;
/*  684 */       this.out.write(ch);
/*  685 */       this.bsBuff <<= 8;
/*  686 */       this.bsLive -= 8;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void bsW(int n, int v) throws IOException {
/*  691 */     OutputStream outShadow = this.out;
/*  692 */     int bsLiveShadow = this.bsLive;
/*  693 */     int bsBuffShadow = this.bsBuff;
/*      */     
/*  695 */     while (bsLiveShadow >= 8) {
/*  696 */       outShadow.write(bsBuffShadow >> 24);
/*  697 */       bsBuffShadow <<= 8;
/*  698 */       bsLiveShadow -= 8;
/*      */     } 
/*      */     
/*  701 */     this.bsBuff = bsBuffShadow | v << 32 - bsLiveShadow - n;
/*  702 */     this.bsLive = bsLiveShadow + n;
/*      */   }
/*      */   
/*      */   private void bsPutUByte(int c) throws IOException {
/*  706 */     bsW(8, c);
/*      */   }
/*      */   
/*      */   private void bsPutInt(int u) throws IOException {
/*  710 */     bsW(8, u >> 24 & 0xFF);
/*  711 */     bsW(8, u >> 16 & 0xFF);
/*  712 */     bsW(8, u >> 8 & 0xFF);
/*  713 */     bsW(8, u & 0xFF);
/*      */   }
/*      */   
/*      */   private void sendMTFValues() throws IOException {
/*  717 */     byte[][] len = this.data.sendMTFValues_len;
/*  718 */     int alphaSize = this.nInUse + 2;
/*      */     
/*  720 */     for (int t = 6; --t >= 0; ) {
/*  721 */       byte[] len_t = len[t];
/*  722 */       for (int v = alphaSize; --v >= 0;) {
/*  723 */         len_t[v] = 15;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  729 */     int nGroups = (this.nMTF < 200) ? 2 : ((this.nMTF < 600) ? 3 : ((this.nMTF < 1200) ? 4 : ((this.nMTF < 2400) ? 5 : 6)));
/*      */ 
/*      */ 
/*      */     
/*  733 */     sendMTFValues0(nGroups, alphaSize);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  738 */     int nSelectors = sendMTFValues1(nGroups, alphaSize);
/*      */ 
/*      */     
/*  741 */     sendMTFValues2(nGroups, nSelectors);
/*      */ 
/*      */     
/*  744 */     sendMTFValues3(nGroups, alphaSize);
/*      */ 
/*      */     
/*  747 */     sendMTFValues4();
/*      */ 
/*      */     
/*  750 */     sendMTFValues5(nGroups, nSelectors);
/*      */ 
/*      */     
/*  753 */     sendMTFValues6(nGroups, alphaSize);
/*      */ 
/*      */     
/*  756 */     sendMTFValues7();
/*      */   }
/*      */   
/*      */   private void sendMTFValues0(int nGroups, int alphaSize) {
/*  760 */     byte[][] len = this.data.sendMTFValues_len;
/*  761 */     int[] mtfFreq = this.data.mtfFreq;
/*      */     
/*  763 */     int remF = this.nMTF;
/*  764 */     int gs = 0;
/*      */     
/*  766 */     for (int nPart = nGroups; nPart > 0; nPart--) {
/*  767 */       int tFreq = remF / nPart;
/*  768 */       int ge = gs - 1;
/*  769 */       int aFreq = 0;
/*      */       
/*  771 */       for (int a = alphaSize - 1; aFreq < tFreq && ge < a;) {
/*  772 */         aFreq += mtfFreq[++ge];
/*      */       }
/*      */       
/*  775 */       if (ge > gs && nPart != nGroups && nPart != 1 && (nGroups - nPart & 0x1) != 0)
/*      */       {
/*  777 */         aFreq -= mtfFreq[ge--];
/*      */       }
/*      */       
/*  780 */       byte[] len_np = len[nPart - 1];
/*  781 */       for (int v = alphaSize; --v >= 0; ) {
/*  782 */         if (v >= gs && v <= ge) {
/*  783 */           len_np[v] = 0; continue;
/*      */         } 
/*  785 */         len_np[v] = 15;
/*      */       } 
/*      */ 
/*      */       
/*  789 */       gs = ge + 1;
/*  790 */       remF -= aFreq;
/*      */     } 
/*      */   }
/*      */   
/*      */   private int sendMTFValues1(int nGroups, int alphaSize) {
/*  795 */     Data dataShadow = this.data;
/*  796 */     int[][] rfreq = dataShadow.sendMTFValues_rfreq;
/*  797 */     int[] fave = dataShadow.sendMTFValues_fave;
/*  798 */     short[] cost = dataShadow.sendMTFValues_cost;
/*  799 */     char[] sfmap = dataShadow.sfmap;
/*  800 */     byte[] selector = dataShadow.selector;
/*  801 */     byte[][] len = dataShadow.sendMTFValues_len;
/*  802 */     byte[] len_0 = len[0];
/*  803 */     byte[] len_1 = len[1];
/*  804 */     byte[] len_2 = len[2];
/*  805 */     byte[] len_3 = len[3];
/*  806 */     byte[] len_4 = len[4];
/*  807 */     byte[] len_5 = len[5];
/*  808 */     int nMTFShadow = this.nMTF;
/*      */     
/*  810 */     int nSelectors = 0;
/*      */     
/*  812 */     for (int iter = 0; iter < 4; iter++) {
/*  813 */       for (int i = nGroups; --i >= 0; ) {
/*  814 */         fave[i] = 0;
/*  815 */         int[] rfreqt = rfreq[i];
/*  816 */         for (int j = alphaSize; --j >= 0;) {
/*  817 */           rfreqt[j] = 0;
/*      */         }
/*      */       } 
/*      */       
/*  821 */       nSelectors = 0;
/*      */       int gs;
/*  823 */       for (gs = 0; gs < this.nMTF; ) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  831 */         int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
/*      */         
/*  833 */         if (nGroups == 6) {
/*      */ 
/*      */           
/*  836 */           short cost0 = 0;
/*  837 */           short cost1 = 0;
/*  838 */           short cost2 = 0;
/*  839 */           short cost3 = 0;
/*  840 */           short cost4 = 0;
/*  841 */           short cost5 = 0;
/*      */           
/*  843 */           for (int m = gs; m <= ge; m++) {
/*  844 */             int icv = sfmap[m];
/*  845 */             cost0 = (short)(cost0 + (len_0[icv] & 0xFF));
/*  846 */             cost1 = (short)(cost1 + (len_1[icv] & 0xFF));
/*  847 */             cost2 = (short)(cost2 + (len_2[icv] & 0xFF));
/*  848 */             cost3 = (short)(cost3 + (len_3[icv] & 0xFF));
/*  849 */             cost4 = (short)(cost4 + (len_4[icv] & 0xFF));
/*  850 */             cost5 = (short)(cost5 + (len_5[icv] & 0xFF));
/*      */           } 
/*      */           
/*  853 */           cost[0] = cost0;
/*  854 */           cost[1] = cost1;
/*  855 */           cost[2] = cost2;
/*  856 */           cost[3] = cost3;
/*  857 */           cost[4] = cost4;
/*  858 */           cost[5] = cost5;
/*      */         } else {
/*      */           
/*  861 */           for (int n = nGroups; --n >= 0;) {
/*  862 */             cost[n] = 0;
/*      */           }
/*      */           
/*  865 */           for (int m = gs; m <= ge; m++) {
/*  866 */             int icv = sfmap[m];
/*  867 */             for (int i1 = nGroups; --i1 >= 0;) {
/*  868 */               cost[i1] = (short)(cost[i1] + (len[i1][icv] & 0xFF));
/*      */             }
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  877 */         int bt = -1;
/*  878 */         for (int j = nGroups, bc = 999999999; --j >= 0; ) {
/*  879 */           int cost_t = cost[j];
/*  880 */           if (cost_t < bc) {
/*  881 */             bc = cost_t;
/*  882 */             bt = j;
/*      */           } 
/*      */         } 
/*      */         
/*  886 */         fave[bt] = fave[bt] + 1;
/*  887 */         selector[nSelectors] = (byte)bt;
/*  888 */         nSelectors++;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  893 */         int[] rfreq_bt = rfreq[bt];
/*  894 */         for (int k = gs; k <= ge; k++) {
/*  895 */           rfreq_bt[sfmap[k]] = rfreq_bt[sfmap[k]] + 1;
/*      */         }
/*      */         
/*  898 */         gs = ge + 1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  904 */       for (int t = 0; t < nGroups; t++) {
/*  905 */         hbMakeCodeLengths(len[t], rfreq[t], this.data, alphaSize, 20);
/*      */       }
/*      */     } 
/*      */     
/*  909 */     return nSelectors;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendMTFValues2(int nGroups, int nSelectors) {
/*  915 */     Data dataShadow = this.data;
/*  916 */     byte[] pos = dataShadow.sendMTFValues2_pos;
/*      */     int i;
/*  918 */     for (i = nGroups; --i >= 0;) {
/*  919 */       pos[i] = (byte)i;
/*      */     }
/*      */     
/*  922 */     for (i = 0; i < nSelectors; i++) {
/*  923 */       byte ll_i = dataShadow.selector[i];
/*  924 */       byte tmp = pos[0];
/*  925 */       int j = 0;
/*      */       
/*  927 */       while (ll_i != tmp) {
/*  928 */         j++;
/*  929 */         byte tmp2 = tmp;
/*  930 */         tmp = pos[j];
/*  931 */         pos[j] = tmp2;
/*      */       } 
/*      */       
/*  934 */       pos[0] = tmp;
/*  935 */       dataShadow.selectorMtf[i] = (byte)j;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sendMTFValues3(int nGroups, int alphaSize) {
/*  940 */     int[][] code = this.data.sendMTFValues_code;
/*  941 */     byte[][] len = this.data.sendMTFValues_len;
/*      */     
/*  943 */     for (int t = 0; t < nGroups; t++) {
/*  944 */       int minLen = 32;
/*  945 */       int maxLen = 0;
/*  946 */       byte[] len_t = len[t];
/*  947 */       for (int i = alphaSize; --i >= 0; ) {
/*  948 */         int l = len_t[i] & 0xFF;
/*  949 */         if (l > maxLen) {
/*  950 */           maxLen = l;
/*      */         }
/*  952 */         if (l < minLen) {
/*  953 */           minLen = l;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  960 */       hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sendMTFValues4() throws IOException {
/*  965 */     boolean[] inUse = this.data.inUse;
/*  966 */     boolean[] inUse16 = this.data.sentMTFValues4_inUse16;
/*      */     int i;
/*  968 */     for (i = 16; --i >= 0; ) {
/*  969 */       inUse16[i] = false;
/*  970 */       int i16 = i * 16;
/*  971 */       for (int k = 16; --k >= 0;) {
/*  972 */         if (inUse[i16 + k]) {
/*  973 */           inUse16[i] = true;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  979 */     for (i = 0; i < 16; i++) {
/*  980 */       bsW(1, inUse16[i] ? 1 : 0);
/*      */     }
/*      */     
/*  983 */     OutputStream outShadow = this.out;
/*  984 */     int bsLiveShadow = this.bsLive;
/*  985 */     int bsBuffShadow = this.bsBuff;
/*      */     
/*  987 */     for (int j = 0; j < 16; j++) {
/*  988 */       if (inUse16[j]) {
/*  989 */         int i16 = j * 16;
/*  990 */         for (int k = 0; k < 16; k++) {
/*      */           
/*  992 */           while (bsLiveShadow >= 8) {
/*  993 */             outShadow.write(bsBuffShadow >> 24);
/*  994 */             bsBuffShadow <<= 8;
/*  995 */             bsLiveShadow -= 8;
/*      */           } 
/*  997 */           if (inUse[i16 + k]) {
/*  998 */             bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
/*      */           }
/* 1000 */           bsLiveShadow++;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1005 */     this.bsBuff = bsBuffShadow;
/* 1006 */     this.bsLive = bsLiveShadow;
/*      */   }
/*      */ 
/*      */   
/*      */   private void sendMTFValues5(int nGroups, int nSelectors) throws IOException {
/* 1011 */     bsW(3, nGroups);
/* 1012 */     bsW(15, nSelectors);
/*      */     
/* 1014 */     OutputStream outShadow = this.out;
/* 1015 */     byte[] selectorMtf = this.data.selectorMtf;
/*      */     
/* 1017 */     int bsLiveShadow = this.bsLive;
/* 1018 */     int bsBuffShadow = this.bsBuff;
/*      */     
/* 1020 */     for (int i = 0; i < nSelectors; i++) {
/* 1021 */       for (int j = 0, hj = selectorMtf[i] & 0xFF; j < hj; j++) {
/*      */         
/* 1023 */         while (bsLiveShadow >= 8) {
/* 1024 */           outShadow.write(bsBuffShadow >> 24);
/* 1025 */           bsBuffShadow <<= 8;
/* 1026 */           bsLiveShadow -= 8;
/*      */         } 
/* 1028 */         bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
/* 1029 */         bsLiveShadow++;
/*      */       } 
/*      */ 
/*      */       
/* 1033 */       while (bsLiveShadow >= 8) {
/* 1034 */         outShadow.write(bsBuffShadow >> 24);
/* 1035 */         bsBuffShadow <<= 8;
/* 1036 */         bsLiveShadow -= 8;
/*      */       } 
/*      */       
/* 1039 */       bsLiveShadow++;
/*      */     } 
/*      */     
/* 1042 */     this.bsBuff = bsBuffShadow;
/* 1043 */     this.bsLive = bsLiveShadow;
/*      */   }
/*      */ 
/*      */   
/*      */   private void sendMTFValues6(int nGroups, int alphaSize) throws IOException {
/* 1048 */     byte[][] len = this.data.sendMTFValues_len;
/* 1049 */     OutputStream outShadow = this.out;
/*      */     
/* 1051 */     int bsLiveShadow = this.bsLive;
/* 1052 */     int bsBuffShadow = this.bsBuff;
/*      */     
/* 1054 */     for (int t = 0; t < nGroups; t++) {
/* 1055 */       byte[] len_t = len[t];
/* 1056 */       int curr = len_t[0] & 0xFF;
/*      */ 
/*      */       
/* 1059 */       while (bsLiveShadow >= 8) {
/* 1060 */         outShadow.write(bsBuffShadow >> 24);
/* 1061 */         bsBuffShadow <<= 8;
/* 1062 */         bsLiveShadow -= 8;
/*      */       } 
/* 1064 */       bsBuffShadow |= curr << 32 - bsLiveShadow - 5;
/* 1065 */       bsLiveShadow += 5;
/*      */       
/* 1067 */       for (int i = 0; i < alphaSize; i++) {
/* 1068 */         int lti = len_t[i] & 0xFF;
/* 1069 */         while (curr < lti) {
/*      */           
/* 1071 */           while (bsLiveShadow >= 8) {
/* 1072 */             outShadow.write(bsBuffShadow >> 24);
/* 1073 */             bsBuffShadow <<= 8;
/* 1074 */             bsLiveShadow -= 8;
/*      */           } 
/* 1076 */           bsBuffShadow |= 2 << 32 - bsLiveShadow - 2;
/* 1077 */           bsLiveShadow += 2;
/*      */           
/* 1079 */           curr++;
/*      */         } 
/*      */         
/* 1082 */         while (curr > lti) {
/*      */           
/* 1084 */           while (bsLiveShadow >= 8) {
/* 1085 */             outShadow.write(bsBuffShadow >> 24);
/* 1086 */             bsBuffShadow <<= 8;
/* 1087 */             bsLiveShadow -= 8;
/*      */           } 
/* 1089 */           bsBuffShadow |= 3 << 32 - bsLiveShadow - 2;
/* 1090 */           bsLiveShadow += 2;
/*      */           
/* 1092 */           curr--;
/*      */         } 
/*      */ 
/*      */         
/* 1096 */         while (bsLiveShadow >= 8) {
/* 1097 */           outShadow.write(bsBuffShadow >> 24);
/* 1098 */           bsBuffShadow <<= 8;
/* 1099 */           bsLiveShadow -= 8;
/*      */         } 
/*      */         
/* 1102 */         bsLiveShadow++;
/*      */       } 
/*      */     } 
/*      */     
/* 1106 */     this.bsBuff = bsBuffShadow;
/* 1107 */     this.bsLive = bsLiveShadow;
/*      */   }
/*      */   
/*      */   private void sendMTFValues7() throws IOException {
/* 1111 */     Data dataShadow = this.data;
/* 1112 */     byte[][] len = dataShadow.sendMTFValues_len;
/* 1113 */     int[][] code = dataShadow.sendMTFValues_code;
/* 1114 */     OutputStream outShadow = this.out;
/* 1115 */     byte[] selector = dataShadow.selector;
/* 1116 */     char[] sfmap = dataShadow.sfmap;
/* 1117 */     int nMTFShadow = this.nMTF;
/*      */     
/* 1119 */     int selCtr = 0;
/*      */     
/* 1121 */     int bsLiveShadow = this.bsLive;
/* 1122 */     int bsBuffShadow = this.bsBuff;
/*      */     
/* 1124 */     for (int gs = 0; gs < nMTFShadow; ) {
/* 1125 */       int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
/* 1126 */       int selector_selCtr = selector[selCtr] & 0xFF;
/* 1127 */       int[] code_selCtr = code[selector_selCtr];
/* 1128 */       byte[] len_selCtr = len[selector_selCtr];
/*      */       
/* 1130 */       while (gs <= ge) {
/* 1131 */         int sfmap_i = sfmap[gs];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1137 */         while (bsLiveShadow >= 8) {
/* 1138 */           outShadow.write(bsBuffShadow >> 24);
/* 1139 */           bsBuffShadow <<= 8;
/* 1140 */           bsLiveShadow -= 8;
/*      */         } 
/* 1142 */         int n = len_selCtr[sfmap_i] & 0xFF;
/* 1143 */         bsBuffShadow |= code_selCtr[sfmap_i] << 32 - bsLiveShadow - n;
/* 1144 */         bsLiveShadow += n;
/*      */         
/* 1146 */         gs++;
/*      */       } 
/*      */       
/* 1149 */       gs = ge + 1;
/* 1150 */       selCtr++;
/*      */     } 
/*      */     
/* 1153 */     this.bsBuff = bsBuffShadow;
/* 1154 */     this.bsLive = bsLiveShadow;
/*      */   }
/*      */   
/*      */   private void moveToFrontCodeAndSend() throws IOException {
/* 1158 */     bsW(24, this.data.origPtr);
/* 1159 */     generateMTFValues();
/* 1160 */     sendMTFValues();
/*      */   }
/*      */   
/*      */   private void blockSort() {
/* 1164 */     this.blockSorter.blockSort(this.data, this.last);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void generateMTFValues() {
/* 1175 */     int lastShadow = this.last;
/* 1176 */     Data dataShadow = this.data;
/* 1177 */     boolean[] inUse = dataShadow.inUse;
/* 1178 */     byte[] block = dataShadow.block;
/* 1179 */     int[] fmap = dataShadow.fmap;
/* 1180 */     char[] sfmap = dataShadow.sfmap;
/* 1181 */     int[] mtfFreq = dataShadow.mtfFreq;
/* 1182 */     byte[] unseqToSeq = dataShadow.unseqToSeq;
/* 1183 */     byte[] yy = dataShadow.generateMTFValues_yy;
/*      */ 
/*      */     
/* 1186 */     int nInUseShadow = 0;
/* 1187 */     for (int i = 0; i < 256; i++) {
/* 1188 */       if (inUse[i]) {
/* 1189 */         unseqToSeq[i] = (byte)nInUseShadow;
/* 1190 */         nInUseShadow++;
/*      */       } 
/*      */     } 
/* 1193 */     this.nInUse = nInUseShadow;
/*      */     
/* 1195 */     int eob = nInUseShadow + 1;
/*      */     int j;
/* 1197 */     for (j = eob; j >= 0; j--) {
/* 1198 */       mtfFreq[j] = 0;
/*      */     }
/*      */     
/* 1201 */     for (j = nInUseShadow; --j >= 0;) {
/* 1202 */       yy[j] = (byte)j;
/*      */     }
/*      */     
/* 1205 */     int wr = 0;
/* 1206 */     int zPend = 0;
/*      */     
/* 1208 */     for (int k = 0; k <= lastShadow; k++) {
/* 1209 */       byte ll_i = unseqToSeq[block[fmap[k]] & 0xFF];
/* 1210 */       byte tmp = yy[0];
/* 1211 */       int m = 0;
/*      */       
/* 1213 */       while (ll_i != tmp) {
/* 1214 */         m++;
/* 1215 */         byte tmp2 = tmp;
/* 1216 */         tmp = yy[m];
/* 1217 */         yy[m] = tmp2;
/*      */       } 
/* 1219 */       yy[0] = tmp;
/*      */       
/* 1221 */       if (m == 0) {
/* 1222 */         zPend++;
/*      */       } else {
/* 1224 */         if (zPend > 0) {
/* 1225 */           zPend--;
/*      */           while (true) {
/* 1227 */             if ((zPend & 0x1) == 0) {
/* 1228 */               sfmap[wr] = Character.MIN_VALUE;
/* 1229 */               wr++;
/* 1230 */               mtfFreq[0] = mtfFreq[0] + 1;
/*      */             } else {
/* 1232 */               sfmap[wr] = '\001';
/* 1233 */               wr++;
/* 1234 */               mtfFreq[1] = mtfFreq[1] + 1;
/*      */             } 
/*      */             
/* 1237 */             if (zPend < 2) {
/*      */               break;
/*      */             }
/* 1240 */             zPend = zPend - 2 >> 1;
/*      */           } 
/* 1242 */           zPend = 0;
/*      */         } 
/* 1244 */         sfmap[wr] = (char)(m + 1);
/* 1245 */         wr++;
/* 1246 */         mtfFreq[m + 1] = mtfFreq[m + 1] + 1;
/*      */       } 
/*      */     } 
/*      */     
/* 1250 */     if (zPend > 0) {
/* 1251 */       zPend--;
/*      */       while (true) {
/* 1253 */         if ((zPend & 0x1) == 0) {
/* 1254 */           sfmap[wr] = Character.MIN_VALUE;
/* 1255 */           wr++;
/* 1256 */           mtfFreq[0] = mtfFreq[0] + 1;
/*      */         } else {
/* 1258 */           sfmap[wr] = '\001';
/* 1259 */           wr++;
/* 1260 */           mtfFreq[1] = mtfFreq[1] + 1;
/*      */         } 
/*      */         
/* 1263 */         if (zPend < 2) {
/*      */           break;
/*      */         }
/* 1266 */         zPend = zPend - 2 >> 1;
/*      */       } 
/*      */     } 
/*      */     
/* 1270 */     sfmap[wr] = (char)eob;
/* 1271 */     mtfFreq[eob] = mtfFreq[eob] + 1;
/* 1272 */     this.nMTF = wr + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static final class Data
/*      */   {
/* 1279 */     final boolean[] inUse = new boolean[256];
/* 1280 */     final byte[] unseqToSeq = new byte[256];
/* 1281 */     final int[] mtfFreq = new int[258];
/* 1282 */     final byte[] selector = new byte[18002];
/* 1283 */     final byte[] selectorMtf = new byte[18002];
/*      */     
/* 1285 */     final byte[] generateMTFValues_yy = new byte[256];
/* 1286 */     final byte[][] sendMTFValues_len = new byte[6][258];
/*      */     
/* 1288 */     final int[][] sendMTFValues_rfreq = new int[6][258];
/*      */     
/* 1290 */     final int[] sendMTFValues_fave = new int[6];
/* 1291 */     final short[] sendMTFValues_cost = new short[6];
/* 1292 */     final int[][] sendMTFValues_code = new int[6][258];
/*      */     
/* 1294 */     final byte[] sendMTFValues2_pos = new byte[6];
/* 1295 */     final boolean[] sentMTFValues4_inUse16 = new boolean[16];
/*      */     
/* 1297 */     final int[] heap = new int[260];
/* 1298 */     final int[] weight = new int[516];
/* 1299 */     final int[] parent = new int[516];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final byte[] block;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final int[] fmap;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final char[] sfmap;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int origPtr;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Data(int blockSize100k) {
/* 1325 */       int n = blockSize100k * 100000;
/* 1326 */       this.block = new byte[n + 1 + 20];
/* 1327 */       this.fmap = new int[n];
/* 1328 */       this.sfmap = new char[2 * n];
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\bzip2\BZip2CompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */