package com.tibco.customwizard.config;

public interface IPageFactory {
	Object createPage(PageConfig pageConfig) throws Exception;
}
