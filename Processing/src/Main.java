import processing.core.PApplet;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textfeldd text = new Textfeldd();

    @Override
    public void setup(){

    }

    @Override
    public void draw(){
        text.s_draw(1, 1);
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        if (key == '\n'){
            text.typed = text.text;
        }else {
            text.text = text.text + key;
        }
    }

    class Textfeldd{
        public  String text = "";
        public String typed = "";

        public void s_draw(int xPos, int yPos){
            text(text, xPos, yPos);
        }
    }
}
