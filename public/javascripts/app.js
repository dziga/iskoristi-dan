(function(){
	var app = angular.module('ispuni-dan', ['ui.bootstrap']);
	var activity = [{name:'yeah'}, {name:'dah'}];
	
	app.controller('MainController', ['$http', function($http){
		var main = this;
		
		main.activities = activity;
		$http.get('/activity-types').success(function(data){
			main.activities = data;
		});
	}]);
	
})();