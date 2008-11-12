/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowAdapter;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import chrriis.common.Utils;
import chrriis.dj.sweet.EventDispatchUtils;
import chrriis.dj.sweet.NSOption;

/**
 * A native web browser, using Internet Explorer or Mozilla on Windows, and Mozilla on other platforms.<br/>
 * @author Christopher Deckers
 */
public class JWebBrowser extends Composite {

  private static final boolean IS_DEBUGGING_OPTIONS = Boolean.parseBoolean(System.getProperty("sweet.components.debug.printoptions"));

  /** The prefix to use when sending a command from some web content, using a static link or by setting window.location from Javascript. */
  public static final String COMMAND_LOCATION_PREFIX = "command://";
  
  /** The prefix to use when sending a command from some web content, by setting window.status from Javascript. */
  public static final String COMMAND_STATUS_PREFIX = "scommand://";
  
  private static final String USE_XULRUNNER_RUNTIME_OPTION_KEY = "XULRunner Runtime";
  private static final NSOption XUL_RUNNER_RUNTIME_OPTION = new NSOption(USE_XULRUNNER_RUNTIME_OPTION_KEY);
  
  /**
   * Create an option to make the web browser use the Mozilla XULRunner runtime.
   * @return the option to use the XULRunner runtime.
   */
  public static NSOption useXULRunnerRuntime() {
    return XUL_RUNNER_RUNTIME_OPTION;
  }
  
  /**
   * Clear all session cookies from all current web browser instances.
   */
  public static void clearSessionCookies() {
    Browser.clearSessions();
  }
  
  private final ResourceBundle RESOURCES = ResourceBundle.getBundle(JWebBrowser.class.getPackage().getName().replace('.', '/') + "/resource/WebBrowser");

  private Composite browserContainer;
  private Browser browser;
  private Composite statusBarPane;
  private Menu menuBar;
  private Menu fileMenu;
  private MenuItem buttonBarCheckBoxMenuItem;
  private MenuItem locationBarCheckBoxMenuItem;
  private MenuItem statusBarCheckBoxMenuItem;
  private Composite buttonBarPane;
  private Composite locationBarPane;
  
  private Label statusLabel;
  private ProgressBar progressBar;

  private Text locationField;
  
  private ToolItem backButton;
  private ToolItem forwardButton;
  private ToolItem reloadButton;
  private ToolItem stopButton;
  
  /**
   * Copy the appearance, the visibility of the various bars, from one web browser to another.
   * @param fromWebBrowser the web browser to copy the appearance from.
   * @param toWebBrowser the web browser to copy the appearance to.
   */
  public static void copyAppearance(JWebBrowser fromWebBrowser, JWebBrowser toWebBrowser) {
    toWebBrowser.setLocationBarVisible(fromWebBrowser.isLocationBarVisible());
    toWebBrowser.setButtonBarVisible(fromWebBrowser.isButtonBarVisible());
    toWebBrowser.setMenuBarVisible(fromWebBrowser.isMenuBarVisible());
    toWebBrowser.setStatusBarVisible(fromWebBrowser.isStatusBarVisible());
  }
  
  /**
   * Copy the content, whether a URL or its HTML content, from one web browser to another.
   * @param fromWebBrowser the web browser to copy the content from.
   * @param toWebBrowser the web browser to copy the content to.
   */
  public static void copyContent(JWebBrowser fromWebBrowser, JWebBrowser toWebBrowser) {
    String location = fromWebBrowser.getResourceLocation();
    if("about:blank".equals(location)) {
      toWebBrowser.setHTMLContent(fromWebBrowser.getHTMLContent());
    } else {
      toWebBrowser.navigate(location);
    }
  }
  
  private boolean isXULRunnerRuntime;
  
  private boolean isXULRunnerRuntime() {
    return isXULRunnerRuntime;
  }
  
