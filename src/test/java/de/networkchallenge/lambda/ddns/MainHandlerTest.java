package de.networkchallenge.lambda.ddns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.HttpMethod;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Unit test for simple MainHandler.
 */
public class MainHandlerTest
{

    @Test
    public void withoutPassword() throws ContainerInitializationException
    {

        final MainHandler mainHandler = new MainHandler();
        Context contextMock = Mockito.mock(Context.class);
        Mockito.when(contextMock.getLogger()).thenReturn(Mockito.mock(LambdaLogger.class));
        final AwsProxyResponse response = mainHandler.handleRequest(
                new AwsProxyRequestBuilder().path("/update").method(HttpMethod.GET).build(),
                new MockLambdaContext());
        assertNotNull("Repsonse must not be null", response);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void withPasswordWithoutDomain() throws ContainerInitializationException
    {

        final MainHandler mainHandler = new MainHandler();
        Context contextMock = Mockito.mock(Context.class);
        Mockito.when(contextMock.getLogger()).thenReturn(Mockito.mock(LambdaLogger.class));
        final AwsProxyResponse response = mainHandler.handleRequest(new AwsProxyRequestBuilder()
                .path("/update").queryString("password", "Test123").method(HttpMethod.GET).build(),
                new MockLambdaContext());
        assertNotNull("Repsonse must not be null", response);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void withoutPasswordWithDomain() throws ContainerInitializationException
    {

        final MainHandler mainHandler = new MainHandler();
        Context contextMock = Mockito.mock(Context.class);
        Mockito.when(contextMock.getLogger()).thenReturn(Mockito.mock(LambdaLogger.class));
        final AwsProxyResponse response = mainHandler.handleRequest(new AwsProxyRequestBuilder()
                .path("/update").queryString("domain", "bla").method(HttpMethod.GET).build(),
                new MockLambdaContext());
        assertNotNull("Repsonse must not be null", response);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusCode());
    }

}
