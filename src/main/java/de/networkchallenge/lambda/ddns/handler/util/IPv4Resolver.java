package de.networkchallenge.lambda.ddns.handler.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Resolves the IPv4-Address of a {@link HttpServletRequest}
 */
public class IPv4Resolver
{
    public String fromRequest(HttpServletRequest request)
    {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress != null)
        {
            ipAddress = ipAddress.contains(",") ? ipAddress.split(",")[0] : ipAddress;
        }
        else
        {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
