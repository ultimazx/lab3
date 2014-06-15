var serialport = require('serialport'),
	
SerialPort = serialport.SerialPort,
	
io = require('socket.io-client');


socket = io.connect('http://192.168.2.11:8080');


var sp = new SerialPort('/dev/ttyUSB0',{
	
baudrate: 115200,
	
parser: serialport.parsers.readline('\r\n')
});



sp.open(function(){
        console.log('Serial port connected');
});

sp.on('data', function(data){
        if(data =='a')
                handshake(data);
        else
                sendData(data);
});

socket.on('disconnect', function(){
        sp.write('0');
});

function handshake(data){
        sp.write('a');
	LED(data);
}

function sendData(data){
        socket.emit('temp', data);
}

function LED(data){
                sp.write(data);
}







