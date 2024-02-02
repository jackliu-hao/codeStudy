package io.undertow.servlet.spec;

import io.undertow.server.handlers.form.FormData;
import io.undertow.servlet.UndertowServletMessages;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

public class PartImpl implements Part {
   private final String name;
   private final FormData.FormValue formValue;
   private final MultipartConfigElement config;
   private final ServletContextImpl servletContext;
   private final HttpServletRequestImpl servletRequest;

   public PartImpl(String name, FormData.FormValue formValue, MultipartConfigElement config, ServletContextImpl servletContext, HttpServletRequestImpl servletRequest) {
      this.name = name;
      this.formValue = formValue;
      this.config = config;
      this.servletContext = servletContext;
      this.servletRequest = servletRequest;
   }

   public InputStream getInputStream() throws IOException {
      if (this.formValue.isFileItem()) {
         return this.formValue.getFileItem().getInputStream();
      } else {
         String charset;
         if (this.formValue.getCharset() != null) {
            charset = this.formValue.getCharset();
         } else if (this.servletRequest.getCharacterEncoding() != null) {
            charset = this.servletRequest.getCharacterEncoding();
         } else {
            charset = this.servletContext.getDeployment().getDefaultRequestCharset().name();
         }

         return new ByteArrayInputStream(this.formValue.getValue().getBytes(charset));
      }
   }

   public String getContentType() {
      return this.formValue.getHeaders().getFirst(Headers.CONTENT_TYPE);
   }

   public String getName() {
      return this.name;
   }

   public String getSubmittedFileName() {
      return this.formValue.getFileName();
   }

   public long getSize() {
      try {
         if (this.formValue.isFileItem()) {
            return this.formValue.getFileItem().getFileSize();
         } else {
            return this.formValue.getCharset() != null ? (long)this.formValue.getValue().getBytes(this.formValue.getCharset()).length : (long)this.formValue.getValue().length();
         }
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void write(String fileName) throws IOException {
      Path target = Paths.get(fileName);
      if (!target.isAbsolute()) {
         if (this.config.getLocation().isEmpty()) {
            target = this.servletContext.getDeployment().getDeploymentInfo().getTempPath().resolve(fileName);
         } else {
            target = Paths.get(this.config.getLocation(), fileName);
         }
      }

      if (this.formValue.isFileItem()) {
         this.formValue.getFileItem().write(target);
      }

   }

   public void delete() throws IOException {
      if (this.formValue.isFileItem()) {
         try {
            this.formValue.getFileItem().delete();
         } catch (IOException var2) {
            throw UndertowServletMessages.MESSAGES.deleteFailed(this.formValue.getPath());
         }
      }

   }

   public String getHeader(String name) {
      return this.formValue.getHeaders().getFirst(new HttpString(name));
   }

   public Collection<String> getHeaders(String name) {
      HeaderValues values = this.formValue.getHeaders().get(new HttpString(name));
      return (Collection)(values == null ? Collections.emptyList() : values);
   }

   public Collection<String> getHeaderNames() {
      Set<String> ret = new HashSet();
      Iterator var2 = this.formValue.getHeaders().getHeaderNames().iterator();

      while(var2.hasNext()) {
         HttpString i = (HttpString)var2.next();
         ret.add(i.toString());
      }

      return ret;
   }
}
