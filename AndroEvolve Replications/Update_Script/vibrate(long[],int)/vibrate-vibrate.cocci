@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
classIden.vibrate(iden0);
+ } else {
+ long[] newParameterVariable0 = { 0, 400, 800 };
+ int newParameterVariable1 = 0;
+ classIden.vibrate(newParameterVariable0,newParameterVariable1);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2;
identifier iden0, iden1, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
+ long[] pattern = iden0;
+ VibrationEffect newParameterVariable0 = VibrationEffect.createWaveform(pattern, 0);
+ classIden.vibrate(newParameterVariable0);
+ } else {
classIden.vibrate(iden0, iden1);
+ }
