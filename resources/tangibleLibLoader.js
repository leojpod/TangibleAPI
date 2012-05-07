/*  by /Leoj -- /Lekko -- /Lojeuv
 *
 */


//this file just make sure that the jQuery lib is loaded and then load the tangibleLib
var jQueryScriptOutputted = false;
function initJQuery() {
  //if the jQuery object isn't available
  if (typeof(jQuery) == 'undefined') {
    if (! jQueryScriptOutputted) {
      //only output the script once..
      jQueryScriptOutputted = true;

      //output the script (load it from google api)
      document.write("<scr"+"ipt type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js\"></scr" + "ipt>");
    }
    setTimeout("initJQuery()", 0);
  } else {

    $(function() {
      $.getScript("http://localhost:9998/resources/tangibleLib.js");
    });
  }

}
initJQuery();