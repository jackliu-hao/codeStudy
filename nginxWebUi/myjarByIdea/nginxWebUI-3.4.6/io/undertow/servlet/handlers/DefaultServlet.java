package io.undertow.servlet.handlers;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.DefaultResourceSupplier;
import io.undertow.server.handlers.resource.DirectoryUtils;
import io.undertow.server.handlers.resource.PreCompressedResourceSupplier;
import io.undertow.server.handlers.resource.RangeAwareResource;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceSupplier;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.DefaultServletConfig;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.util.ByteRange;
import io.undertow.util.CanonicalPathUtils;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.ETagUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultServlet extends HttpServlet {
   public static final String DIRECTORY_LISTING = "directory-listing";
   public static final String DEFAULT_ALLOWED = "default-allowed";
   public static final String ALLOWED_EXTENSIONS = "allowed-extensions";
   public static final String DISALLOWED_EXTENSIONS = "disallowed-extensions";
   public static final String RESOLVE_AGAINST_CONTEXT_ROOT = "resolve-against-context-root";
   public static final String ALLOW_POST = "allow-post";
   private static final Set<String> DEFAULT_ALLOWED_EXTENSIONS = Collections.unmodifiableSet(new HashSet(Arrays.asList("js", "css", "png", "jpg", "gif", "html", "htm", "txt", "pdf", "jpeg", "xml")));
   private Deployment deployment;
   private ResourceSupplier resourceSupplier;
   private boolean directoryListingEnabled = false;
   private boolean defaultAllowed = true;
   private Set<String> allowed;
   private Set<String> disallowed;
   private boolean resolveAgainstContextRoot;
   private boolean allowPost;

   public DefaultServlet() {
      this.allowed = DEFAULT_ALLOWED_EXTENSIONS;
      this.disallowed = Collections.emptySet();
      this.allowPost = false;
   }

   public void init(ServletConfig config) throws ServletException {
      super.init(config);
      ServletContextImpl sc = (ServletContextImpl)config.getServletContext();
      this.deployment = sc.getDeployment();
      DefaultServletConfig defaultServletConfig = this.deployment.getDeploymentInfo().getDefaultServletConfig();
      if (defaultServletConfig != null) {
         this.defaultAllowed = defaultServletConfig.isDefaultAllowed();
         this.allowed = new HashSet();
         if (defaultServletConfig.getAllowed() != null) {
            this.allowed.addAll(defaultServletConfig.getAllowed());
         }

         this.disallowed = new HashSet();
         if (defaultServletConfig.getDisallowed() != null) {
            this.disallowed.addAll(defaultServletConfig.getDisallowed());
         }
      }

      if (config.getInitParameter("default-allowed") != null) {
         this.defaultAllowed = Boolean.parseBoolean(config.getInitParameter("default-allowed"));
      }

      String listings;
      if (config.getInitParameter("allowed-extensions") != null) {
         listings = config.getInitParameter("allowed-extensions");
         this.allowed = new HashSet(Arrays.asList(listings.split(",")));
      }

      if (config.getInitParameter("disallowed-extensions") != null) {
         listings = config.getInitParameter("disallowed-extensions");
         this.disallowed = new HashSet(Arrays.asList(listings.split(",")));
      }

      if (config.getInitParameter("resolve-against-context-root") != null) {
         this.resolveAgainstContextRoot = Boolean.parseBoolean(config.getInitParameter("resolve-against-context-root"));
      }

      if (config.getInitParameter("allow-post") != null) {
         this.allowPost = Boolean.parseBoolean(config.getInitParameter("allow-post"));
      }

      if (this.deployment.getDeploymentInfo().getPreCompressedResources().isEmpty()) {
         this.resourceSupplier = new DefaultResourceSupplier(this.deployment.getDeploymentInfo().getResourceManager());
      } else {
         PreCompressedResourceSupplier preCompressedResourceSupplier = new PreCompressedResourceSupplier(this.deployment.getDeploymentInfo().getResourceManager());
         Iterator var5 = this.deployment.getDeploymentInfo().getPreCompressedResources().entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var5.next();
            preCompressedResourceSupplier.addEncoding((String)entry.getKey(), (String)entry.getValue());
         }

         this.resourceSupplier = preCompressedResourceSupplier;
      }

      listings = config.getInitParameter("directory-listing");
      if (Boolean.valueOf(listings)) {
         this.directoryListingEnabled = true;
      }

   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String path = this.getPath(req);
      if (!this.isAllowed(path, req.getDispatcherType())) {
         resp.sendError(404);
      } else {
         if (File.separatorChar != '/') {
            path = CanonicalPathUtils.canonicalize(path.replace(File.separatorChar, '/'));
         }

         HttpServerExchange exchange = SecurityActions.requireCurrentServletRequestContext().getOriginalRequest().getExchange();
         Resource resource;
         if (File.separatorChar != '/' && path.contains(File.separator)) {
            resource = null;
         } else {
            resource = this.resourceSupplier.getResource(exchange, path);
         }

         if (resource == null) {
            if (req.getDispatcherType() == DispatcherType.INCLUDE) {
               UndertowServletLogger.REQUEST_LOGGER.requestedResourceDoesNotExistForIncludeMethod(path);
               throw new FileNotFoundException(path);
            } else {
               resp.sendError(404);
            }
         } else {
            if (resource.isDirectory()) {
               if ("css".equals(req.getQueryString())) {
                  resp.setContentType("text/css");
                  resp.getWriter().write("body {\n    font-family: \"Lucida Grande\", \"Lucida Sans Unicode\", \"Trebuchet MS\", Helvetica, Arial, Verdana, sans-serif;\n    margin: 5px;\n}\n\nth.loc {\n    background-image: linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -o-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -moz-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -webkit-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    background-image: -ms-linear-gradient(bottom, rgb(153,151,153) 8%, rgb(199,199,199) 54%);\n    \n    background-image: -webkit-gradient(\n        linear,\n        left bottom,\n        left top,\n        color-stop(0.08, rgb(153,151,153)),\n        color-stop(0.54, rgb(199,199,199))\n    );\n    color: black;\n    padding: 2px;\n    font-weight: normal;\n    border: solid 1px;\n    font-size: 150%;\n    text-align: left;\n}\n\nth.label {\n    border: solid  1px;\n    text-align: left;\n    padding: 4px;\n    padding-left: 8px;\n    font-weight: normal;\n    font-size: small;\n    background-color: #e8e8e8;\n}\n\nth.offset {\n    padding-left: 32px;\n}\n\nth.footer {\n    font-size: 75%;\n    text-align: right;\n}\n\na.icon {\n    padding-left: 24px;\n    text-decoration: none;\n    color: black;\n}\n\na.icon:hover {\n    text-decoration: underline;\n}\n\ntable {\n    border: 1px solid;\n    border-spacing: 0px;\n    width: 100%;\n    border-collapse: collapse;\n}\n\ntr.odd {\n    background-color: #f3f6fa;\n}\n\ntr.odd td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.even {\n    background-color: #ffffff;\n}\n\ntr.even td {\n    padding: 2px;\n    padding-left: 8px;\n    font-size: smaller;\n}\n\ntr.eveninvis td {\n    color: #ffffff;\n}\n\ntr.oddinvis td {\n    color: #f3f6fa\n}\n\na.up {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABI0lEQVQ4y2P4//8/Ay7sM4nhPwjjUwMm0ua//Y+M0+e//QrSGDAfgvEZAjdgydHXcAzTXLjWDoxhhqBbhGLA1N0vwBhdM7ohMHVwA8yrzn4zLj/936j8FE7N6IaA1IL0gPQy2DVc+rnp3FeCmtENAekB6WXw7Lz1tWD5x/+wEIdhdI3o8iA9IL0MYZMfvq9a9+V/w+avcIzLAGQ1ID0gvQxJc56/aNn29X/vnm9wjMsAZDWtQD0gvQwFy94+6N37/f/Moz/gGJcByGpAekB6GarXf7427ciP/0vP/YRjdP/CMLIakB6QXobKDd9PN+769b91P2kYpAekl2HJhb8r11/583/9ZRIxUM+8U783MQCBGBDXAHEbibgGrBdfTiMGU2wAAPz+nxp+TnhDAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.dir {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAAA+UlEQVQ4jWP4//8/AyUYTKTNf/sfGafPf/s1be47G5IMWHL0NRxP2f3mbcaCtz/RDUbHKAZM3f2CJAw3wLzq7Dfj8tP/jcpPkYRBekB6GewaLv3cdO7r/y0XSMMgPSC9DJ6dt74WLP/4v3TVZ5IwSA9IL0PY5Ifvq9Z9+d+w+StJGKQHpJchac7zFy3bvv7v3fONJNwK1APSy5C/7O2D3r3f/888+oMkDNID0stQvf7ztWlHfvxfeu4nSRikB6SXoXLD99ONu379b91PGgbpAellWHLh38r1V/78X3+ZRAzUM/fUr00MQCAGxDVA3EYirgHrpUpupAQDAPs+7c1tGDnPAAAAAElFTkSuQmCC') left center no-repeat; background-size: 16px 16px;\n}\n\na.file {\n    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXZwQWcAAAAQAAAAEABcxq3DAAABM0lEQVQ4y5WSTW6DMBCF3xvzc4wuOEIO0kVAuUB7vJ4g3KBdoHSRROomEpusUaoAcaYLfmKoqVRLIxnJ7/M3YwJVBcknACv8b+1U9SvoP1bXa/3WNDVIAQmQBLsNOEsGQYAwDNcARgDqusbl+wIRA2NkBEyqP0s+kCOAQhhjICJdkaDIJDwEvQAhH+G+SHagWTsi4jHoAWYIOxYDZDjnb8Fn4Akvz6AHcAbx3Tp5ETwI3RwckyVtv4Fr4VEe9qq6bDB5tlnYWou2bWGtRRRF6jdwAm5Za1FVFc7nM0QERVG8A9hPDRaGpapomgZlWSJJEuR5ftpsNq8ADr9amC+SuN/vuN1uIIntdnvKsuwZwKf2wxgBxpjpX+dA4jjW4/H4kabpixt2AbvAmDX+XnsAB509ww+A8mAar+XXgQAAAABJRU5ErkJggg==') left center no-repeat;\n}");
                  return;
               }

               if ("js".equals(req.getQueryString())) {
                  resp.setContentType("application/javascript");
                  resp.getWriter().write("function growit() {\n    var table = document.getElementById(\"thetable\");\n\n    var i = table.rows.length - 1;\n    while (i-- > 0) {\n        if (table.rows[i].id == \"eraseme\") {\n            table.deleteRow(i);\n        } else {\n            break;\n        }\n    }\n    table.style.height=\"\";\n    var i = 0;\n    while (table.offsetHeight < window.innerHeight - 24) {\n        i++;\n        var tbody = table.tBodies[0];\n        var row = tbody.insertRow(tbody.rows.length);\n        row.id=\"eraseme\";\n        var cell = row.insertCell(0);\n        if (table.rows.length % 2 != 0) {\n            row.className=\"even eveninvis\";\n        } else {\n            row.className=\"odd oddinvis\";\n        }\n\n        cell.colSpan=3;\n        cell.appendChild(document.createTextNode(\"i\"));\n    }\n    table.style.height=\"100%\";\n    if (i > 0) {\n        document.documentElement.style.overflowY=\"hidden\";\n    } else {\n        document.documentElement.style.overflowY=\"auto\";\n    }\n}");
                  return;
               }

               if (this.directoryListingEnabled) {
                  resp.setContentType("text/html");
                  StringBuilder output = DirectoryUtils.renderDirectoryListing(req.getRequestURI(), resource);
                  resp.getWriter().write(output.toString());
               } else {
                  resp.sendError(403);
               }
            } else {
               if (path.endsWith("/")) {
                  resp.sendError(404);
                  return;
               }

               this.serveFileBlocking(req, resp, resource, exchange);
            }

         }
      }
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      if (this.allowPost) {
         this.doGet(req, resp);
      } else {
         switch (req.getDispatcherType()) {
            case INCLUDE:
            case FORWARD:
            case ERROR:
               this.doGet(req, resp);
               break;
            default:
               super.doPost(req, resp);
         }
      }

   }

   protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      switch (req.getDispatcherType()) {
         case INCLUDE:
         case FORWARD:
         case ERROR:
            this.doGet(req, resp);
            break;
         default:
            super.doPut(req, resp);
      }

   }

   protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      switch (req.getDispatcherType()) {
         case INCLUDE:
         case FORWARD:
         case ERROR:
            this.doGet(req, resp);
            break;
         default:
            super.doDelete(req, resp);
      }

   }

   protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      switch (req.getDispatcherType()) {
         case INCLUDE:
         case FORWARD:
         case ERROR:
            this.doGet(req, resp);
            break;
         default:
            super.doOptions(req, resp);
      }

   }

   protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      switch (req.getDispatcherType()) {
         case INCLUDE:
         case FORWARD:
         case ERROR:
            this.doGet(req, resp);
            break;
         default:
            super.doTrace(req, resp);
      }

   }

   private void serveFileBlocking(HttpServletRequest req, HttpServletResponse resp, Resource resource, HttpServerExchange exchange) throws IOException {
      ETag etag = resource.getETag();
      Date lastModified = resource.getLastModified();
      if (req.getDispatcherType() != DispatcherType.INCLUDE) {
         label101: {
            if (ETagUtils.handleIfMatch(req.getHeader("If-Match"), etag, false) && DateUtils.handleIfUnmodifiedSince(req.getHeader("If-Unmodified-Since"), lastModified)) {
               if (ETagUtils.handleIfNoneMatch(req.getHeader("If-None-Match"), etag, true) && DateUtils.handleIfModifiedSince(req.getHeader("If-Modified-Since"), lastModified)) {
                  break label101;
               }

               if (!req.getMethod().equals("GET") && !req.getMethod().equals("HEAD")) {
                  resp.setStatus(412);
               } else {
                  resp.setStatus(304);
               }

               return;
            }

            resp.setStatus(412);
            return;
         }
      }

      if (resp.getContentType() == null && !resource.isDirectory()) {
         String contentType = this.deployment.getServletContext().getMimeType(resource.getName());
         if (contentType != null) {
            resp.setContentType(contentType);
         } else {
            resp.setContentType("application/octet-stream");
         }
      }

      if (lastModified != null) {
         resp.setHeader("Last-Modified", resource.getLastModifiedString());
      }

      if (etag != null) {
         resp.setHeader("ETag", etag.toString());
      }

      ByteRange.RangeResponseResult rangeResponse = null;
      long start = -1L;
      long end = -1L;

      try {
         Long contentLength = resource.getContentLength();
         if (contentLength != null) {
            resp.getOutputStream();
            if (contentLength > 2147483647L) {
               resp.setContentLengthLong(contentLength);
            } else {
               resp.setContentLength(contentLength.intValue());
            }

            if (resource instanceof RangeAwareResource && ((RangeAwareResource)resource).isRangeSupported() && resource.getContentLength() != null) {
               resp.setHeader("Accept-Ranges", "bytes");
               ByteRange range = ByteRange.parse(req.getHeader("Range"));
               if (range != null) {
                  rangeResponse = range.getResponseResult(resource.getContentLength(), req.getHeader("If-Range"), resource.getLastModified(), resource.getETag() == null ? null : resource.getETag().getTag());
                  if (rangeResponse != null) {
                     start = rangeResponse.getStart();
                     end = rangeResponse.getEnd();
                     resp.setStatus(rangeResponse.getStatusCode());
                     resp.setHeader("Content-Range", rangeResponse.getContentRange());
                     long length = rangeResponse.getContentLength();
                     if (length > 2147483647L) {
                        resp.setContentLengthLong(length);
                     } else {
                        resp.setContentLength((int)length);
                     }

                     if (rangeResponse.getStatusCode() == 416) {
                        return;
                     }
                  }
               }
            }
         }
      } catch (IllegalStateException var16) {
      }

      boolean include = req.getDispatcherType() == DispatcherType.INCLUDE;
      if (!req.getMethod().equals("HEAD")) {
         if (rangeResponse == null) {
            resource.serve(exchange.getResponseSender(), exchange, this.completionCallback(include));
         } else {
            ((RangeAwareResource)resource).serveRange(exchange.getResponseSender(), exchange, start, end, this.completionCallback(include));
         }
      }

   }

   private IoCallback completionCallback(final boolean include) {
      return new IoCallback() {
         public void onComplete(HttpServerExchange exchange, Sender sender) {
            if (!include) {
               sender.close();
            }

         }

         public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
            sender.close();
         }
      };
   }

   private String getPath(HttpServletRequest request) {
      String servletPath;
      String pathInfo;
      if (request.getDispatcherType() == DispatcherType.INCLUDE && request.getAttribute("javax.servlet.include.request_uri") != null) {
         pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
         servletPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
      } else {
         pathInfo = request.getPathInfo();
         servletPath = request.getServletPath();
      }

      String result;
      if (pathInfo == null) {
         result = CanonicalPathUtils.canonicalize(servletPath);
      } else if (this.resolveAgainstContextRoot) {
         result = servletPath + CanonicalPathUtils.canonicalize(pathInfo);
      } else {
         result = CanonicalPathUtils.canonicalize(pathInfo);
      }

      if (result == null || result.isEmpty()) {
         result = "/";
      }

      return result;
   }

   private boolean isAllowed(String path, DispatcherType dispatcherType) {
      if (!path.isEmpty() && dispatcherType == DispatcherType.REQUEST && Paths.isForbidden(path)) {
         return false;
      } else if (this.defaultAllowed && this.disallowed.isEmpty()) {
         return true;
      } else {
         int pos = path.lastIndexOf(47);
         String lastSegment;
         if (pos == -1) {
            lastSegment = path;
         } else {
            lastSegment = path.substring(pos + 1);
         }

         if (lastSegment.isEmpty()) {
            return true;
         } else {
            int ext = lastSegment.lastIndexOf(46);
            if (ext == -1) {
               return true;
            } else {
               String extension = lastSegment.substring(ext + 1, lastSegment.length());
               if (this.defaultAllowed) {
                  return !this.disallowed.contains(extension);
               } else {
                  return this.allowed.contains(extension);
               }
            }
         }
      }
   }
}
