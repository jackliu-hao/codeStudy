package org.noear.solon.core.handle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.noear.solon.core.NvMap;

public class ContextEmpty extends Context {
   private NvMap paramMap = null;
   NvMap cookieMap = null;
   private NvMap headerMap = null;
   private int status = 0;

   public static Context create() {
      return new ContextEmpty();
   }

   public ContextEmpty() {
      this.sessionState = new SessionStateEmpty();
   }

   public Object request() {
      return null;
   }

   public String ip() {
      return null;
   }

   public String method() {
      return null;
   }

   public String protocol() {
      return null;
   }

   public URI uri() {
      return null;
   }

   public String url() {
      return null;
   }

   public long contentLength() {
      return 0L;
   }

   public String contentType() {
      return null;
   }

   public String queryString() {
      return null;
   }

   public InputStream bodyAsStream() throws IOException {
      return null;
   }

   public String[] paramValues(String key) {
      return new String[0];
   }

   public String param(String key) {
      return (String)this.paramMap().get(key);
   }

   public String param(String key, String def) {
      return (String)this.paramMap().getOrDefault(key, def);
   }

   public NvMap paramMap() {
      if (this.paramMap == null) {
         this.paramMap = new NvMap();
      }

      return this.paramMap;
   }

   public Map<String, List<String>> paramsMap() {
      return null;
   }

   public List<UploadedFile> files(String key) throws Exception {
      return null;
   }

   public String cookie(String key) {
      return (String)this.cookieMap().get(key);
   }

   public String cookie(String key, String def) {
      return (String)this.cookieMap().getOrDefault(key, def);
   }

   public NvMap cookieMap() {
      if (this.cookieMap == null) {
         this.cookieMap = new NvMap();
      }

      return this.cookieMap;
   }

   public NvMap headerMap() {
      if (this.headerMap == null) {
         this.headerMap = new NvMap();
      }

      return this.headerMap;
   }

   public Object response() {
      return null;
   }

   protected void contentTypeDoSet(String contentType) {
   }

   public void output(byte[] bytes) {
   }

   public void output(InputStream stream) {
   }

   public OutputStream outputStream() {
      return null;
   }

   public void headerSet(String key, String val) {
      this.headerMap().put(key, val);
   }

   public void headerAdd(String key, String val) {
      this.headerMap().put(key, val);
   }

   public void cookieSet(String key, String val, String domain, String path, int maxAge) {
      this.cookieMap().put(key, val);
   }

   public void redirect(String url) {
   }

   public void redirect(String url, int code) {
   }

   public int status() {
      return this.status;
   }

   protected void statusDoSet(int status) {
      this.status = status;
   }

   public void flush() throws IOException {
   }
}
