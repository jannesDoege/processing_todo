import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textfeld input = new Textfeld();
    PFont inputFont;

    @Override
    public void setup(){
        background(60);
        inputFont = createFont("Arial", 24);
    }



    @Override
    public void draw(){
        background(60);
        input.s_draw(20, 20, inputFont) ;
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

    public class Textfeld{
        public boolean active = true;

        public String text = "";
        public String typed = "";

        public void s_draw(int xPos, int yPos, PFont f) {
            textFont(inputFont);
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
