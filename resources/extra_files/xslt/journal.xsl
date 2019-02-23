<?xml version="1.0"?>
<!-- 
# File version: $Revision: 1.4 $,  $Date: 2006-12-25 22:54:12 $
#  Last change: $Author: zgibek $
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
<xsl:output encoding="UTF-8" omit-xml-declaration="yes"/>

  <xsl:template match="dictionary"/>
  <xsl:template match="dayByDayLoop"/>

  <xsl:template match="task">
        <fo:root line-stacking-strategy="font-height">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="report"
                                       page-height="210mm" page-width="297mm" >
                    <fo:region-body margin="10mm" overflow="visible" region-name="body"/>
                    <fo:region-before extent="10mm" display-align="after" overflow="visible" region-name="header" />
                    <fo:region-after extent="10mm" display-align="after" overflow="visible" region-name="footer" />
                    <fo:region-start extent="10mm" display-align="after" overflow="visible" region-name="start" />
                    <fo:region-end extent="10mm" display-align="after" overflow="visible" region-name="end" />
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:declarations>
            <x:xmpmeta xmlns:x="adobe:ns:meta/">
                <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <rdf:Description rdf:about="" xmlns:dc="http://purl.org/dc/elements/1.1/">
                        <xsl:variable name="reportTitle" select="'noName'"/>
                        <xsl:choose>
                            <xsl:when test="$reportTitle='noName'">
                                <dc:title><xsl:value-of select="name" /></dc:title>
                            </xsl:when>
                            <xsl:otherwise>
                                <dc:title><xsl:value-of select="$reportTitle" /></dc:title>
                            </xsl:otherwise>
                        </xsl:choose>
                        <dc:creator><xsl:value-of select="/TimeSlotTracker_Report/dictionary/report-created-by"/></dc:creator>
                    </rdf:Description>
                </rdf:RDF>
            </x:xmpmeta>
</fo:declarations>

            <fo:page-sequence force-page-count="no-force" master-reference="report"
                              white-space-collapse="false">
                <fo:flow flow-name="body">
                    <xsl:variable name="reportTitle" select="'noName'"/>
                    <xsl:variable name="startDate" select="'noDate'" />
                    <xsl:variable name="stopDate" select="'noDate'" />
                    <xsl:if test="not($reportTitle='noName')">
                        <fo:block ><xsl:value-of select="$reportTitle" /></fo:block><fo:block/>
                        <!-- choosen from dictionary, in english: "Time spent on all tasks" -->
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/duration-spent-on-all-taks"/>: 
                        <xsl:value-of select="./duration/durationWithChildren"/>
                        <fo:block/>
                    </xsl:if>

                    <!-- Print (if given) the report start and end date -->
                    <xsl:if test="not($startDate='noDate') and not($stopDate='noDate')">
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/time-period-start"/>
                        &#160;<xsl:value-of select="$startDate" />&#160;
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/time-period-end"/>
                        &#160;<xsl:value-of select="$stopDate" />
                    </xsl:if>
                    <fo:block/>
                    <fo:table border="1" width="100%" table-layout="fixed" >
                        <fo:table-column column-number="1" column-width="20%" />
                        <fo:table-column column-number="2" column-width="10%" />
                        <fo:table-column column-number="3" column-width="70%" />
                        <fo:table-body>
                            <fo:table-row keep-together.within-page="always" >
                                <fo:table-cell xsl:use-attribute-sets="cell.000 day-title">
                                    <fo:block >
                                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/column-period"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="cell.000 day-title">
                                    <fo:block >
                                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/column-duration"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell xsl:use-attribute-sets="cell.000 day-title">
                                    <fo:block >
                                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/column-description"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <!-- Print timeslots -->
                            <xsl:apply-templates select="//timeslot">
                                <xsl:sort select="./startDate/datetime/year"
                                        data-type="number"
                                        order="ascending" />
                                <xsl:sort select="./startDate/datetime/month"
                                        data-type="number"
                                        order="ascending" />
                                <xsl:sort select="./startDate/datetime/day"
                                        data-type="number"
                                        order="ascending" />
                                <xsl:sort select="./startDate/datetime/hour"
                                        data-type="number"
                                        order="ascending" />
                                <xsl:sort select="./startDate/datetime/min"
                                        data-type="number"
                                        order="ascending" />
                            </xsl:apply-templates>
                        </fo:table-body>
                    </fo:table>
                    <!-- footer -->
                    <fo:block >
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/report-created-by"/>&#160;
                        <fo:basic-link external-destination="url('http://www.sf.net/projects/timeslottracker/')"
                                       color="blue">(http://www.sf.net/projects/timeslottracker/)</fo:basic-link>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
  </xsl:template>               
                 
  <xsl:template match="timeslot">
      <fo:table-row keep-together.within-page="always" >
          <fo:table-cell xsl:use-attribute-sets="cell.000">
              <fo:block>
                  <xsl:apply-templates select="./startDate/datetime" />
                  <xsl:text> ..</xsl:text>
                  <fo:block/>
                  <xsl:apply-templates select="./stopDate/datetime" />
              </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="cell.000 duration">
              <fo:block>
                  <xsl:value-of select="./duration/duration" />
              </fo:block>
          </fo:table-cell>
          <fo:table-cell xsl:use-attribute-sets="cell.000">
              <fo:block>
                  <fo:inline xsl:use-attribute-sets="taskName-title">
                      <xsl:value-of select="../name"/>
                  </fo:inline>
                  <xsl:text>:</xsl:text> 
                  <xsl:value-of select="./description" />
              </fo:block>

              <!-- print attributes -->
              <fo:block>
                  <xsl:apply-templates select="../attributes" />
              </fo:block>
          </fo:table-cell>
      </fo:table-row>
  </xsl:template>
  
  <xsl:template match="datetime">
      <!-- format date time as "yyyy-mm-dd hh:mm" -->
      <xsl:value-of select="format-number(year,'0000')"/>-<xsl:value-of select="format-number(month,'00')"/>-<xsl:value-of select="format-number(day,'00')"/>
      &#160;<xsl:value-of select="hour"/>:<xsl:value-of select="format-number(./min,'00')"/>
  </xsl:template>
  
  <xsl:template match="attributes">
      <xsl:for-each select="./attribute">
        <xsl:value-of select="./name" />:&#160;<xsl:value-of select="value" />
      </xsl:for-each>
  </xsl:template>

  <!-- styles -->
  <xsl:attribute-set name="day-title">
      <xsl:attribute name="background-color">#D6D6D6</xsl:attribute>
      <xsl:attribute name="font-weight">bold</xsl:attribute>
      <xsl:attribute name="text-align">center</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="cell.000">
      <xsl:attribute name="border">1mm double black</xsl:attribute>
      <xsl:attribute name="border-top">1mm double black</xsl:attribute>
      <xsl:attribute name="padding">4pt</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="taskName-title">
      <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="sum-title">
      <xsl:attribute name="background-color">:#D6D6D6</xsl:attribute>
      <xsl:attribute name="text-align">right</xsl:attribute>
      <xsl:attribute name="font-weight">bold</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="duration">
      <xsl:attribute name="text-align">right</xsl:attribute>
  </xsl:attribute-set>
  
</xsl:stylesheet>
