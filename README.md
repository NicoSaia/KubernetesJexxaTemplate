# Digi-Kubernetes

- [Digi-Kubernetes](#digi-kubernetes)
  - [Digital Ocean Nodes bestellen:](#digital-ocean-nodes-bestellen)
  - [Initiale Setup auf DigiPod um mit DigitalOcean kommunizieren zu können:](#initiale-setup-auf-digipod-um-mit-digitalocean-kommunizieren-zu-können)
  - [Portainer installieren](#portainer-installieren)
  - [Deployment](#deployment)
  - [Wichtige Begriffe](#wichtige-begriffe)
  - [Datenbanken und Kubernetes](#datenbanken-und-kubernetes)
  - [Ausrollen einer Anwendung mit Datenbank in separaten Pods](#ausrollen-einer-anwendung-mit-datenbank-in-separaten-pods)


Dieses Repository dient dem sammeln von Erkenntnissen und Konfigurationen.


## Digital Ocean Nodes bestellen:

- 6vCPUs
- 12GB
- 240GB Diskspace
- 3 Nodes 


## Initiale Setup auf DigiPod um mit DigitalOcean kommunizieren zu können:

- https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
- WGET installieren
  - sudo dnf search wget
  - sudo dnf install wget
- https://docs.digitalocean.com/reference/doctl/how-to/install/ 

- Auf digitalocean mit Cluster verbinden
  - doctl kubernetes cluster kubeconfig save …..
  - Hiernach kann der Befehl “kubectl” verwendet werden mit dem Cluster


## Portainer installieren

- Mehrere Möglichkeiten
  - Empfehlung über Terminal
    - https://docs.digitalocean.com/reference/doctl/how-to/install/ 

  - Über Kubernetes Admin Panel (Dashboard)
    - Create new Recource und Stack von Portainer importieren
    - https://docs.portainer.io/start/install/server/kubernetes/baremetal 
- Danach die Anleitung von Portainer befolgen zum einrichten


## Deployment

- Einer Anwendung mit Datenbank:
  - Anwendung und Datenbank in einen Pod
    - Vorteile: Einfacher, Kommunikation über localhost, nur wenig zu konfigurieren
    - Nachteil: Ist ein Antipattern von Kubernetes, Kann nicht gut skalieren  da Datenbank und Anwendung unterschiedlich skalieren
    - Anwendung muss Stateless sein
  - Anwendung in ein Pod; Datenbank in einem Pod


## Wichtige Begriffe

- `Pod`: Objekt, was einen oder mehrere Container ausführt 
- `Deployment`: Definiert ein `ReplicaSet` zur Ausführung eines oder mehrerer Pods 
- `Service`: Stellt einen Netzwerkdienst eines Pods im Kubernetes Cluster bereit. Als `ClusterIP` nur intern, als `NodePort` oder `Loadbalancer` für Außerhalb des Clusters
- `Ingress`: Stellt einen HTTP oder HTTPS Dienst eines Services für außerhalb des Clusters bereit 
- `PersistentVolumeChain`: Stellt ein `PersistentVolume` für einen Pod oder ein Deployment


## Datenbanken und Kubernetes 

- Es wird in Datenbanken "No SQL Database", "Relational Database" und "NewSQL Database" unterschieden
- NoSQL und NewSQL lassen sich einfach horizontal skalieren und eignen sich gut für Kubernetes
  

## Ausrollen einer Anwendung mit Datenbank in separaten Pods

Hierfür müssen sogenannte Deployment.yml Dateien erstellt werden. Jede Deployment Datei repräsentiert einen Pod.

1. Pod [(deployment-activemq-postgres.yml)](deploy\deployment-activemq-postgres.yml)
   - Datenbank (Postgres)
   - MessageBroker (ActiveMQ)

2. Pod [(deployment-jexxa.yml)](/deploy/deployment-jexxa.yml)
   - Anwendung (Jexxa-Template)


Damit beide Pods miteinander kommunizieren können muss ein Service erstellt werden in dem die Ports gemappt werden.

- Service [(service.yml)](/deploy/service.yml)

Nun müssen noch anpassungen an der Software selbst vorgenommen werden. Dort müssen die Adressen für ActiveMQ und Postgres geändert werden. Dies geschieht in den [jexxa-application.properties](/src/main/resources/jexxa-application.properties)

Hier ein beispiel welches für die Adresse den Namen `(activemq-postgres-service)` des Services nutzt. Dieser wird in der [(service.yml)](/deploy/service.yml) definiert und kann auch für die einzelnen Dienste separat erstellt werden.

```properties
io.jexxa.jdbc.autocreate.database=jdbc:postgresql://activemq-postgres-service:5432/postgres
java.naming.provider.url=tcp://activemq-postgres-service:61616
```