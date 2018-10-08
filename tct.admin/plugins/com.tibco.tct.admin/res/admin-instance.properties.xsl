<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="text" indent="yes" xalan:indent-amount="2" />
	<xsl:template match="/">
# AMX Administrator instance specific properties
adminURL=<xsl:value-of select="config/serverconnsetting.adminurl" />
username=<xsl:value-of select="config/admin.authenticationrealm.username" />
password=<xsl:value-of select="config/admin.authenticationrealm.password" />
httpConnectionTimeout=360000

# Admin trust store setting for SSL, variable is not allowed
javax.net.ssl.trustStore=<xsl:value-of select="config/serverconnsetting.truststorelocation"/>
javax.net.ssl.trustStoreType=<xsl:value-of select="config/serverconnsetting.truststoretype" />
javax.net.ssl.trustStorePassword=<xsl:value-of select="config/serverconnsetting.truststorepassword" />

# Admin CLI keystore 
admin.cli.ssl.keystorelocation=<xsl:value-of select="config/admin.cli.ssl.keystorelocation"/>
admin.cli.ssl.keystorepassword=<xsl:value-of select="config/admin.cli.ssl.keystorepassword"/>
admin.cli.ssl.keystoretype=<xsl:value-of select="config/admin.cli.ssl.keystoretype"/>
admin.cli.ssl.keyalias=<xsl:value-of select="config/admin.cli.ssl.keyalias"/>
admin.cli.ssl.keypassword=<xsl:value-of select="config/admin.cli.ssl.keypassword"/>
<xsl:text>&#xa;</xsl:text>
	</xsl:template>
</xsl:stylesheet>