package javax.servlet.descriptor;

import java.util.Collection;

public interface JspConfigDescriptor {
  Collection<TaglibDescriptor> getTaglibs();
  
  Collection<JspPropertyGroupDescriptor> getJspPropertyGroups();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\descriptor\JspConfigDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */