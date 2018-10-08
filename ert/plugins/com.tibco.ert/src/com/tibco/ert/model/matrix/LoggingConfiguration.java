package com.tibco.ert.model.matrix;

import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.EntityActionResultDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.EntityNameAndIDDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.ListOfEntityActionResultDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.NodeLoggingDetailsDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.SALoggingDetailsDocument;
import com.tibco.matrix.admin.server.services.loggingconfiguration.messages.UpdateLoggingConfigurationDocument;
import com.tibco.matrix.administration.command.line.client.loggingconfiguration.OperationFault;

public interface LoggingConfiguration {
    public EntityActionResultDocument updateLoggingConfiguration(UpdateLoggingConfigurationDocument updateloggingconfigurationdocument) throws OperationFault;

    public ListOfEntityActionResultDocument updateSALoggingConfiguration(SALoggingDetailsDocument saloggingdetailsdocument) throws OperationFault;

    public SALoggingDetailsDocument getSALoggingConfiguration(EntityNameAndIDDocument entitynameandiddocument) throws OperationFault;

    public NodeLoggingDetailsDocument getNodeLoggingConfiguration(EntityNameAndIDDocument entitynameandiddocument) throws OperationFault;

    public ListOfEntityActionResultDocument updateNodeLoggingConfiguration(NodeLoggingDetailsDocument nodeloggingdetailsdocument) throws OperationFault;
}
