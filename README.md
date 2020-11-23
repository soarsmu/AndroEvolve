## Usage Example
``
java -jar AndroEvolve.jar --generate-patch "android.widget.TimePicker#getCurrentMinute()#java.lang.Integer" "android.widget.TimePicker#getMinute()#int" --input Example/getCurrentMinute.java --output Patch/getCurrentMinute.cocci
``

``
java -jar AndroEvolve.jar --apply-patch "android.widget.TimePicker#getCurrentMinute()#java.lang.Integer" "android.widget.TimePicker#getMinute()#int" --input Target/getCurrentMinute.java --patch Patch/getCurrentMinute.cocci --output Result/getCurrentMinute.java
``