  /**
   * Construct a new web browser.
   * @param options the options to configure the behavior of this component.
   */
  public JWebBrowser(Composite parent, NSOption... options) {
    super(parent, SWT.NONE);
    String xulRunnerPath = System.getProperty("sweet.webbrowser.xulrunner.home");
    if(xulRunnerPath != null) {
      System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulRunnerPath);
    } else {
      xulRunnerPath = System.getProperty("org.eclipse.swt.browser.XULRunnerPath");
      if(xulRunnerPath == null) {
        xulRunnerPath = System.getenv("XULRUNNER_HOME");
        if(xulRunnerPath != null) {
          System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulRunnerPath);
        }
      }
    }
    int style = SWT.NONE;
    Map<Object, Object> optionMap = NSOption.createOptionMap(options);
    if(IS_DEBUGGING_OPTIONS) {
      StringBuilder sb = new StringBuilder();
      sb.append("Component ").append(getClass().getName()).append("[").append(hashCode()).append("] options: ");
      boolean isFirst = true;
      for(Object key: optionMap.keySet()) {
        if(isFirst) {
          isFirst = false;
        } else {
          sb.append(", ");
        }
        Object value = optionMap.get(key);
        if(value instanceof NSOption) {
          sb.append(value);
        } else {
          sb.append(key).append('=').append(value);
        }
      }
      if(isFirst) {
        sb.append("<none>");
      }
      System.err.println(sb);
    }
    if(optionMap.get(USE_XULRUNNER_RUNTIME_OPTION_KEY) != null || "xulrunner".equals(System.getProperty("sweet.webbrowser.runtime"))) {
      this.isXULRunnerRuntime = true;
      style |= SWT.MOZILLA;
    }
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    setLayout(gridLayout);
    GridData gridData = new GridData();
    buttonBarPane = new Composite(this, SWT.NONE);
    createButtonBarContent();
    buttonBarPane.setLayoutData(gridData);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    locationBarPane = new Composite(this, SWT.NONE);
    createLocationBarContent();
    locationBarPane.setLayoutData(gridData);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalSpan = 2;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    browserContainer = new Composite(this, SWT.NONE);
    BorderUtils.addLoweredBevelBorderPaintListener(browserContainer);
    browserContainer.setLayout(new FillLayout());
    browser = new Browser(browserContainer, style);
    browserContainer.setLayoutData(gridData);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = 2;
    gridData.horizontalAlignment = GridData.FILL;
    statusBarPane = new Composite(this, SWT.BORDER);
    createStatusBarContent();
    statusBarPane.setLayoutData(gridData);
    JWebBrowserWindow webBrowserWindow = getWebBrowserWindow();
    if(webBrowserWindow != null) {
      // In my original Swing implementation, the menu bar is actually added to the browser component.
      // It seems that in SWT, a menu bar can only be set on a shell, so that is what I do here if it is the web browser shell.
      // I still place the code here in case there is actually a way to get it for the browser itself and easily change the code.
      menuBar = new Menu(webBrowserWindow, SWT.BAR);
      createMenuBarContent();
      webBrowserWindow.setMenuBar(menuBar);
    }
    adjustBorder();
    // Listeners
    browser.addTitleListener(new TitleListener() {
      public void changed(TitleEvent e) {
        title = e.title;
        WebBrowserEvent ev = null;
        for(int i=listenerList.size()-1; i>=0; i--) {
          if(ev == null) {
            ev = new WebBrowserEvent(JWebBrowser.this);
          }
          listenerList.get(i).titleChanged(ev);
        }
      }
    });
    browser.addStatusTextListener(new StatusTextListener() {
      public void changed(StatusTextEvent e) {
        String newStatus = e.text;
        if(newStatus.startsWith(COMMAND_STATUS_PREFIX)) {
          browser.execute("window.status = decodeURIComponent('" + Utils.encodeURL(status == null? "": status) + "');");
          String query = newStatus.substring(COMMAND_STATUS_PREFIX.length());
          if(query.endsWith("/")) {
            query = query.substring(0, query.length() - 1);
          }
          List<String> queryElementList = new ArrayList<String>();
          StringTokenizer st = new StringTokenizer(query, "&", true);
          String lastToken = null;
          while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if("&".equals(token)) {
              if(lastToken == null) {
                queryElementList.add("");
              }
              lastToken = null;
            } else {
              lastToken = token;
              queryElementList.add(Utils.decodeURL(token));
            }
          }
          if(lastToken == null) {
            queryElementList.add("");
          }
          String command = queryElementList.isEmpty()? "": queryElementList.remove(0);
          String[] args = queryElementList.toArray(new String[0]);
          WebBrowserEvent ev = null;
          for(int i=listenerList.size()-1; i>=0; i--) {
            if(ev == null) {
              ev = new WebBrowserEvent(JWebBrowser.this);
            }
            listenerList.get(i).commandReceived(ev, command, args);
          }
          return;
        }
        if(newStatus.equals(status)) {
          return;
        }
        status = e.text;
        WebBrowserEvent ev = null;
        for(int i=listenerList.size()-1; i>=0; i--) {
            if(ev == null) {
              ev = new WebBrowserEvent(JWebBrowser.this);
            }
            listenerList.get(i).statusChanged(ev);
        }
      }
    });
    browser.addProgressListener(new ProgressListener() {
      private void updateProgress(int loadingProgress) {
        if(JWebBrowser.this.loadingProgress != loadingProgress) {
          JWebBrowser.this.loadingProgress = loadingProgress;
          WebBrowserEvent e = null;
          for(int i=listenerList.size()-1; i>=0; i--) {
            if(e == null) {
              e = new WebBrowserEvent(JWebBrowser.this);
            }
            listenerList.get(i).loadingProgressChanged(e);
          }
        }
      }
      public void changed(ProgressEvent e) {
        if(e.total <= 0 || e.total < e.current) {
          return;
        }
        isLoading = true;
        updateProgress(e.current == e.total? 100: Math.min(e.current * 100 / e.total, 99));
      }
      public void completed(ProgressEvent progressevent) {
        isLoading = false;
        updateProgress(100);
      }
    });
    browser.addLocationListener(new LocationListener() {
      public void changed(LocationEvent e) {
        isLoading = false;
        WebBrowserNavigationEvent ev = null;
        for(int i=listenerList.size()-1; i>=0; i--) {
          if(ev == null) {
            ev = new WebBrowserNavigationEvent(JWebBrowser.this, e.location, e.top);
          }
          listenerList.get(i).locationChanged(ev);
        }
      }
      public void changing(LocationEvent e) {
        final String location = e.location;
        if(location.startsWith(COMMAND_LOCATION_PREFIX)) {
          e.doit = false;
          String query = location.substring(COMMAND_LOCATION_PREFIX.length());
          if(query.endsWith("/")) {
            query = query.substring(0, query.length() - 1);
          }
          List<String> queryElementList = new ArrayList<String>();
          StringTokenizer st = new StringTokenizer(query, "&", true);
          String lastToken = null;
          while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if("&".equals(token)) {
              if(lastToken == null) {
                queryElementList.add("");
              }
              lastToken = null;
            } else {
              lastToken = token;
              queryElementList.add(Utils.decodeURL(token));
            }
          }
          if(lastToken == null) {
            queryElementList.add("");
          }
          String command = queryElementList.isEmpty()? "": queryElementList.remove(0);
          String[] args = queryElementList.toArray(new String[0]);
          WebBrowserEvent ev = null;
          for(int i=listenerList.size()-1; i>=0; i--) {
            if(ev == null) {
              ev = new WebBrowserEvent(JWebBrowser.this);
            }
            listenerList.get(i).commandReceived(ev, command, args);
          }
          return;
        }
        if(location.startsWith("javascript:")) {
          return;
        }
        isLoading = true;
        {
          boolean isNavigating = true;
          WebBrowserNavigationEvent ev = null;
          for(int i=listenerList.size()-1; i>=0; i--) {
            if(ev == null) {
              ev = new WebBrowserNavigationEvent(JWebBrowser.this, location, e.top);
            }
            listenerList.get(i).locationChanging(ev);
            isNavigating &= !ev.isConsumed();
          }
          e.doit = isNavigating;
        }
        if(!e.doit) {
          isLoading = false;
          WebBrowserNavigationEvent ev = null;
          for(int i=listenerList.size()-1; i>=0; i--) {
            if(ev == null) {
              ev = new WebBrowserNavigationEvent(JWebBrowser.this, e.location, e.top);
            }
            listenerList.get(i).locationChangeCanceled(ev);
          }
        }
      }
    });
    browser.addOpenWindowListener(new OpenWindowListener() {
      public void open(WindowEvent e) {
        JWebBrowser jWebBrowser;
        JWebBrowserWindow webBrowserWindow;
        if(isXULRunnerRuntime()) {
          webBrowserWindow = new JWebBrowserWindow(JWebBrowser.useXULRunnerRuntime());
          jWebBrowser = webBrowserWindow.getWebBrowser();
        } else {
          webBrowserWindow = new JWebBrowserWindow();
          jWebBrowser = webBrowserWindow.getWebBrowser();
        }
        WebBrowserWindowWillOpenEvent ev = null;
        for(int i=listenerList.size()-1; i>=0; i--) {
          if(ev == null) {
            ev = new WebBrowserWindowWillOpenEvent(JWebBrowser.this, jWebBrowser);
          }
          listenerList.get(i).windowWillOpen(ev);
          jWebBrowser = ev.isConsumed()? null: ev.getNewWebBrowser();
        }
        final JWebBrowser newWebBrowser;
        final boolean isDisposed;
        if(jWebBrowser == null) {
          isDisposed = true;
          newWebBrowser = webBrowserWindow.getWebBrowser();
        } else {
          isDisposed = false;
          newWebBrowser = jWebBrowser;
        }
        e.browser = newWebBrowser.getNativeComponent();
        e.browser.addVisibilityWindowListener(new VisibilityWindowAdapter() {
          @Override
          public void show(final WindowEvent e) {
            Browser browser = (Browser)e.widget;
            if(isDisposed) {
              final Shell shell = browser.getShell();
              e.display.asyncExec(new Runnable() {
                public void run() {
                  shell.close();
                }
              });
            } else {
              browser.removeVisibilityWindowListener(this);
              newWebBrowser.setMenuBarVisible(e.menuBar);
              newWebBrowser.setButtonBarVisible(e.toolBar);
              newWebBrowser.setLocationBarVisible(e.addressBar);
              newWebBrowser.setStatusBarVisible(e.statusBar);
              JWebBrowserWindow browserWindow = newWebBrowser.getWebBrowserWindow();;
              if(browserWindow != null) {
                if(e.size != null) {
                  Point windowSize = browserWindow.getSize();
                  Point webBrowserSize = browserWindow.getWebBrowser().getNativeComponent().getSize();
                  windowSize.x -= webBrowserSize.x;
                  windowSize.y -= webBrowserSize.y;
                  windowSize.x += e.size.x;
                  windowSize.y += e.size.y;
                  browserWindow.setSize(windowSize);
                }
                if(e.location != null) {
                  browserWindow.setLocation(e.location);
                }
              }
              WebBrowserWindowOpeningEvent ev = null;
              for(int i=listenerList.size()-1; i>=0; i--) {
                if(ev == null) {
                  ev = new WebBrowserWindowOpeningEvent(JWebBrowser.this, newWebBrowser, e.location, e.size);
                }
                listenerList.get(i).windowOpening(ev);
              }
              new Thread() {
                @Override
                public void run() {
                  try {
                    sleep(600);
                  } catch(Exception e) {
                  }
                  e.display.asyncExec(new Runnable() {
                    public void run() {
                      if(!newWebBrowser.isDisposed()) {
                        JWebBrowserWindow webBrowserWindow = newWebBrowser.getWebBrowserWindow();
                        if(webBrowserWindow != null && !webBrowserWindow.isDisposed()) {
                          webBrowserWindow.setVisible(true);
                        }
                      }
                    }
                  });
                }
              }.start();
            }
          }
        });
      }
    });
    addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void statusChanged(WebBrowserEvent e) {
        updateStatus();
      }
      @Override
      public void loadingProgressChanged(WebBrowserEvent e) {
        updateProgressValue();
        updateStopButton(false);
      }
      @Override
      public void locationChanged(WebBrowserNavigationEvent e) {
        updateStopButton(false);
        if(e.isTopFrame()) {
          updateLocationField(null);
        }
        updateNavigationButtons();
      }
      @Override
      public void locationChanging(WebBrowserNavigationEvent e) {
        if(e.isTopFrame()) {
          updateLocationField(e.getNewResourceLocation());
        }
        updateStopButton(true);
      }
      @Override
      public void locationChangeCanceled(WebBrowserNavigationEvent e) {
        updateStopButton(false);
        if(e.isTopFrame()) {
          updateLocationField(null);
        }
        updateNavigationButtons();
      }
    });
  }
  
  private void createMenuBarContent() {
    JWebBrowserWindow webBrowserWindow = getWebBrowserWindow();
    MenuItem fileMenuItem = new MenuItem(menuBar, SWT.CASCADE);
    fileMenuItem.setText(RESOURCES.getString("FileMenu"));
    fileMenu = new Menu(webBrowserWindow, SWT.DROP_DOWN);
    fileMenuItem.setMenu(fileMenu);
    MenuItem fileNewWindowMenuItem = new MenuItem(fileMenu, SWT.PUSH);
    fileNewWindowMenuItem.setText(RESOURCES.getString("FileNewWindowMenu"));
    fileNewWindowMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        JWebBrowserWindow webBrowserWindow;
        if(isXULRunnerRuntime()) {
          webBrowserWindow = new JWebBrowserWindow(useXULRunnerRuntime());
        } else {
          webBrowserWindow = new JWebBrowserWindow();
        }
        JWebBrowser webBrowser = webBrowserWindow.getWebBrowser();
        JWebBrowser.copyAppearance(JWebBrowser.this, webBrowser);
        JWebBrowser.copyContent(JWebBrowser.this, webBrowser);
        webBrowserWindow.setVisible(true);
      }
    });
    final MenuItem fileOpenLocationMenuItem = new MenuItem(fileMenu, SWT.PUSH);
    fileOpenLocationMenuItem.setText(RESOURCES.getString("FileOpenLocationMenu"));
    fileOpenLocationMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
