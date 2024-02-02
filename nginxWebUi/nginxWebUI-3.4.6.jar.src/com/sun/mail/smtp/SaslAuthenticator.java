package com.sun.mail.smtp;

import javax.mail.MessagingException;

public interface SaslAuthenticator {
  boolean authenticate(String[] paramArrayOfString, String paramString1, String paramString2, String paramString3, String paramString4) throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\smtp\SaslAuthenticator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */