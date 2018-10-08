package com.tibco.customwizard.config;

import java.util.ArrayList;
import java.util.List;

public class PageGroupConfig extends ConfigElement {
	private String id;

	private String title;

	private boolean visible = true;

	private List<PageConfig> pageList = new ArrayList<PageConfig>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public List<PageConfig> getPageList() {
		return pageList;
	}

	public void setPageList(List<PageConfig> pageList) {
		this.pageList = pageList;
	}

	public String toString() {
		return getTitle();
	}
}
