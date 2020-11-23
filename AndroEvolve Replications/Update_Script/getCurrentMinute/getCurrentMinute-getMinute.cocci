@upperbottom_classname@
expression exp0;
identifier classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 classIden.getMinute();
+ } else {
+ classIden.getCurrentMinute();
+ }

@bottomupper_classname@
expression exp0;
identifier classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ classIden.getMinute();
+ } else {
 classIden.getCurrentMinute();
+ }


@upperbottom_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
tempFunctionReturnValue = classIden.getMinute();
+ } else {
+ tempFunctionReturnValue = classIden.getCurrentMinute();
+ }

@bottomupper_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ tempFunctionReturnValue = classIden.getMinute();
+ } else {
tempFunctionReturnValue = classIden.getCurrentMinute();
+ }
