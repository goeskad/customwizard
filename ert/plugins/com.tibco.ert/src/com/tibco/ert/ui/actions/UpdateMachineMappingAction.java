package com.tibco.ert.ui.actions;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.amxadministrator.command.line.typesBase.MachineBase;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.ert.model.utils.ERTUtil;
import com.tibco.ert.model.utils.ZipUtil;

public class UpdateMachineMappingAction implements ICustomAction {
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * get the source machines and target machines and update machine mapping
	 * between them
	 */
	public void execute(IActionContext actionContext) throws Exception {
		WizardConfig wizardConfig = actionContext.getWizardConfig();
		IDataModel dataModel = actionContext.getDataModel();

		File sourceDataFile = new File(dataModel.getValue("/ert/source/datafile"));
		log.info("Loading source machines from " + sourceDataFile.getAbsolutePath());

		InputStream sourceDataInput = ZipUtil.getInputStreamFromZip(sourceDataFile, "Machine.xml");
		if (sourceDataInput == null) {
			throw new Exception("Can not find Machine.xml in " + sourceDataFile.getAbsolutePath());
		}
		Enterprise sourceEnterprise = null;
		try {
			sourceEnterprise = ERTUtil.loadEnterprise(sourceDataInput);
		} finally {
			sourceDataInput.close();
		}

		MachineBase[] sourceMachines = sourceEnterprise.getMachineArray();
		File targetMachineFile = new File(dataModel.getValue("/ert/target/machine-file"));
		log.info("Loading target machines from " + targetMachineFile.getAbsolutePath());
		Enterprise targetEnterprise = ERTUtil.loadEnterprise(targetMachineFile);
		log.info("Total " + (sourceMachines == null ? 0 : sourceMachines.length) + " source machines, "
				+ (targetEnterprise.getMachineArray() == null ? 0 : targetEnterprise.getMachineArray().length) + " target machines");
		if (sourceMachines != null && sourceMachines.length > 0) {
			updateMachineMapping(wizardConfig, dataModel, sourceMachines, targetEnterprise.getMachineArray());
		}
	}

	/**
	 * update machine mapping between source machines and target machines
	 */
	private void updateMachineMapping(WizardConfig wizardConfig, IDataModel dataModel, MachineBase[] sourceMachines, MachineBase[] targetMachines)
			throws Exception {
		File file = new File(WizardHelper.getAbsolutePath(wizardConfig.getConfigFile(), "ui/machine-mapping.xform.template"));

		String targetMachinesStr = "\r\n";
		if (targetMachines != null && targetMachines.length > 0) {
			StringBuffer targetMachinesStrb = new StringBuffer();
			for (int i = 0; i < targetMachines.length; i++) {
				String targetMachineName;
				if (targetMachines.length > i) {
					targetMachineName = targetMachines[i].getHostName() + "(" + targetMachines[i].getTibcoHome() + ")";
				} else {
					targetMachineName = targetMachines[targetMachines.length - 1].getHostName() + "("
							+ targetMachines[targetMachines.length - 1].getTibcoHome() + ")";
				}
				dataModel.setValue("/ert/machine-mapping/target/m" + (i + 1), targetMachineName);
				targetMachinesStrb.append("\r\n");
				targetMachinesStrb.append("                   <item><value>" + targetMachineName + "</value></item>");
			}
			targetMachinesStr = targetMachinesStrb.toString();
		}

		StringBuffer content = new StringBuffer();
		for (int i = 0; i < sourceMachines.length; i++) {
			if (i != 0) {
				content.append("\r\n");
				content.append("               <output style=\"separator: horizontal; align: fill; colspan: 4;\"/>");
				content.append("\r\n");
			}
			content
					.append("               <input ref=\"source/m" + (i + 1)
							+ "\" style=\"hgrab: true; align: fill; border: true; readOnly: true;\">");
			content.append("\r\n                   <label>Source machine " + (i + 1) + "</label>");
			content.append("\r\n               </input>");
			content.append("\r\n               <select1 appearance=\"minimal\" ref=\"target/m" + (i + 1) + "\">");
			content.append("\r\n                   <label>Target machine " + (i + 1) + "</label>");
			content.append(targetMachinesStr);
			content.append("\r\n               </select1>");
			dataModel.setValue("/ert/machine-mapping/source/m" + (i + 1), sourceMachines[i].getHostName() + "(" + sourceMachines[i].getTibcoHome()
					+ ")");
		}
		replaceFileContent(file, "<!--${machineMapping}-->", content.toString());

		log.info("Replaced machine mapping page config");
	}

	/**
	 * replace the pattern in the file to the content of replacement
	 * 
	 * @param file
	 * @param pattern
	 * @param replacement
	 * @throws Exception
	 */
	private void replaceFileContent(File file, String pattern, String replacement) throws Exception {
		String content = readFile(file);
		if (content.indexOf(pattern) > 0) {
			content = replaceAll(content, pattern, replacement);
		}
		String fileName = file.getName();
		ERTUtil.writeFile(new File(file.getParent(), fileName.substring(0, fileName.length() - ".template".length())), content);
	}

	/**
	 * replace all old string to new string in string source
	 * 
	 * @param source
	 * @param oldStr
	 * @param newStr
	 * @return
	 */
	private String replaceAll(String source, String oldStr, String newStr) {
		StringBuffer strbuf = null;
		int index;
		int offset = 0;
		int oldLength = oldStr.length();
		while ((index = source.indexOf(oldStr, offset)) >= 0) {
			if (strbuf == null) {
				strbuf = new StringBuffer();
			}
			strbuf.append(source.substring(offset, index));
			offset = index + oldLength;
			strbuf.append(newStr);
		}

		if (offset == 0) {
			return source;
		} else {
			strbuf.append(source.substring(offset));
			return strbuf.toString();
		}
	}

	/**
	 * read the content of a file
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private String readFile(File file) throws Exception {
		char[] data = new char[1024];
		StringBuffer strbuf = new StringBuffer();
		FileReader reader = new FileReader(file);
		try {
			int len = 0;
			while ((len = reader.read(data)) != -1) {
				strbuf.append(data, 0, len);
			}
		} finally {
			reader.close();
		}

		return strbuf.toString();
	}
}