//        String path = JOptionPane.showInputDialog(JWebBrowser.this, RESOURCES.getString("FileOpenLocationDialogMessage"), RESOURCES.getString("FileOpenLocationDialogTitle"), JOptionPane.QUESTION_MESSAGE);
//        if(path != null) {
//          navigate(path);
//        }
      }
    });
    final MenuItem fileOpenFileMenuItem = new MenuItem(fileMenu, SWT.PUSH);
    fileOpenFileMenuItem.setText(RESOURCES.getString("FileOpenFileMenu"));
    fileOpenFileMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
        String fileName = fileDialog.open();
        if(fileName != null) {
          navigate(fileName);
        }
      }
    });
    MenuItem viewMenuItem = new MenuItem(menuBar, SWT.CASCADE);
    viewMenuItem.setText(RESOURCES.getString("ViewMenu"));
    Menu viewMenu = new Menu(webBrowserWindow, SWT.DROP_DOWN);
    viewMenuItem.setMenu(viewMenu);
    MenuItem viewToolbarsMenuItem = new MenuItem(viewMenu, SWT.CASCADE);
    viewToolbarsMenuItem.setText(RESOURCES.getString("ViewToolbarsMenu"));
    Menu viewToolbarsMenu = new Menu(webBrowserWindow, SWT.DROP_DOWN);
    viewToolbarsMenuItem.setMenu(viewToolbarsMenu);
    buttonBarCheckBoxMenuItem = new MenuItem(viewToolbarsMenu, SWT.CHECK);
    buttonBarCheckBoxMenuItem.setText(RESOURCES.getString("ViewToolbarsButtonBarMenu"));
    buttonBarCheckBoxMenuItem.setSelection(isButtonBarVisible());
    buttonBarCheckBoxMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setButtonBarVisible(buttonBarCheckBoxMenuItem.getSelection());
      }
    });
    locationBarCheckBoxMenuItem = new MenuItem(viewToolbarsMenu, SWT.CHECK);
    locationBarCheckBoxMenuItem.setText(RESOURCES.getString("ViewToolbarsLocationBarMenu"));
    locationBarCheckBoxMenuItem.setSelection(isLocationBarVisible());
    locationBarCheckBoxMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setLocationBarVisible(locationBarCheckBoxMenuItem.getSelection());
      }
    });
    statusBarCheckBoxMenuItem = new MenuItem(viewMenu, SWT.CHECK);
    statusBarCheckBoxMenuItem.setText(RESOURCES.getString("ViewStatusBarMenu"));
    statusBarCheckBoxMenuItem.setSelection(isStatusBarVisible());
    statusBarCheckBoxMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setStatusBarVisible(statusBarCheckBoxMenuItem.getSelection());
      }
    });
  }
  
  private void createStatusBarContent() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    gridLayout.marginHeight = 2;
    gridLayout.marginWidth = 2;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    statusBarPane.setLayout(gridLayout);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    statusLabel = new Label(statusBarPane, SWT.NONE);
    statusLabel.setLayoutData(gridData);
    gridData = new GridData();
    gridData.widthHint = 50;
    gridData.heightHint = 0;
    gridData.verticalAlignment = SWT.FILL;
    progressBar = new ProgressBar(statusBarPane, SWT.HORIZONTAL);
    progressBar.setMinimum(0);
    progressBar.setMaximum(100);
    progressBar.setLayoutData(gridData);
    progressBar.setVisible(false);
  }
  
  private void createButtonBarContent() {
    buttonBarPane.setLayout(new FillLayout());
    ToolBar buttonToolBar = new ToolBar(buttonBarPane, SWT.HORIZONTAL | SWT.FLAT);
    backButton = new ToolItem(buttonToolBar, SWT.PUSH);
    backButton.setImage(createIcon("BackIcon"));
    backButton.setToolTipText(RESOURCES.getString("BackText"));
    backButton.setEnabled(false);
    backButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        navigateBack();
      }
    });
    forwardButton = new ToolItem(buttonToolBar, SWT.PUSH);
    forwardButton.setImage(createIcon("ForwardIcon"));
    forwardButton.setToolTipText(RESOURCES.getString("ForwardText"));
    forwardButton.setEnabled(false);
    forwardButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        navigateForward();
      }
    });
    reloadButton = new ToolItem(buttonToolBar, SWT.PUSH);
    reloadButton.setImage(createIcon("ReloadIcon"));
    reloadButton.setToolTipText(RESOURCES.getString("ReloadText"));
    reloadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reloadPage();
      }
    });
    stopButton = new ToolItem(buttonToolBar, SWT.PUSH);
    stopButton.setImage(createIcon("StopIcon"));
    stopButton.setToolTipText(RESOURCES.getString("StopText"));
    stopButton.setEnabled(false);
    stopButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        stopLoading();
      }
    });
  }
  
  private void updateNavigationButtons() {
    backButton.setEnabled(isBackNavigationEnabled());
    if(backMenuItem != null) {
      backMenuItem.setEnabled(isBackNavigationEnabled());
    }
    forwardButton.setEnabled(isForwardNavigationEnabled());
    if(forwardMenuItem != null) {
      forwardMenuItem.setEnabled(isForwardNavigationEnabled());
    }
  }
  
  private void updateStopButton(boolean isForcedOn) {
    boolean isStopEnabled = isForcedOn || getLoadingProgress() != 100;
    stopButton.setEnabled(isStopEnabled);
    if(stopMenuItem != null) {
      stopMenuItem.setEnabled(isStopEnabled);
    }
  }

  private void updateLocationField(String location) {
    location = location == null? getResourceLocation(): location;
    if(!locationField.getText().equals(location)) {
      locationField.setText(location);
      locationField.setSelection(location.length(), location.length());
    }
  }

  private void updateStatus() {
    statusLabel.setText(status);
    statusBarPane.layout();
  }

  private void updateProgressValue() {
    progressBar.setSelection(loadingProgress);
    boolean isProgressBarVisible = loadingProgress < 100;
    progressBar.setVisible(isProgressBarVisible);
    ((GridData)progressBar.getLayoutData()).exclude = !isProgressBarVisible;
    statusBarPane.layout();
  }

  private void createLocationBarContent() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    gridLayout.marginHeight = 2;
    gridLayout.marginWidth = 2;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    locationBarPane.setLayout(gridLayout);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    locationField = new Text(locationBarPane, SWT.BORDER);
    locationField.setLayoutData(gridData);
    locationField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if(e.keyCode == SWT.ESC) {
          updateLocationField(null);
          locationField.setSelection(0, locationField.getText().length());
        }
      }
    });
    locationField.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        navigate(locationField.getText());
      }
    });
    gridData = new GridData();
    gridData.verticalAlignment = SWT.FILL;
    ToolBar goToolBar = new ToolBar(locationBarPane, SWT.FLAT);
    goToolBar.setLayoutData(gridData);
    ToolItem goButton = new ToolItem(goToolBar, SWT.PUSH);
    goButton.setImage(createIcon("GoIcon"));
    goButton.setToolTipText(RESOURCES.getString("GoText"));
    goButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        navigate(locationField.getText());
      }
    });
  }
  
  /**
   * Set whether the status bar is visible.
   * @param isStatusBarVisible true if the status bar should be visible, false otherwise.
   */
  public void setStatusBarVisible(boolean isStatusBarVisible) {
    if(isStatusBarVisible == isStatusBarVisible()) {
      return;
    }
    statusBarPane.setVisible(isStatusBarVisible);
    ((GridData)statusBarPane.getLayoutData()).exclude = !isStatusBarVisible;
    if(statusBarCheckBoxMenuItem != null) {
      statusBarCheckBoxMenuItem.setSelection(isStatusBarVisible);
    }
    adjustBorder();
    layout();
  }
  
  /**
   * Indicate whether the status bar is visible.
   * @return true if the status bar is visible.
   */
  public boolean isStatusBarVisible() {
    return statusBarPane.getVisible();
  }
  
  /**
   * Set whether the menu bar is visible.
   * @param isMenuBarVisible true if the menu bar should be visible, false otherwise.
   */
  void setMenuBarVisible(boolean isMenuBarVisible) {
    if(isMenuBarVisible == isMenuBarVisible()) {
      return;
    }
    JWebBrowserWindow webBrowserWindow = getWebBrowserWindow();
    if(webBrowserWindow != null) {
      webBrowserWindow.setMenuBar(isMenuBarVisible? menuBar: null);
    }
    adjustBorder();
  }
  
  /**
   * Indicate whether the menu bar is visible.
   * @return true if the menu bar is visible.
   */
  boolean isMenuBarVisible() {
    return menuBar != null && menuBar.getVisible();
  }
  
  /**
   * Set whether the button bar is visible.
   * @param isButtonBarVisible true if the button bar should be visible, false otherwise.
   */
  public void setButtonBarVisible(boolean isButtonBarVisible) {
    if(isButtonBarVisible == isButtonBarVisible()) {
      return;
    }
    buttonBarPane.setVisible(isButtonBarVisible);
    ((GridData)buttonBarPane.getLayoutData()).exclude = !isButtonBarVisible;
    if(buttonBarCheckBoxMenuItem != null) {
      buttonBarCheckBoxMenuItem.setSelection(isButtonBarVisible);
    }
    adjustBorder();
    layout();
  }
  
  /**
   * Indicate whether the button bar is visible.
   * @return true if the button bar is visible.
   */
  public boolean isButtonBarVisible() {
    return buttonBarPane.getVisible();
  }
  
  /**
   * Set whether the location bar is visible.
   * @param isLocationBarVisible true if the location bar should be visible, false otherwise.
   */
  public void setLocationBarVisible(boolean isLocationBarVisible) {
    if(isLocationBarVisible == isLocationBarVisible()) {
      return;
    }
    locationBarPane.setVisible(isLocationBarVisible);
    ((GridData)locationBarPane.getLayoutData()).exclude = !isLocationBarVisible;
    if(locationBarCheckBoxMenuItem != null) {
      locationBarCheckBoxMenuItem.setSelection(isLocationBarVisible);
    }
    adjustBorder();
    layout();
  }
  
  /**
   * Indicate whether the location bar is visible.
   * @return true if the location bar is visible.
   */
  public boolean isLocationBarVisible() {
    return locationBarPane.getVisible();
  }
  
  private String title;
  
  /**
   * Get the title of the web page.
   * @return the title of the page.
   */
  public String getPageTitle() {
    return title == null? "": title;
  }
  
  private String status = "";
  
  /**
   * Get the status text.
   * @return the status text.
   */
  public String getStatusText() {
    return status == null? "": status;
  }

  /**
   * Get the HTML content.
   * @return the HTML content.
   */
  public String getHTMLContent() {
    return browser.getText();
  }
  
  /**
   * Set the HTML content.
   * @param html the HTML content.
   */
  public boolean setHTMLContent(String html) {
    return browser.setText(html);
  }
  
  /**
   * Get the location of the resource currently displayed.
   * @return the location.
   */
  public String getResourceLocation() {
    return browser.getUrl();
  }
  
  /**
   * Navigate to a resource, with its location specified as a URL or path.
   * @param resourceLocation the URL or path.
   * @return true if the navigation was successful. 
   */
  public boolean navigate(String resourceLocation) {
    return browser.setUrl(resourceLocation);
  }
  
  /**
   * Indicate if the web browser Back functionality is enabled.
   * @return true if the web browser Back functionality is enabled.
   */
  public boolean isBackNavigationEnabled() {
    return browser.isBackEnabled();
  }
  
  /**
   * Invoke the web browser Back functionality.
   */
  public void navigateBack() {
    browser.back();
  }
  
  /**
   * Indicate if the web browser Forward functionality is enabled.
   * @return true if the web browser Forward functionality is enabled.
   */
  public boolean isForwardNavigationEnabled() {
    return browser.isForwardEnabled();
  }
  
  /**
   * Invoke the web browser Forward functionality.
   */
  public void navigateForward() {
    browser.forward();
  }
  
  /**
   * Invoke the web browser Reload functionality.
   */
  public void reloadPage() {
    browser.refresh();
  }
  
  /**
   * Invoke the web browser Stop functionality, to stop all current loading operations.
   */
  public void stopLoading() {
    browser.stop();
  }
  
