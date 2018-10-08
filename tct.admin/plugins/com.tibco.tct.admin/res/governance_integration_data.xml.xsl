<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" xmlns:str="http://exslt.org/strings" xmlns:fn="http://www.w3.org/2005/xpath-functions">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="2" />
    <xsl:param name="adminEnterpriseDir"/>
    <xsl:param name="adminInstanceName"/>

    <xsl:template match="/">
        <amxdata_base:Enterprise
            xmlns:amxdata="http://tibco.com/amxadministrator/command/line/types"
            xmlns:amxdata_base="http://tibco.com/amxadministrator/command/line/types_base"
            xmlns:amxdata_binding="http://tibco.com/amxadministrator/command/line/types_binding"
            xmlns:amxdata_reference="http://tibco.com/amxadministrator/command/line/types_reference"
            xmlns:amxdata_jmsssr="http://tibco.com/amxadministrator/command/line/types_jmssr"
            xmlns:amxdata_jmssr_base="http://tibco.com/amxadministrator/command/line/types_jmssr_base"
            xmlns:aggregatorConfig="http://tibco.com/governance/mcr/cli/types_aggregatorconfig"
            xmlns:govobj="http://tibco.com/governance/common/cli/types_govobj"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://tibco.com/amxadministrator/command/line/types_base ../schemas/amxdata_base.xsd http://tibco.com/amxadministrator/command/line/types ../schemas/amxdata.xsd  http://tibco.com/amxadministrator/command/line/types_jmssr ../schemas/jmssr.xsd http://tibco.com/amxadministrator/command/line/types_jmssr_base ../schemas/jmssr_base.xsd http://tibco.com/governance/common/cli/types_govobj ../schemas/govObj.xsd http://tibco.com/governance/mcr/cli/types_aggregatorconfig ../schemas/aggregatorConfig.xsd">

            <Environment xsi:type="amxdata:Environment" description="AMX system environment"
                contact="TIBCO Software Inc.">
                <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                <Node xsi:type="amxdata:Node" contact="TIBCO Software Inc.">
                    <xsl:choose>
                        <xsl:when test="config/mcr.aggregatorconfig.usedefault = 'false'">
                    <xsl:attribute name="name"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodename" /></xsl:attribute>
                    <xsl:attribute name="description"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodedesc" /></xsl:attribute>
                    <xsl:attribute name="hostName"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodedesc" /></xsl:attribute>
                    <xsl:attribute name="portNumber"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.port" /></xsl:attribute>
                    <xsl:attribute name="debugPortNumber"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.debugport" /></xsl:attribute>
                    <xsl:attribute name="enableDebug"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.enabledebug" /></xsl:attribute> 
                        </xsl:when>
                        <xsl:otherwise>
                    <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="config/mcr.ems.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance CSP keystore for EMS Resource" resourceTemplateName="TIBCO ActiveMatrix Governance CSP keystore for EMS Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance SSL Client for EMS Resource" resourceTemplateName="TIBCO ActiveMatrix Governance SSL Client for EMS Resource"/>
                    </xsl:if>
                    <xsl:if test="config/mcr.db.enablessl = 'true'">
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance CSP keystore for DB Resource" resourceTemplateName="TIBCO ActiveMatrix Governance CSP keystore for DB Resource"/>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance SSL Client for DB Resource" resourceTemplateName="TIBCO ActiveMatrix Governance SSL Client for DB Resource"/>
                    </xsl:if>
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance JDBC Resource" resourceTemplateName="TIBCO ActiveMatrix Governance JDBC Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance Hibernate Resource" resourceTemplateName="TIBCO ActiveMatrix Governance Hibernate Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance Teneo Resource" resourceTemplateName="TIBCO ActiveMatrix Governance Teneo Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance JNDI Connection Resource" resourceTemplateName="TIBCO ActiveMatrix Governance JNDI Connection Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance JMS ConnectionFactory Resource" resourceTemplateName="TIBCO ActiveMatrix Governance JMS ConnectionFactory Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance statistics JMS Destination Resource" resourceTemplateName="TIBCO ActiveMatrix Governance statistics JMS Destination Resource" />
                    <ResourceInstance xsi:type="amxdata:ResourceInstance" name="TIBCO ActiveMatrix Governance statistics internal JMS Destination Resource" resourceTemplateName="TIBCO ActiveMatrix Governance statistics internal JMS Destination Resource" />
                    <Logger xsi:type="amxdata:Logger" name="com.tibco.amx.governance.logger.LifecycleNotification" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef" effectiveLevel="INFO">
                            <Appender xsi:type="amxdata_reference:LogAppender_reference" name="amx.serviceprobe.global.log.appender" />
                        </AppenderRef>
                    </Logger>
                    <!--  Turn off hibernate logging. Any errors thrown will be logged in application code. -->    
                    <Logger xsi:type="amxdata:Logger" name="org.hibernate" additivity="false">
                        <AppenderRef xsi:type="amxdata:AppenderRef" effectiveLevel="OFF">
							<Appender xsi:type="amxdata_reference:LogAppender_reference">
                                <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" />_ROOT</xsl:attribute>
                            </Appender>
                        </AppenderRef>
                    </Logger>            
                </Node>
	       <ApplicationFolder xsi:type="amxdata:ApplicationFolder" name="System">
                <Application xsi:type="amxdata:Application" name="TIBCO ActiveMatrix Governance Aggregator" contact="TIBCO Software Inc." description="TIBCO ActiveMatrix Governance aggregator application for statistics. This is a System(platform) service and is required to be running for statistics to show up in the infrastructure dashboards.">
                    <Component xsi:type="amxdata:Component" name="LifecycleNotificationPublisherComponent">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        <Logger xsi:type="amxdata:Logger" name="com.tibco.amx.governance.logger.LifecycleNotification" additivity="false">
                            <AppenderRef xsi:type="amxdata:AppenderRef" effectiveLevel="INFO">
                                <Appender xsi:type="amxdata_reference:LogAppender_reference" name="amx.serviceprobe.global.log.appender" />
                            </AppenderRef>
                        </Logger>
                    </Component>
                    <Component xsi:type="amxdata:Component" name="Aggregator">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        <xsl:if test="config/mcr.aggregatorconfig.ftmode = 'true'">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodename" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        </xsl:if>
                    </Component>
                    <Component xsi:type="amxdata:Component" name="CLEventParserExtension">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        <xsl:if test="config/mcr.aggregatorconfig.ftmode = 'true'">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodename" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        </xsl:if>
                    </Component>
                    <Component xsi:type="amxdata:Component" name="StandardPeriodicWindowExtension">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        <xsl:if test="config/mcr.aggregatorconfig.ftmode = 'true'">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodename" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        </xsl:if>
                    </Component>
                    <Component xsi:type="amxdata:Component" name="StandardAggregateFunctionsExtension">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        <xsl:if test="config/mcr.aggregatorconfig.ftmode = 'true'">
                        <Node>
                            <xsl:attribute name="name"><xsl:value-of select="config/admin.mcrinfo.aggregatorconfig.nodename" /></xsl:attribute>
                            <xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                        </Node>
                        </xsl:if>
                    </Component>
                    <ApplicationTemplate xsi:type="amxdata_reference:ApplicationTemplate_reference" name="com.tibco.governance.mcr.aggregator.daa" />
                </Application>
	      </ApplicationFolder>
            </Environment>
            <xsl:if test="config/mcr.ems.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate"
                name="TIBCO ActiveMatrix Governance CSP keystore for EMS Resource"
                description="TIBCO ActiveMatrix Governance keystore credential service provider shared resource for EMS. Used for internal communications in the product. Do not delete this.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/mcr.ems.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/mcr.ems.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/mcr.ems.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate" 
                name="TIBCO ActiveMatrix Governance SSL Client for EMS Resource" 
                description="TIBCO ActiveMatrix Governance SSL Client shared resource for EMS. Used for internal communications in the product. Do not delete this.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General" 
                    trustStoreServiceProvider="TIBCO ActiveMatrix Governance CSP keystore for EMS Resource" 
                    enableAccessToTrustStore="true" />
            </ResourceTemplate>
            </xsl:if>
            <xsl:if test="config/mcr.db.enablessl = 'true'">
            <ResourceTemplate xsi:type="amxdata:KeystoreCspResourceTemplate"
                name="TIBCO ActiveMatrix Governance CSP keystore for DB Resource"
                description="TIBCO ActiveMatrix Governance keystore credential service provider shared resource for DB. Used for internal communications in the product. Do not delete this.">
                <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/mcr.db.keystorelocation" /></xsl:attribute>
                <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/mcr.db.keystorepassword" /></xsl:attribute>
                <xsl:attribute name="keyStoreType"><xsl:value-of select="config/mcr.db.keystoretype" /></xsl:attribute>
                <xsl:attribute name="keyStoreProvider"></xsl:attribute>
                <xsl:attribute name="keyStoreRefreshInterval">1000</xsl:attribute>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:SslClientResourceTemplate"
                name="TIBCO ActiveMatrix Governance SSL Client for DB Resource" description="TIBCO ActiveMatrix Governance SSL Client Shared Resource For DB. Used for internal communications in the product. Do not delete this.">
                <GeneralConfiguration xsi:type="amxdata:SslClientResourceTemplate_General"
                    trustStoreServiceProvider="TIBCO ActiveMatrix Governance CSP keystore for DB Resource"
                    enableAccessToTrustStore="true" />
            </ResourceTemplate>
            </xsl:if>
            <ResourceTemplate xsi:type="amxdata:JdbcResourceTemplate"
                name="TIBCO ActiveMatrix Governance JDBC Resource" description="TIBCO ActiveMatrix Governance JDBC shared resource to persist statistics. Used for internal communications in the product. Do not delete this.">
                <xsl:attribute name="maxConnections"><xsl:value-of select="config/mcr.db.maxconnections" /></xsl:attribute>
                <xsl:if test="config/mcr.db.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix Governance SSL Client for DB Resource</xsl:attribute>
                </xsl:if>
                <Direct xsi:type="amxdata:Direct" isTransactional="true" loginTimeOut="60000">
                    <xsl:attribute name="dbUrl"><xsl:value-of select="config/mcr.db.url" /></xsl:attribute>
                    <xsl:attribute name="jdbcDriver"><xsl:value-of select="config/mcr.db.driver" /></xsl:attribute>
                </Direct>
				<xsl:if test="config/mcr.db.driver='com.ibm.db2.jcc.DB2Driver'">
                <connection-property name="fullyMaterializeLobData" value="true"/>
                <connection-property name="fullyMaterializeInputStreams" value="true"/>
                <connection-property name="progressiveStreaming" value="2"/>
                </xsl:if>
                <xsl:if test="string-length(config/mcr.db.username) > 0">
                <InlineCredentials>
                    <xsl:attribute name="username"><xsl:value-of select="config/mcr.db.username" /></xsl:attribute>
                    <xsl:if test="string-length(config/mcr.db.password) > 0">
                    <xsl:attribute name="password"><xsl:value-of select="config/mcr.db.password" /></xsl:attribute>
                    </xsl:if>
                </InlineCredentials>
                </xsl:if>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:HibernateResourceTemplate"
                name="TIBCO ActiveMatrix Governance Hibernate Resource" description="TIBCO ActiveMatrix Governance Hibernate shared resource required for accessing statistics DB. Used for internal communications in the product. Do not delete this."
                schemaGeneration="update" sqlLogging="false" statementBatchSize="10" datasourceName="TIBCO ActiveMatrix Governance JDBC Resource">
                <xsl:attribute name="dialect"><xsl:value-of select="config/mcr.db.dialect" /></xsl:attribute>
                <advancedProperties xsi:type="amxdata:Properties">
                    <Property xsi:type="amxdata:Property" name="transactionManagerLookup"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFTibcoHostTransactionManagerLookup" />
                    <Property xsi:type="amxdata:Property" name="connectionProvider"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFConnectionProvider" />
                    <Property xsi:type="amxdata:Property" name="currentSessionContextProvider"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFSessionContext" />
                </advancedProperties>
            </ResourceTemplate>
            <ResourceTemplate xsi:type="amxdata:TeneoResourceTemplate"
                name="TIBCO ActiveMatrix Governance Teneo Resource" description="TIBCO ActiveMatrix Governance Teneo shared resource required for accessing statistics DB. Used for internal communications in the product. Do not delete this."
                schemaGeneration="update" sqlLogging="false" statementBatchSize="1"
                inheritanceMapping="JOINED" datasourceName="TIBCO ActiveMatrix Governance JDBC Resource">
                <xsl:attribute name="dialect"><xsl:value-of select="config/mcr.db.dialect" /></xsl:attribute>
                <advancedProperties xsi:type="amxdata:Properties">
                    <Property xsi:type="amxdata:Property" name="transactionManagerLookup"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFTibcoHostTransactionManagerLookup" />
                    <Property xsi:type="amxdata:Property" name="connectionProvider"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFConnectionProvider" />
                    <Property xsi:type="amxdata:Property" name="currentSessionContextProvider"
                        value="com.tibco.amf.sharedresource.runtime.core.hibernate.AMFSessionContext" />
                    <Property xsi:type="amxdata:Property" name="maximumSqlNameLength"
                        value="30" />
                    <Property xsi:type="amxdata:Property" name="addIndexForForeignKey" value="true"/>
                    <xsl:if test="config/mcr.db.dialect = 'com.tibco.amf.sharedresource.runtime.core.hibernate.dialects.DB2Dialect'">
		      <Property xsi:type="amxdata:Property" name="sqlNameEscapeCharacter" value="\"/>
		    </xsl:if>
                </advancedProperties>
            </ResourceTemplate>
            <LogAppender xsi:type="amxdata:JmsLogAppender"
                name="amx.serviceprobe.global.log.appender"
                jmsConnectionFactoryName="TIBCO ActiveMatrix Governance JMS ConnectionFactory Resource"
                jmsConnectionName="TIBCO ActiveMatrix Governance JNDI Connection Resource" type="jndi"
                description="TIBCO ActiveMatrix Common Logging JMS appender for global Governance service probe. Statistics collected from every node is logged to this JMS appender for aggregator to use. Used for internal communications in the product. Do not delete this."
                jmsDestination="TIBCO ActiveMatrix Governance statistics JMS Destination Resource" />
            <amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate
                xsi:type="amxdata_jmsssr:JNDIConnectionConfigurationResourceTemplate"
                name="TIBCO ActiveMatrix Governance JNDI Connection Resource" initialContextFactory="com.tibco.tibjms.naming.TibjmsInitialContextFactory"
                description="TIBCO ActiveMatrix Governance JNDI connection resource configuration for receiving statistics emitted by metrics collectors that run on every node. Used for internal communications in the product. Do not delete this."> 
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix Governance SSL Client for EMS Resource</xsl:attribute>
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
                name="TIBCO ActiveMatrix Governance JMS ConnectionFactory Resource" jndiName="QueueConnectionFactory"
                jndiConnectionConfigurationName="TIBCO ActiveMatrix Governance JNDI Connection Resource"
                description="TIBCO ActiveMatrix Governance JNDI Connection factory resource configuration for JNDI connection used for receiving statistics. Used for internal communications in the product. Do not delete this."> 
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix Governance SSL Client for EMS Resource</xsl:attribute>
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
                xsi:type="amxdata_jmsssr:JNDIDestinationResourceTemplate" name="TIBCO ActiveMatrix Governance statistics JMS Destination Resource"
                jndiName="direct.queue.amx.governance.stats"
                jndiConnectionConfigurationName="TIBCO ActiveMatrix Governance JNDI Connection Resource"
                description="TIBCO ActiveMatrix Governance statistics destination resource configuration. The queue name specified in this destination is used for statistics in EMS server. Used for internal communications in the product. Do not delete this."> 
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix Governance SSL Client for EMS Resource</xsl:attribute>
                </xsl:if>
            </amxdata_jmsssr:JNDIDestinationResourceTemplate>
            <amxdata_jmsssr:JNDIDestinationResourceTemplate
                xsi:type="amxdata_jmsssr:JNDIDestinationResourceTemplate" name="TIBCO ActiveMatrix Governance statistics internal JMS Destination Resource"
                jndiName="direct.queue.amx.governance.internal.stats"
                jndiConnectionConfigurationName="TIBCO ActiveMatrix Governance JNDI Connection Resource"
                description="TIBCO ActiveMatrix Governance internal statistics destination resource configuration. The queue name specified in this destination is used for statistics in EMS server. Used for internal communications in the product. Do not delete this."> 
                <xsl:if test="config/mcr.ems.enablessl = 'true'">
                <xsl:attribute name="sslJNDIName">TIBCO ActiveMatrix Governance SSL Client for EMS Resource</xsl:attribute>
                </xsl:if>
            </amxdata_jmsssr:JNDIDestinationResourceTemplate>
            <govobj:GovernedObjectSync />
            <xsl:if test="config/mcr.general.config.extended.monitoring.enabled = 'true'">
            <aggregatorConfig:FactoryAggregatorConfig>
                <name>extended</name>
            </aggregatorConfig:FactoryAggregatorConfig>
            <aggregatorConfig:AggregatorConfig>
                <identifier>extended.aggregatordefinition</identifier>
                <majorVersion>1</majorVersion>
                <minorVersion>100</minorVersion>
                <dataFile>extended.aggregatordefinition</dataFile>
            </aggregatorConfig:AggregatorConfig>
            </xsl:if>
        </amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>
