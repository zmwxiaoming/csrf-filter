# About #

Cross-site request forgery (csrf) - is a type of malicious exploit of a website whereby unauthorized commands are transmitted from a user that the website trusts.

There are several types of how to prevent such attacks:
  * check http <a href='http://en.wikipedia.org/wiki/HTTP_referer'>referer header</a>.
  * use token

# Code requirements #

csrf-filter uses token approach. It automatically handles validation and setup of such tokens. It introduces the following restrictions:
  * it validates only POST requests. State of data should be changed only using POST methods
  * it adds attribute, which should be added to all POST requests (could be AJAX or plain form submit)

# Features #

  * token generated on every GET request and added into cookie
  * it is stateless
  * if cookie not found or request parameter not found or values are mismatched then Http 400 status will be sent
  * token name could be configured. This name will be used as parameter name, cookie name and attribute name
  * it adds attribute into every HttpServletRequest.

# Compare with others #

There is another csrf filter: https://github.com/esheri3/OWASP-CSRFGuard
Here is why this filter is better:
  * it is simple. Really simple. No need to read <a href='https://www.owasp.org/index.php/CSRFGuard_3_Configuration'>100+ lines</a> on how to configure it
  * it is stateles or no memory overhead on sessions. Since CSRFGuard always stores token in sesssion it automatically increases overall memory usage. More clients more memory.
  * it is single ~75 lines file. CSRFGuard has its own logging, own abstract token generation factory. It has more than 17 files. More files more chances to have bug in them.
  * this filter has maven repository with sources and checksums

# HowTo #

  * Configure web.xml:
```
	<filter>
		<filter-name>csrfFilter</filter-name>
		<filter-class>com.google.code.csrf.StatelessCookieFilter</filter-class>
		<init-param> 
	        	<param-name>csrfTokenName</param-name> 
	        	<param-value>csrf</param-value> 
	        </init-param>
		<init-param>
                        <!-- optional. urls to exclude from check -->
	        	<param-name>exclude</param-name> 
	        	<param-value>/url1,/url/url2</param-value> 
	        </init-param>
		<init-param>
                        <!-- optional. urls to exclude from generating csrf cookie. Useful for ajax requests that do not contain forms -->
	        	<param-name>excludeGET</param-name> 
	        	<param-value>/url3,/url/url4</param-value> 
	        </init-param>
		<init-param>
                        <!-- optional. urls to exclude from generating csrf cookie. Exclude do check servletPath().startsWith() -->
	        	<param-name>excludeGETStartWith</param-name> 
	        	<param-value>/js/,/css/,/img/</param-value> 
	        </init-param>
		<init-param>
                        <!-- optional. cookieMaxAge. By default 3600 seconds -->
	        	<param-name>cookieMaxAge</param-name> 
	        	<param-value>18000</param-value> 
	        </init-param>
	</filter>
	<filter-mapping>
		<filter-name>csrfFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```

  * Add to every POST request parameter "csrf". For example form.jsp:
```
	<form method="POST">
		<input type="hidden" name="csrf" value="${csrf}">
	</form>
```

  * For "multipart/form-data" requests add to "action" attribute:
```
	<form action="/url?csrf=${csrf}" method="POST" enctype="multipart/form-data">
		<input type="file" name="file" size="50"/>
	</form>
```


# Installation #

  * Add maven repository:
```
	<repositories>
		<repository>
			<id>csrf-repo</id>
			<url>http://csrf-filter.googlecode.com/svn/maven/</url>
		</repository>
	</repositories>
```
  * Add dependency:
```
	<dependency>
		<groupId>com.google.code</groupId>
		<artifactId>simple-csrf-filter</artifactId>
		<version>2.6</version>		
	</dependency>
```
