package javax.mail;

import javax.activation.DataSource;

public interface MultipartDataSource extends DataSource {
  int getCount();
  
  BodyPart getBodyPart(int paramInt) throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\MultipartDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */