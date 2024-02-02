package org.noear.solon.core.handle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.NdMap;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.IpUtil;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.wrap.ClassWrap;

public abstract class Context {
   private Locale locale;
   private boolean handled;
   private boolean rendered;
   private String realIp;
   private boolean allowMultipart = true;
   private String protocolAsUpper;
   private String path;
   private String pathNew;
   private String pathAsUpper;
   private String accept;
   private String body;
   private String bodyNew;
   protected SessionState sessionState;
   protected Charset charset;
   private String contentTypeNew;
   private NdMap attrMap;
   private boolean _remoting;
   @Note("处理结果")
   public Object result;
   @Note("处理错误")
   public Throwable errors;

   public Context() {
      this.charset = StandardCharsets.UTF_8;
      this.attrMap = null;
   }

   public static Context current() {
      return ContextUtil.current();
   }

   public Locale getLocale() {
      return this.locale;
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   @Note("设置处理状态")
   public void setHandled(boolean handled) {
      this.handled = handled;
   }

   @Note("获取处理状态")
   public boolean getHandled() {
      return this.handled;
   }

   @Note("设置渲染状态")
   public void setRendered(boolean rendered) {
      this.rendered = rendered;
   }

   @Note("获取渲染状态")
   public boolean getRendered() {
      return this.rendered;
   }

   @Note("获取请求对象")
   public abstract Object request();

   @Note("获取远程IP")
   public abstract String ip();

   @Note("获取客户端真实IP")
   public String realIp() {
      if (this.realIp == null) {
         this.realIp = IpUtil.getIp(this);
      }

      return this.realIp;
   }

   public boolean autoMultipart() {
      return this.allowMultipart;
   }

   public void autoMultipart(boolean auto) {
      this.allowMultipart = auto;
   }

   public boolean isMultipart() {
      String temp = this.contentType();
      return temp == null ? false : temp.toLowerCase().contains("multipart/");
   }

   public boolean isMultipartFormData() {
      String temp = this.contentType();
      return temp == null ? false : temp.toLowerCase().contains("multipart/form-data");
   }

   @Note("获取请求方式")
   public abstract String method();

   @Note("获取请求协议")
   public abstract String protocol();

   public String protocolAsUpper() {
      if (this.protocolAsUpper == null) {
         this.protocolAsUpper = this.protocol().toUpperCase();
      }

      return this.protocolAsUpper;
   }

   public abstract URI uri();

   public String path() {
      if (this.path == null && this.url() != null) {
         this.path = this.uri().getPath();
         if (this.path.contains("//")) {
            this.path = Utils.trimDuplicates(this.path, '/');
         }
      }

      return this.path;
   }

   public void pathNew(String pathNew) {
      this.pathNew = pathNew;
   }

   public String pathNew() {
      return this.pathNew == null ? this.path() : this.pathNew;
   }

   public NvMap pathMap(String expr) {
      return PathUtil.pathVarMap(this.path(), expr);
   }

   public String pathAsUpper() {
      if (this.pathAsUpper == null) {
         this.pathAsUpper = this.path().toUpperCase();
      }

      return this.pathAsUpper;
   }

   public String userAgent() {
      return this.header("User-Agent");
   }

   public abstract String url();

   public abstract long contentLength();

   public abstract String contentType();

   public abstract String queryString();

   public String accept() {
      if (this.accept == null) {
         this.accept = this.header("Accept", "");
      }

      return this.accept;
   }

   public String body() throws IOException {
      return this.body((String)null);
   }

   public String body(String charset) throws IOException {
      if (this.body == null) {
         InputStream ins = this.bodyAsStream();
         Throwable var3 = null;

         try {
            this.body = Utils.transferToString(ins, charset);
         } catch (Throwable var12) {
            var3 = var12;
            throw var12;
         } finally {
            if (ins != null) {
               if (var3 != null) {
                  try {
                     ins.close();
                  } catch (Throwable var11) {
                     var3.addSuppressed(var11);
                  }
               } else {
                  ins.close();
               }
            }

         }
      }

      return this.body;
   }

   public String bodyNew() throws IOException {
      return this.bodyNew == null ? this.body() : this.bodyNew;
   }

   public void bodyNew(String bodyNew) {
      this.bodyNew = bodyNew;
   }

   public byte[] bodyAsBytes() throws IOException {
      InputStream ins = this.bodyAsStream();
      Throwable var2 = null;

      try {
         ByteArrayOutputStream outs;
         if (ins == null) {
            outs = null;
            return (byte[])outs;
         } else {
            outs = new ByteArrayOutputStream();
            int len = false;
            byte[] buf = new byte[512];

            int len;
            while((len = ins.read(buf)) != -1) {
               outs.write(buf, 0, len);
            }

            byte[] var6 = outs.toByteArray();
            return var6;
         }
      } catch (Throwable var16) {
         var2 = var16;
         throw var16;
      } finally {
         if (ins != null) {
            if (var2 != null) {
               try {
                  ins.close();
               } catch (Throwable var15) {
                  var2.addSuppressed(var15);
               }
            } else {
               ins.close();
            }
         }

      }
   }

   public abstract InputStream bodyAsStream() throws IOException;

   public abstract String[] paramValues(String name);

   public abstract String param(String name);

   public abstract String param(String name, String def);

   public int paramAsInt(String name) {
      return this.paramAsInt(name, 0);
   }

   public int paramAsInt(String name, int def) {
      return Integer.parseInt(this.param(name, String.valueOf(def)));
   }

   public long paramAsLong(String name) {
      return this.paramAsLong(name, 0L);
   }

   public long paramAsLong(String name, long def) {
      return Long.parseLong(this.param(name, String.valueOf(def)));
   }

   public double paramAsDouble(String name) {
      return this.paramAsDouble(name, 0.0);
   }

   public double paramAsDouble(String name, double def) {
      return Double.parseDouble(this.param(name, String.valueOf(def)));
   }

   public BigDecimal paramAsDecimal(String name) {
      return this.paramAsDecimal(name, BigDecimal.ZERO);
   }

   public BigDecimal paramAsDecimal(String name, BigDecimal def) {
      String tmp = this.param(name);
      return Utils.isEmpty(tmp) ? def : new BigDecimal(tmp);
   }

   public <T> T paramAsBean(Class<T> type) {
      return ClassWrap.get(type).newBy(this::param, this);
   }

   public abstract NvMap paramMap();

   public void paramSet(String name, String val) {
      this.paramMap().put(name, val);
      this.paramsAdd(name, val);
   }

   public abstract Map<String, List<String>> paramsMap();

   public void paramsAdd(String name, String val) {
      if (this.paramsMap() != null) {
         List<String> ary = (List)this.paramsMap().get(name);
         if (ary == null) {
            ary = new ArrayList();
            this.paramsMap().put(name, ary);
         }

         ((List)ary).add(val);
      }

   }

   public abstract List<UploadedFile> files(String name) throws Exception;

   public UploadedFile file(String name) throws Exception {
      return (UploadedFile)Utils.firstOrNull(this.files(name));
   }

   public String cookie(String name) {
      return (String)this.cookieMap().get(name);
   }

   public String cookie(String name, String def) {
      return (String)this.cookieMap().getOrDefault(name, def);
   }

   public abstract NvMap cookieMap();

   public String header(String name) {
      return (String)this.headerMap().get(name);
   }

   public String header(String name, String def) {
      return (String)this.headerMap().getOrDefault(name, def);
   }

   public abstract NvMap headerMap();

   public SessionState sessionState() {
      if (this.sessionState == null) {
         this.sessionState = Bridge.sessionState(this);
      }

      return this.sessionState;
   }

   public final String sessionId() {
      return this.sessionState().sessionId();
   }

   public final Object session(String name) {
      return this.sessionState().sessionGet(name);
   }

   @Note("泛型转换，存在转换风险")
   public final <T> T session(String name, T def) {
      Object tmp = this.session(name);
      return tmp == null ? def : tmp;
   }

   public final int sessionAsInt(String name) {
      return this.sessionAsInt(name, 0);
   }

   public final int sessionAsInt(String name, int def) {
      Object tmp = this.session(name);
      if (tmp == null) {
         return def;
      } else if (tmp instanceof Number) {
         return ((Number)tmp).intValue();
      } else {
         if (tmp instanceof String) {
            String str = (String)tmp;
            if (str.length() > 0) {
               return Integer.parseInt(str);
            }
         }

         return def;
      }
   }

   public final long sessionAsLong(String name) {
      return this.sessionAsLong(name, 0L);
   }

   public final long sessionAsLong(String name, long def) {
      Object tmp = this.session(name);
      if (tmp == null) {
         return def;
      } else if (tmp instanceof Number) {
         return ((Number)tmp).longValue();
      } else {
         if (tmp instanceof String) {
            String str = (String)tmp;
            if (str.length() > 0) {
               return Long.parseLong(str);
            }
         }

         return def;
      }
   }

   public final double sessionAsDouble(String name) {
      return this.sessionAsDouble(name, 0.0);
   }

   public final double sessionAsDouble(String name, double def) {
      Object tmp = this.session(name);
      if (tmp == null) {
         return def;
      } else if (tmp instanceof Number) {
         return ((Number)tmp).doubleValue();
      } else {
         if (tmp instanceof String) {
            String str = (String)tmp;
            if (str.length() > 0) {
               return Double.parseDouble(str);
            }
         }

         return def;
      }
   }

   public final void sessionSet(String name, Object val) {
      this.sessionState().sessionSet(name, val);
   }

   public final void sessionRemove(String name) {
      this.sessionState().sessionRemove(name);
   }

   public final void sessionClear() {
      this.sessionState().sessionClear();
   }

   public abstract Object response();

   public void charset(String charset) {
      this.charset = Charset.forName(charset);
   }

   public void contentType(String contentType) {
      this.contentTypeDoSet(contentType);
      if (!"text/plain;charset=UTF-8".equals(contentType)) {
         this.contentTypeNew = contentType;
      }

   }

   public String contentTypeNew() {
      return this.contentTypeNew;
   }

   protected abstract void contentTypeDoSet(String contentType);

   public abstract void output(byte[] bytes);

   public abstract void output(InputStream stream);

   public abstract OutputStream outputStream() throws IOException;

   public void output(String str) {
      if (str != null) {
         this.attrSet("output", str);
         this.output(str.getBytes(this.charset));
      }

   }

   public void output(Throwable ex) {
      this.output(Utils.getFullStackTrace(ex));
   }

   public void outputAsJson(String json) {
      this.contentType("application/json;charset=utf-8");
      this.output(json);
   }

   public void outputAsHtml(String html) {
      this.contentType("text/html;charset=utf-8");
      if (!html.startsWith("<")) {
         StringBuilder sb = new StringBuilder();
         sb.append("<!doctype html>");
         sb.append("<html>");
         sb.append(html);
         sb.append("</html>");
         this.output(sb.toString());
      } else {
         this.output(html);
      }

   }

   public void outputAsFile(DownloadedFile file) throws IOException {
      if (Utils.isNotEmpty(file.contentType)) {
         this.contentType(file.contentType);
      }

      if (Utils.isNotEmpty(file.name)) {
         String fileName = URLEncoder.encode(file.name, Solon.encoding());
         this.headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
      }

      Utils.transferTo(file.content, this.outputStream());
   }

   public void outputAsFile(File file) throws IOException {
      if (Utils.isNotEmpty(file.getName())) {
         String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
         this.headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
      }

      InputStream ins = new FileInputStream(file);
      Throwable var3 = null;

      try {
         Utils.transferTo(ins, this.outputStream());
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (ins != null) {
            if (var3 != null) {
               try {
                  ins.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               ins.close();
            }
         }

      }

   }

   public abstract void headerSet(String name, String val);

   public abstract void headerAdd(String name, String val);

   public void cookieSet(String name, String val) {
      this.cookieSet(name, val, (String)null, -1);
   }

   public void cookieSet(String name, String val, int maxAge) {
      this.cookieSet(name, val, (String)null, maxAge);
   }

   public void cookieSet(String name, String val, String domain, int maxAge) {
      this.cookieSet(name, val, domain, "/", maxAge);
   }

   public abstract void cookieSet(String name, String val, String domain, String path, int maxAge);

   public void cookieRemove(String name) {
      this.cookieSet(name, "", 0);
   }

   @Note("跳转地址")
   public abstract void redirect(String url);

   @Note("跳转地址")
   public abstract void redirect(String url, int code);

   @Note("转发")
   public void forward(String pathNew) {
      this.pathNew(pathNew);
      Solon.app().tryHandle(this);
      this.setHandled(true);
      this.setRendered(true);
   }

   @Note("获取输出状态")
   public abstract int status();

   @Note("设置输出状态")
   public void status(int status) {
      this.statusDoSet(status);
   }

   /** @deprecated */
   @Deprecated
   public void statusSet(int status) {
      this.statusDoSet(status);
   }

   protected abstract void statusDoSet(int status);

   @Note("获取自定义特性并转为Map")
   public Map<String, Object> attrMap() {
      if (this.attrMap == null) {
         this.attrMap = new NdMap();
      }

      return this.attrMap;
   }

   public <T> T attr(String name, T def) {
      Object val = this.attrMap().get(name);
      return val == null ? def : val;
   }

   public <T> T attr(String name) {
      return this.attrMap().get(name);
   }

   public void attrSet(String name, Object val) {
      this.attrMap().put(name, val);
   }

   public void attrSet(Map<String, Object> map) {
      this.attrMap().putAll(map);
   }

   public void attrClear() {
      this.attrMap().clear();
   }

   public final void render(Object obj) throws Throwable {
      this.setRendered(true);
      RenderManager.global.render(obj, this);
   }

   public final void render(String view, Map<String, ?> data) throws Throwable {
      this.render(new ModelAndView(view, data));
   }

   public final String renderAndReturn(Object obj) throws Throwable {
      return RenderManager.global.renderAndReturn(obj, this);
   }

   @Note("是否为远程调用")
   public boolean remoting() {
      return this._remoting;
   }

   public void remotingSet(boolean remoting) {
      this._remoting = remoting;
   }

   @Note("冲刷")
   public abstract void flush() throws IOException;

   @Note("提交响应")
   protected void commit() throws IOException {
   }

   @Note("关闭响应")
   public void close() throws IOException {
   }

   @Note("控制器?")
   public Object controller() {
      return this.attr("controller");
   }

   @Note("动作?")
   public Action action() {
      return (Action)this.attr("action");
   }
}
