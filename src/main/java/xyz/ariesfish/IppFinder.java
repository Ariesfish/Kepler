package xyz.ariesfish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ariesfish.mdnssd.dns.Domain;
import xyz.ariesfish.mdnssd.sd.Instance;
import xyz.ariesfish.mdnssd.sd.Query;
import xyz.ariesfish.mdnssd.sd.Service;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

public class IppFinder {
    final static Logger logger = LoggerFactory.getLogger(IppFinder.class);

    public static void main(String[] args) {
        try {
            Service service = Service.fromName("_ipp._tcp");
            Query query = Query.createFor(service, Domain.LOCAL);
            Set<Instance> instances = query.runOnce();
            instances.stream().forEach(System.out::println);
        } catch (UnknownHostException e) {
            logger.error("Unknown host: ", e);
        } catch (IOException e) {
            logger.error("IO error: ", e);
        }
    }
}
