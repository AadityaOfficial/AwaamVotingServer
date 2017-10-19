from flask import Flask, request, jsonify, send_file, g
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, load_only
from models import Base,User,Election
from werkzeug.exceptions import BadRequest
from urllib import urlretrieve
from passlib.apps import custom_app_context as pwd_context
from flask_httpauth import HTTPBasicAuth
import json

#new imports
import httplib2
from flask import make_response
import requests

auth=HTTPBasicAuth()
engine = create_engine('sqlite:///app.db')
Base.metadata.bind = engine


DBSession = sessionmaker(bind=engine)
session = DBSession()
app = Flask(__name__) 
# Create the appropriate app.route functions, 
#test and see if they work

#Make an app.route() decorator here
@app.route("/")
def testPrint():
  return "Yes its working"
@app.route('/token')
@auth.login_required
def get_auth_token():
    token = g.user.generate_auth_token()
    return jsonify({'token': token.decode('ascii')})

@app.route("/register", methods = ['POST'])
def newUser():
  data=request.get_json(force=True)
  username=data['username']
  password=data['password']
  if username is None or password is None:
    abort(400) #missing arguments
  if session.query(User).filter_by(username=username).first() is not None:
    abort(400)
  user=User(username=username)
  user.hash_password(password)
  session.add(user)
  session.commit()
  return jsonify({'username':user.username}),201

@app.route("/login", methods = ['POST'])
@auth.login_required
def oldUser():
  result=False
  data=request.get_json(force=True)
  username=data['username']
  password=data['password']
  if username is None or password is None:
    abort(400) #missing arguments
  if session.query(User).filter_by(username=username).first() is not None:
    result=verify_password(username, password)
  
  return jsonify({'username':username,'valid':result}),201

@app.route("/userdata/<string:username>", methods = ['GET','PUT'])
@auth.login_required
def userData(username):
    data=request.get_json(force=True)
    if request.method == 'GET':
      return getUserData()
    elif request.method == 'PUT':
      positive_vote=data['positive_vote']
      print positive_vote
      negative_vote=data['negative_vote']
      return updateVotes(username,positive_vote,negative_vote)

@app.route("/alluserdata/<string:username>", methods = ['GET','PUT'])
@auth.login_required
def changeUserData(username):
  if request.method=='GET':
    return getUserProfile(username)
  elif request.method=='PUT':
    data=request.get_json(force=True)
    newUsername=data['newUsername']
    gender=data['gender']
    mobile_number=data['mobile_number']
    return changeData(username,gender,mobile_number,newUsername)


@auth.verify_password
def verify_password(username_or_token, password):
    #Try to see if it's a token first
    user_id = User.verify_auth_token(username_or_token)
    if user_id:
        user = session.query(User).filter_by(id = user_id).one()
    else:
        user = session.query(User).filter_by(username = username_or_token).first()
        if not user or not user.verify_password(password):
            return False
    g.user = user
    return True


@app.route("/election/<string:category>",methods=['GET'])
@auth.login_required
def categoryFunction(category):
  return getElectionByCategory(category)

@app.route("/elections", methods = ['GET', 'POST'])
@auth.login_required
def electionFunction():
  if request.method == 'GET':
    return getAllElections()
  elif request.method == 'POST':
    #Call the method to make a new election
    print "Making a New election"
    data=request.get_json(force=True)
    name=data['name']
    heading=data['heading']
    para=data['para']
    category=data['category']
    start_time=data['start_time']
    end_time=data['end_time']
    in_favour=data['in_favour']
    not_in_favour=data['not_in_favour']
    status=data['status']
    total_votes=data['total_votes']
    return makeANewElection(name,heading,para,category,start_time,end_time,in_favour,not_in_favour,status,total_votes)
 
#Make another app.route() decorator here that takes in an integer id in the URI
@app.route("/elections/<int:id>", methods = ['GET', 'PUT', 'DELETE'])
#Call the method to view a specific election
def electionFunctionId(id):
  if request.method == 'GET':
    return getElection(id)
    
