package io.undertow.security.idm;

public interface IdentityManager {
   Account verify(Account var1);

   Account verify(String var1, Credential var2);

   Account verify(Credential var1);
}
