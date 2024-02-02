/*     */ package cn.hutool.extra.mail;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.mail.Authenticator;
/*     */ import javax.mail.Session;
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
/*     */ public class MailUtil
/*     */ {
/*     */   public static String sendText(String to, String subject, String content, File... files) {
/*  37 */     return send(to, subject, content, false, files);
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
/*     */   
/*     */   public static String sendHtml(String to, String subject, String content, File... files) {
/*  52 */     return send(to, subject, content, true, files);
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
/*     */   
/*     */   public static String send(String to, String subject, String content, boolean isHtml, File... files) {
/*  67 */     return send(splitAddress(to), subject, content, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(String to, String cc, String bcc, String subject, String content, boolean isHtml, File... files) {
/*  85 */     return send(splitAddress(to), splitAddress(cc), splitAddress(bcc), subject, content, isHtml, files);
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
/*     */   public static String sendText(Collection<String> tos, String subject, String content, File... files) {
/*  98 */     return send(tos, subject, content, false, files);
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
/*     */   public static String sendHtml(Collection<String> tos, String subject, String content, File... files) {
/* 112 */     return send(tos, subject, content, true, files);
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
/*     */   public static String send(Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
/* 126 */     return send(tos, (Collection<String>)null, (Collection<String>)null, subject, content, isHtml, files);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
/* 143 */     return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, null, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(MailAccount mailAccount, String to, String subject, String content, boolean isHtml, File... files) {
/* 161 */     return send(mailAccount, splitAddress(to), subject, content, isHtml, files);
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
/*     */   
/*     */   public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
/* 176 */     return send(mailAccount, tos, (Collection<String>)null, (Collection<String>)null, subject, content, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
/* 194 */     return send(mailAccount, false, tos, ccs, bccs, subject, content, null, isHtml, files);
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
/*     */ 
/*     */   
/*     */   public static String sendHtml(String to, String subject, String content, Map<String, InputStream> imageMap, File... files) {
/* 210 */     return send(to, subject, content, imageMap, true, files);
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
/*     */ 
/*     */   
/*     */   public static String send(String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 226 */     return send(splitAddress(to), subject, content, imageMap, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(String to, String cc, String bcc, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 245 */     return send(splitAddress(to), splitAddress(cc), splitAddress(bcc), subject, content, imageMap, isHtml, files);
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
/*     */   
/*     */   public static String sendHtml(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, File... files) {
/* 260 */     return send(tos, subject, content, imageMap, true, files);
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
/*     */   
/*     */   public static String send(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 275 */     return send(tos, (Collection<String>)null, (Collection<String>)null, subject, content, imageMap, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 293 */     return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(MailAccount mailAccount, String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 312 */     return send(mailAccount, splitAddress(to), subject, content, imageMap, isHtml, files);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 329 */     return send(mailAccount, tos, null, null, subject, content, imageMap, isHtml, files);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 349 */     return send(mailAccount, false, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
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
/*     */   public static Session getSession(MailAccount mailAccount, boolean isSingleton) {
/* 361 */     Authenticator authenticator = null;
/* 362 */     if (mailAccount.isAuth().booleanValue()) {
/* 363 */       authenticator = new UserPassAuthenticator(mailAccount.getUser(), mailAccount.getPass());
/*     */     }
/*     */     
/* 366 */     return isSingleton ? Session.getDefaultInstance(mailAccount.getSmtpProps(), authenticator) : 
/* 367 */       Session.getInstance(mailAccount.getSmtpProps(), authenticator);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String send(MailAccount mailAccount, boolean useGlobalSession, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
/* 390 */     Mail mail = Mail.create(mailAccount).setUseGlobalSession(useGlobalSession);
/*     */ 
/*     */     
/* 393 */     if (CollUtil.isNotEmpty(ccs)) {
/* 394 */       mail.setCcs(ccs.<String>toArray(new String[0]));
/*     */     }
/*     */     
/* 397 */     if (CollUtil.isNotEmpty(bccs)) {
/* 398 */       mail.setBccs(bccs.<String>toArray(new String[0]));
/*     */     }
/*     */     
/* 401 */     mail.setTos(tos.<String>toArray(new String[0]));
/* 402 */     mail.setTitle(subject);
/* 403 */     mail.setContent(content);
/* 404 */     mail.setHtml(isHtml);
/* 405 */     mail.setFiles(files);
/*     */ 
/*     */     
/* 408 */     if (MapUtil.isNotEmpty(imageMap)) {
/* 409 */       for (Map.Entry<String, InputStream> entry : imageMap.entrySet()) {
/* 410 */         mail.addImage(entry.getKey(), entry.getValue());
/*     */         
/* 412 */         IoUtil.close(entry.getValue());
/*     */       } 
/*     */     }
/*     */     
/* 416 */     return mail.send();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> splitAddress(String addresses) {
/*     */     List<String> result;
/* 426 */     if (StrUtil.isBlank(addresses)) {
/* 427 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 431 */     if (StrUtil.contains(addresses, ',')) {
/* 432 */       result = StrUtil.splitTrim(addresses, ',');
/* 433 */     } else if (StrUtil.contains(addresses, ';')) {
/* 434 */       result = StrUtil.splitTrim(addresses, ';');
/*     */     } else {
/* 436 */       result = CollUtil.newArrayList((Object[])new String[] { addresses });
/*     */     } 
/* 438 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\MailUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */