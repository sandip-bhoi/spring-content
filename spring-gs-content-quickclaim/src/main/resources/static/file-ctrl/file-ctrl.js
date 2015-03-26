'use strict';

angular.module('wozza-filectrl', [])

	.controller('FileCtrlController', ['$scope', '$location', 
	    
	 function($scope, $location, $modal) {
		
		$scope.debug=false;
		
		$scope.totalWidth = Math.round((document.getElementById('table').offsetWidth));

		$scope.border=2;
		$scope.margin=2;
		$scope.width=75;

		$scope.newImages = [];
		$scope.deletedImages = [];
		
		$scope.getImageUrl = function(image) {
			if (!image) throw "FileCtrlController::getImageUrl; image must be defined";
			if (image.src) return image.src;
			else if (image.dataUrl) return image.dataUrl;
		};
		 	
		$scope.deleteImage = function(image, images) {
			var idx = -1;
			var deleted;
			for (var i=0; i<images.length; i++) {
				if ($scope.getImageUrl(images[i]) == $scope.getImageUrl(image)) {
					deleted = images[i];
					idx = i;
					break;
				}
			}
			if (idx != -1) {
				images.splice(idx, 1);
			}
			return deleted;
		};
		
    }])
    
	.directive('fileCtrl', function() {
		return {
            restrict : 'E',
            controller : 'FileCtrlController',
            scope: {
            	images: '=images',
            	deletedImages: '=deletedImages',
            	newImages: '=newImages',
            	editable: '=editable',
            	debug: '=debug'
            },
            transclude: true,
            templateUrl: 'file-ctrl/file-ctrl.html',
            link:function(scope, element, attrs) {
            
            	var dropzone = element.find('#drop-zone');
            	
		        var checkSize, isTypeValid, processDragOverOrEnter, validMimeTypes;
		        processDragOverOrEnter = function(event) {
		          dropzone.addClass("over");
		          if (event != null) {
		            event.preventDefault();
		          }
		          event.originalEvent.dataTransfer.effectAllowed = 'copy';
		          return false;
		        };
		        validMimeTypes = attrs.fileDropzone;
		        checkSize = function(size) {
		          var _ref;
		          if (((_ref = attrs.maxFileSize) === (void 0) || _ref === '') || (size / 1024) / 1024 < attrs.maxFileSize) {
		            return true;
		          } else {
		            alert("File must be smaller than " + attrs.maxFileSize + " MB");
		            return false;
		          }
		        };
		        isTypeValid = function(type) {
		          if ((validMimeTypes === (void 0) || validMimeTypes === '') || validMimeTypes.indexOf(type) > -1) {
		            return true;
		          } else {
		            alert("Invalid file type.  File must be one of following types " + validMimeTypes);
		            return false;
		          }
		        };
		        dropzone.bind('dragover', processDragOverOrEnter);
		        dropzone.bind('dragenter', processDragOverOrEnter);
		        //dropzone.bind('dragleave', processDragLeaveOrEnd);
		        //dropzone.bind('dragend', processDragLeaveOrEnd);

		        return dropzone.bind('drop', function(event) {
		        	var file, name, reader, size, type;
		        	dropzone.removeClass("over");
		        	if (event != null) {
		        		event.preventDefault();
		        	}
		        	reader = new FileReader();
		        	reader.onload = function(evt) {
		        		if (checkSize(size) && isTypeValid(type)) {
		        			return scope.$apply(function() {
		        				file.dataUrl = evt.target.result;
		        			});
		        		}
		          };
		          file = event.originalEvent.dataTransfer.files[0];
		          scope.newImages.push(file);
		          reader.readAsDataURL(file);
		          return false;
		        });            
            }
		};
	});