package de.networkchallenge.lambda.ddns.filter;

import static de.networkchallenge.lambda.ddns.handler.UrlController.HTTP_PARAM_DOMAIN;
import static spark.Spark.halt;

import org.apache.http.HttpStatus;

import spark.Filter;
import spark.Request;
import spark.Response;

public class DomainFilter implements Filter
{

    @Override
    public void handle(Request request, Response response)
    {
        String domain = request.queryParams(HTTP_PARAM_DOMAIN);

        if (domain == null || domain.trim().length() == 0)
        {
            halt(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
