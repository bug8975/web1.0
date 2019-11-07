// JavaScript Document

// JavaScript Document

function ol_init() {
	
	// marker计数
    var marker_count = 1;

    // 坐标显示
    var mousePositionControl = new ol.control.MousePosition({
        coordinateFormat: ol.coordinate.createStringXY(4),
        projection: 'EPSG: 27700',
        className: 'custom-mouse-position',
        target: document.getElementById("mouse-position"),
        undefinedHTML: '&nbsp',
    })

    //var extent = [0, 0, 3433000, 2406000];
    //var projection = new ol.proj.Projection({
    //    code: 'EPSG:27700',
    //    units: 'm',
    //    extent: extent,
    //});

    var extent = [0, 0, 3433, 2406];
    var projection = new ol.proj.Projection({
        code: 'EPSG:27700',
        units: 'm',
        extent: extent,
    });

    // 地基图层
    var baseLayer = new ol.layer.Image({
        source: new ol.source.ImageStatic({
/*            attributions: '© <a href="http://www.21fet.cn:8888">奇核</a>',
*/            url: '../resources/images/yongdingmen.jpg',
            imageExtent: extent
        })
    });

    // 施工图层
    var buildLayer = new ol.layer.Image({
        source: new ol.source.ImageStatic({
            attributions: '© <a href="http://www.21fet.cn:8888">奇核2</a>',
            url: '../resources/images/yongdingmen_work.jpg',
            imageExtent: extent
        })
    });
    // 设置透明度(0~1)
    buildLayer.setOpacity(0.7);
    buildLayer.setVisible(false);

    var map = new ol.Map({
        target: 'map',
        layers: [
            //new ol.layer.Tile ({
            //   source: new ol.source.OSM(),
            //   name: 'OSM'
            //}),

            //new ol.layer.Vector ({
            //    source: new ol.source.Vector ({
            //        url: "http://localhost:8888/OL3Demo/demos/data/geojson/countries.geojson",
            //        format: new ol.format.GeoJSON()
            //    })
            //}),

            baseLayer,
            buildLayer,
        ],

        view: new ol.View({
            projection: projection,
            center: ol.extent.getCenter(extent),
            zoom: 2,
        }),

        controls: ol.control.defaults().extend([mousePositionControl])
    })

    //Marker
    //var beijing = [400.00, 500.00];
    //var wuhan = [300.00, 600.00];

    //var createLabelStyle = function (feature) {
    //    return new ol.style.Style({
    //      image: new ol.style.Icon(({
    //          anchor: [0.5, 60],
    //          anchorOrigin: 'top-right',
    //          anchorXUnits: 'fraction',
    //          anchorYUnits: 'pixels',
    //          offsetOrigin: 'top-right',
    //          // offset:[0,10],
    //          scale:0.5,                              //图标缩放比例
    //          opacity: 0.75,                          //透明度
    //          src: '../images/icon.png'               //图标的url
    //      })),

    //      text: new ol.style.Text({
    //          textAlign: 'center',                    //位置
    //          textBaseline: 'middle',                 //基准线
    //          font: 'normal 14px 微软雅黑',            //文字样式
    //          text: feature.get('name'),              //文本内容
    //          fill: new ol.style.Fill({ color: '#aa3300' }), //文本填充样式（即文字颜色）
    //          stroke: new ol.style.Stroke({ color: '#ffcc33', width: 2 })
    //      })      
    //  });
    //}

    // Warning-style
    var warnningStyle3 = new ol.style.Style({
        image: new ol.style.Icon(({
            anchor: [0.5, 60],
            anchorOrigin: 'top-right',
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            offsetOrigin: 'top-right',
            // offset:[0,10],
            scale: 0.5,                             //图标缩放比例
            opacity: 1,                          //透明度
            src: "../resources/images/warning3.png"            //图标的url
        })),
    })
    // Warning-style
    var warnningStyle2 = new ol.style.Style({
        image: new ol.style.Icon(({
            anchor: [0.5, 60],
            anchorOrigin: 'top-right',
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            offsetOrigin: 'top-right',
            // offset:[0,10],
            scale: 0.5,                             //图标缩放比例
            opacity: 1,                          //透明度
            src: "../resources/images/warning2.jpg"            //图标的url
        })),
    })    // Warning-style
    var warnningStyle1 = new ol.style.Style({
        image: new ol.style.Icon(({
            anchor: [0.5, 60],
            anchorOrigin: 'top-right',
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            offsetOrigin: 'top-right',
            // offset:[0,10],
            scale: 0.5,                             //图标缩放比例
            opacity: 1,                          //透明度
            src: "../resources/images/warning1.png"            //图标的url
        })),
    })
    
    

    // Normal-style
    var normalStyle = new ol.style.Style({
        image: new ol.style.Icon(({
            anchor: [0.5, 60],
            anchorOrigin: 'top-right',
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            offsetOrigin: 'top-right',
            // offset:[0,10],
            scale: 0.5,                             //图标缩放比例
            opacity: 1,                          //透明度
            src: "../resources/images/normal.jpg"             //图标的url
        })),
    })
    
    

    // Vector-Marker
    //实例化Vector要素，通过矢量图层添加到地图容器中
    //var monitor00 = new ol.Feature({
    //    geometry: new ol.geom.Point([400,500]),
    //    name: '监测点0',
    //    id: 0,
    //    status: 0,
    //    lastupdate: '2016-08-12 23:38:00',
    //});    
    //monitor00.setStyle(normalStyle);

    var monitorSource = new ol.source.Vector({
        features: []
    });

    var monitorLayer = new ol.layer.Vector({
        source: monitorSource
    });

    map.addLayer(monitorLayer);

    //
	var id= 1;
    function addMonitor(coordinate) {
    	var sensor = document.getElementById(id);
    	var	sensorName = sensor.value;
    	var alarmLevel = sensor.getAttribute('data');
    	var feature = new ol.Feature({
            geometry: new ol.geom.Point(coordinate),       //几何信息
            name: sensorName,                 //名称属性
        });

        if (alarmLevel == 0) {
            feature.setStyle(normalStyle);
        }else if (alarmLevel == 1) {
        	alert(status);
            feature.setStyle(warnningStyle1);
        }else if (alarmLevel == 2) {
            feature.setStyle(warnningStyle2);
        }else{
            feature.setStyle(warnningStyle3);
        }

        monitorSource.addFeature(feature);
        id++;
    }
    
    // 添加监测点
    addMonitor([630, 1510]);//坐标点位置
    addMonitor([750, 1510]);
    addMonitor([820, 1510]);
    addMonitor([892, 1510]);
    addMonitor([1030, 1510]);
    addMonitor([1180, 1510]);
    addMonitor([1255, 1505]);
    addMonitor([1330, 1505]);
    addMonitor([1470, 1505]);

    addMonitor([925, 1305]);
    addMonitor([1068, 1300]);
    addMonitor([1150, 1300]);
    addMonitor([1228, 1300]);
    addMonitor([1399, 1300]);
    addMonitor([1518, 1300]);
    addMonitor([1604, 1295]);
    addMonitor([1686, 1295]);
    addMonitor([1825, 1290]);







    // 
    function setInnerText(element, text) {
        if (typeof element.textContent == "string") {
            element.textContent = text;
        } else {
            element.innerText = text;
        }
    }

    // Popup-marker    
    var container = document.getElementById('popup');
    var content = document.getElementById('popup-content');
    var closer = document.getElementById('popup-closer');
    var checkbox_monitorLayer = document.getElementById("checkbox_monitorLayer");
    var checkbox_baseLayer = document.getElementById("checkbox_baseLayer");
    var checkbox_buildLayer = document.getElementById("checkbox_buildLayer");

    // 在地图容器中创建一个Overlay    
    var popup = new ol.Overlay(/** @type {olx.OverlayOptions} */({
        element: container,
        autoPan: true,
        positioning: 'bottom-center',
        stopEvent: false,
        autoPanAnimation: {
            duration: 250
        }
    }));
    map.addOverlay(popup);


    // 添加关闭按钮的单击事件（隐藏popup）    
    closer.onclick = function () {
        popup.setPosition(undefined);  //未定义popup位置
        closer.blur(); //失去焦点
        return false;
    };

    // 显示监测点图层
    checkbox_monitorLayer.onchange = function () {
        monitorLayer.setVisible($("#checkbox_monitorLayer").attr("checked") == "checked" ? true : false);
    }

    // 地基图层
    checkbox_baseLayer.onchange = function () {
        baseLayer.setVisible($("#checkbox_baseLayer").attr("checked") == "checked" ? true : false);
    }

    // 施工图层
    checkbox_buildLayer.onchange = function () {
        buildLayer.setVisible($("#checkbox_buildLayer").attr("checked") == "checked" ? true : false);
    }

    var sensorData = "";
    // 动态创建popup的具体内容
    function addFeatrueInfo(feature) {
    	var format = "yyyy-MM-dd HH:mm:ss";
    	var collectTime = dateFormat(sensorData.collectingTime,format);
    	var beginTime= dateFormat(sensorData.collectingTime-24*3600*1000,format);
    	var endTime= collectTime;
        //新增a元素
        var elementA = document.createElement('a');
        elementA.className = "markerInfo";
        elementA.href = "../picreport/singledataline.htm?sensorTypeName="
        	+ sensorData.sensorType_name +"&monitorlineid="+ sensorData.monitorLineid 
        	+"&sensorid="+ sensorData.id +"&beginTime="+ beginTime +"&endTime="+ endTime;
        elementA.target = '_blank';
        setInnerText(elementA, feature.get('name'));//info.att.title);
        content.appendChild(elementA); // 新建的div元素添加a子节点

        //新增div元素
        var elementDiv = document.createElement('div');
        elementDiv.className = "markerText";
        elementDiv.style.cssText = "width:230px;height:auto"; 
        var info = "<B>状态: </B>";
        var status = sensorData.alarmLevel;
        if (status == 0) {
            info = info + '正常';
        }else if(status == 1){
            info = info + '<b><font color="blue" size="10px">警告</font></b>';
        }else if(status == 2){
            info = info + '<b><font color="yellow" size="10px">警告</font></b>';
        }else {
            info = info + '<b><font color="red" size="10px">警告</font></b>';
        }
        
        if(sensorData == "" && "".equlas(sensorData)){
        	info = info + '<br/><B>上次更新时间:  </B>';
            info = info + '<br/><B>传感器类型:  </B>';
            info = info + '<br/><B>传感器名称:  </B>';
            info = info + '<br/><B>当前读数:  </B>';
            info = info + '<br/><B>即时形变:  </B>';
            info = info + '<br/><B>累计形变:  </B>';
            info = info + '<br/><B>单位:  </B>';
        }else{
        	info = info + '<br/><B>上次更新时间:  </B>' + collectTime;
            info = info + '<br/><B>传感器类型:  </B>' + sensorData.sensorTypeName;
            info = info + '<br/><B>传感器名称:  </B>' + sensorData.sensorName;
            info = info + '<br/><B>当前读数:  </B>' + sensorData.deviceData;
            info = info + '<br/><B>即时形变:  </B>' + sensorData.sinkingData;
            info = info + '<br/><B>累计形变:  </B>' + sensorData.sinkingAccumulation;
            info = info + '<br/><B>单位:  </B>' + sensorData.unit;
        }
        
        elementDiv.innerHTML = info;
        content.appendChild(elementDiv);

        /*//新增img元素
        var elementImg = document.createElement('img');
        elementImg.className = "markerImg";
        elementImg.border = 1;
        elementImg.src = "../resources/images/monitor_chart.jpg";
        content.appendChild(elementImg);*/
    }

    //为地图容器添加单击事件监听
    map.on('click', function (evt) {
        var point = evt.coordinate; //鼠标单击点坐标        
        var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
            return feature;
        });
        var sensorName = feature.get('name');
        jQuery.post("../query_sensor.htm",{"sensorName":sensorName},function(data){
            jQuery.each(data, function(index,item){
            	sensorData = item;
            	if (feature) {
                    content.innerHTML = ''; //清空popup的内容容器
                    addFeatrueInfo(feature); //在popup中加载当前要素的具体信息

                    if (popup.getPosition() != undefined) {
                        popup.setPosition(undefined);
                    }

                    popup.setPosition(point); //设置popup的位置                    
                }
            });
        },"json");

        
    });

    map.on('dblclick', function (evt) {
        var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
            return feature;
        });

        if (feature) {
            if (feature.get('status')) {
                feature.setStyle(normalStyle);
                feature.set('status', 0);
            }
            else {
                feature.setStyle(warnningStyle);
                feature.set('status', 1);
            }

            return false;
        }
    });

    map.on('pointermove', function (e) {
        var pixel = map.getEventPixel(e.originalEvent);
        var hit = map.hasFeatureAtPixel(pixel);
        map.getTargetElement().style.cursor = hit ? 'pointer' : '';
    })
}