/*      */ package org.apache.commons.codec.digest;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.Path;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import org.apache.commons.codec.binary.Hex;
/*      */ import org.apache.commons.codec.binary.StringUtils;
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
/*      */ public class DigestUtils
/*      */ {
/*      */   private static final int STREAM_BUFFER_LENGTH = 1024;
/*      */   private final MessageDigest messageDigest;
/*      */   
/*      */   public static byte[] digest(MessageDigest messageDigest, byte[] data) {
/*   72 */     return messageDigest.digest(data);
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
/*      */   public static byte[] digest(MessageDigest messageDigest, ByteBuffer data) {
/*   87 */     messageDigest.update(data);
/*   88 */     return messageDigest.digest();
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
/*      */   public static byte[] digest(MessageDigest messageDigest, File data) throws IOException {
/*  104 */     return updateDigest(messageDigest, data).digest();
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
/*      */   public static byte[] digest(MessageDigest messageDigest, InputStream data) throws IOException {
/*  120 */     return updateDigest(messageDigest, data).digest();
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
/*      */   public static byte[] digest(MessageDigest messageDigest, Path data, OpenOption... options) throws IOException {
/*  139 */     return updateDigest(messageDigest, data, options).digest();
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
/*      */   public static byte[] digest(MessageDigest messageDigest, RandomAccessFile data) throws IOException {
/*  152 */     return updateDigest(messageDigest, data).digest();
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
/*      */   public static MessageDigest getDigest(String algorithm) {
/*      */     try {
/*  170 */       return MessageDigest.getInstance(algorithm);
/*  171 */     } catch (NoSuchAlgorithmException e) {
/*  172 */       throw new IllegalArgumentException(e);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MessageDigest getDigest(String algorithm, MessageDigest defaultMessageDigest) {
/*      */     try {
/*  195 */       return MessageDigest.getInstance(algorithm);
/*  196 */     } catch (Exception e) {
/*  197 */       return defaultMessageDigest;
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
/*      */   public static MessageDigest getMd2Digest() {
/*  212 */     return getDigest("MD2");
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
/*      */   public static MessageDigest getMd5Digest() {
/*  225 */     return getDigest("MD5");
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
/*      */   public static MessageDigest getSha1Digest() {
/*  239 */     return getDigest("SHA-1");
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
/*      */   public static MessageDigest getSha256Digest() {
/*  252 */     return getDigest("SHA-256");
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
/*      */   public static MessageDigest getSha3_224Digest() {
/*  266 */     return getDigest("SHA3-224");
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
/*      */   public static MessageDigest getSha3_256Digest() {
/*  280 */     return getDigest("SHA3-256");
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
/*      */   public static MessageDigest getSha3_384Digest() {
/*  294 */     return getDigest("SHA3-384");
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
/*      */   public static MessageDigest getSha3_512Digest() {
/*  308 */     return getDigest("SHA3-512");
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
/*      */   public static MessageDigest getSha384Digest() {
/*  321 */     return getDigest("SHA-384");
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
/*      */   public static MessageDigest getSha512_224Digest() {
/*  333 */     return getDigest("SHA-512/224");
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
/*      */   public static MessageDigest getSha512_256Digest() {
/*  345 */     return getDigest("SHA-512/256");
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
/*      */   public static MessageDigest getSha512Digest() {
/*  358 */     return getDigest("SHA-512");
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
/*      */   @Deprecated
/*      */   public static MessageDigest getShaDigest() {
/*  371 */     return getSha1Digest();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAvailable(String messageDigestAlgorithm) {
/*  381 */     return (getDigest(messageDigestAlgorithm, null) != null);
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
/*      */   public static byte[] md2(byte[] data) {
/*  393 */     return getMd2Digest().digest(data);
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
/*      */   public static byte[] md2(InputStream data) throws IOException {
/*  407 */     return digest(getMd2Digest(), data);
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
/*      */   public static byte[] md2(String data) {
/*  419 */     return md2(StringUtils.getBytesUtf8(data));
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
/*      */   public static String md2Hex(byte[] data) {
/*  431 */     return Hex.encodeHexString(md2(data));
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
/*      */   public static String md2Hex(InputStream data) throws IOException {
/*  445 */     return Hex.encodeHexString(md2(data));
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
/*      */   public static String md2Hex(String data) {
/*  457 */     return Hex.encodeHexString(md2(data));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] md5(byte[] data) {
/*  468 */     return getMd5Digest().digest(data);
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
/*      */   public static byte[] md5(InputStream data) throws IOException {
/*  482 */     return digest(getMd5Digest(), data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] md5(String data) {
/*  493 */     return md5(StringUtils.getBytesUtf8(data));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String md5Hex(byte[] data) {
/*  504 */     return Hex.encodeHexString(md5(data));
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
/*      */   public static String md5Hex(InputStream data) throws IOException {
/*  518 */     return Hex.encodeHexString(md5(data));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String md5Hex(String data) {
/*  529 */     return Hex.encodeHexString(md5(data));
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
/*      */   @Deprecated
/*      */   public static byte[] sha(byte[] data) {
/*  542 */     return sha1(data);
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
/*      */   @Deprecated
/*      */   public static byte[] sha(InputStream data) throws IOException {
/*  558 */     return sha1(data);
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
/*      */   @Deprecated
/*      */   public static byte[] sha(String data) {
/*  571 */     return sha1(data);
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
/*      */   public static byte[] sha1(byte[] data) {
/*  583 */     return getSha1Digest().digest(data);
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
/*      */   public static byte[] sha1(InputStream data) throws IOException {
/*  597 */     return digest(getSha1Digest(), data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] sha1(String data) {
/*  608 */     return sha1(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha1Hex(byte[] data) {
/*  620 */     return Hex.encodeHexString(sha1(data));
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
/*      */   public static String sha1Hex(InputStream data) throws IOException {
/*  634 */     return Hex.encodeHexString(sha1(data));
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
/*      */   public static String sha1Hex(String data) {
/*  646 */     return Hex.encodeHexString(sha1(data));
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
/*      */   public static byte[] sha256(byte[] data) {
/*  658 */     return getSha256Digest().digest(data);
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
/*      */   public static byte[] sha256(InputStream data) throws IOException {
/*  672 */     return digest(getSha256Digest(), data);
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
/*      */   public static byte[] sha256(String data) {
/*  684 */     return sha256(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha256Hex(byte[] data) {
/*  696 */     return Hex.encodeHexString(sha256(data));
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
/*      */   public static String sha256Hex(InputStream data) throws IOException {
/*  710 */     return Hex.encodeHexString(sha256(data));
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
/*      */   public static String sha256Hex(String data) {
/*  722 */     return Hex.encodeHexString(sha256(data));
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
/*      */   public static byte[] sha3_224(byte[] data) {
/*  734 */     return getSha3_224Digest().digest(data);
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
/*      */   public static byte[] sha3_224(InputStream data) throws IOException {
/*  748 */     return digest(getSha3_224Digest(), data);
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
/*      */   public static byte[] sha3_224(String data) {
/*  760 */     return sha3_224(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha3_224Hex(byte[] data) {
/*  772 */     return Hex.encodeHexString(sha3_224(data));
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
/*      */   public static String sha3_224Hex(InputStream data) throws IOException {
/*  786 */     return Hex.encodeHexString(sha3_224(data));
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
/*      */   public static String sha3_224Hex(String data) {
/*  798 */     return Hex.encodeHexString(sha3_224(data));
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
/*      */   public static byte[] sha3_256(byte[] data) {
/*  810 */     return getSha3_256Digest().digest(data);
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
/*      */   public static byte[] sha3_256(InputStream data) throws IOException {
/*  824 */     return digest(getSha3_256Digest(), data);
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
/*      */   public static byte[] sha3_256(String data) {
/*  836 */     return sha3_256(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha3_256Hex(byte[] data) {
/*  848 */     return Hex.encodeHexString(sha3_256(data));
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
/*      */   public static String sha3_256Hex(InputStream data) throws IOException {
/*  862 */     return Hex.encodeHexString(sha3_256(data));
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
/*      */   public static String sha3_256Hex(String data) {
/*  874 */     return Hex.encodeHexString(sha3_256(data));
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
/*      */   public static byte[] sha3_384(byte[] data) {
/*  886 */     return getSha3_384Digest().digest(data);
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
/*      */   public static byte[] sha3_384(InputStream data) throws IOException {
/*  900 */     return digest(getSha3_384Digest(), data);
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
/*      */   public static byte[] sha3_384(String data) {
/*  912 */     return sha3_384(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha3_384Hex(byte[] data) {
/*  924 */     return Hex.encodeHexString(sha3_384(data));
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
/*      */   public static String sha3_384Hex(InputStream data) throws IOException {
/*  938 */     return Hex.encodeHexString(sha3_384(data));
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
/*      */   public static String sha3_384Hex(String data) {
/*  950 */     return Hex.encodeHexString(sha3_384(data));
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
/*      */   public static byte[] sha3_512(byte[] data) {
/*  962 */     return getSha3_512Digest().digest(data);
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
/*      */   public static byte[] sha3_512(InputStream data) throws IOException {
/*  976 */     return digest(getSha3_512Digest(), data);
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
/*      */   public static byte[] sha3_512(String data) {
/*  988 */     return sha3_512(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha3_512Hex(byte[] data) {
/* 1000 */     return Hex.encodeHexString(sha3_512(data));
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
/*      */   public static String sha3_512Hex(InputStream data) throws IOException {
/* 1014 */     return Hex.encodeHexString(sha3_512(data));
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
/*      */   public static String sha3_512Hex(String data) {
/* 1026 */     return Hex.encodeHexString(sha3_512(data));
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
/*      */   public static byte[] sha384(byte[] data) {
/* 1038 */     return getSha384Digest().digest(data);
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
/*      */   public static byte[] sha384(InputStream data) throws IOException {
/* 1052 */     return digest(getSha384Digest(), data);
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
/*      */   public static byte[] sha384(String data) {
/* 1064 */     return sha384(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha384Hex(byte[] data) {
/* 1076 */     return Hex.encodeHexString(sha384(data));
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
/*      */   public static String sha384Hex(InputStream data) throws IOException {
/* 1090 */     return Hex.encodeHexString(sha384(data));
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
/*      */   public static String sha384Hex(String data) {
/* 1102 */     return Hex.encodeHexString(sha384(data));
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
/*      */   public static byte[] sha512(byte[] data) {
/* 1114 */     return getSha512Digest().digest(data);
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
/*      */   public static byte[] sha512(InputStream data) throws IOException {
/* 1128 */     return digest(getSha512Digest(), data);
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
/*      */   public static byte[] sha512(String data) {
/* 1140 */     return sha512(StringUtils.getBytesUtf8(data));
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
/*      */   public static byte[] sha512_224(byte[] data) {
/* 1152 */     return getSha512_224Digest().digest(data);
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
/*      */   public static byte[] sha512_224(InputStream data) throws IOException {
/* 1166 */     return digest(getSha512_224Digest(), data);
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
/*      */   public static byte[] sha512_224(String data) {
/* 1178 */     return sha512_224(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha512_224Hex(byte[] data) {
/* 1190 */     return Hex.encodeHexString(sha512_224(data));
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
/*      */   public static String sha512_224Hex(InputStream data) throws IOException {
/* 1204 */     return Hex.encodeHexString(sha512_224(data));
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
/*      */   public static String sha512_224Hex(String data) {
/* 1216 */     return Hex.encodeHexString(sha512_224(data));
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
/*      */   public static byte[] sha512_256(byte[] data) {
/* 1228 */     return getSha512_256Digest().digest(data);
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
/*      */   public static byte[] sha512_256(InputStream data) throws IOException {
/* 1242 */     return digest(getSha512_256Digest(), data);
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
/*      */   public static byte[] sha512_256(String data) {
/* 1254 */     return sha512_256(StringUtils.getBytesUtf8(data));
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
/*      */   public static String sha512_256Hex(byte[] data) {
/* 1266 */     return Hex.encodeHexString(sha512_256(data));
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
/*      */   public static String sha512_256Hex(InputStream data) throws IOException {
/* 1280 */     return Hex.encodeHexString(sha512_256(data));
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
/*      */   public static String sha512_256Hex(String data) {
/* 1292 */     return Hex.encodeHexString(sha512_256(data));
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
/*      */   public static String sha512Hex(byte[] data) {
/* 1304 */     return Hex.encodeHexString(sha512(data));
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
/*      */   public static String sha512Hex(InputStream data) throws IOException {
/* 1318 */     return Hex.encodeHexString(sha512(data));
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
/*      */   public static String sha512Hex(String data) {
/* 1330 */     return Hex.encodeHexString(sha512(data));
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
/*      */   @Deprecated
/*      */   public static String shaHex(byte[] data) {
/* 1343 */     return sha1Hex(data);
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
/*      */   @Deprecated
/*      */   public static String shaHex(InputStream data) throws IOException {
/* 1359 */     return sha1Hex(data);
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
/*      */   @Deprecated
/*      */   public static String shaHex(String data) {
/* 1372 */     return sha1Hex(data);
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
/*      */   public static MessageDigest updateDigest(MessageDigest messageDigest, byte[] valueToDigest) {
/* 1386 */     messageDigest.update(valueToDigest);
/* 1387 */     return messageDigest;
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
/*      */   public static MessageDigest updateDigest(MessageDigest messageDigest, ByteBuffer valueToDigest) {
/* 1401 */     messageDigest.update(valueToDigest);
/* 1402 */     return messageDigest;
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
/*      */   public static MessageDigest updateDigest(MessageDigest digest, File data) throws IOException {
/* 1418 */     try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(data))) {
/* 1419 */       return updateDigest(digest, inputStream);
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
/*      */   private static MessageDigest updateDigest(MessageDigest digest, FileChannel data) throws IOException {
/* 1435 */     ByteBuffer buffer = ByteBuffer.allocate(1024);
/* 1436 */     while (data.read(buffer) > 0) {
/* 1437 */       buffer.flip();
/* 1438 */       digest.update(buffer);
/* 1439 */       buffer.clear();
/*      */     } 
/* 1441 */     return digest;
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
/*      */   public static MessageDigest updateDigest(MessageDigest digest, InputStream inputStream) throws IOException {
/* 1458 */     byte[] buffer = new byte[1024];
/* 1459 */     int read = inputStream.read(buffer, 0, 1024);
/*      */     
/* 1461 */     while (read > -1) {
/* 1462 */       digest.update(buffer, 0, read);
/* 1463 */       read = inputStream.read(buffer, 0, 1024);
/*      */     } 
/*      */     
/* 1466 */     return digest;
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
/*      */   public static MessageDigest updateDigest(MessageDigest digest, Path path, OpenOption... options) throws IOException {
/* 1485 */     try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path, options))) {
/* 1486 */       return updateDigest(digest, inputStream);
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
/*      */   public static MessageDigest updateDigest(MessageDigest digest, RandomAccessFile data) throws IOException {
/* 1501 */     return updateDigest(digest, data.getChannel());
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
/*      */   public static MessageDigest updateDigest(MessageDigest messageDigest, String valueToDigest) {
/* 1521 */     messageDigest.update(StringUtils.getBytesUtf8(valueToDigest));
/* 1522 */     return messageDigest;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public DigestUtils() {
/* 1534 */     this.messageDigest = null;
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
/*      */   public DigestUtils(MessageDigest digest) {
/* 1547 */     this.messageDigest = digest;
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
/*      */   public DigestUtils(String name) {
/* 1563 */     this(getDigest(name));
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
/*      */   public byte[] digest(byte[] data) {
/* 1575 */     return updateDigest(this.messageDigest, data).digest();
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
/*      */   public byte[] digest(ByteBuffer data) {
/* 1588 */     return updateDigest(this.messageDigest, data).digest();
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
/*      */   public byte[] digest(File data) throws IOException {
/* 1602 */     return updateDigest(this.messageDigest, data).digest();
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
/*      */   public byte[] digest(InputStream data) throws IOException {
/* 1616 */     return updateDigest(this.messageDigest, data).digest();
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
/*      */   public byte[] digest(Path data, OpenOption... options) throws IOException {
/* 1632 */     return updateDigest(this.messageDigest, data, options).digest();
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
/*      */   public byte[] digest(String data) {
/* 1644 */     return updateDigest(this.messageDigest, data).digest();
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
/*      */   public String digestAsHex(byte[] data) {
/* 1656 */     return Hex.encodeHexString(digest(data));
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
/*      */   public String digestAsHex(ByteBuffer data) {
/* 1669 */     return Hex.encodeHexString(digest(data));
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
/*      */   public String digestAsHex(File data) throws IOException {
/* 1683 */     return Hex.encodeHexString(digest(data));
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
/*      */   public String digestAsHex(InputStream data) throws IOException {
/* 1697 */     return Hex.encodeHexString(digest(data));
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
/*      */   public String digestAsHex(Path data, OpenOption... options) throws IOException {
/* 1713 */     return Hex.encodeHexString(digest(data, options));
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
/*      */   public String digestAsHex(String data) {
/* 1725 */     return Hex.encodeHexString(digest(data));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MessageDigest getMessageDigest() {
/* 1734 */     return this.messageDigest;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\DigestUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */