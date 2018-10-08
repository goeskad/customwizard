package com.tibco.tct.admin;

import com.tibco.tct.amx.actions.ABasePostCheckConnectionAction;

public class PostCheckLDAPConnectionAction extends ABasePostCheckConnectionAction {

	public PostCheckLDAPConnectionAction() {
		super(new TestLDAPConnectionAction());
	}

}
