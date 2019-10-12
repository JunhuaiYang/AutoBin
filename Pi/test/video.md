* 电脑
"D:\Pi\mplayer\mplayer-svn-38151\mplayer.exe" -fps 200 -demuxer h264es ffmpeg://tcp://192.168.1.100:8090  

* PI 
raspivid -t 0 -w 1280 -h 720 -fps 20 -o - | nc -k -l 8090