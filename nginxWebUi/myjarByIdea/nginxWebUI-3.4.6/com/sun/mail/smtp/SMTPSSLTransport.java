package com.sun.mail.smtp;

import javax.mail.Session;
import javax.mail.URLName;

public class SMTPSSLTransport extends SMTPTransport {
   public SMTPSSLTransport(Session session, URLName urlname) {
      super(session, urlname, "smtps", true);
   }
}
