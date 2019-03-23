<?xml version="1.0" encoding="UTF-8"?>
<!-- 
# File version: $Revision: 1.0 $,  $Date: 2019-03-19 19:54:12 $
#  Last change: $Author: frotondella $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" >
    <xsl:output encoding="UTF-8" />

    <xsl:template match="dictionary"/>
    <xsl:template match="dayByDayLoop"/>

    <xsl:template match="task">
        <fo:root line-stacking-strategy="font-height" font-family="MS Gothic, FreeSans, NotoSansGothic-Regular, UnBatang, TakaoPGothic, Pagul">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="report" page-height="297mm" page-width="210mm" >
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

            <fo:page-sequence force-page-count="no-force" master-reference="report" white-space-collapse="false">
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
                        <xsl:text>    </xsl:text>
                        <xsl:value-of select="$startDate" />
                        <xsl:text>    </xsl:text>
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/time-period-end"/>
                        <xsl:text>    </xsl:text>
                        <xsl:value-of select="$stopDate" />
                    </xsl:if>
                    <fo:block/>
                    <fo:list-block>
                        <xsl:apply-templates mode="child" select="." />
                    </fo:list-block>

                    <!-- footer -->
                    <fo:block >
                        <fo:leader leader-length="190mm" leader-pattern="rule"
                                    rule-style="solid" rule-thickness="0.2mm" color="black"></fo:leader>
                    </fo:block >
                    <fo:block >
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/report-created-by"/>
                        <xsl:text>    </xsl:text>
                        <fo:basic-link external-destination="url('http://www.sf.net/projects/timeslottracker/')"
                                        color="blue">(http://www.sf.net/projects/timeslottracker/)</fo:basic-link>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>               

    <xsl:template match="task" mode="child">
        <!-- include this task only if there is some timeslots 
                (i.e. durationWithChildren is not 0:00) -->
        <xsl:if test="not(./duration/durationWithChildren='0:00')">
            <fo:list-item>
                <fo:list-item-label end-indent="label-end()">
                    <fo:block><xsl:number/>.</fo:block>
                </fo:list-item-label>
                <fo:list-item-body start-indent="body-start()">
                    <fo:block>
                        <fo:inline xsl:use-attribute-sets="taskName-title">
                            <xsl:value-of select="name"/>
                        </fo:inline>

                        <!-- choosen from dictionary, in english: "Duration" -->
                        <xsl:text>   (</xsl:text>
                        <xsl:value-of select="/TimeSlotTracker_Report/dictionary/duration"/>
                        <xsl:text>: </xsl:text>
                        <fo:inline xsl:use-attribute-sets="taskName-title">
                            <xsl:value-of select="./duration/duration"/>
                        </fo:inline>
                        
                        <!-- show the subtasks's time only if differ from this level task -->
                        <xsl:if test="not(./duration/duration=./duration/durationWithChildren)">
                            <!-- coosen from dictionary, in english: "including subtasks" -->
                            <xsl:text>,</xsl:text>
                            <xsl:value-of select="/TimeSlotTracker_Report/dictionary/duration-including-subtasks" />
                            <xsl:text>: </xsl:text> 
                            <fo:inline xsl:use-attribute-sets="taskName-title">
                                <xsl:value-of select="./duration/durationWithChildren"/>
                            </fo:inline>
                        </xsl:if>
                        <xsl:text>)</xsl:text>
                        <xsl:if test="not(./description='')">
                            <fo:block font-size="10pt" >
                                <fo:inline font-size="10pt" >
                                    <xsl:text>(</xsl:text>
                                    <xsl:value-of select="/TimeSlotTracker_Report/dictionary/description" />
                                    <xsl:text>:    </xsl:text>
                                    <xsl:value-of select="description"/>
                                    <xsl:text>)</xsl:text>
                                </fo:inline>
                            </fo:block>
                        </xsl:if>

                        <!-- print attributes -->
                        <fo:block/>
                        <xsl:apply-templates select="./attributes" />
                        </fo:block>

                    <!-- subtasks -->
                    <fo:list-block>
                        <xsl:apply-templates mode="child" select="./task" />
                        <fo:list-item>
                            <fo:list-item-label>
                                <fo:block/>
                            </fo:list-item-label>
                            <fo:list-item-body>
                                <fo:block/>
                            </fo:list-item-body>
                        </fo:list-item>
                    </fo:list-block>
                </fo:list-item-body>
            </fo:list-item>
        </xsl:if>
    </xsl:template>

    <xsl:template match="datetime">
        <!-- format date time as "yyyy-mm-dd hh:mm" -->
        <xsl:value-of select="format-number(year,'0000')"/>-<xsl:value-of select="format-number(month,'00')"/>-<xsl:value-of select="format-number(day,'00')"/>
        <xsl:text>    </xsl:text>
        <xsl:value-of select="hour"/>:<xsl:value-of select="format-number(./min,'00')"/>
    </xsl:template>

    <xsl:template match="attributes">
        <xsl:for-each select="./attribute">
        <fo:block>
            <xsl:value-of select="./name" />:    <xsl:value-of select="value" />
        </fo:block>
        </xsl:for-each>
    </xsl:template>

    <!-- styles -->
    <xsl:attribute-set name="taskName-title">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

</xsl:stylesheet>
