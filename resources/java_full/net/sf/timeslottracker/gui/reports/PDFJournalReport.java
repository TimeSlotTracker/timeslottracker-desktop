package net.sf.timeslottracker.gui.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;

/**
 * Journal report (by days) in PDF form
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class PDFJournalReport extends AbstractReport {
  private Collection<Filter> filters;

  public PDFJournalReport(LayoutManager layoutManager) {
    super(layoutManager);
    filters = new Vector<>();
    filters.add(new EncodingFilter(layoutManager));
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.journalReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return false;
  }

  public Collection<Filter> getExtraFilters() {
    return filters;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/journal.xsl";
    return new StreamSource(
        PDFJournalReport.class.getResourceAsStream(filename));
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
    foUserAgent.setTitle("PDF journal report");
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, new FileOutputStream(resultFile));
    Result res = new SAXResult(fop.getDefaultHandler());
    trans.transform(xmlSource, res);
  }

}
