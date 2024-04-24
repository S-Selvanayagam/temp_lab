from fastapi import FastAPI

app = FastAPI(allow_origins=["*"])

@app.get("/")
def home():
	return {"Hello" : "World"}

@app.get("/hits")
def hit_counter():
	f = open("count.txt", "r")
	curr = int(f.read().strip())
	f.close()
	f = open("count.txt", "w")
	f.write(str(curr+1))
	return {"hits" : curr+1}
