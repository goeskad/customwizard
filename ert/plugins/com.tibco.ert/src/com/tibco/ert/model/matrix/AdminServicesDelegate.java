package com.tibco.ert.model.matrix;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;

import com.tibco.amxadministrator.command.line.types.Machine;
import com.tibco.ert.model.core.AdminAccessConfig;
import com.tibco.matrix.admin.server.services.adminconfiguration.messages.ClusterConfigurationDetailsDocument;
import com.tibco.matrix.admin.server.services.adminconfiguration.types.ClusterConfigurationDetails;
import com.tibco.matrix.admin.server.services.components.messages.GetComponentsRequestDocument;
import com.tibco.matrix.admin.server.services.components.messages.ListOfComponentDetailsDocument;
import com.tibco.matrix.admin.server.services.components.types.ComponentDetails;
import com.tibco.matrix.admin.server.services.components.types.GetComponentsRequest;
import com.tibco.matrix.admin.server.services.components.types.ListOfComponentDetails;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfSRProfileDefinitionBindingsDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfServiceAssemblyWithSUInfoDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfServiceSummaryDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfServiceUnitSvarsDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfSubstitutionVariableDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ListOfTopicDefinitionSummaryDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ServiceAssemblyEntityDocument;
import com.tibco.matrix.admin.server.services.deployment.messages.ServiceUnitEntityDocument;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfSRProfileDefinitionBinding;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfServiceAssemblyWithSUInfo;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfServiceSummary;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfServiceUnitSvars;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfSubstitutionVariable;
import com.tibco.matrix.admin.server.services.deployment.types.ListOfTopicDefinitionSummary;
import com.tibco.matrix.admin.server.services.deployment.types.ResourceProfileResourceDefinitionBinding;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceAssemblyWithSUInfo;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceSummary;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceUnitEntity;
import com.tibco.matrix.admin.server.services.deployment.types.SubstitutionVariable;
import com.tibco.matrix.admin.server.services.deployment.types.TopicDefinitionSummary;
import com.tibco.matrix.admin.server.services.environment.messages.ListOfEnvironmentSummaryDocument;
import com.tibco.matrix.admin.server.services.environment.messages.ListOfMessagingServerDetailsDocument;
import com.tibco.matrix.admin.server.services.environment.types.EnvironmentSummary;
import com.tibco.matrix.admin.server.services.environment.types.ListOfEnvironmentSummary;
import com.tibco.matrix.admin.server.services.environment.types.ListOfMessagingServerDetails;
import com.tibco.matrix.admin.server.services.environment.types.MessagingServerDetails;
import com.tibco.matrix.admin.server.services.inventory.messages.ListOfMachineProductsDocument;
import com.tibco.matrix.admin.server.services.inventory.types.ListOfMachineProducts;
import com.tibco.matrix.admin.server.services.inventory.types.MachineProducts;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.EntityNameAndIDDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.NodeLoggingDetailsDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.SALoggingDetailsDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.NodeLoggingDetails;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.SALoggingDetails;
import com.tibco.matrix.admin.server.services.managedresources.messages.GetMgdResNodeAssocRequestDocument;
import com.tibco.matrix.admin.server.services.managedresources.messages.ListOfMgdResNodeAssociationDocument;
import com.tibco.matrix.admin.server.services.managedresources.types.GetMgdResNodeAssocRequest;
import com.tibco.matrix.admin.server.services.managedresources.types.ListOfMgdResNodeAssociation;
import com.tibco.matrix.admin.server.services.managedresources.types.MgdResNodeAssociation;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.AddSocketDiscoveryConfigurationRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.AddSocketDiscoveryConfigurationResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.ConnectToManagementDaemonsRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.ConnectToManagementDaemonsResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetConnectedManagementDaemonsRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetConnectedManagementDaemonsResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveredManagementDaemonsRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveredManagementDaemonsResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveryConfigurationRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveryConfigurationResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveryConfigurationsRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetDiscoveryConfigurationsResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetInstalledSoftwareRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.GetInstalledSoftwareResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.StartDiscoveryRequestDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.messages.StartDiscoveryResponseDocument;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ConnectedManagementDaemons;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoverManagementDaemons;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveredManagementDaemonInfo;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveredManagementDaemons;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveryConfiguration;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveryConfigurationID;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveryConfigurationIDs;
import com.tibco.matrix.admin.server.services.managementdaemon.types.DiscoveryConfigurations;
import com.tibco.matrix.admin.server.services.managementdaemon.types.GetDiscoveryConfigurations;
import com.tibco.matrix.admin.server.services.managementdaemon.types.Installation;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonInfo;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonsList;
import com.tibco.matrix.admin.server.services.managementdaemon.types.MatrixInstallation;
import com.tibco.matrix.admin.server.services.managementdaemon.types.OperationHandler;
import com.tibco.matrix.admin.server.services.managementdaemon.types.SocketDiscoveryConfiguration;
import com.tibco.matrix.admin.server.services.matrixcommon.types.EntityNameAndID;
import com.tibco.matrix.admin.server.services.matrixcommon.types.EntityResponse;
import com.tibco.matrix.admin.server.services.node.messages.ListOfNodeDetailsDocument;
import com.tibco.matrix.admin.server.services.node.messages.NodeDetailsDocument;
import com.tibco.matrix.admin.server.services.node.types.ListOfNodeDetails;
import com.tibco.matrix.admin.server.services.node.types.NodeDetails;
import com.tibco.matrix.admin.server.services.plugins.messages.DataDocument;
import com.tibco.matrix.admin.server.services.plugins.types.Data;
import com.tibco.matrix.admin.server.services.sharedresources.messages.EmsResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.GetResourceConfigsRequestDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.GetResourceConfigurationsInEnterpriseRequestDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.HttpResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.IdentityResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.JdbcResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.JndiResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.ListOfResourceSummaryDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.RVResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.messages.SSLServerResourceDetailsDocument;
import com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.GetResourceConfigsRequest;
import com.tibco.matrix.admin.server.services.sharedresources.types.HttpResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.IdentityResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.JdbcResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.JndiResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.ListOfResourceSummary;
import com.tibco.matrix.admin.server.services.sharedresources.types.RVResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.ResourceSummary;
import com.tibco.matrix.admin.server.services.sharedresources.types.SSLServerResourceDetails;
import com.tibco.matrix.admin.server.services.svar.messages.ListOfLocalSubstitutionVariableDocument;
import com.tibco.matrix.admin.server.services.svar.types.ListOfLocalSubstitutionVariable;
import com.tibco.matrix.admin.server.services.svar.types.LocalSubstitutionVariable;
import com.tibco.matrix.admin.server.services.usermgmt.messages.GroupNameAndIDDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.ListOfGroupInfoDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.ListOfMemberUserInfoDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.ListOfUserInfoDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.ListOfUserNamesDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.OptionalParentGroupDocument;
import com.tibco.matrix.admin.server.services.usermgmt.messages.SearchRequestDocument;
import com.tibco.matrix.admin.server.services.usermgmt.types.GroupInfo;
import com.tibco.matrix.admin.server.services.usermgmt.types.GroupNameAndID;
import com.tibco.matrix.admin.server.services.usermgmt.types.MemberUserInfo;
import com.tibco.matrix.admin.server.services.usermgmt.types.OptionalParentGroup;
import com.tibco.matrix.admin.server.services.usermgmt.types.SearchRequest;
import com.tibco.matrix.admin.server.services.usermgmt.types.UserInfo;
import com.tibco.matrix.administration.command.line.FormBasedAdminLogin;
import com.tibco.matrix.administration.command.line.ant.AMXAdminTask;
import com.tibco.matrix.administration.command.line.client.adminconfiguration.MatrixAdministrationFaultMessage;
import com.tibco.matrix.administration.command.line.client.adminconfiguration.adminconfiguration;
import com.tibco.matrix.administration.command.line.client.container.components;
import com.tibco.matrix.administration.command.line.client.deployment.deployment;
import com.tibco.matrix.administration.command.line.client.environment.environment;
import com.tibco.matrix.administration.command.line.client.inventory.inventory;
import com.tibco.matrix.administration.command.line.client.loggingconfiguration.loggingconfiguration;
import com.tibco.matrix.administration.command.line.client.machine.managementDaemon;
import com.tibco.matrix.administration.command.line.client.node.node;
import com.tibco.matrix.administration.command.line.client.plugins.plugins;
import com.tibco.matrix.administration.command.line.client.sharedresource.managedresources;
import com.tibco.matrix.administration.command.line.client.sharedresourcedefinition.sharedresources;
import com.tibco.matrix.administration.command.line.client.svar.svar;
import com.tibco.matrix.administration.command.line.client.usermgmt.usermgmt;

