package org.apache.http.impl.auth;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public abstract class GGSSchemeBase extends AuthSchemeBase {
   private final Log log;
   private final Base64 base64codec;
   private final boolean stripPort;
   private final boolean useCanonicalHostname;
   private State state;
   private byte[] token;

   GGSSchemeBase(boolean stripPort, boolean useCanonicalHostname) {
      this.log = LogFactory.getLog(this.getClass());
      this.base64codec = new Base64(0);
      this.stripPort = stripPort;
      this.useCanonicalHostname = useCanonicalHostname;
      this.state = GGSSchemeBase.State.UNINITIATED;
   }

   GGSSchemeBase(boolean stripPort) {
      this(stripPort, true);
   }

   GGSSchemeBase() {
      this(true, true);
   }

   protected GSSManager getManager() {
      return GSSManager.getInstance();
   }

   protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer) throws GSSException {
      return this.generateGSSToken(input, oid, authServer, (Credentials)null);
   }

   protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer, Credentials credentials) throws GSSException {
      GSSManager manager = this.getManager();
      GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
      GSSCredential gssCredential;
      if (credentials instanceof KerberosCredentials) {
         gssCredential = ((KerberosCredentials)credentials).getGSSCredential();
      } else {
         gssCredential = null;
      }

      GSSContext gssContext = this.createGSSContext(manager, oid, serverName, gssCredential);
      return input != null ? gssContext.initSecContext(input, 0, input.length) : gssContext.initSecContext(new byte[0], 0, 0);
   }

   GSSContext createGSSContext(GSSManager manager, Oid oid, GSSName serverName, GSSCredential gssCredential) throws GSSException {
      GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, gssCredential, 0);
      gssContext.requestMutualAuth(true);
      return gssContext;
   }

   /** @deprecated */
   @Deprecated
   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
      return null;
   }

   protected byte[] generateToken(byte[] input, String authServer, Credentials credentials) throws GSSException {
      return this.generateToken(input, authServer);
   }

   public boolean isComplete() {
      return this.state == GGSSchemeBase.State.TOKEN_GENERATED || this.state == GGSSchemeBase.State.FAILED;
   }

   /** @deprecated */
   @Deprecated
   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
      return this.authenticate(credentials, request, (HttpContext)null);
   }

   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
      Args.notNull(request, "HTTP request");
      switch (this.state) {
         case UNINITIATED:
            throw new AuthenticationException(this.getSchemeName() + " authentication has not been initiated");
         case FAILED:
            throw new AuthenticationException(this.getSchemeName() + " authentication has failed");
         case CHALLENGE_RECEIVED:
            try {
               HttpRoute route = (HttpRoute)context.getAttribute("http.route");
               if (route == null) {
                  throw new AuthenticationException("Connection route is not available");
               } else {
                  HttpHost host;
                  if (this.isProxy()) {
                     host = route.getProxyHost();
                     if (host == null) {
                        host = route.getTargetHost();
                     }
                  } else {
                     host = route.getTargetHost();
                  }

                  String hostname = host.getHostName();
                  if (this.useCanonicalHostname) {
                     try {
                        hostname = this.resolveCanonicalHostname(hostname);
                     } catch (UnknownHostException var9) {
                     }
                  }

                  String authServer;
                  if (this.stripPort) {
                     authServer = hostname;
                  } else {
                     authServer = hostname + ":" + host.getPort();
                  }

                  if (this.log.isDebugEnabled()) {
                     this.log.debug("init " + authServer);
                  }

                  this.token = this.generateToken(this.token, authServer, credentials);
                  this.state = GGSSchemeBase.State.TOKEN_GENERATED;
               }
            } catch (GSSException var10) {
               this.state = GGSSchemeBase.State.FAILED;
               if (var10.getMajor() != 9 && var10.getMajor() != 8) {
                  if (var10.getMajor() == 13) {
                     throw new InvalidCredentialsException(var10.getMessage(), var10);
                  }

                  if (var10.getMajor() != 10 && var10.getMajor() != 19 && var10.getMajor() != 20) {
                     throw new AuthenticationException(var10.getMessage());
                  }

                  throw new AuthenticationException(var10.getMessage(), var10);
               }

               throw new InvalidCredentialsException(var10.getMessage(), var10);
            }
         case TOKEN_GENERATED:
            String tokenstr = new String(this.base64codec.encode(this.token));
            if (this.log.isDebugEnabled()) {
               this.log.debug("Sending response '" + tokenstr + "' back to the auth server");
            }

            CharArrayBuffer buffer = new CharArrayBuffer(32);
            if (this.isProxy()) {
               buffer.append("Proxy-Authorization");
            } else {
               buffer.append("Authorization");
            }

            buffer.append(": Negotiate ");
            buffer.append(tokenstr);
            return new BufferedHeader(buffer);
         default:
            throw new IllegalStateException("Illegal state: " + this.state);
      }
   }

   protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
      String challenge = buffer.substringTrimmed(beginIndex, endIndex);
      if (this.log.isDebugEnabled()) {
         this.log.debug("Received challenge '" + challenge + "' from the auth server");
      }

      if (this.state == GGSSchemeBase.State.UNINITIATED) {
         this.token = Base64.decodeBase64(challenge.getBytes());
         this.state = GGSSchemeBase.State.CHALLENGE_RECEIVED;
      } else {
         this.log.debug("Authentication already attempted");
         this.state = GGSSchemeBase.State.FAILED;
      }

   }

   private String resolveCanonicalHostname(String host) throws UnknownHostException {
      InetAddress in = InetAddress.getByName(host);
      String canonicalServer = in.getCanonicalHostName();
      return in.getHostAddress().contentEquals(canonicalServer) ? host : canonicalServer;
   }

   static enum State {
      UNINITIATED,
      CHALLENGE_RECEIVED,
      TOKEN_GENERATED,
      FAILED;
   }
}
