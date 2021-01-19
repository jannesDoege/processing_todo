
import processing.core.PApplet;
import processing.core.PFont;

import java.util.function.Function;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textinput input = new Textinput(){
        @Override
        public void onEnter() {
            System.out.println(this.typed);
        }
    };

    Textinput input2 = new Textinput(){
        @Override
        public void onEnter() {
            System.out.println("zwei");
        }
    };

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
        input.s_draw(50, 40, inputFont, 300, 40);
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        input.s_type();
        input2.s_type();
    }

    @Override
    public void mousePressed(){
        input.click_check(mouseX, mouseY);
    }

    public static String remove_last_char(String str){
        if (str.length() > 0){
        return str.substring(0, str.length() -1);
        }
        return "";
    }

    public class Textinput{
        public boolean active = false;

        public String text = "";
        public String typed = "";

        public int textMargin = 4;
        public int currentFrame = 0;

        int yPosition;
        int xPosition;
        int f_size;

        float t_width = 0;
        float r_width;


        private boolean validKeyPress(char k){
            return Character.toString(k).matches("[A-z?, ]") && t_width < r_width - textWidth(k) - textMargin;
        }

        public void s_draw(int x, int y, PFont f, int rect_width, int bg_color) {
            this.currentFrame++;
            if (this.currentFrame >= 60){
                this.currentFrame = 0;
            }

            f_size = f.getSize();
            r_width = rect_width;
            textFont(f);

            this.yPosition = y;
            this.xPosition = x;


            fill(bg_color);
            rect(x - textMargin, y - (int)f_size/1f, r_width, f_size + f_size/3, 5);
            fill(fillColor);

            t_width = textWidth(text);
            text(text, x, y);

            if (active){
                if (currentFrame - 20 > 0){
                    line(x + t_width + textMargin, y, x + t_width + textMargin, y - f_size + f_size/3);
                }
            }
        }

        public void s_type(){
            if (active) if (key == '\n') {
                this.typed = this.text;
                onEnter();
            } else if (key == BACKSPACE) {
                this.text = remove_last_char(this.text);
            } else if (validKeyPress(key)) {
                this.text = this.text + key;
            }
        }

        public void onEnter(){

        }

        public void click_check(int mx, int my){
            active = mx > this.xPosition && mx < this.xPosition + this.r_width && this.yPosition > my && this.yPosition < my + this.f_size + f_size / 3;
        }
    }
}
