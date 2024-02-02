/*     */ package org.wildfly.common.string;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.nio.CharBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class CompositeCharSequence
/*     */   implements CharSequence, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 4975968165050531721L;
/*     */   private final List<CharSequence> sequences;
/*  36 */   private transient int hashCode = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompositeCharSequence(CharSequence... sequences) {
/*  43 */     this(Arrays.asList(sequences));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompositeCharSequence(List<CharSequence> sequences) {
/*  51 */     this.sequences = sequences;
/*     */   }
/*     */ 
/*     */   
/*     */   public int length() {
/*  56 */     int length = 0;
/*  57 */     for (CharSequence sequence : this.sequences) {
/*  58 */       length += sequence.length();
/*     */     }
/*  60 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public char charAt(int index) {
/*  65 */     int relativeIndex = index;
/*  66 */     for (CharSequence sequence : this.sequences) {
/*  67 */       if (relativeIndex < sequence.length()) {
/*  68 */         return sequence.charAt(relativeIndex);
/*     */       }
/*  70 */       relativeIndex -= sequence.length();
/*     */     } 
/*  72 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence subSequence(int start, int end) {
/*  77 */     if (start < 0 || start > end || end > length()) {
/*  78 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  80 */     if (start == end) return ""; 
/*  81 */     List<CharSequence> result = null;
/*  82 */     int relativeStart = start;
/*  83 */     int relativeEnd = end;
/*  84 */     for (CharSequence sequence : this.sequences) {
/*  85 */       if (relativeStart < sequence.length() && relativeEnd > 0) {
/*  86 */         CharSequence subSequence = sequence.subSequence(Math.max(relativeStart, 0), Math.min(relativeEnd, sequence.length()));
/*  87 */         if (result == null) {
/*     */           
/*  89 */           if (relativeStart >= 0 && relativeEnd <= sequence.length()) {
/*  90 */             return subSequence;
/*     */           }
/*  92 */           result = new LinkedList<>();
/*     */         } 
/*  94 */         result.add(subSequence);
/*     */       } 
/*  96 */       relativeStart -= sequence.length();
/*  97 */       relativeEnd -= sequence.length();
/*     */     } 
/*  99 */     return new CompositeCharSequence(result);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 104 */     int result = this.hashCode;
/* 105 */     if (result == 0) {
/* 106 */       for (int i = 0; i < length(); i++) {
/* 107 */         result = 31 * result + charAt(i);
/*     */       }
/* 109 */       this.hashCode = result;
/*     */     } 
/* 111 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 116 */     if (!(object instanceof CharSequence)) return false; 
/* 117 */     CharSequence sequence = (CharSequence)object;
/* 118 */     int length = sequence.length();
/* 119 */     if (length() != length) return false; 
/* 120 */     for (int i = 0; i < length; i++) {
/* 121 */       if (charAt(i) != sequence.charAt(i)) return false; 
/*     */     } 
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     CharBuffer buffer = CharBuffer.allocate(length());
/* 129 */     for (CharSequence sequence : this.sequences) {
/* 130 */       buffer.put(CharBuffer.wrap(sequence));
/*     */     }
/* 132 */     return String.valueOf(buffer.array());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\string\CompositeCharSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */