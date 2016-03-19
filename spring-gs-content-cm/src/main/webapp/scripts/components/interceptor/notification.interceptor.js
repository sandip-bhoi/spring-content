 'use strict';

angular.module('springcmApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-springcmApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-springcmApp-params')});
                }
                return response;
            }
        };
    });
