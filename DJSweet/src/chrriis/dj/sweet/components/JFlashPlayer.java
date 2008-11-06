/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import chrriis.common.Utils;
import chrriis.common.WebServer;
import chrriis.dj.sweet.NSOption;

/**
 * A native Flash player. It is a browser-based component, which relies on the Flash plugin.<br/>
 * Methods execute when this component is initialized. If the component is not initialized, methods will be executed as soon as it gets initialized.
 * If the initialization fails, the methods will not have any effect. The results from methods have relevant values only when the component is valid. 
 * @author Christopher Deckers
 */
public class JFlashPlayer extends Composite {

  private static final String SET_CUSTOM_JAVASCRIPT_DEFINITIONS_OPTION_KEY = "Flash Player Custom Javascript definitions";
  
  /**
   * Create an option to set some custom Javascript definitions (functions) that are added to the HTML page that contains the plugin.
   * @return the option to set some custom Javascript definitions.
   */
  public static NSOption setCustomJavascriptDefinitions(final String javascript) {
    return new NSOption(SET_CUSTOM_JAVASCRIPT_DEFINITIONS_OPTION_KEY) {
      @Override
      public Object getOptionValue() {
        return javascript;
      }
    };
  }
  
  private final ResourceBundle RESOURCES = ResourceBundle.getBundle(JFlashPlayer.class.getPackage().getName().replace('.', '/') + "/resource/FlashPlayer");

  private Composite webBrowserPanel;
  private JWebBrowser webBrowser;
  
  private Composite controlBarPane;
  private Button playButton;
  private Button pauseButton;
  private Button stopButton;

  private static class NWebBrowserObject extends WebBrowserObject {
    
    private JFlashPlayer flashPlayer;
    
    NWebBrowserObject(JFlashPlayer flashPlayer) {
      super(flashPlayer.webBrowser);
      this.flashPlayer = flashPlayer;
    }
    
    protected ObjectHTMLConfiguration getObjectHtmlConfiguration() {
      ObjectHTMLConfiguration objectHTMLConfiguration = new ObjectHTMLConfiguration();
      objectHTMLConfiguration.setHTMLLoadingMessage(flashPlayer.RESOURCES.getString("LoadingMessage"));
      if(flashPlayer.options != null) {
        // Possible when debugging and calling the same URL again. No options but better than nothing.
        objectHTMLConfiguration.setHTMLParameters(flashPlayer.options.getHTMLParameters());
      }
      objectHTMLConfiguration.setWindowsClassID("D27CDB6E-AE6D-11cf-96B8-444553540000");
      objectHTMLConfiguration.setWindowsInstallationURL("http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0");
      objectHTMLConfiguration.setMimeType("application/x-shockwave-flash");
      objectHTMLConfiguration.setInstallationURL("http://www.adobe.com/go/getflashplayer");
      objectHTMLConfiguration.setWindowsParamName("movie");
      objectHTMLConfiguration.setParamName("src");
      flashPlayer.options = null;
      return objectHTMLConfiguration;
    }
    
    private final String LS = Utils.LINE_SEPARATOR;
    
    @Override
    protected String getJavascriptDefinitions() {
      String javascriptDefinitions = flashPlayer.customJavascriptDefinitions;
      return
        "function " + getEmbeddedObjectJavascriptName() + "_DoFScommand(command, args) {" + LS +
        "  sendCommand(command, args);" + LS +
        "}" + LS +
        (javascriptDefinitions == null? "": javascriptDefinitions);
    }
    
    @Override
    public String getLocalFileURL(File localFile) {
      // Local files cannot be played due to security restrictions. We need to proxy.
      return WebServer.getDefaultWebServer().getResourcePathURL(localFile.getParent(), localFile.getName());
    }

  }
  
  private WebBrowserObject webBrowserObject;

