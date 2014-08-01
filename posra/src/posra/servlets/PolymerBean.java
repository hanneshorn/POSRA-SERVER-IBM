package posra.servlets;

import java.util.*;

public class PolymerBean {
	
	private String smiNoPoly;
	private String smi;
	private ArrayList<ArrayList<String>> smilesArray;
	private ArrayList<ArrayList<String>> pureSMILESArray;
	
	public PolymerBean(){
		smi = "";
		smiNoPoly = "";
		smilesArray = new ArrayList<ArrayList<String>>();
		pureSMILESArray = new ArrayList<ArrayList<String>>();
	}
	
	public PolymerBean(String smiles, String snp, ArrayList<ArrayList<String>> sarray, ArrayList<ArrayList<String>> puresarray) {
		smi = smiles;
		smiNoPoly = snp;
		smilesArray = sarray;
		pureSMILESArray = puresarray;
	}
	
	public String toString() {		
		return "Smiles: " + smi + "\nSmiles w/o Polymer: " + smiNoPoly + "\n" + smilesArray.toString() + "\n" + pureSMILESArray.toString() + "";
	}
	
	public void setSmi(String smile) {
		smi = smile;
	}
	
	public void setSmiNoPoly(String snp){
		smiNoPoly = snp;
	}
	
	public void setSmilesArray(ArrayList<ArrayList<String>> sarray) {
		smilesArray = sarray;
	}
	
	public void setPureSMILESArray(ArrayList<ArrayList<String>> puresarray) {
		pureSMILESArray = puresarray;
	}
	
	public String getSmi() {
		return smi;
	}
	
	public String getSmiNoPoly(){
		return smiNoPoly;
	}
	
	public ArrayList<ArrayList<String>> getSmilesArray() {
		return smilesArray;
	}
	
	public ArrayList<ArrayList<String>> getPureSMILESArray() {
		return pureSMILESArray;
	}

}
