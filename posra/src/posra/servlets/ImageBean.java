package posra.servlets;

import java.io.*;

public class ImageBean {
	
	private String path;
	private File uploadedFile;
	
	public ImageBean() {
		path = "";
		uploadedFile = new File("");
	}
	
	public ImageBean( String p, File u ) {
		path = p;
		uploadedFile = u;
	}
	
	public String getPath() {
		return path;
	}
	
	public File getUploadedFile() {
		return uploadedFile;
	}
	
	public void setPath(String s) {
		path = s;
	}
	
	public void setUploadedFile(File f) {
		uploadedFile = f;
	}

}
