package webServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class WebpageRequest implements Callable<Document> {
	
	private String URL;
	
	/**
	 *  Modifies: URL
	 *  Effects: scrapes page given by url
	 * @param url
	 */
	public void setURL(String url){
		URL = url;
	}
	
	/**
	 *  Modifies: Nothing
	 *  Requires: URL to be given before call
	 *  Effects: Finds and scrapes web page
	 * @throws InterruptedException 
	 */
	public Document call() throws IOException {
		for (int i=1; i <= 10; i++) {
			try {
				return Jsoup.connect(URL).maxBodySize(0) // no data size limit on document body size
						.timeout(i*1000).get(); // timeout set to i*1s. after which an exception is thrown
			}
			catch (Exception e) {
				continue;
			}
		}
		return Jsoup.connect(URL).maxBodySize(0) // no data size limit on document body size
					.timeout(15000).get(); // timeout set to 15s. after which an exception is thrown
	}
}

