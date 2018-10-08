<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="2" />

    <xsl:template match="/">

    <xsl:variable name="th.jmx.url">
        <xsl:text>service:jmx:jmxmp://</xsl:text>
        <xsl:variable name="isIpv6" select="contains(config/th.jmx.host,':')"/>
        <xsl:if test="$isIpv6='true'">[</xsl:if>
        <xsl:value-of select="config/th.jmx.host" />
        <xsl:if test="$isIpv6='true'">]</xsl:if>
		<xsl:text>:</xsl:text>
        <xsl:value-of select="config/th.jmx.port" />
    </xsl:variable>

<amxdata_base:Enterprise
    xmlns:amxdata_bootstrap="http://tibco.com/amf/hpa/core/admin/bootstrap/types"
    xmlns:amxdata_base="http://tibco.com/amxadministrator/command/line/types_base"
    xmlns:amxdata_reference="http://tibco.com/amxadministrator/command/line/types_reference"
    xmlns:amxdata="http://tibco.com/amxadministrator/command/line/types"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:cs="http://tibco.com/trinity/server/credentialserver/cmdline/credentialserver" 
    xsi:schemaLocation="http://tibco.com/amxadministrator/command/line/types_base ../schemas/amxdata_base.xsd http://tibco.com/trinity/server/credentialserver/cmdline/credentialserver ../schemas/CredentialServer.xsd http://tibco.com/amf/hpa/core/admin/bootstrap/types ../schemas/amxdata_bootstrap.xsd http://tibco.com/amxadministrator/command/line/types ../schemas/amxdata.xsd">

    <AdminConfiguration xsi:type="amxdata_bootstrap:AdminConfiguration">
        <xsl:attribute name="name"><xsl:value-of select="config/admin.enterprise.name" /></xsl:attribute>
        <xsl:attribute name="initialSuperuser"><xsl:value-of select="config/admin.authenticationrealm.username" /></xsl:attribute>
        <xsl:attribute name="initialSuperuserPassword"><xsl:value-of select="config/admin.authenticationrealm.password" /></xsl:attribute>
        <xsl:attribute name="httpSessionTimeout"><xsl:value-of select="config/serverconnsetting.timeout" /></xsl:attribute>
        <StatusTransportDetails recoveryTimerTimeout="15000" recoveryAttemptDelay="500">
            <xsl:attribute name="groupName"><xsl:value-of select="config/admin.enterprise.name" /></xsl:attribute>
            <xsl:attribute name="userName"><xsl:value-of select="config/admin.ems.username" /></xsl:attribute>
            <xsl:attribute name="password"><xsl:value-of select="config/admin.ems.password" /></xsl:attribute>
            <xsl:attribute name="serverURL"><xsl:value-of select="config/admin.ems.url" /></xsl:attribute>
            <xsl:if test="config/admin.ems.enablessl='true'">
            <xsl:attribute name="enableSSL">true</xsl:attribute>
            </xsl:if>
            <xsl:if test="config/admin.ems.enablessl='true'">
            <!--SSL configuration for application database -->
            <SSLConfig> 
                <KeyStoreResource name="tibco.admin.qin.ssl.truststore" keyStoreRefreshInterval="60000">
                    <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/admin.ems.keystorepassword" /></xsl:attribute>
                    <xsl:attribute name="keyStoreType"><xsl:value-of select="config/admin.ems.keystoretype" /></xsl:attribute>
                    <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/admin.ems.keystorelocation" /></xsl:attribute>
                </KeyStoreResource>
                <SSLClientResource xsi:type="amxdata_bootstrap:SslClientResource" name="tibco.admin.qin.ssl.client" description="This is SSL Client"> 
                    <GeneralConfiguration xsi:type="amxdata_bootstrap:SslClientResource_General" trustStoreServiceProvider="tibco.admin.qin.ssl.truststore" enableAccessToTrustStore="true" />
                </SSLClientResource> 
            </SSLConfig>
            </xsl:if>
        </StatusTransportDetails>

        <AdminConfigFolders>
            <xsl:attribute name="instanceWorkFolder"><xsl:text>${admin.enterprise.dir}/private/${admin.instance.name}</xsl:text></xsl:attribute>
            <xsl:attribute name="sharedWorkFolder"><xsl:value-of select="config/admin.shared.folder" /></xsl:attribute>
            <xsl:attribute name="productStagingFolder"><xsl:value-of select="config/tibco.home" /></xsl:attribute>
        </AdminConfigFolders>

        <AppDatabaseDetails jndiName="AdminTeneoResource" name="tibco.admin.appdb" schemaGeneration="update">
            <xsl:attribute name="dialect"><xsl:value-of select="config/admin.db.dialect" /></xsl:attribute>
            <xsl:if test="config/admin.db.usedefault='false' and config/admin.db.enablessl='true'">
            <!--SSL configuration for application database -->
            <SSLConfig> 
                <KeyStoreResource name="tibco.admin.default.ssl.truststore" keyStoreRefreshInterval="1000">
                    <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/admin.db.keystorepassword" /></xsl:attribute>
                    <xsl:attribute name="keyStoreType"><xsl:value-of select="config/admin.db.keystoretype" /></xsl:attribute>
                    <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/admin.db.keystorelocation" /></xsl:attribute>
                </KeyStoreResource>
                <SSLClientResource xsi:type="amxdata_bootstrap:SslClientResource" name="tibco.admin.default.ssl.client.jdbc" description="This is SSL Client"> 
                    <GeneralConfiguration xsi:type="amxdata_bootstrap:SslClientResource_General" trustStoreServiceProvider="tibco.admin.default.ssl.truststore" enableAccessToTrustStore="true" />
                </SSLClientResource> 
            </SSLConfig>
            </xsl:if>
            <!--Teneo configuration for application database -->
            <TeneoResource xsi:type="amxdata_bootstrap:TeneoResource"> 
                <advancedProperties xsi:type="amxdata_bootstrap:Properties">
                    <Property xsi:type="amxdata_bootstrap:Property" name="maximumSqlNameLength" value="30"/>
                    <Property xsi:type="amxdata_bootstrap:Property" name="sqlCaseStratrgy" value="uppercase"/>
                    <Property xsi:type="amxdata_bootstrap:Property" name="currentSessionContextProvider" value="thread"/>
                    <Property xsi:type="amxdata_bootstrap:Property" name="emapAsTrueMap" value="false"/>
                    <Property xsi:type="amxdata_bootstrap:Property" name="addIndexForForeignKey" value="true"/>
                </advancedProperties>
            </TeneoResource>
            <xsl:choose>
                <xsl:when test="config/admin.db.usedefault='false'">
            <JdbcResourceTemplate xsi:type="amxdata_bootstrap:JdbcResourceTemplate" name="ApplicationDB" description="Database for AMX Application">
                <xsl:attribute name="maxConnections"><xsl:value-of select="config/admin.db.maxconnections" /></xsl:attribute>
                <Direct xsi:type="amxdata_bootstrap:Direct" isTransactional="false" loginTimeOut="30000">
                    <xsl:attribute name="dbUrl"><xsl:value-of select="config/admin.db.url" /></xsl:attribute>
                    <xsl:attribute name="jdbcDriver"><xsl:value-of select="config/admin.db.driver" /></xsl:attribute>
                </Direct>
                    <xsl:if test="config/admin.db.driver='com.ibm.db2.jcc.DB2Driver'">
                <connection-property name="fullyMaterializeLobData" value="true"/>
                <connection-property name="fullyMaterializeInputStreams" value="true"/>
                <connection-property name="progressiveStreaming" value="2"/>
                    </xsl:if>
                <InlineCredentials>
                    <xsl:attribute name="username"><xsl:value-of select="config/admin.db.username" /></xsl:attribute>
                    <xsl:attribute name="password"><xsl:value-of select="config/admin.db.password" /></xsl:attribute>
                </InlineCredentials>
            </JdbcResourceTemplate>
                </xsl:when>
                <xsl:otherwise>
            <InProcessDatabase databaseAlias="amx">
			    <xsl:attribute name="dataDirectory"><xsl:text>${admin.enterprise.dir}/private/${admin.instance.name}/hsqldb/amx</xsl:text></xsl:attribute>
			</InProcessDatabase>
                </xsl:otherwise>
            </xsl:choose>
        </AppDatabaseDetails>

        <AuthRealm jaasJndiName="tibco.admin.authrealm.jaas" rmsJndiName="tibco.admin.authrealm.rms">
            <xsl:if test="config/admin.authenticationrealm.type='dbrealm'">
            <xsl:attribute name="realmType">database</xsl:attribute>
            <DbRealmDetails>
            <xsl:choose>
                <xsl:when test="config/admin.dbrealm.usedefault='true'">
                <AMXApplicationDB />
                </xsl:when>
                <xsl:otherwise>
                <ExternalDBRealm dsJndiName="tibco.admin.authrealm.external" schemaGeneration="update">
                    <xsl:attribute name="dialect"><xsl:value-of select="config/admin.dbrealm.dialect" /></xsl:attribute> 
                    <xsl:if test="config/admin.dbrealm.usedefault='false' and config/admin.dbrealm.enablessl='true'">
                    <!--SSL configuration for external database for db realm-->
                    <SSLConfig> 
                        <KeyStoreResource name="tibco.admin.default.ssl.truststore.realm" keyStoreRefreshInterval="1000">
                            <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/admin.dbrealm.keystorepassword" /></xsl:attribute>
                            <xsl:attribute name="keyStoreType"><xsl:value-of select="config/admin.dbrealm.keystoretype" /></xsl:attribute>
                            <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/admin.dbrealm.keystorelocation" /></xsl:attribute>
                        </KeyStoreResource>
                        <SSLClientResource xsi:type="amxdata_bootstrap:SslClientResource" name="tibco.admin.default.ssl.client.realm" description="This is SSL Client"> 
                            <GeneralConfiguration xsi:type="amxdata_bootstrap:SslClientResource_General" trustStoreServiceProvider="tibco.admin.default.ssl.truststore.realm" enableAccessToTrustStore="true" /> 
                        </SSLClientResource> 
                    </SSLConfig>
                    </xsl:if>
                    <TeneoResource xsi:type="amxdata_bootstrap:TeneoResource"> 
                        <advancedProperties xsi:type="amxdata_bootstrap:Properties">
                            <Property xsi:type="amxdata_bootstrap:Property" name="maximumSqlNameLength" value="30"/>
                            <Property xsi:type="amxdata_bootstrap:Property" name="hbm2DdlAuto" value="update"/>
                            <Property xsi:type="amxdata_bootstrap:Property" name="sqlCaseStratrgy" value="uppercase"/>
                            <Property xsi:type="amxdata_bootstrap:Property" name="currentSessionContextProvider" value="thread"/>
                            <Property xsi:type="amxdata_bootstrap:Property" name="emapAsTrueMap" value="false"/>
                            <Property xsi:type="amxdata_bootstrap:Property" name="dialect">
                                <xsl:attribute name="value"><xsl:value-of select="config/admin.dbrealm.dialect" /></xsl:attribute>
                            </Property>
                            <Property xsi:type="amxdata_bootstrap:Property" name="addIndexForForeignKey" value="true"/>
                        </advancedProperties>
                    </TeneoResource>
                    <JdbcResourceTemplate xsi:type="amxdata_bootstrap:JdbcResourceTemplate" name="AuthRealmDB" description="External Database for AuthRealm">
                        <xsl:attribute name="maxConnections"><xsl:value-of select="config/admin.dbrealm.maxconnections" /></xsl:attribute>
                        <Direct xsi:type="amxdata_bootstrap:Direct" isTransactional="false" loginTimeOut="30000">
                            <xsl:attribute name="dbUrl"><xsl:value-of select="config/admin.dbrealm.url" /></xsl:attribute>
                            <xsl:attribute name="jdbcDriver"><xsl:value-of select="config/admin.dbrealm.driver" /></xsl:attribute>
                        </Direct>
						<xsl:if test="config/admin.dbrealm.driver='com.ibm.db2.jcc.DB2Driver'">
                        <connection-property name="fullyMaterializeLobData" value="true"/>
                        <connection-property name="fullyMaterializeInputStreams" value="true"/>
                        <connection-property name="progressiveStreaming" value="2"/>
                        </xsl:if>
                        <InlineCredentials>
                            <xsl:attribute name="username"><xsl:value-of select="config/admin.dbrealm.username" /></xsl:attribute>
                            <xsl:attribute name="password"><xsl:value-of select="config/admin.dbrealm.password" /></xsl:attribute>
                        </InlineCredentials>
                    </JdbcResourceTemplate>
                 </ExternalDBRealm>         
                </xsl:otherwise>
            </xsl:choose>
            </DbRealmDetails>      
            </xsl:if>
            <xsl:if test="config/admin.authenticationrealm.type='ldaprealm'">
            <xsl:attribute name="realmType">ldap</xsl:attribute>
            <LdapRealmDetails>
                <xsl:attribute name="LdapBindDNName"><xsl:value-of select="config/admin.ldaprealm.LdapBindDNName" /></xsl:attribute>
                <xsl:attribute name="LdapBindDNPassword"><xsl:value-of select="config/admin.ldaprealm.LdapBindDNPassword" /></xsl:attribute>
                <xsl:attribute name="initialCtxFactory"><xsl:value-of select="config/admin.ldaprealm.initialCtxFactory" /></xsl:attribute>
                <xsl:attribute name="serverURL"><xsl:value-of select="config/admin.ldaprealm.serverURL" /></xsl:attribute>
                <xsl:attribute name="userSearchExpression"><xsl:value-of select="config/admin.ldaprealm.userSearchExpression" /></xsl:attribute>
                <xsl:attribute name="userAttributeUsersName"><xsl:value-of select="config/admin.ldaprealm.userAttributeUsersName" /></xsl:attribute>
                <xsl:attribute name="userSearchBaseDN"><xsl:value-of select="config/admin.ldaprealm.userSearchBaseDN" /></xsl:attribute>
                <xsl:attribute name="groupSearchBaseDN"><xsl:value-of select="config/admin.ldaprealm.groupSearchBaseDN" /></xsl:attribute>
                <xsl:attribute name="groupSearchScopeSubtree"><xsl:value-of select="config/admin.ldaprealm.groupSearchScopeSubtree" /></xsl:attribute>
                <xsl:attribute name="groupSearchExpression"><xsl:value-of select="config/admin.ldaprealm.groupSearchExpression" /></xsl:attribute>
                <xsl:attribute name="groupAttributeGroupsName"><xsl:value-of select="config/admin.ldaprealm.groupAttributeGroupsName" /></xsl:attribute>
                <xsl:attribute name="groupIndication"><xsl:value-of select="config/admin.ldaprealm.groupIndication" /></xsl:attribute>
                <xsl:choose>
                    <xsl:when test="config/admin.ldaprealm.groupIndication='groupHasUsers'">
                        <xsl:attribute name="groupAttributeUsersName"><xsl:value-of select="config/admin.ldaprealm.groupAttributeUsersName" /></xsl:attribute>
                    </xsl:when>
                    <xsl:when test="config/admin.ldaprealm.groupIndication='userHasGroups'">
                        <xsl:attribute name="userAttributeGroupsName"><xsl:value-of select="config/admin.ldaprealm.userAttributeGroupsName" /></xsl:attribute>
                    </xsl:when>
                    <xsl:when test="config/admin.ldaprealm.groupIndication='userDNHasGroups'">
                        <xsl:attribute name="userAttributeGroupsName"><xsl:value-of select="config/admin.ldaprealm.userAttributeGroupsName" /></xsl:attribute>
                    </xsl:when>
                </xsl:choose>
                <xsl:attribute name="groupAttributeSubgroupsName"><xsl:value-of select="config/admin.ldaprealm.groupAttributeSubgroupsName" /></xsl:attribute>
                <xsl:attribute name="searchTimeOut"><xsl:value-of select="config/admin.ldaprealm.searchTimeOut" /></xsl:attribute>
                <xsl:attribute name="userAttributesExtra"><xsl:value-of select="config/admin.ldaprealm.userAttributesExtra" /></xsl:attribute>
                <xsl:attribute name="userSearchScopeSubtree"><xsl:value-of select="config/admin.ldaprealm.userSearchScopeSubtree" /></xsl:attribute>
                <xsl:attribute name="keyPassword"><xsl:value-of select="config/admin.ldaprealm.keyPassword" /></xsl:attribute>
                <xsl:attribute name="securityAuthentication"><xsl:value-of select="config/admin.ldaprealm.securityAuthentication" /></xsl:attribute>
                <xsl:if test="config/admin.ldaprealm.enablessl='true'">
                <!--SSL configuration for external database for db realm-->
                <SSLConfig> 
                    <KeyStoreResource name="tibco.admin.default.ssl.truststore.realm" keyStoreRefreshInterval="1000">
                        <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/admin.ldaprealm.keystorepassword" /></xsl:attribute>
                        <xsl:attribute name="keyStoreType"><xsl:value-of select="config/admin.ldaprealm.keystoretype" /></xsl:attribute>
                        <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/admin.ldaprealm.keystorelocation" /></xsl:attribute>
                    </KeyStoreResource>
                    <SSLClientResource xsi:type="amxdata_bootstrap:SslClientResource" name="tibco.admin.default.ssl.client.realm" description="This is SSL Client"> 
                        <GeneralConfiguration xsi:type="amxdata_bootstrap:SslClientResource_General" trustStoreServiceProvider="tibco.admin.default.ssl.truststore.realm" enableAccessToTrustStore="true" />
                    </SSLClientResource> 
                </SSLConfig>
                </xsl:if>
            </LdapRealmDetails>
        </xsl:if>
        </AuthRealm>

        <AdminKeyStore adminKeyStoreJNDI="tibco.admin.default.keystore" adminKeyStorePassword="#!HNiBLkB++xHg09/wmUMpHvGoJgFAE0j7YW0+5FIcpIo=" keyStoreRefresh="86400000" />

        <SCMDetails type="filesystem">
            <xsl:attribute name="location"><xsl:value-of select="config/admin.shared.folder" />/repo</xsl:attribute>
        </SCMDetails>

        <TrinityCredentialServerDetails>
            <xsl:attribute name="host"><xsl:value-of select="config/admin.cs.host" /></xsl:attribute>
            <xsl:attribute name="port"><xsl:value-of select="config/admin.cs.port" /></xsl:attribute>
            <xsl:attribute name="username"><xsl:value-of select="config/admin.cs.username" /></xsl:attribute>
            <xsl:attribute name="password"><xsl:value-of select="config/admin.cs.password" /></xsl:attribute>
        </TrinityCredentialServerDetails>

        <!-- applicable only for Tibco Host -->
        <TibcoHostConfig>
            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemhost" /></xsl:attribute>
            <xsl:attribute name="jmxUrl"><xsl:value-of select="$th.jmx.url" /></xsl:attribute>
            <xsl:if test="config/serverconnsetting.usebaseurl='true'">
            <xsl:attribute name="serverBaseUrl"><xsl:value-of select="config/serverconnsetting.baseurl" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="config/provisiondrivers.enable='true'">
            <ProvisionDrivers xsi:type="amxdata_bootstrap:ProvisionDrivers"> 
                <ProvisionFeatures xsi:type="amxdata_bootstrap:ProvisionFeatures"> 
                <xsl:if test="config/provisiondrivers.provisionMode='resolve'">
                    <xsl:attribute name="provisionMode">resolve</xsl:attribute>
                </xsl:if>
                <xsl:if test="config/provisiondriver1.id">
                    <ProvisionFeature xsi:type="amxdata_bootstrap:ProvisionFeature">
                        <xsl:attribute name="featureID"><xsl:value-of select="config/provisiondriver1.id" /></xsl:attribute>
                        <xsl:attribute name="version"><xsl:value-of select="config/provisiondriver1.version" /></xsl:attribute>
                    </ProvisionFeature>
                </xsl:if>
				<xsl:if test="config/provisiondriver2.id">
                    <ProvisionFeature xsi:type="amxdata_bootstrap:ProvisionFeature">
                        <xsl:attribute name="featureID"><xsl:value-of select="config/provisiondriver2.id" /></xsl:attribute>
                        <xsl:attribute name="version"><xsl:value-of select="config/provisiondriver2.version" /></xsl:attribute>
                    </ProvisionFeature>
                </xsl:if>
				<xsl:if test="config/provisiondriver3.id">
                    <ProvisionFeature xsi:type="amxdata_bootstrap:ProvisionFeature">
                        <xsl:attribute name="featureID"><xsl:value-of select="config/provisiondriver3.id" /></xsl:attribute>
                        <xsl:attribute name="version"><xsl:value-of select="config/provisiondriver3.version" /></xsl:attribute>
                    </ProvisionFeature>
                </xsl:if>
                </ProvisionFeatures>
            </ProvisionDrivers>
            </xsl:if>
            <ConnectorDetails>
                <xsl:attribute name="port"><xsl:value-of select="config/serverconnsetting.port" /></xsl:attribute>
                <xsl:attribute name="host"><xsl:value-of select="config/serverconnsetting.host" /></xsl:attribute>
                <xsl:if test="config/serverconnsetting.enablessl='true'">
                <SSLConfig> 
                    <KeyStoreResource xsi:type="amxdata_bootstrap:KeystoreCspResource" name="SslKeyStoreRT" keyStoreRefreshInterval = "1000">
                        <xsl:attribute name="keyStorePassword"><xsl:value-of select="config/serverconnsetting.keystorepassword" /></xsl:attribute>
                        <xsl:attribute name="keyStoreType"><xsl:value-of select="config/serverconnsetting.keystoretype" /></xsl:attribute>
                        <xsl:attribute name="keyStoreLocation"><xsl:value-of select="config/serverconnsetting.keystorelocation" /></xsl:attribute>
                    </KeyStoreResource>
                    <SSLServerResource xsi:type="amxdata_bootstrap:SslServerResource" name="SslServerRT" description="This is SSL Server RT"> 
                        <GeneralConfiguration xsi:type="amxdata_bootstrap:SslServerResource_General" identityStoreServiceProvider="tibco.admin.default.ssl.keystore" 
                            enableCredentialStoreAccess="true">
                            <xsl:attribute name="keyAlias"><xsl:value-of select="config/serverconnsetting.keyalias" /></xsl:attribute>
                            <xsl:attribute name="keyPassword"><xsl:value-of select="config/serverconnsetting.keypassword" /></xsl:attribute> 
                        </GeneralConfiguration>
                    </SSLServerResource> 
                </SSLConfig>
                </xsl:if>
            </ConnectorDetails>
            <NodeDetails>
                <xsl:attribute name="envName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
                <xsl:attribute name="nodeName"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
                <xsl:attribute name="portNumber"><xsl:value-of select="config/serverconnsetting.managementport" /></xsl:attribute>
                <xsl:attribute name="loggingConfigPath"><xsl:value-of select="config/logging.config.properties.file" /></xsl:attribute>
            </NodeDetails>
        </TibcoHostConfig>
    </AdminConfiguration>

    <Environment xsi:type="amxdata:Environment">
        <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
        <Node>
            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
            <ResourceInstance xsi:type="amxdata:ResourceInstance"
                name="TIBCO ActiveMatrix Internal HTTP Connector Resource" resourceTemplateName="TIBCO ActiveMatrix Internal HTTP Connector Resource" />
        </Node>
        <Host>
            <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemhost" /></xsl:attribute>
        </Host>
		<ApplicationFolder xsi:type="amxdata:ApplicationFolder" name="System">
			<Application xsi:type="amxdata:Application" name="com.tibco.amx.platform.artifactserver">
				<Node>
					<xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemnode" /></xsl:attribute>
					<xsl:attribute name="environmentName"><xsl:value-of select="config/admin.tibcohost.systemenvironment" /></xsl:attribute>
				</Node>            
				<SVar xsi:type="amxdata_base:SubstitutionVariable" name="ArtifactServerContextRootSvar"
					type="String" value="/amxadministrator/" description="artifact server context root svar" />
				<SVar xsi:type="amxdata_base:SubstitutionVariable" name="ArtifactServerHTTPConnectorSvar"
					type="String" value="TIBCO ActiveMatrix Internal HTTP Connector Resource" description="Must be the name of an Http connector on SystemNode that should be used to serve up files in Admin for the runtime hosts and nodes" />
				<SVar xsi:type="amxdata_base:SubstitutionVariable" name="ArtifactServerInternalSharedLocationSvar"
					type="String" description="admin shared folder svar" >
					<xsl:attribute name="value"><xsl:value-of select="config/admin.shared.folder" /></xsl:attribute>
				</SVar>    
				<ApplicationTemplate xsi:type="amxdata_reference:ApplicationTemplate_reference" name="com.tibco.amf.admin.artifactserver"/>
			</Application>
		</ApplicationFolder>
    </Environment>
    
    <Host xsi:type="amxdata:Host" hostType="TibcoHost" username="" password="" >
        <xsl:attribute name="name"><xsl:value-of select="config/admin.tibcohost.systemhost" /></xsl:attribute>        
        <xsl:attribute name="managementUrl"><xsl:value-of select="$th.jmx.url" /></xsl:attribute>
    </Host>
    
    <ResourceTemplate xsi:type="amxdata:HttpConnectorResourceTemplate" name="TIBCO ActiveMatrix Internal HTTP Connector Resource" 
        description="An http connector used by TIBCO Administrator to serve up certain files that runtime hosts and nodes need to access. Used for internal communications in the product. Do not delete this.">
        <xsl:attribute name="host"><xsl:value-of select="config/admin.internalhttpconn.host" /></xsl:attribute>
        <xsl:attribute name="port"><xsl:value-of select="config/admin.internalhttpconn.port" /></xsl:attribute>
        <xsl:attribute name="headerBufferSize"><xsl:value-of select="config/admin.internalhttpconn.headerBufferSize" /></xsl:attribute>
        <xsl:attribute name="requestBufferSize"><xsl:value-of select="config/admin.internalhttpconn.requestBufferSize" /></xsl:attribute>
        <xsl:attribute name="responseBufferSize"><xsl:value-of select="config/admin.internalhttpconn.responseBufferSize" /></xsl:attribute>
        <xsl:attribute name="maxIdleTime"><xsl:value-of select="config/admin.internalhttpconn.maxIdleTime" /></xsl:attribute>
        <xsl:attribute name="lowResourceMaxIdleTime"><xsl:value-of select="config/admin.internalhttpconn.lowResourceMaxIdleTime" /></xsl:attribute>
        <xsl:attribute name="isSecuredWithTCS"><xsl:value-of select="config/admin.internalhttpconn.isSecuredWithTCS" /></xsl:attribute>
		<xsl:attribute name="acceptors">3</xsl:attribute>
    </ResourceTemplate>
    
    <cs:CredentialServer name="credentialserver">
        <xsl:choose>
                <xsl:when test="config/admin.cs.keystore.autogenerate='true'">
                    <cs:CommonName><xsl:value-of select="config/admin.cs.keystore.commonname" /></cs:CommonName>
                    <cs:OrganizationUnit>Engineering</cs:OrganizationUnit>
                    <cs:Organization>TIBCO</cs:Organization>
                    <cs:City>PaloAlto</cs:City>
                    <cs:State>California</cs:State>
                    <cs:Country>USA</cs:Country>
                    <cs:ServerCertificateValidityPeriod>3650</cs:ServerCertificateValidityPeriod>
                    <cs:ClientCertificateValidityPeriod>730</cs:ClientCertificateValidityPeriod>
                    <cs:KeySize>2048</cs:KeySize>
                    <cs:KeyAlgorithm>RSA</cs:KeyAlgorithm>
                    <cs:KeySignAlgorithm>SHA256WithRSA</cs:KeySignAlgorithm>
                    <cs:KeyStoreLocation></cs:KeyStoreLocation>
                    <cs:KeyStoreType>JCEKS</cs:KeyStoreType>
                    <cs:KeyStoreProvider></cs:KeyStoreProvider>
                    <cs:KeyStorePassword>#!HNiBLkB++xHg09/wmUMpHvGoJgFAE0j7YW0+5FIcpIo=</cs:KeyStorePassword>
					<cs:KeyAlias>trinity</cs:KeyAlias>
					<cs:KeyPassword>#!SizQDuAjDyNqOwES86ydz5gMI12b9CdGtbrTbWt2mj0=</cs:KeyPassword>	                
                </xsl:when>
                <xsl:otherwise>
                    <cs:CommonName></cs:CommonName>
                    <cs:OrganizationUnit></cs:OrganizationUnit>
                    <cs:Organization></cs:Organization>
                    <cs:City></cs:City>
                    <cs:State></cs:State>
                    <cs:Country></cs:Country>
                    <cs:KeyAlgorithm></cs:KeyAlgorithm>
                    <cs:KeySignAlgorithm></cs:KeySignAlgorithm>
                    <cs:KeyStoreLocation><xsl:value-of select="config/admin.cs.keystorelocation" /></cs:KeyStoreLocation>
                    <cs:KeyStoreType><xsl:value-of select="config/admin.cs.keystoretype" /></cs:KeyStoreType>
                    <cs:KeyStoreProvider></cs:KeyStoreProvider>
                    <cs:KeyStorePassword><xsl:value-of select="config/admin.cs.keystorepassword" /></cs:KeyStorePassword>
                    <cs:KeyAlias><xsl:value-of select="config/admin.cs.keyalias" /></cs:KeyAlias>
                    <cs:KeyPassword><xsl:value-of select="config/admin.cs.keypassword" /></cs:KeyPassword>
                </xsl:otherwise>
        </xsl:choose>
        
        <cs:HostName><xsl:value-of select="config/admin.cs.host" /></cs:HostName>
        <cs:Port><xsl:value-of select="config/admin.cs.port" /></cs:Port>
        <cs:EnableSSL>true</cs:EnableSSL>
    </cs:CredentialServer>
                    
    <cs:User>
        <xsl:attribute name="UserName"><xsl:value-of select="config/admin.cs.username" /></xsl:attribute>
        <xsl:attribute name="Password"><xsl:value-of select="config/admin.cs.password" /></xsl:attribute>
    </cs:User> 

</amxdata_base:Enterprise>
    </xsl:template>
</xsl:stylesheet>
