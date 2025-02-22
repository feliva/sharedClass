#!/bin/bash

###
### instale o git bash
### sudo ./install.sh -i /opt/sifw -e darlanfelisberto@gmail.com -s senhaemail -a http://localhost:8080 -o l 
### acresente o -c para limpar o diretorio de instalacao
###

_DEBUG="on"
WIN=l;

function DEBUG(){
    echo $@
    [ "$_DEBUG" == "on" ] && $@
}

function usage() { 
    printf "sudo ./install.sh -i /opt -e darlanfelisberto@gmail.com -s senha -a localhost:8080 -o l\n"; 
    return 1; 
}; 

function main() { 
    while [ "$#" -ge 1 ]; do
        case "$1" in 
            -i|--install) 
                shift;
                PATH_INSTALL="${1:?$(usage)}";
                echo " PATH_INSTALL " $1
                ;; 
            -e|--email) 
                shift; 
                EMAIL="${1:?$(usage)}" 
                ;; 
            -s|--senhaEmail) 
                shift; 
                SENHA_EMAIL="${1:?$(usage)}" 
                ;; 
            -a|--authServerUrl) 
                shift; 
                AUTH_SERVER_URL="${1:?$(usage)}" 
                ;; 
            -w|--wildfly) 
                shift; 
                VERSION_WILDFLY="${1:?$(usage)}" 
                ;; 
            -j|--jarPostgres) 
                shift; 
                VERSION_JAR_POSTGRES="${1:?$(usage)}" 
                ;;
            -c|--clear) 
                shift; 
                removeInstalacao;
                exit 0;
                ;;
            -h|--help) 
                shift; 
                usage;
                exit 0;
                ;;
            -o) 
                shift; 
                WIN="${1:?$(usage)}"
                ;; 
            *) 
                usage;
                return 1 
                ;; 
        esac; 
        shift;
    done; 
};

function removeInstalacao(){
    DEBUG rm -R $PATH_INSTALL
    DEBUG rm -R wildfly.*
    #DEBUG rm -R launch.sh 
    #DEBUG rm $VERSION_JAR_POSTGRES'.jar'
    #DEBUG rm jdk-${VERSION_JDK}_linux-x64_bin.tar.gz
    DEBUG rm -R configure-wildfly.cli
    DEBUG rm -R configuration_linux.properties
    #DEBUG rm /etc/systemd/system/wildfly.service 
    #DEBUG rm -R /etc/wildfly

    echo "Limpo."
}

function installDev(){
    DEBUG mkdir -p $PATH_INSTALL;
    DEBUG mkdir -p $PATH_INSTALL"/imagens";
    fileContantes;
    java;
    wildfly;
}

function fileContantes(){
  # escape / for \/ of path
  DEBUG scapeStrings $PATH_INSTALL INS;

  line='=\"'$INS'\";\/\/<#PATH#>'

  echo 'ins' $line $INS

	DEBUG sed -i "s/=.*<#PATH#>/$line/g" ../src/main/java/br/com/feliva/sharedClass/constantes/InitConstantes.java

  #aou usar o > para redirecionar a saida, como a função DEBUG escreve o comando na saida, esso comando tbm vai para o arquivo, por isso mudei
  cp _configuration_linux.properties configuration_linux.properties
  DEBUG sed -i 's/<#PATH#>/'$INS'/g' configuration_linux.properties
  DEBUG sed -i 's/<#AUTH_SERVER_URL#>/'$AUTH_SERVER_URL'/g' configuration_linux.properties
	DEBUG cp configuration_linux.properties  $PATH_INSTALL/configuration_linux.properties
}

function java(){
  if [ "$WIN" = w ]; then
    downloadJava jdk-${VERSION_JDK}_windows-x64_bin.zip
	  DEBUG unzip jdk-${VERSION_JDK}_windows-x64_bin.zip -d $PATH_INSTALL
    #windowns problema com links synbolicos, git bash, apeans rename
    DEBUG mv $PATH_INSTALL/jdk-${VERSION_JDK} $PATH_INSTALL/jdk${JDK_LINK_NAME}
    DEBUG setx JAVA_HOME $PATH_INSTALL/jdk${JDK_LINK_NAME}
  else
   	downloadJava jdk-${VERSION_JDK}_linux-x64_bin.tar.gz 
	  DEBUG tar -xvzf jdk-${VERSION_JDK}_linux-x64_bin.tar.gz -C $PATH_INSTALL
    DEBUG mv $PATH_INSTALL/jdk-${VERSION_JDK} $PATH_INSTALL/jdk${JDK_LINK_NAME}
    DEBUG export JAVA_HOME=$PATH_INSTALL/jdk${JDK_LINK_NAME}
  fi
}

function downloadJava(){
  echo $1;
  if [ -e $1 ]; then
    echo 'File:'$1' já existe.'
  else
    DEBUG curl -L -O https://download.oracle.com/java/${JDK_LINK_NAME}/archive/$1
  fi
}

function downloadPostgresJar(){
  if [ -e $1 ]; then
    echo 'File:'$1' já existe.'
  else
    DEBUG curl -L -O https://jdbc.postgresql.org/download/$1
  fi
}

function downloadWildfly(){
  if [ -e $1 ]; then
    echo 'File:'$1' já existe.'
  else
    DEBUG curl -L -O https://github.com/wildfly/wildfly/releases/download/$VERSION_WILDFLY/$1
  fi
}

