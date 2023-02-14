package com.jquinss.samplemailer.util;

/**
 * 
 * Class used to serialize/deserialize objects.
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectSerializer {
	private ObjectInputStream objInputStream;
	private ObjectOutputStream objOutputStream;
	private File fileName;
	
	public ObjectSerializer(File fileName) {
		this.fileName = fileName;
	}
	
	public ObjectSerializer(String fileName) {
		this.fileName = new File(fileName);
	}
	
	public void openFileForRead() throws IOException {
		if (objInputStream == null) {
			objInputStream = new ObjectInputStream(new FileInputStream(fileName));
		}
	}
	
	public void openFileForWrite() throws IOException {
		if (objOutputStream == null) {
			objOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
		}
	}
	
	public Object readObject() throws ClassNotFoundException, IOException {
		Object obj = objInputStream.readObject();
		
		return obj;
	}
	
	public void writeObject(Object obj) throws IOException {
		objOutputStream.writeObject(obj);
	}
	
	public void closeInput() throws IOException {
		objInputStream.close();
	}
	
	public void closeOutput() throws IOException {
		objOutputStream.close();
	}
	
	public boolean fileExists() {
		return fileName.exists();
	}
}
