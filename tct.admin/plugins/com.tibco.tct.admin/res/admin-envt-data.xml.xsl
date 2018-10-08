<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="2" />

    <xsl:template match="/">

<amxdata_base:Enterprise
    xmlns:amxdata_bootstrap="http://tibco.com/amf/hpa/core/admin/bootstrap/types"
    xmlns:amxdata_base="http://tibco.com/amxadministrator/command/line/types_base"
    xmlns:amxdata_reference="http://tibco.com/amxadministrator/command/line/types_reference"
    xmlns:amxdata="http://tibco.com/amxadministrator/command/line/types"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:cs="http://tibco.com/trinity/server/credentialserver/cmdline/credentialserver" 
    xsi:schemaLocation="http://tibco.com/amxadministrator/command/line/types_base ../schemas/amxdata_base.xsd http://tibco.com/trinity/server/credentialserver/cmdline/credentialserver ../schemas/CredentialServer.xsd http://tibco.com/amf/hpa/core/admin/bootstrap/types ../schemas/amxdata_bootstrap.xsd http://tibco.com/amxadministrator/command/line/types ../schemas/amxdata.xsd">

    <Environment xsi:type="amxdata:Environment" description="Development environment" >
        <xsl:attribute name="name"><xsl:value-of select="config/dev.envt.name" /></xsl:attribute>
        <Node xsi:type="amxdata:Node" description="Development node" contact="TIBCO Software Inc.">
            <xsl:attribute name="hostName"><xsl:value-of select="config/admin.tibcohost.systemhost" /></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="config/dev.node.name" /></xsl:attribute>
            <xsl:attribute name="portNumber"><xsl:value-of select="config/dev.node.port" /></xsl:attribute>
			<xsl:if test="config/amx.securitymanager.enabled='false'"><xsl:attribute name="jvmArg">-Xmx512m -Xms128m -Damx.securitymanager.enabled=false</xsl:attribute></xsl:if>
        </Node>
        <Host>
            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemhost" /></xsl:attribute>
        </Host>
        <Application xsi:type="amxdata:Application" name="com.tibco.amx.it.mediation.app" contact="TIBCO Software Inc."
            description="TIBCO ActiveMatrix Mediation Implementation Type">
            <Node>
                <xsl:attribute name="name"><xsl:value-of select="config/dev.node.name" /></xsl:attribute>
                <xsl:attribute name="environmentName"><xsl:value-of select="config/dev.envt.name" /></xsl:attribute>
            </Node>
            <ApplicationTemplate xsi:type="amxdata_reference:ApplicationTemplate_reference" name="com.tibco.amx.it.mediation.apt"/>
        </Application>        
    </Environment>
    
</amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>