package eu.robojob.millassist.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class SizeManager {
	public static int WIDTH=0;
	public static int HEIGHT=0;
	
	public static int HEIGHT_TOP=0;
	public static int HEIGHT_BOTTOM=0;
	
	public static int WIDTH_BOTTOM_LEFT=210;
	
	public static int ADMIN_MENU_WIDTH=50;
	public static int ADMIN_SUBMENU_WIDTH=150;
	
	public static void setApplicationSizes(boolean useFullWidth) {
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		setSizes((int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	public static void setApplicationSizes(int width, int height) {
		setSizes(width, height);
	}
	
	private static void setSizes(int width, int height) {
		HEIGHT_TOP = (int) (height * 0.36);
		HEIGHT_BOTTOM = (int) (height * 0.64);
		HEIGHT = HEIGHT_BOTTOM + HEIGHT_TOP;
		WIDTH = (int) width;
	}
	
}
