import java.text.*;
import java.util.*;
import java.io.*;
public class rotate {
    public static void main(String[] args) throws IOException {
        //setting up input file and scanner
        File input = new File("rotate.in");
        Scanner scan = new Scanner(input);

        //scanning in given constants
        double H = scan.nextDouble() * Math.PI/180;
        double V = scan.nextDouble() * Math.PI/180;
        double L = scan.nextDouble();
        double X = scan.nextDouble();
        double Y = scan.nextDouble();

        //solving for A,W,R,P using trigonometry
        double A = L / (2 * Math.tan(V/2)); //we know that tan(H/2) = L/(2A) and solve for A
        double W = 2 * A * Math.tan(H/2); //we know that tan(W/2) = W/2A and solve for W
        double R = Math.atan(-Y/A) * 180/Math.PI; //we know that tan(R) = -Y/A (because positive R corresponds to negative Y) and solve for R
        double P = Math.atan(X/A) * 180/Math.PI; //we know that tan(P) = X/A and solve for P

        //output result, rounding each value to the nearest tenth
        FileWriter myWriter = new FileWriter("rotate.out");
        DecimalFormat df = new DecimalFormat("######.#");
        myWriter.write(df.format(A) + " " + df.format(W) + " " + df.format(R) + " " + df.format(P));

        //close scanner and filewriter
        scan.close();
        myWriter.close();
    }
}
