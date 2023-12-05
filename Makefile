deploy_frontend:
	npm run -C frontend build && \
	rsync -av ./frontend/build/* virusx:/home/sukach/volumes/frontend