//  /**
//   * Execute some javascript, and wait for the indication of success.
//   * @param javascript the javascript to execute.
//   * @return true if the execution succeeded. 
//   */
//  public boolean executeJavascriptAndWait(String javascript) {
//    return nativeComponent.executeJavascriptAndWait(javascript);
//  }
  
  private static Pattern JAVASCRIPT_LINE_COMMENT_PATTERN = Pattern.compile("^\\s*//.*$", Pattern.MULTILINE);
  
  private boolean executeJavascriptAndWait(String javascript) {
    javascript = JAVASCRIPT_LINE_COMMENT_PATTERN.matcher(javascript).replaceAll("");
    return browser.execute(javascript);
  }
  
  /**
   * Execute some javascript.
   * @param javascript the javascript to execute. 
   */
  public void executeJavascript(String javascript) {
    executeJavascriptAndWait(javascript);
  }
  
  /**
   * Execute some javascript, and wait for the result coming from the return statements.
   * @param javascript the javascript to execute which must contain explicit return statements. 
   * @return the value, potentially a String, Number, Boolean.
   */
  public Object executeJavascriptWithResult(String javascript) {
    if(!javascript.endsWith(";")) {
      javascript = javascript + ";";
    }
    String[] result = executeJavascriptWithCommandResult("[[getScriptResult]]",
        "try {" +
        "  var result = function() {" + javascript + "}();" +
        "  var type = result? typeof(result): '';" +
        "  if('string' == type) {" +
        "    window.location = '" + JWebBrowser.COMMAND_LOCATION_PREFIX + "' + encodeURIComponent('[[getScriptResult]]') + '&' + encodeURIComponent(result);" +
        "  } else {" +
        "    window.location = '" + JWebBrowser.COMMAND_LOCATION_PREFIX + "' + encodeURIComponent('[[getScriptResult]]') + '&' + encodeURIComponent(type) + '&' + encodeURIComponent(result);" +
        "  }" +
        "} catch(exxxxx) {" +
        "  window.location = '" + JWebBrowser.COMMAND_LOCATION_PREFIX + "' + encodeURIComponent('[[getScriptResult]]') + '&&'" +
        "}");
    if(result == null) {
      return null;
    }
    if(result.length == 1) {
      return convertJavascriptObjectToJava("string", result[0]);
    }
    return convertJavascriptObjectToJava(result[0], result[1]);
  }

  /**
   * Create the Javascript function call using the function name and Java objects as arguments. Note that it does not contain a semi-colon at the end of the statement, to allow call chaining.
   * @param functionName the name of the Javascript funtion.
   * @param args the Java objects (String, number, boolean, or array) which will get converted to Javascript arguments.
   * @return the function call, in the form "functionName(convArg1, convArg2, ...)".
   */
  public static String createJavascriptFunctionCall(String functionName, Object... args) {
    StringBuilder sb = new StringBuilder();
    sb.append(functionName).append('(');
    for(int i=0; i<args.length; i++) {
      if(i > 0) {
        sb.append(", ");
      }
      sb.append(convertJavaObjectToJavascript(args[i]));
    }
    sb.append(")");
    return sb.toString();
  }
  
  /**
   * Convert a Java object to Javascript, to simplify the task of executing scripts. Conversion adds quotes around Strings (with Java escaping and Javascript unescaping around), add brackets to arrays, treats arrays of arrays, and can handle null values.
   * @param o the object to convert, which can be a String, number, boolean, or array.
   */
  public static String convertJavaObjectToJavascript(Object o) {
    if(o == null) {
      return "null";
    }
    if(o instanceof Boolean || o instanceof Number) {
      return o.toString();
    }
    if(o.getClass().isArray()) {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      int length = Array.getLength(o);
      for(int i=0; i<length; i++) {
        if(i > 0) {
          sb.append(", ");
        }
        sb.append(convertJavaObjectToJavascript(Array.get(o, i)));
      }
      sb.append(']');
      return sb.toString();
    }
    o = o.toString();
    String encodedArg = Utils.encodeURL((String)o);
    if(o.equals(encodedArg)) {
      return '\'' + (String)o + '\'';
    }
    return "decodeURIComponent('" + encodedArg + "')";
  }
  
  private static Object convertJavascriptObjectToJava(String type, String value) {
    if(type.length() == 0) {
      return null;
    }
    if("boolean".equals(type)) {
      return Boolean.parseBoolean(value);
    }
    if("number".equals(type)) {
      try {
        return Integer.parseInt(value);
      } catch(Exception e) {}
      try {
        return Float.parseFloat(value);
      } catch(Exception e) {}
      try {
        return Long.parseLong(value);
      } catch(Exception e) {}
      throw new IllegalStateException("Could not convert number: " + value);
    }
    return value;
  }
  
  private String[] executeJavascriptWithCommandResult(final String command, String script) {
    final Object[] resultArray = new Object[] {null};
    WebBrowserAdapter webBrowserListener = new WebBrowserAdapter() {
      @Override
      public void commandReceived(WebBrowserEvent e, String command_, String[] args) {
        if(command.equals(command_)) {
          resultArray[0] = args;
          removeWebBrowserListener(this);
        }
      }
    };
    addWebBrowserListener(webBrowserListener);
    if(executeJavascriptAndWait(script)) {
      for(int i=0; i<20; i++) {
        EventDispatchUtils.sleepWithEventDispatch(new EventDispatchUtils.Condition() {
          public boolean getValue() {
            return resultArray[0] != null;
          }
        }, 50);
      }
    }
    removeWebBrowserListener(webBrowserListener);
    return (String[])resultArray[0];
  }
  
  private int loadingProgress = 100;
  
  /**
   * Get the loading progress, a value between 0 and 100, where 100 means it is fully loaded.
   * @return a value between 0 and 100 indicating the current loading progress.
   */
  public int getLoadingProgress() {
    return loadingProgress;
  }
  
  /**
   * Show or hide all the bars at once.
   * @param areBarsVisible true to show all bars, false to hide them all.
   */
  public void setBarsVisible(boolean areBarsVisible) {
    setMenuBarVisible(areBarsVisible);
    setButtonBarVisible(areBarsVisible);
    setLocationBarVisible(areBarsVisible);
    setStatusBarVisible(areBarsVisible);
  }
  
  private void adjustBorder() {
    FillLayout layout = (FillLayout)browserContainer.getLayout();
    if(isMenuBarVisible() || isButtonBarVisible() || isLocationBarVisible() || isStatusBarVisible()) {
      layout.marginWidth = 2;
      layout.marginHeight = 2;
    } else {
      layout.marginWidth = 0;
      layout.marginHeight = 0;
    }
    browserContainer.layout();
  }
  
  /**
   * Get the menu bar, which allows to modify the items.
   * @return the menu bar.
   */
  Menu getMenuBar() {
    return menuBar;
  }
  
  /**
   * Get the file menu, which allows to modify the items.
   * @return the file menu.
   */
  Menu getFileMenu() {
    return fileMenu;
  }
  
  private Image createIcon(String resourceKey) {
    String value = RESOURCES.getString(resourceKey);
    return value.length() == 0? null: new Image(getDisplay(), new ImageData(JWebBrowser.class.getResourceAsStream(value)));
  }
  
  /**
   * Get the web browser window if the web browser is contained in one.
   * @return the web browser window, or null if the shell that contains the browser is not a web browser window.
   */
  public JWebBrowserWindow getWebBrowserWindow() {
    Shell shell = getShell();
    if(shell instanceof JWebBrowserWindow) {
      return (JWebBrowserWindow)shell;
    }
    return null;
  }
  
  private boolean isLoading;
  
  private MenuItem backMenuItem;
  private MenuItem forwardMenuItem;
  private MenuItem reloadMenuItem;
  private MenuItem stopMenuItem;
  
  private void registerDefaultPopupMenu(final Browser browser) {
    Menu oldMenu = browser.getMenu();
    if(oldMenu != null) {
      oldMenu.dispose();
    }
    if("ie".equals(browser.getBrowserType())) {
      browser.setMenu(null);
      return;
    }
    Menu menu = new Menu(browser.getShell(), SWT.POP_UP);
    backMenuItem = new MenuItem(menu, SWT.PUSH);
    backMenuItem.setText(RESOURCES.getString("SystemMenuBack"));
    backMenuItem.setImage(new Image(browser.getDisplay(), getClass().getResourceAsStream(RESOURCES.getString("SystemMenuBackIcon"))));
    backMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        navigateBack();
      }
    });
    forwardMenuItem = new MenuItem(menu, SWT.PUSH);
    forwardMenuItem.setText(RESOURCES.getString("SystemMenuForward"));
    forwardMenuItem.setImage(new Image(browser.getDisplay(), getClass().getResourceAsStream(RESOURCES.getString("SystemMenuForwardIcon"))));
    forwardMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        navigateForward();
      }
    });
    reloadMenuItem = new MenuItem(menu, SWT.PUSH);
    reloadMenuItem.setText(RESOURCES.getString("SystemMenuReload"));
    reloadMenuItem.setImage(new Image(browser.getDisplay(), getClass().getResourceAsStream(RESOURCES.getString("SystemMenuReloadIcon"))));
    reloadMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        reloadPage();
      }
    });
    stopMenuItem = new MenuItem(menu, SWT.PUSH);
    stopMenuItem.setText(RESOURCES.getString("SystemMenuStop"));
    stopMenuItem.setImage(new Image(browser.getDisplay(), getClass().getResourceAsStream(RESOURCES.getString("SystemMenuStopIcon"))));
    stopMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        stopLoading();
      }
    });
    menu.addMenuListener(new MenuAdapter() {
      @Override
      public void menuShown(MenuEvent e) {
        backMenuItem.setEnabled(browser.isBackEnabled());
        forwardMenuItem.setEnabled(browser.isForwardEnabled());
        stopMenuItem.setEnabled(isLoading);
      }
    });
    browser.setMenu(menu);
  }
  
  /**
   * Set whether this component is able to detect a popup menu gesture to show its default popup menu.
   * @param isDefaultPopupMenuRegistered true if the default popup menu is registered.
   */
  public void setDefaultPopupMenuRegistered(boolean isDefaultPopupMenuRegistered) {
    backMenuItem = null;
    forwardMenuItem = null;
    reloadMenuItem = null;
    stopMenuItem = null;
    if(isDefaultPopupMenuRegistered) {
      registerDefaultPopupMenu(browser);
    } else {
      Menu oldMenu = browser.getMenu();
      if(oldMenu != null) {
        oldMenu.dispose();
      }
      final Menu menu = new Menu(browser.getShell(), SWT.POP_UP);
      menu.addMenuListener(new MenuAdapter() {
        @Override
        public void menuShown(MenuEvent e) {
          menu.setVisible(false);
        }
      });
      browser.setMenu(menu);
    }
  }
  
  protected List<WebBrowserListener> listenerList = new ArrayList<WebBrowserListener>();
  
  /**
   * Add a web browser listener.
   * @param listener The web browser listener to add.
   */
  public void addWebBrowserListener(WebBrowserListener listener) {
    listenerList.add(listener);
  }
  
  /**
   * Remove a web browser listener.
   * @param listener the web browser listener to remove.
   */
  public void removeWebBrowserListener(WebBrowserListener listener) {
    listenerList.remove(listener);
  }

  /**
   * Get the web browser listeners.
   * @return the web browser listeners.
   */
  public WebBrowserListener[] getWebBrowserListeners() {
    return listenerList.toArray(new WebBrowserListener[0]);
  }
  
  /**
   * Get the native component.
   * @return the native componnet.
   */
  public Browser getNativeComponent() {
    return browser;
  }
  
}
