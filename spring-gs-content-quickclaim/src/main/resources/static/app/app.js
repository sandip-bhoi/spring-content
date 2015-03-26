'use strict';

// Declare app level module which depends on views, and components
angular.module('quickClaims', [
  'ngRoute',
  'quickClaims.claims',
  'quickClaims.claim',
  'wozza-filectrl',
//  'angularFileUpload'
//  'myApp.version'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/claims'});
}]);