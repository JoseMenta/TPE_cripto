[![Main](https://github.com/JoseMenta/TPE_cripto/actions/workflows/main.yaml/badge.svg)](https://github.com/JoseMenta/TPE_cripto/actions/workflows/main.yaml)

# TPE Criptografía y Seguridad

El siguiente proyecto implementa algoritmos de esteganografía sobre archivos con _bmp_, con la opción de encriptar el contenido que se almacena en los archivos utilizando diferentes primitivas y modos.


## Requisitos
- [Java 21](https://www.oracle.com/ar/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)

## Instalación

1. Clonar el Repositorio
2. Ubicarse en la carpeta del proyecto
3. Compilar el proyecto
```bash 
mvn clean package
```
Se creará la carpeta _target_, donde se utilizará el archivo `TPE_cripto-1.0-SNAPSHOT-bin.tar.gz`.

## Ejecución

Para ejecutar el programa, primero se debe extraer el script del archivo comprimido:

1. Ir al directorio `/target`
    ```bash
    cd target
    ```
2. Descomprimir el archivo `TPE_cripto-1.0-SNAPSHOT-bin.tar.gz`
   ```bash
    tar -xvf TPE_cripto-1.0-SNAPSHOT-bin.tar.gz
    ```
3. Ingresar a la carpeta descomprimida
    ```bash
    cd TPE_cripto-1.0-SNAPSHOT
    ```
4. Darle permisos de ejecución al archivo
    ```bash
    chmod u+x stegobmp.sh
   ```

### Ocultamiento

El ejecutable acepta los siguientes argumentos:
- `-Din <path>`: Path al archivo que se va a ocultar (sólo para ocultar).
- `-Dp <path>`: Path al archivo bmp portador.
- `-Dout <path>`: Path al archivo de salida (bmp con la información incrustada).
- `-Dsteg <LSB1 | LSB4 | LSBI>`: Algoritmo de esteganografiado.
- `-Da <aes128 | aes192 | aes256 | des>`: Primitiva utilizada (el default es `aes128`).
- `-Dm <ecb | cfb | ofb | cbc> `: Modo de encadenamiento (el default es `cbc`).
- `-Dpass <password>`: Password de encriptación. Si no se pasa, entonces no se encripta el contenido.

Con esto, se puede ejecutar el programa de la siguiente manera:
```bash
./stegobmp.sh -Dembed -Din <path> -Dp <path> -Dout <path> -Dsteg=<LSB1 | LSB4 | LSBI> -Dpass=<pass> -Da=<aes128 | aes192 | aes256 | des> -Dm= <ecb | cfb | ofb | cbc>
```
Por ejemplo
```bash
/stegobmp.sh -Dembed -Din=hola.txt -Dp=bmp_24.bmp -Dout=test.bmp -Dsteg=LSB4 -Dpass=hello -Da=aes256 -Dm=ofb
```


### Extracción 

El ejecutable acepta los siguientes argumentos:
- `-Dp <path>`: Path al archivo bmp portador.
- `-Dout <path>`: Path al archivo de salida (no se debe especificar la extensión).
- `-Dsteg <LSB1 | LSB4 | LSBI>`: Algoritmo de esteganografiado.
- `-Da <aes128 | aes192 | aes256 | des>`: Primitiva utilizada (el default es `aes128`).
- `-Dm <ecb | cfb | ofb | cbc> `: Modo de encadenamiento (el default es `cbc`).
- `-Dpass <password>`: Password de encriptación. Si no se pasa, entonces no se encripta el contenido.

Con esto, se puede ejecutar el programa de la siguiente manera:
```bash
./stegobmp.sh -Dextract  -Dp <path> -Dout <path> -Dsteg=<LSB1 | LSB4 | LSBI> -Dpass=<pass> -Da=<aes128 | aes192 | aes256 | des> -Dm= <ecb | cfb | ofb | cbc>
```
Por ejemplo
```bash
./stegobmp.sh -Dextract  -Dp=test.bmp -Dout=holaa -Dsteg=LSB4 -Dpass=hello -Da=aes256 -Dm=ofb
```


## Aclaraciones sobre el proyecto
Este proyecto es realizado para la materia Programación de Objetos Distribuidos del ITBA.
**Los integrantes del grupo son:**
- 62041 - [Martín Hecht](https://github.com/martinhecht01)
- 62248 - [José Rodolfo Mentasti](https://github.com/JoseMenta)
- 62329 - [Lautaro Hernando](https://github.com/laucha12)
- 62618 - [Axel Facundo Preiti Tasat](https://github.com/AxelPreitiT)

