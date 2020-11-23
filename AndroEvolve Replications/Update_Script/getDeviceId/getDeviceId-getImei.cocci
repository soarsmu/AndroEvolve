@upperbottom_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
 classIden.getImei();
+ } else {
+ classIden.getDeviceId();
+ }

@bottomupper_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
+ classIden.getImei();
+ } else {
 classIden.getDeviceId();
+ }


@upperbottom_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
tempFunctionReturnValue = classIden.getImei();
+ } else {
+ tempFunctionReturnValue = classIden.getDeviceId();
+ }

@bottomupper_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
+ tempFunctionReturnValue = classIden.getImei();
+ } else {
tempFunctionReturnValue = classIden.getDeviceId();
+ }
