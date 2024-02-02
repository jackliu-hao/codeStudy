package javax.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.activation.DataHandler;

public interface Part {
   String ATTACHMENT = "attachment";
   String INLINE = "inline";

   int getSize() throws MessagingException;

   int getLineCount() throws MessagingException;

   String getContentType() throws MessagingException;

   boolean isMimeType(String var1) throws MessagingException;

   String getDisposition() throws MessagingException;

   void setDisposition(String var1) throws MessagingException;

   String getDescription() throws MessagingException;

   void setDescription(String var1) throws MessagingException;

   String getFileName() throws MessagingException;

   void setFileName(String var1) throws MessagingException;

   InputStream getInputStream() throws IOException, MessagingException;

   DataHandler getDataHandler() throws MessagingException;

   Object getContent() throws IOException, MessagingException;

   void setDataHandler(DataHandler var1) throws MessagingException;

   void setContent(Object var1, String var2) throws MessagingException;

   void setText(String var1) throws MessagingException;

   void setContent(Multipart var1) throws MessagingException;

   void writeTo(OutputStream var1) throws IOException, MessagingException;

   String[] getHeader(String var1) throws MessagingException;

   void setHeader(String var1, String var2) throws MessagingException;

   void addHeader(String var1, String var2) throws MessagingException;

   void removeHeader(String var1) throws MessagingException;

   Enumeration getAllHeaders() throws MessagingException;

   Enumeration getMatchingHeaders(String[] var1) throws MessagingException;

   Enumeration getNonMatchingHeaders(String[] var1) throws MessagingException;
}
