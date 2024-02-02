package javax.mail;

public interface QuotaAwareStore {
   Quota[] getQuota(String var1) throws MessagingException;

   void setQuota(Quota var1) throws MessagingException;
}
