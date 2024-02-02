/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.io.FastByteArrayOutputStream;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.bouncycastle.asn1.ASN1InputStream;
/*     */ import org.bouncycastle.asn1.ASN1Object;
/*     */ import org.bouncycastle.asn1.BERSequence;
/*     */ import org.bouncycastle.asn1.DERSequence;
/*     */ import org.bouncycastle.asn1.DLSequence;
/*     */ import org.bouncycastle.asn1.util.ASN1Dump;
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
/*     */ public class ASN1Util
/*     */ {
/*     */   public static byte[] encodeDer(ASN1Encodable... elements) {
/*  35 */     return encode("DER", elements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encode(String asn1Encoding, ASN1Encodable... elements) {
/*  46 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream();
/*  47 */     encodeTo(asn1Encoding, (OutputStream)out, elements);
/*  48 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void encodeTo(String asn1Encoding, OutputStream out, ASN1Encodable... elements) {
/*     */     DERSequence dERSequence;
/*     */     BERSequence bERSequence;
/*     */     DLSequence dLSequence;
/*  60 */     switch (asn1Encoding) {
/*     */       case "DER":
/*  62 */         dERSequence = new DERSequence(elements);
/*     */         break;
/*     */       case "BER":
/*  65 */         bERSequence = new BERSequence(elements);
/*     */         break;
/*     */       case "DL":
/*  68 */         dLSequence = new DLSequence(elements);
/*     */         break;
/*     */       default:
/*  71 */         throw new CryptoException("Unsupported ASN1 encoding: {}", new Object[] { asn1Encoding });
/*     */     } 
/*     */     try {
/*  74 */       dLSequence.encodeTo(out);
/*  75 */     } catch (IOException e) {
/*  76 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ASN1Object decode(InputStream in) {
/*  87 */     ASN1InputStream asn1In = new ASN1InputStream(in);
/*     */     try {
/*  89 */       return (ASN1Object)asn1In.readObject();
/*  90 */     } catch (IOException e) {
/*  91 */       throw new IORuntimeException(e);
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
/*     */   public static String getDumpStr(InputStream in) {
/* 103 */     return ASN1Dump.dumpAsString(decode(in));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\ASN1Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */