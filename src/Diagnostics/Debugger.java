package Diagnostics;

public class Debugger {
	private static boolean sEnabled = false;
	
	public static boolean isEnabled() {
		return sEnabled;
	}
	
	public static void setEnabled(boolean enabled) {
		sEnabled = enabled;
	}
	
	public static void log(Object object) {
		if(sEnabled) {
			System.out.println(object.toString());
		}
	}
}