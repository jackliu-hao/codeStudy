package com.sun.mail.smtp;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

public class SMTPSendFailedException extends SendFailedException {
   protected InternetAddress addr;
   protected String cmd;
   protected int rc;
   private static final long serialVersionUID = 8049122628728932894L;

   public SMTPSendFailedException(String cmd, int rc, String err, Exception ex, Address[] vs, Address[] vus, Address[] inv) {
      super(err, ex, vs, vus, inv);
      this.cmd = cmd;
      this.rc = rc;
   }

   public String getCommand() {
      return this.cmd;
   }

   public int getReturnCode() {
      return this.rc;
   }
}
