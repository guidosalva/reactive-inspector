package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Reads the JDBC connection settings from the Esper configuration (via XPath),
 * so that they can be used elsewhere.
 */
public class EsperConfigurationReader {

  private static EsperConfigurationReader instance = null;

  private String jdbcClassName;
  private String jdbcUrl;
  private String jdbcUser;
  private String jdbcPassword;

  private EsperConfigurationReader() {
    try {
      // XPath setup
      final URL esperXmlUrl = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path("etc/esper.cfg.xml"), null); //$NON-NLS-1$
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;
      builder = factory.newDocumentBuilder();
      final Document doc = builder.parse(esperXmlUrl.openStream());
      final XPathFactory xPathfactory = XPathFactory.newInstance();
      final XPath xpath = xPathfactory.newXPath();

      // read the class name
      XPathExpression expr = xpath.compile("/esper-configuration/database-reference/drivermanager-connection/@class-name"); //$NON-NLS-1$
      jdbcClassName = (String) expr.evaluate(doc, XPathConstants.STRING);

      // read the URL
      expr = xpath.compile("/esper-configuration/database-reference/drivermanager-connection/@url"); //$NON-NLS-1$)
      jdbcUrl = (String) expr.evaluate(doc, XPathConstants.STRING);

      // read the user name
      expr = xpath.compile("/esper-configuration/database-reference/drivermanager-connection/@user"); //$NON-NLS-1$)
      jdbcUser = (String) expr.evaluate(doc, XPathConstants.STRING);

      // read the password
      expr = xpath.compile("/esper-configuration/database-reference/drivermanager-connection/@password"); //$NON-NLS-1$)
      jdbcPassword = (String) expr.evaluate(doc, XPathConstants.STRING);
    }
    catch (final ParserConfigurationException e) {
      Activator.log(e);
    }
    catch (final MalformedURLException e) {
      Activator.log(e);
    }
    catch (final SAXException e) {
      Activator.log(e);
    }
    catch (final IOException e) {
      Activator.log(e);
    }
    catch (final XPathExpressionException e) {
      Activator.log(e);
    }
  }

  public static EsperConfigurationReader getInstance() {
    if (instance == null) {
      instance = new EsperConfigurationReader();
    }
    return instance;
  }

  public String getJdbcClassName() {
    return jdbcClassName;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
  }

  public String getJdbcUser() {
    return jdbcUser;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

}
