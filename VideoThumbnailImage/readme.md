**Creating a thumbnail out of a video**

Clone the repo

`git clone https://github.com/bahmni-msf/utilities.git`

`cd VideoThumbnailImage`

Run the below command to create jar file

`mvn assembly:assembly -DdescriptorId=jar-with-dependencies`

Give the full path of your directory where you have all videos in it Ex: '/home/bahmni/document_images'. 

`java -jar target/thumbnail-creator-0.89-SNAPSHOT-jar-with-dependencies.jar {directory}`

Logs can be found in your directory as logger.log
