# Update the repo and start the manager
cd .. && \
git pull && \
cd server && \
ant compile && \
java -cp bin main.Manager
