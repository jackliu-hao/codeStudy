package com.sun.mail.util;

import java.io.InputStream;
import javax.mail.MessagingException;

public interface ReadableMime {
   InputStream getMimeStream() throws MessagingException;
}
