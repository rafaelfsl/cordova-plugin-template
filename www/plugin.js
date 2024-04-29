
var exec = require('cordova/exec');

var PLUGIN_NAME = 'MyCordovaPlugin';

var MyCordovaPlugin = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  },
  getDate: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'getDate', []);
  },
  googleSign: function(clientOptions, cb, errorCb) {
    exec(cb, errorCb, PLUGIN_NAME, 'googleSign', [clientOptions]);
  }
};

module.exports = MyCordovaPlugin;
