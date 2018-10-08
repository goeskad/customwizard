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
                    <xsl:if test="config/mcr.ems.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JMSSslClient Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JMSSslClient Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JMSKeystoreProvider Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JMSKeystoreProvider Resource"/>
                    </xsl:if>
                    <xsl:if test="config/logging.db.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService DBKeystoreProvider Resource" resourceTemplateName="TIBCO ActiveMatrix LogService DBKeystoreProvider Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService DBSslClient Resource" resourceTemplateName="TIBCO ActiveMatrix LogService DBSslClient Resource"/>
                    </xsl:if>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JDBC Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JDBC Resource"/>       
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService Teneo Resource" resourceTemplateName="TIBCO ActiveMatrix LogService Teneo Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JMSConnectionFactory Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JMSConnectionFactory Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService JMSDestination Resource" resourceTemplateName="TIBCO ActiveMatrix LogService JMSDestination Resource"/>
					<ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix LogService HttpClient Resource" resourceTemplateName="TIBCO ActiveMatrix LogService HttpClient Resource"/>
                </Node>
                <Application xsi:type="amxdata:Application" name="com.tibco.amx.commonlogging.logservice.app" contact="TIBCO Software Inc." description="TIBCO ActiveMatrix CommonLogging LogService Application is the product application responsible for getting log messages from EMS server and saving these messages into DB, it also provides interface for searching,purging logs from DB." folderPath="/System">
                    <Node>
					    <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                        <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
					</Node>
                    <ApplicationTemplate xsi:type="amxdata_reference:ApplicationTemplate_reference" name="com.tibco.commonlogging.logservice.daa"/>
                    <Logger xsi:type="amxdata:Logger" name="TIBCO ActiveMatrix CommonLogging LogService Logger" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="INFO">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"/>
                        </AppenderRef>
                    </Logger>
                    <Logger xsi:type="amxdata:Logger" name="org.eclipse.emf" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="ERROR">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"/>
                        </AppenderRef>
                    </Logger>
                    <Logger xsi:type="amxdata:Logger" name="org.springframework" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="ERROR">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"/>
                        </AppenderRef>
                    </Logger>
                    <Logger xsi:type="amxdata:Logger" name="org.hibernate" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="ERROR">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"/>
                        </AppenderRef>
                    </Logger>
                    <Logger xsi:type="amxdata:Logger" name="org.apache" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef"  effectiveLevel="ERROR">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"/>
                        </AppenderRef>
                    </Logger>  
                </Application>        
            </Environment>
			<ResourceTemplate	
				xsi:type="amxdata:HttpClientResourceTemplate"	
				name="TIBCO ActiveMatrix LogService HttpClient Resource"
				description="Used by the TIBCO ActiveMatrix log service to reference LogService.">				
				<xsl:attribute name="host"><xsl:value-of select="config/serverconnsetting.host" /></xsl:attribute>
				<xsl:attribute name="port"><xsl:value-of select="config/serverconnsetting.port" /></xsl:attribute>				
			</ResourceTemplate>
            <xsl:if test="config/logging.db.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate" name="TIBCO ActiveMatrix LogService DBKeystoreProvider Resource"
                description="Used by the TIBCO ActiveMatrix log service to load a keystore while accessing a Database using SSL.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/logging.db.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/logging.db.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/logging.db.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreProvider"></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate" name="TIBCO ActiveMatrix LogService DBSslClient Resource" 
			    description="Used by the TIBCO ActiveMatrix log service to create a DB SSL connection.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General"
                    trustStoreServiceProvider="TIBCO ActiveMatrix LogService DBKeystoreProvider Resource"
                    enableAccessToTrustStore="true">
                </GeneralConfiguration>
            </ResourceTemplate>
            </xsl:if>
            <ResourceTemplate xsi:type="amxdata:JdbcResourceTemplate" name="TIBCO ActiveMatrix LogService JDBC Resource"
                description="Used by the TIBCO ActiveMatrix log service to connect to the database.">
                <xsl:attribute name="maxConnections"><xsl:value-of select="config/logging.db.maxconnections" /></xsl:attribute>
				<xsl:if test="config/logging.db.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix LogService DBSslClient Resource</xsl:attribute>
                </xsl:if>  
                <Direct xsi:type="amxdata:Direct" isTransactional="true" loginTimeOut="60000">
                    <xsl:attribute name="dbUrl"><xsl:value-of select="config/logging.db.url" /></xsl:attribute>    
                    <xsl:attribute name="jdbcDriver"><xsl:value-of select="config/logging.db.driver" /></xsl:attribute>                
                </Direct>
				<xsl:if test="config/logging.db.driver='com.ibm.db2.jcc.DB2Driver'">
                <connection-property name="fullyMaterializeLobData" value="true"/>
                <connection-property name="fullyMaterializeInputStreams" value="true"/>
                <connection-property name="progressiveStreaming" value="2"/>
                </xsl:if>
                <xsl:if test="string-length(config/logging.db.username) > 0">
                <InlineCredentials>
                    <xsl:attribute name="username"><xsl:value-of select="config/logging.db.username" /></xsl:attribute>
                    <xsl:attribute name="password"><xsl:value-of select="config/logging.db.password" /></xsl:attribute>
                </InlineCredentials>
                </xsl:if>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:TeneoResourceTemplate" name="TIBCO ActiveMatrix LogService Teneo Resource"
                description="Used by the TIBCO ActiveMatrix log service to access and use the database."
                contact="admin"
                schemaGeneration="update"
                sqlLogging="false"
                statementBatchSize="10"
                datasourceName="TIBCO ActiveMatrix LogService JDBC Resource">
                <xsl:attribute name="dialect"><xsl:value-of select="config/logging.db.dialect" /></xsl:attribute>
                <advancedProperties xsi:type="amxdata:Properties">
                    <Property xsi:type="amxdata:Property" name="currentSessionContextProvider" value="thread"/>
                    <Property xsi:type="amxdata:Property" name="maximumSqlNameLength" value="30"/>
                    <Property xsi:type="amxdata:Property" name="sqlCaseStrategy" value="uppercase"/>
                    <Property xsi:type="amxdata:Property" name="addIndexForForeignKey" value="true"/>
                </advancedProperties>
            </ResourceTemplate>
            <xsl:if test="config/mcr.ems.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate" name="TIBCO ActiveMatrix LogService JMSSslClient Resource"
                description="Used by the TIBCO ActiveMatrix log service to create a JMS SSL connection.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General"
                    trustStoreServiceProvider="TIBCO ActiveMatrix LogService JMSKeystoreProvider Resource"
                    enableAccessToTrustStore="true">
                </GeneralConfiguration>       
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate" name="TIBCO ActiveMatrix LogService JMSKeystoreProvider Resource"
                description="Used by the TIBCO ActiveMatrix log service to load a keystore while accessing a JMS Server using SSL.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/mcr.ems.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/mcr.ems.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/mcr.ems.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            </xsl:if>
            <LogAppender xsi:type="amxdata:FileLogAppender" name="TIBCO ActiveMatrix CommonLogging LogService FileAppender"
                description="File Appender for the TIBCO ActiveMatrix CommonLogging LogService Logger."
                filePath="../logs/CommonLogging_LogService.log"
                maxSize="102400"
                maxBackupNum="5"/>
            <LogAppender xsi:type="amxdata:JmsLogAppender" name="TIBCO ActiveMatrix CommonLogging LogService JmsAppender"
                description="This is a default jms LogAppender created by TIBCO ActiveMatrix CommonLogging LogService."
                jmsConnectionFactoryName="TIBCO ActiveMatrix LogService JMSConnectionFactory Resource"
                jmsConnectionName="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"
                jmsDestination="TIBCO ActiveMatrix LogService JMSDestination Resource"/>
            <amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate xsi:type="amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate"
                name="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"
                initialContextFactory="com.tibco.tibjms.naming.TibjmsInitialContextFactory"
                description="Used by the TIBCO ActiveMatrix log service to look up a JMS server.">
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix LogService JMSSslClient Resource</xsl:attribute>
                </xsl:if>
                <xsl:attribute name="providerUrl"><xsl:value-of select="config/mcr.ems.url" /></xsl:attribute>
                <xsl:if test="string-length(config/mcr.ems.username) > 0">
                <InlineCredentials> 
                    <xsl:attribute name="username"><xsl:value-of select="config/mcr.ems.username" /></xsl:attribute>
                    <xsl:if test="string-length(config/mcr.ems.password) > 0">
                    <xsl:attribute name="password"><xsl:value-of select="config/mcr.ems.password" /></xsl:attribute>
                    </xsl:if> 
                </InlineCredentials>
                </xsl:if>
            </amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate>
            <amxdata_jmsssr:JNDIConnectionFactoryResourceTemplate
                xsi:type="amxdata_jmsssr:JNDIConnectionFactoryResourceTemplate"
                name="TIBCO ActiveMatrix LogService JMSConnectionFactory Resource"
                jndiName="GenericConnectionFactory"
                jndiConnectionConfigurationName="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"
                description="Used by the TIBCO ActiveMatrix log service to create a connection to the JMS server.">
                <xsl:if test="admin/mcrinfo/ems/enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix LogService JMSSslClient Resource</xsl:attribute>
                </xsl:if>
                <xsl:if test="string-length(config/mcr.ems.username) > 0">
                <InlineCredentials> 
                    <xsl:attribute name="username"><xsl:value-of select="config/mcr.ems.username" /></xsl:attribute>
                    <xsl:if test="string-length(config/mcr.ems.password) > 0">
                    <xsl:attribute name="password"><xsl:value-of select="config/mcr.ems.password" /></xsl:attribute>
                    </xsl:if> 
                </InlineCredentials>
                </xsl:if>
            </amxdata_jmsssr:JNDIConnectionFactoryResourceTemplate>
            <amxdata_jmsssr:JNDIDestinationResourceTemplate xsi:type="amxdata_jmsssr:JNDIDestinationResourceTemplate" 
                name="TIBCO ActiveMatrix LogService JMSDestination Resource" 
                jndiName="cl_logservice_queue" 
                jndiConnectionConfigurationName="TIBCO ActiveMatrix LogService JNDIConnectionConfiguration Resource"
                description="Used by the TIBCO ActiveMatrix log service to retrieve logs."> 
                <xsl:if test="admin/mcrinfo/ems/enablessl= 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix LogService JMSSslClient Resource</xsl:attribute>
                </xsl:if>
            </amxdata_jmsssr:JNDIDestinationResourceTemplate>
        </amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>

