package com.sun.mail.smtp;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class SMTPMessage extends MimeMessage {
   public static final int NOTIFY_NEVER = -1;
   public static final int NOTIFY_SUCCESS = 1;
   public static final int NOTIFY_FAILURE = 2;
   public static final int NOTIFY_DELAY = 4;
   public static final int RETURN_FULL = 1;
   public static final int RETURN_HDRS = 2;
   private static final String[] returnOptionString = new String[]{null, "FULL", "HDRS"};
   private String envelopeFrom;
   private int notifyOptions = 0;
   private int returnOption = 0;
   private boolean sendPartial = false;
   private boolean allow8bitMIME = false;
   private String submitter = null;
   private String extension = null;

   public SMTPMessage(Session session) {
      super(session);
   }

   public SMTPMessage(Session session, InputStream is) throws MessagingException {
      super(session, is);
   }

   public SMTPMessage(MimeMessage source) throws MessagingException {
      super(source);
   }

   public void setEnvelopeFrom(String from) {
      this.envelopeFrom = from;
   }

   public String getEnvelopeFrom() {
      return this.envelopeFrom;
   }

   public void setNotifyOptions(int options) {
      if (options >= -1 && options < 8) {
         this.notifyOptions = options;
      } else {
         throw new IllegalArgumentException("Bad return option");
      }
   }

   public int getNotifyOptions() {
      return this.notifyOptions;
   }

   String getDSNNotify() {
      if (this.notifyOptions == 0) {
         return null;
      } else if (this.notifyOptions == -1) {
         return "NEVER";
      } else {
         StringBuffer sb = new StringBuffer();
         if ((this.notifyOptions & 1) != 0) {
            sb.append("SUCCESS");
         }

         if ((this.notifyOptions & 2) != 0) {
            if (sb.length() != 0) {
               sb.append(',');
            }

            sb.append("FAILURE");
         }

         if ((this.notifyOptions & 4) != 0) {
            if (sb.length() != 0) {
               sb.append(',');
            }

            sb.append("DELAY");
         }

         return sb.toString();
      }
   }

   public void setReturnOption(int option) {
      if (option >= 0 && option <= 2) {
         this.returnOption = option;
      } else {
         throw new IllegalArgumentException("Bad return option");
      }
   }

   public int getReturnOption() {
      return this.returnOption;
   }

   String getDSNRet() {
      return returnOptionString[this.returnOption];
   }

   public void setAllow8bitMIME(boolean allow) {
      this.allow8bitMIME = allow;
   }

   public boolean getAllow8bitMIME() {
      return this.allow8bitMIME;
   }

   public void setSendPartial(boolean partial) {
      this.sendPartial = partial;
   }

   public boolean getSendPartial() {
      return this.sendPartial;
   }

   public String getSubmitter() {
      return this.submitter;
   }

   public void setSubmitter(String submitter) {
      this.submitter = submitter;
   }

   public String getMailExtension() {
      return this.extension;
   }

   public void setMailExtension(String extension) {
      this.extension = extension;
   }
}
