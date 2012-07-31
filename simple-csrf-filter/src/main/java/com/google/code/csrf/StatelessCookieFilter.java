package com.google.code.csrf;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatelessCookieFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(StatelessCookieFilter.class);

	private String csrfTokenName;
	private Random random;

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpResp = (HttpServletResponse) resp;
		if (!httpReq.getMethod().equals("POST")) {
			String token = Long.toString(random.nextLong(), 36);
			LOG.debug("new csrf token generated: {}", token);
			httpReq.setAttribute(csrfTokenName, token);
			httpResp.addCookie(new Cookie(csrfTokenName, token));
			chain.doFilter(req, resp);
			return;
		}

		String csrfToken = httpReq.getParameter(csrfTokenName);
		if (csrfToken == null) {
			LOG.error("csrf token not found in POST request: {}", httpReq);
			httpResp.sendError(400);
			return;
		}

		for (Cookie curCookie : httpReq.getCookies()) {
			if (curCookie.getName().equals(csrfTokenName)) {
				if (curCookie.getValue().equals(csrfToken)) {
					chain.doFilter(req, resp);
					return;
				} else {
					LOG.error("mismatched csrf token. expected: {} received: {}", csrfToken, curCookie.getValue());
					httpResp.sendError(400);
					return;
				}
			}
		}
		
		LOG.error("csrf cookie not found");
		httpResp.sendError(400);
	}

	public void destroy() {
		// do nothing
	}

	public void init(FilterConfig config) throws ServletException {
		String value = config.getInitParameter("csrfTokenName");
		if (value == null || value.trim().length() == 0) {
			throw new ServletException("csrfTokenName parameter should be specified");
		}
		csrfTokenName = value;
		random = new SecureRandom();
	}

}
