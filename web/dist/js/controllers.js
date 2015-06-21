(function () {
    var app = angular.module('controllers', []);
    app.controller('kandidatController', ['$scope', '$http', '$KawalService', function ($scope, $http, $KawalService) {
            $KawalService.sendToGa();
            this.showAll = false;
            this.tingkat = "";
            this.errorAlerts = [];
            this.successAlerts = [];
            this.showAddNewCandidate = false;
            this.showKabupaten = false;
            this.submitShow = true;
            this.wilayahs = [];
            this.wilayah = "";
            this.isAdmin=function(user){
                if (user.userlevel>=1000){
                    return true;
                }else{
                    return false;
                }
            }
            var callback = function (data, levelName) {
                context[levelName] = data[0];
            };
            this.kandidat = {
                nama: "",
                tingkat: "",
                tingkatId: "",
                provinsiId: "",
                provinsi: "",
                kabupatenId: "",
                kabupaten: ""
            }
            this.setTahun = function (selected) {
                $scope.$parent.$parent.$tahun = selected.tahun;
                this.getData();
            };
            this.showForm = function (selected) {
                selected.errorAlerts = [];
                selected.successAlerts = [];
                var hashs = window.location.hash.substr(2).split("/");
                selected.kandidat = {
                    nama: "",
                    tingkatId: "",
                    tingkat: hashs[hashs.length - 1],
                    provinsiId: "",
                    provinsi: "",
                    kabupatenId: "",
                    kabupaten: ""
                }
                selected.showAddNewCandidate = !selected.showAddNewCandidate;
            }
            this.provinsis = [];
            this.kandidats=[];
            this.setWilayah = function (scope, selected) {
                scope.wilayah = selected.nama + " | Jumlah Kandidat: " + selected.JumlahKandidat;
                $KawalService.itemyangsedangdiproses.setKandidat(true);
                scope.kandidats=[];
                $http.get('/kandidat/get/' + $scope.$parent.$parent.$tahun + '/' + scope.kandidat.tingkat + '/' + selected.kpuid).
                        success(function (data, status, headers, config) {
                            if (data.length>0){
                                scope.kandidats=data[0]
                            }
                            $KawalService.itemyangsedangdiproses.setKandidat(false);
                        }).
                        error(function (data, status, headers, config) {
                        });
            }
            this.getData = function () {
                if (window.location.hash.substr(window.location.hash.length - 1) === "/") {
                    window.location.hash = window.location.hash.substr(0, window.location.hash.length - 1);
                }
                var hashs = window.location.hash.substr(2).split("/");
                if (hashs[0] !== "kandidat.html") {
                    return;
                }
                this.kandidat = {
                    nama: "",
                    tingkatId: "",
                    tingkat: hashs[hashs.length - 1],
                    provinsiId: "",
                    provinsi: "",
                    kabupatenId: "",
                    kabupaten: ""
                }
                if (this.kandidat.tingkat.toLowerCase() === "kabupatenkota") {
                    this.showKabupaten = true;
                } else {
                    this.showKabupaten = false;
                }
                this.wilayahs=[];
                this.kandidats=[];
                this.wilayah="";
                $KawalService.getWilayahDropdown($http, this, "0", callback, "provinsis");
                $KawalService.itemyangsedangdiproses.setKandidat(true);
                var context = this;
                $http.get('/kandidat/get/2014/' + this.kandidat.tingkat).
                        success(function (data, status, headers, config) {
                            if (data.length > 0) {
                                context.wilayahs = data[0];
                                if (context.wilayahs.length > 0) {
                                    context.setWilayah(context, context.wilayahs[0]);
                                }
                            }
                            context.showAll = true;
                            $KawalService.itemyangsedangdiproses.setKandidat(false);
                        }).
                        error(function (data, status, headers, config) {
                            context.showAll = true;
                        });


            }
            this.dosubmit = function (user, selected) {
                selected.errorAlerts = [];
                selected.successAlerts = [];
                if (selected.kandidat.nama.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Isi Nama Kandidat"});
                    return;
                }
                if (selected.kandidat.provinsiId.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Pilih Provinsi"});
                    return;
                }
                if (selected.kandidat.tingkat.toLowerCase() === "kabupatenkota") {
                    selected.kandidat.tingkatId = selected.kandidat.kabupatenId;
                } else {
                    selected.kandidat.tingkatId = selected.kandidat.provinsiId;
                }
                $KawalService.itemyangsedangdiproses.setKandidat(true);
                selected.submitShow = false;
                $http.post('/kandidat/post/2014/' + selected.kandidat.tingkat + '/' + selected.kandidat.tingkatId, selected.kandidat).
                        success(function (data, status, headers, config) {
                            if (data.length > 0) {
                                selected.wilayahs = data[0];
                                if (selected.wilayahs.length > 0) {
                                    selected.setWilayah(selected, selected.wilayahs[0]);
                                }
                            }
                            selected.submitShow = true;
                            selected.successAlerts.push({"text": "Perubahan Data sudah berhasil disimpan, terima kasih atas kerjasamanya."});
                            $KawalService.itemyangsedangdiproses.setKandidat(false);
                            var hashs = window.location.hash.substr(2).split("/");
                            selected.kandidat = {
                                nama: "",
                                tingkatId: "",
                                tingkat: hashs[hashs.length - 1],
                                provinsiId: "",
                                provinsi: "",
                                kabupatenId: "",
                                kabupaten: ""
                            }
                        }).
                        error(function (data, status, headers, config) {
                            selected.submitShow = true;
                        });
            }
            this.setProvinsi = function (selected) {
                this.kandidat.provinsiId = selected.kpuid;
                this.kandidat.provinsi = selected.nama;
                if (this.kandidat.tingkat.toLowerCase() === "kabupatenkota") {
                    $KawalService.getWilayahDropdown($http, this, selected.kpuid, callback, "kabkotas");
                }
            }
            this.setKabupaten = function (selected) {
                this.kandidat.kabupatenId = selected.kpuid;
                this.kandidat.kabupaten = selected.nama;
            }

            var context = this;
            $scope.$watch(function () {
                return location.hash;
            }, function (value) {
                context.getData();
            });
        }]);
    app.controller('wilayahController', ['$http', '$scope', '$KawalService', function ($http, $scope, $KawalService) {
            this.blmadaData = false;
            this.map = L.map('map').setView([-2, 118], 4.4);
            this.nkri = L.tileLayer.wms("http://geoserver.apps.kawaldesa.id/geoserver/BatasWilayah/wms", {
                layers: 'BatasWilayah:propinsi_shp',
                format: 'image/png',
                transparent: true,
                attribution: "<a href='http://geoserver.apps.kawaldesa.id/geoserver/web/?wicket:bookmarkablePage=:org.geoserver.web.demo.MapPreviewPage' target='_kawaldesa'>geoserver.apps.kawaldesa.id</a>"
            }).addTo(this.map);
            this.kabupaten = null;
            // Disable drag and zoom handlers.
            //this.map.dragging.disable();
            this.map.touchZoom.disable();
            //this.map.doubleClickZoom.disable();
            this.map.scrollWheelZoom.disable();
            this.controlWilayahs = [
                {id: 1, kpuid: "0", nama: "Nasional", tingkat: "Nasional", showdiv: false}
            ];
            this.wilayahs = [];
            this.provinsis = [];
            this.kabkotas = [];
            this.kecamatans = [];
            this.desas = [];
            this.getData = function () {
                if (window.location.hash.substr(window.location.hash.length - 1) === "/") {
                    window.location.hash = window.location.hash.substr(0, window.location.hash.length - 1);
                }
                var hashs = window.location.hash.substr(2).split("/");
                if (hashs[0] !== "wilayah.html") {
                    return;
                }
                var context = this;
                if (hashs.length <= 1) {
                    $KawalService.handleHash(window.location.hash.substr(1) + "/0", $scope.$parent.$parent);
                    hashs.push("0");
                }
                if (this.controlWilayahs.length > hashs.length - 1) {
                    this.controlWilayahs.splice(hashs.length - 1, (this.controlWilayahs.length));
                }
                if (hashs.length > 2) {
                    for (var i = this.controlWilayahs.length + 1; i < hashs.length; i++) {
                        var parentId = hashs[i - 1];
                        var kpuid = hashs[i];
                        var urlFilter = $scope.$parent.$parent.$tahun + "/kpuid/" + parentId + "/" + kpuid;
                        var callback = function (data, id) {
                            if (data.length > 0) {
                                data = data[0];
                                if (data.length > 0) {
                                    data = data[0];
                                    data.id = id;
                                    data.showdiv = true;
                                    context.controlWilayahs.push(data);
                                }
                            }
                        }
                        $KawalService.getWilayah($http, this, urlFilter, callback, i);
                    }
                }
                var parentId = hashs[hashs.length - 1];
                var urlFilter = $scope.$parent.$parent.$tahun + "/" + parentId;
                var callback = function (data) {
                    if (data.length > 0) {
                        if (data[0].length > 0) {
                            var sortid = function (a, b) {
                                return (parseInt(a.kpuid) - parseInt(b.kpuid));
                            }
                            context.wilayahs = data[0].sort(sortid);
                            context.blmadaData = false;
                        } else {
                            context.blmadaData = true;
                        }
                    }
                }
                $KawalService.getWilayah($http, this, urlFilter, callback);
                if (parentId === "0") {
                    this.map.eachLayer(function (layer) {
                        if (layer.options.layers !== "BatasWilayah:propinsi_shp") {
                            context.map.removeLayer(layer);
                        }
                    });
                    this.map.setView([-2, 118], 4.4);
                } else {
                    try {
                        context.map.removeLayer(context.kabupaten);
                    } catch (e) {
                    }
                    context.kabupaten = L.tileLayer.wms("http://geoserver.apps.kawaldesa.id/geoserver/BatasWilayah/wms", {
                        layers: 'BatasWilayah:kabupaten_shp',
                        format: 'image/png',
                        transparent: true,
                        cql_filter: "(kpu_prop_id='" + parentId + "')"
                    }).addTo(this.map);
                }
                $KawalService.sendToGa();
            };
            this.setTahun = function (selected) {
                $scope.$parent.$parent.$tahun = selected.tahun;
                this.getData();
            };
            this.getChild = function (wilayah) {
                $KawalService.handleHash(window.location.hash.substr(1) + "/" + wilayah.kpuid, $scope.$parent.$parent);
            };
            this.setPage = function (controlWilayah, $index) {
                if (this.controlWilayahs.length > $index + 1 && this.controlWilayahs.length > 1) {
                    this.controlWilayahs.splice($index + 1, (this.controlWilayahs.length));
                }
                var urlfilter = "";
                for (var i = 0; i < this.controlWilayahs.length; i++) {
                    urlfilter = urlfilter + "/" + this.controlWilayahs[i].kpuid;
                }
                $KawalService.handleHash("/wilayah.html" + "/" + urlfilter, $scope.$parent.$parent);
            };
            this.getChildMap = function (selected) {
                var hashs = window.location.hash.substr(2).replace("wilayah.html/", "");
                if (hashs.length === 1) {
                    try {
                        this.map.removeLayer($scope.kabupaten);
                    } catch (e) {
                    }
                    this.kabupaten = L.tileLayer.wms("http://geoserver.apps.kawaldesa.id/geoserver/BatasWilayah/wms", {
                        layers: 'BatasWilayah:kabupaten_shp',
                        format: 'image/png',
                        transparent: true,
                        cql_filter: "(kpu_prop_id='" + selected.kpuid + "')"
                    }).addTo(this.map);
                }
            };
            this.clearChildMap = function () {
                var hashs = window.location.hash.substr(2).replace("wilayah.html/", "");
                if (hashs.length === 1) {
                    try {
                        this.map.removeLayer(this.kabupaten);
                    } catch (e) {
                    }
                }
            };
            var context = this;
            $scope.$watch(function () {
                return location.hash;
            }, function (value) {
                context.getData();
            });
        }]);
    app.controller('dashboardController', ['$scope', '$http', '$KawalService', function ($scope, $http, $KawalService) {
            this.setTahun = function (selected) {
                $scope.$parent.$parent.$tahun = selected.tahun;
                $KawalService.getDashboard($http, $scope);
            };
            this.getUser = function () {
                if ($scope.user.userlevel >= 1000) {
                    $scope.panelproprerty.users = "...";
                    $KawalService.getUser($http, $scope);
                }
            };
            $KawalService.getDashboard($http, $scope);
            $KawalService.sendToGa();
        }]);
    app.controller('UserController', ['$scope', '$window', '$http', '$KawalService', function ($scope, $window, $http, $KawalService) {
            this.login = function (url) {
                var rurl = encodeURIComponent(window.location.hash.substr(1));
                $KawalService.openPopupLogin($http, url + rurl + "&tahun=" + $scope.$parent.$tahun, $scope.$parent, $window)
            };
            $KawalService.sendToGa();
        }]);
    app.controller('userProfileController', ['$scope', '$window', '$http', '$KawalService', function ($scope, $window, $http, $KawalService) {
            this.submitShow = true;
            this.userlevelSelection = [[100, "Mengisi Data Suara dan mengupload Foto C1 dari TPS ke kawalpemilukada.org"], [200, "Memverifikasi Data dengan Scan C1 dari http://kpu.go.id/"]];
            this.setUserlevelSelection = function (selected) {
                $scope.$parent.$parent.user.userlevel = selected[0];
                $scope.$parent.$parent.user.userlevelDesc = selected[1];
            }
            this.errorAlerts = [];
            this.successAlerts = [];
            this.login = function (url) {
                var rurl = encodeURIComponent(window.location.hash.substr(1));
                $KawalService.openPopupLogin($http, url + rurl + "&tahun=" + $scope.$parent.$parent.$tahun, $scope.$parent.$parent, $window)
            };
            var callback = function (data, levelName) {
                context[levelName] = data[0];
            };
            this.setProvinsi = function (provinsi) {
                $scope.$parent.$parent.user.provinsi = provinsi.nama;
                $scope.$parent.$parent.user.provinsiId = provinsi.kpuid;
                $scope.$parent.$parent.user.kabkota = "";
                $scope.$parent.$parent.user.kabkotaId = "";

                $scope.$parent.$parent.user.kecamatan = "";
                $scope.$parent.$parent.user.kecamatanId = "";

                $scope.$parent.$parent.user.desa = "";
                $scope.$parent.$parent.user.desId = "";

                $KawalService.getWilayahDropdown($http, this, provinsi.kpuid, callback, "kabkotas");
            }
            this.setKabkota = function (kabkota) {
                $scope.$parent.$parent.user.kabkota = kabkota.nama;
                $scope.$parent.$parent.user.kabkotaId = kabkota.kpuid;
                $scope.$parent.$parent.user.kecamatan = "";
                $scope.$parent.$parent.user.kecamatanId = "";

                $scope.$parent.$parent.user.desa = "";
                $scope.$parent.$parent.user.desId = "";
                $KawalService.getWilayahDropdown($http, this, kabkota.kpuid, callback, "kecamatans");
            }
            this.setKecamatan = function (kecamatan) {
                $scope.$parent.$parent.user.kecamatan = kecamatan.nama;
                $scope.$parent.$parent.user.kecamatanId = kecamatan.kpuid;
                $scope.$parent.$parent.user.desa = "";
                $scope.$parent.$parent.user.desId = "";
                $KawalService.getWilayahDropdown($http, this, kecamatan.kpuid, callback, "desas");
            }
            this.setDesa = function (desa) {
                $scope.$parent.$parent.user.desa = desa.nama;
                $scope.$parent.$parent.user.desId = desa.kpuid;
            }
            this.dosubmit = function (user, selected) {
                selected.errorAlerts = [];
                selected.successAlerts = [];
                if (user.email.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Isi Email Anda"});
                    return;
                }
                if (user.nokontak.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Isi No Kontak Anda"});
                    return;
                }
                if (("" + user.userlevel).length < 0) {
                    selected.errorAlerts.push({"text": "Silahakan Pilih 'Bersedia menjadi Relawan untuk'"});
                    return;
                }
                if (user.provinsi.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Pilih Provinsi Anda"});
                    return;
                }
                if (user.kabkota.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Pilih Kabupaten / Kota Anda"});
                    return;
                }
                if (user.kecamatan.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Pilih Kecamatan Anda"});
                    return;
                }
                if (user.desa.replace(" ", "") === "") {
                    selected.errorAlerts.push({"text": "Silahakan Pilih Kelurahan / Desa Anda"});
                    return;
                }

                selected.submitShow = false;
                $KawalService.itemyangsedangdiproses.setUser(true);
                $http.post('/getModelData?form_action=updateUser', user).
                        success(function (data, status, headers, config) {
                            user = data.user;
                            selected.submitShow = true;
                            selected.successAlerts.push({"text": "Perubahan Data sudah berhasil disimpan, terima kasih atas kerjasamanya."});
                            $KawalService.itemyangsedangdiproses.setUser(false);
                        }).
                        error(function (data, status, headers, config) {
                            selected.submitShow = true;
                        });

            };
            this.reset = function () {
                location.reload();
            };
            this.provinsis = [];
            this.kabkotas = [];
            this.kecamatans = [];
            this.desas = [];
            var context = this;
            $KawalService.getWilayahDropdown($http, this, "0", callback, "provinsis");
            $KawalService.sendToGa();
        }]);
    app.controller('verifiaksiController', ['$http', '$scope', '$KawalService', function ($http, $scope, $KawalService) {
            this.sedangprocess = false;
            this.verifiaksi = {"NIK": "", "NAMA": ""};
            this.errorAlerts = [];
            this.successAlerts = [];
            this.sosial = "";
            this.submitShow = true;
            switch ($scope.$parent.user.type) {
                case "fb":
                    this.sosial = "Facebook";
                    break;
                case "twit":
                    this.sosial = "Twitter";
                    break;
            }
            this.doverifiaksi = function () {
                this.errorAlerts = [];
                this.successAlerts = [];
                if (this.verifiaksi.NIK.replace(" ", "") === "") {
                    this.errorAlerts.push({"text": "Silahakan Isi NIK anda"});
                    return;
                }
                if (this.verifiaksi.NAMA.replace(" ", "") === "") {
                    this.errorAlerts.push({"text": "Silahakan Isi Nama anda"});
                    return;
                }
                this.submitShow = false;
                this.sedangprocess = true;
                var context = this;
                $http.post('/login?form_action=verifikasi', [this.verifiaksi.NIK, this.verifiaksi.NAMA]).
                        success(function (data, status, headers, config) {
                            context.sedangprocess = false;
                            var getFloat = function (input) {
                                if (isNaN(input)) {
                                    return -1;
                                }
                                return parseFloat(input);
                            }
                            if (getFloat(data.status) > 60) {
                                context.successAlerts.push({"text": context.verifiaksi.NAMA + " memiliki " + data.status + "% kecocokan dengan nama di " + context.sosial + " Anda yaitu: " + $scope.user.nama});
                                $KawalService.setloged($http, data.user, "", $scope);
                                $scope.selectedTemplate.closeModal = "/pages/closeModal.html";
                            } else if (getFloat(data.status) >= 0 && getFloat(data.status) <= 60) {
                                context.errorAlerts.push({"text": context.verifiaksi.NAMA + " memiliki " + data.status + "% kecocokan dengan nama di " + context.sosial + " Anda yaitu: " + $scope.user.nama});
                                context.errorAlerts.push({"text": "Tingkat kecocokan harus diatas 60%. Silahkan rubah nama di " + context.sosial + " Anda."});
                                context.submitShow = true;
                            } else {
                                context.errorAlerts.push({"text": data.status});
                                context.submitShow = true;
                            }
                            if (getFloat(data.status) >= 0) {
                                for (var i = 0; i < data.matchs.length; i++) {
                                    var obj = data.matchs[i];
                                    context.successAlerts.push({"text": obj});
                                }
                            }
                            if (getFloat(data.status) > 60) {
                                context.errorAlerts.push({"text": "Jangan rubah nama di " + context.sosial + " Anda. Karena setiap kali anda Login, Nama yang tertera di " + context.sosial + " Anda akan dibandingkan dengan dengan nama di Sistem KawalPemiluKaDa.org"});
                            }
                        }).
                        error(function (data, status, headers, config) {
                            context.submitShow = true;
                            context.sedangprocess = false;
                        });

                //this.verifiaksi = {};
            }
            $KawalService.sendToGa();
        }]);

    app.controller('komentarController', ['$http', '$scope', '$KawalService', function ($http, $scope, $KawalService) {
            this.limit = 20;
            this.offset = 0;
            this.showError = false;
            this.showFile = false;
            this.showPhoto = false;
            this.isi = "";
            this.photos = "";
            this.photosname = "";
            this.files = "";
            this.filename = "";
            this.cursorStr = "";
            this.filter = "";
            this.filterBy = "";
            this.init = function () {
                this.showError = false;
                this.showFile = false;
                this.showPhoto = false;
                this.isi = "";
                this.photos = [];
                this.photosname = "";
                this.files = [];
                this.filename = "";
            };
            this.pesans = [];
            this.pesan = "Pesan Untuk Semua";
            this.setJenisKomentar = function (selected) {
                this.pesan = selected.jenisPesan;
            };
            this.pesanInitialization = function (selected, pesan, $index) {
                pesan.imageUrl = "";
                pesan.foundImageUrl = false;
                pesan.fileUrl = "";
                pesan.foundFileUrl = false;
                pesan.showTanggapi = false;
                pesan.showTanggapiError = false;
                pesan.fileName = "";
                pesan.tanggapan = "";
                pesan.tanggapanPesans = [];
                pesan.tanggapanPesanShow = false;
                pesan.setujuPesans = [];
                pesan.setujuPesanShow = false;
                pesan.tidakSetujuPesans = [];
                pesan.tidakSetujuPesanShow = false;
                pesan.blockbutton_active = "";
                angular.forEach(pesan.files, function (value, key) {
                    var file = angular.fromJson(value);
                    if (file[1].toString().indexOf("image") >= 0) {
                        pesan.imageUrl = file[3];
                        pesan.foundImageUrl = true;
                    } else {
                        pesan.fileUrl = file[3]
                        pesan.foundFileUrl = true;
                        pesan.fileName = file[0];
                    }
                });
                selected.data = ["#tanggapan#" + pesan.id, "", "", "", this.limit, 0, $index];
                $KawalService.getPesans($http, selected);
                selected.data = ["#setuju#" + pesan.id, "Setuju", "setujutidaksetuju", "", this.limit, 0, $index];
                $KawalService.getPesans($http, selected);
                selected.data = ["#setuju#" + pesan.id, "Tidak Setuju", "setujutidaksetuju", "", this.limit, 0, $index];
                $KawalService.getPesans($http, selected);
            };
            this.btnTanggapan = function (user, pesan) {
                pesan.showTanggapiError = false;
                pesan.setujuPesanShow = false;
                pesan.tidakSetujuPesanShow = false;
                pesan.showTanggapi = user.logged && (pesan.showTanggapi ? pesan.showTanggapi = false : pesan.showTanggapi = true);
                if (!user.logged) {
                    pesan.showTanggapiError = true;
                }
                pesan.blockbutton_active = "btnTanggapan";
            };
            this.hideandshowTanggapan = function (pesan) {
                pesan.showTanggapiError = false;
                pesan.setujuPesanShow = false;
                pesan.tidakSetujuPesanShow = false;
                pesan.tanggapanPesanShow = (pesan.tanggapanPesanShow ? pesan.tanggapanPesanShow = false : pesan.tanggapanPesanShow = true);
                pesan.blockbutton_active = "hideandshowTanggapan";
                //$scope.data = ["tanggapan#" + pesan.id, "", "", "", $scope.limit, pesan.tanggapanPesan.length, $index];
            };
            this.hideandshowSetuju = function (pesan) {
                pesan.showTanggapiError = false;
                pesan.tanggapanPesanShow = false;
                pesan.tidakSetujuPesanShow = false;
                pesan.setujuPesanShow = (pesan.setujuPesanShow ? pesan.setujuPesanShow = false : pesan.setujuPesanShow = true);
                pesan.blockbutton_active = "hideandshowSetuju";
                //$scope.data = ["setuju#" + pesan.id, "", "", "", $scope.limit, pesan.tanggapanPesan.length, $index];
            };
            this.hideandshowTidakSetuju = function (pesan) {
                pesan.showTanggapiError = false;
                pesan.tanggapanPesanShow = false;
                pesan.setujuPesanShow = false;
                pesan.tidakSetujuPesanShow = (pesan.tidakSetujuPesanShow ? pesan.tidakSetujuPesanShow = false : pesan.tidakSetujuPesanShow = true);
                pesan.blockbutton_active = "hideandshowTidakSetuju";
                //$scope.data = ["setuju#" + pesan.id, "", "", "", $scope.limit, pesan.tanggapanPesan.length, $index];
            };
            this.isSelected = function (pesan, selected) {
                return pesan.blockbutton_active === selected;
            }
            this.kirimTanggapan = function (selected, pesan, $index) {
                if (pesan.tanggapan.length <= 0) {
                    return;
                }
                pesan.setujuPesanShow = false;
                pesan.tidakSetujuPesanShow = false;
                $KawalService.itemyangsedangdiproses.setKomentar(true);
                selected.data = ["#tanggapan#" + pesan.id, "", "", "", "", pesan.tanggapan, "", "", 1, 0, pesan.id, pesan.key.raw.name, []];
                $KawalService.submitMsg($http, selected, $index);
                this.init();
            }
            this.kirimSetuju = function (user, selected, pesan, $index) {
                pesan.showTanggapiError = false;
                if (!user.logged) {
                    pesan.showTanggapiError = true;
                    return;
                }
                pesan.tanggapanPesanShow = false;
                pesan.tidakSetujuPesanShow = false;
                $KawalService.itemyangsedangdiproses.setKomentar(true);
                selected.data = ["#setuju#" + pesan.id, "", "", "", "", "Setuju", "", "", 1, 0, pesan.id, pesan.key.raw.name, []];
                $KawalService.submitMsg($http, selected, $index);
                pesan.blockbutton_active = "kirimSetuju";
            }
            this.kirimTidakSetuju = function (user, selected, pesan, $index) {
                pesan.showTanggapiError = false;
                if (!user.logged) {
                    pesan.showTanggapiError = true;
                    return;
                }
                pesan.tanggapanPesanShow = false;
                pesan.setujuPesanShow = false;
                $KawalService.itemyangsedangdiproses.setKomentar(true);
                selected.data = ["#setuju#" + pesan.id, "", "", "", "", "Tidak Setuju", "", "", 1, 0, pesan.id, pesan.key.raw.name, []];
                $KawalService.submitMsg($http, selected, $index);
                pesan.blockbutton_active = "kirimTidakSetuju";
            }
            this.photoChange = function (selected) {
                this.photos = selected.files;
                var contex = this;
                for (var i = 0, f; f = this.photos[i]; i++) {
                    if (!f.type.match('image.*')) {
                        continue;
                    }
                    var reader = new FileReader();
                    reader.onload = (function (theFile) {
                        return function (e) {
                            $scope.$apply(function () {
                                contex.photosname = e.target.result;
                                contex.showPhoto = true;
                            })
                        };
                    })(f);
                    reader.readAsDataURL(f);
                }
            };
            this.fileChange = function (selected) {
                this.files = selected.files
                var contex = this;
                for (var i = 0, f; f = this.files[i]; i++) {
                    if (!f.type.match('application/pdf')) {
                        continue;
                    }
                    $scope.$apply(function () {
                        contex.filename = f.name;
                        contex.showFile = true;
                    })
                }
                this.showFile = true;
            };
            this.kirimPesan = function () {
                this.showError = false;
                if (this.isi.length <= 0) {
                    this.showError = true;
                    return;
                }
                $KawalService.itemyangsedangdiproses.setKomentar(true);
                this.data = [this.pesan, "", "", "", "", this.isi, "", "", 1, 0, "0", ""];
                if (this.files.length > 0 || this.photos.length > 0) {
                    $KawalService.getUrlFile($http, this);
                } else {
                    this.data.push([]);
                    $KawalService.submitMsg($http, this);
                }
            };
            this.getMoredata = function () {
                this.offset = this.pesans.length;
                this.data = [this.pesan, this.filter, this.filterBy, this.cursorStr, this.limit, this.offset];
                $KawalService.getPesans($http, this);
                $KawalService.sendToGa();
            }
            this.getMoreTanggapanPesans = function (pesan, $index) {
                this.data = ["#tanggapan#" + pesan.id, "", "", "", this.limit, pesan.tanggapanPesans.length, $index];
                $KawalService.getPesans($http, this);
                $KawalService.sendToGa();
            }
            this.getMoreSetujuPesans = function (pesan, $index) {
                this.data = ["#setuju#" + pesan.id, "Setuju", "setujutidaksetuju", "", this.limit, pesan.setujuPesans.length, $index];
                $KawalService.getPesans($http, this);
                $KawalService.sendToGa();
            }
            this.getMoreTidakSetujuPesans = function (pesan, $index) {
                this.data = ["#setuju#" + pesan.id, "Tidak Setuju", "setujutidaksetuju", "", this.limit, pesan.tidakSetujuPesans.length, $index];
                $KawalService.getPesans($http, this);
                $KawalService.sendToGa();
            }
            this.data = [this.pesan, this.filter, this.filterBy, this.cursorStr, this.limit, this.offset];
            $KawalService.getPesans($http, this);
            $KawalService.sendToGa();
        }]);


})();
