<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="text" indent="yes" xalan:indent-amount="2" />
	<xsl:param name="tibcoHome"/>
	<xsl:param name="tibcoConfigHome"/>
	<xsl:param name="amxVersion"/>
    <xsl:param name="productList"/>
	<!--
    <xsl:param name="securityManager"/>
	-->
	
	<xsl:template match="/">
    <xsl:variable name="ems.server.hostportlist" select="xalan:tokenize(admin/emsconfig/hostportlist,',')"/>
    <xsl:variable name="ems.secured" select="admin/emsconfig/enablessl"/>
    <xsl:variable name="ems.server.url">
        <xsl:for-each select="$ems.server.hostportlist">
            <xsl:variable name="position" select="position()"/>
            <xsl:if test="$position>1">,</xsl:if>
            <xsl:choose>
                <xsl:when test="$ems.secured='true'">
                    <xsl:value-of select="'ssl'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'tcp'"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="'://'"/>
            <xsl:value-of select="normalize-space($ems.server.hostportlist[$position])"/>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="mcr.ems.server.hostportlist" select="xalan:tokenize(admin/mcrinfo/emsconfig/hostportlist,',')"/>
    <xsl:variable name="mcr.ems.secured" select="admin/mcrinfo/emsconfig/enablessl"/>
    <xsl:variable name="mcr.ems.server.url">
        <xsl:for-each select="$mcr.ems.server.hostportlist">
            <xsl:variable name="position" select="position()"/>
            <xsl:if test="$position>1">,</xsl:if>
            <xsl:choose>
                <xsl:when test="$mcr.ems.secured='true'">
                    <xsl:value-of select="'ssl'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'tcp'"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="'://'"/>
            <xsl:value-of select="normalize-space($mcr.ems.server.hostportlist[$position])"/>
        </xsl:for-each>
    </xsl:variable>
	<xsl:variable name="ldap.server.hostportlist" select="xalan:tokenize(admin/ldaprealm/hostportlist,',')"/>
    <xsl:variable name="ldap.secured" select="admin/ldaprealm/enablessl"/>
    <xsl:variable name="ldap.server.url">
        <xsl:for-each select="$ldap.server.hostportlist">
            <xsl:variable name="position" select="position()"/>
            <xsl:if test="$position>1"><xsl:text> </xsl:text></xsl:if>
            <xsl:choose>
                <xsl:when test="$ldap.secured='true'">
                    <xsl:value-of select="'ldaps'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'ldap'"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="'://'"/>
            <xsl:value-of select="normalize-space($ldap.server.hostportlist[$position])"/>
        </xsl:for-each>
    </xsl:variable># The following properties may be modified
tibco.home=<xsl:value-of select="$tibcoHome"/>
tibco.config.mgmt.home=<xsl:value-of select="$tibcoConfigHome"/>
amx.version=<xsl:value-of select="$amxVersion"/>

# Administrator Server Configuration: Details
admin.enterprise.name=<xsl:value-of select="admin/baseinfo/enterprisename" />
admin.instance.name=<xsl:value-of select="admin/baseinfo/servername" />
admin.shared.folder=${tibco.config.mgmt.home}/admin/${admin.enterprise.name}/shared

# Administrator Server Configuration: TIBCO Host Configuration
admin.tibcohost.registerservice=<xsl:value-of select="admin/tibcohost/registerservice" />
admin.tibcohost.createshortcut=<xsl:value-of select="admin/tibcohost/shortcut" />
th.jmx.host=<xsl:choose><xsl:when test="admin/tibcohost/securejmx='true'"><xsl:value-of select="admin/tibcohost/securebootstraphost" /></xsl:when><xsl:otherwise><xsl:value-of select="admin/tibcohost/host" /></xsl:otherwise></xsl:choose>
th.jmx.port=<xsl:value-of select="admin/tibcohost/port" />
admin.tibcohost.systemenvironment=<xsl:value-of select="admin/tibcohost/systemenvironment" />
admin.tibcohost.systemhost=<xsl:value-of select="admin/tibcohost/systemhost" />
admin.tibcohost.systemnode=<xsl:value-of select="admin/tibcohost/systemnode" />
logging.config.properties.file=${tct.templates.dir}/log-config-override.properties

