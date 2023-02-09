# Digi-Kubernetes

- [Digi-Kubernetes](#digi-kubernetes)
  - [Einrichten des Kubernetes Cluster](#einrichten-des-kubernetes-cluster)
    - [Digital Ocean Nodes bestellen:](#digital-ocean-nodes-bestellen)
    - [Initiale Setup auf DigiPod um mit DigitalOcean kommunizieren zu können:](#initiale-setup-auf-digipod-um-mit-digitalocean-kommunizieren-zu-können)
    - [Portainer installieren](#portainer-installieren)
  - [-https://docs.portainer.io/start/install/server/kubernetes/baremetal](#-httpsdocsportaineriostartinstallserverkubernetesbaremetal)
  - [Kubernetes](#kubernetes)
    - [Deployment](#deployment)
    - [Wichtige Begriffe](#wichtige-begriffe)
    - [Datenbanken und Kubernetes](#datenbanken-und-kubernetes)
  - [Erstellen einer Anwendung mit Datenbank in separaten Pods](#erstellen-einer-anwendung-mit-datenbank-in-separaten-pods)
    - [Datenbank einrichten](#datenbank-einrichten)
    - [Anwendung einrichten](#anwendung-einrichten)
    - [Kommunikation zwischen Anwendung und Umgebung](#kommunikation-zwischen-anwendung-und-umgebung)
    - [Kommunikation zur Anwendung](#kommunikation-zur-anwendung)
    - [Anpassungen an Software](#anpassungen-an-software)


Dieses Repository dient dem sammeln von Erkenntnissen und Konfigurationen.
## Einrichten des Kubernetes Cluster 

### Digital Ocean Nodes bestellen:

- 6vCPUs
- 12GB
- 240GB Diskspace
- 3 Nodes 

### Initiale Setup auf DigiPod um mit DigitalOcean kommunizieren zu können:

- https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
- WGET installieren
  - sudo dnf search wget
  - sudo dnf install wget
- https://docs.digitalocean.com/reference/doctl/how-to/install/ 

- Auf digitalocean mit Cluster verbinden
  - doctl kubernetes cluster kubeconfig save …..
  - Hiernach kann der Befehl “kubectl” verwendet werden mit dem Cluster


### Portainer installieren

- Mehrere Möglichkeiten
  - Empfehlung über Terminal (Schwerer Weg)
    - https://docs.digitalocean.com/reference/doctl/how-to/install/ 

  - Über Kubernetes Admin Panel (Leichter Weg)
    - Zuerst das Kubernetes Dashboard über Digital-Ocean öffnen
      - Links auf Kubernetes drücken
      - Danach das Cluster auswählen 
      - Danach auf das Kubernetes Dashboard klicken
    - Create new Recourse und Stack von Portainer importieren
    - https://docs.portainer.io/start/install/server/kubernetes/baremetal 
    - ![](C:\Users\di35859\Documents\GitHub\KubernetesJexxaTemplate\Pictures\Portainer.png "Portainer Auswahl")
      - An dieser Stelle ist es wichtig das ``Expose via LoadBalancer`` ausgewählt wird
      - Die Stackdatei lässt sich dann über den Link im Befehl downladen
        - Als Beispiel der Link im Bild: ``https://downloads.portainer.io/ee2-17/portainer-lb.yaml``
- Danach die Anleitung von Portainer befolgen zum einrichten
  -https://docs.portainer.io/start/install/server/kubernetes/baremetal


## Kubernetes

### Deployment

- Einer Anwendung mit Datenbank:
  - Anwendung und Datenbank in einen Pod
    - Vorteile: Einfacher, Kommunikation über localhost, nur wenig zu konfigurieren
    - Nachteil: Ist ein Antipattern von Kubernetes, Kann nicht gut skalieren  da Datenbank und Anwendung unterschiedlich skalieren
    - Anwendung muss Stateless sein
  - Anwendung in ein Pod; Datenbank in einem Pod


### Wichtige Begriffe

- `Pod`: Objekt, was einen oder mehrere Container ausführt (kleinste Einheit in Kubernetes)
- `Deployment`: Definiert ein `ReplicaSet` zur Ausführung eines oder mehrerer Pods 
- `Service`: Stellt einen Netzwerkdienst eines Pods im Kubernetes Cluster bereit. Als `ClusterIP` nur intern, als `NodePort` oder `Loadbalancer` für Außerhalb des Clusters
- `Ingress`: Stellt einen HTTP oder HTTPS Dienst eines Services für außerhalb des Clusters bereit 
- `PersistentVolumeChain`: Stellt ein `PersistentVolume` für einen Pod oder ein Deployment


### Datenbanken und Kubernetes 

- Es wird in Datenbanken "No SQL Database", "Relational Database" und "NewSQL Database" unterschieden
- NoSQL und NewSQL lassen sich einfach horizontal skalieren und eignen sich gut für Kubernetes
  

## Erstellen einer Anwendung mit Datenbank in separaten Pods


### Datenbank einrichten

Bevor eine Anwendung in Kubernetes mit einer Datenbank ausgerollt werden kann, müssen für die Datenbank sogenannte
PVC (Persistent Volume Claim) erstellt werden. Dies benötigt die Datenbank (in diesem Fall Postgres) um die Daten zu persistieren.

- PVC [(pvc-environment.yml)](/deploy/pvc-environment.yml)

### Anwendung einrichten

Hierfür müssen sogenannte Deployment.yml Dateien erstellt werden. Jede Deployment-Datei repräsentiert einen Pod.
Ein Pod ist wie bereits genannt die kleinste Einheit in Kubernetes und kann aus einem aber auch aus mehreren Containern bestehen.

1. Pod [(deployment-environment.yml)](/deploy/deployment-environment.yml)
   - Datenbank (Postgres)
   - MessageBroker (ActiveMQ)

2. Pod [(deployment-application.yml)](/deploy/deployment-application.yml)
   - Anwendung (Jexxa-Template)

### Kommunikation zwischen Anwendung und Umgebung 

Damit beide Pods miteinander kommunizieren können muss ein Service erstellt werden für den ``Environment Pod`` in dem die Ports gemappt werden.

- Service [(service-environment.yml)](/deploy/service-environment.yml)

Nun können die Anwendungen untereinander Kommunizieren.

### Kommunikation zur Anwendung

Um auf die Anwendung zugreifen zu können muss ein Service erstellt werden für den ``Application Pod`` in dem die Ports gemappt werden.

- Service [(service-application.yml)](/deploy/service-application.yml)

### Anpassungen an Software

Nun müssen noch anpassungen an der Software selbst vorgenommen werden. Dort müssen die Adressen für ActiveMQ und Postgres geändert werden. Dies geschieht in den [jexxa-application.properties](/src/main/resources/jexxa-application.properties)

Hier ein beispiel welches für die Adresse den Namen `(activemq-postgres-service)` des Services nutzt. Dieser wird in der [(service.yml)](/deploy/service-environment.yml) definiert und kann auch für die einzelnen Dienste separat erstellt werden.

```properties
io.jexxa.jdbc.autocreate.database=jdbc:postgresql://activemq-postgres-service:5432/postgres
java.naming.provider.url=tcp://activemq-postgres-service:61616
```

Alle Anpassungen bis zu diesem Punkt sind in der Image-Version `2.0.18` vorhanden:

```bash
docker pull ghcr.io/nicosaia/kubernetesjexxatemplate:2.0.18
```