package xyz.ariesfish;

import com.hp.jipp.encoding.Attribute;
import com.hp.jipp.encoding.IppPacket;
import com.hp.jipp.encoding.Tag;
import com.hp.jipp.model.Types;
import com.hp.jipp.trans.IppPacketData;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hp.jipp.model.Types.*;

public class IppUtil {

    private final static Options options;
    private final static SimpleIppClientTransport transport = new SimpleIppClientTransport();
    private final static Map<String, String> extensionTypes = new HashMap<String, String>() {{
        put("pdf",  "application/pdf");
        put("pclm", "application/PCLm");
        put("pwg",  "image/pwg-raster");
    }};

    static {
        Option fileOption = new Option("p", "print", true, "print a file");
        fileOption.setArgName("FILE");
        OptionGroup requiredOptions = new OptionGroup()
                .addOption(fileOption)
                .addOption(new Option("a", "attributes", false, "get printer attributes"));
        requiredOptions.setRequired(true);

        Option mimeOption = new Option("m", "mime", true, "specify mime type");
        mimeOption.setArgName("MIME");

        options = new Options()
                .addOption("h", "help", false, "show help")
                .addOptionGroup(requiredOptions)
                .addOption(mimeOption);
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

            URI uri;
            try {
                uri = URI.create(argList.get(0));
            } catch (IllegalArgumentException e) {
                throw new ParseException("Failed to parse PRINTER_URL");
            }

            if (command.hasOption("a")) {
                getPrinterInfo(uri, command);
            } else if (command.hasOption("p")) {
                printJob(uri, command);
            } else {
                throw new ParseException("Invalid options");
            }
        } catch (ParseException e) {
            System.err.println("\n" + e.getMessage());
            help();
        }
    }

    private static void getPrinterInfo(URI uri, CommandLine command) throws IOException {
        Attribute<String> requested = requestedAttributes.of(
                Types.ippVersionsSupported.getName(),
                Types.operationsSupported.getName(),
                Types.printerUriSupported.getName(),
                Types.printerName.getName(),
                Types.printerDnsSdName.getName(),
                Types.printerDeviceId.getName(),
                Types.printerUuid.getName(),
                Types.systemFirmwareStringVersion.getName(),
                Types.documentFormatSupported.getName(),
                Types.pdfVersionsSupported.getName(),
                Types.pwgRasterDocumentTypeSupported.getName(),
                Types.printerState.getName(),
                Types.printerStateReasons.getName(),
                Types.printerAlert.getName(),
                Types.printerAlertDescription.getName(),
                Types.printColorModeSupported.getName(),
                Types.pagesPerMinute.getName(),
                Types.pagesPerMinuteColor.getName(),
                Types.copiesSupported.getName(),
                Types.mediaDefault.getName(),
                Types.mediaSupported.getName(),
                Types.mediaColDatabase.getName(),
                Types.sidesDefault.getName(),
                Types.sidesSupported.getName(),
                Types.numberUpSupported.getName(),
                Types.finishingsDefault.getName(),
                Types.finishingsSupported.getName(),
                Types.printerInputTray.getName(),
                Types.printerSupply.getName(),
                Types.printerSupplyDescription.getName(),
                "marker-levels",
                "marker-names",
                Types.queuedJobCount.getName());

        IppPacket printerInfo = IppPacket.getPrinterAttributes(uri)
                .putOperationAttributes(requested)
                .build();

        System.out.println("\nSend: " + printerInfo.prettyPrint(100, "  "));
        IppPacketData request = new IppPacketData(printerInfo);
        IppPacketData response = transport.sendData(uri, request);
        System.out.println("\nReceive: " + response.getPacket().prettyPrint(100, "  "));
    }

    private static void printJob(URI uri, CommandLine command) throws IOException, ParseException {
        String fileName = command.getOptionValue("p");
        System.out.println("Print file: " + fileName);

        String format = command.getOptionValue("m");
        if (format == null) {
            if (fileName.contains(".")) {
                format = extensionTypes.get(fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase());
            }
        }
        // check again
        if (format == null) {
            format = extensionTypes.get("pdf");
        }

        File printFile = new File(fileName);
        if (!printFile.isFile()) {
            throw new ParseException("Cannot read " + fileName);
        }

        IppPacket requested = IppPacket.getPrinterAttributes(uri)
                .putOperationAttributes(requestedAttributes.of(documentFormatSupported.getName()))
                .build();
        IppPacketData request = new IppPacketData(requested);
        IppPacketData response = transport.sendData(uri, request);

        List<String> formats = response.getPacket().getStrings(Tag.printerAttributes, documentFormatSupported);
        if (!formats.contains(format)) {
            throw new ParseException(format + " format not supported by printer in " + formats);
        }

        IppPacket printRequest = IppPacket.printJob(uri).putOperationAttributes(documentFormat.of(format)).build();
        System.out.println("\nSend: " + printRequest.prettyPrint(100, "  "));
        request = new IppPacketData(printRequest, new FileInputStream(printFile));
        response = transport.sendData(uri, request);
        System.out.println("\nReceive: " + response.getPacket().prettyPrint(100, "  "));
    }

    private static void help() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(120);
        helpFormatter.printHelp("IppUtil" + " [OPTIONS] PRINTER_URL\nOPTIONS", options, true);
        System.exit(0);
    }
}
