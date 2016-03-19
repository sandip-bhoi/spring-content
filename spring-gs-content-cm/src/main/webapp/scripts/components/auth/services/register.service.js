'use strict';

angular.module('springcmApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


