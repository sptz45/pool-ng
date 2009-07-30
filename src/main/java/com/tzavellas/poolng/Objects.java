package com.tzavellas.poolng;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Objects {
	
	private Objects() { }
	
	static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == null && o2 == null) return true;
		return o1 != null && o1.equals(o2);
	}

	static boolean equalsUsingFields(final Object o1, final Object o2) {
		if (! (o1.getClass() == o2.getClass()))
			return false;
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				try {
					Field[] fields = o1.getClass().getDeclaredFields();
					for (Field f : fields) {
						f.setAccessible(true);
						if (! nullSafeEquals(f.get(o1), f.get(o2)))
							return false;
					}
					return true;
				} catch (IllegalAccessException e) {
					return false;
				}
			}
		});
	}
}
