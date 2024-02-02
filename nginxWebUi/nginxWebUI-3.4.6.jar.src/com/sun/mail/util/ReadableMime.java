package com.sun.mail.util;

import java.io.InputStream;
import javax.mail.MessagingException;

public interface ReadableMime {
  InputStream getMimeStream() throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\ReadableMime.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */