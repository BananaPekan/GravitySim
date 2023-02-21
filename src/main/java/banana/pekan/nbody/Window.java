package banana.pekan.nbody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Window {

    JFrame frame;

    int width = 512;
    int height = 512;
    float scale = 1.2f;

    int currentFPS = 0;
    int fps = 0;

    public Window() {
        width *= scale;
        height *= scale;
        frame = new JFrame("NBody simulation");

        init();

        frame.setBounds(0, 0, width, height);

        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                render(g);
            }
        };

        Dimension dim = new Dimension(width, height);

        panel.setMinimumSize(dim);
        panel.setPreferredSize(dim);
        panel.setMaximumSize(dim);

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        long last;
        long prev = System.nanoTime();
        long delta = 0;
        long fpsCount = 0;

        while (true) {
            last = System.nanoTime();
            delta += last - prev;
            fpsCount += last - prev;
            prev = last;

            if (delta / 1000000000.0 >= maxFPS) {
                delta = 0;
                panel.repaint();
                currentFPS++;
            }

            if (fpsCount / 1000000000.0 >= 1) {
                fps = currentFPS;
                currentFPS = 0;
                System.out.println("FPS: " + fps);
                fpsCount = 0;
            }


        }

    }

    double maxFPS = 1 / 60d;

    Body[] bodies;

    int simpos = 1;

    int mouseX, mouseY;

    public void init() {
        if (simpos == 0) {
            bodies = new Body[2];
            bodies[0] = new Body(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), 100, 5);
            bodies[1] = new Body(new Vec3d(100, 0, 0), new Vec3d(0, 0.01, 0), 0.01, 0.5);
        }
        else if (simpos == 1) {
            bodies = new Body[2];
            bodies[0] = new Body(new Vec3d(-50, 0, 0), new Vec3d(0.5, -0.5, 0), 100, 0.75);
            bodies[1] = new Body(new Vec3d(50, 0, 0), new Vec3d(-0.5, 0.5, 0), 100, 0.75);
        }
        else if (simpos == 2) {
            bodies = new Body[2];
            bodies[0] = new Body(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), 100, 5);
//            bodies[0] = new Body(new Vec3d(50, 0, 0), new Vec3d(0, 1, 0), 1, 0.5);
            bodies[1] = new Body(new Vec3d(100, 0, 0), new Vec3d(0, 0.35, 1), 1, 0.5);
        }
    }

    int iterations = 10;
    int iteration = 0;

    public void update() {

        updateVelocities();

        updatePositions();

    }

    double timeStep = 1;

    public void updateVelocities() {
        for (Body body : bodies) {
            for (Body otherBody : bodies) {
                if (body == otherBody) continue;

                Vec3d acceleration = body.calculateAcceleration(otherBody);

                body.velocity = body.velocity.add(acceleration.divide(timeStep));

                System.out.println(body.velocity);

            }
        }
    }

    public void updatePositions() {
        for (Body body : bodies) {

            Vec3d originalPos = body.pos;
            body.pos = body.pos.add(body.velocity.divide(timeStep));

            boolean collided = false;

            for (Body otherBody : bodies) {
                if (body == otherBody) continue;

                if (areColliding(body, otherBody)) {
                    collided = true;
                }

                if (collided) {
                    body.pos = originalPos;
                    break;
                }

            }


        }
    }

    double magnify = 10;

    public boolean areColliding(Body a, Body b) {
        double radiusB = b.radius * magnify;

        double xPos = a.pos.x - b.pos.x;
        double yPos = a.pos.y - b.pos.y;
        double zPos = a.pos.z - b.pos.z;

        return xPos >= -radiusB && xPos <= radiusB && yPos >= -radiusB && yPos <= radiusB && zPos >= -radiusB && zPos <= radiusB;
    }

    public void render(Graphics g) {
        update();

        g.setColor(Color.DARK_GRAY.darker());
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);


//        for (Body body : bodies) {
//            drawBody(g, 0, 0, body, magnify);
//        }


        Graphics2D g2d = (Graphics2D) g;

        GradientPaint star = new GradientPaint(0f, 0f, Color.YELLOW.brighter(), 400f, 400f, Color.YELLOW.darker());
        GradientPaint planet = new GradientPaint(0f, 0f, Color.GREEN.brighter(), 0.01f, 0.1f, Color.BLUE);

        g2d.setPaint(star);
        fillBody(g2d, 0, 0, bodies[0], magnify);

        g.setColor(Color.WHITE);
        drawBody(g, 0, 0, bodies[0], magnify);

//        g.setColor(new Color(0x664A4A));
//        g.setColor(new Color(0x6FA549));
        g2d.setPaint(planet);
        fillBody(g2d, 0, 0, bodies[1], magnify);
        g.setColor(Color.WHITE);
        drawBody(g, 0, 0, bodies[1], magnify);

        boolean overlapping = isOverlapping(bodies[1], bodies[0]);

        if (overlapping) {
//            g.setColor(Color.DARK_GRAY.darker());
            g2d.setPaint(star);
            fillBody(g2d, 0, 0, bodies[0], magnify);
            g.setColor(Color.WHITE);
            drawBody(g, 0, 0, bodies[0], magnify);
        }

        if (iteration * maxFPS >= iterations) {
//            iteration = 0;
            if (maxFPS < 1 / 60.0) {
                maxFPS = (1 / ((1 / maxFPS) - 1));
            }
        }
        else {
            iteration++;
        }

    }

    public boolean isOverlapping(Body a, Body b) {

        double radiusA = a.radius * magnify;
        double radiusB = b.radius * magnify;

        boolean overlapping = (a.pos.x - radiusA) < (b.pos.x + radiusB);
        overlapping = overlapping && (a.pos.x + radiusA) > (b.pos.x - radiusB);
        overlapping = overlapping && a.pos.z < b.pos.z;

        return overlapping;

    }

    public void drawBody(Graphics g, double x, double y, Body body, double magnify) {
        g.drawOval((int) ((int) (body.pos.x + width / 2 - (body.radius * magnify)) + x), (int) ((int) (body.pos.y + height / 2 - (body.radius * magnify)) + y), (int) (body.radius * magnify) * 2, (int) (body.radius * magnify) * 2);
    }

    public void fillBody(Graphics g, int x, int y, Body body, double magnify) {
        g.fillOval((int) ((int) (body.pos.x + width / 2 - (body.radius * magnify)) + x), (int) ((int) (body.pos.y + height / 2 - (body.radius * magnify)) + y), (int) (body.radius * magnify) * 2, (int) (body.radius * magnify) * 2);
    }

}
