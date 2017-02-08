package autofinanceschedule.base;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.ConnectionReuseStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

public class RetrieveWeb {

	/**
	 * 
	 * @param address
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL GetURL(String address) throws MalformedURLException{
		
		URL url = new URL(address);
		
		return url;
	}
	
	/**
	 * 
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public static URLConnection GetURLConnection(String address) throws IOException{
		
		URL url = new URL(address);
		
		URLConnection connection = url.openConnection();
		
		return connection;
	}
	
	/**
	 * get the whole page by the input address
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public static String GetWholePage(String address) throws IOException{
		
		URL url = new URL(address);	
		
		//Request.Get(address).execute().saveContent(new File("/Users/lihongbo/Desktop/123.html"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		
		String line = null;
		
		StringBuffer hcontent = new StringBuffer();
		
		while ((line = br.readLine()) != null) {
			
			if(!line.trim().equals("")) {
				
				hcontent.append(line + "\n");
				
			}
		}
		br.close();
		
		/*
		InputStream inputs = new BufferedInputStream(Request.Get(address).execute().returnContent().asStream());
		
		FileOutputStream out = new FileOutputStream("/Users/lihongbo/Desktop/123.html");
		
		//byte[] inputcontent = new byte[inputs.available()];
		
		byte[] inputcontent = new byte[20480];
		
		int count = 0;
		
		while((count = inputs.read(inputcontent, 0, 20480)) != -1) {
			
			out.write(inputcontent, 0, count);

		}
		
		out.flush();
		
		out.close();
		
		inputs.close();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/lihongbo/Desktop/123.html")));
		
		String line = null;
		
		String page = null;
		
		StringBuffer hcontent = new StringBuffer();
		
		while ((line = br.readLine()) != null) {
			if(!line.trim().equals("")) {
				hcontent.append(line + "\n");
				
				page = page + line + "\n";
				
			}
		}
		
		br.close();
		
		int ccount = page.length();
		
		*/
		
		return hcontent.toString();
		
	}
	
	/**
	 * to get the object page using the http-client Lib
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public static String GetPage(String address) throws IOException {
		
		HttpResponse response = Request.Get(address).execute().returnResponse();
		
		int statuscode = response.getStatusLine().getStatusCode();
		
		String page = Integer.toString(statuscode);
		
		if (statuscode == 200 || statuscode == 400) {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			
			String line = null;
			
			StringBuffer hcontent = new StringBuffer();
			
			while ((line = br.readLine()) != null) {
				
				if(!line.trim().equals("")) {
					
					hcontent.append(line + "\n");
					
				}
			}
			
			br.close();
			
			page = hcontent.toString();
			
		}
			
		return page;
		
	} 
	
	public static String GetPage(String address, BasicCookieStore cookie) throws IOException {
		
		HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookie).build();
		 
	    final HttpGet request = new HttpGet(address);
	 
	    Builder config = RequestConfig.custom().setConnectTimeout(150000);
	    
	    request.setConfig(config.build());
	    
	    HttpResponse response = client.execute(request);
		
		int statuscode = response.getStatusLine().getStatusCode();
		
		String page = "";
		
		if (statuscode == 200 || statuscode == 400) {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			
			String line = null;
			
			StringBuffer hcontent = new StringBuffer();
			
			while ((line = br.readLine()) != null) {
				
				if(!line.trim().equals("")) {
					
					hcontent.append(line + "\n");
					
				}
			}
			
			br.close();
			
			page = hcontent.toString();
			
		}
			
		return page;
		
	} 
	
	public RetrieveWeb() {
		// TODO Auto-generated constructor stub
	}
	
	public static String GetWordPage(String server, int port, String target) throws IOException, HttpException {
		
		String page = null;
		
		HttpProcessor httpproc = HttpProcessorBuilder.create()
	            .add(new RequestContent())
	            .add(new RequestTargetHost())
	            .add(new RequestConnControl())
	            .add(new RequestUserAgent("Test/1.1"))
	            .add(new RequestExpectContinue(true)).build();
		
		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

        HttpCoreContext coreContext = HttpCoreContext.create();
        HttpHost host = new HttpHost(server, port);
        coreContext.setTargetHost(host);

        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
        
        ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

        try {

            if (!conn.isOpen()) {
            	
            	Socket socket = new Socket(host.getHostName(), host.getPort());
            	
                conn.bind(socket);
                }

            BasicHttpRequest request = new BasicHttpRequest("GET", target);
                
            System.out.println(">> Request URI: " + request.getRequestLine().getUri());

                
            httpexecutor.preProcess(request, httpproc, coreContext);
                
            HttpResponse response = httpexecutor.execute(request, conn, coreContext);
                
            httpexecutor.postProcess(response, httpproc, coreContext);

                
            page = EntityUtils.toString(response.getEntity());

                
            if (!connStrategy.keepAlive(response, coreContext)) {
                    
            	conn.close();
                
            } else {
                    
            	System.out.println("Connection kept alive...");
                
            }
            
        } finally {
            conn.close();
        }
        
		return page;		
	        
	}
	
	public static String GetWordContent(String address) throws Exception, IOException{
		
		String page = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
        try {
        	
            HttpGet httpget = new HttpGet(address);

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                	
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            
            page = httpclient.execute(httpget, responseHandler);

        } finally {
            httpclient.close();
        }
		
		return page;
	}

}
