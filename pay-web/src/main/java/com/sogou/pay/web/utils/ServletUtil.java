package com.sogou.pay.web.utils;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * User: Liwei
 * Date: 2015/1/16
 * Time: 16:18
 */
public class ServletUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServletUtil.class);

    public static boolean isPost(HttpServletRequest request) {
        return request.getMethod().toLowerCase().equals("post");
    }

	/* ------------------------- session ------------------------- */

    @SuppressWarnings("unchecked")
    public static <T> T getSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return null;
        return (T) session.getAttribute(name);
    }

    public static void setSession(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession(true);
        if (session == null) {
            logger.warn("create session failed.");
            return;
        }
        session.setAttribute(name, value);
    }

    public static void removeSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return;
        session.removeAttribute(name);
    }

	/* ------------------------- cookie ------------------------- */

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key))
                return cookie.getValue();
        }
        return null;
    }


    public static void setJsessionidCookie(HttpServletRequest request, HttpServletResponse response) {
        //specifying name and value of the cookie
        Cookie cookie = new Cookie("JSESSIONID", request.getSession(false).getId());
//		String domainName = request.getServerName(); //Gives www.xyz.com in our example
//		String domainNamePrefix = domainName.substring(domainName.indexOf("."), domainName.length()); //Returns .xyz.com
        //Specifies the domain within which this cookie should be presented.
        cookie.setDomain(defaultDomain);
        response.addCookie(cookie);
    }

    public static String defaultDomain = ".wan.sogou.com";

    public static void setCookie(HttpServletResponse response, String key, String value) {
        saveCookie(response, key, value, -1, "/");
    }

    public static void setCookie(HttpServletResponse response, String key, String value, String path) {
        saveCookie(response, key, value, -1, path);
    }

    public static void saveCookie(HttpServletResponse response, String key, String value, int second, String path) {
        saveCookie(response, key, value, second, path, defaultDomain);
    }

    public static void saveCookie(HttpServletResponse response, String key, String value, int second, String path,
                                  String domain) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second, String path) {
        clearCookie(response, key, second, path, defaultDomain);
    }

    public static void clearCookie(HttpServletResponse response, String key, int second, String path, String domain) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath(path);
        cookie.setMaxAge(second);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static long getCookieUid(HttpServletRequest request) {
        String uidStr = getCookie(request, "noregid");
        if (uidStr != null) {
            return Long.parseLong(uidStr);
        }
        return 0l;
    }

    public static String getRealIp(HttpServletRequest request) {
        String sff = request.getHeader("X-Forwarded-For");// 根据nginx的配置，获取相应的ip
        if (Strings.isNullOrEmpty(sff)) {
            sff = request.getHeader("X-Real-IP");
        }
        if (Strings.isNullOrEmpty(sff)) {
            return Strings.isNullOrEmpty(request.getRemoteAddr()) ? "" : request.getRemoteAddr();
        }
        String[] ips = sff.split(",");
        String realip = ips[0];
        return realip;
    }
}
