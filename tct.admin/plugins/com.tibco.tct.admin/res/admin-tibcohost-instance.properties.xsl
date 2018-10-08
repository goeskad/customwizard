<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
    <xsl:output method="text" indent="yes" xalan:indent-amount="2" />
    <xsl:template match="/">
# Admin's Tibco Host HPA settings
# also used for instance creation
amx.th.instance.name=Admin-${admin.enterprise.name}-${admin.instance.name}

# register windows service
amx.th.instance.registerAsService=${admin.tibcohost.registerservice}

# default TibcoHost HPA instance log4j configuration
amx.th.hpa.log.config.file=${tct.templates.dir}/hpa-log4j.xml_template

# default TibcoHost node log4j configuration
amx.th.hpa.node.log.config.file=${tct.templates.dir}/node-log4j.xml_template

# default TibcoHost Administrator keystore
amx.th.security.keystore.file=../templates/admin.default.ssl.trust.store.ts
amx.th.security.keystore.type=JCEKS
amx.th.security.keystore.password=#!llDFtm1IZoO9hFkLI83SMqigDHIE/V/n

# secure JMX?
amx.th.jmx.secure=false

# Qin notification configuration (as planned now)
amx.th.qin.server.url=${admin.ems.url}
amx.th.qin.groupName=${admin.enterprise.name}
amx.th.qin.userName=${admin.ems.username}
amx.th.qin.pass=${admin.ems.password}
amx.th.qin.startupInterval=3000
amx.th.qin.backoffInterval=0
amx.th.qin.recovery_timer_timeout=5000
amx.th.qin.recovery_attempt_delay=500

amx.th.ems.identity.store.password=#!zIi9ux3C1uDzIZBJ2SjuWEKPpz4VMVLf3IEiSxdROOU=

amx.th.qin.secure=${admin.ems.enablessl}
<xsl:if test="config/admin.ems.enablessl='true'">
amx.th.trinity.keyStoreLocation=${admin.ems.keystorelocation}
amx.th.trinity.keyStorePassword=${admin.ems.keystorepassword}
amx.th.trinity.keyStoreType=${admin.ems.keystoretype}
</xsl:if>

# this should NOT be changeable
amx.th.qin.notificationTransport.impl=com.tibco.amf.hpa.core.runtime.management.impl.NotificationTransportImpl

# Trinity SSL
amx.th.trinity.tcs.url=service:jmx:jmxmp://${admin.cs.host}:${admin.cs.port}
amx.th.trinity.tcs.user=${admin.cs.username}

# Note we use ONE password (the TCS password) in the following four places:
#  key store tamper proofing password, reference and value
#  password for unlocking TCS user credentials
#  the actual TCS password
amx.th.trinity.tcs.password=${admin.cs.password}

# JMX configuration
amx.th.hpa.jmx.port=${th.jmx.port}
amx.th.hpa.jmx.interface=${th.jmx.host}

# Prefix for AMX processes
amx.th.process.prefix=tibamx_

#default node configuration
amf.node.type = com.tibco.amf.hpa.tibcohost.node.hibernate.feature
amf.node.type.version = 1.0.1
    </xsl:template>
</xsl:stylesheet>
