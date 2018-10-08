package com.tibco.configtool.actions;

import java.util.Map;

import org.nuxeo.xforms.xforms.model.XForm;
import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.utils.SSLUtils;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.support.IBackgroudAction;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.xforms.IXFormActionContext;
import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.admin.TibjmsAdminInvalidNameException;
import com.tibco.tibjms.admin.TibjmsAdminNameExistsException;
import com.tibco.tibjms.admin.TibjmsAdminSecurityException;
import com.tibco.tibjms.admin.TopicInfo;
import com.tibco.trinity.runtime.core.provider.identity.IdentityEMSConnectionFactoryBuilder;
import com.tibco.trinity.runtime.core.provider.identity.trust.TrustRuntimeException;

public class TestEMSConnectionAction implements ICustomAction, IBackgroudAction {
	protected IMessageProvider messageProvider;

	public void execute(IActionContext actionContext) throws Exception {
		IXFormActionContext xformActionContext = (IXFormActionContext) actionContext;
		XForm form = xformActionContext.getForm();

		messageProvider = TCTHelper.getMessageProvider(actionContext.getWizardInstance());

		try {
			testTibcoEMSConnection(form);
			WizardHelper.openMessage(actionContext.getWizardInstance(), messageProvider.getMessage("testconnection.success"));
		} catch (Exception e) {
			WizardHelper.openErrorDialog(actionContext.getWizardInstance(), e);
		}
	}

	protected void testTibcoEMSConnection(XForm form) throws Exception {
		String protocol = "tcp";
		boolean sslEnable = Boolean.parseBoolean(form.getUI().getControl("sslcontrol").getXMLValue());
		Map<String, String> sslParams = null;
		if (sslEnable) {
			protocol = "ssl";
			sslParams = SSLUtils.getSslProperties(form.getUI().getControl("keystorelocation").getXMLValue(), form.getUI().getControl(
					"keystorepassword").getXMLValue(), form.getUI().getControl("keystoretype").getXMLValue());
		}
		String hostPorts = form.getUI().getControl("hostportlist").getXMLValue();
		String username = form.getUI().getControl("username").getXMLValue();
		String password = form.getUI().getControl("password").getXMLValue();

		testTibcoEMSConnection(protocol + "://" + hostPorts, username, password, sslParams);
	}

	public boolean testTibcoEMSConnection(String url, String username, String password, Map<String, String> sslParams) throws Exception {
		TibjmsAdmin emsAdmin;
		try {
			if (sslParams != null) {
				emsAdmin = getSSLTibjmsAdmin(url, username, password, sslParams);
			} else {
				emsAdmin = new TibjmsAdmin(url, username, password);
			}
		} catch (TibjmsAdminException e) {
			// Field rootField = e.getClass().getDeclaredField("root");
			// rootField.setAccessible(true);
			// Exception root = (Exception) rootField.get(e);
			// Exception ex = root == null ? e : root;
			// throw ex;
			throw new ActionException(messageProvider.getMessage("TestEMSConnectionAction.error.connect"), e);
		}

		// Check user
		if (!checkEMSUser(emsAdmin)) {
			checkQueueTopic(emsAdmin, username);
		}
		return true;
	}

	private TibjmsAdmin getSSLTibjmsAdmin(String url, String username, String password, Map<String, String> sslParams) throws Exception {
		try {
			IdentityEMSConnectionFactoryBuilder emsBuilder = SSLUtils.getIdentityTrustConnection(sslParams).getEMSConnectionFactoryBuilder();
			if (emsBuilder == null) {
				throw new ActionException(messageProvider.getMessage("TestEMSConnectionAction.error.connect"));
			}
			return emsBuilder.getAdminConnection(url, username, password);
		} catch (TrustRuntimeException e) {
			throw new ActionException(messageProvider.getMessage("testconnection.error.keystore"), e);
		}
	}

	private boolean checkEMSUser(TibjmsAdmin emsAdmin) {
		TopicInfo topic = new TopicInfo("testEmsNoneAdminUserTopicPrivilege ");
		QueueInfo queue = new QueueInfo("testEmsNoneAdminUserQueuePrivilege");
		try {
			emsAdmin.destroyQueue(queue.getName());
			emsAdmin.destroyTopic(topic.getName());
			emsAdmin.createTopic(topic);
			emsAdmin.createQueue(queue);
		} catch (TibjmsAdminSecurityException e) {
			return false;
		} catch (TibjmsAdminNameExistsException e) {
			return false;
		} catch (TibjmsAdminException e) {
			return false;
		}
		return true;
	}

	private boolean checkQueueTopic(TibjmsAdmin emsAdmin, String username) {
		try {
			emsAdmin.getQueue("AMX_SV.>");
			emsAdmin.getQueue("com.tibco.amf.admin.deploymentServerQueue");
			emsAdmin.getTopic("EMSGMS.>");
		} catch (TibjmsAdminInvalidNameException e) {
			throw new ActionException(messageProvider.getMessage("TestEMSConnectionAction.error.user", username), e);
		} catch (TibjmsAdminException e) {
			throw new ActionException(messageProvider.getMessage("TestEMSConnectionAction.error.user", username), e);
		}

		return true;
	}
}
