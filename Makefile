.PHONY: deploy_frontend, deploy_backend, helm_upgrade, helm_install, helm_unistall

backend_version := 0.7.1

backend_image := virusx:32000/backend:$(backend_version)

deploy_frontend:
	npm run build -C ./frontend/
	rsync -av --chmod=a+rx --delete ./frontend/build/* virusx:/home/sukach/volumes/frontend 

deploy_backend:
	mvn clean package -DskipTests -f ./backend
	docker build ./backend -t $(backend_image)
	docker push $(backend_image)
	ssh virusx docker pull $(backend_image)
	$(MAKE) helm_upgrade

helm_upgrade:
	helm upgrade deploy deploy -n prod

helm_install:
	helm install deploy deploy -n prod --debug

helm_unistall:
	helm uninstall deploy -n prod
