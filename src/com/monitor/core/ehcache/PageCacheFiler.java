package com.monitor.core.ehcache;

import com.monitor.core.tools.CommUtil;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.blocking.LockTimeoutException;
import net.sf.ehcache.constructs.web.AlreadyCommittedException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.filter.FilterNonReentrantException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;
import org.apache.commons.lang.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class PageCacheFiler extends SimplePageFragmentCachingFilter {
    @SuppressWarnings("unused")
    private static final String FILTER_URL_PATTERNS = "patterns";

    private static String[] cacheURLs;

    private void init() throws CacheException {
        String patterns = this.filterConfig.getInitParameter("patterns");
        patterns = "/bottom.htm";
        cacheURLs = StringUtils.split(patterns, ",");
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws AlreadyGzippedException, AlreadyCommittedException, FilterNonReentrantException,
            LockTimeoutException, Exception {
        if (cacheURLs == null) {
            init();
        }
        String url = request.getRequestURI();
        String include_url = CommUtil.null2String(request.getAttribute("javax.servlet.include.request_uri"));
        boolean flag = false;
        if ((cacheURLs != null) && (cacheURLs.length > 0)) {
            for (String cacheURL : cacheURLs) {
                if (include_url.trim().equals("")) {
                    if (!(url.contains(cacheURL.trim())))
                        continue;
                    flag = true;
                    break;
                }

                if (include_url.contains(cacheURL.trim()) && !include_url.contains("seller") && !include_url.contains("buyer")) {
                    flag = true;
                    break;
                }

            }

        }

        if (flag)
            super.doFilter(request, response, chain);
        else
            chain.doFilter(request, response);
    }

    @SuppressWarnings("rawtypes")
    private boolean headerContains(HttpServletRequest request, String header, String value) {
        logRequestHeaders(request);
        Enumeration accepted = request.getHeaders(header);
        while (accepted.hasMoreElements()) {
            String headerValue = (String) accepted.nextElement();
            if (headerValue.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    protected boolean acceptsGzipEncoding(HttpServletRequest request) {
        boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
        boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
        return ((acceptsEncoding(request, "gzip")) || (ie6) || (ie7));
    }

    @SuppressWarnings("unused")
    protected String calculateKey(HttpServletRequest httpRequest) {
        String url = httpRequest.getRequestURI();
        String include_url = CommUtil.null2String(httpRequest.getAttribute("javax.servlet.include.request_uri"));
        StringBuffer stringBuffer = new StringBuffer();
        if (include_url.equals("")) {
            stringBuffer.append(httpRequest.getRequestURI()).append(httpRequest.getQueryString());
            String key = stringBuffer.toString();
            return key;
        }
        stringBuffer.append(CommUtil.null2String(httpRequest.getAttribute("javax.servlet.include.request_uri")))
                .append(CommUtil.null2String(httpRequest.getAttribute("javax.servlet.include.query_string")));
        String key = stringBuffer.toString();
        return key;
    }
}