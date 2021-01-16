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
        input.s_draw(50, 40, inputFont, 200, 40);
        input.currentFrame++;
        if (input.currentFrame >= 60){
            input.currentFrame = 0;
        }
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        //System.out.print(input.text);
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

        public int textMargin = 4;
        public int currentFrame = 0;

        float t_width = 0;
        float r_width;


        public void s_draw(int xPos, int yPos, PFont f, int rect_width, int bg_color) {
            int f_size = f.getSize();
            r_width = rect_width;
            textFont(f);

            fill(bg_color);
            rect(xPos - textMargin, yPos - (int)f_size/1f, r_width, f_size + 10, 5);
            fill(fillColor);

            t_width = textWidth(text);
            text(text, xPos, yPos);

            if (active){
                if (currentFrame - 20 > 0){
                    line(xPos + t_width + textMargin, yPos, xPos + t_width + textMargin, yPos - f_size + f_size/3);
                }
            }
        }

        private boolean validKeyPress(char k){
            return Character.toString(k).matches("[A-z?, ]") && t_width < r_width - textWidth(k) - textMargin;
        }

        public void s_type(){
            if (active){
                if (key == '\n'){
                    this.typed = this.text;
                    System.out.println(this.typed);
                }else if(key == BACKSPACE){
                    this.text = remove_last_char(this.text);
                }else if (validKeyPress(key)){
                    this.text = this.text + key;
                }
            }
        }
    }
}
