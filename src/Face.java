import java.util.ArrayList;


public class Face implements Comparable<Face>{
    public ArrayList<Integer > vertices;
    public float avgZ;
    Face(ArrayList<Integer > vertices){
        this.vertices = vertices;
    }


    @Override
    public String toString() {
        return vertices.toString();
    }

    @Override
    public int compareTo(Face o) {
        if (avgZ > o.avgZ)
            return -1;
        else if (avgZ < o.avgZ)
            return 1;
        else
            return 0;
    }
}