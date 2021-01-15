import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textinput input = new Textinput();
    PFont inputFont;

    int fillColor = 200;

    @Override
    public void setup(){
        background(60);
        inputFont = createFont("Arial", 24);
        fill(fillColor);
    }

    @Override
    public void draw(){
        background(60);
        input.s_draw(50, 40, inputFont, 40) ;
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        System.out.print(key);
        input.s_type();
    }

    public static String remove_last_char(String str){
        if (str.length() > 0){
        return str.substring(0, str.length() -1);
        }
        return "";
    }

    public class Textinput{
        public boolean active = true;

        public String text = "";
        public String typed = "";

        public void s_draw(int xPos, int yPos, PFont f, int rect_width) {
            int f_size = f.getSize();
            noFill();
            rect(xPos -4, yPos - (int)f_size/1f, rect_width, f_size + 10, 5);
            fill(fillColor);
            textFont(f);
            text(text, xPos, yPos);
        }

        private boolean validKeyPress(char k){
            return Character.toString(k).matches("[A-z?]");
        }

        public void s_type(){
            if (active){
                if (key == '\n'){
                    this.typed = this.text;
                    this.text = this.text + key;
                }else if(key == BACKSPACE){
                    this.text = remove_last_char(this.text);
                }else if (validKeyPress(key) || keyCode == 32){
                    this.text = this.text + key;
                }
            }
        }
    }
}
