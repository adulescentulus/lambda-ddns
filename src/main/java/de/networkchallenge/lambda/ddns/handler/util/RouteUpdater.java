package de.networkchallenge.lambda.ddns.handler.util;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.*;

import de.networkchallenge.lambda.ddns.model.ResponseObject;

/**
 * Encapsulates all requests to the {@link AmazonRoute53}-Client
 */
public class RouteUpdater
{
    private static final Logger LOG = LoggerFactory.getLogger(RouteUpdater.class);

    public RouteUpdater()
    {

    }

    /**
     * Updates a Route53 managed Hosted Zone and sets the specified fully qualified domain name (within the
     * zone) to one ip
     * 
     * @param fqdn
     *            the fqdn to update
     * @param ip
     *            the ipv4 address of the dns entry
     * @param hostedZone
     *            the zone which manages the fqdn
     * @return a {@link de.networkchallenge.lambda.ddns.model.ResponseObject.Status} to be used in the
     *         {@link ResponseObject}<br>
     *         <br>
     *         If all calls are successful
     *         {@link de.networkchallenge.lambda.ddns.model.ResponseObject.Status#OK} is returned
     */
    public ResponseObject.Status updateFqdnOfZoneWithIp(String fqdn, String ip, String hostedZone)
    {
        Objects.requireNonNull(fqdn, "fqdn must not be null");
        Objects.requireNonNull(ip, "ip must not be null");
        Objects.requireNonNull(hostedZone, "hostedZone must not be null");

        AmazonRoute53 route53 = AmazonRoute53ClientBuilder.defaultClient();
        ResponseObject.Status status;
        try
        {
            LOG.debug("starting");
            if (LOG.isDebugEnabled())
            {
                route53.listHostedZones().getHostedZones()
                        .forEach(zone -> LOG.debug(zone.getName() + " - " + zone.getId()));
            }

            HostedZone ddnsZone = route53.listHostedZones().getHostedZones().stream()
                    .filter(zone -> zone.getName().equals(hostedZone))
                    .findFirst().orElseThrow(UnknownHostException::new);
            LOG.debug("found HostedZone");
            ResourceRecordSet urlRecordSet = route53
                    .listResourceRecordSets(
                            new ListResourceRecordSetsRequest().withHostedZoneId(ddnsZone.getId()))
                    .getResourceRecordSets().stream()
                    .filter(resourceRecordSet -> (fqdn + ".").equals(resourceRecordSet.getName())
                            && RRType.A.toString().equals(resourceRecordSet.getType()))
                    .findFirst().orElse(new ResourceRecordSet(fqdn + ".", RRType.A));
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
