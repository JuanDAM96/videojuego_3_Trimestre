# videojuego_3_Trimestre

T4C30 : Santiago Sánchez Delgado

JuanDAM96 : Juan Antonio Lucas

## Por si no dejan el .exe

git clone https://github.com/JuanDAM96/videojuego_3_Trimestre.git
cd videojuego_3_Trimestre
javac -g -d compilados -cp ".;lib\sqlite-jdbc-3.49.1.0.jar" --module-path "lib\javafx-sdk-24.0.1\lib" --add-modules "javafx.fxml,javafx.controls,javafx.graphics,javafx.media,javafx.base,javafx.swing" *.java
XCOPY Escenas compilados\Escenas\ /E /I /Y
XCOPY lib compilados\lib\ /E /I /Y
java -classpath "compilados;compilados\lib\sqlite-jdbc-3.49.1.0.jar" --module-path "compilados\lib\javafx-sdk-24.0.1\lib" --add-modules "javafx.fxml,javafx.controls,javafx.graphics,javafx.media,javafx.base,javafx.swing" App