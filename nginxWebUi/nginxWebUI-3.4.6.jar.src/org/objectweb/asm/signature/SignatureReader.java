/*     */ package org.objectweb.asm.signature;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignatureReader
/*     */ {
/*     */   private final String signatureValue;
/*     */   
/*     */   public SignatureReader(String signature) {
/*  50 */     this.signatureValue = signature;
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
/*     */   
/*     */   public void accept(SignatureVisitor signatureVistor) {
/*     */     int offset;
/*  64 */     String signature = this.signatureValue;
/*  65 */     int length = signature.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     if (signature.charAt(0) == '<') {
/*     */       char currentChar;
/*     */       
/*  75 */       offset = 2;
/*     */       
/*     */       do {
/*  78 */         int classBoundStartOffset = signature.indexOf(':', offset);
/*  79 */         signatureVistor.visitFormalTypeParameter(signature
/*  80 */             .substring(offset - 1, classBoundStartOffset));
/*     */ 
/*     */ 
/*     */         
/*  84 */         offset = classBoundStartOffset + 1;
/*  85 */         currentChar = signature.charAt(offset);
/*  86 */         if (currentChar == 'L' || currentChar == '[' || currentChar == 'T') {
/*  87 */           offset = parseType(signature, offset, signatureVistor.visitClassBound());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*  92 */         while ((currentChar = signature.charAt(offset++)) == ':') {
/*  93 */           offset = parseType(signature, offset, signatureVistor.visitInterfaceBound());
/*     */ 
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/* 100 */       while (currentChar != '>');
/*     */     } else {
/* 102 */       offset = 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     if (signature.charAt(offset) == '(') {
/* 109 */       offset++;
/* 110 */       while (signature.charAt(offset) != ')') {
/* 111 */         offset = parseType(signature, offset, signatureVistor.visitParameterType());
/*     */       }
/*     */       
/* 114 */       offset = parseType(signature, offset + 1, signatureVistor.visitReturnType());
/* 115 */       while (offset < length)
/*     */       {
/* 117 */         offset = parseType(signature, offset + 1, signatureVistor.visitExceptionType());
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 122 */       offset = parseType(signature, offset, signatureVistor.visitSuperclass());
/* 123 */       while (offset < length) {
/* 124 */         offset = parseType(signature, offset, signatureVistor.visitInterface());
/*     */       }
/*     */     } 
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
/*     */ 
/*     */   
/*     */   public void acceptType(SignatureVisitor signatureVisitor) {
/* 140 */     parseType(this.signatureValue, 0, signatureVisitor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int parseType(String signature, int startOffset, SignatureVisitor signatureVisitor) {
/*     */     int endOffset, start;
/*     */     boolean visited, inner;
/* 153 */     int offset = startOffset;
/* 154 */     char currentChar = signature.charAt(offset++);
/*     */ 
/*     */     
/* 157 */     switch (currentChar) {
/*     */       
/*     */       case 'B':
/*     */       case 'C':
/*     */       case 'D':
/*     */       case 'F':
/*     */       case 'I':
/*     */       case 'J':
/*     */       case 'S':
/*     */       case 'V':
/*     */       case 'Z':
/* 168 */         signatureVisitor.visitBaseType(currentChar);
/* 169 */         return offset;
/*     */ 
/*     */       
/*     */       case '[':
/* 173 */         return parseType(signature, offset, signatureVisitor.visitArrayType());
/*     */ 
/*     */       
/*     */       case 'T':
/* 177 */         endOffset = signature.indexOf(';', offset);
/* 178 */         signatureVisitor.visitTypeVariable(signature.substring(offset, endOffset));
/* 179 */         return endOffset + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 'L':
/* 185 */         start = offset;
/* 186 */         visited = false;
/* 187 */         inner = false;
/*     */         
/*     */         while (true) {
/* 190 */           currentChar = signature.charAt(offset++);
/* 191 */           if (currentChar == '.' || currentChar == ';') {
/*     */ 
/*     */ 
/*     */             
/* 195 */             if (!visited) {
/* 196 */               String name = signature.substring(start, offset - 1);
/* 197 */               if (inner) {
/* 198 */                 signatureVisitor.visitInnerClassType(name);
/*     */               } else {
/* 200 */                 signatureVisitor.visitClassType(name);
/*     */               } 
/*     */             } 
/*     */ 
/*     */             
/* 205 */             if (currentChar == ';') {
/* 206 */               signatureVisitor.visitEnd();
/*     */               break;
/*     */             } 
/* 209 */             start = offset;
/* 210 */             visited = false;
/* 211 */             inner = true; continue;
/* 212 */           }  if (currentChar == '<') {
/*     */ 
/*     */ 
/*     */             
/* 216 */             String name = signature.substring(start, offset - 1);
/* 217 */             if (inner) {
/* 218 */               signatureVisitor.visitInnerClassType(name);
/*     */             } else {
/* 220 */               signatureVisitor.visitClassType(name);
/*     */             } 
/* 222 */             visited = true;
/*     */             
/* 224 */             while ((currentChar = signature.charAt(offset)) != '>') {
/* 225 */               switch (currentChar) {
/*     */                 
/*     */                 case '*':
/* 228 */                   offset++;
/* 229 */                   signatureVisitor.visitTypeArgument();
/*     */                   continue;
/*     */ 
/*     */                 
/*     */                 case '+':
/*     */                 case '-':
/* 235 */                   offset = parseType(signature, offset + 1, signatureVisitor
/* 236 */                       .visitTypeArgument(currentChar));
/*     */                   continue;
/*     */               } 
/*     */               
/* 240 */               offset = parseType(signature, offset, signatureVisitor.visitTypeArgument('='));
/*     */             } 
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 246 */         return offset;
/*     */     } 
/*     */     
/* 249 */     throw new IllegalArgumentException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\signature\SignatureReader.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */