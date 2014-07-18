var bleno = require('bleno');

var name = "MKMS 25";

var serviceUUID1 = 'fdee74dca8de31961149d43596c00a4f';
var serviceUuids = [serviceUUID1];



var PrimaryService = bleno.PrimaryService;

var primaryService = new PrimaryService({
    uuid: serviceUUID1, 
    characteristics: null
});

bleno.on('stateChange', function(state) {
    printToConsole('on -> stateChange: ' + state);
    if (state === 'poweredOn') {
        advertisingMode(true);
    } else {
        advertisingMode(false);
    }
});


bleno.on('advertisingStart', function(error) {
    printToConsole('on -> advertisingStart: ' + (error ? 'error ' + error : 'success'));
    if (!error) {
    	printToConsole("Advertising with this name " + name);
        bleno.setServices([
            primaryService
        ]);
    }
});

function advertisingMode(state){
    if(state){
        bleno.startAdvertising(name);
    } else {
        bleno.stopAdvertising();
    }
}


function printToConsole(message){
	console.log(message);
}