function wildfly(){
  ROOT_PATH_WILDFLY=$PATH_INSTALL/wildfly-$VERSION_WILDFLY

	downloadPostgresJar $VERSION_JAR_POSTGRES.jar
	downloadWildfly wildfly-$VERSION_WILDFLY.tar.gz

	DEBUG tar -xvzf "wildfly-$VERSION_WILDFLY.tar.gz" -C $PATH_INSTALL

  DEBUG scapeStrings $PATH_INSTALL INS;

	DEBUG $ROOT_PATH_WILDFLY/bin/add-user.sh -u manager -p manager

  cp _configure-wildfly.cli configure-wildfly.cli

  DEBUG scapeStrings $PWD PWD_SCA ;
  DEBUG sed -i    's/<#JAR_POSTGRES#>/'$PWD_SCA'\/'$VERSION_JAR_POSTGRES'/g'  configure-wildfly.cli
	DEBUG sed -i    's/<#EMAIL#>/'$EMAIL'/g'                        configure-wildfly.cli
	DEBUG sed -i    's/<#SENHA_EMAIL#>/'$SENHA_EMAIL'/g'            configure-wildfly.cli
  DEBUG sed -i    's/<#AUTH_SERVER_URL#>/'$AUTH_SERVER_URL'/g'    configure-wildfly.cli


  if [ "$WIN" = l ]; then
    DEBUG sed -i    '1i JBOSS_JAVA_SIZING="-Xms256m -Xmx8096m -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=512m -XX:+UsarZGC"'   $ROOT_PATH_WILDFLY/bin/standalone.conf
    DEBUG echo "Sleep de 10 segundos..."
    DEBUG sleep 1
    DEBUG $ROOT_PATH_WILDFLY/bin/standalone.sh &
    DEBUG echo "aguardando 10 segundos..."
    DEBUG sleep 10
    DEBUG $ROOT_PATH_WILDFLY/bin/jboss-cli.sh --connect --file=configure-wildfly.cli
    DEBUG $ROOT_PATH_WILDFLY/bin/jboss-cli.sh --connect --command=shutdown
    echo "Pronto tudo instalado."
    echo "Para instalar como servico, utilize os aquivos launch.sh wildfly.conf e wildfly.service"
  else
    echo 'no cmd executes os comandos:'
    echo $ROOT_PATH_WILDFLY'/bin/standalone.bat &'
    echo $ROOT_PATH_WILDFLY'/bin/jboss-cli.bat --connect --file='$PWD'/configure-wildfly.cli'
    echo $ROOT_PATH_WILDFLY'/bin/jboss-cli.bat --connect --command=shutdown'
  fi;

}

function inicializarComSys(){
  #Somente linux
  DEBUG groupadd -r wildfly
	DEBUG useradd -r -g wildfly -d $PATH_INSTALL -s /sbin/nologin wildfly

	DEBUG chown -R wildfly:wildfly $PATH_INSTALL
  # TODO esse é so pra desenvolvimento
  DEBUG chmod -R 777 $PATH_INSTALL

  DEBUG mkdir -p /etc/wildfly

  # escape / for \/ of PATH_INSTALL
  DEBUG scapeString $PATH_INSTALL INS;

  #TODO - remover debug ou usar cp
  DEBUG sed 's/<#PATH#>/'$INS'/g' _launch.sh > launch.sh
  DEBUG sed 's/<#PATH#>/'$INS'/g' _wildfly.conf > wildfly.conf
  DEBUG sed 's/<#PATH#>/'$INS'/g' _wildfly.service > wildfly.service

  DEBUG cp wildfly.conf /etc/wildfly/
  DEBUG cp wildfly.service /etc/systemd/system/
  DEBUG cp launch.sh $PATH_INSTALL/wildfly/bin/
  DEBUG chmod +x $PATH_INSTALL/wildfly/bin/launch.sh
  DEBUG systemctl start wildfly.service
  DEBUG systemctl enable wildfly.service
}

function scapeStrings(){
  # -n do declre faze um "passgem por referencia"
  declare -n OUTPUT=$2
  auth=""
  anterior=''
  especial='/"'
  for ((i=1;i<=${#1};i++))
  do
    ca=${1:$i-1:1}
    for ((c=1;c<=${#especial};c++))
    do
      esp=${especial:$c-1:1}
      # echo $esp "<<"
      if [ "$anterior" = '\' ]; then
          break;
      fi
      if [ $ca = $esp ]; then
          # echo 'escape '$ca
          auth=$auth'\'
          break
      fi
    done;
    auth=$auth$ca;
    anterior=${auth: -1}
  done
  OUTPUT=$auth;
};

function prepareWindowns(){
    ## PATH_INSTALL é usado para os comando e navergar pelas pastas
    echo 'instalacao em windowns'
    PATH_INSTALL="F:/feliva/install";
    PWD=$(pwd -W);
}

if command -v curl >&2; then
  echo Curl ok
else
  echo instale o comando: curl
  exit 1;
fi


PATH_INSTALL="/opt/feliva/install";
EMAIL="a@b.c";
SENHA_EMAIL="12345678";
AUTH_SERVER_URL="http://localhost:8080";
VERSION_JAR_POSTGRES="postgresql-42.6.0";
VERSION_JDK="21.0.6";
JDK_LINK_NAME="21";
VERSION_WILDFLY="34.0.0.Final";
#diretorio atual, path linux e windowns
PWD=$(pwd)

DEBUG main "$@"

if [ "$WIN" = w ]; then
    prepareWindowns
    echo 'win';
else
    echo 'linux';
fi

#escape caracter / 
DEBUG scapeStrings $AUTH_SERVER_URL AUTH_SERVER_URL

printf ">%s< >%s< >%s< >%s< >%s< >%s< >%s< >%s< \n" $VERSION_WILDFLY $PATH_INSTALL $EMAIL $SENHA_EMAIL $AUTH_SERVER_URL $VERSION_JAR_POSTGRES $VERSION_JDK;

installDev;

#-i /opt -e darlan@gmail.com -s senha -a localhostr:8080 -w wildfly -j jar