#Call the method to edit a specific election  
  elif request.method == 'PUT':
    data=request.get_json(force=True)
    name=data['name']
    heading=data['heading']
    para=data['para']
    category=data['category']
    start_time=data['start_time']
    end_time=data['end_time']
    status=data['status']
    return updateElection(id,name, heading,para,category,start_time,end_time,status)
    
 #Call the method to remove a election 
  elif request.method == 'DELETE':
    return deleteElection(id)

#functions starting******************************************************************
#functions starting *****************************************************************
def getAllElections():
  elections = session.query(Election).all()
  return jsonify(Elections=[i.serialize for i in elections])

def getUserData(username):
  user=session.query(User).filter_by(username=username).one()
  return jsonify({'total_votes':user.total_votes,'positive_votes':user.votes_in_favour,'negative_vote':user.votes_against})

def getElection(id):
  election = session.query(Election).filter_by(election_id = id).one()
  return jsonify(Election=election.serialize) 
  
def makeANewElection(name, heading,para,category,start_time,end_time,in_favour,not_in_favour,status,total_votes):
  election = Election(name = name, heading = heading, para=para, category=category, start_time=start_time, end_time=end_time, in_favour=in_favour, not_in_favour=not_in_favour, status=status, total_votes=total_votes)
  session.add(election)
  session.commit()
  return jsonify(Election=election.serialize)

def updateElection(id,name, heading,para,category,start_time,end_time,status):
  election = session.query(Election).filter_by(election_id = id).one()
  if not name:
    election.name = name
  if not heading:
    election.heading = heading
  if not para:
    election.para = para
  if not category:
    election.category=category
  if not start_time:
    election.start_time=start_time
  if not end_time:
    election.end_time=end_time
  if not status:
    election.status=status
  session.add(election)
  session.commit()
  return "Updated an election with id %s" % id

def updateVotes(username,positive_vote,negative_vote):
  user=session.query(User).filter_by(username=username).one()
  if positive_vote =="True":
    if user.votes_in_favour is None:
      user.votes_in_favour=0
    user.votes_in_favour=user.votes_in_favour+1
  if not negative_vote and negative_vote == "True":
    if user.votes_against is None:
      user.votes_against=0
    user.votes_against=user.votes_against+1
  if user.total_votes is None:
    user.total_votes=0
  else:
    user.total_votes=user.total_votes+1
  session.add(user)
  session.commit()
  return jsonify({'total_votes':user.total_votes,'positive_votes':user.votes_in_favour,'negative_vote':user.votes_against})

def deleteElection(id):
  election = session.query(Election).filter_by(election_id = id).one()
  session.delete(election)
  session.commit()
  return "Removed election with id %s" % id

def getElectionByCategory(category):
  elections=session.query(Election).filter_by(category=category).all()
  return jsonify(Elections=[k.serialize for k in elections])

def getUserProfile(username):
    user=session.query(User).filter_by(username=username).one()
    return jsonify({'username':user.username,'gender':user.gender,'mobile_number':user.mobile_number,'total_votes':user.total_votes,'positive_votes':user.votes_in_favour,'negative_votes':user.votes_against,'role':user.role})


def changeData(username,gender,mobile_number,newUsername):
  user=session.query(User).filter_by(username=username).one()
  if newUsername is not None:
    if session.query(User).filter_by(username=newUsername).first() is not None:
      raise BadRequest()
    else:
      user.username=newUsername
  if gender is not None:
    user.gender=gender
  if mobile_number is not None:
    user.mobile_number=mobile_number
  session.add(user)
  session.commit()
  return jsonify({'username':user.username,'gender':user.gender,'mobile_number':user.mobile_number,'total_votes':user.total_votes,'positive_votes':user.votes_in_favour,'negative_votes':user.votes_against,'role':user.role})





if __name__ == '__main__':
    app.run()  