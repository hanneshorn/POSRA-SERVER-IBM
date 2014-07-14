// ONLY AN EXAMPLE JSON SERIALIZABLE MODEL ENTITY
// TODO: Generate polymer model data with Hybernate...
//

package posra;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
 
import java.util.Vector;

import com.google.gson.annotations.SerializedName;
 
public class SMILES {
 
    @SerializedName("smilesID")
    private int id;
    private String sString;
       
     
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getsString() {
		return sString;
	}

	public void setsString(String sString) {
		this.sString = sString;
	}

	@Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("***** SMILES Details *****\n");
        sb.append("ID="+getId()+"\n");
        sb.append("SMILES="+getsString()+"\n");
        sb.append("*****************************");
         
        return sb.toString();
    }
}