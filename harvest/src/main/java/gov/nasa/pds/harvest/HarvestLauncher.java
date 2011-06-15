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
package gov.nasa.pds.harvest;

import gov.nasa.pds.harvest.commandline.options.Flag;
import gov.nasa.pds.harvest.commandline.options.InvalidOptionException;
import gov.nasa.pds.harvest.logging.ToolsLevel;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;
import gov.nasa.pds.harvest.logging.formatter.HarvestFormatter;
import gov.nasa.pds.harvest.logging.handler.HarvestFileHandler;
import gov.nasa.pds.harvest.logging.handler.HarvestStreamHandler;
import gov.nasa.pds.harvest.policy.Namespace;
import gov.nasa.pds.harvest.policy.Policy;
import gov.nasa.pds.harvest.policy.PolicyReader;
import gov.nasa.pds.harvest.security.SecuredUser;
import gov.nasa.pds.harvest.util.PDSNamespaceContext;
import gov.nasa.pds.harvest.util.ToolInfo;
import gov.nasa.pds.harvest.util.Utility;
import gov.nasa.pds.harvest.util.XMLExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Wrapper class of the Harvest tool that handles the command-line processing.
 *
 * @author mcayanan
 *
 */
public class HarvestLauncher {
  /** logger object. */
  private static Logger log = Logger.getLogger(
      HarvestLauncher.class.getName());

  /** Policy file. */
  private File policy;

  /** The internal policy file. */
  private InputStream globalPolicy;

  /** password of the authorized user. */
  private String password;

  /** an authorized user. */
  private String username;

  /** URL of the registry service. */
  private String registryURL;

  /** URL of the security service. */
  private String securityURL;

  /** A log file name. */
  private String logFile;

  /** The wait interval in seconds in between crawls if running
   *  the tool in continuous mode. */
  private int waitInterval;

  /** The port number to use for the daemon if running the tool
   *  in continuous mode.
   */
  private int daemonPort;

  /**
   * Default constructor.
   *
   */
  public HarvestLauncher() {
    policy = null;
    password = null;
    username = null;
    registryURL = null;
    securityURL = null;
    logFile = null;
    waitInterval = -1;
    daemonPort = -1;

    globalPolicy = this.getClass().getResourceAsStream("global-policy.xml");
  }

  /**
   * A method to parse the command-line arguments.
   *
   * @param args The command-line arguments
   * @return A class representation of the command-line arguments
   *
   * @throws ParseException If there was an error during parsing.
   */
  public final CommandLine parse(final String[] args)
  throws ParseException {
    CommandLineParser parser = new GnuParser();
    return parser.parse(Flag.getOptions(), args);
  }

  /**
   * Examines the command-line arguments passed into the Harvest Tool
   * and takes the appropriate action based on what flags were set.
   *
   * @param line A class representation of the command-line arguments.
   *
   * @throws Exception If there was an error while querying the options
   * that were set on the command-line.
   */
  public final void query(final CommandLine line) throws Exception {
    registryURL = System.getProperty("pds.registry");
    securityURL = System.getProperty("pds.security");
    if (registryURL == null) {
      throw new Exception("\'pds.registry\' java property is not set.");
    }
    List<Option> processedOptions = Arrays.asList(line.getOptions());
    for (Option o : processedOptions) {
      if (o.getOpt().equals(Flag.HELP.getShortName())) {
        displayHelp();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
        displayVersion();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.PASSWORD.getShortName())) {
        password = o.getValue();
      } else if (o.getOpt().equals(Flag.USERNAME.getShortName())) {
        username = o.getValue();
      } else if (o.getOpt().equals(Flag.LOG.getShortName())) {
        logFile = o.getValue();
      } else if (o.getOpt().equals(Flag.PORT.getShortName())) {
        try {
          daemonPort = Integer.parseInt(o.getValue());
        } catch (NumberFormatException n) {
          throw new Exception(n.getMessage());
        }
      } else if (o.getOpt().equals(Flag.WAIT.getShortName())) {
        try {
          waitInterval = Integer.parseInt(o.getValue());
        } catch (NumberFormatException n) {
          throw new Exception(n.getMessage());
        }
      }
    }
    if (line.getArgList().size() != 0) {
      policy = new File(line.getArgList().get(0).toString());
      if (!policy.exists()) {
        throw new InvalidOptionException("Policy file does not exist: "
            + policy);
       }
    } else {
        throw new InvalidOptionException(
          "Policy file not found on the command-line.");
    }

