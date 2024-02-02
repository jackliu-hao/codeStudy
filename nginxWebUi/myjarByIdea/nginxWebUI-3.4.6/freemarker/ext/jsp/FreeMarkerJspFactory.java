package freemarker.ext.jsp;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

abstract class FreeMarkerJspFactory extends JspFactory {
   protected abstract String getSpecificationVersion();

   public JspEngineInfo getEngineInfo() {
      return new JspEngineInfo() {
         public String getSpecificationVersion() {
            return FreeMarkerJspFactory.this.getSpecificationVersion();
         }
      };
   }

   public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
      throw new UnsupportedOperationException();
   }

   public void releasePageContext(PageContext ctx) {
      throw new UnsupportedOperationException();
   }
}
