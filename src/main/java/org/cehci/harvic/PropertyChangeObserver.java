package org.cehci.harvic;

public interface PropertyChangeObserver {
	public void onPropertyChange(String property, Object oldValue,
		Object newValue);
}
