package com.tibco.ert.model.matrix.patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;

import com.tibco.amxadministrator.command.line.typesReference.GroupReference;

public class PatchModel {
    private List<Map<String, String>> allPermissions;

    private User[] dbUsers;

    private List<ServiceAssemblyPatch> serviceAssemblyPatchList = new ArrayList<ServiceAssemblyPatch>();
    
    private Map<String, GroupReference> groupMap = new HashMap<String, GroupReference>();

    private Document patchDocument;

    public List<Map<String, String>> getAllPermissions() {
        return allPermissions;
    }

    public void setAllPermissions(List<Map<String, String>> allPermissions) {
        this.allPermissions = allPermissions;
    }

    public User[] getDbUsers() {
        return dbUsers;
    }

    public void setDbUsers(User[] dbUsers) {
        this.dbUsers = dbUsers;
    }
	
	public List<ServiceAssemblyPatch> getServiceAssemblyPatchList() {
		return serviceAssemblyPatchList;
	}

	public void addServiceAssemblyPatchList(List<ServiceAssemblyPatch> serviceAssemblyPatchList) {
		this.serviceAssemblyPatchList.addAll(serviceAssemblyPatchList);
	}

	public Map<String, GroupReference> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(Map<String, GroupReference> groupMap) {
        this.groupMap = groupMap;
    }

    public Document getPatchDocument() {
        return patchDocument;
    }

    public void setPatchDocument(Document patchDocument) {
        this.patchDocument = patchDocument;
    }
}
