/*    */ package io.undertow.servlet;
/*    */ 
/*    */ import io.undertow.servlet.api.DeploymentManager;
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.nio.file.Path;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.Servlet;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import org.jboss.logging.Messages;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageBundle;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @MessageBundle(projectCode = "UT")
/*    */ public interface UndertowServletMessages
/*    */ {
/* 45 */   public static final UndertowServletMessages MESSAGES = (UndertowServletMessages)Messages.getBundle(UndertowServletMessages.class);
/*    */   
/*    */   @Message(id = 10000, value = "%s cannot be null")
/*    */   IllegalArgumentException paramCannotBeNull(String paramString);
/*    */   
/*    */   @Message(id = 10001, value = "%s cannot be null for %s named %s")
/*    */   IllegalArgumentException paramCannotBeNull(String paramString1, String paramString2, String paramString3);
/*    */   
/*    */   @Message(id = 10002, value = "Deployments can only be removed when in undeployed state, but state was %s")
/*    */   IllegalStateException canOnlyRemoveDeploymentsWhenUndeployed(DeploymentManager.State paramState);
/*    */   
/*    */   @Message(id = 10003, value = "Cannot call getInputStream(), getReader() already called")
/*    */   IllegalStateException getReaderAlreadyCalled();
/*    */   
/*    */   @Message(id = 10004, value = "Cannot call getReader(), getInputStream() already called")
/*    */   IllegalStateException getInputStreamAlreadyCalled();
/*    */   
/*    */   @Message(id = 10005, value = "Cannot call getOutputStream(), getWriter() already called")
/*    */   IllegalStateException getWriterAlreadyCalled();
/*    */   
/*    */   @Message(id = 10006, value = "Cannot call getWriter(), getOutputStream() already called")
/*    */   IllegalStateException getOutputStreamAlreadyCalled();
/*    */   
/*    */   @Message(id = 10007, value = "Two servlets specified with same mapping %s")
/*    */   IllegalArgumentException twoServletsWithSameMapping(String paramString);
/*    */   
/*    */   @Message(id = 10008, value = "Header %s cannot be converted to a date")
/*    */   IllegalArgumentException headerCannotBeConvertedToDate(String paramString);
/*    */   
/*    */   @Message(id = 10009, value = "Servlet %s of type %s does not implement javax.servlet.Servlet")
/*    */   IllegalArgumentException servletMustImplementServlet(String paramString, Class<? extends Servlet> paramClass);
/*    */   
/*    */   @Message(id = 10010, value = "%s of type %s must have a default constructor")
/*    */   IllegalArgumentException componentMustHaveDefaultConstructor(String paramString, Class<?> paramClass);
/*    */   
/*    */   @Message(id = 10011, value = "Filter %s of type %s does not implement javax.servlet.Filter")
/*    */   IllegalArgumentException filterMustImplementFilter(String paramString, Class<? extends Filter> paramClass);
/*    */   
/*    */   @Message(id = 10012, value = "Listener class %s must implement at least one listener interface")
/*    */   IllegalArgumentException listenerMustImplementListenerClass(Class<?> paramClass);
/*    */   
/*    */   @Message(id = 10013, value = "Could not instantiate %s")
/*    */   ServletException couldNotInstantiateComponent(String paramString, @Cause Exception paramException);
/*    */   
/*    */   @Message(id = 10014, value = "Could not load class %s")
/*    */   RuntimeException cannotLoadClass(String paramString, @Cause Exception paramException);
/*    */   
/*    */   @Message(id = 10015, value = "Could not delete file %s")
/*    */   IOException deleteFailed(Path paramPath);
/*    */   
/*    */   @Message(id = 10016, value = "Not a multi part request")
/*    */   ServletException notAMultiPartRequest();
/*    */   
/*    */   @Message(id = 10018, value = "Async not started")
/*    */   IllegalStateException asyncNotStarted();
/*    */   
/*    */   @Message(id = 10019, value = "Response already commited")
/*    */   IllegalStateException responseAlreadyCommited();
/*    */   
/*    */   @Message(id = 10020, value = "Content has been written")
/*    */   IllegalStateException contentHasBeenWritten();
/*    */   
/*    */   @Message(id = 10021, value = "Path %s must start with a /")
/*    */   MalformedURLException pathMustStartWithSlash(String paramString);
/*    */   
/*    */   @Message(id = 10022, value = "Session is invalid")
/*    */   IllegalStateException sessionIsInvalid();
/*    */   
/*    */   @Message(id = 10023, value = "Request %s was not original or a wrapper")
/*    */   IllegalArgumentException requestWasNotOriginalOrWrapper(ServletRequest paramServletRequest);
/*    */   
/*    */   @Message(id = 10024, value = "Response %s was not original or a wrapper")
/*    */   IllegalArgumentException responseWasNotOriginalOrWrapper(ServletResponse paramServletResponse);
/*    */   
/*    */   @Message(id = 10025, value = "Async request already dispatched")
/*    */   IllegalStateException asyncRequestAlreadyDispatched();
/*    */   
/*    */   @Message(id = 10026, value = "Async is not supported for this request, as not all filters or Servlets were marked as supporting async")
/*    */   IllegalStateException startAsyncNotAllowed();
/*    */   
/*    */   @Message(id = 10027, value = "Not implemented")
/*    */   IllegalStateException notImplemented();
/*    */   
/*    */   @Message(id = 10028, value = "Async processing already started")
/*    */   IllegalStateException asyncAlreadyStarted();
/*    */   
/*    */   @Message(id = 10029, value = "Stream is closed")
/*    */   IOException streamIsClosed();
/*    */   
/*    */   @Message(id = 10030, value = "User already logged in")
/*    */   ServletException userAlreadyLoggedIn();
/*    */   
/*    */   @Message(id = 10031, value = "Login failed")
/*    */   ServletException loginFailed();
/*    */   
/*    */   @Message(id = 10032, value = "Authenticationfailed")
/*    */   ServletException authenticationFailed();
/*    */   
/*    */   @Message(id = 10033, value = "No session")
/*    */   IllegalStateException noSession();
/*    */   
/*    */   @Message(id = 10034, value = "Stream not in async mode")
/*    */   IllegalStateException streamNotInAsyncMode();
/*    */   
/*    */   @Message(id = 10035, value = "Stream in async mode was not ready for IO operation")
/*    */   IllegalStateException streamNotReady();
/*    */   
/*    */   @Message(id = 10036, value = "Listener has already been set")
/*    */   IllegalStateException listenerAlreadySet();
/*    */   
/*    */   @Message(id = 10038, value = "No web socket handler was provided to the web socket servlet")
/*    */   ServletException noWebSocketHandler();
/*    */   
/*    */   @Message(id = 10039, value = "Unknown authentication mechanism %s")
/*    */   RuntimeException unknownAuthenticationMechanism(String paramString);
/*    */   
/*    */   @Message(id = 10040, value = "More than one default error page %s and %s")
/*    */   IllegalStateException moreThanOneDefaultErrorPage(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 10041, value = "The servlet context has already been initialized, you can only call this method from a ServletContainerInitializer or a ServletContextListener")
/*    */   IllegalStateException servletContextAlreadyInitialized();
/*    */   
/*    */   @Message(id = 10042, value = "This method cannot be called from a servlet context listener that has been added programatically")
/*    */   UnsupportedOperationException cannotCallFromProgramaticListener();
/*    */   
/*    */   @Message(id = 10043, value = "Cannot add servlet context listener from a programatically added listener")
/*    */   IllegalArgumentException cannotAddServletContextListener();
/*    */   
/*    */   @Message(id = 10044, value = "listener cannot be null")
/*    */   NullPointerException listenerCannotBeNull();
/*    */   
/*    */   @Message(id = 10045, value = "SSL cannot be combined with any other method")
/*    */   IllegalArgumentException sslCannotBeCombinedWithAnyOtherMethod();
/*    */   
/*    */   @Message(id = 10046, value = "No servlet context at %s to dispatch to")
/*    */   IllegalArgumentException couldNotFindContextToDispatchTo(String paramString);
/*    */   
/*    */   @Message(id = 10047, value = "Name was null")
/*    */   NullPointerException nullName();
/*    */   
/*    */   @Message(id = 10048, value = "Can only handle HTTP type of request / response: %s / %s")
/*    */   IllegalArgumentException invalidRequestResponseType(ServletRequest paramServletRequest, ServletResponse paramServletResponse);
/*    */   
/*    */   @Message(id = 10049, value = "Async request already returned to container")
/*    */   IllegalStateException asyncRequestAlreadyReturnedToContainer();
/*    */   
/*    */   @Message(id = 10050, value = "Filter %s used in filter mapping %s not found")
/*    */   IllegalStateException filterNotFound(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 10051, value = "Deployment %s has stopped")
/*    */   ServletException deploymentStopped(String paramString);
/*    */   
/*    */   @Message(id = 10052, value = "Header name was null")
/*    */   NullPointerException headerNameWasNull();
/*    */   
/*    */   @Message(id = 10053, value = "No confidential port is available to redirect the current request.")
/*    */   IllegalStateException noConfidentialPortAvailable();
/*    */   
/*    */   @Message(id = 10054, value = "Unable to create an instance factory for %s")
/*    */   RuntimeException couldNotCreateFactory(String paramString, @Cause Exception paramException);
/*    */   
/*    */   @Message(id = 10055, value = "Listener is not started")
/*    */   IllegalStateException listenerIsNotStarted();
/*    */   
/*    */   @Message(id = 10056, value = "path was not set")
/*    */   IllegalStateException pathWasNotSet();
/*    */   
/*    */   @Message(id = 10057, value = "multipart config was not present on Servlet")
/*    */   IllegalStateException multipartConfigNotPresent();
/*    */   
/*    */   @Message(id = 10058, value = "Servlet name cannot be null")
/*    */   IllegalArgumentException servletNameNull();
/*    */   
/*    */   @Message(id = 10059, value = "Param %s cannot be null")
/*    */   NullPointerException paramCannotBeNullNPE(String paramString);
/*    */   
/*    */   @Message(id = 10060, value = "Trailers not supported for this request due to %s")
/*    */   IllegalStateException trailersNotSupported(String paramString);
/*    */   
/*    */   @Message(id = 10061, value = "Invalid method for push request %s")
/*    */   IllegalArgumentException invalidMethodForPushRequest(String paramString);
/*    */   
/*    */   @Message(id = 10062, value = "No SecurityContext available")
/*    */   ServletException noSecurityContextAvailable();
/*    */   
/*    */   @Message(id = 10063, value = "Path %s must start with a / to get the request dispatcher")
/*    */   IllegalArgumentException pathMustStartWithSlashForRequestDispatcher(String paramString);
/*    */   
/*    */   @Message(id = 10064, value = "Servlet context for context path '%s' in deployment '%s' has already been initialized, can not declare roles.")
/*    */   IllegalStateException servletAlreadyInitialize(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 10065, value = "Can not set empty/null role in servlet context for context path '%s' in deployment '%s' ")
/*    */   IllegalArgumentException roleMustNotBeEmpty(String paramString1, String paramString2);
/*    */   
/*    */   @Message(id = 10066, value = "Can not set invoke 'declareRoles' from dynamic listener in servlet context for context path '%s' in deployment '%s' ")
/*    */   UnsupportedOperationException cantCallFromDynamicListener(String paramString1, String paramString2);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\UndertowServletMessages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */