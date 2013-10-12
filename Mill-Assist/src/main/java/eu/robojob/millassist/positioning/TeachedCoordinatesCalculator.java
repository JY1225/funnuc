package eu.robojob.millassist.positioning;

public final class TeachedCoordinatesCalculator {

	private TeachedCoordinatesCalculator() {
	}
	
	public static Coordinates calculateRelativeTeachedOffset(final Coordinates originalCoordinates, final Coordinates teachedOffset) {
		/*Coordinates result = new Coordinates(0, 0, teachedOffset.getZ(), teachedOffset.getW(), teachedOffset.getP(), teachedOffset.getR());
		double corner = originalCoordinates.getR() / 180 * Math.PI;
		double xR = teachedOffset.getX() * Math.cos(corner) + teachedOffset.getY() * Math.sin(corner);
		double yR = -teachedOffset.getX() * Math.sin(corner) + teachedOffset.getY() * Math.cos(corner);
		result.setX((float) xR);
		result.setY((float) yR);
		return result;*/
		return calculateRelativeOffset2(originalCoordinates, teachedOffset);
	}
	
	public static Coordinates calculateAbsoluteOffset(final Coordinates position, final Coordinates relativeTeachedOffset) {
		/*Coordinates result = new Coordinates(0, 0, relativeTeachedOffset.getZ(), relativeTeachedOffset.getW(), relativeTeachedOffset.getP(), relativeTeachedOffset.getR());
		double corner = position.getR() / 180 * Math.PI;
		double xB = relativeTeachedOffset.getX() * Math.cos(corner) - relativeTeachedOffset.getY() * Math.sin(corner);
		double yB = relativeTeachedOffset.getX() * Math.sin(corner) + relativeTeachedOffset.getY() * Math.cos(corner);
		result.setX((float) xB);
		result.setY((float) yB);
		return result;*/
		return calculateAbsoluteOffset2(position, relativeTeachedOffset);
	}
	
	public static Coordinates calculateRelativeOffset2(final Coordinates position, final Coordinates teachedOffset) {
		Coordinates result = new Coordinates(0, 0, 0, teachedOffset.getW(), teachedOffset.getP(), teachedOffset.getR());
		double w = position.getW() / 180 * Math.PI;
		double p = position.getW() / 180 * Math.PI;
		double r = position.getR() / 180 * Math.PI;
		double x = teachedOffset.getX();
		double y = teachedOffset.getY();
		double z = teachedOffset.getZ();
		double cosw = Math.cos(w);
		double sinw = Math.sin(w);
		double cosp = Math.cos(p);
		double sinp = Math.sin(p);
		double cosr = Math.cos(r);
		double sinr = Math.sin(r);
		double xR = cosp*cosr*x + sinr*cosp*y - sinp*z;
		double yR = (sinp*sinw*cosr - sinr*cosw)*x + (sinp*sinr*sinw + cosr*cosw)*y + sinw*cosp*z;
		double zR = (sinp*cosr*cosw + sinr*sinw)*x + (sinp*sinr*cosw - sinw*cosr)*y + cosp*cosw*z;
		result.setX((float) xR);
		result.setY((float) yR);
		result.setZ((float) zR);
		return result;
	}
	
	public static Coordinates calculateAbsoluteOffset2(final Coordinates position, final Coordinates teachedOffset) {
		Coordinates result = new Coordinates(0, 0, 0, teachedOffset.getW(), teachedOffset.getP(), teachedOffset.getR());
		double w = position.getW() / 180 * Math.PI;
		double p = position.getW() / 180 * Math.PI;
		double r = position.getR() / 180 * Math.PI;
		double x = teachedOffset.getX();
		double y = teachedOffset.getY();
		double z = teachedOffset.getZ();
		double cosw = Math.cos(w);
		double sinw = Math.sin(w);
		double cosp = Math.cos(p);
		double sinp = Math.sin(p);
		double cosr = Math.cos(r);
		double sinr = Math.sin(r);
		double xR = cosp*cosr*x + (sinp*sinw*cosr - sinr*cosw)*y + (sinp*cosr*cosw + sinr*sinw)*z;
		double yR = sinr*cosp*x + (sinp*sinr*sinw + cosr*cosw)*y + (sinp*sinr*cosw - sinw*cosr)*z;
		double zR = -sinp*x + sinw*cosp*y + cosp*cosw*z;
		result.setX((float) xR);
		result.setY((float) yR);
		result.setZ((float) zR);
		return result;
	}
}
