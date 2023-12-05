.PHONY: deploy_frontend, deploy_backend, deploy_kubernetes_configuration

deploy_frontend:
	npm run -C frontend build && \
	rsync -av ./frontend/build/* virusx:/home/sukach/volumes/frontend

deploy_backend:
	mvn package -DskipTests -f ./backend  && \
	docker build ./backend -t virusx:32000/alcosearch

deploy_kubernetes_configuration:
	rsync -av ./kubernetes/* virusx:/home/sukach/kubernetes

