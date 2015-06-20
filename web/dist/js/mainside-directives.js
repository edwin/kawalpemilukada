(function () {
    var app = angular.module('mainside-directives', []);
    app.directive("mainSide", function () {
        return {
            restrict: 'E',
            templateUrl: "../pages/main-side.html",
            controller: function ($scope, $compile, $window, $KawalService) {

                $(window).on("resize.doResize", function () {
                    var height = $KawalService.setWindowResize($scope, $window);

                    $scope.$apply(function () {
                        if (height > 700) {
                            $scope.minHeight = height;
                        } else {
                            $scope.minHeight = 700;
                        }
                        //do something to update current scope based on the new innerWidth and let angular update the view.
                    });
                });
                $scope.$on("$destroy", function () {
                    $(window).off("resize.doResize"); //remove the handler added earlier
                });
                $KawalService.setWindowResize($scope, $window);
                this.setPage = function (page) {
                    $scope.selectedTemplate.hash=page;
                    $KawalService.handleHash(page.substr(1), $scope);
                };
                this.isSelected=function(page){
                    return $scope.selectedTemplate.hash  === page;
                }
                $('#side-menu').metisMenu();
            },
            controllerAs: "mainSide"
        };
    });
})();
