import java.util.*;
import java.io.*;
public class navigate {
    public static void main(String[] args) throws IOException {
        //setting up input file and scanner
        File input = new File("navigate.in");
        Scanner scan = new Scanner(input);

        //scanning in constants
        int N = scan.nextInt();
        int M = scan.nextInt();
        int K = scan.nextInt();
        int P = scan.nextInt();
        Vector<Point> waypoints = new Vector<Point>(); //list of waypoints (including the destination, not including the start)
        for (int i = 0; i < K; i++) {
            waypoints.add(new Point(scan.nextInt(), scan.nextInt()));
        }
        Vector<Vector<Point>> polygons = new Vector<Vector<Point>>(); //a list of all vertices of the polygons 
                                                                      //each polygon's set of vertices is stored in a Vector for that polygon
        for (int i = 0; i < P; i++) {
            int n = scan.nextInt();
            polygons.add(new Vector<Point>());
            for (int j = 0; j < n; j++) {
                polygons.get(i).add(new Point(scan.nextInt(), scan.nextInt()));
            }
        }

        Point currPoint = new Point(1, 1); //the path starts at (1,1)
        Vector<Point> leftToSearch = new Vector<Point>(); //list of possible points left to be searched
        Vector<Point> alreadySearched = new Vector<Point>(); //record of path already taken/points already searched
        Vector<Point> path = new Vector<Point>(); //final answer of the path taken
        Vector<Point> failedPoints = new Vector<Point>(); //if needed, previous points that didn't work
        int ind = 1; //the index, uniquely given to each point, used to track which points can be reachable from which other points (through tags)

        //corresponding values from dx and dy are added to x and y respectively to move in the eight directions
        //in order: up, up+right, right, down+right, down, down+left, left, up+left
        int[] dx = {0,1,1,1,0,-1,-1,-1}; //the differences in x that must be added to the current x for each of the eight possible moves
        int[] dy = {1,1,0,-1,-1,-1,0,1}; //the differences in y that must be added to the current x for each of the eight possible moves

        //each waypoint is treated as an individual destination
        for (Point p: waypoints) {
            leftToSearch.clear(); //the set of points left to search is cleared to start a new path to the next waypoint
            alreadySearched.clear(); //the set of already visited points in cleared because you can revisit points on the way to a new waypoint
            currPoint.setTag(ind); //the current point is also assigned its own index as a tag
            leftToSearch.add(currPoint); //the current point is added to be processed

            while (!leftToSearch.isEmpty()) { //continue while there are still points left to search but the waypoint hasn't been reached yet
                currPoint = getClosestPoint(leftToSearch, p); //the closest point becomes the currPoint to be processed
                while (alreadySearched.size() > 1 && currPoint.getTag() != ind-1) { //if the current point is not the first point (because the tags would be equal) 
                                                              //and the tag does not match the last point processed, the last point should be removed
                                                              //because it did not yield a closer point to the target
                    removeTag(leftToSearch, ind-1); //the last point and any other points it could reach are removed unless they could be reached by other points
                    failedPoints.add(alreadySearched.lastElement()); //assign the failed previous point to prevPoint
                    alreadySearched.remove(alreadySearched.size()-1); //the last point is removed from the path
                    ind--; //the index decreases, since ind-1 is now available as an index
                }

                //if the current point is the waypoint/destination, remove from leftToSearch, add it to the path, 
                //and break the loop to move on to the next waypoint
                if (currPoint.getX() == p.getX() && currPoint.getY() == p.getY()) {
                    if (ind > 1) {
                        leftToSearch.remove(currPoint);
                        alreadySearched.add(currPoint);
                    }
                    break;
                }

                //even if the current point is not the destination, add it to the path and remove it from leftToSearch
                leftToSearch.remove(currPoint);
                currPoint.setTag(ind);
                alreadySearched.add(currPoint);

                //tests out each of the possible points that can be reached from currPoint
                for (int i = 0; i < 8; i++) {
                    //temporarily stores the move being tested
                    Point temp = new Point(currPoint.getX()+dx[i], currPoint.getY()+dy[i]);
                    temp.setTag(ind);

                    //checks if the point is not already in the path, is not a vertex of any polygon, is within the grid's bounds, 
                    //is not the failed previous point, and does not cross a polygon's edge
                    if (contains(alreadySearched, temp) == null && !contains2(polygons, temp)
                        && temp.getX() >= 1 && temp.getX() <= N && temp.getY() >= 1 && temp.getY() <= M
                        && contains(failedPoints, temp) == null 
                        && !touchesPolygon(polygons, currPoint, temp)) {
                        if (contains(leftToSearch, temp) == null)leftToSearch.add(temp); //if this new point is not already in leftToSearch, add it
                        else contains(leftToSearch, temp).setTag(ind); //if it is already in leftToSearch, set its tag to the current point's index
                                                                       //since it is now reachable by a more recent point
                    }
                }

                //update the index in preparation for the next point
                ind++;
            }

            if (p.getX() != waypoints.lastElement().getX() && p.getY() != waypoints.lastElement().getY()
                && p.getX() != waypoints.get(0).getX() && p.getY() != waypoints.get(0).getY()) //prevents waypoints from being added to the path twice
                alreadySearched.remove(alreadySearched.size()-1);
            path.addAll(alreadySearched); //add newly searched points to path

        }


        //output result
        FileWriter myWriter = new FileWriter("navigate.out");
        for (int i = 0; i < path.size(); i++) {
            myWriter.write(path.get(i).getX() + " " + path.get(i).getY() + "\n");
        }

        //close scanner and filewriter
        scan.close();
        myWriter.close();
    }

