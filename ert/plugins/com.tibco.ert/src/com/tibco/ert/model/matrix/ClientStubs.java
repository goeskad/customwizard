package com.tibco.ert.model.matrix;

import com.tibco.matrix.administration.command.line.client.adminconfiguration.adminconfiguration;
import com.tibco.matrix.administration.command.line.client.adminconfiguration.adminconfigurationClient;
import com.tibco.matrix.administration.command.line.client.bwse.matrixcomponentadminserviceClient;
import com.tibco.matrix.administration.command.line.client.bwse.matrixcomponentadminservicePortType;
import com.tibco.matrix.administration.command.line.client.container.components;
import com.tibco.matrix.administration.command.line.client.container.componentsClient;
import com.tibco.matrix.administration.command.line.client.deployment.deployment;
import com.tibco.matrix.administration.command.line.client.deployment.deploymentClient;
import com.tibco.matrix.administration.command.line.client.environment.environment;
import com.tibco.matrix.administration.command.line.client.environment.environmentClient;
import com.tibco.matrix.administration.command.line.client.inventory.inventory;
import com.tibco.matrix.administration.command.line.client.inventory.inventoryClient;
import com.tibco.matrix.administration.command.line.client.keystore.keystore;
import com.tibco.matrix.administration.command.line.client.keystore.keystoreClient;
import com.tibco.matrix.administration.command.line.client.loggingconfiguration.loggingconfiguration;
import com.tibco.matrix.administration.command.line.client.loggingconfiguration.loggingconfigurationClient;
import com.tibco.matrix.administration.command.line.client.logserviceconfig.LogServiceConfigPortType;
import com.tibco.matrix.administration.command.line.client.logserviceconfig.LogServiceConfigServiceClient;
import com.tibco.matrix.administration.command.line.client.machine.managementDaemon;
import com.tibco.matrix.administration.command.line.client.machine.managementDaemonClient;
import com.tibco.matrix.administration.command.line.client.monitoringconfiguration.monitoringconfig;
import com.tibco.matrix.administration.command.line.client.monitoringconfiguration.monitoringconfigClient;
import com.tibco.matrix.administration.command.line.client.node.node;
import com.tibco.matrix.administration.command.line.client.node.nodeClient;
import com.tibco.matrix.administration.command.line.client.permissions.permissions;
import com.tibco.matrix.administration.command.line.client.permissions.permissionsClient;
import com.tibco.matrix.administration.command.line.client.plugins.plugins;
import com.tibco.matrix.administration.command.line.client.plugins.pluginsClient;
import com.tibco.matrix.administration.command.line.client.sharedresource.managedresources;
import com.tibco.matrix.administration.command.line.client.sharedresource.managedresourcesClient;
import com.tibco.matrix.administration.command.line.client.sharedresourcedefinition.sharedresources;
import com.tibco.matrix.administration.command.line.client.sharedresourcedefinition.sharedresourcesClient;
import com.tibco.matrix.administration.command.line.client.svar.svar;
import com.tibco.matrix.administration.command.line.client.svar.svarClient;
import com.tibco.matrix.administration.command.line.client.uddiserver.uddiserver;
import com.tibco.matrix.administration.command.line.client.uddiserver.uddiserverClient;
import com.tibco.matrix.administration.command.line.client.usermgmt.usermgmt;
import com.tibco.matrix.administration.command.line.client.usermgmt.usermgmtClient;

public class ClientStubs {
    private static environmentClient environmentService;

    private static keystoreClient keystoreService;

    private static uddiserverClient uddiserverService;

    private static managementDaemonClient machineService;

    private static sharedresourcesClient sharedresourcedefinitionService;

    private static usermgmtClient usergroupService;

    private static nodeClient nodeService;

    private static deploymentClient deploymentService;

    private static managedresourcesClient sharedresourceService;

    private static componentsClient containerService;

    private static permissionsClient permissionsService;

    private static adminconfigurationClient adminconfigurationService;

    private static loggingconfigurationClient loggingconfigurationService;

    private static LogServiceConfigServiceClient logServiceconfigService;

    private static inventoryClient inventoryService;

    private static monitoringconfigClient monitoringConfigurationService;

    private static svarClient substitutionVariableClientService;

    private static pluginsClient pluginsClientService;

    private static matrixcomponentadminserviceClient bwseService;

    public static environment environmentClient;

    public static keystore keystoreClient;

    public static uddiserver uddiserverClient;

    public static managementDaemon machineClient;

    public static sharedresources sharedresourcedefinitionClient;

    public static usermgmt usergroupClient;

    public static node nodeClient;

    public static deployment deploymentClient;

    public static managedresources sharedresourceClient;

    public static components containerClient;

    public static permissions permissionsClient;

    public static adminconfiguration adminconfigurationClient;

    public static loggingconfiguration loggingconfigurationClient;

    public static LogServiceConfigPortType logServiceClient;

    public static inventory inventoryClient;

    public static monitoringconfig monitoringConfigurationClient;

    public static svar svarClient;

    public static plugins pluginsClient;

    public static matrixcomponentadminservicePortType bwseClient;

    public static void init() {
        environmentService = new environmentClient();
        keystoreService = new keystoreClient();
        uddiserverService = new uddiserverClient();
        machineService = new managementDaemonClient();
        sharedresourcedefinitionService = new sharedresourcesClient();
        usergroupService = new usermgmtClient();
        nodeService = new nodeClient();
        deploymentService = new deploymentClient();
        sharedresourceService = new managedresourcesClient();
        containerService = new componentsClient();
        permissionsService = new permissionsClient();
        adminconfigurationService = new adminconfigurationClient();
        loggingconfigurationService = new loggingconfigurationClient();
        logServiceconfigService = new LogServiceConfigServiceClient();
        inventoryService = new inventoryClient();
        monitoringConfigurationService = new monitoringconfigClient();
        substitutionVariableClientService = new svarClient();
        pluginsClientService = new pluginsClient();
        bwseService = new matrixcomponentadminserviceClient();
        environmentClient = environmentService.getenvironment();
        keystoreClient = keystoreService.getkeystore();
        uddiserverClient = uddiserverService.getuddiserver();
        machineClient = machineService.getmanagementdaemon();
        sharedresourcedefinitionClient = sharedresourcedefinitionService.getsharedresources();
        usergroupClient = usergroupService.getusermgmt();
        nodeClient = nodeService.getnode();
        deploymentClient = deploymentService.getdeployment();
        sharedresourceClient = sharedresourceService.getmanagedresources();
        containerClient = containerService.getcomponents();
        permissionsClient = permissionsService.getpermissions();
        adminconfigurationClient = adminconfigurationService.getadminconfiguration();
        loggingconfigurationClient = loggingconfigurationService.getloggingconfiguration();
        logServiceClient = logServiceconfigService.getLogServiceConfigPort();
        inventoryClient = inventoryService.getinventory();
        monitoringConfigurationClient = monitoringConfigurationService.getmonitoringconfig();
        svarClient = substitutionVariableClientService.getsvar();
        pluginsClient = pluginsClientService.getplugins();
        bwseClient = bwseService.getmatrixcomponentadminserviceSOAP11port0();
    }
}
