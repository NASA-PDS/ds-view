// Copyright 2015, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// This class was copied from HTTPAdaptor to support HTTPS. A bogus 
// TrustManager and HostnameVerifier are setup to ignore certificates or 
// lack thereof.
//
// $Id$

package gov.nasa.pds.portal.product;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jpl.eda.product.ProductException;
import jpl.eda.product.ProductService;
import jpl.eda.product.Retriever;
import jpl.eda.product.Server;
import jpl.eda.xmlquery.LargeResult;
import jpl.eda.xmlquery.XMLQuery;

import org.xml.sax.SAXException;

/**
 * Adapt the web-grid HTTPS product service into the procedural interface expected by the
 * product client.
 *
 * @author kelly
 * @author shardman
 * @version $Revision$
 */
class HTTPSAdaptor implements ProductService {
  /**
   * Creates a new <code>HTTPSAdaptor</code> instance.
   *
   * @param url URL to web-grid HTTPS product service.
   */
  HTTPSAdaptor(URL url) {
    this.url = url;
  }

  public Server createServer() {
    return new HTTPSAccessor();
  }

  /** URL to web-grid HTTPS product service. */
  private URL url;

  /**
   * Access for a single query session.
   */
  private class HTTPSAccessor implements Server, Retriever {
    /**
     * Handle a query for a product.  This method sends the XMLQuery as XML to
     * the web-grid product service.  It interprets a 404 Not Found as
     * returning no results.  For a 200 OK, it synthesizes a result locally
     * and sets up a retriever to get the product data over HTTPS.
     *
     * @param userQuery a <code>XMLQuery</code> value.
     * @return <var>q</var>, possibly adorned with a new result.
     * @throws ProductException if an error occurs.
     */
    public synchronized XMLQuery query(XMLQuery userQuery) throws ProductException {
      XMLQuery q = null;
      try {
        q = new XMLQuery(userQuery.getXMLDocString()); // because clone doesn't work!
        q.getResults().clear();
        StringBuffer b = new StringBuffer("xmlq=");
        b.append(URLEncoder.encode(q.getXMLDocString(), "UTF-8"));

        // Configure the SSLContext with our bogus TrustManager.
        try {
          SSLContext ctx = SSLContext.getInstance("TLS");
          ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
          SSLContext.setDefault(ctx);
        } catch (Exception ex){
          throw new ProductException("An exception occurred setting the SSL context:" + ex.getMessage());
        }

        // Open the connection and set our bogus name verifier.
        c = (HttpsURLConnection) url.openConnection();
        c.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });

        // Set the connection properties.
        c.setDoInput(true);
        c.setDoOutput(true);
        c.setUseCaches(true);
        c.setAllowUserInteraction(false);
        c.setInstanceFollowRedirects(true);
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Length", String.valueOf(b.length()));
        c.connect();
        OutputStream out = null;
        try {
          out = c.getOutputStream();
          out.write(b.toString().getBytes());
        } finally {
          if (out != null) try {
            out.close();
          } catch (IOException ignore) {}
        }
        int rc = c.getResponseCode();
        if (rc == HttpsURLConnection.HTTP_OK) {
          String mime = c.getContentType();
          long size = Long.parseLong(c.getHeaderField("Content-Length"));
          String resourceID = c.getHeaderField("Content-disposition");
          if (resourceID != null && resourceID.length() > 0) {
            Matcher m = DISPOSITION_PATTERN.matcher(resourceID);
            if (m.matches())
              resourceID = m.group(1);
          }
          LargeResult lr = new LargeResult(/*id*/"1", mime, /*profID*/null, resourceID,
            /*headers*/Collections.EMPTY_LIST, size);
          q.getResults().add(lr);
          q.setRetriever(this);
        } else if (rc == HttpsURLConnection.HTTP_NOT_FOUND) {
          // no prob, just leave q unmodified
        } else {
          throw new ProductException("Received response code " + rc + " from HTTPS server");
        }
      } catch (SAXException ex) {
        throw new IllegalStateException("Unexpected SAXException: " + ex.getMessage());
      } catch (IOException ex) {
        throw new ProductException(ex);
      }
      return q;
    }

    /**
     * Retrieve the next chunk over the HTTPS connection.  <strong>Major
     * assumption</strong>: offsets are monotonically increasing.  I know they
     * are since I wrote the ChunkedProductInputStream too, but if that
     * changes, this could well break.
     *
     * @param id Ignored.
     * @param offset Ignored.
     * @param length How large of a block to get.
     * @return a chunk of the product.
     * @throws ProductException if an error occurs.
     */
    public synchronized byte[] retrieveChunk(String id, long offset, int length) throws ProductException {
      try {
        byte[] buf = new byte[length];
        int off = 0;
        while (length > 0) {
          int num = c.getInputStream().read(buf, off, length);
          length -= num;
          off += num;
        }
        return buf;
      } catch (IOException ex) {
        throw new ProductException(ex);
      }
    }

    /** {@inheritDoc} */
    public synchronized void close(String id) throws ProductException {
      try {
        c.getInputStream().close();
        c.disconnect();
        c = null;
      } catch (IOException ex) {
        throw new ProductException(ex);
      }
    }

    /** Established connection to web-grid HTTPS product service. */
    private volatile HttpsURLConnection c;
    private String theQuery;
  }

  /**
   * Our bogus trust manager.
   */
  private static class DefaultTrustManager implements X509TrustManager {
      @Override
      public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

      @Override
      public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

      @Override
      public X509Certificate[] getAcceptedIssuers() {
          return null;
      }
  }

  /** Pattern to grab the filename from a content-disposition header. */
  private static Pattern DISPOSITION_PATTERN = Pattern.compile("^attachment; *filename=\"(.+)\"$");
}
