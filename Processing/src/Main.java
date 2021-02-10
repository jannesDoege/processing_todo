import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import processing.core.PApplet;
import processing.core.PFont;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends PApplet {

    public static void main(String[] args)  {
        PApplet.main("Main", args);
    }

    HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://127.0.0.1:5000/";

    public static String remove_last_char(String str){
        if (str.length() > 0){
            return str.substring(0, str.length() -1);
        }
        return "";
    }

    public HttpResponse<String> taskInfoRequest(int taskID) throws IOException, InterruptedException {
        HttpRequest taskInfoRequest = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(BASE_URL + "/task/" + taskID))
                .build();
        HttpResponse<String> response = client.send(taskInfoRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse<String> taskPostRequest(String taskName) throws IOException, InterruptedException {
        int id = amount_request();
        Map mapData = new HashMap<>() {

            {
                put("name", taskName);
            }
        };

        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(mapData);


        HttpRequest taskPutRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(URI.create(BASE_URL + "task/" + id))
                .build();
        HttpResponse<String> response = client.send(taskPutRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse<String> changeTaskDone(Task task) throws IOException, InterruptedException {
        task.done = !task.done;

        Map mapData = new HashMap<>(){{
            put("done", task.done);
        }};

        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(mapData);

        HttpRequest markTaskAsDoneRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "task/" + task.id))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(data))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(markTaskAsDoneRequest, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public HttpResponse<String> deleteTask(int taskID) throws IOException, InterruptedException {
        TaskContainer.tasks.remove(taskID);
        TaskContainer.delButtons.remove(taskID);

        for (Task task : TaskContainer.tasks){
            task.id = TaskContainer.tasks.indexOf(task);
        }

        for (DeleteButton e : TaskContainer.delButtons){
            e.parentTaskID = TaskContainer.delButtons.indexOf(e);
        }

        HttpRequest deleteTask = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(BASE_URL + "task/" + taskID))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(deleteTask, HttpResponse.BodyHandlers.ofString());
        return response;
    }


    public int amount_request() throws IOException, InterruptedException {
        HttpRequest amountRequest = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(BASE_URL + "/amount"))
                .build();
        HttpResponse<String> amountResponse = client.send(amountRequest, HttpResponse.BodyHandlers.ofString());
        return Integer.valueOf(remove_last_char(amountResponse.body()));
    }

    Textinput Input = new Textinput(){
        @Override
        public void onEnter() throws IOException, InterruptedException {
            taskPostRequest(this.typed);
            this.typed = "";
            this.text = "";
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
        frameRate(60);
        surface.setResizable(false);
        surface.setTitle("Jannes Döge - Programmierprojekt");
    }

    @Override
    public void draw(){
        background(60);
        Input.s_draw((int) Math.round(width/10), (int) Math.round(height/12.5), inputFont, (int) Math.round(width*0.6), backgroundColor, "Name");
        try {
            TaskContainer.s_draw(50, 80, displayFont, width -50 * 2, height -80 * 2, backgroundColor);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            amount_request();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void settings() {
        size(500, 500);

    }

    @Override
    public void keyPressed() {
        try {
            Input.s_type();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mousePressed(){
        Input.click_check(mouseX, mouseY);
        for (int i = 0; i < TaskContainer.delButtons.size(); i++){
            TaskContainer.delButtons.get(i).onClick();
        }

        for (DoneButton e : TaskContainer.doneButtons){
            try {
                e.onClick();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
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
            if (this.currentFrame >= frameRate){
                this.currentFrame = 0;
            }

            f_size = f.getSize();
            r_width = rect_width;
            textFont(f);

            this.yPosition = y;
            this.xPosition = x;


            fill(bg_color);
            rect(x - textMargin, y - f_size, r_width, f_size + f_size/3, 5);
            fill(fillColor);

            t_width = textWidth(text);
            text(text, x, y);



            if (active){
                if (currentFrame - (int)frameRate/3 > 0){
                    line(x + t_width + (int) textMargin/2, y, x + t_width + (int) textMargin/2, y - f_size + (int) f_size/3);
                }
            }else if(text.length() < 1){
                fill(bg_color + bg_color/2);
                text(bg_text, x, y);
                fill(fillColor);
            }
        }

        public void s_type() throws IOException, InterruptedException {
            if (active) if (key == '\n') {
                this.typed = this.text;
                onEnter();
            } else if (key == BACKSPACE) {
                this.text = remove_last_char(this.text);
            } else if (validKeyPress(key)) {
                this.text = this.text + key;
            }
        }

        public void onEnter() throws IOException, InterruptedException {}

        public void click_check(int mx, int my){
            active = mx > this.xPosition && mx < this.xPosition + this.r_width && this.yPosition > my &&
                    this.yPosition < my + this.f_size + f_size / 3;
        }
    }

    class Task{
        public int id;
        public String name;
        public boolean done = false;
    }

    public class TaskHolder{
        List<Task> tasks = new ArrayList<Task>();
        List<DeleteButton> delButtons = new ArrayList<DeleteButton>();
        List<DoneButton> doneButtons = new ArrayList<DoneButton>();

        public int textMargin = 4;

        int yPosition;
        int xPosition;
        int r_width;
        int r_height;

        int textHeight;

        int taskX;
        int taskY;

        public boolean addTask(Task newTask){
            for(Task task : tasks){
               if(task.id == newTask.id){
                   return false;
               }
            }
            tasks.add(newTask);
            delButtons.add(new DeleteButton(newTask.id));
            doneButtons.add(new DoneButton(newTask.id));

            return true;
        }

        void s_draw(int x, int y, PFont f, int rect_width, int rect_height, int bg_color) throws IOException, InterruptedException {
            xPosition = x;
            yPosition = y;
            r_width = rect_width;
            r_height = rect_height;

            textHeight = f.getSize();

            noFill();
            rect(xPosition, yPosition, r_width, r_height, 5);
            fill(fillColor);

            s_updateTasks();

            int i = 0;
            for(Task task : tasks){
                this.t_draw(i, displayFont, textMargin, task, delButtons.get(i), doneButtons.get(i), 5, 3,bg_color);
                i++;
            }
        }

        void t_draw(int taskNumber, PFont font, int textMargin, Task task, DeleteButton deleteButton, DoneButton doneButton,
                    int marginToParent, int yMargin, int bg_color){
            fill(bg_color);
            taskX = this.xPosition + marginToParent;
            taskY = this.yPosition + yMargin + ((textHeight + textMargin) * 2) * taskNumber;

            System.out.println(taskNumber);
            System.out.println(taskY);
            System.out.println(this.r_height);

            boolean drawable = this.taskY - this.yPosition < this.r_height;

            if(drawable){
                deleteButton.s_draw(taskY, taskX + r_width - marginToParent *2 - deleteButton.r_width,
                        textHeight + textMargin * 2,textHeight + textMargin * 2, drawable);

                doneButton.s_draw(taskY, taskX + r_width - marginToParent *2 - deleteButton.r_width - doneButton.r_width,
                        textHeight + textMargin * 2,textHeight + textMargin * 2, drawable);

                rect(taskX, taskY, r_width - marginToParent *2 - deleteButton.r_width - doneButton.r_width, textHeight + textMargin * 2);

                fill(255);
                textFont(font);
                text(task.name, taskX + textMargin, taskY + textHeight + yMargin);
                fill(fillColor);
            }else {
                rect(this.xPosition + (int)this.r_width/2 - (int)this.r_width/6, this.yPosition + this.r_height - (textHeight + textMargin * 2)/2,
                        (int)this.r_width/3, (textHeight + textMargin * 2), 10);

                noStroke();
                fill(255);
                ellipse(this.xPosition + (int)this.r_width/2, this.yPosition + this.r_height,(int)height/50, (int)height/50);
                ellipse(this.xPosition + (int)this.r_width/2 + (int)this.r_width/16, this.yPosition + this.r_height,(int)height/50, (int)height/50);
                ellipse(this.xPosition + (int)this.r_width/2 - (int)this.r_width/16, this.yPosition + this.r_height,(int)height/50, (int)height/50);
                stroke(0);
                fill(fillColor);

            }
            }



        void s_updateTasks() throws IOException, InterruptedException {
            for (int i = 0; i < amount_request(); i++){
                Gson g = new Gson();
                Task new_task = g.fromJson(taskInfoRequest(i).body(), Task.class);

                addTask(new_task);
            }
        }
    }

    abstract class Button{
        int yPosition;
        int xPosition;
        int r_width;
        int r_height;

        public void s_draw(){
            rect(xPosition, yPosition, r_width, r_height);
            if (mouseX > this.xPosition && mouseX < this.xPosition + this.r_width && mouseY > this.yPosition &&
                    mouseY < this.yPosition + this.r_height){
            }
        }

        public void onClick() throws IOException, InterruptedException {}

    }

    class DeleteButton extends Button{
        public int parentTaskID;

        public DeleteButton( int id){
            this.parentTaskID = id;
        }

        public void s_draw(int yp, int xp, int rw, int rh, boolean draw) {
            this.yPosition = yp;
            this.xPosition = xp;
            this.r_width = rw;
            this.r_height = rh;

            if (draw){
                super.s_draw();

                stroke(255, 0, 0);
                line(xPosition + (int) r_width/5, yPosition + (int) r_height/5, xPosition + r_width - (int) r_width/5,
                        yPosition + r_height - (int) r_height/5);

                line(xPosition + (int) r_width/5, yPosition + r_height - (int) r_height/5, xPosition + r_width - (int) r_width/5,
                        yPosition + (int) r_height/5);
                stroke(0);
            }


        }

        @Override
        public void onClick() {
            if (mouseX > this.xPosition && mouseX < this.xPosition + this.r_width && mouseY > this.yPosition &&
            mouseY < this.yPosition + this.r_height){
                try {
                    deleteTask(parentTaskID);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DoneButton extends Button{
        public int parentTaskID;

        public DoneButton(int id){
            this.parentTaskID = id;
        }

        public void s_draw(int yp, int xp, int rw, int rh, boolean draw) {
            this.yPosition = yp;
            this.xPosition = xp;
            this.r_width = rw;
            this.r_height = rh;

            if(draw){
                super.s_draw();

                if(TaskContainer.tasks.get(parentTaskID).done) {
                    stroke(0, 255, 0);
                    line(this.xPosition + (int)this.r_width/4, this.yPosition + (int) this.r_height/2,
                            this.xPosition + (int)this.r_width/3, this.yPosition + this.r_width - (int)this.r_height/5);
                    line(this.xPosition + (int)this.r_width/3, this.yPosition + this.r_width - (int)this.r_height/5,
                            this.xPosition + this.r_width - (int)this.r_width/5, this.yPosition + (int)this.r_height/5);
                    stroke(0);
                }
            }


        }

        public void onClick() throws IOException, InterruptedException {
            if (mouseX > this.xPosition && mouseX < this.xPosition + this.r_width && mouseY > this.yPosition &&
                    mouseY < this.yPosition + this.r_height){
            changeTaskDone(TaskContainer.tasks.get(parentTaskID));

            }
        }
    }
}
