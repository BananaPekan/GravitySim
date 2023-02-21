package banana.pekan.nbody;

public class Vec3d {

    public double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d divide(double d) {
        return new Vec3d(x / d, y / d, z / d);
    }

    public double sqrMagnitude() {
        return x*x + y*y + z*z;
    }

    public double magnitude() {
        return Math.sqrt(sqrMagnitude());
    }

    public Vec3d multiply(double m) {
        return new Vec3d(x * m, y * m, z * m);
    }

    public Vec3d normalize() {
        double m = magnitude();
        return new Vec3d(x / m, y / m, z / m);
    }

    public Vec3d add(Vec3d vec3d) {
        return new Vec3d(x + vec3d.x, y + vec3d.y, z + vec3d.z);
    }

    public Vec3d negate() {
        return new Vec3d(-x, -y, -z);
    }

    public Vec3d subtract(Vec3d vec3d) {
        return add(vec3d.negate());
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
