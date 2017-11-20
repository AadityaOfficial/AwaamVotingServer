from sqlalchemy import Column, Integer, String, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
Base = declarative_base()
from passlib.apps import custom_app_context as pwd_context
import random,string
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
secret_key=''.join(random.choice(string.ascii_uppercase+string.digits) for x in range(32))

class Organisation(Base):
  __tablename__='organisation'
  name=Column(String(30))
  id=Column(Integer,primary_key=True)
  desc=Column(String)

  @property
  def serialize(self):
    return{
    'id':self.id,
    'name':self.name,
    'desc':self.desc
    }


#username is email id
class User(Base):
    __tablename__ = 'user'


    username =Column(String(80),index=True)
    id = Column(Integer, primary_key = True)
    password_hash=Column(String(100))
    gender=Column(String(32))
    mobile_number=Column(String(15))
    total_votes=Column(Integer)
    votes_in_favour=Column(Integer)
    votes_against=Column(Integer)
    role = Column(Integer)
    status=Column(Integer)
    organisation=Column(String)
    userpin=Column(String(5))


    def hash_password(self,password):
      self.password_hash=pwd_context.encrypt(password)
    def verify_password(self,password):
      return pwd_context.verify(password,self.password_hash)
    def generate_auth_token(self, expiration=600):
      s = Serializer(secret_key, expires_in = expiration)
      return s.dumps({'id': self.id })
    @staticmethod
    def verify_auth_token(token):
      s = Serializer(secret_key)
      try:
        data = s.loads(token)
      except SignatureExpired:
        #Valid Token, but expired
        return None
      except BadSignature:
        #Invalid Token
        return None
      user_id = data['id']
      return user_id

    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
          'id': self.id,
           'username': self.username,
           'gender':self.gender,
           'total_votes':self.total_votes,
           'votes_in_favour':self.votes_in_favour,
           'votes_against':self.votes_against,
           'mobile_number':self.mobile_number,
           'role' : self.role,
           'userpin':self.userpin,
           'organisation':self.organisation,
           'status':self.status
       }

class Election(Base):
    __tablename__ = 'election'


    name =Column(String(80), nullable = False)
    heading=Column(String(200), nullable=False)
    para=Column(String(500), nullable=False)
    election_id = Column(Integer, primary_key = True, )
    category = Column(String(100), nullable=False)
    start_time=Column(String(100),nullable=False)
    end_time=Column(String(100),nullable=False)
    in_favour=Column(Integer, nullable=False)
    not_in_favour=Column(Integer,nullable=False)
    status=Column(Integer,nullable=False)
    total_votes=Column(Integer,nullable=False)
    user_favour=Column(String)
    user_against=Column(String)
    user_total=Column(String)
    creator_username=Column(String,nullable=False)
    organisation_name=Column(String,nullable=False)

    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
          'id': self.election_id,
           'name': self.name,
           'heading' : self.heading,
            'paragraph' : self.para,
            'category' :self.category,
            'start_time' :self.start_time,
            'end_time' :self.end_time,
            'in_favour' :self.in_favour,
            'not_in_favour' :self.not_in_favour,
            'status' :self.status,
            'total_votes':self.total_votes,
            'user_favour':self.user_favour,
            'user_against':self.user_against,
            'user_total':self.user_total,
            'creator_username':self.creator_username,
            'organisation_name':self.organisation_name

       }

engine = create_engine('sqlite:///app.db')
Base.metadata.create_all(engine)