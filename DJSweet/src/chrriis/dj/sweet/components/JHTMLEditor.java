/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import chrriis.common.ObjectRegistry;
import chrriis.common.Utils;
import chrriis.common.WebServer;
import chrriis.common.WebServer.HTTPRequest;
import chrriis.common.WebServer.WebServerContent;
import chrriis.dj.sweet.EventDispatchUtils;
import chrriis.dj.sweet.NSOption;

/**
 * An HTML editor. It is a browser-based component, which relies on the FCKeditor (the default) or the TinyMCE editor.<br/>
 * Methods execute when this component is initialized. If the component is not initialized, methods will be executed as soon as it gets initialized.
 * If the initialization fails, the methods will not have any effect. The results from methods have relevant values only when the component is valid.
 * @author Christopher Deckers
 * @author Jörn Heid (TinyMCE implementation)
 */
public class JHTMLEditor extends Composite {

  static interface JHTMLEditorImplementation {

    public WebServerContent getWebServerContent(HTTPRequest httpRequest, String resourcePath, final int instanceID);

    public String getHTMLContent();

    public void setHTMLContent(String html);

    public void setDirtyTrackingActive(boolean isDirtyTrackingActive);

    public void clearDirtyIndicator();

  }

  public static enum HTMLEditorImplementation { FCKEditor, CKEditor, TinyMCE };

  public static class TinyMCEOptions {

    private TinyMCEOptions() {}

    static final String SET_CUSTOM_HTML_HEADERS_OPTION_KEY = "TinyMCE Custom HTML Headers";

    /**
     * Set custom HTML headers, which is mostly useful when integrating certain TinyMCE plugins.
     */
    public static NSOption setCustomHTMLHeaders(final String customHTMLHeaders) {
      return new NSOption(SET_CUSTOM_HTML_HEADERS_OPTION_KEY) {
        @Override
        public Object getOptionValue() {
          return customHTMLHeaders;
        }
      };
    }

    static final String SET_OPTIONS_OPTION_KEY = "TinyMCE Options";

    /**
     * Create an option to set TinyMCE editor options.<br/>
     * The list of possible options to set for TinyMCE can be found here: <a href="http://wiki.moxiecode.com/index.php/TinyMCE:Configuration">http://wiki.moxiecode.com/index.php/TinyMCE:Configuration</a>.
     * @param optionMap a map containing the key/value pairs accepted by TinyMCE.
     * @return the option to set the options.
     */
    public static NSOption setOptions(Map<String, String> optionMap) {
      final Map<String, String> optionMap_ = new HashMap<String, String>(optionMap);
      return new NSOption(SET_OPTIONS_OPTION_KEY) {
        @Override
        public Object getOptionValue() {
          return optionMap_;
        }
      };
    }

  }

  public static class FCKEditorOptions {

    private FCKEditorOptions() {}

    static final String SET_CUSTOM_JAVASCRIPT_CONFIGURATION_OPTION_KEY = "FCKEditor Custom Configuration Script";

    /**
     * Create an option to set custom Javascript configuration for the FCKeditor editor.<br/>
     * The list of possible options to set for FCKeditor can be found here: <a href="http://docs.fckeditor.net/FCKeditor_2.x/Developers_Guide/Configuration/Configuration_Options">http://docs.fckeditor.net/FCKeditor_2.x/Developers_Guide/Configuration/Configuration_Options</a>.<br/>
     * @param javascriptConfiguration the javascript configuration.
     * @return the option to set a custom configuration.
     */
    public static NSOption setCustomJavascriptConfiguration(final String javascriptConfiguration) {
      return new NSOption(SET_CUSTOM_JAVASCRIPT_CONFIGURATION_OPTION_KEY) {
        @Override
        public Object getOptionValue() {
          return javascriptConfiguration;
        }
      };
    }

  }

  public static class CKEditorOptions {

    private CKEditorOptions() {}

    static final String SET_OPTIONS_OPTION_KEY = "CKEditor Options";

