@upperbottom_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= 23) {
 classIden.getHour();
+ } else {
+ classIden.getCurrentHour();
+ }

@bottomupper_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= 23) {
+ classIden.getHour();
+ } else {
 classIden.getCurrentHour();
+ }


@upperbottom_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= 23) {
tempFunctionReturnValue = classIden.getHour();
+ } else {
+ tempFunctionReturnValue = classIden.getCurrentHour();
+ }

@bottomupper_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= 23) {
+ tempFunctionReturnValue = classIden.getHour();
+ } else {
tempFunctionReturnValue = classIden.getCurrentHour();
+ }