  /**
   * Construct a flash player.
   * @param options the options to configure the behavior of this component.
   */
  public JFlashPlayer(Composite parent, NSOption... options) {
    super(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    setLayout(gridLayout);
    Map<Object, Object> optionMap = NSOption.createOptionMap(options);
    customJavascriptDefinitions = (String)optionMap.get(SET_CUSTOM_JAVASCRIPT_DEFINITIONS_OPTION_KEY);
    webBrowserPanel = new Composite(this, SWT.NONE);
    BorderUtils.addLoweredBevelBorderPaintListener(webBrowserPanel);
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowser = new JWebBrowser(webBrowserPanel, options);
    webBrowserObject = new NWebBrowserObject(this);
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void commandReceived(WebBrowserEvent e, String command, String[] args) {
        for(FlashPlayerListener listener: getFlashPlayerListeners()) {
          listener.commandReceived(command, args);
        }
      }
    });
    controlBarPane = new Composite(this, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    controlBarPane.setLayout(rowLayout);
    controlBarPane.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    playButton = new Button(controlBarPane, SWT.PUSH);
    playButton.setImage(createImage("PlayIcon"));
    playButton.setEnabled(false);
    playButton.setToolTipText(RESOURCES.getString("PlayText"));
    playButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        play();
      }
    });
    pauseButton = new Button(controlBarPane, SWT.PUSH);
    pauseButton.setImage(createImage("PauseIcon"));
    pauseButton.setEnabled(false);
    pauseButton.setToolTipText(RESOURCES.getString("PauseText"));
    pauseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        pause();
      }
    });
    stopButton = new Button(controlBarPane, SWT.PUSH);
    stopButton.setImage(createImage("StopIcon"));
    stopButton.setEnabled(false);
    stopButton.setToolTipText(RESOURCES.getString("StopText"));
    stopButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        stop();
      }
    });
    adjustBorder();
    setControlBarVisible(false);
  }
  
  private void adjustBorder() {
    FillLayout layout = (FillLayout)webBrowserPanel.getLayout();
    if(isControlBarVisible()) {
      layout.marginWidth = 2;
      layout.marginHeight = 2;
    } else {
      layout.marginWidth = 0;
      layout.marginHeight = 0;
    }
    webBrowserPanel.layout();
  }
  
  private Image createImage(String resourceKey) {
    String value = RESOURCES.getString(resourceKey);
    return value.length() == 0? null: new Image(getDisplay(), JWebBrowser.class.getResourceAsStream(value));
  }
  
  private String customJavascriptDefinitions;
  
