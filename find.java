import java.util.*;
import java.io.*;
public class find {
    public static void main(String[] args) throws IOException {
        //set up file scanner and writer
        File input = new File("find.in");
        Scanner scan = new Scanner(input);
        FileWriter myWriter = new FileWriter("find.out");

        //scan in constants
        int N = scan.nextInt();
        int M = scan.nextInt();
        float dx = scan.nextFloat();
        float dy = scan.nextFloat();
        int K = scan.nextInt();

        //process each query individually
        for (int i = 0; i < K; i++) {

            //scan in an individual point
            float x = scan.nextFloat();
            float y = scan.nextFloat();

            x /= dx; //divide by dx to find which index of x coordinate is closest
            if (x % 1 == 0.5) x -= 1; //if the point is equally close to two indices, choose the lower index
            x = Math.round(x); //round x to the nearest integer

            //repeat the same process for y (same reasoning)
            y /= dy;
            if (y % 1 == 0.5) y -= 1;
            y = Math.round(y);

            //restrict x and y to the bounds of the coordinate plane
            if (x >= N) x = N-1;
            if (y >= M) y = M-1;

            //output result
            myWriter.write((int)x + " " + (int)y + "\n");
        }

        //close scanner and filewriter
        scan.close();
        myWriter.close();
    }
}
