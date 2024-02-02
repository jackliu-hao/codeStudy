package javax.mail;

public interface QuotaAwareStore {
  Quota[] getQuota(String paramString) throws MessagingException;
  
  void setQuota(Quota paramQuota) throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\QuotaAwareStore.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */