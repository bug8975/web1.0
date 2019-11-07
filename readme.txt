2017年9月14日14:24:57
标准版本整合完毕(曲线查看、趋势分析、多曲线显示、重复数据更新不插入、数据转存、数据导入、根据时间导出报表、曲线上下限范围调整、数据调整等)    
    
9月14日17:05:43
标题数据库修改后，前端自动刷新
/standard/webapp/WEB-INF/html/common/top.html                       定时任务中添加查询标题的方法
/standard/src/com/monitor/action/common/CommonViewAction.java       增加getSysTitle方法获取实时的标题名称,修改预加载的标题查询语句

2017年9月15日11:12:19
修改传感器状态后，前端刷新自动加载
/standard/src/com/monitor/core/base/GenericEntityDao.java           73行代码注释。此处为缓存，去除缓存则实时更新
涉及传感器类型查询语句都将：
List<SensorType> sensorTypes = this.sensorTypeService.getUsingSensorTypes();
修改为
List<SensorType> sensorTypes = this.sensorTypeService.query("select obj from SensorType obj where obj.usingStatus = 0", null);

/standard/src/com/monitor/action/apptransport/AppTransportAction.java      monitorLineModify方法添加和删除监测线时候变更usingStatus值
/standard/src/com/monitor/foundation/dao/MonitorDao.java                   增加UpdateUsingStatus方法修改usingStatus值

2017年9月19日17:18:23
修改增加初始值的方法，增加初始值表
/standard/src/com/monitor/foundation/domain/InitialValue.java
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
/standard/src/com/monitor/core/apptransport/InitialValueItem.java
/standard/src/com/monitor/foundation/dao/MonitorDao.java

初始值下发
/standard/src/com/monitor/core/apptransport/HeartBeatResponse.java      (回传值添加ADDOP)

2017年10月10日13:56:35
初值显示
/standard/webapp/WEB-INF/html/deviceapp/initialValueModify.html         初值显示及设置(也可下发采集指令)
/standard/src/com/monitor/action/deviceapp/InitialValueAction.java      初值以及采集设置,加载预设值,初始报表导出方法
/standard/src/com/monitor/foundation/domain/InitialCollect.java         采集初值指令

修改：
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
heartbeat方法心跳上报三种流程(优先阈值下发,其次初始值下发,最后初值采集指令下发,每次心跳上报只触发以上三种操作之一。若无操作则回复success)


2017年10月11日10:40:52
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
通过firstvaluereport方法上报的初值,状态设置为3（已确认）
/standard/src/com/monitor/action/deviceapp/InitialValueAction.java
修改前端状态的显示名称


2017年10月17日10:05:54
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
修改heartbeat下发json值的格式以及添加新的值resultCode、resultString方便数据定位

/standard/src/com/monitor/core/apptransport/ResultNotification.java
返回result结果
/standard/src/com/monitor/core/apptransport/CollectMsgNotification.java
返回采集值
/standard/src/com/monitor/core/apptransport/FirstValueNotification.java
返回初值

2017年10月19日10:38:56
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
修改无操作时回传参数的格式
/standard/src/com/monitor/core/apptransport/ResultNotification.java
resultString 拼写错误修正

2017年10月20日16:47:12
/standard/src/com/monitor/foundation/dao/MonitorDao.java
修改initialVlaueModify方法增加新的采集信息。只允许同一传感器存在一次未设置状态(initSetStatus = 1)

2017年10月26日17:27:30
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
/standard/src/com/monitor/foundation/dao/MonitorDao.java
/standard/src/com/monitor/action/deviceapp/InitialValueAction.java
/standard/webapp/WEB-INF/html/deviceapp/initialValueModify.html
调整插入指令,同一时刻只存在一次可下发数据。接收firstvaluereport数据后将所有待下发数据设置为历史


2018年5月10日11:48:26
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
三组影响数据的方式修改顺序，1系数调整（乘法），2趋势数据（原始数据 - 特定时间结点数据），3数据调整（加上调整数值，为负数则是减）
/standard/src/com/monitor/action/deviceapp/TrendAnalysisAction.java
增加删除方法，调整为对应线号显示
/standard/src/com/monitor/foundation/dao/TrendAnalysisDao.java
调整数据库对应字段，增加根据线号查询方法，修改根据线号查询
/standard/src/com/monitor/foundation/domain/TrendAnalysis.java
调整状态查看压力式静力水准仪趋势分析数据对应单条线
/standard/webapp/WEB-INF/html/deviceapp/trendanalysis.html
增加删除趋势数据方法


2018年5月10日15:17:14
将数据转存、数据调整的方法从 monitorDao中迁移出来
增加文件：
/standard/src/com/monitor/foundation/dao/AccumulativeDao.java
/standard/src/com/monitor/foundation/dao/ArchCoefficientDao.java
修改文件：@注入dao,引用方法

/standard/src/com/monitor/action/apptransport/AppTransportAction.java
@Autowired
public ArchCoefficientDao archCoefficientDao;
@Autowired
public AccumulativeDao accumulativeDao;

    
/standard/src/com/monitor/action/deviceapp/AccumulativeAction.java
@Autowired
public AccumulativeDao accumulativeDao;

/standard/src/com/monitor/action/deviceapp/DataArchvingAction.java
@Autowired
public ArchCoefficientDao archCoefficientDao;

2018年6月3日10:40:10
/bz/src/com/monitor/action/apptransport/AppTransportAction.java
修改初始值计算方法：
decvicedata = 本次频率
singkingdata = 本次内力
singkingAcculument = 累计变化值
本次内力（KN）=标定系数*（本次频率的平方-出厂频率的平方）
累计变化值（KN）=标定系数*（本次频率的平方-第一次测量频率的平方）
/bz/src/com/monitor/core/apptransport/MonitorDataViewItem.java
将json值存入实体类

/bz/webapp/WEB-INF/html/deviceapp/axialForceCoefficient.html
弦类传感器系数查询与修改
/bz/src/com/monitor/foundation/domain/AxialForceCoefficient.java
/bz/src/com/monitor/action/deviceapp/AxialForceAction.java
/bz/src/com/monitor/foundation/dao/AxialForceCoefficientDao.java



2018年6月11日14:55:44
将初值设置的方法从 monitorDao中迁移出来
增加文件：
/standard/src/com/monitor/foundation/dao/InitialValueSettingDao.java

修改文件
/standard/src/com/monitor/action/deviceapp/InitialValueAction.java
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
增加初始值方法自动注入
    @Autowired
    public InitialValueSettingDao initialValueSettingDao;
    
    
2018年6月11日15:05:45
修改初值回传采集参数
如果有取初值功能，回复取初值参数
[{“ADDOP”,”FVGT”},{“monitorLineName”:”CJ-01,CJ-02”,”startTime”: 1458907797972,”sampleCount”:30},{"resultCode":1,"resultString":"SUCCESS"}]
startTime: 取初值开始、结束时间
sampleCount: 采样次数
/standard/src/com/monitor/action/apptransport/AppTransportAction.java
  hreartbeat.htm 方法修改下发参数 
/standard/src/com/monitor/core/apptransport/CollectMsgNotification.java
  下发参数去除stopTime,stoptime字段，修改sampleInterval字段为sampleCount
  /standard/webapp/WEB-INF/html/common/menu_nav.html
  
  /standard/webapp/WEB-INF/html/common/menu_nav.html