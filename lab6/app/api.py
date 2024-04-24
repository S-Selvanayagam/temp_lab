from fastapi import FastAPI, Body, Depends, Request, File, UploadFile, Response
from fastapi.middleware.cors import CORSMiddleware
from starlette.responses import FileResponse
import pymongo
import os
import shutil

from app.model import PostSchema, UserSchema, UserLoginSchema
from app.auth.auth_bearer import JWTBearer
from app.auth.auth_handler import signJWT, decodeJWT

# Database setup
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["upload_site"]
myusers = mydb["users"] # Login and signup
if (not os.path.exists('./public')):
	os.mkdir('./public')

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
    return {"message": "Welcome to your file upload site!"}

@app.get("/private_files", dependencies=[Depends(JWTBearer())])
async def get_files(request: Request):
	token = request.headers["authorization"].split(" ")[1]
	email = decodeJWT(token)["user_id"]
	if (os.path.exists(f'./{email}')):
		return os.listdir(f'./{email}')
	return []

@app.get("/public_files")
async def get_public_files():
	return os.listdir('./public')

@app.post("/public_upload")
async def handle_public_upload(request: Request, response: Response, file: UploadFile = File(...)):
    try:
        new_path = './public/'+file.filename
        with open(new_path, 'wb') as f:
            shutil.copyfileobj(file.file, f)
    except Exception as e:
        return {"message": "There was an error uploading the file", "error": e}
    finally:
        file.file.close()
    return {"message": "Uploaded!"}

@app.post("/private_upload", dependencies=[Depends(JWTBearer())])
async def handle_private_upload(request: Request, response: Response, file: UploadFile = File(...)):
    token = request.headers["authorization"].split(" ")[1]
    email = decodeJWT(token)["user_id"]
    if (not os.path.exists(f'./{email}')):
        os.mkdir(f'./{email}')
    try:
        new_path = f'./{email}/'+file.filename
        with open(new_path, 'wb') as f:
            shutil.copyfileobj(file.file, f)
    except Exception as e:
        return {"message": "There was an error uploading the file", "error": e}
    finally:
        file.file.close()
    return {"message": "Uploaded!"}

@app.get("/download_public_file/{file}")
async def download_public_file(file: str):
    file_location = './public/'+file
    return FileResponse(file_location, media_type='application/octet-stream',filename=file)

@app.get("/download_private_file/{file}", dependencies=[Depends(JWTBearer())])
async def download_private_file(file: str, request: Request):
    token = request.headers["authorization"].split(" ")[1]
    email = decodeJWT(token)["user_id"]
    file_location = f'./{email}/'+file
    return FileResponse(file_location, media_type='application/octet-stream',filename=file)

@app.post("/user/signup", tags=["user"])
async def create_user(user: UserSchema = Body(...)):
    myusers.insert_one(user.dict())
    return signJWT(user.email)

@app.post("/user/login", tags=["user"])
async def user_login(user: UserLoginSchema = Body(...)):
    if check_user(user):
        return signJWT(user.email)
    return {
        "error": "Wrong login details!"
    }