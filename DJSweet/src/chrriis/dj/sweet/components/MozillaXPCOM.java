/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import org.mozilla.interfaces.nsIWebBrowser;

/**
 * This class is meant to allow accessing the JavaXPCOM nsIWebBrowser interface, and other Mozilla XPCOM interfaces.
 * @author Christopher Deckers
 */
public class MozillaXPCOM {

  private MozillaXPCOM() {}

  /**
   * Get the Mozilla JavaXPCOM nsIWebBrowser if it is available.<br/>
   * Availability requires the web browser to be using the XULRunner runtime (version 1.8.1.2 or greater) and the JavaXPCOM classes (version 1.8.1.2 or greater) to be in the classpath.
   * @return the Mozilla JavaXPCOM nsIWebBrowser, or null if it is not available.
   */
  public static nsIWebBrowser getWebBrowser(JWebBrowser webBrowser) {
    return (nsIWebBrowser)webBrowser.getNativeComponent().getWebBrowser();
  }

}
