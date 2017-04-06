# Update the repo and start the manager
cd .. && \
git pull && \
cd server && \
ant compile && \
java -cp "bin:lib/sqlite-jdbc-3.16.1.jar" main.Manager
