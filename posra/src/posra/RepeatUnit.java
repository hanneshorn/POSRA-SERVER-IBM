// ONLY AN EXAMPLE JSON SERIALIZABLE MODEL ENTITY
// TODO: Generate polymer model data with Hybernate...
//

package posra;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
import java.util.Vector;

import com.google.gson.annotations.SerializedName;
 
public class RepeatUnit extends Segment{
 
    @SerializedName("ruID")
    private int id;
    private int numSegments;
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

	public Vector<Segment> getSegments() {
		return segments;
	}


	public void setSegments(Vector<Segment> segments) {
		this.segments = segments;
	}

	@Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("***** Repeat Unit Details *****\n");
        sb.append("ID="+getId()+"\n");
        sb.append("Segments= " + getNumSegments()+"\n");
        sb.append(getSegments() +"\n");
        sb.append("*****************************");
         
        return sb.toString();
    }
}