
var app_url = "http://localhost:8015/FSA/";

  
var baseurl =  app_url;

  String.prototype.replaceAll = function(search, replacement) {
      var target = this;
      return target.split(search).join(replacement);
  };