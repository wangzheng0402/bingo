/* Copyright c 2005-2012.
 * Licensed under GNU  LESSER General Public License, Version 3.
 * http://www.gnu.org/licenses
 */
package org.bingo.springmvc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CookieUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CookieUtils.class);

	private CookieUtils() {
	}

	public static String getCookieValue(Cookie cookie) {
		try {
			return URLDecoder.decode(cookie.getValue(), "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取cookie中的value<br>
	 * 自动负责解码<br>
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		try {
			Cookie cookie = getCookie(request, cookieName);
			if (null == cookie) {
				return null;
			} else {
				return URLDecoder.decode(cookie.getValue(), "utf-8");
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Convenience method to get a cookie by name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the cookie to find
	 * @return the cookie (if found), null if not found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;

		if (cookies == null) {
			return returnCookie;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];
			if (thisCookie.getName().equals(name) && StringUtils.isNotEmpty(thisCookie.getValue())) {
				returnCookie = thisCookie;
				break;
			}
		}
		return returnCookie;
	}

	/**
	 * Convenience method to set a cookie <br>
	 * 刚方法自动将value进行编码存储<br>
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param path
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int age) {
		LOG.debug("add cookie[name:{},value={},path={}]", name, value, path);
		Cookie cookie = null;
		try {
			cookie = new Cookie(name, URLEncoder.encode(value, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		cookie.setSecure(false);
		cookie.setPath(path);
		cookie.setMaxAge(age);
		response.addCookie(cookie);
	}

	/**
	 * 默认按照应用上下文进行设置
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @param age
	 * @throws Exception
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int age) {
		String contextPath = request.getContextPath();
		// if (!contextPath.endsWith("/")) {
		// contextPath += "/";
		// }
		addCookie(request, response, name, value, contextPath, age);
	}

	public static void deleteCookieByName(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];
			if (thisCookie.getName().equals(name)) {
				deleteCookie(response, thisCookie, request.getContextPath());
			}
		}
		deleteCookie(response, getCookie(request, name), request.getContextPath());
	}

	/**
	 * Convenience method for deleting a cookie by name
	 * 
	 * @param response
	 *            the current web response
	 * @param cookie
	 *            the cookie to delete
	 * @param path
	 *            the path on which the cookie was set (i.e. /appfuse)
	 */
	public static void deleteCookie(HttpServletResponse response, Cookie cookie, String contextPath) {
		if (cookie != null) {
			// Delete the cookie by setting its maximum age to zero
			cookie.setMaxAge(-1);
			cookie.setValue(null);
			 cookie.setPath(contextPath);
			response.addCookie(cookie);
		}
	}
}
