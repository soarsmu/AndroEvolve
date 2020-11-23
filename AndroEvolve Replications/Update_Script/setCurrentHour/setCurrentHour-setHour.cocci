@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
classIden.setHour(iden0);
+ } else {
+ classIden.setCurrentHour(iden0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ classIden.setHour(iden0);
+ } else {
classIden.setCurrentHour(iden0);
+ }
