require('log-timestamp');
const QRCode = require('qrcode');
const express = require('express');
const app = express();
const crypto = require('crypto');
let qrCode = null;
const WebSocketServer = require('ws').Server;
const path = require('path');

const server = require('http').createServer();
const clientwss = new WebSocketServer({server: server});
let isAlive = true;
let currentws;
let monitorws;

app.use(express.static(path.join(__dirname, '/public')));
app.get('/qr', (req, res) => {
  res.redirect("./user.html");
});
app.get('/monitor', (req, res) => {
  res.redirect("./monitor.html");
});
server.on('request', app);
server.listen(3030, () => {
  if (!(monitorws === undefined)) {
    monitorws.send(timeStamp()+'[SERVER] - server started on port 3030');
  };
  console.log('[SERVER] - server started on port 3030');
});

function noop() {}

function heartbeat() {
  this.isAlive = true;
  isAlive = true;
  /*if (!(monitorws === undefined)) {
    monitorws.send(timeStamp()+'[APP] - isAlive' );
  };*/
}

clientwss.on('connection', (ws) => {
  currentws = ws;
  ws.isAlive = true;
  ws.on('pong', heartbeat);
  ws.on('message', (msg) => {
    if (!(monitorws === undefined)) {
      monitorws.send(timeStamp()+'[APP] - ' + msg);
    };
    console.log('[APP] - ' + msg);
    if (msg == 'generatekey') {
      if (qrCode === null) {
        if (!(monitorws === undefined)) {
          monitorws.send(timeStamp()+'[SERVER] - generate a new key');
        };
        console.log('[SERVER] - generate a new key');
        qrCode = crypto.randomBytes(30).toString('hex');
        if (!(monitorws === undefined)) {
          monitorws.send(timeStamp()+'[SERVER] - ' + qrCode );
        };
        console.log('[SERVER] - ' + qrCode);
        QRCode.toDataURL(qrCode, { scale: '25'}, (err, url) => {
          if (err) console.log('[SERVER ERR] - ' + err);
          ws.send(url);
        });
      }
    } else if (msg == 'renewkey') {
      if (!(monitorws === undefined)) {
        monitorws.send(timeStamp()+'[SERVER] - regenerate a key' );
      };
      console.log('[SERVER] - regenerate a key');
      qrCode = crypto.randomBytes(30).toString('hex');
      if (!(monitorws === undefined)) {
        monitorws.send(timeStamp()+'[SERVER] - ' + qrCode );
      };
      console.log('[SERVER] - ' + qrCode);
      QRCode.toDataURL(qrCode, { scale: '25'}, (err, url) => {
        if (err) console.log('[SERVER ERR] - ' + err);
        ws.send(url);
      });
    } else if (msg == 'expirekey') {
      qrCode = null;
      if (!(monitorws === undefined)) {
        monitorws.send(timeStamp() + '[SERVER] - expire key');
      };
      console.log('[SERVER] - expire key');
    }
  });
});

const heartbeatInterval = setInterval(() => {
  if (currentws === undefined) {

  } else {
    if (currentws.isAlive === false) {
      isAlive = false;
      return;
    }
    currentws.isAlive = false;
    currentws.ping(noop);
  }
}, 100);

const monwss = new WebSocketServer({ port: 3050});
monwss.on('connection', (ws) => {
  monitorws = ws;
  ws.on('message', (msg) => {
    if (!(monitorws === undefined)) {
      monitorws.send(timeStamp() + '[MONITOR] - ' + msg);
    };
    console.log('[MONITOR] - ' + msg);
  });
});

const wss = new WebSocketServer({ port: 3000 });

wss.on('connection', (ws) => {
  ws.on('message', (msg) => {
    if (!(monitorws === undefined)) {
      monitorws.send(timeStamp() + '[DOORLOCK] - ' + msg);
    };
    console.log('[DOORLOCK] - ' + msg);
    if (msg == "connection established") {
    } else if (msg === qrCode) {
      setTimeout(()=> {
        if (isAlive === true) {
          if (!(monitorws === undefined)) {
            monitorws.send(timeStamp() + '[SERVER] -> [DOORLOCK] - success');
          };
          if (!(monitorws === undefined)) {
            monitorws.send(timeStamp() + '[DOORLOCK] - door opened');
          };
          console.log('[SERVER] -> [DOORLOCK] - success');
          ws.send("success");
          currentws.send("success");
        } else {
          if (!(monitorws === undefined)) {
            monitorws.send(timeStamp() + '[SERVER] - heartbeat auth failed');
          };
          console.log('[SERVER] - heartbeat auth failed');
        }
      },200);
    } else {
      if (!(monitorws === undefined)) {
        monitorws.send(timeStamp() + '[SERVER] - invalid key');
        monitorws.send(timeStamp() + '[SERVER] -> [DOORLOCK] - fail');
      };
      console.log('[SERVER] - invalid key');
      console.log('[SERVER] -> [DOORLOCK] - fail');
      ws.send("fail");
    }
  });
});

function timeStamp() {
// Create a date object with the current time
  var now = new Date();

// Create an array with the current month, day and time
  var date = [ now.getMonth() + 1, now.getDate(), now.getFullYear() ];

// Create an array with the current hour, minute and second
  var time = [ now.getHours(), now.getMinutes(), now.getSeconds() ];

// Determine AM or PM suffix based on the hour
  var suffix = ( time[0] < 12 ) ? "AM" : "PM";

// Convert hour from military time
  time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;

// If hour is 0, set it to 12
  time[0] = time[0] || 12;

// If seconds and minutes are less than 10, add a zero
  for ( var i = 1; i < 3; i++ ) {
    if ( time[i] < 10 ) {
      time[i] = "0" + time[i];
    }
  }

// Return the formatted string
  return '['+ date.join("/") + " " + time.join(":") + " " + suffix + ']';
};
