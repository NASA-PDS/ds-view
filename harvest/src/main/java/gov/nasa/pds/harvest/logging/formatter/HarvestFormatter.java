// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.harvest.logging.formatter;

import gov.nasa.pds.harvest.crawler.status.Status;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Class that formats the Harvest logging messages.
 *
 * @author mcayanan
 *
 */
public class HarvestFormatter extends Formatter {
    private static String lineFeed = System.getProperty("line.separator", "\n");
    private static String doubleLineFeed = lineFeed + lineFeed;

    private int productsRegistered;
    private int productsNotRegistered;
    private int filesSkipped;
    private int discoveredProducts;
    private int badFiles;
    private int associationsRegistered;
    private int associationsFailed;

    private StringBuffer config;
    private StringBuffer summary;

    public HarvestFormatter() {
        productsRegistered = 0;
        productsNotRegistered = 0;
        filesSkipped = 0;
        discoveredProducts = 0;
        badFiles = 0;
        associationsRegistered = 0;
        associationsFailed = 0;

        config = new StringBuffer("PDS Harvest Tool Log" + doubleLineFeed);
        summary = new StringBuffer("Summary:" + doubleLineFeed);
    }


    public String format(LogRecord record) {
        ToolsLogRecord tlr = (ToolsLogRecord) record;

        if(tlr.getLevel().intValue() == ToolsLevel.NOTIFICATION.intValue()) {
            if(tlr.getMessage().equals(Status.DISCOVERY)) {
                ++discoveredProducts;
            } else if(tlr.getMessage().equals(Status.BADFILE)) {
                ++badFiles;
            }
            return "";
        } else if(tlr.getLevel().intValue() == ToolsLevel.INGEST_SUCCESS.intValue()) {
            ++productsRegistered;
        } else if(tlr.getLevel().intValue() == ToolsLevel.INGEST_ASSOC_SUCCESS.intValue()) {
           ++associationsRegistered;
        } else if(tlr.getLevel().intValue() == ToolsLevel.INGEST_FAIL.intValue()) {
            ++productsNotRegistered;
        } else if(tlr.getLevel().intValue() == ToolsLevel.INGEST_ASSOC_FAIL.intValue()) {
            ++associationsFailed;
        } else if(tlr.getLevel().intValue() == ToolsLevel.SKIP.intValue()) {
            ++filesSkipped;
        }

        StringBuffer message = new StringBuffer();

        if(tlr.getLevel().intValue() != ToolsLevel.CONFIGURATION.intValue()) {
            if(tlr.getLevel().intValue() == ToolsLevel.SEVERE.intValue()) {
                message.append("ERROR");
            } else {
                message.append(tlr.getLevel().getName());
            }
            message.append(":   ");
        }
        if(tlr.getFilename() != null) {
            message.append("[" + tlr.getFilename() + "] ");
        }
        if(tlr.getLine() != -1) {
            message.append("line " + tlr.getLine() + ": ");
        }
        message.append(tlr.getMessage());
        message.append(lineFeed);

        return message.toString();
    }

    private void processSummary() {
        int totalFiles = discoveredProducts + badFiles + filesSkipped;
        int totalAssociations = associationsRegistered + associationsFailed;
        summary.append(discoveredProducts + " of " + totalFiles + " files parsed successfully, " + filesSkipped + " skipped" + lineFeed);
        summary.append(productsRegistered + " of " + discoveredProducts + " products registered." + lineFeed);
        summary.append(associationsRegistered + " of " + totalAssociations + " associations registered." + lineFeed);
    }

    public String getTail(Handler handler) {
        StringBuffer report = new StringBuffer("");

        processSummary();

        report.append(lineFeed);
        report.append(summary);
        report.append(doubleLineFeed + "End of Log" + doubleLineFeed);

        return report.toString();
    }
}
