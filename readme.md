# Proyecto base para Android Studio

Este es un proyecto base para comenzar ya con el setup inicial de una app básica en Android con un simple comando en la consola.  
  
El proyecto tiene la siguiente configuración inicial
    

  * *Fragments Navigation* con *Safe Args*
  * *DataBinding*
  * Inyección de dependencias con *Hilt*
  * Configuración necesaria para usar *Retrofit*
  * Una base de datos básica usando *Room*
  * La configuración necesaria para usar *shared preferences* encriptadas

Para facilitar iniciar un nuevo proyecto con esta configuración se ha creado un script `new_android_project` y una estructura de carpetas específica que no corresponde exactamente con la estructura necesaria para Android Studio. Por tal motivo se debe inicializar el proyecto haciendo uso del script antes mencionado.  
  
**Recomendación**

Crear un `alias` apuntando al archivo del script.
Para ello bastará con agregar la siguiente línea al archivo `~/.bash_aliases`  
  
    alias android-project='/path/to/your/download/folder/new_android_project'  
  
**Nuevo proyecto**

Una vez creado el `alias` es simple crear un nuevo proyecto base usando el siguiente comando desde la consola, posicionandose antes en la carpeta donde se desea crear el proyecto base

    android-project -p cu.jaco.new_project -n "Nuevo Proyecto"

El valor del argumento `-p` define el package que usará el nuevo proyecto, mientras que el valor de `-n` define el nombre de la carpeta del proyecto y el nombre de la aplicación una vez instalada.


