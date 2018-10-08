package com.tibco.customwizard.config;

import java.io.File;

public interface IDataModelLoader {
	public IDataModel createDataModel(File file) throws Exception;
	public void loadDataModel(IDataModel currentDataModel, IDataModel targetDataModel) throws Exception;
	public void saveDataModel(IDataModel dataModel, File file) throws Exception;
}
