package com.tibco.customwizard.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;

public class PropertyUtils {
	public static void setAttributes(Object bean, Map<Object, Object> attributes) throws Exception {
        if (attributes.size() > 0) {
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for (Entry<Object, Object> entry : attributes.entrySet()) {
                setProperty(bean, entry.getKey().toString(), entry.getValue(), propertyDescriptors);
            }
        }
    }

    public static void setAttributes(Object bean, Attributes attributes) throws Exception {
        int len = attributes.getLength();
        if (len > 0) {
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for (int i = 0; i < len; i++) {
                setProperty(bean, attributes.getQName(i), attributes.getValue(i), propertyDescriptors);
            }
        }
    }

    private static void setProperty(Object bean, String propertyName, Object value, PropertyDescriptor[] propertyDescriptors) throws Exception {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equals(propertyName)) {
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null) {
                    writeMethod.invoke(bean, new Object[] { convertPropertyValue(value, propertyDescriptor.getPropertyType()) });
                }
            }
        }
    }

	public static Map<Object, Object> convertToMap(Attributes attributes) {
		int len = attributes.getLength();
		Map<Object, Object> attrMap = new HashMap<Object, Object>(len);
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				attrMap.put(attributes.getQName(i), attributes.getValue(i));
			}
		}
		return attrMap;
	}
	
    public static Object convertPropertyValue(Object value, Class<?> propertyClass) throws Exception {
        if (value != null && propertyClass != value.getClass()) {
            if (propertyClass == boolean.class || propertyClass == Boolean.class) {
                return Boolean.valueOf(value.toString());
            } else if (propertyClass == int.class || propertyClass == Integer.class) {
                return Integer.valueOf(value.toString());
            } else if (propertyClass == long.class || propertyClass == Long.class) {
                return Integer.valueOf(value.toString());
            } else {
                return null;
            }
        }
        return value;
    }
}