<xsl:if test="admin/tibcohost/createnode = 'true'">
create.dev.node=<xsl:value-of select="admin/tibcohost/createnode" />
</xsl:if>
dev.envt.name=<xsl:value-of select="admin/tibcohost/envname" />
dev.host.name=<xsl:value-of select="admin/tibcohost/systemhost" />
dev.node.name=<xsl:value-of select="admin/tibcohost/nodename" />
dev.node.port=<xsl:value-of select="admin/tibcohost/nodeport" />

# Administrator Server Configuration: Connection Settings
serverconnsetting.host=<xsl:value-of select="admin/serverconnsetting/host" />
serverconnsetting.port=<xsl:value-of select="admin/serverconnsetting/httpport" />
serverconnsetting.adminurl=<xsl:value-of select="admin/serverconnsetting/adminurl" />
serverconnsetting.managementport=<xsl:value-of select="admin/serverconnsetting/managementport" />
serverconnsetting.timeout=<xsl:value-of select="admin/serverconnsetting/timeout" />

serverconnsetting.usebaseurl=<xsl:value-of select="admin/serverconnsetting/usebaseurl" />
serverconnsetting.baseurl=<xsl:value-of select="admin/serverconnsetting/baseurl" />

serverconnsetting.enablessl=<xsl:value-of select="admin/serverconnsetting/enablessl" />
# Connector Keystore properties
<xsl:choose>
    <xsl:when test="admin/serverconnsetting/autogeneratekeystore='true'">
serverconnsetting.keystorelocation=${tibco.home}/administrator/${amx.version}/samples/admin.default.ssl.jceks
serverconnsetting.keystoretype=jceks
serverconnsetting.keystorepassword=#!/HPUsQmSFTrrT0jx3pmrh60D68/g6Zjm
serverconnsetting.keyalias=mykey
serverconnsetting.keypassword=#!/HPUsQmSFTrrT0jx3pmrh60D68/g6Zjm</xsl:when>
    <xsl:otherwise>
serverconnsetting.keystorelocation=<xsl:value-of select="admin/serverconnsetting/keystorelocation" />
serverconnsetting.keystoretype=<xsl:value-of select="admin/serverconnsetting/keystoretype" />
serverconnsetting.keystorepassword=<xsl:value-of select="admin/serverconnsetting/keystorepassword" />
serverconnsetting.keyalias=<xsl:value-of select="admin/serverconnsetting/keyalias" />
serverconnsetting.keypassword=<xsl:value-of select="admin/serverconnsetting/keypassword" /></xsl:otherwise>
</xsl:choose>

# Admin trust store setting for SSL
serverconnsetting.truststorelocation=<xsl:value-of select="admin/serverconnsetting/truststorelocation"/>
serverconnsetting.truststoretype=<xsl:value-of select="admin/serverconnsetting/truststoretype" />
serverconnsetting.truststorepassword=<xsl:value-of select="admin/serverconnsetting/truststorepassword" />

# Admin CLI keystore 
admin.cli.ssl.keystorelocation=<xsl:value-of select="admin/cli/keystorelocation"/>
admin.cli.ssl.keystorepassword=<xsl:value-of select="admin/cli/keystorepassword"/>
admin.cli.ssl.keystoretype=<xsl:value-of select="admin/cli/keystoretype"/>
admin.cli.ssl.keyalias=<xsl:value-of select="admin/cli/keyalias"/>
admin.cli.ssl.keypassword=<xsl:value-of select="admin/cli/keypassword"/>

# Administrator Server Configuration: Internal HTTP Settings
admin.internalhttpconn.host=<xsl:value-of select="admin/internalhttpconn/host" />
admin.internalhttpconn.port=<xsl:value-of select="admin/internalhttpconn/port" />
admin.internalhttpconn.headerBufferSize=<xsl:value-of select="admin/internalhttpconn/headerBufferSize" />
admin.internalhttpconn.requestBufferSize=<xsl:value-of select="admin/internalhttpconn/requestBufferSize" />
admin.internalhttpconn.responseBufferSize=<xsl:value-of select="admin/internalhttpconn/responseBufferSize" />
admin.internalhttpconn.maxIdleTime=<xsl:value-of select="admin/internalhttpconn/maxIdleTime" />
admin.internalhttpconn.lowResourceMaxIdleTime=<xsl:value-of select="admin/internalhttpconn/lowResourceMaxIdleTime" />
admin.internalhttpconn.isSecuredWithTCS=<xsl:value-of select="admin/internalhttpconn/isSecuredWithTCS" />

