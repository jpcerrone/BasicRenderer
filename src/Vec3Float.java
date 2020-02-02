public class Vec3Float {
    //Class that describes 3D Vectors and their operations
    public float x;
    public float y;
    public float z;

    public Vec3Float(float x,float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3Float(Vec3Float v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vec3Float() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3Float mult(FloatMatrix m){
        Vec3Float temp = new Vec3Float();
        temp.x = x*m.elements[0][0] + y*m.elements[1][0] + z*m.elements[2][0];
        temp.y = x*m.elements[0][1] + y*m.elements[1][1] + z*m.elements[2][1];
        temp.z = x*m.elements[0][2] + y*m.elements[1][2] + z*m.elements[2][2];
        return temp;
    }

    public Vec3Float crossProduct(Vec3Float v){
        Vec3Float temp = new Vec3Float();
        temp.x = y*v.z - z*v.y;
        temp.y =z*v.x - x*v.z;
        temp.z = x*v.y - y*v.x;
        return temp;
    }

    public float dotProduct(Vec3Float v){
        return x*v.x + y*v.y +z*v.z;
    }

    private float magnitude(){
        return (float) Math.sqrt(Math.pow(x,2)+ Math.pow(y,2) + Math.pow(z,2));
    }

    public void normalize(){
        float magnitudN = magnitude();
        x = x/magnitudN;
        y = y/magnitudN;
        z = z/magnitudN;

    }
    public String toString() {
        return x + "," + y + "," + z;
    }
}
