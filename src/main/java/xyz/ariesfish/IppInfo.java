package xyz.ariesfish;

import com.hp.jipp.encoding.Attribute;
import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.trans.IppPacketData;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.hp.jipp.model.Types.requestedAttributes;

public class IppInfo {

    private final static Options options;
    static {
        Option fileOption = new Option("p", "print-job", true, "print a file");
        fileOption.setArgName("FILE");

        OptionGroup requiredOptions = new OptionGroup()
                .addOption(fileOption)
                .addOption(new Option("a", "get-attributes", false, "get printer attributes"));
        requiredOptions.setRequired(true);
        Option mimeTypeOption = new Option("m", "mime-type", true, "specify mime type");
        mimeTypeOption.setArgName("MIME");

        options = new Options()
                .addOption("h", "help", false, "show help")
                .addOptionGroup(requiredOptions)
                .addOption(mimeTypeOption);
    }

    public static void main(String[] args) throws IOException {

        try {
            CommandLineParser parser = new BasicParser();
            CommandLine command = parser.parse(options, args);

            if (command.hasOption("h")) {
                help();
            }

            List<String> argList = command.getArgList();
            if (argList.size() != 1) {
                throw new ParseException("Must supply a single PRINTER_URL");
            }
            URI path;
            try {
                path = URI.create(argList.get(0));
            } catch (IllegalArgumentException e) {
                throw new ParseException("Failed to parse PRINTER_URL");
            }
        } catch (ParseException e) {
            System.err.println("\n" + e.getMessage());
            help();
        }

        SimpleIppClientTransport transport = new SimpleIppClientTransport();
        URI uri = URI.create("ipp://localhost:631/ipp/print");
        Attribute<String> requested = requestedAttributes.of("all");
        IppPacket attributeRequest =
                IppPacket.getPrinterAttributes(uri)
                        .putOperationAttributes(requested)
                        .build();

        System.out.println("\nSending: " + attributeRequest.prettyPrint(100, "  "));
        IppPacketData request = new IppPacketData(attributeRequest);
        IppPacketData response = transport.sendData(uri, request);
        System.out.println("\nReceived: " + response.getPacket().prettyPrint(100, "  "));
    }

    private static void help() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(120);
        helpFormatter.printHelp("IppInfo" + " [OPTIONS] PRINTER_URL\nOPTIONS", options, true);
        System.exit(0);
    }
}
