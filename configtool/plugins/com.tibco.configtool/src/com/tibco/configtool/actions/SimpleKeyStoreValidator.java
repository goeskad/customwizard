package com.tibco.configtool.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;

import org.nuxeo.xforms.ui.UIForm;

import com.tibco.customwizard.action.ICustomAction;

public class SimpleKeyStoreValidator extends KeyStoreValidator implements ICustomAction {

	public void verifyKeystore(UIForm form) throws Exception {
		
		String keyStoreType = form.getControl("keystoretype").getXMLValue();
		String keyStoreLocation= form.getControl("keystorelocation").getXMLValue();
		String keyStorePassword= form.getControl("keystorepassword").getXMLValue();
		String keyAlias=form.getControl("keyalias").getXMLValue();
		String keyPassword= form.getControl("keypassword").getXMLValue();
		
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		InputStream keyFileInput = new FileInputStream(new File(keyStoreLocation));
		ks.load(keyFileInput,keyStorePassword.toCharArray());
		Key key = ks.getKey(keyAlias, keyPassword.toCharArray());
		System.out.println(key);
		if(key==null){
			throw new Exception("Key with alias is not exist or password is not correct");
		} 
		
	}
	
}
