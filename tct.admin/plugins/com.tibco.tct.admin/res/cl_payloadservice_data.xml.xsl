<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="2" />

    <xsl:template match="/">
        <amxdata_base:Enterprise xmlns:amxdata_jmsssr="http://tibco.com/amxadministrator/command/line/types_jmssr"
                 xmlns:amxdata="http://tibco.com/amxadministrator/command/line/types" 
                                 xmlns:amxdata_base="http://tibco.com/amxadministrator/command/line/types_base" 
                                 xmlns:amxdata_reference="http://tibco.com/amxadministrator/command/line/types_reference" 
                                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                                 xsi:schemaLocation="http://tibco.com/amxadministrator/command/line/types_base ../schemas/amxdata_base.xsd http://tibco.com/amxadministrator/command/line/types ../schemas/amxdata.xsd">

            <Environment xsi:type="amxdata:Environment" description="AMX system environment" contact="TIBCO Software Inc.">
                <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                <Node xsi:type="amxdata:Node" contact="TIBCO Software Inc.">
                    <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                    <xsl:if test="config/mcr.ems.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JMSSslClient Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JMSSslClient Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JMSKeystoreProvider Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JMSKeystoreProvider Resource"/>
                    </xsl:if>
                    <xsl:if test="config/payload.db.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService DBKeystoreProvider Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService DBKeystoreProvider Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService DBSslClient Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService DBSslClient Resource"/>
                    </xsl:if>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JDBC Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JDBC Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService Teneo Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService Teneo Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JMSConnectionFactory Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JMSConnectionFactory Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix PayloadService JMSDestination Resource" resourceTemplateName="TIBCO ActiveMatrix PayloadService JMSDestination Resource"/>
                </Node>
                <Application xsi:type="amxdata:Application" name="com.tibco.amx.commonlogging.payloadservice.app" contact="TIBCO Software Inc." description="TIBCO ActiveMatrix CommonLogging PayloadService Application is the product application responsible for getting payload datas from EMS server and saving these datas into DB, it also provides interface for searching,purging payload datas from DB." folderPath="/System">
                    <Component xsi:type="amxdata:Component" name="PayloadComp">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node> 
                    </Component>
                    <Node>
                        <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                        <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                    </Node> 
                    <SVar xsi:type="amxdata_base:SubstitutionVariable" name="fileRootDir" type="String" value="./payload" description="Payload file root dir"/>
                    <ApplicationTemplate xsi:type="amxdata_reference:ApplicationTemplate_reference" name="com.tibco.commonlogging.payload.daa"/>
                </Application>
            </Environment>
            <xsl:if test="config/payload.db.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate" name="TIBCO ActiveMatrix PayloadService DBKeystoreProvider Resource"
                description="Used by the TIBCO ActiveMatrix payload service to load a keystore while accessing a Database using SSL.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/payload.db.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/payload.db.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/payload.db.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreProvider"></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate"
                name="TIBCO ActiveMatrix PayloadService DBSslClient Resource" description="Used by the TIBCO ActiveMatrix payload service to create a DB SSL connection.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General"
                    trustStoreServiceProvider="TIBCO ActiveMatrix PayloadService DBKeystoreProvider Resource"
                    enableAccessToTrustStore="true" />
            </ResourceTemplate>
            </xsl:if>
            <ResourceTemplate xsi:type="amxdata:TeneoResourceTemplate" name="TIBCO ActiveMatrix PayloadService Teneo Resource"
                description="Used by the TIBCO ActiveMatrix payload service to access and use the database."
                contact="admin"
                schemaGeneration="update"
                sqlLogging="false"
                statementBatchSize="10"
                datasourceName="TIBCO ActiveMatrix PayloadService JDBC Resource">
                <xsl:attribute name="dialect"><xsl:value-of select="config/payload.db.dialect" /></xsl:attribute>
                <advancedProperties xsi:type="amxdata:Properties">
                    <Property xsi:type="amxdata:Property" name="currentSessionContextProvider" value="thread"/>
                    <Property xsi:type="amxdata:Property" name="maximumSqlNameLength" value="30"/>
                    <Property xsi:type="amxdata:Property" name="sqlCaseStratrgy" value="uppercase"/>
                    <Property xsi:type="amxdata:Property" name="addIndexForForeignKey" value="true"/>
                </advancedProperties>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:JdbcResourceTemplate" name="TIBCO ActiveMatrix PayloadService JDBC Resource"
                description="Used by the TIBCO ActiveMatrix payload service to connect to the database.">
                <xsl:attribute name="maxConnections"><xsl:value-of select="config/payload.db.maxconnections" /></xsl:attribute>       
                <xsl:if test="config/payload.db.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix PayloadService DBSslClient Resource</xsl:attribute>
                </xsl:if>
                <Direct xsi:type="amxdata:Direct" isTransactional="true" loginTimeOut="60000">
                    <xsl:attribute name="dbUrl"><xsl:value-of select="config/payload.db.url" /></xsl:attribute>    
                    <xsl:attribute name="jdbcDriver"><xsl:value-of select="config/payload.db.driver" /></xsl:attribute>                
                </Direct>
				<xsl:if test="config/payload.db.driver='com.ibm.db2.jcc.DB2Driver'">
                <connection-property name="fullyMaterializeLobData" value="true"/>
                <connection-property name="fullyMaterializeInputStreams" value="true"/>
                <connection-property name="progressiveStreaming" value="2"/>
                </xsl:if>
                <InlineCredentials>
                    <xsl:attribute name="username"><xsl:value-of select="config/payload.db.username" /></xsl:attribute>
                    <xsl:attribute name="password"><xsl:value-of select="config/payload.db.password" /></xsl:attribute>
                </InlineCredentials>
            </ResourceTemplate>
            <xsl:if test="config/mcr.ems.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate"
                name="TIBCO ActiveMatrix PayloadService JMSSslClient Resource"
                description="Used by the TIBCO ActiveMatrix payload service to create a JMS SSL connection.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General"
                    trustStoreServiceProvider="TIBCO ActiveMatrix PayloadService JMSKeystoreProvider Resource"
                    enableAccessToTrustStore="true">
                </GeneralConfiguration>       
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate"
                name="TIBCO ActiveMatrix PayloadService JMSKeystoreProvider Resource"
                description="Used by the TIBCO ActiveMatrix payload service to load a keystore while accessing a JMS Server using SSL.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/mcr.ems.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/mcr.ems.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/mcr.ems.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            </xsl:if>

            <amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate
                xsi:type="amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate"
                name="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource"
                initialContextFactory="com.tibco.tibjms.naming.TibjmsInitialContextFactory"
                description="Used by the TIBCO ActiveMatrix payload service to look up a JMS server.">
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix PayloadService JMSSslClient Resource</xsl:attribute>
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
                <!--
                    ReferencedCredentials
                    identityJNDIName="JNDIConnectionConfiguration_Identity"/
                -->
            </amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate>
            
            <amxdata_jmsssr:JNDIConnectionFactoryResourceTemplate
                xsi:type="amxdata_jmsssr:JNDIConnectionFactoryResourceTemplate"
                name="TIBCO ActiveMatrix PayloadService JMSConnectionFactory Resource"
                jndiName="GenericConnectionFactory"
                jndiConnectionConfigurationName="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource"
                description="Used by the TIBCO ActiveMatrix payload service to create a connection to the JMS server.">
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                    <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix PayloadService JMSSslClient Resource</xsl:attribute>
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

            <amxdata_jmsssr:JNDIDestinationResourceTemplate 
                xsi:type="amxdata_jmsssr:JNDIDestinationResourceTemplate" 
                name="TIBCO ActiveMatrix PayloadService JMSDestination Resource" 
                jndiName="cl_payload_queue" 
                jndiConnectionConfigurationName="TIBCO ActiveMatrix PayloadService JNDIConnectionConfiguration Resource"
                description="Used by the TIBCO ActiveMatrix payload service to retrieve logs."> 
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                    <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix PayloadService JMSSslClient Resource</xsl:attribute>
                </xsl:if>
            </amxdata_jmsssr:JNDIDestinationResourceTemplate>

  </amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>
