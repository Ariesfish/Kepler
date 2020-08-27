package xyz.ariesfish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import xyz.ariesfish.mdnssd.dns.Domain;
import xyz.ariesfish.mdnssd.sd.Instance;
import xyz.ariesfish.mdnssd.sd.Query;
import xyz.ariesfish.mdnssd.sd.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

public class IppFinder {
    //final static Logger logger = LoggerFactory.getLogger(IppFinder.class);

    public static void main(String[] args) {
//        try {
//            Service service = Service.fromName("_ipp._tcp");
//            Query query = Query.createFor(service, Domain.LOCAL);
//            Set<Instance> instances = query.runOnce();
//            instances.stream().forEach(System.out::println);
//        } catch (UnknownHostException e) {
//            logger.error("Unknown host: ", e);
//        } catch (IOException e) {
//            logger.error("IO error: ", e);
//        }

        String ip = "172.16.9.82";
        String community = "public";

        String oidPrinterName = "1.3.6.1.2.1.43.5.1.1.16.1";    // Get, String
        String oidSerialNumber = "1.3.6.1.2.1.43.5.1.1.17.1";   // Get, String

        String oidInputTrayType = "1.3.6.1.2.1.43.8.2.1.2.1";   // Walk, Integer
        String oidInputTrayMaxCap = "1.3.6.1.2.1.43.8.2.1.9.1"; // Walk, Integer
        String oidInputTrayLevel = "1.3.6.1.2.1.43.8.2.1.10.1"; // Walk, Integer
        String oidInputTrayName = "1.3.6.1.2.1.43.8.2.1.13.1";  // Walk, String

        String oidCountLife = "1.3.6.1.2.1.43.10.2.1.4";        // GetNext, Integer
        String oidCountPowerOn = "1.3.6.1.2.1.43.10.2.1.5";     // GetNext, Integer

        String oidTonerType = "1.3.6.1.2.1.43.11.1.1.5.1";      // Walk, Integer
        String oidTonerDesc = "1.3.6.1.2.1.43.11.1.1.6.1";      // Walk, String
        String oidTonerMaxCap = "1.3.6.1.2.1.43.11.1.1.8.1";    // Walk, Integer
        String oidTonerLevel = "1.3.6.1.2.1.43.11.1.1.9.1";     // Walk, Integer

        String oidAlertCode = "1.3.6.1.2.1.43.18.1.1.7.1";      // Walk, Integer
        String oidAlertDesc = "1.3.6.1.2.1.43.18.1.1.8.1";      // Walk, String

        String oidPrinterStatus = "1.3.6.1.2.1.25.3.5.1.1";     // GetNext, Integer
        String oidDeviceStatus = "1.3.6.1.2.1.25.3.2.1.5";      // GetNext, Integer

        String oidProductName = "1.3.6.1.4.1.1602.1.1.1.1.0";   // Get, String
        String oidPaperSize = "1.3.6.1.4.1.1602.1.4.64.1.1.1";  // Walk, Integer

        String oidPrintCountType = "1.3.6.1.4.1.1602.1.11.1.3.1.2"; // Walk, Integer
        String oidPrintCountDesc = "1.3.6.1.4.1.1602.1.11.2.1.1.2"; // Walk, String
        String oidPrintCountValue = "1.3.6.1.4.1.1602.1.11.2.1.1.3";// Walk, Integer

        String oidWakeUp = "1.3.6.1.4.1.1602.1.5.1.1.2.1.0"; // Set, Integer
        

        PDU response = SnmpUtil.snmpGet(ip, community, oidAlertDesc);
        if (response != null) {
            for (int i = 0; i < response.size(); i++) {
                System.out.println("OID: " + response.get(i).getOid() + ", Value: " + response.get(i).getVariable().toString());
            }
        }
    }
}
