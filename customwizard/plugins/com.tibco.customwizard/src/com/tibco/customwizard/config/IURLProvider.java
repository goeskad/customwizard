package com.tibco.customwizard.config;

import java.net.URL;
import java.util.List;

public interface IURLProvider {
    void addUrls(List<URL> urlList) throws Exception;
}
