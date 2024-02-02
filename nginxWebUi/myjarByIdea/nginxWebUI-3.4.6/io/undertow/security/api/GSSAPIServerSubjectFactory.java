package io.undertow.security.api;

import java.security.GeneralSecurityException;
import javax.security.auth.Subject;

public interface GSSAPIServerSubjectFactory {
   Subject getSubjectForHost(String var1) throws GeneralSecurityException;
}
