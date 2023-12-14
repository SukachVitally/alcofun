.PHONY: deploy_frontend, deploy_backend, helm_upgrade, helm_install, helm_unistall

frontend_version := 0.1.0
backend_version := 0.1.0

server := virusx:32000
backend_image := $(server)/backend:$(backend_version)
frontend_image := $(server)/frontend:$(frontend_version)

deploy_frontend:
	# npm run -C frontend build 
	docker build ./frontend -t $(frontend_image)
	docker push $(frontend_image)
	ssh virusx docker pull $(frontend_image)
	$(MAKE) helm_upgrade
	

deploy_backend:
	mvn clean package -DskipTests -f ./backend
	docker build ./backend -t $(backend_image)
	docker push $(backend_image)
	ssh virusx docker pull $(backend_image)
	$(MAKE) helm_upgrade

helm_upgrade:
	helm upgrade deploy deploy -n prod --debug

helm_install:
	helm install deploy deploy -n prod --debug

helm_unistall:
	helm uninstall deploy -n prod
