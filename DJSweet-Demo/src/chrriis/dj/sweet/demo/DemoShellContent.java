/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import chrriis.dj.sweet.components.JSyntaxHighlighter;
import chrriis.dj.sweet.components.JSyntaxHighlighter.ContentLanguage;

/**
 * @author Christopher Deckers
 */
public class DemoShellContent extends Composite {

  public DemoShellContent(Composite parent) {
    super(parent, SWT.NONE);
    final Display display = getDisplay();
    Image EXAMPLE_GROUP_ICON = new Image(display, DemoShellContent.class.getResourceAsStream("resource/fldr_obj.gif"));
    Image EXAMPLE_ICON = new Image(display, DemoShellContent.class.getResourceAsStream("resource/brkp_obj.gif"));
    setLayout(new FillLayout());
    SashForm sashForm = new SashForm(this, SWT.HORIZONTAL | SWT.SMOOTH);
    Tree tree = new Tree(sashForm, SWT.SINGLE | SWT.BORDER);
    for(ExampleGroup exampleGroup: DemoExampleDefinitionLoader.getExampleGroupList()) {
      TreeItem exampleGroupItem = new TreeItem (tree, SWT.NONE);
      exampleGroupItem.setImage(EXAMPLE_GROUP_ICON);
      exampleGroupItem.setText(exampleGroup.getName());
      for(Example example: exampleGroup.getExamples()) {
        TreeItem exampleItem = new TreeItem (exampleGroupItem, SWT.NONE);
        exampleItem.setData(example);
        exampleItem.setImage(EXAMPLE_ICON);
        exampleItem.setText(example.getName());
      }
    }
    final Composite content = new Composite(sashForm, SWT.NONE);
    content.setLayout(new FillLayout());
    sashForm.setWeights(new int[] {2, 7});
    tree.addSelectionListener(new SelectionAdapter() {
      private Example lastSelectedExample;
      @Override
      public void widgetSelected(SelectionEvent e) {
        Object data = e.item.getData();
        if(data instanceof Example) {
          Example example = (Example)data;
          if(example.equals(lastSelectedExample)) {
            return;
          }
          for(Control c: content.getChildren()) {
            c.dispose();
          }
          Composite exampleParent = content;
          lastSelectedExample = example;
          final Class<? extends Control> componentClass = example.getComponentClass();
          if(example.isShowingSources()) {
            final TabFolder tabFolder = new TabFolder(content, SWT.NONE);
            TabItem exampleItem = new TabItem(tabFolder, SWT.NONE);
            exampleItem.setText("Demo");
            Composite exampleContainer = new Composite(tabFolder, SWT.BORDER);
            exampleContainer.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            FillLayout fillLayout = new FillLayout();
            fillLayout.marginWidth = 2;
            fillLayout.marginHeight = 2;
            exampleContainer.setLayout(fillLayout);
            exampleItem.setControl(exampleContainer);
            exampleParent = exampleContainer;
            final TabItem sourceItem = new TabItem(tabFolder, SWT.NONE);
            sourceItem.setText("Source");
            final Composite sourceComposite = new Composite(tabFolder, SWT.NONE);
            sourceComposite.setLayout(new FillLayout());
            sourceItem.setControl(sourceComposite);
            tabFolder.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                if(e.item == sourceItem) {
                  tabFolder.removeSelectionListener(this);
                  try {
                    InputStreamReader reader;
                    try {
                      reader = new InputStreamReader(DemoShellContent.class.getResourceAsStream("/src/" + componentClass.getName().replace('.', '/') + ".java"), "UTF-8");
                    } catch(Exception ex) {
                      reader = new InputStreamReader(new BufferedInputStream(new FileInputStream("src/" + componentClass.getName().replace('.', '/') + ".java")), "UTF-8");
                    }
                    StringWriter writer = new StringWriter();
                    char[] chars = new char[1024];
                    for(int i; (i=reader.read(chars)) >= 0; writer.write(chars, 0, i));
                    JSyntaxHighlighter syntaxHighlighter = new JSyntaxHighlighter(sourceComposite);
                    syntaxHighlighter.setContent(writer.toString(), ContentLanguage.Java);
                    reader.close();
                  } catch(Exception ex) {
                    ex.printStackTrace();
                  }
                  sourceComposite.layout();
                }
              }
            });
          }
          try {
            Composite exampleWrapper = new Composite(exampleParent, SWT.NONE);
            exampleWrapper.setLayout(new GridLayout());
            Text descriptionTextField = new Text(exampleWrapper, SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
            descriptionTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            descriptionTextField.setText(example.getDescription());
            if(!example.isAvailable()) {
              Label notAvailableLabel = new Label(exampleWrapper, SWT.WRAP);
              notAvailableLabel.setText(example.getNotAvailableMessage());
              notAvailableLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
            } else {
              Control exampleComponent = componentClass.getConstructor(Composite.class).newInstance(exampleWrapper);
              exampleComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          content.layout();
        }
      }
    });
  }
  
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    Class<DemoShellContent> clazz = DemoShellContent.class;
    shell.setText("The DJ Project - Sweet");
    shell.setImages(new Image[] {
        new Image(display, clazz.getResourceAsStream("resource/DJIcon16x16.png")),
        new Image(display, clazz.getResourceAsStream("resource/DJIcon24x24.png")),
        new Image(display, clazz.getResourceAsStream("resource/DJIcon32x32.png")),
        new Image(display, clazz.getResourceAsStream("resource/DJIcon48x48.png")),
        new Image(display, clazz.getResourceAsStream("resource/DJIcon256x256.png")),
    });
    shell.setLayout(new FillLayout());
    new DemoShellContent(shell);
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
