package cn.hutool.extra.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.Authenticator;
import javax.mail.Session;

public class MailUtil {
   public static String sendText(String to, String subject, String content, File... files) {
      return send(to, subject, content, false, files);
   }

   public static String sendHtml(String to, String subject, String content, File... files) {
      return send(to, subject, content, true, files);
   }

   public static String send(String to, String subject, String content, boolean isHtml, File... files) {
      return send((Collection)splitAddress(to), subject, content, isHtml, files);
   }

   public static String send(String to, String cc, String bcc, String subject, String content, boolean isHtml, File... files) {
      return send((Collection)splitAddress(to), (Collection)splitAddress(cc), (Collection)splitAddress(bcc), subject, (String)content, isHtml, files);
   }

   public static String sendText(Collection<String> tos, String subject, String content, File... files) {
      return send(tos, subject, content, false, files);
   }

   public static String sendHtml(Collection<String> tos, String subject, String content, File... files) {
      return send(tos, subject, content, true, files);
   }

   public static String send(Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
      return send((Collection)tos, (Collection)null, (Collection)null, subject, (String)content, isHtml, files);
   }

   public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
      return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, (Map)null, isHtml, files);
   }

   public static String send(MailAccount mailAccount, String to, String subject, String content, boolean isHtml, File... files) {
      return send((MailAccount)mailAccount, (Collection)splitAddress(to), subject, (String)content, isHtml, files);
   }

   public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, boolean isHtml, File... files) {
      return send((MailAccount)mailAccount, (Collection)tos, (Collection)null, (Collection)null, subject, (String)content, isHtml, files);
   }

   public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, boolean isHtml, File... files) {
      return send(mailAccount, false, tos, ccs, bccs, subject, content, (Map)null, isHtml, files);
   }

   public static String sendHtml(String to, String subject, String content, Map<String, InputStream> imageMap, File... files) {
      return send(to, subject, content, imageMap, true, files);
   }

   public static String send(String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send((Collection)splitAddress(to), (String)subject, content, (Map)imageMap, isHtml, files);
   }

   public static String send(String to, String cc, String bcc, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send((Collection)splitAddress(to), (Collection)splitAddress(cc), (Collection)splitAddress(bcc), (String)subject, content, (Map)imageMap, isHtml, files);
   }

   public static String sendHtml(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, File... files) {
      return send(tos, subject, content, imageMap, true, files);
   }

   public static String send(Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send((Collection)tos, (Collection)null, (Collection)null, (String)subject, content, (Map)imageMap, isHtml, files);
   }

   public static String send(Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send(GlobalMailAccount.INSTANCE.getAccount(), true, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
   }

   public static String send(MailAccount mailAccount, String to, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send((MailAccount)mailAccount, (Collection)splitAddress(to), (String)subject, content, (Map)imageMap, isHtml, files);
   }

   public static String send(MailAccount mailAccount, Collection<String> tos, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send(mailAccount, tos, (Collection)null, (Collection)null, subject, content, imageMap, isHtml, files);
   }

   public static String send(MailAccount mailAccount, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      return send(mailAccount, false, tos, ccs, bccs, subject, content, imageMap, isHtml, files);
   }

   public static Session getSession(MailAccount mailAccount, boolean isSingleton) {
      Authenticator authenticator = null;
      if (mailAccount.isAuth()) {
         authenticator = new UserPassAuthenticator(mailAccount.getUser(), mailAccount.getPass());
      }

      return isSingleton ? Session.getDefaultInstance(mailAccount.getSmtpProps(), authenticator) : Session.getInstance(mailAccount.getSmtpProps(), authenticator);
   }

   private static String send(MailAccount mailAccount, boolean useGlobalSession, Collection<String> tos, Collection<String> ccs, Collection<String> bccs, String subject, String content, Map<String, InputStream> imageMap, boolean isHtml, File... files) {
      Mail mail = Mail.create(mailAccount).setUseGlobalSession(useGlobalSession);
      if (CollUtil.isNotEmpty(ccs)) {
         mail.setCcs((String[])ccs.toArray(new String[0]));
      }

      if (CollUtil.isNotEmpty(bccs)) {
         mail.setBccs((String[])bccs.toArray(new String[0]));
      }

      mail.setTos((String[])tos.toArray(new String[0]));
      mail.setTitle(subject);
      mail.setContent(content);
      mail.setHtml(isHtml);
      mail.setFiles(files);
      if (MapUtil.isNotEmpty(imageMap)) {
         Iterator var11 = imageMap.entrySet().iterator();

         while(var11.hasNext()) {
            Map.Entry<String, InputStream> entry = (Map.Entry)var11.next();
            mail.addImage((String)entry.getKey(), (InputStream)entry.getValue());
            IoUtil.close((Closeable)entry.getValue());
         }
      }

      return mail.send();
   }

   private static List<String> splitAddress(String addresses) {
      if (StrUtil.isBlank(addresses)) {
         return null;
      } else {
         Object result;
         if (StrUtil.contains(addresses, ',')) {
            result = StrUtil.splitTrim(addresses, ',');
         } else if (StrUtil.contains(addresses, ';')) {
            result = StrUtil.splitTrim(addresses, ';');
         } else {
            result = CollUtil.newArrayList((Object[])(addresses));
         }

         return (List)result;
      }
   }
}
