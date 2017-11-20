from flask import Flask, request, jsonify, send_file, g
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, load_only
from models import Base,User,Election, Organisation
from werkzeug.exceptions import BadRequest
from passlib.apps import custom_app_context as pwd_context
from flask_httpauth import HTTPBasicAuth
import json
import time
import datetime
import ciso8601

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

#First endpoint to check if server working
@app.route("/")
def testPrint():
  return "Yes its working"



# Endpoint to get token
@app.route('/token')
@auth.login_required
def get_auth_token():
    token = g.user.generate_auth_token()
    return jsonify({'token': token.decode('ascii')})

@app.route('/getorg',methods=['GET'])
def organisation_basic_value():
  if request.method=='GET':
    organistaions=session.query(Organisation).all()
    return jsonify(Organistaions=[k.serialize for k in organistaions])


@app.route('/registerorg',methods=['POST'])
@auth.login_required
def organisation_basic():
    data=request.get_json(force=True)
    name=data['name']
    desc=data['desc']
    organisation = Organisation(name = name,desc=desc)
    session.add(organisation)
    session.commit()
    return jsonify({'status':"registered",'name':organisation.name,'desc':organisation.desc})

# Endpoint to register a new user
@app.route("/registeruser", methods = ['POST'])
def newUser():
  data=request.get_json(force=True)
  username=data['username']
  password=data['password']
  org=data['organisation']
  role=data['role']
  if role ==2:
      raise BadRequest()
  status=data['status']
  if username is None or password is None:
    raise BadRequest() #missing arguments
  if session.query(User).filter_by(username=username).first() is not None:
    raise BadRequest()
  user=User(username=username)
  user.hash_password(password)
  user.status=status
  user.organisation=organisation
  user.role=role
  session.add(user)
  session.commit()
  return jsonify({'username':user.username,'role':user.role,'org':user.organisation}),201

#Endpoint to enable login for users
@app.route("/loginuser", methods = ['POST'])
def oldUser():
  result=False
  data=request.get_json(force=True)
  username=data['username']
  password=data['password']
  if username is None or password is None:
    raise BadRequest() #missing arguments
  user=session.query(User).filter_by(username=username).first()
  if user.status is 0:
    raise BadRequest()
  if  user is not None:
    result=verify_password(username, password)
  return jsonify({'username':username,'valid':result,'role':user.role}),201

#Enpoint to get election status
@app.route("/electionstatus/<int:election_id>", methods = ['GET'])
@auth.login_required
def electionStatus(election_id):
  election=session.query(Election).filter_by(election_id = election_id).one()
  unixTimeCurrent=time.time()
  ts = ciso8601.parse_datetime(election.end_time)
  endTimeElection=time.mktime(ts.timetuple())
  # valueTime=time.strftime("%Y-%m-%d %H:%M:%S",time.localtime())

  if unixTimeCurrent>endTimeElection:
    election.status=1
    return jsonify({'status':"completed",'positive_vote':election.in_favour,'negative_vote':election.not_in_favour,'total_votes':election.total_votes,'election_status':election.status})
  else:
    election.status=0
    return jsonify({'status':"not completed",'positive_vote':election.in_favour,'negative_vote':election.not_in_favour,'total_votes':election.total_votes,'election_status':election.status})

#Enpoint to GET the user vote details and PUT user vote changes
@app.route("/userdata/<string:username>", methods = ['GET','PUT'])
@auth.login_required
def userData(username):
    data=request.get_json(force=True)
    if request.method == 'GET':
      return getUserData(username)
    elif request.method == 'PUT':
      positive_vote=data['positive_vote']
      negative_vote=data['negative_vote']
      return updateVotes(username,positive_vote,negative_vote)

@app.route("/userpin/<string:username>", methods = ['GET','PUT'])
@auth.login_required
def userPin(username):
    if request.method == 'GET':
      return getUserPin(username)
    elif request.method == 'PUT':
      data=request.get_json(force=True)
      user_pin=data['user_pin']
      return updatePin(username,user_pin)

@app.route("/validatuser/<string:username>", methods = ['GET','POST'])
@auth.login_required
def validateUser(username):
  user=session.query(User).filter_by(username=username).one()
  if user.role==0:
    raise BadRequest()
  if request.method=='GET':
    users = session.query(User).filter_by(role=0).all()
    return jsonify(Users=[i.serialize for i in users])
  if request.method=='POST':
    data=request.get_json(force=True)
    smallusername=data['smallusername']
    role_change=data['new_role']
    user2=session.query(User).filter_by(username=smallusername).one()
    user2.role=role_change
    session.add(user2)
    session.commit()

#Enpoint to GET election vote data and PUT new vote
@app.route("/voteelection/<int:election_id>", methods = ['GET','PUT'])
@auth.login_required
def voteElection(election_id):
    if request.method == 'GET':
      return getElectionVoteData(election_id)
    elif request.method == 'PUT':
      data=request.get_json(force=True)
      data=request.get_json(force=True)
      username=data['username']
      positive_vote=data['positive_vote']
      negative_vote=data['negative_vote']
      return updateElectionVotes(username,positive_vote,negative_vote,election_id)

