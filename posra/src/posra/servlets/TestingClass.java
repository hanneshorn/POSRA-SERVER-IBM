package posra.servlets;

// import java.util.*;

public class TestingClass {

	public static void main(String[] args) {		
		try {
	        String hibernateVersion = org.hibernate.annotations.common.Version.getVersionString();
	        System.out.println("Hibernate Version: "+ hibernateVersion);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}