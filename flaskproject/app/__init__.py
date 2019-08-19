from flask import Flask, redirect, url_for, abort, render_template, request, jsonify, json 
from flask_sqlalchemy import  SQLAlchemy		# database ORM
#from sqlalchemy import or_, distinct, desc, over
from sqlalchemy import *
from flask_sqlalchemy import get_debug_queries		# database_debug
from datetime import datetime, timedelta	# timestamp
import random
import os

app = Flask(__name__)  # Get Flast Object，named by the module
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://postgres:32274550scw@localhost/test'
db = SQLAlchemy(app)
basedir = os.path.abspath(os.path.dirname(__file__))

class User(db.Model):
	__tablename__ = 'user'
	id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String(50))
	email = db.Column(db.String(50))
	password = db.Column(db.String(50))
	
	def __init__(self, username, password, email):
		#self.id = id
		self.username = username
		self.password = password
		self.email = email

class Event(db.Model):
	__tablename__ = 'Event'
	id = db.Column(db.Integer, primary_key=True)
	date = db.Column(db.String(50))
	type = db.Column(db.String(30))
	content = db.Column(db.String(100))
	lat = db.Column(db.Float)
	long = db.Column(db.Float)
	username = db.Column(db.String(50))
	
	#def __init__(self, id, date, type, content):
	def __init__(self, date, type, content, lat, long, username):
		#self.id = id
		self.date = date
		self.type = type
		self.content = content
		self.lat = lat
		self.long = long
		self.username = username
	
	def to_json(self):
		return{
			"date": self.date,
			"type": self.type,
			"content": self.content,
			"lat": self.lat,
			"long": self.long,
			"username": self.username
		}


class Bluetooth_Device(db.Model):
	__tablename__ = 'bluetooth_device'
	id = db.Column(db.Integer, primary_key=True)
	user_to_device_id = db.Column(db.Integer)
	mac_address = db.Column(db.String(50))
	rssi = db.Column(db.Integer)
	date = db.Column(db.String(50))
	lat = db.Column(db.Float)
	long = db.Column(db.Float)
	
	def __init__(self, user_to_device_id, mac_address, rssi, date, lat, long):
		self.user_to_device_id = user_to_device_id
		self.mac_address = mac_address
		self.rssi = rssi
		self.date = date
		self.lat = lat
		self.long = long
	
	def to_json(self):
		return{
			"user_to_device_id": self.user_to_device_id,
			"mac_address": self.mac_address,
			"rssi": self.rssi,
			"date": self.date,
			"lat": self.lat,
			"long": self.long
		}
	

class Bluetooth(db.Model):
	__tablename__ = 'bluetooth'
	id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String(50))
	date = db.Column(db.String(50))
	lat = db.Column(db.Float)
	long = db.Column(db.Float)
	#close = db.Column(db.Integer, unique=False)
	#middle = db.Column(db.Integer, unique=False)
	#far = db.Column(db.Integer, unique=False)

	
	def __init__(self, username, date, lat, long):
		self.username = username # should be user name
		self.date = date
		self.lat = lat
		self.long = long
		#self.close = close
		#self.middle = middle
		#self.far = far
	
	def to_json(self):
		return{
			"id": self.id,
			"date": self.date,
			"lat": self.lat,
			"long": self.long
		}

		
	
	def __repr__(self):
		return '(%r, %r, %r, %r)' % (self.username, self.date, self.lat, self.long)



@app.route('/' )
def index():
	#myBluetooth = Bluetooth.query.all()
	#return render_template('add_user.html', myBluetooth=myBluetooth)
	#mapbox_access_token='pk.eyJ1IjoibzBtYXh3ZWxsMG8iLCJhIjoiY2pudmh1Z3drMGZzeTNrcnd1OWhqc2o5MSJ9.bKICAZaDkuSUzMPkKbyEQg'
	#return render_template('new.html')
	#print(aa)
	return render_template('map.html')
	#return redirect(url_for('post_user'))
	#return "<h1 style='color: red'>index</h1>" 

# test html
@app.route('/data', methods=['GET'])
def send():
	#dict = {"a":random.random(), "b":2} 
	#dic={}
	#geometry={}
	#geometry{"type"} = "Point"
	#coordinates=[124.71679687499999,56.17002298293205]
	#geometry{"coordinates"} = coordinates
	data={}
	data["type"]="FeatureCollection"
	features=[]
	
	features_son1={}
	features_son1["type"]="Feature"
	
	features_son2={}
	features_son2["type"]="Feature"
	
	geometry1 = {}
	geometry1["type"] = "Point"
	coordinates1 = [139.677331,35.663352]
	geometry1["coordinates"] = coordinates1
	features_son1["geometry"]=geometry1
	
	geometry2 = {}
	geometry2["type"] = "Point"
	coordinates2 = [139.677811,35.660430]
	geometry2["coordinates"] = coordinates2
	features_son2["geometry"]=geometry2
	
	features=[]
	for repeat in range(x):
		features.append(features_son1)
	
	for repeat in range(y):
		features.append(features_son2)
	data["features"]=features
	aa=json.dumps(data,sort_keys=False)
	
	return aa




