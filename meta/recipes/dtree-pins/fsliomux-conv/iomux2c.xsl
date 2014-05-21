<?xml version="1.0" encoding="utf-8"?>
<!--
Copyright (C) 2013 Enrico Scholz <enrico.scholz@sigma-chemnitz.de>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 and/or 3 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="text"/>

  <xsl:param name="disable"/>
  <xsl:param name="only"/>
  <xsl:param name="mode">struct</xsl:param>
  <xsl:param name="fn">DCD</xsl:param>

  <xsl:variable name="_only"     select="concat('/', translate(normalize-space($only), ' ', '/ /'), '/')"/>
  <xsl:variable name="_disabled" select="concat('/', translate(normalize-space($disable), ' ', '/ /'), '/')"/>


  <xsl:key name="instance" match="SignalDesign" use="@InstanceAlias"/>
  <xsl:key name="address"  match="/PinMuxDesign/SignalDesign/Register" use="@Address"/>


  <xsl:template match="/PinMuxDesign">
    <!-- TODO: the 'only' and 'disabled' filter are working on the
         '@Instance' attribute at the moment. It might be useful to
         compare with '@InstanceAlias' in some cases. -->
    <xsl:apply-templates select="SignalDesign[generate-id(.)=generate-id(key('instance',@InstanceAlias)[1]) and
				 @IsChecked = 'true' and
				 (($only != '' and contains($_only, concat('/', @Instance, '/'))) or
				  ($only  = '' and not(contains($_disabled, concat('/', @Instance, '/')))))]"/>
  </xsl:template>

  <xsl:template match="/PinMuxDesign/SignalDesign"
>
/* {{{ instance <xsl:value-of select="@InstanceAlias"/> */
<xsl:for-each select="key('instance', @InstanceAlias)"
><xsl:if test="position() != 1"><xsl:text>
</xsl:text></xsl:if
>	/* <xsl:value-of select="@Name"/>: <xsl:value-of select="Routing/@padNet"/> (<xsl:value-of select="Routing/@ball"/>) -> <xsl:value-of select="Routing/@mode"/> */
<xsl:apply-templates select="Register"/>
	</xsl:for-each
>/* }}} instance <xsl:value-of select="@InstanceAlias"/> */
  </xsl:template>

  <xsl:template match="/PinMuxDesign/SignalDesign/Register">
    <xsl:variable name="value" select="@Value"/>
    <!-- TODO: the 'only' and 'disabled' filter are working on the
         '@Instance' attribute at the moment. It might be useful to
         compare with '@InstanceAlias' in some cases. -->
    <xsl:variable name="all" select="key('address', @Address)[
				     ../@IsChecked = 'true' and
				     (($only != '' and contains($_only, concat('/', ../@Instance, '/'))) or
				      ($only  = '' and not(contains($_disabled, concat('/', ../@Instance, '/')))))]"/>
    <xsl:variable name="first" select="$all[1]"/>
    <xsl:choose>
      <xsl:when test="generate-id($first) = generate-id(.)"
><xsl:call-template name="print-register"
/></xsl:when>
      <xsl:otherwise
><xsl:if test="$first/@Value != @Value">
	  <xsl:message terminate="yes">Setup of <xsl:value-of select="$all[1]/../@Name"/> conflicts with <xsl:value-of select="@Name"/></xsl:message>
	</xsl:if>
#if 0	/* already set by <xsl:value-of select="$all[1]/../@Name"/> -> <xsl:value-of select="$all[1]/@Name"/> */
<xsl:call-template name="print-register"
/>#endif
</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="print-register">
    <xsl:choose>
      <xsl:when test="$mode = 'struct'">
	<xsl:apply-templates select="." mode="print-struct"/>
      </xsl:when>
      <xsl:when test="$mode = 'fn'">
	<xsl:apply-templates select="." mode="print-fn"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:message terminate="yes">Unsupported mode</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="Register" mode="print-struct"
>	{ <xsl:value-of select="@Address"/>, <xsl:value-of select="@Value"/> }, /* <xsl:value-of select="@Name"/> */
</xsl:template>

  <xsl:template match="Register" mode="print-fn">
    <xsl:text>	</xsl:text>
    <xsl:value-of select="$fn"/>(<xsl:value-of select="@Address"/>, <xsl:value-of select="@Value"/>), /* <xsl:value-of select="@Name"/> */
</xsl:template>

</xsl:stylesheet>
