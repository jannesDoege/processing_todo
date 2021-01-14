import processing.core.PApplet;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textfeld text = new Textfeld();

    @Override
    public void setup(){
        background(60);
    }

    @Override
    public void draw(){
        background(60);
        text.s_draw(20, 20);
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        System.out.print(key);
        text.s_type();
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

        public void s_draw(int xPos, int yPos) {
            text(text, xPos, yPos);
        }

        public void s_type(){
            if (active){
                if (key == '\n'){
                    this.typed = this.text;
                    this.text = this.text + key;
                }else if(key == BACKSPACE){
                    this.text = remove_last_char(this.text);
                }else if (true){ //TODO filter out Shift, Space, ...
                    this.text = this.text + key;
                }
            }
        }
    }
}
