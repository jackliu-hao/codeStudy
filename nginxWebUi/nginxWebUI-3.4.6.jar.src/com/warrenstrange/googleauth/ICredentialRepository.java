package com.warrenstrange.googleauth;

import java.util.List;

public interface ICredentialRepository {
  String getSecretKey(String paramString);
  
  void saveUserCredentials(String paramString1, String paramString2, int paramInt, List<Integer> paramList);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\ICredentialRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */