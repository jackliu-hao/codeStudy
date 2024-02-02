package javax.mail.internet;

import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.Part;

public interface MimePart extends Part {
  String getHeader(String paramString1, String paramString2) throws MessagingException;
  
  void addHeaderLine(String paramString) throws MessagingException;
  
  Enumeration getAllHeaderLines() throws MessagingException;
  
  Enumeration getMatchingHeaderLines(String[] paramArrayOfString) throws MessagingException;
  
  Enumeration getNonMatchingHeaderLines(String[] paramArrayOfString) throws MessagingException;
  
  String getEncoding() throws MessagingException;
  
  String getContentID() throws MessagingException;
  
  String getContentMD5() throws MessagingException;
  
  void setContentMD5(String paramString) throws MessagingException;
  
  String[] getContentLanguage() throws MessagingException;
  
  void setContentLanguage(String[] paramArrayOfString) throws MessagingException;
  
  void setText(String paramString) throws MessagingException;
  
  void setText(String paramString1, String paramString2) throws MessagingException;
  
  void setText(String paramString1, String paramString2, String paramString3) throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MimePart.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */