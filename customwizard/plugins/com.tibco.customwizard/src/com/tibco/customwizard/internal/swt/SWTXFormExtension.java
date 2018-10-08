package com.tibco.customwizard.internal.swt;

import org.nuxeo.xforms.ui.UIBuilder;
import org.nuxeo.xforms.ui.swt.SWTRegistry;

import com.tibco.customwizard.internal.swt.proxies.ConfirmPasswordProxy;
import com.tibco.customwizard.internal.swt.proxies.FileChooserProxy;
import com.tibco.customwizard.internal.swt.proxies.HyperlinkProxy;
import com.tibco.customwizard.internal.swt.proxies.PasswordProxy;
import com.tibco.customwizard.internal.swt.proxies.ScrolledCompositeProxy;
import com.tibco.customwizard.internal.swt.style.Disable;
import com.tibco.customwizard.internal.swt.style.FillBoth;
import com.tibco.customwizard.internal.xforms.BaseXFormExtension;

public class SWTXFormExtension extends BaseXFormExtension {
	private static CWSWTRegistry swtRegistry = new CWSWTRegistry();
	static{
		swtRegistry.registerControlProxy("scroll", ScrolledCompositeProxy.class);
		swtRegistry.registerControlProxy("filechooser", FileChooserProxy.class);
		swtRegistry.registerControlProxy("password", PasswordProxy.class);
		swtRegistry.registerControlProxy("confirmpassword", ConfirmPasswordProxy.class);
		swtRegistry.registerControlProxy("hyperlink", HyperlinkProxy.class);
		
		swtRegistry.registerStyleProperty("disable", new Disable());
		swtRegistry.registerStyleProperty("fillboth", new FillBoth());
	}

	public UIBuilder getUIBuilder() {
		CWSWTBuilder swtBuilder = new CWSWTBuilder(swtRegistry);
		return swtBuilder;
	}

	private static class CWSWTRegistry extends SWTRegistry {
	}
}
