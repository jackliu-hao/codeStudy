package cn.hutool.extra.mail;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

public class Mail implements Builder<MimeMessage> {
   private static final long serialVersionUID = 1L;
   private final MailAccount mailAccount;
   private String[] tos;
   private String[] ccs;
   private String[] bccs;
   private String[] reply;
   private String title;
   private String content;
   private boolean isHtml;
   private final Multipart multipart;
   private boolean useGlobalSession;
   private PrintStream debugOutput;

   public static Mail create(MailAccount mailAccount) {
      return new Mail(mailAccount);
   }

   public static Mail create() {
      return new Mail();
   }

   public Mail() {
      this(GlobalMailAccount.INSTANCE.getAccount());
   }

   public Mail(MailAccount mailAccount) {
      this.multipart = new MimeMultipart();
      this.useGlobalSession = false;
      mailAccount = null != mailAccount ? mailAccount : GlobalMailAccount.INSTANCE.getAccount();
      this.mailAccount = mailAccount.defaultIfEmpty();
   }

   public Mail to(String... tos) {
      return this.setTos(tos);
   }

   public Mail setTos(String... tos) {
      this.tos = tos;
      return this;
   }

   public Mail setCcs(String... ccs) {
      this.ccs = ccs;
      return this;
   }

   public Mail setBccs(String... bccs) {
      this.bccs = bccs;
      return this;
   }

   public Mail setReply(String... reply) {
      this.reply = reply;
      return this;
   }

   public Mail setTitle(String title) {
      this.title = title;
      return this;
   }

   public Mail setContent(String content) {
      this.content = content;
      return this;
   }

   public Mail setHtml(boolean isHtml) {
      this.isHtml = isHtml;
      return this;
   }

   public Mail setContent(String content, boolean isHtml) {
      this.setContent(content);
      return this.setHtml(isHtml);
   }

   public Mail setFiles(File... files) {
      if (ArrayUtil.isEmpty((Object[])files)) {
         return this;
      } else {
         DataSource[] attachments = new DataSource[files.length];

         for(int i = 0; i < files.length; ++i) {
            attachments[i] = new FileDataSource(files[i]);
         }

         return this.setAttachments(attachments);
      }
   }

   public Mail setAttachments(DataSource... attachments) {
      if (ArrayUtil.isNotEmpty((Object[])attachments)) {
         Charset charset = this.mailAccount.getCharset();

         try {
            DataSource[] var5 = attachments;
            int var6 = attachments.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               DataSource attachment = var5[var7];
               MimeBodyPart bodyPart = new MimeBodyPart();
               bodyPart.setDataHandler(new DataHandler(attachment));
               String nameEncoded = attachment.getName();
               if (this.mailAccount.isEncodefilename()) {
                  nameEncoded = InternalMailUtil.encodeText(nameEncoded, charset);
               }

               bodyPart.setFileName(nameEncoded);
               if (StrUtil.startWith(attachment.getContentType(), "image/")) {
                  bodyPart.setContentID(nameEncoded);
               }

               this.multipart.addBodyPart(bodyPart);
            }
         } catch (MessagingException var9) {
            throw new MailException(var9);
         }
      }

      return this;
   }

   public Mail addImage(String cid, InputStream imageStream) {
      return this.addImage(cid, imageStream, (String)null);
   }

   public Mail addImage(String cid, InputStream imageStream, String contentType) {
      ByteArrayDataSource imgSource;
      try {
         imgSource = new ByteArrayDataSource(imageStream, (String)ObjectUtil.defaultIfNull(contentType, (Object)"image/jpeg"));
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }

      imgSource.setName(cid);
      return this.setAttachments(imgSource);
   }

   public Mail addImage(String cid, File imageFile) {
      InputStream in = null;

      Mail var4;
      try {
         in = FileUtil.getInputStream(imageFile);
         var4 = this.addImage(cid, in, FileTypeMap.getDefaultFileTypeMap().getContentType(imageFile));
      } finally {
         IoUtil.close(in);
      }

      return var4;
   }

   public Mail setCharset(Charset charset) {
      this.mailAccount.setCharset(charset);
      return this;
   }

   public Mail setUseGlobalSession(boolean isUseGlobalSession) {
      this.useGlobalSession = isUseGlobalSession;
      return this;
   }

   public Mail setDebugOutput(PrintStream debugOutput) {
      this.debugOutput = debugOutput;
      return this;
   }

   public MimeMessage build() {
      try {
         return this.buildMsg();
      } catch (MessagingException var2) {
         throw new MailException(var2);
      }
   }

   public String send() throws MailException {
      try {
         return this.doSend();
      } catch (MessagingException var4) {
         if (var4 instanceof SendFailedException) {
            Address[] invalidAddresses = ((SendFailedException)var4).getInvalidAddresses();
            String msg = StrUtil.format("Invalid Addresses: {}", new Object[]{ArrayUtil.toString(invalidAddresses)});
            throw new MailException(msg, var4);
         } else {
            throw new MailException(var4);
         }
      }
   }

   private String doSend() throws MessagingException {
      MimeMessage mimeMessage = this.buildMsg();
      Transport.send(mimeMessage);
      return mimeMessage.getMessageID();
   }

   private MimeMessage buildMsg() throws MessagingException {
      Charset charset = this.mailAccount.getCharset();
      MimeMessage msg = new MimeMessage(this.getSession());
      String from = this.mailAccount.getFrom();
      if (StrUtil.isEmpty(from)) {
         msg.setFrom();
      } else {
         msg.setFrom(InternalMailUtil.parseFirstAddress(from, charset));
      }

      msg.setSubject(this.title, null == charset ? null : charset.name());
      msg.setSentDate(new Date());
      msg.setContent(this.buildContent(charset));
      msg.setRecipients(MimeMessage.RecipientType.TO, (Address[])InternalMailUtil.parseAddressFromStrs(this.tos, charset));
      if (ArrayUtil.isNotEmpty((Object[])this.ccs)) {
         msg.setRecipients(MimeMessage.RecipientType.CC, (Address[])InternalMailUtil.parseAddressFromStrs(this.ccs, charset));
      }

      if (ArrayUtil.isNotEmpty((Object[])this.bccs)) {
         msg.setRecipients(MimeMessage.RecipientType.BCC, (Address[])InternalMailUtil.parseAddressFromStrs(this.bccs, charset));
      }

      if (ArrayUtil.isNotEmpty((Object[])this.reply)) {
         msg.setReplyTo(InternalMailUtil.parseAddressFromStrs(this.reply, charset));
      }

      return msg;
   }

   private Multipart buildContent(Charset charset) throws MessagingException {
      String charsetStr = null != charset ? charset.name() : MimeUtility.getDefaultJavaCharset();
      MimeBodyPart body = new MimeBodyPart();
      body.setContent(this.content, StrUtil.format("text/{}; charset={}", new Object[]{this.isHtml ? "html" : "plain", charsetStr}));
      this.multipart.addBodyPart(body);
      return this.multipart;
   }

   private Session getSession() {
      Session session = MailUtil.getSession(this.mailAccount, this.useGlobalSession);
      if (null != this.debugOutput) {
         session.setDebugOut(this.debugOutput);
      }

      return session;
   }
}
