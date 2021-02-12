package managers;

import util.OSChecker;
import util.ObjectSerializer;
import mail.EmailTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.ListView;

public class TemplateManager extends ListViewManager<EmailTemplate> {
	private static final String TEMPLATES_PATH = OSChecker.getOSDataDirectory() + File.separator + "Templates.dat";
	private final ObjectSerializer objectSerializer;
	private boolean templateListModified = false;
	
	public TemplateManager(ListView<EmailTemplate> templateListView) {
		super(templateListView);
		objectSerializer = new ObjectSerializer(TEMPLATES_PATH);
	}
	
	public void addItem(EmailTemplate emailTemplate) {
		super.addItem(emailTemplate);
		templateListModified = true;
	}
	
	public void removeItem(EmailTemplate emailTemplate) {
		super.removeItem(emailTemplate);
		templateListModified = true;
	}
	
	public void loadItems() {
		if (objectSerializer.fileExists()) {
			try {
				objectSerializer.openFileForRead();
				List<EmailTemplate> templateList = (List<EmailTemplate>) objectSerializer.readObject();
				setItems(templateList);
			}
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					objectSerializer.closeInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveItems() {
		try {
			objectSerializer.openFileForWrite();
			List<EmailTemplate> emailTemplateList = getItems();
			objectSerializer.writeObject(emailTemplateList);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				objectSerializer.closeOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		templateListModified = false;
	}
	
	public boolean isTemplateListModified() {
		return templateListModified;
	}
}
