// ONLY AN EXAMPLE JSON SERIALIZABLE MODEL ENTITY
// TODO: Generate polymer model data with Hybernate...
//

package posra;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
import java.util.Vector;

import com.google.gson.annotations.SerializedName;
 
public class Segment {
 
    @SerializedName("segID")
    private int id;
    private int degree;
    private int numBonds;
    private SMILES sString;
    private Vector<Segment> bonds;
     
     
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getDegree() {
		return degree;
	}


	public void setDegree(int degree) {
		this.degree = degree;
	}


	public int getNumBonds() {
		return numBonds;
	}


	public void setNumBonds(int numBonds) {
		this.numBonds = numBonds;
	}


	public SMILES getsString() {
		return sString;
	}


	public void setsString(SMILES sString) {
		this.sString = sString;
	}


	public Vector<Segment> getBonds() {
		return bonds;
	}


	public void setBonds(Vector<Segment> bonds) {
		this.bonds = bonds;
	}


	@Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("***** Segment Details *****\n");
        sb.append("ID="+getId()+"\n");
        sb.append("Degree="+getDegree()+"\n");
        sb.append("SMILES="+getsString()+"\n");
        sb.append("Bonds= " + getNumBonds()+"\n");
        sb.append(getBonds() +"\n");
        sb.append("*****************************");
         
        return sb.toString();
    }
}