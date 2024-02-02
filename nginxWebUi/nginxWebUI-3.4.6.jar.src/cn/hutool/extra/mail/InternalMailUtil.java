/*     */ package cn.hutool.extra.mail;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeUtility;
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
/*     */ public class InternalMailUtil
/*     */ {
/*     */   public static InternetAddress[] parseAddressFromStrs(String[] addrStrs, Charset charset) {
/*  31 */     List<InternetAddress> resultList = new ArrayList<>(addrStrs.length);
/*     */     
/*  33 */     for (String addrStr : addrStrs) {
/*  34 */       InternetAddress[] addrs = parseAddress(addrStr, charset);
/*  35 */       if (ArrayUtil.isNotEmpty((Object[])addrs)) {
/*  36 */         Collections.addAll(resultList, addrs);
/*     */       }
/*     */     } 
/*  39 */     return resultList.<InternetAddress>toArray(new InternetAddress[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static InternetAddress parseFirstAddress(String address, Charset charset) {
/*  50 */     InternetAddress[] internetAddresses = parseAddress(address, charset);
/*  51 */     if (ArrayUtil.isEmpty((Object[])internetAddresses)) {
/*     */       try {
/*  53 */         return new InternetAddress(address);
/*  54 */       } catch (AddressException e) {
/*  55 */         throw new MailException(e);
/*     */       } 
/*     */     }
/*  58 */     return internetAddresses[0];
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
/*     */   public static InternetAddress[] parseAddress(String address, Charset charset) {
/*     */     InternetAddress[] addresses;
/*     */     try {
/*  72 */       addresses = InternetAddress.parse(address);
/*  73 */     } catch (AddressException e) {
/*  74 */       throw new MailException(e);
/*     */     } 
/*     */     
/*  77 */     if (ArrayUtil.isNotEmpty((Object[])addresses)) {
/*  78 */       String charsetStr = (null == charset) ? null : charset.name();
/*  79 */       for (InternetAddress internetAddress : addresses) {
/*     */         try {
/*  81 */           internetAddress.setPersonal(internetAddress.getPersonal(), charsetStr);
/*  82 */         } catch (UnsupportedEncodingException e) {
/*  83 */           throw new MailException(e);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  88 */     return addresses;
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
/*     */   public static String encodeText(String text, Charset charset) {
/*     */     try {
/* 101 */       return MimeUtility.encodeText(text, charset.name(), null);
/* 102 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/*     */ 
/*     */       
/* 105 */       return text;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\InternalMailUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */