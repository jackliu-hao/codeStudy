package javax.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.activation.DataHandler;

public interface Part {
  public static final String ATTACHMENT = "attachment";
  
  public static final String INLINE = "inline";
  
  int getSize() throws MessagingException;
  
  int getLineCount() throws MessagingException;
  
  String getContentType() throws MessagingException;
  
  boolean isMimeType(String paramString) throws MessagingException;
  
  String getDisposition() throws MessagingException;
  
  void setDisposition(String paramString) throws MessagingException;
  
  String getDescription() throws MessagingException;
  
  void setDescription(String paramString) throws MessagingException;
  
  String getFileName() throws MessagingException;
  
  void setFileName(String paramString) throws MessagingException;
  
  InputStream getInputStream() throws IOException, MessagingException;
  
  DataHandler getDataHandler() throws MessagingException;
  
  Object getContent() throws IOException, MessagingException;
  
  void setDataHandler(DataHandler paramDataHandler) throws MessagingException;
  
  void setContent(Object paramObject, String paramString) throws MessagingException;
  
  void setText(String paramString) throws MessagingException;
  
  void setContent(Multipart paramMultipart) throws MessagingException;
  
  void writeTo(OutputStream paramOutputStream) throws IOException, MessagingException;
  
  String[] getHeader(String paramString) throws MessagingException;
  
  void setHeader(String paramString1, String paramString2) throws MessagingException;
  
  void addHeader(String paramString1, String paramString2) throws MessagingException;
  
  void removeHeader(String paramString) throws MessagingException;
  
  Enumeration getAllHeaders() throws MessagingException;
  
  Enumeration getMatchingHeaders(String[] paramArrayOfString) throws MessagingException;
  
  Enumeration getNonMatchingHeaders(String[] paramArrayOfString) throws MessagingException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Part.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */