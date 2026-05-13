FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENV DATABASE_URL="<insert-database-url>"
ENV DATABASE_USERNAME="<insert-database-username>"
ENV DATABASE_PASSWORD="<insert-database-password>"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


#      {  totalReq: 0,
#          processed: 0,
#          failed: 0 }


#          {time,rps,latency for graph and ui to show}

            { id: 'h1', time, level: 'INFO', msg: 'Connecting to Central Hub wss://hub.network/stream...' },


#        Log
#        { id: 'h_sys1', time, level: 'INFO', msg: 'Hub Core Initialized. Cluster_01 online.' },
#
#    State
#  const [stats, setStats] = useState({ totalReq: 0, processed: 0, rps: 0, latency: 0 });
#
#
#        then add the websocket in centeral hub
#            and then test that aswell