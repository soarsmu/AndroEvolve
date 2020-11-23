@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
 classIden.registerGnssStatusCallback(iden0);
+ } else {
+ Listener newParameterVariable0 = this;
+ classIden.addGpsStatusListener(newParameterVariable0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
+ Callback newParameterVariable0 = callback;
+ classIden.registerGnssStatusCallback(newParameterVariable0);
+ } else {
 classIden.addGpsStatusListener(iden0);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
tempFunctionReturnValue = classIden.registerGnssStatusCallback(iden0);
+ } else {
+ Listener newParameterVariable0 = this;
+ tempFunctionReturnValue = classIden.addGpsStatusListener(newParameterVariable0);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
+ Callback newParameterVariable0 = callback;
+ tempFunctionReturnValue = classIden.registerGnssStatusCallback(newParameterVariable0);
+ } else {
tempFunctionReturnValue = classIden.addGpsStatusListener(iden0);
+ }