    if ((username != null && password == null)
        || (username == null && password != null)) {
      throw new InvalidOptionException(
        "Username and/or password must be specified.");
    }
    setLogger();
  }

  /**
   * Logs header information for the log output.
   *
   */
  private void logHeader() {
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "PDS Harvest Tool Log\n"));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Version                     " + ToolInfo.getVersion()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Time                        " + Utility.getDateTime()));
    log.log(new ToolsLogRecord(ToolsLevel.CONFIGURATION,
        "Registry Location           " + registryURL.toString() + "\n"));
  }

  /**
   * Sets the appropriate handlers for the logging.
   *
   * @throws IOException If a log file was specified and could not
   * be read.
   */
  private void setLogger() throws IOException {
    Logger logger = Logger.getLogger("");
    logger.setLevel(Level.ALL);
    Handler []handler = logger.getHandlers();
    for (int i = 0; i < logger.getHandlers().length; i++) {
      logger.removeHandler(handler[i]);
    }
    if (logFile != null) {
      logger.addHandler(new HarvestFileHandler(logFile, Level.INFO,
          new HarvestFormatter()));
    } else {
      logger.addHandler(new HarvestStreamHandler(System.out,
          Level.INFO, new HarvestFormatter()));
    }
  }

  /**
   * Displays the current version and disclaimer notice.
   *
   */
  public final void displayVersion() {
    System.err.println("\n" + ToolInfo.getName());
    System.err.println(ToolInfo.getVersion());
    System.err.println("Release Date: " + ToolInfo.getReleaseDate());
    System.err.println(ToolInfo.getCopyright() + "\n");
  }

  /**
   * Displays tool usage.
   *
   */
  public final void displayHelp() {
    int maxWidth = 80;
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(maxWidth, "Harvest <policy file> <options>",
        null, Flag.getOptions(), null);
  }

  /**
   * Closes the handlers for the logger.
   *
   */
  private void closeHandlers() {
    Logger logger = Logger.getLogger("");
    Handler []handlers = logger.getHandlers();
    for (int i = 0; i < logger.getHandlers().length; i++) {
      handlers[i].close();
    }
  }

  /**
   * Perform harvesting of the target files.
   *
   * @param policy Class representation of the policy file.
   * @param securityUrl Url of the Security Service. Can be null
   * if the Registry Service instance is not tied to Security.
   *
   * @throws ParserConfigurationException If an error occurred during
   * metadata extraction.
   * @throws MalformedURLException If the URL to the registry service
   * is invalid.
   * @throws RegistryClientException
   */
  private void doHarvesting(final Policy policy, final String securityUrl)
  throws MalformedURLException, ParserConfigurationException {
    log.log(new ToolsLogRecord(ToolsLevel.INFO, "XML extractor set to the "
        + "following default namespace: "
        + XMLExtractor.getDefaultNamespace()));
    Harvester harvester = new Harvester(registryURL);
    if ((username != null) && (password != null)) {
      harvester.setSecuredUser(new SecuredUser(username, password));
    }
    if (daemonPort != -1 && waitInterval != -1) {
      harvester.setDaemonPort(daemonPort);
      harvester.setWaitInterval(waitInterval);
    }
    harvester.harvest(policy);
  }

  /**
   * Sets up the configuration for the XML extractor.
   *
   * @param namespaces A list of namespaces.
   *
   */
  private void setupExtractor(List<Namespace> namespaces) {
    String defaultNamespaceUri = "";
    for (Iterator<Namespace> i = namespaces.iterator();
      i.hasNext();) {
      Namespace n = i.next();
      if (n.isDefault()) {
        defaultNamespaceUri = n.getUri();
        break;
      }
    }
    // isEmpty() is a java 1.6 method
    if (!defaultNamespaceUri.equals("")) {
//  if (!defaultNamespaceUri.isEmpty()) {
      XMLExtractor.setDefaultNamespace(defaultNamespaceUri);
    }
    if (!namespaces.isEmpty()) {
      XMLExtractor.setNamespaceContext(new PDSNamespaceContext(
          namespaces));
    }
  }

  /**
   * Process main.
   *
   * @param args Command-line arguments.
   */
  private void processMain(String []args) {
    if (args.length == 0) {
      System.out.println("\nType 'Harvest -h' for usage");
      System.exit(0);
    }
    try {
      CommandLine commandline = parse(args);
      query(commandline);
      Policy policy = PolicyReader.unmarshall(this.policy);
      Policy globalPolicy = PolicyReader.unmarshall(this.globalPolicy);
      policy.getCandidates().getNamespace().addAll(
          globalPolicy.getCandidates().getNamespace());
      policy.getCandidates().getProductMetadata().addAll(
          globalPolicy.getCandidates().getProductMetadata());
      setupExtractor(policy.getCandidates().getNamespace());
      logHeader();
      doHarvesting(policy, securityURL);
      closeHandlers();
    } catch (JAXBException je) {
      //Don't do anything
    } catch (ParseException pEx) {
      System.err.println("Command-line parse failure: "
            + pEx.getMessage());
      System.exit(1);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Main class.
   *
   * @param args The command-line arguments.
   */
  public static void main(final String []args) {
    new HarvestLauncher().processMain(args);
  }
}
