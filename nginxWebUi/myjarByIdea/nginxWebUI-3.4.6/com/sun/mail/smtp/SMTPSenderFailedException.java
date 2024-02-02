package com.sun.mail.smtp;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

public class SMTPSenderFailedException extends SendFailedException {
   protected InternetAddress addr;
   protected String cmd;
   protected int rc;
   private static final long serialVersionUID = 514540454964476947L;

   public SMTPSenderFailedException(InternetAddress addr, String cmd, int rc, String err) {
      super(err);
      this.addr = addr;
      this.cmd = cmd;
      this.rc = rc;
   }

   public InternetAddress getAddress() {
      return this.addr;
   }

   public String getCommand() {
      return this.cmd;
   }

   public int getReturnCode() {
      return this.rc;
   }
}
