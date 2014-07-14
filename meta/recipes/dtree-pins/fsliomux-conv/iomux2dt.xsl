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
    <xsl:text>#undef DTS_FOUND_INSTANCE

</xsl:text>
    <!-- TODO: the 'only' and 'disabled' filter are working on the
         '@Instance' attribute at the moment. It might be useful to
         compare with '@InstanceAlias' in some cases. -->
    <xsl:apply-templates select="SignalDesign[generate-id(.)=generate-id(key('instance',@InstanceAlias)[1]) and
				 @IsChecked = 'true' and
				 (($only != '' and contains($_only, concat('/', @Instance, '/'))) or
				  ($only  = '' and not(contains($_disabled, concat('/', @Instance, '/')))))]"/>
  </xsl:template>

  <xsl:template match="/PinMuxDesign/SignalDesign">
    <xsl:text>/* {{{ instance </xsl:text><xsl:value-of select="@InstanceAlias"/>
    <xsl:text> */
#define DTS_IMX6_PINS_</xsl:text><xsl:value-of select="@InstanceAlias"/>
    <xsl:for-each select="key('instance', @InstanceAlias)">
      <xsl:variable name="reg_mux" select="substring(Register[starts-with(@Name, 'IOMUXC_SW_MUX_')]/@Address, 8)"/>
      <xsl:variable name="reg_pad" select="substring(Register[starts-with(@Name, 'IOMUXC_SW_PAD_')]/@Address, 8)"/>
      <xsl:variable name="reg_inp">
	<xsl:variable name="tmp" select="substring(Register[substring(@Name, string-length(@Name) - 5) = '_INPUT']/@Address, 8)"/>
	<xsl:choose>
	  <xsl:when test="$tmp"><xsl:value-of select="$tmp"/></xsl:when>
	  <xsl:otherwise>000</xsl:otherwise>
	</xsl:choose>
      </xsl:variable>

      <xsl:choose>
	<xsl:when test="string-length($reg_mux) > 0 and string-length($reg_pad) > 0 and
			count(Register[starts-with(@Name, 'IOMUXC_SW_MUX_')]) = 1 and
			count(Register[starts-with(@Name, 'IOMUXC_SW_PAD_')]) = 1 and
			count(Register[substring(@Name, string-length(@Name) - 5) = '_INPUT']) &lt;= 1">
	  <xsl:variable name="val_mux" select="Register[starts-with(@Name, 'IOMUXC_SW_MUX_')]/@Value"/>
	  <xsl:variable name="val_pad" select="Register[starts-with(@Name, 'IOMUXC_SW_PAD_')]/@Value"/>
	  <xsl:variable name="val_inp">
	    <xsl:variable name="tmp" select="Register[substring(@Name, string-length(@Name) - 5) = '_INPUT']/@Value"/>
	    <xsl:choose>
	      <xsl:when test="$tmp"><xsl:value-of select="$tmp"/></xsl:when>
	      <xsl:otherwise>0x00000000</xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	  <xsl:variable name="val_sion">
	    <xsl:variable name="tmp" select="substring($val_mux, 9,1)"/>
	    <xsl:choose>
	      <xsl:when test="$tmp = '1'">4</xsl:when>
	      <xsl:when test="$tmp = '0'">0</xsl:when>
	      <xsl:when test="$tmp = ''"></xsl:when>
	      <xsl:otherwise>
		<xsl:message terminate="yes">Unsupported SION value <xsl:value-of select="$tmp"/> for <xsl:value-of select="Routing/@mode"/></xsl:message>
	      </xsl:otherwise>
	    </xsl:choose>
	  </xsl:variable>
	  <xsl:if test="substring($val_mux, 1, 8) != '0x000000'">
	    <xsl:message terminate="yes">Unsupported mux value <xsl:value-of select="$val_mux"/> for <xsl:value-of select="Routing/@mode"/></xsl:message>
	  </xsl:if>
	  <xsl:if test="substring($val_inp, 1, 9) != '0x0000000'">
	    <xsl:message terminate="yes">Unsupported inp value <xsl:value-of select="$val_inp"/> for <xsl:value-of select="Routing/@mode"/></xsl:message>
	  </xsl:if>
	  <xsl:if test="substring($val_pad, 3, 1) != '0'">
	    <xsl:message terminate="yes">Unsupported pad value <xsl:value-of select="$val_pad"/> for <xsl:value-of select="Routing/@mode"/></xsl:message>
	  </xsl:if>
	  <xsl:text> \
	</xsl:text>
	  <xsl:value-of select="concat('0x', $reg_mux, ' 0x', $reg_pad, ' 0x', $reg_inp,
				'   0x', substring($val_mux, 10,1), ' 0x', substring($val_inp, 10,1),
				'   0x', $val_sion, substring($val_pad,4))"/>
	  <xsl:text>	/* </xsl:text><xsl:value-of select="@Name"/>: <xsl:value-of select="Routing/@padNet"/> (<xsl:value-of select="Routing/@ball"/>) -> <xsl:value-of select="Routing/@mode"/>
	  <xsl:text>*/</xsl:text>
	</xsl:when>
	<xsl:otherwise>
	  <xsl:text> \
	/* bad: </xsl:text><xsl:value-of select="Routing/@padNet"/>
	  <xsl:text> */</xsl:text>
<!--	  <xsl:message>Unsupported setup for <xsl:value-of select="Routing/@padNet"/></xsl:message> -->
	</xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:text>
/* }}} instance </xsl:text><xsl:value-of select="@InstanceAlias"/>
    <xsl:text> */

</xsl:text>
  </xsl:template>

</xsl:stylesheet>
