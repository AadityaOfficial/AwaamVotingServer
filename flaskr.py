from flask import Flask, request, jsonify
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from models import Base,User,Election


engine = create_engine('sqlite:///app.db')
Base.metadata.bind = engine

DBSession = sessionmaker(bind=engine)
session = DBSession()

app = Flask(__name__) 

# Create the appropriate app.route functions, 
#test and see if they work


#Make an app.route() decorator here
@app.route("/")
@app.route("/elections", methods = ['GET', 'POST'])
def electionFunction():
  if request.method == 'GET':
    return getAllElections()
  elif request.method == 'POST':
    #Call the method to make a new puppy
    print "Making a New election"
    name = request.args.get('name', '')
    desc_h1 = request.args.get('desc_h1', '')
    desc_h2=request.args.get('desc_h2', '')
    desc_h3=request.args.get('desc_h3', '')
    desc_p1=request.args.get('desc_p1', '')
    desc_p2=request.args.get('desc_p2', '')
    desc_p3=request.args.get('desc_p3', '')
    category = request.args.get('category', '')
    start_time=request.args.get('start_time', '')
    end_time=request.args.get('end_time', '')
    in_favour=request.args.get('in_favour', '')
    not_in_favour=request.args.get('not_in_favour', '')
    status=request.args.get('status', '')
    total_votes=request.args.get('total_votes', '')
    return makeANewElection(name, desc_h1,desc_h2,desc_h3,desc_p1,desc_p2,desc_p3,category,start_time,end_time,in_favour,not_in_favour,status,total_votes)
 
#Make another app.route() decorator here that takes in an integer id in the URI
@app.route("/elections/<int:id>", methods = ['GET', 'PUT', 'DELETE'])
#Call the method to view a specific puppy
def electionFunctionId(id):
  if request.method == 'GET':
    return getElection(id)
    
#Call the method to edit a specific puppy  
  elif request.method == 'PUT':
    name = request.args.get('name', '')
    desc_h1 = request.args.get('desc_h1', '')
    desc_h2=request.args.get('desc_h2', '')
    desc_h3=request.args.get('desc_h3', '')
    desc_p1=request.args.get('desc_p1', '')
    desc_p2=request.args.get('desc_p2', '')
    desc_p3=request.args.get('desc_p3', '')
    category = request.args.get('category', '')
    start_time=request.args.get('start_time', '')
    end_time=request.args.get('end_time', '')
    status=request.args.get('status', '')
    return updateElection(id,name, desc_h1,desc_h2,desc_h3,desc_p1,desc_p2,desc_p3,category,start_time,end_time,status)
    
 #Call the method to remove a puppy 
  elif request.method == 'DELETE':
    return deleteElection(id)

def getAllElections():
  elections = session.query(Election).all()
  return jsonify(Elections=[i.serialize for i in elections])

def getElection(id):
  election = session.query(Election).filter_by(election_id = id).one()
  return jsonify(Election=election.serialize) 
  
def makeANewElection(name, desc_h1,desc_h2,desc_h3,desc_p1,desc_p2,desc_p3,category,start_time,end_time,in_favour,not_in_favour,status,total_votes):
  election = Election(name = name, desc_h1 = desc_h1, desc_h2=desc_h2, desc_h3=desc_h3, desc_p1=desc_p1, desc_p2=desc_p2, desc_p3= desc_p3, category=category, start_time=start_time, end_time=end_time, in_favour=in_favour, not_in_favour=not_in_favour, status=status, total_votes=total_votes)
  session.add(election)
  session.commit()
  return jsonify(Election=election.serialize)

def updateElection(id,name, desc_h1,desc_h2,desc_h3,desc_p1,desc_p2,desc_p3,category,start_time,end_time,status):
  election = session.query(Election).filter_by(election_id = id).one()
  if not name:
    election.name = name
  if not desc_h1:
    election.desc_h1 = desc_h1
  if not desc_h2:
    election.desc_h2 = desc_h2
  if not desc_h3:
    election.desc_h3 = desc_h3
  if not desc_p1:
    election.desc_p1 = desc_p1
  if not desc_p2:
    election.desc_p2 = desc_p2
  if not desc_p3:
    election.desc_p3 = desc_p3
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

def deleteElection(id):
  election = session.query(Election).filter_by(election_id = id).one()
  session.delete(election)
  session.commit()
  return "Removed election with id %s" % id


if __name__ == '__main__':
    app.debug = False
    app.run(host='0.0.0.0', port=5000)  