git clone https://github.com/JuanDAM96/videojuego_3_Trimestre.git

cd videojuego_3_Trimestre

javac -g -d compilados .:lib/videojuego_3_Trimestre\lib\sqlite-jdbc-3.49.1.0.jar --module-path \videojuego_3_Trimestre\lib\javafx-sdk-24.0.1\lib --add-modules javafx.fxml,javafx.controls,javafx.graphics,javafx.media,javafx.base,javafx.swing \videojuego_3_Trimestre\*.java

java -classpath compilados .;lib/videojuego_3_Trimestre\lib\sqlite-jdbc-3.49.1.0.jar \videojuego_3_Trimestre\lib\javafx-sdk-24.0.1\lib --add-modules javafx.fxml,javafx.controls videojuego_3_Trimestre.App