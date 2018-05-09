package de.networkchallenge.lambda.ddns.handler;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import de.networkchallenge.lambda.ddns.handler.util.RouteUpdater;
import de.networkchallenge.lambda.ddns.model.ResponseObject;
import spark.Request;
import spark.Response;

public class UrlControllerTest
{

    private HttpServletRequest httpServletRequest;
    private Request            request;
    private Response           response;

    @Before
    public void setUp()
    {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getRemoteAddr()).thenReturn("1.1.1.1");

        request = Mockito.mock(Request.class);
        Mockito.when(request.raw()).thenReturn(httpServletRequest);
        response = Mockito.mock(Response.class);
    }

    @Test
    public void handleUpdateRequestWithStatusOK()
    {
        RouteUpdater routeUpdater = getRouteUpdaterMock(ResponseObject.Status.OK);

        assertEquals(ResponseObject.Status.OK, UrlController
                .handleUpdateRequest(request, response, routeUpdater).getStatus());
    }

    @Test
    public void handleUpdateRequestWithStatusOtherThanOK()
    {
        RouteUpdater routeUpdater = getRouteUpdaterMock(ResponseObject.Status.URL_INVALID);

        assertEquals(ResponseObject.Status.URL_INVALID, UrlController
                .handleUpdateRequest(request, response, routeUpdater).getStatus());
        Mockito.verify(response, Mockito.times(1)).status(HttpStatus.SC_NOT_FOUND);
    }

    private RouteUpdater getRouteUpdaterMock(ResponseObject.Status status)
    {
        RouteUpdater routeUpdater = Mockito.mock(RouteUpdater.class);
        Mockito.when(routeUpdater.updateFqdnOfZoneWithIp(Matchers.anyString(), Matchers.anyString(),
                Matchers.anyString()))
                .thenReturn(status);
        return routeUpdater;
    }
}