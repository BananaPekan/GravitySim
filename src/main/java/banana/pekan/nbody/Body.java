package banana.pekan.nbody;

public class Body {

    public double mass;
    public double radius;

    public Vec3d pos;

    public Vec3d velocity;

    public Body(Vec3d pos, Vec3d initialVelocity, double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        this.pos = pos;
        this.velocity = initialVelocity;
    }

    public Vec3d getCenter() {
        return new Vec3d(pos.x + radius / 2, pos.y + radius / 2, pos.z + radius / 2);
    }

    public Vec3d calculateAcceleration(Body body) {
        return calculateForce(this, body).divide(mass);
    }

    public static Vec3d calculateForce(Body a, Body b) {
        double massA = a.mass;
        double massB = b.mass;

        double sqrDistance = b.getCenter().subtract(a.getCenter()).sqrMagnitude();
        Vec3d direction = b.getCenter().subtract(a.getCenter()).normalize();

        double rawForce = Universe.G * massA * massB / sqrDistance;

        Vec3d force;
        force = direction.multiply(rawForce);

        return force;
    }

}
