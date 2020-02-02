import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.*;


public class RenderWindow extends JFrame implements KeyListener {

    //Objects rotation and distance attributes
    private static final float rotSpeed = 2f;
    private static final float zoomSpeed = 0.1f;
    private static final float defaultZDist = 2.5f;
    private static final float defaultYRRot = 180;
    private float rotX;
    private float rotY;
    private float rotZ;
    private float zDist;

    //Structures that contain the original vertices and their transformed counterparts
    private java.util.List<Vec3Float> vertices;
    private java.util.List<Vec3Float> transformedVertices;

    //Object´s faces
    private java.util.List<Face> faces;

    //Lighting related attributes
    private static final Vec3Float lightDirection = new Vec3Float(0,0,1);;
    private static final float ambientLightIntensity = 0.1f;

    //Contains .obj files locations
    private List<String> paths;
    private int currentObj = 0;

    //Object´s colors
    private Color[] colors;

    //Normalized space
    //Screen´s center at 0,0
    private static final float NOR_MAX = 1.0f;

    //Screen Space
    //Top Left Corner is 0,0
    private static final int SCR_WIDTH = 800;
    private static final int SCR_HEIGHT = 800;

    //Control's image
    private final BufferedImage image = ImageIO.read(new File("img\\controls.jpg"));

    public RenderWindow() throws HeadlessException, IOException {

        initializeFrame();

        //Loading colors
        colors = new Color[4];
        colors[0] = Color.ORANGE;
        colors[1] = Color.YELLOW;
        colors[2] = Color.RED;
        colors[3] = Color.PINK;

        //Loading paths
        this.paths = new ArrayList<>();
        File directorio = new File("objs");
        for(String s : directorio.list()){
            paths.add(directorio.getAbsolutePath() + "\\"+ s);
        }

        //Loads the first object
        loadObj();

        //Render loop, redraws the screen at 60 fps
        Timer timer = new Timer(33, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        //keyboard controls
        switch (e.getKeyCode()) {
            case VK_W: rotX += rotSpeed; break;
            case VK_S: rotX -= rotSpeed; break;
            case VK_A: rotY += rotSpeed; break;
            case VK_D: rotY -= rotSpeed; break;
            case VK_Q: rotZ -= rotSpeed; break;
            case VK_E: rotZ += rotSpeed; break;
            case VK_CONTROL: zDist += zoomSpeed; break;
            case VK_SHIFT: zDist -= zoomSpeed; break;
            case VK_TAB: loadObj(); break;
        }
    }

    private void sortFaces(){
        //Sorts objects faces (Painter´s algorithm)
        for(Face c : faces){
            c.avgZ = (transformedVertices.get(c.vertices.get(0)-1).z + transformedVertices.get(c.vertices.get(1)-1).z + transformedVertices.get(c.vertices.get(2)-1).z)/3;
        }
        faces.sort(Face::compareTo);
    }

    private class Panel extends JPanel{
        //Panel over which we'll draw
        public void paintComponent(Graphics g) {
            permormCalculations();
            g.clearRect(0,0,SCR_WIDTH,SCR_HEIGHT);

            for(Face c : faces){
                //Get´s all 3 of the face´s vertices
                Vec3Float v0 = transformedVertices.get(c.vertices.get(0) - 1);
                Vec3Float v1 = transformedVertices.get(c.vertices.get(1) - 1);
                Vec3Float v2 = transformedVertices.get(c.vertices.get(2) - 1);

                //Calculate the face´s normal vector
                Vec3Float v0v1 = new Vec3Float(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z);
                Vec3Float v0v2 = new Vec3Float(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z);
                Vec3Float n = v0v1.crossProduct(v0v2);

                //If the face is visible then it is drawn (Back Face Culling)
                if(n.dotProduct(v0) < 0f){
                    //Gets the face´s screen position
                    float[] v0P = getScreenPosition(v0);
                    float[] v1P = getScreenPosition(v1);
                    float[] v2P = getScreenPosition(v2);

                    n.normalize();

                    //Calculate the face´s color depending on its angle with the light source
                    float intensity = -n.dotProduct(lightDirection);
                    Color currentColor = colors[currentObj%paths.size()];
                    float red = Math.max(currentColor.getRed()*ambientLightIntensity,Math.min(currentColor.getRed()*intensity,currentColor.getRed()));
                    float green = Math.max(currentColor.getGreen()*ambientLightIntensity,Math.min(currentColor.getGreen()*intensity,currentColor.getGreen()));
                    float blue = Math.max(currentColor.getBlue()*ambientLightIntensity,Math.min(currentColor.getBlue()*intensity,currentColor.getBlue()));

                    //Draw the face
                    g.setColor(new Color((int)red,(int)green,(int)blue));
                    int[] xs = {(int) v0P[0], (int) v1P[0], (int) v2P[0]};
                    int[] ys = {(int) v0P[1], (int) v1P[1], (int) v2P[1]};
                    g.fillPolygon(xs,ys,3);

                    //Draw the controls
                    g.drawImage(image, 800, 0, null);
                }
            }
        }
    }

    private void permormCalculations(){
        //Calculate rotation matrix
        FloatMatrix rotation = FloatMatrix.getRotationX(rotX).mult(FloatMatrix.getRotationY(rotY)).mult(FloatMatrix.getRotationZ(rotZ));
        //Vertices get rotated and Z-corrected
        for(int i = 0; i <vertices.size();i++){
            Vec3Float mult = vertices.get(i).mult(rotation);
            transformedVertices.get(i).x = mult.x;
            transformedVertices.get(i).y = mult.y;
            transformedVertices.get(i).z = mult.z;
            transformedVertices.get(i).z += zDist;
        }
        sortFaces();
    }

    private void loadObj(){
        //Loads next object and restores the default rotation and position values
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
        rotX = rotY = rotZ = 0;
        rotY = defaultYRRot;
        zDist = defaultZDist;
        float max = 0;
        try {
            max = OBJLoader.loadOBJ(paths.get(currentObj%paths.size()),vertices,faces);
        } catch (IOException e) {
            e.printStackTrace();
        }
        transformedVertices = new ArrayList<Vec3Float>(vertices.size());
        //Correction to aproximate objects sizes so they can be more similar. Also adds a copy of each vertex to the transfromedVertices list
        for(Vec3Float v : vertices){
            v.x = v.x/max;
            v.y = v.y/max;
            v.z = v.z/max;
            transformedVertices.add(new Vec3Float(v));
        }

        currentObj++;
    }

    private float[] getScreenPosition(Vec3Float vertice){
        //Gets a position in normalized space (NOR) and returns that position in screen space (SCR)
        float[] temp = new float[2];
        float factorX = SCR_WIDTH/(NOR_MAX*2);
        float factorY = SCR_HEIGHT/(NOR_MAX*2);
        temp[0] = ((vertice.x/(vertice.z) + 1.0f)*factorX);
        temp[1] = ((-vertice.y/(vertice.z) + 1.0f)*factorY);
        return temp;
    }

    private void initializeFrame(){
        setTitle("Renderer");
        setSize(SCR_WIDTH+256, SCR_HEIGHT);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panel = new Panel();
        this.add(panel);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
    }
}
