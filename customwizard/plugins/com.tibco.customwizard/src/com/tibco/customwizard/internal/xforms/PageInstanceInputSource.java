package com.tibco.customwizard.internal.xforms;

import java.net.URL;

import org.nuxeo.xforms.xml.URLInputSource;

import com.tibco.customwizard.instance.PageInstance;

public class PageInstanceInputSource extends URLInputSource {
    private PageInstance pageInstance;

    public PageInstanceInputSource(PageInstance pageInstance, URL url) {
        super(url);
        this.pageInstance = pageInstance;
    }

    public PageInstance getPageInstance() {
        return pageInstance;
    }
}
