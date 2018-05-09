package de.networkchallenge.lambda.ddns.handler;

import static spark.Spark.before;
import static spark.Spark.get;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.networkchallenge.lambda.ddns.filter.DomainFilter;
import de.networkchallenge.lambda.ddns.filter.PasswordFilter;
import de.networkchallenge.lambda.ddns.handler.util.IPv4Resolver;
import de.networkchallenge.lambda.ddns.handler.util.RouteUpdater;
import de.networkchallenge.lambda.ddns.model.ResponseObject;
import spark.Request;
import spark.Response;

public class UrlController
{
    public static final String  HTTP_PARAM_DOMAIN = "domain";
    private static final Logger LOG               = LoggerFactory.getLogger(UrlController.class);
    private static final String HOSTED_ZONE       = "ddns.networkchallenge.de.";

    public UrlController(RouteUpdater ru)
    {
        LOG.debug("constructor");
        Gson gson = new Gson();
        before(new PasswordFilter(), new DomainFilter());
        get("/update", (request, response) -> handleUpdateRequest(request, response, ru), gson::toJson);
    }

    public static ResponseObject handleUpdateRequest(Request request, Response response, RouteUpdater ru)
    {
        String domain = request.queryParams(HTTP_PARAM_DOMAIN);

        ResponseObject.Status status;
        String sourceIp = new IPv4Resolver().fromRequest(request.raw());
        LOG.debug("update with ip:" + sourceIp);
        status = ru.updateFqdnOfZoneWithIp(domain, sourceIp, HOSTED_ZONE);
        if (!ResponseObject.Status.OK.equals(status))
            response.status(HttpStatus.SC_NOT_FOUND);
        return new ResponseObject().setStatus(status);
    }

}
