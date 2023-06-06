public class Vector2 {

    // 2D position class

    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double Distance(Vector2 fromPos, Vector2 intersection) {
        return Math.sqrt(Math.pow(intersection.x - fromPos.x, 2) + Math.pow(intersection.y - fromPos.y, 2));
    }

    public static Vector2 fromAngle(double playerRot) {
        return new Vector2(Math.cos(playerRot), Math.sin(playerRot));
    }

    public Vector2 scale(double d) {
       return new Vector2(x * d, y * d);
    }

    public Vector2 add(Vector2 scale) {
        return new Vector2(x + scale.x, y + scale.y);
    }

    public String toString() {
        return "X " + ((int) x) + " Y " + ((int)y);
    }

    public Vector2 clone() {
        return new Vector2(x, y);
    }

    public Vector2 normalize() {
        double length = Math.sqrt(x * x + y * y);
        return new Vector2(x / length, y / length);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

}
