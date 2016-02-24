import os


lastAppServiceLogPositionFilePath='last_app_service_log_position'

appServiceLogPath='/app/app-service/logs/catalina.out'


def initStatLogPosition(lastStatLogPositionFilePath,statLogPath):
        lastStatLogPositionFile=open(lastStatLogPositionFilePath,'w')
        statLog=open(statLogPath)

        statLog.seek(0,2)

        lastStatLogPositionFile.write(str(statLog.tell()));

        lastStatLogPositionFile.close()
        statLog.close()

def getLastStatLogPosition(lastStatLogPositionFilePath,statLogPath):
	lastStatLogPosition=0L;

        if os.path.isfile(lastStatLogPositionFilePath):
                lastStatLogPositionFile=open(lastStatLogPositionFilePath);
                lastStatLogPosition=long(lastStatLogPositionFile.read());
		lastStatLogPositionFile.close();
	   	statLog=open(statLogPath)	
		statLog.seek(0,2)
		logFileLineNum=long(statLog.tell())
		statLog.close()
		if lastStatLogPosition>long(logFileLineNum):
			initStatLogPosition(lastStatLogPositionFilePath,statLogPath)
			return 1L

	if lastStatLogPosition==0L:
                initStatLogPosition(lastStatLogPositionFilePath,statLogPath);
	return lastStatLogPosition;
	

def recordStatLogPosition(lastStatLogPositionFilePath,position):
        lastStatLogPositionFile=open(lastStatLogPositionFilePath,'w');
        lastStatLogPositionFile.write(str(position))
        lastStatLogPositionFile.close()

def getAppServiceLogFile():
        return open(appServiceLogPath)

def getLastAppServiceLogPosition():
	return getLastStatLogPosition(lastAppServiceLogPositionFilePath,appServiceLogPath)

def recordAppServiceLogPosition(position):
	recordStatLogPosition(lastAppServiceLogPositionFilePath,position)
