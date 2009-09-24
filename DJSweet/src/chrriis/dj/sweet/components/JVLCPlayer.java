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
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import chrriis.common.WebServer;
import chrriis.dj.sweet.NSOption;

/**
 * A native multimedia player. It is a browser-based component, which relies on the VLC plugin.<br/>
 * Methods execute when this component is initialized. If the component is not initialized, methods will be executed as soon as it gets initialized.
 * If the initialization fails, the methods will not have any effect. The results from methods have relevant values only when the component is valid.
 * @author Christopher Deckers
 */
public class JVLCPlayer extends Composite {

  private final ResourceBundle RESOURCES = ResourceBundle.getBundle(JVLCPlayer.class.getPackage().getName().replace('.', '/') + "/resource/VLCPlayer");

  private Composite webBrowserPanel;
  private JWebBrowser webBrowser;

  private Composite controlBarPane;
  private Button playButton;
  private Button pauseButton;
  private Button stopButton;

  private static class NWebBrowserObject extends WebBrowserObject {

    private JVLCPlayer vlcPlayer;

    public NWebBrowserObject(JVLCPlayer vlcPlayer) {
      super(vlcPlayer.webBrowser);
      this.vlcPlayer = vlcPlayer;
    }

    @Override
    protected ObjectHTMLConfiguration getObjectHtmlConfiguration() {
      ObjectHTMLConfiguration objectHTMLConfiguration = new ObjectHTMLConfiguration();
      if(vlcPlayer.options != null) {
        // Possible when debugging and calling the same URL again. No options but better than nothing.
        objectHTMLConfiguration.setHTMLParameters(vlcPlayer.options.getParameters());
      }
      objectHTMLConfiguration.setWindowsClassID("9BE31822-FDAD-461B-AD51-BE1D1C159921");
      objectHTMLConfiguration.setWindowsInstallationURL("http://downloads.videolan.org/pub/videolan/vlc/latest/win32/axvlc.cab");
      objectHTMLConfiguration.setMimeType("application/x-vlc-plugin");
      objectHTMLConfiguration.setInstallationURL("http://www.videolan.org");
//      objectHTMLConfiguration.setWindowsParamName("Src");
//      objectHTMLConfiguration.setParamName("target");
      objectHTMLConfiguration.setVersion("VideoLAN.VLCPlugin.2");
      vlcPlayer.options = null;
      return objectHTMLConfiguration;
    }

    @Override
    public String getLocalFileURL(File localFile) {
      try {
        return "file://" + localFile.toURI().toURL().toString().substring("file:".length());
      } catch (Exception e) {
        return "file:///" + localFile.getAbsolutePath();
      }
    }

  }

  private WebBrowserObject webBrowserObject;

  WebBrowserObject getWebBrowserObject() {
    return webBrowserObject;
  }

//  private Scale seekBarSlider;
//  private volatile boolean isAdjustingSeekBar;
//  private volatile Thread updateThread;
//  private Label timeLabel;
//  private Button volumeButton;
//  private Scale volumeSlider;
//  private boolean isAdjustingVolume;

//  void adjustVolumePanel() {
//    volumeButton.setEnabled(true);
//    VLCAudio vlcAudio = getVLCAudio();
//    boolean isMute = vlcAudio.isMute();
//    if(isMute) {
//      volumeButton.setImage(createImage("VolumeOffIcon"));
//      volumeButton.setToolTipText(RESOURCES.getString("VolumeOffText"));
//    } else {
//      volumeButton.setImage(createImage("VolumeOnIcon"));
//      volumeButton.setToolTipText(RESOURCES.getString("VolumeOnText"));
//    }
//    volumeSlider.setEnabled(!isMute);
//    if(!isMute) {
//      isAdjustingVolume = true;
//      volumeSlider.setSelection(vlcAudio.getVolume());
//      isAdjustingVolume = false;
//    }
//  }
//
//  private void stopUpdateThread() {
//    updateThread = null;
//  }
//
//  private void startUpdateThread() {
//    if(updateThread != null) {
//      return;
//    }
//    updateThread = new Thread("Sweet - VLC Player control bar update") {
//      @Override
//      public void run() {
//        final Thread currentThread = this;
//        while(currentThread == updateThread) {
//          if(isDisposed()) {
//            stopUpdateThread();
//            return;
//          }
//          try {
//            sleep(1000);
//          } catch(Exception e) {}
//          getDisplay().asyncExec(new Runnable() {
//            public void run() {
//              if(currentThread != updateThread) {
//                return;
//              }
//              if(isDisposed()) {
//                return;
//              }
//              VLCInput vlcInput = getVLCInput();
//              VLCMediaState state = vlcInput.getMediaState();
//              boolean isValid = state == VLCMediaState.OPENING || state == VLCMediaState.BUFFERING || state == VLCMediaState.PLAYING || state == VLCMediaState.PAUSED || state == VLCMediaState.STOPPING;
//              if(isValid) {
//                int time = vlcInput.getAbsolutePosition();
//                int length = vlcInput.getDuration();
//                isValid = time >= 0 && length > 0;
//                if(isValid) {
//                  isAdjustingSeekBar = true;
//                  seekBarSlider.setSelection(Math.round(time * 10000f / length));
//                  isAdjustingSeekBar = false;
//                  timeLabel.setText(formatTime(time, length >= 3600000) + " / " + formatTime(length, false));
//                }
//              }
//              if(!isValid) {
//                timeLabel.setText("");
//              }
//              seekBarSlider.setVisible(isValid);
//            }
//          });
//        }
//      }
//    };
//    updateThread.setDaemon(true);
//    updateThread.start();
//  }
//
//  private static String formatTime(int milliseconds, boolean showHours) {
//    int seconds = milliseconds / 1000;
//    int hours = seconds / 3600;
//    int minutes = (seconds % 3600) / 60;
//    seconds = seconds % 60;
//    StringBuilder sb = new StringBuilder();
//    if(hours != 0 || showHours) {
//      sb.append(hours).append(':');
//    }
//    sb.append(minutes < 10? "0": "").append(minutes).append(':');
//    sb.append(seconds < 10? "0": "").append(seconds);
//    return sb.toString();
//  }

