'use strict';

angular.module('quickClaims.claims', ['ngRoute', 'angularFileUpload'])

.config(['$routeProvider', function($routeProvider, ClaimsService) {
  $routeProvider.when('/claims', {
    templateUrl: 'claims/claims.html',
    controller: 'ClaimsCtrl',
    resolve: {
    	claims: function(ClaimsService) {
    		return ClaimsService.findAll();
    	}
    }
  });
}])

.controller('ClaimsCtrl', ['$scope', '$rootScope', '$location', 'claims', 'ClaimsService', function($scope, $rootScope, $location, claims, ClaimsService) {
	$scope.claims = claims;
	
	$scope.onEditClaim = function(claim) {
		// TODO: yuk, yuk, yuk!
		$rootScope.claim = claim;
		$location.path('/editClaim');
	}
	
	$scope.onDeleteClaim = function(claim) {
		ClaimsService.delete(claim)
		.then(function(response) {
			return ClaimsService.findAll();
		})
		.then(function(response) {
			$scope.claims = response;
		});
	}
}])

.service('ClaimsService', ['$http', '$upload', function($http, $upload) {
	
	this.findAll = function() {
		var findAllPromise = $http({
			method: 'GET',
			url: '/xclaims'
		})
		.then(function(response) {
			if (response.data._embedded)
				return response.data._embedded.xclaims;
			else
				return [];
		})
		return findAllPromise;
	}
	
	this.save = function(claim) {
		
		var method = "POST";
		var url = "/xclaims";
		
		if (claim._links && claim._links.self) {
			method = "PUT";
			var url = claim._links.self.href;
		} 
		
		var savePromise = $http({
			method: method,
			url: url,
			data: claim
		})
		.then(function(response) {
			if (response.status == 201 || response.status == 204) { 
				var claimUrl = response.headers('Location');
				if (claimUrl)
					return $http.get(claimUrl)
						.then(function(response) {
							return response.data;
						});
			} else {
				return response.data;
			}
		});
		return savePromise;
	};
	
	this.postContent = function(claim, contentProperty, contentFile) {
		var contentPromise = $upload.upload({
			url: claim._links.self.href  + '/' + contentProperty,
			method: 'POST',
			file: contentFile
		})
		.then(function(response) {
			var i=0;
		})
		return contentPromise;
	};
	
	this.delete = function(claim) {
		var deletePromise = $http.delete(claim._links.self.href);
		return deletePromise;
	};
	
}]);