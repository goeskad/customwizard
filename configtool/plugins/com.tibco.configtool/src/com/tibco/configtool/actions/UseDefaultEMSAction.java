package com.tibco.configtool.actions;

import java.util.ArrayList;
import java.util.List;

public class UseDefaultEMSAction extends UseDefaultAction {
	public UseDefaultEMSAction() {
		List<String> copySuffixList = new ArrayList<String>();
		copySuffixList.add("hostportlist");
		copySuffixList.add("username");
		copySuffixList.add("password");
		copySuffixList.add("enablessl");
		copySuffixList.add("keystorelocation");
		copySuffixList.add("keystoretype");
		copySuffixList.add("keystorepassword");
		setCopySuffixList(copySuffixList);
	}
}
