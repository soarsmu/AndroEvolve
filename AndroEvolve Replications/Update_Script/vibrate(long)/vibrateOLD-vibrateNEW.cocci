@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
classIden.vibrateNEW(iden0);
+ } else {
+ long newParameterVariable0 = 500;
+ classIden.vibrateOLD(newParameterVariable0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
+ VibrationEffect newParameterVariable0 = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
+ classIden.vibrateNEW(newParameterVariable0);
+ } else {
classIden.vibrateOLD(iden0);
+ }
