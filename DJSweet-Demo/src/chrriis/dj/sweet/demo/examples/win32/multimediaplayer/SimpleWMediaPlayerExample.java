/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.win32.multimediaplayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.win32.JWMediaPlayer;

/**
 * @author Christopher Deckers
 */
public class SimpleWMediaPlayerExample extends Composite {

  public SimpleWMediaPlayerExample(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    // Create the components that allow to load a file in the player.
    // Create the components that allow to load a file in the player.
    Composite playerFilePanel = new Composite(this, SWT.NONE);
    playerFilePanel.setLayout(new GridLayout(3, false));
    playerFilePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    Label playerFileLabel = new Label(playerFilePanel, SWT.NONE);
    playerFileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    playerFileLabel.setText("File: ");
    final Text playerFileTextField = new Text(playerFilePanel, SWT.BORDER);
    playerFileTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    Button playerFileButton = new Button(playerFilePanel, SWT.PUSH);
    playerFileButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    playerFileButton.setText("...");
    // Create the player.
    Group playerPanel = new Group(this, SWT.NONE);
    playerPanel.setLayout(new FillLayout());
    playerPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    playerPanel.setText("Native Media Player component");
    final JWMediaPlayer player = new JWMediaPlayer(playerPanel);
    // Create an additional bar allowing to show/hide the control bar.
    Composite buttonPanel = new Composite(this, SWT.NONE);
    buttonPanel.setLayout(new GridLayout());
    buttonPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    player.setControlBarVisible(false);
    final Button controlBarCheckBox = new Button(buttonPanel, SWT.CHECK);
    controlBarCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    controlBarCheckBox.setText("Control Bar");
    controlBarCheckBox.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        player.setControlBarVisible(controlBarCheckBox.getSelection());
      }
    });
    // Add listeners
    final Runnable loadPlayerFileRunnable = new Runnable() {
      public void run() {
        player.load(playerFileTextField.getText());
      }
    };
    playerFileTextField.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        loadPlayerFileRunnable.run();
      }
    });
    playerFileButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
        String filePath = fileDialog.open();
        if(filePath != null) {
          playerFileTextField.setText(filePath);
          loadPlayerFileRunnable.run();
        }
      }
    });
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SimpleWMediaPlayerExample(shell);
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
