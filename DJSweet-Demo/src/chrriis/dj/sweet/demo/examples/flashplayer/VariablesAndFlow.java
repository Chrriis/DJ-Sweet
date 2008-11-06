/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.flashplayer;

import java.util.HashMap;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.FlashPluginOptions;
import chrriis.dj.sweet.components.JFlashPlayer;

/**
 * @author Christopher Deckers
 */
public class VariablesAndFlow extends Composite {

  public VariablesAndFlow(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group flashPlayerPanel = new Group(this, SWT.NONE);
    flashPlayerPanel.setLayout(new FillLayout());
    flashPlayerPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    flashPlayerPanel.setText("Native Flash Player component");
    final JFlashPlayer flashPlayer = new JFlashPlayer(flashPlayerPanel);
    flashPlayer.setControlBarVisible(true);
    FlashPluginOptions flashLoadingOptions = new FlashPluginOptions();
    flashLoadingOptions.setVariables(new HashMap<String, String>() {{put("mytext", "My Text");}});
    flashPlayer.load(getClass(), "resource/dyn_text_moving.swf", flashLoadingOptions);
    Group variablePanel = new Group(this, SWT.NONE);
    variablePanel.setLayout(new GridLayout());
    variablePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    variablePanel.setText("Get/Set Variables");
    Composite getSetNorthPanel = new Composite(variablePanel, SWT.NONE);
    getSetNorthPanel.setLayout(new GridLayout(4, false));
    Label textLabel = new Label(getSetNorthPanel, SWT.NONE);
    textLabel.setText("Text:");
    final Text setTextField = new Text(getSetNorthPanel, SWT.BORDER);
    GridData gridData = new GridData();
    gridData.widthHint = 100;
    setTextField.setLayoutData(gridData);
    setTextField.setText("Set");
    setTextField.setTextLimit(7);
    Button setButton = new Button(getSetNorthPanel, SWT.PUSH);
    setButton.setText("Set");
    setButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        flashPlayer.setVariable("mytext", setTextField.getText());
      }
    });
    Button getButton = new Button(getSetNorthPanel, SWT.NONE);
    getButton.setText("Get");
    Composite getSetSouthPanel = new Composite(variablePanel, SWT.NONE);
    getSetSouthPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    getSetSouthPanel.setLayout(new GridLayout(2, false));
    Label lastTextLabel = new Label(getSetSouthPanel, SWT.NONE);
    lastTextLabel.setText("Last acquired text:");
    final Label getLabel = new Label(getSetSouthPanel, SWT.NONE);
    getLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    getLabel.setText("-");
    getButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        String value = (String)flashPlayer.getVariable("mytext");
        getLabel.setText(value == null || value.length() == 0? " ": value);
      }
    });
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new VariablesAndFlow(shell);
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
