package de.networkchallenge.lambda.ddns.handler.util;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class IPv4ResolverTest
{

    private final String       DEFAULT_IP = "1.1.1.1";
    private HttpServletRequest request;

    @Before
    public void setUp()
    {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void fromRequestWithRemoteAddrMatchesRemoteAddr()
    {
        Mockito.when(request.getRemoteAddr()).thenReturn(DEFAULT_IP);
        Assert.assertEquals(DEFAULT_IP, new IPv4Resolver().fromRequest(request));
    }

    @Test
    public void fromRequestWithProxyMatchesProxyHeader()
    {
        Mockito.when(request.getRemoteAddr()).thenReturn("99.99.99.99");
        Mockito.when(request.getHeader("X-FORWARDED-FOR")).thenReturn(DEFAULT_IP);
        Assert.assertNotEquals(DEFAULT_IP, request.getRemoteAddr());
        Assert.assertEquals(DEFAULT_IP, new IPv4Resolver().fromRequest(request));
    }

    @Test
    public void fromRequestWithProxyCascadeMatchesFirstProxyHeader()
    {
        Mockito.when(request.getRemoteAddr()).thenReturn("99.99.99.99");
        Mockito.when(request.getHeader("X-FORWARDED-FOR")).thenReturn(DEFAULT_IP + ", 88.88.8.8, 1.1.1.2");
        Assert.assertNotEquals(DEFAULT_IP, request.getRemoteAddr());
        Assert.assertEquals(DEFAULT_IP, new IPv4Resolver().fromRequest(request));
    }
}