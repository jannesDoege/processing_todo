
import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {
    public static void main(String[] args){

        PApplet.main("Main", args);
    }

    Textinput Input = new Textinput(){
        @Override
        public void onEnter() {
            System.out.println(this.typed);
        }
    };


    TaskHolder TaskContainer = new TaskHolder();

    PFont inputFont;
    PFont displayFont;

    int fillColor = 200;
    int backgroundColor = 40;

    @Override
    public void setup(){
        background(60);
        inputFont = createFont("Arial", 24);
        displayFont = createFont("Arial", 15);
        fill(fillColor);
    }

    @Override
    public void draw(){
        background(60);
        Input.s_draw(50, 40, inputFont, 300, backgroundColor, "Name");
        TaskContainer.s_draw(50, 80, displayFont, width -50 * 2, height -80 * 2, backgroundColor);
    }

    @Override
    public void settings() {
        size(500, 500);
    }

    @Override
    public void keyPressed() {
        Input.s_type();
    }

    @Override
    public void mousePressed(){
        Input.click_check(mouseX, mouseY);
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

        public void s_draw(int x, int y, PFont f, int rect_width, int bg_color, String bg_text) {
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
            }else if(text.length() < 1){
                fill(bg_color + bg_color/2);
                text(bg_text, x, y);
                fill(fillColor);
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

        public void onEnter(){}

        public void click_check(int mx, int my){
            active = mx > this.xPosition && mx < this.xPosition + this.r_width && this.yPosition > my && this.yPosition < my + this.f_size + f_size / 3;
        }
    }

    public class Task{
        public String name;
        public boolean done = false;
    }

    public class TaskHolder{
        public int taskAmount = 0;
        public Task[] tasks = new Task[taskAmount];

        public int textMargin = 4;

        int yPosition;
        int xPosition;
        int r_width;
        int r_height;

        int textHeight;

        void s_draw(int x, int y, PFont f, int rect_width, int rect_height, int bg_color){
            xPosition = x;
            yPosition = y;
            r_width = rect_width;
            r_height = rect_height;

            textHeight = f.getSize();

            noFill();
            rect(xPosition, yPosition, r_width, r_height, 5);
            fill(fillColor);

            int i = 1;
            for(Task task : tasks){
                this.t_draw(i, displayFont, textMargin, task, 5, 3,bg_color);
                i++;
            }
        }

        void t_draw(int taskNumber, PFont font, int textMargin, Task task, int marginToParent, int yMargin, int bg_color){
            fill(bg_color);
            rect(this.xPosition + marginToParent, taskNumber * this.yPosition + marginToParent, r_width - marginToParent *2 ,
                    textHeight + textMargin * 2);
        }
    }
}