# Administrator Server Configuration: Notification and Messaging Bus Server
admin.ems.url=<xsl:value-of select="$ems.server.url" />
admin.ems.username=<xsl:value-of select="admin/emsconfig/username" />
admin.ems.password=<xsl:value-of select="admin/emsconfig/password" />
admin.ems.enablessl=<xsl:value-of select="admin/emsconfig/enablessl" />
admin.ems.keystorepassword=<xsl:value-of select="admin/emsconfig/keystorepassword" />
admin.ems.keystoretype=<xsl:value-of select="admin/emsconfig/keystoretype" />
admin.ems.keystorelocation=<xsl:value-of select="admin/emsconfig/keystorelocation" />

# Administrator Server Configuration: Database Details
admin.db.usedefault=<xsl:value-of select="admin/database/usedefault" />
<xsl:choose>
    <xsl:when test="admin/database/usedefault='true'">
admin.db.url=jdbc:hsqldb:file:${tibco.config.mgmt.home}/admin/${admin.enterprise.name}/private/${admin.instance.name}/hsqldb/amx</xsl:when>
    <xsl:otherwise>
admin.db.url=<xsl:value-of select="admin/database/url" />
    </xsl:otherwise>
</xsl:choose>

admin.db.driver=<xsl:value-of select="admin/database/driver" />
admin.db.dialect=<xsl:value-of select="admin/database/dialect" />
admin.db.username=<xsl:value-of select="admin/database/username" />
admin.db.password=<xsl:value-of select="admin/database/password" />
admin.db.maxconnections=<xsl:value-of select="admin/database/maxconnections" />
admin.db.enablessl=<xsl:value-of select="admin/database/enablessl" />
admin.db.keystorepassword=<xsl:value-of select="admin/database/keystorepassword" />
admin.db.keystoretype=<xsl:value-of select="admin/database/keystoretype" />
admin.db.keystorelocation=<xsl:value-of select="admin/database/keystorelocation" />

# Administrator Server Configuration: Authentication Realm
admin.authenticationrealm.type=<xsl:value-of select="admin/authenticationrealm/type" />
admin.authenticationrealm.username=<xsl:value-of select="admin/authenticationrealm/username" />
admin.authenticationrealm.password=<xsl:value-of select="admin/authenticationrealm/password" />
<xsl:choose>
    <xsl:when test="admin/authenticationrealm/type='dbrealm'">

# Administrator Server Configuration: Database Authentication Realm Details
admin.dbrealm.usedefault=<xsl:value-of select="admin/dbrealm/usedefault" />
        <xsl:choose>
            <xsl:when test="admin/dbrealm/usedefault='true'">
admin.dbrealm.url=${admin.db.url}
admin.dbrealm.driver=${admin.db.driver}
admin.dbrealm.dialect=${admin.db.dialect}
admin.dbrealm.username=${admin.db.username}
admin.dbrealm.password=${admin.db.password}
admin.dbrealm.maxconnections=${admin.db.maxconnections}
admin.dbrealm.enablessl=${admin.db.enablessl}
admin.dbrealm.keystorepassword=${admin.db.keystorepassword}
admin.dbrealm.keystoretype=${admin.db.keystoretype}
admin.dbrealm.keystorelocation=${admin.db.keystorelocation}</xsl:when>
            <xsl:otherwise>
admin.dbrealm.url=<xsl:value-of select="admin/dbrealm/url" />
admin.dbrealm.driver=<xsl:value-of select="admin/dbrealm/driver" />
admin.dbrealm.dialect=<xsl:value-of select="admin/dbrealm/dialect" />
admin.dbrealm.username=<xsl:value-of select="admin/dbrealm/username" />
admin.dbrealm.password=<xsl:value-of select="admin/dbrealm/password" />
admin.dbrealm.maxconnections=<xsl:value-of select="admin/dbrealm/maxconnections" />
admin.dbrealm.enablessl=<xsl:value-of select="admin/dbrealm/enablessl" />
admin.dbrealm.keystorepassword=<xsl:value-of select="admin/dbrealm/keystorepassword" />
admin.dbrealm.keystoretype=<xsl:value-of select="admin/dbrealm/keystoretype" />
admin.dbrealm.keystorelocation=<xsl:value-of select="admin/dbrealm/keystorelocation" /></xsl:otherwise>
	    </xsl:choose>
    </xsl:when>
