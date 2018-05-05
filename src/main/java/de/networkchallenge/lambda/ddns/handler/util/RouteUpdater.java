package de.networkchallenge.lambda.ddns.handler.util;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.*;

import de.networkchallenge.lambda.ddns.model.ResponseObject;

public class RouteUpdater
{
    private static final Logger LOG = LoggerFactory.getLogger(RouteUpdater.class);
    private String              url;
    private String              ip;
    private String              hostedZone;

    public RouteUpdater(String url, String ip, String hostedZone)
    {
        this.url = url;
        this.ip = ip;
        this.hostedZone = hostedZone;
    }

    public ResponseObject.Status invoke()
    {
        AmazonRoute53 route53 = AmazonRoute53ClientBuilder.defaultClient();
        ResponseObject.Status status;
        try
        {
            LOG.debug("starting");
            if (LOG.isDebugEnabled())
            {
                route53.listHostedZones().getHostedZones()
                        .forEach(hostedZone -> LOG.debug(hostedZone.getName() + " - " + hostedZone.getId()));
            }

            HostedZone ddnsZone = route53.listHostedZones().getHostedZones().stream()
                    .filter(hostedZone -> hostedZone.getName().equals(this.hostedZone))
                    .findFirst().orElseThrow(UnknownHostException::new);
            LOG.debug("found HostedZone");
            ResourceRecordSet urlRecordSet = route53
                    .listResourceRecordSets(
                            new ListResourceRecordSetsRequest().withHostedZoneId(ddnsZone.getId()))
                    .getResourceRecordSets().stream()
                    .filter(resourceRecordSet -> (url + ".").equals(resourceRecordSet.getName())
                            && RRType.A.toString().equals(resourceRecordSet.getType()))
                    .findFirst().orElse(new ResourceRecordSet(url + ".", RRType.A));
            LOG.debug("found ResourceRecordSet");
            route53.changeResourceRecordSets(new ChangeResourceRecordSetsRequest(ddnsZone.getId(),
                    new ChangeBatch(Arrays.asList(
                            new Change(ChangeAction.UPSERT, urlRecordSet
                                    .withResourceRecords(Arrays.asList(new ResourceRecord(ip)))
                                    .withTTL(300L))))));
            LOG.debug("executed ChangeResourceRecordSetsRequest");
            status = ResponseObject.Status.OK;
        }
        catch (UnknownHostException e)
        {
            status = ResponseObject.Status.URL_INVALID;
        }
        return status;
    }
}
