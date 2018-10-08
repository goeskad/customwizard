package com.tibco.configtool.actions;

import java.util.ArrayList;
import java.util.List;

public class UseDefaultDBAction extends UseDefaultAction {
	public UseDefaultDBAction() {
		List<String> copySuffixList = new ArrayList<String>();
		copySuffixList.add("vendor");
		copySuffixList.add("url");
		copySuffixList.add("username");
		copySuffixList.add("password");
		copySuffixList.add("enablessl");
		copySuffixList.add("keystorelocation");
		copySuffixList.add("keystoretype");
		copySuffixList.add("keystorepassword");
		setCopySuffixList(copySuffixList);
	}
}