# test simulator
@app.route('/post_query', methods=['POST'])
def post_query():
	info = request.get_json()
	time_fix = datetime.strptime(info['date'], '%Y-%m-%d %H:%M:%S')
	start = time_fix - timedelta(seconds = 10)	# send info 10seconds from now
	bluetooth = Bluetooth.query.filter(Bluetooth.date.between(str(start), str(time_fix))).all()
	if bluetooth:
		id = []
		for x in bluetooth:
			id.append(x.id)
		print(id)
		count = Bluetooth_Device.query.filter(or_(Bluetooth_Device.user_to_device_id == x for x in id)).distinct(Bluetooth_Device.mac_address)	# distinct the repeat info
	#count = Bluetooth_Device.query.filter(or_(Bluetooth_Device.user_to_device_id == x for x in id))
	#dist = Bluetooth_Device.query.func().row_number().over(partition_by=Bluetooth_Device.mac_address,order_by=Bluetooth_Device.date.desc())
	#dist = db.session.query(Bluetooth_Device,func.row_number().over(partition_by=Bluetooth_Device.mac_address,order_by=Bluetooth_Device.date.desc())).select_from(count)
		print(count)
		info = []
		for x in count:
			info.append(x.to_json())
		return jsonify(info)
	else:
		return "false"

# send event to phone
@app.route('/get_event', methods=['GET'])
def get_event():
	event = Event.query.all()
	result = []
	for x in event:
		result.append(x.to_json())
	#even_json = json.dumps(event,sort_keys=False)
	return jsonify(result)
	
# get event from phone
@app.route('/post_event', methods=['POST'])
def post_event():
	data = request.get_json()
	event = Event(data['type'], data['content'])
	db.session.add(event)
	db.session.commit()
	return 'ok'
	
# get date from phone, send device to phone problem: send the last info, location
@app.route('/post_data', methods=['POST'])
def post_data():    
	info = request.get_json()
	bluetooth = Bluetooth(info['username'], info['date'], info['lat'], info['long'])
	db.session.add(bluetooth)
	db.session.commit()
	getid=bluetooth.id
	for p in info['device']:
		line = Bluetooth_Device(getid, p['address'], p['rssi'], info['date'], info['lat'], info['long'])
		db.session.add(line)
	db.session.commit()
	
	now = datetime.now()
	start = now - timedelta(seconds = 10)	# send info 10seconds from now
	bluetooth = Bluetooth.query.filter(Bluetooth.date.between(str(start), str(now))).all()
	if bluetooth:
		id = []
		for x in bluetooth:
			id.append(x.id)
		print(id)
		count = Bluetooth_Device.query.filter(or_(Bluetooth_Device.user_to_device_id == x for x in id)).distinct(Bluetooth_Device.mac_address)	# distinct the repeat info
	#count = Bluetooth_Device.query.filter(or_(Bluetooth_Device.user_to_device_id == x for x in id))
	#dist = Bluetooth_Device.query.func().row_number().over(partition_by=Bluetooth_Device.mac_address,order_by=Bluetooth_Device.date.desc())
	#dist = db.session.query(Bluetooth_Device,func.row_number().over(partition_by=Bluetooth_Device.mac_address,order_by=Bluetooth_Device.date.desc())).select_from(count)
		print(count)
		info = []
		for x in count:
			info.append(x.to_json())
		return jsonify(info)
	else:
		return "false"
		
# store photo
@app.route('/post_photo', methods=['POST'])
def post_photo(): 
	image = request.files['photo']
	#username = request.form.get("name")
	stringtest = request.form['infomessage']
	print(stringtest)
	username = "test"
	path = basedir+"/static/photo/"
	file_path = path+image.filename
	image.save(file_path)
	print('upload')
	return 'ok'


# login
@app.route('/login', methods=['POST'])
def login():
	info = request.get_json()
	user_check = User.query.filter(User.username==info['username'], User.password==info['password']).first()
	# all() return a list
	# first() return the first data of the resulit
	if user_check == None:
		return 'null'
	else:
		return 'good'

# register
@app.route('/register', methods=['POST'])
def register():
	info = request.get_json()
	user_check = User.query.filter(User.username==info['username']).first()
	if user_check != None:
		return 'exist'
	else:
		user=User(info['username'], info['password'], info['email'])
		db.session.add(user)
		db.session.commit()
		return 'good'
		
		
# phonedata
@app.route('/phone', methods=['POST'])
def phone():
	info = request.get_json()
	fw=open('data.txt','w')
	fw.write(info['data'])
	fw.close()
	return "ok"
	
	
def sql_debug(response):
    queries = list(get_debug_queries())
    query_str = ''
    total_duration = 0.0
    for q in queries:
        total_duration += q.duration
        stmt = str(q.statement % q.parameters).replace('\n', '\n       ')
        query_str += 'Query: {0}\nDuration: {1}ms\n\n'.format(stmt, round(q.duration * 1000, 2))

    print('=' * 80)
    print(' SQL Queries - {0} Queries Executed in {1}ms'.format(len(queries), round(total_duration * 1000, 2)))
    print('=' * 80)
    print(query_str.rstrip('\n'))
    print('=' * 80 + '\n')
    return response
app.after_request(sql_debug)
	


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)

