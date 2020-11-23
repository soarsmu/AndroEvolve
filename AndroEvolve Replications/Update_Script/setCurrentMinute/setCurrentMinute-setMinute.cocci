@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
classIden.setMinute(iden0);
+ } else {
+ classIden.setCurrentMinute(iden0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ classIden.setMinute(iden0);
+ } else {
classIden.setCurrentMinute(iden0);
+ }
