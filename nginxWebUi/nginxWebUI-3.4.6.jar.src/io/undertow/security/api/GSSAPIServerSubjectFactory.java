package io.undertow.security.api;

import java.security.GeneralSecurityException;
import javax.security.auth.Subject;

public interface GSSAPIServerSubjectFactory {
  Subject getSubjectForHost(String paramString) throws GeneralSecurityException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\api\GSSAPIServerSubjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */