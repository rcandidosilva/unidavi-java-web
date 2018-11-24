# Laboratório 09

## Objetivos
- Empacote o projeto com Docker

## Tarefas

### Instalação do ambiente Docker
- Realize a instalação do ambiente Docker no ambiente, caso ainda não tenha realizado
- Para instalação nativa
  - Windows
    - https://docs.docker.com/docker-for-windows/install/
  - Mac
    - https://docs.docker.com/docker-for-mac/install/
  - Ubuntu / Debian / Fedora
    - https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/
    - https://docs.docker.com/engine/installation/linux/docker-ce/debian/
    - https://docs.docker.com/engine/installation/linux/docker-ce/fedora/
- Para a instalação do Docker Toolbox
  - Windows
    - https://docs.docker.com/toolbox/toolbox_install_windows/
  - Mac
    - https://docs.docker.com/toolbox/toolbox_install_mac/

### Configure o Dockerfile para o projeto
- Defina um arquivo `Dockerfile` no diretório `src/main/docker` no projeto definido
```
FROM java:8

ARG SPRING_PROFILES_ACTIVE
ARG JAVA_OPTS
ARG PORT

ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE:-docker}
ENV JAVA_OPTS ${JAVA_OPTS:-'-Xmx512m'}
ENV DEBUG_OPTS ${DEBUG_OPTS}
ENV PORT ${PORT:-8080}

ADD *.jar /app.jar

VOLUME /tmp

RUN sh -c 'touch /app.jar'

EXPOSE ${PORT}

CMD java ${JAVA_OPTS} ${DEBUG_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar
```
- Configure o plugin `docker-maven-plugin` no `pom.xml` de cada projeto
```xml
<project>
  ...
  <build>
        <plugins>
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>0.4.13</version>
                <configuration>
                    <imageName>${project.artifactId}</imageName>
                    <dockerDirectory>${project.basedir}/src/main/docker</dockerDirectory>
                    <resources>
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
- Realize o build da aplicação e verifique as Docker images sendo criada para cada microservice
  - `mvn clean package docker:build`

### Execute a aplicação utilizando as imagem Docker definida
- Liste e identifique a imagem definida no registro Docker local
```
docker images
```
- Execute a imagem Docker definida
```
docker run -d -p 8000:8000 --name boot [project.artifactId]
```
- Verifique a imagem rodando no ambiente local
```
docker ps
```
- Execute e teste a aplicação
  - Você pode verificar o log da aplicação utilizando `docker logs`
```
docker logs -f boot
```
- Termine a execução da imagem Docker iniciada anteriormente
```
docker stop boot
```
### Publique a imagem Docker no repositório Docker Hub
- Utilize a imagem Docker produzida anteriormente
- Realize o Docker login no Docker Hub
```
docker login -u [user] -p [password]
```
- Defina tag para a imagem Docker produzida
```
docker tag [project.artifactId] [username]/[project.artifactId]
```
- Publique a imagem Docker no repositório
```
docker push [username]/[project.artifactId]
```