public class AdminServicesDelegate {
    private AdminAccessConfig accessConfig;

    public AdminServicesDelegate(AdminAccessConfig accessConfig) throws Exception {
        this.accessConfig = accessConfig;

        init(accessConfig);
    }

    public AdminAccessConfig getAccessConfig() {
        return accessConfig;
    }

    public DiscoveryConfiguration getDiscoveryConfiguration(long searcherId) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        DiscoveryConfigurationID configurationId = DiscoveryConfigurationID.Factory.newInstance();
        configurationId.setEntityID(searcherId);
        GetDiscoveryConfigurationRequestDocument doc = GetDiscoveryConfigurationRequestDocument.Factory.newInstance();
        doc.setGetDiscoveryConfigurationRequest(configurationId);

        GetDiscoveryConfigurationResponseDocument replyDoc = stub.getDiscoveryConfiguration(doc);
        DiscoveryConfiguration result = replyDoc.getGetDiscoveryConfigurationResponse();

        return result;
    }

    public SocketDiscoveryConfiguration[] getScoketDiscoveryConfigurations() throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        GetDiscoveryConfigurations request = GetDiscoveryConfigurations.Factory.newInstance();
        request.setConfigType("");
        GetDiscoveryConfigurationsRequestDocument doc = GetDiscoveryConfigurationsRequestDocument.Factory.newInstance();
        doc.setGetDiscoveryConfigurationsRequest(request);

        GetDiscoveryConfigurationsResponseDocument replyDoc = stub.getDiscoveryConfigurations(doc);
        DiscoveryConfigurations result = replyDoc.getGetDiscoveryConfigurationsResponse();

        return result.getSocketDiscoveryConfigurations().getSocketDiscoveryConfigurationArray();
    }

    public EntityResponse addSocketDiscoveryConfiguration(String name) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        SocketDiscoveryConfiguration configuration = SocketDiscoveryConfiguration.Factory.newInstance();
        configuration.setName(name);
        configuration.setHostName("239.100.106.107");
        configuration.setPort(1965);
        AddSocketDiscoveryConfigurationRequestDocument doc = AddSocketDiscoveryConfigurationRequestDocument.Factory.newInstance();
        doc.setAddSocketDiscoveryConfigurationRequest(configuration);

        AddSocketDiscoveryConfigurationResponseDocument replyDoc = stub.addSocketDiscoveryConfiguration(doc);
        EntityResponse result = replyDoc.getAddSocketDiscoveryConfigurationResponse();

        return result;
    }

    public OperationHandler startDiscovery(long searcherId) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        DiscoverManagementDaemons request = DiscoverManagementDaemons.Factory.newInstance();
        DiscoveryConfigurationIDs configurationIDs = DiscoveryConfigurationIDs.Factory.newInstance();
        DiscoveryConfigurationID configurationID = DiscoveryConfigurationID.Factory.newInstance();
        configurationID.setEntityID(searcherId);
        configurationIDs.setDiscoveryConfigurationIDArray(new DiscoveryConfigurationID[] { configurationID });
        request.setDiscoveryConfigurationIDs(configurationIDs);
        request.setDiscoveryTimeout(30);

        StartDiscoveryRequestDocument doc = StartDiscoveryRequestDocument.Factory.newInstance();
        doc.setStartDiscoveryRequest(request);

        StartDiscoveryResponseDocument replyDoc = stub.startDiscovery(doc);
        OperationHandler result = replyDoc.getStartDiscoveryResponse();

        return result;
    }

    public ManagementDaemonInfo[] getDiscoveredManagementDaemons(OperationHandler operationHandler) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        GetDiscoveredManagementDaemonsRequestDocument doc = GetDiscoveredManagementDaemonsRequestDocument.Factory.newInstance();
        doc.setGetDiscoveredManagementDaemonsRequest(operationHandler);

        GetDiscoveredManagementDaemonsResponseDocument replyDoc = stub.getDiscoveredManagementDaemons(doc);
        DiscoveredManagementDaemons result = replyDoc.getGetDiscoveredManagementDaemonsResponse();

        if (result.getDiscoveredManagementDaemonInfoArray() != null && result.getDiscoveredManagementDaemonInfoArray().length > 0) {
            return result.getDiscoveredManagementDaemonInfoArray()[0].getManagementDaemonsList().getManagementDaemonsInfoArray();
        }
        return null;
    }

    public ManagementDaemonInfo[] connectToManagementDaemons(ManagementDaemonInfo[] managementDaemons) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        ManagementDaemonsList managementDaemonsList = ManagementDaemonsList.Factory.newInstance();
        managementDaemonsList.setManagementDaemonsInfoArray(managementDaemons);
        DiscoveredManagementDaemonInfo discoveredManagementDaemonInfo = DiscoveredManagementDaemonInfo.Factory.newInstance();
        discoveredManagementDaemonInfo.setManagementDaemonsList(managementDaemonsList);
        DiscoveredManagementDaemons request = DiscoveredManagementDaemons.Factory.newInstance();
        request.setDiscoveredManagementDaemonInfoArray(new DiscoveredManagementDaemonInfo[] { discoveredManagementDaemonInfo });
        ConnectToManagementDaemonsRequestDocument doc = ConnectToManagementDaemonsRequestDocument.Factory.newInstance();
        doc.setConnectToManagementDaemonsRequest(request);

        ConnectToManagementDaemonsResponseDocument replyDoc = stub.connectToManagementDaemons(doc);
        ManagementDaemonInfo[] result = replyDoc.getConnectToManagementDaemonsResponse().getDiscoveredManagementDaemonInfoArray()[0].getManagementDaemonsList().getManagementDaemonsInfoArray();

        return result;
    }

    public MatrixInstallation[] getInstalledSoftware(Machine machine) throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        Installation request = Installation.Factory.newInstance();
        request.setHostName(machine.getHostName());
        request.setTibcoHome(machine.getTibcoHome());
        GetInstalledSoftwareRequestDocument doc = GetInstalledSoftwareRequestDocument.Factory.newInstance();
        doc.setGetInstalledSoftwareRequest(request);

        GetInstalledSoftwareResponseDocument replyDoc = stub.getInstalledSoftware(doc);
        MatrixInstallation[] result = replyDoc.getGetInstalledSoftwareResponse().getMatrixInstallationArray();

        return result;
    }

    public ManagementDaemonInfo[] getMachines() throws Exception {
        managementDaemon stub = ClientStubs.machineClient;

        GetConnectedManagementDaemonsRequestDocument doc = GetConnectedManagementDaemonsRequestDocument.Factory.newInstance();
        doc.setGetConnectedManagementDaemonsRequest(null);

        GetConnectedManagementDaemonsResponseDocument replyDoc = stub.getConnectedManagementDaemons(doc);
        ConnectedManagementDaemons result = replyDoc.getGetConnectedManagementDaemonsResponse();

        ManagementDaemonInfo[] machines = result.getConnectedManagementDaemonArray();
        return machines;
    }

    public MachineProducts[] getMachinesInEnvironment(long environmentId) throws Exception {
        inventory stub = ClientStubs.inventoryClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(environmentId);
        com.tibco.matrix.admin.server.services.inventory.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.inventory.messages.EntityNameAndIDDocument.Factory.newInstance();
        doc.setEntityNameAndID(enid);

        ListOfMachineProductsDocument replyDoc = stub.getProductsInEnvironment(doc);
        ListOfMachineProducts result = replyDoc.getListOfMachineProducts();

        MachineProducts[] machines = result.getMachineProductsArray();
        return machines;
    }

    public UserInfo[] getUsers() throws Exception {
        usermgmt stub = ClientStubs.usergroupClient;

        SearchRequest request = SearchRequest.Factory.newInstance();
        request.setSearchLimit(10000);
        SearchRequestDocument doc = SearchRequestDocument.Factory.newInstance();
        doc.setSearchRequest(request);

        ListOfUserInfoDocument replyDoc = stub.searchUsers(doc);
        UserInfo[] result = replyDoc.getListOfUserInfo().getUserInfoArray();

        return result;
    }

    public GroupInfo[] getChildGroups(String parentGroupId) throws Exception {
        usermgmt stub = ClientStubs.usergroupClient;

        OptionalParentGroup request = null;
        if (parentGroupId != null) {
            request = OptionalParentGroup.Factory.newInstance();
            GroupNameAndID groupId = GroupNameAndID.Factory.newInstance();
            groupId.setGroupId(parentGroupId);
            request.setParentGroup(groupId);
        }
        OptionalParentGroupDocument doc = OptionalParentGroupDocument.Factory.newInstance();
        doc.setOptionalParentGroup(request);

        ListOfGroupInfoDocument replyDoc = stub.getChildGroups(doc);
        GroupInfo[] result = replyDoc.getListOfGroupInfo().getGroupInfoArray();

        return result;
    }

    public String[] getSuperUsers() throws Exception {
        usermgmt stub = ClientStubs.usergroupClient;

        ListOfUserNamesDocument replyDoc = stub.getSuperUsers();
        String[] result = replyDoc.getListOfUserNames().getUsernameArray();

        return result;
    }
    
    public MemberUserInfo[] getAllUsersInGroup(String groupId) throws Exception {
        usermgmt stub = ClientStubs.usergroupClient;

        GroupNameAndID groupNameAndID = GroupNameAndID.Factory.newInstance();
        groupNameAndID.setGroupId(groupId);
        GroupNameAndIDDocument doc = GroupNameAndIDDocument.Factory.newInstance();
        doc.setGroupNameAndID(groupNameAndID);
        ListOfMemberUserInfoDocument replyDoc = stub.getAllUsersInGroup(doc);
        MemberUserInfo[] result = replyDoc.getListOfMemberUserInfo().getMemberUserInfoArray();

        return result;
    }

    public ResourceSummary[] getSharedresources() throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        GetResourceConfigurationsInEnterpriseRequestDocument doc = GetResourceConfigurationsInEnterpriseRequestDocument.Factory.newInstance();
        doc.setGetResourceConfigurationsInEnterpriseRequest(null);

        ListOfResourceSummaryDocument replyDoc = stub.getResourceConfigurationsInEnterprise(doc);
        ListOfResourceSummary result = replyDoc.getListOfResourceSummary();

        ResourceSummary[] sharedresources = result.getResourceSummaryArray();
        return sharedresources;
    }

    public ResourceSummary[] getSharedresourcesInEnvironment(long environmentId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        GetResourceConfigsRequest enid = GetResourceConfigsRequest.Factory.newInstance();
        enid.setEntityID(environmentId);
        GetResourceConfigsRequestDocument doc = GetResourceConfigsRequestDocument.Factory.newInstance();
        doc.setGetResourceConfigsRequest(enid);

        ListOfResourceSummaryDocument replyDoc = stub.getResourceConfigsInEnvironment(doc);
        ListOfResourceSummary result = replyDoc.getListOfResourceSummary();

        ResourceSummary[] tmps = result.getResourceSummaryArray();
        List<ResourceSummary> sharedresourceList = new ArrayList<ResourceSummary>();
        for (ResourceSummary tmp : tmps) {
            if (tmp.getIsAssociatedToEnv()) {
                sharedresourceList.add(tmp);
            }
        }
        return sharedresourceList.toArray(new ResourceSummary[sharedresourceList.size()]);
    }

    public SALoggingDetails getSALoggingConfiguration(long serviceAssemblyId) throws Exception {
        loggingconfiguration stub = LoggingConfigurationClient.loggingconfigurationstub;

        EntityNameAndIDDocument request = EntityNameAndIDDocument.Factory.newInstance();
        EntityNameAndID entity = EntityNameAndID.Factory.newInstance();
        entity.setEntityID(serviceAssemblyId);
        request.setEntityNameAndID(entity);

        SALoggingDetailsDocument replyDoc = stub.getSALoggingConfiguration(request);
        SALoggingDetails result = replyDoc.getSALoggingDetails();
        return result;
    }

    public NodeLoggingDetails getLoggingConfigurationInNode(long nodeId) throws Exception {
//        loggingconfiguration stub = ClientStubs.loggingconfigurationClient;
        loggingconfiguration stub = LoggingConfigurationClient.loggingconfigurationstub;

        EntityNameAndIDDocument request = EntityNameAndIDDocument.Factory.newInstance();
        EntityNameAndID entityId = EntityNameAndID.Factory.newInstance();
        entityId.setEntityID(nodeId);
        request.setEntityNameAndID(entityId);

        NodeLoggingDetailsDocument replyDoc = stub.getNodeLoggingConfiguration(request);
        NodeLoggingDetails result = replyDoc.getNodeLoggingDetails();

        return result;
    }

    public ComponentDetails[] getComponentInNode(long nodeId) throws Exception {
        components stub = ClientStubs.containerClient;

        GetComponentsRequestDocument request = GetComponentsRequestDocument.Factory.newInstance();
        GetComponentsRequest entity = GetComponentsRequest.Factory.newInstance();
        entity.setEntityID(nodeId);
        request.setGetComponentsRequest(entity);

        ListOfComponentDetailsDocument replyDoc = stub.getComponentsInNode(request);
        ListOfComponentDetails result = replyDoc.getListOfComponentDetails();
        ComponentDetails[] components = result.getComponentDetailsArray();

        return components;
    }

    public MgdResNodeAssociation[] getSharedresourcesInNode(long nodeId) throws Exception {
        managedresources stub = ClientStubs.sharedresourceClient;

        EntityNameAndID node = EntityNameAndID.Factory.newInstance();
        node.setEntityID(nodeId);
        GetMgdResNodeAssocRequest getMgdResNodeAssocRequest = GetMgdResNodeAssocRequest.Factory.newInstance();
        getMgdResNodeAssocRequest.setParentNode(node);
        GetMgdResNodeAssocRequestDocument doc = GetMgdResNodeAssocRequestDocument.Factory.newInstance();
        doc.setGetMgdResNodeAssocRequest(getMgdResNodeAssocRequest);

        ListOfMgdResNodeAssociationDocument replyDoc = stub.getManagedResourceNodeAssociations(doc);
        ListOfMgdResNodeAssociation result = replyDoc.getListOfMgdResNodeAssociation();

        MgdResNodeAssociation[] tmps = result.getMgdResNodeAssociationArray();
        List<MgdResNodeAssociation> sharedresourceList = new ArrayList<MgdResNodeAssociation>();
        for (MgdResNodeAssociation tmp : tmps) {
            if (tmp.getIsAssociatedToNode()) {
                sharedresourceList.add(tmp);
            }
        }
        
        return sharedresourceList.toArray(new MgdResNodeAssociation[sharedresourceList.size()]);
    }

    public ResourceProfileResourceDefinitionBinding[] getSharedresourcesInServiceAssembly(long serviceAssemblyId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        EntityNameAndID serviceAssemblyIdentifier = EntityNameAndID.Factory.newInstance();
        serviceAssemblyIdentifier.setEntityID(serviceAssemblyId);
        ServiceAssemblyEntityDocument doc = ServiceAssemblyEntityDocument.Factory.newInstance();
        doc.setServiceAssemblyEntity(serviceAssemblyIdentifier);
        
        ListOfSRProfileDefinitionBindingsDocument replyDoc = stub.getSRProfileDefinitionBindings(doc);
        ListOfSRProfileDefinitionBinding result = replyDoc.getListOfSRProfileDefinitionBindings();

        ResourceProfileResourceDefinitionBinding[] sharedresources = result.getSrProfileDefinitionBindingArray();
        return sharedresources;
    }

    public EnvironmentSummary[] getEnvironments() throws Exception {
        environment stub = ClientStubs.environmentClient;

        ListOfEnvironmentSummaryDocument replyDoc = stub.getEnvironments();

        ListOfEnvironmentSummary result = replyDoc.getListOfEnvironmentSummary();
        EnvironmentSummary[] environments = result.getEnvironmentSummaryArray();
        return environments;
    }

    public MessagingServerDetails[] getMessagingServersInEnvironment(long environmentId) throws Exception {
        environment stub = ClientStubs.environmentClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(environmentId);
        com.tibco.matrix.admin.server.services.environment.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.environment.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEntityNameAndID(enid);

        ListOfMessagingServerDetailsDocument replyDoc = stub.getServiceBusConfigurations(doc);

        ListOfMessagingServerDetails result = replyDoc.getListOfMessagingServerDetails();
        MessagingServerDetails[] messagingServers = result.getMessagingServerDetailsArray();
        return messagingServers;
    }

    public NodeDetails[] getNodes(long environmentId) throws Exception {
        node stub = ClientStubs.nodeClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(environmentId);
        com.tibco.matrix.admin.server.services.node.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.node.messages.EntityNameAndIDDocument.Factory.newInstance();
        doc.setEntityNameAndID(enid);

        ListOfNodeDetailsDocument replyDoc = stub.getNodesInEnvironment(doc);
        ListOfNodeDetails result = replyDoc.getListOfNodeDetails();

        NodeDetails[] nodes = result.getNodeDetailsArray();
        return nodes;
    }

    public NodeDetails getNode(long nodeId) throws Exception {
        node stub = ClientStubs.nodeClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(nodeId);
        com.tibco.matrix.admin.server.services.node.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.node.messages.EntityNameAndIDDocument.Factory.newInstance();
        doc.setEntityNameAndID(enid);

        NodeDetailsDocument replyDoc = stub.getNodeDetails(doc);
        NodeDetails node = replyDoc.getNodeDetails();

        return node;
    }

    public ServiceAssemblyWithSUInfo[] getServiceAssembliesWithSUInfo(long environmentId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(environmentId);
        com.tibco.matrix.admin.server.services.deployment.messages.EnvEntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.deployment.messages.EnvEntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEnvEntityNameAndID(enid);

        ListOfServiceAssemblyWithSUInfoDocument replyDoc = stub.getServiceAssembliesWithSUInfo(doc);
        ListOfServiceAssemblyWithSUInfo result = replyDoc.getListOfServiceAssemblyWithSUInfo();

        ServiceAssemblyWithSUInfo[] serviceAssembliesWithSUInfos = result.getListOfServiceAssemblyWithSUInfoArray();
        return serviceAssembliesWithSUInfos;
    }

    public com.tibco.matrix.admin.server.services.deployment.types.NodeDetails[] getServiceUnitNodeMappings(long serviceAssemblyId, long serviceUnitId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        EntityNameAndID serviceAssemblyIdentifier = EntityNameAndID.Factory.newInstance();
        serviceAssemblyIdentifier.setEntityID(serviceAssemblyId);
        EntityNameAndID serviceUnitIdentifier = EntityNameAndID.Factory.newInstance();
        serviceUnitIdentifier.setEntityID(serviceUnitId);
        ServiceUnitEntity serviceUnitEntity = ServiceUnitEntity.Factory.newInstance();
        serviceUnitEntity.setParentServiceAssembly(serviceAssemblyIdentifier);
        serviceUnitEntity.setServiceUnitIdentifier(serviceUnitIdentifier);
        ServiceUnitEntityDocument doc = ServiceUnitEntityDocument.Factory.newInstance();
        doc.setServiceUnitEntity(serviceUnitEntity);

        com.tibco.matrix.admin.server.services.deployment.messages.ListOfNodeDetailsDocument replyDoc = stub.getServiceUnitNodeMappings(doc);
        com.tibco.matrix.admin.server.services.deployment.types.ListOfNodeDetails result = replyDoc.getListOfNodeDetails();

        com.tibco.matrix.admin.server.services.deployment.types.NodeDetails[] nodes = result.getNodeDetailsArray();
        return nodes;
    }

    public TopicDefinitionSummary[] getServiceAssemblyTopics(long serviceAssemblyId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        EntityNameAndID serviceAssemblyIdentifier = EntityNameAndID.Factory.newInstance();
        serviceAssemblyIdentifier.setEntityID(serviceAssemblyId);
        ServiceAssemblyEntityDocument doc = ServiceAssemblyEntityDocument.Factory.newInstance();
        doc.setServiceAssemblyEntity(serviceAssemblyIdentifier);

        ListOfTopicDefinitionSummaryDocument replyDoc = stub.getTopicDefinitions(doc);
        ListOfTopicDefinitionSummary result = replyDoc.getListOfTopicDefinitionSummary();

        TopicDefinitionSummary[] topics = result.getListOfTopicDefinitionsArray();
        return topics;
    }

    public ServiceSummary[] getServicesInServiceAssembly(long serviceAssemblyId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        EntityNameAndID serviceAssemblyIdentifier = EntityNameAndID.Factory.newInstance();
        serviceAssemblyIdentifier.setEntityID(serviceAssemblyId);
        ServiceAssemblyEntityDocument doc = ServiceAssemblyEntityDocument.Factory.newInstance();
        doc.setServiceAssemblyEntity(serviceAssemblyIdentifier);

        ListOfServiceSummaryDocument replyDoc = stub.getServicesForServiceAssembly(doc);
        ListOfServiceSummary result = replyDoc.getListOfServiceSummary();

        ServiceSummary[] services = result.getServiceSummaryArray();
        return services;
    }

    public EmsResourceDetails getJmsResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEntityNameAndID(enid);

        EmsResourceDetailsDocument replyDoc = stub.getEmsResourceDetails(doc);
        EmsResourceDetails result = replyDoc.getEmsResourceDetails();

        return result;
    }

    public JdbcResourceDetails getJdbcResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEntityNameAndID(enid);

        JdbcResourceDetailsDocument replyDoc = stub.getJdbcResourceDetails(doc);
        JdbcResourceDetails result = replyDoc.getJdbcResourceDetails();

        return result;
    }

    public HttpResourceDetails getHttpResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEntityNameAndID(enid);

        HttpResourceDetailsDocument replyDoc = stub.getHttpResourceDetails(doc);
        HttpResourceDetails result = replyDoc.getHttpResourceDetails();

        return result;
    }

    public JndiResourceDetails getJndiResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument doc = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        doc.setEntityNameAndID(enid);

        JndiResourceDetailsDocument replyDoc = stub.getJndiResourceDetails(doc);
        JndiResourceDetails result = replyDoc.getJndiResourceDetails();

        return result;
    }

    public RVResourceDetails getRVResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument request = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        request.setEntityNameAndID(enid);

        RVResourceDetailsDocument replyDoc = stub.getRVResourceDetails(request);

        RVResourceDetails result = replyDoc.getRVResourceDetails();

        return result;
    }

    public IdentityResourceDetails getIdentityResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;

        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument request = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        request.setEntityNameAndID(enid);

        IdentityResourceDetailsDocument replyDoc = stub.getIdentityResourceDetails(request);
        IdentityResourceDetails result = replyDoc.getIdentityResourceDetails();
        return result;
    }

    public SSLServerResourceDetails getSSLServerResourceDetails(long resourceId) throws Exception {
        sharedresources stub = ClientStubs.sharedresourcedefinitionClient;
        com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument request = com.tibco.matrix.admin.server.services.sharedresources.messages.EntityNameAndIDDocument.Factory
                .newInstance();
        EntityNameAndID enid = EntityNameAndID.Factory.newInstance();
        enid.setEntityID(resourceId);
        request.setEntityNameAndID(enid);

        SSLServerResourceDetailsDocument replyDoc = stub.getSSLServerResourceDetails(request);
        SSLServerResourceDetails result = replyDoc.getSSLServerResourceDetails();
        return result;
    }

    public com.tibco.matrix.admin.server.services.svar.types.SubstitutionVariable[] getSVarsInEnterprise() throws Exception {
        svar stub = ClientStubs.svarClient;

        com.tibco.matrix.admin.server.services.svar.messages.ListOfSubstitutionVariableDocument replyDoc = stub.getSvarsInEnterprise();
        com.tibco.matrix.admin.server.services.svar.types.ListOfSubstitutionVariable result = replyDoc.getListOfSubstitutionVariable();
        return result.getSubstitutionVariablesArray();
    }

    public LocalSubstitutionVariable[] getSVarsInNode(long nodeId) throws Exception {
        svar stub = ClientStubs.svarClient;

        com.tibco.matrix.admin.server.services.svar.messages.EntityNameAndIDDocument request = com.tibco.matrix.admin.server.services.svar.messages.EntityNameAndIDDocument.Factory.newInstance();
        EntityNameAndID entity = EntityNameAndID.Factory.newInstance();
        entity.setEntityID(nodeId);
        request.setEntityNameAndID(entity);

        ListOfLocalSubstitutionVariableDocument replyDoc = stub.getSvarsInNode(request);
        ListOfLocalSubstitutionVariable result = replyDoc.getListOfLocalSubstitutionVariable();
        return result.getSubstitutionVariablesArray();
    }

    public com.tibco.matrix.admin.server.services.deployment.types.SubstitutionVariable[] getServiceAssemblySVars(long serviceAssemblyId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        ServiceAssemblyEntityDocument request = ServiceAssemblyEntityDocument.Factory.newInstance();
        EntityNameAndID entity = EntityNameAndID.Factory.newInstance();
        entity.setEntityID(serviceAssemblyId);
        request.setServiceAssemblyEntity(entity);

        ListOfSubstitutionVariableDocument replyDoc = stub.getServiceAssemblySVars(request);
        ListOfSubstitutionVariable result = replyDoc.getListOfSubstitutionVariable();
        return result.getSubstitutionVariableArray();
    }

    public SubstitutionVariable[] getServiceUnitSVars(long serviceAssemblyId, long serviceUnitId) throws Exception {
        deployment stub = ClientStubs.deploymentClient;

        ServiceUnitEntityDocument request = ServiceUnitEntityDocument.Factory.newInstance();

        ServiceUnitEntity entity = ServiceUnitEntity.Factory.newInstance();
        EntityNameAndID saId = EntityNameAndID.Factory.newInstance();
        saId.setEntityID(serviceAssemblyId);
        EntityNameAndID suId = EntityNameAndID.Factory.newInstance();
        suId.setEntityID(serviceUnitId);
        entity.setParentServiceAssembly(saId);
        entity.setServiceUnitIdentifier(suId);

        request.setServiceUnitEntity(entity);

        ListOfServiceUnitSvarsDocument replyDoc = stub.getServiceUnitSVars(request);
        ListOfServiceUnitSvars result = replyDoc.getListOfServiceUnitSvars();
        return result.getSubstitutionVariableArray();
    }

    /**
     * get the cluster configuration details of admin server 
     * @return
     * @throws RemoteException
     * @throws MatrixAdministrationFaultMessage
     */
    public ClusterConfigurationDetails getAdminClusterConfiguration() throws Exception {
        adminconfiguration stub = ClientStubs.adminconfigurationClient;

        ClusterConfigurationDetailsDocument replyDoc = stub.getAdminClusterConfiguration();
        ClusterConfigurationDetails result = replyDoc.getClusterConfigurationDetails();

        return result;
    }

    public Data buildUIResourceTree() throws Exception {
        plugins stub = ClientStubs.pluginsClient;

        DataDocument replyDoc = stub.buildUIResourceTree();
        Data result = replyDoc.getData();
        return result;
    }

    private void init(AdminAccessConfig accessConfig) throws Exception {
        boolean initStubs = false;
        if (AMXAdminTask.ADMIN_URL == null || !AMXAdminTask.ADMIN_URL.equals(accessConfig.getAdminUrl())) {
            AMXAdminTask.ADMIN_URL = accessConfig.getAdminUrl();
            initStubs = true;
        }

        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
        httpClient.getParams().setParameter(HttpClientParams.RETRY_HANDLER, new AMXHttpMethodRetryHandler());
        AMXAdminTask.httpClient = httpClient;

        String cookie = FormBasedAdminLogin.login(accessConfig.getUsername(), accessConfig.getPassword());

		if (cookie == null) {
			throw new Exception("Cannot login to " + accessConfig.getAdminUrl() + " via " + accessConfig.getUsername()
					+ "/" + accessConfig.getPassword());
		} else {
			AMXAdminTask.COOKIE = cookie;
        }
        
        if (initStubs) {
            ClientStubs.init();
        }
    }
}