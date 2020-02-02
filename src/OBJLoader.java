import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
    //Basic .obj loader, loads vertices and faces present in a file
    //Returns max vertex size (For use in normalization)
    public static float loadOBJ(String path, List<Vec3Float> vertices, List<Face> faces) throws IOException {
        FileReader obj = new FileReader(path);
        float max = 0;
        try (BufferedReader br = new BufferedReader(obj)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")){
                    //Vertices
                    float[] aL = new float[3];
                    int i = 2;
                    int currentVertex  = 0;
                    StringBuilder sB = new StringBuilder();
                    while(i < line.length()){
                        if(line.charAt(i) == ' '){
                            aL[currentVertex] = Float.parseFloat(sB.toString());
                            if(Float.parseFloat(sB.toString()) > max){
                                max = Float.parseFloat(sB.toString());
                            }
                            currentVertex++;
                            sB.delete(0,sB.length());
                        }
                        else
                            sB.append(line.charAt(i));
                        i++;
                    }
                    aL[currentVertex] = Float.parseFloat(sB.toString());
                    vertices.add(new Vec3Float(aL[0],aL[1],aL[2]));
                }

                if (line.startsWith("f ")){
                    //Faces
                    ArrayList<Integer> aLV = new ArrayList<Integer>(3);
                    int i = 2;
                    StringBuilder sB = new StringBuilder();
                    while(i < line.length()){
                        if(line.charAt(i) == ' '){
                            aLV.add(Integer.parseInt(sB.toString()));
                            sB.delete(0,sB.length());
                        }
                        else
                                sB.append(line.charAt(i));
                        i++;
                        }

                    aLV.add(Integer.parseInt(sB.toString()));
                    faces.add(new Face(aLV));
                }
            }
            return max;
        }
    }
}
