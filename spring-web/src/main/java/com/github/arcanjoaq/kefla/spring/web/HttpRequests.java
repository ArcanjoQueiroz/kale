package com.github.arcanjoaq.kefla.spring.web;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.google.common.base.Preconditions;

public class HttpRequests {

  private HttpRequests() { }

  public static String getClientIpAddress() {
    final var optional = getServletRequest();
    return optional.isPresent() ? getClientIpAddress(optional.get()) : null;
  }
  
  public static String getClientIpAddress(
      final HttpServletRequest request) {
    Preconditions.checkArgument(request != null);
    var ip = request.getHeader("X-Forwarded-For");  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("Proxy-Client-IP");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("WL-Proxy-Client-IP");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_X_FORWARDED");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_CLIENT_IP");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_FORWARDED_FOR");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_FORWARDED");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("HTTP_VIA");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getHeader("REMOTE_ADDR");  
    }  
    if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {  
      ip = request.getRemoteAddr();  
    }  
    return ip;  
  }

  private static Optional<HttpServletRequest> getServletRequest() {
    try {
      final HttpServletRequest httpServletRequest = ((ServletRequestAttributes)
          RequestContextHolder.getRequestAttributes()).getRequest();
      return Optional.ofNullable(httpServletRequest);
    } catch (final Exception e) {
      return Optional.empty();
    }
  }
}
