package com.tibco.ert.model.core;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RepeatExtractor implements Runnable {
	private EnterpriseExtractor extractor;

	private int interval;
	private String target;
	private File extractionDir;

	private Log log = LogFactory.getLog(getClass());

	private boolean stop;

	public RepeatExtractor(EnterpriseExtractor extractor, int interval, String target, File extractionDir) {
		this.extractor = extractor;
		this.interval = interval * 60 * 1000;
		this.target = target;
		this.extractionDir = extractionDir;
	}

	public void destroy() {
		stop = true;
	}

	public void run() {
		int count = 0;
		while (!stop) {
			count++;

			try {
				log.info("Repeat extracting, count: " + count);
				extractor.extractToZipFile(target, extractionDir);
			} catch (Exception e) {
				log.error("Extraction failed", e);
			}

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				log.error("Count: " + count + ", interrupted", e);
			}
		}

		try {
			extractor.release();
		} catch (Exception e) {
			log.error(e);
		}

		log.info("Task(" + toString() + ") is stopped.");
	}
}
