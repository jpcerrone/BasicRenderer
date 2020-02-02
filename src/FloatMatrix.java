public class FloatMatrix {
    //Class that describes 3x3 Float Matrices and their operations.
    public float[][] elements;

    public FloatMatrix(float[][] elements) {
        this.elements = elements;
    }

    public FloatMatrix(Vec3Float column1, Vec3Float column2, Vec3Float column3) {
        elements = new float[][]
                         {{column1.x,column2.x,column3.x},
                        {column1.y,column2.y,column3.y},
                        {column1.z,column2.z,column3.z}};
    }

    public FloatMatrix mult(FloatMatrix B){
        float[][] temp = new float[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                temp[i][j] = elements[i][0]*B.elements[0][j] + elements[i][1]*B.elements[1][j] + elements[i][2]*B.elements[2][j] ;
            }

        }
        return new FloatMatrix(temp);

    }

    public static FloatMatrix getRotationX(double angle){
        angle = Math.toRadians(angle);
        float cos = (float) Math.cos(angle);
        float sen = (float) Math.sin(angle);
        float[][] rotationX =
                        {{1,0,0},
                        {0,cos,-sen},
                        {0,sen,cos}};
        return new FloatMatrix(rotationX);
    }

    public static FloatMatrix getRotationY(double angle){
        angle = Math.toRadians(angle);
        float cos = (float) Math.cos(angle);
        float sen = (float) Math.sin(angle);
        float[][] rotationY =
                        {{cos,0,sen},
                        {0,1,0},
                        {-sen,0,cos}};
        return new FloatMatrix(rotationY);
    }

    public static FloatMatrix getRotationZ(double angle){
        angle = Math.toRadians(angle);
        float cos = (float) Math.cos(angle);
        float sen = (float) Math.sin(angle);
        float[][] rotationZ =
                        {{cos,-sen,0},
                        {sen,cos,0},
                        {0,0,1}};
        return new FloatMatrix(rotationZ);
    }

}
