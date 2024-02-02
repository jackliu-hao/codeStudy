/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.utils.IOUtils;
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
/*     */ class BinaryTree
/*     */ {
/*     */   private static final int UNDEFINED = -1;
/*     */   private static final int NODE = -2;
/*     */   private final int[] tree;
/*     */   
/*     */   public BinaryTree(int depth) {
/*  50 */     if (depth < 0 || depth > 30) {
/*  51 */       throw new IllegalArgumentException("depth must be bigger than 0 and not bigger than 30 but is " + depth);
/*     */     }
/*     */     
/*  54 */     this.tree = new int[(int)((1L << depth + 1) - 1L)];
/*  55 */     Arrays.fill(this.tree, -1);
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
/*     */   public void addLeaf(int node, int path, int depth, int value) {
/*  67 */     if (depth == 0) {
/*     */       
/*  69 */       if (this.tree[node] != -1) {
/*  70 */         throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
/*     */       }
/*  72 */       this.tree[node] = value;
/*     */     } else {
/*     */       
/*  75 */       this.tree[node] = -2;
/*     */ 
/*     */       
/*  78 */       int nextChild = 2 * node + 1 + (path & 0x1);
/*  79 */       addLeaf(nextChild, path >>> 1, depth - 1, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(BitStream stream) throws IOException {
/*  90 */     int bit, value, currentIndex = 0;
/*     */     
/*     */     while (true) {
/*  93 */       bit = stream.nextBit();
/*  94 */       if (bit == -1) {
/*  95 */         return -1;
/*     */       }
/*     */       
/*  98 */       int childIndex = 2 * currentIndex + 1 + bit;
/*  99 */       value = this.tree[childIndex];
/* 100 */       if (value == -2)
/*     */       
/* 102 */       { currentIndex = childIndex; continue; }  break;
/* 103 */     }  if (value != -1) {
/* 104 */       return value;
/*     */     }
/* 106 */     throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static BinaryTree decode(InputStream inputStream, int totalNumberOfValues) throws IOException {
/* 116 */     if (totalNumberOfValues < 0) {
/* 117 */       throw new IllegalArgumentException("totalNumberOfValues must be bigger than 0, is " + totalNumberOfValues);
/*     */     }
/*     */ 
/*     */     
/* 121 */     int size = inputStream.read() + 1;
/* 122 */     if (size == 0) {
/* 123 */       throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
/*     */     }
/*     */     
/* 126 */     byte[] encodedTree = IOUtils.readRange(inputStream, size);
/* 127 */     if (encodedTree.length != size) {
/* 128 */       throw new EOFException();
/*     */     }
/*     */ 
/*     */     
/* 132 */     int maxLength = 0;
/*     */     
/* 134 */     int[] originalBitLengths = new int[totalNumberOfValues];
/* 135 */     int pos = 0;
/* 136 */     for (byte b : encodedTree) {
/*     */       
/* 138 */       int numberOfValues = ((b & 0xF0) >> 4) + 1;
/* 139 */       if (pos + numberOfValues > totalNumberOfValues) {
/* 140 */         throw new IOException("Number of values exceeds given total number of values");
/*     */       }
/* 142 */       int bitLength = (b & 0xF) + 1;
/*     */       
/* 144 */       for (int n = 0; n < numberOfValues; n++) {
/* 145 */         originalBitLengths[pos++] = bitLength;
/*     */       }
/*     */       
/* 148 */       maxLength = Math.max(maxLength, bitLength);
/*     */     } 
/*     */     
/* 151 */     int oBitLengths = originalBitLengths.length;
/*     */     
/* 153 */     int[] permutation = new int[oBitLengths];
/* 154 */     for (int k = 0; k < permutation.length; k++) {
/* 155 */       permutation[k] = k;
/*     */     }
/*     */     
/* 158 */     int c = 0;
/* 159 */     int[] sortedBitLengths = new int[oBitLengths];
/* 160 */     for (int j = 0; j < oBitLengths; j++) {
/*     */       
/* 162 */       for (int l = 0; l < oBitLengths; l++) {
/*     */         
/* 164 */         if (originalBitLengths[l] == j) {
/*     */           
/* 166 */           sortedBitLengths[c] = j;
/*     */ 
/*     */           
/* 169 */           permutation[c] = l;
/*     */           
/* 171 */           c++;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 177 */     int code = 0;
/* 178 */     int codeIncrement = 0;
/* 179 */     int lastBitLength = 0;
/*     */     
/* 181 */     int[] codes = new int[totalNumberOfValues];
/*     */     
/* 183 */     for (int i = totalNumberOfValues - 1; i >= 0; i--) {
/* 184 */       code += codeIncrement;
/* 185 */       if (sortedBitLengths[i] != lastBitLength) {
/* 186 */         lastBitLength = sortedBitLengths[i];
/* 187 */         codeIncrement = 1 << 16 - lastBitLength;
/*     */       } 
/* 189 */       codes[permutation[i]] = code;
/*     */     } 
/*     */ 
/*     */     
/* 193 */     BinaryTree tree = new BinaryTree(maxLength);
/*     */     
/* 195 */     for (int m = 0; m < codes.length; m++) {
/* 196 */       int bitLength = originalBitLengths[m];
/* 197 */       if (bitLength > 0) {
/* 198 */         tree.addLeaf(0, Integer.reverse(codes[m] << 16), bitLength, m);
/*     */       }
/*     */     } 
/*     */     
/* 202 */     return tree;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\BinaryTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */