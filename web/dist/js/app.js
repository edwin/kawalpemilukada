(function () {
    var app = angular.module('KawalPemiluKaDaApp', ['controllers', 'mainfooter-directives', 'mainheader-directives', 'mainside-directives']);
    app.service('$KawalService', function () {
        var sendToGa = function () {
            ga('send', 'screenview', {
                'screenName': window.location.hash
            });
        }
        var itemyangsedangdiproses = {
            scope: null,
            user: false,
            dashboard: false,
            wilayah: false,
            komentar: false,
            kandidat:false,
            setScope: function (value) {
                this.scope = value;
            },
            setKandidat: function (value) {
                this.kandidat = value;
                this.scope.sedangDiproses = this.returnvalue();
            },
            setUser: function (value) {
                this.user = value;
                this.scope.sedangDiproses = this.returnvalue();
            },
            setKomentar: function (value) {
                this.komentar = value;
                this.scope.sedangDiproses = this.returnvalue();
            },
            setDashboard: function (value) {
                this.dashboard = value;
                this.scope.sedangDiproses = this.returnvalue();
            },
            setWilayah: function (value) {
                this.wilayah = value;
                this.scope.sedangDiproses = this.returnvalue();
            },
            returnvalue: function () {
                return this.user || this.dashboard || this.wilayah || this.komentar || this.kandidat;
            }
        };
        var replaceSpecial = function (inp, t) {
            var val = inp.replace(/\ /g, t).replace(/\,/g, t).replace(/\`/g, t).replace(/\~/g, t).replace(/\!/g, t).replace(/\@/g, t).replace(/\#/g, t).replace(/\$/g, t).replace(/\%/g, t).replace(/\^/g, t).replace(/\&/g, t).replace(/\*/g, t).replace(/\(/g, t).replace(/\)/g, t).replace(/\+/g, t).replace(/\|/g, t).replace(/\{/g, t).replace(/\}/g, t).replace(/\[/g, t).replace(/\]/g, t).replace(/\:/g, t).replace(/\;/g, t).replace(/\"/g, t).replace(/\'/g, t).replace(/\?/g, t);
            return val;
        };
        var setWindowResize = function ($scope, $window) {
            var topOffset = 50;
            var width = ($window.innerWidth > 0) ? $window.innerWidth : screen.width;
            if (width < 768) {
                $('div.navbar-collapse').addClass('collapse');
                topOffset = 100; // 2-row-menu
            } else {
                $('div.navbar-collapse').removeClass('collapse');
            }
            var height = (($window.innerHeight > 0) ? $window.innerHeight : screen.height) - 1;
            height = height - topOffset;
            if (height < 1)
                height = 1;

            if (height > topOffset) {
                if (height > 700) {
                    $scope.minHeight = height;
                } else {
                    $scope.minHeight = 700;
                }
            }
            return height;
        };
        var setloged = function ($http, user, errorMsg, $scope) {
            var verified = function (user) {
                $scope.user = user;
                $scope.user.logged = true;
                $scope.user.userlevelDesc = "";
                if ($scope.user.userlevel === 100) {
                    $scope.user.userlevelDesc = this.userlevelSelection[0][1];
                }
                if ($scope.user.userlevel === 200) {
                    $scope.user.userlevelDesc = this.userlevelSelection[1][1];
                }
                $scope.selectedTemplate.dropdownuser = "/pages/logout.html";
                $scope.jenisPesans.push("Pesan Untuk Saya");
                var callback = function (data, levelName) {
                    try {
                        angular.element($("#user-profile")).scope().userProfileCtrl[levelName] = data[0];
                    } catch (e) {
                    }
                }
                if (user.provinsiId.length > 0) {
                    getWilayahDropdown($http, $scope, user.provinsiId, callback, "kabkotas");
                }
                if (user.kabkotaId.length > 0) {
                    getWilayahDropdown($http, $scope, user.kabkotaId, callback, "kecamatans");
                }
                if (user.kecamatanId.length > 0) {
                    getWilayahDropdown($http, $scope, user.kecamatanId, callback, "desas");
                }
            };
            if (errorMsg === "Data Anda belum terverifikasi.") {
                $scope.user = user;
                $scope.selectedTemplate.pathmodal = "/pages/verifikasi.html";
                $('#myModal').modal({backdrop: 'static'});
            } else {
                verified(user);
            }
        };

        var openPopupLogin = function ($http, url, $scope, $window) {
            if (!jQuery.browser.mobile) {
                $window.inviteCallback = function (user, dashboard, errorMsg) {
                    $scope.$apply(function () {
                        $scope.panelproprerty = {
                            users: dashboard.users,
                            tasks: dashboard.tasks,
                            comments: dashboard.comments,
                            kandidat: dashboard.kandidat,
                            provinsi: dashboard.provinsi,
                            kabupaten: dashboard.kabupaten,
                            kecamatan: dashboard.kecamatan,
                            desa: dashboard.desa,
                            TPS: dashboard.TPS
                        };
                        setloged($http, user, errorMsg, $scope);
                    });
                };
                var prop = "screenX=0,screenY=0,top=50%,left=50%,resizable=0,toolbar=0,location=0,directories=0," +
                        "addressbar=0,scrollbars=1,status=1,menubar=0,width=450,height=450";
                var modalWin = $window.open(url, "modalWin", prop);
                modalWin.focus();
            } else {
                $window.location = url;
            }
        };

        var getPesans = function ($http, $scope) {
            itemyangsedangdiproses.setKomentar(true);
            $http.post('/getModelData?form_action=getPesan', $scope.data)
                    .success(function (data, status, headers, config) {
                        itemyangsedangdiproses.setKomentar(false);
                        $scope.init();
                        if (data.pesans.length > 0) {
                            $scope.pesans = $scope.pesans.concat(data.pesans);
                        }
                        if (data.tanggapanPesans.length > 0) {
                            $scope.pesans[$scope.data[6]].tanggapanPesans = $scope.pesans[$scope.data[6]].tanggapanPesans.concat(data.tanggapanPesans);
                        }
                        if (data.setujuPesans.length > 0) {
                            $scope.pesans[$scope.data[6]].setujuPesans = $scope.pesans[$scope.data[6]].setujuPesans.concat(data.setujuPesans);
                        }
                        if (data.tidakSetujuPesans.length > 0) {
                            $scope.pesans[$scope.data[6]].tidakSetujuPesans = $scope.pesans[$scope.data[6]].tidakSetujuPesans.concat(data.tidakSetujuPesans);
                        }
                        $scope.cursorStr = data.cursorStr;
                    })
                    .error(function (data, status, headers, config) {

                    });
        };

        var submitMsg = function ($http, $scope, $index) {
            itemyangsedangdiproses.setKomentar(true);
            $http.post('/getModelData?form_action=setPesan', $scope.data)
                    .success(function (data, status, headers, config) {
                        itemyangsedangdiproses.setKomentar(false);
                        $scope.init();
                        if (data.pesan) {
                            $scope.pesans.unshift(data.pesan);
                        }
                        if (data.parentPesan) {
                            $scope.pesans[$index].jumlahTanggapan = data.parentPesan.jumlahTanggapan;
                            $scope.pesans[$index].jumlahSetuju = data.parentPesan.jumlahSetuju;
                            $scope.pesans[$index].jumlahTidakSetuju = data.parentPesan.jumlahTidakSetuju;
                            $scope.pesans[$index].tanggapan = "";
                        }
                        if (data.tanggapanPesan) {
                            $scope.pesans[$index].tanggapanPesans.unshift(data.tanggapanPesan);
                            $scope.pesans[$index].tanggapanPesanShow = true;
                        }
                        if ($scope.pesans[$index].jumlahSetuju > 0) {
                            $scope.pesans[$index].setujuPesans = data.setujuPesans;
                        } else {
                            $scope.pesans[$index].setujuPesans = [];
                        }
                        if ($scope.pesans[$index].jumlahTidakSetuju > 0) {
                            $scope.pesans[$index].tidakSetujuPesans = data.tidakSetujuPesans;
                        } else {
                            $scope.pesans[$index].tidakSetujuPesans = [];
                        }
                    })
                    .error(function (data, status, headers, config) {

                    });
        };
        var getUrlFile = function ($http, $scope) {
            itemyangsedangdiproses.setKomentar(true);
            $http.get('/getModelData?form_action=getUrlFile').success(function (data) {
                if (data.uploadurl.length > 0) {
                    submitFile($http, $scope, data.uploadurl);
                }
            }).error(function () {
            });
        };
        var submitFile = function ($http, $scope, uploadurl) {
            itemyangsedangdiproses.setKomentar(true);
            var data = new FormData();
            var files = $scope.photos;
            var pI = 0;
            for (var i = 0, f; f = files[i]; i++) {
                if (!f.type.match('image.*')) {
                    continue;
                }
                data.append("pN" + i, f.name);
                data.append("pT" + i, f.type);
                data.append("pF" + i, f);
                pI = i + 1;
            }
            data.append("pI", pI);
            var fI = 0;
            var files2 = $scope.files;
            for (var i = 0, f2; f2 = files2[i]; i++) {
                if (!f2.type.match('application/pdf')) {
                    continue;
                }
                data.append("fN" + i, f2.name);
                data.append("fT" + i, f2.type);
                data.append("fF" + i, f2);
                fI = i + 1;
            }
            data.append("fI", fI);

            $http.post(uploadurl, data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                    .success(function (data, status, headers, config) {
                        if (data.success === "OK") {
                            $scope.data.push(data.retval);
                            submitMsg($http, $scope)
                        }
                    })
                    .error(function (data, status, headers, config) {

                    });
        };
        var getDashboard = function ($http, $scope) {
            itemyangsedangdiproses.setDashboard(true);
            $http.get('/getModelData?form_action=getDashboard&tahun=' + $scope.$tahun).success(function (data) {
                $scope.panelproprerty = {
                    users: data.dashboard.users,
                    tasks: data.dashboard.tasks,
                    comments: data.dashboard.comments,
                    kandidat: data.dashboard.kandidat,
                    provinsi: data.dashboard.provinsi,
                    kabupaten: data.dashboard.kabupaten,
                    kecamatan: data.dashboard.kecamatan,
                    desa: data.dashboard.desa,
                    TPS: data.dashboard.TPS
                };
                itemyangsedangdiproses.setDashboard(false);
            }).error(function (data) {
                /*setTimeout(function () {
                 location.reload()
                 }, 3000);*/
            });
        };
        var getUser = function ($http, $scope) {
            itemyangsedangdiproses.setDashboard(true);
            $http.get('/getModelData?form_action=getNumberUser&tahun=' + $scope.$tahun).success(function (data) {
                $scope.panelproprerty = {
                    users: data.dashboard.users,
                    tasks: data.dashboard.tasks,
                    comments: data.dashboard.comments,
                    kandidat: data.dashboard.kandidat,
                    provinsi: data.dashboard.provinsi,
                    kabupaten: data.dashboard.kabupaten,
                    kecamatan: data.dashboard.kecamatan,
                    desa: data.dashboard.desa,
                    TPS: data.dashboard.TPS
                };
                itemyangsedangdiproses.setDashboard(false);
            }).error(function (data) {
                /*setTimeout(function () {
                 location.reload()
                 }, 3000);*/
            });
        };
        

        var cekauth = function ($http, $scope) {
            itemyangsedangdiproses.setUser(true);
            $http.get('/login?form_action=cekauth').success(function (data) {
                if (data.status === "terverifikasi") {
                    setloged($http, data.user, data.status, $scope);
                } else if (data.status === "Data Anda belum terverifikasi.") {
                    setloged($http, data.user, data.status, $scope);
                } else if (data.status === "belum login") {
                    $scope.selectedTemplate.dropdownuser = "/pages/login.html";
                }
                itemyangsedangdiproses.setUser(false);
            }).error(function (data) {
                /*setTimeout(function () {
                 location.reload()
                 }, 3000);*/
            });
        };

        var getWilayahDropdown = function ($http, $scope, filter, callback, levelName) {
            itemyangsedangdiproses.setWilayah(true);
            $http.get('/wilayah/2014/' + filter).success(function (data) {
                callback(data, levelName);
                itemyangsedangdiproses.setWilayah(false);
            }).error(function (data) {

            });
        };

        var getWilayah = function ($http, $scope, $urlFilter, $callback, $i) {
            $scope.wilayahs = [];
            itemyangsedangdiproses.setWilayah(true);
            $http.get('/wilayah/' + $urlFilter).success(function (data) {
                $callback(data, $i);
                itemyangsedangdiproses.setWilayah(false);
            }).error(function (data) {
                /*setTimeout(function () {
                 location.reload()
                 }, 3000);*/
            });
        };

        var handleHash = function (hash, $scope) {
            if (hash.length > 0) {
                hash = replaceSpecial(hash, "/");
                hash = hash.replace("//", "/");
                window.location.hash = "#" + hash;
                var hashs = hash.substr(1).split("/");
                if ($scope.pages.indexOf(hashs[0]) >= 0) {
                    $scope.selectedTemplate.path = "/pages/" + hashs[0];
                }
            }
        };

        return {
            openPopupLogin: openPopupLogin
            , setloged: setloged
            , setWindowResize: setWindowResize
            , getDashboard: getDashboard
            , cekauth: cekauth
            , replaceSpecial: replaceSpecial
            , getWilayah: getWilayah
            , handleHash: handleHash
            , itemyangsedangdiproses: itemyangsedangdiproses
            , getUrlFile: getUrlFile
            , submitMsg: submitMsg
            , submitFile: submitFile
            , getPesans: getPesans
            , getWilayahDropdown: getWilayahDropdown
            , getUser: getUser
            , sendToGa: sendToGa
        };
    });



    app.controller('KawalPemiluKaDaCtrl', ['$scope', '$KawalService', function ($scope, $KawalService) {
            if (window.location.protocol === "http:"){
                //window.location.href = "https:" + window.location.href.substring(window.location.protocol.length);
            }
            $KawalService.itemyangsedangdiproses.setScope($scope);
            $scope.sedangDiproses = $KawalService.itemyangsedangdiproses.returnvalue();
            $scope.tahuns = [2014, 2015];
            $scope.jenisPesans = ["Pesan Untuk Semua"];
            $scope.$tahun = 2015;
            $scope.user = {
                logged: false,
                userlevelDesc: "",
                userlevel:0
            };
            $scope.minHeight = 100;
            $scope.pages = ["dashboard.html", "wilayah.html", "kandidat.html",
                "user-profile.html",
                "tabulasi.html",
                "komentar.html",
                "grafik.html",
                "task.html", "users.html", "privasi.html"];
            $scope.selectedTemplate = {
                "hash": "#/dashboard.html",
                "path": "/pages/dashboard.html",
                "dropdownuser": "/pages/kosong.html",
                "pathmodal": "/pages/kosong.html"
            };
            $scope.panelproprerty = {
                users: 0,
                tasks: 0,
                comments: 0,
                kandidat: 0,
                provinsi: 0,
                kabupaten: 0,
                kecamatan: 0,
                desa: 0,
                TPS: 0
            };

            $scope.$watch(function () {
                return location.hash;
            }, function (value) {
                $scope.selectedTemplate.hash = value;
                $KawalService.handleHash(value.substr(1), $scope);
                $KawalService.sendToGa();
            });
        }]);
})();

