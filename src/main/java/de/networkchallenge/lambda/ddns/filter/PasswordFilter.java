package de.networkchallenge.lambda.ddns.filter;

import static de.networkchallenge.lambda.ddns.handler.UrlController.HTTP_PARAM_DOMAIN;
import static spark.Spark.halt;

import spark.Filter;
import spark.Request;
import spark.Response;

public class PasswordFilter implements Filter
{
    private static final String HTTP_PARAM_PASSWORD = "password";
    private static final String ENV_SECRET          = "DDNS_SECRET";
    private static final String SECRET              = System.getenv(ENV_SECRET) == null ? "Test123"
            : System.getenv(ENV_SECRET);

    @Override
    public void handle(Request request, Response response)
    {
        String domain = request.queryParams(HTTP_PARAM_DOMAIN);
        String password = request.queryParams(HTTP_PARAM_PASSWORD);

        if (!SECRET.equals(password))
        {
            halt(401);
        }
    }
}
