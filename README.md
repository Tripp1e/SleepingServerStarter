﻿# SleepingServerStarter
 <br>
Execute `sh start.sh` when http request is sent to `:25580/run_server` In my use case this is for a Minecraft Server. It will detect if the screen session already has this process running and if yes will just return. Otherwise it will start it. <br><br>
This means you can reattach to each process from a ssh shell.
