package no.tine.solidum.proxy.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.rbrastad.extlib.servlet.fileupload.FileMeta;
import no.rbrastad.extlib.servlet.fileupload.MultipartRequestHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpProxy {
	  /** A boolean parameter then when enabled will log input and target URLs to the servlet log. */
	  public static final String P_LOG = "log";

	  private URI targetUri;
	  protected HttpClient proxyClient;
	  private int reponseStatusCode = -1;
	  private String reponseStatusReasonPhrase = null;
	  
	  private OutputStream reponseOutputStream = null;
	  private String responseStream = null;
	  private Header[] responseHeaders = null;
	  
	  private String proxyRequestUri = null;
	  
	  public HttpProxy() {
	    HttpParams hcParams = new BasicHttpParams();	    
	    proxyClient = createHttpClient(hcParams);

	  }

	  /** Called from {@link #init(javax.servlet.ServletConfig)}. HttpClient offers many opportunities for customization.
	   * @param hcParams*/
	  protected HttpClient createHttpClient(HttpParams hcParams) {
		  CacheConfig cacheConfig = new CacheConfig();  
		  cacheConfig.setHeuristicCachingEnabled(false);
		  cacheConfig.setSharedCache(false);
		  cacheConfig.setMaxCacheEntries(1);
		 
	  return new CachingHttpClient( addTrustAllSSL( new DefaultHttpClient() ),cacheConfig ); 
	  }

	  public void destroy() {
	    if (proxyClient != null)
	      proxyClient.getConnectionManager().shutdown();
	    }
	  

	  public void service(HttpServletRequest servletRequest) throws ServletException, IOException {    
	    // Make the Request
	    String method = servletRequest.getMethod();
	    try {
	    	String uriHost = URIUtils.extractHost( new URI( proxyRequestUri )).toString() ;
			targetUri = new URI( uriHost );
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			}
			    
	    HttpRequest proxyRequest;
	    //spec: RFC 2616, sec 4.3: either these two headers signal that there is a message body.
	    if (servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null ||
	        servletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
	    
	    	HttpEntityEnclosingRequest eProxyRequest = new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
	      // Add the input entity (streamed)
	      //  note: we don't bother ensuring we close the servletInputStream since the container handles it
	      eProxyRequest.setEntity(new InputStreamEntity(servletRequest.getInputStream(), servletRequest.getContentLength()));
	      proxyRequest = eProxyRequest;
	    } else
	      proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
		
	    copyRequestHeaders(servletRequest, proxyRequest);
	    
	    rewriteUrlFromRequest(servletRequest);
	    
	    try {
	      // Execute the request
	       HttpResponse proxyResponse = proxyClient.execute(URIUtils.extractHost(targetUri) ,proxyRequest);

	      // Process the response
	      setStatusLine( proxyResponse.getStatusLine() );
	  
          if(getReponseStatusCode() == 401){
	    	  return;
	      }else if(getReponseStatusCode() == 304){
	    	  
	      }else{
		      responseHeaders = proxyResponse.getAllHeaders();
		      
		      boolean isGZipped = false;
			    for(Header header : proxyResponse.getAllHeaders() ){
			    	if(header.getName().equals("Content-Encoding") && header.getValue().equals("gzip"))
			    		isGZipped = true;
			    }
	    	  
		      if( isGZipped  ){
		    	  InputStream inputStream = new GzipDecompressingEntity(proxyResponse.getEntity() ).getContent();
		    	  try{
		    		  responseStream =  Streams.asString( inputStream );
		    	  }finally{
		    		  inputStream.close();
		    	  }
		      }else
		    	  responseStream =  EntityUtils.toString( proxyResponse.getEntity() );  
		      
		      responseStream = new String(responseStream.getBytes("ISO-8859-1"), "UTF-8");
	      }
	           
	      if (proxyRequest instanceof AbortableHttpRequest) {
		        AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
		        abortableHttpRequest.abort();
		      }
	    } catch (Exception e) {
	    	  e.printStackTrace();
		      
	      //abort request, according to best practice with HttpClient
	      if (proxyRequest instanceof AbortableHttpRequest) {
	        AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
	        abortableHttpRequest.abort();
	      }
	      
	      if (e instanceof RuntimeException)
	        e.printStackTrace();
	      if (e instanceof ServletException)
	    	  e.printStackTrace();
	      if (e instanceof IOException)
	    	  e.printStackTrace();
	    }
	  }

	  private void copyRequestParameters(HttpServletRequest servletRequest,HttpRequest proxyRequest) {
			Enumeration paramNames = servletRequest.getParameterNames();
			while(paramNames.hasMoreElements()) {
				  String paramName = (String)paramNames.nextElement();
				  System.out.println("solidum param: " + paramName + " - " +servletRequest.getParameter(paramName));
			}
			  	
			try {
				List<FileMeta> paramsList = MultipartRequestHandler.uploadByApacheFileUpload( servletRequest );
				
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();			
				HttpParams params = new BasicHttpParams();
				for(FileMeta paramMeta : paramsList){
					urlParameters.add(new BasicNameValuePair( paramMeta.getFieldName() ,  paramMeta.getFieldValue() ));
					params.setParameter( paramMeta.getFieldName() , paramMeta.getFieldValue());
				}
				
				proxyRequest.setParams(params);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
				}  	  
	}

	private void setStatusLine(StatusLine statusLine) {
		reponseStatusCode = statusLine.getStatusCode();
		reponseStatusReasonPhrase = statusLine.getReasonPhrase();
	}

	private boolean doResponseRedirectOrNotModifiedLogic(HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpResponse proxyResponse, int statusCode) throws ServletException, IOException {
	    // Check if the proxy response is a redirect
	    // The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
	    if (statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
	        && statusCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
	      Header locationHeader = proxyResponse.getLastHeader(HttpHeaders.LOCATION);
	      if (locationHeader == null) {
	        throw new ServletException("Received status code: " + statusCode
	            + " but no " + HttpHeaders.LOCATION + " header was found in the response");
	      }
	      // Modify the redirect to go to this proxy servlet rather that the proxied host
	      String locStr = rewriteUrlFromResponse(servletRequest, locationHeader.getValue());

	      servletResponse.sendRedirect(locStr);
	      return true;
	    }
	    // 304 needs special handling.  See:
	    // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
	    // We get a 304 whenever passed an 'If-Modified-Since'
	    // header and the data on disk has not changed; server
	    // responds w/ a 304 saying I'm not going to send the
	    // body because the file has not changed.
	    if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
	      servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
	      servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	      return true;
	    }
	    return false;
	  }

	  protected void closeQuietly(Closeable closeable) {
	    try {
	      closeable.close();
	    } catch (IOException e) {
	  //    logger.severe(e.getMessage());
	    }
	  }

	  /** These are the "hop-by-hop" headers that should not be copied.
	   * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
	   * I use an HttpClient HeaderGroup class instead of Set<String> because this
	   * approach does case insensitive lookup faster.
	   */
	  private static final HeaderGroup hopByHopHeaders;
	  static {
	    hopByHopHeaders = new HeaderGroup();
	    String[] headers = new String[] {
	        "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
	        "TE", "Trailers", "Transfer-Encoding", "Upgrade" };
	    for (String header : headers) {
	      hopByHopHeaders.addHeader(new BasicHeader(header, null));
	    }
	  }

	  /** Copy request headers from the servlet client to the proxy request. */
	  protected void copyRequestHeaders(HttpServletRequest servletRequest, HttpRequest proxyRequest) {
	    // Get an Enumeration of all of the header names sent by the client
	    Enumeration enumerationOfHeaderNames = servletRequest.getHeaderNames();
	    while (enumerationOfHeaderNames.hasMoreElements()) {
	      String headerName = (String) enumerationOfHeaderNames.nextElement();
	      //Instead the content-length is effectively set via InputStreamEntity
	      if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
	        continue;
	      if (hopByHopHeaders.containsHeader(headerName))
	        continue;
	      // As per the Java Servlet API 2.5 documentation:
	      //		Some headers, such as Accept-Language can be sent by clients
	      //		as several headers each with a different value rather than
	      //		sending the header as a comma separated list.
	      // Thus, we get an Enumeration of the header values sent by the client
	      Enumeration headers = servletRequest.getHeaders(headerName);
	      while (headers.hasMoreElements()) {
	        String headerValue = (String) headers.nextElement();
	        // In case the proxy host is running multiple virtual servers,
	        // rewrite the Host header to ensure that we get content from
	        // the correct virtual server
	        if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
	          HttpHost host = URIUtils.extractHost(this.targetUri);
	          headerValue = host.getHostName();
	          if (host.getPort() != -1)
	            headerValue += ":"+host.getPort();
	        }
	        proxyRequest.addHeader(headerName, headerValue);
	      }
	    }
	    
	  }

	  private String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
	    StringBuilder uri = new StringBuilder(500);
	    // Handle the path given to the servlet
	    if (servletRequest.getPathInfo() != null) {//ex: /my/path.html
	      uri.append(encodeUriQuery(servletRequest.getPathInfo()));
	    }
	    // Handle the query string
	    String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
	    if (queryString != null && queryString.length() > 0) {
	      //uri.append('?');
	      int fragIdx = queryString.indexOf('#');
	      String queryNoFrag = (fragIdx < 0 ? queryString : queryString.substring(0,fragIdx));
	      uri.append(encodeUriQuery(queryNoFrag));
	      if (fragIdx >= 0) {
	        uri.append('#');
	        uri.append(encodeUriQuery(queryString.substring(fragIdx + 1)));
	      }
	    }
	    
	    return uri.toString();
	  }

	  private String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl) {
	    if (theUrl.startsWith(this.targetUri.toString())) {
	      String curUrl = servletRequest.getRequestURL().toString();//no query
	      String pathInfo = servletRequest.getPathInfo();
	      if (pathInfo != null) {
	        assert curUrl.endsWith(pathInfo);
	        curUrl = curUrl.substring(0,curUrl.length()-pathInfo.length());//take pathInfo off
	      }
	      theUrl = curUrl+theUrl.substring(this.targetUri.toString().length());
	    }
	    return theUrl;
	  }

	  /**
	   * <p>Encodes characters in the query or fragment part of the URI.
	   *
	   * <p>Unfortunately, an incoming URI sometimes has characters disallowed by the spec.  HttpClient
	   * insists that the outgoing proxied request has a valid URI because it uses Java's {@link URI}. To be more
	   * forgiving, we must escape the problematic characters.  See the URI class for the spec.
	   *
	   * @param in example: name=value&foo=bar#fragment
	   */
	  static CharSequence encodeUriQuery(CharSequence in) {
	    //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
	    StringBuilder outBuf = null;
	    Formatter formatter = null;
	    for(int i = 0; i < in.length(); i++) {
	      char c = in.charAt(i);
	      boolean escape = true;
	      if (c < 128) {
	        if (asciiQueryChars.get((int)c)) {
	          escape = false;
	        }
	      } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
	        escape = false;
	      }
	      if (!escape) {
	        if (outBuf != null)
	          outBuf.append(c);
	      } else {
	        //escape
	        if (outBuf == null) {
	          outBuf = new StringBuilder(in.length() + 5*3);
	          outBuf.append(in,0,i);
	          formatter = new Formatter(outBuf);
	        }
	        //leading %, 0 padded, width 2, capital hex
	        formatter.format("%%%02X",(int)c);//TODO
	      }
	    }
	    return outBuf != null ? outBuf : in;
	  }


	  static final BitSet asciiQueryChars;
	  static {
	    char[] c_unreserved = "_-!.~'()*".toCharArray();//plus alphanum
	    char[] c_punct = ",;:$&+=".toCharArray();
	    char[] c_reserved = "?/[]@".toCharArray();//plus punct

	    asciiQueryChars = new BitSet(128);
	    for(char c = 'a'; c <= 'z'; c++) asciiQueryChars.set((int)c);
	    for(char c = 'A'; c <= 'Z'; c++) asciiQueryChars.set((int)c);
	    for(char c = '0'; c <= '9'; c++) asciiQueryChars.set((int)c);
	    for(char c : c_unreserved) asciiQueryChars.set((int)c);
	    for(char c : c_punct) asciiQueryChars.set((int)c);
	    for(char c : c_reserved) asciiQueryChars.set((int)c);

	    asciiQueryChars.set((int)'%');//leave existing percent escapes in place
	  }

	
	  @SuppressWarnings("deprecation")
	public HttpClient addTrustAllSSL(HttpClient base) {
		    try {
		        SSLContext ctx = SSLContext.getInstance("TLS");
		        X509TrustManager tm = new X509TrustManager() {
		            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		 
		            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }
		 
		            public X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		        };
		        ctx.init(null, new TrustManager[]{tm}, null);
		        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		        ClientConnectionManager ccm = base.getConnectionManager();
		        SchemeRegistry sr = ccm.getSchemeRegistry();
		        sr.register(new Scheme("https", ssf, 443));
		        return new DefaultHttpClient(ccm, base.getParams());
		    } catch (Exception ex) {
		        return null;
		    }
		}
	  
	public String getProxyRequestUri() {
		return proxyRequestUri;
	}

	public void setProxyRequestUri(String proxyRequestUri) {
		this.proxyRequestUri = proxyRequestUri;
	}

	public OutputStream getReponseOutputStream() {
		return reponseOutputStream;
	}

	public void setReponseOutputStream(OutputStream reponseOutputStream) {
		this.reponseOutputStream = reponseOutputStream;
	}

	public String getResponseStream() {
		return responseStream;
	}

	public void setResponseStream(String responseStream) {
		this.responseStream = responseStream;
	}

	public int getReponseStatusCode() {
		return reponseStatusCode;
	}

	public void setReponseStatusCode(int reponseStatusCode) {
		this.reponseStatusCode = reponseStatusCode;
	}

	public String getReponseStatusReasonPhrase() {
		return reponseStatusReasonPhrase;
	}

	public void setReponseStatusReasonPhrase(String reponseStatusReasonPhrase) {
		this.reponseStatusReasonPhrase = reponseStatusReasonPhrase;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	}
