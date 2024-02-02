package com.warrenstrange.googleauth;

import java.util.List;

public interface ICredentialRepository {
   String getSecretKey(String var1);

   void saveUserCredentials(String var1, String var2, int var3, List<Integer> var4);
}
