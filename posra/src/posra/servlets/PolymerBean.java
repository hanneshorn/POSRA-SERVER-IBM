package posra.servlets;

import java.io.*;
import java.util.*;

public class PolymerBean {
	
	private String SmiNoPoly;
	private ArrayList<ArrayList<String>> SMILESArray;
	private ArrayList<ArrayList<String>> pureSMILESArray;
	
	public PolymerBean(){
		SmiNoPoly = "";
		SMILESArray = new ArrayList<ArrayList<String>>();
		pureSMILESArray = new ArrayList<ArrayList<String>>();
	}
	
	public PolymerBean(String snp, ArrayList<ArrayList<String>> sarray, ArrayList<ArrayList<String>> puresarray) {
		SmiNoPoly = snp;
		SMILESArray = sarray;
		pureSMILESArray = puresarray;
	}
	
	public void setSmiNoPoly(String snp){
		SmiNoPoly = snp;
	}
	
	public String getSmiNoPoly(){
		return SmiNoPoly;
	}
	
	public void setSMILESArray(ArrayList<ArrayList<String>> sarray) {
		SMILESArray = sarray;
	}
	
	public ArrayList<ArrayList<String>> getSMILESArray() {
		return SMILESArray;
	}
	
	public void setPureSMILESArray(ArrayList<ArrayList<String>> puresarray) {
		pureSMILESArray = puresarray;
	}
	
	public ArrayList<ArrayList<String>> getPureSMILESArray() {
		return pureSMILESArray;
	}

}