  /**
   * Construct a VLC player.
   * @param options the options to configure the behavior of this component.
   */
  public JVLCPlayer(Composite parent, NSOption... options) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    webBrowserPanel = new Composite(this, SWT.NONE);
    BorderUtils.addLoweredBevelBorderPaintListener(webBrowserPanel);
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowser = new JWebBrowser(webBrowserPanel, options);
    webBrowserObject = new NWebBrowserObject(this);
    vlcAudio = new VLCAudio(this);
    vlcInput = new VLCInput(this);
    vlcPlaylist = new VLCPlaylist(this);
    vlcVideo = new VLCVideo(this);
    controlBarPane = new Composite(this, SWT.NONE);
    GridLayout controlBarPaneGridLayout = new GridLayout();
    controlBarPaneGridLayout.marginWidth = 0;
    controlBarPaneGridLayout.marginHeight = 0;
    controlBarPane.setLayout(controlBarPaneGridLayout);
    controlBarPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//    seekBarSlider = new Scale(controlBarPane, SWT.NONE);
//    seekBarSlider.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//    seekBarSlider.setMaximum(10000);
//    seekBarSlider.setVisible(false);
//    seekBarSlider.addSelectionListener(new SelectionAdapter() {
//      @Override
//      public void widgetSelected(SelectionEvent e) {
//        if(!isAdjustingSeekBar) {
//          getVLCInput().setRelativePosition(((float)seekBarSlider.getSelection()) / 10000);
//        }
//      }
//    });
    Composite buttonBarPanel = new Composite(controlBarPane, SWT.NONE);
    GridLayout buttonBarPanelGridLayout = new GridLayout(3, false);
    buttonBarPanelGridLayout.marginWidth = 0;
    buttonBarPanelGridLayout.marginHeight = 0;
    buttonBarPanel.setLayout(buttonBarPanelGridLayout);
    buttonBarPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//    timeLabel = new Label(buttonBarPanel, SWT.NONE);
//    timeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
//    timeLabel.setText(" ");
    Composite buttonPanel = new Composite(buttonBarPanel, SWT.NONE);
    GridLayout buttonPanelGridLayout = new GridLayout(3, false);
    buttonPanelGridLayout.marginWidth = 0;
    buttonPanelGridLayout.marginHeight = 0;
    buttonPanel.setLayout(buttonPanelGridLayout);
    buttonPanel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    playButton = new Button(buttonPanel, SWT.PUSH);
    playButton.setImage(createImage("PlayIcon"));
    playButton.setEnabled(false);
    playButton.setToolTipText(RESOURCES.getString("PlayText"));
    playButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        getVLCPlaylist().play();
      }
    });
    pauseButton = new Button(buttonPanel, SWT.PUSH);
    pauseButton.setImage(createImage("PauseIcon"));
    pauseButton.setEnabled(false);
    pauseButton.setToolTipText(RESOURCES.getString("PauseText"));
    pauseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        getVLCPlaylist().togglePause();
      }
    });
    stopButton = new Button(buttonPanel, SWT.PUSH);
    stopButton.setImage(createImage("StopIcon"));
    stopButton.setEnabled(false);
    stopButton.setToolTipText(RESOURCES.getString("StopText"));
    stopButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        getVLCPlaylist().stop();
      }
    });
