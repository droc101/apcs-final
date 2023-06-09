import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Ray {

    // more friendly wrapper for native raycast function (see Native.java and native/Native.c)

    public Vector2 origin;
    public double direction;

    private Map<Wall, Vector2> intersections = new HashMap<Wall, Vector2>();

    public Ray(Vector2 origin, double direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public boolean Intersects(Wall wall) {
        Vector2 intersection = Intersection(wall);
        return intersection != null;
    }

    public double Cast(Level level) {
        Wall closestWall = null;
        double closestDist = Double.MAX_VALUE;
        for (Wall wall : level.GetAllWalls()) {
            Vector2 intersection = Intersection(wall);
            if (intersection != null) {
                double distance = Vector2.Distance(origin, intersection);
                if (distance < closestDist) {
                    closestWall = wall;
                    closestDist = distance;
                }
            }
        }
        // If no wall was hit, return -1
        if (closestWall != null) {
            return closestDist;
        }
        return -1;
    }

    public Entity HitscanEntities(Level level) {
        HashMap<Entity, Wall> entityWalls = new HashMap<Entity, Wall>();
        for (Entity entity : level.entities) {
            Wall wall = entity.makeWall();
            entityWalls.put(entity, wall);
        }

        // Find the closest wall
        // key value pair of entity and wall
        Entity closestWall = null;
        Wall closestWallWall = null;
        double closestDist = Double.MAX_VALUE;
        for (Wall wall : entityWalls.values()) {
            Vector2 intersection = Intersection(wall);
            if (intersection != null) {
                double distance = Vector2.Distance(origin, intersection);
                if (distance < closestDist) {
                    closestWall = entityWalls.entrySet().stream().filter(entry -> entry.getValue().equals(wall)).findFirst().get().getKey();
                    closestWallWall = wall;
                    closestDist = distance;
                }
            }
        }
        // If no wall was hit, return -1
        if (closestDist == Double.MAX_VALUE) {
            return null;
        }
        return closestWall;
    }

    public Vector2 Intersection(Wall wall) {
        if (intersections.containsKey(wall)) {
            return intersections.get(wall);
        }

        double[] result = Native.RayCast(wall.vertA.x, wall.vertA.y, wall.vertB.x, wall.vertB.y, origin.x, origin.y, direction);
        // Check the length of the result array
        if (result.length == 0) {
            return null; // No intersection
        }

        // Convert the result array to a Vector2
        Vector2 intersection = new Vector2(result[0], result[1]);
        intersections.put(wall, intersection);
        return intersection;
    }
}