<xsl:otherwise>

# Administrator Server Configuration: LDAP Authentication Realm Details
admin.ldaprealm.LdapBindDNName=<xsl:value-of select="admin/ldaprealm/username" />
admin.ldaprealm.LdapBindDNPassword=<xsl:value-of select="admin/ldaprealm/password" />
admin.ldaprealm.initialCtxFactory=<xsl:value-of select="admin/ldaprealm/factory" />
admin.ldaprealm.serverURL=<xsl:value-of select="$ldap.server.url" />
admin.ldaprealm.userSearchExpression=<xsl:value-of select="admin/ldaprealm/user/filter" />
admin.ldaprealm.userAttributeUsersName=<xsl:value-of select="admin/ldaprealm/user/attrname" />
admin.ldaprealm.userSearchBaseDN=<xsl:value-of select="admin/ldaprealm/user/basedn" />
admin.ldaprealm.groupSearchBaseDN=<xsl:value-of select="admin/ldaprealm/group/basedn" />
admin.ldaprealm.groupSearchScopeSubtree=<xsl:value-of select="admin/ldaprealm/group/subtree" />
admin.ldaprealm.groupSearchExpression=<xsl:value-of select="admin/ldaprealm/group/filter" />
admin.ldaprealm.groupAttributeGroupsName=<xsl:value-of select="admin/ldaprealm/group/groupattr" />
admin.ldaprealm.groupIndication=<xsl:value-of select="admin/ldaprealm/group/indication" />
<xsl:choose>
	    <xsl:when test="admin/ldaprealm/group/indication='groupHasUsers'">
admin.ldaprealm.groupAttributeUsersName=<xsl:value-of select="admin/ldaprealm/group/groupuserattr" />
		</xsl:when>
	    <xsl:when test="admin/ldaprealm/group/indication='userHasGroups'">
admin.ldaprealm.userAttributeGroupsName=<xsl:value-of select="admin/ldaprealm/group/userAttributeGroupsName" />
		</xsl:when>
	    <xsl:when test="admin/ldaprealm/group/indication='userDNHasGroups'">
admin.ldaprealm.userAttributeGroupsName=<xsl:value-of select="admin/ldaprealm/group/userAttributeGroupsName" />
        </xsl:when>
</xsl:choose>
admin.ldaprealm.groupAttributeSubgroupsName=<xsl:value-of select="admin/ldaprealm/group/subgroupattr" />
admin.ldaprealm.searchTimeOut=<xsl:value-of select="admin/ldaprealm/timeout" />
admin.ldaprealm.userAttributesExtra=<xsl:value-of select="admin/ldaprealm/user/extraattr" />
admin.ldaprealm.userSearchScopeSubtree=<xsl:value-of select="admin/ldaprealm/user/subtree" />
admin.ldaprealm.keyPassword=<xsl:value-of select="admin/ldaprealm/keypassword" />
admin.ldaprealm.securityAuthentication=<xsl:value-of select="admin/ldaprealm/authenticationtype" />
admin.ldaprealm.enablessl=<xsl:value-of select="admin/ldaprealm/enablessl" />
admin.ldaprealm.keystorepassword=<xsl:value-of select="admin/ldaprealm/keystorepassword" />
admin.ldaprealm.keystoretype=<xsl:value-of select="admin/ldaprealm/keystoretype" />
admin.ldaprealm.keystorelocation=<xsl:value-of select="admin/ldaprealm/keystorelocation" /></xsl:otherwise>
</xsl:choose>

# Administrator Server Configuration: Credential Server Details
admin.cs.host=<xsl:value-of select="admin/credentialservice/host" />
admin.cs.port=<xsl:value-of select="admin/credentialservice/port" />
admin.cs.username=<xsl:value-of select="admin/credentialservice/username" />
admin.cs.password=<xsl:value-of select="admin/credentialservice/password" />

