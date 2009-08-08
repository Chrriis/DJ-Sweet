/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.flashplayer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.FlashPlayerListener;
import chrriis.dj.sweet.components.JFlashPlayer;

/**
 * @author Christopher Deckers
 */
public class FunctionCalls extends Composite {

  public FunctionCalls(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group flashPlayerPanel = new Group(this, SWT.NONE);
    flashPlayerPanel.setLayout(new FillLayout());
    flashPlayerPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    flashPlayerPanel.setText("Native Flash Player component");
    final JFlashPlayer flashPlayer = new JFlashPlayer(flashPlayerPanel);
    // Flash Demo from Paulus Tuerah (www.goldenstudios.or.id)
    flashPlayer.load(getClass(), "resource/FlashPlayerInteractions.swf");
    Group interactionsPanel = new Group(this, SWT.NONE);
    interactionsPanel.setLayout(new GridLayout(2, false));
    interactionsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    interactionsPanel.setText("Java Interactions");
    Label functionLabel = new Label(interactionsPanel, SWT.NONE);
    functionLabel.setText("Function Call: ");
    Composite getterSetterFunctionPanel = new Composite(interactionsPanel, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.center = true;
    getterSetterFunctionPanel.setLayout(rowLayout);
    Button getterButton = new Button(getterSetterFunctionPanel, SWT.PUSH);
    getterButton.setText("Get");
    final Text functionTextField = new Text(getterSetterFunctionPanel, SWT.BORDER);
    Button setterButton = new Button(getterSetterFunctionPanel, SWT.PUSH);
    setterButton.setText("Set");
    Label commandLabel = new Label(interactionsPanel, SWT.NONE);
    commandLabel.setText("Received Command: ");
    final Label commandValueLabel = new Label(interactionsPanel, SWT.NONE);
    commandValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    commandValueLabel.setText("-");
    // Attach the listeners
    getterButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        functionTextField.setText((String)flashPlayer.invokeFlashFunctionWithResult("getMessageX"));
      }
    });
    setterButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        flashPlayer.invokeFlashFunction("setMessageX", functionTextField.getText());
      }
    });
    flashPlayer.addFlashPlayerListener(new FlashPlayerListener() {
      public void commandReceived(String command, Object[] args) {
        if("sendCommandTest".equals(command)) {
          StringBuilder sb = new StringBuilder();
          for(int i=0; i<args.length; i++) {
            if(i > 0) {
              sb.append(", ");
            }
            sb.append(args[i]);
          }
          commandValueLabel.setText(sb.toString());
        }
      }
    });
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new FunctionCalls(shell);
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
