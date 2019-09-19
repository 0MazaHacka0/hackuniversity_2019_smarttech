from flask import Flask, request

app = Flask(__name__)

isBound = False


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/bind')
def bind():
    global isBound
    isBound = request.args.get('param', default=1, type=int)
    return f"{isBound}"


@app.route('/get')
def get():
    global isBound
    if isBound:
        isBound = False
        return "1"
    else:
        return "0"


if __name__ == '__main__':
    app.run()