# Administrator Server Configuration: Credential Server Keystore
admin.cs.keystore.autogenerate=<xsl:value-of select="admin/credentialkeystore/autogenerate" />
<xsl:choose>
    <xsl:when test="admin/credentialkeystore/commonname=admin/baseinfo/enterprisename">
admin.cs.keystore.commonname=${admin.enterprise.name}</xsl:when>
    <xsl:otherwise>
admin.cs.keystore.commonname=<xsl:value-of select="admin/credentialkeystore/commonname" /></xsl:otherwise>
</xsl:choose>
admin.cs.keystorelocation=<xsl:value-of select="admin/credentialkeystore/keystorelocation" />
admin.cs.keystoretype=<xsl:value-of select="admin/credentialkeystore/keystoretype" />
admin.cs.keystorepassword=<xsl:value-of select="admin/credentialkeystore/keystorepassword" />
admin.cs.keyalias=<xsl:value-of select="admin/credentialkeystore/keyalias" />
admin.cs.keypassword=<xsl:value-of select="admin/credentialkeystore/keypassword" />

# Administrator Server Configuration: Monitoring Notification Server
mcr.ems.usedefault=<xsl:value-of select="admin/mcrinfo/emsconfig/usedefault" />
<xsl:choose>
    <xsl:when test="admin/mcrinfo/emsconfig/usedefault='true'">
mcr.ems.url=${admin.ems.url}
mcr.ems.username=${admin.ems.username}
mcr.ems.password=${admin.ems.password}
mcr.ems.enablessl=${admin.ems.enablessl}
mcr.ems.keystorepassword=${admin.ems.keystorepassword}
mcr.ems.keystoretype=${admin.ems.keystoretype}
mcr.ems.keystorelocation=${admin.ems.keystorelocation}</xsl:when>
    <xsl:otherwise>
mcr.ems.url=<xsl:value-of select="$mcr.ems.server.url" />
mcr.ems.username=<xsl:value-of select="admin/mcrinfo/emsconfig/username" />
mcr.ems.password=<xsl:value-of select="admin/mcrinfo/emsconfig/password" />
mcr.ems.enablessl=<xsl:value-of select="admin/mcrinfo/emsconfig/enablessl" />
mcr.ems.keystorepassword=<xsl:value-of select="admin/mcrinfo/emsconfig/keystorepassword" />
mcr.ems.keystoretype=<xsl:value-of select="admin/mcrinfo/emsconfig/keystoretype" />
mcr.ems.keystorelocation=<xsl:value-of select="admin/mcrinfo/emsconfig/keystorelocation" /></xsl:otherwise>
</xsl:choose>

# Administrator Server Configuration: Monitoring Database Configuration
<xsl:choose>
    <xsl:when test="admin/mcrinfo/database/usedefault='true'">mcr.db.url=${admin.db.url}
mcr.db.driver=${admin.db.driver}
mcr.db.dialect=${admin.db.dialect}
mcr.db.username=${admin.db.username}
mcr.db.password=${admin.db.password}
mcr.db.maxconnections=${admin.db.maxconnections}
mcr.db.enablessl=${admin.db.enablessl}
mcr.db.keystorepassword=${admin.db.keystorepassword}
mcr.db.keystoretype=${admin.db.keystoretype}
mcr.db.keystorelocation=${admin.db.keystorelocation}</xsl:when>
    <xsl:otherwise>mcr.db.url=<xsl:value-of select="admin/mcrinfo/database/url" />
mcr.db.driver=<xsl:value-of select="admin/mcrinfo/database/driver" />
mcr.db.dialect=<xsl:value-of select="admin/mcrinfo/database/dialect" />
mcr.db.username=<xsl:value-of select="admin/mcrinfo/database/username" />
mcr.db.password=<xsl:value-of select="admin/mcrinfo/database/password" />
mcr.db.maxconnections=<xsl:value-of select="admin/mcrinfo/database/maxconnections" />
mcr.db.enablessl=<xsl:value-of select="admin/mcrinfo/database/enablessl" />
mcr.db.keystorepassword=<xsl:value-of select="admin/mcrinfo/database/keystorepassword" />
mcr.db.keystoretype=<xsl:value-of select="admin/mcrinfo/database/keystoretype" />
mcr.db.keystorelocation=<xsl:value-of select="admin/mcrinfo/database/keystorelocation" /></xsl:otherwise>
</xsl:choose>

