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
 * Detail report returning all data in PDF format
 * 
 * @version File version: $Revision: 888 $, $Date: 2019-03-25 18:53:21 +0700
 *          (Sat, 25 Mon 2019) $
 * @author Last change: $Author: frotondella $
 */
public class PDFDetailReport extends AbstractReport {

  public PDFDetailReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.detailReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/detail.xsl";
    return new StreamSource(PDFDetailReport.class.getResourceAsStream(filename));
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
    foUserAgent.setTitle("PDF detail report");
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, new FileOutputStream(resultFile));
    Result res = new SAXResult(fop.getDefaultHandler());
    trans.transform(xmlSource, res);
  }

}
