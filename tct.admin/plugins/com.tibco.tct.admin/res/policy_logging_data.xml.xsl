<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="2" />

    <xsl:template match="/">
        <amxdata_base:Enterprise 
            xmlns:amxdata_jmsssr="http://tibco.com/amxadministrator/command/line/types_jmssr" 
            xmlns:amxdata="http://tibco.com/amxadministrator/command/line/types"
            xmlns:amxdata_base="http://tibco.com/amxadministrator/command/line/types_base" xmlns:amxdata_reference="http://tibco.com/amxadministrator/command/line/types_reference" 
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
            xsi:schemaLocation="http://tibco.com/amxadministrator/command/line/types_base ../schemas/amxdata_base.xsd http://tibco.com/amxadministrator/command/line/types ../schemas/amxdata.xsd">

            <Environment xsi:type="amxdata:Environment" description="AMX system environment" contact="TIBCO Software Inc.">
                <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                <Node xsi:type="amxdata:Node" contact="TIBCO Software Inc.">
                    <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                    <Logger xsi:type="amxdata:Logger" name="governance.policy.logger" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="INFO">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="governance.policy.appender"/>
                        </AppenderRef>
                    </Logger>
                </Node> 				
            </Environment>
			
			<xsl:if test="config/create.dev.node = 'true'">
				<Environment xsi:type="amxdata:Environment" description="Development environment" >
					<xsl:attribute name="name"><xsl:value-of select="config/dev.envt.name" /></xsl:attribute>
					<Node xsi:type="amxdata:Node" description="Development node" contact="TIBCO Software Inc.">
						<xsl:attribute name="name"><xsl:value-of select="config/dev.node.name" /></xsl:attribute>
						<Logger xsi:type="amxdata:Logger" name="governance.policy.logger" additivity="false">
							<AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="INFO">
								<Appender xsi:type="amxdata_reference:LogAppender_reference" name="governance.policy.appender"/>
							</AppenderRef>
						</Logger>
					</Node>      
				</Environment>
			</xsl:if>
			
            <LogAppender xsi:type="amxdata:JmsLogAppender" name="governance.policy.appender"
                description="This is a default jms LogAppender created by Policy Logging."
                jmsConnectionFactoryName="TIBCO ActiveMatrix LogService JMSConnectionFactory Resource"
                jmsConnectionName="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"
                jmsDestination="TIBCO ActiveMatrix LogService JMSDestination Resource"
				payload="true"
				payloadConnectionFactoryName="TIBCO ActiveMatrix PayloadService JMSConnectionFactory Resource"
				payloadConnectionName="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource"
				payloadDestination="TIBCO ActiveMatrix PayloadService JMSDestination Resource"/>
        </amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>