//    Composite volumePanel = new Composite(buttonBarPanel, SWT.NONE);
//    volumePanel.setLayout(new GridLayout(2, false));
//    volumePanel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
//    volumeButton = new Button(volumePanel, SWT.PUSH);
//    volumeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//    volumeButton.addSelectionListener(new SelectionAdapter() {
//      @Override
//      public void widgetSelected(SelectionEvent e) {
//        getVLCAudio().toggleMute();
//      }
//    });
//    volumeSlider = new Scale(volumePanel, SWT.NONE);
//    volumeSlider.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//    volumeSlider.addSelectionListener(new SelectionAdapter() {
//      @Override
//      public void widgetSelected(SelectionEvent e) {
//        if(!isAdjustingVolume) {
//          getVLCAudio().setVolume(volumeSlider.getSelection());
//        }
//      }
//    });
//    adjustVolumePanel();
//    volumeButton.setEnabled(false);
//    volumeSlider.setEnabled(false);
    setControlBarVisible(false);
//    adjustBorder();
    addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
//        stopUpdateThread();
        webBrowserObject.load(null);
      }
    });
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

  /**
   * Get the web browser that contains this component. The web browser should only be used to add listeners, for example to listen to window creation events.
   * @return the web browser.
   */
  public JWebBrowser getWebBrowser() {
    return webBrowser;
  }

//  public String getLoadedResource() {
//    return webBrowserObject.getLoadedResource();
//  }

  /**
   * Load the player, with no content.
   */
  public void load() {
    load((VLCPluginOptions)null);
  }

  /**
   * Load a file.
   * @param resourceLocation the path or URL to the file.
   */
  public void load(String resourceLocation) {
    load(resourceLocation, null);
  }

  /**
   * Load the player, with no content.
   * @param options the options to better configure the initialization of the VLC plugin.
   */
  public void load(VLCPluginOptions options) {
    load_("", options);
  }

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
   * @param options the options to better configure the initialization of the VLC plugin.
   */
  public void load(Class<?> clazz, String resourcePath, VLCPluginOptions options) {
    addReferenceClassLoader(clazz.getClassLoader());
    load(WebServer.getDefaultWebServer().getClassPathResourceURL(clazz.getName(), resourcePath), options);
  }

  private volatile VLCPluginOptions options;

  /**
   * Load a file.
   * @param resourceLocation the path or URL to the file.
   * @param options the options to better configure the initialization of the VLC plugin.
   */
  public void load(String resourceLocation, VLCPluginOptions options) {
    if("".equals(resourceLocation)) {
      resourceLocation = null;
    }
    load_(resourceLocation, options);
  }

  private void load_(String resourceLocation, VLCPluginOptions options) {
    if(options == null) {
      options = new VLCPluginOptions();
    }
    this.options = options;
    webBrowserObject.load(resourceLocation);
    VLCPlaylist playlist = getVLCPlaylist();
    if(resourceLocation != null && !"".equals(resourceLocation)) {
      playlist.clear();
      playlist.addItem(resourceLocation);
      playlist.play();
    }
    boolean hasContent = webBrowserObject.hasContent();
    playButton.setEnabled(hasContent);
    pauseButton.setEnabled(hasContent);
    stopButton.setEnabled(hasContent);
//    if(hasContent) {
//      adjustVolumePanel();
//      startUpdateThread();
//    }
  }

  /**
   * Indicate whether the control bar is visible.
   * @return true if the control bar is visible.
   */
  public boolean isControlBarVisible() {
    return controlBarPane.isVisible();
  }

  /**
   * Set whether the control bar is visible.
   * @param isControlBarVisible true if the control bar should be visible, false otherwise.
   */
  public void setControlBarVisible(boolean isControlBarVisible) {
    controlBarPane.setVisible(isControlBarVisible);
    ((GridData)controlBarPane.getLayoutData()).exclude = !isControlBarVisible;
    controlBarPane.getParent().layout();
    adjustBorder();
  }

  /* ------------------------- VLC API exposed ------------------------- */

  private VLCAudio vlcAudio;

  /**
   * Get the VLC object responsible for audio-related actions.
   * @return the VLC audio object.
   */
  public VLCAudio getVLCAudio() {
    return vlcAudio;
  }

  private VLCInput vlcInput;

  /**
   * Get the VLC object responsible for input-related actions.
   * @return the VLC input object.
   */
  public VLCInput getVLCInput() {
    return vlcInput;
  }

  private VLCPlaylist vlcPlaylist;

  /**
   * Get the VLC object responsible for playlist-related actions.
   * @return the VLC playlist object.
   */
  public VLCPlaylist getVLCPlaylist() {
    return vlcPlaylist;
  }

  private VLCVideo vlcVideo;

  /**
   * Get the VLC object responsible for video-related actions.
   * @return the VLC video object.
   */
  public VLCVideo getVLCVideo() {
    return vlcVideo;
  }

  private List<ClassLoader> referenceClassLoaderList = new ArrayList<ClassLoader>(1);

  void addReferenceClassLoader(ClassLoader referenceClassLoader) {
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
