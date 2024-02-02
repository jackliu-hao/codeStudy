package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.mail.internet.MailDateFormat;

public class INTERNALDATE implements Item {
   static final char[] name = new char[]{'I', 'N', 'T', 'E', 'R', 'N', 'A', 'L', 'D', 'A', 'T', 'E'};
   public int msgno;
   protected Date date;
   private static MailDateFormat mailDateFormat = new MailDateFormat();
   private static SimpleDateFormat df;

   public INTERNALDATE(FetchResponse r) throws ParsingException {
      this.msgno = r.getNumber();
      r.skipSpaces();
      String s = r.readString();
      if (s == null) {
         throw new ParsingException("INTERNALDATE is NIL");
      } else {
         try {
            this.date = mailDateFormat.parse(s);
         } catch (ParseException var4) {
            throw new ParsingException("INTERNALDATE parse error");
         }
      }
   }

   public Date getDate() {
      return this.date;
   }

   public static String format(Date d) {
      StringBuffer sb = new StringBuffer();
      synchronized(df) {
         df.format(d, sb, new FieldPosition(0));
      }

      int rawOffsetInMins = -d.getTimezoneOffset();
      if (rawOffsetInMins < 0) {
         sb.append('-');
         rawOffsetInMins = -rawOffsetInMins;
      } else {
         sb.append('+');
      }

      int offsetInHrs = rawOffsetInMins / 60;
      int offsetInMins = rawOffsetInMins % 60;
      sb.append(Character.forDigit(offsetInHrs / 10, 10));
      sb.append(Character.forDigit(offsetInHrs % 10, 10));
      sb.append(Character.forDigit(offsetInMins / 10, 10));
      sb.append(Character.forDigit(offsetInMins % 10, 10));
      return sb.toString();
   }

   static {
      df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss ", Locale.US);
   }
}
