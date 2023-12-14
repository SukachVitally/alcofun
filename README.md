# alcofun-deploy
Ansible configuration to deploy web ui with backend

# Kubernetes commands

## Get all namespaces
```sh
microk8s kubectl get all --all-namespaces
```

## Run kubernetes dashboard
```sh
microk8s dashboard-proxy
```

## Show kubernetes namespaces
```sh
microk8s kubectl get namespaces
```

## Show kubernetes pods
```sh
microk8s kubectl get pods -A
```

## Show kubernetes services
```sh
microk8s kubectl get services -A
```

## Apply configuration
```sh
microk8s kubectl apply -f <config.yaml> 
```

## Get pods for namespace
```sh
microk8s kubectl get pods -n <namespace>
```

## Get pod info
```sh
microk8s kubectl <pod-id> -n <namespace>
```

## Get pod open ports info
```sh
microk8s kubectl get pods -n <namespace> -o wide
```

## Connect to pod
```sh
microk8s kubectl exec -it <name-id> -n <namespace> -- sh
```

## Show interactive pod logs
```sh
microk8s kubectl logs -f <name-id> -n <namespace>
```

## Update tls sertificates
Update certbot certificate 


```sh
rsync -av ./kubernetes/* virusx:/home/sukach/kubernetes 
microk8s kubectl create secret tls alcofun-co-ua-tls  -n prod  
  --cert=certificates/alcofun.co.ua/fullchain1.pem   
  --key=certificates/alcofun.co.ua/privkey1.pem

```

## Helm 
```sh
helm upgrade --atomic --install <name> ./chart
```

