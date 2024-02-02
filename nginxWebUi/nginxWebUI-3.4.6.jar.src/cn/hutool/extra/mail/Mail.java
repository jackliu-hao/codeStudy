/*     */ package cn.hutool.extra.mail;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Date;
/*     */ import javax.activation.DataHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.activation.FileDataSource;
/*     */ import javax.activation.FileTypeMap;
/*     */ import javax.mail.Address;
/*     */ import javax.mail.BodyPart;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.Multipart;
/*     */ import javax.mail.SendFailedException;
/*     */ import javax.mail.Session;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import javax.mail.util.ByteArrayDataSource;
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
/*     */ public class Mail
/*     */   implements Builder<MimeMessage>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final MailAccount mailAccount;
/*     */   private String[] tos;
/*     */   private String[] ccs;
/*     */   private String[] bccs;
/*     */   private String[] reply;
/*     */   private String title;
/*     */   private String content;
/*     */   private boolean isHtml;
/*  77 */   private final Multipart multipart = (Multipart)new MimeMultipart();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean useGlobalSession = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PrintStream debugOutput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mail create(MailAccount mailAccount) {
/*  95 */     return new Mail(mailAccount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Mail create() {
/* 104 */     return new Mail();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail() {
/* 113 */     this(GlobalMailAccount.INSTANCE.getAccount());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail(MailAccount mailAccount) {
/* 122 */     mailAccount = (null != mailAccount) ? mailAccount : GlobalMailAccount.INSTANCE.getAccount();
/* 123 */     this.mailAccount = mailAccount.defaultIfEmpty();
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
/*     */   public Mail to(String... tos) {
/* 137 */     return setTos(tos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setTos(String... tos) {
/* 147 */     this.tos = tos;
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setCcs(String... ccs) {
/* 159 */     this.ccs = ccs;
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setBccs(String... bccs) {
/* 171 */     this.bccs = bccs;
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setReply(String... reply) {
/* 183 */     this.reply = reply;
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setTitle(String title) {
/* 194 */     this.title = title;
/* 195 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setContent(String content) {
/* 206 */     this.content = content;
/* 207 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setHtml(boolean isHtml) {
/* 217 */     this.isHtml = isHtml;
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setContent(String content, boolean isHtml) {
/* 229 */     setContent(content);
/* 230 */     return setHtml(isHtml);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setFiles(File... files) {
/* 240 */     if (ArrayUtil.isEmpty((Object[])files)) {
/* 241 */       return this;
/*     */     }
/*     */     
/* 244 */     DataSource[] attachments = new DataSource[files.length];
/* 245 */     for (int i = 0; i < files.length; i++) {
/* 246 */       attachments[i] = (DataSource)new FileDataSource(files[i]);
/*     */     }
/* 248 */     return setAttachments(attachments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setAttachments(DataSource... attachments) {
/* 259 */     if (ArrayUtil.isNotEmpty((Object[])attachments)) {
/* 260 */       Charset charset = this.mailAccount.getCharset();
/*     */ 
/*     */       
/*     */       try {
/* 264 */         for (DataSource attachment : attachments) {
/* 265 */           MimeBodyPart bodyPart = new MimeBodyPart();
/* 266 */           bodyPart.setDataHandler(new DataHandler(attachment));
/* 267 */           String nameEncoded = attachment.getName();
/* 268 */           if (this.mailAccount.isEncodefilename()) {
/* 269 */             nameEncoded = InternalMailUtil.encodeText(nameEncoded, charset);
/*     */           }
/*     */           
/* 272 */           bodyPart.setFileName(nameEncoded);
/* 273 */           if (StrUtil.startWith(attachment.getContentType(), "image/"))
/*     */           {
/* 275 */             bodyPart.setContentID(nameEncoded);
/*     */           }
/* 277 */           this.multipart.addBodyPart((BodyPart)bodyPart);
/*     */         } 
/* 279 */       } catch (MessagingException e) {
/* 280 */         throw new MailException(e);
/*     */       } 
/*     */     } 
/* 283 */     return this;
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
/*     */   public Mail addImage(String cid, InputStream imageStream) {
/* 295 */     return addImage(cid, imageStream, null);
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
/*     */   public Mail addImage(String cid, InputStream imageStream, String contentType) {
/*     */     ByteArrayDataSource imgSource;
/*     */     try {
/* 310 */       imgSource = new ByteArrayDataSource(imageStream, (String)ObjectUtil.defaultIfNull(contentType, "image/jpeg"));
/* 311 */     } catch (IOException e) {
/* 312 */       throw new IORuntimeException(e);
/*     */     } 
/* 314 */     imgSource.setName(cid);
/* 315 */     return setAttachments(new DataSource[] { (DataSource)imgSource });
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
/*     */   public Mail addImage(String cid, File imageFile) {
/* 327 */     InputStream in = null;
/*     */     try {
/* 329 */       in = FileUtil.getInputStream(imageFile);
/* 330 */       return addImage(cid, in, FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile));
/*     */     } finally {
/* 332 */       IoUtil.close(in);
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
/*     */   public Mail setCharset(Charset charset) {
/* 344 */     this.mailAccount.setCharset(charset);
/* 345 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setUseGlobalSession(boolean isUseGlobalSession) {
/* 356 */     this.useGlobalSession = isUseGlobalSession;
/* 357 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mail setDebugOutput(PrintStream debugOutput) {
/* 368 */     this.debugOutput = debugOutput;
/* 369 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MimeMessage build() {
/*     */     try {
/* 376 */       return buildMsg();
/* 377 */     } catch (MessagingException e) {
/* 378 */       throw new MailException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String send() throws MailException {
/*     */     try {
/* 390 */       return doSend();
/* 391 */     } catch (MessagingException e) {
/* 392 */       if (e instanceof SendFailedException) {
/*     */         
/* 394 */         Address[] invalidAddresses = ((SendFailedException)e).getInvalidAddresses();
/* 395 */         String msg = StrUtil.format("Invalid Addresses: {}", new Object[] { ArrayUtil.toString(invalidAddresses) });
/* 396 */         throw new MailException(msg, e);
/*     */       } 
/* 398 */       throw new MailException(e);
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
/*     */   private String doSend() throws MessagingException {
/* 411 */     MimeMessage mimeMessage = buildMsg();
/* 412 */     Transport.send((Message)mimeMessage);
/* 413 */     return mimeMessage.getMessageID();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MimeMessage buildMsg() throws MessagingException {
/* 423 */     Charset charset = this.mailAccount.getCharset();
/* 424 */     MimeMessage msg = new MimeMessage(getSession());
/*     */     
/* 426 */     String from = this.mailAccount.getFrom();
/* 427 */     if (StrUtil.isEmpty(from)) {
/*     */       
/* 429 */       msg.setFrom();
/*     */     } else {
/* 431 */       msg.setFrom((Address)InternalMailUtil.parseFirstAddress(from, charset));
/*     */     } 
/*     */     
/* 434 */     msg.setSubject(this.title, (null == charset) ? null : charset.name());
/*     */     
/* 436 */     msg.setSentDate(new Date());
/*     */     
/* 438 */     msg.setContent(buildContent(charset));
/*     */     
/* 440 */     msg.setRecipients(MimeMessage.RecipientType.TO, (Address[])InternalMailUtil.parseAddressFromStrs(this.tos, charset));
/*     */     
/* 442 */     if (ArrayUtil.isNotEmpty((Object[])this.ccs)) {
/* 443 */       msg.setRecipients(MimeMessage.RecipientType.CC, (Address[])InternalMailUtil.parseAddressFromStrs(this.ccs, charset));
/*     */     }
/*     */     
/* 446 */     if (ArrayUtil.isNotEmpty((Object[])this.bccs)) {
/* 447 */       msg.setRecipients(MimeMessage.RecipientType.BCC, (Address[])InternalMailUtil.parseAddressFromStrs(this.bccs, charset));
/*     */     }
/*     */     
/* 450 */     if (ArrayUtil.isNotEmpty((Object[])this.reply)) {
/* 451 */       msg.setReplyTo((Address[])InternalMailUtil.parseAddressFromStrs(this.reply, charset));
/*     */     }
/*     */     
/* 454 */     return msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Multipart buildContent(Charset charset) throws MessagingException {
/* 465 */     String charsetStr = (null != charset) ? charset.name() : MimeUtility.getDefaultJavaCharset();
/*     */     
/* 467 */     MimeBodyPart body = new MimeBodyPart();
/* 468 */     body.setContent(this.content, StrUtil.format("text/{}; charset={}", new Object[] { this.isHtml ? "html" : "plain", charsetStr }));
/* 469 */     this.multipart.addBodyPart((BodyPart)body);
/*     */     
/* 471 */     return this.multipart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Session getSession() {
/* 481 */     Session session = MailUtil.getSession(this.mailAccount, this.useGlobalSession);
/*     */     
/* 483 */     if (null != this.debugOutput) {
/* 484 */       session.setDebugOut(this.debugOutput);
/*     */     }
/*     */     
/* 487 */     return session;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\mail\Mail.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */