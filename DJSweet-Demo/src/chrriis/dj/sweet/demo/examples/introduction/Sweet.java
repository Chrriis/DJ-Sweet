/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.introduction;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christopher Deckers
 */
public class Sweet extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public Sweet(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    Browser editorPane = new Browser(this, SWT.BORDER);
    editorPane.setText(
        "<html>" + LS +
        "  <body>" + LS +
        "    <h1>What for?</h1>" + LS +
        "    <p>SWT is a nice user-interface toolkit, that allows accessing the native capabilities of various platforms.</p>" + LS +
        "    <p>Nevertheless, there is a lack of high level components, with higher level APIs.<br/>" + LS +
        "    This is where Sweet comes into play, by providing a rich component suite with a user friendly API.</p>" + LS +
        "    <h1>DJ Sweet relatives</h1>" + LS +
        "    <p>DJ Sweet is a sub-project of the DJ Project, which contains another similar sub-project: DJ Native Swing.</p>" + LS +
        "    <p>DJ Native Swing offers a rich component suite for Swing, with native integration capabilities.<br/>" + LS +
        "      DJ Sweet is a port of this API to SWT, hence the similarities between the two APIs.</p>" + LS +
        "  </body>" + LS +
        "</html>");
  }
  
}
