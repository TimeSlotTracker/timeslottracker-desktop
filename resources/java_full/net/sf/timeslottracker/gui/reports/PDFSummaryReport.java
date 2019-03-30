package net.sf.timeslottracker.gui.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Summary report returning all data in PDF form, but without the timeslots
 * 
 * @version File version: $Revision: 888 $, $Date: 2019-03-19 19:36:21 +0700
 *          (Tue, 19 Mar 2019) $
 * @author Last change: $Author: frotondella $
 */
public class PDFSummaryReport extends AbstractReport {

  public PDFSummaryReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.summaryReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/summary.xsl";
    return new StreamSource(PDFSummaryReport.class.getResourceAsStream(filename));
  }

  @Override
  public void transform(File resultFile, Source xmlSource, Transformer trans) throws Exception {
    // FopFactory fopFactory = FopFactory.newInstance(resultFile.toURI());
    DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
    InputStream configFile = ReportConfiguration.class.getResourceAsStream("/xslt/fop_conf.xml");
    org.apache.avalon.framework.configuration.Configuration cfg = cfgBuilder.build(configFile);
    //File configFile = new File("fop.xconf");
    //org.apache.avalon.framework.configuration.Configuration cfg = cfgBuilder.buildFromFile(configFile);
    FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(resultFile.toURI()).setConfiguration(cfg);
    FopFactory fopFactory = fopFactoryBuilder.build();
    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
    foUserAgent.setAuthor("TimeSlotTracker");
    foUserAgent.setTitle("PDF summary report");
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, new FileOutputStream(resultFile));
    Result res = new SAXResult(fop.getDefaultHandler());
    trans.transform(xmlSource, res);
  }

}
