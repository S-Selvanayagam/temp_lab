from fastapi import FastAPI, Body, Depends, Request
from fastapi.middleware.cors import CORSMiddleware
import pymongo

from app.model import PostSchema, UserSchema, UserLoginSchema
from app.auth.auth_bearer import JWTBearer
from app.auth.auth_handler import signJWT, decodeJWT

# Database setup
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["blog"]
myposts = mydb["posts"] # Blog posts
myusers = mydb["users"] # Login and signup
mylogins = mydb["logins"]

app = FastAPI()

# Allow anyone to call the API from their own apps
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
)

def check_user(data: UserLoginSchema):
    data = myusers.find_one({"email": data.email, "password": data.password})
    if (data):
        return True
    return False

@app.get("/", tags=["root"])
async def read_root() -> dict:
    return {"message": "Welcome to your blog!"}

@app.get("/attempts", dependencies=[Depends(JWTBearer())])
async def get_logins(request: Request):
	token = request.headers["authorization"].split(" ")[1]
	email = decodeJWT(token)["user_id"]
	data = mylogins.find_one({"email" : email}, {'_id': 0})
	return data

@app.post("/user/signup", tags=["user"])
async def create_user(user: UserSchema = Body(...)):
    myusers.insert_one(user.dict())
    mylogins.insert_one({"email" : user.dict()['email'], "success" : 0, "failure": 0})
    return signJWT(user.email)

@app.post("/user/login", tags=["user"])
async def user_login(user: UserLoginSchema = Body(...)):
    if check_user(user):
        mylogins.update_one({"email": user.dict()['email']}, {"$inc": {"success" : 1}})
        return signJWT(user.email)
    
    mylogins.update_one({"email": user.dict()['email']}, {"$inc": {"failure" : 1}})
    return {
        "error": "Wrong login details!"
    }