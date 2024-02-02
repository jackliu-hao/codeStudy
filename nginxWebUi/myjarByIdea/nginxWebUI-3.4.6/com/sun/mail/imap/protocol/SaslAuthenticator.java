package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;

public interface SaslAuthenticator {
   boolean authenticate(String[] var1, String var2, String var3, String var4, String var5) throws ProtocolException;
}