//  public String getLoadedResource() {
//    return webBrowserObject.getLoadedResource();
//  }
  
  /**
   * Load a file from the classpath.
   * @param clazz the reference clazz of the file to load.
   * @param resourcePath the path to the file.
   */
  public void load(Class<?> clazz, String resourcePath) {
    load(clazz, resourcePath, null);
  }
  
  /**
   * Load a file from the classpath.
   * @param clazz the reference clazz of the file to load.
   * @param resourcePath the path to the file.
   * @param options the options to better configure the initialization of the flash plugin.
   */
  public void load(Class<?> clazz, String resourcePath, FlashPluginOptions options) {
    addReferenceClassLoader(clazz.getClassLoader());
    load(WebServer.getDefaultWebServer().getClassPathResourceURL(clazz.getName(), resourcePath), options);
  }
  
  /**
   * Load a file.
   * @param resourceLocation the path or URL to the file.
   */
  public void load(String resourceLocation) {
    load(resourceLocation, null);
  }
  
  private FlashPluginOptions options;
  
  /**
   * Load a file.
   * @param resourceLocation the path or URL to the file.
   * @param options the options to better configure the initialization of the flash plugin.
   */
  public void load(String resourceLocation, FlashPluginOptions options) {
    if("".equals(resourceLocation)) {
      resourceLocation = null;
    }
    if(options == null) {
      options = new FlashPluginOptions();
    }
    this.options = options;
    webBrowserObject.load(resourceLocation);
    boolean isEnabled = resourceLocation != null;
    playButton.setEnabled(isEnabled);
    pauseButton.setEnabled(isEnabled);
    stopButton.setEnabled(isEnabled);
  }

  /**
   * Play a timeline-based flash applications. 
   */
  public void play() {
    if(!webBrowserObject.hasContent()) {
      return;
    }
    webBrowserObject.invokeObjectFunction("Play");
  }
  
  /**
   * Pause the execution of timeline-based flash applications. 
   */
  public void pause() {
    if(!webBrowserObject.hasContent()) {
      return;
    }
    webBrowserObject.invokeObjectFunction("StopPlay");
  }
  
  /**
   * Stop the execution of timeline-based flash applications. 
   */
  public void stop() {
    if(!webBrowserObject.hasContent()) {
      return;
    }
    webBrowserObject.invokeObjectFunction("Rewind");
  }
  
  /**
   * Set the value of a variable. It is also possible to set object properties with that method, though it is recommended to create special accessor methods.
   * @param name the name of the variable.
   * @param value the new value of the variable.
   */
  public void setVariable(String name, String value) {
    if(!webBrowserObject.hasContent()) {
      return;
    }
    webBrowserObject.invokeObjectFunction("SetVariable", name, value);
  }
  
  /**
   * Get the value of a variable, or an object property if the web browser used is Internet Explorer. On Mozilla, it is not possible to access object properties with that method, an accessor method or a global variable in the Flash application should be used instead.
   * @return the value, potentially a String, Number, Boolean.
   */
  public Object getVariable(String name) {
    if(!webBrowserObject.hasContent()) {
      return null;
    }
    return webBrowserObject.invokeObjectFunctionWithResult("GetVariable", name);
  }
  
  /**
   * Invoke a function on the Flash object, with optional arguments (Strings, numbers, booleans).
   * @param functionName the name of the function to invoke.
   * @param args optional arguments.
   */
  public void invokeFlashFunction(String functionName, Object... args) {
    webBrowserObject.invokeObjectFunction(functionName, args);
  }
  
  /**
   * Invoke a function on the Flash object and waits for a result, with optional arguments (Strings, numbers, booleans).
   * @param functionName the name of the function to invoke.
   * @param args optional arguments.
   * @return The value, potentially a String, Number, Boolean.
   */
  public Object invokeFlashFunctionWithResult(String functionName, Object... args) {
    return webBrowserObject.invokeObjectFunctionWithResult(functionName, args);
  }
  
  /**
   * Get the web browser that contains this component. The web browser should only be used to add listeners, for example to listen to window creation events.
   * @return the web browser.
   */
  public JWebBrowser getWebBrowser() {
    return webBrowser;
  }
  
  /**
   * Indicate whether the control bar is visible.
   * @return true if the control bar is visible.
   */
  public boolean isControlBarVisible() {
    return controlBarPane.getVisible();
  }
  
  /**
   * Set whether the control bar is visible.
   * @param isControlBarVisible true if the control bar should be visible, false otherwise.
   */
  public void setControlBarVisible(boolean isControlBarVisible) {
    controlBarPane.setVisible(isControlBarVisible);
    ((GridData)controlBarPane.getLayoutData()).exclude = !isControlBarVisible;
    adjustBorder();
    layout();
  }
  
  protected List<FlashPlayerListener> listenerList = new ArrayList<FlashPlayerListener>();
  
  /**
   * Add a flash player listener.
   * @param listener The flash player listener to add.
   */
  public void addFlashPlayerListener(FlashPlayerListener listener) {
    listenerList.add(listener);
  }
  
  /**
   * Remove a flash player listener.
   * @param listener the flash player listener to remove.
   */
  public void removeFlashPlayerListener(FlashPlayerListener listener) {
    listenerList.remove(listener);
  }

  /**
   * Get the flash player listeners.
   * @return the flash player listeners.
   */
  public FlashPlayerListener[] getFlashPlayerListeners() {
    return listenerList.toArray(new FlashPlayerListener[0]);
  }
  
  private List<ClassLoader> referenceClassLoaderList = new ArrayList<ClassLoader>(1);
  
  private void addReferenceClassLoader(ClassLoader referenceClassLoader) {
    if(referenceClassLoader == null || referenceClassLoader == getClass().getClassLoader() || referenceClassLoaderList.contains(referenceClassLoader)) {
      return;
    }
    // If a different class loader is used to locate a resource, we need to allow th web server to find that resource
    referenceClassLoaderList.add(referenceClassLoader);
    WebServer.getDefaultWebServer().addReferenceClassLoader(referenceClassLoader);
  }
  
  @Override
  protected void finalize() throws Throwable {
    for(ClassLoader referenceClassLoader: referenceClassLoaderList) {
      WebServer.getDefaultWebServer().removeReferenceClassLoader(referenceClassLoader);
    }
    referenceClassLoaderList.clear();
    super.finalize();
  }
  
}
