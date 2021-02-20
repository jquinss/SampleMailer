package managers;

import util.OSChecker;
import util.ObjectSerializer;
import mail.EmailTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.scene.control.ListView;

public class TemplateManager extends ListViewManager<EmailTemplate> {
	private static final String TEMPLATES_FILE_NAME = "Templates.dat";
	private static final String TEMPLATES_DIR_NAME = "SampleMailer";
	private static final String TEMPLATES_ROOT_DIR = OSChecker.getOSDataDirectory() + File.separator + TEMPLATES_DIR_NAME;
	private static final String TEMPLATES_PATH = TEMPLATES_ROOT_DIR + File.separator + TEMPLATES_FILE_NAME;

	private ObjectSerializer objectSerializer;
	private boolean templateListModified = false;
	
	public TemplateManager(ListView<EmailTemplate> templateListView) {
		super(templateListView);
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
		objectSerializer = new ObjectSerializer(TEMPLATES_PATH);
		
		if (objectSerializer.fileExists()) {
			try {
				objectSerializer.openFileForRead();
				@SuppressWarnings("unchecked")
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
		objectSerializer = new ObjectSerializer(TEMPLATES_PATH);
		
		try {
			Files.createDirectories(Paths.get(TEMPLATES_ROOT_DIR));
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
