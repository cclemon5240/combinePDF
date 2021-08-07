package test;

import org.apache.http.cookie.Cookie;

public class HtmlStructure {

	private String htmlContent;
	
	private Cookie customCookie;

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public Cookie getCustomCookie() {
		return customCookie;
	}

	public void setCustomCookie(Cookie customCookie) {
		this.customCookie = customCookie;
	}
}
