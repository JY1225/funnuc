package eu.robojob.millassist.positioning;

public final class TeachedCoordinatesCalculator {

	private TeachedCoordinatesCalculator() {
	}
	
	public static Coordinates calculateRelativeTeachedOffset(final Coordinates originalCoordinates, final Coordinates teachedOffset) {
		Coordinates result = new Coordinates(0, 0, teachedOffset.getZ(), teachedOffset.getW(), teachedOffset.getP(), teachedOffset.getR());
		double corner = originalCoordinates.getR() / 180 * Math.PI;
		double xR = teachedOffset.getX() * Math.cos(corner) + teachedOffset.getY() * Math.sin(corner);
		double yR = -teachedOffset.getX() * Math.sin(corner) + teachedOffset.getY() * Math.cos(corner);
		result.setX((float) xR);
		result.setY((float) yR);
		return result;
	}
	
	public static Coordinates calculateAbsoluteOffset(final Coordinates position, final Coordinates relativeTeachedOffset) {
		Coordinates result = new Coordinates(0, 0, relativeTeachedOffset.getZ(), relativeTeachedOffset.getW(), relativeTeachedOffset.getP(), relativeTeachedOffset.getR());
		double corner = position.getR() / 180 * Math.PI;
		double xB = relativeTeachedOffset.getX() * Math.cos(corner) - relativeTeachedOffset.getY() * Math.sin(corner);
		double yB = relativeTeachedOffset.getX() * Math.sin(corner) + relativeTeachedOffset.getY() * Math.cos(corner);
		result.setX((float) xB);
		result.setY((float) yB);
		return result;
	}
}
