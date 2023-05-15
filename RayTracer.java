import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RayTracer {
    Level level;

    public RayTracer(Level level) {
        this.level = level;
    }

    public void RenderCol(FrameBuffer buffer, Vector2 FromPos, double FromRot, int col, int screenH) {

        // Get the angle of the column
        // FOV is 90 degrees
        double angle = Math.atan2(col - buffer.width / 2, buffer.width / 2) + FromRot;

        // Create a ray
        Ray ray = new Ray(FromPos, angle);
        // Loop over each wall until a wall is hit
        
        // Look over each wall and get the closest one
        Wall closestWall = null;
        double closestDist = Double.MAX_VALUE;
        for (Wall wall : level.walls) {
            // Check if the ray intersects the wall
            if (ray.Intersects(wall)) {
                // Get the intersection point
                Vector2 intersection = ray.Intersection(wall);
                // Get the distance from the ray origin to the intersection point
                double distance = Vector2.Distance(FromPos, intersection) * Math.cos(angle - FromRot);
                // Check if the distance is less than the closest distance
                if (distance < closestDist) {
                    // Set the closest wall to the current wall
                    closestWall = wall;
                    // Set the closest distance to the current distance
                    closestDist = distance;
                }
            }
        }

        if (closestWall == null) {return;}
                // Get the wall's texture
                String wallTex = "texture/" + closestWall.texture + ".png";
                // Load the texture using ImageIO
                BufferedImage tex = null;
                try {
                    tex = ImageIO.read(new File(wallTex));
                } catch (IOException e) {
                    // Load texture/missing.png instead
                    try {
                        tex = ImageIO.read(new File("texture/missing.png"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                // Get the height of the texture
                int texH = tex.getHeight();
                // Get the width of the texture
                int texW = tex.getWidth();

                


                //!BUG! ArrayIndexOutOfBoundsException: Coordinate out of bounds!
                // Get the intersection point
                Vector2 intersection = ray.Intersection(closestWall);

                // Get the length of the wall
                double wallLength = Vector2.Distance(closestWall.vertA, closestWall.vertB);

                // Get the local x position of the intersection point
                double localX = Vector2.Distance(closestWall.vertA, intersection);

                double texCol = localX / wallLength * texW;
                texCol *= wallLength;
                texCol %= texW;

                // texCol = 0;

                // Get the distance from the ray origin to the intersection point
                double distance = Vector2.Distance(FromPos, intersection) * Math.cos(angle - FromRot);

                // Get the height of the wall on screen
                double height = screenH / distance;

                // Get the y position of the wall
                double y = (buffer.height - height) / 2;

                // Calcuate the shade of the wall based on the camera angle and wall normal
                double shade = Math.abs(Math.cos((FromRot + (1.5*Math.PI)) - closestWall.getAngle()));
                // Make sure the shade is between 40 and 255
                shade = Math.max(0.4, Math.min(1, shade));

                // Calculate the color of the wall
                int r = (int) (255 * shade);
                int g = (int) (255 * shade);
                int b = (int) (255 * shade);
                Color color = new Color(r, g, b);

                // Draw the wall using its texture and shade
                for (int i = 0; i < height; i++) {
                    // Check if the Y pixel is on screen
                    if (y + i < 0 || y + i >= buffer.height) continue;
                    // Get the y position of the texture
                    int texY = (int) (i / height * texH);
                    // Get the color of the pixel in the texture
                    //System.out.println(texCol + " " + texY);
                    int texColor = tex.getRGB((int)texCol, texY);
                    // Get the color of the pixel in the texture
                    int texR = (texColor >> 16) & 0xFF;
                    int texG = (texColor >> 8) & 0xFF;
                    int texB = (texColor >> 0) & 0xFF;
                    // Calculate the color of the pixel on the wall
                    r = (int) (texR * shade);
                    g = (int) (texG * shade);
                    b = (int) (texB * shade);
                    color = new Color(r, g, b);
                    // Draw the pixel
                    buffer.setPixel(col, (int) y + i, color);
                }
                //buffer.drawRect(col, (int) y, 1, (int) height, color);

                // Return
                return;
    }

    // Ray trace from a position and rotation until a wall is hit
    public Vector2 RayTrace(Vector2 FromPos, double FromRot) {
        // Create a ray
        Ray ray = new Ray(FromPos, FromRot);

        // Loop over each wall
        for (Wall wall : level.walls) {
            return ray.Intersection(wall);
        }

        // Return null if no wall was hit
        return null;
    }
}
