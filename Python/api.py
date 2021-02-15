from flask import Flask
from flask_restful import Api, Resource, reqparse, abort, fields, marshal_with, inputs
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
api = Api(app)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
db = SQLAlchemy(app)

class TaskModel(db.Model): # baseclass for all tasks
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    done = db.Column(db.Boolean, default = False, server_default="false", nullable=False)

    def __repr__(self):
        return f"Task(name = {self.name}, done = {self.done})"

#db.create_all() # if you are running this script for the first time

task_put_args = reqparse.RequestParser()
task_put_args.add_argument("name", type=str, help="Name of task", required = True)
task_put_args.add_argument("done", type=inputs.boolean, help="Is the task done", required = False)

task_update_args = reqparse.RequestParser()
task_update_args.add_argument("name", type=str, help="Name of task", required=False)
task_update_args.add_argument("done", type=inputs.boolean, help="Is the task done", required=False)

resource_fields = {
    "id": fields.Integer,
    "name": fields.String,
    "done": fields.Boolean
}


class Task(Resource): # task resource
    # return the properties of a specified entry
    @marshal_with(resource_fields) # serialize object
    def get(self, task_id):

        result = TaskModel.query.filter_by(id=task_id).first() # get the task with the correct id
        if not result: # abort if the id is invalid
            abort(404, message="There is no task associated with that id - try a different one")
        return result
    
    # create a new entry
    @marshal_with(resource_fields)
    def post(self, task_id):
        args = task_put_args.parse_args() # get acces to the arguments
        result = TaskModel.query.filter_by(id=TaskModel.query.count()).first()
        if result:
            abort(409, message="Task id already in use...")

        task = TaskModel(id=task_id, name=args["name"], done=args["done"]) 
        db.session.add(task) # create database entry
        db.session.commit()
        return task, 201
    
    # update an existing entry
    @marshal_with(resource_fields)
    def patch(self, task_id):
        args = task_update_args.parse_args()
        result = TaskModel.query.filter_by(id=task_id).first()
        
        if not result:
            abort(404, message="unused id, cannot update")
        
        for arg in args: # the user doesn't has to specifie every argument, if they only want to change one value
            if args.get(arg) is not None:
                setattr(result, arg, args.get(arg))

        db.session.commit()

        return result

    # delete an existing entry
    @marshal_with(resource_fields)
    def delete(self, task_id):
        result = TaskModel.query.filter_by(id=task_id).first()
        if not result:
            abort(404, message="there is no task with that id")
        
        
        db.session.delete(result)
        db.session.commit()
        updateIDs()


        return "", 204

class TaskAmount(Resource): # info about the total amount of tasks in the database
    def get(self):
        rows = TaskModel.query.count()
        return rows

# update all of the ids to ensure that they stay in order
def updateIDs():
    for i in range(TaskModel.query.count()):
        task = TaskModel.query.get(i+1)
        if task is not None:
            print(task)
            new_task = TaskModel(id=i, name=task.name, done=task.done)
            db.session.add(new_task)
            db.session.delete(task)
            db.session.commit()
        
        
# add resources to the api
api.add_resource(Task, "/task/<int:task_id>") 
api.add_resource(TaskAmount, "/amount")

if __name__ == "__main__": 
    app.run(debug=True)