/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author Christopher Deckers
 * @author Stefan Fussenegger
 */
public enum SweetSystemProperty {

  /**
   * sweet.localhostaddress
   * = &lt;String&gt; or "_localhost_" (default: auto-detect, usually 127.0.0.1)<br/>
   * Set the address that is used as the local host address for all the internal
   * communication channels that require a sockets (local web server, etc.).
   */
  LOCALHOSTADDRESS("sweet.localhostaddress", Type.READ_WRITE),

  /**
   * sweet.debug.printlocalhostaddressdetection
   * = true/false (default: false)<br/>
   * Set whether to print the steps of local host address detection.
   */
  DEBUG_PRINTLOCALHOSTADDRESSDETECTION("sweet.debug.printlocalhostaddressdetection", Type.READ_WRITE),

  /**
   * sweet.debug.printlocalhostaddress
   * = true/false (default: false)<br/>
   * Set whether the address found as the local host address should be printed.
   */
  DEBUG_PRINTLOCALHOSTADDRESS("sweet.debug.printlocalhostaddress", Type.READ_WRITE),


  /**
   * sweet.webserver.debug.printport
   * = true/false (default: false)<br/>
   * Set whether the port that is used by the embedded web server should be
   * printed.
   */
  WEBSERVER_DEBUG_PRINTREQUESTS("sweet.webserver.debug.printrequests", Type.READ_WRITE),

  /**
   * sweet.webserver.debug.printrequests
   * = true/false (default: false)<br/>
   * Set whether the web server should print the requests it receives, along with
   * the result (200 or 404).
   */
  WEBSERVER_DEBUG_PRINTPORT("sweet.webserver.debug.printport", Type.READ_WRITE),


  /**
   * sweet.components.debug.printoptions
   * = true/false (default: false)<br/>
   * Set whether the options used to create a component should be printed.
   */
  COMPONENTS_DEBUG_PRINTOPTIONS("sweet.components.debug.printoptions", Type.READ_WRITE),


  /**
   * sweet.webbrowser.runtime
   * = xulrunner (default: none)<br/>
   * Set the runtime of the web browser. Currently, only XULRunner is supported.
   */
  WEBBROWSER_RUNTIME("sweet.webbrowser.runtime", Type.READ_WRITE),

  /**
   * sweet.webbrowser.xulrunner.home
   * = &lt;path to XULRunner&gt;<br/>
   * Set which XULRunner installation is used. This property is taken into account
   * when using the XULRunner runtime.
   */
  WEBBROWSER_XULRUNNER_HOME("sweet.webbrowser.xulrunner.home", Type.READ_WRITE),

  ;

  //private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
  //    .getLogger(SystemProperty.class);

  /**
   * Type of property (only used in constructor for readability)
   */
  private enum Type {
    READ_WRITE, READ_ONLY;
  }

  private final String _name;
  private final boolean _readOnly;

  private SweetSystemProperty(String name) {
    this(name, Type.READ_ONLY);
  }

  private SweetSystemProperty(String name, Type type) {
    if (name == null) {
      throw new NullPointerException("name");
    }
    name = name.trim();
    if ("".equals(name)) {
      throw new IllegalArgumentException();
    }

    _name = name;
    _readOnly = type == Type.READ_ONLY;
  }

  /**
   * @see System#getProperty(String)
   * @see AccessController#doPrivileged(PrivilegedAction)
   * @return the string value of the system property, or <code>null</code> if there is no property with that key.
   */
  public String get() {
    return get(null);
  }

  /**
   * @param defaultValue the default value to return if the property is not defined.
   * @see System#getProperty(String)
   * @see AccessController#doPrivileged(PrivilegedAction)
   * @return the string value of the system property, or the default value if there is no property with that key.
   */
  public String get(final String defaultValue) {
    return AccessController.doPrivileged(new PrivilegedAction<String>() {
      public String run() {
        return System.getProperty(getName(), defaultValue);
      }
    });
  }

  /**
   * @param value the new value
   * @see System#setProperty(String, String)
   * @see AccessController#doPrivileged(PrivilegedAction)
   * @see #isReadOnly()
   * @exception UnsupportedOperationException if this property is read-only
   */
  public String set(final String value) {
    if (isReadOnly()) {
      throw new UnsupportedOperationException(getName() + " is a read-only property");
    }

    return AccessController.doPrivileged(new PrivilegedAction<String>() {
      public String run() {
        return System.setProperty(getName(), value);
      }
    });
  }

  /**
   * @return name of this property
   */
  public String getName() {
    return _name;
  }

  /**
   * whether this property should not be modified (i.e. "read-only"). Note
   * that it is possible to use {@link System#setProperty(String, String)}
   * directly. It's use is discouraged though (as it might not have the
   * desired effect)
   *
   * @return <code>true</code> if this property should not be modified
   */
  public boolean isReadOnly() {
    return _readOnly;
  }

  /**
   * @return property value (same as {@link #get()}
   * @see #get()
   */
  @Override
  public String toString() {
    return get();
  }

  /**
   * @return a string representation of this object (e.g. "OS_NAME: os.name=Linux (read-only)")
   */
  public String toDebugString() {
    StringBuilder buf = new StringBuilder();
    buf.append(name()).append(": ");
    buf.append(getName()).append("=");
    buf.append(get());
    if (isReadOnly()) {
      buf.append(" (read-only)");
    }
    return buf.toString();
  }

}
