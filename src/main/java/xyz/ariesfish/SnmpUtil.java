package xyz.ariesfish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class SnmpUtil {
    private static final Logger logger = LoggerFactory.getLogger(SnmpUtil.class);
    private static final int DEFAULT_VERSION = SnmpConstants.version1;
    private static final String DEFAULT_PROTOCOL = "udp";
    private static final int DEFAULT_PORT = 161;
    private static final long DEFAULT_TIMEOUT = 3000L;
    private static final int DEFAULT_RETRY = 2;

    private static CommunityTarget createTarget(String ip, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
                + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);

        return target;
    }

    private static boolean isWalkFinished(OID targetOID, PDU response, VariableBinding vb) {
        boolean finished = false;

        if (response.getErrorStatus() != PDU.noError) {
            logger.error("Check Walk: response error " + response.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            logger.error("Check Walk: gotten OID is null");
            finished = true;
        } else if (vb.getOid().size() < targetOID.size()) { // check new oid or not
            logger.error("Check Walk: gotten OID size is smaller than target");
            finished = true;
        } else if (targetOID.leftMostCompare(targetOID.size(), vb.getOid()) != 0) { // check child oid or not
            logger.error("Check Walk: gotten OID is not child of target");
            finished = true;
        } else if (vb.getOid().compareTo(targetOID) <= 0) {
            logger.error("Check Walk: gotten OID is not lexicographic successor of target");
            finished = true;
        } else if (Null.isExceptionSyntax(response.get(0).getVariable().getSyntax())) {
            logger.error("Check Walk: variable syntax exception");
            finished = true;
        }

        return finished;
    }

    private static PDU snmpGetEx(String ip, String community, String oid, int getType) {
        CommunityTarget target = createTarget(ip, community);
        Snmp snmp = null;
        PDU response = null;

        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            PDU request = new PDU();
            request.add(new VariableBinding(new OID(oid)));
            request.setType(getType);

            ResponseEvent resEvent = snmp.send(request, target);
            response = resEvent.getResponse();

            if (response == null) {
                logger.error("Failed to get response");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {
                }
            }
        }

        return response;
    }

    public static PDU snmpGet(String ip, String community, String oid) {
        return snmpGetEx(ip, community, oid, PDU.GET);
    }

    public static PDU snmpGetNext(String ip, String community, String oid) {
        return snmpGetEx(ip, community, oid, PDU.GETNEXT);
    }

    public static PDU snmpWalk(String ip, String community, String oid) {
        CommunityTarget target = createTarget(ip, community);
        Snmp snmp = null;
        PDU response = null;

        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            PDU request = new PDU();
            OID targetOID = new OID(oid);
            request.add(new VariableBinding(targetOID));

            boolean finish = false;
            int index = 0;

            while (!finish) {
                ResponseEvent resEvent = snmp.getNext(request, target);
                response = resEvent.getResponse();

                if (response != null) {
                    VariableBinding vb = response.get(index);
                    finish = isWalkFinished(targetOID, response, vb);
                    if (!finish) {
                        logger.info("Walk value: " + vb.getOid() + " = " + vb.getVariable());

                        // add new OID into current request for the next entry.
                        request.add(new VariableBinding(vb.getOid()));
                        index++;
                    } else {
                        response.remove(index);
                        snmp.close();
                    }
                } else {
                    logger.error("Failed to get response");
                    finish = true;
                }
            }
        } catch (Exception e) {
            logger.error("Snmp walk exception: " + e);
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {

                }
            }
        }

        return response;
    }
}
