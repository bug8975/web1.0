//JavaScript Document

//JavaScript Document

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
			 */            url: '../resources/images/testGIS.jpg',
			 imageExtent: extent
		})
	});

	// 施工图层
	var buildLayer = new ol.layer.Image({
		source: new ol.source.ImageStatic({
			attributions: '© <a href="http://www.21fet.cn:8888">奇核2</a>',
			url: '../resources/images/testGIS.jpg',
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
	//P-levelingTransducer
	addMonitor([2330, 1062]);
	addMonitor([2263, 1036]);
	addMonitor([2276, 999]);
	addMonitor([2186, 946]);
	addMonitor([2190, 894]);
	//InclinoMeter
	addMonitor([3103, 1630]);
	addMonitor([3130, 1632]);
	//LaserSensor
	addMonitor([1738, 604]);
	addMonitor([1716, 587]);
	addMonitor([1695, 572]);
	addMonitor([1673, 558]);
	addMonitor([1652, 542]);
	addMonitor([1630, 529]);
	addMonitor([1609, 515]);
	addMonitor([1588, 500]);
	addMonitor([1566, 486]);
	addMonitor([1557, 479]);
	//AnchorDynamometer
	addMonitor([1700, 527]);
	addMonitor([1689, 518]);
	addMonitor([1667, 503]);
	addMonitor([1646, 488]);
	addMonitor([1625, 473]);
	addMonitor([1603, 457]);
	addMonitor([1582, 444]);
	addMonitor([1560, 430]);
	addMonitor([1538, 417]);
	addMonitor([1527, 411]);
	//SteelStressMeter
	addMonitor([1347, 412]);
	addMonitor([1336, 412]);
	addMonitor([1313, 414]);
	addMonitor([1290, 416]);
	addMonitor([1268, 419]);
	addMonitor([1245, 419]);
	addMonitor([1222, 421]);
	addMonitor([1199, 425]);
	addMonitor([1176, 428]);
	addMonitor([1163, 429]);
	//StrainGauge
	addMonitor([1345, 365]);
	addMonitor([1329, 365]);
	addMonitor([1306, 368]);
	addMonitor([1283, 368]);
	addMonitor([1260, 370]);
	addMonitor([1238, 374]);
	addMonitor([1215, 375]);
	addMonitor([1192, 378]);
	addMonitor([1170, 379]);
	addMonitor([1158, 382]);
	
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
		if(sensorData.sensorType=="FixedInclinoMeter"){
			var elementDiv = document.createElement('div');
			elementDiv.className = "markerText";
			elementDiv.id = "monitor"
			elementDiv.style.cssText = "width:230px;height:auto"; 
			content.appendChild(elementDiv);

	        var rangeability = 20;
			var x_Axis = sensorData.sinkingData;
			if( x_Axis.length <= 20){
				x_Axis = x_Axis.length;
			}else{
				x_Axis = 20;
			}
			var cellTime = sensorData.cellectTime;
			var dataTmp = "";
			dataTmp += "{name: '偏移值',color:'#FF7744 ',data:" + sensorData.sinkingData + "}" + ","  ;
			var chart = new Highcharts.Chart({
				chart: {
					inverted: true,        //x与Y轴反转
					renderTo: 'monitor',
					defaultSeriesType: 'line', //图表类别，可取值有：line、spline、area、areaspline、bar、column等
					marginRight: 20,
					marginBottom: 40
				},
				title: {
					text: sensorData.monitorName + '偏移图', //设置一级标题
					x: 5, 
					y: 30
				},
				subtitle: {
					text:'时间：' + cellTime, //设置二级标题
					x: 10,
					y: 45
				},
				/* scrollbar: {
                        enabled: true
                    }, */
				credits: { 
					enabled: false
				}, //是否显示版权信息
				xAxis: {
					labels: {
						formatter: function() {
							return this.value + 1;
						}
					},
					lineColor:'#800080',//x轴颜色
					tickInterval: 1,
					reversed: false,
				},
				yAxis: {
					tickInterval:rangeability/2,
					max:rangeability,
					min:-rangeability,
					title: {
						text: '偏移量(mm)',
					},
					labels: {
						formatter: function () {
							return this.value;
						}
					},
					//                  gridLineColor: '   #800080 '
				},
				lang:{
					months:['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
					shortMonths:['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
					weekdays:['星期天','星期一','星期二','星期三','星期四','星期五','星期六'],
					exportButtonTitle:'导出PDF',
					printButtonTitle:'打印图片'
				},
				legend: {
					layout: 'vertical',
					align: 'top', //设置说明文字的文字 left/right/top/
					verticalAlign: 'top',
					x: -10,
					y: -10,
					borderWidth: 0
				},
				plotOptions: {
					line: {
						dataLabels: {
							enabled: true //显示每条曲线每个节点的数据项的值
						},
						enableMouseTracking: false
					}
				},
				series: eval("[" + dataTmp + "]") 
			});

			/*//新增img元素
            var elementImg = document.createElement('img');
            elementImg.className = "markerImg";
            elementImg.border = 1;
            elementImg.src = "../resources/images/monitor_chart.jpg";
            content.appendChild(elementImg);*/
		}else{
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
			}else if(sensorData.base == true){
				info = info + '<br/><B>上次更新时间:  </B>' + collectTime;
				info = info + '<br/><B>传感器类型:  </B>' + sensorData.sensorTypeName;
				info = info + '<br/><B>传感器名称:  </B>' + sensorData.sensorName;
				info = info + '<br/><B>当前读数:  </B>基准点';
				info = info + '<br/><B>即时形变:  </B>基准点';
				info = info + '<br/><B>累计形变:  </B>基准点';
				info = info + '<br/><B>单位:  </B>' + sensorData.unit;
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
		}
	}

	//为地图容器添加单击事件监听
	map.on('click', function (evt) {
		var point = evt.coordinate; //鼠标单击点坐标        
		var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
			return feature;
		});
		var Name = feature.get('name');
		jQuery.post("../query_sensorType.htm",{"sensorName":Name},function(data){
			jQuery.each(data, function(index,item){
				if(item.sensorType=="FixedInclinoMeter"){
					jQuery.post("../query_monitorline.htm",{"monitorName":Name},function(data){
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
				}else{
					jQuery.post("../query_sensor.htm",{"sensorName":Name},function(data){
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