# Enpoint to GET all user data and PUT new data
@app.route("/alluserdata/<string:username>", methods = ['GET','POST'])
@auth.login_required
def changeUserData(username):
  if request.method=='GET':
    return getUserProfile(username)
  elif request.method=='POST':
    data=request.get_json(force=True)
    newUsername=data['newUsername']
    gender=data['gender']
    mobile_number=data['mobile_number']
    return changeData(username,gender,mobile_number,newUsername)

#Function to verify password
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

# Endpoint to GET election data according to category
@app.route("/election/<string:organisation_name>/<string:category>",methods=['GET'])
@auth.login_required
def categoryFunction(category,organisation_name):
  return getElectionByCategory(category,organisation_name)


# Endpoint to GET all elections and POST new Election
@app.route("/elections/<string:organisation_name>", methods = ['GET', 'POST'])
@auth.login_required
def electionFunction(organisation_name):
  if request.method == 'GET':
    return getAllElections(organisation_name)
  elif request.method == 'POST':
    #Call the method to make a new election
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
    creator_username=data['creator']
    org_name=organisation_name
    return makeANewElection(name,heading,para,category,start_time,end_time,in_favour,not_in_favour,status,total_votes,org_name,creator_username)

#Endpoint to get election detail according to ID or make changes to election
@app.route("/elections/<int:election_id>", methods = ['GET', 'PUT', 'DELETE'])
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
    return updateElection(election_id,name, heading,para,category,start_time,end_time,status)

 #Call the method to remove a election
  elif request.method == 'DELETE':
    return deleteElection(election_id)

@app.route("/elections/<string:username>", methods = ['GET'])
def getCreatorElections(username):
    elections = session.query(Election).filter_by(creator_username=username).all()
    return jsonify(Elections=[i.serialize for i in elections])

#functions starting******************************************************************
#functions starting *****************************************************************
def getAllElections(organisation_name):
  elections = session.query(Election).filter_by(organisation_name=organisation_name).all()
  return jsonify(Elections=[i.serialize for i in elections])

def getUserData(username):
  user=session.query(User).filter_by(username=username).one()
  return jsonify({'total_votes':user.total_votes,'positive_votes':user.votes_in_favour,'negative_vote':user.votes_against})

def getElection(id):
  election = session.query(Election).filter_by(election_id = id).one()
  return jsonify(Election=election.serialize)

def makeANewElection(name, heading,para,category,start_time,end_time,in_favour,not_in_favour,status,total_votes,org_name,creator_username):
  election = Election(name = name, heading = heading, para=para, category=category, start_time=start_time, end_time=end_time, in_favour=in_favour, not_in_favour=not_in_favour, status=status, total_votes=total_votes,organisation_name=org_name,creator_username=creator_username)
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
  elif negative_vote == "True":
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

def getElectionByCategory(category,organisation_name):
  elections=session.query(Election).filter_by(category=category,organisation_name=organisation_name).all()
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

def getElectionVoteData(election_id):
  election=session.query(Election).filter_by(election_id = election_id).one()
  return jsonify({'total_votes':election.total_votes,'positive_votes':election.in_favour,'negative_votes':election.not_in_favour,'user_votes':election.user_total,'user_favour':election.user_favour,'user_against':election.user_against})

def updateElectionVotes(username,positive_vote,negative_vote,election_id):
  election=session.query(Election).filter_by(election_id = election_id).one()
  usersall=election.user_total
  userList=[]
  if usersall is not None:
      userList=usersall.split('*')
  if username not in userList:
    updateVotes(username,positive_vote,negative_vote)
    if election.user_total is None:
        election.user_total=""
    election.user_total=election.user_total+'*'+username
    election.total_votes=election.total_votes+1
    if positive_vote=="True":
        if election.user_favour is None:
            election.user_favour=""
        election.in_favour=election.in_favour+1
        election.user_favour=election.user_favour+'*'+username
    elif negative_vote=="True":
        if election.user_against is None:
            election.user_against=""
        election.not_in_favour=election.not_in_favour+1
        election.user_against=election.user_against+'*'+username
        session.add(election)
        session.commit()
    return jsonify({'status':True})
  else:
    return jsonify({'status':False})

def getUserPin(username):
  user=session.query(User).filter_by(username=username).one()
  return jsonify({'username':username,'user_pin':user.userpin})

def updatePin(username,user_pin):
  user=session.query(User).filter_by(username=username).one()
  user.userpin=user_pin
  return jsonify({'status':"pin updated",'username':username,'user_pin':user.userpin})

if __name__ == '__main__':
    app.debug = False
    app.run(host='0.0.0.0', port=8079)  

