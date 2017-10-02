from sqlalchemy import Column, Integer, String, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine

Base = declarative_base()

class User(Base):
    __tablename__ = 'user'


    user_name =Column(String(80), nullable = False)
    user_id = Column(Integer, primary_key = True)
    role = Column(Boolean, nullable=False)
    email=Column(String(200), nullable=False)
    votes=Column(String(500), nullable=False)
    image=Column(String(200), nullable=False)

    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
       		'id': self.user_id,
           'name': self.user_name,
           'role' : self.description,
           'email':self.email,
           'votes':self.votes,
           'image':self.image
       }
 
class Election(Base):
    __tablename__ = 'election'


    name =Column(String(80), nullable = False)
    desc_h1=Column(String(200), nullable=False)
    desc_h2=Column(String(200), nullable=False)
    desc_h3=Column(String(200), nullable=False)
    desc_p1=Column(String(500), nullable=False)
    desc_p2=Column(String(500), nullable=False)
    desc_p3=Column(String(500), nullable=False)
    election_id = Column(Integer, primary_key = True, )
    category = Column(String(100), nullable=False)
    start_time=Column(String(100),nullable=False)
    end_time=Column(String(100),nullable=False)
    in_favour=Column(Integer, nullable=False)
    not_in_favour=Column(Integer,nullable=False)
    status=Column(Integer,nullable=False)
    total_votes=Column(Integer,nullable=False)
    
    @property
    def serialize(self):
       """Return object data in easily serializeable format"""
       return {
          'id': self.election_id,
           'name': self.name,
           'description_header1' : self.desc_h1,
            'description_header2' : self.desc_h2,
            'description_header3' : self.desc_h3,
            'description_para1' : self.desc_p1,
            'description_para2' : self.desc_p2,
            'description_para3' : self.desc_p3,
            'category' :self.category,
            'start_time' :self.start_time,
            'end_time' :self.end_time,
            'in_favour' :self.in_favour,
            'not_in_favour' :self.not_in_favour,
            'status' :self.status,
            'total_votes':self.total_votes
       }
engine = create_engine('sqlite:///app.db')
Base.metadata.create_all(engine)