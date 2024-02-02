package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.NetworkUtils;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.xnio.Bits;

public class IPAddressAccessControlHandler implements HttpHandler {
   private static final Pattern IP4_EXACT = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}");
   private static final Pattern IP4_WILDCARD = Pattern.compile("(?:(?:\\d{1,3}|\\*)\\.){3}(?:\\d{1,3}|\\*)");
   private static final Pattern IP4_SLASH = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}\\/\\d\\d?");
   private static final Pattern IP6_EXACT = Pattern.compile("^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$");
   private static final Pattern IP6_WILDCARD = Pattern.compile("(?:(?:[a-zA-Z0-9]{1,4}|\\*):){7}(?:[a-zA-Z0-9]{1,4}|\\*)");
   private static final Pattern IP6_SLASH = Pattern.compile("(?:[a-zA-Z0-9]{1,4}:){7}[a-zA-Z0-9]{1,4}\\/\\d{1,3}");
   private volatile HttpHandler next;
   private volatile boolean defaultAllow;
   private final int denyResponseCode;
   private final List<PeerMatch> ipv6acl;
   private final List<PeerMatch> ipv4acl;
   private static final boolean traceEnabled;
   private static final boolean debugEnabled;

   public IPAddressAccessControlHandler(HttpHandler next) {
      this(next, 403);
   }

   public IPAddressAccessControlHandler(HttpHandler next, int denyResponseCode) {
      this.defaultAllow = false;
      this.ipv6acl = new CopyOnWriteArrayList();
      this.ipv4acl = new CopyOnWriteArrayList();
      this.next = next;
      this.denyResponseCode = denyResponseCode;
   }

   public IPAddressAccessControlHandler() {
      this.defaultAllow = false;
      this.ipv6acl = new CopyOnWriteArrayList();
      this.ipv4acl = new CopyOnWriteArrayList();
      this.next = ResponseCodeHandler.HANDLE_404;
      this.denyResponseCode = 403;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      InetSocketAddress peer = exchange.getSourceAddress();
      if (this.isAllowed(peer.getAddress())) {
         this.next.handleRequest(exchange);
      } else {
         if (debugEnabled) {
            UndertowLogger.PREDICATE_LOGGER.debugf("Access to [%s] blocked from %s.", exchange, peer.getHostString());
         }

         exchange.setStatusCode(this.denyResponseCode);
         exchange.endExchange();
      }

   }

   boolean isAllowed(InetAddress address) {
      Iterator var2;
      PeerMatch rule;
      if (address instanceof Inet4Address) {
         var2 = this.ipv4acl.iterator();

         while(var2.hasNext()) {
            rule = (PeerMatch)var2.next();
            if (traceEnabled) {
               UndertowLogger.PREDICATE_LOGGER.tracef("Comparing rule [%s] to IPv4 address %s.", rule.toPredicateString(), address.getHostAddress());
            }

            if (rule.matches(address)) {
               return !rule.isDeny();
            }
         }
      } else if (address instanceof Inet6Address) {
         var2 = this.ipv6acl.iterator();

         while(var2.hasNext()) {
            rule = (PeerMatch)var2.next();
            if (traceEnabled) {
               UndertowLogger.PREDICATE_LOGGER.tracef("Comparing rule [%s] to IPv6 address %s.", rule.toPredicateString(), address.getHostAddress());
            }

            if (rule.matches(address)) {
               return !rule.isDeny();
            }
         }
      }

      return this.defaultAllow;
   }

   public int getDenyResponseCode() {
      return this.denyResponseCode;
   }

   public boolean isDefaultAllow() {
      return this.defaultAllow;
   }

   public IPAddressAccessControlHandler setDefaultAllow(boolean defaultAllow) {
      this.defaultAllow = defaultAllow;
      return this;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public IPAddressAccessControlHandler setNext(HttpHandler next) {
      this.next = next;
      return this;
   }

   public IPAddressAccessControlHandler addAllow(String peer) {
      return this.addRule(peer, false);
   }

   public IPAddressAccessControlHandler addDeny(String peer) {
      return this.addRule(peer, true);
   }

   public IPAddressAccessControlHandler clearRules() {
      this.ipv4acl.clear();
      this.ipv6acl.clear();
      return this;
   }

   private IPAddressAccessControlHandler addRule(String peer, boolean deny) {
      if (IP4_EXACT.matcher(peer).matches()) {
         this.addIpV4ExactMatch(peer, deny);
      } else if (IP4_WILDCARD.matcher(peer).matches()) {
         this.addIpV4WildcardMatch(peer, deny);
      } else if (IP4_SLASH.matcher(peer).matches()) {
         this.addIpV4SlashPrefix(peer, deny);
      } else if (IP6_EXACT.matcher(peer).matches()) {
         this.addIpV6ExactMatch(peer, deny);
      } else if (IP6_WILDCARD.matcher(peer).matches()) {
         this.addIpV6WildcardMatch(peer, deny);
      } else {
         if (!IP6_SLASH.matcher(peer).matches()) {
            throw UndertowMessages.MESSAGES.notAValidIpPattern(peer);
         }

         this.addIpV6SlashPrefix(peer, deny);
      }

      return this;
   }

   private void addIpV6SlashPrefix(String peer, boolean deny) {
      String[] components = peer.split("\\/");
      String[] parts = components[0].split("\\:");
      int maskLen = Integer.parseInt(components[1]);

      assert parts.length == 8;

      byte[] pattern = new byte[16];
      byte[] mask = new byte[16];

      int i;
      for(i = 0; i < 8; ++i) {
         int val = Integer.parseInt(parts[i], 16);
         pattern[i * 2] = (byte)(val >> 8);
         pattern[i * 2 + 1] = (byte)(val & 255);
      }

      for(i = 0; i < 16; ++i) {
         if (maskLen > 8) {
            mask[i] = -1;
            maskLen -= 8;
         } else {
            if (maskLen == 0) {
               break;
            }

            mask[i] = (byte)(Bits.intBitMask(8 - maskLen, 7) & 255);
            maskLen = 0;
         }
      }

      this.ipv6acl.add(new PrefixIpV6PeerMatch(deny, peer, mask, pattern));
   }

   private void addIpV4SlashPrefix(String peer, boolean deny) {
      String[] components = peer.split("\\/");
      String[] parts = components[0].split("\\.");
      int maskLen = Integer.parseInt(components[1]);
      int mask = Bits.intBitMask(32 - maskLen, 31);
      int prefix = 0;

      for(int i = 0; i < 4; ++i) {
         prefix <<= 8;
         String part = parts[i];
         int no = Integer.parseInt(part);
         prefix |= no;
      }

      this.ipv4acl.add(new PrefixIpV4PeerMatch(deny, peer, mask, prefix));
   }

   private void addIpV6WildcardMatch(String peer, boolean deny) {
      byte[] pattern = new byte[16];
      byte[] mask = new byte[16];
      String[] parts = peer.split("\\:");

      assert parts.length == 8;

      for(int i = 0; i < 8; ++i) {
         if (!parts[i].equals("*")) {
            int val = Integer.parseInt(parts[i], 16);
            pattern[i * 2] = (byte)(val >> 8);
            pattern[i * 2 + 1] = (byte)(val & 255);
            mask[i * 2] = -1;
            mask[i * 2 + 1] = -1;
         }
      }

      this.ipv6acl.add(new PrefixIpV6PeerMatch(deny, peer, mask, pattern));
   }

   private void addIpV4WildcardMatch(String peer, boolean deny) {
      String[] parts = peer.split("\\.");
      int mask = 0;
      int prefix = 0;

      for(int i = 0; i < 4; ++i) {
         mask <<= 8;
         prefix <<= 8;
         String part = parts[i];
         if (!part.equals("*")) {
            int no = Integer.parseInt(part);
            mask |= 255;
            prefix |= no;
         }
      }

      this.ipv4acl.add(new PrefixIpV4PeerMatch(deny, peer, mask, prefix));
   }

   private void addIpV6ExactMatch(String peer, boolean deny) {
      try {
         byte[] bytes = NetworkUtils.parseIpv6AddressToBytes(peer);
         this.ipv6acl.add(new ExactIpV6PeerMatch(deny, peer, bytes));
      } catch (IOException var5) {
         throw UndertowMessages.MESSAGES.invalidACLAddress(var5);
      }
   }

   private void addIpV4ExactMatch(String peer, boolean deny) {
      String[] parts = peer.split("\\.");
      byte[] bytes = new byte[]{(byte)Integer.parseInt(parts[0]), (byte)Integer.parseInt(parts[1]), (byte)Integer.parseInt(parts[2]), (byte)Integer.parseInt(parts[3])};
      this.ipv4acl.add(new ExactIpV4PeerMatch(deny, peer, bytes));
   }

   public String toString() {
      String predicate = "ip-access-control( default-allow=" + this.defaultAllow + ", acl={ ";
      List<PeerMatch> acl = new ArrayList();
      acl.addAll(this.ipv4acl);
      acl.addAll(this.ipv6acl);
      predicate = predicate + (String)acl.stream().map((s) -> {
         return "'" + s.toPredicateString() + "'";
      }).collect(Collectors.joining(", "));
      predicate = predicate + " }";
      if (this.denyResponseCode != 403) {
         predicate = predicate + ", failure-status=" + this.denyResponseCode;
      }

      predicate = predicate + " )";
      return predicate;
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
      debugEnabled = UndertowLogger.PREDICATE_LOGGER.isDebugEnabled();
   }

   private static class Holder {
      final String rule;
      final boolean deny;

      private Holder(String rule, boolean deny) {
         this.rule = rule;
         this.deny = deny;
      }

      // $FF: synthetic method
      Holder(String x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class Wrapper implements HandlerWrapper {
      private final List<Holder> peerMatches;
      private final boolean defaultAllow;
      private final int failureStatus;

      private Wrapper(List<Holder> peerMatches, boolean defaultAllow, int failureStatus) {
         this.peerMatches = peerMatches;
         this.defaultAllow = defaultAllow;
         this.failureStatus = failureStatus;
      }

      public HttpHandler wrap(HttpHandler handler) {
         IPAddressAccessControlHandler res = new IPAddressAccessControlHandler(handler, this.failureStatus);
         Iterator var3 = this.peerMatches.iterator();

         while(var3.hasNext()) {
            Holder match = (Holder)var3.next();
            if (match.deny) {
               res.addDeny(match.rule);
            } else {
               res.addAllow(match.rule);
            }
         }

         res.setDefaultAllow(this.defaultAllow);
         return res;
      }

      // $FF: synthetic method
      Wrapper(List x0, boolean x1, int x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "ip-access-control";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("acl", String[].class);
         params.put("failure-status", Integer.TYPE);
         params.put("default-allow", Boolean.TYPE);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("acl");
      }

      public String defaultParameter() {
         return "acl";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         String[] acl = (String[])((String[])config.get("acl"));
         Boolean defaultAllow = (Boolean)config.get("default-allow");
         Integer failureStatus = (Integer)config.get("failure-status");
         List<Holder> peerMatches = new ArrayList();
         String[] var6 = acl;
         int var7 = acl.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String rule = var6[var8];
            String[] parts = rule.split(" ");
            if (parts.length != 2) {
               throw UndertowMessages.MESSAGES.invalidAclRule(rule);
            }

            if (parts[1].trim().equals("allow")) {
               peerMatches.add(new Holder(parts[0].trim(), false));
            } else {
               if (!parts[1].trim().equals("deny")) {
                  throw UndertowMessages.MESSAGES.invalidAclRule(rule);
               }

               peerMatches.add(new Holder(parts[0].trim(), true));
            }
         }

         return new Wrapper(peerMatches, defaultAllow == null ? false : defaultAllow, failureStatus == null ? 403 : failureStatus);
      }
   }

   static class PrefixIpV6PeerMatch extends PeerMatch {
      private final byte[] mask;
      private final byte[] prefix;

      protected PrefixIpV6PeerMatch(boolean deny, String pattern, byte[] mask, byte[] prefix) {
         super(deny, pattern);
         this.mask = mask;
         this.prefix = prefix;

         assert mask.length == prefix.length;

      }

      boolean matches(InetAddress address) {
         byte[] bytes = address.getAddress();
         if (bytes == null) {
            return false;
         } else if (bytes.length != this.mask.length) {
            return false;
         } else {
            for(int i = 0; i < this.mask.length; ++i) {
               if ((bytes[i] & this.mask[i]) != this.prefix[i]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static class PrefixIpV4PeerMatch extends PeerMatch {
      private final int mask;
      private final int prefix;

      protected PrefixIpV4PeerMatch(boolean deny, String pattern, int mask, int prefix) {
         super(deny, pattern);
         this.mask = mask;
         this.prefix = prefix;
      }

      boolean matches(InetAddress address) {
         byte[] bytes = address.getAddress();
         if (bytes == null) {
            return false;
         } else {
            int addressInt = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            return (addressInt & this.mask) == this.prefix;
         }
      }
   }

   static class ExactIpV6PeerMatch extends PeerMatch {
      private final byte[] address;

      protected ExactIpV6PeerMatch(boolean deny, String pattern, byte[] address) {
         super(deny, pattern);
         this.address = address;
      }

      boolean matches(InetAddress address) {
         return Arrays.equals(address.getAddress(), this.address);
      }
   }

   static class ExactIpV4PeerMatch extends PeerMatch {
      private final byte[] address;

      protected ExactIpV4PeerMatch(boolean deny, String pattern, byte[] address) {
         super(deny, pattern);
         this.address = address;
      }

      boolean matches(InetAddress address) {
         return Arrays.equals(address.getAddress(), this.address);
      }
   }

   abstract static class PeerMatch {
      private final boolean deny;
      private final String pattern;

      protected PeerMatch(boolean deny, String pattern) {
         this.deny = deny;
         this.pattern = pattern;
      }

      abstract boolean matches(InetAddress var1);

      boolean isDeny() {
         return this.deny;
      }

      public String toString() {
         return this.getClass().getSimpleName() + "{deny=" + this.deny + ", pattern='" + this.pattern + '\'' + '}';
      }

      public String toPredicateString() {
         return this.pattern + " " + (this.deny ? "deny" : "allow");
      }
   }
}
