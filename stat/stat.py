import os
import re
import sys
sys.path.append('statfile')
import statfile
import datetime


now=datetime.datetime.now()
print now

nowByMinute=now.replace(now.year,now.month,now.day,now.hour,now.minute,0,0)
nowByDay=now.replace(now.year,now.month,now.day,0,0,0,0)
nowByMonth=now.replace(now.year,now.month,1,0,0,0,0)
nowByYear=now.replace(now.year,1,1,0,0,0,0)

minLogTime=now
logTimeRegex=re.compile('^\[(\d{4}-[0-1][0-9]-[0-3][0-9] [0-5][0-9]:[0-5][0-9]:[0-5][0-9])\.[0-9]{3}\]')

#################################app-service log path#################################
appServiceLogPaths=[]
appServiceLogPaths.append('/app/app-service/logs/catalina.out')
appServiceLogPaths.append('/var/log/tomcat-log/app-service-1/logs/catalina.out')
appServiceLogPaths.append('/var/log/tomcat-log/app-service-2/logs/catalina.out')
appServiceLogPaths.append('/var/log/tomcat-log/app-service-3/logs/catalina.out')

############################device-gateway log path################################
devGWLogPaths=[]
devGWLogPaths.append('/var/log/dev-gateway-service/service.log')

##############################big-router log path################################
bigRouterLogPaths=[]
bigRouterLogPaths.append('/var/log/bigrouter-service/service.log')

#############################third-party-gateway log path##############################
thirdGWLogPaths=[]
thirdGWLogPaths.append('/app/third-party-gateway/logs/catalina.out')
thirdGWLogPaths.append('/var/log/tomcat-log/third-party-gateway-1/logs/catalina.out')
thirdGWLogPaths.append('/var/log/tomcat-log/third-party-gateway-2/logs/catalina.out')
thirdGWLogPaths.append('/var/log/tomcat-log/third-party-gateway-3/logs/catalina.out')



##########################app-service regex##################################

appServiceRegs=[]

#unicast persistence success
appServiceRegs.append({'type':'count','value':re.compile('.*\[\w+\] ReliablePush\[true\] \[\w+\.\w+\].*')})

#broadcast persistence success
appServiceRegs.append({'type':'count','value':re.compile('.*\[broadcast\] message \[\w+\] will be send to \[\d+\] clients.*')})

#third-party-gateway sent message to big-router success
appServiceRegs.append({'type':'count','value':re.compile('.*\[\w+\] sent succeed.*')})

lastAppServiceLogPositionFilePath='position/app_service_log_position'

########################device-gateway regex################################
devGWRegs=[]

#reach broadcast component of device-gateway
devGWRegs.append({'type':'count','value':re.compile('.*\[\w+\.\w+\] ready to broadcast msg.*')})

#app acked unicast
devGWRegs.append({'type':'count','value':re.compile('.*\[\w+\] update unicast app acked success.*')})

#app acked broadcast
devGWRegs.append({'type':'count','value':re.compile('.*\[\w+\] increase app acked count success.*')})

#unicast message request to device-gateway
devGWRegs.append({'type':'count','value':re.compile('.*\[\w+\] unicast message request.*')})

#broadcast message request to device-gateway
devGWRegs.append({'type':'count','value':re.compile('.*recieve broadcast msg from .*')})

#redeliver message num
devGWRegs.append({'type':'redelived','value':re.compile('.*\[\w+\] \[(\d+)\] msg has been redelivered to \w+')})

#today's msg redeliver success number
devGWRegs.append({'type':'redelivedToday','value':re.compile('.*\[\w+\] \[(\d+)\] msg has been redelivered to \w+')})

#this month redeliver success number
devGWRegs.append({'type':'redelivedThisMonth','value':re.compile('.*\[\w+\] \[(\d+)\] msg has been redelivered to \w+')})

#this year redeliver success number
devGWRegs.append({'type':'redelivedThisYear','value':re.compile('.*\[\w+\] \[(\d+)\] msg has been redelivered to \w+')})

lastDevGWLogPositionFilePath='position/dev_gw_log_position'

##############################bigrouter regex################################
bigRouterRegs=[]

#unicast message request to big-router
bigRouterRegs.append({'type':'count','value':re.compile('.*\[devrouter\] \[unicast\] \[\w+\] request route.*')})

