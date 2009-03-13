/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.webbrowser;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.interfaces.nsICancelable;
import org.mozilla.interfaces.nsIComponentRegistrar;
import org.mozilla.interfaces.nsIFactory;
import org.mozilla.interfaces.nsILocalFile;
import org.mozilla.interfaces.nsIMIMEInfo;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsITransfer;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;

import chrriis.dj.sweet.components.JWebBrowser;

/**
 * @author Christopher Deckers
 */
public class XPCOMDownloadManager extends Composite {

  public XPCOMDownloadManager(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group webBrowserPanel = new Group(this, SWT.NONE);
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowserPanel.setText("Native Web Browser component");
    final JWebBrowser webBrowser = new JWebBrowser(webBrowserPanel, JWebBrowser.useXULRunnerRuntime());
    webBrowser.navigate("http://www.eclipse.org/downloads");
    // Create an additional area to see the downloads in progress.
    final Group downloadsPanel = new Group(this, SWT.NONE);
    downloadsPanel.setLayout(new GridLayout());
    downloadsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    downloadsPanel.setText("Download manager (on-going downloads are automatically added to this area)");
    // We can only access XPCOM when it is properly initialized.
    // This happens when the web browser is created so we run our code in sequence.
    try {
      nsIComponentRegistrar registrar = Mozilla.getInstance().getComponentRegistrar();
      String NS_DOWNLOAD_CID = "e3fa9D0a-1dd1-11b2-bdef-8c720b597445";
      String NS_TRANSFER_CONTRACTID = "@mozilla.org/transfer;1";
      registrar.registerFactory(NS_DOWNLOAD_CID, "Transfer", NS_TRANSFER_CONTRACTID, new nsIFactory() {
        public nsISupports queryInterface(String uuid) {
          if(uuid.equals(nsIFactory.NS_IFACTORY_IID) || uuid.equals(nsIFactory.NS_ISUPPORTS_IID)) {
            return this;
          }
          return null;
        }
        public nsISupports createInstance(nsISupports outer, String iid) {
          return createTransfer(downloadsPanel);
        }
        public void lockFactory(boolean lock) {}
      });
    } catch(Exception e) {
      MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
      messageBox.setText("XPCOM interface");
      messageBox.setMessage("Failed to register XPCOM download manager.\nPlease check your XULRunner configuration.");
      messageBox.open();
    }
  }

  private static nsITransfer createTransfer(final Composite downloadsPanel) {
    return new nsITransfer() {
      public nsISupports queryInterface(String uuid) {
        if(uuid.equals(nsITransfer.NS_ITRANSFER_IID) ||
            uuid.equals(nsITransfer.NS_IWEBPROGRESSLISTENER2_IID) ||
            uuid.equals(nsITransfer.NS_IWEBPROGRESSLISTENER_IID) ||
            uuid.equals(nsITransfer.NS_ISUPPORTS_IID)) {
          return this;
        }
        return null;
      }
      private Composite downloadComponent;
      private Label downloadStatusLabel;
      private String baseText;
      public void init(nsIURI source, nsIURI target, String displayName, nsIMIMEInfo MIMEInfo, double startTime, nsILocalFile tempFile, final nsICancelable cancelable) {
        downloadComponent = new Composite(downloadsPanel, SWT.NONE);
        downloadComponent.setLayout(new GridLayout(2, false));
        Button cancelDownloadButton = new Button(downloadComponent, SWT.PUSH);
        cancelDownloadButton.setText("Cancel");
        final String path = target.getPath();
        cancelDownloadButton.addSelectionListener(new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            cancelable.cancel(Mozilla.NS_ERROR_ABORT);
            removeDownloadComponent();
            new File(path + ".part").delete();
          }
        });
        baseText = "Downloading to " + path;
        downloadStatusLabel = new Label(downloadComponent, SWT.NONE);
        downloadStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        downloadStatusLabel.setText(baseText);
        downloadsPanel.getParent().layout();
      }
      public void onStateChange(nsIWebProgress webProgress, nsIRequest request, long stateFlags, long status) {
        if((stateFlags & nsIWebProgressListener.STATE_STOP) != 0) {
          removeDownloadComponent();
        }
      }
      private void removeDownloadComponent() {
        downloadComponent.dispose();
        downloadsPanel.getParent().layout();
      }
      public void onProgressChange64(nsIWebProgress webProgress, nsIRequest request, long curSelfProgress, long maxSelfProgress, long curTotalProgress, long maxTotalProgress) {
        long currentKBytes = curTotalProgress / 1024;
        long totalKBytes = maxTotalProgress / 1024;
        downloadStatusLabel.setText(baseText + " (" + currentKBytes + "/" + totalKBytes + ")");
        downloadsPanel.layout();
      }
      public void onStatusChange(nsIWebProgress webProgress, nsIRequest request, long status, String message) {}
      public void onSecurityChange(nsIWebProgress webProgress, nsIRequest request, long state) {}
      public void onProgressChange(nsIWebProgress webProgress, nsIRequest request, int curSelfProgress, int maxSelfProgress, int curTotalProgress, int maxTotalProgress) {}
      public void onLocationChange(nsIWebProgress webProgress, nsIRequest request, nsIURI location) {}
    };
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new XPCOMDownloadManager(shell);
    shell.setSize(800, 600);
    shell.open();
    while(!shell.isDisposed()) {
      if(!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }

}