    /**
     * Create an option to set CKEditor editor options.<br/>
     * The list of possible options to set for CKEditor can be found here: <a href="http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html">http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html</a>.
     * @param optionMap a map containing the key/value pairs accepted by CKEditor.
     * @return the option to set the options.
     */
    public static NSOption setOptions(Map<String, String> optionMap) {
      final Map<String, String> optionMap_ = new HashMap<String, String>(optionMap);
      return new NSOption(SET_OPTIONS_OPTION_KEY) {
        @Override
        public Object getOptionValue() {
          return optionMap_;
        }
      };
    }

  }

  private JWebBrowser webBrowser;
  private int instanceID;

  private JHTMLEditorImplementation implementation;

  JHTMLEditorImplementation getImplementation() {
    return implementation;
  }

  /**
   * Construct an HTML editor.
   * @param options the options to configure the behavior of this component.
   */
  public JHTMLEditor(Composite parent, HTMLEditorImplementation editorImplementation, NSOption... options) {
    super(parent, SWT.NONE);
    if(editorImplementation == null) {
      throw new NullPointerException("The editor implementation cannot be null!");
    }
    setLayout(new FillLayout());
    Map<Object, Object> optionMap = NSOption.createOptionMap(options);
    webBrowser = new JWebBrowser(this, options);
    switch(editorImplementation) {
      case FCKEditor:
        try {
          implementation = new JHTMLEditorFCKeditor(this, optionMap);
          break;
        } catch(RuntimeException e) {
          if(editorImplementation != null) {
            throw e;
          }
        }
      case CKEditor:
        try {
          implementation = new JHTMLEditorCKeditor(this, optionMap);
          break;
        } catch(RuntimeException e) {
          if(editorImplementation != null) {
            throw e;
          }
        }
      case TinyMCE:
        try {
          implementation = new JHTMLEditorTinyMCE(this, optionMap);
          break;
        } catch(RuntimeException e) {
          if(editorImplementation != null) {
            throw e;
          }
        }
      default:
        throw new IllegalStateException("A suitable HTML editor (FCKeditor, TinyMCE) distribution could not be found on the classpath!");
    }
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void commandReceived(WebBrowserCommandEvent e) {
        String command = e.getCommand();
        if("[Chrriis]JH_setLoaded".equals(command)) {
          for(int i=initializationListenerList.size()-1; i>=0; i--) {
            initializationListenerList.get(i).objectInitialized();
          }
        } else if("[Chrriis]JH_setDirty".equals(command)) {
          setDirty(true);
        }
      }
    });
    webBrowser.setBarsVisible(false);
    instanceID = ObjectRegistry.getInstance().add(this);
    final AtomicBoolean result = new AtomicBoolean();
    InitializationListener initializationListener = new InitializationListener() {
      public void objectInitialized() {
        removeInitializationListener(this);
        result.set(true);
      }
    };
    addInitializationListener(initializationListener);
    webBrowser.navigate(WebServer.getDefaultWebServer().getDynamicContentURL(JHTMLEditor.class.getName(), String.valueOf(instanceID),  "index.html"));
    EventDispatchUtils.sleepWithEventDispatch(new EventDispatchUtils.Condition() {
      public boolean getValue() {
        return result.get();
      }
    }, 4000);
    removeInitializationListener(initializationListener);
  }

  /**
   * Get the web browser that contains this component. The web browser should only be used to add listeners, for example to listen to window creation events.
   * @return the web browser.
   */
  public JWebBrowser getWebBrowser() {
    return webBrowser;
  }

  protected static WebServerContent getWebServerContent(final HTTPRequest httpRequest) {
    String resourcePath = httpRequest.getResourcePath();
    int index = resourcePath.indexOf('/');
    int instanceID = Integer.parseInt(resourcePath.substring(0, index));
    JHTMLEditor htmlEditor = (JHTMLEditor)ObjectRegistry.getInstance().get(instanceID);
    if(htmlEditor == null) {
      return null;
    }
    String resourcePath_ = resourcePath.substring(index + 1);
    if(resourcePath_.startsWith("/")) {
      resourcePath_ = resourcePath_.substring(1);
    }
    return htmlEditor.getWebServerContent(httpRequest, resourcePath_, instanceID);
  }

  /**
   * Serve the HTTP content requested by the editor web page, which can be altered by subclasses.
   * Note that altering the default content is generally not needed and is not recommended.
   * @return the content.
   */
  protected WebServerContent getWebServerContent(HTTPRequest httpRequest, String resourcePath, final int instanceID) {
    return implementation.getWebServerContent(httpRequest, resourcePath, instanceID);
  }

  /**
   * Get the HTML content.
   * @return the HTML content.
   */
  public String getHTMLContent() {
    return convertLinksToLocal(implementation.getHTMLContent());
  }

  /**
   * Set the HTML content.
   * @param html the HTML content.
   */
  public void setHTMLContent(String html) {
    html = JHTMLEditor.convertLinksFromLocal(html.replaceAll("[\r\n]", " "));
    implementation.setHTMLContent(html);
    setDirty(false);
  }

  private boolean isDirty;

  /**
   * Indicate whether the editor is dirty, which means its content has changed since it was last set or the dirty state was cleared.
   * @return true if the editor is dirty, false otherwise.
   */
  public boolean isDirty() {
    return isDirty;
  }

  private void setDirty(boolean isDirty) {
    if(this.isDirty == isDirty) {
      return;
    }
    this.isDirty = isDirty;
    HTMLEditorDirtyStateEvent e = null;
    for(HTMLEditorListener listener: getHTMLEditorListeners()) {
      if(e == null) {
        e = new HTMLEditorDirtyStateEvent(this, isDirty);
      }
      listener.notifyDirtyStateChanged(e);
    }
  }

  /**
   * Clear the dirty state.
   */
  public void clearDirtyState() {
    implementation.clearDirtyIndicator();
    setDirty(false);
  }

  static String convertLinksToLocal(String html) {
    if(html == null) {
      return html;
    }
    // Transform proxied URLs to "file:///".
    Pattern p = Pattern.compile("=\\s*\"(" + WebServer.getDefaultWebServer().getURLPrefix() + "/resource/)([^/]+)/([^\"]+)\"\\s");
    for(Matcher m; (m = p.matcher(html)).find(); ) {
      String codeBase = html.substring(m.start(2), m.end(2));
      String resource = html.substring(m.start(3), m.end(3));
      try {
        resource = new File(Utils.decodeURL(Utils.decodeURL(codeBase)), resource).toURI().toURL().toExternalForm();
      } catch (MalformedURLException e) {
      }
      html = html.substring(0, m.start(1)) + resource + html.substring(m.end(3));
    }
    return html;
  }

  static String convertLinksFromLocal(String html) {
    if(html == null) {
      return html;
    }
    // Transform "file:///" to proxied URLs.
    Pattern p = Pattern.compile("=\\s*\"(file:/{1,3})([^\"]+)\"\\s");
    for(Matcher m; (m = p.matcher(html)).find(); ) {
      String resource = html.substring(m.start(2), m.end(2));
      File resourceFile = new File(resource);
      resource = WebServer.getDefaultWebServer().getResourcePathURL(Utils.encodeURL(resourceFile.getParent()), resourceFile.getName());
      html = html.substring(0, m.start(1)) + resource + html.substring(m.end(2));
    }
    return html;
  }

  protected List<HTMLEditorListener> htmlEditorListenerList = new ArrayList<HTMLEditorListener>();

  /**
   * Add an HTML editor listener.
   * @param listener The HTML editor listener to add.
   */
  public void addHTMLEditorListener(HTMLEditorListener listener) {
    htmlEditorListenerList.add(listener);
  }

  /**
   * Remove an HTML editor listener.
   * @param listener the HTML editor listener to remove.
   */
  public void removeHTMLEditorListener(HTMLEditorListener listener) {
    htmlEditorListenerList.remove(listener);
  }

  /**
   * Get the HTML editor listeners.
   * @return the HTML editor listeners.
   */
  public HTMLEditorListener[] getHTMLEditorListeners() {
    return htmlEditorListenerList.toArray(new HTMLEditorListener[0]);
  }

  private static interface InitializationListener extends EventListener {
    public void objectInitialized();
  }

  protected List<InitializationListener> initializationListenerList = new ArrayList<InitializationListener>();

  private void addInitializationListener(InitializationListener listener) {
    initializationListenerList.add(listener);
  }

  private void removeInitializationListener(InitializationListener listener) {
    initializationListenerList.remove(listener);
  }

//  private InitializationListener[] getInitializationListeners() {
//    return initializationListenerList.toArray(new InitializationListener[0]);
//  }

}
