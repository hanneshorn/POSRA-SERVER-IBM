package posra.servlets;

public class TestingClass {

	public static void main(String[] args) {
		String[] s = {"C[Po:n]CC(C(=O)OC)([Lv:n]C)C", 
				"CCCC([Lv:m]O[R])CCCOC(=O)[Te:n;m]CCO[Po:n]C", 
				"C[Lv:n]CC([Po:n]C)C",
				"[Lv:n]OCC[Po:n]OC",
				"C[Po:50]CC(c1ccccc1)[Lv:50]C",
				};
		
		for(String ss: s) {
			System.out.println("Original: " + ss);
			System.out.println("Result  : " + fixSideChain(ss, ""));
		}
	}
	
	public static String fixSideChain(String s, String stack) {

		int firstParen = s.indexOf("(");
		int lastParen = s.lastIndexOf(")");
		
		if(firstParen < 0 || lastParen < 0) { return s; }
		
		String eg1=s.substring(0,firstParen);
		String eg2=s.substring(lastParen + 1, s.length());
		
		boolean eg1hp = hasPolymer(eg1);
		boolean eg2hp = hasPolymer(eg2);
		
		//find the matching parenthesis of the firstParen
		// args are as such:
		// the index of the paren; -1 if closing, 1 if opening; string to iterate over
		int firstMatch = findMatchingParen(firstParen, 1, s);
		int lastMatch = findMatchingParen(lastParen, -1, s);
		
		String firstSeg = s.substring(firstParen+1, firstMatch);
		String lastSeg = s.substring(lastMatch+1, lastParen);
		boolean fshp = hasPolymer(firstSeg);
		boolean lshp = hasPolymer(lastSeg);
		
		if(fshp && !eg1hp) { 
			s = switchParentheses(s, eg1, firstSeg, 0);
		}
		if(lshp && !eg2hp) { 
			s = switchParentheses(s, eg2, lastSeg, lastParen + 1);
		}
		
		return s;
	}
	
	public static boolean hasPolymer(String s) {
		return s.contains("[Lv:") || s.contains("[Po:") || s.contains("[Te:");
	}
	
	public static int findMatchingParen(int paren, int adder, String s) {
		int match = paren, counter = 1;
		
		while(counter > 0) {
			match += adder;
			char c = s.charAt(match);
			if(c =='(' || c == '[') { counter += adder; }
			else if(c == ')' || c == ']') { counter -= adder; }
		}
		
		return match;
	}
	
	public static String switchParentheses(String s, String eg, String seg, int firstOrLast) {
		// if we are passed a 0 in 'firstOrLast', we are dealing with a replacement at the front
		// and we will need to rearrange the string that was originally inside the parentheses
		// (which is here called the 'seg')
		
		eg = "(" + eg + ")";
		String temp = "";
		if(firstOrLast == 0) { 
			seg = switchOrder(seg);
			temp = seg + eg;
			return temp + s.substring(temp.length(), s.length());
		}
		
		temp = eg + seg; 
		return s.substring(0, s.length() - temp.length()) + temp;	
	}
	
	public static String switchOrder(String switchMe) {
		// switch order of everything naively except the 
		// contents of polymer brackets... want to maintain
		// the left-to-right reading of those.
		String newString = "", temp = "";
		int matcher;
		
		for(int i=0; i<switchMe.length(); ++i) {
			if((i+4) < switchMe.length() && hasPolymer(switchMe.substring(i, i+4))) {
				newString = switchMe.substring(i, (switchMe.indexOf("]")+1)) + newString;
				i = switchMe.indexOf("]") + 1;
			} if (switchMe.substring(i, (i+1)).equals("(") || 
				  switchMe.substring(i, (i+1)).equals("[")) {
				matcher = findMatchingParen(i, 1, switchMe);
				temp = switchOrder(switchMe.substring((i+1), matcher));
				
				if(switchMe.substring(i, (i+1)).equals("(")) {
					newString = "(" + temp + ")" + newString;
				}
				else {
					newString = "[" + temp + "]" + newString;
				}
				
				i = matcher+1;
			} else {
				newString = switchMe.substring(i, i+1) + newString;
			}
		}
		return newString;
	}
}