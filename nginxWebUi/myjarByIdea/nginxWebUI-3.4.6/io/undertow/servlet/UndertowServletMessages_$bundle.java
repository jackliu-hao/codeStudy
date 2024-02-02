package io.undertow.servlet;

import io.undertow.servlet.api.DeploymentManager;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class UndertowServletMessages_$bundle implements UndertowServletMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final UndertowServletMessages_$bundle INSTANCE = new UndertowServletMessages_$bundle();
   private static final Locale LOCALE;

   protected UndertowServletMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String paramCannotBeNull1$str() {
      return "UT010000: %s cannot be null";
   }

   public final IllegalArgumentException paramCannotBeNull(String param) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramCannotBeNull1$str(), param));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String paramCannotBeNull3$str() {
      return "UT010001: %s cannot be null for %s named %s";
   }

   public final IllegalArgumentException paramCannotBeNull(String param, String componentType, String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramCannotBeNull3$str(), param, componentType, name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String canOnlyRemoveDeploymentsWhenUndeployed$str() {
      return "UT010002: Deployments can only be removed when in undeployed state, but state was %s";
   }

   public final IllegalStateException canOnlyRemoveDeploymentsWhenUndeployed(DeploymentManager.State state) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.canOnlyRemoveDeploymentsWhenUndeployed$str(), state));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String getReaderAlreadyCalled$str() {
      return "UT010003: Cannot call getInputStream(), getReader() already called";
   }

   public final IllegalStateException getReaderAlreadyCalled() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.getReaderAlreadyCalled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String getInputStreamAlreadyCalled$str() {
      return "UT010004: Cannot call getReader(), getInputStream() already called";
   }

   public final IllegalStateException getInputStreamAlreadyCalled() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.getInputStreamAlreadyCalled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String getWriterAlreadyCalled$str() {
      return "UT010005: Cannot call getOutputStream(), getWriter() already called";
   }

   public final IllegalStateException getWriterAlreadyCalled() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.getWriterAlreadyCalled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String getOutputStreamAlreadyCalled$str() {
      return "UT010006: Cannot call getWriter(), getOutputStream() already called";
   }

   public final IllegalStateException getOutputStreamAlreadyCalled() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.getOutputStreamAlreadyCalled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String twoServletsWithSameMapping$str() {
      return "UT010007: Two servlets specified with same mapping %s";
   }

   public final IllegalArgumentException twoServletsWithSameMapping(String path) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.twoServletsWithSameMapping$str(), path));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String headerCannotBeConvertedToDate$str() {
      return "UT010008: Header %s cannot be converted to a date";
   }

   public final IllegalArgumentException headerCannotBeConvertedToDate(String header) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.headerCannotBeConvertedToDate$str(), header));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String servletMustImplementServlet$str() {
      return "UT010009: Servlet %s of type %s does not implement javax.servlet.Servlet";
   }

   public final IllegalArgumentException servletMustImplementServlet(String name, Class<? extends Servlet> servletClass) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.servletMustImplementServlet$str(), name, servletClass));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String componentMustHaveDefaultConstructor$str() {
      return "UT010010: %s of type %s must have a default constructor";
   }

   public final IllegalArgumentException componentMustHaveDefaultConstructor(String componentType, Class<?> componentClass) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.componentMustHaveDefaultConstructor$str(), componentType, componentClass));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String filterMustImplementFilter$str() {
      return "UT010011: Filter %s of type %s does not implement javax.servlet.Filter";
   }

   public final IllegalArgumentException filterMustImplementFilter(String name, Class<? extends Filter> filterClass) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.filterMustImplementFilter$str(), name, filterClass));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String listenerMustImplementListenerClass$str() {
      return "UT010012: Listener class %s must implement at least one listener interface";
   }

   public final IllegalArgumentException listenerMustImplementListenerClass(Class<?> listenerClass) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.listenerMustImplementListenerClass$str(), listenerClass));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotInstantiateComponent$str() {
      return "UT010013: Could not instantiate %s";
   }

   public final ServletException couldNotInstantiateComponent(String name, Exception e) {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.couldNotInstantiateComponent$str(), name), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotLoadClass$str() {
      return "UT010014: Could not load class %s";
   }

   public final RuntimeException cannotLoadClass(String className, Exception e) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.cannotLoadClass$str(), className), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String deleteFailed$str() {
      return "UT010015: Could not delete file %s";
   }

   public final IOException deleteFailed(Path file) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.deleteFailed$str(), file));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notAMultiPartRequest$str() {
      return "UT010016: Not a multi part request";
   }

   public final ServletException notAMultiPartRequest() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.notAMultiPartRequest$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String asyncNotStarted$str() {
      return "UT010018: Async not started";
   }

   public final IllegalStateException asyncNotStarted() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.asyncNotStarted$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String responseAlreadyCommited$str() {
      return "UT010019: Response already commited";
   }

   public final IllegalStateException responseAlreadyCommited() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.responseAlreadyCommited$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String contentHasBeenWritten$str() {
      return "UT010020: Content has been written";
   }

   public final IllegalStateException contentHasBeenWritten() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.contentHasBeenWritten$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathMustStartWithSlash$str() {
      return "UT010021: Path %s must start with a /";
   }

   public final MalformedURLException pathMustStartWithSlash(String path) {
      MalformedURLException result = new MalformedURLException(String.format(this.getLoggingLocale(), this.pathMustStartWithSlash$str(), path));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionIsInvalid$str() {
      return "UT010022: Session is invalid";
   }

   public final IllegalStateException sessionIsInvalid() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionIsInvalid$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String requestWasNotOriginalOrWrapper$str() {
      return "UT010023: Request %s was not original or a wrapper";
   }

   public final IllegalArgumentException requestWasNotOriginalOrWrapper(ServletRequest request) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.requestWasNotOriginalOrWrapper$str(), request));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String responseWasNotOriginalOrWrapper$str() {
      return "UT010024: Response %s was not original or a wrapper";
   }

   public final IllegalArgumentException responseWasNotOriginalOrWrapper(ServletResponse response) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.responseWasNotOriginalOrWrapper$str(), response));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String asyncRequestAlreadyDispatched$str() {
      return "UT010025: Async request already dispatched";
   }

   public final IllegalStateException asyncRequestAlreadyDispatched() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.asyncRequestAlreadyDispatched$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String startAsyncNotAllowed$str() {
      return "UT010026: Async is not supported for this request, as not all filters or Servlets were marked as supporting async";
   }

   public final IllegalStateException startAsyncNotAllowed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.startAsyncNotAllowed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notImplemented$str() {
      return "UT010027: Not implemented";
   }

   public final IllegalStateException notImplemented() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.notImplemented$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String asyncAlreadyStarted$str() {
      return "UT010028: Async processing already started";
   }

   public final IllegalStateException asyncAlreadyStarted() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.asyncAlreadyStarted$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamIsClosed$str() {
      return "UT010029: Stream is closed";
   }

   public final IOException streamIsClosed() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.streamIsClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String userAlreadyLoggedIn$str() {
      return "UT010030: User already logged in";
   }

   public final ServletException userAlreadyLoggedIn() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.userAlreadyLoggedIn$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String loginFailed$str() {
      return "UT010031: Login failed";
   }

   public final ServletException loginFailed() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.loginFailed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String authenticationFailed$str() {
      return "UT010032: Authenticationfailed";
   }

   public final ServletException authenticationFailed() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.authenticationFailed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noSession$str() {
      return "UT010033: No session";
   }

   public final IllegalStateException noSession() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noSession$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamNotInAsyncMode$str() {
      return "UT010034: Stream not in async mode";
   }

   public final IllegalStateException streamNotInAsyncMode() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.streamNotInAsyncMode$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamNotReady$str() {
      return "UT010035: Stream in async mode was not ready for IO operation";
   }

   public final IllegalStateException streamNotReady() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.streamNotReady$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String listenerAlreadySet$str() {
      return "UT010036: Listener has already been set";
   }

   public final IllegalStateException listenerAlreadySet() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.listenerAlreadySet$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noWebSocketHandler$str() {
      return "UT010038: No web socket handler was provided to the web socket servlet";
   }

   public final ServletException noWebSocketHandler() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.noWebSocketHandler$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unknownAuthenticationMechanism$str() {
      return "UT010039: Unknown authentication mechanism %s";
   }

   public final RuntimeException unknownAuthenticationMechanism(String mechName) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.unknownAuthenticationMechanism$str(), mechName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String moreThanOneDefaultErrorPage$str() {
      return "UT010040: More than one default error page %s and %s";
   }

   public final IllegalStateException moreThanOneDefaultErrorPage(String defaultErrorPage, String location) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.moreThanOneDefaultErrorPage$str(), defaultErrorPage, location));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String servletContextAlreadyInitialized$str() {
      return "UT010041: The servlet context has already been initialized, you can only call this method from a ServletContainerInitializer or a ServletContextListener";
   }

   public final IllegalStateException servletContextAlreadyInitialized() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.servletContextAlreadyInitialized$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotCallFromProgramaticListener$str() {
      return "UT010042: This method cannot be called from a servlet context listener that has been added programatically";
   }

   public final UnsupportedOperationException cannotCallFromProgramaticListener() {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.cannotCallFromProgramaticListener$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotAddServletContextListener$str() {
      return "UT010043: Cannot add servlet context listener from a programatically added listener";
   }

   public final IllegalArgumentException cannotAddServletContextListener() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.cannotAddServletContextListener$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String listenerCannotBeNull$str() {
      return "UT010044: listener cannot be null";
   }

   public final NullPointerException listenerCannotBeNull() {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.listenerCannotBeNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sslCannotBeCombinedWithAnyOtherMethod$str() {
      return "UT010045: SSL cannot be combined with any other method";
   }

   public final IllegalArgumentException sslCannotBeCombinedWithAnyOtherMethod() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.sslCannotBeCombinedWithAnyOtherMethod$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotFindContextToDispatchTo$str() {
      return "UT010046: No servlet context at %s to dispatch to";
   }

   public final IllegalArgumentException couldNotFindContextToDispatchTo(String originalContextPath) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.couldNotFindContextToDispatchTo$str(), originalContextPath));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String nullName$str() {
      return "UT010047: Name was null";
   }

   public final NullPointerException nullName() {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.nullName$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidRequestResponseType$str() {
      return "UT010048: Can only handle HTTP type of request / response: %s / %s";
   }

   public final IllegalArgumentException invalidRequestResponseType(ServletRequest request, ServletResponse response) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidRequestResponseType$str(), request, response));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String asyncRequestAlreadyReturnedToContainer$str() {
      return "UT010049: Async request already returned to container";
   }

   public final IllegalStateException asyncRequestAlreadyReturnedToContainer() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.asyncRequestAlreadyReturnedToContainer$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String filterNotFound$str() {
      return "UT010050: Filter %s used in filter mapping %s not found";
   }

   public final IllegalStateException filterNotFound(String filterName, String mapping) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.filterNotFound$str(), filterName, mapping));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String deploymentStopped$str() {
      return "UT010051: Deployment %s has stopped";
   }

   public final ServletException deploymentStopped(String deployment) {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.deploymentStopped$str(), deployment));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String headerNameWasNull$str() {
      return "UT010052: Header name was null";
   }

   public final NullPointerException headerNameWasNull() {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.headerNameWasNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noConfidentialPortAvailable$str() {
      return "UT010053: No confidential port is available to redirect the current request.";
   }

   public final IllegalStateException noConfidentialPortAvailable() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noConfidentialPortAvailable$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotCreateFactory$str() {
      return "UT010054: Unable to create an instance factory for %s";
   }

   public final RuntimeException couldNotCreateFactory(String className, Exception e) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.couldNotCreateFactory$str(), className), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String listenerIsNotStarted$str() {
      return "UT010055: Listener is not started";
   }

   public final IllegalStateException listenerIsNotStarted() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.listenerIsNotStarted$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathWasNotSet$str() {
      return "UT010056: path was not set";
   }

   public final IllegalStateException pathWasNotSet() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.pathWasNotSet$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String multipartConfigNotPresent$str() {
      return "UT010057: multipart config was not present on Servlet";
   }

   public final IllegalStateException multipartConfigNotPresent() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.multipartConfigNotPresent$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String servletNameNull$str() {
      return "UT010058: Servlet name cannot be null";
   }

   public final IllegalArgumentException servletNameNull() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.servletNameNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String paramCannotBeNullNPE$str() {
      return "UT010059: Param %s cannot be null";
   }

   public final NullPointerException paramCannotBeNullNPE(String name) {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.paramCannotBeNullNPE$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String trailersNotSupported$str() {
      return "UT010060: Trailers not supported for this request due to %s";
   }

   public final IllegalStateException trailersNotSupported(String reason) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.trailersNotSupported$str(), reason));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidMethodForPushRequest$str() {
      return "UT010061: Invalid method for push request %s";
   }

   public final IllegalArgumentException invalidMethodForPushRequest(String method) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidMethodForPushRequest$str(), method));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noSecurityContextAvailable$str() {
      return "UT010062: No SecurityContext available";
   }

   public final ServletException noSecurityContextAvailable() {
      ServletException result = new ServletException(String.format(this.getLoggingLocale(), this.noSecurityContextAvailable$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathMustStartWithSlashForRequestDispatcher$str() {
      return "UT010063: Path %s must start with a / to get the request dispatcher";
   }

   public final IllegalArgumentException pathMustStartWithSlashForRequestDispatcher(String path) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.pathMustStartWithSlashForRequestDispatcher$str(), path));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String servletAlreadyInitialize$str() {
      return "UT010064: Servlet context for context path '%s' in deployment '%s' has already been initialized, can not declare roles.";
   }

   public final IllegalStateException servletAlreadyInitialize(String deploymentName, String contextPath) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.servletAlreadyInitialize$str(), deploymentName, contextPath));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String roleMustNotBeEmpty$str() {
      return "UT010065: Can not set empty/null role in servlet context for context path '%s' in deployment '%s' ";
   }

   public final IllegalArgumentException roleMustNotBeEmpty(String deploymentName, String contextPath) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.roleMustNotBeEmpty$str(), deploymentName, contextPath));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cantCallFromDynamicListener$str() {
      return "UT010066: Can not set invoke 'declareRoles' from dynamic listener in servlet context for context path '%s' in deployment '%s' ";
   }

   public final UnsupportedOperationException cantCallFromDynamicListener(String deploymentName, String contextPath) {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.cantCallFromDynamicListener$str(), deploymentName, contextPath));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
