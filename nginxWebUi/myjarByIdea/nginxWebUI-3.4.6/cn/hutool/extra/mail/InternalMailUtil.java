package cn.hutool.extra.mail;

import cn.hutool.core.util.ArrayUtil;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

public class InternalMailUtil {
   public static InternetAddress[] parseAddressFromStrs(String[] addrStrs, Charset charset) {
      List<InternetAddress> resultList = new ArrayList(addrStrs.length);
      String[] var4 = addrStrs;
      int var5 = addrStrs.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String addrStr = var4[var6];
         InternetAddress[] addrs = parseAddress(addrStr, charset);
         if (ArrayUtil.isNotEmpty((Object[])addrs)) {
            Collections.addAll(resultList, addrs);
         }
      }

      return (InternetAddress[])resultList.toArray(new InternetAddress[0]);
   }

   public static InternetAddress parseFirstAddress(String address, Charset charset) {
      InternetAddress[] internetAddresses = parseAddress(address, charset);
      if (ArrayUtil.isEmpty((Object[])internetAddresses)) {
         try {
            return new InternetAddress(address);
         } catch (AddressException var4) {
            throw new MailException(var4);
         }
      } else {
         return internetAddresses[0];
      }
   }

   public static InternetAddress[] parseAddress(String address, Charset charset) {
      InternetAddress[] addresses;
      try {
         addresses = InternetAddress.parse(address);
      } catch (AddressException var10) {
         throw new MailException(var10);
      }

      if (ArrayUtil.isNotEmpty((Object[])addresses)) {
         String charsetStr = null == charset ? null : charset.name();
         InternetAddress[] var4 = addresses;
         int var5 = addresses.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            InternetAddress internetAddress = var4[var6];

            try {
               internetAddress.setPersonal(internetAddress.getPersonal(), charsetStr);
            } catch (UnsupportedEncodingException var9) {
               throw new MailException(var9);
            }
         }
      }

      return addresses;
   }

   public static String encodeText(String text, Charset charset) {
      try {
         return MimeUtility.encodeText(text, charset.name(), (String)null);
      } catch (UnsupportedEncodingException var3) {
         return text;
      }
   }
}
