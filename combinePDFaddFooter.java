package test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
/*
https://codertw.com/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80/547722/
https://blog.csdn.net/z446981439/article/details/103994221
https://vimsky.com/zh-tw/examples/detail/java-class-com.itextpdf.text.pdf.ColumnText.html

skip ssl verify
https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/

iText Doc
https://api.itextpdf.com/iText5/java/5.5.13.2/
*/
public class combinePDFaddFooter {

	
	public static void main(String[] args) {
		System.out.println("Start!!!");
		String[] files = {"/Users/ricktseng/a.pdf", "/Users/ricktseng/createSamplePDF2.pdf"};
		final String urlPath1 = "https://cathaybk.moneydj.com/w/CustFundIDMap.djhtm?FUNDID=0001B011&DownFile=8";
		final String fmt = "UTF-8";
		try {
			//第一次詢問(GET)
			HtmlStructure gethtmlStructure1 = getHTMLbyGet(urlPath1, fmt);
			String urlPath2 = gethtmlStructure1.getHtmlContent().substring(gethtmlStructure1.getHtmlContent().indexOf("'") + 1, gethtmlStructure1.getHtmlContent().lastIndexOf("'")); 
			//第二次詢問(GET)
			HtmlStructure gethtmlStructure = getHTMLbyGet(urlPath2, fmt);
			
			//取得回應form的參數
			String[] formArr = gethtmlStructure.getHtmlContent().split("<");
			String urlPath3 = "https://" + gethtmlStructure.getCustomCookie().getDomain() + "/";
			List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
			String key = "";
			String val = "";
			for(int i = 1 ;i < formArr.length; i ++) {
				String[] inputArr = formArr[i].split("\\s");
				if(i == 1) {
					for(String input : inputArr) {
						if(input.startsWith("action=")) {
							urlPath3 = urlPath3.concat(input.substring(8, input.length() - 1));
						}
					}
				} else {
					for(String input : inputArr) {
						if(input.startsWith("id=")) {
							key = input.substring(4, input.length()-1);
						} else if(input.startsWith("value=")){
							val = input.substring(6);
						}
					}
					parameters.add(new BasicNameValuePair(key, val));
				}
			}
			
			//Cookie設定
			BasicCookieStore cookieStore = new BasicCookieStore();
		    BasicClientCookie cookie = new BasicClientCookie(gethtmlStructure.getCustomCookie().getName(),gethtmlStructure.getCustomCookie().getValue());
		    cookie.setDomain(gethtmlStructure.getCustomCookie().getDomain());
		    cookie.setPath("/");
		    cookieStore.addCookie(cookie);
			//第三次詢問(POST)
			HttpPost httpPost = new HttpPost(urlPath3);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			httpPost.addHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
			httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
			byte[] backEndLoadPdf = null;
	        try (PoolingHttpClientConnectionManager connManager = ConnectionManagerBuilder();
	        		CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).setDefaultCookieStore(cookieStore).build();
	        		CloseableHttpResponse response = httpclient.execute(httpPost);) {
	            if (response.getStatusLine().getStatusCode() == 200) {
	                BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
	                backEndLoadPdf = bis.readAllBytes();
	                bis.close();
	            }
	        }
			
	        //將兩個PDF combine
			Document document = new Document();
			PdfReader readUrl = new PdfReader(backEndLoadPdf);
			PdfCopy copy = new PdfSmartCopy(document, new FileOutputStream("/Users/ricktseng/combinePDF.pdf"));
			PdfImportedPage page;
			PdfCopy.PageStamp stamp;
			document.open();
			PdfReader[] reader = new PdfReader[3];
			copy.addDocument(readUrl);
			copy.freeReader(readUrl);
			for (int i = 0; i < files.length; i++) {
	            reader[i] = new PdfReader(new FileInputStream(files[i]));
	            copy.addDocument(reader[i]);
	            copy.freeReader(reader[i]);
	            reader[i].close();
	        }
			readUrl.close();
			document.close();
			//將PDF加上footer
			PdfReader reader2 = new PdfReader(new FileInputStream("/Users/ricktseng/combinePDF.pdf"));
			copy = new PdfSmartCopy(document, new FileOutputStream("/Users/ricktseng/combinePDFaddFooter.pdf"));
			document.open();
			int pageNum = reader2.getNumberOfPages();
			for (int i = 1; i <= pageNum; i ++) {
				page = copy.getImportedPage(reader2, i);
				stamp = copy.createPageStamp(page);
				ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_CENTER,new Phrase(String.format("page %d of %d", i, pageNum)),297.5f, 28, 0);
				//Header  
				float x = document.top(-10);
				ColumnText.showTextAligned(stamp.getUnderContent(), Element.ALIGN_RIGHT,new Phrase("202107/1008"),document.right(), x, 0);
				stamp.alterContents();
				copy.addPage(page);
			}
			reader2.close();
			document.close();
			System.out.println("Finish!!!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
     * 抓網頁資料
     * @param urlPath
     * @param fmt
     * @param cookie
     * @return
     * @throws IOException
     */
    public static HtmlStructure getHTMLbyGet(String urlPath, String fmt) throws IOException{
    	HttpGet httpGet = new HttpGet(urlPath);
    	HttpClientContext context = HttpClientContext.create();
    	HtmlStructure htmlScHtmlStructure = new HtmlStructure();
    	try(PoolingHttpClientConnectionManager connManager = ConnectionManagerBuilder();
    			CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connManager).build();
    			CloseableHttpResponse response = httpclient.execute(httpGet,context);) {
    		if(!context.getCookieStore().getCookies().isEmpty()) {
    			CookieStore cookieStore = context.getCookieStore();
    			Cookie customCookie = cookieStore.getCookies()
    					.stream()
    					.findFirst()
    					.orElseThrow(IllegalStateException::new);
    			htmlScHtmlStructure.setCustomCookie(customCookie);    			
    		}
            
            if (response.getStatusLine().getStatusCode() == 200) {
                htmlScHtmlStructure.setHtmlContent(EntityUtils.toString(response.getEntity(), fmt));
            }
        } catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return htmlScHtmlStructure;
    }
    
    /**
     * 跳過憑證驗證
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static PoolingHttpClientConnectionManager ConnectionManagerBuilder() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] { trustManager }, null);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return connManager;
    }
}