# Administrator Server Configuration: Monitoring General Configuration
mcr.general.config.extended.monitoring.enabled=<xsl:value-of select="admin/mcrinfo/generalconfig/extendedmonitoringenabled" />

# Administrator Server Configuration: Log Service Database Configuration
<xsl:choose>
    <xsl:when test="admin/clinfo/logsrvdatabase/usedefault='true'">logging.db.url=${admin.db.url}
logging.db.driver=${admin.db.driver}
logging.db.dialect=${admin.db.dialect}
logging.db.username=${admin.db.username}
logging.db.password=${admin.db.password}
logging.db.maxconnections=${admin.db.maxconnections}
logging.db.enablessl=${admin.db.enablessl}
logging.db.keystorepassword=${admin.db.keystorepassword}
logging.db.keystoretype=${admin.db.keystoretype}
logging.db.keystorelocation=${admin.db.keystorelocation}</xsl:when>
    <xsl:otherwise>logging.db.url=<xsl:value-of select="admin/clinfo/logsrvdatabase/url" />
logging.db.driver=<xsl:value-of select="admin/clinfo/logsrvdatabase/driver" />
logging.db.dialect=<xsl:value-of select="admin/clinfo/logsrvdatabase/dialect" />
logging.db.username=<xsl:value-of select="admin/clinfo/logsrvdatabase/username" />
logging.db.password=<xsl:value-of select="admin/clinfo/logsrvdatabase/password" />
logging.db.maxconnections=<xsl:value-of select="admin/clinfo/logsrvdatabase/maxconnections" />
logging.db.enablessl=<xsl:value-of select="admin/clinfo/logsrvdatabase/enablessl" />
logging.db.keystorepassword=<xsl:value-of select="admin/clinfo/logsrvdatabase/keystorepassword" />
logging.db.keystoretype=<xsl:value-of select="admin/clinfo/logsrvdatabase/keystoretype" />
logging.db.keystorelocation=<xsl:value-of select="admin/clinfo/logsrvdatabase/keystorelocation" /></xsl:otherwise>
</xsl:choose>

# Administrator Server Configuration: Payload Service Database Configuration
<xsl:choose>
    <xsl:when test="admin/clinfo/payloadsrvdatabase/usedefault='true'">payload.db.url=${admin.db.url}
payload.db.driver=${admin.db.driver}
payload.db.dialect=${admin.db.dialect}
payload.db.username=${admin.db.username}
payload.db.password=${admin.db.password}
payload.db.maxconnections=${admin.db.maxconnections}
payload.db.enablessl=${admin.db.enablessl}
payload.db.keystorepassword=${admin.db.keystorepassword}
payload.db.keystoretype=${admin.db.keystoretype}
payload.db.keystorelocation=${admin.db.keystorelocation}</xsl:when>
    <xsl:otherwise>payload.db.url=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/url" />
payload.db.driver=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/driver" />
payload.db.dialect=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/dialect" />
payload.db.username=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/username" />
payload.db.password=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/password" />
payload.db.maxconnections=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/maxconnections" />
payload.db.enablessl=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/enablessl" />
payload.db.keystorepassword=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/keystorepassword" />
payload.db.keystoretype=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/keystoretype" />
payload.db.keystorelocation=<xsl:value-of select="admin/clinfo/payloadsrvdatabase/keystorelocation" /></xsl:otherwise>
</xsl:choose>

# Administrator Server Configuration:Provision Drivers
provisiondrivers.enable=<xsl:value-of select="admin/provisiondrivers/enable" />
provisiondrivers.provisionMode=<xsl:value-of select="admin/provisiondrivers/provisionMode" />
<xsl:for-each select="admin/provisiondrivers/provisiondriver">
provisiondriver<xsl:value-of select="position()"/>.id=<xsl:value-of select="id" />
provisiondriver<xsl:value-of select="position()"/>.version=<xsl:value-of select="version" />
</xsl:for-each>

# Summary: Products to deploy on the Dev Node
dev.node.products=<xsl:value-of select="$productList"/>
<!--		
amx.securitymanager.enabled=<xsl:value-of select="$securityManager"/>
-->
	</xsl:template>
</xsl:stylesheet>