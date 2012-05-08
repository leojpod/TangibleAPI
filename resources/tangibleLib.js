/*  by /Leoj -- /Lekko -- /Lojeuv
 *
 */
/*jslint devel: true*/
/*global $ */

// <editor-fold defaultstate="collapsed" desc="Basic methods">
function tangibleREST(method, uri, params, onSuccess, onError, async) {
	"use strict";
	var ajaxParams = {
		type : method,
		dataType : "json",
		data : params
	};
	if (onSuccess !== undefined) {
		ajaxParams.success =
			function (data, textStatus, jqXHR) {
				onSuccess(data);
			};
	}
	if (onError !== undefined) {
		ajaxParams.error =
			function (jqXHR, textStatus, errorThrown) {
				onError(errorThrown);
			};
	}
	if (async !== undefined) {
		ajaxParams.async = async;
		if (async === false) {
			console.log('making an async call to : <<' + uri + '>>');
		}
	}
	$.ajax(
		"http://localhost:9998/tangibleapi/" + uri,
		ajaxParams
	);
}

function tangibleGET(uri, params, onSuccess, onError, async) {
	"use strict";
	tangibleREST("GET", uri, params, onSuccess, onError, async);
}
function tangiblePOST(uri, params, onSuccess, onError, async) {
	"use strict";
	tangibleREST("POST", uri, params, onSuccess, onError, async);
}
function tangibleDELETE(uri, params, onSuccess, onError, async) {
	"use strict";
	tangibleREST("DELETE", uri, params, onSuccess, onError, async);
}
function tangiblePUT(uri, params, onSuccess, onError, async) {
	"use strict";
	tangibleREST("PUT", uri, params, onSuccess, onError, async);
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Method to support the API ">
function registerApplication(name, description, onSuccess, onError, async) {
	"use strict";
	tangiblePUT("app/registration/", {
		appname : name,
		description : description
	}, onSuccess, onError, async);
}
function removeApplication(appUUID, onSuccess, onError, async) {
	"use strict";
	tangibleDELETE("app/registration/" + appUUID, {}, onSuccess, onError, async);
}
function getDeviceList(appUUID, onSuccess, onError, async) {
	"use strict";
	var url = appUUID + "/device/";
	tangibleGET(url, {}, onSuccess, onError, async);
}
function reserveDeviceById(appUUID, devId, onSuccess, onError, async) {
	"use strict";
	var url = appUUID + "/device/reservation/" + devId;
	tangiblePUT(url, {}, onSuccess, onError, async);
}
function removeReservation(appUUID, devId, onSuccess, onError, async) {
	"use strict";
	var uri = appUUID + "/device/reservation/" + devId;
	tangibleDELETE(uri, {}, onSuccess, onError, async);
}
function infoOnDevice(appUUID, devId, onSuccess, onError, async) {
	"use strict";
	var uri = appUUID + "/device/info/" + devId;
	tangibleGET(uri, {}, onSuccess, onError, async);
}
function showColor(appUUID, devId, color, onSuccess, onError, async) {
	"use strict";
	var uri = appUUID + "/device_methods/" + devId + "/show_color/";
	tangiblePUT(uri, {
		color : color
	}, onSuccess, onError, async);
}
//TODO add the suppport for picture and events

// </editor-fold>

function TangibleAPI() {
	"use strict";
	var appUUID = null, reservedDevices = [], that = this;

	function requestDeviceFromList(listOfDevices, onSuccess, onError, async) {
		var dev = listOfDevices.shift();
		that.reserveDevice(dev.id, onSuccess, function (data) {
			if (listOfDevices.length > 0) {
				requestDeviceFromList(listOfDevices, onSuccess, onError, async);
			} else {
				onError({
					msg : 'no device available for reservation: ' + data.msg
				});
			}
		}, async);
	}

	this.register = function (name, description, onSuccess, onError, async) {
		if (appUUID === null) {
			tangiblePUT("app/registration/",
				{
					appname : name,
					description : description
				},
				function (data) {
					appUUID = data.msg;
					onSuccess(data);
				}, onError, async);
		} else {
			onError({
				msg : 'application already registered'
			});
		}
	};
	this.unregister = function (onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered, impossible to unregister'
			});
		} else {
			tangibleDELETE("app/registration/" + appUUID, {},
				function (data) {
					appUUID = null;
					onSuccess(data);
				}, onError, async);
		}
	};
	this.listDevices = function (onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered!'
			});
		} else {
			tangibleGET(appUUID + "/device/", {}, onSuccess, onError, async);
		}
	};
	this.reserveDevice = function (deviceId, onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered!'
			});
		} else {
			tangiblePUT(appUUID + "/device/reservation/" + deviceId, {},
				function (data) {
					reservedDevices.push(data.msg);
					onSuccess(data);
				}, onError, async);
		}
	};
	this.releaseDevice = function (deviceId, onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered!'
			});
		} else {
			tangibleDELETE(appUUID + "/device/reservation/" + deviceId, {},
				function (data) {
					var idx = -1, i;
					for (i = 0; i < reservedDevices.length && idx === -1; i += 1) {
						if (reservedDevices[i] === data.msg) {
							idx = i;
						}
					}
					if (idx === -1) {
						onError({
							msg : 'internal problem occured during the releasing proccess'
						});
					} else {
						reservedDevices.splice(idx, 1);
						onSuccess(data);
					}
				}, onError, async);
		}
	};
	this.showColor = function (deviceId, color, onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered!'
			});
		} else {
			tangiblePUT(appUUID + "/device_methods/" + deviceId + "/show_color",
				{
					color : color
				}, onSuccess, onError, async);
		}
	};
	this.requestAnyDevice = function (onSuccess, onError, async) {
		if (appUUID === null) {
			onError({
				msg : 'application not registered!'
			});
		} else {
			this.listDevices(
				function (data) {
					requestDeviceFromList(data.msg, onSuccess, onError, async);
				},
				onError,
				async
			);
		}
	};
	this.releaseAllDevices = function (onSuccess, onError, async) {
		if (reservedDevices.length === 0) {
			onSuccess({
				msg : 'all devices released successfully'
			});
		} else {
			this.releaseDevice(reservedDevices.shift(),
				function (data) {
					this.releaseAllDevices(onSuccess, onError, async);
				},
				function (data) {
					onError({
						msg : 'impossible to unrealease all the devices: ' + data.msg
					});
				}, async);
		}
	};
	this.getDeviceId = function (number) {
		return reservedDevices[number - 1].id;
	};
	this.getReservedDevices = function () {
		return reservedDevices;
	};
	this.getAppUUID = function () {
		return appUUID;
	};
}

