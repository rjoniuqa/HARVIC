package org.cehci.harvic;

public interface PropertyChangeObservable {
	public void attachObserver(PropertyChangeObserver observer);

	public void notifyPropertyChange(String property, Object oldValue, Object newValue);
}
