/*     */ package io.undertow.servlet;
/*     */ 
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.MalformedURLException;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UndertowServletMessages_$bundle
/*     */   implements UndertowServletMessages, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  30 */   public static final UndertowServletMessages_$bundle INSTANCE = new UndertowServletMessages_$bundle();
/*     */   protected Object readResolve() {
/*  32 */     return INSTANCE;
/*     */   }
/*  34 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  36 */     return LOCALE;
/*     */   }
/*     */   protected String paramCannotBeNull1$str() {
/*  39 */     return "UT010000: %s cannot be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramCannotBeNull(String param) {
/*  43 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramCannotBeNull1$str(), new Object[] { param }));
/*  44 */     _copyStackTraceMinusOne(result);
/*  45 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  48 */     StackTraceElement[] st = e.getStackTrace();
/*  49 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String paramCannotBeNull3$str() {
/*  52 */     return "UT010001: %s cannot be null for %s named %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramCannotBeNull(String param, String componentType, String name) {
/*  56 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramCannotBeNull3$str(), new Object[] { param, componentType, name }));
/*  57 */     _copyStackTraceMinusOne(result);
/*  58 */     return result;
/*     */   }
/*     */   protected String canOnlyRemoveDeploymentsWhenUndeployed$str() {
/*  61 */     return "UT010002: Deployments can only be removed when in undeployed state, but state was %s";
/*     */   }
/*     */   
/*     */   public final IllegalStateException canOnlyRemoveDeploymentsWhenUndeployed(DeploymentManager.State state) {
/*  65 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), canOnlyRemoveDeploymentsWhenUndeployed$str(), new Object[] { state }));
/*  66 */     _copyStackTraceMinusOne(result);
/*  67 */     return result;
/*     */   }
/*     */   protected String getReaderAlreadyCalled$str() {
/*  70 */     return "UT010003: Cannot call getInputStream(), getReader() already called";
/*     */   }
/*     */   
/*     */   public final IllegalStateException getReaderAlreadyCalled() {
/*  74 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), getReaderAlreadyCalled$str(), new Object[0]));
/*  75 */     _copyStackTraceMinusOne(result);
/*  76 */     return result;
/*     */   }
/*     */   protected String getInputStreamAlreadyCalled$str() {
/*  79 */     return "UT010004: Cannot call getReader(), getInputStream() already called";
/*     */   }
/*     */   
/*     */   public final IllegalStateException getInputStreamAlreadyCalled() {
/*  83 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), getInputStreamAlreadyCalled$str(), new Object[0]));
/*  84 */     _copyStackTraceMinusOne(result);
/*  85 */     return result;
/*     */   }
/*     */   protected String getWriterAlreadyCalled$str() {
/*  88 */     return "UT010005: Cannot call getOutputStream(), getWriter() already called";
/*     */   }
/*     */   
/*     */   public final IllegalStateException getWriterAlreadyCalled() {
/*  92 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), getWriterAlreadyCalled$str(), new Object[0]));
/*  93 */     _copyStackTraceMinusOne(result);
/*  94 */     return result;
/*     */   }
/*     */   protected String getOutputStreamAlreadyCalled$str() {
/*  97 */     return "UT010006: Cannot call getWriter(), getOutputStream() already called";
/*     */   }
/*     */   
/*     */   public final IllegalStateException getOutputStreamAlreadyCalled() {
/* 101 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), getOutputStreamAlreadyCalled$str(), new Object[0]));
/* 102 */     _copyStackTraceMinusOne(result);
/* 103 */     return result;
/*     */   }
/*     */   protected String twoServletsWithSameMapping$str() {
/* 106 */     return "UT010007: Two servlets specified with same mapping %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException twoServletsWithSameMapping(String path) {
/* 110 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), twoServletsWithSameMapping$str(), new Object[] { path }));
/* 111 */     _copyStackTraceMinusOne(result);
/* 112 */     return result;
/*     */   }
/*     */   protected String headerCannotBeConvertedToDate$str() {
/* 115 */     return "UT010008: Header %s cannot be converted to a date";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException headerCannotBeConvertedToDate(String header) {
/* 119 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), headerCannotBeConvertedToDate$str(), new Object[] { header }));
/* 120 */     _copyStackTraceMinusOne(result);
/* 121 */     return result;
/*     */   }
/*     */   protected String servletMustImplementServlet$str() {
/* 124 */     return "UT010009: Servlet %s of type %s does not implement javax.servlet.Servlet";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException servletMustImplementServlet(String name, Class<? extends Servlet> servletClass) {
/* 128 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), servletMustImplementServlet$str(), new Object[] { name, servletClass }));
/* 129 */     _copyStackTraceMinusOne(result);
/* 130 */     return result;
/*     */   }
/*     */   protected String componentMustHaveDefaultConstructor$str() {
/* 133 */     return "UT010010: %s of type %s must have a default constructor";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException componentMustHaveDefaultConstructor(String componentType, Class<?> componentClass) {
/* 137 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), componentMustHaveDefaultConstructor$str(), new Object[] { componentType, componentClass }));
/* 138 */     _copyStackTraceMinusOne(result);
/* 139 */     return result;
/*     */   }
/*     */   protected String filterMustImplementFilter$str() {
/* 142 */     return "UT010011: Filter %s of type %s does not implement javax.servlet.Filter";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException filterMustImplementFilter(String name, Class<? extends Filter> filterClass) {
/* 146 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), filterMustImplementFilter$str(), new Object[] { name, filterClass }));
/* 147 */     _copyStackTraceMinusOne(result);
/* 148 */     return result;
/*     */   }
/*     */   protected String listenerMustImplementListenerClass$str() {
/* 151 */     return "UT010012: Listener class %s must implement at least one listener interface";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException listenerMustImplementListenerClass(Class<?> listenerClass) {
/* 155 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), listenerMustImplementListenerClass$str(), new Object[] { listenerClass }));
/* 156 */     _copyStackTraceMinusOne(result);
/* 157 */     return result;
/*     */   }
/*     */   protected String couldNotInstantiateComponent$str() {
/* 160 */     return "UT010013: Could not instantiate %s";
/*     */   }
/*     */   
/*     */   public final ServletException couldNotInstantiateComponent(String name, Exception e) {
/* 164 */     ServletException result = new ServletException(String.format(getLoggingLocale(), couldNotInstantiateComponent$str(), new Object[] { name }), e);
/* 165 */     _copyStackTraceMinusOne((Throwable)result);
/* 166 */     return result;
/*     */   }
/*     */   protected String cannotLoadClass$str() {
/* 169 */     return "UT010014: Could not load class %s";
/*     */   }
/*     */   
/*     */   public final RuntimeException cannotLoadClass(String className, Exception e) {
/* 173 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), cannotLoadClass$str(), new Object[] { className }), e);
/* 174 */     _copyStackTraceMinusOne(result);
/* 175 */     return result;
/*     */   }
/*     */   protected String deleteFailed$str() {
/* 178 */     return "UT010015: Could not delete file %s";
/*     */   }
/*     */   
/*     */   public final IOException deleteFailed(Path file) {
/* 182 */     IOException result = new IOException(String.format(getLoggingLocale(), deleteFailed$str(), new Object[] { file }));
/* 183 */     _copyStackTraceMinusOne(result);
/* 184 */     return result;
/*     */   }
/*     */   protected String notAMultiPartRequest$str() {
/* 187 */     return "UT010016: Not a multi part request";
/*     */   }
/*     */   
/*     */   public final ServletException notAMultiPartRequest() {
/* 191 */     ServletException result = new ServletException(String.format(getLoggingLocale(), notAMultiPartRequest$str(), new Object[0]));
/* 192 */     _copyStackTraceMinusOne((Throwable)result);
/* 193 */     return result;
/*     */   }
/*     */   protected String asyncNotStarted$str() {
/* 196 */     return "UT010018: Async not started";
/*     */   }
/*     */   
/*     */   public final IllegalStateException asyncNotStarted() {
/* 200 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), asyncNotStarted$str(), new Object[0]));
/* 201 */     _copyStackTraceMinusOne(result);
/* 202 */     return result;
/*     */   }
/*     */   protected String responseAlreadyCommited$str() {
/* 205 */     return "UT010019: Response already commited";
/*     */   }
/*     */   
/*     */   public final IllegalStateException responseAlreadyCommited() {
/* 209 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), responseAlreadyCommited$str(), new Object[0]));
/* 210 */     _copyStackTraceMinusOne(result);
/* 211 */     return result;
/*     */   }
/*     */   protected String contentHasBeenWritten$str() {
/* 214 */     return "UT010020: Content has been written";
/*     */   }
/*     */   
/*     */   public final IllegalStateException contentHasBeenWritten() {
/* 218 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), contentHasBeenWritten$str(), new Object[0]));
/* 219 */     _copyStackTraceMinusOne(result);
/* 220 */     return result;
/*     */   }
/*     */   protected String pathMustStartWithSlash$str() {
/* 223 */     return "UT010021: Path %s must start with a /";
/*     */   }
/*     */   
/*     */   public final MalformedURLException pathMustStartWithSlash(String path) {
/* 227 */     MalformedURLException result = new MalformedURLException(String.format(getLoggingLocale(), pathMustStartWithSlash$str(), new Object[] { path }));
/* 228 */     _copyStackTraceMinusOne(result);
/* 229 */     return result;
/*     */   }
/*     */   protected String sessionIsInvalid$str() {
/* 232 */     return "UT010022: Session is invalid";
/*     */   }
/*     */   
/*     */   public final IllegalStateException sessionIsInvalid() {
/* 236 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionIsInvalid$str(), new Object[0]));
/* 237 */     _copyStackTraceMinusOne(result);
/* 238 */     return result;
/*     */   }
/*     */   protected String requestWasNotOriginalOrWrapper$str() {
/* 241 */     return "UT010023: Request %s was not original or a wrapper";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException requestWasNotOriginalOrWrapper(ServletRequest request) {
/* 245 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), requestWasNotOriginalOrWrapper$str(), new Object[] { request }));
/* 246 */     _copyStackTraceMinusOne(result);
/* 247 */     return result;
/*     */   }
/*     */   protected String responseWasNotOriginalOrWrapper$str() {
/* 250 */     return "UT010024: Response %s was not original or a wrapper";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException responseWasNotOriginalOrWrapper(ServletResponse response) {
/* 254 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), responseWasNotOriginalOrWrapper$str(), new Object[] { response }));
/* 255 */     _copyStackTraceMinusOne(result);
/* 256 */     return result;
/*     */   }
/*     */   protected String asyncRequestAlreadyDispatched$str() {
/* 259 */     return "UT010025: Async request already dispatched";
/*     */   }
/*     */   
/*     */   public final IllegalStateException asyncRequestAlreadyDispatched() {
/* 263 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), asyncRequestAlreadyDispatched$str(), new Object[0]));
/* 264 */     _copyStackTraceMinusOne(result);
/* 265 */     return result;
/*     */   }
/*     */   protected String startAsyncNotAllowed$str() {
/* 268 */     return "UT010026: Async is not supported for this request, as not all filters or Servlets were marked as supporting async";
/*     */   }
/*     */   
/*     */   public final IllegalStateException startAsyncNotAllowed() {
/* 272 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), startAsyncNotAllowed$str(), new Object[0]));
/* 273 */     _copyStackTraceMinusOne(result);
/* 274 */     return result;
/*     */   }
/*     */   protected String notImplemented$str() {
/* 277 */     return "UT010027: Not implemented";
/*     */   }
/*     */   
/*     */   public final IllegalStateException notImplemented() {
/* 281 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), notImplemented$str(), new Object[0]));
/* 282 */     _copyStackTraceMinusOne(result);
/* 283 */     return result;
/*     */   }
/*     */   protected String asyncAlreadyStarted$str() {
/* 286 */     return "UT010028: Async processing already started";
/*     */   }
/*     */   
/*     */   public final IllegalStateException asyncAlreadyStarted() {
/* 290 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), asyncAlreadyStarted$str(), new Object[0]));
/* 291 */     _copyStackTraceMinusOne(result);
/* 292 */     return result;
/*     */   }
/*     */   protected String streamIsClosed$str() {
/* 295 */     return "UT010029: Stream is closed";
/*     */   }
/*     */   
/*     */   public final IOException streamIsClosed() {
/* 299 */     IOException result = new IOException(String.format(getLoggingLocale(), streamIsClosed$str(), new Object[0]));
/* 300 */     _copyStackTraceMinusOne(result);
/* 301 */     return result;
/*     */   }
/*     */   protected String userAlreadyLoggedIn$str() {
/* 304 */     return "UT010030: User already logged in";
/*     */   }
/*     */   
/*     */   public final ServletException userAlreadyLoggedIn() {
/* 308 */     ServletException result = new ServletException(String.format(getLoggingLocale(), userAlreadyLoggedIn$str(), new Object[0]));
/* 309 */     _copyStackTraceMinusOne((Throwable)result);
/* 310 */     return result;
/*     */   }
/*     */   protected String loginFailed$str() {
/* 313 */     return "UT010031: Login failed";
/*     */   }
/*     */   
/*     */   public final ServletException loginFailed() {
/* 317 */     ServletException result = new ServletException(String.format(getLoggingLocale(), loginFailed$str(), new Object[0]));
/* 318 */     _copyStackTraceMinusOne((Throwable)result);
/* 319 */     return result;
/*     */   }
/*     */   protected String authenticationFailed$str() {
/* 322 */     return "UT010032: Authenticationfailed";
/*     */   }
/*     */   
/*     */   public final ServletException authenticationFailed() {
/* 326 */     ServletException result = new ServletException(String.format(getLoggingLocale(), authenticationFailed$str(), new Object[0]));
/* 327 */     _copyStackTraceMinusOne((Throwable)result);
/* 328 */     return result;
/*     */   }
/*     */   protected String noSession$str() {
/* 331 */     return "UT010033: No session";
/*     */   }
/*     */   
/*     */   public final IllegalStateException noSession() {
/* 335 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noSession$str(), new Object[0]));
/* 336 */     _copyStackTraceMinusOne(result);
/* 337 */     return result;
/*     */   }
/*     */   protected String streamNotInAsyncMode$str() {
/* 340 */     return "UT010034: Stream not in async mode";
/*     */   }
/*     */   
/*     */   public final IllegalStateException streamNotInAsyncMode() {
/* 344 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), streamNotInAsyncMode$str(), new Object[0]));
/* 345 */     _copyStackTraceMinusOne(result);
/* 346 */     return result;
/*     */   }
/*     */   protected String streamNotReady$str() {
/* 349 */     return "UT010035: Stream in async mode was not ready for IO operation";
/*     */   }
/*     */   
/*     */   public final IllegalStateException streamNotReady() {
/* 353 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), streamNotReady$str(), new Object[0]));
/* 354 */     _copyStackTraceMinusOne(result);
/* 355 */     return result;
/*     */   }
/*     */   protected String listenerAlreadySet$str() {
/* 358 */     return "UT010036: Listener has already been set";
/*     */   }
/*     */   
/*     */   public final IllegalStateException listenerAlreadySet() {
/* 362 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), listenerAlreadySet$str(), new Object[0]));
/* 363 */     _copyStackTraceMinusOne(result);
/* 364 */     return result;
/*     */   }
/*     */   protected String noWebSocketHandler$str() {
/* 367 */     return "UT010038: No web socket handler was provided to the web socket servlet";
/*     */   }
/*     */   
/*     */   public final ServletException noWebSocketHandler() {
/* 371 */     ServletException result = new ServletException(String.format(getLoggingLocale(), noWebSocketHandler$str(), new Object[0]));
/* 372 */     _copyStackTraceMinusOne((Throwable)result);
/* 373 */     return result;
/*     */   }
/*     */   protected String unknownAuthenticationMechanism$str() {
/* 376 */     return "UT010039: Unknown authentication mechanism %s";
/*     */   }
/*     */   
/*     */   public final RuntimeException unknownAuthenticationMechanism(String mechName) {
/* 380 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), unknownAuthenticationMechanism$str(), new Object[] { mechName }));
/* 381 */     _copyStackTraceMinusOne(result);
/* 382 */     return result;
/*     */   }
/*     */   protected String moreThanOneDefaultErrorPage$str() {
/* 385 */     return "UT010040: More than one default error page %s and %s";
/*     */   }
/*     */   
/*     */   public final IllegalStateException moreThanOneDefaultErrorPage(String defaultErrorPage, String location) {
/* 389 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), moreThanOneDefaultErrorPage$str(), new Object[] { defaultErrorPage, location }));
/* 390 */     _copyStackTraceMinusOne(result);
/* 391 */     return result;
/*     */   }
/*     */   protected String servletContextAlreadyInitialized$str() {
/* 394 */     return "UT010041: The servlet context has already been initialized, you can only call this method from a ServletContainerInitializer or a ServletContextListener";
/*     */   }
/*     */   
/*     */   public final IllegalStateException servletContextAlreadyInitialized() {
/* 398 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), servletContextAlreadyInitialized$str(), new Object[0]));
/* 399 */     _copyStackTraceMinusOne(result);
/* 400 */     return result;
/*     */   }
/*     */   protected String cannotCallFromProgramaticListener$str() {
/* 403 */     return "UT010042: This method cannot be called from a servlet context listener that has been added programatically";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException cannotCallFromProgramaticListener() {
/* 407 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), cannotCallFromProgramaticListener$str(), new Object[0]));
/* 408 */     _copyStackTraceMinusOne(result);
/* 409 */     return result;
/*     */   }
/*     */   protected String cannotAddServletContextListener$str() {
/* 412 */     return "UT010043: Cannot add servlet context listener from a programatically added listener";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException cannotAddServletContextListener() {
/* 416 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), cannotAddServletContextListener$str(), new Object[0]));
/* 417 */     _copyStackTraceMinusOne(result);
/* 418 */     return result;
/*     */   }
/*     */   protected String listenerCannotBeNull$str() {
/* 421 */     return "UT010044: listener cannot be null";
/*     */   }
/*     */   
/*     */   public final NullPointerException listenerCannotBeNull() {
/* 425 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), listenerCannotBeNull$str(), new Object[0]));
/* 426 */     _copyStackTraceMinusOne(result);
/* 427 */     return result;
/*     */   }
/*     */   protected String sslCannotBeCombinedWithAnyOtherMethod$str() {
/* 430 */     return "UT010045: SSL cannot be combined with any other method";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException sslCannotBeCombinedWithAnyOtherMethod() {
/* 434 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), sslCannotBeCombinedWithAnyOtherMethod$str(), new Object[0]));
/* 435 */     _copyStackTraceMinusOne(result);
/* 436 */     return result;
/*     */   }
/*     */   protected String couldNotFindContextToDispatchTo$str() {
/* 439 */     return "UT010046: No servlet context at %s to dispatch to";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException couldNotFindContextToDispatchTo(String originalContextPath) {
/* 443 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), couldNotFindContextToDispatchTo$str(), new Object[] { originalContextPath }));
/* 444 */     _copyStackTraceMinusOne(result);
/* 445 */     return result;
/*     */   }
/*     */   protected String nullName$str() {
/* 448 */     return "UT010047: Name was null";
/*     */   }
/*     */   
/*     */   public final NullPointerException nullName() {
/* 452 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), nullName$str(), new Object[0]));
/* 453 */     _copyStackTraceMinusOne(result);
/* 454 */     return result;
/*     */   }
/*     */   protected String invalidRequestResponseType$str() {
/* 457 */     return "UT010048: Can only handle HTTP type of request / response: %s / %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidRequestResponseType(ServletRequest request, ServletResponse response) {
/* 461 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidRequestResponseType$str(), new Object[] { request, response }));
/* 462 */     _copyStackTraceMinusOne(result);
/* 463 */     return result;
/*     */   }
/*     */   protected String asyncRequestAlreadyReturnedToContainer$str() {
/* 466 */     return "UT010049: Async request already returned to container";
/*     */   }
/*     */   
/*     */   public final IllegalStateException asyncRequestAlreadyReturnedToContainer() {
/* 470 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), asyncRequestAlreadyReturnedToContainer$str(), new Object[0]));
/* 471 */     _copyStackTraceMinusOne(result);
/* 472 */     return result;
/*     */   }
/*     */   protected String filterNotFound$str() {
/* 475 */     return "UT010050: Filter %s used in filter mapping %s not found";
/*     */   }
/*     */   
/*     */   public final IllegalStateException filterNotFound(String filterName, String mapping) {
/* 479 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), filterNotFound$str(), new Object[] { filterName, mapping }));
/* 480 */     _copyStackTraceMinusOne(result);
/* 481 */     return result;
/*     */   }
/*     */   protected String deploymentStopped$str() {
/* 484 */     return "UT010051: Deployment %s has stopped";
/*     */   }
/*     */   
/*     */   public final ServletException deploymentStopped(String deployment) {
/* 488 */     ServletException result = new ServletException(String.format(getLoggingLocale(), deploymentStopped$str(), new Object[] { deployment }));
/* 489 */     _copyStackTraceMinusOne((Throwable)result);
/* 490 */     return result;
/*     */   }
/*     */   protected String headerNameWasNull$str() {
/* 493 */     return "UT010052: Header name was null";
/*     */   }
/*     */   
/*     */   public final NullPointerException headerNameWasNull() {
/* 497 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), headerNameWasNull$str(), new Object[0]));
/* 498 */     _copyStackTraceMinusOne(result);
/* 499 */     return result;
/*     */   }
/*     */   protected String noConfidentialPortAvailable$str() {
/* 502 */     return "UT010053: No confidential port is available to redirect the current request.";
/*     */   }
/*     */   
/*     */   public final IllegalStateException noConfidentialPortAvailable() {
/* 506 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noConfidentialPortAvailable$str(), new Object[0]));
/* 507 */     _copyStackTraceMinusOne(result);
/* 508 */     return result;
/*     */   }
/*     */   protected String couldNotCreateFactory$str() {
/* 511 */     return "UT010054: Unable to create an instance factory for %s";
/*     */   }
/*     */   
/*     */   public final RuntimeException couldNotCreateFactory(String className, Exception e) {
/* 515 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), couldNotCreateFactory$str(), new Object[] { className }), e);
/* 516 */     _copyStackTraceMinusOne(result);
/* 517 */     return result;
/*     */   }
/*     */   protected String listenerIsNotStarted$str() {
/* 520 */     return "UT010055: Listener is not started";
/*     */   }
/*     */   
/*     */   public final IllegalStateException listenerIsNotStarted() {
/* 524 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), listenerIsNotStarted$str(), new Object[0]));
/* 525 */     _copyStackTraceMinusOne(result);
/* 526 */     return result;
/*     */   }
/*     */   protected String pathWasNotSet$str() {
/* 529 */     return "UT010056: path was not set";
/*     */   }
/*     */   
/*     */   public final IllegalStateException pathWasNotSet() {
/* 533 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), pathWasNotSet$str(), new Object[0]));
/* 534 */     _copyStackTraceMinusOne(result);
/* 535 */     return result;
/*     */   }
/*     */   protected String multipartConfigNotPresent$str() {
/* 538 */     return "UT010057: multipart config was not present on Servlet";
/*     */   }
/*     */   
/*     */   public final IllegalStateException multipartConfigNotPresent() {
/* 542 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), multipartConfigNotPresent$str(), new Object[0]));
/* 543 */     _copyStackTraceMinusOne(result);
/* 544 */     return result;
/*     */   }
/*     */   protected String servletNameNull$str() {
/* 547 */     return "UT010058: Servlet name cannot be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException servletNameNull() {
/* 551 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), servletNameNull$str(), new Object[0]));
/* 552 */     _copyStackTraceMinusOne(result);
/* 553 */     return result;
/*     */   }
/*     */   protected String paramCannotBeNullNPE$str() {
/* 556 */     return "UT010059: Param %s cannot be null";
/*     */   }
/*     */   
/*     */   public final NullPointerException paramCannotBeNullNPE(String name) {
/* 560 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), paramCannotBeNullNPE$str(), new Object[] { name }));
/* 561 */     _copyStackTraceMinusOne(result);
/* 562 */     return result;
/*     */   }
/*     */   protected String trailersNotSupported$str() {
/* 565 */     return "UT010060: Trailers not supported for this request due to %s";
/*     */   }
/*     */   
/*     */   public final IllegalStateException trailersNotSupported(String reason) {
/* 569 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), trailersNotSupported$str(), new Object[] { reason }));
/* 570 */     _copyStackTraceMinusOne(result);
/* 571 */     return result;
/*     */   }
/*     */   protected String invalidMethodForPushRequest$str() {
/* 574 */     return "UT010061: Invalid method for push request %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidMethodForPushRequest(String method) {
/* 578 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidMethodForPushRequest$str(), new Object[] { method }));
/* 579 */     _copyStackTraceMinusOne(result);
/* 580 */     return result;
/*     */   }
/*     */   protected String noSecurityContextAvailable$str() {
/* 583 */     return "UT010062: No SecurityContext available";
/*     */   }
/*     */   
/*     */   public final ServletException noSecurityContextAvailable() {
/* 587 */     ServletException result = new ServletException(String.format(getLoggingLocale(), noSecurityContextAvailable$str(), new Object[0]));
/* 588 */     _copyStackTraceMinusOne((Throwable)result);
/* 589 */     return result;
/*     */   }
/*     */   protected String pathMustStartWithSlashForRequestDispatcher$str() {
/* 592 */     return "UT010063: Path %s must start with a / to get the request dispatcher";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException pathMustStartWithSlashForRequestDispatcher(String path) {
/* 596 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), pathMustStartWithSlashForRequestDispatcher$str(), new Object[] { path }));
/* 597 */     _copyStackTraceMinusOne(result);
/* 598 */     return result;
/*     */   }
/*     */   protected String servletAlreadyInitialize$str() {
/* 601 */     return "UT010064: Servlet context for context path '%s' in deployment '%s' has already been initialized, can not declare roles.";
/*     */   }
/*     */   
/*     */   public final IllegalStateException servletAlreadyInitialize(String deploymentName, String contextPath) {
/* 605 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), servletAlreadyInitialize$str(), new Object[] { deploymentName, contextPath }));
/* 606 */     _copyStackTraceMinusOne(result);
/* 607 */     return result;
/*     */   }
/*     */   protected String roleMustNotBeEmpty$str() {
/* 610 */     return "UT010065: Can not set empty/null role in servlet context for context path '%s' in deployment '%s' ";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException roleMustNotBeEmpty(String deploymentName, String contextPath) {
/* 614 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), roleMustNotBeEmpty$str(), new Object[] { deploymentName, contextPath }));
/* 615 */     _copyStackTraceMinusOne(result);
/* 616 */     return result;
/*     */   }
/*     */   protected String cantCallFromDynamicListener$str() {
/* 619 */     return "UT010066: Can not set invoke 'declareRoles' from dynamic listener in servlet context for context path '%s' in deployment '%s' ";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException cantCallFromDynamicListener(String deploymentName, String contextPath) {
/* 623 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), cantCallFromDynamicListener$str(), new Object[] { deploymentName, contextPath }));
/* 624 */     _copyStackTraceMinusOne(result);
/* 625 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\UndertowServletMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */