


var cordova = require('cordova');

console.log('DJIPlugin installed');

var PLUGIN_NAME = 'DJIPlugin';

module.exports = {
	jurg: function() {
		console.log('DJIPlugin Export Test');
	},
	echo: function(phrase, cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
	},

	getDate: function(cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'getDate', []);
	},

	attachToDevice: function(cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'attachToDevice', []);
	},

	setTestMode: function(mode, cb) {
        cordova.exec(cb, null, PLUGIN_NAME, 'setTestMode', [mode]);
    },

	getLocation: function(cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'getLocation', []);
	},

	getAttitude: function(cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'getAttitude', []);
	},
	getStatus: function(cb) {
		cordova.exec(cb, null, PLUGIN_NAME, 'getStatus', []);
	}


};