    //returns the point from the points not yet searched that is closest to the destination (the next waypoint)
    public static Point getClosestPoint(Vector<Point> leftToSearch, Point dest) { 
        double minDis = Double.MAX_VALUE; //stores the minimum distance found so far
        Point closest = leftToSearch.get(0); //stores the closest point found so far

        //checks each point that has not been searched yet
        for (int i = 1; i < leftToSearch.size(); i++) {
            //the distance from the destination is taken to be the square of the distance from the current point to the destination
            double dis = Math.pow(leftToSearch.get(i).getX()-dest.getX(), 2) + Math.pow(leftToSearch.get(i).getY()-dest.getY(),2);
            if (dis < minDis) {
                minDis = dis;
                closest = leftToSearch.get(i);
            }
        }
        return closest;
    }

    //checks if Point p (or a point with the same coordinates) is included in the given vector
    public static Point contains(Vector<Point> points, Point p) {
        for (Point curr: points) {
            //if the point's coordinates match with p's, it is returned
            if (curr.getX() == p.getX() && curr.getY() == p.getY()) return curr;
        }

        //if p was not found in the vector, a null is returned
        return null;
    }

    //checks if Point p is a vertex of any of the polygons
    public static boolean contains2(Vector<Vector<Point>> polygons, Point p) {
        //checks each of the polygons stored in the vector
        for (Vector<Point> currVector: polygons) {
            //if the point has been found in one of the polygons, return true
            if (contains(currVector, p) != null) return true;
        }

        //the point was not a vertex of any of the polygons
        return false;
    }

    //checks if the line segment p1p2 will intersect/touch any of the polygons
    public static boolean touchesPolygon(Vector<Vector<Point>> polygons, Point p1, Point p2) {
        //currVector looks at each polygon in the vector polygons
        for (Vector<Point> currVector: polygons) {
            //checks every edge of the polygon
            for (int i = 0; i < currVector.size(); i++) {
                //if p1p2 does intersect a side of the polygon, return true
                if (willIntersect(p1, p2, currVector.get(i), currVector.get((i+1)%currVector.size()))) return true;
            }
        }

        //p1p2 does not touch any of the polygons
        return false;
    }

    //checks whether line segments p1q1 and p2q2 have an intersection
    public static boolean willIntersect(Point p1, Point q1, Point p2, Point q2) {
        //values to be used later        
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
  
        //general case
        if (o1 != o2 && o3 != o4) return true;
  
        //edge cases
        //if p2 is on p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        //if q2 is on p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        //if p1 is on p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        //if q1 is on p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;
  
        //the two line segments will not intersect
        return false;
    }

    //given that p, q, r are collinear points, will check if q is between p and r
    static boolean onSegment(Point p, Point q, Point r) {
        //checks whether q's coordinates is within those of p and r
        if (q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) 
        && q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY())) return true;
        
        //q is not on pr
        return false;
    }

    // checks the orientation of points p,q,r (in that order)
    public static int orientation(Point p, Point q, Point r)
    {
        //derived using slope - if the slope of pq is greater than qr, it is clockwise. 
        //if the reverse is true, they are counterclockwise. if they are the same, the points are collinear
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
      
        //return orientation
        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clockwise (1) or counterclockwise (2)
    }

    //when the current point was not viable, removes traces of it from leftToSearch (more detail below)
    public static void removeTag(Vector<Point> leftToSearch, int tag) {
        //checks each point in leftToSearch
        for (int i = leftToSearch.size()-1; i >= 0; i--) {
            Point p = leftToSearch.get(i);

            //if p was reachable from the current point (has the designated tag)
            if (p.getTag() == tag) {
                //if p was only reachable from the current point, it is removed from leftToSearch
                if (p.getPrevTag() == 0) leftToSearch.remove(i); 
                
                //if p could be reached from another previous point in the path, its tag is reverted to that point's index
                else p.setTag(p.getPrevTag());
            }
        }
    }
}

//class for storing points on the coordinate grid
class Point {
    private int x; //x-coordinate
    private int y; //y-coordinate
    private int tag = 0; //the current tag is the last index value of point
                         //from which the current point can be reached 
                         //(each new step in the path corresponds with a new index)
    private Vector<Integer> prevTags = new Vector<>(); //a list of the point's previous tags in chronological order
                                                       //(not including the current one) 

    //constructor
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTag() {
        return tag;
    }

    //gets the last/most recent previous tag
    public int getPrevTag() {
        return prevTags.get(prevTags.size()-1);
    }

    //sets a new tag and adds the old tag to prevTags
    public void setTag(int tag) {
        this.prevTags.add(this.tag);
        this.tag = tag;
    }
}