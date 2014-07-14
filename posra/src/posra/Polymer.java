// ONLY AN EXAMPLE JSON SERIALIZABLE MODEL ENTITY
// TODO: Generate polymer model data with Hybernate...
//

package posra;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
import java.util.Vector;

import com.google.gson.annotations.SerializedName;
 
public class Polymer {
 
    @SerializedName("polymID")
    private int id;
    private int numSegments;
    private String name;
    private Vector<Segment> segments;
     
    public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getNumSegments() {
		return numSegments;
	}


	public void setNumSegments(int numSegments) {
		this.numSegments = numSegments;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Vector<Segment> getSegments() {
		return segments;
	}


	public void setSegments(Vector<Segment> segments) {
		this.segments = segments;
	}

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("***** Polymer Details *****\n");
        sb.append("ID="+getId()+"\n");
        sb.append("Name="+getName()+"\n");
        sb.append("Segments= " + getNumSegments()+"\n");
        sb.append(getSegments() +"\n");
        sb.append("*****************************");
         
        return sb.toString();
    }
}