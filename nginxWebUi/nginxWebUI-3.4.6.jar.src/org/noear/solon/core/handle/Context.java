/*      */ package org.noear.solon.core.handle;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URI;
/*      */ import java.net.URLEncoder;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.noear.solon.Solon;
/*      */ import org.noear.solon.Utils;
/*      */ import org.noear.solon.annotation.Note;
/*      */ import org.noear.solon.core.NdMap;
/*      */ import org.noear.solon.core.NvMap;
/*      */ import org.noear.solon.core.util.IpUtil;
/*      */ import org.noear.solon.core.util.PathUtil;
/*      */ import org.noear.solon.core.wrap.ClassWrap;
/*      */ 
/*      */ public abstract class Context {
/*      */   private Locale locale;
/*      */   private boolean handled;
/*      */   private boolean rendered;
/*      */   private String realIp;
/*      */   
/*      */   public static Context current() {
/*   33 */     return ContextUtil.current();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Locale getLocale() {
/*   42 */     return this.locale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocale(Locale locale) {
/*   49 */     this.locale = locale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("设置处理状态")
/*      */   public void setHandled(boolean handled) {
/*   62 */     this.handled = handled;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取处理状态")
/*      */   public boolean getHandled() {
/*   70 */     return this.handled;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("设置渲染状态")
/*      */   public void setRendered(boolean rendered) {
/*   83 */     this.rendered = rendered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取渲染状态")
/*      */   public boolean getRendered() {
/*   91 */     return this.rendered;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取请求对象")
/*      */   public abstract Object request();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取远程IP")
/*      */   public abstract String ip();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取客户端真实IP")
/*      */   public String realIp() {
/*  113 */     if (this.realIp == null) {
/*  114 */       this.realIp = IpUtil.getIp(this);
/*      */     }
/*      */     
/*  117 */     return this.realIp;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean allowMultipart = true;
/*      */   
/*      */   private String protocolAsUpper;
/*      */   private String path;
/*      */   private String pathNew;
/*      */   
/*      */   public boolean autoMultipart() {
/*  128 */     return this.allowMultipart;
/*      */   }
/*      */   private String pathAsUpper; private String accept; private String body; private String bodyNew;
/*      */   protected SessionState sessionState;
/*      */   
/*      */   public void autoMultipart(boolean auto) {
/*  134 */     this.allowMultipart = auto;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMultipart() {
/*  141 */     String temp = contentType();
/*  142 */     if (temp == null) {
/*  143 */       return false;
/*      */     }
/*  145 */     return temp.toLowerCase().contains("multipart/");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMultipartFormData() {
/*  153 */     String temp = contentType();
/*  154 */     if (temp == null) {
/*  155 */       return false;
/*      */     }
/*  157 */     return temp.toLowerCase().contains("multipart/form-data");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取请求方式")
/*      */   public abstract String method();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取请求协议")
/*      */   public abstract String protocol();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String protocolAsUpper() {
/*  179 */     if (this.protocolAsUpper == null) {
/*  180 */       this.protocolAsUpper = protocol().toUpperCase();
/*      */     }
/*      */     
/*  183 */     return this.protocolAsUpper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract URI uri();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String path() {
/*  196 */     if (this.path == null && url() != null) {
/*  197 */       this.path = uri().getPath();
/*      */       
/*  199 */       if (this.path.contains("//")) {
/*  200 */         this.path = Utils.trimDuplicates(this.path, '/');
/*      */       }
/*      */     } 
/*      */     
/*  204 */     return this.path;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void pathNew(String pathNew) {
/*  211 */     this.pathNew = pathNew;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String pathNew() {
/*  220 */     if (this.pathNew == null) {
/*  221 */       return path();
/*      */     }
/*  223 */     return this.pathNew;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NvMap pathMap(String expr) {
/*  231 */     return PathUtil.pathVarMap(path(), expr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String pathAsUpper() {
/*  240 */     if (this.pathAsUpper == null) {
/*  241 */       this.pathAsUpper = path().toUpperCase();
/*      */     }
/*      */     
/*  244 */     return this.pathAsUpper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String userAgent() {
/*  251 */     return header("User-Agent");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String url();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long contentLength();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String contentType();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String queryString();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String accept() {
/*  280 */     if (this.accept == null) {
/*  281 */       this.accept = header("Accept", "");
/*      */     }
/*      */     
/*  284 */     return this.accept;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String body() throws IOException {
/*  294 */     return body(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String body(String charset) throws IOException {
/*  301 */     if (this.body == null) {
/*  302 */       try (InputStream ins = bodyAsStream()) {
/*  303 */         this.body = Utils.transferToString(ins, charset);
/*      */       } 
/*      */     }
/*      */     
/*  307 */     return this.body;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String bodyNew() throws IOException {
/*  316 */     if (this.bodyNew == null) {
/*  317 */       return body();
/*      */     }
/*  319 */     return this.bodyNew;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void bodyNew(String bodyNew) {
/*  327 */     this.bodyNew = bodyNew;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] bodyAsBytes() throws IOException {
/*  334 */     try (InputStream ins = bodyAsStream()) {
/*  335 */       if (ins == null) {
/*  336 */         return null;
/*      */       }
/*      */       
/*  339 */       ByteArrayOutputStream outs = new ByteArrayOutputStream();
/*      */       
/*  341 */       int len = 0;
/*  342 */       byte[] buf = new byte[512];
/*  343 */       while ((len = ins.read(buf)) != -1) {
/*  344 */         outs.write(buf, 0, len);
/*      */       }
/*      */       
/*  347 */       return outs.toByteArray();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract InputStream bodyAsStream() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String[] paramValues(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String param(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String param(String paramString1, String paramString2);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int paramAsInt(String name) {
/*  375 */     return paramAsInt(name, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int paramAsInt(String name, int def) {
/*  382 */     return Integer.parseInt(param(name, String.valueOf(def)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long paramAsLong(String name) {
/*  389 */     return paramAsLong(name, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long paramAsLong(String name, long def) {
/*  396 */     return Long.parseLong(param(name, String.valueOf(def)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double paramAsDouble(String name) {
/*  403 */     return paramAsDouble(name, 0.0D);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double paramAsDouble(String name, double def) {
/*  410 */     return Double.parseDouble(param(name, String.valueOf(def)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal paramAsDecimal(String name) {
/*  417 */     return paramAsDecimal(name, BigDecimal.ZERO);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal paramAsDecimal(String name, BigDecimal def) {
/*  424 */     String tmp = param(name);
/*  425 */     if (Utils.isEmpty(tmp)) {
/*  426 */       return def;
/*      */     }
/*  428 */     return new BigDecimal(tmp);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T paramAsBean(Class<T> type) {
/*  437 */     return (T)ClassWrap.get(type).newBy(this::param, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract NvMap paramMap();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void paramSet(String name, String val) {
/*  449 */     paramMap().put(name, val);
/*  450 */     paramsAdd(name, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Map<String, List<String>> paramsMap();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void paramsAdd(String name, String val) {
/*  462 */     if (paramsMap() != null) {
/*  463 */       List<String> ary = paramsMap().get(name);
/*  464 */       if (ary == null) {
/*  465 */         ary = new ArrayList<>();
/*  466 */         paramsMap().put(name, ary);
/*      */       } 
/*  468 */       ary.add(val);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract List<UploadedFile> files(String paramString) throws Exception;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UploadedFile file(String name) throws Exception {
/*  485 */     return (UploadedFile)Utils.firstOrNull(files(name));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String cookie(String name) {
/*  494 */     return (String)cookieMap().get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String cookie(String name, String def) {
/*  504 */     return (String)cookieMap().getOrDefault(name, def);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract NvMap cookieMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String header(String name) {
/*  518 */     return (String)headerMap().get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String header(String name, String def) {
/*  528 */     return (String)headerMap().getOrDefault(name, def);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract NvMap headerMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SessionState sessionState() {
/*  542 */     if (this.sessionState == null) {
/*  543 */       this.sessionState = Bridge.sessionState(this);
/*      */     }
/*      */     
/*  546 */     return this.sessionState;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String sessionId() {
/*  553 */     return sessionState().sessionId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Object session(String name) {
/*  562 */     return sessionState().sessionGet(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("泛型转换，存在转换风险")
/*      */   public final <T> T session(String name, T def) {
/*  572 */     Object tmp = session(name);
/*  573 */     if (tmp == null) {
/*  574 */       return def;
/*      */     }
/*  576 */     return (T)tmp;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int sessionAsInt(String name) {
/*  587 */     return sessionAsInt(name, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int sessionAsInt(String name, int def) {
/*  597 */     Object tmp = session(name);
/*  598 */     if (tmp == null) {
/*  599 */       return def;
/*      */     }
/*  601 */     if (tmp instanceof Number)
/*  602 */       return ((Number)tmp).intValue(); 
/*  603 */     if (tmp instanceof String) {
/*  604 */       String str = (String)tmp;
/*  605 */       if (str.length() > 0) {
/*  606 */         return Integer.parseInt(str);
/*      */       }
/*      */     } 
/*      */     
/*  610 */     return def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long sessionAsLong(String name) {
/*  621 */     return sessionAsLong(name, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final long sessionAsLong(String name, long def) {
/*  631 */     Object tmp = session(name);
/*  632 */     if (tmp == null) {
/*  633 */       return def;
/*      */     }
/*  635 */     if (tmp instanceof Number)
/*  636 */       return ((Number)tmp).longValue(); 
/*  637 */     if (tmp instanceof String) {
/*  638 */       String str = (String)tmp;
/*  639 */       if (str.length() > 0) {
/*  640 */         return Long.parseLong(str);
/*      */       }
/*      */     } 
/*      */     
/*  644 */     return def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final double sessionAsDouble(String name) {
/*  655 */     return sessionAsDouble(name, 0.0D);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final double sessionAsDouble(String name, double def) {
/*  665 */     Object tmp = session(name);
/*  666 */     if (tmp == null) {
/*  667 */       return def;
/*      */     }
/*  669 */     if (tmp instanceof Number)
/*  670 */       return ((Number)tmp).doubleValue(); 
/*  671 */     if (tmp instanceof String) {
/*  672 */       String str = (String)tmp;
/*  673 */       if (str.length() > 0) {
/*  674 */         return Double.parseDouble(str);
/*      */       }
/*      */     } 
/*      */     
/*  678 */     return def;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void sessionSet(String name, Object val) {
/*  689 */     sessionState().sessionSet(name, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void sessionRemove(String name) {
/*  698 */     sessionState().sessionRemove(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void sessionClear() {
/*  705 */     sessionState().sessionClear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Object response();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void charset(String charset) {
/*  719 */     this.charset = Charset.forName(charset);
/*      */   }
/*      */   
/*  722 */   protected Charset charset = StandardCharsets.UTF_8;
/*      */   
/*      */   private String contentTypeNew;
/*      */ 
/*      */   
/*      */   public void contentType(String contentType) {
/*  728 */     contentTypeDoSet(contentType);
/*      */ 
/*      */     
/*  731 */     if (!"text/plain;charset=UTF-8".equals(contentType)) {
/*  732 */       this.contentTypeNew = contentType;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String contentTypeNew() {
/*  740 */     return this.contentTypeNew;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract void contentTypeDoSet(String paramString);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void output(byte[] paramArrayOfbyte);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void output(InputStream paramInputStream);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract OutputStream outputStream() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void output(String str) {
/*  767 */     if (str != null) {
/*  768 */       attrSet("output", str);
/*  769 */       output(str.getBytes(this.charset));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void output(Throwable ex) {
/*  777 */     output(Utils.getFullStackTrace(ex));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputAsJson(String json) {
/*  784 */     contentType("application/json;charset=utf-8");
/*  785 */     output(json);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputAsHtml(String html) {
/*  792 */     contentType("text/html;charset=utf-8");
/*  793 */     if (!html.startsWith("<")) {
/*  794 */       StringBuilder sb = new StringBuilder();
/*  795 */       sb.append("<!doctype html>");
/*  796 */       sb.append("<html>");
/*  797 */       sb.append(html);
/*  798 */       sb.append("</html>");
/*      */       
/*  800 */       output(sb.toString());
/*      */     } else {
/*  802 */       output(html);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputAsFile(DownloadedFile file) throws IOException {
/*  810 */     if (Utils.isNotEmpty(file.contentType)) {
/*  811 */       contentType(file.contentType);
/*      */     }
/*      */     
/*  814 */     if (Utils.isNotEmpty(file.name)) {
/*  815 */       String fileName = URLEncoder.encode(file.name, Solon.encoding());
/*  816 */       headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
/*      */     } 
/*      */     
/*  819 */     Utils.transferTo(file.content, outputStream());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void outputAsFile(File file) throws IOException {
/*  826 */     if (Utils.isNotEmpty(file.getName())) {
/*  827 */       String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
/*  828 */       headerSet("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
/*      */     } 
/*      */     
/*  831 */     try (InputStream ins = new FileInputStream(file)) {
/*  832 */       Utils.transferTo(ins, outputStream());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void headerSet(String paramString1, String paramString2);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void headerAdd(String paramString1, String paramString2);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cookieSet(String name, String val) {
/*  850 */     cookieSet(name, val, null, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cookieSet(String name, String val, int maxAge) {
/*  857 */     cookieSet(name, val, null, maxAge);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cookieSet(String name, String val, String domain, int maxAge) {
/*  864 */     cookieSet(name, val, domain, "/", maxAge);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void cookieSet(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cookieRemove(String name) {
/*  876 */     cookieSet(name, "", 0);
/*      */   }
/*      */ 
/*      */   
/*      */   @Note("跳转地址")
/*      */   public abstract void redirect(String paramString);
/*      */ 
/*      */   
/*      */   @Note("跳转地址")
/*      */   public abstract void redirect(String paramString, int paramInt);
/*      */ 
/*      */   
/*      */   @Note("转发")
/*      */   public void forward(String pathNew) {
/*  890 */     pathNew(pathNew);
/*      */     
/*  892 */     Solon.app().tryHandle(this);
/*  893 */     setHandled(true);
/*  894 */     setRendered(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("获取输出状态")
/*      */   public abstract int status();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("设置输出状态")
/*      */   public void status(int status) {
/*  908 */     statusDoSet(status);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void statusSet(int status) {
/*  913 */     statusDoSet(status);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  919 */   private NdMap attrMap = null; private boolean _remoting; @Note("处理结果")
/*      */   public Object result;
/*      */   @Note("获取自定义特性并转为Map")
/*      */   public Map<String, Object> attrMap() {
/*  923 */     if (this.attrMap == null) {
/*  924 */       this.attrMap = new NdMap();
/*      */     }
/*      */     
/*  927 */     return (Map<String, Object>)this.attrMap;
/*      */   }
/*      */   @Note("处理错误")
/*      */   public Throwable errors;
/*      */   
/*      */   protected abstract void statusDoSet(int paramInt);
/*      */   
/*      */   public <T> T attr(String name, T def) {
/*  935 */     Object val = attrMap().get(name);
/*      */     
/*  937 */     if (val == null) {
/*  938 */       return def;
/*      */     }
/*      */     
/*  941 */     return (T)val;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T attr(String name) {
/*  948 */     return (T)attrMap().get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void attrSet(String name, Object val) {
/*  955 */     attrMap().put(name, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void attrSet(Map<String, Object> map) {
/*  962 */     attrMap().putAll(map);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void attrClear() {
/*  969 */     attrMap().clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void render(Object obj) throws Throwable {
/*  977 */     setRendered(true);
/*  978 */     RenderManager.global.render(obj, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void render(String view, Map<String, ?> data) throws Throwable {
/*  985 */     render(new ModelAndView(view, data));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final String renderAndReturn(Object obj) throws Throwable {
/*  992 */     return RenderManager.global.renderAndReturn(obj, this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("是否为远程调用")
/*      */   public boolean remoting() {
/* 1002 */     return this._remoting;
/*      */   }
/*      */   
/*      */   public void remotingSet(boolean remoting) {
/* 1006 */     this._remoting = remoting;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("冲刷")
/*      */   public abstract void flush() throws IOException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("提交响应")
/*      */   protected void commit() throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("关闭响应")
/*      */   public void close() throws IOException {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("控制器?")
/*      */   public Object controller() {
/* 1040 */     return attr("controller");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Note("动作?")
/*      */   public Action action() {
/* 1048 */     return attr("action");
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */