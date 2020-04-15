from flask import Flask, request

from vertifyimage.main import Verify, code_xy

app = Flask(__name__)

v = Verify()


@app.route('/answer', methods=['POST'])
def answer():
    base64_image = request.form['base64_image']
    res = v.verify(base64_image)
    xy = code_xy(res, False)
    return xy


if __name__ == '__main__':
    app.run()
