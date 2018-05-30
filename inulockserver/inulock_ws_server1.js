var QRCode = require('qrcode');
var http = require('http');
var crypto = require('crypto');
var qrCode = crypto.randomBytes(30).toString('hex');

function testQRCode (req, res) {
  res.writeHead(200, { 'Content-Type': 'text/html' })
  QRCode.toDataURL(qrCode, { scale: '25'}, function (err, url) {
    if (err) console.log('error: ' + err)
    res.end("<!DOCTYPE html/><html><head><title>node-qrcode</title></head><body><img src='" + url + "'/></body></html>")
  });
};
http.createServer(testQRCode).listen(3030);
console.log('test server started on port 3030');
console.log(qrCode);

var WebSocketServer = require("ws").Server;
var wss = new WebSocketServer({ port: 3000 });

wss.on("connection", function(ws) {
  ws.on("message", function(message) {
    console.log('->Received: '+message);
    if (message == "connection established") {
    } else if (message === qrCode) {
      console.log("<-Send: success");
      ws.send("success");
    } else {
      console.log("<-Send: fail");
      ws.send("fail");
    }
  });
});
