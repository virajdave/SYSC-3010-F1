#==============================================================================
#title           		:dataPassingObject.py
#description    	:Messaging stucture
#author          		:Dillon Verhaeghe
#date            		:20170326
#version         		:0.4
#usage           		:message(type,data)
#notes           		:Used between mirror threads to pass info around with all understanding
#python_version :3 
#==============================================================================

class message:
	def __init__(self, messageType, info):
		self.messageType = messageType
		self.info = info
		