#broadcast message request to big-router
bigRouterRegs.append({'type':'count','value':re.compile('.*\[devrouter\] \[broadcast\] \[\w+\] request route.*')})

#big-router sent message to device-gateway success
bigRouterRegs.append({'type':'count','value':re.compile('.*\[\w+\] sent succeed.*')})

lastBigrouterLogPositionFilePath='position/big_router_log_position'

##############################third-party-gateway regex#################################
thirdGWRegs=[]

#unicast request to third-party-gateway
thirdGWRegs.append({'type':'count','value':re.compile('.*\[OppoMessageResource\] pushToClient to \w{32}$')})

#broadcast request to third-party-gateway
thirdGWRegs.append({'type':'count','value':re.compile('.*\[OppoMessageResource\] pushToBroadcast to .*')})

lastThirdGWLogPositionFilePath='position/third_gw_log_position'



def stat(lastStatLogPositionFilePath,statLogPaths,searchRegs):
        nums=[]

	for searchReg in searchRegs:
		nums.append(0)

	k=1
        for statLogPath in statLogPaths:
		if os.path.isfile(statLogPath) is not True:
			print '%s is not a file' % statLogPath
			continue
		lastStatLogPositionFilePath='%s_%s' % (lastStatLogPositionFilePath,k)
		lastStatLogPosition=long(statfile.getLastStatLogPosition(lastStatLogPositionFilePath,statLogPath))
		k+=1
		if lastStatLogPosition!=0L:
			statLog=open(statLogPath);
			statLog.seek(lastStatLogPosition)
			
			lineNum=0
			while True:
				lineNum+=1
				line=statLog.readline()
				if line!='':
					i=0
					if lineNum==1 and logTimeRegex.match(line) is not None:
							minLogTime=datetime.datetime.strptime(logTimeRegex.match(line).group(1),'%Y-%m-%d %H:%M:%S')
							print '%s\'s minLogTime is %s' % (statLogPath,minLogTime)
					for searchReg in searchRegs:
						matcher=searchReg['value'].match(line)
						if searchReg['type']=='count' and matcher is not None:
							nums[i]=nums[i]+1
						elif searchReg['type']=='group' and matcher is not None:
							nums[i]=nums[i]+int(matcher.group(1))
						elif searchReg['type']=='redelived' and matcher is not None and datetime.datetime.fromtimestamp(long(matcher.group(1))/1000)<minLogTime:
							nums[i]=nums[i]+1
						elif searchReg['type']=='redelivedToday' and matcher is not None:
							msgCreateTime=datetime.datetime.fromtimestamp(long(matcher.group(1))/1000)
						 	if msgCreateTime>nowByDay and msgCreateTime<minLogTime:
								nums[i]=nums[i]+1
						elif searchReg['type']=='redelivedThisMonth' and matcher is not None:
							msgCreateTime=datetime.datetime.fromtimestamp(long(matcher.group(1))/1000)
						 	if msgCreateTime>nowByMonth and msgCreateTime<minLogTime:
								nums[i]=nums[i]+1
						elif searchReg['type']=='redelivedThisYear' and matcher is not None:
							msgCreateTime=datetime.datetime.fromtimestamp(long(matcher.group(1))/1000)
						 	if msgCreateTime>nowByYear and msgCreateTime<minLogTime:
								nums[i]=nums[i]+1
						i+=1
				else:
					statfile.recordStatLogPosition(lastStatLogPositionFilePath,statLog.tell())
					statLog.close()
					break
	return nums


appServiceStats=stat(lastAppServiceLogPositionFilePath,appServiceLogPaths,appServiceRegs)
print 'unicast persistence success %s' %  appServiceStats[0]
print 'broadcast persistence success %s' % appServiceStats[1]
print 'third-party-gateway sent message to big-router success %s' % appServiceStats[2]


devGWStats=stat(lastDevGWLogPositionFilePath,devGWLogPaths,devGWRegs)
print 'reach broadcast component of device-gateway %s' % devGWStats[0]
print 'app acked unicast %s' % devGWStats[1]
print 'app acked broadcast %s' % devGWStats[2]
print 'unicast message request to device-gateway %s' % devGWStats[3]
print 'broadcast message request to device-gateway %s' % devGWStats[4]
print 'redeliver message num %s' % devGWStats[5]
print 'today\'s msg redeliver success number %s' % devGWStats[6]
print 'this month msg redeliver success number %s' % devGWStats[7]
print 'this year msg redeliver success number %s' % devGWStats[8]

