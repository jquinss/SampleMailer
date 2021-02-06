package managers;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TableViewManager<T> {
	protected final TableView<T> tableView;
	protected final ObservableList<T> obsList = FXCollections.observableArrayList();
	
	public TableViewManager(TableView<T> tableView) {
		this.tableView = tableView;
		this.tableView.setItems(obsList);
	}
	
	public void addItem(T item) {
		obsList.add(item);
	}
	
	public void removeItem(T item) {
		obsList.remove(item);
	}
	
	public T getSelectedItem() {
		return tableView.getSelectionModel().getSelectedItem();
	}
	
	public void addItems(List<T> items) {
		obsList.addAll(items);
	}
	
	public void setItems(List<T> items) {
		obsList.setAll(items);
	}
	
	public List<T> getItems() {
		return obsList.stream().collect(Collectors.toList());
	}
	
	public void replaceItem(T oldItem, T newItem) {
		int oldItemIndex = obsList.indexOf(oldItem);
		obsList.remove(oldItemIndex);
		obsList.add(oldItemIndex, newItem);
	}
	
	public void removeAllItems() {
		obsList.clear();
	}
}
