'use strict';

angular.module('quickClaims.claim', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/claim', {
    templateUrl: 'claim/claim.html',
    controller: 'ClaimCtrl',
    resolve: {
    		claim: function() {
    	   		return {};
       		}
    	}
  	})
  	.when('/editClaim', {
	    templateUrl: 'claim/claim.html',
	    controller: 'ClaimCtrl',
	    resolve: {
	       claim: function($rootScope) {
	    	   return $rootScope.claim;
	       }
	    }
    });
}])

.controller('ClaimCtrl', ['$scope', '$timeout', 'claim', 'ClaimsService', '$location', function($scope, $timeout, claim, ClaimsService, $location) {

	$scope.claim = claim;
	$scope.claimForm;
	
	$scope.images = [];
	$scope.newImages = [];
	$scope.deletedImages = [];

	$scope.onFileSelected = function(element) {
		if (element.files) {
			if (element.files.length >= 1) {
				$timeout(function() {
					$scope.claimForm = element.files[0];
				});
			}
		}
	}
	
	$scope.ok = function() {
		
		ClaimsService.save($scope.claim)
			.then(function(response) {
				$scope.claim = response;
				if ($scope.claimForm)
					return ClaimsService.postContent($scope.claim, "claimForm", $scope.claimForm);
			})
			.then(function(response) {
				$location.path('/claims');
			});
	};
	
}]);