bigrouterStats=stat(lastBigrouterLogPositionFilePath,bigRouterLogPaths,bigRouterRegs)
print 'unicast message request to big-router %s' %  bigrouterStats[0]
print 'broadcast message request to big-router %s' %  bigrouterStats[1]
print 'big-router sent to device-gateway success %s' %  bigrouterStats[2]


thirdGWStats=stat(lastThirdGWLogPositionFilePath,thirdGWLogPaths,thirdGWRegs)
print 'unicast request to third-party-gateway %s' % thirdGWStats[0]
print 'broadcast request to third-party-gateway %s' % thirdGWStats[1]



stats={}

sumUnicastPendingNum=appServiceStats[0]-(devGWStats[1]-devGWStats[5]) if appServiceStats[0]-(devGWStats[1]-devGWStats[5])>0 else 0
sumUnicastFailedNum=thirdGWStats[0]-appServiceStats[0] if thirdGWStats[0]-appServiceStats[0]>0 else 0

stats['sumUnicastReq']=thirdGWStats[0]
stats['sumUnicastSuccess']=devGWStats[1]
stats['sumUnicastPending']=sumUnicastPendingNum
stats['sumUnicastFailed']=sumUnicastFailedNum

stats['sumBroadcastReq']=thirdGWStats[1]
stats['sumBroadcastSuccess']=devGWStats[2]
stats['sumBroadcastFailed']=thirdGWStats[1]-devGWStats[0] if thirdGWStats[1]-devGWStats[0]>0 else 0

stats['thirdGWReq']=thirdGWStats[0]+thirdGWStats[1]
stats['thirdGWSuccess']=appServiceStats[2]
stats['thirdGWFailed']=thirdGWStats[0]+thirdGWStats[1]-appServiceStats[2] if thirdGWStats[0]+thirdGWStats[1]-appServiceStats[2]>0 else 0


stats['bigrouterReq']=bigrouterStats[0]+bigrouterStats[1]
stats['bigrouterSuccess']=bigrouterStats[2]
stats['bigrouterFailed']=bigrouterStats[0]+bigrouterStats[1]-bigrouterStats[2] if bigrouterStats[0]+bigrouterStats[1]-bigrouterStats[2]>0 else 0 


stats['devGWUnicastReq']=devGWStats[3]
stats['devGWUnicastSuccess']=devGWStats[1]
stats['devGWUnicastFailed']=devGWStats[3]-(devGWStats[1]-devGWStats[5]) if devGWStats[3]-(devGWStats[1]-devGWStats[5])>0 else 0

stats['devGWBroadcastReq']=devGWStats[4]
stats['devGWBroadcastSuccess']=devGWStats[2]
stats['devGWBroadcastFailed']=devGWStats[4]-devGWStats[0] if devGWStats[4]-devGWStats[0]>0 else 0


#from pymongo import Connection
import pymongo
con = pymongo.Connection('mongodb', 27017)
statCollection=con.stat.stat

statCollection.update({'rate':'fiveMinutes','createTime':nowByMinute},{'$inc':stats},True,False)
statCollection.update({'rate':'oneDay','createTime':nowByDay},{'$inc':stats},True,False)
statCollection.update({'rate':'oneMonth','createTime':nowByMonth},{'$inc':stats},True,False)
statCollection.update({'rate':'oneYear','createTime':nowByYear},{'$inc':stats},True,False)

if devGWStats[6]>0:
	statCollection.update({'rate':'oneDay','createTime':nowByDay},{'$inc':{'sumUnicastPending':-devGWStats[6],'devGWUnicastFailed':-devGWStats[6]}},True,False)
if devGWStats[7]>0:
	statCollection.update({'rate':'oneMonth','createTime':nowByMonth},{'$inc':{'sumUnicastPending':-devGWStats[7],'devGWUnicastFailed':-devGWStats[7]}},True,False)
if devGWStats[8]>0:	
	statCollection.update({'rate':'oneYear','createTime':nowByYear},{'$inc':{'sumUnicastPending':-devGWStats[8],'devGWUnicastFailed':-devGWStats[8]}},True,False)
print stats