var tangibleComponent = function () {
  "use strict";
  var instance = (function () {
    //private part
    var api = new TangibleAPI(),
      labeledDevices = [],
      onReadyListener = [],
      ready = false;

    function setReady(bool) {
      ready = bool;
      if (ready) {
        var listener;
        while ((listener = onReadyListener.shift()) !== undefined) {
          listener();
        }
      }
    }
    function initComponent() {
      api.register("tangibleComponent",
        "gateway application to allow SATIN components to use the API",
        function () {
          setReady(true);
        },
        function () {
          console.log("impossible to register the tangibleComponent!");
        });
    }

    return {//public part
      useDevice : function (label, onUsable, onError, deviceProperties, async) {
        if (!ready) {
          onError({msg : "the tangibleComponent is not initialized!"});
          return;
        }
        if (labeledDevices[label] !== undefined) {
          onUsable(labeledDevices[label]);
        } else {
          if (deviceProperties === undefined) {
            api.requestAnyDevice(function (data) {
              labeledDevices[label] = data.msg;
              onUsable(data.msg);
            }, onError, async);
          } else {
            //TODO create a reservation based on the type of devices or on its capacity
            console.log("specifying deviceProperties is not implemented yet!");
          }
        }
      },
      onReadyCallback: function (callbackWhenReady) {
        onReadyListener.push(callbackWhenReady);
      }
    };
  }());

  tangibleComponent = function () {
    return instance;
  };
  return tangibleComponent();
};
