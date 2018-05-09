package de.networkchallenge.lambda.ddns;

import static spark.Spark.before;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spark.SparkLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import de.networkchallenge.lambda.ddns.handler.UrlController;
import de.networkchallenge.lambda.ddns.handler.VersionController;
import de.networkchallenge.lambda.ddns.handler.util.RouteUpdater;
import spark.Spark;

/**
 * MainHandler for all API-Gateway proxy requests
 */
public class MainHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse>
{

    private SparkLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler =
            SparkLambdaContainerHandler.getAwsProxyHandler();
    private boolean initialized = false;

    /**
     * Lambda needs this to be public!
     *
     * @throws ContainerInitializationException
     */
    public MainHandler() throws ContainerInitializationException {
    }

    public static void main(String... args) {
        initSpark();
    }

    private static void initSpark() {
        defineRoutes();
        Spark.awaitInitialization();
    }

    private static void defineRoutes() {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            // Note: this may or may not be necessary in your particular application
            response.type("application/json");
        });
        new UrlController(new RouteUpdater());
        new VersionController();
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context)
    {
        if (!initialized)
        {
            initialized = true;
            initSpark();
        }
        return handler.proxy(awsProxyRequest, context);
    }
}
